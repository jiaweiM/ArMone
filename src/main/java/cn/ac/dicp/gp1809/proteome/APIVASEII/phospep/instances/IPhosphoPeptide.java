/* 
 ******************************************************************************
 * File: IPhosphoPeptide.java * * * Created on 02-17-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.instances;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;

/**
 * The instance of phosphopeptide which extends the Peptide and contains global
 * methods of phosphopeptide features.
 * 
 * @author Xinning
 * @version 0.1, 02-17-2009, 16:11:11
 */
public interface IPhosphoPeptide extends IPeptide {

	/**
	 * The number of phosphorylated sites
	 * 
	 * @return
	 */
	public int getPhosphoSiteNumber();

	/**
	 * The phosphorylated site. If this peptide is not a phosphopeptide, null
	 * will be return.
	 * 
	 * @return
	 */
	public PhosphoSite[] getPhosphoSites();

}
