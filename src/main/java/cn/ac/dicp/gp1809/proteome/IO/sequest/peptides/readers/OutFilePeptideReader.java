/*
 * *****************************************************************************
 * File: OutFilePeptideReader.java * * * Created on 08-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.ISequestReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.BatchOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.IBatchOutReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFileReadingException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile.Hit;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SimSequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim.SimCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * Implements ProReader for the reading of peptide information. The name of dir
 * containing all sequest out files is given as input.
 * 
 * <p>
 * Alternatively, you can input a IBatchOutReader instance and get the peptides
 * from this reader.
 * 
 * <p>
 * If an instance of ISequestReader is provided, you can calculated the Sim
 * scores for the peptide identifications if the isCalSim is selected as true.
 * The Sim score is described by Z. Q. Zhang in Amgen.
 * 
 * 
 * @author Xinning
 * @version 0.7.4, 05-02-2010, 10:25:27
 */
public class OutFilePeptideReader extends AbstractSequestPeptideReader {

	private IBatchOutReader breader;

	private ISequestReader sreader;
	private boolean isCalSim;
	private SimCalculator simCalor;
	private MwCalculator mwcalor;

	private int curtPepIdx = 1;// current peptide count in an out file, for
	// more than one peptide accepted in an out file
	private int curtPepCount;

	// Current peptide list from out file
	private SequestPeptide[] curtPeptides = new SequestPeptide[0];

	// Only used when the isCalSim is selected as true
	private SequestScanDta curtDta;
	// The current out file
	private OutFile curtOut;

	private SequestParameter parameter;
	
	private Enzyme enzyme;

	public OutFilePeptideReader(String foldername)
	        throws ImpactReaderTypeException, SequestFileIllegalException {
		this(new File(foldername));
	}

	public OutFilePeptideReader(File folder) throws ImpactReaderTypeException,
	        SequestFileIllegalException {
		this(new BatchOutReader(folder));
	}

	/**
	 * Create the reader using specific directory and the name of outfiles (not
	 * the path). The parameter should also be specified.
	 * 
	 * @param folder
	 * @param filenames
	 * @param parameter
	 * @throws ImpactReaderTypeException
	 * @throws SequestFileIllegalException
	 */
	public OutFilePeptideReader(File folder, String[] filenames, File parameter)
	        throws ImpactReaderTypeException, SequestFileIllegalException {
		this(new BatchOutReader(folder, filenames, parameter));
	}

	/**
	 * Create a ProReader from BatchOutReader
	 * 
	 * @param breader
	 * @throws ImpactReaderTypeException
	 */
	public OutFilePeptideReader(IBatchOutReader breader)
	        throws ImpactReaderTypeException {
		super(breader.getFile());

		this.breader = breader;
		this.parameter = breader.getSearchParameters();
		this.enzyme = this.parameter.getEnzyme();
		
		if (this.breader.getNumberofOutFiles() == 0)
			throw new ImpactReaderTypeException("OutReader unsuit Exception");
	}

	/**
	 * Create a ProReader from SequestReader.
	 * 
	 * @param breader
	 */
	public OutFilePeptideReader(ISequestReader breader) {
		this(breader, null);
	}

	/**
	 * Create a ProReader from SequestReader. Similarity values are calculated
	 * if the calSim is selected as true. The sim value is defined as described
	 * in paper: 1. Z. Zhang, "Prediction of low-energy collision-induced
	 * dissociation spectra of peptides". Anal. Chem. (2004), 76(14), 3908-3922.
	 * 2. Z. Zhang, "Prediction of Low-Energy Collision-Induced Dissociation
	 * Spectra of Peptides with Three or More Charges", Anal. Chem. (2005),
	 * 77(19), 6364-6373.
	 * 
	 * And the sim score is calculated by the KineticModel.dll provided by Z.Q.
	 * Zhang in Amgen
	 * 
	 * @param breader
	 * @param SimCalculator:
	 *            calculator for sim value, if not calculated set as null.
	 */
	public OutFilePeptideReader(ISequestReader breader, SimCalculator simCalor) {
		super(breader.getFile());

		if (simCalor != null) {
			this.isCalSim = true;
			this.sreader = breader;
			this.simCalor = simCalor;
			this.mwcalor = breader.getSearchParameters().getMwCalculator();

			// Set the sim formatter
			this.setPeptideFormat(SimSequestPeptideFormat.newInstance());
		}

		this.breader = breader;
		this.parameter = breader.getSearchParameters();
		this.enzyme = this.parameter.getEnzyme();
	}

	@Override
	protected SequestPeptide getPeptideImp() throws PeptideParsingException {
		if (this.curtPepIdx >= this.curtPepCount) {
			this.curtPepIdx = 0;

			try {
				this.curtOut = this.breader.getNextOut();
			} catch (OutFileReadingException e) {
				throw new PeptideParsingException(
				        "Error in reading the out file: "
				                + this.breader.getNameofCurtOutFile()
				                + " for reading peptides.", e);
			}

			if (this.curtOut == null) {// End of out files
				return null;
			}

			Hit[] hits = this.curtOut.getHits(this.getTopN());

			if (hits == null) {
				this.curtPepCount = 0;
				return this.getPeptideImp();
			}

			this.curtPepCount = hits.length;
			this.curtPeptides = this.getPeptide(hits, this.curtOut);

			if (this.isCalSim) {
				try {
					this.curtDta = this.sreader.getDtaFileForOut(this.curtOut,
					        true);
				} catch (DtaFileParsingException e) {
					throw new PeptideParsingException(
					        "Error occurs while geting dta for Sim "
					                + "score calcualtion for the out file: "
					                + this.breader.getNameofCurtOutFile()
					                + " for reading peptides.", e);
				}
			}
		}

		SequestPeptide pep = this.curtPeptides[this.curtPepIdx++];

		if (this.isCalSim) {
			pep.setSim(this.simCalor.getSim(pep.getSequence(), this.mwcalor,
			        pep.getCharge(), this.curtDta));
		}

		return pep;
	}

	/**
	 * Create a peptide instance.
	 * 
	 * @param hit
	 * @return
	 */
	private SequestPeptide[] getPeptide(Hit[] hits, OutFile curtout) {

		int siz = hits.length;
		SequestPeptide[] peps = new SequestPeptide[siz];

		for (int i = 0; i < siz; i++) {
			Hit hit = hits[i];
			
			SequestScanName scanname = curtout.getscanName();
			String sequence = this.parameter.parsePeptide(hit.getSequence());
			Set <ProteinReference> refs = hit.getRefs();
			//The deocy should be assigned
			this.parseDecoy(refs);

			SequestPeptide pep = new SequestPeptide(scanname,
			        sequence, scanname.getCharge(), hit.getRsp(), hit.getMh(),
			        curtout.getExperimentMH() - hit.getMh(), hit.getRank(), hit
			                .getDcn_from_next_xcorr(), hit.getXcorr(), hit
			                .getSp(), hit.getIons(), hit.getRefs(), this.getPeptideFormat());

			pep.setEnzyme(this.enzyme);

			peps[i] = pep;
		}
		
		return peps;
	}

	private void parseDecoy(Set<ProteinReference> refs) {
		for(ProteinReference ref : refs) {
			ref.setDecoy(this.getDecoyJudger().isDecoy(ref.getName()));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.ProReader#close()
	 */
	public void close() {
		this.breader.close();

		System.out.println("Finished reading.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getSearchParameter()
	 */
	@Override
	public SequestParameter getSearchParameter() {
		return this.parameter;
	}

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader#setDecoyJudger(cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger)
     */
    @Override
    public void setDecoyJudger(IDecoyReferenceJudger judger) {
	    super.setDecoyJudger(judger);
	    this.breader.setDecoyJudger(judger);
    }
	
	public static void main(String[] args) throws ImpactReaderTypeException,
	        SequestFileIllegalException, PeptideParsingException {

	}
}
