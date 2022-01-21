package hk.edu.polyu.comp.comp2021.clevis.model;//package hk.edu.polyu.comp.comp2021.clevis;
//import hk.edu.polyu.comp.comp2021.clevis.model.*;
import hk.edu.polyu.comp.comp2021.clevis.gui.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest{
    CommandLine receiver;
    String input;
    Circles C1;
    Circles C2;

    Lines test01;
    Lines test02;

    Rectangles R1;
    Rectangles R2;

    Squares S1;
    Squares S2;

    Groups G1;
    Groups G2;

    ArrayList<Shapes> temp;
    ArrayList<Shapes> simuantounes;
    @BeforeEach
    public void annCommandLineTest() {
        test01=new Lines("y=5x+7", 0, 7, -1, 2);
        test02=new Lines("y=-4.1x+5.178", 0, 5.178, 10, -35.822);

        C1=new Circles("(x-7.7)^2+(y-2.168)^2=1.956^2", 7.7, 2.168, 1.956);
        C2=new Circles("(x+2.7)^2+(y-5)^2=1^2", -2.7, 5, 1);

        R1=new Rectangles("R1", -3, 5, 2, 6);
        R2=new Rectangles("R2", 2, 3.66011, 5.1, 0.003);

        S1=new Squares("S1", 3, -1.007, 5);
        S2=new Squares("S2", 1.3, -6.4, 3);

        temp=new ArrayList<Shapes>();
        temp.add(C1); temp.add(R1); temp.add(S1); temp.add(test01);
        simuantounes=new ArrayList<>();
        simuantounes.add(C2); simuantounes.add(R2); simuantounes.add(S2); simuantounes.add(test02);
        G1=new Groups("G1", temp);
        G2=new Groups("G2", simuantounes);
        try {
            CommandLine.initialize("html.html", "txt.txt");}
        catch (IOException e) {

        }

    }

    /**
     *
     * @throws CommandLine.QuitException handle in application
     * IMPORTANT: Please make sure the files "html.html" and "txt.txt"
     * do not exist in the project directory, otherwise unit test will not
     * run correctly.
     *
     * DELETE "html.html" and "txt.txt" before re-test!!!!
     * DELETE "html.html" and "txt.txt" before re-test!!!!
     */

    @Test
    public void executeonce() throws CommandLine.QuitException {
        CommandLine.a.add(C1);
        CommandLine.delete(C1);
        String[] tys=new String[] {
        "rectangle letstry 5 2 6.0 3.3",
        "circle letstry1 5 2 6.0",
        "square letstry2 5 2 3.3",
        "group letstry3 letstry1 letstry2",
        "line letstry4 5 2 6.0 3.3",
        "listall",
        "boundingbox letstry3",
        "move letstry3 4 5",
        "list letstry3",
        "pick-and-move 5 2 1 1",
        "undo", "undo","redo",
        "redo","redo","undo","intersect letstry4 letstry3",
        "ungroup letstry3","undo","delete letstry3"
        ,"rectangle rr 1 2 3 4", "undo", "redo",
        "group gg rr", "undo", "redo", "delete gg", "undo"};
        for(int i=0;i<tys.length;i++){
            CommandLine.getInput(tys[i]);}
        String stop="quit";
        try {
            CommandLine.getInput(stop);
        }catch (CommandLine.QuitException ignored){

        }}


}