/*
 ******************************************************************************
 * File: CruxPhosPairFormat.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.phosphorylation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.AbstractPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.CruxPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ICruxPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.PhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.TScores;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * The Crux PhosPair formatter
 * 
 * @author Xinning
 * @version 0.1, 04-02-2009, 23:08:45
 */
public class CruxPhosPairFormat extends
        AbstractPeptideFormat<ICruxPhosphoPeptidePair> implements
        ICruxPhosPairFormat {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * The decimal format of "0.##"
	 */
	public static final DecimalFormat DF2 ;

	/**
	 * The decimal format of "0.###"
	 */
	public static final DecimalFormat DF3 ;

	/**
	 * The decimal format of "0.####"
	 */
	public static final DecimalFormat DF4 ;

	/**
	 * The decimal format of "0.#####"
	 */
	public static final DecimalFormat DF5 ;
	
	public static final DecimalFormat dfe ;
	
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		DF2 = new DecimalFormat("0.##");
		DF3 = new DecimalFormat("0.###");
		DF4 = new DecimalFormat("0.####");
		DF5 = new DecimalFormat("0.#####");
		
		dfe = new DecimalFormat("0.###E0");
		
		Locale.setDefault(def);
	}
	
	private HashMap<String, Integer> peptideIndexMap;

	// the column number of each entry in peptideArray;

	private int scanColumn = -1;
	private int sequenceColumn = -1;
	private int chargeColumn = -1;
	private int massColumn = -1;
	private int deltaMassColumn = -1;
	private int rankColumn = -1;
	private int proteinColumn = -1;
	private int piColumn = -1;
	private int NTTColumn = -1;

	private int phositeColumn = -1;
	private int neulocColumn = -1;
	private int tscoreColumn = -1;
	private int mzms2Column = -1;
	private int mzms3Column = -1;

	private int ms2scanColumn = -1;
	private int ms3scanColumn = -1;

	private int xcorrsColumn = -1;
	private int deltaCnmColumn = -1;
	private int ms2xcorrColumn = -1;
	private int ms3xcorrColumn = -1;

	private int pvalueColumn = -1;
	private int ms2pvalueColumn = -1;
	private int ms3pvalueColumn = -1;

	private int qvalueColumn = -1;
	private int ms2qvalueColumn = -1;
	private int ms3qvalueColumn = -1;

	private int perScoreColumn = -1;
	private int ms2perScoreColumn = -1;
	private int ms3perScoreColumn = -1;

	/**
	 * A default peptide format
	 * 
	 * @throws IllegalFormaterException
	 */
	public CruxPhosPairFormat() {
		this.peptideIndexMap = new HashMap<String, Integer>();
		// initial peptide index

		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);

		peptideIndexMap.put(XCORR_MERGE, 7);
		peptideIndexMap.put(DELTACAN_MERGE, 8);
		peptideIndexMap.put(PVALUE_MERGE, 9);
		peptideIndexMap.put(QVALUE_MERGE, 10);
		peptideIndexMap.put(PERCOLATORSCORE_MERGE, 11);

		peptideIndexMap.put(PHOS_SITE_NUM, 12);
		peptideIndexMap.put(NEU_LOCATION, 13);
		peptideIndexMap.put(TSCORE, 14);
		peptideIndexMap.put(PROTEINS, 15);
		peptideIndexMap.put(MZ_MS2, 16);
		peptideIndexMap.put(MZ_MS3, 17);

		peptideIndexMap.put(SCAN_MS2, 18);
		peptideIndexMap.put(SCAN_MS3, 19);

		peptideIndexMap.put(XCORR_MS2, 20);
		peptideIndexMap.put(XCORR_MS3, 21);

		peptideIndexMap.put(PVALUE_MS2, 22);
		peptideIndexMap.put(PVALUE_MS3, 23);

		peptideIndexMap.put(QVALUE_MS2, 24);
		peptideIndexMap.put(QVALUE_MS3, 25);

		peptideIndexMap.put(PERCOLATORSCORE_MS2, 26);
		peptideIndexMap.put(PERCOLATORSCORE_MS3, 27);

		peptideIndexMap.put(PI, 28);
		peptideIndexMap.put(NUM_TERMS, 29);
		try {
			this.intitalColumns(peptideIndexMap);// Will not threw
		} catch (IllegalFormaterException e) {
			e.printStackTrace();
		}
	}

	protected CruxPhosPairFormat(HashMap<String, Integer> peptideIndexMap)
	        throws IllegalFormaterException {
		this.peptideIndexMap = peptideIndexMap;
		this.intitalColumns(peptideIndexMap);
	}

	/**
	 * Initial the column index
	 * 
	 * @param map
	 * @throws IllegalFormaterException
	 */
	private void intitalColumns(HashMap<String, Integer> map)
	        throws IllegalFormaterException {
		// the column number of each entry in peptideArray;

		HashSet<Integer> usedColumn = new HashSet<Integer>();

		Integer integer;
		integer = peptideIndexMap.get(SCAN);
		if (integer != null) {
			scanColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + SCAN
			        + " must be included in the formater");

		integer = peptideIndexMap.get(SEQUENCE);
		if (integer == null) {
			integer = peptideIndexMap.get("Peptide");// For old version
		}

		if (integer != null) {
			sequenceColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + SEQUENCE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(CHARGE);
		if (integer == null) {
			integer = peptideIndexMap.get("z");// For old version
		}

		if (integer != null) {
			chargeColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else {
			throw new IllegalFormaterException("The attribute: " + CHARGE
			        + " must be included in the formater");
		}

		integer = peptideIndexMap.get(MH);
		if (integer != null) {
			massColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + MH
			        + " must be included in the formater");

		integer = peptideIndexMap.get(DELTAMH);
		if (integer != null) {
			deltaMassColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + DELTAMH
			        + " must be included in the formater");

		integer = peptideIndexMap.get(RANK);
		if (integer != null) {
			rankColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + RANK
			        + " must be included in the formater");

		integer = peptideIndexMap.get(XCORR_MERGE);

		if (integer != null) {
			this.xcorrsColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + XCORR_MERGE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(DELTACAN_MERGE);
		if (integer != null) {
			this.deltaCnmColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: "
			        + DELTACAN_MERGE + " must be included in the formater");

		integer = peptideIndexMap.get(PVALUE_MERGE);

		if (integer != null) {
			this.pvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PVALUE_MERGE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(QVALUE_MERGE);
		if (integer != null) {
			this.qvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + QVALUE_MERGE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PERCOLATORSCORE_MERGE);

		if (integer != null) {
			this.perScoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: "
			        + PERCOLATORSCORE_MERGE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PROTEINS);
		if (integer != null) {
			proteinColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PROTEINS
			        + " must be included in the formater");

		integer = peptideIndexMap.get(SCAN_MS2);
		if (integer != null) {
			this.ms2scanColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + SCAN_MS2
			        + " must be included in the formater");

		integer = peptideIndexMap.get(SCAN_MS3);
		if (integer != null) {
			this.ms3scanColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + SCAN_MS3
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PHOS_SITE_NUM);
		if (integer != null) {
			this.phositeColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: "
			        + PHOS_SITE_NUM + " must be included in the formater");

		integer = peptideIndexMap.get(NEU_LOCATION);

		if (integer != null) {
			this.neulocColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + NEU_LOCATION
			        + " must be included in the formater");

		integer = peptideIndexMap.get(TSCORE);
		if (integer != null) {
			this.tscoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + TSCORE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(XCORR_MS2);
		if (integer != null) {
			this.ms2xcorrColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + XCORR_MS2
			        + " must be included in the formater");

		integer = peptideIndexMap.get(XCORR_MS3);
		if (integer != null) {
			this.ms3xcorrColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + XCORR_MS3
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PVALUE_MS2);
		if (integer != null) {
			this.ms2pvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PVALUE_MS2
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PVALUE_MS3);
		if (integer != null) {
			this.ms3pvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PVALUE_MS3
			        + " must be included in the formater");

		integer = peptideIndexMap.get(QVALUE_MS2);
		if (integer != null) {
			this.ms2qvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + QVALUE_MS2
			        + " must be included in the formater");

		integer = peptideIndexMap.get(QVALUE_MS3);
		if (integer != null) {
			this.ms3qvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + QVALUE_MS3
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PERCOLATORSCORE_MS2);
		if (integer != null) {
			this.ms2perScoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: "
			        + PERCOLATORSCORE_MS2 + " must be included in the formater");

		integer = peptideIndexMap.get(PERCOLATORSCORE_MS3);
		if (integer != null) {
			this.ms3perScoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: "
			        + PERCOLATORSCORE_MS3 + " must be included in the formater");

		integer = peptideIndexMap.get(MZ_MS2);
		if (integer != null) {
			this.mzms2Column = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + MZ_MS2
			        + " must be included in the formater");

		integer = peptideIndexMap.get(MZ_MS3);
		if (integer != null) {
			this.mzms3Column = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + MZ_MS3
			        + " must be included in the formater");

		integer = peptideIndexMap.get(NUM_TERMS);
		if (integer != null) {
			NTTColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + NUM_TERMS
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PI);
		if (integer != null) {
			piColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PI
			        + " must be included in the formater");
	}

	/**
	 * Parse the format from the title of each attribute in the output file.
	 * These files should also be output by the IPeptideFormat
	 * 
	 * @param title
	 * @return
	 * @throws IllegalFormaterException
	 * @throws NullPointerException
	 *             if the title is null or with length of 0
	 */
	public static CruxPhosPairFormat parseTitle(String title)
	        throws IllegalFormaterException, NullPointerException {

		if (title == null || title.length() == 0)
			throw new NullPointerException(
			        "The formatter can not be created from a null title.");

		HashMap<String, Integer> peptideIndexMap = new HashMap<String, Integer>(
		        16);
		// initial peptide index

		String[] titles = StringUtil.split(title, SEPARATOR);

		for (int i = 0; i < titles.length; i++) {
			String ts = titles[i].trim();
			if (ts.length() > 0)
				peptideIndexMap.put(ts, new Integer(i));
		}

		return new CruxPhosPairFormat(peptideIndexMap);
	}
	
	public static CruxPhosPairFormat parseTitle(String [] titles)
		throws IllegalFormaterException, NullPointerException {

		if (titles == null || titles.length == 0)
			throw new NullPointerException(
	        	"The formatter can not be created from a null title.");

		HashMap<String, Integer> peptideIndexMap = new HashMap<String, Integer>(
				16);
		// initial peptide index

		for (int i = 0; i < titles.length; i++) {
			String ts = titles[i].trim();
			if (ts.length() > 0)
				peptideIndexMap.put(ts, new Integer(i));
		}

		return new CruxPhosPairFormat(peptideIndexMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#format
	 * (cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String format(ICruxPhosphoPeptidePair pep) {

		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = String.valueOf(pep.getCharge());
		strs[this.deltaMassColumn] = DF5.format(pep.getDeltaMH());
		strs[this.massColumn] = DF5.format(pep.getMH());
		strs[this.ms2scanColumn] = String.valueOf(pep.getMS2Scan());
		strs[this.ms3scanColumn] = String.valueOf(pep.getMS3Scan());
		strs[this.mzms2Column] = DF5.format(pep.getMS2MZ());
		strs[this.mzms3Column] = DF5.format(pep.getMS3MZ());
		strs[this.neulocColumn] = String.valueOf(pep.getNeutralLossSite());
		strs[this.NTTColumn] = String.valueOf(pep.getNumberofTerm());
		strs[this.phositeColumn] = String.valueOf(pep.getPhosphoSiteNumber());
		strs[this.piColumn] = DF2.format(pep.getPI());
		strs[this.proteinColumn] = pep.getProteinReferenceString();
		strs[this.rankColumn] = String.valueOf(pep.getRank());
		strs[this.scanColumn] = pep.getScanNum();
		strs[this.sequenceColumn] = pep.getSequence();
		strs[this.tscoreColumn] = pep.getTScores().toString();

		strs[this.deltaCnmColumn] = DF3.format(pep.getDeltaCn());
		strs[this.xcorrsColumn] = DF5.format(pep.getXcorrSum());
		strs[this.ms3xcorrColumn] = DF3.format(pep.getMS3Xcorr());
		strs[this.ms2xcorrColumn] = DF3.format(pep.getMS2Xcorr());

		strs[this.qvalueColumn] = String.valueOf(pep.getQvalueSum());
		strs[this.ms3qvalueColumn] = String.valueOf(pep.getMS3Qvalue());
		strs[this.ms2qvalueColumn] = String.valueOf(pep.getMS2Qvalue());

		strs[this.pvalueColumn] = String.valueOf(pep.getPvalueSum());
		strs[this.ms2pvalueColumn] = String.valueOf(pep.getMS2Pvalue());
		strs[this.ms3pvalueColumn] = String.valueOf(pep.getMS3Pvalue());

		strs[this.perScoreColumn] = String.valueOf(pep.getPercolatorScoreSum());
		strs[this.ms2perScoreColumn] = String.valueOf(pep
		        .getMS2PercolatorScore());
		strs[this.ms3perScoreColumn] = String.valueOf(pep
		        .getMS3PercolatorScore());

		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			if (s != null)
				sb.append(s);

			sb.append(SEPARATOR);
		}

		if(sb.length()>0)
			return sb.substring(0, sb.length()-1);
		else
			return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
	 */
	@Override
	public String[] getTitle() {
		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = CHARGE;
		strs[this.deltaMassColumn] = DELTAMH;
		strs[this.massColumn] = MH;
		strs[this.ms2scanColumn] = SCAN_MS2;
		strs[this.ms3scanColumn] = SCAN_MS3;
		strs[this.neulocColumn] = NEU_LOCATION;
		strs[this.NTTColumn] = NUM_TERMS;
		strs[this.phositeColumn] = PHOS_SITE_NUM;
		strs[this.piColumn] = PI;
		strs[this.proteinColumn] = PROTEINS;
		strs[this.rankColumn] = RANK;
		strs[this.scanColumn] = SCAN;
		strs[this.sequenceColumn] = SEQUENCE;
		strs[this.tscoreColumn] = TSCORE;
		strs[this.mzms2Column] = MZ_MS2;
		strs[this.mzms3Column] = MZ_MS3;

		strs[this.deltaCnmColumn] = DELTACAN_MERGE;
		strs[this.xcorrsColumn] = XCORR_MERGE;
		strs[this.ms3xcorrColumn] = XCORR_MS3;
		strs[this.ms2xcorrColumn] = XCORR_MS2;

		strs[this.qvalueColumn] = QVALUE_MERGE;
		strs[this.ms2qvalueColumn] = QVALUE_MS2;
		strs[this.ms3qvalueColumn] = QVALUE_MS3;

		strs[this.pvalueColumn] = PVALUE_MERGE;
		strs[this.ms2pvalueColumn] = PVALUE_MS2;
		strs[this.ms3pvalueColumn] = PVALUE_MS3;

		strs[this.perScoreColumn] = PERCOLATORSCORE_MERGE;
		strs[this.ms2perScoreColumn] = PERCOLATORSCORE_MS2;
		strs[this.ms3perScoreColumn] = PERCOLATORSCORE_MS3;

		return strs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .IPeptideFormat#parsePeptide(java.lang.String)
	 */
	@Override
	public ICruxPhosphoPeptidePair parse(String pepstring) {

		String[] columns = StringUtil.split(pepstring, SEPARATOR);

		String scanNum = columns[this.scanColumn];
		IPhosphoPeptideSequence sequence = this
		        .checkPeptideSequence(columns[this.sequenceColumn]);
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		//		short phossitenum = Short.parseShort(columns[this.phositeColumn]);
		int neuloc = Integer.parseInt(columns[this.neulocColumn]);
		//		int ms3scan = Integer.parseInt(columns[this.ms3scanColumn]);
		//		int ms2scan = Integer.parseInt(columns[this.ms2scanColumn]);
		TScores tscores = TScores
		        .parseFormattedTScores(columns[this.tscoreColumn]);
		double mz_ms2 = Double.parseDouble(columns[this.mzms2Column]);
		double mz_ms3 = Double.parseDouble(columns[this.mzms3Column]);

		float xcorrs = Float.parseFloat(columns[this.xcorrsColumn]);
		float deltaCn = Float.parseFloat(columns[this.deltaCnmColumn]);
		float xcorrms2 = Float.parseFloat(columns[this.ms2xcorrColumn]);
		float xcorrms3 = Float.parseFloat(columns[this.ms3xcorrColumn]);

		float pvalue = Float.parseFloat(columns[this.pvalueColumn]);
		float pvaluems2 = Float.parseFloat(columns[this.ms2pvalueColumn]);
		float pvaluems3 = Float.parseFloat(columns[this.ms3pvalueColumn]);

		float qvalue = Float.parseFloat(columns[this.qvalueColumn]);
		float qvaluems2 = Float.parseFloat(columns[this.ms2qvalueColumn]);
		float qvaluems3 = Float.parseFloat(columns[this.ms3qvalueColumn]);

		float pervalue = Float.parseFloat(columns[this.perScoreColumn]);
		float pervaluems2 = Float.parseFloat(columns[this.ms2perScoreColumn]);
		float pervaluems3 = Float.parseFloat(columns[this.ms3perScoreColumn]);

		HashSet<ProteinReference> references = new HashSet<ProteinReference>();
		String[] names = StringUtil.split(columns[this.proteinColumn],
		        IPeptide.ProteinNameSpliter);
		for (String name : names)
			references.add(ProteinReference.parse(name));

		return new CruxPhosphoPeptidePair(scanNum, sequence, neuloc, charge,
		        mh, deltamh, mz_ms2, mz_ms3, rank, references, pi, NTT,
		        xcorrms2, xcorrms3, xcorrs, deltaCn, pvaluems2, pvaluems3,
		        pvalue, qvaluems2, qvaluems3, qvalue, pervaluems2, pervaluems3,
		        pervalue, tscores, this);
	}
	
	public ICruxPhosphoPeptidePair parse(String[] columns) {

		String scanNum = columns[this.scanColumn];
		IPhosphoPeptideSequence sequence = this
		        .checkPeptideSequence(columns[this.sequenceColumn]);
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		//		short phossitenum = Short.parseShort(columns[this.phositeColumn]);
		int neuloc = Integer.parseInt(columns[this.neulocColumn]);
		//		int ms3scan = Integer.parseInt(columns[this.ms3scanColumn]);
		//		int ms2scan = Integer.parseInt(columns[this.ms2scanColumn]);
		TScores tscores = TScores
		        .parseFormattedTScores(columns[this.tscoreColumn]);
		double mz_ms2 = Double.parseDouble(columns[this.mzms2Column]);
		double mz_ms3 = Double.parseDouble(columns[this.mzms3Column]);

		float xcorrs = Float.parseFloat(columns[this.xcorrsColumn]);
		float deltaCn = Float.parseFloat(columns[this.deltaCnmColumn]);
		float xcorrms2 = Float.parseFloat(columns[this.ms2xcorrColumn]);
		float xcorrms3 = Float.parseFloat(columns[this.ms3xcorrColumn]);

		float pvalue = Float.parseFloat(columns[this.pvalueColumn]);
		float pvaluems2 = Float.parseFloat(columns[this.ms2pvalueColumn]);
		float pvaluems3 = Float.parseFloat(columns[this.ms3pvalueColumn]);

		float qvalue = Float.parseFloat(columns[this.qvalueColumn]);
		float qvaluems2 = Float.parseFloat(columns[this.ms2qvalueColumn]);
		float qvaluems3 = Float.parseFloat(columns[this.ms3qvalueColumn]);

		float pervalue = Float.parseFloat(columns[this.perScoreColumn]);
		float pervaluems2 = Float.parseFloat(columns[this.ms2perScoreColumn]);
		float pervaluems3 = Float.parseFloat(columns[this.ms3perScoreColumn]);

		HashSet<ProteinReference> references = new HashSet<ProteinReference>();
		String[] names = StringUtil.split(columns[this.proteinColumn],
		        IPeptide.ProteinNameSpliter);
		for (String name : names)
			references.add(ProteinReference.parse(name));

		return new CruxPhosphoPeptidePair(scanNum, sequence, neuloc, charge,
		        mh, deltamh, mz_ms2, mz_ms3, rank, references, pi, NTT,
		        xcorrms2, xcorrms3, xcorrs, deltaCn, pvaluems2, pvaluems3,
		        pvalue, qvaluems2, qvaluems3, qvalue, pervaluems2, pervaluems3,
		        pervalue, tscores, this);
	}

	/*
	 * If there is allene peptide, two sequences will be outputted in
	 * noredundant
	 * 
	 * Current now this is only used for the compatible of old version
	 * noredundant file
	 * 
	 * (currently only in old form noredundant file), only the front one will be
	 * selected. This method is used to refresh this type of sequence.
	 */
	private IPhosphoPeptideSequence checkPeptideSequence(String sequence) {
		String seq;
		int idx = sequence.indexOf('!');
		if (idx != -1) {
			seq = sequence.substring(0, idx);
		} else {
			seq = sequence;
		}

		return PhosphoPeptideSequence.parseSequence(seq,
		        PhosConstants.PHOS_SYMBOL, PhosConstants.NEU_SYMBOL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#type()
	 */
	@Override
	public PeptideType type() {
		return PeptideType.APIVASE_CRUX;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getIndex(java
	 * .lang.String)
	 */
	@Override
	public int getIndex(String term) {
		Integer idx = this.peptideIndexMap.get(term);
		if (idx == null)
			return -1;
		return idx;
	}

	public HashMap <String, Integer> getIndexMap(){
		return peptideIndexMap;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#getSimpleFormatTitle()
	 */
	@Override
	public String getSimpleFormatTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#simpleFormat(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String simpleFormat(ICruxPhosphoPeptidePair peptide) {
		// TODO Auto-generated method stub
		return null;
	}
}
