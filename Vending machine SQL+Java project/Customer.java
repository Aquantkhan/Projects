import java.util.*;
import java.io.*;
import java.io.Console;
import java.sql.*;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.*;
import oracle.sql.*;

public class Customer {
    private PreparedStatement pstmt;
    private OracleConnection Conn;
    public Customer(OracleConnection Conn) throws SQLException {
        this.Conn = Conn;
        boolean run = true;
        while (run) {
            System.out.print("1 -->> Get addresses of vending machine that has a certain item \n" +
                            "2 -->> Get a list of items in a vending machine with a certain address\n" +
                            "-1 -->> Exit\n" +
                            "Please select the type of query you like to make:\n");
            Scanner operation = new Scanner(System.in);
            int operationNum;
            if (operation.hasNextLine()) {
                operationNum = Integer.parseInt(operation.nextLine());
                try{
                    switch (operationNum) {

                        case 1 : {
                            System.out.print("Enter the name of the item that you would like to buy: ");
                            Scanner item = new Scanner(System.in);
                            this.getMachine(item.nextLine());
                            break;
                        }

                        case 2 : {
                            System.out.print("Enter the address of the machine that you would like to check: ");
                            Scanner machine = new Scanner(System.in);
                            this.getItems(machine.nextLine());
                            System.out.print("The task has been set as finished.");
                            break;
                        }
                        case -1 : {
                            run = false;
                            break;
                        }
                        default :{
                            throw new IllegalArgumentException("Please enter a legit number.");
                        }
                    }
                } catch (IllegalArgumentException e){System.out.println(e.getMessage());}
            }
        }
    }

    public void getMachine(String name) throws SQLException {
        try {
            pstmt = Conn.prepareStatement("SELECT Vending_Machine.Adress " +
                    "FROM (Vending_Machine INNER JOIN " +
                    "(Warehouse INNER JOIN " +
                    "((SELECT Delivery_Invoice.Delivery_ID FROM Delivery_Invoice) INNER JOIN" +
                    "(SELECT Item.Item_ID FROM Item WHERE Item_Name = ?) " +
                    "ON Item.Item_ID = Delivery_Invoice.Item_ID) " +
                    "ON Warehouse.Warehouse_ID = Delivery_Invoice.Warehouse_ID)" +
                    "ON Vending_Machine.Warehouse_ID = Warehouse.Warehouse_ID)");
            pstmt.setString(1, name);
            ResultSet rset = pstmt.executeQuery();
            if (!rset.next()) throw new IllegalArgumentException("Wrong item name or empty stock.");
            while (rset.next()) {
                System.out.println(rset.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something is wrong with SQL.");
        }
    }

    public void getItems(String adress) throws SQLException {
        try {
            pstmt = Conn.prepareStatement("SELECT Item.Item_Name, Item.Sell_Price " +
                    "FROM (Item INNER JOIN " +
                    "(Delivery_Invoice INNER JOIN " +
                    "((SELECT Refill_Invoice.Delivery_ID FROM Refill_Invoice) INNER JOIN " +
                    "(SELECT * FROM Vending_Machine WHERE Adress = ?) " +
                    "ON Vending_Machine.Vending_Machine_ID = Refill_Invoice.Machine_ID) " +
                    "ON Delivery_Invoice.Delivery_ID = Refill_Invoice.Delivery_ID)" +
                    "ON Item.Item_ID = Delivery_Invoice.Item_ID)");
            pstmt.setString(1, adress);
            ResultSet rset = pstmt.executeQuery();
            if (!rset.next()) throw new IllegalArgumentException("Wrong item name or empty stock.");
            while (rset.next()) {
                System.out.println(rset.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something is wrong with SQL.");
        }
    }

}







