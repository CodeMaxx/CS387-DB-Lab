

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		HttpSession sess = request.getSession(false);

		if(sess.getAttribute("userid") == null)
			response.sendRedirect("Login");

//        out.println("<html><body>Hi " + sess.getAttribute("userid") + "\n</body></html>");
		out.println("<html><body>");
        out.println("<form action=\"./Home\" method=\"post\"><input type=\"hidden\" name=\"screen\" value=\"create\"><input type=\"submit\" value=\"Create Post\"></form>");
        out.println("<form action=\"./Home\" method=\"post\"><input type=\"hidden\" name=\"screen\" value=\"mine\"><input type=\"submit\" value=\"My Posts\"></form>");
        out.println("<form action=\"./Home\" method=\"post\"><input type=\"hidden\" name=\"screen\" value=\"followers\"><input type=\"submit\" value=\"View Feed\"></form>");
        out.println("<a href=\"./Lout\">Logout</a>");
        out.print("</body></html>");
	}

	protected void showPosts(Connection conn, PreparedStatement stmt, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		String screen = (String) request.getAttribute("screen");
		if(screen.equals("followers")) {
			stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, postid, uid from post where uid in (select uid2 from follows where uid1 = ?) order by post.time_stamp desc");
		}
		else if(screen.equals("mine")){
			stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, postid, uid from post where uid = ? order by post.time_stamp desc");
		}

		stmt.setString(1, (String) request.getSession(false).getAttribute("userid"));
		ResultSet rs = stmt.executeQuery();
		PrintWriter out = response.getWriter();
		out.println("<html><body>");

		while(rs.next()) {
			PreparedStatement username = conn.prepareStatement("select name from users where uid = ?");
			username.setString(1, rs.getString(4));
			ResultSet unamers = username.executeQuery();
			unamers.next();
			String name = unamers.getString(1);
			out.print("<b>" + name + "</b>");
			out.println("<br>");
			out.println(rs.getString(2));
			out.println("<br>");
			out.println(rs.getString(1));
			out.println("<br>");
			String postid = rs.getString(3);
			PreparedStatement com = conn.prepareStatement("select date_trunc('second', time_stamp), text, uid from comment where postid = ?");
			com.setString(1, postid);
			ResultSet comrs = com.executeQuery();
			while(comrs.next()) {
				username = conn.prepareStatement("select name from users where uid = ?");
				username.setString(1, comrs.getString(3));
				unamers = username.executeQuery();
				unamers.next();
				name = unamers.getString(1);
				out.print("<i>" + name + ":</i>");
				out.println(comrs.getString(2));
				out.println("<br>");
				out.println(comrs.getString(1));
				out.println("<br>");
			}
			if(screen.equals("followers")) {
				out.println("<form action=\"./Home\" method=\"post\"><input type=\"text\" name=\"content\"><input type=\"hidden\" name=\"where\" value=\"follow\"><input type=\"hidden\" name=\"postid\" value=\"" + postid +"\"><input type=\"hidden\" name=\"screen\" value=\"comment_submit\"><input type=\"submit\" value=\"Comment\"></form>");
			}
			else if(screen.equals("mine")) {
				out.println("<form action=\"./Home\" method=\"post\"><input type=\"text\" name=\"content\"><input type=\"hidden\" name=\"where\" value=\"mine\"><input type=\"hidden\" name=\"postid\" value=\"" + postid +"\"><input type=\"hidden\" name=\"screen\" value=\"comment_submit\"><input type=\"submit\" value=\"Comment\"></form>");
			}
			out.println();
			out.println("<hr>");
		}
		out.println("</body></html>");
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession sess = request.getSession(false);
		if(sess.getAttribute("userid") == null)
			response.sendRedirect("Login");

		String userid = (String) sess.getAttribute("userid");

		PrintWriter out = response.getWriter();

		try (
			    Connection conn = DriverManager.getConnection(
			    		Login.host, Login.username, Login.password);
			){
			PreparedStatement stmt = null;
			String screen = request.getParameter("screen");
			out.println("<html><body>");
//			out.println("")
			if(screen.equals("create")) {
				out.println("<form action=\"./Home\" method=\"post\"><input type=\"text\" name=\"content\"><input type=\"hidden\" name=\"screen\" value=\"create_submit\"><input type=\"submit\" value=\"Publish\"></form>");;
			}
			else if(screen.equals("create_submit")) {
				String content = request.getParameter("content");
				stmt = conn.prepareStatement("insert into post values(nextval(\'post_seq\'),?,now(), ?)");
				stmt.setString(1, userid);
				stmt.setString(2, content);
				stmt.executeUpdate();
				doGet(request, response);
			}
			else if(screen.equals("mine") || screen.equals("followers")) {
				request.setAttribute("screen", screen);
				showPosts(conn, stmt, request, response);
			}
			else if(screen.equals("comment_submit")) {
				String content = request.getParameter("content");
				String postid = request.getParameter("postid");
				stmt = conn.prepareStatement("insert into comment values(nextval(\'comment_seq\'),?,?,now(), ?)");
				stmt.setString(1, postid);
				stmt.setString(2, userid);
				stmt.setString(3, content);
				stmt.executeUpdate();
//				String where = request.getParameter("where");
//				if(where.equals("mine")) {
//					request.setAttribute("screen", "mine");
//				}
//				else {
//					request.setAttribute("screen", "followers");
//				}
				response.sendRedirect("Home");
//				showPosts(conn, stmt, request, response);
			}
			else {
//				out.println(screen);
				response.sendRedirect("Login");
			}
			out.println("</body></html>");
		}
		catch(SQLException e)
		{
			out.println("<html><body>"+ e + "</body></html>");
		}
	}

}
