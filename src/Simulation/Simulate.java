/**
* This program do the simulation
*/

package Simulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Simulate {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws BiffException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, BiffException, RowsExceededException, WriteException {
		Class.forName("com.mysql.jdbc.Driver");
		File fileInode = new File("C:\\Users\\ran\\Desktop\\Inode.txt");
		File fileRnode = new File("C:\\Users\\ran\\Desktop\\Rnode.txt");
		File fileIedge = new File("C:\\Users\\ran\\Desktop\\Iedge.txt");
		File fileRedge = new File("C:\\Users\\ran\\Desktop\\Redge.txt");
		Connection connect = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		Statement state = connect.createStatement();
		int total = 0;
		int T = 0, begin = 1;
		int edgeid = 1;
		ArrayList<Integer> I  = new ArrayList<Integer>();
		I.add(1);
		while(true){
			
			System.out.println("开始算法");
			
			T++;
			total = I.size();
			
			System.out.println("当前I集合总数" + total);
			
			//Traverse all the nodes in I
			for(int i = begin - 1;i <= total;i++){
				
				System.out.println("当前的信息源节点" + I.get(i));
				
				//get children
				ArrayList<String> al = getSons(I.get(i));
				//traverse children
				for(int j = 0;j < al.size();j++){
					//visit children
//					int degree = 0;
					boolean inI = false;
					for(int m = 0;m < I.size();m++)
						if(I.get(m).equals(al.get(j)))
							inI = true;
					if(inI)
						break;
					String nodestr = "select * from web.simulationdata where idsimulationdata = " + al.get(j);
					ResultSet noders = state.executeQuery(nodestr);
					int id = 0;
					double threshold = 0;
					while(noders.next()){
						id = noders.getInt("idsimulationdata");
						threshold = noders.getDouble("threshold");
					}
					noders.close();
//					ArrayList<String> alsons = getSons(id);
					ArrayList<String> alfathers = getFathers(id);					
//					//get the sum of degree of neighbors of current node
//					for(int k = 0;k < alsons.size();k++)
//						degree += getDegree(Integer.parseInt(alsons.get(k)));
//					for(int k = 0;k < alfathers.size();k++)
//						degree += getDegree(Integer.parseInt(alfathers.get(k)));
//					
//					System.out.println(degree);
					
					double b = 0;
					//traverse the parents of the current nodes to determine whether or not be infected
					for(int k = 0;k < alfathers.size();k++){
						double W = 0;
						String nodesfathertr = "select * from web.simulationdata where idsimulationdata =" + alfathers.get(k);
						ResultSet nodesfatherrs = state.executeQuery(nodesfathertr);
						double fatherinf =0;
						int num = 0;
						while(nodesfatherrs.next()){
							//check whether its parent get infected
							for(int m = 0;m < I.size();m++){
								if(nodesfatherrs.getInt("idsimulationdata") == I.get(m)){
									fatherinf= nodesfatherrs.getDouble("inf");
									num++;
								}
							}
						
							System.out.println("当前父亲的inf" + fatherinf);
						
						}
						if( fatherinf > 0)
							 W = getW(Integer.parseInt(al.get(j)),Integer.parseInt(alfathers.get(k)));
						b += (fatherinf + W);	
						if(num > 0)
							b = b / num;
					}
					
					System.out.println("b=" + b);
					
					System.out.println("开始判断");
					
					if(b > threshold){
						writeNode(fileInode, id, T);
						writeEdge(fileIedge, id, i, edgeid);
						I.add(id);
						
						System.out.println("I: " + id + " " + threshold);
						
					}
					else{
						writeNode(fileRnode, id, T);
						writeEdge(fileRedge, id, i, edgeid);
						
						System.out.println("R: " + id + " " + threshold);
						
					}
					edgeid++;
					
					System.out.println("写入数据" + i + "得到的结果");
					
				}			
			}
			
			if(begin > total)
				break;
			else
				begin = total + 1;			
		}		
		state.close();
		connect.close();
	}
	
	//get w 
	private static double getW(int i, int j) throws BiffException, IOException{
		
		System.out.println(i + "节点" + j + "的W为"+ ((double)(getC(i,j)) / (getOut(i,j) - 2 + getC(i,j))));
		
		return ((double)(getC(i,j)) / (getOut(i,j) - 2 + getC(i,j)));
	}
	
	//get the total degree of current node from excel spreadsheet
	private static int getDegree(int i) throws BiffException, IOException{
		String path = "simulationData.xls";
		InputStream is = new FileInputStream(path);
		jxl.Workbook wb = Workbook.getWorkbook(is);
		Sheet st = wb.getSheet(0);
		int degree = Integer.parseInt(st.getCell(3, i).getContents()) + Integer.parseInt(st.getCell(4, i).getContents());		
		wb.close();
		
		System.out.println("节点" + i + "的度数和为" + degree);
		
		return degree;
	}
	
	//get the total followers of current node
	private static int getOut(int i, int j) throws BiffException, IOException{
		String path = "simulationData.xls";
		InputStream is = new FileInputStream(path);
		jxl.Workbook wb = Workbook.getWorkbook(is);
		Sheet st = wb.getSheet(0);
		int degree = Integer.parseInt(st.getCell(4, j).getContents()) + Integer.parseInt(st.getCell(4, i).getContents());		
		wb.close();
		
		System.out.println("节点" + i + "的度数和为" + degree);
		
		return degree;
	}
	
	//get C by ID
	private static int getC(int i, int j) throws BiffException, IOException{		
//		ArrayList<String> sonsi = getSons(i);
//		ArrayList<String> sonsj = getSons(j);
		ArrayList<String> fathersi = getFathers(i);
		ArrayList<String> fathersj = getFathers(j);
		int C = 0;
//		for(int k = 0;k < sonsi.size();k++)
//			for(int r = 0;r < sonsj.size();r++){
//				if(sonsi.get(k).equals(sonsj.get(r)))
//						C++;
//			}
		for(int k = 0;k < fathersi.size();k++)
			for(int r = 0;r < fathersj.size();r++){
				if(fathersi.get(k).equals(fathersj.get(r)))
						C++;
			}
		
		System.out.println(i + "节点" + j + "的C为"+ C);
		
		return C;
	}
	
	//get children by id
	private static ArrayList<String> getSons(int i) throws BiffException, IOException{
		String path = "simulationWeb.xls";
		InputStream is = new FileInputStream(path);
		jxl.Workbook wb = Workbook.getWorkbook(is);
		Sheet st = wb.getSheet(0);
		ArrayList<String> sons = new ArrayList<String>();
		for(int k = 0; k < st.getRows();k++){
			Cell cell = st.getCell(0,k);
			if(Integer.parseInt(cell.getContents()) == i)
				sons.add(st.getCell(1, k).getContents());
		}
		wb.close();
		return sons;
	}
	
	//get parent by id
	private static ArrayList<String> getFathers(int i)throws BiffException, IOException{
		String path = "simulationWeb.xls";
		InputStream is = new FileInputStream(path);
		jxl.Workbook wb = Workbook.getWorkbook(is);
		Sheet st = wb.getSheet(0);
		ArrayList<String> fathers = new ArrayList<String>();
		for(int k = 0; k < st.getRows();k++){
			Cell cell = st.getCell(1,k);
			if(Integer.parseInt(cell.getContents()) == i)
				fathers.add(st.getCell(0, k).getContents());
		}
		wb.close();		
		return fathers;
	}
	
	private static void writeNode(File file, int id, int t) throws IOException, SQLException{
		FileWriter nfw = new FileWriter(file, true);
		nfw.write("<node id=\"" + id + "\" label=\"" + t + "\"/>\r\n");
		nfw.close();
	}
	
	private static void writeEdge(File file, int id, int father, int num) throws IOException, SQLException{
		FileWriter efw = new FileWriter(file, true);
		efw.write("<edge id=\"" + num + "\" source=\"" + father + "\" target=\"" + id +"\"/>\r\n");
		efw.close();
	}
}
