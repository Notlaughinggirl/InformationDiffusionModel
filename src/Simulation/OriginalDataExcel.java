/**
* This program save data into a excel
*/

package Simulation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import jxl.Workbook;
import jxl.write.Label;

public class OriginalDataExcel {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection( "jdbc:mysql://localhost:3306/web?useUnicode=true&characterEncoding=GB2312","root","123456");
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "select * from web.simulationdata";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery(sql);
			String filePath = "C:\\Users\\ran\\Desktop\\SimulationData.xls";

			File myFilePath = new File(filePath);
			if (!myFilePath.exists())
				myFilePath.createNewFile();
				
			OutputStream outf = new FileOutputStream(filePath);
			jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(outf);
			jxl.write.WritableSheet ws = wwb.createSheet("sheettest", 0);

			int i = 0;
			int j = 0;
			for (int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
				ws.addCell(new Label(k, 0, rs.getMetaData()
						.getColumnName(k + 1)));
			}
			while (rs.next()) {
				System.out.println(rs.getMetaData().getColumnCount());
				for (int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
					ws.addCell(new Label(k, j + i + 1, rs.getString(k + 1)));
				}
				i++;
			}
			wwb.write();
			wwb.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			conn.close();
		}
		}

}
