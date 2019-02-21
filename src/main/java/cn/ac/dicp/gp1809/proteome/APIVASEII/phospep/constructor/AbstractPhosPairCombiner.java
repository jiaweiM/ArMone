/* 
 ******************************************************************************
 * File: AbstractPhosPairCombiner.java * * * Created on 03-04-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.phospep.constructor;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation.IPhosPairFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;

/**
 * The abstract phospair combiner
 * 
 * @author Xinning
 * @version 0.1, 03-04-2009, 21:04:45
 */
abstract class AbstractPhosPairCombiner<T extends IPhosPeptidePair, F extends IPeptide>
        implements IPhosPepPairCombiner<T, F> {

	private IPhosPairFormat formatter;

	protected AbstractPhosPairCombiner(IPhosPairFormat formatter) {
		this.formatter = formatter;
	}

	/**
	 * Compute the actual mz value of the precursor ion
	 * 
	 * @param mh_ms2
	 * @param deltaMH_ms2
	 * @return
	 */
	protected final double getMZ(double mh, double deltamh, short charge) {
		return SpectrumUtil.getMZ(mh + deltamh, charge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.APIVASEII.phospep.constructor.IPhosPepPairCombiner#
	 * getPeptideFormatter()
	 */
	@Override
	public IPhosPairFormat getPeptideFormatter() {
		return this.formatter;
	}
}
