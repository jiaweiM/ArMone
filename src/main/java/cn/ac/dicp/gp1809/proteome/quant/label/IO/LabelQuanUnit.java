/* 
 ******************************************************************************
 * File: LabelQuanUnit.java * * * Created on 2013-8-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * 
 * PeptidePair with different charges
 * 
 * @author ck
 * @version 2013-8-26, 9:08:41
 */
public class LabelQuanUnit {
	
	private IPeptide peptide;
	private short modCount;
	private String delegateRef;
	protected String refs;
	protected String src;

	private int ratioNum;
	private int pairNum;
	
	private ArrayList<double[]> allRatioList;
	private ArrayList<double[]> allRIAList;
	private ArrayList<double[]> allIntensityList;
	private ArrayList<Boolean> accurateList;
	private boolean accurate = true;
	
	private double[] ratio;
	private double[] ria;
	private double[] intensity;
	
	protected DecimalFormat dfE4 = DecimalFormats.DF_E4;
	
	public LabelQuanUnit(IPeptide peptide){
		this.peptide = peptide;
		this.allRatioList = new ArrayList<double[]>();
		this.allRIAList = new ArrayList<double[]>();
		this.allIntensityList = new ArrayList<double[]>();
		this.accurateList = new ArrayList<Boolean>();
	}
	
	public void initial(){

		double [] ratio0 = allRatioList.get(0);
		double [] intensity0 = allIntensityList.get(0);
		double [] ria0 = allRIAList.get(0);
		this.accurate = false;
		
		if(allRatioList.size()==1){
			this.ratio = ratio0;
			this.ria = ria0;
			this.intensity = intensity0;
			this.accurate = accurateList.get(0);
		}else{
			double [] ratio = new double[ratio0.length];
			double [] ratio2 = new double[ratio0.length];
			double [] ria = new double[ria0.length];
			double [] ria2 = new double[ria0.length];
			double [] intensity = new double[intensity0.length];
			double [] intensity2 = new double[intensity0.length];
			double totalIntensity = 0;
			double totalIntensity2 = 0;
			for(int i=0;i<allRatioList.size();i++){
				double intensityi = MathTool.getTotal(this.allIntensityList.get(i));
				if(accurateList.get(i)){
					totalIntensity += intensityi;
					for(int j=0;j<ratio.length;j++){
						ratio[j] += allRatioList.get(i)[j]*intensityi;
						ria[j] += allRIAList.get(i)[j]*intensityi;
					}
					for(int j=0;j<intensity.length;j++){
						intensity[j] += allIntensityList.get(i)[j];
					}
					this.accurate = true;
				}else{
					totalIntensity2 += intensityi;
					for(int j=0;j<ratio2.length;j++){
						ratio2[j] += allRatioList.get(i)[j]*intensityi;
						ria2[j] += allRIAList.get(i)[j]*intensityi;
					}
					for(int j=0;j<intensity2.length;j++){
						intensity2[j] += allIntensityList.get(i)[j];
					}
				}
			}
			for(int i=0;i<ratio.length;i++){
				ratio[i] = totalIntensity==0 ? 0.0 : ratio[i]/totalIntensity;
				ratio2[i] = totalIntensity2==0 ? 0.0 : ratio2[i]/totalIntensity2;
				ria[i] = totalIntensity==0 ? 0.0 : ria[i]/totalIntensity;
				ria2[i] = totalIntensity2==0 ? 0.0 : ria2[i]/totalIntensity2;
			}
			
			if(this.accurate){
				this.ratio = ratio;
				this.ria = ria;
				this.intensity = intensity;
			}else{
				this.ratio = ratio2;
				this.ria = ria2;
				this.intensity = intensity2;
			}
		}
	}
	
	public double [] getRatio(){
		return ratio;
	}
	
	public double [] getRIA(){
		return ria;
	}
	
	public double[] getRiasSixplex() {
		double [] rias = new double [3];
		rias[0] = this.ria[0];
		rias[1] = this.ria[9];
		rias[2] = this.ria[14];
		return rias;
	}
	
	public void addRatio(double[] ratio){
		this.allRatioList.add(ratio);
	}
	
	public void addIntensity(double[] intensity){
		this.allIntensityList.add(intensity);
	}
	
	public double [] getIntensity(){
		return intensity;
	}
	
	public void addRatioInfo(double[] ratio, double[] ria, double[] intensity, boolean accurate){
		this.allRatioList.add(ratio);
		this.allRIAList.add(ria);
		this.allIntensityList.add(intensity);
		this.accurateList.add(accurate);
	}
	
	public IPeptide getPeptide(){
		return peptide;
	}

	/**
	 * @return the accurate
	 */
	public boolean isAccurate() {
		return accurate;
	}

	/**
	 * @param accurate the accurate to set
	 */
	public void setAccurate(boolean accurate) {
		this.accurate = accurate;
	}
	
	/**
	 * @return
	 */
	public boolean hasVariMod() {
		// TODO Auto-generated method stub
		char [] chars = PeptideUtil.getSequence(this.getSequence()).toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]<'A' || chars[i]>'Z'){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @return
	 */
	public String getSequence() {
		// TODO Auto-generated method stub
		return this.peptide.getSequence();
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	
	/**
	 * @return
	 */
	public String getSrc() {
		// TODO Auto-generated method stub
		return src;
	}

	/**
	 * @param modCount
	 */
	public void setModCount(short modCount) {
		// TODO Auto-generated method stub
		this.modCount = modCount;
	}

	/**
	 * @return
	 */
	public short getModCount() {
		// TODO Auto-generated method stub
		return modCount;
	}

	/**
	 * @return
	 */
	public String getRefs() {
		// TODO Auto-generated method stub
		return refs;
	}

	/**
	 * @param delegateRef
	 */
	public void setDelegateRef(String delegateRef) {
		// TODO Auto-generated method stub
		this.delegateRef = delegateRef;
	}

	/**
	 * @return
	 */
	public String getDelegateRef() {
		// TODO Auto-generated method stub
		return delegateRef;
	}
	
	/**
	 * @return the ratioNum
	 */
	public int getRatioNum() {
		return ratioNum;
	}

	/**
	 * @param ratioNum the ratioNum to set
	 */
	public void setRatioNum(int ratioNum) {
		this.ratioNum = ratioNum;
	}

	/**
	 * @return the pairNum
	 */
	public int getPairNum() {
		return pairNum;
	}

	/**
	 * @param pairNum the pairNum to set
	 */
	public void setPairNum(int pairNum) {
		this.pairNum = pairNum;
	}

	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();

		sb.append(this.getSequence()).append("\t");
		
		for(int i=0;i<ratio.length;i++){
			sb.append(ratio[i]).append("\t");
		}
			
		for(int j=0;j<intensity.length;j++){
			sb.append(dfE4.format(intensity[j])).append("\t");	
		}

//		sb.append(features.getPresentFeasNum()).append("\t");
		String deleRef = this.getDelegateRef();
		if(deleRef==null){
			sb.append(this.getRefs()).append("\t").append(src);
//			sb.append(this.getRefs()).append("\t");
		}else{
			sb.append(deleRef).append("\t").append(src);
//			sb.append(deleRef).append("\t");
		}
		
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
