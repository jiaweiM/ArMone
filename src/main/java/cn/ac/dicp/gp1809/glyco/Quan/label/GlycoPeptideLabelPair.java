/* 
 ******************************************************************************
 * File: GlycoPepLabelFeatures.java * * * Created on 2011-3-23
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
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2011-3-23, 10:12:36
 */
public class GlycoPeptideLabelPair extends PeptidePair
{
	
	private IGlycoPeptide peptide;
	private NGlycoSSM ssm;
	private int peptideId;
	private ArrayList<Integer> ssmsIds;
	private int deleSSMId;

	private double [] labelAddMass;
	private double pepMass;
	private double glycoMass;
	private GlycoSite [] sites;
	private double [] glycoPercent;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_2;
	private DecimalFormat df2 = DecimalFormats.DF0_2;
	private DecimalFormat df4 = DecimalFormats.DF0_4;

	/**
	 * 
	 * Used in GlycoLabelFeaturesXMLReader, need SSM
	 * 
	 * @param peptide
	 * @param feas
	 * @param ssm
	 * @param peptideId
	 * @param ssmsIds
	 * @param deleSSMId
	 */
	public GlycoPeptideLabelPair(IGlycoPeptide peptide, LabelFeatures feas, NGlycoSSM ssm,
			int peptideId, ArrayList<Integer> ssmsIds, int deleSSMId){
		
		super(peptide, feas);
		this.peptideId = peptideId;
		this.ssmsIds = ssmsIds;
		this.deleSSMId = deleSSMId;
		this.ssm = ssm;
		
		this.pepMass = ssm.getPepMass();
		this.glycoMass = ssm.getGlycoMass();
		this.sites = peptide.getAllGlycoSites();
//		this.labelAddMass = new double[masses.length];
	}

	/**
	 * 
	 * Used in NGlyStrucLabelGetter, not need ssm
	 * 
	 * @param peptide2
	 * @param feas2
	 * @param integer
	 * @param ssmIdList
	 * @param deleId
	 */
	public GlycoPeptideLabelPair(IGlycoPeptide peptide, LabelFeatures feas,
			int peptideId, ArrayList<Integer> ssmIdList, int deleId) {
		// TODO Auto-generated constructor stub
		super(peptide, feas);
		this.peptideId = peptideId;
		this.ssmsIds = ssmIdList;
		this.deleSSMId = deleId;
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
	 * @return the ssmsIds
	 */
	public ArrayList<Integer> getSsmsIds() {
		return ssmsIds;
	}

	/**
	 * @param ssmsIds the ssmsIds to set
	 */
	public void setSsmsIds(ArrayList<Integer> ssmsIds) {
		this.ssmsIds = ssmsIds;
	}

	/**
	 * @return the deleSSMId
	 */
	public int getDeleSSMId() {
		return deleSSMId;
	}

	/**
	 * @param deleSSMId the deleSSMId to set
	 */
	public void setDeleSSMId(int deleSSMId) {
		this.deleSSMId = deleSSMId;
	}

	public NGlycoSSM getSSM(){
		return ssm;
	}

	public String getSiteInfo(SimpleProInfo info, SeqLocAround sla){
		
		StringBuilder sb = new StringBuilder();
		sb.append(info.getRef()).append("\t");
		
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
		IGlycoPeptide pep = (IGlycoPeptide) this.getPeptide();
		sb.append(this.getSequence()).append("\t");
		sb.append(pepMass).append("\t");
		sb.append(glycoMass).append("\t");

		sb.deleteCharAt(sb.length()-1);
		sb.append("\t");
		
		/*double [] glycoPercent = pep.getGlycoPercents();
		for(int i=0;i<glycoPercent.length;i++){
			sb.append(dfPer.format(glycoPercent[i])).append("\t");
		}*/

		double [] ratios = this.getSelectRatios();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
		}
		
		double [] intens = this.getTotalIntens();
		for(int i=0;i<intens.length;i++){
			sb.append(intens[i]).append("\t");
		}
		
		return sb.toString();
		
	}
	
	public String getGlycanInfo(){
		
		StringBuilder sb = new StringBuilder();
		IGlycoPeptide pep = (IGlycoPeptide) this.getPeptide();
		NGlycoSSM ssm = pep.getDeleStructure();
		sb.append(ssm.getName()).append("\t");
		sb.append(ssm.getScore()).append("\t");
		sb.append(ssm.getRank()).append("\t");
		
		return sb.toString();
	}
	
	public String getPepInfo(double [] proRatio, NGlycoSSM [] ssms){
		
		StringBuilder sb = new StringBuilder();
		IGlycoPeptide pep = (IGlycoPeptide) this.getPeptide();
		sb.append(this.getSequence()).append("\t");
		sb.append(pepMass).append("\t");
		sb.append(glycoMass).append("\t");
		
		for(int i=0;i<ssmsIds.size();i++){
			int scannum = ssms[ssmsIds.get(i)].getScanNum();
			sb.append(scannum).append(", ");
		}

		sb.delete(sb.length()-2, sb.length()-1);
		sb.append("\t");
		
		double [] glycoPercent = pep.getGlycoPercents();
		for(int i=0;i<glycoPercent.length;i++){
			sb.append(dfPer.format(glycoPercent[i])).append("\t");
		}

		double [] ratios = this.getSelectRatios();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
		}
		
		for(int i=0;i<ratios.length;i++){
			
			double rela = 0;
			if(proRatio[i]!=0){
				rela = ratios[i]/proRatio[i];
			}
			sb.append(df4.format(rela)).append("\t");
		}
		
		for(int i=0;i<proRatio.length;i++){
			sb.append(proRatio[i]).append("\t");
		}
		
		return sb.toString();
		
	}

	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.getSequence()).append("\t");
		sb.append(pepMass).append("\t");
		sb.append(glycoMass).append("\t");
		
		double [] ratios = this.getRatios();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
//				.append(df4.format(score[i])).append("\t");
		}
			
		double [] intens = this.getTotalIntens();
		for(int j=0;j<intens.length;j++){
			sb.append(intens[j]).append("\t").append(glycoPercent[j]).append("\t");	
		}
		sb.append(this.getRefs()).append("\t");

		sb.append(this.getSrc());
		
		return sb.toString();
	}
	
	public String getFeaturesObjectString(){
		
		StringBuilder sb = new StringBuilder();

		sb.append(this.getSequence()).append("\t");
		sb.append(glycoMass).append("\t");
		sb.append(pepMass).append("\t");
		
		double [] ratios = this.getSelectRatios();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
		}
			
		double [] intens = this.getTotalIntens();
		for(int j=0;j<intens.length;j++){
			sb.append(dfE4.format(intens[j])).append("\t");
			sb.append(glycoPercent[j]).append("\t");
		}		

		String deleRef = this.getDelegateRef();
		if(deleRef==null){
			sb.append(this.getRefs()).append("\t");
		}else{
			sb.append(deleRef).append("\t");
		}
		
		NGlycoSSM ssm = ((IGlycoPeptide)this.getPeptide()).getDeleStructure();
		sb.append(ssm.getName()).append("\t");
		sb.append(ssm.getScore()).append("\t");
		sb.append(ssm.getRank()).append("\t");
		
		sb.append(this.getSrc());
		
		return sb.toString();
	}

	public String outputString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.getSequence()).append("\t");
		sb.append(pepMass).append("\t");
		sb.append(glycoMass).append("\t");
		
		double [] ratios = this.getRatios();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
//				.append(df4.format(score[i])).append("\t");
		}
			
		double [] intens = this.getTotalIntens();
		for(int j=0;j<intens.length;j++){
			sb.append(intens[j]).append("\t");	
		}
		sb.append(this.getRefs()).append("\t");
		sb.append(this.getSrc()).append("\t");

		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o){
		
		if(o instanceof GlycoPeptideLabelPair){
			GlycoPeptideLabelPair p = (GlycoPeptideLabelPair)o;
			
			String k1 = this.getSequence() + this.getSrc();
			String k2 = p.getSequence() + p.getSrc();
			
			if(k1.equals(k2)){
				double p1 = this.pepMass;
				double g1 = this.glycoMass;
				double p2 = p.pepMass;
				double g2 = p.glycoMass;
				if(Math.abs(p1-p2)<0.1 && Math.abs(g1-g2)<0.1){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}		
	}
	
	@Override
	public int hashCode(){
		String s = this.getSequence() + this.getSrc() +
			this.glycoMass + "" + this.pepMass;
		return s.hashCode();
	}
	
	public String getPairObjectString(){
		
		StringBuilder sb = new StringBuilder();

		sb.append(this.getSequence()).append("\t");
		sb.append(this.glycoMass).append("\t");
		sb.append(df4.format(this.pepMass)).append("\t");

		double [] ratios = this.getSelectRatios();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
//				.append(df4.format(score[i])).append("\t");
		}
			
		double [] intens = this.getTotalIntens();
		for(int j=0;j<intens.length;j++){
			sb.append(dfE4.format(intens[j])).append("\t");	
		}

		String deleRef = this.getDelegateRef();
		if(deleRef==null){
			sb.append(super.peptide.getDelegateReference()).append("\t");
		}else{
			sb.append(deleRef).append("\t");
		}
		sb.append(this.ssm.getName()).append("\t");
		sb.append(df2.format(this.ssm.getScore())).append("\t");
		
		return sb.toString();
	}
	
}
