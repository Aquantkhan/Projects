package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * as name of class, this for creating circle
 */
public class Circles extends Shapes {
    private double x,y;
    private final double r;
    /**
     * @return position of centre in x-axis
     */
    public double getX(){return x;}
    /**
     * @return position of the centre in y-axis
     */
    public double getY(){return y;}
    /**
     * @return length of radius
     */
    public double getR(){return r;}
    /**
     * @param n means its name
     * @param a simply means x-position
     * @param b simply means y-position
     * @param c means radius
     */
    Circles(String n, double a, double b, double c){
        this.n=n;
        this.type=Type.CIRCLE;
        this.grouped=false;
        x=a;y=b;r=c;
    }
    @Override
    final String list(){
        return "Name: "+n+"\nType: "+getType()+"\nCentre: ("+format(x)+","+format(y)+")\nRadius: "+format(r);
    }
    @Override
    final box boundingbox(){
        return new box(x-r,y+r,2*r,2*r);
    }
    @Override
    final void move(double dx, double dy){
        x+=dx;y+=dy;
    }
    @Override
    final double distance(double m, double n){
        return Math.abs(distance(x,y,m,n)-r);
    }
    @Override
    final boolean intersects(Shapes other){
        if(other.getType()!=Type.CIRCLE){
            return other.intersects(this);
        }
        Circles circle=(Circles)other;
        double d=distance(x,y,circle.getX(),circle.getY());
        return r+circle.getR()-d>-DELTA && r-circle.getR()-d<DELTA && circle.getR()-r-d<DELTA;
    }
}