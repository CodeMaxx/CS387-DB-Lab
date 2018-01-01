

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
 * Servlet implementation class WritePost
 */
@WebServlet("/WritePost")
public class WritePost extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WritePost() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Login.checkLogin(request, response);
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Login.checkLogin(request, response);
		
		HttpSession sess = request.getSession(false);
		String userid = (String) sess.getAttribute("id");
		String content = request.getParameter("content");
		PrintWriter out = response.getWriter();
		
		if(content != null)
		{
			try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
					PreparedStatement stmt = conn.prepareStatement("insert into post values(nextval(\'post_seq\'),?,now(), ?)");
				){
					stmt.setString(1, userid);
					stmt.setString(2, content);
					stmt.executeUpdate();
					out.println("Posted Successfully!");
			} catch (SQLException e) {
				out.println("error");
			}
		}
		else
			out.println("error");
	}

}
