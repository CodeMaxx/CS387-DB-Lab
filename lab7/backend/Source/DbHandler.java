

import java.sql.*;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DbHandler {
	// connection strings
	private static String connString = "jdbc:postgresql://localhost:5432/postgres";
	private static String userName = "upen";
	private static String passWord = "";
	
	
	public static JSONObject authenticate(String id, String password,HttpServletRequest request){		
		JSONObject obj = new JSONObject();
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select count(*) from password where id=? and password=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			preparedStmt.setString(2, password);
			ResultSet result =  preparedStmt.executeQuery();
			result.next();
			boolean ans = (result.getInt(1) > 0); 
			preparedStmt.close();
			conn.close();
			if(ans==true){
				request.getSession(true).setAttribute("id", id);
				obj.put("status",true);				
				obj.put("data", id);			
			}
			else{						
					obj.put("status",false);
					obj.put("message", "Authentication Failed");					
			}			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONObject createpost(String id, String postText)
	{
		JSONObject obj = new JSONObject();
		try{   
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pStmt = conn.prepareStatement("insert into post(uid,text,timestamp) values(?,?,CURRENT_TIMESTAMP);");
			pStmt.setString(1, id);
			pStmt.setString(2, postText);
			if(pStmt.executeUpdate()>0)
			{
				obj.put("status", true);
				obj.put("data","Created Post");				
			}
			else
			{
				obj.put("status",false);
				obj.put("message", "Unable to create");
			}	
			}catch (Exception sqle)
			{
				sqle.printStackTrace();
			}
		return obj;
	}
	
	
	public static JSONObject writecomment(String id, String PostId, String comment)
	{
		JSONObject obj = new JSONObject();
		try{   
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pStmt = conn.prepareStatement("insert into comment(postid,uid,timestamp,text) values(?,?,CURRENT_TIMESTAMP,?);");
			pStmt.setInt(1, Integer.parseInt(PostId));
			pStmt.setString(2, id);
			pStmt.setString(3,comment);
			if(pStmt.executeUpdate()>0)
			{
				obj.put("status", true);
				obj.put("data","Created Post Successfully");				
			}
			else
			{
				obj.put("status",false);
				obj.put("message", "Could not Post");
			}	
			}catch (Exception sqle)
			{
				sqle.printStackTrace();
			}
		return obj;
	}
	
public static JSONArray userFollow(String id){
		
		JSONArray jsonObj = new JSONArray();
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select uid2 as uid, name from follows, \"user\" where \"user\".uid "
					+ "= uid2 and uid1 = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			ResultSet result =  preparedStmt.executeQuery();
			
			jsonObj = ResultSetConverter(result);	
			preparedStmt.close();
			conn.close();
			 
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return jsonObj;
	}
	
	
	
	
	public static JSONObject deauth(HttpServletRequest request) throws JSONException
	{
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) {
			obj.put("status", false);
			obj.put("message", "Invalid Session");
			return obj;
		}else 
		{
			request.getSession(false).invalidate();
			obj.put("status", true);
			obj.put("data", "sucessfully logged out");
			return obj;
		}
	}
	
	public static JSONArray seeMyPosts(String id, int offset, int limit){
		JSONArray json = new JSONArray();
		try (
		    Connection conn = DriverManager.getConnection(
		    		connString, userName, "");
		    PreparedStatement postSt = conn.prepareStatement("select postid,timestamp,uid,text from post where post.uid = ? order by timestamp desc offset ? limit ?");
		)
		{
			postSt.setString(1, id);
			postSt.setInt(2, offset);
			postSt.setInt(3, limit);
			ResultSet rs = postSt.executeQuery();
			conn.close();
			json = ResultSetConverter(rs);			
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	public static JSONArray seeUserPosts(String id, int offset, int limit){
		JSONArray json = new JSONArray();
		try (
		    Connection conn = DriverManager.getConnection(
		    		connString, userName, "");
		    PreparedStatement postSt = conn.prepareStatement("select postid,timestamp,uid,text from post where post.uid = ? order by timestamp desc offset ? limit ?");
		)
		{
			postSt.setString(1, id);
			postSt.setInt(2, offset);
			postSt.setInt(3, limit);
			ResultSet rs = postSt.executeQuery();
			conn.close();
			json = ResultSetConverter(rs);			
			return json;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	public static JSONArray seePosts(String id, int offset, int limit){
		JSONArray json = new JSONArray();
		try (
		    Connection conn = DriverManager.getConnection(
		    		connString, userName, "");
			PreparedStatement postSt = conn.prepareStatement("select postid,timestamp,uid,text from post where post.uid in (select uid2 from follows where uid1 = ? UNION select uid from \"user\" where uid=? ) order by timestamp asc offset ? limit ?");
		)
		{	
			postSt.setString(1, id);
			postSt.setString(2,id);
			postSt.setInt(3, offset);
			postSt.setInt(4, limit);
			ResultSet rs = postSt.executeQuery();
			json = ResultSetConverter(rs);
			return json;
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	private static JSONArray ResultSetConverter(ResultSet rs) throws SQLException, JSONException {
		
		// TODO Auto-generated method stub
		JSONArray json = new JSONArray();
	    ResultSetMetaData rsmd = rs.getMetaData();
	    while(rs.next()) {
	        int numColumns = rsmd.getColumnCount();
	        JSONObject obj = new JSONObject();
	        int postid=-1;
	        for (int i=1; i<numColumns+1; i++) {
	          String column_name = rsmd.getColumnName(i);

	          if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
	           obj.put(column_name, rs.getArray(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
	           obj.put(column_name, rs.getBoolean(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
	           obj.put(column_name, rs.getBlob(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
	           obj.put(column_name, rs.getDouble(column_name)); 
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
	           obj.put(column_name, rs.getFloat(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
	           obj.put(column_name, rs.getNString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
	           obj.put(column_name, rs.getString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
	           obj.put(column_name, rs.getDate(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
	          obj.put(column_name, rs.getTimestamp(column_name));   
	          }
	          else{
	           obj.put(column_name, rs.getObject(column_name));
	          }
	          
	          if(column_name.equals((String)"postid"))
	          {
	        	  postid = rs.getInt(column_name);
	        	  
	          }
	          
	        }
	        json.put(obj);
	        if(postid!=-1)
	        {
	       	     JSONArray comObj = getComments(postid);
	       	     obj.put("Comment", comObj);
	        }
	       
	      }
	    return json;
	}
	
	public static JSONArray getComments(int postid){
		JSONArray json = new JSONArray();
		try (
			    Connection conn = DriverManager.getConnection(
			    		connString, userName, "");
			    PreparedStatement commSt = conn.prepareStatement("select timestamp,comment.uid, name, text from comment,\"user\" as us where postid = ? and us.uid=comment.uid order by timestamp asc")
			    		
			)
		{
				commSt.setInt(1, postid);
				ResultSet rs = commSt.executeQuery();
				json = ResultSetConverter(rs);
				return json;
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
		
	}
	
	public static JSONObject follow(String uid1,String uid2) throws JSONException
	{
		JSONObject obj = new JSONObject();
		try (
			    Connection conn = DriverManager.getConnection(
			    		connString, userName, "");
			    PreparedStatement commSt = conn.prepareStatement("insert into follows values(?,?)");
			    		
			)
		{
			commSt.setString(1, uid1);
			commSt.setString(2, uid2);
			if(commSt.executeUpdate()>0)
			{
				obj.put("status", true);
				obj.put("data", "user followed " + uid2);
			}
			else
			{
				obj.put("status", false);
				obj.put("message", "could not follow");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			obj.put("status", false);
			obj.put("message", "Already followed");
		}
		return obj;
	}
	
	public static JSONObject unfollow(String uid1,String uid2) throws JSONException
	{
		JSONObject obj = new JSONObject();
		try (
			    Connection conn = DriverManager.getConnection(
			    		connString, userName, "");
				PreparedStatement check = conn.prepareStatement("select * from follows where uid1=? and uid2=?"); 
			    		
			)
		{
			check.setString(1, uid1);
			check.setString(2, uid2);
			ResultSet result =  check.executeQuery();
			if(result.next())
			{
				PreparedStatement commSt = conn.prepareStatement("delete from follows where uid1=? and uid2=?");
				commSt.setString(1, uid1);
				commSt.setString(2, uid2);
				if(commSt.executeUpdate()>0)
				{
					obj.put("status", true);
					obj.put("data", "unfollowed "+uid2);
				}
				else
				{
					obj.put("status", false);
					obj.put("message", "could not unfollow");
					
				}
			}
			else
			{
				obj.put("status", false);
				obj.put("message", "user not followed");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONArray getSuggestion(String search)
	{
		JSONArray jsonToSend = new JSONArray();
		if(search.length()<3)
			return jsonToSend;
		try (
			    Connection conn = DriverManager.getConnection(
			    		connString, userName, "");
				PreparedStatement commSt = conn.prepareStatement("select name,uid,email from \"user\" where name like ? or uid like ? or email like ? limit 10");
			)
		{
			
			
			search = "%" + search + "%";
			commSt.setString(1, search);
			commSt.setString(2, search);
			commSt.setString(3, search);
			ResultSet rset = commSt.executeQuery();
			jsonToSend.put(ResultSetConverter(rset));			
			return jsonToSend;
		} 
				
		catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonToSend;
	}
}
