/* 
 ******************************************************************************
 * File:GlycoSpectrum.java * * * Created on 2010-10-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2010-10-15, 13:32:04
 */
public class GlycoSpecUnit implements Comparable <GlycoSpecUnit>{

	private double precurseMz;
	private int [] scanRange;
	private int preCharge;
	private double pepMr;
	private ArrayList <Double> gPepPeaks;
	
	public GlycoSpecUnit(int [] scanRange, double precurseMz, int preCharge){
		this.precurseMz = precurseMz;
		this.scanRange = scanRange;
		this.preCharge = preCharge;
		this.gPepPeaks = new ArrayList <Double>();
		this.setPepMr();
	}
	
	public GlycoSpecUnit(int [] scanRange, double precurseMz, int preCharge, ArrayList <Double> gPepPeaks){
		this.precurseMz = precurseMz;
		this.scanRange = scanRange;
		this.preCharge = preCharge;
		this.gPepPeaks = gPepPeaks;
		this.setPepMr();
	}

	public void setPepMr(){
		DecimalFormat f = DecimalFormats.DF0_4;
		double mr = (precurseMz-AminoAcidProperty.PROTON_W)*preCharge;
		this.pepMr = Double.parseDouble(f.format(mr));
	}
	
	public double getPepMr(){
		return pepMr;
	}

	public double getMz(){
		return precurseMz;
	}
	
	public int [] getPreScanNum(){
		return scanRange;
	}
	
	public GlycoSpecUnit combine(GlycoSpecUnit unit){
		int [] s0 = this.scanRange;
		int [] s1 = unit.scanRange;
		ArrayList <Double> f0 = this.gPepPeaks;
		ArrayList <Double> f1 = unit.gPepPeaks;
		if(s1[0]<s0[0])
			s0[0] = s1[0];
		if(s1[1]>s0[1])
			s0[1] = s1[1];
		f0.addAll(f1);
		
		ArrayList <Double> f = new ArrayList <Double>();
		
		return new GlycoSpecUnit(s0, this.precurseMz, this.preCharge, f0);
	}
	
	@Override
	public int compareTo(GlycoSpecUnit gsu1) {
		// TODO Auto-generated method stub
		double m0 = this.pepMr;
		double m1 = gsu1.pepMr;
		if(m0<m1)
			return -1;
		else if(m0>m1)
			return 1;
		else
			return 0;
	}
	
}
