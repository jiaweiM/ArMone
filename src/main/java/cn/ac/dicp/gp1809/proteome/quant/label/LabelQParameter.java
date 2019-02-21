/* 
 ******************************************************************************
 * File: LabelQParameter.java * * * Created on 2011-1-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label;

/**
 * @author ck
 *
 * @version 2011-1-13, 10:25:08
 */
public class LabelQParameter {

	private float mzTolerance;
	private int missNum;
	private int leastIdenNum;
	
	public LabelQParameter(float mzTolerance, int missNum, int leastIdenNum){
		this.mzTolerance = mzTolerance;
		this.missNum = missNum;
		this.leastIdenNum = leastIdenNum;
	}
	
	public static LabelQParameter default_parameter(){
		return new LabelQParameter(20f, 4, 1);
	}
	
	public float getMzTole(){
		return this.mzTolerance;
	}
	
	public int getMissNum(){
		return this.missNum;
	}
	
	public int getLeastINum(){
		return this.leastIdenNum;
	}
	
}
