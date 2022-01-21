package hk.edu.polyu.comp.comp2021.clevis.gui;

import javax.swing.*;

/**
 * initiator
 */
public class MyFrame extends JFrame{
    /**
     * name
     */
    public static final String TITLE="Graphical interface";
    /**
     * width
     */
    public static final int WIDTH=900;
    /**
     * height
     */
    public static final int HEIGHT=600;

    /**
     * initiator
     */
    public MyFrame(){
        super();
        // used to refer super class object, JFrame
        initFrame();
    }

    private void initFrame(){
        // set the title and the size of the frame
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Sets the operation that will happen by default
        // when the user initiates a "close" on this frame.

        setLocationRelativeTo(null);


    }
}
