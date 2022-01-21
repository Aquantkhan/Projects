import java.util.*;
import java.io.*;
import java.io.Console;
import java.sql.*;
import oracle.jdbc.driver.*;
import oracle.sql.*;

public class Clients
{

	public static void main(String args[]) throws SQLException, IOException
	{
		Console console = System.console();
		System.out.print("Enter your username: ");
        String username = console.readLine();
        System.out.print("Enter your password: ");
        char[] password = console.readPassword();
		String pwd = String.valueOf(password);

		// Connection
		DriverManager.registerDriver(new OracleDriver());
		OracleConnection conn = 
			(OracleConnection)DriverManager.getConnection(
			 "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms",username,pwd);

		System.out.print("1 -->> Customer \n" +
				"2 -->> Technician  \n" +
				"3 -->> Administrator\n" +
				"-1 -->> Exit\n" +
				"Please enter the number of your position:");
		Scanner pos = new Scanner(System.in);
		int posNum = 4;
		if (pos.hasNextLine()) posNum = Integer.parseInt(pos.nextLine());

		switch (posNum){
			case 1 :
			{new Customer(conn);
			break;}

			case 2 : {
					System.out.print("Please enter your Technician id:");
					Scanner id = new Scanner(System.in);
					String idNum = null;
					if (id.hasNextLine()) idNum = pos.nextLine();
					new Technician(idNum, conn);
			}

			case 3 :
			{new Administrator(conn);
			break;}
			case 4 : {break;}
			default : {throw new IllegalArgumentException("Please enter a legit number.");}
		}
		conn.close();
		System.out.println("The execution of the application has ended.");


	}
}
