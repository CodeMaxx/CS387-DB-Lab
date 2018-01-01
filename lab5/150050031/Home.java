

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Login.checkLogin(request, response);
		PrintWriter out = response.getWriter();
		String list = "";
		try (
			    Connection conn = DriverManager.getConnection(
			    		Login.host, Login.username, Login.password);
				PreparedStatement stmt = conn.prepareStatement("select uid from users");
			){
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					list += "\"";
					list += rs.getString(1);
					list += "\",";
				}
				list = list.substring(0, list.length() - 1);

		} catch (SQLException e) {
			out.println("error");
		}

		String s = "";
		s += "<html><head>";
		s += "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.css\">\n" +
				"        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
				"        <script src=\"https://code.jquery.com/jquery-1.12.4.js\"></script>\n" +
				"        <script src=\"https://code.jquery.com/ui/1.12.1/jquery-ui.js\"></script>";

		s += "\n<script type=\"text/javascript\">";
		s += "\n			$(document).ready(function() {\n" +
				"                var allIDs = [";

		s += list;

		s += "];\n" +
				"\n" +
				"                var hidecontent = function() {\n" +
				"                    $(\"#content\").hide();\n" +
				"                    $(\"#write_post\").show();\n" +
				"                };\n" +
				"\n" +
				"                var showcontent = function() {\n" +
				"                    $(\"#write_post\").hide();\n" +
				"                    $(\"#content\").show();\n" +
				"                };\n" +
				"\n" +
				"                $(\"#write_post\").hide();\n" +
				"\n" +
				"                $(\"#home_button\").click(function(){\n" +
				"                    showcontent();\n" +
				"                    $.post(\"./GetPosts\",\n" +
				"                        {\n" +
				"                            id: \"*\",\n" +
				"                            type: \"follow\"\n" +
				"                        },\n" +
				"                        function(data, status){\n" +
				"                            $(\"#content\").html(data);\n" +
				"                    });\n" +
				"                });\n" +
				"\n" +
				"			$(\"#uid\").autocomplete({\n" +
				"                    source: allIDs,\n" +
				"                    select: function(event, ui) {\n" +
				"                        showcontent();\n" +
				"                        var userid = ui.item.value;\n" +
				"                        $.post(\"./GetPosts\",\n" +
				"                            {\n" +
				"                                id: userid,\n" +
				"                                type: \"all\"\n" +
				"                            },\n" +
				"                            function(data, status){\n" +
				"                                $(\"#content\").html(data);\n" +
				"                        });\n" +
				"						ui.item.value = '';\n" +
				"						$('#uid').innerHTML = '';" +
				"                    }\n" +
				"                });" +
				"\n" +
				"                $(\"#add_post\").click(function(){\n" +
				"                    hidecontent();\n" +
				"                });\n" +
				"\n" +
				"			$(\"#publish\").click(function(event){\n" +
				"                    event.preventDefault();\n" +
				"                    var url = $(\"post_form\").attr( 'action' );\n" +
				"                    var cont = $(\"#cont\").val();\n" +
				"                    $.post('./WritePost',\n" +
				"                        { content: cont },\n" +
				"                        function( data ) {\n" +
				"                            alert(data);\n" +
				"                        });\n" +
				"					document.getElementById(\"cont\").value = '';" +
				"                    showcontent();\n" +
				"                });" +
				"\n" +
				"                $(\"#home_button\").click();\n" +
				"            });\n" +
				"        </script>\n" +
				"    </head>\n";

		s +=	 "    <body>\n" +
				"        <button class=\"ui-button ui-widget ui-corner-all\" id=\"home_button\">Home</button>\n" +
				"        <button class=\"ui-button ui-widget ui-corner-all\" id=\"add_post\">Add Post</button>\n" +
				"\n" +
				"        <br><br>\n" +
				"\n" +
				"        <div class=\"ui-widget\">\n" +
				"            <label>Search: </label>\n" +
				"            <input id=\"uid\">\n" +
				"        </div>\n" +
				"        <br>\n" +
				"<hr>" +
				"\n" +
				"        <div id=\"content\">\n" +
				"        </div>\n" +
				"\n" +
				"        <div id=\"write_post\">\n" +
				"            <form action=\"./WritePost\" method=\"post\" id=\"post_form\">\n" +
				"                <input type=\"text\" name=\"cont\" id=\"cont\">\n" +
				"                <input class=\"ui-button ui-widget ui-corner-all\" id=\"publish\" type=\"submit\" value=\"Publish\">\n" +
				"            </form>\n" +
				"        </div>\n" +
				"<a href=\"./Logout\">Logout</a>" +
				"    </body>\n" +
				"</html>\n";

		out.println(s);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
