/* 
 ******************************************************************************
 * File: DefaultReferenceDetailFormat.java * * * Created on 08-28-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.ReferenceDetail;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Formatter for Reference detail
 * 
 * @author Xinning
 * @version 0.1.1, 04-10-2009, 19:03:39
 */
public class DefaultReferenceDetailFormat implements IReferenceDetailFormat {

	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/*
	 * Format number to ##.## format for pi value fromating
	 */
	public static final DecimalFormat PIDF ;

	public static final DecimalFormat COVERDF ;

	public static final DecimalFormat DF3 ;

	public static final DecimalFormat DF4 ;
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		PIDF = new DecimalFormat("#0.##");
		COVERDF = new DecimalFormat("#0.##%");
		DF3 = new DecimalFormat("0.###");
		DF4 = new DecimalFormat("0.####");
		
		Locale.setDefault(def);
	}
	
	
	private HashMap<String, Integer> refIndexMap;

	/**
	 * Number of columns. It should be noted that this value may not equal to
	 * the number of elements in the peptideIndexMap. And the other columns
	 * which have not been assigned with a name will be filled with the
	 * separator.
	 */
	private int num_columns;

	// the column number of each entry in peptideArray;

	private int referenceColumn = -1;
	private int pepCountColumn = -1;
	private int unipepCountColumn = -1;
	private int coverageColumn = -1;
	private int probColumn = -1;
	private int pIColumn = -1;
	private int mwColumn = -1;
	private int lengthColumn = -1;
	private int targetColumn = -1;
	private int groupidxColumn = -1;
	private int crossProColumn = -1;
	private int SInColumn = -1;
	private int HydroScoreColumn = -1;

	/**
	 * A default reference detail format
	 * 
	 * @throws IllegalFormaterException
	 */
	public DefaultReferenceDetailFormat() {
		this.refIndexMap = new HashMap<String, Integer>(16);
		// initial reference index

		refIndexMap.put(REFERENCE, 1);
		refIndexMap.put(PEPCOUNT, 2);
		refIndexMap.put(UNIQUEPEPCOUNT, 3);
		refIndexMap.put(COVERPERCENT, 4);
		refIndexMap.put(PROBABILITY, 5);
		refIndexMap.put(MW, 6);
		refIndexMap.put(PI, 7);
		refIndexMap.put(LENGTH, 8);

		refIndexMap.put(GROUPIDX, 9);
		refIndexMap.put(CROSSPROCOUNT, 10);

		refIndexMap.put(ISTARGET, 11);
		refIndexMap.put(SIn, 12);
		refIndexMap.put(HydroScore, 13);

		try {
			this.intitalColumns(refIndexMap);// Will not threw
		} catch (IllegalFormaterException e) {
			e.printStackTrace();
		}
	}

	protected DefaultReferenceDetailFormat(HashMap<String, Integer> refIndexMap)
	        throws IllegalFormaterException {
		this.refIndexMap = refIndexMap;
		this.intitalColumns(refIndexMap);
	}

	private void intitalColumns(HashMap<String, Integer> map)
	        throws IllegalFormaterException {
		// the column number of each entry in peptideArray;

		HashSet<Integer> usedColumn = new HashSet<Integer>();

		Integer integer;
		integer = refIndexMap.get(REFERENCE);
		if (integer != null) {
			referenceColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + REFERENCE
			        + " must be included in the formater");

		integer = refIndexMap.get(PEPCOUNT);

		if (integer != null) {
			pepCountColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PEPCOUNT
			        + " must be included in the formater");

		integer = refIndexMap.get(UNIQUEPEPCOUNT);

		if (integer != null) {
			unipepCountColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else {
			throw new IllegalFormaterException("The attribute: "
			        + UNIQUEPEPCOUNT + " must be included in the formater");
		}
		
		integer = refIndexMap.get(COVERPERCENT);
		if (integer != null) {
			this.coverageColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + COVERPERCENT
			        + " must be included in the formater");
		

		integer = refIndexMap.get(PROBABILITY);
		if (integer != null) {
			this.probColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PROBABILITY
			        + " must be included in the formater");

		integer = refIndexMap.get(PI);
		if (integer == null) {
			integer = refIndexMap.get("PI");// For old version
		}

		if (integer != null) {
			pIColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PI
			        + " must be included in the formater");

		integer = refIndexMap.get(MW);
		if (integer != null) {
			mwColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + MW
			        + " must be included in the formater");

		integer = refIndexMap.get(LENGTH);
		if (integer != null) {
			this.lengthColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + LENGTH
			        + " must be included in the formater");

		integer = refIndexMap.get(ISTARGET);
		if (integer != null) {
			this.targetColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + ISTARGET
			        + " must be included in the formater");

		integer = refIndexMap.get(GROUPIDX);
		if (integer != null) {
			this.groupidxColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}

		integer = refIndexMap.get(CROSSPROCOUNT);
		if (integer != null) {
			this.crossProColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = refIndexMap.get(SIn);
		if (integer != null) {
			SInColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + SIn
			        + " must be included in the formater");
		
		integer = refIndexMap.get(HydroScore);
		if (integer != null) {
			HydroScoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} 
		
	}

	/*
	 * Validate whether the current column has been used by other attribute
	 */
	private void validateColumns(HashSet<Integer> columns, Integer integer)
	        throws IllegalFormaterException {

		if (integer < 1) {
			throw new IllegalFormaterException(
			        "The column index value should be within [1 - n].");
		}

		if (columns.contains(integer))
			throw new IllegalFormaterException(
			        "One column has been assigned to different attributes.");

		int value = integer.intValue();
		if (this.num_columns <= value)
			this.num_columns = value + 1;

		columns.add(integer);
	}

	/**
	 * Parse the format from the title of each attribute in the output file.
	 * These files should also be output by the IReferenceDetailFormat
	 * 
	 * @param title
	 * @return
	 * @throws IllegalFormaterException
	 * @throws NullPointerException
	 *             if the title is null or with length of 0
	 */
	public static DefaultReferenceDetailFormat parseTitle(String title)
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

		return new DefaultReferenceDetailFormat(peptideIndexMap);
	}
	
	public static DefaultReferenceDetailFormat parseTitle(String [] titles)
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

		return new DefaultReferenceDetailFormat(peptideIndexMap);
	}

	/**
	 * For the convenience of Peptide instance
	 * 
	 * The default peptide formatter for Peptide instance. It should be noted
	 * that, if the the instance of IPeptide is also an instance of Peptide,
	 * this method return the same string as format(IPeptide)
	 * 
	 * @param pep
	 * @return
	 */
	public String format(IReferenceDetail refdetail) {

		String[] strs = new String[this.num_columns];
		strs[this.unipepCountColumn] = String.valueOf(refdetail
		        .getPeptideCount());
		strs[this.mwColumn] = DF3.format(refdetail.getMW());
		strs[this.coverageColumn] = COVERDF.format(refdetail.getCoverage());
		strs[this.referenceColumn] = refdetail.getName();
		strs[this.pepCountColumn] = String
		        .valueOf(refdetail.getSpectrumCount());
		strs[this.pIColumn] = PIDF.format(refdetail.getPI());
		strs[this.targetColumn] = String.valueOf(refdetail.isTarget() ? 1 : -1);
		strs[this.lengthColumn] = String.valueOf(refdetail.getNumAminoacids());
		strs[this.SInColumn] = String.valueOf(refdetail.getSIn());
		strs[this.HydroScoreColumn] = String.valueOf(refdetail.getHyporScore());

		if (this.probColumn >= 0) {
			float prob = refdetail.getProbability();
			if (prob > -0.00001f)
				strs[this.probColumn] = DF4.format(prob);
		}

		if (this.crossProColumn >= 0) {
			strs[this.crossProColumn] = String.valueOf(refdetail
			        .getCrossProtein());
		}

		if (this.groupidxColumn >= 0)
			strs[this.groupidxColumn] = String.valueOf(refdetail
			        .getGroupIndex());

		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			if (s != null)
				sb.append(s);

			sb.append(SEPARATOR);
		}

		return sb.substring(0, sb.length()-1);
	}

	public String format1(IReferenceDetail refdetail) {

		String[] strs = new String[this.num_columns];
		strs[this.unipepCountColumn] = String.valueOf(refdetail
		        .getPeptideCount());
		strs[this.mwColumn] = DF3.format(refdetail.getMW());
		strs[this.coverageColumn] = COVERDF.format(refdetail.getCoverage());
		strs[this.referenceColumn] = refdetail.getName();
		strs[this.pepCountColumn] = String
		        .valueOf(refdetail.getSpectrumCount());
		strs[this.pIColumn] = PIDF.format(refdetail.getPI());
		strs[this.targetColumn] = String.valueOf(refdetail.isTarget() ? 1 : -1);
		strs[this.lengthColumn] = String.valueOf(refdetail.getNumAminoacids());
		strs[this.SInColumn] = String.valueOf(refdetail.getSIn());
		strs[this.HydroScoreColumn] = String.valueOf(refdetail.getHyporScore());

		if (this.probColumn >= 0) {
			float prob = refdetail.getProbability();
			if (prob > -0.00001f)
				strs[this.probColumn] = DF4.format(prob);
		}

		if (this.crossProColumn >= 0) {
			strs[this.crossProColumn] = String.valueOf(refdetail
			        .getCrossProtein());
		}

		if (this.groupidxColumn >= 0)
			strs[this.groupidxColumn] = String.valueOf(refdetail
			        .getGroupIndex());

		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<strs.length;i++){
			if(strs[i]!=null){
				if(i==HydroScoreColumn){
					sb.append("(").append(strs[i]).append(")");
				}else{
					sb.append(strs[i]);
				}
			}
			sb.append(SEPARATOR);
		}

		return sb.substring(0, sb.length()-1);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
	 */
	public String[] getTitle() {
		String[] strs = new String[this.num_columns];
		strs[this.unipepCountColumn] = UNIQUEPEPCOUNT;
		strs[this.mwColumn] = MW;
		strs[this.coverageColumn] = COVERPERCENT;
		strs[this.probColumn] = PROBABILITY;
		strs[this.pIColumn] = PI;
		strs[this.referenceColumn] = REFERENCE;
		strs[this.pepCountColumn] = PEPCOUNT;
		strs[this.targetColumn] = ISTARGET;
		strs[this.lengthColumn] = LENGTH;
		strs[this.SInColumn] = SIn;
		strs[this.HydroScoreColumn] = HydroScore;

		if (this.crossProColumn >= 0)
			strs[this.crossProColumn] = CROSSPROCOUNT;

		if (this.groupidxColumn >= 0)
			strs[this.groupidxColumn] = GROUPIDX;

		return strs;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
	 */
	@Override
	public String getTitleString() {
		String[] strs = this.getTitle();

		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			if (s != null)
				sb.append(s);

			sb.append(SEPARATOR);
		}

		return sb.substring(0, sb.length()-1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#parse(java.lang.String)
	 */
	@Override
	public IReferenceDetail parse(String pepstring) {

		String[] columns = StringUtil.split(pepstring, SEPARATOR);

		String name = columns[this.referenceColumn];
		int pepcount = Integer.parseInt(columns[this.pepCountColumn]);

		float coverage = 0f;
		try {
			coverage = COVERDF.parse(columns[this.coverageColumn]).floatValue();
		} catch (ParseException e) {
			System.err.println("Error while parsing the coverage: "
			        + columns[this.coverageColumn] + ", set as 0.");
		}

		int upepcount = Integer.parseInt(columns[this.unipepCountColumn]);
		float pi = Float.parseFloat(columns[this.pIColumn]);
		double mw = Double.parseDouble(columns[this.mwColumn]);
		int length = Integer.parseInt(columns[this.lengthColumn]);
		double SIn = Double.parseDouble(columns[this.SInColumn]);
		
		boolean isTarget = true;
		int value = Integer.parseInt(columns[this.targetColumn]);
		if (value == 1)
			;
		else if (value == -1)
			isTarget = false;
		else
			throw new IllegalArgumentException(
			        "The value indicating Target/Decoy must be 1 or -1, current: "
			                + value);

		float prob = -1f;
		String s = columns[this.probColumn];
		if (s.length() > 0)
			prob = Float.parseFloat(s);

		int groupIdx = -1;
		s = columns[this.groupidxColumn];
		if (s.length() > 0)
			groupIdx = Integer.parseInt(s);

		int crossPro = -1;
		s = columns[this.crossProColumn];
		if (s.length() > 0)
			crossPro = Integer.parseInt(s);

		double hydroScore = -1;
		s = columns[this.HydroScoreColumn];
		if(s.length()>0){
			if(s.charAt(0)=='('){
				s = s.substring(1, s.length()-1);
			}
		}
		hydroScore = Double.parseDouble(s);
		
		return new ReferenceDetail(name, pepcount, upepcount, coverage, prob,
		        pi, mw, length, isTarget, groupIdx, crossPro, SIn, hydroScore);
	}
	
	public IReferenceDetail parse(String[] columns) {

		String name = columns[this.referenceColumn];
		int pepcount = Integer.parseInt(columns[this.pepCountColumn]);

		float coverage = 0f;
		try {
			coverage = COVERDF.parse(columns[this.coverageColumn]).floatValue();
		} catch (ParseException e) {
			System.err.println("Error while parsing the coverage: "
			        + columns[this.coverageColumn] + ", set as 0.");
		}

		int upepcount = Integer.parseInt(columns[this.unipepCountColumn]);
		float pi = Float.parseFloat(columns[this.pIColumn]);
		double mw = Double.parseDouble(columns[this.mwColumn]);
		int length = Integer.parseInt(columns[this.lengthColumn]);
		double SIn = Double.parseDouble(columns[this.SInColumn]);
		
		boolean isTarget = true;
		int value = Integer.parseInt(columns[this.targetColumn]);
		if (value == 1)
			;
		else if (value == -1)
			isTarget = false;
		else
			throw new IllegalArgumentException(
			        "The value indicating Target/Decoy must be 1 or -1, current: "
			                + value);

		float prob = -1f;
		String s = columns[this.probColumn];
		if (s.length() > 0)
			prob = Float.parseFloat(s);

		int groupIdx = -1;
		s = columns[this.groupidxColumn];
		if (s.length() > 0)
			groupIdx = Integer.parseInt(s);

		int crossPro = -1;
		s = columns[this.crossProColumn];
		if (s.length() > 0)
			crossPro = Integer.parseInt(s);

		double hydroScore = -1;
		s = columns[this.HydroScoreColumn];
		if(s.length()>0){
			if(s.charAt(0)=='('){
				s = s.substring(1, s.length()-1);
			}
		}
		hydroScore = Double.parseDouble(s);
		
		return new ReferenceDetail(name, pepcount, upepcount, coverage, prob,
		        pi, mw, length, isTarget, groupIdx, crossPro, SIn, hydroScore);
	}

}
