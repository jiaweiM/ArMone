/* 
 ******************************************************************************
 * File: PeptideInfo.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.pepinfo;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;

/**
 * The peptide info
 * 
 * @author Xinning
 * @version 0.2.1, 06-08-2010, 15:42:26
 */
public class PeptideInfo implements IPeptideInfo {
	
	private String sequence;
	private IPeptide peptide;
	private Ions ions;
	private NeutralLossInfo[] neuloss;
	private double experimental_mz;
	private short charge;

	/**
     * @param peptide
     * @param ions
     * @param neuloss
     * @param lossString
     */
    public PeptideInfo(IPeptide peptide, Ions ions, NeutralLossInfo[] neuloss) {
    	this(peptide, peptide.getSequence(), ions, neuloss);
    }
    
	/**
	 * Use the specific sequence but not the sequence in peptide
	 * 
     * @param peptide
     * @param ions
     * @param neuloss
     * @param lossString
     */
    public PeptideInfo(IPeptide peptide, String sequence, Ions ions, NeutralLossInfo[] neuloss) {
	    this.sequence = sequence;
    	this.peptide = peptide;
	    this.ions = ions;
	    this.neuloss = neuloss;
	    this.experimental_mz = peptide.getExperimentalMZ();
	    this.charge = peptide.getCharge();
    }
    
    public PeptideInfo(double experimental_mz, short charge, String sequence, Ions ions, NeutralLossInfo[] neuloss) {
	    this.sequence = sequence;
    	this.charge  = charge;
	    this.ions = ions;
	    this.neuloss = neuloss;
	    this.experimental_mz = experimental_mz;
    }
    
	/**
	 * For phosphopeptide pair (peptides from apivase)
	 * 
     * @param peptide
     * @param ions
     * @param neuloss
     * @param lossString
     */
    public PeptideInfo(IPhosPeptidePair peptide, boolean is_MS3_spectra, Ions ions, NeutralLossInfo[] neuloss) {
	    this.sequence = is_MS3_spectra ? peptide.getNeutralLossPeptideSequence().getFormattedSequence() 
	    		: peptide.getPeptideSequence().getFormattedSequence();
    	this.peptide = peptide;
	    this.ions = ions;
	    this.neuloss = neuloss;
	    this.experimental_mz = is_MS3_spectra ? peptide.getMS3MZ() : peptide.getMS2MZ();
	    this.charge = peptide.getCharge();
    }
    
	/**
     * @param peptide
     * @param ions
     * @param neuloss
     * @param lossString
     */
    public PeptideInfo(IPeptide peptide, Ions ions) {
    	this(peptide, peptide.getSequence(), ions, null);
    }

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo#getCharge()
	 */
	@Override
	public short getCharge() {
		return charge;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo#getIons()
	 */
	@Override
	public Ions getIons() {
		return this.ions;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo#getLoss()
	 */
	@Override
	public NeutralLossInfo[] getLosses() {
		return this.neuloss;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo#getMZ()
	 */
	@Override
	public double getMZ() {
		return this.experimental_mz;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo#getPeptide()
	 */
	@Override
	public IPeptide getPeptide() {
		return this.peptide;
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo#getSequence()
	 */
	@Override
    public String getSequence() {
	    return this.sequence;
    }
}
