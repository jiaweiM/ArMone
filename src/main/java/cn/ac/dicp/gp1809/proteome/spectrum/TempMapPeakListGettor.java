/* 
 ******************************************************************************
 * File: cn.ac.dicp.gp1809.proteome.spectrum * * * Created on 2010-11-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.util.HashMap;

/**
 * @author ck
 *
 * @version 2010-11-16, 09:19:52
 */
public class TempMapPeakListGettor implements IPeakListGetter {

	private HashMap <Integer, IPeakList> peakListMap;
	
	public TempMapPeakListGettor(){
		this.peakListMap = new HashMap <Integer, IPeakList>();
	}
	
	public void addPeakList(int scan_num, IPeakList peaklist) {
		this.peakListMap.put(scan_num, peaklist);
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.IPeakListGetter#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		this.peakListMap = null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.IPeakListGetter#getPeakList(int)
	 */
	@Override
	public IPeakList getPeakList(int scan_num) {
		// TODO Auto-generated method stub
		return this.peakListMap.get(scan_num);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
