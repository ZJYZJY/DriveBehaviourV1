package baseType;

public class TestTime
{
	long foreTime;
	
	public TestTime()
	{
		foreTime=System.currentTimeMillis();
	}

	public void showTime(String str)
	{
		long currTime=System.currentTimeMillis();
		System.out.println(str+"："+(currTime-foreTime));
		foreTime=currTime;
	}
	
}
