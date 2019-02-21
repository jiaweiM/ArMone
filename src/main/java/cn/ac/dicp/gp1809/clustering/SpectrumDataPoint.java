/* 
 ******************************************************************************
 * File:SpectrumDataPoint.java * * * Created on 2012-7-17
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;

/**
 * @author ck
 *
 * @version 2012-7-17, 08:43:10
 */
public class SpectrumDataPoint {
	
	private IPeak [] peaks;
	private IPeak [] intenSortPeaks;
	private int length;
	
	public SpectrumDataPoint(IMS2PeakList peaklist){
//		this.peaks = peaklist.getPeakList();
		this.intenSortPeaks = peaklist.getPeaksSortByIntensity();
		this.length = this.intenSortPeaks.length;
	}
	
	public double [] getIntenDistribution(int count){
		
		double baseInten = intenSortPeaks[0].getIntensity();
		int [] dis = new int [count];
		for(int i=1;i<length;i++){
			int id = (int) (intenSortPeaks[i].getIntensity()*count/baseInten);
			if(id>=count){
				dis[count-1]++;
			}else{
				dis[id]++;
			}
		}
		
		double [] dd = new double [count];
		for(int i=0;i<dd.length;i++){
			dd[i] = (double) dis[i]/(double) length;
		}
		
		return dd;
	}

	public double [] getIntenDistribution(){
		
		int peakid = intenSortPeaks.length>5 ? 5 : intenSortPeaks.length-1;
		double baseInten = intenSortPeaks[peakid].getIntensity();
		int [] dis = new int [5];
		for(int i=0;i<length;i++){
			int id = (int) (intenSortPeaks[i].getIntensity()*10.0/baseInten);
			switch (id) {
			case 0: 
				break;
			case 1: 
				dis[0]++;
				break;
			case 2: 
				dis[1]++;
				break;
			case 3: 
				dis[2]++;
				break;
			case 4: 
				dis[3]++;
				break;
			default:
				dis[4]++;
				break;
			}
		}
		
		double [] dd = new double [5];
		for(int i=0;i<dd.length;i++){
			dd[i] = (double) dis[i]/(double) length;
		}
		
		return dd;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}

}
