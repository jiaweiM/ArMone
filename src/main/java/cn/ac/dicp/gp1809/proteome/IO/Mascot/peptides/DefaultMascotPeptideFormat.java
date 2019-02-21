/*
 * *****************************************************************************
 * File: DefaultMascotPeptideFormat.java * * * Created on 11-04-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.AbstractPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.GRAVY.GRAVYCalculator;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Default formatter for Mascot peptides
 * 
 * @author Xinning
 * @version 0.1.1, 08-23-2009, 16:11:42
 */
public class DefaultMascotPeptideFormat extends
        AbstractPeptideFormat<IMascotPeptide> implements
        IMascotPeptideFormat<IMascotPeptide> {

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
	private int evalueColumn = -1;
	private int ionscoreColumn = -1;
	private int deltaISColumn = -1;
	private int queryIdenNumColumn = -1;
	private int idenThresColumn = -1;
	private int homoThresColumn = -1;
	private int ionsMatchedColumn = -1;
	private int ions1Column = -1;
	private int ions2Column = -1;
	private int ions3Column = -1;
	private int simColumn = -1;
	private int proteinColumn = -1;
	private int piColumn = -1;
	private int NTTColumn = -1;
	private int ascoreColumn = -1;
	private int fragIntenColumn = -1;
	private int intenColumn = -1;
	private int hydroScoreColumn = -1;
	private int rtColumn = -1;

//	private int pepBeginColumn = -1;
//	private int pepEndColumn = -1;
	
	/**
	 * A default peptide format
	 * 
	 * @throws IllegalFormaterException
	 */
	public DefaultMascotPeptideFormat() {
		this.peptideIndexMap = new HashMap<String, Integer>(20);
		// initial peptide index

		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);
		peptideIndexMap.put(IONSCORE, 7);
		peptideIndexMap.put(deltaIS, 8);
		peptideIndexMap.put(idenThres, 9);
		peptideIndexMap.put(homoThres, 10);
		peptideIndexMap.put(E_VALUE, 11);
		peptideIndexMap.put(queryIdenNum, 12);

		peptideIndexMap.put(PROTEINS, 13);
		peptideIndexMap.put(PI, 14);
		peptideIndexMap.put(NUM_TERMS, 15);

		peptideIndexMap.put(fragmentInten, 16);
		peptideIndexMap.put(inten, 17);
		peptideIndexMap.put(HydroScore, 18);
		peptideIndexMap.put(retentionTime, 19);
		
		peptideIndexMap.put(matchedIons, 20);
		peptideIndexMap.put(peaksFromIons1, 21);
		peptideIndexMap.put(peaksFromIons2, 22);
		peptideIndexMap.put(peaksFromIons3, 23);
//		peptideIndexMap.put(peptideBeg, 19);
//		peptideIndexMap.put(peptideEnd, 20);

		try {
			this.intitalColumns(peptideIndexMap);// Will not threw
		} catch (IllegalFormaterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The String key and the column
	 * 
	 * @param peptideIndexMap
	 * @throws IllegalFormaterException
	 */
	public DefaultMascotPeptideFormat(HashMap<String, Integer> peptideIndexMap)
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
		
		integer = peptideIndexMap.get(RANK);
		if (integer != null) {
			rankColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + RANK
			        + " must be included in the formater");

		integer = peptideIndexMap.get(E_VALUE);
		if (integer != null) {
			evalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + E_VALUE
			        + " must be included in the formater");

		integer = peptideIndexMap.get(IONSCORE);

		if (integer != null) {
			ionscoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + IONSCORE
			        + " must be included in the formater");
		
		integer = peptideIndexMap.get(deltaIS);

		if (integer != null) {
			deltaISColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} 

		integer = peptideIndexMap.get(PROTEINS);
		if (integer != null) {
			proteinColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PROTEINS
			        + " must be included in the formater");

		integer = peptideIndexMap.get(NUM_TERMS);

		if (integer != null) {
			NTTColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + NUM_TERMS
			        + " must be included in the formater");

		integer = peptideIndexMap.get(DELTAMH);

		if (integer == null) {
			integer = peptideIndexMap.get("Diff(MH+)");// For old version
		}

		if (integer != null) {
			deltaMassColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + DELTAMH
			        + " must be included in the formater");

		integer = peptideIndexMap.get(fragmentInten);

		if (integer != null) {
			fragIntenColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + fragmentInten
			        + " must be included in the formater");
		
		integer = peptideIndexMap.get(inten);

		if (integer != null) {
			intenColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + inten
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PI);

		if (integer != null) {
			piColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PI
			        + " must be included in the formater");

		integer = peptideIndexMap.get(HydroScore);

		if (integer == null) {
			integer = peptideIndexMap.get("HydroScore");
		}

		if (integer != null) {
			hydroScoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(retentionTime);

		if (integer == null) {
			integer = peptideIndexMap.get("retentionTime");
		}

		if (integer != null) {
			rtColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(idenThres);

		if (integer != null) {
			idenThresColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(homoThres);

		if (integer != null) {
			homoThresColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(queryIdenNum);

		if (integer != null) {
			queryIdenNumColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(matchedIons);

		if (integer != null) {
			ionsMatchedColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(peaksFromIons1);

		if (integer != null) {
			ions1Column = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(peaksFromIons2);

		if (integer != null) {
			ions2Column = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(peaksFromIons3);

		if (integer != null) {
			ions3Column = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}

		simColumn = (integer = peptideIndexMap.get(SIM)) == null ? -1 : integer
		        .intValue();
		if (simColumn >= 0)
			this.validateColumns(usedColumn, integer);
		ascoreColumn = (integer = peptideIndexMap.get(ASCORE)) == null ? -1 : integer
		        .intValue();
		if (ascoreColumn >= 0)
			this.validateColumns(usedColumn, integer);

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
	public static DefaultMascotPeptideFormat parseTitle(String title)
	        throws IllegalFormaterException, NullPointerException {

		if (title == null || title.length() == 0)
			throw new NullPointerException(
			        "The formatter can not be created from a null title.");

		HashMap<String, Integer> peptideIndexMap = new HashMap<String, Integer>(
		        20);
		// initial peptide index

		String[] titles = StringUtil.split(title, SEPARATOR);

		for (int i = 0; i < titles.length; i++) {
			String ts = titles[i].trim();
			if (ts.length() > 0)
				peptideIndexMap.put(ts, new Integer(i));
		}

		return new DefaultMascotPeptideFormat(peptideIndexMap);
	}
	
	public static DefaultMascotPeptideFormat parseTitle(String [] titles)
		throws IllegalFormaterException, NullPointerException {

		if (titles == null || titles.length == 0)
			throw new NullPointerException(
				"The formatter can not be created from a null title.");

		HashMap<String, Integer> peptideIndexMap = new HashMap<String, Integer>(
				20);
		// initial peptide index

		for (int i = 0; i < titles.length; i++) {
			String ts = titles[i].trim();
			if (ts.length() > 0)
				peptideIndexMap.put(ts, new Integer(i));
		}

		return new DefaultMascotPeptideFormat(peptideIndexMap);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.ISequestPeptideFormat
	 *      #format(cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.SequestPeptide)
	 */
	public String format(IMascotPeptide pep) {

		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = String.valueOf(pep.getCharge());
		strs[this.deltaMassColumn] = DF5.format(pep.getDeltaMH());
		strs[this.massColumn] = DF5.format(pep.getMH());
		strs[this.rankColumn] = String.valueOf(pep.getRank());
		strs[this.piColumn] = DF2.format(pep.getPI());
		strs[this.proteinColumn] = pep.getProteinReferenceString();
		strs[this.ionscoreColumn] = String.valueOf(pep.getIonscore());
		strs[this.scanColumn] = pep.getScanNum();
		strs[this.sequenceColumn] = pep.getSequence();
		strs[this.fragIntenColumn] = String.valueOf(pep.getFragInten());
		strs[this.intenColumn] = String.valueOf(pep.getInten());
		strs[this.rtColumn] = String.valueOf(pep.getRetentionTime());
		strs[this.idenThresColumn] = String.valueOf(pep.getIdenThres());
		strs[this.homoThresColumn] = String.valueOf(pep.getHomoThres());
		strs[this.deltaISColumn] = String.valueOf(pep.getDeltaS());
		strs[this.queryIdenNumColumn] = String.valueOf(pep.getQueryIdenNum());
		strs[this.ionsMatchedColumn] = String.valueOf(pep.getNumOfMatchedIons());
		strs[this.ions1Column] = String.valueOf(pep.getPeaksUsedFromIons1());
		strs[this.ions2Column] = String.valueOf(pep.getPeaksUsedFromIons2());
		strs[this.ions3Column] = String.valueOf(pep.getPeaksUsedFromIons3());
		
		double evalue = pep.getEvalue();
		strs[this.evalueColumn] = evalue  > 0.1 ? DF3.format(evalue) : dfe.format(evalue);

		strs[this.NTTColumn] = String.valueOf(pep.getNumberofTerm());

		if(this.hydroScoreColumn > 0){
			strs[this.hydroScoreColumn] = DF5.format(GRAVYCalculator.calculate(pep));
		}

		if (this.simColumn >= 0) {
			float sim = pep.getSim();
			if (sim > -0.0001f)
				strs[this.simColumn] = DF3.format(sim);
		}
		
		if (this.ascoreColumn >= 0) {
			StringBuilder sb = new StringBuilder();
			
			double[] ascores = pep.getAscores();
			if(ascores == null) {
				sb.append(0);
			}
			else {
				for(double ascore : ascores) {
					sb.append(DF2.format(ascore)).append(',');
				}
				sb.deleteCharAt(sb.length()-1);
			}
			
			strs[this.ascoreColumn] = sb.toString();
		}
		
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
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
	 */
	@Override
	public String[] getTitle() {
		
		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = CHARGE;
		strs[this.deltaMassColumn] = DELTAMH;
		strs[this.massColumn] = MH;
		strs[this.rankColumn] = RANK;
		strs[this.NTTColumn] = NUM_TERMS;
		strs[this.piColumn] = PI;
		strs[this.proteinColumn] = PROTEINS;
		strs[this.scanColumn] = SCAN;
		strs[this.sequenceColumn] = SEQUENCE;

		strs[this.evalueColumn] = E_VALUE;
		strs[this.ionscoreColumn] = IONSCORE;
		strs[this.fragIntenColumn] = fragmentInten;
		strs[this.intenColumn] = inten;
		strs[this.rtColumn] = retentionTime;
		strs[this.idenThresColumn] = idenThres;
		strs[this.homoThresColumn] = homoThres;
		strs[this.deltaISColumn] = deltaIS;
		strs[this.queryIdenNumColumn] = queryIdenNum;
		strs[this.ionsMatchedColumn] = matchedIons;
		strs[this.ions1Column] = peaksFromIons1;
		strs[this.ions2Column] = peaksFromIons2;
		strs[this.ions3Column] = peaksFromIons3;

		if(this.hydroScoreColumn >= 0)
			strs[this.hydroScoreColumn] = HydroScore;	
		
		if (this.simColumn >= 0)
			strs[this.simColumn] = SIM;

		if (this.ascoreColumn >= 0)
			strs[this.ascoreColumn] = ASCORE;

		return strs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#parse(java.lang.String)
	 */
	@Override
	public IMascotPeptide parse(String pepstring) {

		String[] columns = StringUtil.split(pepstring, SEPARATOR);

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		double evalue = Double.parseDouble(columns[this.evalueColumn]);
		float ionscore = Float.parseFloat(columns[this.ionscoreColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double fraginten = Double.parseDouble(columns[this.fragIntenColumn]);
		double inten = Double.parseDouble(columns[this.intenColumn]);

		float idenThres = -1f;
		if(this.idenThresColumn>=0){
			String s = columns[this.idenThresColumn];
			if(s.length()>0)
				idenThres = Float.parseFloat(columns[this.idenThresColumn]);
		}
		
		float homoThres = -1f;
		if(this.homoThresColumn>=0){
			String s = columns[this.homoThresColumn];
			if(s.length()>0)
				homoThres = Float.parseFloat(columns[this.homoThresColumn]);
		}

		float deltaIS = -1f;
		if(this.deltaISColumn>=0){
			String s = columns[this.deltaISColumn];
			if(s.length()>0)
				deltaIS = Float.parseFloat(columns[this.deltaISColumn]);
		}
		
		int queryIdenNum = -1;
		if(this.queryIdenNumColumn>=0){
			String s = columns[this.queryIdenNumColumn];
			if(s.length()>0)
				queryIdenNum = Integer.parseInt(columns[this.queryIdenNumColumn]);
		}
		
		double rt = -1;
		if(this.rtColumn>=0){
			String s = columns[this.rtColumn];
			if(s.length()>0)
				rt = Double.parseDouble(columns[this.rtColumn]);
		}
		
		int ionsMatched = -1;
		if(this.ionsMatchedColumn>=0){
			String s = columns[this.ionsMatchedColumn];
			if(s.length()>0)
				ionsMatched = Integer.parseInt(columns[this.ionsMatchedColumn]);
		}
		
		int ions1 = -1;
		if(this.ions1Column>=0){
			String s = columns[this.ions1Column];
			if(s.length()>0)
				ions1 = Integer.parseInt(columns[this.ions1Column]);
		}
		
		int ions2 = -1;
		if(this.ions2Column>=0){
			String s = columns[this.ions2Column];
			if(s.length()>0)
				ions2 = Integer.parseInt(columns[this.ions2Column]);
		}
		
		int ions3 = -1;
		if(this.ions3Column>=0){
			String s = columns[this.ions3Column];
			if(s.length()>0)
				ions3 = Integer.parseInt(columns[this.ions3Column]);
		}

		double hydroScore = -1;
		if(this.hydroScoreColumn>=0){
			String s = columns[this.hydroScoreColumn];
			if(s.length()>0)
				hydroScore = Double.parseDouble(columns[this.hydroScoreColumn]);
			if(hydroScore==0)
				hydroScore = GRAVYCalculator.calculate(sequence);
		}
		
		HashSet<ProteinReference> references = new HashSet<ProteinReference>();
		HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
		
		String[] names = StringUtil.split(columns[this.proteinColumn],
		        IPeptide.ProteinNameSpliter);
		
		for (String name : names){
			
			String [] sss = name.split("\\+");
			ProteinReference pr = ProteinReference.parse(sss[0]);
			references.add(pr);
			
			int beg = Integer.parseInt(sss[1]);
			int end = Integer.parseInt(sss[2]);
			
			if(sss.length==3){
				SeqLocAround sla = new SeqLocAround(beg, end, "", "");
				locAroundMap.put(pr.toString(), sla);
			}else if(sss.length==4){
				SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
				locAroundMap.put(pr.toString(), sla);
			}else if(sss.length==5){
				SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
				locAroundMap.put(pr.toString(), sla);
			}
		}

		MascotPeptide peptide = new MascotPeptide(scanNum, sequence, charge, mh,
		        deltamh, NTT, rank, ionscore, evalue, references, pi,this);
		
		peptide.setFragInten(fraginten);
		peptide.setInten(inten);
		peptide.setHydroScore(hydroScore);
		peptide.setRetentionTime(rt);
		peptide.setIndenThres(idenThres);
		peptide.setHomoThres(homoThres);
		peptide.setPepLocAroundMap(locAroundMap);
		peptide.setDeltaS(deltaIS);
		peptide.setQueryIdenNum(queryIdenNum);
		peptide.setNumOfMatchedIons(ionsMatched);
		peptide.setPeaksUsedFromIons1(ions1);
		peptide.setPeaksUsedFromIons2(ions2);
		peptide.setPeaksUsedFromIons3(ions3);
		
		/*
		 * float sim = -1f; if (this.simColumn >= 0) { String s =
		 * columns[this.simColumn]; if (s.length() > 0) sim =
		 * Float.parseFloat(s); }
		 */
		
		if (this.simColumn >= 0) {
			String s = columns[this.simColumn];
			if (s.length() > 0) {
				float sim = Float.parseFloat(s);
				peptide.setSim(sim);
			}
		}
		
		if (this.ascoreColumn >= 0) {
			String s = columns[this.ascoreColumn];
			
			String[] ssc = StringUtil.split(s, ',');
			int len = ssc.length;
			double scores[] = new double[len];
			
			for(int i=0; i< len; i++) {
				scores[i] = Double.parseDouble(ssc[i]);
			}
			
			peptide.setAscores(scores);
		}

		return peptide;
	}
	
	public IMascotPeptide parse(String[] columns) {

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		double evalue = Double.parseDouble(columns[this.evalueColumn]);
		float ionscore = Float.parseFloat(columns[this.ionscoreColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double fraginten = Double.parseDouble(columns[this.fragIntenColumn]);
		double inten = Double.parseDouble(columns[this.intenColumn]);

		float idenThres = -1f;
		if(this.idenThresColumn>=0){
			String s = columns[this.idenThresColumn];
			if(s.length()>0)
				idenThres = Float.parseFloat(columns[this.idenThresColumn]);
		}
		
		float homoThres = -1f;
		if(this.homoThresColumn>=0){
			String s = columns[this.homoThresColumn];
			if(s.length()>0)
				homoThres = Float.parseFloat(columns[this.homoThresColumn]);
		}

		float deltaIS = -1f;
		if(this.deltaISColumn>=0){
			String s = columns[this.deltaISColumn];
			if(s.length()>0)
				deltaIS = Float.parseFloat(columns[this.deltaISColumn]);
		}
		
		int queryIdenNum = -1;
		if(this.queryIdenNumColumn>=0){
			String s = columns[this.queryIdenNumColumn];
			if(s.length()>0)
				queryIdenNum = Integer.parseInt(columns[this.queryIdenNumColumn]);
		}
		
		double rt = -1;
		if(this.rtColumn>=0){
			String s = columns[this.rtColumn];
			if(s.length()>0)
				rt = Double.parseDouble(columns[this.rtColumn]);
		}

		double hydroScore = -1;
		if(this.hydroScoreColumn>=0){
			String s = columns[this.hydroScoreColumn];
			if(s.length()>0)
				hydroScore = Double.parseDouble(columns[this.hydroScoreColumn]);
			if(hydroScore==0)
				hydroScore = GRAVYCalculator.calculate(sequence);
		}
		
		int ionsMatched = -1;
		if(this.ionsMatchedColumn>=0){
			String s = columns[this.ionsMatchedColumn];
			if(s.length()>0)
				ionsMatched = Integer.parseInt(columns[this.ionsMatchedColumn]);
		}
		
		int ions1 = -1;
		if(this.ions1Column>=0){
			String s = columns[this.ions1Column];
			if(s.length()>0)
				ions1 = Integer.parseInt(columns[this.ions1Column]);
		}
		
		int ions2 = -1;
		if(this.ions2Column>=0){
			String s = columns[this.ions2Column];
			if(s.length()>0)
				ions2 = Integer.parseInt(columns[this.ions2Column]);
		}
		
		int ions3 = -1;
		if(this.ions3Column>=0){
			String s = columns[this.ions3Column];
			if(s.length()>0)
				ions3 = Integer.parseInt(columns[this.ions3Column]);
		}
		
		HashSet<ProteinReference> references = new HashSet<ProteinReference>();
		HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
		
		String[] names = StringUtil.split(columns[this.proteinColumn],
		        IPeptide.ProteinNameSpliter);
		
		for (String name : names){
			
			String [] sss = name.split("\\+");
			ProteinReference pr = ProteinReference.parse(sss[0]);
			references.add(pr);
			
			int beg = Integer.parseInt(sss[1]);
			int end = Integer.parseInt(sss[2]);
			
			if(sss.length==3){
				SeqLocAround sla = new SeqLocAround(beg, end, "", "");
				locAroundMap.put(pr.toString(), sla);
			}else if(sss.length==4){
				SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
				locAroundMap.put(pr.toString(), sla);
			}else if(sss.length==5){
				SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
				locAroundMap.put(pr.toString(), sla);
			}
		}

		MascotPeptide peptide = new MascotPeptide(scanNum, sequence, charge, mh,
		        deltamh, NTT, rank, ionscore, evalue, references, pi,this);
		
		peptide.setFragInten(fraginten);
		peptide.setInten(inten);
		peptide.setHydroScore(hydroScore);
		peptide.setRetentionTime(rt);
		peptide.setIndenThres(idenThres);
		peptide.setHomoThres(homoThres);
		peptide.setPepLocAroundMap(locAroundMap);
		peptide.setDeltaS(deltaIS);
		peptide.setQueryIdenNum(queryIdenNum);
		peptide.setNumOfMatchedIons(ionsMatched);
		peptide.setPeaksUsedFromIons1(ions1);
		peptide.setPeaksUsedFromIons2(ions2);
		peptide.setPeaksUsedFromIons3(ions3);
		
		/*
		 * float sim = -1f; if (this.simColumn >= 0) { String s =
		 * columns[this.simColumn]; if (s.length() > 0) sim =
		 * Float.parseFloat(s); }
		 */
		
		if (this.simColumn >= 0) {
			String s = columns[this.simColumn];
			if (s.length() > 0) {
				float sim = Float.parseFloat(s);
				peptide.setSim(sim);
			}
		}
		
		if (this.ascoreColumn >= 0) {
			String s = columns[this.ascoreColumn];
			
			String[] ssc = StringUtil.split(s, ',');
			int len = ssc.length;
			double scores[] = new double[len];
			
			for(int i=0; i< len; i++) {
				scores[i] = Double.parseDouble(ssc[i]);
			}
			
			peptide.setAscores(scores);
		}

		return peptide;
	}

	@Override
	public PeptideType type() {
		return PeptideType.MASCOT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getIndex(java.lang.String)
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
		StringBuilder sb = new StringBuilder();
		sb.append(SCAN).append("\t");
		sb.append(MH).append("\t");
		sb.append(DELTAPPM).append("\t");
		sb.append(CHARGE).append("\t");
		sb.append(SEQUENCE).append("\t");
		sb.append(IONSCORE).append("\t");
		sb.append(deltaIS).append("\t");

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#simpleFormat(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String simpleFormat(IMascotPeptide peptide) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getScanNum()).append("\t");
		sb.append(peptide.getMH()).append("\t");
		sb.append(peptide.getAbsoluteDeltaMZppm()).append("\t");
		sb.append(peptide.getCharge()).append("\t");
		sb.append(peptide.getSequence()).append("\t");
		sb.append(peptide.getIonscore()).append("\t");
		sb.append(peptide.getDeltaS()).append("\t");
		
		return sb.toString();
	}
}
