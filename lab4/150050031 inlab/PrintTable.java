package slytherin;

import java.sql.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PrintTable {
	public static void print(ResultSet rs, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		try {

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			// Iterate through the data in the result set and display it.

	        out.println("<html><body>");
	        out.println("<table style=\"width:100%\">");
			while (rs.next()) {
			//Print one row
				out.println("<tr>");
			for(int i = 1 ; i <= columnsNumber; i++){

			      out.println("<th>" + rs.getString(i) + "</th>"); //Print one element of a row

			}
			out.println("</tr>");

//			 out.println("<br>");//Move to the next line to print the next row.

			    }
			out.println("</table>");
			out.println("</body></html>");

		}
		catch(Exception e) {
			out.println("<html><body>"+ e + "</body></html>");

		}

	}
}
