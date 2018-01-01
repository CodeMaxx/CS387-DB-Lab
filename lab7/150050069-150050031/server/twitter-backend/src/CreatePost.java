

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;

/**
 * Servlet implementation class CreatePost
 */
@WebServlet("/CreatePost")
public class CreatePost extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreatePost() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();	
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");	
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) 
		{
			try {
				obj.put("staus", false);
				obj.put("message", "Invalid session");
				out.print(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else 
		{
			String id = (String)request.getSession().getAttribute("id");
			String post = request.getParameter("content");
			String image = request.getParameter("image");
			
			if (image != null) {
				System.out.println(image);
			}
			else {
				System.out.println("No photo");
			}
			
			out.print(DbHandler.createpost(id, post, image));
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		InputStream inputStream = request.getInputStream();
//		int nbytes;
//		byte[] bytes = new byte[1024];
//		StringBuilder stringBuilder = new StringBuilder();
//		while ((nbytes = inputStream.read(bytes, 0, 1024)) >= 0) {
//			stringBuilder.append(new String(bytes));
//		}
//		
//		
//		System.out.println(stringBuilder.toString());
		
		
//		JSONObject object;
//		try {
//			object = new JSONObject(stringBuilder.toString());
//			String image;
//			image = object.getString("image");
//			if (image != null) {
////				System.out.println(image);
				
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		doGet(request, response);
	}

}
