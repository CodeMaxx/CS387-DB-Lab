

import java.sql.*;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.apache.tomcat.jni.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.org.apache.xpath.internal.operations.And;

import javafx.scene.shape.Ellipse;
import java.io.*;
import java.rmi.server.UID;


public class DbHandler {
	// connection strings
	private static String connString = "jdbc:postgresql://localhost:5432/twitter_backend";
	private static String userName = "akash";
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
				obj.put("data", getname(id));			
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

	public static JSONObject register(String name, String uid, String email, String password) {
		JSONObject obj = new JSONObject();
			
		String sql = "insert into \"user\"(name, uid, email) values (?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement preparedStmt = conn.prepareStatement(sql);) {
			
			if (!(name != null && uid != null && email != null && password != null && !name.equals("") && !uid.equals("") && !email.equals("") && !password.equals(""))) {
				obj.put("status", false);
				obj.put("message", "empty field");
			}
			else {
			
				preparedStmt.setString(1, name);
				preparedStmt.setString(2, uid);
				preparedStmt.setString(3, email);
				
				if (preparedStmt.executeUpdate() > 0) {
					sql = "insert into password(id, password) values (?, ?);";
					try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
						pStatement.setString(1, uid);
						pStatement.setString(2, password);
					
						if (pStatement.executeUpdate() > 0) {
							obj.put("status", true);
							obj.put("message", "register successful");
						}
						else {
							obj.put("status", false);
							obj.put("message", "could not register");
							
							sql = "delete from \"user\" where uid = ?";
							try (PreparedStatement pStatement2 = conn.prepareStatement(sql)) {
								pStatement2.setString(1, uid);
								
								pStatement2.executeUpdate();
							}
							catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				else {
					obj.put("status", false);
					obj.put("message", "could not register");
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				obj.put("status", false);
				obj.put("message", "username already used");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			System.out.println(e.toString());
		} 
		
		
		return obj;
	}
	
	public static boolean getimage(String fileid, OutputStream out) {
		String sql = "select * from images where id = ?";
		try (Connection conn = DriverManager.getConnection(connString, userName, passWord);
				PreparedStatement pStatement = conn.prepareStatement(sql)) {
			
			pStatement.setLong(1, Long.parseLong(fileid));
			ResultSet resultSet = pStatement.executeQuery();
			if (resultSet.next()) {
				
				InputStream inputStream = resultSet.getBinaryStream("image");
				
				int nbytes;
				byte[] bytes = new byte[1024 * 256];
				
				while ( (nbytes = inputStream.read(bytes, 0, 1024 * 256)) >= 0 ) {
					out.write(bytes, 0, nbytes);
				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static JSONObject createpost(String id, String postText, String image)
	{
		
		
		JSONObject obj = new JSONObject();
		String fileid = null;
		if ( (postText != null && !postText.equals("")) || (image != null && !image.equals("")) ) {
			try {
				
				Connection conn = DriverManager.getConnection(connString, userName, passWord);
				
				if (image != null) {
					String sql = "insert into images(id, image) values (pseudo_encrypt(nextval('rand')), ?) returning images.id";
					PreparedStatement pStatement = conn.prepareStatement(sql);
					
					
					ByteArrayInputStream img = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(image));
					pStatement.setBinaryStream(1, img);
					if (pStatement.execute()) {
						ResultSet resultSet = pStatement.getResultSet();
						resultSet.next();
						long l = resultSet.getLong("id");
						System.out.println(l);
						fileid = "" + l;
					}
				}
				
				PreparedStatement pStmt = conn.prepareStatement("insert into post(uid,text,timestamp,imageid) values(?,?,CURRENT_TIMESTAMP, ?);");
				pStmt.setString(1, id);
				pStmt.setString(2, postText);
				pStmt.setString(3, fileid);
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
			}
			catch (Exception sqle)
			{
				sqle.printStackTrace();
			}
		}
		else {
			try {
				obj.put("status",false);
				obj.put("message", "Unable to create");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return obj;
	}
	
	public static String getname(String uid) {
		String name = "";
		try{   
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			PreparedStatement pStmt = conn.prepareStatement("select name from \"user\" where uid = ?;");
			
			pStmt.setString(1,  uid);
			ResultSet resultSet = pStmt.executeQuery();
			resultSet.next();
			name = resultSet.getString("name");

		}catch (Exception sqle)
			{
				sqle.printStackTrace();
			}
		return name;
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
		    PreparedStatement postSt = conn.prepareStatement("select name, postid,timestamp,uid,text,imageid from (select postid,timestamp,uid,text,imageid from post where post.uid = ? order by timestamp desc offset ? limit ?) as S natural left outer join \"user\"");
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
		    PreparedStatement postSt = conn.prepareStatement("select name,postid,timestamp,uid,text,imageid from (select postid,timestamp,uid,text,imageid from post where post.uid = ? order by timestamp desc offset ? limit ?) as S natural left outer join \"user\"");
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
	
	public static Timestamp getLatest (String id) {
		String sql = "select latest from \"user\" where uid = ?";
		Timestamp timestamp = null;
		try (Connection connection = DriverManager.getConnection(connString, userName, "");
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				timestamp = rs.getTimestamp("latest");
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			return new Timestamp(0);
		}
		return timestamp;
		
	}
	
	public static JSONArray seePosts(String id, int offset, int limit){
		JSONArray json = new JSONArray();
		String sql; 
		
		if (offset == -1) {
			sql = "select postid,timestamp,uid,text,name,imageid "
					+ "from (select postid,timestamp,uid,text,imageid "
					+ "			from post "
					+ "			where post.uid in (select uid2 "
					+ "								from follows "
					+ "								where uid1 = ? UNION select uid from \"user\" where uid=? ) "
					+ "			order by timestamp) as S natural left outer join \"user\""
					+ "where timestamp > ? order by timestamp asc offset ? limit ? ";
		}
		else {
//			sql = "select postid,timestamp,uid,text,name,imageid "
//					+ "from (select postid,timestamp,uid,text,imageid "
//					+ "			from post "
//					+ "			where post.uid in (select uid2 "
//					+ "								from follows "
//					+ "								where uid1 = ? UNION select uid from \"user\" where uid=? ) "
//					+ "			and timestamp =< ? order by timestamp desc ) as S natural left outer join \"user\""
//					+ "order by timestamp asc offset ? limit ?";
			
			sql = "select postid,timestamp,uid,text,name,imageid \n" + 
					"from (select postid,timestamp,uid,text,imageid \n" + 
					"			from post \n" + 
					"			where post.uid in (select uid2 \n" + 
					"								from follows \n" + 
					"								where uid1 = ? UNION select uid from \"user\" where uid=? ) \n" + 
					"			and timestamp <= ? order by timestamp desc offset ? limit ?) as S natural left outer join \"user\" \n" + 
					"order by timestamp asc";
		}
		try (
		    Connection conn = DriverManager.getConnection(
		    		connString, userName, "");
			PreparedStatement postSt = conn.prepareStatement(sql);
		)
		{
			postSt.setString(1, id);
			postSt.setString(2, id);
			postSt.setTimestamp(3, getLatest(id));
			if (offset == -1) {
				postSt.setInt(4, 0);
			}
			else {
				postSt.setInt(4, offset);
			}
			postSt.setInt(5, limit);			 
			ResultSet rs = postSt.executeQuery();
			json = ResultSetConverter(rs);
			if (offset == -1 && json.length() > 0) {
				Timestamp newLatest = ((Timestamp) ((JSONObject) json.get(json.length() - 1)).get("timestamp"));
				
				sql = "update \"user\" set latest = ? where uid = ?;";
				try (PreparedStatement pStatement = conn.prepareStatement(sql)) {
					pStatement.setTimestamp(1, newLatest);
					pStatement.setString(2, id);
					
					if (pStatement.executeUpdate() > 0) {
						System.out.println("is this cool? Check!");
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
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
			    PreparedStatement commSt = conn.prepareStatement("select timestamp,comment.uid, name, text, commentid from comment,\"user\" as us where postid = ? and us.uid=comment.uid order by timestamp asc")
			    		
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
	
	public static JSONArray getSuggestion(String search, String uid)
	{
		JSONArray jsonToSend = new JSONArray();
		if(search.length()<3)
			return jsonToSend;
		try (
			    Connection conn = DriverManager.getConnection(
			    		connString, userName, "");
				PreparedStatement commSt = conn.prepareStatement("select name,uid,email, (select count(*) from follows where uid1 = ? and uid2 = uid) > 0 as following from \"user\" where name like ? or uid like ? or email like ? limit 10");
			)
		{
			
			
			search = "%" + search + "%";
			commSt.setString(1, uid);
			commSt.setString(2, search);
			commSt.setString(3, search);
			commSt.setString(4, search);
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
