/* 
 ******************************************************************************
 * File: QueryResult.java * * * Created on 11-14-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.util.Arrays;

/**
 * The results (peptide hits) for a mascot query.
 * 
 * @version 1.00
 * @author Xinning
 * @version 0.1, 11-14-2008, 21:29:21
 */
public class QueryResult {

	private String title;
	private short charge;
	private PeptideHit[] hits;

	public QueryResult() {}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the charge
	 */
	public final short getCharge() {
		return charge;
	}

	/**
	 * @param charge the charge to set
	 */
	public final void setCharge(short charge) {
		this.charge = charge;
	}

	/**
	 * @return the hits
	 */
	public final PeptideHit[] getHits() {
		return hits;
	}

	/**
	 * @param hits the hits to set
	 */
	public final void setHits(PeptideHit[] hits) {
		for (int i = 0; i < hits.length - 1; i++) {
			// float deltaS =
			// (hits[i].getIonsScore()-hits[i+1].getIonsScore())/hits[i].getIonsScore();
			float deltaS = hits[i].getIonsScore() - hits[i + 1].getIonsScore();
			hits[i].setDeltaS(deltaS);
		}
		hits[hits.length - 1].setDeltaS(0);
		this.hits = hits;
	}

	/**
	 * There will be Min(topN, Totoal_hit_numer) peptides returne
	 * 
	 * @return the hits
	 */
	public final PeptideHit[] getHits(int topN) {
		if (topN < 1)
			throw new IllegalArgumentException(
					"The selected topN for peptide hit reproting should big than 0. Current " + topN);

		if (topN >= this.hits.length)
			return hits;
		else
			return Arrays.copyOfRange(this.hits, 0, topN);
	}
}
