/**
*	This program get the information network of a weibo
*/

package GetWeb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class GetInfoWeb {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	
	public static String ROOT = "陕西身边事";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/weibo?useUnicode=true&characterEncoding=GB2312","root","123456");
		Statement state = connect.createStatement();
		File nodefile = new File("C:\\Users\\ran\\Desktop\\node.txt");
		FileWriter nfw = new FileWriter(nodefile);
		File edgefile = new File("C:\\Users\\ran\\Desktop\\edge.txt");
		FileWriter efw = new FileWriter(edgefile);
		int edgeid = 1;
		ArrayList<String> al = new ArrayList<String>();
		al.add(ROOT);
		ResultSet idrs = state.executeQuery("select name from weibo.baseinfo group by name");
		while(idrs.next()){
			if(!(idrs.getString("name").equals(ROOT)))
				al.add(idrs.getString("name"));
		}
		idrs.close();
		for(int i = 0;i < al.size();i++)
			nfw.write("<node id=\"" + i + "\" label=\"" + al.get(i) + "\"/>\r\n");
		ResultSet rs = state.executeQuery("select name, status from weibo.baseinfo");
		while(rs.next()){ 
			String status = rs.getString("status");
			String name = rs.getString("name");
			String[] path = getNode(status, name);
			int[] pathnode = new int[path.length];
			for(int i = 0;i < path.length;i++)
				for(int j = 0;j < al.size();j++){
					if(path[i].equals(al.get(j))){
						pathnode[i] = j;
						break;
					}
			}
			for(int j = pathnode.length - 1;j > 0; j--){
				efw.write("<edge id=\"" + edgeid++ + "\" source=\"" + pathnode[j] + "\" target=\"" + pathnode[j - 1] +"\"/>\r\n");
			}
		}
		rs.close();
		nfw.close();
		efw.close();	
		state.close();
		connect.close();		
	}
	
	private static String[] getNode(String str, String name){	
		String[] node;
		node = str.split("//@");
		String[] s = new String[node.length + 1];
		for(int i = 0;i < node.length;i++){
			node[i].replace("http:", "");
			if(node[i].indexOf(":") >= 0)
				s[i] = node[i].substring(0, node[i].indexOf(":"));
			else
				s[i] = name;
		}
		s[node.length] = ROOT;
		s[0] = name;
		return s;		
	}

}
