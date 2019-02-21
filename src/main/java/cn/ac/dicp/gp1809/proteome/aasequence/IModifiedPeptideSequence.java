/* 
 ******************************************************************************
 * File: IModifiedPeptideSequence.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;

/**
 * This is peptide sequence with potential modifications. Commonly, this
 * indicate peptide output by database search engines with format of
 * A.AAAAAA#AAAA*A.A
 * 
 * @author Xinning
 * @version 0.1.1, 06-09-2009, 21:53:24
 */
public interface IModifiedPeptideSequence extends IPeptideSequence {

	/**
	 * The sequence with modification sites. For example, AAAAA#AAANgAAA, in
	 * which g indicates the glyco modification at the aminoacid before g (this
	 * is N). No terminal aminoacid
	 * 
	 * @return
	 */
	public String getSequence();

	/**
	 * Get the modification informations on this peptide sequence. If there is
	 * no modifications, null should be returned.
	 * 
	 * @return
	 */
	public IModifSite[] getModifications();
	
	/**
	 * Get the target modifications by the specified symbol
	 * @return
	 */
	public IModifSite[] getTargetMod(char symbol);

	/**
	 * Get the modification at specific localization. The loc is 1 based index
	 * without consideration of variable modifications. For example, the
	 * localization of "#" modification is 3 for the sequence of "K.A*AA#AAAA.R"
	 * 
	 * <li>I think Modifications at term such as N-term is different with modifications
	 * at aminoacids. So if use "." indicate the N-term, the location can be begin 
	 * with 0.
	 * @since 0.1.1
	 * @param loc 1 based index
	 * @return null if the specific aminoacid for the localization is not modified
	 */
	public IModifSite getModificationAt(int loc);

	/**
	 * The number of modification sites.
	 * 
	 * @return
	 */
	public int getModificationNumber();

	/**
	 * There are two ways to renew the sequence: <li>1. if you only want to
	 * change the modification symbol (e.g. change # to p for phosphorylation
	 * modification), you may fist get the modification sites (or the
	 * phosphorylation sites) and change the symbols defined in them, then
	 * invoke this method. <li>2. if you want to change the modification sites
	 * (the number of modifications and the modification sites may both be
	 * changed), you can use the method
	 * {@link #renewModifiedSequence(IModifSite[])};
	 */
	public void renewModifiedSequence();

	/**
	 * Renew the modified sequence by the new modified sites. The old
	 * modifications will be replaced by the new. If you want to set the
	 * sequence with no modification, just set the newmodifs as null or with
	 * empty element.
	 * 
	 * @param newmodifs
	 */
	public void renewModifiedSequence(IModifSite[] newmodifs);

}
