package baseType;
	
public class GpsData {
	private double Jd;
	private double Wd;
	private double Speed;
	private int Direction;
	private String DataTime;
	
	public GpsData()
	{
		Jd = -1;
		Wd = -1;
		Speed = -1;
		Direction = -1;
		DataTime = "";
	}
	
	public GpsData(double jd, double wd, double speed, int direction, String datatime)
	{
		Jd = jd;
		Wd = wd;
		Speed = speed;
		Direction = direction;
		DataTime = datatime;
	}

	public GpsData(double speed, String datatime, int direction)
	{
		Jd = 0;
		Wd = 0;
		Speed = speed;
		Direction = direction;
		DataTime = datatime;
	}
	
	public void setJd(double jd)	{
		Jd = jd;
	}
	
	public double getJd(){
		return Jd;
	}	

	public double getWd() {
		return Wd;
	}

	public void setWd(double wd) {
		Wd = wd;
	}

	public double getSpeed() {
		return Speed;
	}

	public void setSpeed(double speed) {
		Speed = speed;
	}

	public int getDirection() {
		return Direction;
	}

	public void setDirection(int direction) {
		Direction = direction;
	}

	public String getDataTime() {
		return DataTime;
	}

	public void setDataTime(String dataTime) {
		DataTime = dataTime;
	}
	
	public static void main(String[] args) {
		
	}
}