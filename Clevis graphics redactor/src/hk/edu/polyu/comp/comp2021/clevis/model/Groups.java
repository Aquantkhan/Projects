package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.ArrayList;

/**
 * Groups
 */
public class Groups extends Shapes {


    private final ArrayList<Shapes> components;//list of components

    /**
     * @return return the attributes components
     */
    public ArrayList<Shapes> getComponents(){return components;}

    /**
     * @param n name of this group
     * @param shapeList  the components
     */
    public Groups(String n, ArrayList<Shapes> shapeList){
        this.type=Type.GROUP;
        this.grouped=false;
        for(Shapes i:shapeList){
            i.setGrouped(true);   // all update of group has been updated in there
        }
        this.n=n;
        components=shapeList;
    }
    @Override
    final String list(){
        StringBuilder st=new StringBuilder("Name: "+n+"\nType: "+getType()+"\nComponents: ");
        for(Shapes i:components){
            st.append(i.getName()).append("  ");
        }
        return st.toString();
    }
    @Override
    public final box boundingbox(){
        ArrayList<box> boxes= new ArrayList<>();
        for(Shapes s:components){
            boxes.add(s.boundingbox());
        }
        double minx=boxes.get(0).getX(), maxy=boxes.get(0).getY();
        double maxx=minx+boxes.get(0).getW(), miny=maxy-boxes.get(0).getH();
        for(box b:boxes){
            minx=Math.min(minx,b.getX());
            maxx=Math.max(maxx,b.getX()+b.getW());
            miny=Math.min(miny,b.getY()-b.getH());
            maxy=Math.max(maxy,b.getY());
        }
        return new box(minx,maxy,maxx-minx,maxy-miny);
    }

    @Override
    final void move(double dx, double dy){
        for(Shapes s:components){
            s.move(dx,dy);
        }
    }

    @Override
    final double distance(double m, double n){
        double d=components.get(0).distance(m,n);
        for(Shapes s:components){
            d=Math.min(d,s.distance(m,n));
        }
        return d;
    }

    @Override
    final boolean intersects(Shapes other){
        for(Shapes s:components){
            if(s.intersects(other)){
                return true;
            }
        }
        return false;
    }
}