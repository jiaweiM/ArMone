/* 
 ******************************************************************************
 * File: TMHProtein.java * * * Created on 2013-7-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.TMHMM;

import java.util.ArrayList;

/**
 * @author ck
 *
 * @version 2013-7-28, 13:32:31
 */
public class TMHProtein {

	private String ipi;
	private int length;
	/**
	 * -1: outside
	 * 0: inside
	 */
	private int numOfTHMs;
	private double expAAs;
	private double first60AAs;
	private double probOfN_in;
	
	private ArrayList <int []> inside;
	private ArrayList <int []> outside;
	private ArrayList <int []> TMhelix;
	
	public TMHProtein(String ipi){
		this.ipi = ipi;
		this.inside = new ArrayList <int []>();
		this.outside = new ArrayList <int []>();
		this.TMhelix = new ArrayList <int []>();
	}
	
	
	
	/**
	 * @return the ipi
	 */
	public String getIpi() {
		return ipi;
	}



	/**
	 * @param ipi the ipi to set
	 */
	public void setIpi(String ipi) {
		this.ipi = ipi;
	}



	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}



	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}



	/**
	 * @return the numOfTHMs
	 */
	public int getNumOfTHMs() {
		return numOfTHMs;
	}



	/**
	 * @param numOfTHMs the numOfTHMs to set
	 */
	public void setNumOfTHMs(int numOfTHMs) {
		this.numOfTHMs = numOfTHMs;
	}



	/**
	 * @return the expAAs
	 */
	public double getExpAAs() {
		return expAAs;
	}



	/**
	 * @param expAAs the expAAs to set
	 */
	public void setExpAAs(double expAAs) {
		this.expAAs = expAAs;
	}



	/**
	 * @return the first60AAs
	 */
	public double getFirst60AAs() {
		return first60AAs;
	}



	/**
	 * @param first60aAs the first60AAs to set
	 */
	public void setFirst60AAs(double first60aAs) {
		first60AAs = first60aAs;
	}



	/**
	 * @return the probOfN_in
	 */
	public double getProbOfN_in() {
		return probOfN_in;
	}



	/**
	 * @param probOfN_in the probOfN_in to set
	 */
	public void setProbOfN_in(double probOfN_in) {
		this.probOfN_in = probOfN_in;
	}



	/**
	 * @return the inside
	 */
	public ArrayList<int[]> getInside() {
		return inside;
	}



	/**
	 * @param inside the inside to set
	 */
	public void setInside(ArrayList<int[]> inside) {
		this.inside = inside;
	}



	/**
	 * @return the outside
	 */
	public ArrayList<int[]> getOutside() {
		return outside;
	}



	/**
	 * @param outsite the outside to set
	 */
	public void setOutside(ArrayList<int[]> outside) {
		this.outside = outside;
	}



	/**
	 * @return the tMhelix
	 */
	public ArrayList<int[]> getTMhelix() {
		return TMhelix;
	}



	/**
	 * @param tMhelix the tMhelix to set
	 */
	public void setTMhelix(ArrayList<int[]> tMhelix) {
		TMhelix = tMhelix;
	}

	public void addInsite(int [] aaloc){
		this.inside.add(aaloc);
	}
	
	public void addTMhelix(int [] aaloc){
		this.TMhelix.add(aaloc);
	}
	
	public void addOutside(int [] aaloc){
		this.outside.add(aaloc);
	}

	public int judge(int loc){
		
		for(int i=0;i<outside.size();i++){
			int [] st = outside.get(i);
			if(loc>=st[0] && loc<=st[1]){
				return 2;
			}
		}
		
		for(int i=0;i<inside.size();i++){
			int [] st = inside.get(i);
			if(loc>=st[0] && loc<=st[1]){
				return 0;
			}
		}
		
		for(int i=0;i<TMhelix.size();i++){
			int [] st = TMhelix.get(i);
			if(loc>=st[0] && loc<=st[1]){
				return 1;
			}
		}
		
		return -1;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
