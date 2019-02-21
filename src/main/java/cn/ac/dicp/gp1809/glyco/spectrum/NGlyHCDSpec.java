/* 
 ******************************************************************************
 * File: NGlyHCDSpec.java * * * Created on 2011-6-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.NGlycoCompose;
import cn.ac.dicp.gp1809.glyco.NGlycoPossiForm;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;

/**
 * @author ck
 *
 * @version 2011-6-21, 10:37:01
 */
public class NGlyHCDSpec extends AbstractGlycoSpectrum {

	private NGlycoPossiForm [] forms;
	
	private static String [] charges = new String [] {"", "+", "++", "+++", "++++", 
		"+++++", "++++++", "+++++++", "++++++++", "+++++++++"};
	
	private static String [] fucCoreInfo = new String [] {"pep", "(pep+HexNAc+Fuc)", "(pep+2HexNAc+Fuc)",
		"(pep+2HexNAc+Fuc+Hex)", "(pep+2HexNAc+Fuc+2Hex)", "(pep+2HexNAc+Fuc+3Hex)"};
	
	private static String [] noFucCoreInfo = new String [] {"pep", "(pep+HexNAc)", "(pep+2HexNAc)",
		"(pep+2HexNAc+Hex)", "(pep+2HexNAc+2Hex)", "(pep+2HexNAc+3Hex)"};
	
	/**
	 * @param peaklist
	 * @param preScanNum
	 * @param glycoScanNum
	 */
	public NGlyHCDSpec(IMS2PeakList peaklist, int preScanNum, int glycoScanNum) {
		super(peaklist, preScanNum, glycoScanNum);
	}
	
	/**
	 * 
	 * @param gsp
	 * @param pepMass
	 * @param mzThresPPM
	 * @return
	 */
	public int NGlycoPepMatching(double [] pepMass, double mzThresPPM, double mzThresAMU){

		int labelNum = pepMass.length;
		IMS2PeakList peaklist = this.getPeakList();
		
		short preCharge = peaklist.getPrecursePeak().getCharge();
		IPeak [] peaks = peaklist.getPeakArray();

		boolean [] match = new boolean [labelNum];
		Arrays.fill(match, false);
		
		boolean [] fuc = new boolean [labelNum];
		Arrays.fill(fuc, false);
		
		HashSet <Double> [] glycoPeakSet = new HashSet [labelNum];
		HashMap <Double, String> [] glycoPeakMap = new HashMap [labelNum];
		for(int i=0;i<glycoPeakMap.length;i++){
			glycoPeakMap[i] = new HashMap <Double, String>();
		}
		
		double [] matchGlycoIntens = new double [labelNum];
		
LK:		for(int k=0;k<labelNum;k++){

			double [] glyPepMz1 = getPossGPepMass(pepMass[k]);
			HashSet <Double> usedset1 = new HashSet <Double>();

L:			for(int peakn=0;peakn<peaks.length;peakn++){

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
							glycoPeakMap[k].put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
							matchGlycoIntens[k] += peak.getIntensity();
							continue L;

						}else if(Math.abs(pmz-dm-glyPepMz1[pepn])<=delta){

							usedset1.add(glyPepMz1[pepn]);
							glycoPeakMap[k].put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
							matchGlycoIntens[k] += peak.getIntensity();
							continue L;

						}else if(Math.abs(pmz-2.0*dm-glyPepMz1[pepn])<=delta){

							usedset1.add(glyPepMz1[pepn]);
							glycoPeakMap[k].put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
							matchGlycoIntens[k] += peak.getIntensity();
							continue L;

						}else if(Math.abs(pmz-3.0*dm-glyPepMz1[pepn])<=delta){

							usedset1.add(glyPepMz1[pepn]);
							glycoPeakMap[k].put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
							matchGlycoIntens[k] += peak.getIntensity();
							continue L;

						}
					}
				}
			}

			if(usedset1.size()>=3){
				
				fuc[k] = false;
				match [k] = true;
				glycoPeakSet [k] = usedset1;
				
				continue LK;
			}
			
			if(!match [k]){

				glycoPeakMap[k] = new HashMap <Double, String>();
				matchGlycoIntens[k] = 0;

				double [] glyPepMz2 = getPossGPMassFuc(pepMass[k]);
				HashSet <Double> usedset2 = new HashSet <Double>();

L1:				for(int peakn=0;peakn<peaks.length;peakn++){
					
					IPeak peak = peaks[peakn];
					
					for(int i=preCharge;i>0;i--){
						
						double pmz = (peak.getMz()-PROTON_W)*i;
						double delta = pmz * mzThresPPM/1000000.0;
						
						for(int pepn=0;pepn<glyPepMz2.length;pepn++){
							
							if(usedset2.contains(glyPepMz2[pepn]))
								continue;
							
							if((glyPepMz2[pepn]-pmz>delta)){
								continue L1;
							}
							
							if(Math.abs(pmz-glyPepMz2[pepn])<=delta){

								usedset2.add(glyPepMz2[pepn]);
								glycoPeakMap[k].put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
								matchGlycoIntens[k] += peak.getIntensity();
								continue L1;

							}else if(Math.abs(pmz-dm-glyPepMz2[pepn])<=delta){

								usedset2.add(glyPepMz2[pepn]);
								glycoPeakMap[k].put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
								matchGlycoIntens[k] += peak.getIntensity();
								continue L1;

							}else if(Math.abs(pmz-2.0*dm-glyPepMz2[pepn])<=delta){

								usedset2.add(glyPepMz2[pepn]);
								glycoPeakMap[k].put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
								matchGlycoIntens[k] += peak.getIntensity();
								continue L1;
								
							}else if(Math.abs(pmz-3.0*dm-glyPepMz2[pepn])<=delta){

								usedset2.add(glyPepMz2[pepn]);
								glycoPeakMap[k].put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
								matchGlycoIntens[k] += peak.getIntensity();
								continue L1;

							}
						}
					}	
				}
				
				if(usedset2.size()>=3){
					
					fuc[k] = true;
					match[k] = true;
					glycoPeakSet [k] = usedset2;
				}
			}
		}
		
		int count = 0;
		int type = 0;
		for(int i=0;i<match.length;i++){
			if(match[i]){
				type = i;
				count++;
			}
		}

		if(count==0){
			
			return -1;
			
		}else if(count==1){

			this.glyCoreMzMap = glycoPeakMap [type];
			this.matchGlycoInten = matchGlycoIntens [type];
			this.setFuc(fuc[type]);
			return type;
			
		}else{

			return -2;
		}
	}

	/**
	 * This method is used for label free quantitation.
	 * @param gsp
	 * @param pepMass
	 * @return
	 */
	public boolean NGlycoPepMatching(double pepMass, double mzThresPPM, double mzThresAMU){

		IMS2PeakList peaklist = this.getPeakList();
		
		short preCharge = peaklist.getPrecursePeak().getCharge();
		IPeak [] peaks = peaklist.getPeakArray();
		
		double [] glyPepMz1 = getPossGPepMass(pepMass);
		
		HashSet <Double> usedset1 = new HashSet <Double>();
		HashMap <Double, String> glycoPeakMap1 = new HashMap <Double, String>();
//System.out.println(peaklist);		
		double matchGlycoInten1 = 0;

L:		for(int peakn=0;peakn<peaks.length;peakn++){

			IPeak peak = peaks[peakn];

			for(int i=preCharge;i>0;i--){

				double pmz = (peak.getMz()-PROTON_W)*i;
				double delta = pmz * mzThresPPM/1000000.0;
				
				if((glyPepMz1[0]-pmz>delta)){
					continue L;
				}
				
//System.out.println(pmz+"\t"+delta+"\t"+i);
				for(int pepn=0;pepn<glyPepMz1.length;pepn++){

					if(usedset1.contains(glyPepMz1[pepn]))
						continue;

					if(Math.abs(pmz-glyPepMz1[pepn])<=delta){
//System.out.println("match\t"+pmz+"\t"+glyPepMz1);
						usedset1.add(glyPepMz1[pepn]);
						glycoPeakMap1.put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
						matchGlycoInten1 += peak.getIntensity();
						continue L;

					}else if(Math.abs(pmz-dm-glyPepMz1[pepn])<=delta){
//						System.out.println("match\t"+pmz+"\t"+glyPepMz1);

						usedset1.add(glyPepMz1[pepn]);
						glycoPeakMap1.put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
						matchGlycoInten1 += peak.getIntensity();
						continue L;

					}else if(Math.abs(pmz-2.0*dm-glyPepMz1[pepn])<=delta){
//						System.out.println("match\t"+pmz+"\t"+glyPepMz1);

						usedset1.add(glyPepMz1[pepn]);
						glycoPeakMap1.put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
						matchGlycoInten1 += peak.getIntensity();
						continue L;

					}else if(Math.abs(pmz-3.0*dm-glyPepMz1[pepn])<=delta){
//						System.out.println("match\t"+pmz+"\t"+glyPepMz1);

						usedset1.add(glyPepMz1[pepn]);
						glycoPeakMap1.put(peak.getMz(), noFucCoreInfo[pepn]+charges[i]);
						matchGlycoInten1 += peak.getIntensity();
						continue L;

					}
				}
			}
		}
	
//		System.out.println(glycoPeakMap1);
		
		if(usedset1.size()>=5){
			this.glyCoreMzMap = glycoPeakMap1;
			this.matchGlycoInten = matchGlycoInten1;
			this.setPepMr(pepMass);
			this.setFuc(false);
			
			return true;
		}
		
		double matchGlycoInten2 = 0;
		
		double [] glyPepMz2 = getPossGPMassFuc(pepMass);
		HashSet <Double> usedset2 = new HashSet <Double>();
		HashMap <Double, String> glycoPeakMap2 = new HashMap <Double, String>();

L1:			for(int peakn=0;peakn<peaks.length;peakn++){
	
			IPeak peak = peaks[peakn];
			
			for(int i=preCharge;i>0;i--){
				
				double pmz = (peak.getMz()-PROTON_W)*i;
				double delta = pmz * mzThresPPM/1000000.0;
				
				if((glyPepMz1[0]-pmz>delta)){
					continue L1;
				}

				for(int pepn=0;pepn<glyPepMz1.length;pepn++){
					
					if(usedset2.contains(glyPepMz1[pepn]))
						continue;

					if(Math.abs(pmz-glyPepMz1[pepn])<=delta){

						usedset2.add(glyPepMz1[pepn]);
						glycoPeakMap2.put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
						matchGlycoInten2 += peak.getIntensity();
						continue L1;

					}else if(Math.abs(pmz-dm-glyPepMz1[pepn])<=delta){

						usedset2.add(glyPepMz1[pepn]);
						glycoPeakMap2.put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
						matchGlycoInten2 += peak.getIntensity();
						continue L1;

					}else if(Math.abs(pmz-2.0*dm-glyPepMz1[pepn])<=delta){

						usedset2.add(glyPepMz1[pepn]);
						glycoPeakMap2.put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
						matchGlycoInten2 += peak.getIntensity();
						continue L1;
						
					}else if(Math.abs(pmz-3.0*dm-glyPepMz2[pepn])<=delta){

						usedset2.add(glyPepMz2[pepn]);
						glycoPeakMap2.put(peak.getMz(), fucCoreInfo[pepn]+charges[i]);
						matchGlycoInten2 += peak.getIntensity();
						continue L1;

					}
				}
			}
		}
		
//		System.out.println(glycoPeakMap2);
		
		if(usedset1.size()>=3){
			
			if(usedset2.size()>=3){
				
				if(matchGlycoInten1>matchGlycoInten2*2){
					
					this.glyCoreMzMap = glycoPeakMap1;
					this.matchGlycoInten = matchGlycoInten1;
					this.setPepMr(pepMass);
					this.setFuc(false);
					return true;
				}
				
				if(matchGlycoInten2>matchGlycoInten1*2){
					
					this.glyCoreMzMap = glycoPeakMap2;
					this.matchGlycoInten = matchGlycoInten2;
					this.setPepMr(pepMass);
					this.setFuc(true);
					return true;
				}
				
				return false;
				
			}else{
				
				this.glyCoreMzMap = glycoPeakMap1;
				this.matchGlycoInten = matchGlycoInten1;
				this.setPepMr(pepMass);
				this.setFuc(false);
				return true;
			}
			
		}else{
			
			if(usedset2.size()>=3){
				
				this.glyCoreMzMap = glycoPeakMap2;
				this.matchGlycoInten = matchGlycoInten2;
				this.setPepMr(pepMass);
				this.setFuc(true);
				return true;
				
			}else{
				return false;
			}
		}
	}

	public NGlycoPossiForm [] getPossiComposition(double mzThresPPM, double mzThresAMU){
		
		if(this.forms != null)
			return forms;
		
		double preMr = this.getPrecurseMH() - AminoAcidProperty.PROTON_W;
		double pepMass = this.getPepMr();
		double glycoMass = preMr-pepMass;

		boolean fuc = this.getFuc();

		NGlycoPossiForm [] forms = NGlycoCompose.calWithCorePPM(glycoMass, fuc);
		
		return forms;
	}

	public double [] getPossGPepMz(double pepMass, short icharge){
		
		double [] glyPepMz = new double [6];
		
		/* pep+0; pep+203.079373*(1~2);
		 * pep+203.079373*2+162.052832*(1~3);
		 */
		glyPepMz[0] = (pepMass+icharge*AminoAcidProperty.PROTON_W)/icharge;
		glyPepMz[1] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ HexNAc)/icharge;
		glyPepMz[2] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ 2.0*HexNAc)/icharge;
		glyPepMz[3] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ 2.0*HexNAc + Hex)/icharge;
		glyPepMz[4] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ 2.0*HexNAc + 2.0*Hex)/icharge;
		glyPepMz[5] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ 2.0*HexNAc + 3.0*Hex)/icharge;

		return glyPepMz;
	}
	
	/**
	 * An fucose in the first HexNAc
	 * @param pepMass
	 * @param icharge
	 * @return
	 */
	public double [] getPossGPMzFuc(double pepMass, short icharge){
		
		double [] glyPepMz = new double [6];
		
		/* pep+0; pep+146.057909+203.079373*(1~2);
		 * pep+146.057909+203.079373*2+162.052832*(1~3);
		 */
		glyPepMz[0] = (pepMass+icharge*AminoAcidProperty.PROTON_W)/icharge;
		
		glyPepMz[1] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ dHex+ HexNAc)/icharge;
		glyPepMz[2] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ dHex + HexNAc*2)/icharge;
		glyPepMz[3] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ dHex + 2.0*HexNAc + Hex)/icharge;
		glyPepMz[4] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ dHex + 2.0*HexNAc + 2.0*Hex)/icharge;
		glyPepMz[5] = (pepMass+icharge*AminoAcidProperty.PROTON_W
				+ dHex + 2.0*HexNAc + 3.0*Hex)/icharge;
		
		return glyPepMz;
	}
	
	public static double [] getPossGPepMass(double pepMass){
		
		double [] glyPepMz = new double [6];
		
		/* pep+0; pep+203.079373*(1~2);
		 * pep+203.079373*2+162.052832*(1~3);
		 */
		glyPepMz[0] = pepMass;
		
		glyPepMz[1] = pepMass+ HexNAc;
		
		glyPepMz[2] = pepMass+ 2.0*HexNAc;
		
		glyPepMz[3] = pepMass+ 2.0*HexNAc + Hex;
		
		glyPepMz[4] = pepMass+ 2.0*HexNAc + 2.0*Hex;
		
		glyPepMz[5] = pepMass+ 2.0*HexNAc + 3.0*Hex;

		return glyPepMz;
	}
	
	/**
	 * An fucose in the first HexNAc
	 * @param pepMass
	 * @param icharge
	 * @return
	 */
	public static double [] getPossGPMassFuc(double pepMass){
		
		double [] glyPepMz = new double [6];
		
		/* pep+0; pep+146.057909+203.079373*(1~2);
		 * pep+146.057909+203.079373*2+162.052832*(1~3);
		 */
		glyPepMz[0] = pepMass;
		
		glyPepMz[1] = pepMass+ dHex+ HexNAc;

		glyPepMz[2] = pepMass+ dHex + HexNAc*2;
				
		glyPepMz[3] = pepMass+ dHex + 2.0*HexNAc + Hex;
		
		glyPepMz[4] = pepMass+ dHex + 2.0*HexNAc + 2.0*Hex;
		
		glyPepMz[5] = pepMass+ dHex + 2.0*HexNAc + 3.0*Hex;
		
		return glyPepMz;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.AbstractGlycoSpectrum#OGlycoPepMatching(double[], cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide, double, double)
	 */
	@Override
	public int OGlycoPepMatching(double[] pepMass, IGlycoPeptide pep,
			double mzThresPPM, double mzThresAMU) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.spectrum.AbstractGlycoSpectrum#OGlycoPepMatching(double, cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide, double, double)
	 */
	@Override
	public boolean OGlycoPepMatching(double pepMass, IGlycoPeptide pep,
			double mzThresPPM, double mzThresAMU) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) throws IOException, XMLStreamException {
		// TODO Auto-generated method stub
	
		double [] d1 = NGlyHCDSpec.getPossGPepMass(946.4722);
		for(int i=0;i<d1.length;i++){
			System.out.print(d1[i]+"\t");
		}
		System.out.print("\n");
/*		
		double [] d2 = NGlyHCDSpec.getPossGPMassFuc(1116.592);
		for(int i=0;i<d2.length;i++){
			System.out.print(d2[i]+"\t");
		}
		System.out.print("\n");
*/
		
		String peakfile = "I:\\glyco\\label-free\\20111123_HILIC_1105_HCD_111124034738.mzXML";
		MzXMLReader getter = new MzXMLReader(peakfile);
		getter.rapMS2ScanList();
		IMS2PeakList peaklist = getter.getMS2PeakList(2138);
		System.out.println(peaklist.getPrecursePeak());
		
//		NGlyHCDSpec spec = new NGlyHCDSpec(peaklist, 778, 779);
//		boolean boo = spec.NGlycoPepMatching(946.4722, 20, 0.1);
//		System.out.println(boo);
		
	}
	
}
