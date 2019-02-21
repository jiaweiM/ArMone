/* 
 ******************************************************************************
 * File: AbstractPeptide.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequenceUpdateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.pi.PICalculator;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * An easy to use abstract peptide.
 * <p>
 * Implemented methods: getProteinReferenceString()
 * 
 * <p>
 * Changes
 * <li>0.2.4, 02-20-2009: use the IModifiedPeptideSequence to contain the
 * peptide sequence. Add constructor using IModifiedPeptideSequence instance.
 * <li>0.2.5, 02-20-2009: remove the solid instance of String-sequence. Use the
 * instance of pepsequence instead.
 * <li>0.2.6, 02-27-2009: Change foramtter into field which one should specified
 * which constructing.
 * <li>0.2.7, 03-03-2009: Use IKnownFormatScanName
 * <li>0.2.8, 05-03-2009: add {@link #setPeptideFormat(IPeptideFormat)}
 * <li>0.2.9, 09-07-2009: Add rule for target decoy parsing, decoy references in composite references will be removed.
 * 
 * @author Xinning
 * @version 0.2.9, 09-07-2009, 16:59:20
 */
public abstract class AbstractPeptide implements IPeptide {
	
	/**
	 * If remove the composite references in peptides which can identified from both target and
	 * deocy proteins.
	 */
	public static final boolean isRemoveCompositeReferences = true;
	
	/**
	 * The default enzyme. If the enzyme is not specifically assigned for a new instance 
	 * of peptide, this will be used as the default enzyme. After changing of this value, 
	 * the enzyme of all the <b>new</b> peptide instances which be this value.
	 */
	public static Enzyme ENZYME_DEFAULT = Enzyme.TRYPSIN;
	
	// Rank in peptide identification list
	private short rank;
	private String scanNum;
	private IModifiedPeptideSequence pepsequence;
	// For high accuracy mass spectrometer, using double
	private double mh;
	// MH - theoMH
	private double deltamh;
	
//	private double mr;

	// Delta m/z in ppm, equals deltamass/mh
	private double deltamzppm = 10E20;

	private short charge;

	private short NumofTerms = -1;

	// If this peptide is a true positive peptide: from forward sequence;
	private boolean isTP = true;
	private float pi;

	// if the peptide passed the threshold;
	private boolean isUsed = true;

	/*
	 * The probability of this peptide If not assigned this value is -1;
	 */
	private float probability = -1f;

	/**
	 * The protein references which are merged togeter with the splitter of '$'.
	 */
	private HashSet <ProteinReference> references = null;
	
	/**
	 * The location of the peptide in a protein
	 */
//	private HashMap <ProteinReference, int []> pepLocMap;
	
	/**
	 * 
	 */
	private HashMap <String, SeqLocAround> locAroundMap;

	/*
	 * Formatter used to format the peptide into the toString value. Change this
	 * value to generated different output peptide strings.
	 */
	private IPeptideFormat formatter;

	/*
	 * The id string for this peptide. Equals scannumber+charge+peptide sequence
	 * Commonly, only peptides come from same scan with same charge and sequence
	 * are consider as the same peptide.
	 */
	private String idstr;

	/*
	 * The formatted scan name will be parsed into this instance.
	 */
	private IScanName scanname;

	/**
	 * the enzyme 
	 */
	private Enzyme enzyme = ENZYME_DEFAULT;
	
	/*
	 * Number of miss cleave sites
	 */
	private short misscleave = -1;
	private float sim = -1f;

	private double[] ascores;
	private double intensity;
	
	private double fragInten;
	
	private LabelType labeltype;
		
	private double hydroScore;
	
	private double retentionTime;
	
	private float primaryScore;
	
	private float qValue;
	
	private String delegateRef;

	public AbstractPeptide(IPeptide pep){
		
		this.setSequence(pep.getSequence());
		this.setCharge(pep.getCharge());
		this.setMH(pep.getMH());
		this.setDeltaMH(pep.getDeltaMH());
		this.setRank(pep.getRank());
		this.setProteinReference(pep.getProteinReferences());
		this.isTP = isTp(this.references);

		this.setPI(pep.getPI());
		this.setNumberofTerm(pep.getNumberofTerm());

		this.setScanNum(pep.getBaseName(), pep.getScanNumBeg(), pep.getScanNumEnd());
		this.setRetentionTime(pep.getRetentionTime());
		
		this.setPrimaryScore(pep.getPrimaryScore());
		this.locAroundMap = new HashMap <String, SeqLocAround>();
		this.locAroundMap.putAll(pep.getPepLocAroundMap());

		this.formatter = pep.getPeptideFormat();
	}
	

	/**
	 * @param sequence
	 * @param refs
	 * @param scanName
	 */
	public AbstractPeptide(String sequence, HashSet<ProteinReference> refs, String scanName){
		this.setSequence(sequence);
		this.setProteinReference(refs);
		this.setScanNum(scanName, 0, 0);
//		this.setScanNum(scanName);
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	public AbstractPeptide(String sequence, short charge, HashSet<ProteinReference> refs, String scanName){
		this.setSequence(sequence);
		this.setCharge(charge);
		this.setProteinReference(refs);
		this.setScanNum(scanName);
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}
	
	/**
	 * This is a simple peptide constructor only used in quantitation
	 * @param sequence
	 * @param refs
	 */
	public AbstractPeptide(String sequence, short charge, HashSet<ProteinReference> refs, String scanName, 
			int scanBeg, int scanEnd, HashMap <String, SeqLocAround> locAroundMap){
		
		this.setSequence(sequence);
		this.setCharge(charge);
		this.setProteinReference(refs);
		this.setScanNum(scanName, scanBeg, scanEnd);
		this.locAroundMap = locAroundMap;
	}
	
	protected AbstractPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, HashSet<ProteinReference> refs, IPeptideFormat<?> formatter) {

		this.setSequence(sequence);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(refs);

		this.setPI((float) PICalculator
		        .compute(pepsequence.getUniqueSequence()));

		this.setScanNum(baseName, scanNumBeg, scanNumEnd);
		this.formatter = formatter;
		
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(IScanName scanName, String sequence,
	        short charge, double mh, double deltaMs, short rank,
	        HashSet<ProteinReference> refs, IPeptideFormat<?> formatter) {

		this.setSequence(sequence);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI((float) PICalculator
		        .compute(pepsequence.getUniqueSequence()));

		if (scanName != null)
			this.setScanNum(scanName);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, HashSet<ProteinReference> refs,
	        IPeptideFormat<?> formatter) {

		this.setSequence(sequence);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI((float) PICalculator
		        .compute(pepsequence.getUniqueSequence()));

		if (scanNum != null)
			this.setScanNum(scanNum);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String scanNum, String sequence, short charge,
	        double mh, double deltaMs, short rank, HashSet<ProteinReference> refs,
	        float pi, short numofTerm, IPeptideFormat<?> formatter) {

		this.setSequence(sequence);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI(pi);
		this.setNumberofTerm(numofTerm);

		if (scanNum != null)
			this.setScanNum(scanNum);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        String sequence, short charge, double mh, double deltaMs,
	        short rank, HashSet<ProteinReference> refs, float pi, short numofTerm,
	        IPeptideFormat<?> formatter) {

		this.setSequence(sequence);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI(pi);
		this.setNumberofTerm(numofTerm);

		this.setScanNum(baseName, scanNumBeg, scanNumEnd);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        IModifiedPeptideSequence mpepseq, short charge, double mh,
	        double deltaMs, short rank, HashSet<ProteinReference> refs,
	        IPeptideFormat<?> formatter) {

		this.setSequence(mpepseq);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI((float) PICalculator
		        .compute(pepsequence.getUniqueSequence()));

		this.setScanNum(baseName, scanNumBeg, scanNumEnd);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(IScanName scanName,
	        IModifiedPeptideSequence mpepseq, short charge, double mh,
	        double deltaMs, short rank, HashSet<ProteinReference> refs,
	        IPeptideFormat<?> formatter) {

		this.setSequence(mpepseq);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI((float) PICalculator
		        .compute(pepsequence.getUniqueSequence()));

		if (scanName != null)
			this.setScanNum(scanName);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String scanNum, IModifiedPeptideSequence mpepseq,
	        short charge, double mh, double deltaMs, short rank,
	        HashSet<ProteinReference> refs, IPeptideFormat<?> formatter) {

		this.setSequence(mpepseq);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI((float) PICalculator
		        .compute(pepsequence.getUniqueSequence()));

		if (scanNum != null)
			this.setScanNum(scanNum);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String scanNum, IModifiedPeptideSequence mpepseq,
	        short charge, double mh, double deltaMs, short rank,
	        HashSet<ProteinReference> refs, float pi, short numofTerm,
	        IPeptideFormat<?> formatter) {

		this.setSequence(mpepseq);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI(pi);
		this.setNumberofTerm(numofTerm);

		if (scanNum != null)
			this.setScanNum(scanNum);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	protected AbstractPeptide(String baseName, int scanNumBeg, int scanNumEnd,
	        IModifiedPeptideSequence mpepseq, short charge, double mh,
	        double deltaMs, short rank, HashSet<ProteinReference> refs, float pi,
	        short numofTerm, IPeptideFormat<?> formatter) {

		this.setSequence(mpepseq);
		this.setCharge(charge);
		this.setMH(mh);
		this.setDeltaMH(deltaMs);
		this.setRank(rank);
		this.setProteinReference(refs);
		this.isTP = isTp(this.references);

		this.setPI(pi);
		this.setNumberofTerm(numofTerm);

		this.setScanNum(baseName, scanNumBeg, scanNumEnd);

		this.formatter = formatter;
		this.locAroundMap = new HashMap <String, SeqLocAround>();
	}

	/**
	 * Test whether the peptide from the list of protein references is true
	 * positive. <b>Only if all the protein references are decoy references,
	 * this peptide is considered as the decoy peptide.</b>
	 * 
	 * @param proteins
	 *            the references which was identified by the peptide
	 * @return
	 */
	protected static final boolean isTp(Collection<ProteinReference> proteins) {

		boolean contain_target = false;
		boolean contain_decoy = false;
		
		for (Iterator<ProteinReference> it = proteins.iterator(); it.hasNext();) {
			if (it.next().isDecoy()) {
				if(!contain_decoy) {
					contain_decoy = true;
				}
			}
			else {
				if(!contain_target) {
					contain_target = true;
				}
			}
		}
		
		
		if(contain_target && contain_decoy) {
			
			/*
			 * Remove the decoy references
			 */
			if(isRemoveCompositeReferences) {
				for (Iterator<ProteinReference> it = proteins.iterator(); it.hasNext();) {
					if(it.next().isDecoy())
						it.remove();
				}
			}
			
			/*
			 * Considered as target identification
			 */
			return true;
		}

		return contain_target? true : false;
	}

	/**
	 * HashSet the charge state of this peptide
	 * 
	 * @param charge
	 */
	public void setCharge(short charge) {
		this.charge = charge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getCharge()
	 */
	public short getCharge() {
		return this.charge;
	}

	/**
	 * HashSet the rank for this peptide identification.
	 * 
	 * @param rank
	 */
	public void setRank(short rank) {
		this.rank = rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getRank()
	 */
	@Override
	public short getRank() {
		return this.rank;
	}

	/**
	 * HashSet the delta MH value (The deltaMH = MH - theoroticalMH )
	 * 
	 * @since 0.2.1
	 * @param deltaMH
	 */
	public void setDeltaMH(double deltaMH) {
		this.deltamh = deltaMH;

		this.deltamzppm = this.deltamh / charge
		        / this.getPreCursorIonMZ() * 1E6;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getDeltaMH()
	 */
	public double getDeltaMH() {
		return this.deltamh;
	}

	/**
	 * HashSet the MH+ value of this peptide
	 * 
	 * @param mh
	 */
	public void setMH(double mh) {
		this.mh = mh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getMH()
	 */
	@Override
	public double getMH() {
		return this.mh;
	}
	
	public double getMr() {
		return this.mh-AminoAcidProperty.PROTON_W;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getExperimentalMZ()
	 */
	@Override
	public double getExperimentalMZ() {
		return (this.mh + this.deltamh - 1.00782) / this.charge + 1.00782;
	}

	/**
	 * HashSet the pI
	 * 
	 * @param pI
	 */
	protected void setPI(float pI) {
		this.pi = pI;
	}

	@Override
	public float getPI() {
		return this.pi;
	}

	/**
	 * HashSet the probability for this peptide identification
	 * 
	 * @param probability
	 */
	public void setProbability(float probability) {
		this.probability = probability;
	}

	@Override
	public float getProbabilty() {
		return this.probability;
	}

	@Override
	public HashSet <ProteinReference> getProteinReferences() {
		return this.references;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.
	 * IPeptide#setScanNum(java.lang.String)
	 */
	@Override
	public void setScanNum(String scanNum) {
		if (scanNum == null) {
			System.err
			        .println("ScanNum is null, the original scan number will not be changed.");
			return;
		}

		this.scanname = ScanNameFactory.parseName(scanNum);
		this.setScanNum(this.scanname);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#setBasename(java.lang
	 * .String)
	 */
	@Override
	public boolean setBasename(String baseName) {
		// The scan name is with known format and can be parsed.
		if (this.scanname instanceof IKnownFormatScanName) {
			this.scanname.setBaseName(baseName);
			this.scanNum = ((IKnownFormatScanName) this.scanname)
			        .getFormattedScanName().getScanName();

			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#setScanNum(java.lang.
	 * String, int, int)
	 */
	public void setScanNum(String baseName, int scanNumBeg, int scanNumEnd) {
		this.scanname = new SequestScanName(baseName, scanNumBeg, scanNumEnd,
		        charge, null);

		this.setScanNum(this.scanname);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#setScanNum(
	 * cn.ac.dicp.gp1809.proteome.util.IScanName)
	 */
	public void setScanNum(IScanName scanNum) {

		if (scanNum == null) {
			System.err
			        .println("ScanNum is null, the original scan number will not be changed.");
			return;
		}

		this.scanname = scanNum;

		// The scan name is with known format and can be parsed.
		if (this.scanname instanceof IKnownFormatScanName) {
			this.scanNum = ((IKnownFormatScanName) this.scanname)
			        .getFormattedScanName().getScanName();
		} else {
			this.scanNum = scanNum.getScanName();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getScanNum()
	 */
	@Override
	public String getScanNum() {
		return this.scanNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getBaseName()
	 */
	public String getBaseName() {
		if (this.scanname == null)
			this.getScanNum(this.getScanNum());

		return this.scanname.getBaseName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getScanNumBeg()
	 */
	public int getScanNumBeg() {
		if (this.scanname == null)
			this.getScanNum(this.getScanNum());

		return this.scanname.getScanNumBeg();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getScanNumEnd()
	 */
	public int getScanNumEnd() {
		if (this.scanname == null)
			this.getScanNum(this.getScanNum());

		return this.scanname.getScanNumEnd();
	}

	/*
	 * Parse the formatted scan number into detail informations: basename +
	 * scanNumBeg + scanNumEnd
	 * 
	 * @throws IllegalArgumentException is the scan string is not formatted
	 * properly
	 */
	private final void getScanNum(String scan) {

		if (scan == null)
			throw new NullPointerException(
			        "Can't generated scan number informations from a null scan string.");

		try {
			int position;
			String temp = scan;
			String baseName = null;
			int scanNumBeg;
			int scanNumEnd;

			if ((position = scan.indexOf(',')) > 0) {
				baseName = temp.substring(0, position);
				temp = temp.substring(position + 1, scan.length()).trim();
			}

			if ((position = temp.indexOf('-')) > 0) {
				scanNumBeg = Integer.parseInt(temp.substring(0, position)
				        .trim());
				scanNumEnd = Integer.parseInt(temp.substring(position + 1,
				        temp.length()).trim());
			} else {
				scanNumBeg = scanNumEnd = Integer.parseInt(temp);
			}

			this.scanname = new SequestScanName(baseName, scanNumBeg,
			        scanNumEnd, this.charge, null);

		} catch (Exception e) {
			throw new IllegalArgumentException(
			        "The input scan number must be formatted as: \r\n"
			                + "\"XXXXX, scanNumber\"\r\n"
			                + "or \"XXXXX, scanNumBeg - scanNumEnd\"  if the dta is a grouped dta file");
		}
	}

	/**
	 * Number of enzymatic terminals. For tryptic peptides, this value is
	 * commonly called NTT (number of tryptic terminals)
	 * 
	 * @param not
	 */
	protected void setNumberofTerm(short not) {
		this.NumofTerms = not;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#getNumberofTerm()
	 */
	public short getNumberofTerm() {
		if (this.NumofTerms < 0)
			this.NumofTerms = getEnzyme().getNumberofTerm(this.pepsequence);

		return this.NumofTerms;
	}

	/**
	 * HashSet the sequence. If the sequence of this peptide has not been
	 * initialized, this will set the sequence for this peptide. Otherwise, this
	 * behave as updateSequence(String).
	 * 
	 * @param sequence
	 * @throws NullPointerException
	 *             is the sequence is null
	 */
	public void setSequence(String sequence) {

		if (sequence == null)
			throw new NullPointerException(
			        "The sequence of peptide must not be null.");

		if (this.pepsequence == null) {
			this.pepsequence = ModifiedPeptideSequence.parseSequence(sequence);
		} else {
			this.updateSequence(sequence);
		}
	}

	/**
	 * Set the sequence. If the sequence of this peptide has not been
	 * initialized, this will set the sequence for this peptide. Otherwise, this
	 * behave as updateSequence(String).
	 * 
	 * @param mpepseq
	 * @throws NullPointerException
	 *             is the sequence is null
	 */
	public void setSequence(IModifiedPeptideSequence mpepseq) {

		if (mpepseq == null)
			throw new NullPointerException(
			        "The sequence of peptide must not be null.");

		if (this.pepsequence == null) {
			this.pepsequence = mpepseq;
		} else {
			this.updateSequence(mpepseq);
		}
	}

	@Override
	public String getSequence() {
		return this.pepsequence.getFormattedSequence();
	}

	@Override
	public IModifiedPeptideSequence getPeptideSequence() {
		return this.pepsequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#isUsed()
	 */
	@Override
	public boolean isUsed() {
		return this.isUsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#setUsed(boolean)
	 */
	public boolean setUsed(boolean isused) {
		return (this.isUsed = isused);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#setProteinReference
	 * (java.util.List)
	 */
	@Override
	public void setProteinReference(HashSet<ProteinReference> references) {
		if (references == null || references.size() == 0) {
			throw new NullPointerException(
			        "Can not assign a null references to the peptide");
		}

		this.references = references;
	}

	@Override
	public void updateSequence(String newSeq) throws SequenceUpdateException {
		if (newSeq == null) {
			System.err
			        .println("Skip the action: try to update the sequence as null.");
			return;
		}
/*
		String seq = PeptideUtil.getUniqueSequence(newSeq);

		if (!seq.equals(this.getPeptideSequence().getUniqueSequence()))
			throw new SequenceUpdateException(
			        "The new sequence must be with the same unique "
			                + "sequence as the original one.");
*/
		this.pepsequence = ModifiedPeptideSequence.parseSequence(newSeq);
	}

	@Override
	public void updateSequence(IModifiedPeptideSequence newSeq)
	        throws SequenceUpdateException {

		if (newSeq == null) {
			System.err
			        .println("Skip the action: try to update the sequence as null.");
			return;
		}

		if (!newSeq.getUniqueSequence().equals(
		        this.getPeptideSequence().getUniqueSequence()))
			throw new SequenceUpdateException(
			        "The new sequence must be with the same unique "
			                + "sequence as the original one.");

		this.pepsequence = newSeq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#
	 * getProteinReferenceString()
	 */
	@Override
	public final String getProteinReferenceString() {
		
		StringBuilder sb = new StringBuilder();
		ProteinReference pr = null;
		
		Iterator<ProteinReference> iterator = this.getProteinReferences()
		        .iterator();
		
		if(iterator.hasNext())
			pr = iterator.next();
		
		sb.append(pr);
//System.out.println(pr.toString()+"\t"+this.getSequence());
		SeqLocAround sla = this.getPepLocAroundMap().get(pr.toString());
		sb.append(SeqLocAround.spliter).append(sla.toString());

		while (iterator.hasNext()) {
			pr = iterator.next();
			sb.append(ProteinNameSpliter).append(pr);
			sla = this.getPepLocAroundMap().get(pr.toString());
			sb.append(SeqLocAround.spliter).append(sla.toString());
		}
		return sb.toString();
	}

	public String getReferenceOutString(){
		
		StringBuilder sb = new StringBuilder();
		ProteinReference pr = null;
		
		Iterator<ProteinReference> iterator = this.getProteinReferences()
		        .iterator();
		
		pr = iterator.next();
		sb.append(pr.getName());
		
//		int [] loc = this.getPepLocMap().get(pr);
//		sb.append(",").append(loc[0]).append("~").append(loc[1]);

		while (iterator.hasNext()) {
			pr = iterator.next();
			sb.append(";").append(pr.getName());
//			loc = this.getPepLocMap().get(pr);
//			sb.append(",").append(loc[0]).append("~").append(loc[1]);
		}
//		System.out.println(sb);
		return sb.toString();
	}
	/**
	 * If this peptide identified more than one proteins, add them in one by
	 * one. The protein can be the full or partial name of the protein, or
	 * formatted ProteinReference "name(000000)".
	 * 
	 * @see ProteinReference;
	 * @param protein
	 *            addtional protein the peptide can result in.
	 */
	public ProteinReference addProteinReference(String protein) {
		ProteinReference ref = ProteinReference.parse(protein);
		this.references.add(ref);
		return ref;
	}

	/**
	 * If this peptide identified more than one proteins, add them to the
	 * protein reference list one by one.
	 * 
	 * @param ref
	 *            additional protein the peptide can result in.
	 */
	public void addProteinReference(ProteinReference ref) {
		this.references.add(ref);
	}

	/**
	 * The number of proteins which can be identified by this protein
	 * 
	 * @return
	 */
	public final int getNumberofProtein() {
		return this.references.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.proteome.IPeptide#isTP()
	 */
	public boolean isTP() {
		return this.isTP;
	}

	/**
	 * The number of miss cleave sites for this peptide sequence.
	 * 
	 * @return
	 */
	public short getMissCleaveNum() {
		if (this.misscleave < 0) {
			this.misscleave = getEnzyme().getMissCleaveNum(this.pepsequence);
		}
		return misscleave;
	}

	/**
	 * The precursor ion mz value for this peptide identification. This value is
	 * calculated from the mh, deltamass and charge state, by calculating
	 * ((mh+deltamass)-1)/charge+1
	 * 
	 * @return
	 */
	public double getPreCursorIonMZ() {
		return (this.mh + this.deltamh - 1.00782d) / this.charge + 1.00782d;
	}

	/**
	 * Get the absolute delta mz value in ppm unit. That is the absolute value
	 * of delta mz ppm (|delta mz ppm|).
	 * 
	 * @return
	 */
	public double getAbsoluteDeltaMZppm() {
		return Math.abs(deltamzppm);
	}
	
	public double getDeltaMZppm(){
		return this.deltamzppm;
	}

	/**
	 * The ID String for peptide. Peptides with the same ID are considered as
	 * completely the same. The value is scannumber+charge+sequence.
	 * 
	 * @return the ID for peptide
	 */
	public String getID() {
		if (this.idstr == null)
			this.parseId();
		return this.idstr;
	}

	/*
	 * only when the peptides with same scan number, charge state and same
	 * sequence are considered as the same.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this.idstr == null)
			this.parseId();

		if (obj instanceof AbstractPeptide) {
			if (((AbstractPeptide) obj).idstr.equals(idstr))
				return true;
		}

		return false;
	}

	/*
	 * The id for this peptide
	 */
	private void parseId() {
		String seq = this.pepsequence.getSequence();
		this.idstr = new StringBuilder(this.scanNum.length() + seq.length() + 1)
		        .append(this.scanNum).append(this.charge).append(seq)
		        .toString();
	}

	/*
	 * The hash code is equals to the hashcode of idstr
	 * scannumber+charge+sequence.
	 */
	@Override
	public int hashCode() {
		if (idstr == null)
			this.parseId();

		return this.idstr.hashCode();
	}

	/**
	 * The similarity value between the experimental and theoretical spectra.
	 * <p>
	 * If the sim value is not calculated, the returned value is -1f;
	 * <p>
	 * <p>
	 * The sim value is defined as described in paper:
	 * <p>
	 * 1. Z. Zhang, "Prediction of low-energy collision-induced dissociation
	 * spectra of peptides".
	 * <p>
	 * Anal. Chem. (2004), 76(14), 3908-3922.
	 * <p>
	 * 2. Z. Zhang, "Prediction of Low-Energy Collision-Induced Dissociation
	 * Spectra of Peptides
	 * <p>
	 * with Three or More Charges", Anal. Chem. (2005), 77(19), 6364-6373.
	 * <p>
	 * <p>
	 * And the sim score is calculated by the KineticModel.dll provided by Z.Q.
	 * Zhang in Amgen
	 */
	public void setSim(float sim) {
		this.sim = sim;
	}

	/**
	 * The similarity value between the experimental and theoretical spectra.
	 * <p>
	 * If the sim value is not calculated, the returned value is -1f;
	 * <p>
	 * <p>
	 * The sim value is defined as described in paper:
	 * <p>
	 * 1. Z. Zhang, "Prediction of low-energy collision-induced dissociation
	 * spectra of peptides".
	 * <p>
	 * Anal. Chem. (2004), 76(14), 3908-3922.
	 * <p>
	 * 2. Z. Zhang, "Prediction of Low-Energy Collision-Induced Dissociation
	 * Spectra of Peptides
	 * <p>
	 * with Three or More Charges", Anal. Chem. (2005), 77(19), 6364-6373.
	 * <p>
	 * <p>
	 * And the sim score is calculated by the KineticModel.dll provided by Z.Q.
	 * Zhang in Amgen
	 */
	public float getSim() {
		return this.sim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getAscores()
	 */
	@Override
	public double[] getAscores() {
		return this.ascores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#setAscores(double[])
	 */
	@Override
	public void setAscores(double[] ascores) {
		this.ascores = ascores;
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#setEnzyme(cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme)
	 */
	public void setEnzyme(Enzyme enzyme) {
		if(enzyme == null)
			throw new NullPointerException("Null enzyme input!");
		
		this.misscleave = -1;
		this.NumofTerms = -1;
		
		this.enzyme = enzyme;
	}
	
	public Enzyme getEnzyme(){
		return this.enzyme;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#getPeptideFormat()
	 */
	@Override
	public IPeptideFormat<?> getPeptideFormat() {
		return formatter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide#setPeptideFormat(cn.ac
	 * .dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat)
	 */
	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {
		if (format != null)
			this.formatter = format;
	}

	public void formatReference(IFastaAccesser accesser) {
			// throw new IllegalArgumentException("The reference of peptides can
			// only be formatted once.");

		for (Iterator<ProteinReference> it = this.references.iterator(); it
			       .hasNext();) {
			ProteinReference pref = it.next();
			String ref = pref.getName();
				
			/*
			 * In very few cases, the partial reference of the same protein
			 * outputted by sequest may be with different lengths, e.g.
			 * IPI:IPI00218816.4|SWISS-PROT:P68871|TREMBL and
			 * IPI:IPI00218816.4|SWISS-PROT:, to avoid assamble them as two
			 * different proteins , the reference must be formatted into the
			 * same length.
			 */
			if (accesser.getDecoyJudger().isDecoy(ref)) {
				if (ref.length() > accesser.getSplitRevLength())
					ref = ref.substring(0, accesser.getSplitRevLength());
			} else {
				if (ref.length() > accesser.getSplitLength())
					ref = ref.substring(0, accesser.getSplitLength());
			}

			// -----------------------------------------------------------//
			// Refresh the name by using the reference with minimum length
			pref.setName(ref);
		}
	}

	public void setInten(double inten){
		this.intensity = inten;
	}
	
	public double getInten(){
		return this.intensity;
	}
	
	public void setFragInten(double fragInten){
		this.fragInten = fragInten;
	}
	
	public double getFragInten(){
		return fragInten;
	}
	
	/**
	 * The label type of the peptide.
	 * @return
	 */
	public LabelType getLabelType(){
		return labeltype;
	}
	
	public void setLabelType(LabelType type){
		this.labeltype = type;
	}
	
	public double getHydroScore(){
		return this.hydroScore;
	}
	
	public void setHydroScore(double hydroScore){
		this.hydroScore = hydroScore;
	}
	
	public double getRetentionTime(){
		return this.retentionTime;
	}
	
	public void setRetentionTime(double retentionTime){
		this.retentionTime = retentionTime;
	}
	
	public void setTP(boolean isTp){
		this.isTP = isTp;
	}
	
	public void setPrimaryScore(float primaryScore){
		this.primaryScore = primaryScore;
	}
	
	public float getPrimaryScore(){
		return this.primaryScore;
	}
	
	public HashMap <String, SeqLocAround> getPepLocAroundMap(){
		return this.locAroundMap;
	}
	
	public void setPepLocAroundMap(HashMap <String, SeqLocAround> locAroundMap){
		this.locAroundMap = locAroundMap;
	}
	
	public void addPepLocAround(String ref, SeqLocAround sla){
		this.locAroundMap.put(ref, sla);
	}
	
	public void setDelegateReference(String delegateRef){
		this.delegateRef = delegateRef;
	}
	
	public String getDelegateReference(){
		return this.delegateRef;
	}
	
	public String getPepInfo(){
		return delegateRef+formatter.format(this);
	}
	
	public void setQValue(float qValue){
		this.qValue = qValue;
	}
	
	public float getQValue(){
		return this.qValue;
	}
	
	@Override
	public String toString() {
		return this.formatter.format(this);
	}
	
	public String toSimpleInfoFormat() {
		return this.formatter.simpleFormat(this);
	}
	
}
