/** 
 * File        : DataPoint.java 
 * Author      : zhouyujie 
 * Date        : 2012-01-11 16:00:00 
 * Description : Java实现一元线性回归的算法，回归线实现类，(可实现统计指标的预测) 
 */ 
package singleCar;

import java.math.BigDecimal;  
import java.util.ArrayList;
import java.util.List;  
  
public class RegressionLine // implements Evaluatable  
{  
    /** sum of x */  
    private double sumX;  
  
    /** sum of y */  
    private double sumY;  
  
    /** sum of x*x */  
    private double sumXX;  
  
    /** sum of x*y */  
    private double sumXY;  
  
    /** sum of y*y */  
    private double sumYY;  
  
    /** sum of yi-y */  
    private double sumDeltaY;  
  
    /** sum of sumDeltaY^2 */  
    private double sumDeltaY2;  
  
    /** 误差 */  
    private double sse;  
  
    private double sst;  
  
    private double E;  
  
    private String[] xy;  
  
    private List listX;  
    private List listY;  
  
    private double XMin, XMax, YMin, YMax;  
  
    /** line coefficient a */  
    private double a;  
  
    /** line coefficient b */  
    private double b;  
  
    /** number of data points */  
    private int pn;  
  
    /** true if coefficients valid */  
    private boolean coefsValid;  
  
    /** 
     * Constructor. 
     */  
    public RegressionLine() {  
        XMax = 0;  
        YMax = 0;  
        pn = 0;  
        xy = new String[2];  
        listX = new ArrayList();  
        listY = new ArrayList();  
    }  
  
    /** 
     * Constructor. 
     *  
     * @param data 
     *            the array of data points 
     */  
    public RegressionLine(DataPoint data[]) {  
        pn = 0;  
        xy = new String[2];  
        listX = new ArrayList();  
        listY = new ArrayList();  
        for (int i = 0; i < data.length; ++i) {  
            addDataPoint(data[i]);  
        }  
    }  
  
    /** 
     * Return the current number of data points. 
     *  
     * @return the count 
     */  
    public int getDataPointCount() {  
        return pn;  
    }  
  
    /** 
     * Return the coefficient a. 
     *  
     * @return the value of a 
     */  
    public double getA() {  
        validateCoefficients();  
        return a;  
    }  
  
    /** 
     * Return the coefficient b. 
     *  
     * @return the value of b 
     */  
    public double getB() {  
        validateCoefficients();  
        return b;  
    }  
  
    /** 
     * Return the sum of the x values. 
     *  
     * @return the sum 
     */  
    public double getSumX() {  
        return sumX;  
    }  
  
    /** 
     * Return the sum of the y values. 
     *  
     * @return the sum 
     */  
    public double getSumY() {  
        return sumY;  
    }  
  
    /** 
     * Return the sum of the x*x values. 
     *  
     * @return the sum 
     */  
    public double getSumXX() {  
        return sumXX;  
    }  
  
    /** 
     * Return the sum of the x*y values. 
     *  
     * @return the sum 
     */  
    public double getSumXY() {  
        return sumXY;  
    }  
  
    public double getSumYY() {  
        return sumYY;  
    }  
  
    public double getXMin() {  
        return XMin;  
    }  
  
    public double getXMax() {  
        return XMax;  
    }  
  
    public double getYMin() {  
        return YMin;  
    }  
  
    public double getYMax() {  
        return YMax;  
    }  
  
    /** 
     * Add a new data point: Update the sums. 
     *  
     * @param dataPoint 
     *            the new data point 
     */  
    public void addDataPoint(DataPoint dataPoint) {  
        sumX += dataPoint.x;  
        sumY += dataPoint.y;  
        sumXX += dataPoint.x * dataPoint.x;  
        sumXY += dataPoint.x * dataPoint.y;  
        sumYY += dataPoint.y * dataPoint.y;  
  
        if (dataPoint.x > XMax) {  
            XMax =  dataPoint.x;  
        }  
        if (dataPoint.y > YMax) {  
            YMax =  dataPoint.y;  
        }  
  
        // 把每个点的具体坐标存入ArrayList中，备用  
  
        xy[0] =  dataPoint.x + "";  
        xy[1] =  dataPoint.y + "";  
        if (dataPoint.x != 0 && dataPoint.y != 0) {  
            /*System.out.print(xy[0] + ",");  
            System.out.println(xy[1]); */ 
  
            try {  
                // System.out.println("n:"+n);  
            	//System.out.println("pn="+pn+", xy[0]="+xy[0]);
                listX.add(xy[0]);  
                listY.add(xy[1]);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
  
            /* 
             * System.out.println("N:" + n); System.out.println("ArrayList 
             * listX:"+ listX.get(n)); System.out.println("ArrayList listY:"+ 
             * listY.get(n)); 
             */  
        }  
        ++pn;  
        coefsValid = false;  
    }  
  
    /** 
     * Return the value of the regression line function at x. (Implementation of 
     * Evaluatable.) 
     *  
     * @param x 
     *            the value of x 
     * @return the value of the function at x 
     */  
    public double at(int x) {  
        if (pn < 2)  
            return Float.NaN;  
  
        validateCoefficients();  
        return a + b * x;  
    }  
  
    /** 
     * Reset. 
     */  
    public void reset() {  
        pn = 0;  
        sumX = sumY = sumXX = sumXY = 0;  
        coefsValid = false;  
    }  
  
    /** 
     * Validate the coefficients. 计算方程系数 y=bx+a 中的a,b 
     */  
    private void validateCoefficients() {  
        if (coefsValid)  
            return;  
        
        if (pn >= 2) {  
            double xBar = (double) sumX / pn;  
            double yBar = (double) sumY / pn;  
  
            b = (double) ((pn * sumXY - sumX * sumY) / (pn * sumXX - sumX  
                    * sumX));  
            a = (double) (yBar - b * xBar);  
        } else {  
            a = b = Float.NaN;  
        }  
        
        coefsValid = true;  
    }  
  
    /** 
     * 返回误差 
     */  
    public double getR() {  
        // 遍历这个list并计算分母  
        for (int i = 0; i < pn - 1; i++) {  
            double Yi = (double) Integer.parseInt(listY.get(i).toString());  
            double Y = at(Integer.parseInt(listX.get(i).toString()));  
            double deltaY = Yi - Y;  
            double deltaY2 = deltaY * deltaY;  
            /* 
             * System.out.println("Yi:" + Yi); System.out.println("Y:" + Y); 
             * System.out.println("deltaY:" + deltaY); 
             * System.out.println("deltaY2:" + deltaY2); 
             */  
  
            sumDeltaY2 += deltaY2;  
            // System.out.println("sumDeltaY2:" + sumDeltaY2);  
  
        }  
  
        sst = sumYY - (sumY * sumY) / pn;  
        // System.out.println("sst:" + sst);  
        E = 1 - sumDeltaY2 / sst;  
  
        return round(E, 4);  
    }  
  
    // 用于实现精确的四舍五入  
    public double round(double v, int scale) {  
  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
  
    }  
  
    public float round(float v, int scale) {  
    	  
        if (scale < 0) {  
            throw new IllegalArgumentException(  
                    "The scale must be a positive integer or zero");  
        }  
  
        BigDecimal b = new BigDecimal(Double.toString(v));  
        BigDecimal one = new BigDecimal("1");  
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();  
  
    }  
}  
