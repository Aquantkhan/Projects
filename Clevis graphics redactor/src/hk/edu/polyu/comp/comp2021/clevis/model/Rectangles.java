package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Rectangle
 */
public class Rectangles extends Shapes {
    private double x,y;
    private final double w,h;
    /**
     * @return x position of top-left point
     */
    public double getX(){return x;}

    /**
     * @return y position of top-left point
     */
    public double getY(){return y;}

    /**
     * @return width
     */
    public double getW(){return w;}

    /**
     * @return height
     */
    public double getH(){return h;}
    private final Lines l1,l2,l3,l4;//4 edges

    /**
     * @return upper side
     */
    public Lines getL1(){return l1;}

    /**
     * @return right side
     */
    public Lines getL2(){return l2;}

    /**
     * @return downside
     */
    public Lines getL3(){return l3;}

    /**
     * @return left side
     */
    public Lines getL4(){return l4;}

    /**
     * @param n name
     * @param a x position of left-top point
     * @param b y position of left-top point
     * @param c width
     * @param d height
     */
    Rectangles(String n, double a, double b, double c, double d){
        this.n=n;
        this.type=Type.RECTANGLE;
        this.grouped=false;
        x=a;y=b;w=c;h=d;
        l1=new Lines("l1",x,y,x+w,y);
        l2=new Lines("l2",x+w,y,x+w,y-h);
        l3=new Lines("h1",x,y-h,x+w,y-h);
        l4=new Lines("h2",x,y,x,y-h);
    }
    @Override
    String list(){
        return "Name: "+n+"\nType: "+getType()+"\nTop-left Corner: ("+format(x)+","+format(y)+")\nWidth: "+format(w)+"\nHeight: "+format(h);
    }
    @Override
    final box boundingbox(){
        return new box(x,y,w,h);
    }
    @Override
    final void move(double dx, double dy){
        x+=dx;y+=dy;
    }
    @Override
    final double distance(double m, double n){
        return Math.min(Math.min(l1.distance(m,n),l2.distance(m,n)),Math.min(l3.distance(m,n),l4.distance(m,n)));
    }
    @Override
    final boolean intersects(Shapes other){
        return l1.intersects(other)||l2.intersects(other)||l3.intersects(other)||l4.intersects(other);
    }
}