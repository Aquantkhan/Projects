package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.gui.MyWindow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
/**
 * receiver of order/command in operation interface
 */
public class CommandLine
{
    private static MyWindow mywindow;
    /**
     *operations stores all commands typed by user that can be undone
     */
    static final Stack<String[]> operations= new Stack<>();//contains those changing the ArrayList only
    /**
     * undoneoperations will store the order been popped from operations
     */
    static final Stack<String[]> undoneoperations= new Stack<>();
    /**
     * deletedposition store those Shapes that been moved from store queue : a
     */
    static final Stack<Integer> deletedposition= new Stack<>();
    /**
     * meanwhile deletedshapes stores the info/data of deleted Shapes,
     * one thing need to be focused is that all these two push are executed before "remove"
     */
    static final Stack<Shapes> deletedshapes= new Stack<>();
    /**
     * stores those been ungrouped/removed group, prepare for command undo
     */
    static final Stack<Groups> ungroupedgroups= new Stack<>();
    /**
     * basically the same utility as deleteposition
     */
    static final Stack<Integer> ungroupedposition= new Stack<>();
    /**
     * this "a" stores all the order(expect those been moved)
     */
    static final ArrayList<Shapes> a = new ArrayList<>();
    private final static int maxvalue=50;
    private static String[] values=new String[maxvalue];

    /**
     * for customized error warning
     */
    public static class QuitException extends Exception{}
    /**
     * for customized error warning
     */
    static class DuplicateNameException extends Exception{}

    private static FileCreator file;

    /**
     * as its name, initiator
     * @param htmlname name of html file
     * @param txtname name of txt file
     * @throws IOException if file(s) cannot be created
     */
    public static void initialize(String htmlname, String txtname)throws IOException
    {
        if(!htmlname.isEmpty() && !txtname.isEmpty())
            file = new FileCreator(htmlname, txtname);
        else
            System.out.println("No log files specified. Commands will not be logged.");
        values = new String[maxvalue];
    }

    /**
     * utility as its name
     * @param s input order
     * @throws QuitException customized error warning
     */
    public static void getInput(String s)throws QuitException
    {
        String full_command = purify(s);
        values = getValues(full_command);
        // values contain all the sub-strings separated into indices, command, name, values, etc.
        try{
        	Execute(values);
        	if(file!=null)
        	    file.fileWrite(full_command);
        	switch(values[0]){
        		case "group":
        		case "ungroup":
        			undoneoperations.clear(); 
        			break;
        		case "line":
        		case "circle":
        		case "rectangle":
        		case "square":
        		case "move":
        		case "pick-and-move":
        		case "delete":
        			undoneoperations.clear(); 
        		case "undo":
        		case "redo":
        			if(mywindow!=null)mywindow.destroy();
        			mywindow=new MyWindow(a);       			
        			break;
        	}
        }catch(IllegalArgumentException e){
        	System.out.println("Commmand is incorrect. Please try again.");
        }catch(DuplicateNameException e){
        	System.out.println("This name already exists. Please try again.");
        }
        //print();

    }


    static private String purify(String s)
    {
        if(s == null)
            throw new NullPointerException("the Command is null");
        s=s.trim();

        StringBuilder res = new StringBuilder();
        for(int i = 0; i < s.length(); i++)
        {
            if(s.charAt(i) == ' ' && s.charAt(i + 1) == ' ')
            {
                continue;
            }
            res.append(s.charAt(i));
        }
        s = res.toString();
        //System.out.println(s);
        return s;
    }

    private static String[] getValues(String s)
    {
        StringBuilder res = new StringBuilder();
        String[] arguments = new String[maxvalue];
        s = s.trim();
        int j = 0;
        for(int i = 0; i < s.length(); i++)
        {
            if(s.charAt(i) == ' ')
            {
                arguments[j] = res.toString();
                res.delete(0, res.length());
                j++;
            }
            else
                res.append(s.charAt(i));
        }
        arguments[j] = res.toString();
        return arguments;
    }

    /**
     * OK, this one corresponding for REQ6, dissolve the group and kick it out from order pool "a"
     * @param g the unlucky one will be kicked out
     */
    public static void ungroup(Groups g){
        for(Shapes i:g.getComponents()){
            i.setGrouped(false);
        }
        ungroupedgroups.push(g);
        ungroupedposition.push(a.indexOf(g));
        a.remove(g);
    }

    /**
     * delete a shape.
     * @param s the Shape will be deleted
     */
    public static void delete(Shapes s){
        if(s.getType()==Shapes.Type.GROUP){
            for(int i=0;i<((Groups)s).getComponents().size();i++){
                delete(((Groups)s).getComponents().get(i));
            }
        }
        deletedshapes.push(s);
        deletedposition.push(a.indexOf(s));
        a.remove(s);
    }
    /**
     * as its name
     * @param s needed shapes
     * @param indentations for a beauty and neat print
     */
    public static void listDetails(Shapes s, int indentations){
        StringBuilder sb=new StringBuilder();
        sb.append("    ".repeat(indentations));
        String st=s.list();
        for(int i=0;i<st.length();i++){
            sb.append(st.charAt(i));
            if(st.charAt(i)=='\n'){
                sb.append("    ".repeat(indentations));
            }
        }
        System.out.println(sb);
        if(s.getType()==Shapes.Type.GROUP){
            for(Shapes shape:((Groups)s).getComponents()){
                listDetails(shape,indentations+1);
            }
        }
        System.out.println();
    }

    /**
     * as its name. Req11? REQ12?. I forget it. but it's in REQs
     */
    public static void listAll(){
        for(int i = a.size()-1; i>=0; i--){
            if(!a.get(i).isGrouped()){
                listDetails(a.get(i),0);
            }
        }
    }
	
	private static void checkArgs(String[] s, int len)throws IllegalArgumentException
	{
		int count=0;
        for (String value : s) {
            if (value == null) break;
            else count++;
        }
		if(count!=len)throw new IllegalArgumentException();
	}
	
	private static void checkName(String name)throws DuplicateNameException
	{
		for(Shapes i:a)
			if(i.getName().equals(name))
				throw new DuplicateNameException();
	}
	
    private static void Execute(String[] s)throws DuplicateNameException,QuitException,IllegalArgumentException
    {
        //System.out.println(Arrays.toString(s));
        String command = s[0].toLowerCase();
        boolean found = false;
        switch (command)
        {
            default:
            	throw new IllegalArgumentException();
            case "listall":
            	checkArgs(s,1);
                listAll();
                break;
            case "list":
            	checkArgs(s,2);
                int r;
                for( r = 0; r < a.size(); r++)
                {
                	if(a.get(r).isGrouped())continue;
                    if(a.get(r).getName().trim().equals(s[1].trim()))
                    {
                        found = true;
                        break;
                    }
                }                
                if(!found)
                    System.out.println("Shape does not exist.");
                else{
                	System.out.println(a.get(r).list());
                }
                break;
                
            case "intersect":
            	checkArgs(s,3);
                for(Shapes i : a)
                {
                    if(i.getName().equals(s[1]) && !i.isGrouped())
                    {
                        for(Shapes j : a)
                        {
                            if(j.getName().equals(s[2]) && !j.isGrouped())
                            {
                                found = true;
                                System.out.println(i.intersects(j));
                                break;
                            }
                        }
                        break;
                    }
                }
                if(!found)
                    System.out.println("Shape does not exist.");
                break;
                
            case "pick-and-move":
            	checkArgs(s,5);
                for(int i=a.size()-1;i>=0;i--)
                {
                    if(a.get(i).contains(Double.parseDouble(s[1]), Double.parseDouble(s[2])))
                    {
                        a.get(i).move(Double.parseDouble(s[3]), Double.parseDouble(s[4]));
                        operations.push(new String[]{"move",a.get(i).getName(),s[3],s[4]});
                        found = true;
                        break;
                    }
                }
                if(!found)
                    System.out.println("No shape contains (" + s[1] + "," + s[2] + ").");
                break;
                
            case "move":
            	checkArgs(s,4);
                for(Shapes i : a)
                {
                    if (i.getName().equals(s[1]) && !i.isGrouped())
                    {
                        found = true;
                        i.move(Double.parseDouble(s[2]), Double.parseDouble(s[3]));
                        operations.push(s);
                        break;
                    }
                }
                if(!found)
                    System.out.println( "Shape " + s[1] + " does not exist.");
                break;
                
            case "boundingbox":
            	checkArgs(s,2);
                for(Shapes i : a)
                {
                    if (i.getName().equals(s[1]) && !i.isGrouped())
                    {
                        found = true;
                        System.out.println(i.boundingbox());
                    }
                }
                if(!found)
                    System.out.println( "Shape " + s[1] + " does not exist.");
                break;
                
            case "delete":
            	checkArgs(s,2);
                int k;
                for(k = 0; k < a.size(); k++)
                {
                    if (a.get(k).getName().equals(s[1]) && !a.get(k).isGrouped())
                    {
                        found = true;
                        break;
                    }
                }
                if(!found)
                    System.out.println( "Shape " + s[1] + " does not exist.");
                else{
                	delete(a.get(k));
                    operations.push(s);
                }
                break;
                
            case "ungroup":
            	checkArgs(s,2);
                Shapes temps=null;
                for(Shapes i : a)
                {
                    if(i.getType()==Shapes.Type.GROUP && i.getName().equals( s[1]) && !i.isGrouped())
                    {
                            temps=i;
                            break;                            
                    }
                }
                if(temps==null)
                    System.out.println( "Group " + s[1] + " does not exist.");
                else{
                    operations.push(s);
                    ungroup((Groups) temps);
                }
                break;
                
            case "group":
            	if(s[2]==null)throw new IllegalArgumentException();
            	checkName(s[1]);
                ArrayList<Shapes> b = new ArrayList<>();
                boolean flag=false;
                for(int j = 2; j < s.length; j++ )
                {
                    if(s[j]==null)break;
                    Shapes temp=null;
                    for(Shapes i : a)
                    {
                        if(i.getName().equals(s[j]) && !i.isGrouped()){
                        	temp=i;
                        	break;
                        }
                    }
                    if(temp==null){
                    	flag=true;
                    	break;
                    }
                    b.add(temp);
                }
                if(flag)
                	throw new IllegalArgumentException();
                else{
                	operations.push(s);
                	a.add(new Groups(s[1], b));
                }                
                break;
                
            case "square":
            	checkArgs(s,5);
            	checkName(s[1]);
                a.add(new Squares(s[1], Double.parseDouble(s[2]), Double.parseDouble(s[3]), Double.parseDouble(s[4])));
                operations.push(s);
                break;
                
            case "circle":
            	checkArgs(s,5);
            	checkName(s[1]);
                a.add(new Circles(s[1], Double.parseDouble(s[2]), Double.parseDouble(s[3]), Double.parseDouble(s[4])));
                operations.push(s);
                break;
                
            case "line":
            	checkArgs(s,6);
            	checkName(s[1]);
                a.add(new Lines(s[1], Double.parseDouble(s[2]),Double.parseDouble(s[3]), Double.parseDouble(s[4]), Double.parseDouble(s[5])));
                operations.push(s);                
                break;
                
            case "rectangle":
            	checkArgs(s,6);
            	checkName(s[1]);
                a.add(new Rectangles(s[1], Double.parseDouble(s[2]),Double.parseDouble(s[3]), Double.parseDouble(s[4]), Double.parseDouble(s[5])));
                operations.push(s);                
                break;
            
            case "undo":
            	checkArgs(s,1);
            	if(operations.isEmpty())
            		throw new IllegalArgumentException();
            	String[] op=operations.pop();
            	undoneoperations.push(op);
            	//System.out.println("undoing"+Arrays.toString(op));
            	switch(op[0]){
            		case "line":
            		case "circle":
            		case "square":
            		case "rectangle":
            			for(int i=a.size()-1;i>=0;i--)
            				if(a.get(i).getName().equals(op[1])){
            					a.remove(i);
            					break;
            				}
            			break;
            		case "group":
            			for(int i=a.size()-1;i>=0;i--)
            				if(a.get(i).getName().equals(op[1])){
            					for(Shapes shape:((Groups)a.get(i)).getComponents())
            						shape.setGrouped(false);
            					a.remove(i);
            					break;
            				}
            			break;
            		case "ungroup":
            			Groups g=ungroupedgroups.pop();
            			int pos=ungroupedposition.pop();
            			a.add(pos,g);
            			for(Shapes shape:g.getComponents())
            				shape.setGrouped(true);
            			break;
            		case "delete":
            			Shapes shape=deletedshapes.pop();
            			int pos2=deletedposition.pop();
            			if(shape.getType()!=Shapes.Type.GROUP){
            				a.add(pos2,shape);
            			}else{
            				a.add(pos2,shape);
            				for(int i=0;i<groupsize((Groups)shape);i++)
            					a.add(deletedposition.pop(),deletedshapes.pop());
            			}
            			break;
            		case "move":
            			for(Shapes shape2:a){
            				if(shape2.getName().equals(op[1])){
            					shape2.move(-Double.parseDouble(op[2]),-Double.parseDouble(op[3]));
            				}
            			}
            			break;
            			//pick-and-move is converted to move before pushed into operations
            	}
            	break;
            	
            case "redo":
            	checkArgs(s,1);
            	if(undoneoperations.isEmpty())
            		throw new IllegalArgumentException();
            	Execute(undoneoperations.pop());
            	break;
            	
            case "quit":
            	checkArgs(s,1);
            	if(mywindow!=null)mywindow.destroy();
            	throw new QuitException();
        }
    }
    
    private static int groupsize(Groups g){
    	int size=0;
    	for(Shapes s:g.getComponents()){
    		size++;
    		if(s.getType()==Shapes.Type.GROUP)
    			size+=groupsize((Groups)s);
    	}
    	return size;
    }
}
