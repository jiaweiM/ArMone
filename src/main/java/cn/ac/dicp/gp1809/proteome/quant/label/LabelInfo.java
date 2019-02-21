/* 
 ******************************************************************************
 * File:LabelInfo.java * * * Created on 2010-5-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2010-5-8, 09:38:09
 */
public class LabelInfo {

	private ModSite site;
	private double mass;
	private char symbol;
	private String des;
	
	// This ModSite will be labeled absolutely or partially
	private boolean absolutely = true;
	
	public static final LabelInfo Dimethyl_CH3_K = new LabelInfo(ModSite.newInstance_aa('K'), 28.0313, "Dimethyl_CH3_K");
	public static final LabelInfo Dimethyl_CH3_Nt = new LabelInfo(ModSite.newInstance_PepNterm(), 28.0313, "Dimethyl_CH3_Nt");
	public static final LabelInfo Dimethyl_CH3_NPt = new LabelInfo(ModSite.newInstance_ProNterm(), 28.0313, "Dimethyl_CH3_NPt");
	public static final LabelInfo Dimethyl_CHD2_K = new LabelInfo(ModSite.newInstance_aa('K'), 32.056407, "Dimethyl_CHD2_K");
	public static final LabelInfo Dimethyl_CHD2_Nt = new LabelInfo(ModSite.newInstance_PepNterm(), 32.056407, "Dimethyl_CHD2_Nt");
	public static final LabelInfo Dimethyl_CHD2_NPt = new LabelInfo(ModSite.newInstance_ProNterm(), 32.056407, "Dimethyl_CHD2_NPt");
	public static final LabelInfo Dimethyl_C13HD2_K = new LabelInfo(ModSite.newInstance_aa('K'), 34.063117, "Dimethyl_C13HD2_K");
	public static final LabelInfo Dimethyl_C13HD2_Nt = new LabelInfo(ModSite.newInstance_PepNterm(), 34.063117, "Dimethyl_C13HD2_Nt");
	public static final LabelInfo Dimethyl_C13HD2_NPt = new LabelInfo(ModSite.newInstance_ProNterm(), 34.063117, "Dimethyl_C13HD2_NPt");
	public static final LabelInfo Dimethyl_C13D3_K = new LabelInfo(ModSite.newInstance_aa('K'), 36.075670, "Dimethyl_C13D3_K");
	public static final LabelInfo Dimethyl_C13D3_Nt = new LabelInfo(ModSite.newInstance_PepNterm(), 36.075670, "Dimethyl_C13D3_Nt");
	public static final LabelInfo Dimethyl_C13D3_NPt = new LabelInfo(ModSite.newInstance_ProNterm(), 36.075670, "Dimethyl_C13D3_NPt");
	
	public static final LabelInfo SILAC_Arg6 = new LabelInfo(ModSite.newInstance_aa('R'), 6.020129, "SILAC_Arg6");
	public static final LabelInfo SILAC_Arg10 = new LabelInfo(ModSite.newInstance_aa('R'), 10.008269, "SILAC_Arg10");
	public static final LabelInfo SILAC_Lys4 = new LabelInfo(ModSite.newInstance_aa('K'), 4.025127, "SILAC_Lys4");
	public static final LabelInfo SILAC_Lys6 = new LabelInfo(ModSite.newInstance_aa('K'), 6.020129, "SILAC_Lys6");
	public static final LabelInfo SILAC_Lys8 = new LabelInfo(ModSite.newInstance_aa('K'), 8.014199, "SILAC_Lys8");
	public static final LabelInfo SILAC_Leu3 = new LabelInfo(ModSite.newInstance_aa('L'), 3.018845, "SILAC_Leu3");
	
	public static final LabelInfo ICPL0_K = new LabelInfo(ModSite.newInstance_aa('K'), 105.021464, "ICPL0_K");
	public static final LabelInfo ICPL4_K = new LabelInfo(ModSite.newInstance_aa('K'), 109.046571, "ICPL4_K");
	public static final LabelInfo ICPL6_K = new LabelInfo(ModSite.newInstance_aa('K'), 111.041593, "ICPL6_K");
	public static final LabelInfo ICPL10_K = new LabelInfo(ModSite.newInstance_aa('K'), 115.0667, "ICPL10_K");
	public static final LabelInfo ICPL0_PepN = new LabelInfo(ModSite.newInstance_PepNterm(), 105.021464, "ICPL0_PepN");
	public static final LabelInfo ICPL4_PepN = new LabelInfo(ModSite.newInstance_PepNterm(), 109.046571, "ICPL4_PepN");
	public static final LabelInfo ICPL6_PepN = new LabelInfo(ModSite.newInstance_PepNterm(), 111.041593, "ICPL6_PepN");
	public static final LabelInfo ICPL10_PepN = new LabelInfo(ModSite.newInstance_PepNterm(), 115.0667, "ICPL10_PepN");
	public static final LabelInfo ICPL0_ProN = new LabelInfo(ModSite.newInstance_ProNterm(), 105.021464, "ICPL0_ProN");
	public static final LabelInfo ICPL4_ProN = new LabelInfo(ModSite.newInstance_ProNterm(), 109.046571, "ICPL4_ProN");
	public static final LabelInfo ICPL6_ProN = new LabelInfo(ModSite.newInstance_ProNterm(), 111.041593, "ICPL6_ProN");
	public static final LabelInfo ICPL10_ProN = new LabelInfo(ModSite.newInstance_ProNterm(), 115.0667, "ICPL10_ProN");

	public LabelInfo(ModSite site, double mass, String des){
		this.site = site;
		this.mass = mass;
		this.des = des;
	}
	
	public LabelInfo(ModSite site, double mass, char symbol){
		this.site = site;
		this.mass = mass;
		this.symbol = symbol;
	}
	
	public void setSymbol(char symbol){
		this.symbol = symbol;
	}
	
	public ModSite getSite(){
		return site;
	}
	
	public double getMass(){
		return mass;
	}
	
	public String getDes(){
		return des;
	}
	
	public char getSymbol(){
		return symbol;
	}
/*
	public static LabelInfo getLabelInfo(LabelType type, IModification motif){
		double addMz = motif.getAddedMonoMass();
		short [] typeArray = type.getIsoType();
		double [] mass = type.getMass();
		String des = type.getLabelName();
		short isotope = 0;
		
		for(int i=0;i<typeArray.length;i++){
			short s = (short) addMz;
			if(s==(short)mass[i]){
				isotope = typeArray[i];
			}	
		}
		return new LabelInfo(des,isotope);
	}
	
	public static String getDescrib(LabelType type, IModification motif){
		double addMz = motif.getAddedMonoMass();	
		return getDescrib(type, addMz);
	}
	
	public static String getDescrib(LabelType type, double add){
		
		short [] typeArray = type.getIsoType();
		double [] mass = type.getMass();
		String des = type.getLabelName();
		String s1 = null;
		
		for(int i=0;i<typeArray.length;i++){
			short s = (short) add;
			if(s==(short)mass[i]){
				s1 = String.valueOf(typeArray[i]);
			}	
		}
		if(s1==null){
			return null;
		}else{
			return des+"_"+s1;
		}
	}
	
	public static String getDescribTemp(LabelType type, double add){

		String des = type.getLabelName();
		String s1 = null;
		
		short s = (short) add;
		
		if(s==14 || s==28 || s==42){
			s1 = "1";
		}
		if(s==17 || s==34 || s==51){
			s1 = "2";
		}

		
		if(s==6)
			s1 = "2";
		
		if(s1==null){
			return null;
		}else{
			return des+"_"+s1;			
		}
	}
	
	public String getDescrib(LabelInfo info){
		return info.toString();
	}
*/	
	
	public void setAbsolutely(boolean absolutely){
		this.absolutely = absolutely;
	}
	
	public boolean getAbsolutely(){
		return absolutely;
	}

	public String getDescription(){
		StringBuilder sb = new StringBuilder();
		sb.append(site).append("\t");
		sb.append(mass).append("\t");
		sb.append(symbol).append("\t");
		sb.append(des).append("\t");
		return sb.toString();
	}
	
	public String getNoSymbolDescription(){
		StringBuilder sb = new StringBuilder();
		sb.append(site).append("\t");
		sb.append(mass).append("\t");
		sb.append(des).append("\t");
		return sb.toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(des).append("\t");
		return sb.toString();
	}

}
