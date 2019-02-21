/*
 * *****************************************************************************
 * File: IMascotPeptide.java * * * Created on 11-04-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The Mascot Peptide
 * 
 * @author Xinning
 * @version 0.1, 11-04-2008, 20:06:41
 */
public interface IInspectPeptide extends IPeptide{

	/**
	 * @return the MQScore
	 */
	public float getMQScore();
	
	/**
	 * The pvalue
	 * @return
	 */
	public double getPValue();
	
	/**
	 * The F score
	 * @return
	 */
	public float getFscore();

}