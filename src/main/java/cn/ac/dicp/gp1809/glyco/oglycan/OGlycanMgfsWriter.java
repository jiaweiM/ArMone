/* 
 ******************************************************************************
 * File: OGlycanMgfsWriter.java * * * Created on 2012-12-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

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
 * @version 2012-12-17, 8:55:21
 */
public class OGlycanMgfsWriter {

	private File dir;

	public OGlycanMgfsWriter(String dir) {
		this(new File(dir));
	}

	public OGlycanMgfsWriter(File dir) {
		if (!dir.exists()) {
			dir.mkdir();
		}
		this.dir = dir;
	}

	public void write(ArrayList<String>[] typelist) throws IOException {

		for (int i = 0; i < typelist.length; i++) {
			PrintWriter pw = new PrintWriter(dir.getAbsoluteFile() + "\\type"
					+ (i + 1) + ".mgf");
			for (int j = 0; j < typelist[i].size(); j++) {
				pw.write(typelist[i].get(j));
			}
			pw.close();
		}
	}

	private static void writeGlycoSpectra(String in, String out)
			throws IOException, DtaFileParsingException {

		double tolerance = 0.1;
		String lineSeparator = IOConstant.lineSeparator;

		PrintWriter writer = new PrintWriter(out);

		MgfReader reader = new MgfReader(in);
		MS2Scan ms2scan = null;

		while ((ms2scan = reader.getNextMS2Scan()) != null) {

			IMS2PeakList peaklist = ms2scan.getPeakList();
			PrecursePeak pp = peaklist.getPrecursePeak();
			double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
			double mz = pp.getMz();
			short charge = pp.getCharge();
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
				rsdlist[tempintenlist.size() - 1] = MathTool
						.getRSDInDouble(tempintenlist);
			}

			for (int i = 1; i <= rsdlist.length; i++) {

				if (rsdlist[rsdlist.length - i] < intenthres1) {
					intenthres2 = temppeaks[i - 1].getIntensity();
					percent = ((double) temppeaks.length - i + 1)
							/ ((double) temppeaks.length);
					break;
				}
			}
			// System.out.println(intenthres1+"\t"+intenthres2);
			IPeak[] peaks = peaklist.getPeakArray();
			Arrays.sort(peaks);

			double maxinten = 0;
			int[] markpeaks = new int[7];

			ArrayList<IPeak> lowIntenList = new ArrayList<IPeak>();
			ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();

			StringBuilder sb = new StringBuilder();
			sb.append("BEGIN IONS" + lineSeparator);
			sb.append("PEPMASS=" + mz + lineSeparator);
			sb.append("CHARGE=" + charge + "+" + lineSeparator);
			sb.append("TITLE=" + name + lineSeparator);

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
				continue;
			}

			if (highIntenList.size() <= 5)
				continue;

			for (int i = 0; i < highIntenList.size(); i++) {
				sb.append(highIntenList.get(i).getMz() + "\t"
						+ highIntenList.get(i).getIntensity() + lineSeparator);
			}
			sb.append("END IONS" + lineSeparator);

			writer.write(sb.toString());
		}

		writer.close();
	}

	private static void batchWriteGlycoSpectra(String in, String out)
			throws DtaFileParsingException, FileNotFoundException {

		File[] files = (new File(in)).listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if (arg0.getName().endsWith("mgf"))
					return true;

				return false;
			}

		});

		PrintWriter writer = new PrintWriter(out);
		double tolerance = 0.1;
		String lineSeparator = IOConstant.lineSeparator;

		for (int fileid = 0; fileid < files.length; fileid++) {

			System.out.println((fileid + 1) + "\t" + files[fileid].getName());
			MgfReader reader = new MgfReader(files[fileid]);
			MS2Scan ms2scan = null;

			while ((ms2scan = reader.getNextMS2Scan()) != null) {

				IMS2PeakList peaklist = ms2scan.getPeakList();
				PrecursePeak pp = peaklist.getPrecursePeak();
				double mw = pp.getMH() - AminoAcidProperty.PROTON_W;
				double mz = pp.getMz();
				short charge = pp.getCharge();
				String name = ms2scan.getScanName().getScanName();
				if (name.endsWith(", "))
					name = name.substring(0, name.length() - 2);
				name = name.replace("1.1.1", "1.1." + (fileid + 1));

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
					rsdlist[tempintenlist.size() - 1] = MathTool
							.getRSDInDouble(tempintenlist);
				}

				for (int i = 1; i <= rsdlist.length; i++) {

					if (rsdlist[rsdlist.length - i] < intenthres1) {
						intenthres2 = temppeaks[i - 1].getIntensity();
						percent = ((double) temppeaks.length - i + 1)
								/ ((double) temppeaks.length);
						break;
					}
				}
				// System.out.println(intenthres1+"\t"+intenthres2);
				IPeak[] peaks = peaklist.getPeakArray();
				Arrays.sort(peaks);

				double maxinten = 0;
				int[] markpeaks = new int[7];

				ArrayList<IPeak> lowIntenList = new ArrayList<IPeak>();
				ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();

				StringBuilder sb = new StringBuilder();
				sb.append("BEGIN IONS" + lineSeparator);
				sb.append("PEPMASS=" + mz + lineSeparator);
				sb.append("CHARGE=" + charge + "+" + lineSeparator);
				sb.append("TITLE=" + name + lineSeparator);

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
					continue;
				}

				if (highIntenList.size() <= 5)
					continue;

				for (int i = 0; i < highIntenList.size(); i++) {
					sb.append(highIntenList.get(i).getMz() + "\t"
							+ highIntenList.get(i).getIntensity()
							+ lineSeparator);
				}
				sb.append("END IONS" + lineSeparator);

				writer.write(sb.toString());
			}

		}
		writer.close();
	}

	private void write(String file) throws DtaFileParsingException, IOException {

		OGlycanSpecSpliter spliter = new OGlycanSpecSpliter(file);
		// OGlycanSpec2DSpliter spliter = new OGlycanSpec2DSpliter(file);
		spliter.split();
		// HashMap <String, ArrayList<String>> map = spliter.getSpecMap();
		// ArrayList <String> list = spliter.getSpecList();

		OGlycanMgfsWriter writer = new OGlycanMgfsWriter(
				"H:\\OGlycan_final\\1D_complex"
						+ "\\20120328_humaneserum_trypsin_HILIC_8uL-03");
		// writer.write(map, list);
		writer.write(spliter.getTypeList());

	}

	/**
	 * @param args
	 * @throws DtaFileParsingException
	 * @throws IOException
	 */
	public static void main(String[] args) throws DtaFileParsingException,
			IOException {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
/*
		String file = "H:\\OGlycan_final_20130710\\humaneserum\\20120328_humaneserum_trypsin_HILIC_8uL-02.mgf";
		OGlycanSpecSpliter2 spliter = new OGlycanSpecSpliter2(file);
		spliter.split();
		OGlycanMgfsWriter writer = new OGlycanMgfsWriter(
				"H:\\OGlycan_final_20130710\\humaneserum");
*/
		OGlycanMgfsWriter.batchWriteGlycoSpectra("H:\\OGlycan_0530_2D\\2D_trypsin",
		"H:\\OGlycan_final_20130830\\serum_2D\\20131104\\trypsin.denoise.mgf");

		long end = System.currentTimeMillis();

		System.out.println("Time:" + (end - begin) / 60000.0 + "min");
	}

}
