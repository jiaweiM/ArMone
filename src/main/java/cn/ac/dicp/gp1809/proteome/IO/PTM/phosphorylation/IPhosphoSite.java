/* 
 ******************************************************************************
 * File: IPhosphoSite.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;

/**
 * The phosphorylation site
 * 
 * @author Xinning
 * @version 0.1, 02-19-2009, 21:27:18
 */
public interface IPhosphoSite extends IModifSite {

	/**
	 * If this site loses the phosphate in this sequence. In MS2 this should
	 * always be false.
	 * 
	 * @return
	 */
	public boolean isNeutralLoss();

	/**
	 * Set whether this site loses phosphate in MS3 as dehydrate. In MS2 this
	 * should always be false.
	 * 
	 * @param isNeutralLoss
	 */
	public void setNeutralLoss(boolean isNeutralLoss);
	
	/**
	 * Deep clone
	 */
	public IPhosphoSite deepClone();
}
