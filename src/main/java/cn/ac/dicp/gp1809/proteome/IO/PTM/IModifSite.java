/* 
 ******************************************************************************
 * File: IModifSite.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * The modification site on a peptide sequence.
 * 
 * @author Xinning
 * @version 0.2, 05-19-2009, 16:08:03
 */
public interface IModifSite extends IDeepCloneable{

	/**
	 * The aminoacid of the modification is not defined, you can set as this
	 * value. In other word, if you don't exactly know which aminoacid the
	 * modification occurs, you can set as this character.
	 */
	public static final char NOT_DEFINED_AA = 0;

	/**
	 * The modified site
	 * 
	 * @return
	 */
	public ModSite modifiedAt();

	/**
	 * The localization of the modification on this peptide sequence. from 1 - n
	 * 
	 * @return
	 */
	public int modifLocation();

	/**
	 * The symbol for this modification.
	 * 
	 * @return
	 */
	public char symbol();

	/**
	 * Set the modified aminoacid
	 * 
	 * @param aa
	 */
	public void setModifiedAt(ModSite site);

	/**
	 * The localization (from 1 - n)
	 * 
	 * @param loc
	 */
	public void setModifLocation(int loc);

	/**
	 * The symbol of this modification site
	 * 
	 * @param symbol
	 */
	public void setSymbol(char symbol);
	
	/**
	 * Deep clone
	 */
	public IModifSite deepClone();

}
