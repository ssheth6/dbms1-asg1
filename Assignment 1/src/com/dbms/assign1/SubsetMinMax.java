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
			if(arraylist.cust = rs.cust and arraylist.prod = rs.prod)
				if(rs.state = NY and rs.year>2000 and rs.year<2005)
					if(arraylist.ny_max not empty)
						if(arraylist.ny_max < rs.quant)
							arraylist.ny_max = rs.quant
					else set value
				elseif(rs.state = NJ and rs.year>2000 and rs.year<2005)
					if(arraylist.nj_max not empty)
						if(arraylist.nj_max < rs.quant)
							arraylist.nj_max = rs.quant
					else set value
				elseif(rs.state = CT)
					if(arraylist.ct_min not empty)
						if(arraylist.ct_min > rs.quant)
							arraylist.ct_min = rs.quant
					else set value
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

public class SubsetMinMax
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
			int i=0, j=0, match=0, nomatch=0;
			
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
				//each row has the attributes listed in the following order: cust, prod, ny_max, ny_date, nj_max, nj_date, ct_min, ct_date
				if(display.size() == 0)
				{
					if(rs.getString("state").equals("NY") && (Integer.parseInt(rs.getString("year")) > 2000 && Integer.parseInt(rs.getString("year")) < 2005))
					{
						display.add(new String []{rs.getString("cust"), rs.getString("prod"), rs.getString("quant"), date, "<NULL>", "<NULL>", "<NULL>", "<NULL>"});
					}
					else if(rs.getString("state").equals("NJ") && (Integer.parseInt(rs.getString("year")) > 2000 && Integer.parseInt(rs.getString("year")) < 2005))
					{
						display.add(new String []{rs.getString("cust"), rs.getString("prod"), "<NULL>", "<NULL>", rs.getString("quant"), date, "<NULL>", "<NULL>"});
					}
					else if(rs.getString("state").equals("CT"))
					{
						display.add(new String []{rs.getString("cust"), rs.getString("prod"), "<NULL>", "<NULL>", "<NULL>", "<NULL", rs.getString("quant"), date});
					}
				}
				
				//compare each element of the resultset with the rows in the arraylist
				for(i=0; i < display.size(); i++)
				{
					//the cust and prod of the current row of the resultset matches with any row's cust and prod in the arraylist
					if(display.get(i)[0].equals(rs.getString("cust")) && display.get(i)[1].equals(rs.getString("prod")))
					{
						//increment the flag for a match found
						match++;
						
						//the current resultset row has state as NY and year between 2000 and 2005
						if(rs.getString("state").equals("NY") && (Integer.parseInt(rs.getString("year")) > 2000 && Integer.parseInt(rs.getString("year")) < 2005))
						{
							//current ny_max is not empty in the arraylist
							if(!display.get(i)[2].equals("<NULL>"))
							{
								//the ny_max in the arraylist is less than the quant in the current resultset row
								if(Integer.parseInt(display.get(i)[2]) < Integer.parseInt(rs.getString("quant")))
								{
									display.get(i)[2] = rs.getString("quant"); //set the current row's quant as ny_max
									display.get(i)[3] = date; //set the corresponding date as well
								}
							}
							//current ny_max is empty in the arraylist, then set it with a value
							else
							{
								display.get(i)[2] = rs.getString("quant"); //set the current row's quant as ny_max
								display.get(i)[3] = date; //set the corresponding date as well
							}
						}
						
						//the current resultset row has state as NJ and year between 2000 and 2005
						else if(rs.getString("state").equals("NJ") && (Integer.parseInt(rs.getString("year")) > 2000 && Integer.parseInt(rs.getString("year")) < 2005))
						{
							//current nj_max is not empty in the arraylist
							if(!display.get(i)[4].equals("<NULL>"))
							{
								//the nj_max in the arraylist is less than the quant in the current resultset row
								if(Integer.parseInt(display.get(i)[4]) < Integer.parseInt(rs.getString("quant")))
								{
									display.get(i)[4] = rs.getString("quant"); //set the current row's quant as nj_max
									display.get(i)[5] = date; //set the corresponding date as well
								}
							}
							//current nj_max is empty in the arraylist, then set it with a value
							else
							{
								display.get(i)[4] = rs.getString("quant"); //set the current row's quant as nj_max
								display.get(i)[5] = date; //set the corresponding date as well
							}
						}
						
						//the current resultset row has state as CT
						else if(rs.getString("state").equals("CT"))
						{
							//current ct_mix is not empty in the arraylist
							if(!display.get(i)[6].equals("<NULL>"))
							{
								//the ct_min in the arraylist is greater than the quant in the current resultset row
								if(Integer.parseInt(display.get(i)[6]) > Integer.parseInt(rs.getString("quant")))
								{
									display.get(i)[6] = rs.getString("quant"); //set the current row's quant as ct_min
									display.get(i)[7] = date; //set the corresponding date as well
								}
							}
							//current ct_min is empty in the arraylist, then set it with a value
							else
							{
								display.get(i)[6] = rs.getString("quant"); //set the current row's quant as ct_min
								display.get(i)[7] = date; //set the corresponding date as well
							}
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
					if(rs.getString("state").equals("NY") && (Integer.parseInt(rs.getString("year")) > 2000 && Integer.parseInt(rs.getString("year")) < 2005))
					{
						display.add(new String []{rs.getString("cust"), rs.getString("prod"), rs.getString("quant"), date, "<NULL>", "<NULL>", "<NULL>", "<NULL>"});
					}
					else if(rs.getString("state").equals("NJ") && (Integer.parseInt(rs.getString("year")) > 2000 && Integer.parseInt(rs.getString("year")) < 2005))
					{
						display.add(new String []{rs.getString("cust"), rs.getString("prod"), "<NULL>", "<NULL>", rs.getString("quant"), date, "<NULL>", "<NULL>"});
					}
					else if(rs.getString("state").equals("CT"))
					{
						display.add(new String []{rs.getString("cust"), rs.getString("prod"), "<NULL>", "<NULL>", "<NULL>", "<NULL>", rs.getString("quant"), date});
					}
				}
			}
			
			//print the arraylist to output the max for NY and NJ (between years 2000 and 2005) and min for CT for each cust and prod
			System.out.println("CUSTOMER" + "  " + "PRODUCT" + "   " + "NY_MAX" + "  " + "DATE" + "        " + "NJ_MAX" + "  " + "DATE" + "        " + "CT_MIN" + "  " + "DATE");
			System.out.println("========" + "  " + "========" + "  " + "======" + "  " + "==========" + "  " + "======" + "  " + "==========" + "  " + "======" + "  " + "==========");
			
			for(j=0; j < display.size(); j++)
			{
				System.out.printf("%-9s %-9s %6s %11s %7s %11s %7s %12s", display.get(j)[0], display.get(j)[1], display.get(j)[2], display.get(j)[3], display.get(j)[4], display.get(j)[5], display.get(j)[6], display.get(j)[7] + "\n");
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
