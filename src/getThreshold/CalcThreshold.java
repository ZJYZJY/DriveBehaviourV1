package getThreshold;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import jdbc.SqlConnection;
import utils.Parser;

public class CalcThreshold {

	public static double PThreshold;
	public static double NThreshold;
	public static double DThreshold;
	public static String DATETIME;
	public static List<Double> pBList = new ArrayList<Double>();
	public static List<Double> nBList = new ArrayList<Double>();
	public static List<Double> dYList = new ArrayList<Double>();
	
	
	
	public static double getPThreshold() {
		return PThreshold;
	}

	public static void setPThreshold(double pThreshold) {
		PThreshold = pThreshold;
	}

	public static double getNThreshold() {
		return NThreshold;
	}

	public static void setNThreshold(double nThreshold) {
		NThreshold = nThreshold;
	}

	public static double getDThreshold() {
		return DThreshold;
	}

	public static void setDThreshold(double dThreshold) {
		DThreshold = dThreshold;
	}

	public static String getDATETIME() {
		return DATETIME;
	}

	public static void setDATETIME(String dATETIME) {
		DATETIME = dATETIME;
	}

	public static void getKList()
	{
		List<Double> blist = new ArrayList<Double>();
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "SELECT * FROM CLOSELINE"+DATETIME;
		
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				double pb = rs.getDouble("B");
				double nb = rs.getDouble("NB");
				double dy = rs.getDouble("DY");
				pBList.add(pb);
				nBList.add(nb);
				dYList.add(dy);
			}
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/*public static List<String> getCaridList(String datetime)
	{
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "SELECT CARID FROM CLOSELINE"+datetime+" WHERE SIGN=0";
		List<String> caridlist = new ArrayList<String>();
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String carid = rs.getString("CARID");
				caridlist.add(carid);
			}
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return caridlist;
	}*/
	
	public static double getCloseLineThreshold(List<Double> dataSet) {
	     
		Kmeans k=new Kmeans(3);						
		k.setDataSet(dataSet);		
		k.execute();		
		List<List<Double>> cluster = k.getCluster();		
		List<Double> center = k.getCenter();
							
		int max = cluster.get(0).size();
		int maxi = 0;
		for(int i=1;i<cluster.size();i++) //找出最大的类簇
		{
			if(cluster.get(i).size() > max)
			{
				max = cluster.get(i).size();
				maxi = i;
			}					
		}
		return center.get(maxi);
	}
	
	private static void checkIsExist(Connection conn) 
	{
		PreparedStatement pstmt;
		String sql = "SELECT COUNT(*) AS NUMS FROM THRESHOLD WHERE DATETIME='"+DATETIME+"'";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				int count = rs.getInt("NUMS");
				if(count == 0)
					insertCar(conn);
			}
			pstmt.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void insertCar(Connection conn) {
		PreparedStatement pstmt;
		String sql = "INSERT INTO THRESHOLD (DATETIME) VALUES('"+DATETIME+"')";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int rsk = pstmt.executeUpdate();			
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void saveThreshold() {
		Connection conn = SqlConnection.getConnection();
		checkIsExist(conn);
		PreparedStatement pstmt;
		String sql = "UPDATE THRESHOLD SET PB="+PThreshold+",NB="+NThreshold+",DY="+DThreshold
				+" WHERE DATETIME='"+DATETIME+"'";
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int rsk = pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		CalcThreshold.setDATETIME(Parser.getDate());
		getKList();
		double pthreshold = getCloseLineThreshold(CalcThreshold.pBList);
		CalcThreshold.setPThreshold(pthreshold);
		
		double nthreshold = getCloseLineThreshold(CalcThreshold.nBList);
		CalcThreshold.setNThreshold(nthreshold);
		
		double dthreshold = getCloseLineThreshold(CalcThreshold.dYList);
		CalcThreshold.setDThreshold(dthreshold);
		
		saveThreshold();
	}

	

}
