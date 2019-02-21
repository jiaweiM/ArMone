/* 
 ******************************************************************************
 * File: OGlycanSpecSpliter4.java * * * Created on 2013-12-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-12-12, 14:40:40
 */
public class OGlycanSpecSpliter4 {

	private final static double dm = 1.00286864;
	private static final String lineSeparator = IOConstant.lineSeparator;

	private File in;
	private PrintWriter infoWriter;
	private PrintWriter scanWriter;
	// private double ppm = 20;
	private OGlycanUnit[] units;
	private OGlycanUnit[] simpleUnits = new OGlycanUnit[] {
			OGlycanUnit.core1_1, OGlycanUnit.core1_2, OGlycanUnit.core1_3,
			OGlycanUnit.core1_4,  OGlycanUnit.core1_5,
			OGlycanUnit.core2_1, OGlycanUnit.core2_2,
			OGlycanUnit.core2_3, OGlycanUnit.core2_4, OGlycanUnit.core2_5 };

	private int glycocount;
	private double tolerance = 0.1;
	private static DecimalFormat df4 = DecimalFormats.DF0_4;

	public OGlycanSpecSpliter4(String in, String out) throws DtaFileParsingException,
			IOException {
		this(new File(in), out);
	}

	public OGlycanSpecSpliter4(File in, String out) throws DtaFileParsingException,
			IOException {
		
		this.in = in;
		this.units = simpleUnits;

		File file = new File(out);
		if (!file.exists()) {
			file.mkdirs();
		}

		String filename = file.getName();
		String s1 = "";
		String s2 = "";

		if (filename.endsWith("mgf")) {

			s1 = filename.replace("mgf", "info");
			s2 = filename.replace("mgf", "oglycan.mgf");

		} else {

			s1 = filename + ".info";
			s2 = filename + ".oglycan.mgf";
		}
		
		this.infoWriter = new PrintWriter(out + "\\" + s1);
		this.scanWriter = new PrintWriter(out + "\\" + s2);
	}

	public void deglyco() throws DtaFileParsingException, IOException {

		int total = 0;

		if (in.isDirectory()) {

			File[] files = in.listFiles(new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					// TODO Auto-generated method stub
					if (arg0.getName().endsWith("mgf"))
						return true;

					return false;
				}

			});

			Arrays.sort(files);
L:			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i]);
				MgfReader reader = new MgfReader(files[i]);
				MS2Scan ms2scan = null;
				while ((ms2scan = reader.getNextMS2Scan()) != null) {
					total++;
//					String name = ms2scan.getScanName().getScanName();
//					if(name.startsWith("Locus:1.1.1.4940.6")){
						this.judge(ms2scan, (i + 1));
//						break L;
//					}
				}
			}

		} else {

			MgfReader reader = new MgfReader(in);
			MS2Scan ms2scan = null;
			while ((ms2scan = reader.getNextMS2Scan()) != null) {
				total++;
				String name = ms2scan.getScanName().getScanName();
				// if(name.startsWith("Locus:1.1.1.5167.8"))
				// this.judge(ms2scan);
				this.judge(ms2scan, 1);
			}
		}

//		System.out.println("O-glycan spectra count\t" + this.glycocount);
		
		this.infoWriter.close();
		this.scanWriter.close();
	}

	private void combine() throws DtaFileParsingException, IOException {

		int total = 0;

		if (in.isDirectory()) {

			File[] files = in.listFiles(new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					// TODO Auto-generated method stub
					if (arg0.getName().endsWith("mgf"))
						return true;

					return false;
				}

			});

			Arrays.sort(files);
L:			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i]);
				MgfReader reader = new MgfReader(files[i]);
				MS2Scan ms2scan = null;
				while ((ms2scan = reader.getNextMS2Scan()) != null) {
					total++;
//					String name = ms2scan.getScanName().getScanName();
//					if(name.startsWith("Locus:1.1.1.4940.6")){
						this.denoise(ms2scan, (i + 1));
//						break L;
//					}
				}
			}

		} else {

			MgfReader reader = new MgfReader(in);
			MS2Scan ms2scan = null;
			while ((ms2scan = reader.getNextMS2Scan()) != null) {
				total++;
				String name = ms2scan.getScanName().getScanName();
				// if(name.startsWith("Locus:1.1.1.5167.8"))
				// this.judge(ms2scan);
				this.denoise(ms2scan, 1);
			}
		}

		System.out.println("O-glycan spectra count\t" + this.glycocount);
		this.infoWriter.close();
		this.scanWriter.close();
	}
	
	private void judge(MS2Scan ms2scan, int fileid) {

		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
		double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
		double mz = pp.getMz();
		short charge = pp.getCharge();
		if (charge == 0)
			return;

		String name = ms2scan.getScanName().getScanName();
		if (name.endsWith(", "))
			name = name.substring(0, name.length() - 2);
		double intenthres1 = 0;
		double intenthres2 = peaklist.getBasePeak().getIntensity() * 0.05;

		IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
		if (temppeaks.length >= 300) {
			intenthres1 = 0.4;
		} else if (temppeaks.length < 300 && temppeaks.length >= 200) {
			intenthres1 = 0.35;
		} else if (temppeaks.length < 200 && temppeaks.length >= 140) {
			intenthres1 = 0.28;
		} else if (temppeaks.length < 140 && temppeaks.length >= 70) {
			intenthres1 = 0.2;
		} else {
			intenthres1 = 0.1;
		}

		ArrayList<Double> tempintenlist = new ArrayList<Double>();
		double[] rsdlist = new double[temppeaks.length];
		double percent = 0;
		for (int i = temppeaks.length - 1; i >= 0; i--) {
			tempintenlist.add(temppeaks[i].getIntensity());
			rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
		}

		for (int i = 1; i <= rsdlist.length; i++) {

			if (rsdlist[rsdlist.length - i] < intenthres1) {
				intenthres2 = temppeaks[i - 1].getIntensity();
				percent = ((double) temppeaks.length - i + 1)
						/ ((double) temppeaks.length);
				break;
			}
		}

		IPeak[] peaks = peaklist.getPeakArray();
		Arrays.sort(peaks);

		double maxinten = 0;
		int[] markpeaks = new int[7];

		ArrayList<IPeak> lowIntenList = new ArrayList<IPeak>();
		ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();

		boolean oglycan = false;

		for (int i = 0; i < peaks.length; i++) {

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();

			if (inteni < intenthres2) {
				lowIntenList.add(peaks[i]);
			} else {
				if (Math.abs(mzi - 163.060101) < tolerance) {
					continue;
				} else if (Math.abs(mzi - 204.086649) < tolerance) {
					oglycan = true;
					continue;
				} else if (Math.abs(mzi - 274.087412635) < tolerance) {
					markpeaks[0] = 1;
					continue;
				} else if (Math.abs(mzi - 292.102692635) < tolerance) {
					markpeaks[0] = 1;
					continue;
				} else if (Math.abs(mzi - 366.139472) < tolerance) {
					markpeaks[1] = 1;
					continue;
					// HexNAc+NeuAc
				} else if (Math.abs(mzi - 495.18269) < tolerance) {
					markpeaks[2] = 1;
					continue;
					// NeuAc*2
				} else if (Math.abs(mzi - 583.19873) < tolerance) {
					markpeaks[3] = 1;
					continue;
					// HexNAc*2
				} else if (Math.abs(mzi - 407.16665) < tolerance) {
					markpeaks[4] = 1;
					continue;
					// HexNAc*2+Hex
				} else if (Math.abs(mzi - 569.218847) < tolerance) {
					markpeaks[5] = 1;
					continue;
					// HexNAc*2+Hex*2
				} else if (Math.abs(mzi - 731.271672) < tolerance) {
					markpeaks[6] = 1;
					continue;
					// HexNAc+Hex*2, N-glyco
				} else if (Math.abs(mzi - 528.192299) < tolerance) {
					// if(inteni>intenthres2)
					// return;
					// HexNAc+Hex*3, N-glyco
				} else if (Math.abs(mzi - 690.245124) < tolerance) {
					// if(inteni>intenthres2)
					// return;
				} else {
					highIntenList.add(peaks[i]);
					if (peaks[i].getIntensity() > maxinten) {
						maxinten = peaks[i].getIntensity();
					}
					continue;
				}
			}
		}

		if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
			return;
		}

		if (highIntenList.size() <= 5)
			return;

		glycocount++;
		IPeak[] noglyPeaks = highIntenList.toArray(new IPeak[highIntenList
				.size()]);

		IPeak[] noglyPeaksIntenSort = new IPeak[noglyPeaks.length];
		System.arraycopy(noglyPeaks, 0, noglyPeaksIntenSort, 0,
				noglyPeaks.length);
		Arrays.sort(noglyPeaksIntenSort, new Comparator<IPeak>() {

			@Override
			public int compare(IPeak arg0, IPeak arg1) {
				// TODO Auto-generated method stub
				if (arg0.getIntensity() < arg1.getIntensity())
					return 1;
				else if (arg0.getIntensity() > arg1.getIntensity())
					return -1;
				return 0;
			}

		});

		StringBuilder monoinfo = new StringBuilder();
		for (int i = 0; i < markpeaks.length; i++) {
			monoinfo.append(markpeaks[i]).append("_");
		}
		
		int maxcharge = charge>3 ? 3:2;
		
		HashMap<String, OGlycanTypeInfo2> infomap = new HashMap<String, OGlycanTypeInfo2>();
		double firstIntensity = 0;
		for (int id = 0; id < noglyPeaksIntenSort.length; id++) {

			double mzi = noglyPeaksIntenSort[id].getMz();
			double inteni = noglyPeaksIntenSort[id].getIntensity();

			if (mzi < 450)
				continue;

			if (inteni * 10 < firstIntensity)
				break;

			int i = Arrays.binarySearch(noglyPeaks, noglyPeaksIntenSort[id]);
			if (i < 0)
				i = -i - 1;

			for (int chargeid = 1; chargeid <= maxcharge; chargeid++) {

				double mass = (mzi - AminoAcidProperty.PROTON_W) * chargeid;

				for (int j = 0; j < units.length; j++) {

					double[] toleis = new double[5];
					toleis[0] = Math.abs(mw - mass - units[j].getMass());
					toleis[1] = Math.abs(mw - (mass - 203.079373)
							- units[j].getMass());
					toleis[2] = Math.abs(mw - (mass - 365.132198)
							- units[j].getMass());
					toleis[3] = Math.abs(mw - (mass - 494.174789635)
							- units[j].getMass());
					toleis[4] = Math.abs(mw - (mass - 406.158746)
							- units[j].getMass());

//					for (int k = 0; k < toleis.length; k++) {
					for (int k = 0; k < 2; k++) {

						if (toleis[k] < tolerance) {

							if(!this.validate(k, markpeaks, new OGlycanUnit[]{units[j]}))
								continue;
							
							double pepmass = mw - units[j].getMass();
							if(pepmass<500) continue;
							
							if(firstIntensity==0) firstIntensity = inteni;
							String info = "1\t" + units[j].getName() + "\t"
									+ units[j].getMass() + "\t" + pepmass
									+ "\t" + name + "\t" + monoinfo;
//System.out.println("349\t"+k+"\t"+mzi+"\t"+charge+"\t"+info);
							double[] fraglist = units[j].getFragment();

							if(infomap.containsKey(units[j].getName())){
								int [] findtype = infomap.get(units[j].getName()).getFindType();
								findtype[k] = 1;
								infomap.get(units[j].getName()).setFindType(findtype);
							}else{
								int [] findtype = new int[5];
								findtype[k] = 1;
								OGlycanTypeInfo2 typeinfo = new OGlycanTypeInfo2(pepmass, findtype, fraglist, monoinfo.toString());
								infomap.put(units[j].getName(), typeinfo);
							}
						}
					}
				}

				for (int j1 = 0; j1 < units.length; j1++) {
					for (int j2 = j1; j2 < units.length; j2++) {

						double gm = units[j1].getMass() + units[j2].getMass();
						double[] toleis = new double[5];
						toleis[0] = Math.abs(mw - mass - gm);
						toleis[1] = Math.abs(mw - (mass - 203.079373) - gm);
						toleis[2] = Math.abs(mw - (mass - 365.132198) - gm);
						toleis[3] = Math.abs(mw - (mass - 494.174789635) - gm);
						toleis[4] = Math.abs(mw - (mass - 406.158746) - gm);

//						for (int k = 0; k < toleis.length; k++) {
						for (int k = 0; k < 2; k++) {

							if (toleis[k] < tolerance) {

								if(!this.validate(k, markpeaks, new OGlycanUnit[]{units[j1], units[j2]}))
									continue;
								
								double pepmass = mw - units[j1].getMass()
										- units[j2].getMass();
								if(pepmass<500) continue;
								
								if(firstIntensity==0) firstIntensity = inteni;
								
								HashSet<Double> frags = new HashSet<Double>();
								String info = "2\t" + units[j1].getName()
										+ "\t" + units[j1].getMass() + "\t"
										+ units[j2].getName() + "\t"
										+ units[j2].getMass() + "\t" + pepmass
										+ "\t" + name + "\t" + monoinfo;
//System.out.println("383\t"+k+"\t"+mzi+"\t"+charge+"\t"+info);
								String uname = units[j1].getName()+"\t"
										+ units[j2].getName();
								double[] fraglist1 = units[j1].getFragment();
								for (int m = 0; m < fraglist1.length; m++) {
									frags.add(fraglist1[m]);
								}

								double[] fraglist2 = units[j2].getFragment();
								for (int m = 0; m < fraglist2.length; m++) {
									frags.add(fraglist2[m]);
								}

								double[] fraglist = new double[frags.size()];
								int fragid = 0;
								for (Double d : frags) {
									fraglist[fragid++] = d;
								}

								if(infomap.containsKey(uname)){
									int [] findtype = infomap.get(uname).getFindType();
									findtype[k] = 1;
									infomap.get(uname).setFindType(findtype);
								}else{
									int [] findtype = new int[5];
									findtype[k] = 1;
									OGlycanTypeInfo2 typeinfo = new OGlycanTypeInfo2(pepmass, findtype, fraglist, monoinfo.toString());
									infomap.put(uname, typeinfo);
								}
							}
						}
					}
				}

				for (int j1 = 0; j1 < units.length; j1++) {
					for (int j2 = j1; j2 < units.length; j2++) {
						for (int j3 = j2; j3 < units.length; j3++) {

							double gm = units[j1].getMass()
									+ units[j2].getMass() + units[j3].getMass();
							double[] toleis = new double[5];
							toleis[0] = Math.abs(mw - mass - gm);
							toleis[1] = Math.abs(mw - (mass - 203.079373) - gm);
							toleis[2] = Math.abs(mw - (mass - 365.132198) - gm);
							toleis[3] = Math.abs(mw - (mass - 494.174789635)
									- gm);
							toleis[4] = Math.abs(mw - (mass - 406.158746) - gm);

//							for (int k = 0; k < toleis.length; k++) {
							for (int k = 0; k < 2; k++) {
								
								if (toleis[k] < tolerance) {

									if(!this.validate(k, markpeaks, new OGlycanUnit[]{units[j1], units[j2], units[j3]}))
										continue;
									
									double pepmass = mw - units[j1].getMass()
											- units[j2].getMass()
											- units[j3].getMass();
									if(pepmass<500) continue;
									
									if(firstIntensity==0) firstIntensity = inteni;
									HashSet<Double> frags = new HashSet<Double>();
									String uname = units[j1].getName()+"\t"
											+ units[j2].getName()+"\t"+ units[j3].getName();

									String info = "3\t" + units[j1].getName()
											+ "\t" + units[j1].getMass() + "\t"
											+ units[j2].getName() + "\t"
											+ units[j2].getMass() + "\t"
											+ units[j3].getName() + "\t"
											+ units[j3].getMass() + "\t"
											+ pepmass + "\t" + name + "\t"
											+ monoinfo;
System.out.println("444\t"+k+"\t"+mzi+"\t"+charge+"\t"+info);
									double[] fraglist1 = units[j1]
											.getFragment();
									for (int m = 0; m < fraglist1.length; m++) {
										frags.add(fraglist1[m]);
									}

									double[] fraglist2 = units[j2]
											.getFragment();
									for (int m = 0; m < fraglist2.length; m++) {
										frags.add(fraglist2[m]);
									}

									double[] fraglist3 = units[j3]
											.getFragment();
									for (int m = 0; m < fraglist3.length; m++) {
										frags.add(fraglist3[m]);
									}

									double[] fraglist = new double[frags.size()];
									int fragid = 0;
									for (Double d : frags) {
										fraglist[fragid++] = d;
									}

									if(infomap.containsKey(uname)){
										int [] findtype = infomap.get(uname).getFindType();
										findtype[k] = 1;
										infomap.get(uname).setFindType(findtype);
									}else{
										int [] findtype = new int[5];
										findtype[k] = 1;
										OGlycanTypeInfo2 typeinfo = new OGlycanTypeInfo2(pepmass, findtype, fraglist, monoinfo.toString());
										infomap.put(uname, typeinfo);
									}
								}
							}
						}
					}
				}
			}
		}

		if (infomap.size() > 0) {

			Iterator<String> it = infomap.keySet().iterator();
			HashMap<Double, StringBuilder> mzmap = new HashMap<Double, StringBuilder>();
			while (it.hasNext()) {

				String uname = it.next();
				OGlycanTypeInfo2 typeinfo = infomap.get(uname);
				int[] findtype = typeinfo.getFindType();
				if(findtype[0]==0) continue;
				
				double pepmz = Double.parseDouble(df4.format(typeinfo.getMass()
						/ (double) charge + AminoAcidProperty.PROTON_W));
				if (pepmz < 100)
					continue;
//System.out.println("495\t"+uname+"\t"+pepmz);

				if (mzmap.containsKey(pepmz)) {
					mzmap.get(pepmz).append("#").append(uname);
				} else {

					String newname = name + "." + fileid + "." + (mzmap.size() + 1);

					StringBuilder sb2 = new StringBuilder();
					sb2.append(newname).append("\t");
					sb2.append(typeinfo.getMass()).append("\t");
					sb2.append(typeinfo.getMarks()).append("\t");
					sb2.append(typeinfo.getFindTypeString());
					sb2.append("#").append(uname);
					mzmap.put(pepmz, sb2);

					int peakcount = 0;
					StringBuilder sb = new StringBuilder();
					sb.append("BEGIN IONS" + lineSeparator);
					sb.append("PEPMASS=" + df4.format(pepmz) + lineSeparator);
					sb.append("CHARGE=" + charge + "+" + lineSeparator);
					sb.append("TITLE=" + newname + lineSeparator);

L:					for (int i = 0; i < noglyPeaks.length; i++) {
						
//						boolean use = true;
						
						for (int chargeid = 1; chargeid <= maxcharge; chargeid++) {
							
							double peakmass = (noglyPeaks[i].getMz()-AminoAcidProperty.PROTON_W)*(double)chargeid;
							double pepmass = typeinfo.getMass();
							
							if (Math.abs(peakmass - pepmass) < tolerance || 
									Math.abs(peakmass - pepmass - dm) < tolerance) {
								
//								use = false;
								continue L;
							}
							
							double[] fraglist = typeinfo.getFragments();

							for (int j = 0; j < fraglist.length; j++) {

								if (Math.abs(peakmass - (pepmass + fraglist[j])) <= tolerance
										|| Math.abs(peakmass - (pepmass + fraglist[j] - 18.010565)) <= tolerance) {

//									use = false;
									continue L;
								}

								if (Math.abs(peakmass - (pepmass + fraglist[j]) - dm) <= tolerance
										|| Math.abs(peakmass - (pepmass + fraglist[j] - 18.010565) - dm) <= tolerance) {

//									use = false;
									continue L;
								}
							}
						}

//						if (use) {
							peakcount++;
							sb.append(noglyPeaks[i].getMz() + "\t"
									+ noglyPeaks[i].getIntensity()
									+ lineSeparator);
//						} else {
//							sb2.append(noglyPeaks[i].getMz() + "\t"
//									+ noglyPeaks[i].getIntensity() + "\t");
//						}
					}

					if (peakcount <= 5)
						continue;

					sb.append("END IONS" + lineSeparator);
					this.scanWriter.write(sb.toString());
				}
			}
			Iterator<Double> it2 = mzmap.keySet().iterator();
			while(it2.hasNext()){
				double pmz = it2.next();
//				System.out.println("574\t"+pmz);
				this.infoWriter.write(mzmap.get(pmz) + lineSeparator);
			}
		}
	}
	
	private void getEnumType(OGlycanUnit[] units){
		
	}
	
	private void denoise(MS2Scan ms2scan, int fileid){

		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
		double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
		double mz = pp.getMz();
		short charge = pp.getCharge();
		if (charge == 0)
			return;

		String name = ms2scan.getScanName().getScanName();
		if (name.endsWith(", "))
			name = name.substring(0, name.length() - 2);
		double intenthres1 = 0;
		double intenthres2 = peaklist.getBasePeak().getIntensity() * 0.05;

		String newname = name + "." + fileid;
		
		IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
		if (temppeaks.length >= 300) {
			intenthres1 = 0.4;
		} else if (temppeaks.length < 300 && temppeaks.length >= 200) {
			intenthres1 = 0.35;
		} else if (temppeaks.length < 200 && temppeaks.length >= 140) {
			intenthres1 = 0.28;
		} else if (temppeaks.length < 140 && temppeaks.length >= 70) {
			intenthres1 = 0.2;
		} else {
			intenthres1 = 0.1;
		}

		ArrayList<Double> tempintenlist = new ArrayList<Double>();
		double[] rsdlist = new double[temppeaks.length];
		double percent = 0;
		for (int i = temppeaks.length - 1; i >= 0; i--) {
			tempintenlist.add(temppeaks[i].getIntensity());
			rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
		}

		for (int i = 1; i <= rsdlist.length; i++) {

			if (rsdlist[rsdlist.length - i] < intenthres1) {
				intenthres2 = temppeaks[i - 1].getIntensity();
				percent = ((double) temppeaks.length - i + 1)
						/ ((double) temppeaks.length);
				break;
			}
		}

		IPeak[] peaks = peaklist.getPeakArray();
		Arrays.sort(peaks);

		int[] markpeaks = new int[7];
		boolean oglycan = false;

		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN IONS" + lineSeparator);
		sb.append("PEPMASS=" + df4.format(mz) + lineSeparator);
		sb.append("CHARGE=" + charge + "+" + lineSeparator);
//		sb.append("CHARGE=" + "1" + "+" + lineSeparator);
		sb.append("TITLE=" + newname + lineSeparator);
		
		int peakcount = 0;
		
		for (int i = 0; i < peaks.length; i++) {

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();

			if (inteni < intenthres2) {

			} else {
				
				if (Math.abs(mzi - 163.060101) < tolerance) {
					continue;
				} else if (Math.abs(mzi - 204.086649) < tolerance) {
					oglycan = true;
					continue;
				} else if (Math.abs(mzi - 274.087412635) < tolerance) {
					markpeaks[0] = 1;
					continue;
				} else if (Math.abs(mzi - 292.102692635) < tolerance) {
					markpeaks[0] = 1;
					continue;
				} else if (Math.abs(mzi - 366.139472) < tolerance) {
					markpeaks[1] = 1;
					continue;
					// HexNAc+NeuAc
				} else if (Math.abs(mzi - 495.18269) < tolerance) {
					markpeaks[2] = 1;
					continue;
					// NeuAc*2
				} else if (Math.abs(mzi - 583.19873) < tolerance) {
					markpeaks[3] = 1;
					continue;
					// HexNAc*2
				} else if (Math.abs(mzi - 407.16665) < tolerance) {
					markpeaks[4] = 1;
					continue;
					// HexNAc*2+Hex
				} else if (Math.abs(mzi - 569.218847) < tolerance) {
					markpeaks[5] = 1;
					continue;
					// HexNAc*2+Hex*2
				} else if (Math.abs(mzi - 731.271672) < tolerance) {
					markpeaks[6] = 1;
					continue;
					// HexNAc+Hex*2, N-glyco
				} else if (Math.abs(mzi - 528.192299) < tolerance) {
					// if(inteni>intenthres2)
					// return;
					// HexNAc+Hex*3, N-glyco
					continue;
					
				} else if (Math.abs(mzi - 690.245124) < tolerance) {
					// if(inteni>intenthres2)
					// return;
					continue;
					
				} else {
					
				}
				
				peakcount++;
				sb.append(mzi + "\t" + inteni + lineSeparator);
			}
		}

		if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
			return;
		}

		if (peakcount <= 5)
			return;

		sb.append("END IONS" + lineSeparator);
		this.scanWriter.write(sb.toString());
	}
	
	private boolean validate(int findType, int[] marks, OGlycanUnit[] unit) {
		
		int [] typeCount = new int[10];
		for(int i=0;i<unit.length;i++){
			if(unit[i]==OGlycanUnit.core1_1){
				typeCount[0]++;
			}else if(unit[i]==OGlycanUnit.core1_2){
				typeCount[1]++;
			}else if(unit[i]==OGlycanUnit.core1_3){
				typeCount[2]++;
			}else if(unit[i]==OGlycanUnit.core1_4){
				typeCount[3]++;
			}else if(unit[i]==OGlycanUnit.core1_5){
				typeCount[4]++;
			}else if(unit[i]==OGlycanUnit.core2_1){
				typeCount[5]++;
			}else if(unit[i]==OGlycanUnit.core2_2){
				typeCount[6]++;
			}else if(unit[i]==OGlycanUnit.core2_3){
				typeCount[7]++;
			}else if(unit[i]==OGlycanUnit.core2_4){
				typeCount[8]++;
			}else if(unit[i]==OGlycanUnit.core2_5){
				typeCount[9]++;
			}
		}
		
		boolean validate = false;
		if(findType==2){
			for (int i = 0; i < typeCount.length; i++) {
				if (i!=0 && i!=2 && typeCount[i]>0) {
					validate = true;
					break;
				}
			}
		}else{
			validate = true;
		}
		
		if(validate==false) 
			return false;
//System.out.println(Arrays.toString(typeCount));
		int[] possible = new int[3];
		for(int i=0;i<unit.length;i++){
			int[] composition = unit[i].getCompCount();
			for(int j=0;j<composition.length;j++){
				possible[j]+=composition[j];
			}
		}

		for(int i=0;i<marks.length;i++){
			if(marks[i]==0)
				continue;
			
			switch(i){
			case 0:
				if(possible[2]==0) return false;
				break;
			case 1:
				if(possible[1]==0) return false;
				break;
			case 2:
				if(typeCount[3]==0 && typeCount[4]==0) return false;
				break;
			case 3:
				if(typeCount[4]==0) return false;
				break;
			case 4:
				if(typeCount[5]==0 && typeCount[6]==0 && typeCount[7]==0 && typeCount[8]==0 && typeCount[9]==0) return false;
				break;
			case 5:
				if(typeCount[5]==0 && typeCount[6]==0 && typeCount[7]==0 && typeCount[8]==0 && typeCount[9]==0) return false;
				break;
			case 6:
				if(typeCount[7]==0 && typeCount[8]==0 && typeCount[9]==0) return false;
				break;
			}
		}

		return true;
	}

	/*private void denoise(MS2Scan ms2scan, int fileid){

		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
		double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
		double mz = pp.getMz();
		short charge = pp.getCharge();
		if (charge == 0)
			return;

		String name = ms2scan.getScanName().getScanName();
		if (name.endsWith(", "))
			name = name.substring(0, name.length() - 2);
		double intenthres1 = 0;
		double intenthres2 = peaklist.getBasePeak().getIntensity() * 0.05;

		String newname = name.replace("1.1.1", "1.1."
				+ ((fileid-1)*2 + 1));
		
		IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
		if (temppeaks.length >= 300) {
			intenthres1 = 0.4;
		} else if (temppeaks.length < 300 && temppeaks.length >= 200) {
			intenthres1 = 0.35;
		} else if (temppeaks.length < 200 && temppeaks.length >= 140) {
			intenthres1 = 0.28;
		} else if (temppeaks.length < 140 && temppeaks.length >= 70) {
			intenthres1 = 0.2;
		} else {
			intenthres1 = 0.1;
		}

		ArrayList<Double> tempintenlist = new ArrayList<Double>();
		double[] rsdlist = new double[temppeaks.length];
		double percent = 0;
		for (int i = temppeaks.length - 1; i >= 0; i--) {
			tempintenlist.add(temppeaks[i].getIntensity());
			rsdlist[tempintenlist.size() - 1] = MathTool.getRSD(tempintenlist);
		}

		for (int i = 1; i <= rsdlist.length; i++) {

			if (rsdlist[rsdlist.length - i] < intenthres1) {
				intenthres2 = temppeaks[i - 1].getIntensity();
				percent = ((double) temppeaks.length - i + 1)
						/ ((double) temppeaks.length);
				break;
			}
		}

		IPeak[] peaks = peaklist.getPeakList();
		Arrays.sort(peaks);

		int[] markpeaks = new int[7];
		boolean oglycan = false;

		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN IONS" + lineSeparator);
		sb.append("PEPMASS=" + df4.format(mz) + lineSeparator);
		sb.append("CHARGE=" + charge + "+" + lineSeparator);
//		sb.append("CHARGE=" + "1" + "+" + lineSeparator);
		sb.append("TITLE=" + newname + lineSeparator);
		
		int peakcount = 0;
		
		for (int i = 0; i < peaks.length; i++) {

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();

			if (inteni < intenthres2) {

			} else {
				
				if (Math.abs(mzi - 163.060101) < tolerance) {
					continue;
				} else if (Math.abs(mzi - 204.086649) < tolerance) {
					oglycan = true;
					continue;
				} else if (Math.abs(mzi - 274.087412635) < tolerance) {
					markpeaks[0] = 1;
					continue;
				} else if (Math.abs(mzi - 292.102692635) < tolerance) {
					markpeaks[0] = 1;
					continue;
				} else if (Math.abs(mzi - 366.139472) < tolerance) {
					markpeaks[1] = 1;
					continue;
					// HexNAc+NeuAc
				} else if (Math.abs(mzi - 495.18269) < tolerance) {
					markpeaks[2] = 1;
					continue;
					// NeuAc*2
				} else if (Math.abs(mzi - 583.19873) < tolerance) {
					markpeaks[3] = 1;
					continue;
					// HexNAc*2
				} else if (Math.abs(mzi - 407.16665) < tolerance) {
					markpeaks[4] = 1;
					continue;
					// HexNAc*2+Hex
				} else if (Math.abs(mzi - 569.218847) < tolerance) {
					markpeaks[5] = 1;
					continue;
					// HexNAc*2+Hex*2
				} else if (Math.abs(mzi - 731.271672) < tolerance) {
					markpeaks[6] = 1;
					continue;
					// HexNAc+Hex*2, N-glyco
				} else if (Math.abs(mzi - 528.192299) < tolerance) {
					// if(inteni>intenthres2)
					// return;
					// HexNAc+Hex*3, N-glyco
					continue;
					
				} else if (Math.abs(mzi - 690.245124) < tolerance) {
					// if(inteni>intenthres2)
					// return;
					continue;
					
				} else {
					
				}
				
				peakcount++;
				sb.append(mzi + "\t" + inteni + lineSeparator);
			}
		}

		if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
			return;
		}

		if (peakcount <= 5)
			return;

		sb.append("END IONS" + lineSeparator);
		this.scanStringList.add(sb.toString());
	}

	private void combine(MS2Scan ms2scan, int fileid){

		IMS2PeakList peaklist = ms2scan.getPeakList();
		PrecursePeak pp = peaklist.getPrecursePeak();
		double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
		double mz = pp.getMz();
		short charge = pp.getCharge();
		if (charge == 0)
			return;

		String name = ms2scan.getScanName().getScanName();
		if (name.endsWith(", "))
			name = name.substring(0, name.length() - 2);
		double intenthres1 = 0;
		double intenthres2 = peaklist.getBasePeak().getIntensity() * 0.05;

		String newname = name.replace("1.1.1", "1.1."
				+ ((fileid-1)*2 + 1));
		
		IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
		if (temppeaks.length >= 300) {
			intenthres1 = 0.4;
		} else if (temppeaks.length < 300 && temppeaks.length >= 200) {
			intenthres1 = 0.35;
		} else if (temppeaks.length < 200 && temppeaks.length >= 140) {
			intenthres1 = 0.28;
		} else if (temppeaks.length < 140 && temppeaks.length >= 70) {
			intenthres1 = 0.2;
		} else {
			intenthres1 = 0.1;
		}

		ArrayList<Double> tempintenlist = new ArrayList<Double>();
		double[] rsdlist = new double[temppeaks.length];
		double percent = 0;
		for (int i = temppeaks.length - 1; i >= 0; i--) {
			tempintenlist.add(temppeaks[i].getIntensity());
			rsdlist[tempintenlist.size() - 1] = MathTool.getRSD(tempintenlist);
		}

		for (int i = 1; i <= rsdlist.length; i++) {

			if (rsdlist[rsdlist.length - i] < intenthres1) {
				intenthres2 = temppeaks[i - 1].getIntensity();
				percent = ((double) temppeaks.length - i + 1)
						/ ((double) temppeaks.length);
				break;
			}
		}

		IPeak[] peaks = peaklist.getPeakList();
		Arrays.sort(peaks);

		int[] markpeaks = new int[7];
		boolean oglycan = false;

		StringBuilder sb = new StringBuilder();
		sb.append("BEGIN IONS" + lineSeparator);
		sb.append("PEPMASS=" + df4.format(mz) + lineSeparator);
		sb.append("CHARGE=" + charge + "+" + lineSeparator);
//		sb.append("CHARGE=" + "1" + "+" + lineSeparator);
		sb.append("TITLE=" + newname + lineSeparator);
		
		int peakcount = 0;
		
		for (int i = 0; i < peaks.length; i++) {

			double mzi = peaks[i].getMz();
			double inteni = peaks[i].getIntensity();

			if (inteni < intenthres2) {

			} else {
				
				if (Math.abs(mzi - 163.060101) < tolerance) {

				} else if (Math.abs(mzi - 204.086649) < tolerance) {
					oglycan = true;
				} else if (Math.abs(mzi - 274.087412635) < tolerance) {
					markpeaks[0] = 1;
				} else if (Math.abs(mzi - 292.102692635) < tolerance) {
					markpeaks[0] = 1;
				} else if (Math.abs(mzi - 366.139472) < tolerance) {
					markpeaks[1] = 1;
					// HexNAc+NeuAc
				} else if (Math.abs(mzi - 495.18269) < tolerance) {
					markpeaks[2] = 1;
					// NeuAc*2
				} else if (Math.abs(mzi - 583.19873) < tolerance) {
					markpeaks[3] = 1;
					// HexNAc*2
				} else if (Math.abs(mzi - 407.16665) < tolerance) {
					markpeaks[4] = 1;
					// HexNAc*2+Hex
				} else if (Math.abs(mzi - 569.218847) < tolerance) {
					markpeaks[5] = 1;
					// HexNAc*2+Hex*2
				} else if (Math.abs(mzi - 731.271672) < tolerance) {
					markpeaks[6] = 1;
					// HexNAc+Hex*2, N-glyco
				} else if (Math.abs(mzi - 528.192299) < tolerance) {
					// if(inteni>intenthres2)
					// return;
					// HexNAc+Hex*3, N-glyco
					
				} else if (Math.abs(mzi - 690.245124) < tolerance) {
					// if(inteni>intenthres2)
					// return;					
				} else {
					
				}
				
				peakcount++;
				sb.append(mzi + "\t" + inteni + lineSeparator);
			}
		}

		if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
			return;
		}

		if (peakcount <= 5)
			return;

		sb.append("END IONS" + lineSeparator);
		this.scanStringList.add(sb.toString());
	}
	*/

	private static void test(String in) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("BEGIN")) {
				System.out.println(line);
			} else if (line.startsWith("PEPMASS")) {
				System.out.println(line);
			}
		}
		reader.close();
	}
	
	

	/**
	 * @param args
	 * @throws IOException
	 * @throws DtaFileParsingException
	 */
	public static void main(String[] args) throws DtaFileParsingException,
			IOException {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
		
//		OGlycanSpecSpliter4 spliter = new OGlycanSpecSpliter4(
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\2D_elastase", "H:\\OGLYCAN\\OGlycan_final_20140312\\2D_elastase");
		
		OGlycanSpecSpliter4 spliter = new OGlycanSpecSpliter4(
//				"H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin", "H:\\OGLYCAN\\OGlycan_final_20140312\\fetuin");
				"D:\\P\\o-glyco\\2014.03.04.revise\\test\\original", "D:\\P\\o-glyco\\2014.03.04.revise\\test\\result");
		
		spliter.deglyco();
		
		long end = System.currentTimeMillis();
		System.out.println((end - begin) / 60000.0);
	}

}
