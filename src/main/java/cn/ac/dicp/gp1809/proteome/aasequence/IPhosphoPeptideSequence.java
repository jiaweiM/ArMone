/* 
 ******************************************************************************
 * File: IPhosphoPeptideSequence.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;

/**
 * The phosphopeptide sequence.
 * 
 * @author Xinning
 * @version 0.1.1, 09-04-2009, 22:33:04
 */
public interface IPhosphoPeptideSequence extends IModifiedPeptideSequence {

	/**
	 * The phosphorylation sites on this peptide sequence. Null will be returned
	 * if there is no phosphorylation site
	 * 
	 * @return
	 */
	public IPhosphoSite[] getPhosphorylations();

	/**
	 * The sites of phosphorylation which has lost the phosphate. This is useful
	 * for MS3 spectra. If this is a peptide from MS2 spectra, that is, sequece
	 * without neutral loss site, null will be returned.
	 * 
	 * @since 0.1.1
	 * @return
	 */
	public IPhosphoSite[] getNeuLostPhosphorylations();

	/**
	 * The number of phosphorylation sites
	 * 
	 * @return
	 */
	public int getPhosphorylationNumber();
	
	/**
	 * The number of phosphorylation which lost the phosphate
	 * 
	 * @since0.1.1
	 * @return
	 */
	public int getNeutralLostPhosphorylationNumber();

	/**
	 * There are two ways to renew the sequence: <li>1. if you only want to
	 * change the modification symbol (e.g. change # to p for phosphorylation
	 * modification), you may fist get the modification sites (or the
	 * phosphorylation sites) and change the symbols defined in them, then
	 * invoke this method. <li>2. if you want to change the modification sites
	 * (the number of modifications and the modification sites may both be
	 * changed), you can use the method
	 * {@link #renewModifiedSequence(IModifSite[])} or
	 * {@link #renewPhosphoSequence(IPhosphoSite[])};
	 * <p>
	 * If you want to change the phosphorylation symbol, you only need to reset
	 * the symbols in IPhosphoSite[] but not need to change the corresponding
	 * site in IModifSite[]
	 */
	public void renewModifiedSequence();

	/**
	 * Renew the phosphorylated peptides with the new assigned phosphosites. The
	 * original other modification (exception phospho) will not be changed.
	 * 
	 * @param phosSites
	 */
	public void renewPhosphoSequence(IPhosphoSite[] phosSites);

	/**
	 * {@inheritDoc}
	 */
	public IPhosphoPeptideSequence deepClone();
}
