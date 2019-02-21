/* 
 ******************************************************************************
 * File: DefaultMaxQuantPeptideFormat.java * * * Created on 2012-1-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides;

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
 * @author ck
 *
 * @version 2012-1-4, 14:28:55
 */
public class DefaultMaxQuantPeptideFormat extends
	AbstractPeptideFormat<IMaxQuantPeptide> implements
	IMaxQuantPeptideFormat<IMaxQuantPeptide> {

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
	
	private int scanColumn = -1;
	private int sequenceColumn = -1;
	private int chargeColumn = -1;
	private int massColumn = -1;
	private int deltaMassColumn = -1;
	private int rankColumn = -1;
	private int scoreColumn = -1;
	private int PEPColumn = -1;
	private int simColumn = -1;
	private int proteinColumn = -1;
	private int piColumn = -1;
	private int NTTColumn = -1;
	private int ascoreColumn = -1;
	private int fragIntenColumn = -1;
	private int intenColumn = -1;
	private int hydroScoreColumn = -1;
	private int rtColumn = -1;
	
	public DefaultMaxQuantPeptideFormat(){

		this.peptideIndexMap = new HashMap<String, Integer>(20);
		// initial peptide index

		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);
		peptideIndexMap.put(Score, 7);
		peptideIndexMap.put(PEP, 8);

		peptideIndexMap.put(PROTEINS, 9);
		peptideIndexMap.put(PI, 10);
		peptideIndexMap.put(NUM_TERMS, 11);

		peptideIndexMap.put(fragmentInten, 12);
		peptideIndexMap.put(inten, 13);
		peptideIndexMap.put(HydroScore, 14);
		peptideIndexMap.put(retentionTime, 15);

		try {
			this.intitalColumns(peptideIndexMap);// Will not threw
		} catch (IllegalFormaterException e) {
			e.printStackTrace();
		}
	
	}

	public DefaultMaxQuantPeptideFormat(HashMap<String, Integer> peptideIndexMap) 
		throws IllegalFormaterException{
		
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

		integer = peptideIndexMap.get(Score);
		if (integer != null) {
			scoreColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + Score
			        + " must be included in the formater");

		integer = peptideIndexMap.get(PEP);

		if (integer != null) {
			PEPColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		} else
			throw new IllegalFormaterException("The attribute: " + PEP
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

/*		
		integer = peptideIndexMap.get(peptideBeg);

		if (integer != null) {
			pepBeginColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
		
		integer = peptideIndexMap.get(peptideEnd);

		if (integer != null) {
			pepEndColumn = integer.intValue();
			this.validateColumns(usedColumn, integer);
		}
*/
		simColumn = (integer = peptideIndexMap.get(SIM)) == null ? -1 : integer
		        .intValue();
		if (simColumn >= 0)
			this.validateColumns(usedColumn, integer);
		ascoreColumn = (integer = peptideIndexMap.get(ASCORE)) == null ? -1 : integer
		        .intValue();
		if (ascoreColumn >= 0)
			this.validateColumns(usedColumn, integer);

	
	}
	
	public static DefaultMaxQuantPeptideFormat parseTitle(String [] titles)
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

		return new DefaultMaxQuantPeptideFormat(peptideIndexMap);
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
	public static DefaultMaxQuantPeptideFormat parseTitle(String title)
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

		return new DefaultMaxQuantPeptideFormat(peptideIndexMap);
	}
	
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#format(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String format(IMaxQuantPeptide pep) {
		// TODO Auto-generated method stub

		String[] strs = new String[this.num_columns];
		strs[this.chargeColumn] = String.valueOf(pep.getCharge());
		strs[this.deltaMassColumn] = DF5.format(pep.getDeltaMH());
		strs[this.massColumn] = DF5.format(pep.getMH());
		strs[this.rankColumn] = String.valueOf(pep.getRank());
		strs[this.piColumn] = DF2.format(pep.getPI());
		strs[this.proteinColumn] = pep.getProteinReferenceString();
		strs[this.scoreColumn] = String.valueOf(pep.getScore());
		strs[this.PEPColumn] = String.valueOf(pep.getPEP());
		strs[this.scanColumn] = pep.getScanNum();
		strs[this.sequenceColumn] = pep.getSequence();
		strs[this.fragIntenColumn] = String.valueOf(pep.getFragInten());
		strs[this.intenColumn] = String.valueOf(pep.getInten());
		strs[this.rtColumn] = String.valueOf(pep.getRetentionTime());

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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#getIndex(java.lang.String)
	 */
	@Override
	public int getIndex(String term) {
		// TODO Auto-generated method stub
		Integer idx = this.peptideIndexMap.get(term);
		if (idx == null)
			return -1;
		return idx;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#getIndexMap()
	 */
	@Override
	public HashMap<String, Integer> getIndexMap() {
		// TODO Auto-generated method stub
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
		sb.append(Score).append("\t");
		sb.append(PEP).append("\t");

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#parse(java.lang.String)
	 */
	@Override
	public IMaxQuantPeptide parse(String pepstring) {
		// TODO Auto-generated method stub
		
		String[] columns = StringUtil.split(pepstring, SEPARATOR);
		
		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		double score = Double.parseDouble(columns[this.scoreColumn]);
		double PEP = Float.parseFloat(columns[this.PEPColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double fraginten = Double.parseDouble(columns[this.fragIntenColumn]);
		double inten = Double.parseDouble(columns[this.intenColumn]);

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

		MaxQuantPeptide peptide = new MaxQuantPeptide(scanNum, sequence, charge, mh,
		        deltamh, NTT, rank, score, PEP, references, pi, this);
		
		peptide.setFragInten(fraginten);
		peptide.setInten(inten);
		peptide.setHydroScore(hydroScore);
		peptide.setRetentionTime(rt);
		peptide.setPepLocAroundMap(locAroundMap);

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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#parse(java.lang.String[])
	 */
	@Override
	public IMaxQuantPeptide parse(String[] columns) {
		// TODO Auto-generated method stub

		String scanNum = columns[this.scanColumn];
		String sequence = columns[this.sequenceColumn];
		double mh = Double.parseDouble(columns[this.massColumn]);
		short charge = Short.parseShort(columns[this.chargeColumn]);
		double score = Double.parseDouble(columns[this.scoreColumn]);
		double PEP = Float.parseFloat(columns[this.PEPColumn]);
		short NTT = Short.parseShort(columns[this.NTTColumn]);
		float pi = Float.parseFloat(columns[this.piColumn]);
		double deltamh = Double.parseDouble(columns[this.deltaMassColumn]);
		short rank = Short.parseShort(columns[this.rankColumn]);
		double fraginten = Double.parseDouble(columns[this.fragIntenColumn]);
		double inten = Double.parseDouble(columns[this.intenColumn]);

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

		MaxQuantPeptide peptide = new MaxQuantPeptide(scanNum, sequence, charge, mh,
		        deltamh, NTT, rank, score, PEP, references, pi, this);
		
		peptide.setFragInten(fraginten);
		peptide.setInten(inten);
		peptide.setHydroScore(hydroScore);
		peptide.setRetentionTime(rt);
		peptide.setPepLocAroundMap(locAroundMap);

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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#simpleFormat(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public String simpleFormat(IMaxQuantPeptide peptide) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getScanNum()).append("\t");
		sb.append(peptide.getMH()).append("\t");
		sb.append(peptide.getAbsoluteDeltaMZppm()).append("\t");
		sb.append(peptide.getCharge()).append("\t");
		sb.append(peptide.getSequence()).append("\t");
		sb.append(peptide.getScore()).append("\t");
		sb.append(peptide.getPEP()).append("\t");
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat#type()
	 */
	@Override
	public PeptideType type() {
		// TODO Auto-generated method stub
		return PeptideType.MaxQuant;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IFormat#getTitle()
	 */
	@Override
	public String[] getTitle() {
		// TODO Auto-generated method stub

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

		strs[this.scoreColumn] = Score;
		strs[this.PEPColumn] = PEP;
		strs[this.fragIntenColumn] = fragmentInten;
		strs[this.intenColumn] = inten;
		strs[this.rtColumn] = retentionTime;

		if(this.hydroScoreColumn >= 0)
			strs[this.hydroScoreColumn] = HydroScore;	
		
		if (this.simColumn >= 0)
			strs[this.simColumn] = SIM;

		if (this.ascoreColumn >= 0)
			strs[this.ascoreColumn] = ASCORE;

		return strs;
	
	}
	
	
}
