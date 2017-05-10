package baseType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDatetime {

	public static String toFormat(String datetime)
	{		
		String formattime = "";
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd HHmmss");
		Date date = null;
		try {
			date = sdf1.parse(datetime); //将date+time转换成date格式
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formattime = sdf2.format(date);//format将其转换成需要的格式
		
		return formattime;
	}
	
	public static double DeltaTime(String datetime1,String datetime2)
	{
		Date date1 = null,date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date1 = sdf.parse(datetime1);
			date2 = sdf.parse(datetime2);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		return (date2.getTime()-date1.getTime())/1000;		
	}
	
}
