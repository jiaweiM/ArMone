/* 
 ******************************************************************************
 * File: GlycoQuanResult.java * * * Created on 2013-8-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-8-25, 12:49:30
 */
public class GlycoQuanResult{
	
	private IGlycoPeptide peptide;
	private NGlycoSSM ssm;
	private HashSet<Integer> idset;
	private ArrayList<double[]> allRatioList;
	private ArrayList<double[]> allRIAList;
	private ArrayList<double[]> allIntensityList;
	private ArrayList<Boolean> accurateList;
	private boolean accurate = true;
	
	private double[] ratio;
	private double[] ria;
	private double[] intensity;
	private int peptideId;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private DecimalFormat dfE4 = DecimalFormats.DF_E4;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_2;
	
	public GlycoQuanResult(IGlycoPeptide peptide, NGlycoSSM ssm){
		this.peptide = peptide;
		this.ssm = ssm;
		this.allRatioList = new ArrayList<double[]>();
		this.allRIAList = new ArrayList<double[]>();
		this.allIntensityList = new ArrayList<double[]>();
		this.accurateList = new ArrayList<Boolean>();
		this.idset = new HashSet<Integer>();
	}

	public void initial(){

		double [] ratio0 = allRatioList.get(0);
		double [] intensity0 = allIntensityList.get(0);
		double [] ria0 = allRIAList.get(0);
		this.accurate = false;
		
		if(allRatioList.size()==1){
			this.ratio = ratio0;
			this.intensity = intensity0;
			this.ria = ria0;
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
	
	public double [] getIntensity(){
		return intensity;
	}
	
	public void addSSMIds(ArrayList<Integer> ids){
		this.idset.addAll(ids);
	}

	/**
	 * @return the peptide
	 */
	public IGlycoPeptide getPeptide() {
		return peptide;
	}

	/**
	 * @param peptide the peptide to set
	 */
	public void setPeptide(IGlycoPeptide peptide) {
		this.peptide = peptide;
	}

	/**
	 * @return the peptideId
	 */
	public int getPeptideId() {
		return peptideId;
	}

	/**
	 * @param peptideId the peptideId to set
	 */
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}

	/**
	 * @return the ssm
	 */
	public NGlycoSSM getSsm() {
		return ssm;
	}

	/**
	 * @param ssm the ssm to set
	 */
	public void setSsm(NGlycoSSM ssm) {
		this.ssm = ssm;
	}

	public Integer[] getSSMIds(){
		Integer [] ids = this.idset.toArray(new Integer[this.idset.size()]);
		Arrays.sort(ids);
		return ids;
	}
	
	public void addRatioInfo(double[] ratio, double[] ria, double[] intensity, boolean accurate){
		this.allRatioList.add(ratio);
		this.allRIAList.add(ria);
		this.allIntensityList.add(intensity);
		this.accurateList.add(accurate);
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

	public String getSiteInfo(SimpleProInfo info, SeqLocAround sla){
		
		StringBuilder sb = new StringBuilder();
		sb.append(info.getRef()).append("\t");
		
		GlycoSite[] sites = this.peptide.getAllGlycoSites();
		for(int k=0;k<sites.length;k++){
			
			int loc = sites[k].modifLocation();
			int beg = sla.getBeg();
			int proloc = loc+beg-1;
			
			sb.append(proloc).append("\t");
		}
		return sb.toString();
	}

	public String getSiteInfo(){
		
		StringBuilder sb = new StringBuilder();
		HashMap <String, SimpleProInfo> proInfoMap = this.peptide.getProInfoMap();
		HashMap <String, SeqLocAround> slaMap = this.getPeptide().getPepLocAroundMap();
		
		Iterator <String> it = proInfoMap.keySet().iterator();
		GlycoSite[] sites = this.peptide.getAllGlycoSites();
		
		while(it.hasNext()){
			
			String key = it.next();
			SimpleProInfo info = proInfoMap.get(key);
			SeqLocAround sla = slaMap.get(key);
			
			sb.append(info.getRef()).append("\t");
			
			for(int k=0;k<sites.length;k++){
				
				int loc = sites[k].modifLocation();
				int beg = sla.getBeg();
				int proloc = loc+beg-1;
				
				sb.append(proloc).append("\t");
			}
		}

		return sb.toString();
	}

	/**
	 * @return
	 */
	public String getPepInfo(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getSequence()).append("\t");
		sb.append(ssm.getPepMass()).append("\t");
		sb.append(ssm.getGlycoMass()).append("\t");

		/*double [] glycoPercent = pep.getGlycoPercents();
		for(int i=0;i<glycoPercent.length;i++){
			sb.append(dfPer.format(glycoPercent[i])).append("\t");
		}*/

		double [] ratios = this.getRatio();
		for(int i=0;i<ratios.length;i++){
			sb.append(df4.format(ratios[i])).append("\t");
		}
		
		double [] intens = this.getIntensity();
		for(int i=0;i<intens.length;i++){
			sb.append(dfE4.format(intens[i])).append("\t");
		}
		
		return sb.toString();
		
	}
	
	public String getGlycanInfo(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(ssm.getName()).append("\t");
		sb.append(ssm.getScore()).append("\t");
		sb.append(ssm.getRank()).append("\t");
		
		return sb.toString();
	}
	
	public String getPepInfo(double [] proRatio, NGlycoSSM [] ssms){
		
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getSequence()).append("\t");
		sb.append(ssm.getPepMass()).append("\t");
		sb.append(ssm.getGlycoMass()).append("\t");
		
		for(Integer id : this.idset){
			int scannum = ssms[id].getScanNum();
			sb.append(scannum).append(", ");
		}

		sb.delete(sb.length()-2, sb.length()-1);
		sb.append("\t");
		
		/*double [] glycoPercent = peptide.getGlycoPercents();
		for(int i=0;i<glycoPercent.length;i++){
			sb.append(dfPer.format(glycoPercent[i])).append("\t");
		}*/

		double [] ratios = this.getRatio();
		for(int i=0;i<ratios.length;i++){
			sb.append(df4.format(ratios[i])).append("\t");
		}
		
		for(int i=0;i<ratios.length;i++){
			
			double rela = 0;
			if(proRatio[i]!=0){
				rela = ratios[i]/proRatio[i];
			}
			sb.append(df4.format(rela)).append("\t");
		}
		
		for(int i=0;i<proRatio.length;i++){
			sb.append(df4.format(proRatio[i])).append("\t");
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
