/* 
 ******************************************************************************
 * File:IScanList.java * * * Created on 2010-9-29
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;

/**
 * @author ck
 *
 * @version 2010-9-29, 09:09:45
 */
public interface IScanList {

	public void add(ISpectrum scan);
	
	public ISpectrum [] getScans();
	
	public int getFirstScan();
	
	public int getLastScan();
	
}
