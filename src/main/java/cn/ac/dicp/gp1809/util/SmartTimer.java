/*
 * *****************************************************************************
 * File: SmartTimer.java * * * Created on 09-24-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.util;

/**
 * A simple used Timer
 * 
 * @author Xinning
 * @version 0.1.1, 09-24-2008, 09:32:19
 */
public class SmartTimer {
	
	private long start = 0l;
	
	public SmartTimer(){
		start = System.currentTimeMillis();
	}
	
	/**
	 * Reset the timer;
	 */
	public void reset(){
		this.start = System.currentTimeMillis();
	}
	
	/**
	 * @return hour time (e.g. 0.02);
	 */
	public double getHourTime(){
		long ms = this.getMSecondTime();
		double hor = ms/3600000d;
		return hor;
	}
	
	/**
	 * @return minute time;
	 */
	public double getMinTime(){
		long ms = this.getMSecondTime();
		double min = ms/(double)60000;
		return min;
	}
	
	/**
	 * @return micro second time;
	 */
	public long getMSecondTime(){
		long end = System.currentTimeMillis();
		return end-start;
	}
	
	/**
	 * @return string formated as follow: -h-m-s;
	 */
	@Override
	public String toString(){
		long ms = this.getMSecondTime();
		int t = (int)(ms/1000);
		int h = t/3600;
		t = t%3600;
		int m = t/60;
		int s = t%60;
		
		StringBuilder sb = new StringBuilder();
		sb.append(h);
		sb.append("h ");
		sb.append(m);
		sb.append("m ");
		sb.append(s);
		sb.append('s');
		
		return sb.toString();
	}
}
