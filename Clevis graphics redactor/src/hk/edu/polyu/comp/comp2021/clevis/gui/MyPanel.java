package hk.edu.polyu.comp.comp2021.clevis.gui;

import hk.edu.polyu.comp.comp2021.clevis.model.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * for drawing Shapes
 */
public class MyPanel extends JPanel implements ChangeListener {
    private final RenderingHints hints;

    private final ArrayList<Shapes> shapesList;
    private Shape[] shapesList2; // contains the shape elements converted from the elements in shapesList
    private final Dimension size; // encapsulate the width and height of a component

    private double scale=1.0; // used for zoom in and out

    private static final int WIDTH=900;
    private static final int HEIGHT=600;

    /**
     * @param a the Shapes will be drawn
     */
    public MyPanel(ArrayList<Shapes> a){
        super();
        shapesList=a;

        hints = new RenderingHints(null);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);


        size=new Dimension(WIDTH, HEIGHT);
        final int R=225, G=203, B=203;
        setBackground(new Color(R,G,B));
        setVisible(true);

        initShapes();

    }

    @Override
    public final void stateChanged(ChangeEvent e){ // get the message of the change of event
        int value=((JSlider)e.getSource()).getValue();
        final double SCALAR=100.0;
        scale=value/SCALAR;
        repaint(); // repaint its component.
        revalidate();
    }
    @Override
    public final Dimension getPreferredSize(){
        int w=(int)(scale*size.width);
        int h=(int)(scale*size.height);
        return new Dimension(w, h);
    }

    /**
     * @return used for zoom in and out
     */
    final JSlider getControl(){ // used for zoom in and out
        final int MIN=50, MAX=200, VALUE=100,MAJORTICKSPACING=50,MINORTICKSPACING=10;
        JSlider slider=new JSlider(JSlider.HORIZONTAL, MIN, MAX, VALUE);
        slider.setMajorTickSpacing(MAJORTICKSPACING);
        slider.setMinorTickSpacing(MINORTICKSPACING);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        return slider;
    }

    private void initShapes(){ // initialize all the shapes by converting them
    	if(shapesList.isEmpty()){
    		shapesList2=new Shape[0];
    		return;
    	}
        shapesList2=new Shape[shapesList.size()];
		
		ArrayList<Shapes> templist= new ArrayList<>();
		for(Shapes s:shapesList)
			if(!s.isGrouped())templist.add(s);
		Groups g=new Groups("g",templist);
		Shapes.box b=g.boundingbox();
		CommandLine.ungroup(g);
        final int SCALEX=600, SCALEY=400, DELTAX=150, DELTAY=100, HEIGHTADJUSTER=250;
		double scale= Math.min(SCALEX / b.getW(), SCALEY / b.getH());
		//System.out.println("Scale="+scale);
		double dx=b.getX()*scale-DELTAX,dy=b.getY()*scale-DELTAY;

        int index=0;
        for(int i=0; i<shapesList.size();i++){
            Shapes shape = shapesList.get(index);
            switch (shape.getType()) {
                case LINE: // draw line
                    shapesList2[i] = new Line2D.Double(((Lines) shape).getX1()*scale-dx, HEIGHTADJUSTER-(((Lines) shape).gety1()*scale-dy), ((Lines) shape).getX2()*scale-dx, HEIGHTADJUSTER-(((Lines) shape).gety2()*scale-dy));
                    break;
                case CIRCLE:// draw circle
                    shapesList2[i] = new Ellipse2D.Double(((Circles) shape).getX()*scale-dx-((Circles) shape).getR()*scale, HEIGHTADJUSTER-(((Circles) shape).getY()*scale-dy+((Circles) shape).getR()*scale), ((Circles) shape).getR() * 2*scale, ((Circles) shape).getR() * 2*scale);
                    break;
                case SQUARE:
                case RECTANGLE: // draw rectangle
                    shapesList2[i] = new Rectangle2D.Double(((Rectangles) shape).getX()*scale-dx, HEIGHTADJUSTER-(((Rectangles) shape).getY()*scale-dy), ((Rectangles) shape).getW()*scale, ((Rectangles) shape).getH()*scale);
                    break;
                case GROUP:
                    index--;
            }
            index++;
        }
    }
    @Override
    protected final void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHints(hints);

        double x = (getWidth() - scale*size.width)/2;
        double y = (getHeight() - scale*size.height)/2;

        AffineTransform at=AffineTransform.getTranslateInstance(x, y);
        at.scale(scale, scale);
		
		if(shapesList2==null)return;
        if(shapesList2.length==0)return;

        for (Shape s : shapesList2) {

            if (s == null) break;
            g2.draw(at.createTransformedShape(s));
        }
    }
}
