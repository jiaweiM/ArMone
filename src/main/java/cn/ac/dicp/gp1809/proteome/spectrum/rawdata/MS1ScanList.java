/* 
 ******************************************************************************
 * File:MS1ScanList.java * * * Created on 2010-4-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * @author ck
 *
 * @version 2010-4-23, 03:19:48
 */
public class MS1ScanList implements IScanList{

	private HashMap <Integer, IMS1Scan> scanMap;

	private DtaType type;

	/*
	 * The first scan
	 */
	private int firstscan = Integer.MAX_VALUE;
	
	/*
	 * The last scan
	 */
	private int lastscan = 0;
	
	public MS1ScanList(DtaType type){
		this.type = type;
		this.scanMap = new HashMap <Integer, IMS1Scan>();
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanList#add(cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum)
	 */
	@Override
	public void add(ISpectrum scan) {
		// TODO Auto-generated method stub
		
		int num = scan.getScanNum();
		scanMap.put(num, (IMS1Scan)scan);

		if(num < this.firstscan)
			this.firstscan = num;
		
		if(num > this.lastscan)
			this.lastscan = num;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanList#getFirstScan()
	 */
	@Override
	public int getFirstScan() {
		// TODO Auto-generated method stub
		return firstscan;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanList#getLastScan()
	 */
	@Override
	public int getLastScan() {
		// TODO Auto-generated method stub
		return lastscan;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IScanList#getScans()
	 */
	@Override
	public IMS1Scan [] getScans() {
		// TODO Auto-generated method stub
		return this.scanMap.values().toArray(new IMS1Scan[this.scanMap.size()]);
	}

	public IMS1Scan getScan(int scannumber) {
		IMS1Scan scan = scanMap.get(scannumber);
//		if (scan == null)
//			System.out.println("The query scan with scan index of "
//			        + scannumber + " is null (don't exist).");
		return scan;
	}

	/**
	 * @return total number of scans in this list;
	 */
	public int getNumScans() {
		return scanMap.size();
	}
	
	public DtaType getDtaType() {
		return this.type;
	}
	
	public IMS1Scan getPreviousScan(int scannumber) {

		for(int i=scannumber-1;i>0;i--){			
			if(scanMap.containsKey(i)){
				return scanMap.get(i);
			}
		}

		return null;
	}
	
	public IMS1Scan getNextScan(int scannumber) {

		for(int i=scannumber+1;i<=lastscan;i++){			
			if(scanMap.containsKey(i)){
				return scanMap.get(i);
			}
		}
		return null;
	}
	
}
