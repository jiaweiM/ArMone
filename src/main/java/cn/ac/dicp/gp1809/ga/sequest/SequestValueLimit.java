/* 
 ******************************************************************************
 * File: SequestValueLimit.java * * * Created on 02-11-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

/**
 * This class is used to determine the true value of random genes;
 * When random genes are calculated for value.
 * 
 * @author Xinning
 * @version 0.1.1, 08-04-2010, 10:54:42
 */
public class SequestValueLimit {
	
	private double dcnuplim=0.8,dcnlowlim=0.01;
	private double xcorruplim=8,xcorrlowlim=0.3;
	private double spuplim=1000,splowlim=10;
	private double rspuplim=250,rsplowlim=1;
	private double ionpercentlowlim=0.01,ionpercentuplim=1;
	private double deltamsuplim = 3000, deltamslowlim = 0;
	
	public double getDeltaCnUpperlimit(){
		return this.dcnuplim;
	}
	
	public double getDeltaCnLowerlimit(){
		return this.dcnlowlim;
	}
	
	public void setDeltaCnUpperlimit(double dcnuplim){
		this.dcnuplim = dcnuplim;
	}
	
	public void setDeltaCnLowlimit(double dcnlowlim){
		this.dcnlowlim = dcnlowlim;
	}
	
	public double getXcorrUpperlimit(){
		return this.xcorruplim;
	}
	
	public double getXcorrLowerlimit(){
		return this.xcorrlowlim;
	}
	
	public void setXcorrUpperlimit(double xcorruplim){
		this.xcorruplim = xcorruplim;
	}
	
	public void setXcorrLowlimit(double xcorrlowlim){
		this.xcorrlowlim = xcorrlowlim;
	}
	
	public double getSpUpperlimit(){
		return this.spuplim;
	}
	
	public double getSpLowerlimit(){
		return this.splowlim;
	}
	
	public void setSpUpperlimit(double spuplim){
		this.spuplim = spuplim;
	}
	
	public void setSpLowlimit(double splowlim){
		this.splowlim = splowlim;
	}
	
	public double getRspUpperlimit(){
		return this.rspuplim;
	}
	
	public double getRspLowerlimit(){
		return this.rsplowlim;
	}
	
	public void setRspUpperlimit(double rspuplim){
		this.rspuplim = rspuplim;
	}
	
	public void setRspLowlimit(double rsplowlim){
		this.rsplowlim = rsplowlim;
	}
	
	public double getIonPercentUpperlimit(){
		return this.ionpercentuplim;
	}
	
	public double getIonPercentLowlimit(){
		return this.ionpercentlowlim;
	}
	
	public void setIonPercentUpperlimit(double ionpercentuplim){
		this.ionpercentuplim = ionpercentuplim;
	}
	
	public void setIonPercentLowlimit(double ionpercentlowlim){
		this.ionpercentlowlim = ionpercentlowlim;
	}
	
	public double getDeltaMSUpperlimit(){
		return this.deltamsuplim;
	}
	
	public double getDeltaMSLowlimit(){
		return this.deltamslowlim;
	}
	
	public void setDeltaMSUpperlimit(double deltaMSuplim){
		this.deltamsuplim = deltaMSuplim;
	}
	
	public void setDeltaMSLowlimit(double deltaMSlowlim){
		this.deltamslowlim = deltaMSlowlim;
	}
}
