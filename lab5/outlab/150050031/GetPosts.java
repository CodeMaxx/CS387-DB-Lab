

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static String get_name_from_uid(String uid) {
    		String name = "";
	    	try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
			) {
	    		PreparedStatement username = conn.prepareStatement("select name from users where uid = ?");
			username.setString(1, uid);
			ResultSet unamers = username.executeQuery();
			unamers.next();
			name = unamers.getString(1);
	    	}
	    	catch(Exception e) {
	    		System.out.println(e);;
	    	}
	    	return name;
    }

    public static JSONArray ResultSetConverter(ResultSet rs, String type) throws SQLException, JSONException {

		JSONArray json = new JSONArray();
	    	ResultSetMetaData rsmd = rs.getMetaData();

	    	while(rs.next()) {
	    		int numColumns = rsmd.getColumnCount();
	    		JSONObject obj = new JSONObject();
	    		JSONArray com_array = new JSONArray();

	    		for (int i=1; i<numColumns+1; i++) {
	    			String column_name = rsmd.getColumnName(i);
	    			try (
    					    Connection conn = DriverManager.getConnection(
    					    		Login.host, Login.username, Login.password);
    				) {
	    				if(type.equals("post") && column_name.equals("uid")) {

						String name = get_name_from_uid(rs.getString(4));
						obj.put("name", name);

						String postid = rs.getString(3);
						PreparedStatement com = conn.prepareStatement("select date_trunc('second', time_stamp), text, uid from comment where postid = ? limit 4");
						com.setString(1, postid);
						ResultSet comrs = com.executeQuery();
						com_array = ResultSetConverter(comrs, "comment");
						obj.put("comments", com_array);
		    			}
		    			else if(type.equals("comment") && column_name.equals("uid")) {
		    				// TODO: Repeated code
		    				String name = get_name_from_uid(rs.getString(3));
						obj.put("name", name);
		    			}
	    			}
    				catch(Exception e) {
    					System.out.println(e);
    				}

	    			if(rsmd.getColumnType(i) == java.sql.Types.ARRAY){
	    				obj.put(column_name, rs.getArray(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.BIGINT){
	    				obj.put(column_name, rs.getInt(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.BOOLEAN){
	    				obj.put(column_name, rs.getBoolean(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.BLOB){
	    				obj.put(column_name, rs.getBlob(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.DOUBLE){
	    				obj.put(column_name, rs.getDouble(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.FLOAT){
	    				obj.put(column_name, rs.getFloat(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.INTEGER){
	    				obj.put(column_name, rs.getInt(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.NVARCHAR){
	    				obj.put(column_name, rs.getNString(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.VARCHAR){
	    				obj.put(column_name, rs.getString(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.TINYINT){
	    				obj.put(column_name, rs.getInt(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.SMALLINT){
	    				obj.put(column_name, rs.getInt(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.DATE){
	    				obj.put(column_name, rs.getDate(column_name));
	    			}
	    			else if(rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP){
	    				obj.put(column_name, rs.getString(column_name));
	    			}
	    			else{
	    				obj.put(column_name, rs.getObject(column_name));
	    			}
	    		}
	    		json.put(obj);
	    	}
	    	return json;
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
		String offset = request.getParameter("offset");
		String limit = request.getParameter("limit");
		System.out.println("offset" + offset);
		System.out.println("limit" + limit);
		PrintWriter out = response.getWriter();
		if(userID != null && type != null && offset != null && limit != null) {
			try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
				){
				PreparedStatement stmt = null;
				if(type.equals("follow")) {
					stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, postid, uid from post where uid in (select uid2 from follows where uid1 = ?) order by post.time_stamp desc limit ? offset ?");
					stmt.setString(1, loginID);
				}
				else if(type.equals("all")) {
					stmt = conn.prepareStatement("select date_trunc('second', time_stamp), text, postid, uid from post where uid = ? order by post.time_stamp desc limit ? offset ?");
					stmt.setString(1, userID);
				}

				stmt.setInt(2, Integer.parseInt(limit));
				stmt.setInt(3, Integer.parseInt(offset));

				ResultSet rs = stmt.executeQuery();

				String s = "";
				response.setContentType("text/html");
				JSONArray posts = ResultSetConverter(rs, "post");
				JSONObject obj = new JSONObject();
				if(type.equals("follow")) {
					stmt = conn.prepareStatement("select count(*) from post where uid in (select uid2 from follows where uid1 = ?)");
					stmt.setString(1, loginID);
				}
				else if(type.equals("all")) {
					stmt = conn.prepareStatement("select count(*) from post where uid = ?");
					stmt.setString(1, userID);
				}
				rs = stmt.executeQuery();
				rs.next();
				int all = rs.getInt(1);

				if(all > Integer.parseInt(limit) + Integer.parseInt(offset))
					obj.put("more", 1);
				else
					obj.put("more", 0);

				obj.put("posts", posts);

				JSONArray finalarray = new JSONArray();
				finalarray.put(obj);
				s = finalarray.toString();
				System.out.println(s);
				rs.close();
				stmt.close();
				out.println(s);
			}
			catch(Exception e)
			{
				System.out.println(e);
		        out.println(Login.starthtml + e + Login.endhtml);
			}
		}
		else {
			System.out.println("Got out");
			System.out.println(offset);
			System.out.println(limit);
			out.println(Login.starthtml + "Improper Parameters" + Login.endhtml);
		}


	}

}
