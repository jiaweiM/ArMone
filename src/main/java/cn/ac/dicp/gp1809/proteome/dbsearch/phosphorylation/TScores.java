/* 
 ******************************************************************************
 * File: TScores.java * * * Created on 04-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation;

import java.text.DecimalFormat;
import java.util.Locale;

import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * The Tscores after calculation
 * 
 * @author Xinning
 * @version 0.1.1, 06-09-2009, 21:40:33
 */
public class TScores {
	private static DecimalFormat DF ;
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		DF = new DecimalFormat("0.##");
		
		Locale.setDefault(def);
	}

	private char[] sites;
	private int[] locs;
	private double[] scores;
	private int idx = -1;
	private int size;

	public TScores(char[] sites, int[] locs, double[] scores, int size) {
		if (sites.length != size || locs.length != size
		        || scores.length != size) {
			throw new IllegalArgumentException(
			        "The number of sites is illegal");
		}
		this.sites = sites;
		this.locs = locs;
		this.scores = scores;
		this.size = size - 1;
	}

	/**
	 * If there is remaining possible site and localization
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return this.idx < size;
	}

	/**
	 * Roll to next, use {@link #getSite()}, {@link #getScore()},
	 * {@link #getLocalization()} and {@link #getRank()}, to get the
	 * informations.
	 */
	public void next() {
		this.idx++;
	}

	/**
	 * The site (aa) of current rank
	 * 
	 * @return
	 */
	public char getSite() {
		return sites[idx];
	}

	/**
	 * The localization (1- n)
	 * 
	 * @return
	 */
	public int getLocalization() {
		return this.locs[idx];
	}

	/**
	 * Return the current rank
	 * 
	 * @return
	 */
	public int getRank() {
		return idx + 1;
	}

	/**
	 * The tscore of current ranked site localization
	 * 
	 * @return
	 */
	public double getScore() {
		return scores[idx];
	}
	
	public double [] getScores() {
		return scores;
	}

	/**
	 * Number of site localizations and tscores
	 * 
	 * @return
	 */
	public int size() {
		return size + 1;
	}

	/**
	 * Rewind, to get the site localization from the beginning
	 */
	public void rewind() {
		this.idx = -1;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0, n=this.size(); i<n; i++) {
			char site = sites[i];
			int loc = this.locs[i];
			double score = this.scores[i];
			sb.append(site).append(loc)
			        .append(": ").append(DF.format(score))
			        .append("; ");
		}
		return sb.toString();
	}
	
	/**
	 * Parse the formatted TScores String into instance
	 * 
	 * @param tscores
	 * @return
	 */
	public static TScores parseFormattedTScores(String tscores) {
		if (tscores == null)
			return null;

		String ts[] = StringUtil.split(tscores.trim(), ';');
		int len = ts.length;
		char[] sites = new char[len];
		int[] locs = new int[len];
		double[] scores = new double[len];

		for (int i = 0; i < len; i++) {
			String t = ts[i].trim();
			sites[i] = t.charAt(0);
			int idx = t.indexOf(':');
			locs[i] = Integer.parseInt(t.substring(1, idx).trim());
			scores[i] = Double.parseDouble(t.substring(idx + 1).trim());
		}

		return new TScores(sites, locs, scores, len);
	}
}
