/**
*	This program get the users network
*/

package GetWeb;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetAllWeb {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		String userstxt = "SELECT * FROM web.users";
		String relationshipstxt = "SELECT * FROM web.relationships";
		writeNode(userstxt);
		writeEdge(relationshipstxt);		
	}
	
	private static void writeNode(String str) throws IOException, SQLException{
		File file = new File("C:\\Users\\ran\\Desktop\\node.txt");
		FileWriter nfw = new FileWriter(file);
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		Statement state = connect.createStatement();
		ResultSet rs = state.executeQuery(str);
		while(rs.next()){
			nfw.write(rs.getString("idusers") + " \"" + rs.getString("screenName") + "\"\r\n");
		}
		nfw.close();
		rs.close();
		state.close();
		connect.close();
	}
	
	private static void writeEdge(String str) throws IOException, SQLException{
		File edgefile = new File("C:\\Users\\ran\\Desktop\\edge.txt");
		FileWriter efw = new FileWriter(edgefile);
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		Statement state = connect.createStatement();
		ResultSet rs = state.executeQuery(str);
		while(rs.next()){
			efw.write(rs.getString("fromID") + " " + rs.getString("toID") + "\r\n");
		}
		efw.close();
		rs.close();
		state.close();
		connect.close();
	}

}
