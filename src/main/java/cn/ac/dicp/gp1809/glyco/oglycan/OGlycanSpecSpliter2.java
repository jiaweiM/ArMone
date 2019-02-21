/* 
 ******************************************************************************
 * File: OGlycanSpecSpliter2.java * * * Created on 2013-7-9
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-7-9, 15:07:41
 */
public class OGlycanSpecSpliter2 {
	
	/**
	 * nbt.1511-S1, p9
	 */
	private final static double dm = 1.00286864;
	private static final String lineSeparator = IOConstant.lineSeparator;
	
	private MgfReader reader;
	private PrintWriter pw;
//	private double ppm = 20;
	private OGlycanUnit [] units;
	private OGlycanUnit [] simpleUnits= new OGlycanUnit[]{OGlycanUnit.core1_1, OGlycanUnit.core1_2, OGlycanUnit.core1_3,
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
	private int ichi = 0;
	private int ni = 0;
	
	public OGlycanSpecSpliter2(String file) throws DtaFileParsingException, FileNotFoundException{
		this(new File(file));
	}
	
	public OGlycanSpecSpliter2(File file) throws DtaFileParsingException, FileNotFoundException{
		
		this.reader = new MgfReader(file);
		String out = file.getAbsolutePath().replace("mgf", "peps.info");
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
		typesets[0].add("core2_5");
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
		MS2Scan ms2scan = null;
		while((ms2scan=reader.getNextMS2Scan())!=null){
			total++;
			String name = ms2scan.getScanName().getScanName();
//			if(name.startsWith("Locus:1.1.1.5167.8"))
//			this.judge(ms2scan);
			this.judge(ms2scan);
		}
		System.out.println("glyco count\t"+this.glycocount);
		int totalog = 0;
		for(int i=0;i<splist.length;i++){
			System.out.println(i+"\t"+splist[i].size());
			totalog+=splist[i].size();
		}
		System.out.println(totalog);
		System.out.println(ichi+"\t"+ni);
		pw.close();
	}
	
	private void judge(MS2Scan ms2scan){

		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
		double mw = pp.getMH()-AminoAcidProperty.PROTON_W;
		double mz = pp.getMz();
		short charge = pp.getCharge();
		String name = ms2scan.getScanName().getScanName();
		if(name.endsWith(", ")) name = name.substring(0, name.length()-2);
		double intenthres1 = 0;
		double intenthres2 = peaklist.getBasePeak().getIntensity()*0.05;
		
		IPeak [] temppeaks = peaklist.getPeaksSortByIntensity();
		if(temppeaks.length>=300){
			intenthres1 = 0.4;
		}else if(temppeaks.length<300 && temppeaks.length>=200){
			intenthres1 = 0.35;
		}else if(temppeaks.length<200 && temppeaks.length>=140){
			intenthres1 = 0.28;
		}else if(temppeaks.length<140 && temppeaks.length>=70){
			intenthres1 = 0.2;
		}else{
			intenthres1 = 0.1;
		}
		
		ArrayList <Double> tempintenlist = new ArrayList <Double>();
		double [] rsdlist = new double[temppeaks.length];
		double percent = 0;
		for(int i=temppeaks.length-1;i>=0;i--){
			tempintenlist.add(temppeaks[i].getIntensity());
			rsdlist[tempintenlist.size()-1] = MathTool.getRSDInDouble(tempintenlist);
		}

		for(int i=1;i<=rsdlist.length;i++){
			
			if(rsdlist[rsdlist.length-i]<intenthres1){
				intenthres2 = temppeaks[i-1].getIntensity();
				percent = ((double)temppeaks.length-i+1)/((double)temppeaks.length);
				break;
			}
		}
//System.out.println(intenthres1+"\t"+intenthres2);
		IPeak [] peaks = peaklist.getPeakArray();
		Arrays.sort(peaks);

		double maxinten = 0;
		int [] markpeaks = new int [7];
		
		ArrayList <IPeak> lowIntenList = new ArrayList <IPeak>();
		ArrayList <IPeak> highIntenList = new ArrayList <IPeak>();
		
		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN IONS"+lineSeparator);
		sb.append("PEPMASS="+mz+lineSeparator);
		sb.append("CHARGE="+charge+"+"+lineSeparator);
		sb.append("TITLE="+name+lineSeparator);

		boolean oglycan = false;
		
		for(int i=0;i<peaks.length;i++){

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();

			if(inteni<intenthres2){
				lowIntenList.add(peaks[i]);
			}else{
				if(Math.abs(mzi-163.060101)<tolerance){
					continue;
				}else if(Math.abs(mzi-204.086649)<tolerance){
					oglycan = true;
					continue;
				}else if(Math.abs(mzi-274.087412635)<tolerance){
					markpeaks[0] = 1;
					continue;
				}else if(Math.abs(mzi-292.102692635)<tolerance){
					markpeaks[0] = 1;
					continue;
				}else if(Math.abs(mzi-366.139472)<tolerance){
					markpeaks[1] = 1;
					continue;
				// HexNAc+NeuAc	
				}else if(Math.abs(mzi-495.18269)<tolerance){
					markpeaks[2] = 1;
					continue;
				// NeuAc*2
				}else if(Math.abs(mzi-583.19873)<tolerance){
					markpeaks[3] = 1;
					continue;
				// HexNAc*2
				}else if(Math.abs(mzi-407.16665)<tolerance){
					markpeaks[4] = 1;
					continue;
				// HexNAc*2+Hex
				}else if(Math.abs(mzi-569.218847)<tolerance){
					markpeaks[5] = 1;
					continue;
				// HexNAc*2+Hex*2
				}else if(Math.abs(mzi-731.271672)<tolerance){
					markpeaks[6] = 1;
					continue;
				// HexNAc+Hex*2, N-glyco
				}else if(Math.abs(mzi-528.192299)<tolerance){
//					if(inteni>intenthres2)
//						return;
				// HexNAc+Hex*3, N-glyco	
				}else if(Math.abs(mzi-690.245124)<tolerance){
//					if(inteni>intenthres2)
//						return;
				}else{
					highIntenList.add(peaks[i]);
					if(peaks[i].getIntensity()>maxinten){
						maxinten = peaks[i].getIntensity();
					}
					continue;
				}
			}
		}

		if(!oglycan && MathTool.getTotal(markpeaks)<3){
			return;
		}
		
		if(highIntenList.size()<=5)
			return;

		glycocount++;
		IPeak [] noglyPeaks = highIntenList.toArray(new IPeak[highIntenList.size()]);
		double mzmax = noglyPeaks[noglyPeaks.length-1].getMz();
		double pepmz = 0;
		double pepinten = 0;

		StringBuilder monoinfo = new StringBuilder();
		for(int i=0;i<markpeaks.length;i++){
			monoinfo.append(markpeaks[i]).append("_");
		}

		ArrayList <OGlycanTypeInfo> infolist = new ArrayList <OGlycanTypeInfo>();
		
		IPeak [] noglyPeaksIntenSort = new IPeak[noglyPeaks.length];
		System.arraycopy(noglyPeaks, 0, noglyPeaksIntenSort, 0, noglyPeaks.length);
		Arrays.sort(noglyPeaksIntenSort, new Comparator<IPeak>(){

			@Override
			public int compare(IPeak arg0, IPeak arg1) {
				// TODO Auto-generated method stub
				if(arg0.getIntensity()<arg1.getIntensity())
					return 1;
				else if(arg0.getIntensity()>arg1.getIntensity())
					return -1;
				return 0;
			}
			
		});
		
		double firstmz = 0;
		double firstIntensity = 0;

L:		for(int id = 0;id<noglyPeaksIntenSort.length;id++){
	
			if(infolist.size()==2)
				break;

			double mzi = noglyPeaksIntenSort[id].getMz();
			double inteni = noglyPeaksIntenSort[id].getIntensity();
			
			if(mzi<450)
				continue;
			
			if(inteni*5<firstIntensity)
				break;

			int i = Arrays.binarySearch(noglyPeaks, noglyPeaksIntenSort[id]);
			if(i<0) i = -i-1;
			
			boolean pass = false;
			int possicharge = 1;
			
			if(mzi+203.079373>mzmax){
				pass = true;
			}else{
L1:			for(int chargeid=1;chargeid<=charge;chargeid++){
	
					for(int j=i+1;j<noglyPeaks.length;j++){
						
						double mzj = noglyPeaks[j].getMz();
					
						if(Math.abs((mzj-mzi)*(double)chargeid-203.079373)<tolerance){
							possicharge = chargeid;
							pass = true;
							break L1;
							
						}else if(Math.abs((mzj-mzi)*(double)chargeid-203.079373+18.010565)<tolerance){
							possicharge = chargeid;
							pass = true;
							break L1;
						}
					}
				}
			}
	
//			if(!pass) continue;
			
			pass = false;
			for(int j=i+1;j<noglyPeaks.length;j++){
				double mzj = noglyPeaks[j].getMz();
				for(int chargeid=1;chargeid<=charge;chargeid++){
					if(Math.abs((mzj-mzi)*(double)chargeid-203.079373)<tolerance){
						pass = true;
						break;
					}else if(Math.abs(mzj-mzi-203.079373+18.010565)<tolerance){
						pass = true;
						break;
					}
				}
			}

for(int chargeid=1;chargeid<=2;chargeid++)	{possicharge = chargeid;
			mzi = (mzi-AminoAcidProperty.PROTON_W)*possicharge;
			
			if(Math.abs(mzi-firstmz)<2)
				continue;

			for(int j=0;j<units.length;j++){

				double tolei = Math.abs(mw-mzi-units[j].getMass());
//System.out.println("1\t"+"\t"+mzi+"\t"+tolei+"\t"+mw+"\t"+units[j].getMass());
				
				if(tolei<tolerance){

					pepmz = mzi;
					pepinten = inteni;
					String unitname = units[j].getName();

					for(int k=i+1;k<noglyPeaks.length;k++){
						tolei = Math.abs(mw-noglyPeaks[k].getMz()-units[j].getMass());
						if(tolei<tolerance){
							if(noglyPeaks[k].getIntensity()>pepinten){
								pepmz = noglyPeaks[k].getMz();
								pepinten = noglyPeaks[k].getIntensity();
							}
						}else{
							break;
						}
					}

					for(int l=0;l<this.typesets.length;l++){
						
						if(typesets[l].contains(unitname)){

							String info  = (l+1)+"\t1\t"+units[j].getName()+"\t"+
									units[j].getMass()+"\t"+pepmz+"\t"+name+"\t"+monoinfo+lineSeparator;

							double [] fraglist = units[j].getFragment();
							
							OGlycanTypeInfo typeinfo = new OGlycanTypeInfo(mzi, inteni, l, info, fraglist);
							infolist.add(typeinfo);
							if(infolist.size()==1){
								firstmz = mzi;
								firstIntensity = inteni;
							}
							
							continue L;
						}
					}
				}
			}
			
			for(int j1=0;j1<units.length;j1++){
				for(int j2=0;j2<units.length;j2++){
					
					double tolei = Math.abs(mw-mzi-units[j1].getMass()-units[j2].getMass());
					if(tolei<tolerance){
//System.out.println("2\t"+"\t"+mzi+"\t"+tolei+"\t"+mw+"\t"+units[j1].getMass()+"\t"+units[j2].getMass());					

						pepmz = mzi;
						pepinten = inteni;
						
						String unitname1 = units[j1].getName();
						String unitname2 = units[j2].getName();

						for(int k=i+1;k<noglyPeaks.length;k++){
							tolei = Math.abs(mw-noglyPeaks[k].getMz()-units[j1].getMass()-units[j2].getMass());
							if(tolei<tolerance){
								if(noglyPeaks[k].getIntensity()>pepinten){
									pepmz = noglyPeaks[k].getMz();
									pepinten = noglyPeaks[k].getIntensity();
								}
							}else{
								break;
							}
						}

						HashSet <Double> frags = new HashSet <Double>();
						
						for(int l=0;l<this.typesets.length;l++){
							if(typesets[l].contains(unitname1) && typesets[l].contains(unitname2)){

								String info = (l+1)+"\t2\t"+units[j1].getName()+"\t"+
										units[j1].getMass()+"\t"+units[j2].getName()+"\t"+
										units[j2].getMass()+"\t"+pepmz+"\t"+name+"\t"+monoinfo+lineSeparator;
								
								double [] fraglist1 = units[j1].getFragment();
								for(int m=0;m<fraglist1.length;m++){
									frags.add(fraglist1[m]);
								}
								
								double [] fraglist2 = units[j2].getFragment();
								for(int m=0;m<fraglist2.length;m++){
									frags.add(fraglist2[m]);
								}
								
								double [] fraglist = new double[frags.size()];
								int fragid = 0;
								for(Double d : frags){
									fraglist[fragid++] = d;
								}
								
								OGlycanTypeInfo typeinfo = new OGlycanTypeInfo(mzi, inteni, l, info, fraglist);
								infolist.add(typeinfo);
								if(infolist.size()==1){
									firstmz = mzi;
									firstIntensity = inteni;
								}
								continue L;
							}
						}
					}
				}
			}
			
			for(int j1=0;j1<units.length;j1++){
				for(int j2=0;j2<units.length;j2++){
					for(int j3=0;j3<units.length;j3++){
						
						double tolei = Math.abs(mw-mzi-units[j1].getMass()-units[j2].getMass()-units[j3].getMass());
						
						if(tolei<tolerance){
//System.out.println("3\t"+"\t"+mzi+"\t"+tolei+"\t"+mw+"\t"+units[j1].getMass()+"\t"+units[j2].getMass()+"\t"+units[j3].getMass());					

							pepmz = mzi;
							pepinten = inteni;
							
							String unitname1 = units[j1].getName();
							String unitname2 = units[j2].getName();
							String unitname3 = units[j3].getName();

							for(int k=i+1;k<noglyPeaks.length;k++){
								tolei = Math.abs(mw-noglyPeaks[k].getMz()-units[j1].getMass()-units[j2].getMass()
										-units[j3].getMass());
								if(tolei<tolerance){
									if(noglyPeaks[k].getIntensity()>pepinten){
										pepmz = noglyPeaks[k].getMz();
										pepinten = noglyPeaks[k].getIntensity();
									}
								}else{
									break;
								}
							}

							HashSet <Double> frags = new HashSet <Double>();
							
							for(int l=0;l<this.typesets.length;l++){
								if(typesets[l].contains(unitname1) && typesets[l].contains(unitname2)
										&& typesets[l].contains(unitname3)){

									String info = (l+1)+"\t3\t"+units[j1].getName()+"\t"+
											units[j1].getMass()+"\t"+units[j2].getName()+"\t"+
											units[j2].getMass()+"\t"+units[j3].getName()+"\t"+
											units[j3].getMass()+"\t"+pepmz+"\t"+name+"\t"+monoinfo+lineSeparator;
									
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
									
									double [] fraglist = new double[frags.size()];
									int fragid = 0;
									for(Double d : frags){
										fraglist[fragid++] = d;
									}
									
									OGlycanTypeInfo typeinfo = new OGlycanTypeInfo(mzi, inteni, l, info, fraglist);
									infolist.add(typeinfo);
									if(infolist.size()==1){
										firstmz = mzi;
										firstIntensity = inteni;
									}
									continue L;
								}
							}
						}
					}
				}
			}			
}			
		}
		
//System.out.println("487\t"+pepmz+"\t"+infolist);

		if(infolist.size()==0){
			
			this.dislist.add(ms2scan);
			return;
			
		}else{
			
			if(infolist.size()==1){
				ichi++;
			}else if(infolist.size()==2){
				ni++;
			}
			
			for(int infoid=0;infoid<infolist.size();infoid++){
				
				OGlycanTypeInfo typeinfoi = infolist.get(infoid);
				
				int peakcount = 0;

				StringBuilder sbi = new StringBuilder();
				sbi.append(sb);
				
				for(int i=0;i<noglyPeaks.length;i++){
					
					if(Math.abs(pepmz-noglyPeaks[i].getMz())<tolerance){
						continue;
					}
					
					boolean use = true;
					double [] fraglist = typeinfoi.getFragments();

					for(int j=0;j<fraglist.length;j++){
						if(Math.abs(pepmz+fraglist[j]-noglyPeaks[i].getMz())<=tolerance ||
								Math.abs(pepmz+fraglist[j]-18.010565-noglyPeaks[i].getMz())<=tolerance){
							
							use = false;

							break;
						}
					}
					if(use){
						peakcount++;
						sbi.append(noglyPeaks[i].getMz()+"\t"+noglyPeaks[i].getIntensity()+lineSeparator);
					}
				}

				if(peakcount<=5) continue;

				pw.write(typeinfoi.getInfo());
				
				sbi.append("END IONS"+lineSeparator);
				
				this.splist[typeinfoi.getType()].add(sbi.toString());
			}
		}

	}
/*
	private void judgeProfile(MS2Scan ms2scan){
		
		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
		double mh = pp.getMH();
		double mz = pp.getMz();
		short charge = pp.getCharge();
		String name = ms2scan.getScanName().getScanName();
		if(name.endsWith(", ")) name = name.substring(0, name.length()-2);
		double intenthres1 = 0;
		double intenthres2 = peaklist.getBasePeak().getIntensity()*0.05;
		
		IPeak [] temppeaks = peaklist.getPeaksSortByIntensity();
		if(temppeaks.length>=300){
			intenthres1 = 0.4;
		}else if(temppeaks.length<300 && temppeaks.length>=200){
			intenthres1 = 0.35;
		}else if(temppeaks.length<200 && temppeaks.length>=140){
			intenthres1 = 0.28;
		}else if(temppeaks.length<140 && temppeaks.length>=70){
			intenthres1 = 0.2;
		}else{
			intenthres1 = 0.1;
		}
		
		ArrayList <Double> tempintenlist = new ArrayList <Double>();
		double [] rsdlist = new double[temppeaks.length];
		double percent = 0;
		for(int i=temppeaks.length-1;i>=0;i--){
			tempintenlist.add(temppeaks[i].getIntensity());
			rsdlist[tempintenlist.size()-1] = MathTool.getRSD(tempintenlist);
		}

		for(int i=1;i<=rsdlist.length;i++){
			
			if(rsdlist[rsdlist.length-i]<intenthres1){
				intenthres2 = temppeaks[i-1].getIntensity();
				percent = ((double)temppeaks.length-i+1)/((double)temppeaks.length);
				break;
			}
		}

		IPeak [] peaks = peaklist.getPeakList();
		Arrays.sort(peaks);

		double maxinten = 0;
		int [] markpeaks = new int [7];
		
		ArrayList <IPeak> lowIntenList = new ArrayList <IPeak>();
		ArrayList <IPeak> highIntenList = new ArrayList <IPeak>();
		
		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN IONS"+lineSeparator);
		sb.append("PEPMASS="+mz+lineSeparator);
		sb.append("CHARGE="+charge+"+"+lineSeparator);
		sb.append("TITLE="+name+lineSeparator);

		for(int i=0;i<peaks.length;i++){

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();

			if(inteni<intenthres2){
				lowIntenList.add(peaks[i]);
			}else{
				if(Math.abs(mzi-163.060101)<tolerance){
					continue;
				}else if(Math.abs(mzi-204.086649)<tolerance){
					continue;
				}else if(Math.abs(mzi-274.087412635)<tolerance){
					markpeaks[0] = 1;
					continue;
				}else if(Math.abs(mzi-292.102692635)<tolerance){
					markpeaks[0] = 1;
					continue;
				}else if(Math.abs(mzi-366.139472)<tolerance){
					markpeaks[1] = 1;
					continue;
				}else if(Math.abs(mzi-495.18269)<tolerance){
					markpeaks[2] = 1;
					continue;
				}else if(Math.abs(mzi-583.19873)<tolerance){
					markpeaks[3] = 1;
					continue;
				}else if(Math.abs(mzi-407.16665)<tolerance){
					markpeaks[4] = 1;
					continue;
				}else if(Math.abs(mzi-569.218847)<tolerance){
					markpeaks[5] = 1;
					continue;
				}else if(Math.abs(mzi-731.271672)<tolerance){
					markpeaks[6] = 1;
					continue;
				}else if(Math.abs(mzi-528.192299)<tolerance){
					if(inteni>intenthres2)
						return;
				}else if(Math.abs(mzi-690.245124)<tolerance){
					if(inteni>intenthres2)
						return;
				}else{
					highIntenList.add(peaks[i]);
					if(peaks[i].getIntensity()>maxinten){
						maxinten = peaks[i].getIntensity();
					}
					continue;
				}
			}
		}
		
		if(highIntenList.size()+lowIntenList.size()==peaks.length || highIntenList.size()<=5) return;

		glycocount++;
		IPeak [] noglyPeaks = highIntenList.toArray(new IPeak[highIntenList.size()]);
		double mzmax = noglyPeaks[noglyPeaks.length-1].getMz();
		double pepmz = 0;
		double pepinten = 0;
		
		HashSet <Double> frags = new HashSet <Double>();
		String info = "";
		StringBuilder monoinfo = new StringBuilder();
		for(int i=0;i<markpeaks.length;i++){
			monoinfo.append(markpeaks[i]).append("_");
		}
		int type = -1;

L:		for(int i=0;i<noglyPeaks.length;i++){
	
			double mzi = noglyPeaks[i].getMz();
			double inteni = noglyPeaks[i].getIntensity();

			boolean pass = false;
			if(mzi+203.079373>mzmax){
				pass = true;
			}else{
				for(int j=i+1;j<noglyPeaks.length;j++){
					double mzj = noglyPeaks[j].getMz();
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
System.out.println(pass+"\t"+mzi+"\t");
			for(int j=0;j<units.length;j++){

				double tolei = Math.abs(mh-mzi-units[j].getMass());
System.out.println("1\t"+pass+"\t"+mzi+"\t"+tolei+"\t"+mh+"\t"+units[j].getMass());
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
//										continue L;
									}
									if(pepinten/maxinten<0.4){
										pepinten = 0;
//										continue L;
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
System.out.println("2\t"+pass+"\t"+mzi+"\t"+tolei+"\t"+mh+"\t"+units[j1].getMass()+"\t"+units[j2].getMass());					
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
//											continue L;
										}
										if(pepinten/maxinten<0.4){
											pepinten = 0;
//											continue L;
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
System.out.println("3\t"+pass+"\t"+mzi+"\t"+tolei+"\t"+mh+"\t"+units[j1].getMass()+"\t"+units[j2].getMass()+"\t"+units[j3].getMass());					
						
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
//												continue L;
											}
											if(pepinten/maxinten<0.4){
												pepinten = 0;
//												continue L;
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
//System.out.println(pepmz+"\t"+type);
		if(pepmz==0 || type==-1){
			this.dislist.add(ms2scan);
			return;
		}
		
		int peakcount = 0;
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
				peakcount++;
				sb.append(noglyPeaks[i].getMz()+"\t"+noglyPeaks[i].getIntensity()+lineSeparator);
			}
		}

		if(peakcount<=5) return;

		pw.write(info);
		sb.append("END IONS"+lineSeparator);
		this.splist[type].add(sb.toString());
	}
	
	public void writeDis(String out) throws IOException{
		PrintWriter pw = new PrintWriter(out);
		for(int i=0;i<this.dislist.size();i++){
			
			MS2Scan ms2scan = dislist.get(i);
			IMS2PeakList peaklist = ms2scan.getPeakList();
			PrecursePeak pp = peaklist.getPrecursePeak();
			double mz = pp.getMz();
			short charge = pp.getCharge();
			String name = ms2scan.getScanName().getScanName();
			if(name.endsWith(", ")) name = name.substring(0, name.length()-2);
			
			IPeak [] peaks = peaklist.getPeakList();
			Arrays.sort(peaks);
			
			StringBuilder sb = new StringBuilder();
			sb.append("BEGIN IONS"+lineSeparator);
			sb.append("PEPMASS="+mz+lineSeparator);
			sb.append("CHARGE="+charge+"+"+lineSeparator);
			sb.append("TITLE="+name+lineSeparator);
			for(int j=0;j<peaks.length;j++){
				sb.append(peaks[j].getMz()+"\t"+peaks[j].getIntensity()+lineSeparator);
			}
			sb.append("END IONS"+lineSeparator);
			pw.write(sb.toString());
		}
		pw.close();
	}
*/

	public HashMap <String, ArrayList <String>> getSpecMap(){
		return map1;
	}
	
	public ArrayList <String> getSpecList(){
		return complexlist;
	}
	
	public ArrayList <String> [] getTypeList(){
		return splist;
	}
	
	public void dispose(){
		this.reader.close();
	}

	/**
	 * @param args
	 * @throws DtaFileParsingException 
	 * @throws IOException 
	 * @throws CloneNotSupportedException 
	 */
	public static void main(String[] args) throws DtaFileParsingException, IOException {
		// TODO Auto-generated method stub

		String file = "H:\\OGlycan_final\\1D_complex\\20120328_humaneserum_trypsin_HILIC_8uL-02.mgf";
		OGlycanSpecSpliter2 spliter = new OGlycanSpecSpliter2(file);
		spliter.split();
		spliter.dispose();
//		spliter.writeDis("H:\\OGlycan\\20130305\\1\\20130414\\rest.mgf");
	}

}
