/* 
 ******************************************************************************
 * File: GlycoHcdPsm.java * * * Created on 2011-12-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.util.Arrays;
import java.util.HashMap;

import cn.ac.dicp.gp1809.glyco.NGlycoCompose;
import cn.ac.dicp.gp1809.glyco.NGlycoPossiForm;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;

/**
 * The peptide spectrum matching of the peptide and the hcd spectrum.
 * 
 * @author ck
 *
 * @version 2011-12-22, 10:07:50
 */
public class GlycoHcdPsm implements Comparable <GlycoHcdPsm> {

	private int hcdScannum;
	private double matchGlycoInten;
	private String peakOneLine;	
	private HashMap <Double, String> matchInfo;
	
	public GlycoHcdPsm(int hcdScannum, double matchGlycoInten, String peakOneLine, 
			HashMap <Double, String> matchInfo){
		
		this.hcdScannum = hcdScannum;
		this.matchGlycoInten = matchGlycoInten;
		this.peakOneLine = peakOneLine;
		this.matchInfo = matchInfo;
	}
	
	public int getHcdScannum(){
		return this.hcdScannum;
	}
	
	public double getMatchGlycoInten(){
		return this.matchGlycoInten;
	}
	
	public String getPeakOneLine(){
		return this.peakOneLine;
	}
	
	public HashMap <Double, String> getMatchInfo(){
		return this.matchInfo;
	}

	public double [] getMassList(){
		
		Double [] mz = this.matchInfo.keySet().toArray(new Double[matchInfo.size()]);
		int [] charges = new int[mz.length];
		
		for(int i=0;i<mz.length;i++){
			
			String info = matchInfo.get(mz[i]);
			int loc = info.indexOf(")");
			if(loc>0){
				charges[i] = info.length()-loc-1;
			}else{
				charges[i] = info.length()-3;
			}
		}
		
		double [] mass = new double[mz.length];
		for(int i=0;i<mass.length;i++){
			mass[i] = (mz[i]-AminoAcidProperty.PROTON_W) * charges[i];
		}
		
		Arrays.sort(mass);
		
		return mass;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GlycoHcdPsm o) {
		// TODO Auto-generated method stub
		
		double [] mass0 = this.getMassList();
		double [] mass1 = o.getMassList();
		
		double delta = Math.abs(mass0[0]-mass1[0]);
		NGlycoPossiForm [] forms = NGlycoCompose.calNoCore(delta);
		
		if(forms!=null){
			
			if(mass0[0]<mass1[0]){
				
				return 1;
				
			}else if(mass0[0]>mass1[0]){
				
				return -1;
			}
		}

		double i0 = this.matchGlycoInten;
		double i1 = o.matchGlycoInten;
		
		if(i0>i1*2){
			return 1;
		}
		
		if(i0*2<i1){
			return -1;
		}

		return 0;
	}
	
}
