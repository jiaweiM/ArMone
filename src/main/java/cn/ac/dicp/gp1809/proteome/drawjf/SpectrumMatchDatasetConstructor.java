/* 
 ******************************************************************************
 * File: SpectrumMatchDatasetConstructor.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.IPhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.PeptideInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * Construct the spectrum match data set
 * 
 * <p>
 * Changes:
 * <li>0.1.1, 05-30-2009: the match type and the mass type can be specified.
 * 
 * @author Xinning
 * @version 0.1.2, 06-08-2010, 15:57:58
 */
public class SpectrumMatchDatasetConstructor {

	private AminoacidFragment aaf;

	public SpectrumMatchDatasetConstructor(AminoacidFragment aaf) {
		this.aaf = aaf;
	}

	public SpectrumMatchDatasetConstructor(ISearchParameter parameter) {
/*		
		System.out.println("change in SpectrumMatchDatasetConstructor 46");
		
		AminoacidModification aam = parameter.getVariableInfo();
		if(aam.getModifSites('#')==null){
			
		}else{
			aam.changeModifSymbol('#', '*');
		}
		this.aaf = new AminoacidFragment(parameter.getStaticInfo(), aam);
*/		
		this.aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
	}

	/**
	 * Construct the match spectrum data set. Only test the b & y type ions
	 * matches using mono isotope mass.
	 * 
	 * @param peaklist
	 * @param peptide
	 * @return
	 */
	public ISpectrumDataset construct(IMS2PeakList peaklist, IPeptide peptide) {
		return this.construct(peaklist, peptide, null, new int[] { Ion.TYPE_B,
		        Ion.TYPE_Y }, true, null);
	}

	/**
	 * Construct the match spectrum data set. Only test the b & y type ions
	 * matches using mono isotope mass.
	 * 
	 * @param peaklist
	 * @param peptide
	 * @param neuloss
	 * @param lossString
	 * @return
	 */
	public ISpectrumDataset construct(IMS2PeakList peaklist, IPeptide peptide,
	        NeutralLossInfo[] losses) {
		return this.construct(peaklist, peptide, losses, new int[] {
		        Ion.TYPE_B, Ion.TYPE_Y }, true, null);
	}

	/**
	 * Construct the match spectrum data set
	 * 
	 * @param peaklist
	 * @param peptide
	 * @param neuloss
	 * @param lossString
	 * @return
	 */
	public ISpectrumDataset construct(IMS2PeakList peaklist, IPeptide peptide,
	        NeutralLossInfo[] losses, int[] types, boolean isMono,
	        ISpectrumThreshold threshold) {
		Ions ions = this.aaf.fragment(peptide.getPeptideSequence(), types,
		        isMono);
		PeptideInfo info = new PeptideInfo(peptide, ions, losses);
		return new SpectrumMatchDataset(peaklist, info, threshold);
	}

	/**
	 * Construct the match spectrum data set. The sequence for fragment is the
	 * sequences specified and using other informations for the peptide.
	 * 
	 * @param peaklist
	 * @param peptide
	 * @param neuloss
	 * @param lossString
	 * @return
	 */
	public ISpectrumDataset construct(IMS2PeakList peaklist, IPeptide peptide,
	        String sequence, NeutralLossInfo[] losses, int[] types,
	        boolean isMono, ISpectrumThreshold threshold) {
		Ions ions = this.aaf.fragment(sequence, types, isMono);
		PeptideInfo info = new PeptideInfo(peptide, sequence, ions, losses);
		return new SpectrumMatchDataset(peaklist, info, threshold);
	}
	
	public ISpectrumDataset construct(IMS2PeakList peaklist, double mz, short charge, 
	        String sequence, String titleseq,  NeutralLossInfo[] losses, int[] types,
	        boolean isMono) {
		
		Ions ions = this.aaf.fragment(sequence, types, isMono);
		PeptideInfo info = new PeptideInfo(mz, charge, titleseq, ions, losses);
		return new SpectrumMatchDataset(peaklist, info, null);
	}
	
	public ISpectrumDataset construct(IMS2PeakList peaklist, double mz, short charge, 
	        String sequence, String titleseq, int[] types,
	        boolean isMono) {
		
		Ions ions = this.aaf.fragment(sequence, types, isMono);
		PeptideInfo info = new PeptideInfo(mz, charge, titleseq, ions, null);
		return new SpectrumMatchDataset(peaklist, info, null);
	}

	/**
	 * Construct the match spectrum data set. The sequence for fragment is the
	 * sequences specified and using other informations for the peptide.
	 * 
	 * @param peaklist
	 * @param peptide
	 * @param neuloss
	 * @param lossString
	 * @return
	 */
	public ISpectrumDataset construct(IMS2PeakList peaklist, IPeptide peptide,
	        IModifiedPeptideSequence msequence, NeutralLossInfo[] losses,
	        int[] types, boolean isMono, ISpectrumThreshold threshold) {
		Ions ions = this.aaf.fragment(msequence, types, isMono);
		PeptideInfo info = new PeptideInfo(peptide, msequence
		        .getFormattedSequence(), ions, losses);
		return new SpectrumMatchDataset(peaklist, info, threshold);
	}

	/**
	 * specifically for phosphopeptide pairs generated by apivase
	 * 
	 * @param peaklist
	 * @param peptide
	 * @param neuloss
	 * @param lossString
	 * @return
	 */
	public ISpectrumDataset construct(IPeakList peaklist,
	        IPhosPeptidePair peptide, boolean is_MS3, NeutralLossInfo[] losses,
	        int[] types, boolean isMono, ISpectrumThreshold threshold) {
		IPhosphoPeptideSequence phosseq = is_MS3 ? peptide
		        .getNeutralLossPeptideSequence() : peptide.getPeptideSequence();
		Ions ions = this.aaf.fragment(phosseq, types, isMono);
		PeptideInfo info = new PeptideInfo(peptide, is_MS3, ions, losses);
		return new SpectrumMatchDataset((IMS2PeakList) peaklist, info, threshold);
	}
}
