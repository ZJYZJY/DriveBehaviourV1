package singleCar;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.DefaultEditorKit.InsertBreakAction;

import baseType.AccBean;
import baseType.FormatDatetime;
import baseType.GpsData;
import jdbc.SqlConnection;
import utils.Parser;


public class SingleCarLine {
	private static final int MAX_POINTS = 10; 
	 
	private String CARID;
	private String DATETIME;
	private double PK;
	private double NK;
	private double DY;
	private double AvgDeltaD;
	private List<GpsData> GpsDataList = new ArrayList<GpsData>();
	private List<AccBean> JiasuList = new ArrayList<AccBean>();
	private List<AccBean> JiansuList = new ArrayList<AccBean>();
	private List<AccBean> ZhuanwanList = new ArrayList<AccBean>();
	
	private AccBean maxspeed = new AccBean(0,0);
	private AccBean maxacceleration = new AccBean(0,0);
	
	public SingleCarLine(String carid,String datetime)
	{
		CARID = carid;
		DATETIME = datetime;
		PK = 0;
		NK = 0;
		getGpsData();
		//System.out.println("gpsdata");
		generateJiasuDataList();
		//System.out.println("jiasu");
		generateJiansuDataList();
		//System.out.println("jiansu");
		generateZhuanwanDataList();
		//System.out.println("zhuanwan");
	}
	
	public List<GpsData> getGpsDataList() {
		return GpsDataList;
	}

	public void setGpsDataList(List<GpsData> gpsDataList) {
		GpsDataList = gpsDataList;
	}

	public List<AccBean> getJiasuList() {
		return JiasuList;
	}

	public void setJiasuList(List<AccBean> jiasuList) {
		JiasuList = jiasuList;
	}

	public List<AccBean> getJiansuList() {
		return JiansuList;
	}

	public void setJiansuList(List<AccBean> jiansuList) {
		JiansuList = jiansuList;
	}

	public List<AccBean> getZhuanwanList() {
		return ZhuanwanList;
	}

	public void setZhuanwanList(List<AccBean> zhuanwanList) {
		ZhuanwanList = zhuanwanList;
	}

	public String getCARID() {
		return CARID;
	}

	public void setCARID(String cARID) {
		CARID = cARID;
	}

	public double getJiasuK() {
		return PK;
	}

	public void setJiasuK(double pk) {
		PK = pk;
	}

	public double getJiansuK() {
		return NK;
	}

	public void setJiansuK(double nk) {
		NK = nk;
	}

	public String getDATETIME() {
		return DATETIME;
	}

	public void setDATETIME(String dATETIME) {
		DATETIME = dATETIME;
	}

	public void getGpsData()
	{
		Connection conn = SqlConnection.getConnection();
		
		String dataTablename = "DATE"+DATETIME;		
		
		String sql = "SELECT * FROM "+dataTablename+" WHERE CAR_ID='"+CARID+"' ORDER BY DATEDAY,TIME";
		PreparedStatement pstmt;
		
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);			
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{	
				double speed = rs.getDouble("SPEED");
				String date = rs.getString("DATEDAY");
				String time = rs.getString("TIME");
				int direction = rs.getInt("DIRECTION");
				String datetime = FormatDatetime.toFormat(date+" "+time);
				GpsDataList.add(new GpsData(speed,datetime,direction));
			}			
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void generateJiasuDataList()
	{
		BigDecimal b = new BigDecimal(0);
		for(int i=0;i<GpsDataList.size()-1;i++)
		{			
			AccBean accbean = new AccBean();
			
			GpsData gpsdatatmp_before = GpsDataList.get(i);
			GpsData gpsdatatmp_after = GpsDataList.get(i+1);
			double speedbean = gpsdatatmp_after.getSpeed()/3.6; //加速用后速度
			b = new BigDecimal(speedbean);
			double tmp = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
			accbean.setSpeed(tmp); //速度
			
			double deltat = FormatDatetime.DeltaTime(gpsdatatmp_before.getDataTime(), gpsdatatmp_after.getDataTime());
			accbean.setDeltaT(deltat); //时间差
			
			double deltaspeed = (gpsdatatmp_after.getSpeed() - gpsdatatmp_before.getSpeed())/3.6;
			
			double accelerationbean = 0;
			if(deltat != 0)
			{
				accelerationbean = deltaspeed/deltat;
				b = new BigDecimal(accelerationbean);
				accelerationbean = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
				accbean.setAcceleration(accelerationbean); //加速度
				if(accelerationbean != 0)
				{		
					if(tmp > maxspeed.getSpeed())
					{
						maxspeed.setSpeed(tmp);
						maxspeed.setAcceleration(accelerationbean);
					}
					if(Math.abs(accelerationbean) > maxacceleration.getAcceleration())
					{	
						maxacceleration.setAcceleration(Math.abs(accelerationbean));
						maxacceleration.setSpeed(tmp);
					}
					
					if(accelerationbean > 0)
						JiasuList.add(accbean);					
				}
			}
		}
		
	}
	
	public void generateJiansuDataList()
	{
		BigDecimal b = new BigDecimal(0);
		for(int i=0;i<GpsDataList.size()-1;i++)
		{			
			AccBean accbean = new AccBean();
			
			GpsData gpsdatatmp_before = GpsDataList.get(i);
			GpsData gpsdatatmp_after = GpsDataList.get(i+1);
			double speedbean = gpsdatatmp_before.getSpeed()/3.6; //减速用前速度
			b = new BigDecimal(speedbean);
			double tmp = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
			accbean.setSpeed(tmp); //速度
			
			double deltat = FormatDatetime.DeltaTime(gpsdatatmp_before.getDataTime(), gpsdatatmp_after.getDataTime());
			accbean.setDeltaT(deltat); //时间差
			
			double deltaspeed = (gpsdatatmp_after.getSpeed() - gpsdatatmp_before.getSpeed())/3.6;
			
			double accelerationbean = 0;
			if(deltat != 0)
			{
				accelerationbean = deltaspeed/deltat;
				b = new BigDecimal(accelerationbean);
				accelerationbean = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
				accbean.setAcceleration(accelerationbean); //加速度
				if(accelerationbean != 0)
				{		
					if(tmp > maxspeed.getSpeed())
					{
						maxspeed.setSpeed(tmp);
						maxspeed.setAcceleration(Math.abs(accelerationbean));
					}
					if(Math.abs(accelerationbean) > maxacceleration.getAcceleration())
					{	
						maxacceleration.setAcceleration(Math.abs(accelerationbean));
						maxacceleration.setSpeed(tmp);
					}
					
					if(accelerationbean < 0)		
					{
						accbean.setAcceleration(Math.abs(accelerationbean));
						JiansuList.add(accbean);
					}
				}
			}
		}
	}
	
	private int getdeltad(int d1, int d2) {
		int deltad = Math.abs(d2 - d1);
		if(deltad > 180)
			deltad = 360 - deltad;
		if(deltad < 0) 
		{
			deltad = 0;
		}
		return deltad;
	}
	
	public void generateZhuanwanDataList()
	{
		BigDecimal b = new BigDecimal(0);
		double Avg = 0;
		for(int i=0;i<GpsDataList.size()-1;i++)
		{			
			AccBean accbean = new AccBean();
			
			GpsData gpsdatatmp_before = GpsDataList.get(i);
			GpsData gpsdatatmp_after = GpsDataList.get(i+1);
			double speedbean = gpsdatatmp_before.getSpeed()/3.6; //转弯用前速度
			b = new BigDecimal(speedbean);
			double tmp = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
			accbean.setSpeed(tmp); //速度
			
			double deltat = FormatDatetime.DeltaTime(gpsdatatmp_before.getDataTime(), gpsdatatmp_after.getDataTime());
			accbean.setDeltaT(deltat); //时间差
			if(deltat != 0)
			{
				int d1 = gpsdatatmp_before.getDirection();
				int d2 = gpsdatatmp_after.getDirection();
				int deltad = getdeltad(d1,d2);
				double accelerationbean = deltad/deltat;
				b = new BigDecimal(accelerationbean);
				accelerationbean = b.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
				Avg += accelerationbean;
				accbean.setAcceleration(accelerationbean); //角加速度
				if(accelerationbean > 0)
					ZhuanwanList.add(accbean);
			}
				
		}
		
		Avg = Avg/ZhuanwanList.size();
		AvgDeltaD = Avg;
	}
	
	

	public void findCloseLine(int sign)
	{
		List<AccBean> tmplist = new ArrayList<AccBean>();
		if(sign == 1)
			tmplist = new ArrayList<AccBean>(JiasuList);
		else 
			tmplist = new ArrayList<AccBean>(JiansuList);
		
		System.out.println(tmplist.size());
		
		RegressionLine line = new RegressionLine();  
        
        if(tmplist.size() > 10)
        {
        	for(int j=0;j<tmplist.size();j++)
        	{
        		double speed = tmplist.get(j).getSpeed();
        		double acceleration = tmplist.get(j).getAcceleration();
        		line.addDataPoint(new DataPoint(speed,acceleration));
        		
        	}
        
            double B = line.getB(); //获取斜率
            double delta_k = Math.abs(B/(double)10); 
    
            int sum=tmplist.size() , n=0;
            while( ((double)n)/((double)sum) < 0.95 && B<5)
            {
            	n=0;
            	for(int index=0;index<tmplist.size();index++)
            	{
            		double x = tmplist.get(index).getSpeed();
            		double y = tmplist.get(index).getAcceleration();
            		if(x*B > y)
            			n++;    //统计线上方的点个数      		
            	}
            	B = B + delta_k;   
            }
            
            if(sign == 1)
            	PK = B;
            else
            	NK = B;
            System.out.println(B);
            saveK(sign);
        }           
	}
	private void saveK(int sign) 
	{
		checkIsExist(CARID);
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		
		String sql = "";
		if(sign == 1)	
			sql = "UPDATE CLOSELINE"+DATETIME+" SET B="+PK+",COUNT="+JiasuList.size()
				+" WHERE CARID='"+CARID+"'";
		else
			sql = "UPDATE CLOSELINE"+DATETIME+" SET NB="+NK+",NCOUNT="+JiansuList.size()
				+" WHERE CARID='"+CARID+"'";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int k = pstmt.executeUpdate();
			pstmt.close();	
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void findDLine()
	{
		double Y = 0;
		if(ZhuanwanList.size() > 10)
        {
			double delta = AvgDeltaD / 10;
			int size = ZhuanwanList.size();
			int n = 0;
			Y = AvgDeltaD;
			while( (double)n/(double)size < 0.98 && (Y < 30) )
			{
				n = 0;
				for(int j=0;j<ZhuanwanList.size();j++)
				{
					if( ZhuanwanList.get(j).getAcceleration() < Y)
						n++;
				}
				Y += delta;
			}
			System.out.println(Y);
			DY = Y;
			saveY();
        }	
	}
	
	private void saveY() {
		checkIsExist(CARID);
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "UPDATE CLOSELINE"+DATETIME+" SET DY="+DY+",DCOUNT="+ZhuanwanList.size()
				+" WHERE CARID='"+CARID+"'";
		
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int k = pstmt.executeUpdate();
			pstmt.close();	
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void checkIsExist(String cARID2) {
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "SELECT COUNT(*) AS NUMS FROM CLOSELINE"+DATETIME+" WHERE CARID='"+CARID+"'";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				int count = rs.getInt("NUMS");
				if(count == 0)
					insertCar(conn,CARID);
			}
			pstmt.close();	
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void insertCar(Connection conn,String cARID2) {
		PreparedStatement pstmt;
		String sql = "INSERT INTO CLOSELINE"+DATETIME+" (CARID) VALUES('"+CARID+"')";
		try {			
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			int rsk = pstmt.executeUpdate();			
			pstmt.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getCaridList(String datetime)
	{
		Connection conn = SqlConnection.getConnection();
		PreparedStatement pstmt;
		String sql = "SELECT DISTINCT CAR_ID FROM CAR_ID_LIST"+datetime;
		List<String> caridlist = new ArrayList<String>();
		try {
			pstmt = (PreparedStatement)conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String carid = rs.getString("CAR_ID");
				caridlist.add(carid);
			}
			pstmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String sql2 = "SELECT DISTINCT CARID FROM CLOSELINE"+datetime;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql2);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				String carid = rs.getString("CARID");
				caridlist.remove(carid);
			}
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		
		return caridlist;
	}
	
	public static void main(String[] args) {
		
		String Datetime = Parser.getDate();
		List<String> caridlist = getCaridList(Datetime);
		System.out.println(caridlist.size());
		for(int index=0;index<caridlist.size();index++)
		{			
			String Carid = caridlist.get(index);
			System.out.println(Carid);
			SingleCarLine car = new SingleCarLine(Carid,Datetime);
			car.findCloseLine(1);
			car.findCloseLine(2);	
			car.findDLine();
			System.out.println();
		}
		
	}

}
