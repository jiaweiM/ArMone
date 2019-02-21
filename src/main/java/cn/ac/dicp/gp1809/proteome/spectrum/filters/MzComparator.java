/*
 ******************************************************************************
 * File: MzComparator.java * * * Created on 05-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import java.util.Comparator;

import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;

/**
 * Arrange the peaks by mz, from small to big.
 * 
 * @author Xinning
 * @version 0.1, 05-25-2009, 21:46:37
 */
public class MzComparator implements Comparator<IPeak> {

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(IPeak peak1, IPeak peak2) {
		double mz1 = peak1.getMz();
		double mz2 = peak2.getMz();
		
		if(mz1 == mz2)
			return 0;
		
		return mz1 > mz2 ? 1 : -1;
	}

}
