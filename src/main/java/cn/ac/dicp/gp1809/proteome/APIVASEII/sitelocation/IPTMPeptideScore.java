/* 
 ******************************************************************************
 * File: IPhosPeptideScore.java * * * Created on 06-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import cn.ac.dicp.gp1809.proteome.spectrum.Ion;

/**
 * The peptide score for phosphorylation site localization. <p>Now not only used in phosphorylation
 * but also used in other PTMs.
 * 
 * @see PTMScore, PeptideScore
 * @author Xinning
 */
public interface IPTMPeptideScore {

	/**
	 * The calculated peptide score the input ions of phosphorylated peptides
	 * 
	 * @param ions
	 * @return
	 */
	public double calculateScore(Ion[] ions);
}
