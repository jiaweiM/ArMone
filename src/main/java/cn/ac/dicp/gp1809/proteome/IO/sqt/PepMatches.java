/* 
 ******************************************************************************
 * File: PepMatches.java * * * Created on 04-01-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.util.Arrays;

import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * The matches and spectrum information for a spectrum
 * 
 * @author Xinning
 * @version 0.1, 04-01-2009, 09:08:12
 */
public class PepMatches implements IPepMatches {

	private String lineSeparator = IOConstant.lineSeparator;

	private IPepMatch[] pepmatches;
	private ISpectrumInfo info;

	/**
	 * @param pepmatches
	 * @param info
	 */
	protected PepMatches(IPepMatch[] pepmatches, ISpectrumInfo info) {
		this.pepmatches = pepmatches;
		this.info = info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatches#getPepMatches()
	 */
	@Override
	public IPepMatch[] getPepMatches() {
		return this.pepmatches;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatches#getPepMatches(int)
	 */
	@Override
	public IPepMatch[] getPepMatches(int topN) {
		if (this.pepmatches == null)
			return null;

		if (topN >= this.pepmatches.length)
			return this.pepmatches;

		return Arrays.copyOf(this.pepmatches, topN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatches#getSpectrumInfo()
	 */
	@Override
	public ISpectrumInfo getSpectrumInfo() {
		return this.info;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(info.toString());

		for (IPepMatch match : this.pepmatches) {
			sb.append(lineSeparator).append(match);
		}

		return sb.toString();
	}
}
