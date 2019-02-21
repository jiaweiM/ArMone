/* 
 ******************************************************************************
 * File: ISpectrumThreshold.java * * * Created on 02-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.filters;

import cn.ac.dicp.gp1809.util.IThreshold;

/**
 * The spectrum threshold 
 * 
 * @author Xinning
 * @version 0.1, 02-24-2009, 14:27:38
 */
public interface ISpectrumThreshold extends IThreshold {
	
	/**
	 * The tolerance of the mass (> 0)
	 * 
	 * @return The tolerance of the mass
	 */
	public double getMassTolerance();
	
	/**
	 * The intensity threshold. [0, 1]
	 * 
	 * @return The intensity threshold. [0, 1]
	 */
	public double getInstensityThreshold();
	
}
