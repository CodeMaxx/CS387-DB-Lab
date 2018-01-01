import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

// 150050031 - Akash Trehan

// This file also contains all the part 1 code also.

public class Main {
	public static void main(String[] args) {

		// In the JDBC API 4.0, the DriverManager.getConnection method loads
		// JDBC drivers automatically. As a result you do not need to call the
		// Class.forName method
		// try {
		// 	  Class.forName ("org.postgresql.Driver");
		//  }
		//  catch (Exception e) {
		// 	System.out.println("Could not load driver: " + e);
		// }


		// The following syntax is called try with resources which can be used with any resource
		// that supports the java.lang.AutoCloseable interface.
		// It ensures that the resources get closed at the end of the try block.
		// It is **MUCH** preferred to the old style to avoid connection leakage.
		// Note the URL syntax below:  jdbc:postgresql tells the DriverManager to use the
		// postgresql JDBC driver.
		// localhost can be replaced with a host name if the postgresql is running on a remote machine.
		// Replace 6432 with the port number you are using, and dbis with your database name
		// Similarly, replace sudarsha with the user name you are using for your database.
		//Class.forName("org.postgresql.Driver");
		try (
		    Connection conn = DriverManager.getConnection(
		    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
		    Statement stmt = conn.createStatement();
		)
		{
			try {
				if (stmt.executeUpdate(
						"drop table bigstudent;") > 0) {
					System.out.println("Successfully dropped bigstudent");
				} ;
			} catch ( SQLException sqle) {
				System.out.println("Could not drop bigstudent. " + sqle);
			}

			try {
				if (stmt.executeUpdate(
						"create table bigstudent" +
								"	(ID			varchar(5), " +
								"	 name			varchar(20) not null, " +
								"	 dept_name		varchar(20), " +
								"	 tot_cred		numeric(3,0) check (tot_cred >= 0)," +
								"	 primary key (ID)" +
								"	);") > 0) {
					System.out.println("Successfully created bigstudent");
				} ;
			} catch ( SQLException sqle) {
				System.out.println("Could not create bigstudent. " + sqle);
			}


		    long starttime = System.currentTimeMillis();
//			ConnectionLeakage();
//			ConnectionPerRow();
//			SingleConnection();
		    GroupCommit();
//		    BatchedInsert();
		    long endtime = System.currentTimeMillis();
		    System.out.println("Time taken for 1000000 loops = " + (endtime - starttime) + " milliseconds");

            printSchema(conn);

		    // The following are not required anymore since the connections were opened
		    // with the try with resources feature:
		    // stmt.close();
		    // conn.close();
		}
		catch (Exception sqle)
		{
		System.out.println("Exception : " + sqle);
		}

//		preparedInput();
		injection();
	}




	public static void printSchema(Connection conn) {

		try {
			DatabaseMetaData dmd = conn.getMetaData();
		    ResultSet result = dmd.getTables("", "", "%",new String[] {"TABLE"});
	                      /* The first three attributes specify which catalog, schema and table name to get information about.
	                         The empty string specifies current catalog, current schema.  The "%" for tablename specifies
	                         all tables.  The last attribute specifies what types of tables to include;
	                         using just TABLE here restricts to user tables, otherwise you
	                         could get information about catalog tables
	                       */
		    while(result.next()) {
		        String tableName = result.getString(3);
		        ResultSet columns = dmd.getColumns("", "", tableName, "%");
		        System.out.println("Table: " + tableName);
		        String query = "create table " + tableName + "\n(";
		        while(columns.next()) {
		        	String name = columns.getString(4);
		        	String type = columns.getString(6);
		        	int size = columns.getInt(7);
		        	int decimal = columns.getInt(9);

		        	query += name + " " + type;

		        	if(type.equals("char") || type.equals("varchar") || type.equals("binary") || type.equals("varbinary") || type.equals("integer") || type.equals("numeric") || type.equals("decimal") || type.equals("float")) {
		        		query += "(" + size;
		        		if(type.equals("decimal") || type.equals("numeric")) {
		        			query += "," + decimal;
		        		}
		        		query += ")";
		        		}

		        	query += ",\n";
		        }
		        query += ");";
		        System.out.println(query);

		        System.out.println("\n");
		    }
		}
		catch(SQLException sqle) {
			System.out.println("Exception : " + sqle);
		}
	}



	// Reference for reading file taken from https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
	 public static void readfile(ArrayList a) {

	        String csvFile = "/users/ug15/atrehan/Downloads/student.txt";
	        BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ", ";

	        try {

	            br = new BufferedReader(new FileReader(csvFile));
	            while ((line = br.readLine()) != null) {
	                String[] student = line.split(cvsSplitBy);
	                String s = student[0];
	                student[0] = s.substring(1,s.length() - 1);
	                s = student[1];
	                student[1] = s.substring(1,s.length() - 1);
	                s = student[2];
	                student[2] = s.substring(1,s.length() - 1);
	                a.add(student);
	            }

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	    }

// ConnectionLeakage: SEVERE: Connection error:
//	 org.postgresql.util.PSQLException: FATAL: sorry, too many clients already
//		at org.postgresql.core.v3.ConnectionFactoryImpl.doAuthentication(ConnectionFactoryImpl.java:438)
//		at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl(ConnectionFactoryImpl.java:222)
//		at org.postgresql.core.ConnectionFactory.openConnection(ConnectionFactory.java:49)
//		at org.postgresql.jdbc.PgConnection.<init>(PgConnection.java:194)
//		at org.postgresql.Driver.makeConnection(Driver.java:450)
//		at org.postgresql.Driver.connect(Driver.java:252)
//		at java.sql.DriverManager.getConnection(DriverManager.java:664)
//		at java.sql.DriverManager.getConnection(DriverManager.java:247)
//		at Main.ConnectionLeakage(Main.java:170)
//		at Main.main(Main.java:109)
//
//	Could not update bigstudent. org.postgresql.util.PSQLException: FATAL: sorry, too many clients already
//	Time taken for 1000000 loops = 929 milliseconds

	public static void ConnectionLeakage() {
		ArrayList a = new ArrayList();
		readfile(a);

		for(int i = 0; i < a.size(); i++) {

			try {
				Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
			    PreparedStatement stmt = conn.prepareStatement("insert into bigstudent values (?, ?, ?, ?)");

				String[] student = (String[]) a.get(i);
				try {
					stmt.setString(1, student[0]);
					stmt.setString(2, student[1]);
					stmt.setString(3, student[2]);
					stmt.setInt(4, Integer.parseInt(student[3]));
					if (stmt.executeUpdate() > 0) {
//						System.out.println("Successfully inserted tuple into bugstudent");
					} ;
				} catch ( SQLException sqle) {
					System.out.println("Could not insert tuple. " + sqle);
				}
			}
			catch(SQLException e) {
				System.out.println("Could not update bigstudent. " + e);
				break;
			}
		}
	}

	public static void ConnectionPerRow() {
		ArrayList a = new ArrayList();
		readfile(a);

		for(int i = 0; i < a.size(); i++) {
			try(Connection conn = DriverManager.getConnection(
			    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
			    PreparedStatement stmt = conn.prepareStatement("insert into bigstudent values (?, ?, ?, ?)");
				){
				String[] student = (String[]) a.get(i);
				try {
					stmt.setString(1, student[0]);
					stmt.setString(2, student[1]);
					stmt.setString(3, student[2]);
					stmt.setInt(4, Integer.parseInt(student[3]));

					if (stmt.executeUpdate() > 0) {
//						System.out.println("Successfully inserted tuple into bugstudent");
					} ;
				} catch ( SQLException sqle) {
					System.out.println("Could not insert tuple. " + sqle);
				}
			}
			catch(SQLException e) {
				System.out.println("Could not update bigstudent. " + e);
				break;
			}
		}
	}

// ConnectionPerRow: Time taken for 1000000 loops = 85676 milliseconds

	public static void SingleConnection() {
		ArrayList a = new ArrayList();
		readfile(a);

		try(Connection conn = DriverManager.getConnection(
	    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
			    PreparedStatement stmt = conn.prepareStatement("insert into bigstudent values (?, ?, ?, ?)");
		){
			for(int i = 0; i < a.size(); i++) {
					String[] student = (String[]) a.get(i);
					try {
						stmt.setString(1, student[0]);
						stmt.setString(2, student[1]);
						stmt.setString(3, student[2]);
						stmt.setInt(4, Integer.parseInt(student[3]));
						if (stmt.executeUpdate() > 0) {
//							System.out.println("Successfully inserted tuple into bugstudent");
						} ;
					} catch ( SQLException sqle) {
						System.out.println("Could not insert tuple. " + sqle);
					}
				}
		}
		catch(SQLException e) {
			System.out.println("Could not update bigstudent. " + e);
		}
	}

	// SimgleConnection: Time taken for 1000000 loops = 11189 milliseconds

	public static void GroupCommit() {
		ArrayList a = new ArrayList();
		readfile(a);

		try(Connection conn = DriverManager.getConnection(
	    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
			    PreparedStatement stmt = conn.prepareStatement("insert into bigstudent values (?, ?, ?, ?)");

		){
			conn.setAutoCommit(false);
			for(int i = 0; i < a.size(); i++) {
					String[] student = (String[]) a.get(i);
					try {
						stmt.setString(1, student[0]);
						stmt.setString(2, student[1]);
						stmt.setString(3, student[2]);
						stmt.setInt(4, Integer.parseInt(student[3]));
						if (stmt.executeUpdate() > 0) {
//							System.out.println("Successfully inserted tuple into bugstudent");
						} ;
					} catch ( SQLException sqle) {
						System.out.println("Could not insert tuple. " + sqle);
					}
					if((i+1) %1000 == 0)
						conn.commit();
				}
			conn.commit();
		}
		catch(SQLException e) {
			System.out.println("Could not update bigstudent. " + e);
		}
	}

// GroupCommit: Time taken for 1000000 loops = 1484 milliseconds

	public static void BatchedInsert() {
		ArrayList a = new ArrayList();
		readfile(a);

		try(Connection conn = DriverManager.getConnection(
	    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
			    PreparedStatement stmt = conn.prepareStatement("insert into bigstudent values (?, ?, ?, ?)");

		){
			for(int i = 0; i < a.size(); i++) {
					String[] student = (String[]) a.get(i);

					try {
						stmt.setString(1, student[0]);
						stmt.setString(2, student[1]);
						stmt.setString(3, student[2]);
						stmt.setInt(4, Integer.parseInt(student[3]));
							if(i % 1000 == 999) {
								stmt.executeBatch();
//								System.out.println("Successfully inserted 1000 tuples into bugstudent");
							}
							else
								stmt.addBatch();

					} catch ( SQLException sqle) {
						System.out.println("Could not insert tuple. " + sqle);
					}

				}
			stmt.executeBatch();
		}
		catch(SQLException e) {
			System.out.println("Could not update bigstudent. " + e);
		}
	}

	// BatchedInsert: Time taken for 1000000 loops = 927 milliseconds

	public static void preparedInput() {
		   Scanner sc = new Scanner(System.in);
		   System.out.println("Enter string: ");
		   String name = sc.next();
		   name = "%" + name + "%";

		   try(Connection conn = DriverManager.getConnection(
		    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
				    PreparedStatement stmt = conn.prepareStatement("select * from bigstudent where name like ?");
			){

					try {
						stmt.setString(1, name);
						ResultSet rs = stmt.executeQuery();
						while(rs.next()){
					         //Retrieve by column name
					         String id  = rs.getString("id");
					         int tot_cred = rs.getInt("tot_cred");
					         String stud_name = rs.getString("name");
					         String dept_name = rs.getString("dept_name");

					         //Display values
					         System.out.print("ID: " + id);
					         System.out.print(", Name: " + stud_name);
					         System.out.print(", Department Name: " + dept_name);
					         System.out.println(", Total Credits: " + tot_cred);
					      };
					} catch ( SQLException sqle) {
						System.out.println("Could not insert tuple. " + sqle);
					}
			}
			catch(SQLException e) {
				System.out.println("Could not update bigstudent. " + e);
			}
	}

	public static void injection() {
		Scanner sc = new Scanner(System.in);
		   System.out.println("Enter string: ");
		   String name = sc.nextLine();

		   try(Connection conn = DriverManager.getConnection(
		    		"jdbc:postgresql://localhost:5310/postgres", "atrehan", "");
				   Statement stmt = conn.createStatement();
			){

					try {
						System.out.println("select * from bigstudent where name like %" + name + "%");
						ResultSet rs = stmt.executeQuery("select * from bigstudent where name like '%" + name + "%'");
						while(rs.next()){
					         //Retrieve by column name
					         String id  = rs.getString("id");
					         int tot_cred = rs.getInt("tot_cred");
					         String stud_name = rs.getString("name");
					         String dept_name = rs.getString("dept_name");

					         //Display values
					         System.out.print("ID: " + id);
					         System.out.print(", Name: " + stud_name);
					         System.out.print(", Department Name: " + dept_name);
					         System.out.println(", Total Credits: " + tot_cred);
					      };
					} catch ( SQLException sqle) {
						System.out.println("Could not get tuples. " + sqle);
					}
			}
			catch(SQLException e) {
				System.out.println("Could not update bigstudent. " + e);
			}
	}

	// Injection Input used:  `ag%';drop table bigstudent; --` (remove backticks)
}