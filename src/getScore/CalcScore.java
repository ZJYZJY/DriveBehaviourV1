package getScore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import baseType.AccBean;
import jdbc.SqlConnection;
import singleCar.SingleCarLine;
import utils.Parser;

public class CalcScore {

	public static String DATETIME;
	public static double PThreshold;
	public static double NThreshold;
	public static double DThreshold;
	
	public static List<String> getCaridList()
	{
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "SELECT DISTINCT CARID FROM CLOSELINE"+DATETIME;
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
	}
	
	public static void getThreshold()
	{
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "SELECT * FROM THRESHOLD WHERE DATETIME='"+DATETIME+"'";
		
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				PThreshold = rs.getDouble("PB");
				NThreshold = rs.getDouble("NB");
				DThreshold = rs.getDouble("DY");
			}
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static double getScore_1(String carid,List<AccBean> datalist,int sign) 
	{
		double threshold = 0;
		if(sign == 1) 
			threshold = PThreshold;
		else
			threshold = NThreshold;
		int count = 0;
		for(int i=0;i<datalist.size();i++)
		{
			double speed = datalist.get(i).getSpeed();
			double acceleration = datalist.get(i).getAcceleration();
			if(acceleration > speed * threshold )
				count++;
		}
		double score = (double)count/(double)datalist.size();
		saveScore(carid,score,datalist.size(),sign);
		return score;
	}
	
	private static void checkIsExist(Connection conn,String carid) 
	{
		PreparedStatement pstmt;
		String sql = "SELECT COUNT(*) AS NUMS FROM SCORE"+DATETIME+" WHERE CARID='"+carid+"'";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				int count = rs.getInt("NUMS");
				if(count == 0)
					insertCar(conn,carid);
			}
			pstmt.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void insertCar(Connection conn,String carid) {
		PreparedStatement pstmt;
		String sql = "INSERT INTO SCORE"+DATETIME+" (CARID) VALUES('"+carid+"')";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int rsk = pstmt.executeUpdate();			
			pstmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static double getZhuanwanScore(String carid,List<AccBean> datalist) 
	{
		int count = 0 ;
		for(int i=0;i<datalist.size();i++)
		{
			double acceleration = datalist.get(i).getAcceleration();
			if(acceleration > DThreshold)
				count++;
		}
		double score = (double)count/(double)datalist.size();
		saveScore(carid, score, datalist.size(), 3);
		return score;
	}
	
	public static void getTotalScore(String carid,double pscore,double nscore,double dscore) 
	{
		double totalscore = pscore + nscore + dscore;
		saveScore(carid, totalscore, 0, 4);
	}
	
	public static void saveScore(String carid, double score, int size,int sign) 
	{
		Connection conn = SqlConnection.getConnection();
		checkIsExist(conn,carid);
		PreparedStatement pstmt;
		String sql = "";
		if(sign == 1)
			sql = "UPDATE SCORE"+DATETIME+" SET PSCORE="+score+" WHERE CARID="+carid;
		if(sign == 2)
			sql = "UPDATE SCORE"+DATETIME+" SET NSCORE="+score+" WHERE CARID="+carid;
		if(sign == 3)
			sql = "UPDATE SCORE"+DATETIME+" SET DSCORE="+score+" WHERE CARID="+carid;
		if(sign == 4)
			sql = "UPDATE SCORE"+DATETIME+" SET TOTALSCORE="+score+" WHERE CARID="+carid;
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int rsk = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println(carid+" "+score+","+sign);
	}
	public static void main(String[] args) {
		DATETIME = Parser.getDate();
		getThreshold(); //获取阈值
		
		List<String> caridlist = getCaridList();//车辆列表
		System.out.println(caridlist.size());
		
		for(int i=0;i<caridlist.size();i++)
		{
			String carid = caridlist.get(i);
			System.out.println(carid);
			SingleCarLine singlecar = new SingleCarLine(carid,DATETIME);
			List<AccBean> jiasulist = singlecar.getJiasuList();
			
			List<AccBean> jiansulist = singlecar.getJiansuList();
			
			List<AccBean> zhuanwanlist = singlecar.getZhuanwanList();
			
			double pscore = getScore_1(carid,jiasulist,1);
			double nscore = getScore_1(carid,jiansulist,2);
			double dscore = getZhuanwanScore(carid,zhuanwanlist);
			getTotalScore(carid,pscore,nscore,dscore);
		}
	}
}
