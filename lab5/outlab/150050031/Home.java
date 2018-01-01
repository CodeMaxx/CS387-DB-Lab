

import java.io.*;
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
				PreparedStatement stmt = conn.prepareStatement("select uid, name, email from users");
			){
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					list += "{ label: \"";
					list += rs.getString(1);
					list += " | ";
					list += rs.getString(2);
					list += " | ";
					list += rs.getString(3);
					list += "\", value: \"";
					list += rs.getString(1);
					list += "\"}, ";
				}
				list = list.substring(0, list.length() - 1);

		} catch (SQLException e) {
			out.println("error");
		}

		String s = "";
//		ClassLoader cl = this.getClass().getClassLoader();
//		InputStream is = cl.getResourceAsStream("./resources/home");
//		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
//
//		String line = buf.readLine();
//		StringBuilder sb = new StringBuilder();
//
//		while(line != null){
//		   sb.append(line).append("\n");
//		   line = buf.readLine();
//		}
//
//		s = sb.toString();

		s += "<html><head>";
		s += "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.1/jquery-ui.css\">\n" +
				"        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
				"        <script src=\"https://code.jquery.com/jquery-1.12.4.js\"></script>\n" +
				"        <script src=\"https://code.jquery.com/ui/1.12.1/jquery-ui.js\"></script>" +
				"<script type=\"text/javascript\" language=\"javascript\" src=\"https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js\"></script>" +
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.datatables.net/1.10.16/css/jquery.dataTables.min.css\">";

		s += "\n<script type=\"text/javascript\">";
		s += "var offset = 0;\n" +
		"var more_posts = 1;" +
				"var showing_user = '';" +
				"            var limit = 10;"
				+ "var table;"
				+ "var dict = {};"
				+ "alert = function() {};";
		s += "\n			$(document).ready(function() {\n" +
				"                var allIDs = [";

		s += list;

		s += "];\n" +
				"\n" +
				"var show_write = function() {\n" +
				"                    $(\"#content\").hide();\n" +
				"                    $(\"#write_post\").show();\n" +
				"                    $(\"#follow_table\").hide();\n" +
				"                };\n" +
				"\n" +
				"                var show_content = function() {\n" +
				"                    $(\"#write_post\").hide();\n" +
				"                    $(\"#content\").show();\n" +
				"                    $(\"#follow_table\").hide();\n" +
				"                };\n" +
				"\n" +
				"                var show_table = function() {\n" +
				"                    $(\"#write_post\").hide();\n" +
				"                    $(\"#content\").hide();\n" +
				"                    $(\"#follow_table\").show();\n" +
				"                }" +
				"\n" +
				"                $(\"#write_post\").hide();\n" +
				"\n" +
				"$(\"body\").on('click', '#more_home_post', function(){\n" +
//				"alert('more_home_post');" +
				"                    $.post(\"./GetPosts\",\n" +
				"                        {\n" +
				"                            id: \"*\",\n" +
				"                            type: \"follow\",\n" +
				"                            offset: window.offset.toString(),\n" +
				"                            limit: window.limit.toString(),\n" +
				"                        },\n" +
				"                        function(data, status){\n" +
				"                            $('#more_home_post').remove();\n" +
//				"alert(data);" +
				"                            var all_parsed_data = JSON.parse(data);\n" +
				"                            var s = '';\n" +
				"                            var parsed_data = all_parsed_data[0]['posts'];\n" +
				"\n" +
				"                            window.more_posts = all_parsed_data[0]['more'];\n" +
				"                            for(var key in parsed_data) {\n" +
				"                                s += \"<b>\" + parsed_data[key].name + \"</b><br>\";\n" +
				"                                s += parsed_data[key].text + \"<br>\" + parsed_data[key].date_trunc + \"<br><br>\";\n" +
				"                                var comments = parsed_data[key].comments\n" +
				"                                s += \"<div id='\" + parsed_data[key].postid + \"'>\"\n" +
				"                                var j ='';\n" +
				"                                var more = 0;\n" +
				"                                for(var comkey in comments) {\n" +
				"                                    if(comkey < 3) {//new\n" +
				"                                        s += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                        s += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                    }\n" +
				"                                    else\n" +
				"                                        more = 1;\n" +
				"                                    j += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                    j += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                }\n" +
				"                                if(more)\n" +
				"                                    s += \"<button postid='\" + parsed_data[key].postid + \"' onClick='more_comments(this)'>More</button>\";\n" +
				"                                window.dict[parsed_data[key].postid] = j;\n" +
				"                                s += \"</div>\";\n" +
				"                                s += \"<hr>\";\n" +
				"                            }\n" +
//				"alert(s);" +
"                                if(window.more_posts)\n" +
"                                    s += \"<button id='more_home_post'>More</button>\\n\"\n" +
				"                            $(\"#content\").append(s);\n" +
				"                            window.offset += 10;\n" +
				"                    });\n" +
				"                });" +
				"" +
				"$(\"body\").on('click', '#more_all_post', function(){\n" +
				"                    $.post(\"./GetPosts\",\n" +
				"                        {\n" +
				"                                id: window.showing_user.toString(),\n" +
				"                                type: \"all\",\n" +
				"                                offset: window.offset.toString(),\n" +
				"                                limit: window.limit.toString(),\n" +
				"                        },\n" +
				"                        function(data, status){\n" +
				"                            $('#more_all_post').remove();\n" +
				"                            var all_parsed_data = JSON.parse(data);\n" +
				"                            var s = '';\n" +
				"                            var parsed_data = all_parsed_data[0]['posts'];\n" +
				"\n" +
				"                            window.more_posts = all_parsed_data[0]['more'];\n" +
				"                            for(var key in parsed_data) {\n" +
				"                                s += \"<b>\" + parsed_data[key].name + \"</b><br>\";\n" +
				"                                s += parsed_data[key].text + \"<br>\" + parsed_data[key].date_trunc + \"<br><br>\";\n" +
				"                                var comments = parsed_data[key].comments\n" +
				"                                s += \"<div id='\" + parsed_data[key].postid + \"'>\"\n" +
				"                                var j ='';\n" +
				"                                var more = 0;\n" +
				"                                for(var comkey in comments) {\n" +
				"                                    if(comkey < 3) {//new\n" +
				"                                        s += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                        s += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                    }\n" +
				"                                    else\n" +
				"                                        more = 1;\n" +
				"                                    j += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                    j += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                }\n" +
				"                                if(more)\n" +
				"                                    s += \"<button postid='\" + parsed_data[key].postid + \"' onClick='more_comments(this)'>More</button>\";\n" +
				"                                window.dict[parsed_data[key].postid] = j;\n" +
				"                                s += \"</div>\";\n" +
				"                                s += \"<hr>\";\n" +
				"                            }\n" +
				"                                if(window.more_posts)\n" +
				"                                    s += \"<button id='more_all_post'>More</button>\\n\"\n" +
				"                            $(\"#content\").append(s);\n" +
				"                            window.offset += 10;\n" +
				"                    });\n" +
				"                });" +
				"\n" +
				"                $(\"#home_button\").click(function(){\n" +
				"                    show_content();\n" +
				"                    $.post(\"./GetPosts\",\n" +
				"                        {\n" +
				"                            id: \"*\",\n" +
				"                            type: \"follow\",\n" +
				"offset: 0,\n" +
				"                            limit: window.limit.toString()," +
				"                        },\n" +
				"                        function(data, status){\n" +
				"var all_parsed_data = JSON.parse(data);\n" +
				"                            var s = '';\n" +
				"                            var parsed_data = all_parsed_data[0].posts;\n" +
				"                            window.more_posts = all_parsed_data[0].more;\n" +
				"" +
				"for(var key in parsed_data) {\n" +
				"                                s += \"<b>\" + parsed_data[key].name + \"</b><br>\";\n" +
				"                                s += parsed_data[key].text + \"<br>\" + parsed_data[key].date_trunc + \"<br><br>\";\n" +
				"                                var comments = parsed_data[key].comments\n" +
				"                                s += \"<div id='\" + parsed_data[key].postid + \"'>\"\n" +
				"                                var j ='';\n" +
				"                                var more = 0;\n" +
				"                                for(var comkey in comments) {\n" +
				"                                    if(comkey < 3) {//new\n" +
				"                                        s += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                        s += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                    }\n" +
				"                                    else\n" +
				"                                        more = 1;\n" +
				"                                    j += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                    j += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                }\n" +
				"                                if(more)\n" +
				"                                    s += \"<button postid='\" + parsed_data[key].postid + \"' onClick='more_comments(this)'>More</button>\";\n" +
				"                                window.dict[parsed_data[key].postid] = j;\n" +
				"                                s += \"</div>\";\n" +
				"                                s += \"<hr>\";\n" +
				"                            }" +
				"                                if(window.more_posts)\n" +
				"                                    s += \"<button id='more_home_post'>More</button>\\n\"\n" +
				"                            $(\"#content\").html(s);\n" +
				"                            window.offset = 10;" +
				"                    });\n" +
				"                });\n" +
				"\n" +
				"			$(\"#uid\").autocomplete({\n" +
				"                    source: allIDs,\n" +
				"                    select: function(event, ui) {\n" +
				"                        show_content();\n" +
				"                        var userid = ui.item.value;\n" +
				"						window.showing_user = ui.item.value;" +
				"                        $.post(\"./GetPosts\",\n" +
				"                            {\n" +
				"                                id: userid,\n" +
				"                                type: \"all\",\n" +
				"								offset: 0,\n" +
				"                                limit: window.limit.toString()," +
				"                            },\n" +
				"						function(data, status){\n" +
				"                            var all_parsed_data = JSON.parse(data);\n" +
				"                            var s = '';\n" +
				"                            var parsed_data = all_parsed_data[0]['posts'];\n" +
				"\n" +
				"                            window.more_posts = all_parsed_data[0]['more'];\n" +
				"                            for(var key in parsed_data) {\n" +
				"                                s += \"<b>\" + parsed_data[key].name + \"</b><br>\";\n" +
				"                                s += parsed_data[key].text + \"<br>\" + parsed_data[key].date_trunc + \"<br><br>\";\n" +
				"                                var comments = parsed_data[key].comments\n" +
				"                                s += \"<div id='\" + parsed_data[key].postid + \"'>\"\n" +
				"                                var j ='';\n" +
				"                                var more = 0;\n" +
				"                                for(var comkey in comments) {\n" +
				"                                    if(comkey < 3) {//new\n" +
				"                                        s += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                        s += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                    }\n" +
				"                                    else\n" +
				"                                        more = 1;\n" +
				"                                    j += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"                                    j += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"                                }\n" +
				"                                if(more)\n" +
				"                                    s += \"<button postid='\" + parsed_data[key].postid + \"' onClick='more_comments(this)'>More</button>\";\n" +
				"                                window.dict[parsed_data[key].postid] = j;\n" +
				"                                s += \"</div>\";\n" +
				"                                s += \"<hr>\";\n" +
				"                            }\n" +
				"								if(window.more_posts)\n" +
				"                                    s += \"<button id='more_all_post'>More</button>\\n\";" +
				"                            $(\"#content\").html(s);\n" +
				"                            window.offset = 10;\n" +
				"                    });" +
//				"						ui.item.value = '';\n" +
//				"						$('#uid').innerHTML = '';" +
				"                    }\n" +
				"                });" +
				"\n" +
				"                $(\"#add_post\").click(function(){\n" +
				"                    show_write();\n" +
				"                });\n" +
				"				$(\"#following\").click(function(){\n" +
				"                    $.post(\"./GetFollow\",{\n" +
				"                        id: \"0000\",\n" +
				"                        unfollow: 0,\n" +
				"                    },\n" +
				"                    function(data, status) {\n" +
//				"alert(data);" +
				"                        var parsed_data = JSON.parse(data);\n" +
				"                        var s = \"\";\n" +
				"s += '<thead><tr><th>UID</th><th>Name</th><th>Unfollow</th></tr></thead>';" +
				"s += '<tfoot><tr><th>UID</th><th>Name</th><th>Unfollow</th></tr></tfoot>';" +
				"s += '<tbody>';" +
				"                        for(var key in parsed_data) {\n" +
				"                            s += \"<tr>\";\n" +
				"                            s += \"<td>\" + parsed_data[key].uid + \"</td>\";\n" +
				"                            s += \"<td>\" + parsed_data[key].name + \"</td>\";\n" +
				"                            s += \"<td><input type='button' user='\"+ parsed_data[key].uid + \"' value='X' onclick='deleteRow(this)'></td>\";\n" +
				"                            s += \"</tr>\";\n" +
				"                        }\n"
				+ "s+= '</tbody>';" +
//				"alert(s);" +
				"						$(\"#follow_table\").html(s); " +
				"					window.table = $('#follow_table').DataTable({\"order\": [[ 1, \"asc\" ]]});" +
				"						show_table();" +
				"                    });\n" +
				"                });" +
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
				"                    show_content();\n" +
				"                });" +
				"\n" +
				"                $(\"#home_button\").click();\n" +
				"            });\n" +
				"        </script>\n" +
				"<style>\n" +
				"table {\n" +
				"    font-family: arial, sans-serif;\n" +
				"    border-collapse: collapse;\n" +
				"    width: 100%;\n" +
				"}\n" +
				"\n" +
				"td, th {\n" +
				"    border: 1px solid #dddddd;\n" +
				"    text-align: left;\n" +
				"    padding: 8px;\n" +
				"}\n" +
				"\n" +
				"tr:nth-child(even) {\n" +
				"    background-color: #dddddd;\n" +
				"}\n" +
				"</style>" +
				"<script>\n" +
				"function deleteRow(r) {\n" +
				"    if (confirm('Are you sure ?')) {\n" +
				"        var i = r.parentNode.parentNode.rowIndex;\n" +
				"        var user = $(r).attr(\"user\");\n" +
				"        console.log(user);\n" +
				"        $.post(\"./GetFollow\",{\n" +
				"            id: user,\n" +
				"            unfollow: 1,\n" +
				"        },\n" +
				"        function(data, status) {\n" +
				"            console.log(\"unfollowed\");\n" +
				"        });\n" +
				"console.log($(r).parents('tr').html());\n" +
				"	window.table.row($(r).closest('tr')).remove();\n"
				+ "window.table.draw();" +
				"    }\n" +
				"}\n" +
				"</script>" +
				"<script>\n" +
				"function more_comments(r) {\n" +
				"    var post = $(r).attr(\"postid\");\n" +
				"    $.post(\"./GetComments\",{\n" +
				"        postid: post,\n" +
				"    },\n" +
				"    function(data, status){\n" +
				"        var comments = JSON.parse(data);\n" +
				"        var s = '';\n" +
				"        for(var comkey in comments) {\n" +
				"            s += \"<i>\" + comments[comkey].name + \":</i>\";\n" +
				"            s += comments[comkey].text + \"<br>\" + comments[comkey].date_trunc + \"<br>\";\n" +
				"        }\n" +
				"        $(\"#\"+post).html(s);\n" +
				"    })\n" +
				"}\n" +
				"</script>" +
				"    </head>\n";

		s +=	 "    <body>\n" +
				"        <button class=\"ui-button ui-widget ui-corner-all\" id=\"home_button\">Home</button>\n" +
				"        <button class=\"ui-button ui-widget ui-corner-all\" id=\"add_post\">Add Post</button>\n" +
				"        <button class=\"ui-button ui-widget ui-corner-all\" id=\"following\">Following</button>\n" +
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
				"<table class='display' cellspacing=\"0\" width=\"100%\" id=\"follow_table\">"+
				"</table>" +
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
