/* 
 ******************************************************************************
 * File: GlycoSpecStrucGetter.java * * * Created on 2012-2-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.structure;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoDatabaseMatcher;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

public class NGlycoSpecStrucGetter {

	private IRawSpectraReader reader;
	private GlycoJudgeParameter jpara;
	private GlycoDatabaseMatcher matcher;

	private HashMap<Integer, Integer> isotopeMap;

	private double intenThres;
	private double mzThresPPM;
	private double mzThresAMU;
	private int topn;
	private int ms2count;
	/**
	 * number of glyco spectra
	 */
	private int glycoSpectraCount;
	private NGlycoSSM[] ssms;

	/**
	 * nbt.1511-S1, p9
	 */
	protected static final double dm = 1.00286864;

	private static final double nGlycanCoreFuc = 1038.375127;
	protected static final double Hex = Glycosyl.Hex.getMonoMass();
	protected static final double HexNAc = Glycosyl.HexNAc.getMonoMass();

	private DecimalFormat df4 = DecimalFormats.DF0_4;

	public NGlycoSpecStrucGetter(String peakfile) throws XMLStreamException, IOException {

		if (peakfile.endsWith("mzXML")) {

			this.reader = new MzXMLReader(peakfile);

		} else if (peakfile.endsWith("mzData")) {

			this.reader = new MzDataStaxReader(peakfile);

		} else {
			throw new IOException("Unknown file type: " + peakfile);
		}

		this.jpara = GlycoJudgeParameter.defaultParameter();
		this.matcher = new GlycoDatabaseMatcher(jpara.getMzThresPPM());
		this.isotopeMap = new HashMap<Integer, Integer>();

		this.parseNGlyco();
	}

	public NGlycoSpecStrucGetter(String peakfile, GlycoJudgeParameter jpara) throws XMLStreamException, IOException {

		if (peakfile.endsWith("mzXML")) {

			this.reader = new MzXMLReader(peakfile);

		} else if (peakfile.endsWith("mzData")) {

			this.reader = new MzDataStaxReader(peakfile);

		} else {
			throw new IOException("Unknown file type: " + peakfile);
		}

		this.jpara = jpara;
		this.matcher = new GlycoDatabaseMatcher(jpara.getMzThresPPM());
		this.isotopeMap = new HashMap<Integer, Integer>();
		this.parseNGlyco();
	}

	private void parseNGlyco() throws IOException {

		HashMap<Integer, IMS2Scan> ms2ScanMap = new HashMap<Integer, IMS2Scan>();
		HashMap<Integer, Double> neuAcMap = new HashMap<Integer, Double>();

		double[] mzs = new double[5]; // oxonium ion

		// 162.052824; 204.086649; 274.08741263499996; 292.102692635;
		// 366.139472; 657.2348890000001
		// huang junfeng modification -47.025881
		mzs[0] = Glycosyl.Hex.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[1] = Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[2] = Glycosyl.NeuAc_H2O.getMonoMass() + AminoAcidProperty.PROTON_W - 47.025881;
		mzs[3] = Glycosyl.NeuAc.getMonoMass() + AminoAcidProperty.PROTON_W - 47.025881;
		mzs[4] = Glycosyl.Hex.getMonoMass() + Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;

		intenThres = jpara.getIntenThres();
		mzThresPPM = jpara.getMzThresPPM();
		mzThresAMU = jpara.getMzThresAMU();
		topn = jpara.getTopnStructure();

		ArrayList<NGlycoSSM> list = new ArrayList<NGlycoSSM>();
		ISpectrum spectrum;
		
		IPeak[] ms1Peaks = null; // for precursor calibration

		while ((spectrum = reader.getNextSpectrum()) != null) {

			int msLevel = spectrum.getMSLevel();
			double totIonCurrent = spectrum.getTotIonCurrent();

			if (msLevel > 1) {
				ms2count++;
				MS2Scan ms2 = (MS2Scan) spectrum;

				float preMz = (float) ms2.getPrecursorMZ();
				short preCharge = ms2.getCharge();
				int snum = ms2.getScanNum();

				if (preMz * preCharge <= nGlycanCoreFuc)
					continue;

				// number of match peaks
				int count = 0;
				IMS2PeakList peaklist = ms2.getPeakList();

				IPeak[] peaks = peaklist.getPeakArray();
				double[] markerIntensity = new double[5];

				// filter with oxonium ion
				L: for (int i = 0; i < peaks.length; i++) {

					double mz = peaks[i].getMz();
					double inten = peaks[i].getIntensity();
					if (inten / totIonCurrent < intenThres)
						continue;

					// test the largest mass
					if ((mz - mzs[4]) > mzThresAMU) {
						break;
					}

					// test 5 glycan peaks
					for (int j = 0; j < mzs.length; j++) {

						// mz is too small
						if ((mzs[j] - mz) > mzThresAMU)
							continue L;

						// within tolerance
						if (Math.abs(mz - mzs[j]) <= mzThresAMU) {
							// choose the larger intensity
							if (markerIntensity[j] == 0) {
								markerIntensity[j] = inten;
								count++;
							} else {
								if (inten > markerIntensity[j]) {
									markerIntensity[j] = inten;
								}
							}
						}
					}
				}
				// test pass
				if (count >= 2) {
					this.glycoSpectraCount++;

					this.findIsotope(ms2, ms1Peaks);

					ms2ScanMap.put(snum, ms2);
					double neuAcScore = 0;
					if (markerIntensity[1] > 0) {
						neuAcScore += markerIntensity[2] > 0 ? (markerIntensity[2] / markerIntensity[1] + 1)
								: 0;
						neuAcScore += markerIntensity[3] > 0 ? (markerIntensity[3] / markerIntensity[1] + 1)
								: 0;
					} else {
						neuAcScore += markerIntensity[2] > 0 ? 1 : 0;
						neuAcScore += markerIntensity[3] > 0 ? 1 : 0;
					}
					neuAcMap.put(snum, neuAcScore);
				}

			} else if (msLevel == 1) {
				ms1Peaks = spectrum.getPeakList().getPeakArray();
			}
		}

		Integer[] scans = ms2ScanMap.keySet().toArray(new Integer[ms2ScanMap.size()]);
		Arrays.sort(scans);
		for (int scanid = 0; scanid < scans.length; scanid++) {

			Integer scannum = scans[scanid];
			IMS2Scan ms2 = ms2ScanMap.get(scannum);

			float preMz = (float) ms2.getPrecursorMZ();
			short preCharge = ms2.getCharge();

			IMS2PeakList peaklist = ms2.getPeakList();
			PrecursePeak ppeak = peaklist.getPrecursePeak();
			int preScanNum0 = ppeak.getScanNum();
			double rt = ppeak.getRT();

			NGlycoConstructor gc = new NGlycoConstructor(preMz, preCharge, mzThresPPM);
			gc.setScanNum(scannum);

			IPeak[] peaks = peaklist.getPeakArray();

			double[] peakinten = new double[peaks.length];
			for (int i = 0; i < peaks.length; i++) {
				peakinten[i] = peaks[i].getIntensity();
			}
			Arrays.sort(peakinten);

			// calculate the intensity threshold
			double ave = 0;
			ArrayList<Double> ddlist = new ArrayList<Double>();
			for (int i = 0; i < peakinten.length; i++) {

				if (ddlist.size() == 10) {
					double rsd = MathTool.getRSDInDouble(ddlist);
					ddlist.remove(0);

					if (rsd > 0.05) {
						ave = MathTool.getAveInDouble(ddlist);
						break;
					}
				}
				ddlist.add(peakinten[i]);
			}

			for (int i = 0; i < peaks.length; i++) {

				double inten = peaks[i].getIntensity();

				if (inten < ave)
					continue;

				gc.addNCorePeak(peaks[i]);
			}

			gc.initial();

			NGlycoSSM[] ssm = matcher.match(gc, isotopeMap.get(scannum), neuAcMap.get(scannum));

			if (ssm != null) {

				for (int i = 0; i < ssm.length; i++) {

					if (ssm[i].getRank() > topn) {
						break;
					}

					ssm[i].setMS1Scannum(preScanNum0);
					ssm[i].setRT(rt);

					list.add(ssm[i]);
				}
			}
		}

		this.ssms = list.toArray(new NGlycoSSM[list.size()]);
	}

	private void findIsotope(IMS2Scan scan, IPeak[] ms1Peaks) {

		IPeak precursorPeak = scan.getPeakList().getPrecursePeak();
		int isoloc = Arrays.binarySearch(ms1Peaks, precursorPeak);

		if (isoloc < 0)
			isoloc = -isoloc - 1;
		if (isoloc >= ms1Peaks.length) {
			this.isotopeMap.put(scan.getScanNumInteger(), 0);
			return;
		}
		int charge = scan.getCharge();
		double mz = precursorPeak.getMz();
		double intensity = precursorPeak.getIntensity();

		int k = 1;
		int i = isoloc;

		for (; i >= 0; i--) {

			double delta = mz - ms1Peaks[i].getMz() - k * dm / (double) charge;
			double loginten = Math.log10(intensity / ms1Peaks[i].getIntensity());
			if (Math.abs(delta) <= mz * mzThresPPM * 1E-6) {
				if (Math.abs(loginten) < 1) {
					k++;
					if (intensity < ms1Peaks[i].getIntensity())
						intensity = ms1Peaks[i].getIntensity();

				}
			} else if (delta > mz * mzThresPPM * 1E-6) {
				break;
			}

			if (k > 7)
				break;
		}
		this.isotopeMap.put(scan.getScanNumInteger(), k - 1);
	}

	/**
	 * not validated
	 * 
	 * @deprecated
	 * @param peaks
	 * @return
	 */
	private IPeak[] filterPeakList(IPeak[] peaks) {

		double intenthres1 = 0;
		double intenthres2 = 0;

		if (peaks.length >= 300) {
			intenthres1 = 0.4;
		} else if (peaks.length < 300 && peaks.length >= 200) {
			intenthres1 = 0.35;
		} else if (peaks.length < 200 && peaks.length >= 140) {
			intenthres1 = 0.28;
		} else if (peaks.length < 140 && peaks.length >= 70) {
			intenthres1 = 0.2;
		} else {
			intenthres1 = 0.1;
		}

		ArrayList<Double> tempintenlist = new ArrayList<Double>();
		double[] rsdlist = new double[peaks.length];
		double percent = 0;
		for (int i = peaks.length - 1; i >= 0; i--) {
			tempintenlist.add(peaks[i].getIntensity());
			rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
		}

		for (int i = 1; i <= rsdlist.length; i++) {

			if (rsdlist[rsdlist.length - i] < intenthres1) {
				intenthres2 = peaks[i - 1].getIntensity();
				percent = ((double) peaks.length - i + 1) / ((double) peaks.length);
				break;
			}
		}

		ArrayList<IPeak> filteredList = new ArrayList<IPeak>();
		for (int i = 0; i < peaks.length; i++) {
			if (peaks[i].getIntensity() > intenthres2) {
				filteredList.add(peaks[i]);
			}
		}

		return filteredList.toArray(new IPeak[filteredList.size()]);
	}

	public NGlycoSSM[] getGlycoSSMs() {
		return ssms;
	}

	public static void main(String[] args) throws XMLStreamException, IOException {
		long beg = System.currentTimeMillis();

		GlycoJudgeParameter jpara = new GlycoJudgeParameter(0.001f, 30f, 0.15f, 500, 0.3f, 60.0f, 3);

		// String file =
		// "H:\\NGlyco_xubo_20131114\\glyco\\centroid\\Bo_20131031_HEKmedium_TFRC_HILIC_1_replicate2.mzXML";
		String file = "H:\\NGLYCO\\NGlyco_original_data_2D\\2D\\glyco\\Rui_20130604_HEK_HILIC_F1.mzXML";
		// String file =
		// "D:\\sun_glyco\\20130529\\130528_TRAF_FA_glyco_HCD_30%_10ms_10MSMS.mzXML";
		// String file =
		// "D:\\hulianghai\\20130526_HLH_glyco-antibody_HCD_10ms_35%.mzXML";
		NGlycoSpecStrucGetter gssg = new NGlycoSpecStrucGetter(file, GlycoJudgeParameter.defaultParameter());
		System.out.println(gssg.glycoSpectraCount + "\t" + gssg.ms2count);
		long end = System.currentTimeMillis();

		System.out.println((end - beg) / 1E3 + "s");
	}

}
