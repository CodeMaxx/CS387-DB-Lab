

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetPosts
 */
@WebServlet("/GetPosts")
public class GetPosts extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPosts() {
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

		String loginID = (String)request.getSession(false).getAttribute("id");
		String userID = request.getParameter("id");
		String type = request.getParameter("type");
		PrintWriter out = response.getWriter();

		if(userID != null && type != null) {
			try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
				){
				PreparedStatement stmt = null;
				if(type.equals("follow")) {
					stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, postid, uid from post where uid in (select uid2 from follows where uid1 = ?) order by post.time_stamp desc");
					stmt.setString(1, loginID);
				}
				else if(type.equals("all")) {
					stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, postid, uid from post where uid = ? order by post.time_stamp desc");
					stmt.setString(1, userID);
				}

				ResultSet rs = stmt.executeQuery();

				String s = "";
				response.setContentType("text/html");

				while(rs.next()) {
					PreparedStatement username = conn.prepareStatement("select name from users where uid = ?");
					username.setString(1, rs.getString(4));
					ResultSet unamers = username.executeQuery();
					unamers.next();
					String name = unamers.getString(1);
					s += "<b>" + name + "</b><br>";
					s += rs.getString(2) + "<br>" + rs.getString(1) + "<br><br>";
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
						s += "<i>" + name + ":</i>";
						s += comrs.getString(2) + "<br>" + comrs.getString(1) + "<br>";
					}
					s += "<hr>";

				}
				rs.close();
				stmt.close();
				out.println(s);
			}
			catch(Exception e)
			{
		        out.println(Login.starthtml + e + Login.endhtml);
			}
		}
		else
			out.println(Login.starthtml + "Improper Parameters" + Login.endhtml);

	}

}
