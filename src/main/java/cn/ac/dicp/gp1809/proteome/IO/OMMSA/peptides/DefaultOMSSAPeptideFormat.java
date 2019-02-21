/* 
 ******************************************************************************
 * File: DefaultOMSSAPeptideFormat.java * * * Created on 08-31-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides;

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
 * Default formatter for OMSSA peptides
 * 
 * @author Xinning
 * @version 0.4.3, 08-23-2009, 16:11:23
 */
public class DefaultOMSSAPeptideFormat extends
        AbstractPeptideFormat<IOMSSAPeptide> implements
        IOMSSAPeptideFormat<IOMSSAPeptide> {
	
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
	private int pvalueColumn = -1;
	private int simColumn = -1;
	private int proteinColumn = -1;
	private int piColumn = -1;
	private int NTTColumn = -1;
	private int probColumn = -1;
	private int ascoreColumn = -1;
	private int intensityColumn = -1;
	private int hydroScoreColumn = -1;
	private int fragIntenColumn = -1;

	/**
	 * A default peptide format
	 * 
	 * @throws IllegalFormaterException
	 */
	public DefaultOMSSAPeptideFormat() {
		this.peptideIndexMap = new HashMap<String, Integer>(16);
		// initial peptide index

		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);
		peptideIndexMap.put(E_VALUE, 7);
		peptideIndexMap.put(P_VALUE, 8);

		peptideIndexMap.put(PROTEINS, 9);
		peptideIndexMap.put(PI, 10);
		peptideIndexMap.put(NUM_TERMS, 11);

		peptideIndexMap.put(inten, 12);
		peptideIndexMap.put(fragmentInten, 13);
		peptideIndexMap.put(HydroScore, 14);

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
	public DefaultOMSSAPeptideFormat(HashMap<String, Integer> peptideIndexMap)
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

		integer = peptideIndexMap.get(P_VALUE);

		if (integer != null) {
			pvalueColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + P_VALUE
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

		integer = peptideIndexMap.get(inten);
		if (integer != null) {
			intensityColumn = integer.intValue();
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

		if (integer != null) {
			hydroScoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + HydroScore
			        + " must be included in the formater");
		
		integer = peptideIndexMap.get(fragmentInten);

		if (integer != null) {
			fragIntenColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + fragmentInten
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
	public static DefaultOMSSAPeptideFormat parseTitle(String title)
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

		return new DefaultOMSSAPeptideFormat(peptideIndexMap);
	}

	public static DefaultOMSSAPeptideFormat parseTitle(String [] titles)
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

		return new DefaultOMSSAPeptideFormat(peptideIndexMap);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.ISequestPeptideFormat
	 *      #format(cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.SequestPeptide)
	 */
	public String format(IOMSSAPeptide pep) {

		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = String.valueOf(pep.getCharge());
		strs[this.deltaMassColumn] = DF5.format(pep.getDeltaMH());
		strs[this.rankColumn] = String.valueOf(pep.getRank());
		strs[this.massColumn] = DF5.format(pep.getMH());
		strs[this.piColumn] = DF2.format(pep.getPI());
		strs[this.proteinColumn] = pep.getProteinReferenceString();
		strs[this.pvalueColumn] = String.valueOf(pep.getPvalue());
		strs[this.scanColumn] = pep.getScanNum();
		strs[this.sequenceColumn] = pep.getSequence();
		strs[this.intensityColumn] = DF4.format(pep.getInten());
		strs[this.fragIntenColumn] = String.valueOf(pep.getFragInten());

		double evalue = pep.getEvalue();
		strs[this.evalueColumn] = evalue  > 0.1 ? DF3.format(evalue) : dfe.format(evalue);

		strs[this.NTTColumn] = String.valueOf(pep.getNumberofTerm());
		
		if(this.hydroScoreColumn > 0){
			strs[this.hydroScoreColumn] = DF5.format(GRAVYCalculator.calculate(pep));
		}

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
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
	 */
	@Override
	public String[] getTitle() {
		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = CHARGE;
		strs[this.deltaMassColumn] = DELTAMH;
		strs[this.massColumn] = MH;
		strs[this.NTTColumn] = NUM_TERMS;
		strs[this.rankColumn] = RANK;
		strs[this.piColumn] = PI;
		strs[this.proteinColumn] = PROTEINS;
		strs[this.scanColumn] = SCAN;
		strs[this.sequenceColumn] = SEQUENCE;
		strs[this.intensityColumn] = inten;
		strs[this.evalueColumn] = E_VALUE;
		strs[this.pvalueColumn] = P_VALUE;
		
		if(this.fragIntenColumn >= 0)
			strs[this.fragIntenColumn] = fragmentInten;	
		
		if(this.hydroScoreColumn >= 0)
			strs[this.hydroScoreColumn] = HydroScore;	

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
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#parse(java.lang.String)
	 */
	@Override
	public IOMSSAPeptide parse(String pepstring) {

		String[] columns = StringUtil.split(pepstring, SEPARATOR);

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		double evalue = Double.parseDouble(columns[this.evalueColumn]);
		double pvalue = Double.parseDouble(columns[this.pvalueColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double inten = Double.parseDouble(columns[this.intensityColumn]);
		double fraginten = Double.parseDouble(columns[this.fragIntenColumn]);

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
		
		double hydroScore = -1;
		if(this.hydroScoreColumn>=0){
			String s = columns[this.hydroScoreColumn];
			if(s.length()>0)
				hydroScore = Double.parseDouble(columns[this.hydroScoreColumn]);
			if(hydroScore==0)
				hydroScore = GRAVYCalculator.calculate(sequence);
		}
		
		OMSSAPeptide peptide = new OMSSAPeptide(scanNum, sequence, charge, mh,
		        deltamh, rank, evalue, pvalue, references, pi, NTT, this);
		peptide.setInten(inten);
		peptide.setPepLocAroundMap(locAroundMap);
		peptide.setHydroScore(hydroScore);
		peptide.setFragInten(fraginten);
		
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

	public IOMSSAPeptide parse(String[] columns) {

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		double evalue = Double.parseDouble(columns[this.evalueColumn]);
		double pvalue = Double.parseDouble(columns[this.pvalueColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double inten = Double.parseDouble(columns[this.intensityColumn]);
		double fraginten = Double.parseDouble(columns[this.fragIntenColumn]);

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

		double hydroScore = -1;
		if(this.hydroScoreColumn>=0){
			String s = columns[this.hydroScoreColumn];
			if(s.length()>0)
				hydroScore = Double.parseDouble(columns[this.hydroScoreColumn]);
			if(hydroScore==0)
				hydroScore = GRAVYCalculator.calculate(sequence);
		}
		
		OMSSAPeptide peptide = new OMSSAPeptide(scanNum, sequence, charge, mh,
		        deltamh, rank, evalue, pvalue, references, pi, NTT, this);
		peptide.setInten(inten);
		peptide.setPepLocAroundMap(locAroundMap);
		peptide.setFragInten(fraginten);
		peptide.setHydroScore(hydroScore);
		
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
		return PeptideType.OMSSA;
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
		sb.append(E_VALUE).append("\t");
		sb.append(P_VALUE).append("\t");

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#simpleFormat(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String simpleFormat(IOMSSAPeptide peptide) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getScanNum()).append("\t");
		sb.append(peptide.getMH()).append("\t");
		sb.append(peptide.getAbsoluteDeltaMZppm()).append("\t");
		sb.append(peptide.getCharge()).append("\t");
		sb.append(peptide.getSequence()).append("\t");
		sb.append(peptide.getEvalue()).append("\t");
		sb.append(peptide.getPvalue()).append("\t");
		
		return sb.toString();
	}
}
