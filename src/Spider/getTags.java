/**
 *	This program get tags of weibo.
 */

package Spider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import weibo4j.Tags;
import weibo4j.model.Tag;
import weibo4j.model.TagWapper;
import weibo4j.model.WeiboException;

public class getTags{

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, WeiboException {
		String access_token = "2.00mi_foCCXcdbD1f937384f3lLHRPE";
		Tags tm = new Tags();
		tm.client.setToken(access_token);
		ArrayList<TagWapper> tags = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
			Statement state = connect.createStatement();
			for(int j = 2099;j < (44949 - 1)/ 20;j++){
				System.out.println(j);
				String uuids = "";
				for(int i = 20 * j + 1;i <= 20 * j + 20;i++){
					String weibotxt = "SELECT * FROM web.users where idusers = '" + i + "'";
					ResultSet rs = state.executeQuery(weibotxt);
					while(rs.next())uuids += (rs.getString("UID") + ",");
					rs.close();
				}
				String uids = uuids.substring(0,uuids.length()-1);
				System.out.println(uids);
				tags = tm.getTagsBatch(uids);
				for(int i = 0;i < tags.size();i++){
					String tagstr = "";
					String inserttag = "";
					for(Tag tag: tags.get(i).getTags()){				
						tagstr += tag.getValue(); 
					}					
					inserttag = "UPDATE web.users SET tags = '" + tagstr + "' where UID = '" + tags.get(i).getId() + "'";
					state.executeUpdate(inserttag);
				}
			}
			state.close();
			connect.close();
		}catch (WeiboException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
}

