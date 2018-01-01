

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

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auly once in static vato-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
        out.println("<html><body><form action=\"./Login\" method=\"post\">\n" +
        		"                  ID:  <input type=\"text\" name = \"ID\">\n\n" +
        		"Password: <input type=\"password\" name=\"password\">\n" +
        		"                  <input type=\"submit\" value = \"Submit\">\n" +
        		"              </form></body></html>");

        if(request.getSession().getAttribute("invalid").equals("1")) {
//        	request.getSession().setAttribute("invalid", "0");
        	out.println("<html><body>Invalid Credentials</body></html>");
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String ID = request.getParameter("ID");
		String pass = request.getParameter("password");
		PrintWriter out1 = response.getWriter();
        out1.println("<html><body>In login post" + ID + " " +pass + "</body></html>");
		if(ID !=null && pass != null) {
			try (
				    Connection conn = DriverManager.getConnection(
				    		host, username, password);
				    PreparedStatement stmt = conn.prepareStatement("select * from password where uid=? and pass=?");
				){
				stmt.setString(1, ID);
				stmt.setString(2, pass);
				ResultSet rs = stmt.executeQuery();
//		        out.println("<html><body>Hi " + name + "</body></html>");

				if(rs.next()) {
					HttpSession sess = request.getSession(true);
					sess.setAttribute("userid", ID);
					response.sendRedirect("Home");
				}
				else {
					request.getSession().setAttribute("invalid", "1");
					response.sendRedirect("Login");
				}
			}
			catch(SQLException e)
			{
				out1 = response.getWriter();
		        out1.println("<html><body>Error " + ID + " " +pass + " " + e + "</body></html>");
			} // check ID pass; create session; store userid
		}
		else
			doGet(request, response);
	}

}
