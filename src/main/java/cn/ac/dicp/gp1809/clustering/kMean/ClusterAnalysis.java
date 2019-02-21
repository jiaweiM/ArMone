/* 
 ******************************************************************************
 * File:ClusterAnalysis.java * * * Created on 2012-7-24
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering.kMean;

import java.util.ArrayList;


/**
 * @author ck
 *
 * @version 2012-7-24, 19:25:50
 */
public class ClusterAnalysis {
	
	private Cluster[] clusters;
	private ArrayList <DataPoint> list;
	private double distance;
	private int iterCount = 100;
	
	public ClusterAnalysis(ArrayList <DataPoint> list){
		this.list = list;
	}
	
	public ClusterAnalysis(ArrayList <DataPoint> list, DataPoint [] dataPoints){
		this.list = list;
		this.clusters = new Cluster[dataPoints.length];
		for(int i=0;i<clusters.length;i++){
			clusters[i] = new Cluster();
			Centroid centroid = new Centroid(dataPoints[i]);
			clusters[i].setCentroid(centroid);
			centroid.setCluster(clusters[i]);
		}
	}
	
	public void calDistance(){
		double dis = 0;
		for(int i=0;i<clusters.length;i++){
			dis += clusters[i].getSumOfDis();
		}
		this.distance = dis;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public void analysis(){
		
		int idx = 0;
		for(int i=0;i<list.size();i++){
			clusters[idx].addDataPoint(list.get(i));
			idx++;
			if(idx==clusters.length)
				idx = 0;
		}
		this.calDistance();
		
		for(int i=0;i<clusters.length;i++){
			clusters[i].getCentroid().calcCentroid();
		}
		this.calDistance();
		
		int count = 0;
		while(true){
			
			if(count>this.iterCount)
				break;
			
			for(int i=0;i<clusters.length;i++){
				for(int j=0;j<clusters[i].getNumOfDP();j++){
					
					DataPoint dij = clusters[i].getDataPoint(j);
					double tempDistance = dij.getDistance();
					int matchId = -1;
					
					for(int k=0;k<clusters.length;k++){
						if(i!=k){
							double dijDis = dij.calcDistance(clusters[k].getCentroid());
							if(tempDistance>dijDis){
								tempDistance = dijDis;
								matchId = k;
							}
						}
					}
					
					if(matchId>-1){
						clusters[matchId].addDataPoint(dij);
						clusters[i].removeDataPoint(dij);
						for(int m=0;m<clusters.length;m++){
							if(m==i || m==matchId)
								clusters[m].getCentroid().calcCentroid();
						}
						this.calDistance();
					}
				}
			}
			count++;
		}
	}
	
	public Cluster [] getClusters(){
		return this.clusters;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
