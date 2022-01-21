package hk.edu.polyu.comp.comp2021.clevis.gui;
import hk.edu.polyu.comp.comp2021.clevis.model.Shapes;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Author: Anthony Zhejun
 * Date: 11/02/2021
 * Description:
 */


public class MyWindow {
	
	private MyFrame frame;

    private final static int WIDTH=900, HEIGHT=600, LOCATION=50;

    /**
     * @param a the Shapes will be drawn
     */
    public MyWindow(ArrayList<Shapes> a) { // frame+panel
        EventQueue.invokeLater(() -> {
            frame=new MyFrame();
            MyPanel panel=new MyPanel(a);

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new JScrollPane(panel));
            frame.getContentPane().add(panel.getControl(), "Last");
            // display the slider and get the change message
            frame.setSize(WIDTH, HEIGHT);
            frame.setLocation(LOCATION,LOCATION);
            frame.setVisible(true);
        });
    }

    /**
     * delete picture
     */
	public final void destroy(){
		if(frame!=null) frame.dispose();
	}
}
