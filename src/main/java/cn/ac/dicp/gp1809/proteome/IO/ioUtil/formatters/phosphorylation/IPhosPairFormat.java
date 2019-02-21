/* 
 ******************************************************************************
 * File: IPhosPairFormat.java * * * Created on 02-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;

/**
 * The phospho peptide pair format
 * 
 * @author Xinning
 * @version 0.1.1, 06-12-2009, 20:42:02
 */
public interface IPhosPairFormat<Pep extends IPhosPeptidePair> extends
        IPeptideFormat<Pep> {

	/**
	 * The tscore for each site
	 */
	public static String TSCORE = "TScores";
	
	
	/**
	 * The scan number of MS2
	 */
	public static String SCAN_MS2 = "Scan_MS2";
	
	/**
	 * The scan number of MS3
	 */
	public static String SCAN_MS3 = "Scan_MS3";
	
	/**
	 * The actual mz value of MS2 precursor ion
	 */
	public static String MZ_MS2 = "MZ_MS2";
	
	/**
	 * The actual mz value of MS3 precursor ion
	 */
	public static String MZ_MS3 = "MZ_MS3";
	
	/**
	 * Number of phospho sites
	 */
	public static String PHOS_SITE_NUM = "PSiteNum";
	
	/**
	 * The localization of the neutral loss in MS3
	 */
	public static String NEU_LOCATION = "NeuLossLoc";
	
}
