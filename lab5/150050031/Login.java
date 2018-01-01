

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
      static String host = "jdbc:postgresql://localhost:5432/postgres";
      static String username = "akash";
      static String password = "";
      static String starthtml = "<html><body>";
      static String endhtml = "</body></html>";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

    public static void checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
    		HttpSession sess = request.getSession(false);
		if(sess.getAttribute("id") == null)
			response.sendRedirect("Login");
    }

    /**
	 * @throws IOException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		try {

	        String s = Login.starthtml;
	        s += "<form action=\"./Login\" method=\"post\">\n" +
	        		"                  ID:  <input type=\"text\" name = \"ID\">\n\n" +
	        		"Password: <input type=\"password\" name=\"password\">\n" +
	        		"                  <input type=\"submit\" value = \"Submit\">\n" +
	        		"              </form>";

	        String invalid = (String) request.getSession().getAttribute("invalid");
	        if(invalid != null && invalid.equals("1")) {
	        		s += "\nInvalid Credentials";
	        }
	        s += Login.endhtml;

	        out.println(s);
		}
		catch(Exception e) {
			out.println(Login.starthtml + e + Login.endhtml);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String ID = request.getParameter("ID");
		String pass = request.getParameter("password");
		PrintWriter  out = response.getWriter();

		if(ID !=null && pass != null) {
			try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
				    PreparedStatement stmt = conn.prepareStatement("select * from password where uid=? and pass=?");
				){
				stmt.setString(1, ID);
				stmt.setString(2, pass);
				ResultSet rs = stmt.executeQuery();

				if(rs.next()) {
					HttpSession sess = request.getSession(true);
					sess.setAttribute("id", ID);
					rs.close();
					response.sendRedirect("Home");
				}
				else {
					request.getSession().setAttribute("invalid", "1");
					response.sendRedirect("Login");
				}
			}
			catch(SQLException e)
			{
		        out.println(Login.starthtml + "Error " + ID + " " + pass + " " + e + Login.endhtml);
			}
		}
		else
			doGet(request, response);
	}

}
