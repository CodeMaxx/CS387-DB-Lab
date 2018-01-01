

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import slytherin.PrintTable;

/**
 * Servlet implementation class Mycourses
 */
@WebServlet("/Mycourses")
public class Mycourses extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mycourses() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession sess = request.getSession(false);
		if(sess.getAttribute("userid") == null)
			response.sendRedirect("Login");

		PrintWriter out = response.getWriter();
		try (
			    Connection conn = DriverManager.getConnection(
			    		Login.host, Login.username, Login.password);
			    PreparedStatement stmt = conn.prepareStatement("select course_id, title, year, semester, sec_id from (takes natural join course) as bla where id=?");
			){
			stmt.setString(1, (String) sess.getAttribute("userid"));
			ResultSet rs = stmt.executeQuery();

//	        out.println("<html><body>Hi " + na me + "</body></html>");

			PrintTable.print(rs, response);
		}
		catch(SQLException e)
		{
			out.println("<html><body>"+ e + "</body></html>");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
