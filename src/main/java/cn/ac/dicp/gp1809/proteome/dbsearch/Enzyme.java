/*
 * *****************************************************************************
 * File: Enzyme.java * * * Created on 09-01-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;
import cn.ac.dicp.gp1809.proteome.aasequence.IAminoacidSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.IPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Enzyme for protein cleavage.
 * 
 * <p>
 * Changes:
 * <li>0.5.2, 02-20-2009: pull up the parameter for the getting of miss cleavage
 * site and the terminal number
 * <li>0.5.3, 02-26-2009: Modified for new AminoacidModification.
 * <li>0.5.3.1, 03-23-2009: Add noenzyme static instance.
 * <li>0.5.4, 06-13-2009: implemented IDeepCloneable
 * @author Xinning
 * @version 0.5.4, 06-13-2009, 20:28:41
 */
public class Enzyme implements IDeepCloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Not enzyme specific cleavage (all the site may be cleaved).
	 */
	public static final int ENZYMATIC_NO = 0;

	/**
	 * Full enzymatic cleavage: both of the terminal of theoretically generated
	 * peptide is enzyme specific cleavage.
	 */
	public static final int ENZYMATIC_FULL = 1;

	/**
	 * Partial enzymatic cleavage: at least one (one of both) of the terminal of
	 * theoretically generated peptide is enzyme specific cleavage.
	 */
	public static final int ENZYMATIC_PARTIAL = 2;

	/**
	 * Cleave at N-terminal ?? (currently not used, and not cleared).
	 */
	public static final int ENZYMATIC_N_TERM = 3;

	/**
	 * Cleave at C-terminal ?? (currently not used, and not cleared).
	 */
	public static final int ENZYMATIC_C_TERM = 4;

	/**
	 * The default maximum modification number on a single peptide 3.
	 */
	public static final int MAXIMUM_MODIFICATION_DEF = 3;

	/**
	 * A static instance indicating the enzyme of trypsin with the enzymatic
	 * type of full enzymatic.
	 * <p>
	 * Details: Name: Trypsin, cleaveAt: KR, notCleaveAt: P, cleavePosition:
	 * "C".
	 */
	public static final Enzyme TRYPSIN = getEnzyme("Trypsin", true, "KR", "P");

	/**
	 * A static instance indicating the enzyme of none specified enzyme (in
	 * database search)
	 * <p>
	 * Details: Name: noenzyme, cleaveAt: null, notCleaveAt: null
	 */
	public static final Enzyme NOENZYME = getEnzyme("noenzyme", true, null,
	        null);

	/**
	 * Parse the formatted enzyme string into the enzyme instance. The format of
	 * the enzyme string is "name, sense_C (true or false), cleaveAt,
	 * notCleaveAt". e.g.
	 * <p>
	 * "Trypsin, true, KR, P" and "NoEnzyme, true(or false), -, -"
	 * 
	 * @param infos
	 * @return
	 */
	public static final Enzyme parse(String infos) {
		String[] entries = StringUtil.split(infos, ',');

		if (entries.length != 4) {
			throw new IllegalArgumentException(
			        "The input enzyme pattern is illegal: " + infos);
		}

		try {
			return new Enzyme(entries[0].trim(), Boolean
			        .parseBoolean(entries[1].trim()), entries[2].trim(),
			        entries[3].trim());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/*
	 * For the convenient creation of default enzyme
	 */
	private static final Enzyme getEnzyme(String enzymeName, boolean sense_C,
	        String cleaveAt, String notcleaveAt) {
		Enzyme enzyme = null;
		try {
			enzyme = new Enzyme(enzymeName, sense_C, cleaveAt, notcleaveAt);
		} catch (InvalidEnzymeCleavageSiteException e) {
			e.printStackTrace();
		}
		return enzyme;
	}

	/*
	 * The cleavage sites, if null, there is no enzyme selected.
	 */
	private char[] cleaveAts = new char[0];

	/*
	 * If cleavage sites adjacent with this(these) site
	 */
	private char[] notCleaveAts = new char[0];

	/*
	 * if this is true, then enzyme will cleave the peptide at C terminal
	 */
	private boolean sense_C;

	/*
	 * If this is no enzyme
	 */
	private boolean noEnzyme;

	private String name;

	/**
	 * Construct an enzyme. <b>If there is no cleavage site (no enzyme) or no
	 * notCleaveSite, the string should be "-" or null or ""</b>
	 * 
	 * @param enzymeName
	 *            name of the enzyme
	 * @param sense_C
	 *            : if peptide will be cleaved after the cleavage site
	 *            (cleaveAt, in C-terminal) this value should be true,
	 *            otherwise, false.
	 * @param cleaveAt
	 *            : A string for char array on which enzymatic cleavage will be
	 *            occurred. e.g. for trypsin, this should be "KR"
	 * @param notcleaveAt
	 *            : for some enzyme, if this site(s) is adjacent with the
	 *            cleavage site, enzymatic cleavage will not occurred. e.g. for
	 *            trypsin, if ...KP..., then trypsin will not cleave at this K
	 *            site
	 * @throws InvalidEnzymeCleavageSiteException
	 */
	public Enzyme(String enzymeName, boolean sense_C, String cleaveAt,
	        String notcleaveAt) throws InvalidEnzymeCleavageSiteException {

		if (cleaveAt == null || cleaveAt.length() == 0 || cleaveAt.equals("-")) {
			this.name = "No enzyme";
			this.noEnzyme = true;
		} else {
			this.cleaveAts = this.validateSite(cleaveAt);
			this.name = enzymeName;
		}

		if (notcleaveAt != null && notcleaveAt.length() != 0
		        && !notcleaveAt.equals("-"))
			this.notCleaveAts = this.validateSite(notcleaveAt);

		this.sense_C = sense_C;
	}

	// validate the cleavesite or not cleave site
	private char[] validateSite(String site)
	        throws InvalidEnzymeCleavageSiteException {
		String cleaveup = site.toUpperCase();
		char[] chars = cleaveup.toCharArray();
		int count = 0;
		L2: for (char ch : chars) {
			if (ch > 'Z' && ch < 'A')
				throw new InvalidEnzymeCleavageSiteException(
				        "The cleavage (or not cleavage) site '" + ch
				                + "' is not valid.");
			for (int i = 0; i < count; i++) {
				char c = chars[i];
				if (c == ch)// Ignore the duplicated character
					continue L2;
			}

			chars[count++] = ch;
		}

		return Arrays.copyOf(chars, count);
	}

	/**
	 * Cleave protein (or big peptide) into peptides accepting of 0 miss
	 * cleavage site. The returned peptide sequences are disordered.
	 * 
	 * @param seq
	 *            sequence of protein or large peptide
	 * @param enzymaticType
	 *            the type of the enzyme (including full enzymatic(1), partial
	 *            enzymatic(2), N-term cleavage(3), and C-term cleavage(4). If
	 *            no enzyme is selected, this value will be 0) see also Enzyme.
	 * 
	 * @return peptide sequences enzymatic theoretically.
	 */
	public String[] cleave(String seq, int enzymaticType) {
		return this.cleave(seq, 0, enzymaticType);
	}

	/**
	 * Cleave protein (or big peptide) into peptides accepting of specific miss
	 * cleavage site. The returned peptide sequences are disordered.
	 * 
	 * @param seq
	 *            sequence of protein or large peptide
	 * @param missCleaveSite
	 *            number of site accepted for miss cleavage.
	 * @param enzymaticType
	 *            the type of the enzyme (including full enzymatic(1), partial
	 *            enzymatic(2), N-term cleavage(3), and C-term cleavage(4). If
	 *            no enzyme is selected, this value will be 0) see also Enzyme.
	 * @return peptide sequences enzymatic theoretically.
	 */
	public String[] cleave(String seq, int missCleaveSite, int enzymaticType) {
		if (seq == null)
			return null;

		if (enzymaticType == ENZYMATIC_FULL) {
			if (missCleaveSite < 0) {
				System.out
				        .println("Number of miss cleavage site must be not less than 0, current: \""
				                + missCleaveSite + "\", consider as 0 instead!");
				missCleaveSite = 0;
			}

			char[] chars = seq.toCharArray();
			int[] idx = this.getCleaveIdx(chars);
			HashSet<String> set = new HashSet<String>();
			int end = missCleaveSite + 1;
			for (int i = 1; i <= end; i++) {
				for (int j = 0, n = idx.length - i; j < n; j++) {
					int start = idx[j];
					int len = idx[j + i] - start;
					set.add(new String(chars, start, len));
				}
			}

			return set.toArray(new String[set.size()]);
		} else {
			throw new RuntimeException(
			        "Currently, partial enzymatic digestion "
			                + "has not been designed.");
		}
	}
	
	/**
	 * Cleave protein (or big peptide) into peptides accepting of specific miss
	 * cleavage site. The returned peptide sequences are disordered.
	 * 
	 * @param seq
	 *            sequence of protein or large peptide
	 * @param missCleaveSite
	 *            number of site accepted for miss cleavage.
	 * @param minMass
	 *            The minimum mass which is accepted for the final cleaved
	 *            peptides
	 * @param maxMass
	 *            The maximum mass which is accepted for the final cleaved
	 *            peptides
	 * @return peptide sequences enzymatic theoretically.
	 */
	public String[] cleave(String seq, int missCleaveSite,
	        MwCalculator mwcalor, double minMass, double maxMass) {
		if (minMass > maxMass)
			throw new IllegalArgumentException("The mass range [" + minMass
			        + ", " + maxMass + "] is illegal!");

		if (mwcalor == null)
			throw new NullPointerException(
			        "The MwCalculator for in silico digestion is null!");

		String[] seqs = this.cleave(seq, missCleaveSite);
		if (seqs == null)
			return null;

		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < seqs.length; i++) {
			String pep = seqs[i];
			double mw = mwcalor.getMonoIsotopeMh(pep);
			if (mw >= minMass && mw <= maxMass)
				list.add(pep);
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Cleave protein (or big peptide) into peptides accepting of specific miss
	 * cleavage site. The returned peptide sequences are disordered. If the
	 * variableModif is true, then peptide with probable variable modifications
	 * will be considered as different peptides. And the maximum number of
	 * variable modification on a single peptide is 3 by default.
	 * <p>
	 * <b>The AminoacidModification instance in MwCalculator should contain
	 * modification informations for aminoacid, that is, the method
	 * getModifcation(char) must not return null.</b>
	 * <p>
	 * <b>If more than one variable modification can occur on an aminoacid, we
	 * assume that only one of them can occur once a time.</b>
	 * 
	 * @param seq
	 *            sequence of protein or large peptide
	 * @param missCleaveSite
	 *            number of site accepted for miss cleavage.
	 * @param minMass
	 *            The minimum mass which is accepted for the final cleaved
	 *            peptides
	 * @param maxMass
	 *            The maximum mass which is accepted for the final cleaved
	 *            peptides
	 * @param variableModif
	 *            if using variable modification for the in silico digestion. If
	 *            false, this method equals
	 *            {@link #cleave(String, int, MwCalculator, double, double)}
	 *            otherwise, peptide with and without variable modification will
	 *            be consider as two different peptides, <b>e.g. EQPSDDSMSDK and
	 *            EQPSDDSM*SDK</b>
	 * @return peptide sequences enzymatic theoretically.
	 */
	public String[] cleave(String seq, int missCleaveSite,
	        MwCalculator mwcalor, double minMass, double maxMass,
	        boolean variableModif) {
		return this.cleave(seq, missCleaveSite, mwcalor, minMass, maxMass,
		        variableModif, MAXIMUM_MODIFICATION_DEF);
	}

	/**
	 * Cleave protein (or big peptide) into peptides accepting of specific miss
	 * cleavage site. The returned peptide sequences are disordered.If the
	 * variableModif is true, then peptide with probable variable modifications
	 * will be considered as different peptides.
	 * <p>
	 * <b>The AminoacidModification instance in MwCalculator should contain
	 * modification informations for aminoacid, that is, the method
	 * getModifcation(char) must not return null.</b>
	 * <p>
	 * <b>If more than one variable modification can occur on an aminoacid, we
	 * assume that only one of them can occur once a time.</b>
	 * 
	 * @param seq
	 *            sequence of protein or large peptide
	 * @param missCleaveSite
	 *            number of site accepted for miss cleavage.
	 * @param minMass
	 *            The minimum mass which is accepted for the final cleaved
	 *            peptides
	 * @param maxMass
	 *            The maximum mass which is accepted for the final cleaved
	 *            peptides
	 * @param variableModif
	 *            if using variable modification for the in silico digestion. If
	 *            false, this method equals
	 *            {@link #cleave(String, int, MwCalculator, double, double)}
	 *            otherwise, peptide with and without variable modification will
	 *            be consider as two different peptides, <b>e.g. EQPSDDSMSDK and
	 *            EQPSDDSM*SDK</b>
	 * @param maxModif
	 *            the maximum number of variable modifications which can be
	 *            accepted on a single peptide. Default 3
	 *            {@link #cleave(String, int, MwCalculator, double, double, boolean)}
	 *            .
	 * @return peptide sequences enzymatic theoretically.
	 */
	public String[] cleave(String seq, int missCleaveSite,
	        MwCalculator mwcalor, double minMass, double maxMass,
	        boolean variableModif, int maxModif) {
		if (!variableModif)
			return this.cleave(seq, missCleaveSite, mwcalor, minMass, maxMass);

		if (maxModif <= 0) {
			throw new IllegalArgumentException(
			        "The maximum number of modification for a "
			                + "peptide must not be less 1, current: "
			                + maxModif);
		}

		String[] seqs = this.cleave(seq, missCleaveSite);
		if (seqs == null)
			return null;

		AminoacidModification aamodif = mwcalor.getAAModification();
		ModSite[] sites = aamodif.getModifiedSites();
		if (sites == null) {
			throw new NullPointerException(
			        "There is no aminoacid variable modification information"
			                + "containing in AminoacidModification.");
		}

		int len = sites.length;
		ModSite[] modifchar = new ModSite[sites.length];
		for (int i = 0; i < len; i++) {
			modifchar[i] = sites[i];
		}

		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < seqs.length; i++) {
			String pep = seqs[i];
			int tl = pep.length();
			int[] idxs = this.getModifIdx(pep, tl, modifchar);
			if (idxs != null) {
				ArrayList<String> inlist = this.breedModifiedPeptides(pep,
				        idxs, aamodif, maxModif);
				int sz = inlist.size();
				for (int j = 0; j < sz; j++) {
					String tpep = inlist.get(j);
					if (this.withinMassRange(tpep, mwcalor, minMass, maxMass))
						list.add(tpep);
				}
			} else {
				if (this.withinMassRange(pep, mwcalor, minMass, maxMass))
					list.add(pep);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * The start position and end position of these peptides cleaved by enzyme of a protein can be
	 * get by use this method.
	 * 
	 * @param seq
	 *            sequence of protein or large peptide
	 * @param missCleaveSite
	 *            number of site accepted for miss cleavage.
	 * @param enzymaticType
	 *            the type of the enzyme (including full enzymatic(1), partial
	 *            enzymatic(2), N-term cleavage(3), and C-term cleavage(4). If
	 *            no enzyme is selected, this value will be 0) see also Enzyme.
	 * @return a map contain the infomation of peptides' start position and end position,
	 * 			the key = the arrays of start position and end position,
	 * 			the value = peptide string.
	 */
	
	public HashMap <Integer [], String> getCleaveLoca(String seq, int missCleaveSite, int enzymaticType) {
		if (seq == null)
			return null;
	
		if (enzymaticType == ENZYMATIC_FULL) {
			if (missCleaveSite < 0) {
				System.out
				        .println("Number of miss cleavage site must be not less than 0, current: \""
				                + missCleaveSite + "\", consider as 0 instead!");
				missCleaveSite = 0;
			}
	
			char[] chars = seq.toCharArray();
			int[] idx = this.getCleaveIdx(chars);
			
			HashMap <Integer [], String> pepMap = new HashMap <Integer [], String> ();
			int end = missCleaveSite+1;
			
			for (int i = 1; i <= end; i++) {
				for (int j = 0, n = idx.length - i; j < n; j++) {
					int startAA = idx[j];
					int endAA = idx[j+i];
					int len = endAA-startAA;
					String pep = new String (chars, startAA, len);
					pepMap.put(new Integer[] {startAA,endAA} , pep);
					
				}
			}
			
			return pepMap;
		} else {
			throw new RuntimeException(
			        "Currently, partial enzymatic digestion "
			                + "has not been designed.");
		}
	}

	private boolean withinMassRange(String pep, MwCalculator mwcalor,
	        double minMass, double maxMass) {
		double mw = mwcalor.getMonoIsotopeMh(pep);
		if (mw >= minMass && mw <= maxMass)
			return true;
		return false;
	}

	/*
	 * Generated probably modified peptides in silico.
	 */
	private ArrayList<String> breedModifiedPeptides(String pep, int[] idxs,
	        AminoacidModification aamodif, int maxModif) {

		ArrayList<String> inlist = new ArrayList<String>();
		// The list corresponding to the inlist, in which the number is the
		// current modification number for the corresponding peptide
		ArrayList<Integer> modiflist = new ArrayList<Integer>();
		inlist.add(pep);
		modiflist.add(0);

		for (int idi = idxs.length - 1; idi >= 0; idi--) {
			int idx = idxs[idi];
			int size = inlist.size();

			for (int j = 0; j < size; j++) {
				int modifnum = modiflist.get(j);
				if (modifnum >= maxModif)// Reach the maximum modification
					continue;

				modifnum++;
				String p1 = inlist.get(j);
				char aa = p1.charAt(idx);
				HashSet<Modif> syms = aamodif.getModifSymbols(aa);
				StringBuilder tsb = new StringBuilder(p1);
				int sn = syms.size();
				if (sn == 1) {
					char c = syms.iterator().next().getSymbol();
					tsb.insert(idx + 1, c);
					inlist.add(tsb.toString());
					modiflist.add(modifnum);
				} else {
					int crti = idx + 1;
					tsb.insert(crti, ' ');
					for (Iterator<Modif> si = syms.iterator(); si.hasNext();) {
						tsb.setCharAt(crti, si.next().getSymbol());
						inlist.add(tsb.toString());
						modiflist.add(modifnum);
					}
				}
			}
		}

		return inlist;
	}

	/*
	 * The index of aa which may be modified.
	 */
	private int[] getModifIdx(String pep, int len, ModSite[] sites) {

		if (true)
			throw new RuntimeException("Has not been designed.");

		int size = sites.length << 1;
		int[] idxs = new int[size];
		int curtidx = 0;
		for (int i = 0; i < len; i++) {
			char c = pep.charAt(i);
			for (ModSite site : sites) {

				if (true) {
					if (curtidx >= size) {
						size <<= 1;// *2
						int[] arr1 = new int[size];
						System.arraycopy(idxs, 0, arr1, 0, idxs.length);
						idxs = arr1;
					}
					idxs[curtidx++] = i;
					break;
				}
			}
		}
		if (curtidx == 0)
			return null;

		return Arrays.copyOfRange(idxs, 0, curtidx);
	}

	/*
	 * The index of aa which may be modified.
	 */
	private int[] getModifIdx(String pep, int len, char[] modifchar) {
		int size = modifchar.length << 1;
		int[] idxs = new int[size];
		int curtidx = 0;
		for (int i = 0; i < len; i++) {
			char c = pep.charAt(i);
			for (char ma : modifchar) {
				if (ma == c) {
					if (curtidx >= size) {
						size <<= 1;// *2
						int[] arr1 = new int[size];
						System.arraycopy(idxs, 0, arr1, 0, idxs.length);
						idxs = arr1;
					}
					idxs[curtidx++] = i;
					break;
				}
			}
		}
		if (curtidx == 0)
			return null;

		return Arrays.copyOfRange(idxs, 0, curtidx);
	}

	/**
	 * Get the cleavage indexes for the specific aminoacid sequences using this
	 * enzyme. The start one idx[0] will always be 0, indicating the start point
	 * of the sequence. Therefore, the idx[1] should be the first cleavage
	 * point.
	 * 
	 * <p>
	 * <b>If the enzyme is ENZYME_NO, the returned array will be a ordered array
	 * from 0 to the length of the aminoacid sequence because all the site can
	 * be cleaved.</b>
	 * 
	 * <p>
	 * <b>It should be noted that, the point in the returned index array does
	 * not always equal to the position site; only when the offset while
	 * digesting is before, these two values are the same. E.g. -MQYEDSASKM,
	 * using trypsin, the point in index array is 9, while the index of
	 * enzymatic site 'K' is 8 as the enzymatic type for trypsin is after the
	 * amino acid.</b>
	 * 
	 * @param seq
	 *            the char array of the aminoacid sequence.
	 * @return the integer array of the cleavage indexes. <b>If sequence is null
	 *         or the length of sequence is 0, Null will be returned.</b>
	 */
	public int[] getCleveIdxs(String sequence) {
		if (sequence == null)
			return null;
		return this.getCleaveIdx(sequence.toCharArray());
	}

	/**
	 * Get the cleavage indexes for the specific aminoacid sequences using this
	 * enzyme. The start one idx[0] will always be 0, indicating the start point
	 * of the sequence. Therefore, the idx[1] should be the first cleavage
	 * point.
	 * 
	 * <p>
	 * <b>Notice: if the enzyme is ENZYME_NO, the returned array will be a
	 * ordered array from 0 to the length of the aminoacid sequence because all
	 * the site can be cleaved.</b>
	 * 
	 * <p>
	 * <b>It should be noted that, the point in the returned index array does
	 * not always equal to the position site; only when the offset while
	 * digesting is before, these two values are the same. E.g. -MQYEDSASKM,
	 * using trypsin, the point in index array is 9, while the index of
	 * enzymatic site 'K' is 8 as the enzymatic type for trypsin is after the
	 * amino acid.</b>
	 * 
	 * 
	 * @param seq
	 *            the char array of the aminoacid sequence.
	 * @return the integer array of the cleavage indexes. <b>If sequence is null
	 *         or the length of sequence is 0, Null will be returned.</b>
	 */
	public int[] getCleaveIdx(char[] sequence) {
		if (sequence == null)
			return null;
		int seqlen = sequence.length;
		if (seqlen == 0)
			return null;

		if (this.noEnzyme) {
			int[] idxarr = new int[seqlen + 1];
			for (int i = 0; i <= seqlen; i++) {
				idxarr[i] = i;
			}
			return idxarr;
		}

		/*
		 * We assumed that peptide after digestion with average length of 8;
		 */
		int arrlen = seqlen / 8 + 2;
		int[] idxarr = new int[arrlen];
		int curtidx = 1; // the first index is 0;

		int len = this.cleaveAts.length;
		/*
		 * If offset is after, then it can started from 0. Otherwise, the start
		 * can be 1 because the n terminal is always in the index list
		 */
		int st = this.sense_C ? 0 : 1;
		L1: for (int n = sequence.length - 1; st < n; st++) {
			char c = sequence[st];
			for (int j = 0; j < len; j++) {
				if (c == this.cleaveAts[j]) {

					if (this.notCleaveAts != null) {
						// i+1 will not overflow
						char c2 = sequence[st + 1];
						for (int k = 0; k < this.notCleaveAts.length; k++) {
							if (c2 == this.notCleaveAts[k])// need not to
								// cleave
								continue L1;
						}
					}

					int cleaveidx = st;
					if (this.sense_C)
						cleaveidx++;

					if (curtidx >= arrlen) {
						arrlen += arrlen >> 1;// *1.5
						int[] arr1 = new int[arrlen];
						System.arraycopy(idxarr, 0, arr1, 0, idxarr.length);
						idxarr = arr1;
					}

					idxarr[curtidx++] = cleaveidx;
					break;
				}
			}
		}

		if (curtidx >= arrlen) {
			arrlen++;
			int[] arr1 = new int[arrlen];
			System.arraycopy(idxarr, 0, arr1, 0, idxarr.length);
			idxarr = arr1;
		}
		idxarr[curtidx++] = sequence.length;

		int[] rearr = new int[curtidx];
		System.arraycopy(idxarr, 0, rearr, 0, curtidx);
		return rearr;
	}

	/**
	 * @return the name of this enzyme;
	 */
	public String getEnzymeName() {
		return this.name;
	}

	/**
	 * The site for enzymatic cleavage. if no enzyme is used, return null;
	 * return sites for enzymatic cleavage. e.g. KR
	 */
	public String getCleaveSites() {
		if (this.cleaveAts == null)
			return null;

		return new String(this.cleaveAts);
	}

	/**
	 * The site for enzymatic cleavage which not cleaveat if this sites occurs
	 * after the cleave site. If there their is no notcleave, return null, e.g.
	 * P
	 */
	public String getNotCleaveSites() {
		if (this.notCleaveAts == null)
			return null;

		return new String(this.notCleaveAts);
	}

	/**
	 * @return the sence of enzymatic cleavage. e.g. Trypsin cleavage at the c
	 *         terminal of K and R, the retuned value will be C.
	 */
	public char getSence() {
		if (this.sense_C)
			return 'C';
		else
			return 'N';
	}

	/**
	 * @return the number of enzymatic sites of the terminal aminoacids. (NTT
	 *         for trypsin)
	 */
	public short getNumberofTerm(IPeptideSequence pepseq) {
		return this.getNumberofTerm(pepseq.getPreviousAA(), pepseq.getFistAA(),
		        pepseq.getLastAA(), pepseq.getNextAA());
	}

	/**
	 * @param pep_XXX_aa
	 *            : the previous or next aminoacid of this peptide, the fist and
	 *            last aa; e.g. peptide "K.MDERYTK.S", these four aminoacid is
	 *            'K', 'M', 'K' and 'S' respectively.
	 *            <p>
	 *            <b>If there is no aminoaicid in the next or previous position
	 *            (end of the protein sequence), the char should be '-'</b>
	 * @return the NTT for peptide (0 for no enzymatic, 1 for partial and 2 for
	 *         full enzymatic)
	 */
	public short getNumberofTerm(char pep_prev_aa, char pep_first_aa,
	        char pep_last_aa, char pep_next_aa) {
		if (this.noEnzyme)
			return 0;

		short ntt = 0;

		if (this.sense_C) {
			if (pep_prev_aa == '-')
				ntt++;
			else
				for (char site : this.cleaveAts) {
					if (pep_prev_aa == site) {
						ntt++;
						break;
					}
				}

			if (pep_next_aa == '-')
				ntt++;
			else
				for (char site : this.cleaveAts) {
					if (pep_last_aa == site) {
						ntt++;
						break;
					}
				}
		} else {
			if (pep_next_aa == '-')
				ntt++;
			else
				for (char site : this.cleaveAts) {
					if (pep_next_aa == site) {
						ntt++;
						break;
					}
				}
			if (pep_prev_aa == '-')
				ntt++;
			else
				for (char site : this.cleaveAts) {
					if (pep_first_aa == site) {
						ntt++;
						break;
					}
				}
		}

		return ntt;
	}

	/**
	 * Get the number of miss cleavage site in this peptide sequence.
	 * 
	 * @param pseq
	 * @return
	 */
	public short getMissCleaveNum(IAminoacidSequence pseq) {
		return this.getMissCleaveNum(pseq.getUniqueSequence());
	}

	/**
	 * Get the number of miss cleavage site in this peptide sequence.
	 * 
	 * @return the number of miss cleave sites.
	 */
	public short getMissCleaveNum(String uniseq) {
		if (this.noEnzyme)
			return 0;

		short miss = 0;
		int len = this.cleaveAts.length;
		int st = this.sense_C ? 0 : 1;
		L1: for (int n = uniseq.length() - 1; st < n; st++) {
			char c = uniseq.charAt(st);
			for (int j = 0; j < len; j++) {
				if (c == cleaveAts[j]) {

					if (notCleaveAts != null) {
						// i+1 will not overflow
						char c2 = uniseq.charAt(st + 1);
						for (int k = 0; k < notCleaveAts.length; k++) {
							if (c2 == notCleaveAts[k])// need not to cleave
								continue L1;
						}
					}

					miss++;
					break;
				}
			}
		}

		return miss;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: \"").append(this.name).append("\", cleaveAt: \"")
		        .append(
		                this.cleaveAts.length == 0 ? "-"
		                        : new String(cleaveAts)).append(
		                "\", notCleaveAt: \"").append(
		                notCleaveAts.length == 0 ? "-" : new String(
		                        notCleaveAts)).append("\", sence: \"").append(
		                sense_C ? 'C' : 'N').append("\".");

		return sb.toString();
	}

	/**
	 * Deep clone
	 */
	public Enzyme deepClone() {
		Enzyme copy = this.clone();
		copy.cleaveAts = this.cleaveAts.clone();
		copy.notCleaveAts = this.notCleaveAts.clone();

		return copy;
	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
	public Enzyme clone() {
		try {
	        return (Enzyme) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}

	public static void main(String[] args)
	        throws InvalidEnzymeCleavageSiteException, IOException {
		
		Enzyme enzyme = new Enzyme("Trypsin", true, "K", "P");
		
		MwCalculator mwcalor = new MwCalculator();
/*
		System.out.println(enzyme.getMissCleaveNum("SDFGKSDFDRPSDFSDR"));

		if (true)
			return;

		
		Aminoacids aacids = new Aminoacids();
		aacids.setCysCarboxyamidomethylation();
		mwcalor.setAacids(aacids);
		AminoacidModification aamodif = new AminoacidModification();
		//		aamodif.setModification('M', '*', 16d);
		mwcalor.setAamodif(aamodif);

		String[] str = enzyme.cleave("MMMMMMMKPKE", Enzyme.ENZYMATIC_FULL);

		for (String st : str) {
			System.out.println(st);
		}
*/
		long beg = System.currentTimeMillis();
		
		FastaReader reader = new FastaReader(
		        "E:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta");
		ProteinSequence pseq = null;
		
		PrintWriter pw = new PrintWriter(new FileWriter("E:\\ipi_human_3.80_pep.txt"));
		
//		HashSet<String> set = new HashSet<String>();
		int count = 0;
		int countE = 0;
		int countR = 0;
		int pro = 0;
		// double count = 0;
		while ((pseq = reader.nextSequence()) != null) {
//			String[] peps = enzyme.cleave(pseq.getUniqueSequence(), 2, mwcalor,
//			        600d, 3500d, false);

			pro++;
			String[] peps = enzyme.cleave(pseq.getUniqueSequence(), 0, 1);
			/*
			 * if(pseq.getReference().startsWith("IPI:IPI00179357.1")){
			 * System.out.println(mwcalor.getMonoIsotopeMw(pseq.getSequence()));
			 * count = peps.length; System.out.println(count); }
			 */

			for (int i = 0; i < peps.length; i++) {
				if(peps[i].indexOf("E")>=0)
					countE++;
				if(peps[i].indexOf("R")>=0)
					countR++;
				count++;
				
				pw.println(peps[i]);
			}
		}

		System.out.println(countE+"\t"+(double)countE/count);
		System.out.println(countR+"\t"+(double)countR/count);
		System.out.println(count);
		System.out.println(pro);
		
		pw.close();
		
		long end = System.currentTimeMillis();
		
		System.out.println((end-beg)/1000.0+"\tm");

	}
}
