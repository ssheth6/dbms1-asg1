/*******
General instructions on how to execute the program

Setup the environment to run the programa. Install Java on Ubuntu,sudo add-apt-repository ppa:webupd8team/javasudo apt-get updatesudo apt-get install oracle-java8-installerb. Assuming java is installed under the path “/usr/bin/java” (could be something else in your case), the path should be imported on the $PATH environment variable using the terminal,export PATH=/usr/bin:$PATHc. Export the postgres driver path to the CLASSPATH variable (The path for you will be where you download the driver) using the terminal,export CLASSPATH=$CLASSPATH:/Users/sneha/Library/Drivers/postgresql-9.4-1203.jdbc4.jarSteps to execute the programsa. Report 1 – On the terminal run the following commands:javac AllMinMax.javajava AllMinMax [username] [password] [database_name]where username and password are access credentials to the database_name.b. Report 2 – On the terminal run the following commands:javac SubsetMinMax.javajava SubsetMinMax [username] [password] [database_name]where username and password are access credentials to the database_name.
***/

/******
Justification of your choice of data structures for the program

The arraylist supports dynamic arrays that grow as and when needed.
Unlike arrays that are fixed size and cannot grow or shrink, arraylists are created with an initial size and can be enlarged or shrunk when the data grows or reduces respectively.

***/

/******
A detailed description of the algorithm of the program
begin
while(rows exist in resultset)
	if(array list is empty)
		initialize
	else
		foreach(row in the arraylist)
			if(array list.cust == rs.cust && array list.prod == rs.prod)
				if(arraylist.max < rs.quant && arraylist.min < rs.quant)
					arraylist.max = rs.quant
				elseif(arraylist.max > rs.quant && arraylist.min > rs.quant)
					arraylist.min = rs.quant
			else
				arraylist.add(rs.row)
	display arraylist
end


***/

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllMinMax
{
	public static void main (String[] args)
	{
		String usr = args[0]; //first command line argument will be used as the username to connect to the database
		
		String pwd = args[1]; //second command line argument will be used as the password to connect to the database
		
		String url = "jdbc:postgresql://localhost:5432/"+args[2]; //url to connect to the database
		
		//load driver
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		
		//throws an exception if it cannot load the driver
		catch (Exception e)
		{
			System.out.println("Failed to load the driver!");
			e.printStackTrace();
		}
		
		//connect server
		try
		{
			//establish connection with the database
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			
			//get query result in ResultSet rs
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery("select * from sales");
			
			//declare variables to be used in the program
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			ArrayList<String[]> display = new ArrayList<String[]>();
			Date format_date;
			String date="";
			int i=0, j=0, k=0, match=0, nomatch=0;

			//fetch each element from the resultset rs
			while(rs.next())
			{
				match = 0; //initialize variable to flag matching rows
				nomatch = 0; //initialize variable to flag non-matching rows

				//put together the month, day and year in the required mm/dd/yyyy format
				try 
				{
					format_date = (Date) sdf.parse(rs.getString("month") + "/" + rs.getString("day") + "/" + rs.getString("year"));
					date = sdf.format(format_date);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
				
				//initialize the arraylist to start comparing the rows from resultset rs
				//each row has the attributes listed in the following order: cust, prod, max_q, max_date, max_state, min_q, min_date, min_state
				if(display.size() == 0)
				{
					display.add(new String[] {rs.getString("cust"), rs.getString("prod"), rs.getString("quant"), date, rs.getString("state"), rs.getString("quant"), date, rs.getString("state")});
				}
				
				//compare each element of the resultset with the rows in the arraylist
				for(i=0; i < display.size(); i++)
				{
					//the cust and prod of the current row of the resultset matches with any row's cust and prod in the arraylist
					if(display.get(i)[0].equals(rs.getString("cust")) && display.get(i)[1].equals(rs.getString("prod")))
					{
						//increment the flag for a match found
						match++;
						
						//the resultset row's quant is greater than the current min and max
						if(Integer.parseInt(display.get(i)[2]) < Integer.parseInt(rs.getString("quant")) && Integer.parseInt(display.get(i)[5]) < Integer.parseInt(rs.getString("quant")))
						{
							display.get(i)[2] = rs.getString("quant"); //set max quant to the resultset row's quant
							display.get(i)[3] = date; //set date for max quant to the resultset row's date
							display.get(i)[4] = rs.getString("state"); //set state for max quant to the resultset row's state
						}
						
						//the resultset row's quant is smaller than the current min and max
						else if(Integer.parseInt(display.get(i)[2]) > Integer.parseInt(rs.getString("quant")) && Integer.parseInt(display.get(i)[5]) > Integer.parseInt(rs.getString("quant")))
						{
							display.get(i)[5] = rs.getString("quant"); //set min quant to the resultset row's quant
							display.get(i)[6] = date; //set date for min quant to the resultset row's date
							display.get(i)[7] = rs.getString("state"); //set state for min quant to the resultset row's state
						}
					}
					
					//the cust and prod of the current row of the resultset does not match with any row's cust and prod in the arraylist
					else if(!display.get(i)[0].equals(rs.getString("cust")) && !display.get(i)[1].equals(rs.getString("prod")))
					{
						nomatch++; //increment the flag for no match found
					}
				}
				
				//for the current resultset row, no match is found in the entire arraylist, add that row to the arraylist
				if(match == 0 && nomatch > 0)
				{
					display.add(new String[] {rs.getString("cust"), rs.getString("prod"), rs.getString("quant"), date, rs.getString("state"), rs.getString("quant"), date, rs.getString("state")});
				}
				
			}
			
			//print the arraylist to output the max and min quant for each cust and prod
			System.out.println("CUSTOMER" + "  " + "PRODUCT" + "   " + "MAX_Q" + "  " + "DATE" + "        " + "ST" + "  " + "MIN_Q" + "  " + "DATE" + "        " + "ST" + "  " + "AVG_Q");
			System.out.println("========" + "  " + "========" + "  " + "=====" + "  " + "==========" + "  " + "==" + "  " + "=====" + "  " + "==========" + "  " + "==" + "  " + "=======");
			for(j=0; j < display.size(); j++)
			{
				System.out.printf("%-9s %-7s %7s %11s %3s %6s %11s %3s %9s", display.get(j)[0], display.get(j)[1], display.get(j)[2], display.get(j)[3], display.get(j)[4], display.get(j)[5], display.get(j)[6], display.get(j)[7], (Float.parseFloat(display.get(j)[2]) + Float.parseFloat(display.get(k)[5]))/2 + "\n");
			}
		}
		
		//throws an exception if there are errors connecting to the database
		catch (SQLException e)
		{
			System.out.println("Connection URL or username or password errors!"); 
			e.printStackTrace();
		}
	}
}
