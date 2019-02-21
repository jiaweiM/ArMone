/* 
 ******************************************************************************
 * File:PSMInstanceCreator.java * * * Created on 2012-7-26
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.penn.probability.wekakd.MyInstance;
import weka.core.Attribute;
import weka.core.Instances;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim.SimCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.SpectrumMatcher;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * @author ck
 *
 * @version 2012-7-26, 10:43:42
 */
public class PSMInstanceCreator {
	
	private AminoacidFragment aaf;
	private double tolerance = 0.8;
	private int [] ionTypes = new int []{Ion.TYPE_B, Ion.TYPE_Y};
	private ISpectrumThreshold spThres = SpectrumMatcher.DEFAULT_THRESHOLD;
	private int capsity = 1000;
	
	private MwCalculator mwcalor;
	private SimCalculator simCalor;
	
	public PSMInstanceCreator(AminoacidFragment aaf){
		this.aaf = aaf;
		this.mwcalor = aaf.getMwCalculator();
		this.simCalor = new SimCalculator(SimCalculator.INST_ORBITRAP);
	}

	public PSMInstanceCreator(AminoacidFragment aaf, int [] ionTypes){
		this.aaf = aaf;
		this.mwcalor = aaf.getMwCalculator();
		this.simCalor = new SimCalculator(SimCalculator.INST_ORBITRAP);
		this.ionTypes = ionTypes;
	}
	
	public PSMInstanceCreator(AminoacidFragment aaf, ISpectrumThreshold spThres){
		this.aaf = aaf;
		this.mwcalor = aaf.getMwCalculator();
		this.simCalor = new SimCalculator(SimCalculator.INST_ORBITRAP);
		this.spThres = spThres;
	}
	
	public PSMInstanceCreator(AminoacidFragment aaf, int [] ionTypes, ISpectrumThreshold spThres){
		this.aaf = aaf;
		this.mwcalor = aaf.getMwCalculator();
		this.simCalor = new SimCalculator(SimCalculator.INST_ORBITRAP);
		this.ionTypes = ionTypes;
		this.spThres = spThres;
	}

	public MyInstance createInstance(IPeptide pep){
		
		int scannum = pep.getScanNumBeg();
		String seq = pep.getSequence();
		String uniqueSeq = PeptideUtil.getUniqueSequence(seq);
		int length = uniqueSeq.length();
		
		int modcount = seq.length()-length-4;
		
		float mpf = 0f;
		for (int i = 0;i<uniqueSeq.length();i++) {
			char c = uniqueSeq.charAt(i);
			if (c == 'R')
				mpf += 1f;
			else if (c == 'K')
				mpf += 0.8f;
			else if (c == 'H')
				mpf += 0.5f;
		}

//		float sim = this.simCalor.getSim(seq, mwcalor, pep.getCharge(), peaklist);
		
		ArrayList <Double> attrList = new ArrayList <Double>();

//		attrList.add((double)pep.getRank());
		attrList.add((double)length);
		attrList.add((double)modcount);
		attrList.add((double)pep.getCharge());
		attrList.add((double)pep.getMissCleaveNum());
		attrList.add((double)pep.getNumberofTerm());
		
		attrList.add(pep.getDeltaMH());
		attrList.add(pep.getDeltaMZppm());
		attrList.add((double)mpf);
//		attrList.add((double)sim);
//		attrList.add((double)pep.getInten());
//		attrList.add((double)pep.getFragInten()/(double)length/(double)pep.getCharge());
		
		MascotPeptide mp = (MascotPeptide)pep;
//		attrList.add((double)mp.getIonscore());
		attrList.add((double)mp.getIdenThres());
		attrList.add((double)mp.getHydroScore());
		
		double [] attribute = new double[attrList.size()];
		for(int i=0;i<attribute.length;i++){
			attribute[i] = attrList.get(i);
		}

		MyInstance mi = new MyInstance(attribute, scannum, !pep.isTP(), pep.getPrimaryScore(), pep.getRank());
		
		return mi;
	}
	
	public MyInstance createInstance(IPeptide pep, int spcount, int pepcount){
		
		int scannum = pep.getScanNumBeg();
		String seq = pep.getSequence();
		String uniqueSeq = PeptideUtil.getUniqueSequence(seq);
		int length = uniqueSeq.length();
		
		int modcount = seq.length()-length-4;
		
/*		float mpf = 0f;
		for (int i = 0;i<uniqueSeq.length();i++) {
			char c = uniqueSeq.charAt(i);
			if (c == 'R')
				mpf += 1f;
			else if (c == 'K')
				mpf += 0.8f;
			else if (c == 'H')
				mpf += 0.5f;
		}
*/
//		float sim = this.simCalor.getSim(seq, mwcalor, pep.getCharge(), peaklist);
		
		ArrayList <Double> attrList = new ArrayList <Double>();

		attrList.add((double)spcount);
		attrList.add((double)pepcount);
//		attrList.add((double)pep.getRank());
		attrList.add((double)length);
		attrList.add((double)modcount);
		attrList.add((double)pep.getCharge());
		attrList.add((double)pep.getExperimentalMZ());
		attrList.add((double)pep.getMH());
		attrList.add((double)pep.getMissCleaveNum());
		attrList.add((double)pep.getNumberofTerm());
		
		attrList.add(pep.getDeltaMH());
		attrList.add(pep.getDeltaMZppm());
//		attrList.add((double)pep.getInten());
//		attrList.add((double)pep.getFragInten());
//		attrList.add((double)pep.getFragInten()/(double)length);
		
		MascotPeptide mp = (MascotPeptide)pep;
		attrList.add((double)mp.getHydroScore());
		attrList.add((double)mp.getNumOfMatchedIons());

		double [] attribute = new double[attrList.size()];
		for(int i=0;i<attribute.length;i++){
			attribute[i] = attrList.get(i);
		}

		MyInstance mi = new MyInstance(attribute, scannum, !pep.isTP(), pep.getPrimaryScore(), pep.getRank());
		
		return mi;
	}
	
	public MyInstance createInstanceForLTQ(IPeptide pep, int spcount, int pepcount){
		
		int scannum = pep.getScanNumBeg();
		String seq = pep.getSequence();
		String uniqueSeq = PeptideUtil.getUniqueSequence(seq);
		int length = uniqueSeq.length();
		
		int modcount = seq.length()-length-4;
		
		float mpf = 0f;
		for (int i = 0;i<uniqueSeq.length();i++) {
			char c = uniqueSeq.charAt(i);
			if (c == 'R')
				mpf += 1f;
			else if (c == 'K')
				mpf += 0.8f;
			else if (c == 'H')
				mpf += 0.5f;
		}

//		float sim = this.simCalor.getSim(seq, mwcalor, pep.getCharge(), peaklist);
		
		ArrayList <Double> attrList = new ArrayList <Double>();

		attrList.add((double)spcount);
		attrList.add((double)pepcount);
//		attrList.add((double)pep.getRank());
		attrList.add((double)length);
		attrList.add((double)modcount);
		attrList.add((double)pep.getCharge());
		attrList.add((double)pep.getMissCleaveNum());
		attrList.add((double)pep.getNumberofTerm());
		
//		attrList.add(pep.getDeltaMH());
//		attrList.add(pep.getDeltaMZppm());
		attrList.add((double)mpf);
//		attrList.add((double)sim);
		attrList.add((double)pep.getInten());
		attrList.add((double)pep.getFragInten());
//		attrList.add((double)pep.getFragInten()/(double)length/(double)pep.getCharge());
		
		MascotPeptide mp = (MascotPeptide)pep;
//		attrList.add((double)mp.getIonscore());
		attrList.add((double)mp.getIdenThres());
		attrList.add((double)mp.getHydroScore());
		
		double [] attribute = new double[attrList.size()];
		for(int i=0;i<attribute.length;i++){
			attribute[i] = attrList.get(i);
		}

		MyInstance mi = new MyInstance(attribute, scannum, !pep.isTP(), pep.getPrimaryScore(), pep.getRank());
		
		return mi;
	}

	public MyInstance createInstanceForOrbi(IPeptide pep, int spcount, int pepcount){
		
		int scannum = pep.getScanNumBeg();
		String seq = pep.getSequence();
		String uniqueSeq = PeptideUtil.getUniqueSequence(seq);
		int length = uniqueSeq.length();
		
		int modcount = seq.length()-length-4;
		
		float mpf = 0f;
		for (int i = 0;i<uniqueSeq.length();i++) {
			char c = uniqueSeq.charAt(i);
			if (c == 'R')
				mpf += 1f;
			else if (c == 'K')
				mpf += 0.8f;
			else if (c == 'H')
				mpf += 0.5f;
		}

//		float sim = this.simCalor.getSim(seq, mwcalor, pep.getCharge(), peaklist);
		
		ArrayList <Double> attrList = new ArrayList <Double>();

		attrList.add((double)spcount);
		attrList.add((double)pepcount);
//		attrList.add((double)pep.getRank());
		attrList.add((double)length);
		attrList.add((double)modcount);
		attrList.add((double)pep.getCharge());
		attrList.add((double)pep.getMissCleaveNum());
		attrList.add((double)pep.getNumberofTerm());
		
		attrList.add(pep.getDeltaMH());
		attrList.add(pep.getDeltaMZppm());
		attrList.add((double)mpf);
//		attrList.add((double)sim);
		attrList.add((double)pep.getInten());
		attrList.add((double)pep.getFragInten());
//		attrList.add((double)pep.getFragInten()/(double)length/(double)pep.getCharge());
		
		MascotPeptide mp = (MascotPeptide)pep;
//		attrList.add((double)mp.getIonscore());
		attrList.add((double)mp.getIdenThres());
		attrList.add((double)mp.getHydroScore());
		
		double [] attribute = new double[attrList.size()];
		for(int i=0;i<attribute.length;i++){
			attribute[i] = attrList.get(i);
		}

		MyInstance mi = new MyInstance(attribute, scannum, !pep.isTP(), pep.getPrimaryScore(), pep.getRank());
		
		return mi;
	}

	public MyInstance createInstance(IPeptide pep, IMS2PeakList peaklist){
		
		int scannum = pep.getScanNumBeg();
		String seq = pep.getSequence();
		String uniqueSeq = PeptideUtil.getUniqueSequence(seq);
		int length = uniqueSeq.length();
		short charge = pep.getCharge();
		
		int modcount = seq.length()-length-4;
		
		Ions ions = aaf.fragment(pep.getPeptideSequence(), ionTypes,
		        true);
		Ion [] bs = ions.getIons(Ion.TYPE_B);
		Ion [] ys = ions.getIons(Ion.TYPE_Y);
		IPeak [] peaks = peaklist.getPeaksSortByIntensity();
		
		
		double [] bIonMasses = new double [bs.length];
		double [] yIonMasses = new double [ys.length];
		
		for(int i=0;i<bIonMasses.length;i++){
			bIonMasses[i] = bs[i].getMz()-AminoAcidProperty.PROTON_W;
		}
		
		double totalInten = 0;
		int matchCount = 0;
		
		for(int i=0;i<peaks.length;i++){
			
			double mz = peaks[i].getMz();
//			double intensity = peaks[i].getIntensity();
			double intensity = (double)(peaks.length-i)/(double)peaks.length;
//			double intensity = Math.log(peaks[i].getIntensity());

			for(int j=1;j<=charge;j++){
				
//				int matchCount = 0;
				ArrayList <Integer> matchedList = new ArrayList <Integer>();
				
				double mr = (mz-AminoAcidProperty.PROTON_W)*(double) j;
				int idy = Arrays.binarySearch(yIonMasses, mr);
				if(idy<0) idy = -idy-1;
				
				if(idy==yIonMasses.length){
					if(Math.abs(mr-yIonMasses[yIonMasses.length-1])<tolerance){
						totalInten += intensity;
						matchCount++;
					}
					continue;
				}
				
				for(int l=idy-1;l>=0;l--){
					if(Math.abs(mr-yIonMasses[l])<tolerance){
//						System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//						System.out.println(Arrays.toString(yIonMasses[k]));
						totalInten += intensity;
						matchCount++;
						
					}else if(mr-yIonMasses[l]>tolerance){
						break;
					}
				}

				for(int l=idy;l<yIonMasses.length;l++){
					if(Math.abs(mr-yIonMasses[l])<tolerance){
//						System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//						System.out.println(Arrays.toString(yIonMasses[k]));
						totalInten += intensity;
						matchCount++;
						
					}
				}

				int idb = Arrays.binarySearch(bIonMasses, mr);
				if(idb<0) idb = -idb-1;

				for(int k=0;k<bIonMasses.length;k++){
					
					if(idb==bIonMasses.length){
						if(Math.abs(mr-bIonMasses[bIonMasses.length-1])<tolerance){
							totalInten += intensity;
							matchCount++;
						}
						continue;
					}
					
					for(int l=idb-1;l>=0;l--){
						if(Math.abs(mr-bIonMasses[l])<tolerance){
//							System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//							System.out.println(Arrays.toString(yIonMasses[k]));
							totalInten += intensity;
							matchCount++;
							matchedList.add(k);
							
						}else if(mr-bIonMasses[l]>tolerance){
							break;
						}
					}
					
					for(int l=idb;l<bIonMasses.length;l++){
						if(Math.abs(mr-bIonMasses[l])<tolerance){
//							System.out.println("Y+"+(l+1)+"\t"+yIonMasses[k][l]+"\t"+mr+"\t"+k);
//							System.out.println(Arrays.toString(yIonMasses[k]));
							totalInten += intensity;
							matchCount++;							
						}
					}
				}
			}
		}
		
//		float sim = this.simCalor.getSim(seq, mwcalor, pep.getCharge(), peaklist);
/*		
		double hydro = GRAVYCalculator.calculate(uniqueSeq);
		double pi = PICalculator.compute(uniqueSeq);
		
		Ions ions = aaf.fragment(pep.getPeptideSequence(), ionTypes,
		        true);
		
		PeakForMatch [] peaks = SpectrumMatcher.matchBY(peaklist, ions, 
				pep.getCharge(), spThres);
		
		double [][] dd = new double[4][length];
		double [] bycount = new double [4];
		ArrayList <Double> distance = new ArrayList <Double>();
		ArrayList <Double> relaIntensity = new ArrayList <Double>();
		
		for(int i=0;i<peaks.length;i++){
			
			relaIntensity.add(peaks[i].getIntensity());
			
			if(peaks[i].isMatched()){
				
				distance.add(peaks[i].getDistance());
				
				double inten = peaks[i].getIntensity();
				int [] mt = peaks[i].getMatchedTypes();
				for(int j=0;j<mt.length;j++){
					Ion [] is = peaks[i].getMatchIons(mt[j]);
					for(int k=0;k<is.length;k++){
						if(is[k]!=null){
							int it = is[k].getType();
							int series = is[k].getSeries();
							if(it==Ion.TYPE_B){
								if(k==0){
									dd[0][series-1]+=inten;
									bycount[0]++;
								}else if(k==1){
									dd[1][series-1]+=inten;
									bycount[1]++;
								}
							}else if(it==Ion.TYPE_Y){
								if(k==0){
									dd[2][series-1]+=inten;
									bycount[2]++;
								}else if(k==1){
									dd[3][series-1]+=inten;
									bycount[3]++;
								}
							}
						}
					}
				}
			}
		}
*/		
		float mpf = 0f;
		for (int i = 0;i<uniqueSeq.length();i++) {
			char c = uniqueSeq.charAt(i);
			if (c == 'R')
				mpf += 1f;
			else if (c == 'K')
				mpf += 0.8f;
			else if (c == 'H')
				mpf += 0.5f;
		}

		
		ArrayList <Double> attrList = new ArrayList <Double>();
	
		attrList.add((double)length);
		attrList.add((double)modcount);
		attrList.add((double)pep.getCharge());
		attrList.add((double)pep.getMissCleaveNum());
//		attrList.add(pep.get)
		attrList.add(pep.getDeltaMH());
		attrList.add(pep.getDeltaMZppm());
		attrList.add((double)mpf);
		attrList.add(totalInten);
		attrList.add((double)matchCount);
//		attrList.add((double)sim);
		
		MascotPeptide mp = (MascotPeptide)pep;
		attrList.add((double)mp.getIdenThres());
		attrList.add((double)mp.getHydroScore());
		
//		attrList.add((double)pep.getPrimaryScore());
//		attrList.add(MathTool.getAve(dd[0]));
//		attrList.add(MathTool.getStdDev(dd[0]));
//		attrList.add((double)bycount[0]/(double)dd[0].length);
//		attrList.add(MathTool.getAve(dd[1]));
//		attrList.add(MathTool.getStdDev(dd[1]));
//		attrList.add((double)bycount[1]/(double)dd[1].length);
//		attrList.add(MathTool.getAve(dd[2]));
//		attrList.add(MathTool.getStdDev(dd[2]));
//		attrList.add((double)bycount[2]/(double)dd[2].length);
//		attrList.add(MathTool.getAve(dd[3]));
//		attrList.add(MathTool.getStdDev(dd[3]));
//		attrList.add((double)bycount[3]/(double)dd[3].length);
//		attrList.add(hydro);
//		attrList.add(pi);

//		double aveDis = MathTool.getAve(distance);
//		double stdevDis = MathTool.getStdDev(distance);
//		double density = (peaks.length)/(peaks[peaks.length-1].getMz()-peaks[0].getMz());
		
//		attrList.add(aveDis);
//		attrList.add(stdevDis);
//		attrList.add(density);
		
//		double aveRelaIntensity = MathTool.getAve(relaIntensity);
//		double stdevRelaIntensity = MathTool.getStdDev(relaIntensity);
		
//		attrList.add(aveRelaIntensity);
//		attrList.add(stdevRelaIntensity);
		
		double [] attribute = new double[attrList.size()];
		for(int i=0;i<attribute.length;i++){
			attribute[i] = attrList.get(i);
		}

		MyInstance mi = new MyInstance(attribute, scannum, !pep.isTP(), pep.getPrimaryScore());
		
		return mi;
	}

	public Instances createInstances(String dataName){
		
		ArrayList <Attribute> attInfo = new ArrayList <Attribute> (8);

		attInfo.add(new Attribute("length"));
		attInfo.add(new Attribute("modcount"));
		attInfo.add(new Attribute("charge"));
		attInfo.add(new Attribute("miss"));
		attInfo.add(new Attribute("b1Ave"));
		attInfo.add(new Attribute("b1Stdev"));
		attInfo.add(new Attribute("b1cr"));
		attInfo.add(new Attribute("b2Ave"));
		attInfo.add(new Attribute("b2Stdev"));
		attInfo.add(new Attribute("b2cr"));
		attInfo.add(new Attribute("y1Ave"));
		attInfo.add(new Attribute("y1Stdev"));
		attInfo.add(new Attribute("y1cr"));
		attInfo.add(new Attribute("y2Ave"));
		attInfo.add(new Attribute("y2Stdev"));
		attInfo.add(new Attribute("y2cr"));
		attInfo.add(new Attribute("hydro"));
		attInfo.add(new Attribute("pi"));
		attInfo.add(new Attribute("aveDis"));
		attInfo.add(new Attribute("stdevDis"));
		attInfo.add(new Attribute("density"));
		attInfo.add(new Attribute("aveRelaIntensity"));
		attInfo.add(new Attribute("stdevRelaIntensity"));
		
		Instances instances = new Instances(dataName, attInfo, capsity);
		
		return instances;
	}
	
	public static void main(String [] args) throws FileDamageException, IOException, PeptideParsingException{
		
//		System.out.println(System.getProperty("java.library.path"));
		
		IPeptideListReader reader = new PeptideListReader("H:\\Validation\\2D_phos_new\\clustering\\F003067_0.dat.ppl");
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf);
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			IMS2PeakList peaks = reader.getPeakLists()[0];
			String seq = pep.getSequence();
			float sim = creator.simCalor.getSim(seq, creator.mwcalor, pep.getCharge(), peaks);
		}
		reader.close();
		
	}
	
}
