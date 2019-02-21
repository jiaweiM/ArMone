/* 
 ******************************************************************************
 * File: PeptidePair.java * * * Created on 2012-10-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2012-10-25, 14:26:30
 */
public class PeptidePair {
	
	protected IPeptide peptide;
	protected LabelFeatures features;
	
	protected String delegateRef;
	protected String refs;
	protected String src;
	protected short modCount;
	
	protected DecimalFormat dfE4 = DecimalFormats.DF_E4;
	
	public PeptidePair(IPeptide peptide, LabelFeatures features){
		this.peptide = peptide;
		this.features = features;
	}
	
	public IPeptide getPeptide(){
		return peptide;
	}
	
	public LabelFeatures getFeatures(){
		return features;
	}
	
	public int getCharge() {
		return this.features.getCharge();
	}
	
	/**
	 * @return
	 */
	public String getSequence() {
		// TODO Auto-generated method stub
		return this.peptide.getSequence();
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
	
	public void setSrc(String src){
		this.src = src;
	}
	
	public String getSrc(){
		return this.src;
	}
	
	public double[] getSelectRatios(){
		return this.features.getSelectRatio();
	}
	
	public double[] getSelectRIA(){
		return this.features.getSelectRIA();
	}
	
	public double[] getTotalIntens(){
		return this.features.getTotalIntens();
	}
	
	public boolean hasVariMod(){

		char [] chars = PeptideUtil.getSequence(this.getSequence()).toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]<'A' || chars[i]>'Z'){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isAccurate(){
		return this.features.isValidate();
	}

	@Override
	public String toString(){
		
		StringBuilder sb = new StringBuilder();

		sb.append(this.getSequence()).append("\t");
		
		double [] ratios = features.getSelectRatio();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
//				.append(df4.format(score[i])).append("\t");
		}
			
		double [] intens = features.getTotalIntens();
		for(int j=0;j<intens.length;j++){
			sb.append(dfE4.format(intens[j])).append("\t");	
		}

//		sb.append(features.getPresentFeasNum()).append("\t");
		String deleRef = this.getDelegateRef();
		if(deleRef==null){
//			sb.append(this.getRefs()).append("\t").append(src);
			sb.append(this.getRefs()).append("\t");
		}else{
//			sb.append(deleRef).append("\t").append(src);
			sb.append(deleRef).append("\t");
		}
		
		return sb.toString();
	}
	
	public String getPairObjectString(){
		
		StringBuilder sb = new StringBuilder();

		sb.append(this.getSequence()).append("\t");

		double [] ratios = features.getSelectRatio();
		for(int i=0;i<ratios.length;i++){
			sb.append(ratios[i]).append("\t");
//				.append(df4.format(score[i])).append("\t");
		}
			
		double [] intens = features.getTotalIntens();
		for(int j=0;j<intens.length;j++){
			sb.append(dfE4.format(intens[j])).append("\t");	
		}

		String deleRef = this.getDelegateRef();
		if(deleRef==null){
			sb.append(this.getRefs()).append("\t").append(src);
		}else{
			sb.append(deleRef).append("\t").append(src);
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o){
		
		if(o instanceof PeptidePair){
			PeptidePair p = (PeptidePair) o;
			String k1 = this.getSequence() + this.src + this.getCharge();
			String k2 = p.getSequence() + p.src + p.getCharge();
			return k1.equals(k2);
			
		}else{
			return false;
		}		
	}
	
	@Override
	public int hashCode(){
		String s = this.getSequence() + this.src + features.getCharge();
		return s.hashCode();
	}

	/**
	 * @return
	 */
	public double[] getRatios() {
		// TODO Auto-generated method stub
		return features.getRatios();
	}

}
