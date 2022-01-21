import java.util.*;
import java.sql.*;

import oracle.jdbc.OracleConnection;

public class Technician {
    private final String id;
    private OracleConnection Conn;
    private PreparedStatement pstmt;

    public Technician (String id, OracleConnection Conn) throws SQLException {
        this.id = id;
        this.Conn = Conn;
        boolean run = true;
        while (run) {
        System.out.print(
                "1 -->> Get your refill invoice \n" +
                "2 -->> Get a list of task to be done  \n" +
                "3 -->> Get the location of your warehouse  \n" +
                "-1 -->> Exit\n" +
                "Please select the type of query you like to make:\n");
        Scanner operation = new Scanner(System.in);
        int operationNum;
            if (operation.hasNextLine()) {
                operationNum = Integer.parseInt(operation.nextLine());
                try{
                    switch (operationNum) {

                        case 1 :
                        {
                            this.getRefillInvoice();
                            break;
                        }

                        case 2 : {
                            this.getTasks();

                            System.out.print("Enter the task id number if you want to set a job as done. Enter -1 to exit.");
                            Scanner job = new Scanner(System.in);
                            this.setJobDone(job.nextLine());
                            System.out.print("The task has been set as finished.");
                            break;
                        }

                        case 3 :{
                            this.getLocation();
                            break;
                        }

                        case -1 :{

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

    public void getRefillInvoice() {
        try {
            pstmt = Conn.prepareStatement("SELECT Refill_Invoice.Refill_ID, Delivery_Invoice.Item_ID, Refill_Invoice.Machine_ID, Refill_Invoice.Quantity, Refill_Invoice.Refill_Date " +
                    "FROM Refill_Invoice, Delivery_Invoice " +
                    "WHERE Refill_Invoice.Technician_ID = ? AND Refill_Invoice.Delivery_ID = Delivery_Invoice.Delivery_ID");
            pstmt.setInt(1,Integer.parseInt(id));
            ResultSet rset = pstmt.executeQuery();
            while (rset.next())
            {
                System.out.println(rset.getString(1)
                        + " " + rset.getString(2)
                        + " " + rset.getString(3)
                        + " " + rset.getString(4)
                        + " " + rset.getString(5)
                        + " " + rset.getString(6));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTasks()  {
        try {
            pstmt = Conn.prepareStatement("SELECT * FROM Task WHERE Technician_ID = ? AND Status = ?");
            pstmt.setInt(1,Integer.parseInt(id));
            pstmt.setString(2,"In Progress");
            ResultSet rset = pstmt.executeQuery();
            while (rset.next())
            {
                System.out.println(rset.getString(1)
                        + " " + rset.getString(2)
                        + " " + rset.getString(3)
                        + " " + rset.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void setJobDone(String jobID) {
        try {
            pstmt = Conn.prepareStatement("UPDATE Task SET Status = ? WHERE Job_id = ?");
            pstmt.setString(1,"Completed");
            pstmt.setInt(2,Integer.parseInt(jobID));
            int rowNum0 = pstmt.executeUpdate();
            if (rowNum0 == 0) throw new IllegalArgumentException("Wrong job ID.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getLocation()  {
        try {
            pstmt = Conn.prepareStatement("SELECT Adress FROM Warehouse WHERE Warehouse.Warehouse_ID = " +
                    "(SELECT Warehouse_ID FROM Technician WHERE Technician_ID = ?");
            pstmt.setInt(1,Integer.parseInt(id));
            ResultSet rset = pstmt.executeQuery();
            while (rset.next())
            {
                System.out.println(rset.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
