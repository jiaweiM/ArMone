/* 
 ******************************************************************************
 * File: FreeFeatures.java * * * Created on 2012-10-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree;

import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2012-10-16, 9:09:32
 */
public class FreeFeatures {

	protected IPeptide peptide;
	private HashMap <Integer, FreeFeature> feaMap;
	private double pepmass;
	private double [] intenlist;
	private double intensity;
	
	public FreeFeatures() {
		// TODO Auto-generated constructor stub
		this.feaMap = new HashMap <Integer, FreeFeature>();
	}
	
	public FreeFeatures(FreeFeatures feas) {
		// TODO Auto-generated constructor stub
		this.feaMap = feas.feaMap;
		this.intenlist = feas.intenlist;
		this.intensity = feas.intensity;
	}
	
	public FreeFeatures(HashMap <Integer, FreeFeature> feaMap) {
		// TODO Auto-generated constructor stub
		this.feaMap = feaMap;
		this.setInfo();
	}
	
	/**
	 * @param f
	 */
	public FreeFeatures(FreeFeature fea) {
		// TODO Auto-generated constructor stub
		this.feaMap = new HashMap <Integer, FreeFeature>();
		this.feaMap.put(fea.getScanNum(), fea);
	}

	/**
	 * @param f
	 */
	public void addFeature(FreeFeature fea) {
		// TODO Auto-generated method stub
		feaMap.put(fea.getScanNum(), fea);
	}

	/**
	 * 
	 */
	public void setInfo() {
		// TODO Auto-generated method stub
		this.intenlist = new double[feaMap.size()];
		Iterator <Integer> it = feaMap.keySet().iterator();
		int id = 0;
		while(it.hasNext()){
			Integer sn = it.next();
			FreeFeature ff = feaMap.get(sn);
			this.intenlist[id++] = ff.getIntensity();
			this.intensity += ff.getIntensity();
		}
	}

	/**
	 * @return
	 */
	public HashMap<Integer, FreeFeature> getFeaMap() {
		// TODO Auto-generated method stub
		return feaMap;
	}

	/**
	 * @return
	 */
	public double getPepMass() {
		// TODO Auto-generated method stub
		return pepmass;
	}
	
	/**
	 * @param mr
	 */
	public void setPepMass(double mr) {
		// TODO Auto-generated method stub
		this.pepmass = mr;
	}

	/**
	 * @return
	 */
	public double[] getIntenList() {
		// TODO Auto-generated method stub
		return intenlist;
	}
	
	public int getLength(){
		return this.feaMap.size();
	}
	
	public double getInten(){
		return intensity;
	}
	
	public void setPeptide(IPeptide peptide){
		this.peptide = peptide;
	}

	public IPeptide getPeptide(){
		return peptide;
	}
	
	public String getSequence(){
		return peptide.getSequence();
	}
	
	/**
	 * @param length
	 * @return
	 */
	public double getTopNInten(int length) {
		// TODO Auto-generated method stub
		return MathTool.getTopnTotal(intenlist, length);
	}

	/**
	 * @param normal
	 */
	public void setNormalRatio(double[] normal) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
}
