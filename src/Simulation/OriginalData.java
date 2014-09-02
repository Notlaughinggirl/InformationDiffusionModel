/**
* This program set the original values for simulation data
*/

package Simulation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OriginalData {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		Statement state = connect.createStatement();
		String str = "select count(*) from web.users";
		ResultSet rs;
		rs = state.executeQuery(str);
		int total = 0;
		while(rs.next())
			total = rs.getInt(1);		
		rs.close();
		String nodeId = "";
		String screenName = "";
		int out = 0;
		int in = 0;
		String des = "";
		String tags = "";
		double inf = 0;
		double threshold = 0;
		
		//set initial values to simulation data
		for(int i = 1;i <= total;i++){
			System.out.println("开始读取数据" + i);
			String find = "select * from web.users where idusers='" + i + "'";
			ResultSet findrs = state.executeQuery(find);
			while(findrs.next()){
				nodeId = findrs.getString("UID");
				screenName = findrs.getString("screenName");
				des = findrs.getString("description");
				tags = findrs.getString("tags");
			}
			if(des == null)
				des = "";
			if(tags == null)
				tags = "";
			String outstr = "select count(*) from web.relationships where fromID ='" + i + "'";
			String instr = "select count(*) from web.relationships where toID ='" + i + "'";
			findrs = state.executeQuery(outstr);
			while(findrs.next())out = findrs.getInt(1);
			findrs = state.executeQuery(instr);
			while(findrs.next())in = findrs.getInt(1);
			System.out.println("获取inf" + i);
			inf = getInf(des, tags);
			threshold = getThreshold(inf);
			connect.prepareStatement("insert into web.simulationdata (nodeId, screenName, outDegree, inDegree, inf, threshold) values ('" + nodeId + "','" + screenName + "'," + out + "," + in + "," + inf + "," + threshold + ")").execute();
			System.out.println("insert into web.simulationdata (nodeId, screenName, outDegree, inDegree, inf, threshold) values ('" + nodeId + "','" + screenName + "'," + out + "," + in + "," + inf + "," + threshold + ")");
			findrs.close();
		}
		
		//give values for simulation data inf
		for(int i = 1;i <= total;i++){
			System.out.println("开始读取数据" + i);
			//get current inf
			ResultSet infrs = state.executeQuery("select * from web.simulationdata where idsimulationdata = " + i);
			while(infrs.next())
				inf = infrs.getDouble("inf");
			infrs.close();
			//get all the followers
			ResultSet trs = state.executeQuery("select * from web.relationships where fromID = " + i);
			ArrayList<String> totalfollowers = new ArrayList<String>();
			while(trs.next())
				totalfollowers.add(trs.getString("toID"));
			trs.close();
			//count the number of followers whose inf>0
			int _inf = 0; //表示inf>0的粉丝的个数
			double followersinf = 0;
			for(int j = 0;j < totalfollowers.size();j++){
				ResultSet irs = state.executeQuery("select * from web.simulationdata where idsimulationdata = " + totalfollowers.get(j) + " and inf>0");
				while(irs.next()){
						_inf++;
						followersinf += irs.getDouble("inf"); 
					}
				irs.close();
			}
			connect.prepareStatement("UPDATE web.simulationdata SET inf = " + getInf(inf, followersinf, _inf) + " where idsimulationdata = " + i);
			System.out.println("UPDATE web.simulationdata SET inf = " + getInf(inf, followersinf, _inf) + " where idsimulationdata = " + i);
		}		
		state.close();
		connect.close();
	}
	
	//calculate threshold
	private static double getThreshold(double d){
		return ((double)(0.5 * (1 - d))) > 0 ? ((double)(0.5 * (1 - d))) : 0;
	}
	
	//calculate inf
	private static double getInf(String des, String tags){
		double inf = (double)(getPublicWelFareNum(des) + getPublicWelFareNum(tags)) / (printChineseCharacterCount(des) + printChineseCharacterCount(tags));
		if((printChineseCharacterCount(des) + printChineseCharacterCount(tags) == 0))
			inf = 0;
		if(inf > 1)
			return 1;
		return inf;
	}
	
	private static double getInf(double inf, double followersinf, int n){
		if(n == 0)
			return 0;
		return (inf + followersinf / n) > 1 ? 1 : (inf + followersinf / n);
	}
	
	//count the number of key words
 	private static int getPublicWelFareNum(String tags){
		int ntags = 0;
		if(containsAny(tags, "微公益"))
			ntags += 3;
		if(containsAny(tags, "救助"))
			ntags += 2;
		if(containsAny(tags, "爱"))
			ntags++;
		else if (containsAny(tags, "爱心"))
			ntags += 2;
		if(containsAny(tags, "公益"))
			ntags += 2;
		if(containsAny(tags, "NGO"))
			ntags -= 2;
		if(containsAny(tags, "大病医保"))
			ntags += 4;
		if(containsAny(tags, "免费午餐"))
			ntags += 4;
		if(containsAny(tags, "儿慈会"))
			ntags += 3;
		if(containsAny(tags, "施乐会"))
			ntags += 3;
		if(containsAny(tags, "红十字"))
			ntags += 3;
		if(containsAny(tags, "捐助"))
			ntags += 2;
		if(containsAny(tags, "助医"))
			ntags += 2;
		if(containsAny(tags, "志愿者"))
			ntags += 3;
		if(containsAny(tags, "扶贫"))
			ntags += 2;
		if(containsAny(tags, "义工"))
			ntags += 2;
		if(containsAny(tags, "助学"))
			ntags += 2;
		if(containsAny(tags, "募捐"))
			ntags += 2;
		if(containsAny(tags, "正能量"))
			ntags += 3;
		if(containsAny(tags, "无偿"))
			ntags += 2;
		return ntags;
	}
	
	//check whether it contains key words
	private static boolean containsAny(String str, String searchChars) { 
		if(str.length() != str.replace(searchChars,"").length()){
			return true; 
		} 
		return false;
	}
	
	//count base
	private static int printChineseCharacterCount(String str) {  
	    int ccCount = 0;  
	    String regEx = "[\u0391-\uFFE5]";  
	    java.util.regex.Pattern p = java.util.regex.Pattern.compile(regEx);  
	    java.util.regex.Matcher m = p.matcher(str);  
	    while (m.find()) {  
	        for (int i = 0; i <= m.groupCount(); i++) {  
	            ccCount = ccCount + 1;  
	        }  
	    }
	    return ccCount;
	}
}
