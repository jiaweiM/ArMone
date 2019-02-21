/*
 ******************************************************************************
 * File: IntensityComparator.java * * * Created on 02-18-2009
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
 * Arrange the peaks by intensity, from big to small.
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 14:21:10
 */
public class IntensityComparator implements Comparator<IPeak> {

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(IPeak peak1, IPeak peak2) {
		double intens1 = peak1.getIntensity();
		double intens2 = peak2.getIntensity();
		
		if(intens1 == intens2)
			return 0;
		
		return intens1 > intens2 ? -1 : 1;
	}

}
