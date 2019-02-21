/* 
 ******************************************************************************
 * File: OGlycanSpecSpliter3.java * * * Created on 2013-7-11
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
import java.util.HashSet;

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
 * @version 2013-7-11, 14:43:16
 */
public class OGlycanSpecSpliter3 {

	private final static double dm = 1.00286864;
	private static final String lineSeparator = IOConstant.lineSeparator;

	private File file;
	private ArrayList<String> infoStringList;
	private ArrayList<String> scanStringList;
	// private double ppm = 20;
	private OGlycanUnit[] units;
	private OGlycanUnit[] simpleUnits = new OGlycanUnit[] {
			OGlycanUnit.core1_1, OGlycanUnit.core1_2, OGlycanUnit.core1_3,
			OGlycanUnit.core1_4,  OGlycanUnit.core1_5,
			OGlycanUnit.core2_1, OGlycanUnit.core2_2,
			OGlycanUnit.core2_3, OGlycanUnit.core2_4, OGlycanUnit.core2_5 };

	private int glycocount;
	private int t1;
	private int t2;
	private double tolerance = 0.1;
	private static DecimalFormat df4 = DecimalFormats.DF0_4;

	public OGlycanSpecSpliter3(String file) throws DtaFileParsingException,
			IOException {
		this(new File(file));
	}

	public OGlycanSpecSpliter3(File file) throws DtaFileParsingException,
			IOException {
		this.file = file;
		this.units = simpleUnits;
		this.infoStringList = new ArrayList<String>();
		this.scanStringList = new ArrayList<String>();
		this.process();
	}

	private void process() throws DtaFileParsingException, IOException {

		int total = 0;

		if (file.isDirectory()) {

			File[] files = file.listFiles(new FileFilter() {

				@Override
				public boolean accept(File arg0) {
					// TODO Auto-generated method stub
					if (arg0.getName().endsWith("mgf"))
						return true;

					return false;
				}

			});

			Arrays.sort(files);
			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i]);
				MgfReader reader = new MgfReader(files[i]);
				MS2Scan ms2scan = null;
				while ((ms2scan = reader.getNextMS2Scan()) != null) {
					total++;
					String name = ms2scan.getScanName().getScanName();
					// if(name.startsWith("Locus:1.1.1.5167.8"))
					// this.judge(ms2scan);
//					this.judge(ms2scan, (i + 1));
					this.denoise(ms2scan, (i + 1));
//					this.combine(ms2scan, (i + 1));
				}
			}

		} else {

			MgfReader reader = new MgfReader(file);
			MS2Scan ms2scan = null;
			while ((ms2scan = reader.getNextMS2Scan()) != null) {
				total++;
				String name = ms2scan.getScanName().getScanName();
				// if(name.startsWith("Locus:1.1.1.5167.8"))
				// this.judge(ms2scan);
				this.judge(ms2scan, 1);
//				this.denoise(ms2scan, 1);
			}
		}

		System.out.println("O-glycan spectra count\t" + this.glycocount);
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

		ArrayList<OGlycanTypeInfo> infolist = new ArrayList<OGlycanTypeInfo>();

		double firstmass = 0;
		double firstIntensity = 0;

		L: for (int id = 0; id < noglyPeaksIntenSort.length; id++) {

			if (infolist.size() == 2)
				break;

			double mzi = noglyPeaksIntenSort[id].getMz();
			double inteni = noglyPeaksIntenSort[id].getIntensity();

			if (mzi < 450)
				continue;

			if (inteni * 5 < firstIntensity)
				break;

			int i = Arrays.binarySearch(noglyPeaks, noglyPeaksIntenSort[id]);
			if (i < 0)
				i = -i - 1;

			for (int chargeid = 1; chargeid <= 2; chargeid++) {

				double mass = (mzi - AminoAcidProperty.PROTON_W) * chargeid;

				if (Math.abs(mass - firstmass) < 5)
					continue;

				for (int j = 0; j < units.length; j++) {

					double tolei = Math.abs(mw - mass - units[j].getMass());
					// System.out.println("1\t"+"\t"+mzi+"\t"+tolei+"\t"+mw+"\t"+units[j].getMass());

					if (tolei < tolerance) {
						
						double pepmass = mw -  units[j].getMass();

						String info = "1\t" + units[j].getName() + "\t"
								+ units[j].getMass() + "\t" + pepmass + "\t" + name
								+ "\t" + monoinfo + lineSeparator;

						double[] fraglist = units[j].getFragment();

						OGlycanTypeInfo typeinfo = new OGlycanTypeInfo(mzi,
								inteni, pepmass, info, fraglist);

						infolist.add(typeinfo);
						if (infolist.size() == 1) {
							firstmass = mass;
							firstIntensity = inteni;
						}

						continue L;
					}
				}

				for (int j1 = 0; j1 < units.length; j1++) {
					for (int j2 = 0; j2 < units.length; j2++) {

						double tolei = Math.abs(mw - mass - units[j1].getMass()
								- units[j2].getMass());

						if (tolei < tolerance) {
							// System.out.println("2\t"+"\t"+mzi+"\t"+tolei+"\t"+mw+"\t"+units[j1].getMass()+"\t"+units[j2].getMass());

							double pepmass = mw - units[j1].getMass() - units[j2].getMass();
							
							HashSet<Double> frags = new HashSet<Double>();

							String info = "2\t" + units[j1].getName() + "\t"
									+ units[j1].getMass() + "\t"
									+ units[j2].getName() + "\t"
									+ units[j2].getMass() + "\t" + pepmass + "\t"
									+ name + "\t" + monoinfo + lineSeparator;

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

							OGlycanTypeInfo typeinfo = new OGlycanTypeInfo(mzi,
									inteni, pepmass, info, fraglist);

							infolist.add(typeinfo);
							if (infolist.size() == 1) {
								firstmass = mass;
								firstIntensity = inteni;
							}
							continue L;
						}
					}
				}

				for (int j1 = 0; j1 < units.length; j1++) {
					for (int j2 = 0; j2 < units.length; j2++) {
						for (int j3 = 0; j3 < units.length; j3++) {

							double tolei = Math.abs(mw - mass
									- units[j1].getMass() - units[j2].getMass()
									- units[j3].getMass());

							if (tolei < tolerance) {
								// System.out.println("3\t"+"\t"+mzi+"\t"+tolei+"\t"+mw+"\t"+units[j1].getMass()+"\t"+units[j2].getMass()+"\t"+units[j3].getMass());

								double pepmass = mw- units[j1].getMass() - units[j2].getMass() - units[j3].getMass();
								HashSet<Double> frags = new HashSet<Double>();

								String info = "3\t" + units[j1].getName()
										+ "\t" + units[j1].getMass() + "\t"
										+ units[j2].getName() + "\t"
										+ units[j2].getMass() + "\t"
										+ units[j3].getName() + "\t"
										+ units[j3].getMass() + "\t" + pepmass
										+ "\t" + name + "\t" + monoinfo
										+ lineSeparator;

								double[] fraglist1 = units[j1].getFragment();
								for (int m = 0; m < fraglist1.length; m++) {
									frags.add(fraglist1[m]);
								}

								double[] fraglist2 = units[j2].getFragment();
								for (int m = 0; m < fraglist2.length; m++) {
									frags.add(fraglist2[m]);
								}

								double[] fraglist3 = units[j3].getFragment();
								for (int m = 0; m < fraglist3.length; m++) {
									frags.add(fraglist3[m]);
								}

								double[] fraglist = new double[frags.size()];
								int fragid = 0;
								for (Double d : frags) {
									fraglist[fragid++] = d;
								}

								OGlycanTypeInfo typeinfo = new OGlycanTypeInfo(
										mzi, inteni, pepmass, info, fraglist);

								infolist.add(typeinfo);
								if (infolist.size() == 1) {
									firstmass = mass;
									firstIntensity = inteni;
								}
								continue L;
							}
						}
					}
				}
			}
		}

		if (infolist.size() == 0) {

		} else {

			for (int infoid = 0; infoid < infolist.size(); infoid++) {
				
				if(infoid==0){
					t1++;
				}else{
					t2++;
				}

				OGlycanTypeInfo typeinfoi = infolist.get(infoid);
				double pepmz = typeinfoi.getMass() / (double) charge
						+ AminoAcidProperty.PROTON_W;
				double pepmass = typeinfoi.getMass() + AminoAcidProperty.PROTON_W;
				String newname = name.replace("1.1.1", "1.1."
						+ ((fileid-1)*2 + infoid+1));

				String info = typeinfoi.getInfo().replace(name, newname);
				this.infoStringList.add(info);

				int peakcount = 0;
				StringBuilder sb = new StringBuilder();
				sb.append("BEGIN IONS" + lineSeparator);
				sb.append("PEPMASS=" + df4.format(pepmz) + lineSeparator);
				sb.append("CHARGE=" + charge + "+" + lineSeparator);
//				sb.append("CHARGE=" + "1" + "+" + lineSeparator);
				sb.append("TITLE=" + newname + lineSeparator);

				for (int i = 0; i < noglyPeaks.length; i++) {

					if (Math.abs(noglyPeaks[i].getMz() - pepmass) < tolerance) {
						continue;
					}
					
					if (Math.abs(noglyPeaks[i].getMz() - pepmass - dm) < tolerance) {
						continue;
					}

					boolean use = true;
					double[] fraglist = typeinfoi.getFragments();

					for (int j = 0; j < fraglist.length; j++) {
						
						if (Math.abs(noglyPeaks[i].getMz() - (pepmass + fraglist[j])) <= tolerance
								|| Math.abs(noglyPeaks[i].getMz() - (pepmass + fraglist[j] - 18.010565)) <= tolerance) {

							use = false;
							break;
						}
						
						if (Math.abs(noglyPeaks[i].getMz() - (pepmass + fraglist[j]) - dm) <= tolerance
								|| Math.abs(noglyPeaks[i].getMz() - (pepmass + fraglist[j] - 18.010565)  - dm) <= tolerance) {

							use = false;
							break;
						}
					}
					if (use) {
						peakcount++;
						sb.append(noglyPeaks[i].getMz() + "\t"
								+ noglyPeaks[i].getIntensity() + lineSeparator);
					}
				}

				if (peakcount <= 5)
					continue;

				sb.append("END IONS" + lineSeparator);
				this.scanStringList.add(sb.toString());
			}
		}
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
	
	/**
	 * @return the infoStringList
	 */
	public ArrayList<String> getInfoStringList() {
		return infoStringList;
	}

	/**
	 * @return the scanStringList
	 */
	public ArrayList<String> getScanStringList() {
		return scanStringList;
	}

	public void write(String out) throws IOException {

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

		PrintWriter infoWriter = new PrintWriter(out + "\\" + s1);
		for (int i = 0; i < this.infoStringList.size(); i++) {
			infoWriter.write((i+1)+"\t"+infoStringList.get(i));
		}
		infoWriter.close();

		PrintWriter scanWriter = new PrintWriter(out + "\\" + s2);
		for (int i = 0; i < this.scanStringList.size(); i++) {
			scanWriter.write(scanStringList.get(i));
		}
		scanWriter.close();
	}
	
	public void write(String out, String info) throws IOException {

		PrintWriter infoWriter = new PrintWriter(info);
		for (int i = 0; i < this.infoStringList.size(); i++) {
			infoWriter.write((i+1)+"\t"+infoStringList.get(i));
		}
		infoWriter.close();

		PrintWriter scanWriter = new PrintWriter(out);
		for (int i = 0; i < this.scanStringList.size(); i++) {
			scanWriter.write(scanStringList.get(i));
		}
		scanWriter.close();
	}

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

		OGlycanSpecSpliter3 spliter3 = new OGlycanSpecSpliter3(
				"H:\\OGlycan_0530_2D_original_data\\2D_trypsin");
		System.out.println(spliter3.getScanStringList().size() + "\t"
				+ spliter3.getInfoStringList().size()+"\t"+spliter3.t1+"\t"+spliter3.t2);
		spliter3.write("H:\\OGlycan_final_20131212\\trypsin_20131224_sp3");

		// OGlycanSpecSpliter3.test("H:\\OGlycan_final_20130712\\20120323_kappa_casein_1\\20120323_kappa_casein_1.oglycan.mgf");

		long end = System.currentTimeMillis();
		System.out.println((end - begin) / 60000.0);
	}

}
