package hk.edu.polyu.comp.comp2021.clevis.model;//package hk.edu.polyu.comp.comp2021.clevis;
//import hk.edu.polyu.comp.comp2021.clevis.model.*;
import hk.edu.polyu.comp.comp2021.clevis.gui.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ShapeTest {
    Lines test01;
    Lines test_parall01;
    Lines vertical;
    Lines horizontal;
    Lines interacts;

    Circles C1;
    Circles C2;
    Circles C3;

    Rectangles R1;
    Rectangles R2;
    Rectangles R3;

    Squares S1;
    Squares S2;
    Squares S3;

    Groups G1;
    Groups G2;

    ArrayList<Shapes> temp;
    ArrayList<Shapes> simuantounes;

    @BeforeEach
    public void prepare() {
        test01=new Lines("y=4x+3", 0, 3, -1, -1);
        test_parall01=new Lines("y=4x-11", 2,-3,3,1);
        vertical=new Lines("x=3", 3, 2, 3, 7);
        horizontal = new Lines("y=8", 6, 8, 1096.777, 8.0000099);
        interacts=new Lines("y=-4x+7", 2, -1, 6.25, -18.0);

        C1=new Circles("C1",1,1.2,1.98);
        C2=new Circles("C1",1,1.2,1.98879);
        C3=new Circles("C3", 1,0.2,1.88);

        R1=new Rectangles("R1", 2.25, -2.003,2,6);
        R2=new Rectangles("R2", 2,3.000011, 2, 5);
        R3=new Rectangles("R3", 5.99988,9.400011,1.0000221,1.000023);

        S1=new Squares("S1", 6,9.4, 1);
        S2=new Squares("S2", 0,1.45, 3);
        S3=new Squares("S3", 0,1,1);

        temp= new ArrayList<Shapes>();
        temp.add(new Circles("c1", 3.05, 2.22, 3.14));
        temp.add(C1); temp.add(C2);
        simuantounes=new ArrayList<>();
        simuantounes.add(C3); simuantounes.add(R1); simuantounes.add(R2);
        simuantounes.add(S3);

        G1=new Groups("G1", temp);
        G2=new Groups("G2", simuantounes);
    }

    @Test
    void list() {
        test01.list();
        horizontal.list();
        interacts.list();
    }

    @Test
    void boundingbox() {
        assertEquals("-1.00 3.00 1.00 4.00", test01.boundingbox().toString()); // Lines
        assertEquals("-0.98 3.18 3.96 3.96", C1.boundingbox().toString());     // Circles
        assertEquals("2.00 3.00 2.00 5.00", R2.boundingbox().toString());      // Rectangles
        assertEquals("6.00 9.40 1.00 1.00", S1.boundingbox().toString());      // Square
    }

    @Test
    void move() {
        R1.move(2,0);
        assertTrue(4.25==R1.getX() && -2.003==R1.getY());
        test01.move(0,0.113);
        assertTrue(test01.getX1()==0 && test01.getX2()==-1 && test01.gety1()==3.113 && test01.gety2()==-0.887);
        C1.move(2.96, 3.00011);
        assertTrue(C1.getX()==3.96 && C1.getY()==4.20011);
    }

    @Test
    void distance() {  // distance is a auxiliary function, pls dont pay attention on this.
        // this test for vertical line
        assertEquals((double) 5, vertical.distance(7,10));
        assertEquals(5.77, vertical.distance(3.0, 12.77)); //?
        assertEquals(4.265, vertical.distance(7.265, 5.5));
        // this test for horizontal line
        assertEquals(0.24439517184261325, horizontal.distance(1097,7.8999999999));
    }

    @Test
    void intersects_Lines() {
    // we here calculate the intersects between two Line segment
        assertFalse(vertical.intersects(horizontal));
        assertFalse(test01.intersects(test_parall01));
        assertFalse(test01.intersects(interacts));
    }

    @Test
    void Intersect_Circle_Lines() {
    // specifically for circle and line segment
        assertFalse(test_parall01.intersects(C1));
        assertTrue(test_parall01.intersects(C2));
    // Circle with Circle
        // Concentric circle
        assertFalse(C1.intersects(C2));
        assertTrue(C3.intersects(C1));
    }

    @Test
    void Intersects_Square_Rectangle() {
    // rectangle
        assertFalse(test_parall01.intersects(R1));
        assertFalse(R1.intersects(R2));
        assertTrue(R2.intersects(C1));
    // Square
        assertTrue(S2.intersects(S3));
        assertFalse(S1.intersects(S2));
    }

    @Test
    void Interects_Group() {
        assertEquals( 0.41795006481866914, G1.distance(0,0));
        assertTrue(G1.intersects(G2));
    }

    @Test
    void built_2DGraphic() {
        MyWindow newmy=new MyWindow(temp);
        MyWindow newmy2=new MyWindow(simuantounes);
    }

}