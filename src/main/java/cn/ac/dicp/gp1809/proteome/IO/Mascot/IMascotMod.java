/* 
 ******************************************************************************
 * File: IMascotMod.java * * * Created on 11-16-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;

/**
 * The mascot modification
 * 
 * @author Xinning
 * @version 0.1, 11-16-2008, 10:45:06
 */
public interface IMascotMod extends IModification {

	/**
	 * The unique int value indicating the index of this modification in the
	 * used variable (or fix) modifications <b>(1-n)</b>
	 * <p>
	 * The id for variable and fix modifications are counted separately.
	 * 
	 * 
	 * @return
	 */

	public int getIndex();

}
