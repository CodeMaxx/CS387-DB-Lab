import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.apache.tomcat.jni.File;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.regexp.internal.recompile;
import com.sun.org.apache.xpath.internal.operations.And;

/**
 * Servlet implementation class Image
 */
@WebServlet("/Image")
public class Image extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String fileid = request.getParameter("id");
		
		if ( request.getSession(false) != null ) {
			OutputStream outputStream = response.getOutputStream();
			if (!DbHandler.getimage(fileid, outputStream)) {
				response.sendError(404);
			}
			outputStream.close();
//			java.io.File file = new java.io.File( "img" + fileid + ".jpg");
//			if (file.exists()) {
//				
//				FileInputStream fStream = new FileInputStream(file);
//				response.setContentType("image/jpep");
//				byte[] bytes = new byte[1024 * 256];
//				int nbytes;
//				
//				OutputStream out = response.getOutputStream();
//				
//				while ( (nbytes = fStream.read(bytes, 0, 1024 * 256)) >= 0) {
//					out.write(bytes, 0, nbytes);
//				}
//				
//				out.flush();
//				out.close();
//			}
//			else {
//				response.sendError(404);
//			}
		}
		else {
			response.sendError(404);
		}
		System.out.println("Working Directory = " +
	              System.getProperty("user.dir") + " ---- " + 
	      		this.getClass().getProtectionDomain().getCodeSource().getLocation());
		String rootPath = System.getProperty("catalina.home");
		System.out.println(rootPath + " -- " +
			getServletContext().getRealPath("/"));
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
