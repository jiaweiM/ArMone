package cn.ac.dicp.gp1809.clustering.kMean;

import java.util.ArrayList;


public class Cluster {

	private Centroid centroid;
	private double sumOfDis;
	private ArrayList <DataPoint> list;
	
	public Cluster(){
		this.list = new ArrayList <DataPoint>();
	}
	
	public Centroid getCentroid(){
		return centroid;
	}
	
	public void setCentroid(Centroid centroid){
		this.centroid = centroid;
	}
	
	public void addDataPoint(DataPoint dataPoint){
		dataPoint.setCluster(this);
		this.list.add(dataPoint);
		this.calcSumOfDis();
	}
	
	public void removeDataPoint(DataPoint dataPoint){
		this.list.remove(dataPoint);
		this.calcSumOfDis();
	}
	
	public DataPoint getDataPoint(int i){
		return this.list.get(i);
	}
	
	public int getNumOfDP(){
		return list.size();
	}
	
	public DataPoint getMeanDataPoint(){
		
		DataPoint dp = null;
		
		if(list.size()>0){
			dp = list.get(0);
			for(int i=1;i<list.size();i++){
				dp = dp.plus(list.get(i));
			}
			dp = dp.divide(list.size());
		}
		return dp;
	}
	
	public void calcSumOfDis(){
		
		double dis = 0;
		for(int i=0;i<list.size();i++){
			dis += list.get(i).getDistance();
		}
		this.sumOfDis = dis;
	}
	
	public double getSumOfDis(){
		return sumOfDis;
	}
	
	public ArrayList <DataPoint> getDataList(){
		return list;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
