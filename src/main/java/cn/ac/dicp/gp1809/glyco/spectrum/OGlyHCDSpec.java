/* 
 ******************************************************************************
 * File: OGlyHCDSpec.java * * * Created on 2011-6-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.util.HashSet;

import cn.ac.dicp.gp1809.glyco.GlycoForm;
import cn.ac.dicp.gp1809.glyco.NGlycoCompose;
import cn.ac.dicp.gp1809.glyco.NGlycoPossiForm;
import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.OGlycoPossiForm;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;

/**
 * @author ck
 *
 * @version 2011-6-21, 10:38:55
 */
public class OGlyHCDSpec extends AbstractGlycoSpectrum {

	protected static final double HexHexNAc = Hex+HexNAc;
	
	protected static final double HexHexNAcNeuAc = Hex+HexNAc+NeuAc;

	/**
	 * @param peaklist
	 * @param preScanNum
	 * @param glycoScanNum
	 */
	public OGlyHCDSpec(IMS2PeakList peaklist, int preScanNum, int glycoScanNum) {
		super(peaklist, preScanNum, glycoScanNum);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.GlycoSpectrum#getPossiComposition()
	 */
	@Override
	public GlycoForm[] getPossiComposition(double mzThresPPM, double mzThresAMU) {
		// TODO Auto-generated method stub
		double preMr = this.getPrecurseMH() - AminoAcidProperty.PROTON_W;
		double pepMass = this.getPepMr();
		double glycoMass = preMr-pepMass;

		int [] core = new int []{0, 0, 0, 0, 0};
		
		NGlycoPossiForm [] forms = NGlycoCompose.calNoCorePPM(glycoMass);
		
		return forms;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.GlycoSpectrum#pepMatching(cn.ac.dicp.gp1809.glyco.spectrum.GlycoSpectrum, double[], double)
	 */
	@Override
	public int OGlycoPepMatching(double [] pepMass,IGlycoPeptide pep, double mzThresPPM, double mzThresAMU) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<pepMass.length;i++){
			boolean b = OGlycoPepMatching(pepMass[i], pep, mzThresPPM, mzThresAMU);
			if(b){
				return i;
			}
		}
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.GlycoSpectrum#pepMatching(double, double)
	 */
	@Override
	public boolean OGlycoPepMatching(double pepMass, IGlycoPeptide pep, double mzThresPPM, double mzThresAMU) {
		// TODO Auto-generated method stub

		double preMr = this.getPrecurseMH() - AminoAcidProperty.PROTON_W;
		
		// NeuAc is released in this experiment, if deGlycoMass = N * NeuAc, matching is successful
		double deGlycoMass = preMr-pepMass;
		int glySiteNum = pep.getGlycoSiteNum();
		int deNum = -1;
		
		double glycoMass = 0;

		if(Math.abs(deGlycoMass)<5){
			
			if(Math.abs(deGlycoMass)<=mzThresAMU){

				glycoMass = preMr - pep.getPepMrNoGlyco();
				deNum = 0;

			}else if(Math.abs(deGlycoMass-1)<=mzThresAMU){

				preMr--;
				glycoMass = preMr - pep.getPepMrNoGlyco();
				deNum = 0;
				
			}else if(Math.abs(deGlycoMass-2)<=mzThresAMU){

				preMr-=2;
				glycoMass = preMr - pep.getPepMrNoGlyco();
				deNum = 0;
				
			}else if(Math.abs(deGlycoMass-3)<=mzThresAMU){

				preMr-=3;
				glycoMass = preMr - pep.getPepMrNoGlyco();
				deNum = 0;
			}
			
		}else if(deGlycoMass>200){
			
			for(int i=1;i<=glySiteNum;i++){
				
				if(Math.abs(deGlycoMass-(double)i*NeuAc)<=mzThresAMU){

					glycoMass = preMr - pep.getPepMrNoGlyco();
					deNum = i;
					break;
					
				}else if(Math.abs(deGlycoMass-(double)i*NeuAc-1)<=mzThresAMU){

					preMr--;
					glycoMass = preMr - pep.getPepMrNoGlyco();
					deNum = i;
					break;
					
				}else if(Math.abs(deGlycoMass-(double)i*NeuAc-2)<=mzThresAMU){

					preMr-=2;
					glycoMass = preMr - pep.getPepMrNoGlyco();
					deNum = i;
					break;
					
				}else if(Math.abs(deGlycoMass-(double)i*NeuAc-3)<=mzThresAMU){

					preMr-=3;
					glycoMass = preMr - pep.getPepMrNoGlyco();
					deNum = i;
					break;
				}
			}
		}
		
		IMS2PeakList peaklist = this.getPeakList();
		
		short preCharge = peaklist.getPrecursePeak().getCharge();
		IPeak [] peaks = peaklist.getPeakArray();

		switch (deNum){
		
			case -1: {
				return false;
			}
			
			case 0: {
				
				int [] lowGlyPeaks = this.getLowGlyPeaks();
				if(lowGlyPeaks[3]==1){
//					System.out.println("spec357\t"+pep.getSequence()+"\t"+pepMass+"\t"+this.getGlycoScanNum()+"\t"+this.getPrecurseMH());
					return false;
				}

//				System.out.println("MIPA\t"+this.getGlycoScanNum()+"\t"+deGlycoMass+"\t"+pep.getSequence());
				
//				double [] glyPepMz1 = getPossGPepMass365(pep);
				double [] glyPepMz1 = getPossGPepMass365(pepMass, pep);

				HashSet <Double> usedset1 = new HashSet <Double>();

L:				for(int peakn=0;peakn<peaks.length;peakn++){

					if(usedset1.size()>=2){

						this.firstIsotopeMz = preMr;

						GlycoSite [] sites = pep.getAllGlycoSites();
						double dm = deGlycoMass/(double)sites.length;
						
						for(int i=0;i<sites.length;i++){
							
							double add = sites[i].getModMass();
							
							if((int)add==203){
								
								OGlycoPossiForm of = new OGlycoPossiForm(new int[]{0,0,1,0,0}, dm);
								GlycoForm [] forms = new GlycoForm [] {of};
//								sites[i].setGlycoStructure(glycoMass, forms);

							}else if((int)add==365){
								
								OGlycoPossiForm of = new OGlycoPossiForm(new int[]{0,1,1,0,0}, dm);
								GlycoForm [] forms = new GlycoForm [] {of};
//								sites[i].setGlycoStructure(glycoMass, forms);
							}
						}
						
//						pep.addGlySpec(this.getGlycoScanNum());
						this.setPepMr(pep.getPepMrNoGlyco());
						return true;
					}
					
					IPeak peak = peaks[peakn];
					
					for(int i=preCharge;i>0;i--){
						
						double pmz = (peak.getMz()-PROTON_W)*i;
						double delta = pmz * mzThresPPM/1000000.0;

						for(int pepn=0;pepn<glyPepMz1.length;pepn++){

							if(usedset1.contains(glyPepMz1[pepn]))
								continue;

							if((glyPepMz1[pepn]-pmz>delta)){
								continue L;
							}
							
							if(Math.abs(pmz-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}else if(Math.abs(pmz-1-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}else if(Math.abs(pmz-2-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}else if(Math.abs(pmz-3-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}
						}
					}
				}
				break;
			}
			
			default : {

				double [] glyPepMz1 = getPossGPepMass365(pepMass, pep);

				HashSet <Double> usedset1 = new HashSet <Double>();

L:				for(int peakn=0;peakn<peaks.length;peakn++){

					if(usedset1.size()>=2){

						this.firstIsotopeMz = preMr;
						
						GlycoSite [] sites = pep.getAllGlycoSites();
						double dm = (deGlycoMass-HexNAc*(double)deNum)/(double)sites.length;
						
						for(int i=0;i<sites.length;i++){
							
							double add = sites[i].getModMass();
							if((int)add==203){
								
								OGlycoPossiForm of = new OGlycoPossiForm(new int[]{0,0,1,0,0}, dm);
								GlycoForm [] forms = new GlycoForm [] {of};
//								sites[i].setGlycoStructure(HexNAc, forms);
								
							}else if((int)add==365){
								
								if(deNum==0){
									
									OGlycoPossiForm of = new OGlycoPossiForm(new int[]{0,1,1,0,0}, dm);
									GlycoForm [] forms = new GlycoForm [] {of};
//									sites[i].setGlycoStructure(HexHexNAc, forms);
									
								}else{
									
									deNum--;
									
									OGlycoPossiForm of = new OGlycoPossiForm(new int[]{0,1,1,1,0}, dm);
									GlycoForm [] forms = new GlycoForm [] {of};
//									sites[i].setGlycoStructure(HexHexNAcNeuAc, forms);
								}
							}
						}
						
						if(deNum!=0){
//							System.out.println("spec357\t"+pep.getSequence()+"\t"+pepMass+"\t"+this.getGlycoScanNum()+"\t"+this.getPrecurseMH());
							return false;
						}
						
//						pep.addGlySpec(this.getGlycoScanNum());
						this.setPepMr(pep.getPepMrNoGlyco());
						return true;
					}
					
					IPeak peak = peaks[peakn];
					
					for(int i=preCharge;i>0;i--){
						
						double pmz = (peak.getMz()-PROTON_W)*i;
						double delta = pmz * mzThresPPM/1000000.0;

						for(int pepn=0;pepn<glyPepMz1.length;pepn++){

							if(usedset1.contains(glyPepMz1[pepn]))
								continue;

							if((glyPepMz1[pepn]-pmz>delta)){
								continue L;
							}
							
							if(Math.abs(pmz-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}else if(Math.abs(pmz-1-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}else if(Math.abs(pmz-2-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}else if(Math.abs(pmz-3-glyPepMz1[pepn])<=delta){

								usedset1.add(glyPepMz1[pepn]);
								continue L;

							}
						}
					}
				}
				break;
			}
		}

		return false;
	}
	
	private double [] getPossGPepMass203(IGlycoPeptide pep){
		
		double pepNoGly = pep.getPepMrNoGlyco();
		double pepNexAc = pepNoGly + HexNAc;
		
		double [] masses = new double [2];
		masses [0] = pepNoGly;
		masses [1] = pepNexAc;
		
		return masses;
	}
	
	private double [] getPossGPepMass365(double pepMass, IGlycoPeptide pep){
		
		double pepNoGly = pep.getPepMrNoGlyco();
		double pepNexAc = pepNoGly + HexNAc;
		
		double [] masses = new double [3];
		masses [0] = pepNoGly;
		masses [1] = pepNexAc;
		masses [2] = pepMass;
		
		return masses;
	}

	public static void main(String [] args){
		System.out.println(OGlyHCDSpec.HexHexNAc);
		System.out.println(OGlyHCDSpec.HexHexNAcNeuAc);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.AbstractGlycoSpectrum#pepMatching(double[], double, double)
	 */
	@Override
	public int NGlycoPepMatching(double[] pepMass, double mzThresPPM,
			double mzThresAMU) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.AbstractGlycoSpectrum#pepMatching(double, double, double)
	 */
	@Override
	public boolean NGlycoPepMatching(double pepMass, double mzThresPPM,
			double mzThresAMU) {
		// TODO Auto-generated method stub
		return false;
	}
}
