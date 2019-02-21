/* 
 ******************************************************************************
 * File: GlycoPeakList.java * * * Created on 2011-5-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import cn.ac.dicp.gp1809.proteome.spectrum.AbstractPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;

/**
 * @author ck
 *
 * @version 2011-5-26, 15:25:41
 */
public class GlycoPeakList extends AbstractPeakList implements IPeakList{

/*	
	public void add(IPeak peak){
		if(peak instanceof GlycoPepPeak){
			addGlycoPeak((GlycoPepPeak) peak);
		}
	}

	public void addGlycoPeak(GlycoPepPeak gp){
		
	}
*/
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.IPeakList#newInstance()
	 */
	@Override
	public IPeakList newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.IPeakList#toBytePeaks()
	 */
	@Override
	public byte[] toBytePeaks() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.IPeakList#toPeaksOneLine()
	 */
	@Override
	public String toPeaksOneLine() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
