/* 
 ******************************************************************************
 * File:ModifLabelPair.java * * * Created on 2010-6-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * The quantitative result of a modified site in a protein sequence, different peptide
 * pairs containing this site may be included.
 * 
 * @author ck
 *
 * @version 2010-6-22, 14:31:19
 */
public class ModifLabelPair implements Comparable <ModifLabelPair> {

	private ArrayList <double []> ratios;
	private HashSet <String> pepseqs;
	private String site;
	private char aa;
	private int loc;
	private String sequence;
	private double [] proRatio;

	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	/**
	 * 
	 * @param loc The site information of the modification in the protein sequence.
	 * @param sequence The sequence in the protein.
	 * @param intens
	 * @param src
	 */
	public ModifLabelPair(String site, String sequence, ArrayList <double []> ratios, HashSet <String> pepseqs){
		this.site = site;
		this.ratios = ratios;
		this.sequence = sequence;
		this.aa = site.charAt(0);
		this.loc = Integer.parseInt(site.substring(1, site.length()));
		this.pepseqs = pepseqs;
	}
	
	public ModifLabelPair(String site, String sequence, double [] ratiolist, String pepseq){
		this.site = site;
		this.sequence = sequence;
		this.ratios = new ArrayList <double []>();
		ratios.add(ratiolist);
		this.aa = site.charAt(0);
		this.loc = Integer.parseInt(site.substring(1, site.length()));
		
		this.pepseqs = new HashSet <String>();
		this.pepseqs.add(pepseq);
	}
	
	public String getAAround(){
		return sequence;
	}

	public int getLoc(){
		return loc;
	}
	
	public char getModAt(){
		return aa;
	}
	
	public String getSite(){
		return site;
	}
	
	public ArrayList <double []> getRatios(){
		return ratios;
	}
	
	public void setIntens(ArrayList <double []> ratios){
		this.ratios = ratios;
	}

	public void add(ModifLabelPair pair){
		this.ratios.addAll(pair.ratios);
		this.pepseqs.addAll(pair.pepseqs);
	}
	
	/**
	 * Get the average ratio of the intensity. 
	 * @return
	 */
	public double [] getRatio(){
		Iterator <double []> it = this.ratios.iterator();
		int n = 0;
		double [] ratioList = new double [ratios.get(0).length];
L:		while(it.hasNext()){
			double [] rs = it.next();
			for(int i=0;i<rs.length;i++){
				if(rs[i]==0){
					continue L;
				}
			}
			for(int i=0;i<rs.length;i++){
				ratioList[i] += rs[i];				
			}
			n++;
		}
		if(n>0){
			for(int i=0;i<ratioList.length;i++){
				ratioList[i] = Double.parseDouble(df4.format(ratioList[i]/n));
			}
		}else{
			Arrays.fill(ratioList, 0);
		}
		return ratioList;
	}

	public void setProRatio(double [] proRatio){
		this.proRatio = proRatio;
	}
	
	public double [] getRelaRatio(){
		double [] ratio = this.getRatio();
		double [] relaRatio = new double [ratio.length];
		if(proRatio.length != ratio.length){
			try {
				throw new Exception("");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i=0;i<ratio.length;i++){
			if(proRatio[i]==0){
				relaRatio[i] = 0;
			}else{
				relaRatio[i] = Double.parseDouble(df4.format(ratio[i]/proRatio[i]));
			}			
		}		
		return relaRatio;
	}
	
	public boolean use(){
		double [] ratio = this.getRatio();
		if(ratio[0]>0)
			return true;
		else
			return false;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(site).append("\t");
		sb.append(sequence).append("\t");
		double [] ratio = getRatio();
		double [] relaRatio = getRelaRatio();
		for(int i=0;i<ratio.length;i++){
			sb.append(ratio[i]).append("\t").append(relaRatio[i]).append("\t");
		}
	
		Iterator <String> it = this.pepseqs.iterator();
		while(it.hasNext()){
			String seq = it.next();
			sb.append(seq).append("\t");
		}

		return sb.toString();
	}
	
	public String toStringWithoutPep(){
		StringBuilder sb = new StringBuilder();
		sb.append(site).append("\t");
		sb.append(sequence).append("\t");
		double [] ratio = getRatio();
		double [] relaRatio = getRelaRatio();
		for(int i=0;i<ratio.length;i++){
			sb.append(ratio[i]).append("\t").append(relaRatio[i]).append("\t");
		}

		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ModifLabelPair o) {
		// TODO Auto-generated method stub

		int loc0 = Integer.parseInt(this.site.substring(1, this.site.length()));
		int loc1 = Integer.parseInt(o.site.substring(1, o.site.length()));
		if(loc0<loc1)
			return -1;
		else if(loc0>loc1)
			return 1;
		else
			return 0;
	}
	
	public boolean equals(Object obj){
		
		if(obj instanceof ModifLabelPair){
			
			ModifLabelPair mp = (ModifLabelPair) obj;
			if(!this.sequence.equals(mp.sequence)){
				return false;
			}
			
			if(this.loc!=mp.loc){
				return false;
			}
			
			if(!this.site.equals(mp.site)){
				return false;
			}
			
		}else{
			return false;
		}
		
		return true;
	}
	
	public int hashCode(){
		String key = this.site+"_"+this.loc+"_"+this.sequence;
		return key.hashCode();
	}

}
