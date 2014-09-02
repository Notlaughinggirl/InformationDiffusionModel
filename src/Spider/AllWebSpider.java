/**
 *	In this program, we get weibo users acount's follower's ids
 */

package Spider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import weibo4j.Friendships;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class AllWebSpider {
	
	public static String UID = "2735747990";//Weibo acount ID
	
	//acquired network doesn't include initial node
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String access_token="2.00mi_foCkJihXC140f1a9708Aqjq8D";
		Class.forName("com.mysql.jdbc.Driver");
		BFS(UID,access_token);
	}
	
	//search and check whether to visit
	public static void BFS(String uid, String access_token) throws SQLException{
		int userId = 2;
		visit(uid, access_token);
		while(true){
			String userid = getUid(userId);
			if(userid.equals("NULL"))
				break;
			if(isRepost(userid))
				visit(userid, access_token);
			userId++;
		}
	}
	
	//check current whether node retweet 
	public static boolean isRepost(String uid) throws SQLException{
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/weibo?useUnicode=true&characterEncoding=GB2312","root","123456");
		String weibotxt = "SELECT * FROM weibo.baseinfo where userid = '" + uid + "'";
		Statement state = connect.createStatement();
		ResultSet rs = state.executeQuery(weibotxt);
		boolean bl = rs.next();
		rs.close();
		state.close();
		connect.close();
		return bl;
	}

	//get uid from next visited node
	public static String getUid(int num) throws SQLException{
		String id;
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		String weibotxt = "SELECT * FROM web.users where idusers = '" + num + "'";
		Statement state = connect.createStatement();
		ResultSet rs = state.executeQuery(weibotxt);
		if(rs.next())
			id = rs.getString("UID");
		else id = "NULL";
		rs.close();
		state.close();
		connect.close();
		return id;
	}
	
	//check whether or not current node has been visited 
	public static boolean in(String uid) throws SQLException{
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		String weibotxt = "SELECT * FROM web.users where UID = '" + uid + "'";
		Statement state = connect.createStatement();
		ResultSet rs = state.executeQuery(weibotxt);
		boolean bl = rs.next();
		rs.close();
		state.close();
		connect.close();
		return bl;
	}
	
	//visit current node and save follower info into database
	public static void visit(String uid, String access_token) throws SQLException{
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		Friendships fm = new Friendships();
		fm.client.setToken(access_token);
		Statement state = connect.createStatement();		
		int nextCursor = 0;
		String from ="";
		String to = "";
		try {
			while(true){				
				UserWapper users = fm.getFollowersById(uid, 200, nextCursor);
				for(User u : users.getUsers()){
					if(!in(u.getId())){
						System.out.println("INSERT INTO web.users (UID, screenName, description) VALUES ('" + u.getId() + "','" + u.getScreenName() + "','" + u.getFollowersCount() + "','" + u.getFriendsCount() + "','" + u.getDescription().replace("'", "").replace("\\", "") + "')");	
						connect.prepareStatement("INSERT INTO web.users (UID, screenName, description) VALUES ('" + u.getId() + "','" + u.getScreenName() + "','" + u.getDescription().replace("'", "").replace("\\", "").replace(")", "") + "')").execute();
					}
					ResultSet rs;
					rs = state.executeQuery("select idusers from web.users where UID = '" + uid + "'");
					while(rs.next())from = rs.getString("idusers");
					rs = state.executeQuery("select idusers from web.users where UID = '" + u.getId() + "'");
					while(rs.next())to = rs.getString("idusers");
					connect.prepareStatement("INSERT INTO web.relationships (fromID, toID) VALUES ('" + from + "','" + to + "')").execute();
					rs.close();
				}
				nextCursor = (int) users.getNextCursor();
				if(nextCursor == 0)
					break;
				System.out.println("当前的uid是" + uid + "    当前的nextcursor是" + nextCursor);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		state.close();
		connect.close();
	}
}
