/* 
 ******************************************************************************
 * File:QModsResult.java * * * Created on 2010-9-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2010-9-20, 09:28:45
 */
public class QModsResult {

	private String ref;
	private QModResult [] modResult;
	private double [] proRatio;
	private double [] RSD;
	
	private int index;
	private boolean isUnique;
	private boolean isGroup;
	
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	
	public QModsResult(String ref, QModResult [] modResult, double [] proRatio, double [] RSD){
		this.ref = ref;
		this.modResult = modResult;
		this.proRatio = proRatio;
		this.RSD = RSD;
	}
	
	public QModResult [] getQModResult(){
		return modResult;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public double [] getProRatio(){
		return proRatio;
	}
	
	public void setGroup(boolean isGroup){
		this.isGroup = isGroup;
	}
	
	public boolean getGroup(){
		return isGroup;
	}
	
	public void setUnique(boolean isUnique){
		this.isUnique = isUnique;
	}
	
	public boolean getUnique(){
		return isUnique;
	}
	
	public String getRef(){
		return ref;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(isUnique){
			sb.append(index).append("\t");
		}else{
			sb.append("(").append(index).append(")").append("\t");
		}
		sb.append(ref).append("\t");
		for(int i=0;i<proRatio.length;i++){
			sb.append(proRatio[i]).append("\t");
			sb.append(dfPer.format(RSD[i])).append("\t");
		}
		sb.append("\n");
		for(int i=0;i<modResult.length;i++){
			sb.append(modResult[i]);
		}
		return sb.toString();
	}
	
}
