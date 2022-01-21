package hk.edu.polyu.comp.comp2021.clevis.model;
import java.lang.Math;

/**
 * abstract class which define structure of rest class
 */
public abstract class Shapes {
	// variables:
	/**
	 * used to compare interaction of two Shape,
	 */
	protected final double DELTA=1e-5;
	/**
	 * name
	 */
	protected String n;  //name

	/**
	 * @return return name
	 */
	public final String getName() {return n;}

	/**
	 * whether is in a group (if true then should not be accessible)
	 */
	protected boolean grouped;

	/**
	 * @param b set group
	 */
	public void setGrouped(boolean b){grouped=b;}

	/**
	 * @return return a status of a shape, ungrouped/grouped
	 */
	public boolean isGrouped(){return grouped;}

	/**
	 * shape type
	 */
	protected Type type;

	/**
	 * @return return shape type
	 */
	public final Type getType() {return type;}

	/**
	 * @param f value be printed
	 * @return numbers around two decimal
	 */
	public static String format(double f){
		//format to 2 decimal places
		return String.format("%.2f",f);
	}

	/**
	 * @param x1  x position of f-p
	 * @param y1  y position of f-p
	 * @param x2  x position of s-p
	 * @param y2  y position of s-p
	 * @return distance of two point
	 */
	final double distance(double x1, double y1, double x2, double y2){
		//point to point distance
		return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
	}

	/**
	 * all possible Shape type
	 */
    public enum Type {
		/**
		 * five possible used Shape, this is Line
		 */
    	LINE,
		/**
		 *  Rectangle
		 */
		RECTANGLE,
		/**
		 *  Square
		 */
		SQUARE,
		/**
		 * Circle
		 */
		CIRCLE,
		/**
		 * Group
		 */
		GROUP
	}

	/**
	 * @return shape
	 */
    abstract String list();//REQ13

	/**
	 * box the attributes, in case of illegal or invalid access
	 */
    public static class box{
    	//describes a bounding box
		private final double x,y,w,h;

		/**
		 * @return x
 		 */
		public double getX(){return x;}

		/**
		 * @return y
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

		/**
		 * @param a x
		 * @param b y
		 * @param c width
		 * @param d height
		 */
    	public box(double a, double b, double c, double d){
    		x=a;y=b;w=c;h=d;
    	}
    	public final String toString(){
    		return format(x)+" "+format(y)+" "+format(w)+" "+format(h);
    	}
    }

	/**
	 * @return min bounding area
	 */
    abstract box boundingbox();

	/**
	 * @param dx move units of x
	 * @param dy move units of y
	 */
    abstract void move(double dx, double dy);

	/**
	 * @param dx x
	 * @param dy y
	 * @return distance
	 */
    abstract double distance(double dx, double dy);

	/**
	 * @param dx x position of the point, this method used to judge whether this point in a shape
	 * @param dy y
	 * @return boolean, result of whether be contained
	 */
    final boolean contains(double dx, double dy){
		final double DELTA2 = 0.05;
		return distance(dx,dy)< DELTA2;
    }

	/**
	 * @param other another shape
	 * @return whether two shape intersect each other
	 */
    abstract boolean intersects(Shapes other);//REQ12
}