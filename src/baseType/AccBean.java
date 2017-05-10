package baseType;

public class AccBean {
	
	private double Speed;	
	private double Acceleration;
	private double DeltaT;
	private String DataTime="";
	
	public AccBean()
	{
		Speed = 0;
		Acceleration = 0;
		DeltaT = 0;
	}
	public AccBean(double speed,double acceleration)
	{
		Speed = speed;
		Acceleration = acceleration;
	}
	
	public double getSpeed() {
		return Speed;
	}
	public void setSpeed(double speed) {
		Speed = speed;
	}
	public double getAcceleration() {
		return Acceleration;
	}
	public void setAcceleration(double acceleration) {
		Acceleration = acceleration;
	}
	public String getDataTime() {
		return DataTime;
	}
	public void setDataTime(String dataTime) {
		DataTime = dataTime;
	}

	public double getDeltaT() {
		return DeltaT;
	}

	public void setDeltaT(double deltaT) {
		DeltaT = deltaT;
	}
}
