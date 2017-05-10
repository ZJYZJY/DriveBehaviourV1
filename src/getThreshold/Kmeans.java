package getThreshold;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * K均值聚类算法
 */
public class Kmeans {
	private int k;// 分成多少簇
	private int m;// 迭代次数
	private int dataSetLength;// 数据集元素个数，即数据集的长度
	private List<Double> dataSet;// 数据集链表
	private List<Double> center;// 中心链表
	private List<List<Double>> cluster; // 簇
	private List<Double> jc;// 误差平方和，k越接近dataSetLength，误差越小
	private Random random;
	private double MinK = 0;
	private double MaxK = 0;
	private double MiddleK = 0;
	/**
	 * 设置需分组的原始数据集
	 * 
	 * @param dataSet
	 */

	public void setDataSet(List<Double> dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * 获取结果分组
	 * 
	 * @return 结果集
	 */

	public List<List<Double>> getCluster() {
		return cluster;
	}
	
	public List<Double> getCenter() {
		return center;
	}
	
	

	/**
	 * 构造函数，传入需要分成的簇数量
	 * 
	 * @param k
	 *            簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
	 */
	public Kmeans(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
	}

	/**
	 * 初始化
	 */
	private void init() {
		m = 0;
		random = new Random();
		if (dataSet == null || dataSet.size() == 0) {
			initDataSet();
		}
		dataSetLength = dataSet.size();
		if (k > dataSetLength) {
			k = dataSetLength;
		}
		center = initCenters2();
		cluster = initCluster();
		jc = new ArrayList<Double>();
	}

	/**
	 * 如果调用者未初始化数据集，则采用内部测试数据集
	 */
	private void initDataSet() {
		/*dataSet = new ArrayList<Double>();
		// 其中{6,3}是一样的，所以长度为15的数据集分成14簇和15簇的误差都为0
		ArrayList<Double> dataSetArray = new ArrayList<Double>();

		for (int i = 0; i < dataSetArray.size(); i++) {
			dataSet.add(dataSetArray.get(i));
		}*/
	}

	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点
	 * 
	 * @return 中心点集
	 */
	private List<Double> initCenters() {
		List<Double> centertmp = new ArrayList<Double>();
		int[] randoms = new int[k];
		boolean flag;
		int temp = random.nextInt(dataSetLength);
		randoms[0] = temp;
		//System.out.println("temp = "+temp);

		for (int i = 1; i < k; i++) {
			flag = true; //判重标志
			while (flag) {				
				temp = random.nextInt(dataSetLength);
				int j = 0;
				while (j < i) 
				{
					double now = dataSet.get(temp);
					double before = dataSet.get(randoms[j]);
					//去和前面已经随机出的i-1个中心点比较，看有没有重复的值（值不同而不只是位置不同）, 时间复杂度约为 k*(k-1)
					//切记在list中的值做比较时，必须取出到临时变量，不然比的是引用
					if (now == before) 
					{						
						break;					
					}
					j++;
				}
				if (j == i) {  //说明没有重复
					flag = false;
				}
			}
			//System.out.println("temp = "+temp);
			randoms[i] = temp;
		}
		/*for(int i=0 ; i<k; i++)
			System.out.println("random["+i+"]= "+randoms[i]);*/
		 		
		for (int i = 0; i < k; i++) {
			centertmp.add(dataSet.get(randoms[i]));// 生成初始化中心链表
			//System.out.println(dataSet.get(randoms[i]));
		}
		return centertmp;
	}

	private List<Double> initCenters2()
	{
		List<Double> centertmp = new ArrayList<Double>();
		
		MiddleK = dataSet.get(dataSetLength/2);
		MinK = dataSet.get(0);
		MaxK = dataSet.get(dataSetLength-1);
		for(int i=0;i<dataSetLength;i++)
		{
			double tmp = dataSet.get(i);
			if(tmp < MinK)
			{
				//System.out.println("min"+i);
				MinK = tmp;
			}
			if(tmp > MaxK)
			{
				//System.out.println("max"+i);
				MaxK = tmp;
			}
		}
		centertmp.add(MinK);
		centertmp.add(MiddleK);
		centertmp.add(MaxK);
		return centertmp;
	}
	/**
	 * 初始化簇集合
	 * 
	 * @return 一个分为k簇的空数据的簇集合
	 */
	private List<List<Double>> initCluster() {
		List<List<Double>> cluster = new ArrayList<List<Double>>();
		for (int i = 0; i < k; i++) {
			cluster.add(new ArrayList<Double>());
		}

		return cluster;
	}

	/**
	 * 计算两个点之间的距离
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 欧几里得距离
	 */
	private double distance(double element, double center) {
		double distance = 0.0;
		
		double t1 = (element - center);
		
		distance = Math.abs(t1);
		
		//distance=Math.abs(element-center);
		
		return distance;
	}

	/**
	 * 获取距离集合中最小距离的位置
	 * 
	 * @param distance
	 *            距离数组
	 * @return 最小距离在距离数组中的位置
	 */
	private int minDistance(double[] distance) {
		double minDistance = distance[0];
		int minLocation = 0;
		for (int i = 1; i < distance.length; i++) {
			if (distance[i] < minDistance) 
			{
				minDistance = distance[i];
				minLocation = i;
			} else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
			{
				if(cluster.get(i).size()<cluster.get(minLocation).size())
					minLocation = i;
			}
		}

		return minLocation;
	}

	/**
	 * 核心，将当前元素放到最小距离中心相关的簇中
	 */
	private void clusterSet() {
		double[] distance = new double[k];
		for (int i = 0; i < dataSetLength; i++) 
		{
			for (int j = 0; j < k; j++) 
			{
				distance[j] = distance(dataSet.get(i), center.get(j));
				// System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);

			}
			int minLocation = minDistance(distance);
			// System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
			// System.out.println();

			cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

		}
	}

	/**
	 * 求两点误差平方的方法
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 误差平方
	 */
	private double errorSquare(double element, double  center) {
		double distance = 0.0;
		
		double t1 = (element - center);		
		double errSquare = t1*t1;
		//System.out.println("SSE="+errSquare);

		return errSquare;
	}

	/**
	 * 计算误差平方和准则函数方法
	 */
	private void countRule() {
		double jcF = 0;
		double temp = 0;
		for (int i = 0; i < cluster.size(); i++) 
		{
			for (int j = 0; j < cluster.get(i).size(); j++) 
			{
				temp = errorSquare(cluster.get(i).get(j), center.get(i));
				jcF += temp;
			}
		}
		jc.add(jcF);
	}

	/**
	 * 设置新的簇中心方法
	 */
	private void setNewCenter() {
		double temp = 0 ;
		
		double ksum=0.0;
		
		for (int i = 0; i < k; i++) 
		{
			double newCenter = 0;//非常重要，这是引用，每次都要new！！！
			ksum=0.0;
			int n = cluster.get(i).size();
			if (n != 0) 
			{				
				for (int j = 0; j < n; j++) 
				{
					temp = cluster.get(i).get(j);
					ksum += temp;
				}
				// 设置一个平均值
				newCenter = ksum/n;				
				//System.out.println("newCenter"+i+": "+newCenter.getSpeed()+", "+newCenter.getAcceleration());
				center.set(i, newCenter);
			}
		}
	}
	
	
	
	/**
	 * 打印数据，测试用
	 * 
	 * @param dataArray
	 *            数据集
	 * @param dataArrayName
	 *            数据集名称
	 */
	/*public void printDataArray(List<Car_bean> dataArray,
			String dataArrayName,int index) {
		Car_bean datatemp;
		for (int i = 0; i < dataArray.size(); i++) 
		{
			datatemp = dataArray.get(i);
			System.out.println("print:" + dataArrayName + "[" + i + "]={"
					+ datatemp.getSpeed() + ", " + datatemp.getAcceleration()  + "}  time= "+datatemp.getReceive_time());
		}		
		Car_bean centertemp = center.get(index);
		
		System.out.println(" center"+index+" = ("+centertemp.getSpeed()+","+centertemp.getAcceleration()+")");
		System.out.println("==========================================================================");
		System.out.println("print:" + dataArrayName+"  "+dataArray.size()+"---------------------------------------------");
		for (int i = 0; i < dataArray.size(); i++) 
		{
			datatemp = dataArray.get(i);
			System.out.println("["+datatemp.getSpeed() + ", " + datatemp.getAcceleration()  + "], ");
		}		
		
		
	}*/
	
	public void printCluster(List<Double> dataArray, int index)
	{
		double datatemp;
		System.out.println("C"+(index)+"(数量"+dataArray.size()+"个)={");
		
		if(dataArray.size() > 0)
		{
			for(int i = 0 ; i<dataArray.size(); i++)
			{
				datatemp = dataArray.get(i);
				System.out.println("   [ "+datatemp + " ]");
			}
		}
		System.out.println("};");
		
		/*byte[] buff=new byte[]{};  
        try   
        {                
            buff=outstr.getBytes();  
            FileOutputStream out=new FileOutputStream("D://out"+index+".js");  
            out.write(buff,0,buff.length);  
              
        }   
        catch (FileNotFoundException e)   
        {  
            e.printStackTrace();  
        }  
        catch (IOException e)   
        {  
            e.printStackTrace();  
        } */
		
		
	}
	
	
	public void printCenter(int index)
	{
		double centertemp = center.get(index);		
		int count = cluster.get(index).size();
		System.out.println(" center"+index+" = ( "+centertemp+" ) "+count+"个");
	}

	/**
	 * Kmeans算法核心过程方法
	 */
	private void kmeans() {
		
		//初始化
		init();		
		
		// printDataArray(dataSet,"initDataSet");
		// printDataArray(center,"initCenter");

		// 循环分组，直到误差不变为止
		while (true) {
			
			clusterSet();
			
			/*System.out.println("***************************************************");
			System.out.println("聚类分配完毕");
			for(int i=0;i<cluster.size();i++)
			{
				printCluster(cluster.get(i),i);
			}		
			System.out.println("***************************************************");*/
			
			countRule();
			//System.out.println("计算误差完毕");
			// 误差不变了，分组完成			
			if (m != 0) {
				/*System.out.println("m = "+m);
				System.out.println(jc.get(m)+"        "+jc.get(m - 1));
				System.out.println("误差 = "+(jc.get(m) - jc.get(m - 1)));*/
				if (Math.abs(jc.get(m) - jc.get(m - 1)) == 0)
				{
					/*System.out.println("jc = " + jc.get(m));
					System.out.println("m = "+m);*/
					break;				
				}
			}
			setNewCenter();
			//System.out.println("设置新的中心点完毕");
			// printDataArray(center,"newCenter");
			m++;
			cluster.clear();
			cluster = initCluster();
		}

		// System.out.println("note:the times of repeat:m="+m);//输出迭代次数
	}

	/**
	 * 执行算法
	 */
	public void execute() {
		long startTime = System.currentTimeMillis();
		System.out.println("kmeans begins");
		kmeans();
		long endTime = System.currentTimeMillis();
		System.out.println("kmeans running time=" + (endTime - startTime) + "ms");
		//System.out.println("kmeans ends");
		//System.out.println();
	}
}
