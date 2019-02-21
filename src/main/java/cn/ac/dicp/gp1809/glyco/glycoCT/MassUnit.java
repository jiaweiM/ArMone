/* 
 ******************************************************************************
 * File: MassUnit.java * * * Created on 2012-4-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.glycoCT;

import java.io.Serializable;

/**
 * @author ck
 *
 * @version 2012-4-18, 13:59:26
 */
public class MassUnit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private double mono;
	private double average;
	private double [][] fragments;
	private double [][] rev_fragments;
	private int [] composition;
	
	private int matchPeakId;
	
	public MassUnit(double mono, double average, double [][] fragments){
		
		this.mono = mono;
		this.average = average;
		this.fragments = fragments;
	}
	
	public MassUnit(int id, double mono, double average, double [][] fragments){
		
		this.id = id;
		this.mono = mono;
		this.average = average;
		this.fragments = fragments;
	}
	
	public MassUnit(int id, double mono, double average, double [][] fragments, double [][] rev_fragments){
		
		this.id = id;
		this.mono = mono;
		this.average = average;
		this.fragments = fragments;
		this.rev_fragments = rev_fragments;
	}

	public int getId(){
		return id;
	}
	
	public double getMono(){
		return mono;
	}
	
	public double getAverage(){
		return average;
	}
	
	public int [] getComposition(){
		return composition;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public double [][] getFragments(){
		return fragments;
	}
	
	public double [][] getRevFragments(){
		return rev_fragments;
	}

	public int getMatchPeakId(){
		return matchPeakId;
	}
	
	public void setMatchPeakId(int matchPeakId){
		this.matchPeakId = matchPeakId;
	}
}
