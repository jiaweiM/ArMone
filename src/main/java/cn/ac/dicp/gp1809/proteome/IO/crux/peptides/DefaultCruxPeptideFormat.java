/*
 ******************************************************************************
 * File: DefaultCruxPeptideFormat.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides;

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
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Default formatter for Crux peptides
 * 
 * @author Xinning
 * @version 0.1.2, 08-23-2009, 16:12:39
 */
public class DefaultCruxPeptideFormat extends
        AbstractPeptideFormat<ICruxPeptide> implements
        ICruxPeptideFormat<ICruxPeptide> {

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
	
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		DF2 = new DecimalFormat("0.##");
		DF3 = new DecimalFormat("0.###");
		DF4 = new DecimalFormat("0.####");
		DF5 = new DecimalFormat("0.#####");
		
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

	private int xcorrColumn = -1;
	private int deltaCnColumn = -1;
	private int spColumn = -1;
	private int pvalueColumn = -1;
	private int qvalueColumn = -1;
	private int percolatorColomun = -1;

	private int rxcColumn = -1;
	private int rspColumn = -1;
	private int ionsColumn = -1;

	private int simColumn = -1;
	private int proteinColumn = -1;
	private int piColumn = -1;
	private int NTTColumn = -1;
	private int probColumn = -1;
	private int ascoreColumn = -1;
	private int intensityColumn = -1;
	
	private int knownScoreCount;
	
	/**
	 * A default peptide format
	 * 
	 * @throws IllegalFormaterException
	 */
	public DefaultCruxPeptideFormat(String[] matchColumnTitle) {
		this.peptideIndexMap = new HashMap<String, Integer>(16);
		// initial peptide index

		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);

		/*
		 * Parse the three scores in crux
		 */

		int knownScoreCount = 0;
		for (int i = 0; i < matchColumnTitle.length; i++) {
			String tit = matchColumnTitle[i];
			if (tit.equalsIgnoreCase("deltaCn")) {
				peptideIndexMap.put(DELTACN, knownScoreCount + 7);
				knownScoreCount++;
				continue;
			}

			if (tit.equalsIgnoreCase("log(p-value) score")) {
				peptideIndexMap.put(P_VALUE, knownScoreCount + 7);
				knownScoreCount++;
				continue;
			}

			if (tit.equalsIgnoreCase("xcorr score")) {
				peptideIndexMap.put(XCORR, knownScoreCount + 7);
				knownScoreCount++;
				continue;
			}

			if (tit.equalsIgnoreCase("sp score")) {
				peptideIndexMap.put(SP, knownScoreCount + 7);
				knownScoreCount++;
				continue;
			}

			if (tit.equalsIgnoreCase("percolator_score score")) {
				peptideIndexMap.put(PERCALOTAR_VALUE, knownScoreCount + 7);
				knownScoreCount++;
				continue;
			}

			if (tit.equalsIgnoreCase("q_value score")) {
				peptideIndexMap.put(Q_VALUE, knownScoreCount + 7);
				knownScoreCount++;
				continue;
			}
		}

		peptideIndexMap.put(RXC, knownScoreCount + 7);
		peptideIndexMap.put(RSP, knownScoreCount + 8);
		peptideIndexMap.put(IONS, knownScoreCount + 9);
		peptideIndexMap.put(PROTEINS, knownScoreCount + 10);
		peptideIndexMap.put(PI, knownScoreCount + 11);
		peptideIndexMap.put(NUM_TERMS, knownScoreCount + 12);

		peptideIndexMap.put(PROB, knownScoreCount + 13);
		peptideIndexMap.put(fragmentInten, knownScoreCount + 14);
		peptideIndexMap.put(HydroScore, knownScoreCount + 15);

		try {
			this.intitalColumns(peptideIndexMap);// Will not threw
		} catch (IllegalFormaterException e) {
			e.printStackTrace();
		}
	}

	protected DefaultCruxPeptideFormat(HashMap<String, Integer> peptideIndexMap)
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

		/*
		 * The scores
		 */

		integer = peptideIndexMap.get(P_VALUE);
		if (integer != null) {
			pvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
			this.knownScoreCount ++;
		}

		integer = peptideIndexMap.get(XCORR);
		if (integer != null) {
			xcorrColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
			this.knownScoreCount ++;
		}

		integer = peptideIndexMap.get(DELTACN);
		if (integer != null) {
			deltaCnColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
			this.knownScoreCount ++;
		}

		integer = peptideIndexMap.get(SP);
		if (integer != null) {
			spColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
			this.knownScoreCount ++;
		}

		integer = peptideIndexMap.get(Q_VALUE);
		if (integer != null) {
			this.qvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
			
			this.knownScoreCount ++;
		}

		integer = peptideIndexMap.get(PERCALOTAR_VALUE);
		if (integer != null) {
			this.percolatorColomun = integer.intValue();
			this.validateColumns(usedColumn, integer);
			
			this.knownScoreCount ++;
		}

		integer = peptideIndexMap.get(RXC);
		if (integer != null) {
			this.rxcColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
			
		} else
			throw new IllegalFormaterException("The attribute: " + RXC
			        + " must be included in the formater");

		integer = peptideIndexMap.get(RSP);
		if (integer != null) {
			rspColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + RSP
			        + " must be included in the formater");

		integer = peptideIndexMap.get(IONS);
		if (integer != null) {
			ionsColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + IONS
			        + " must be included in the formater");

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

		integer = peptideIndexMap.get(inten);

		if (integer != null) {
			intensityColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + inten
			        + " must be included in the formater");
		
		integer = peptideIndexMap.get(DELTAMH);
		if (integer != null) {
			deltaMassColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + DELTAMH
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PI);

		if (integer != null) {
			piColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PI
			        + " must be included in the formater");

		simColumn = (integer = peptideIndexMap.get(SIM)) == null ? -1 : integer
		        .intValue();
		if (simColumn >= 0)
			this.validateColumns(usedColumn, integer);
		ascoreColumn = (integer = peptideIndexMap.get(ASCORE)) == null ? -1 : integer
		        .intValue();
		if (ascoreColumn >= 0)
			this.validateColumns(usedColumn, integer);
		probColumn = (integer = peptideIndexMap.get(PROB)) == null ? -1
		        : integer.intValue();
		if (probColumn >= 0)
			this.validateColumns(usedColumn, integer);
	}
	
	
	protected int getKnownScoreCount() {
		return this.knownScoreCount;
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
	public static DefaultCruxPeptideFormat parseTitle(String title)
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

		return new DefaultCruxPeptideFormat(peptideIndexMap);
	}
	
	public static DefaultCruxPeptideFormat parseTitle(String [] titles)
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

		return new DefaultCruxPeptideFormat(peptideIndexMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.ISequestPeptideFormat
	 * #
	 * format(cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.SequestPeptide
	 * )
	 */
	public String format(ICruxPeptide pep) {

		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = String.valueOf(pep.getCharge());
		strs[this.deltaMassColumn] = DF5.format(pep.getDeltaMH());
		strs[this.massColumn] = DF5.format(pep.getMH());
		strs[this.rankColumn] = String.valueOf(pep.getRank());
		strs[this.piColumn] = DF2.format(pep.getPI());
		strs[this.proteinColumn] = pep.getProteinReferenceString();
		strs[this.scanColumn] = pep.getScanNum();
		strs[this.sequenceColumn] = pep.getSequence();
		strs[this.NTTColumn] = String.valueOf(pep.getNumberofTerm());
		strs[this.intensityColumn] = DF4.format(pep.getInten());

		if (this.pvalueColumn >= 0)
			strs[this.pvalueColumn] = String.valueOf(pep.getPValue());
		if (this.xcorrColumn >= 0)
			strs[this.xcorrColumn] = String.valueOf(pep.getXcorr());
		if (this.spColumn >= 0)
			strs[this.spColumn] = String.valueOf(pep.getSp());
		if (this.deltaCnColumn >= 0)
			strs[this.deltaCnColumn] = String.valueOf(pep.getDeltaCn());
		if (this.qvalueColumn >= 0)
			strs[this.qvalueColumn] = String.valueOf(pep.getQValue());
		if (this.percolatorColomun >= 0)
			strs[this.percolatorColomun] = String.valueOf(pep
			        .getPercolator_score());

		strs[this.rxcColumn] = String.valueOf(pep.getRxc());
		strs[this.rspColumn] = String.valueOf(pep.getRsp());

		strs[this.ionsColumn] = " " + pep.getIons();
		if (this.probColumn >= 0) {
			float prob = pep.getProbabilty();
			if (prob > -0.00001f)
				strs[this.probColumn] = DF4.format(prob);
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
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
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
		strs[this.intensityColumn] = inten;

		if (this.pvalueColumn >= 0)
			strs[this.pvalueColumn] = P_VALUE;
		if (this.xcorrColumn >= 0)
			strs[this.xcorrColumn] = XCORR;
		if (this.deltaCnColumn >= 0)
			strs[this.deltaCnColumn] = DELTACN;
		if (this.spColumn >= 0)
			strs[this.spColumn] = SP;
		if (this.qvalueColumn >= 0)
			strs[this.qvalueColumn] = Q_VALUE;
		if (this.percolatorColomun >= 0)
			strs[this.percolatorColomun] = PERCALOTAR_VALUE;

		strs[this.rxcColumn] = RXC;
		strs[this.rspColumn] = RSP;
		strs[this.ionsColumn] = IONS;
		if (this.simColumn >= 0)
			strs[this.simColumn] = SIM;

		if (this.ascoreColumn >= 0)
			strs[this.ascoreColumn] = ASCORE;

		if (this.probColumn >= 0)
			strs[this.probColumn] = PROB;

		return strs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#parse(java
	 * .lang.String)
	 */
	@Override
	public ICruxPeptide parse(String pepstring) {

		String[] columns = StringUtil.split(pepstring, SEPARATOR);

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double inten = Double.parseDouble(columns[this.intensityColumn]);

		HashSet<ProteinReference> references = new HashSet<ProteinReference>();
		HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
		
		String [] names = StringUtil.split(columns[this.proteinColumn],
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

		short rxc = Short.parseShort(columns[this.rxcColumn]);
		short rsp = Short.parseShort(columns[this.rspColumn]);
		String ions = columns[this.ionsColumn].trim();

		float pvalue = -100000f;
		if (this.pvalueColumn >= 0)
			pvalue = Float.parseFloat(columns[this.pvalueColumn]);

		float xcorr = 0;
		if (this.xcorrColumn >= 0)
			xcorr = Float.parseFloat(columns[this.xcorrColumn]);

		float deltaCn = 0;
		if (this.deltaCnColumn >= 0)
			deltaCn = Float.parseFloat(columns[this.deltaCnColumn]);

		float qvalue = 1f;
		if (this.qvalueColumn >= 0)
			qvalue = Float.parseFloat(columns[this.qvalueColumn]);

		float sp = 0;
		if (this.spColumn >= 0)
			sp = Float.parseFloat(columns[this.spColumn]);

		float percolatorvalue = -100000f;;
		if (this.percolatorColomun >= 0)
			percolatorvalue = Float.parseFloat(columns[this.percolatorColomun]);

		CruxPeptide peptide = new CruxPeptide(scanNum, sequence, charge, rank,
		        mh, deltamh, xcorr, deltaCn, sp, pvalue, qvalue,
		        percolatorvalue, rxc, rsp, ions, references, NTT, pi, this);
		
		peptide.setPepLocAroundMap(locAroundMap);
		peptide.setInten(inten);

		/*
		 * float sim = -1f; if (this.simColumn >= 0) { String s =
		 * columns[this.simColumn]; if (s.length() > 0) sim =
		 * Float.parseFloat(s); }
		 */

		if (this.probColumn >= 0) {
			String s = columns[this.probColumn];
			if (s.length() > 0)
				peptide.setProbability(Float.parseFloat(s));
		}
		
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
	
	public ICruxPeptide parse(String[] columns) {

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double inten = Double.parseDouble(columns[this.intensityColumn]);

		HashSet<ProteinReference> references = new HashSet<ProteinReference>();
		HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
		
		String [] names = StringUtil.split(columns[this.proteinColumn],
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

		short rxc = Short.parseShort(columns[this.rxcColumn]);
		short rsp = Short.parseShort(columns[this.rspColumn]);
		String ions = columns[this.ionsColumn].trim();

		float pvalue = -100000f;
		if (this.pvalueColumn >= 0)
			pvalue = Float.parseFloat(columns[this.pvalueColumn]);

		float xcorr = 0;
		if (this.xcorrColumn >= 0)
			xcorr = Float.parseFloat(columns[this.xcorrColumn]);

		float deltaCn = 0;
		if (this.deltaCnColumn >= 0)
			deltaCn = Float.parseFloat(columns[this.deltaCnColumn]);

		float qvalue = 1f;
		if (this.qvalueColumn >= 0)
			qvalue = Float.parseFloat(columns[this.qvalueColumn]);

		float sp = 0;
		if (this.spColumn >= 0)
			sp = Float.parseFloat(columns[this.spColumn]);

		float percolatorvalue = -100000f;;
		if (this.percolatorColomun >= 0)
			percolatorvalue = Float.parseFloat(columns[this.percolatorColomun]);

		CruxPeptide peptide = new CruxPeptide(scanNum, sequence, charge, rank,
		        mh, deltamh, xcorr, deltaCn, sp, pvalue, qvalue,
		        percolatorvalue, rxc, rsp, ions, references, NTT, pi, this);
		
		peptide.setPepLocAroundMap(locAroundMap);
		peptide.setInten(inten);

		/*
		 * float sim = -1f; if (this.simColumn >= 0) { String s =
		 * columns[this.simColumn]; if (s.length() > 0) sim =
		 * Float.parseFloat(s); }
		 */

		if (this.probColumn >= 0) {
			String s = columns[this.probColumn];
			if (s.length() > 0)
				peptide.setProbability(Float.parseFloat(s));
		}
		
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
		return PeptideType.CRUX;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat#
	 * isContainsDeltaCn()
	 */
	@Override
	public boolean isContainsDeltaCn() {
		return this.deltaCnColumn >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat#
	 * isContainsPercolatorScore()
	 */
	@Override
	public boolean isContainsPercolatorScore() {
		return this.percolatorColomun >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat#
	 * isContainsPvalue()
	 */
	@Override
	public boolean isContainsPvalue() {
		return this.pvalueColumn >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat#
	 * isContainsQvalue()
	 */
	@Override
	public boolean isContainsQvalue() {
		return this.qvalueColumn >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat#isContainsSp
	 * ()
	 */
	@Override
	public boolean isContainsSp() {
		return this.spColumn >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat#
	 * isContainsXcorr()
	 */
	@Override
	public boolean isContainsXcorr() {
		return this.xcorrColumn >= 0;
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
		sb.append(XCORR).append("\t");
		sb.append(DELTACN).append("\t");

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#simpleFormat(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String simpleFormat(ICruxPeptide peptide) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getScanNum()).append("\t");
		sb.append(peptide.getMH()).append("\t");
		sb.append(peptide.getAbsoluteDeltaMZppm()).append("\t");
		sb.append(peptide.getCharge()).append("\t");
		sb.append(peptide.getSequence()).append("\t");
		sb.append(peptide.getXcorr()).append("\t");
		sb.append(peptide.getDeltaCn()).append("\t");
		
		return sb.toString();
	}
	
}
