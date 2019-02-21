/* 
 ******************************************************************************
 * File: MascotValueLimit.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

/**
 * @author ck
 *
 * @version 2011-8-31, 13:37:51
 */
public class MascotValueLimit {

	private double iScoreUplim=50, iScoreLowlim=0.00;
	private double deltaISUplim=0.8, deltaISLowlim=0.00;
	private double mhtUplim=20, mthLowlim=-50;
	private double mitUplim=20, mitLowlim=-50;
	private double eValueUplim=1, eValueLowlim=0.0;
	private double ionpercentlowlim=0.01, ionpercentuplim=1;

	public double getIonScoreUpperlimit(){
		return this.iScoreUplim;
	}
	
	public double getIonScoreLowerlimit(){
		return this.iScoreLowlim;
	}
	
	public void setIonScoreUpperlimit(double iScoreUplim){
		this.iScoreUplim = iScoreUplim;
	}
	
	public void setIonScoreLowlimit(double iScoreLowlim){
		this.iScoreLowlim = iScoreLowlim;
	}
	
	public double getDeltaISUpperlimit(){
		return this.deltaISUplim;
	}
	
	public double getDeltaISLowerlimit(){
		return this.deltaISLowlim;
	}
	
	public void setDeltaISUpperlimit(double deltaISUplim){
		this.deltaISUplim = deltaISUplim;
	}
	
	public void setDeltaISLowlimit(double deltaISLowlim){
		this.deltaISLowlim = deltaISLowlim;
	}
	
	public double getMHTUpperlimit(){
		return this.mhtUplim;
	}
	
	public double getMHTLowerlimit(){
		return this.mthLowlim;
	}
	
	public void setMHTUpperlimit(double mhtUplim){
		this.mhtUplim = mhtUplim;
	}
	
	public void setMHTLowlimit(double mthLowlim){
		this.mthLowlim = mthLowlim;
	}
	
	public double getMITUpperlimit(){
		return this.mitUplim;
	}
	
	public double getMITLowerlimit(){
		return this.mitLowlim;
	}
	
	public void setMITUpperlimit(double mitUplim){
		this.mitUplim = mitUplim;
	}
	
	public void setMITLowlimit(double mitLowlim){
		this.mitLowlim = mitLowlim;
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
	
	public double getEValueUpperlimit(){
		return this.eValueUplim;
	}
	
	public double getEValueLowerlimit(){
		return this.eValueLowlim;
	}
	
	public void setEValueUplimit(double eValueUplimit){
		this.eValueUplim = eValueUplimit;
	}

	public void setEValueLowlimit(double eValueLowlimit){
		this.eValueLowlim = eValueLowlimit;
	}
}
