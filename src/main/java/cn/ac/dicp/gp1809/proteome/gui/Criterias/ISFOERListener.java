/* 
 ******************************************************************************
 * File: ISFOERListener.java * * * Created on 08-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui.Criterias;

import java.awt.event.ActionListener;

/**
 * 
 * @author Xinning
 * @version 0.1, 08-08-2009, 19:19:34
 */
public interface ISFOERListener extends ActionListener {

	/**
	 * The maximum FDR for optimization
	 * 
	 * @param fdr
	 */
	public void setMaxFDR(double fdr);

	/**
	 * Set whether use the Sp and Rsp score
	 * 
	 * @param use
	 */
	public void setUseSpRsp(boolean use);

	/**
	 * Set whether use the delta M/z
	 * 
	 * @param use
	 */
	public void setUseDeltaMZ(boolean use);

	/**
	 * Set whether use FDR for confidence evaluation or FPR
	 * 
	 * @param use
	 */
	public void setUseFDR(boolean use);

}
