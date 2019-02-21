/* 
 ******************************************************************************
 * File:TwoDArraysDataPoint.java * * * Created on 2012-7-24
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering.kMean;

import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2012-7-24, 18:32:28
 */
public class TwoDArraysDataPoint extends DataPoint{
	
	private double [][] data;

	public TwoDArraysDataPoint(double [][] data){
		super(-1);
		this.data = data;
	}

	public TwoDArraysDataPoint(double [][] data, int id){
		super(id);
		this.data = data;
	}
	
	public void calcDistance() {
		// TODO Auto-generated method stub
		DataPoint centroidDp = this.cluster.getCentroid().getDataPoint();
		this.distance = MathTool.getCorr2(data, ((TwoDArraysDataPoint)centroidDp).getData());
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.clustering.DataPoint#calcDistance(cn.ac.dicp.gp1809.clustering.Centroid)
	 */
	@Override
	public double calcDistance(Centroid centroid) {
		
		// TODO Auto-generated method stub
		DataPoint centroidDp = centroid.getDataPoint();
		return MathTool.getCorr2(data, ((TwoDArraysDataPoint)centroidDp).getData());
	}
	
	public DataPoint plus(DataPoint dp){
		
		DataPoint ndp = null;
		
		if(dp instanceof TwoDArraysDataPoint){
			
			TwoDArraysDataPoint tdp = (TwoDArraysDataPoint) dp;
			double [][] nd = new double[data.length][];
			for(int i=0;i<data.length;i++){
				nd[i] = new double[data[i].length];
				for(int j=0;j<data[i].length;j++){
					nd[i][j] = this.data[i][j] + tdp.getData()[i][j];
				}
			}
			
			ndp = new TwoDArraysDataPoint(nd);
		}

		return ndp;
	}

	public double [][] getData(){
		return data;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.clustering.DataPoint#divide(int)
	 */
	@Override
	public DataPoint divide(int n) {
		// TODO Auto-generated method stub
		DataPoint ndp = null;
		if(n!=0){
			double [][] nd = new double[data.length][];
			for(int i=0;i<data.length;i++){
				nd[i] = new double[data[i].length];
				for(int j=0;j<data[i].length;j++){
					nd[i][j] = this.data[i][j]/(double)n;
				}
			}
			ndp = new TwoDArraysDataPoint(nd);
		}
		return ndp;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.clustering.DataPoint#multiply(int)
	 */
	@Override
	public DataPoint multiply(int n) {
		// TODO Auto-generated method stub
		DataPoint ndp = null;
		if(n!=0){
			double [][] nd = new double[data.length][];
			for(int i=0;i<data.length;i++){
				nd[i] = new double[data[i].length];
				for(int j=0;j<data[i].length;j++){
					nd[i][j] = this.data[i][j]*(double)n;
				}
			}
			ndp = new TwoDArraysDataPoint(nd);
		}
		return ndp;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[i].length;j++){
				sb.append(data[i][j]).append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	
}
