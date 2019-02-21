/* 
 ******************************************************************************
 * File: GlycoSpectrum.java * * * Created on 2011-3-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.glyco.GlycoForm;
import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;

/**
 * @author ck
 *
 * @version 2011-3-16, 08:50:04
 */
public abstract class AbstractGlycoSpectrum {

	protected IMS2PeakList peaklist;
	protected double pepMr;
	protected PrecursePeak precurse;
	protected int preScanNum;
	protected int glycoScanNum;
	protected double mzThres;
	protected double basepeakInten;
	protected double intenThres;
	protected String matchPepSeq;
	protected double glycoMass;
	protected double calcuMz;
	
	protected HashSet <GlycoPepPeak> glycoPepSet;
	protected HashMap <Double, String> glyCoreMzMap;
	protected double matchGlycoInten;
	
	private boolean fuc;
	private GlycoForm r1Comp;
	
	protected int [] lowGlyPeaks;
	
	/**
	 * The precurse mz is used in matching 
	 */
	protected double firstIsotopeMz;
	
	protected boolean newGlycoComp = false;
	
	protected static final double dHex = Glycosyl.Fuc.getMonoMass();
	protected static final double Hex = Glycosyl.Hex.getMonoMass();
	protected static final double HexNAc = Glycosyl.HexNAc.getMonoMass();
	protected static final double NeuAc = Glycosyl.NeuAc.getMonoMass();
	
	protected static final double PROTON_W = AminoAcidProperty.PROTON_W;
	protected static final double nGlycanCore = 892.317218;
	protected static final double nGlycanCoreFuc = 1038.375127;
	
	/**
	 * nbt.1511-S1, p9
	 */
	protected static final double dm = 1.00286864;

	public AbstractGlycoSpectrum(IMS2PeakList peaklist, int preScanNum, int glycoScanNum){
		this.peaklist = peaklist;
		this.precurse = peaklist.getPrecursePeak();
		this.preScanNum = preScanNum;
		this.glycoPepSet = new HashSet <GlycoPepPeak>();
		this.glyCoreMzMap = new HashMap <Double, String>();
		this.glycoScanNum = glycoScanNum;
		this.basepeakInten = peaklist.getBasePeak().getIntensity();
	}

	public IMS2PeakList getPeakList(){
		return peaklist;
	}
	
	/**
	 * Peptide mass without glycans.
	 * @param pepMr
	 */
	public void setPepMr(double pepMr){
		this.pepMr = pepMr;
	}
	
	public double getPepMr(){
		return pepMr;
	}
	
	public PrecursePeak getPrecursePeak(){
		return precurse;
	}

	public double getPrecurseMz(){
		return precurse.getMz();
	}
	
	public double getPrecurseMH(){
		return precurse.getMH();
	}
	
	public void setPreMz(double preMz){
		this.precurse.setMz(preMz);
	}
	
	public short getPrecurseCharge(){
		return precurse.getCharge();
	}
	
	public double getPrecurseInten(){
		return precurse.getIntensity();
	}
	
	public String getMatchPepSeq(){
		return this.matchPepSeq;
	}
	
	public double getGlycoMass(){
		return this.glycoMass;
	}
	
	public void setGlycoMass(double glycoMass){
		this.glycoMass = glycoMass;
	}
	
	public double getCalcuMz(){
		return this.calcuMz;
	}
/*	
	public double getPrecurseRT(){
		return precurse.getRT();
	}
*/	
	public double getMatchIntensity(){
		return matchGlycoInten;
	}
	
	public double getMatchIntenThres(){
		return this.intenThres;
	}
	
	public void setMzThres(double mzThres){
		this.mzThres =  mzThres;
	}
	
	public int getPreScanNum(){
		return preScanNum;
	}
	
	public int getGlycoScanNum(){
		return glycoScanNum;
	}

	public void setFuc(boolean fuc){
		this.fuc = fuc;
	}
	
	public boolean getFuc(){
		return fuc;
	}
	
	public HashMap <Double, String> getGlyCoreMzMap(){
		return glyCoreMzMap;
	}
	
	public void addGlycoPepPeak(GlycoPepPeak gpp){
		this.glycoPepSet.add(gpp);
	}
	
	public HashSet <GlycoPepPeak> getGlycoPepSet(){
		return this.glycoPepSet;
	}

	public GlycoPepPeak [] getGlycoPepList(){
		GlycoPepPeak [] gpp = glycoPepSet.toArray(new GlycoPepPeak [glycoPepSet.size()]);
		Arrays.sort(gpp);
		return gpp;
	}
	
	public int getGlycoPepNum(){
		return glycoPepSet.size();
	}
	
	public void getFeature(){
		IPeak [] mzPeaks = this.peaklist.getPeakArray();
		HashMap <Double, Integer> intenIdxMap = new HashMap <Double, Integer>();
		for(int i=0;i<mzPeaks.length;i++){
			intenIdxMap.put(mzPeaks[i].getIntensity(), i);
		}
		IPeak [] intenPeaks = this.peaklist.getPeaksSortByIntensity();
		HashSet <IPeak> usedPeak = new HashSet <IPeak>();
		for(int i=0;i<intenPeaks.length;i++){
			IPeak p = intenPeaks[i];
			if(usedPeak.contains(p))
				continue;

			double pmz = p.getMz();
			double pinten = p.getIntensity();
			int idx = intenIdxMap.get(pinten);
			int c = findCharge(p, mzPeaks, idx+1, usedPeak, 6);
			if(c>0)
				System.out.println(pmz+"\t"+c);
		}

	}
	
	/**
	 * Many glyco peptide peaks don't show isotope envelopes so determine the charge of 
	 * glyco peptide is difficult.
	 * @param peak
	 * @param mzPeaks
	 * @param idx
	 * @param usedPeak
	 * @param charge
	 * @return
	 */
	private int findCharge(IPeak peak, IPeak [] mzPeaks, int idx,
			HashSet <IPeak> usedPeak, int charge){
		
		if(charge==0)
			return 0;
		
		double pmz = peak.getMz();
		for(int j=idx+1;j<mzPeaks.length;j++){
			IPeak pj = mzPeaks[j];
			if(usedPeak.contains(pj))
				continue;
			
			double pjmz = pj.getMz();
			if((pjmz-pmz)>1.1)
				break;
			
			if((pjmz-pmz)>(dm/charge + pmz*mzThres)){
				findCharge(peak, mzPeaks, idx, usedPeak, charge-1);
			}else if(Math.abs(pjmz-pmz-dm/charge) < pmz*mzThres){
				usedPeak.add(peak);
				usedPeak.add(pj);
				for(int k=idx;k<mzPeaks.length;k++){
					if(Math.abs(mzPeaks[k].getMz()-pmz-dm/charge) < pmz*mzThres){
						usedPeak.add(mzPeaks[k]);
					}
					if(Math.abs(mzPeaks[k].getMz()-pmz) > 5.0/charge){
						break;
					}
				}
				for(int k=idx;k>0;k--){
					if(Math.abs(pmz-mzPeaks[k].getMz()-dm/charge) < pmz*mzThres){
						usedPeak.add(mzPeaks[k]);
					}
					if(Math.abs(pmz-mzPeaks[k].getMz()) > 5.0/charge){
						break;
					}
				}
				
				return charge;
				
			}else{
				findCharge(peak, mzPeaks, idx+1, usedPeak, charge);
			}
		}
		
		return 0;
	}

	public GlycoForm getR1GlycoComp() {
		// TODO Auto-generated method stub
		return r1Comp;
	}
	
	public void setR1GlycoComp(GlycoForm form) {
		// TODO Auto-generated method stub
		this.r1Comp = form;
	}
	
	public void setNewGlycoComp(boolean newGlycoComp){
		this.newGlycoComp = newGlycoComp;
	}
	
	public boolean getNewGlycoComp(){
		return newGlycoComp;
	}
	
	public void setFirstIsotopeMz(double firstIsotopeMz){
		this.firstIsotopeMz = firstIsotopeMz;
	}
	
	public double getFirstIsotopeMz(){
		return this.firstIsotopeMz;
	}
	
	public void setLowGlyPeaks(int [] lowGlyPeaks){
		this.lowGlyPeaks = lowGlyPeaks;
	}
	
	/**
	 * The glyco peaks in low mz section
	 * @return
	 */
	public int [] getLowGlyPeaks(){
		return this.lowGlyPeaks;
	}
	
	public String toString(){
		return this.peaklist.toString();
	}
	
	public String toPeakOneLine(){
		return this.peaklist.toPeaksOneLine();
	}
	
	/**
	 * Get possible glycans composition.
	 * @param mzThresPPM
	 * @param mzThresAMU
	 * @return
	 */
	public abstract GlycoForm [] getPossiComposition(double mzThresPPM, double mzThresAMU);
	
	/**
	 * Get possible glycans composition.
	 * @param mzThresPPM
	 * @param mzThresAMU
	 * @return
	 */
//	public abstract GlycoForm [] getPossiComposition(double glycoMass, double mzThresPPM, double mzThresAMU);
	
	/**
	 * Matching peptide with HCD spectra, used for isotope label sample.
	 * @param pepMass
	 * @param mzThresPPM
	 * @return
	 */
	public abstract int NGlycoPepMatching(double [] pepMass, double mzThresPPM, double mzThresAMU);
	
	/**
	 * Matching peptide with HCD spectra, used for label free sample.
	 * @param pepMass
	 * @param mzThresPPM
	 * @return
	 */
	public abstract boolean NGlycoPepMatching(double pepMass, double mzThresPPM, double mzThresAMU);
	
	/**
	 * Matching peptide with HCD spectra, used for isotope label sample.
	 * @param pepMass
	 * @param mzThresPPM
	 * @return
	 */
	public abstract int OGlycoPepMatching(double [] pepMass, IGlycoPeptide pep, double mzThresPPM, double mzThresAMU);
	
	/**
	 * Matching peptide with HCD spectra, used for label free sample.
	 * @param pepMass
	 * @param mzThresPPM
	 * @return
	 */
	public abstract boolean OGlycoPepMatching(double pepMass, IGlycoPeptide pep, double mzThresPPM, double mzThresAMU);
}
