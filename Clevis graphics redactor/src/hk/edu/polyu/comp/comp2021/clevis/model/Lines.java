package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Lines
 */
public class Lines extends Shapes {

    private double x1,y1,x2,y2;
    /**
     * @return return x position of first point
     */
    public double getX1(){return x1;}

    /**
     * @return return y position of first point
     */
    public double gety1(){return y1;}

    /**
     * @return return x position of second point
     */
    public double getX2(){return x2;}

    /**
     * @return return y position of second point
     */
    public double gety2(){return y2;}

    /**
     * @param n name
     * @param a x for first point
     * @param b y for f-p
     * @param c x for s-p
     * @param d y for s-p
     */
    Lines(String n, double a, double b, double c, double d){
        this.n=n;
        this.type=Type.LINE;
        this.grouped=false;
        x1=a;y1=b;x2=c;y2=d;
    }
    @Override
    final String list(){
        return "Name: "+n+"\nType: "+getType()+"\nEnd1: ("+format(x1)+","+format(y1)+")\nEnd2: ("+format(x2)+","+format(y2)+")";
    }  // list the basic info, REQ13
    @Override
    final box boundingbox(){
        return new box(Math.min(x1,x2),Math.max(y1,y2),Math.abs(x1-x2),Math.abs(y1-y2));
    }
    @Override
    final void move(double dx, double dy){
        x1+=dx;x2+=dx;
        y1+=dy;y2+=dy;
    }
    @Override
    final double distance(double m, double n){
        //the line x=x0            //used to calculate the distance between a SHAPE and a point
        if(Math.abs(x1-x2)<DELTA){   // delta less than 0.05
            double maxy=Math.max(y1,y2),miny=Math.min(y1,y2);
            if(n>maxy){
                return distance(m,n,x1,maxy);
            }else if(n<miny){
                return distance(m,n,x1,miny);
            }else{
                return Math.abs(m-x1);
            }  //this is the distance between a vertical/horizontal line and a point
        }

        //the line y=y0
        if(Math.abs(y1-y2)<DELTA){  // same too
            double maxx=Math.max(x1,x2),minx=Math.min(x1,x2);
            if(m>maxx){
                return distance(m,n,maxx,y1);
            }else if(m<minx){
                return distance(m,n,minx,y1);
            }else{
                return Math.abs(n-y1);
            }
        }
        //the line y=kx+b
        double k=(y2-y1)/(x2-x1);
        double b=y1-x1*k;

        //the perpendicular point
        double px=(m/k+n-b)/(k+1/k);  // pay attention that m is variable
        double py=k*px+b;

        //closest point on the line segment
        double cx,cy;  // more specify, this part is used to calculated which point between x1y1
        if(px<Math.min(x1,x2)){  // and x2y2 closer to pxpy
            cx=Math.min(x1,x2);
            cy=k*cx+b;
        }else if(px>Math.max(x1,x2)){
            cx=Math.max(x1,x2);
            cy=k*cx+b;
        }else{
            cx=px;cy=py;
        }

        return distance(m,n,cx,cy);
    }

    @Override
    final boolean intersects(Shapes other){
        switch(other.getType()){
            case LINE: return intersects((Lines)other);
            case CIRCLE: return intersects((Circles)other);
            case SQUARE:
            case RECTANGLE: return intersects((Rectangles)other);
        }
        return intersects((Groups)other);
    }

    /**
     * @param l an Object of Line
     * @return	return the result of judging interaction
     */
    final boolean intersects(Lines l){
        //x=x1,x=x2
        if(Math.abs(x1-x2)<DELTA && Math.abs(l.getX1()-l.getX2())<DELTA){  // judge whether the two points on the same perpendicular
            if(Math.abs(x1-l.getX1())>DELTA){  // line. then decide whether the two line in the had intersects
                return false;
            }
            if(Math.min(y1,y2)-Math.max(l.gety1(),l.gety2())>DELTA){
                return false;
            }
            return !(Math.min(l.gety1(), l.gety2()) - Math.max(y1, y2) > DELTA);
        }
        //y=y1,y=y2
        if(Math.abs(y1-y2)<DELTA && Math.abs(l.gety1()-l.gety2())<DELTA){
            if(Math.abs(y1-l.gety1())>DELTA){
                return false;
            }
            if(Math.min(x1,x2)-Math.max(l.getX1(),l.getX2())>DELTA){
                return false;
            }
            return !(Math.min(l.getX1(), l.getX2()) - Math.max(x1, x2) > DELTA);
        }
        //x=x0,y=y0
        if(Math.abs(x1-x2)<DELTA && Math.abs(l.gety1()-l.gety2())<DELTA){  // one perpendicular to x-axis, one
            return distance(x1,l.gety1())<DELTA && l.distance(x1,l.gety1())<DELTA; // perpendicular to y-axis
        }
        //y=y0,x=x0
        if(Math.abs(y1-y2)<DELTA && Math.abs(l.getX1()-l.getX2())<DELTA){  // reverse the previous condition
            return distance(l.getX1(),y1)<DELTA && l.distance(l.getX1(),y1)<DELTA;
        }
        //x=x0,y=kx+b or y=y0,y=kx+b
        if(Math.abs(x1-x2)<DELTA || Math.abs(y1-y2)<DELTA){  // this is a horizontal/vertical line
            return l.intersects(this);  // l is a slant line
        }

        double k=(y2-y1)/(x2-x1);
        double b=y1-x1*k;

        //y=kx+b,x=x0
        if(Math.abs(l.getX1()-l.getX2())<DELTA){
            double iy=k*l.getX1()+b;
            return distance(l.getX1(),iy)<DELTA && l.distance(l.getX1(),iy)<DELTA;
        }
        //y=kx+b,y=y0
        if(Math.abs(l.gety1()-l.gety2())<DELTA){
            double ix=(l.gety1()-b)/k;
            return distance(ix,l.gety1())<DELTA && l.distance(ix,l.gety1())<DELTA;
        }

        //y=kx+b, y=k2x+b2
        double k2=(l.gety2()-l.gety1())/(l.getX2()-l.getX1());
        double b2=l.gety1()-l.getX1()*k2;
        double ix=(b2-b)/(k-k2);
        return ix-Math.min(x1,x2)>-DELTA && ix-Math.min(l.getX1(),l.getX2())>-DELTA
                && Math.max(x1,x2)-ix>-DELTA && Math.max(l.getX1(),l.getX2())-ix>-DELTA;
    }

    /**
     * @param c an Object of Circle
     * @return judging interactions
     */
    final boolean intersects(Circles c){
        //both ends inside
        double d1=distance(x1,y1,c.getX(),c.getY()),d2=distance(x2,y2,c.getX(),c.getY());
        if(d1<c.getR()&& d2<c.getR()){
            return false;
        }
        //at least one outside
        return distance(c.getX(),c.getY())-c.getR()<DELTA;
    }

    /**
     * @param r an object of Rectangles
     * @return result of whether the two Shape has interacted
     */
    final boolean intersects(Rectangles r){
        return intersects(r.getL1())||intersects(r.getL2())||intersects(r.getL3())||intersects(r.getL4());
    }

    /**
     * @param g an Object of Group
     * @return this intersects basically depoend on Lines
     */
    final boolean intersects(Groups g){
        return g.intersects(this);
    }
}