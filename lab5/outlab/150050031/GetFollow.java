

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetFollow
 */
@WebServlet("/GetFollow")
public class GetFollow extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFollow() {
        super();
        // TODO Auto-generated constructor stub
    }


    private static JSONArray ResultSetConverter(ResultSet rs) throws SQLException, JSONException {

		JSONArray json = new JSONArray();
	    	ResultSetMetaData rsmd = rs.getMetaData();

	    	while(rs.next()) {
	    		int numColumns = rsmd.getColumnCount();
	    		JSONObject obj = new JSONObject();

	    		for (int i=1; i<numColumns+1; i++) {
	    			String column_name = rsmd.getColumnName(i);

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
		// TODO Auto-generated method stub
		response.sendRedirect("follows.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Login.checkLogin(request, response);

		String loginID = (String)request.getSession(false).getAttribute("id");
		String userID = request.getParameter("id"); // Whom to unfollow
		int unfollow = Integer.parseInt(request.getParameter("unfollow"));
		System.out.println("userid:" + userID);
		System.out.println("unfollow" + unfollow);
		PrintWriter out = response.getWriter();
		if(userID != null) {
			try (
				    Connection conn = DriverManager.getConnection(
				    		Login.host, Login.username, Login.password);
				){
				PreparedStatement stmt = null;

					if(unfollow == 1) {
						stmt = conn.prepareStatement("delete from follows where uid1 = ? and uid2 = ?");
						stmt.setString(1, loginID);
						stmt.setString(2, userID);
						stmt.executeUpdate();
					}
					else
					{
						stmt = conn.prepareStatement("select uid2 as uid, name from follows, users where follows.uid1 = ? and users.uid = follows.uid2");
						stmt.setString(1, loginID);
						ResultSet rs = stmt.executeQuery();
						String s = ResultSetConverter(rs).toString();
						System.out.println(s);
						out.println(s);
					}
			}
			catch(Exception e) {
				System.out.println(e);
			}

		}

	}

}