/*
 * *****************************************************************************
 * File: ScanList.java Created on 11-21-2007
 * 
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * *****************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * A list of scans in a raw file
 * 
 * @author Xinning
 * @version 0.1.2, 02-25-2009, 14:28:26
 */
public class MS2ScanList implements IScanList{
	
	private HashMap<Integer, IMS2Scan> map;
	
	private DtaType type;

	private boolean isContainPeaklist = true;

	/*
	 * The first scan
	 */
	private int firstscan = Integer.MAX_VALUE;
	
	/*
	 * The last scan
	 */
	private int lastscan = 0;

	/**
	 * A scan list
	 */
	public MS2ScanList(DtaType type) {
		this.type = type;
		map = new HashMap<Integer, IMS2Scan>();
	}

	/**
	 * Create a scan with the specific approximate number of scans.
	 * 
	 * @param approx_scan
	 */
	public MS2ScanList(DtaType type, int approx_scan) {
		this.type = type;
		map = new HashMap<Integer, IMS2Scan>(approx_scan);
	}

	/**
	 * Add scan to the scanlist. Note that, only one instance of Scan for a
	 * specific scan number can be maintained in the list. The later added one
	 * will overwrite the previous one if the scan number is the same.
	 * 
	 * @param Scan
	 */
	public void add(ISpectrum scan) {
		
		int num = scan.getScanNum();
		map.put(num, (IMS2Scan)scan);

		if(num < this.firstscan)
			this.firstscan = num;
		
		if(num > this.lastscan)
			this.lastscan = num;
		
		//make sure all the inner scans has peaklist
		if (this.isContainPeaklist && !scan.isContainPeaklist())
			this.isContainPeaklist = false;
	}

	/**
	 * The map of the scan list. key = scan number, value = scan instance
	 * 
	 * @return
	 */
	public HashMap<Integer, IMS2Scan> getScanListMap() {
		return this.map;
	}

	/**
	 * The scans in the scan list
	 * 
	 * @return
	 */
	public IMS2Scan[] getScans() {
		return this.map.values().toArray(new IMS2Scan[this.map.size()]);
	}
	
	/**
	 * The DtaType of the raw file to generate this scan list
	 * 
	 * @return
	 */
	public DtaType getDtaType() {
		return this.type;
	}

	/**
	 * The scan number of the first scan. If no scan return 0
	 * 
	 * @return
	 */
	public int getFirstScan() {
		
		if(this.firstscan == Integer.MAX_VALUE)
			return 0;
		
		return this.firstscan;
	}
	
	/**
	 * The scan number of the last scan. If no scan, return 0
	 * 
	 * @return
	 */
	public int getLastScan() {
		return this.lastscan;
	}
	
	/**
	 * The scan numbers of the scans in this scan list.
	 * 
	 * @return
	 */
	public int[] getScanNums() {
		int[] nums = new int[this.getNumScans()];
		int i = 0;
		for (Integer key : this.map.keySet()) {
			nums[i++] = key;
		}
		return nums;
	}

	/**
	 * Return scan instence for the scan number. If the scan for the scan number
	 * is not existed, null will be returned.
	 * 
	 * @param scannumber
	 * @return scan
	 */
	public ISpectrum getScan(int scannumber) {
		ISpectrum scan = map.get(new Integer(scannumber));
//		if (scan == null)
//			System.out.println("The query scan with scan index of "
//			        + scannumber + " is null (don't exist).");
		return scan;
	}

	/**
	 * @return total number of scans in this list;
	 */
	public int getNumScans() {
		return map.size();
	}

	/**
	 * get the scans whose scan number is lower than the specific scan number by 3
	 * this is mainly for the ms3 to find its ms2 spectral,
	 */
	public IMS2Scan[] getPrevious3Scans(int scannumber) {
		return this.getPreviousScans(scannumber, 3);
	}

	/**
	 * Get the previous scans before the specific scan number. The number of
	 * previous scans can be specified by the parameter of "count"
	 * 
	 * @param scannumber
	 * @param count
	 * @return
	 */
	public IMS2Scan[] getPreviousScans(int scannumber, int count) {
		
		ArrayList <IMS2Scan> scanlist = new ArrayList <IMS2Scan> ();
		for(int i=1;i<scannumber;i++){
			if(map.containsKey(scannumber-i)){
				scanlist.add(map.get(scannumber-i));
			}
			if(scanlist.size()==count)
				break;
		}
		
		IMS2Scan [] scans = scanlist.toArray(new IMS2Scan[scanlist.size()]);
		return scans;
	}

	/**
	 * Whether all the scans in the scan list contains peaklist (peaks
	 * information)
	 * 
	 * @return
	 */
	public boolean isContainPeaklist() {
		return this.isContainPeaklist;
	}
}
