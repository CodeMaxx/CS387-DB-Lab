

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * Servlet implementation class GetComments
 */
@WebServlet("/GetComments")
public class GetComments extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetComments() {
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
		String postid = request.getParameter("postid");
		System.out.println("postid: " + postid);

		PrintWriter out = response.getWriter();

		if(postid != null) {
			try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
				){
				PreparedStatement stmt = null;

				stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, uid from comment where postid = ?");
				stmt.setString(1, postid);
				ResultSet rs = stmt.executeQuery();
				String s = GetPosts.ResultSetConverter(rs, "comment").toString();
				System.out.println(s);
				out.println(s);
			}
			catch(Exception e) {
				System.out.println(e);
			}
		}
		else
		{
			System.out.println("postid is null");
		}
	}
}
