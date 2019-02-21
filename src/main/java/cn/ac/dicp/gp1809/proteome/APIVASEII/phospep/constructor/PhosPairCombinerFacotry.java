/* 
 ******************************************************************************
 * File: PhosphoPeptidePairCombiner.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Factory to construct the combiner.
 * 
 * @author Xinning
 * @version 0.1.0.2, 04-02-2009, 23:20:14
 */
public class PhosPairCombinerFacotry {

	/**
	 * Construct a phosphopeptide pair combiner
	 * 
	 * @param type
	 * @return
	 */
	public static IPhosPepPairCombiner<?,?> construct(PeptideType type,
	        char phosSymbol, char neuSymbol) {

		switch (type) {
		case SEQUEST:
			return new SequestPhosPepPairCombiner();
		case MASCOT:
			return new MascotPhosPepPairCombiner();
		case OMSSA:
			return new OMSSAPhosPepPairCombiner();
		case XTANDEM:
			return new XTandemPhosPepPairCombiner();
		case INSPECT:
			return new InspectPhosPepPairCombiner();
		case CRUX:
			return new CruxPhosPepPairCombiner();
		}

		throw new IllegalArgumentException(
		        "Cann't find the combiner for the search engine: "
		                + type.getAlgorithm_name());
	}

}
