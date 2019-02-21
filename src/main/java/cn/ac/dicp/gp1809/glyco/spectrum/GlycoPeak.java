/* 
 ******************************************************************************
 * File:GlycoPeak.java * * * Created on 2010-10-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;

/**
 * @author ck
 *
 * @version 2010-10-15, 01:36:47
 */
public class GlycoPeak extends Peak{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * N-Acetylhexosamine, N-linked glycosylation
	 */
	public static final double HexNAc = 203.079373;
	
	public static final double Hex2NAc = 365.132197;

	public GlycoPeak(IPeak peak){
		super(peak.getMz(), peak.getIntensity());
	}

	public static GlycoPeak getGlycoPeak(IPeakList peaklist){
		double [] mzs = new double []{HexNAc+AminoAcidProperty.PROTON_W, 
				Hex2NAc+AminoAcidProperty.PROTON_W};
		
		IPeak basepeak = peaklist.getBasePeak();
		double baseInten = basepeak.getIntensity();
		
		IPeak [] peaks = peaklist.getPeakArray();

		for(int j=0;j<peaks.length;j++){
			double mz = peaks[j].getMz();
			double inten = peaks[j].getIntensity();
			for(int k=0;k<mzs.length;k++){
				if(Math.abs(mz-mzs[k])<0.01){
					if(inten/baseInten > 0.1){
						return new GlycoPeak(peaks[j]);
					}							
				}
			}
			if(mz-mzs[mzs.length-1]>0.1)
				break;
		}
		return null;
	}
	
	public static void main(String [] args){
		Pattern p1 = Pattern.compile(
				"Elution from: ([\\d\\.]*) to ([\\d\\.]*) .*FinneganScanNumber: (\\d*).*", Pattern.CASE_INSENSITIVE);
		String title = "Elution from: 59.387 to 59.387 period: 0 experiment: 1 cycles: 1 precIntensity: 857476.0 FinneganScanNumber: 6830 MStype: ";
		Matcher m = p1.matcher(title);
		if(m.matches()){
			System.out.println(m.groupCount());
			System.out.println(m.group(0));
			System.out.println(m.group(1));
			System.out.println(m.group(2));
			System.out.println(m.group(3));
		}
	}
	
}
