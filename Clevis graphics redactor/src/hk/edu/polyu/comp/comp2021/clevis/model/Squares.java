package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Square
 */
public class Squares extends Rectangles {
    private final double l;

    /**
     * @param n name
     * @param a x value of top-left point
     * @param b y value of top-left point
     * @param c radius
     */
    Squares(String n, double a, double b, double c){
        super(n,a,b,c,c);
        type=Type.SQUARE;
        l=c;
    }
    @Override
    final String list(){
        return "Name: "+n+"\nType: "+getType()+"\nTop-left Corner: ("+format(getX())+","+format(getY())+")\nSide length: "+format(l);
    }
}
