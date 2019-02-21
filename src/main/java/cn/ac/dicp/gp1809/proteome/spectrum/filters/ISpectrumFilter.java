/*
 * *****************************************************************************
 * File: PeakFilter.java * * * Created on 08-04-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * This interface is mainly used for the filtering of PeakList so that only the
 * peaks passing specific filters are retained.
 * 
 * @author Xinning
 * @version 0.1, 08-04-2008, 15:48:41
 */
public interface ISpectrumFilter {

	/**
	 * Filter the PeakList to retain peaks passing specific filters. And as
	 * return, a new PeakList will be formed.
	 * 
	 * @param peaklist
	 * @return
	 */
	public IMS2PeakList filter(IMS2PeakList peaklist);

}
