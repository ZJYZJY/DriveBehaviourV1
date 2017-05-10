package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 73958 on 2017/5/10.
 */
public class Parser {

    /**
     * 用当前系统日期
     */
    public static String getDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    /**
     * 用当前系统时间
     */
    public static String getTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(date);
    }
}
