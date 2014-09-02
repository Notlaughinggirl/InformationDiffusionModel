/**
 * This program is to get the basic information of the acquired Weibo id
 */
 
package Spider;

import weibo4j.Timeline;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import java.io.*;
import java.sql.*;

public class InfoSpider {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	
	public static String ID = "3582883820680490";//抓取微博的微博id
	public static int pageNum = 2;//分页数
	
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String access_token="2.00mi_foCkJihXC140f1a9708Aqjq8D";
		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		int i = 1;
		int num = 1;		
		Paging page = new Paging();
		page.setCount(200);
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/weibo?useUnicode=true&characterEncoding=GB2312","root","123456");
		try {
			while(i < pageNum){
				page.setPage(i);
				StatusWapper status = tm.getRepostTimeline(ID, page);
				for(Status s : status.getStatuses()){
						connect.prepareStatement("INSERT INTO baseinfo VALUES ('" + num + "','" + s.getUser().getId() + "','" + s.getUser().getScreenName() + "','" + s.getUser().getCreatedAt() + "','" + s.getUser().getFriendsCount() + "','" + s.getUser().getFollowersCount() + "','" + s.getUser().getDescription().replace("'", "") + "','" + s.getId() + "','" + s.getRepostsCount() + "','" + s.getCreatedAt() + "','" + s.getText().replace("'", "") + "')").execute();
						num++;
					}
				i++;
			}		
		} catch (WeiboException e) {
			e.printStackTrace();
		}		
	}		
}
