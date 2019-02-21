/* 
 ******************************************************************************
 * File: OGlycanSpecSpliter4Profile.java * * * Created on 2013-4-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * @author ck
 *
 * @version 2013-4-15, 12:59:58
 */
public class OGlycanSpecSpliter4Profile {
	
	/**
	 * nbt.1511-S1, p9
	 */
	private final static double dm = 1.00286864;
	private final static double [] dm2 = new double[]{0.002, 0.003, 0.004, 0.0045, 0.005, 0.005, 0.006};
	private static final String lineSeparator = IOConstant.lineSeparator;
	private static final DecimalFormat df4 = DecimalFormats.DF0_4;
	
	private MzXMLReader reader;
	private PrintWriter pw;
	private double ppm = 20;
	private double [] markIons = new double []{163.060101, 204.086649, 274.087412635, 292.102692635, 366.139472,
			407.16665, 495.18269, 528.192299, 569.218847, 583.19873, 690.245124, 731.271672};
	
	private OGlycanUnit [] units;
	private OGlycanUnit [] simpleUnits = new OGlycanUnit[]{OGlycanUnit.core1_1, OGlycanUnit.core1_2, OGlycanUnit.core1_3,
			OGlycanUnit.core1_4, OGlycanUnit.core1_4b, OGlycanUnit.core1_5, OGlycanUnit.core1_5b,
			OGlycanUnit.core2_1, OGlycanUnit.core2_2, OGlycanUnit.core2_3, OGlycanUnit.core2_4, OGlycanUnit.core2_5};;
			
	private HashMap <String, ArrayList <String>> map1;
//	private HashMap <String, ArrayList <String>> map2;
	private ArrayList <String> [] splist;
//	private HashMap <String, Integer> countmap;
	private ArrayList <String> complexlist;
	private int glycocount;
	private double tolerance = 0.1;
	private HashSet <String> [] typesets;
	private ArrayList <MS2Scan> dislist;
	
	public OGlycanSpecSpliter4Profile(String file) throws DtaFileParsingException, FileNotFoundException, XMLStreamException{
		this(new File(file));
	}
	
	public OGlycanSpecSpliter4Profile(File file) throws DtaFileParsingException, FileNotFoundException, XMLStreamException{
		
		this.reader = new MzXMLReader(file);
		String out = file.getAbsolutePath().replace("mzXML", "glyco.peps.info");
		this.pw = new PrintWriter(out);
//		this.units = OGlycanUnit.values();
		this.units = simpleUnits;
		this.dislist = new ArrayList <MS2Scan>();
		
		this.map1 = new HashMap <String, ArrayList <String>>();
//		this.map2 = new HashMap <String, ArrayList <String>>();
		this.complexlist = new ArrayList <String>();
//		this.countmap = new HashMap <String, Integer>();
		this.splist = new ArrayList [5];
		this.typesets = new HashSet [5];
		for(int i=0;i<5;i++){
			splist[i] = new ArrayList <String>();
			typesets[i] = new HashSet <String>();
		}
		typesets[0].add("core1_4");
		typesets[1].add("core1_5");
		typesets[2].add("core1_1");
		typesets[2].add("core1_2");
		typesets[2].add("core2_1");
		typesets[2].add("core2_3");
		typesets[3].add("core1_1");
		typesets[3].add("core1_2");
		typesets[3].add("core1_3");
		typesets[3].add("core1_4");
		typesets[3].add("core2_2");
		typesets[4].add("core1_2");
		typesets[4].add("core1_4");
		typesets[4].add("core1_5");
		typesets[4].add("core2_4");
		typesets[4].add("core2_5");
	}
	
	public void split(){
		
		int total = 0;
		ISpectrum spec = null;
		IPeak [] ms1peaks = null;
		double totalCurrent = -1;
		while((spec=reader.getNextSpectrum())!=null){
			int level = spec.getMSLevel();
			if(level==1){
				ms1peaks = spec.getPeakList().getPeakArray();
				totalCurrent = spec.getTotIonCurrent();
			}else{
				MS2Scan ms2scan = (MS2Scan) spec;
//				if(ms2scan.getScanNum()==4305){
					this.judge(ms2scan, ms1peaks, totalCurrent);
//					break;
//				}
				
				total++;
//				System.out.println(total);
				if(ms2scan.getScanNum()%1000==0){
					System.out.println(ms2scan.getScanNum());
				}
			}
		}
		System.out.println(this.glycocount);
		int totalog = 0;
		for(int i=0;i<splist.length;i++){
			System.out.println(i+"\t"+splist[i].size());
			totalog+=splist[i].size();
		}
		System.out.println(totalog);
		
		pw.close();
		
	}

	private void judge(MS2Scan ms2scan, IPeak [] ms1peaks, double totalCurrent){
		
		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
//		double mh = pp.getMH();
		double mz = pp.getMz();
//		short charge = pp.getCharge();
//		String name = ms2scan.getScanName().getScanName();
//		if(name.endsWith(", ")) name = name.substring(0, name.length()-2);
//		double intenthres1 = 0;
//		double intenthres2 = peaklist.getBasePeak().getIntensity()*0.05;
		double totalIonCurrent = ms2scan.getTotIonCurrent();
		if(totalIonCurrent<30000) return;
		
		IPeak [] peaks = peaklist.getPeakArray();
		ArrayList <IPeak> highIntenList = new ArrayList <IPeak>();

		int [] markpeaks = new int [7];
		double maxinten = 0;
		
		boolean begin = false;
		double peakinten = 0;
		double peakMaxInten = 0;
		double peakmz = 0;
		int peakcount = 0;
		for(int i=1;i<peaks.length;i++){
			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();
			if(inteni==0){
				if(begin){
					if(peakcount>=6 && (peaks[i-1].getMz()>peakmz)){
						IPeak peak = new Peak(peakmz, peakinten);
						highIntenList.add(peak);
						if(peakinten>maxinten){
							maxinten = peakinten;
						}
					}
					peakinten = 0;
					peakMaxInten = 0;
					peakmz = 0;
					peakcount = 0;
					begin = false;
				}else{
					continue;
				}
			}
			int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
			if(begin){
				if(mzi-peaks[i-1].getMz()<=dm2[dmid]){
					if(mzi-peakmz<0.03){
						peakinten += inteni;
						peakcount++;
						if(inteni>peakMaxInten){
							peakMaxInten = inteni;
							peakmz = mzi;
						}
					}else{
						if(peakcount>=6){
							IPeak peak = new Peak(peakmz, peakinten);
							highIntenList.add(peak);
							if(peakinten>maxinten){
								maxinten = peakinten;
							}
						}
						peakinten = 0;
						peakMaxInten = 0;
						peakmz = 0;
						peakcount = 0;
						begin = false;
					}
					
				}else{
					if(peakcount>=6){
						IPeak peak = new Peak(peakmz, peakinten);
						highIntenList.add(peak);
						if(peakinten>maxinten){
							maxinten = peakinten;
						}
					}
					peakinten = 0;
					peakMaxInten = 0;
					peakmz = 0;
					peakcount = 0;
					begin = false;
				}
			}else{
				if(mzi-peaks[i-1].getMz()<=dm2[dmid]){
					if(inteni>peaks[i-1].getIntensity()){
						begin = true;
						peakinten = peaks[i-1].getIntensity()+inteni;
						peakcount = 2;
						peakMaxInten = inteni;
						peakmz = mzi;
					}
				}
			}
		}
		
		int matchMarkIon = 0;
		int markid = 0;
		Iterator <IPeak> it = highIntenList.iterator();
		while(it.hasNext()){
			IPeak peaki = it.next();
			double mzi = peaki.getMz();
			double inteni = peaki.getIntensity();
			if(inteni*100<maxinten){
				it.remove();
				continue;
			}
			for(int j=markid;j<markIons.length;j++){
				if(Math.abs(mzi-this.markIons[j])<0.02){
					matchMarkIon++;
					it.remove();
					break;
				}else if(mzi-this.markIons[j]>0.02){
					markid++;
				}else if(this.markIons[j]-mzi>0.02){
					break;
				}
			}
		}

		if(matchMarkIon<2 || highIntenList.size()<=8) return;
		
		Peak minus3peak = new Peak(pp.getMz()-3.0, 0);
		int precursorId = Arrays.binarySearch(ms1peaks, minus3peak);
		if(precursorId<0) precursorId = -precursorId-1;
		
		ArrayList <IPeak> preIsotope = new ArrayList <IPeak>();
		
		boolean prebegin = false;
		double prepeakinten = 0;
		double prepeakMaxInten = 0;
		double prepeakmz = 0;
		int prepeakcount = 0;
		for(int i=precursorId+1;i<ms1peaks.length;i++){
			
			double mzi = ms1peaks[i].getMz();
			double inteni = ms1peaks[i].getIntensity();
			if(inteni==0){
				if(prebegin){
					if(prepeakcount>=6){
						if(prepeakmz-pp.getMz()<3){
//							System.out.println("274\t"+prepeakmz+"\t"+prepeakinten+"\t"+totalCurrent*0.01);
							if(prepeakinten>5000){
								IPeak peak = new Peak(prepeakmz, prepeakinten);
								preIsotope.add(peak);
							}
							
						}else{
							break;
						}
					}
					prepeakinten = 0;
					prepeakMaxInten = 0;
					prepeakmz = 0;
					prepeakcount = 0;
					prebegin = false;
				}else{
					continue;
				}
			}
			
			int dmid = mzi/200.0>6 ? 6 : (int)mzi/200;
			if(prebegin){
				if(mzi-ms1peaks[i-1].getMz()<=dm2[dmid]){
					if(mzi-prepeakmz<0.03){
						prepeakinten += inteni;
						prepeakcount++;
						if(inteni>prepeakMaxInten){
							prepeakMaxInten = inteni;
							prepeakmz = mzi;
						}
					}else{
						if(prepeakcount>=6){
							if(prepeakmz-pp.getMz()<3){
//								System.out.println("307\t"+prepeakmz+"\t"+prepeakinten+"\t"+totalCurrent*0.01);
								if(prepeakinten>5000){
									IPeak peak = new Peak(prepeakmz, prepeakinten);
									preIsotope.add(peak);
								}
								
							}else{
								break;
							}
						}
						prepeakinten = 0;
						prepeakMaxInten = 0;
						prepeakmz = 0;
						prepeakcount = 0;
						prebegin = false;
					}
					
				}else{
					if(prepeakcount>=6){
						if(prepeakmz-pp.getMz()<3){
//							System.out.println("327\t"+prepeakmz+"\t"+prepeakinten+"\t"+totalCurrent*0.01);
							if(prepeakinten>5000){
								IPeak peak = new Peak(prepeakmz, prepeakinten);
								preIsotope.add(peak);
							}
							
						}else{
							break;
						}
					}
					prepeakinten = 0;
					prepeakMaxInten = 0;
					prepeakmz = 0;
					prepeakcount = 0;
					prebegin = false;
				}
			}else{
				if(mzi-ms1peaks[i-1].getMz()<=dm2[dmid] && ms1peaks[i-1].getIntensity()>0){
					if(inteni>ms1peaks[i-1].getIntensity()){
						prebegin = true;
						prepeakinten = ms1peaks[i-1].getIntensity()+inteni;
						prepeakcount = 2;
						prepeakMaxInten = ms1peaks[i].getIntensity();
						prepeakmz = mzi;
					}
				}
			}
		}
		
		IPeak finalPrecursor = null;
		double massdiff = 100;
		int preId = -1;
		double monomass = -1;
		double monointen = -1;
		for(int i=0;i<preIsotope.size();i++){
			IPeak pi = preIsotope.get(i);
			double diffi = Math.abs(pp.getMz()-pi.getMz());
			if(diffi<massdiff){
				massdiff = diffi;
				finalPrecursor = pi;
				preId = i;
				monomass = pi.getMz();
				monointen = pi.getIntensity();
			}
		}
		
		int [] preIsoScore = new int [4];
		int [] isocount = new int [4];
		Arrays.fill(isocount, 1);
		for(int i=preId+1;i<preIsotope.size();i++){
			IPeak pi = preIsotope.get(i);
			if(Math.abs(Math.log10(monointen/pi.getIntensity()))>1)
				continue;
			if(Math.abs(pi.getMz()-finalPrecursor.getMz()-0.25*(double)isocount[3])<0.03){
				preIsoScore[3]++;
				isocount[3]++;;
			}else if(Math.abs(pi.getMz()-finalPrecursor.getMz()-0.3333*(double)isocount[2])<0.03){
				preIsoScore[2]++;
				isocount[2]++;;
			}else if(Math.abs(pi.getMz()-finalPrecursor.getMz()-0.5*(double)isocount[1])<0.03){
				preIsoScore[1]++;
				isocount[1]++;;
			}else if(Math.abs(pi.getMz()-finalPrecursor.getMz()-1.0*(double)isocount[0])<0.03){
				preIsoScore[0]++;
				isocount[0]++;;
			}
		}
		
		int charge = -1;
		int maxScore = -1;
		for(int i=0;i<preIsoScore.length;i++){
			if(preIsoScore[i]>maxScore){
				maxScore = preIsoScore[i];
				charge = i+1;
			}
		}

		if(charge==-1){
			
			for(int i=preId-1;i>=0;i--){
				IPeak pi = preIsotope.get(i);
				if(Math.abs(Math.log10(monointen/pi.getIntensity()))>1)
					continue;
				if(Math.abs(pi.getMz()-finalPrecursor.getMz()-0.25*(double)isocount[3])<0.03){
					preIsoScore[3]++;
					isocount[3]++;;
				}else if(Math.abs(pi.getMz()-finalPrecursor.getMz()-0.3333*(double)isocount[2])<0.03){
					preIsoScore[2]++;
					isocount[2]++;;
				}else if(Math.abs(pi.getMz()-finalPrecursor.getMz()-0.5*(double)isocount[1])<0.03){
					preIsoScore[1]++;
					isocount[1]++;;
				}else if(Math.abs(pi.getMz()-finalPrecursor.getMz()-1.0*(double)isocount[0])<0.03){
					preIsoScore[0]++;
					isocount[0]++;;
				}
			}
			
			for(int i=0;i<preIsoScore.length;i++){
				if(preIsoScore[i]>maxScore){
					maxScore = preIsoScore[i];
					charge = i+1;
				}
			}
			
			if(charge==-1){
				return;
			}else{
				for(int i=preId-1;i>=0;i--){
					IPeak pi = preIsotope.get(i);
					if(Math.abs(monomass-pi.getMz()-1.0/(double)charge)<0.03){
						monomass = pi.getMz();
						monointen = pi.getIntensity();
					}
				}
			}
			
		}else{
			for(int i=preId-1;i>=0;i--){
				IPeak pi = preIsotope.get(i);
				if(Math.abs(monomass-pi.getMz()-1.0/(double)charge)<0.03){
					monomass = pi.getMz();
					monointen = pi.getIntensity();
				}
			}
		}
/*		if(monomass>0){
			System.out.println(pp.getScanNum()+"\t"+ms2scan.getScanNum()+"\t"+totalCurrent+"\t"+pp.getMz()+"\t"+
					monomass+"\t"+charge+"\t"+Arrays.toString(preIsoScore)+"\t"+preIsotope.size());
		}
			if(ms2scan.getScanNum()==4305){
				
				for(int i=0;i<preIsotope.size();i++){
					IPeak pi = preIsotope.get(i);
					System.out.println(pi.getMz()+"\t"+pi.getIntensity());
				}
				System.out.println("~~~~~~~~~~~~~~~~~");
				for(int i=precursorId;i<ms1peaks.length;i++){
					IPeak pi = ms1peaks[i];
//					System.out.println(pp.getScanNum()+"\t"+pp.getMz()+"\t"+ms1peaks[i].getMz()+"\t"+ms1peaks[i].getIntensity());
					System.out.println(pi.getMz()+"\t"+pi.getIntensity());
					if(pi.getMz()-pp.getMz()>3)
						break;
				}
				System.exit(0);
			}
			if(ms2scan.getScanNum()==4305){
				System.out.println("charge\t"+charge);
				System.exit(0);
			}
*/			
			
/*			StringBuilder sb = new StringBuilder();
			sb.append("BEGIN IONS"+lineSeparator);
			sb.append("PEPMASS="+df4.format(mz)+lineSeparator);
			sb.append("CHARGE="+charge+"+"+lineSeparator);
			sb.append("TITLE="+"Spectrum "+ms2scan.getScanNum()+lineSeparator);
			for(int i=0;i<highIntenList.size();i++){
				sb.append(highIntenList.get(i).getMz()+"\t"+highIntenList.get(i).getIntensity()+lineSeparator);
			}
			sb.append("END IONS"+lineSeparator);
			this.pw.write(sb.toString());
*/			
//		}

		glycocount++;
		IPeak [] noglyPeaks = highIntenList.toArray(new IPeak[highIntenList.size()]);
		double mzmax = noglyPeaks[noglyPeaks.length-1].getMz();
		double pepmz = 0;
		double pepinten = 0;
		double mh = (monomass-AminoAcidProperty.PROTON_W)*(double)charge;
		String name = "Spectrum "+ms2scan.getScanNum();
		
		HashSet <Double> frags = new HashSet <Double>();
		String info = "";
		StringBuilder monoinfo = new StringBuilder();
		for(int i=0;i<markpeaks.length;i++){
			monoinfo.append(markpeaks[i]).append("_");
		}
		int type = -1;

		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN IONS"+lineSeparator);
		sb.append("PEPMASS="+mz+lineSeparator);
		sb.append("CHARGE="+charge+"+"+lineSeparator);
		sb.append("TITLE="+name+lineSeparator);
		
L:		for(int i=0;i<noglyPeaks.length;i++){
	
			for(int chargeid=0;chargeid<=charge;chargeid++){
				
				double mzi = (noglyPeaks[i].getMz()-AminoAcidProperty.PROTON_W)*chargeid;
				double inteni = noglyPeaks[i].getIntensity();

				boolean pass = false;
				if(mzi+203.079373>mzmax){
					pass = true;
				}else{
					for(int j=i+1;j<noglyPeaks.length;j++){
						double mzj = (noglyPeaks[j].getMz()-AminoAcidProperty.PROTON_W)*chargeid;
						if(Math.abs(mzj-mzi-203.079373)<tolerance){
							pass = true;
							break;
						}else if(Math.abs(mzj-mzi-203.079373+18.010565)<tolerance){
							pass = true;
							break;
						}
					}
				}
				if(!pass) continue;
	//System.out.println(pass+"\t"+mzi+"\t");
				for(int j=0;j<units.length;j++){

					double tolei = Math.abs(mh-mzi-units[j].getMass());
	//System.out.println("1\t"+pass+"\t"+mzi+"\t"+tolei+"\t"+mh+"\t"+units[j].getMass());
					if(tolei<tolerance){

						pepmz = mzi;
						pepinten = inteni;
						String unitname = units[j].getName();

						for(int k=i+1;k<noglyPeaks.length;k++){
							tolei = Math.abs(mh-noglyPeaks[k].getMz()-units[j].getMass());
							if(tolei<tolerance){
								if(noglyPeaks[k].getIntensity()>pepinten){
									pepmz = noglyPeaks[k].getMz();
									pepinten = noglyPeaks[k].getIntensity();
								}
							}else{
								for(int l=0;l<this.typesets.length;l++){
									if(typesets[l].contains(unitname)){

										if(l==2 && markpeaks[0]==1){
											pepinten = 0;
//											continue L;
										}
										if(pepinten/maxinten<0.4){
											pepinten = 0;
//											continue L;
										}
										
										info = (l+1)+"\t1\t"+units[j].getName()+"\t"+
												units[j].getMass()+"\t"+pepmz+"\t"+name+"\t"+monoinfo+lineSeparator;
										type = l;

										double [] fraglist = units[j].getFragment();
										for(int m=0;m<fraglist.length;m++){
											frags.add(fraglist[m]);
										}
										
										break L;
									}
								}
							}
						}
					}
				}
				
				for(int j1=0;j1<units.length;j1++){
					for(int j2=0;j2<units.length;j2++){
						
						double tolei = Math.abs(mh-mzi-units[j1].getMass()-units[j2].getMass());
	//System.out.println("2\t"+pass+"\t"+mzi+"\t"+tolei+"\t"+mh+"\t"+units[j1].getMass()+"\t"+units[j2].getMass());					
						if(tolei<tolerance){

							pepmz = mzi;
							pepinten = inteni;
							
							String unitname1 = units[j1].getName();
							String unitname2 = units[j2].getName();

							for(int k=i+1;k<noglyPeaks.length;k++){
								tolei = Math.abs(mh-noglyPeaks[k].getMz()-units[j1].getMass()-units[j2].getMass());
								if(tolei<tolerance){
									if(noglyPeaks[k].getIntensity()>pepinten){
										pepmz = noglyPeaks[k].getMz();
										pepinten = noglyPeaks[k].getIntensity();
									}
								}else{
									for(int l=0;l<this.typesets.length;l++){
										if(typesets[l].contains(unitname1) && typesets[l].contains(unitname2)){
											
											if(l==2 && markpeaks[0]==1){
												pepinten = 0;
//												continue L;
											}
											if(pepinten/maxinten<0.4){
												pepinten = 0;
//												continue L;
											}
											
											info = (l+1)+"\t2\t"+units[j1].getName()+"\t"+
													units[j1].getMass()+"\t"+units[j2].getName()+"\t"+
													units[j2].getMass()+"\t"+pepmz+"\t"+name+"\t"+monoinfo+lineSeparator;
											type = l;
											
											double [] fraglist1 = units[j1].getFragment();
											for(int m=0;m<fraglist1.length;m++){
												frags.add(fraglist1[m]);
											}
											
											double [] fraglist2 = units[j2].getFragment();
											for(int m=0;m<fraglist2.length;m++){
												frags.add(fraglist2[m]);
											}
											
											break L;
										}
									}
								}
							}
						}
					}
				}
				
				for(int j1=0;j1<units.length;j1++){
					for(int j2=0;j2<units.length;j2++){
						for(int j3=0;j3<units.length;j3++){
							
							double tolei = Math.abs(mh-mzi-units[j1].getMass()-units[j2].getMass()-units[j3].getMass());
	//System.out.println("3\t"+pass+"\t"+mzi+"\t"+tolei+"\t"+mh+"\t"+units[j1].getMass()+"\t"+units[j2].getMass()+"\t"+units[j3].getMass());					
							
							if(tolei<tolerance){

								pepmz = mzi;
								pepinten = inteni;
								
								String unitname1 = units[j1].getName();
								String unitname2 = units[j2].getName();
								String unitname3 = units[j3].getName();

								for(int k=i+1;k<noglyPeaks.length;k++){
									tolei = Math.abs(mh-noglyPeaks[k].getMz()-units[j1].getMass()-units[j2].getMass()
											-units[j3].getMass());
									if(tolei<tolerance){
										if(noglyPeaks[k].getIntensity()>pepinten){
											pepmz = noglyPeaks[k].getMz();
											pepinten = noglyPeaks[k].getIntensity();
										}
									}else{
										for(int l=0;l<this.typesets.length;l++){
											if(typesets[l].contains(unitname1) && typesets[l].contains(unitname2)
													&& typesets[l].contains(unitname3)){
												
												if(l==2 && markpeaks[0]==1){
													pepinten = 0;
//													continue L;
												}
												if(pepinten/maxinten<0.4){
													pepinten = 0;
//													continue L;
												}
												
												info = (l+1)+"\t3\t"+units[j1].getName()+"\t"+
														units[j1].getMass()+"\t"+units[j2].getName()+"\t"+
														units[j2].getMass()+"\t"+units[j3].getName()+"\t"+
														units[j3].getMass()+"\t"+pepmz+"\t"+name+"\t"+monoinfo+lineSeparator;
												type = l;
												
												double [] fraglist1 = units[j1].getFragment();
												for(int m=0;m<fraglist1.length;m++){
													frags.add(fraglist1[m]);
												}
												
												double [] fraglist2 = units[j2].getFragment();
												for(int m=0;m<fraglist2.length;m++){
													frags.add(fraglist2[m]);
												}
												
												double [] fraglist3 = units[j3].getFragment();
												for(int m=0;m<fraglist3.length;m++){
													frags.add(fraglist3[m]);
												}
												
												break L;
											}
										}
									}
								}
							}
						}
					}
				}			
			}
		}
//System.out.println(pepmz+"\t"+type);
		if(pepmz==0 || type==-1){
			this.dislist.add(ms2scan);
			return;
		}
		
		int peakcount1 = 0;
		Arrays.sort(noglyPeaks);
		for(int i=0;i<noglyPeaks.length;i++){
			
			if(Math.abs(pepmz-noglyPeaks[i].getMz())<tolerance){
				continue;
			}
			
			boolean use = true;
			Double [] fraglist = frags.toArray(new Double[frags.size()]);
			Arrays.sort(fraglist);
			for(int j=0;j<fraglist.length;j++){
				if(Math.abs(pepmz+fraglist[j]-noglyPeaks[i].getMz())<=tolerance ||
						Math.abs(pepmz+fraglist[j]-18.010565-noglyPeaks[i].getMz())<=tolerance){
					
					use = false;

					break;
				}
			}
			if(use){
				peakcount1++;
				sb.append(noglyPeaks[i].getMz()+"\t"+noglyPeaks[i].getIntensity()+lineSeparator);
			}
		}

		if(peakcount1<=5) return;

		pw.write(info);
		sb.append("END IONS"+lineSeparator);
		this.splist[type].add(sb.toString());
		
	}

	public void dispose(){
		this.reader.close();
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 * @throws DtaFileParsingException 
	 */
	public static void main(String[] args) throws DtaFileParsingException, FileNotFoundException, XMLStreamException {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
		
		String file = "H:\\OGlycan\\20130305\\1\\all_scan.mzXML";
		OGlycanSpecSpliter4Profile spliter = new OGlycanSpecSpliter4Profile(file);
		spliter.split();
		spliter.dispose();
		
		long end = System.currentTimeMillis();
		System.out.println((end-begin)/1000.0+"s");
	}

}
