/* 
 ******************************************************************************
 * File: IPhosphoPeptidePairMerger.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation.SeqvsTscore;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;

/**
 * The merger which is used to merge peptide identifications from MS2 and its
 * corresponding MS3 together
 * 
 * @author Xinning
 * @version 0.2, 06-12-2009, 20:29:10
 */
public interface IPhosPepPairCombiner<T extends IPhosPeptidePair, F extends IPeptide> {

	/**
	 * Merge them together and construct a phospeptide pair
	 * 
	 * @param pepMS2 the peptide identified from ms2
	 * @param pepMS3 the peptide identified from ms3
	 * @param ms2 the scan number of ms2
	 * @param ms3 the scan number of ms3
	 * @param seqt the sequence with Tscore instance
	 * @param source the source of the raw file
	 * @return
	 */
	@Deprecated
	public T combine(F pepMS2, F pepMS3, int ms2, int ms3, SeqvsTscore seqt);
	
	
	/**
	 * Merge them together and construct a phospeptide pair using the Ascore algorithm
	 * 
	 * @param pepMS2 the peptide identified from ms2
	 * @param pepMS3 the peptide identified from ms3
	 * @param ms2 the scan number of ms2
	 * @param ms3 the scan number of ms3
	 * @param seqt the sequence with Ascore instance
	 * @param source the source of the raw file
	 * @return
	 */
	public T combineAScore(F pepMS2, F pepMS3, int ms2, int ms3, SeqvsAscore seqt);
	
	
	/**
	 * The Peptide formatter for the combined phosphopeptide pair instance
	 * 
	 * @return
	 */
	public IPhosPairFormat<T> getPeptideFormatter();

}
