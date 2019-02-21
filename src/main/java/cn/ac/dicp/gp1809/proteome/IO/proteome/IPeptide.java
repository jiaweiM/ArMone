/*
 ******************************************************************************
 * File: IPeptide.java * * * Created on 06-06-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequenceUpdateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * This interface is mainly for the group of protein. Containing all the
 * information for the group construction.
 * 
 * <p>
 * Changes:
 * <li>0.2.8, 02-20-2009, Use the instance IModifiedPeptideSequence to instead
 * IPeptideSequence. Add updateSequence(IModifiedPeptideSequence)
 * <li>0.2.9, 04-13-2009: Add {@link #getExperimentalMZ()}
 * <li>0.3.1, 05-03-2009: add {@link #setPeptideFormat(IPeptideFormat)}
 * 
 * @author Xinning
 * @version 0.3.1, 05-03-2009, 20:11:49
 */
public interface IPeptide {

	/**
	 * While the peptide can indicate more than one proteins, the name of each
	 * protein is splited by this char and return a String;
	 */
	public static final char ProteinNameSpliter = '$';

	/**
	 * After database search, there commonly more than one peptide can match to
	 * the spectrum. The peptide with the biggest identification score will be
	 * considered as the best match and is commonly reported as the peptide
	 * match to the spectrum. The rank of best match is 1, the secod best match
	 * will be 2 and so on.
	 * 
	 * @since 0.2.4
	 * @return the rank of the peptide for the spctrum. If the rank is
	 *         undetectable (e.g. bioworks exported file), 0 will be returned.
	 */
	public short getRank();

	/**
	 * Get the Formatter for the peptide output format.
	 * 
	 * @return
	 */
	public IPeptideFormat<?> getPeptideFormat();

	/**
	 * Set the Formatter for the peptide output format.
	 * 
	 * @since 0.3.1
	 * @param format
	 */
	public void setPeptideFormat(IPeptideFormat<?> format);

	/**
	 * Get the enzyme used for the digestion of peptide and thereby the database
	 * search
	 * 
	 * @return
	 */
	public Enzyme getEnzyme();

	/**
	 * The proteins which contain this peptide sequence. If more than one
	 * protein contains this peptide, then each reference
	 * 
	 * @return
	 */
	public HashSet<ProteinReference> getProteinReferences();

	/**
	 * Get the formatted protein references String for this peptide. If the
	 * peptide identified more than one proteins, each protein will be split
	 * with ProteinNameSpliter.
	 * <p>
	 * For example: ref1(000000)+beg+end+pre7aa+next7aa$ref2(000000)+beg+end+pre7aa+next7aa
	 * 
	 * @return
	 */
	public String getProteinReferenceString();
	
	/**
	 * For example: ref1(000000),beg~end;ref2(000000),beg~end
	 * @return
	 */
	public String getReferenceOutString();
	
	public void setDelegateReference(String delegateRef);
	
	public String getDelegateReference();

	/**
	 * Proteins are identified by the peptides in shorgun proteomics. For
	 * convenience, some peptides with quite high scores but failed to pass the
	 * criteria are mark with false instead of removal. If a peptide is marked
	 * with false, then it is not used for protein identifications.
	 * 
	 * @return true if the peptide is used for protein identification, else
	 *         false;
	 */
	public boolean isUsed();

	/**
	 * @return the parsed original sequence of this peptide
	 */
	public IModifiedPeptideSequence getPeptideSequence();

	/**
	 * The sequence string which is obtained directly from database search. In
	 * the format of A.AAAAA#AAA.A
	 * 
	 * @return
	 */
	public String getSequence();

	/**
	 * The charge state of this peptide identification
	 * 
	 * @return
	 */
	public short getCharge();

	/**
	 * The probability for this peptide identification. If this probability is
	 * less than 0 (Commonly -1), this means that the probability is not
	 * calcualated by models.
	 * 
	 * @return
	 */
	public float getProbabilty();

	/**
	 * Set the probability for this peptide identification
	 * 
	 * @since 0.2.5
	 * @param probability
	 */
	public void setProbability(float probability);

	/**
	 * The primary score of this peptide identification. This score is used to
	 * judge which peptide is the top hit peptide for a single spectrum.
	 * Therefore, this value is with different meanings for different search
	 * engines.
	 * 
	 * <div>For SEQUEST: this score is Xcorr</div> <div>For Mascot: this score
	 * is IonScore</div> <div>For OMMSA: this score is the p-value</div><div>For
	 * X!Tandem: this score is the E-value</div><div>For CombPeptide: this score
	 * is the probability</div>
	 * 
	 * @return
	 */
	public float getPrimaryScore();
	
	/**
	 * 
	 * @param score
	 */
	public void setPrimaryScore(float score);

	/**
	 * ScanNumber String which is formatted as
	 * <p/>
	 * "XXXXX, scanNumber"
	 * <p/>
	 * or "XXXXX, scanNumBeg - scanNumEnd" if the dta is a grouped dta file
	 * 
	 * @return
	 */
	public String getScanNum();

	/**
	 * Set the scan number string for this peptide. This method is mainly for
	 * the case that the format of scanNumber needs to be changed or the scan
	 * number has not been assigned. The dta formatted scan number will be
	 * automatically translated to XXXX, 000 or XXXX, 000 - 000 if this is a
	 * grouped scan.
	 * <p>
	 * <b>Notice: The charge state of this peptide will not be changed even
	 * though the scan number to be set contains charge state information</b>
	 * 
	 * <p>
	 * Be care of this method because the scan number is the basic attribute for
	 * this peptide identification.
	 * 
	 * @since 0.2.1
	 */
	public void setScanNum(String scanNum);

	/**
	 * Set the scan number string for this peptide.
	 * 
	 * @since 0.2.3
	 * 
	 * @param baseName
	 * @param scanNumBeg
	 * @param scanNumEnd
	 */
	public void setScanNum(String baseName, int scanNumBeg, int scanNumEnd);

	/**
	 * The begin scan number of this peptide identification. If this Peptide is
	 * a peptide pair (e.g. peptide with MS2 and MS3). This scan number
	 * indicates the ms2 scan number.
	 * 
	 * <p>
	 * <b>Warnning: some exceptions will be threw is the scan number is not
	 * formatted as
	 * <p/>
	 * "XXXXX, scanNumber"
	 * <p/>
	 * or "XXXXX, scanNumBeg - scanNumEnd"</b>
	 * 
	 * @return
	 */
	public int getScanNumBeg();

	/**
	 * Sometimes scans with precursor mass less than 1.4Da will be grouped as a
	 * single dta file return the end scan number; else the begin scan number
	 * equals to the end scan number;
	 * 
	 * <p>
	 * <b>Warnning: some exceptions will be threw is the scan number is not
	 * formatted as
	 * <p/>
	 * "XXXXX, scanNumber"
	 * <p/>
	 * or "XXXXX, scanNumBeg - scanNumEnd"</b>
	 * 
	 * @return end scan number
	 */
	public int getScanNumEnd();

	/**
	 * Update the peptide sequence, and refresh all the relative informations.
	 * This method is used ONLY for the change of peptide sequence with
	 * modifications into new forms, the unique peptide sequence MUST not be
	 * changed. In other words, the unique sequence of this new sequence must be
	 * the same as the original unique sequence
	 * 
	 * @param newSeq
	 * @throws SequenceUpdateException
	 *             if the new sequence is with different unique sequence of the
	 *             original one.
	 */
	public void updateSequence(String newSeq) throws SequenceUpdateException;

	/**
	 * Update the peptide sequence, and refresh all the relative informations.
	 * This method is used ONLY for the change of peptide sequence with
	 * modifications into new forms, the unique peptide sequence MUST not be
	 * changed. In other words, the unique sequence of this new sequence must be
	 * the same as the original unique sequence
	 * 
	 * @param newSeq
	 * @throws SequenceUpdateException
	 *             if the new sequence is with different unique sequence of the
	 *             original one.
	 */
	public void updateSequence(IModifiedPeptideSequence newSeq)
	        throws SequenceUpdateException;

	/**
	 * Set the protein reference list for this peptide identification
	 * 
	 * @param references
	 */
	public void setProteinReference(HashSet<ProteinReference> references);

	/**
	 * MH+ value of this peptide theoretically.
	 * 
	 * @return
	 */
	public double getMH();
	
	/**
	 * Mr value of this peptide theoretically.
	 * 
	 * @return
	 */
	public double getMr();

	/**
	 * The deltaMH = MH - theoroticalMH
	 * 
	 * @return
	 */
	public double getDeltaMH();

	/**
	 * The experimental MZ for this peptide identification
	 * 
	 * @since 0.2.9
	 * @return
	 */
	public double getExperimentalMZ();

	/**
	 * The pi value of this peptide
	 * 
	 * @return
	 */
	public float getPI();

	/**
	 * Number of enzymatic terminals. For tryptic peptides, this value is
	 * commonly called NTT (number of tryptic terminals)
	 * 
	 * @return number of enzymatic terminals
	 */
	public short getNumberofTerm();

	/**
	 * After integration of out files, the scan number of each peptide
	 * identification will started with a tag indicating which raw file does
	 * this peptide come from. For some cases, only scan number is indicated for
	 * a peptide identification, then null will be returned. For example,
	 * "XXXXXX, scan", the base name is XXXXX
	 * 
	 * 
	 * @return The file name of the raw. (Null if can't find the scan file)
	 */
	public String getBaseName();

	/**
	 * Set the base name for the peptide, if the original scan name is a unknown
	 * scan name, nothing will be done
	 * 
	 * @since 0.3
	 * @param basename
	 * @return
	 */
	public boolean setBasename(String basename);

	/**
	 * If this peptide passes the threshold and used for protein identification
	 * 
	 * @param isused
	 * @return isused
	 */
	public boolean setUsed(boolean isused);

	/**
	 * If this peptide is a true positive peptide: from forward sequence;
	 */
	public boolean isTP();

	/**
	 * If this peptide is a true positive peptide: from forward sequence;
	 */
	public void setTP(boolean isTp);
	
	/**
	 * The number of miss cleave sites for this peptide sequence.
	 * 
	 * @return
	 */
	public short getMissCleaveNum();

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
	public void setSim(float sim);

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
	public float getSim();

	/**
	 * 
	 * The ambiguous score for phosphorylated peptide localization. This is not
	 * a necessary value.
	 * 
	 */
	public void setAscores(double[] ascores);

	/**
	 * 
	 * The ambiguous score for phosphorylated peptide localization. This is not
	 * a necessary value.
	 * 
	 */
	public double[] getAscores();

	/**
	 * Get the absolute delta mz value in ppm unit. That is the absolute value
	 * of delta mz ppm (|delta mz ppm|).
	 * 
	 * @return
	 */
	public double getAbsoluteDeltaMZppm();
	
	/**
	 * Get the delta mz value in ppm unit.
	 * 
	 * @return
	 */
	public double getDeltaMZppm();

	/**
	 * Set the scan number (<b>Charge state will not be set</b>). Use
	 * {@link #setCharge(short)} to set the charge state.
	 * <p>
	 * In some cases such as scanName in OMSSA, the charge state in scan name
	 * may not equals to the actual charge state of the identified peptides as
	 * OMSSA itself contains a charge state evaluating strategy. Therefore, This
	 * method only set the scan number even though the ScanName also contains
	 * the charge state information.
	 * 
	 * @param scannum
	 */
	public void setScanNum(IScanName scanNum);

	/**
	 * The type (search algorithm) of this peptide.
	 * 
	 * @since 0.2.7
	 * @return
	 */
	public PeptideType getPeptideType();
	
	/**
	 * Get the intensity of the peptide(intensity of precuser ion).
	 */
	public double getInten();
	
	public void setInten(double inten);
	
	/**
	 * Get the total intensity of the ms2 fragment ions, used for SIn quantitation.
	 * @return
	 */
	public double getFragInten();
	
	public void setFragInten(double fragInten);
	
	public double getHydroScore();
	
	public void setHydroScore(double hydroScore);
	
	/**
	 * The label type of the peptide.
	 * @return
	 */
	public LabelType getLabelType();
	
	public void setLabelType(LabelType type);
	
	public void formatReference(IFastaAccesser accesser);
	
	/**
	 * Get the retention time of this MS2 scan
	 * @return
	 */
	public double getRetentionTime();
	
	public void setRetentionTime(double retentionTime);
	
	/**
	 * Set the enzyme for the generation of this peptide
	 * 
	 * @param enzyme
	 */
	public void setEnzyme(Enzyme enzyme);
	
//	public void setFragIntenScore(double score);
	
//	public double getFragIntenScore();
	
	public void setMH(double mh);
	
	public void setSequence(String sequence);
	
	public void setSequence(IModifiedPeptideSequence mpepseq);
	
	/**
	 * 
	 * @return
	 */
//	public HashMap <ProteinReference, int []> getPepLocMap();
	
	/**
	 * 
	 */
	public HashMap <String, SeqLocAround> getPepLocAroundMap();
	
	/**
	 * 
	 * @param pepLocMap
	 */
//	public void setPepLocMap(HashMap <ProteinReference, int []> pepLocMap);
	
	/**
	 * 
	 */
	public void setPepLocAroundMap(HashMap<String, SeqLocAround> locAroundMap);
	
	/**
	 * 
	 * @param ref
	 * @param locs
	 */
	public void addPepLocAround(String ref, SeqLocAround sla);
	
	/**
	 * 
	 * @return
	 */
	public String toSimpleInfoFormat();

	/**
	 * @return
	 */
	public String getPepInfo();
	
	public void setQValue(float qValue);
	
	public float getQValue();

	/**
	 * Used in cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2
	 * <b><p> Now is not used.
	 * @see in cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2
	 * @param nameAccesser
	 */
//	public void formatReference(ProteinNameAccesser nameAccesser);

}
