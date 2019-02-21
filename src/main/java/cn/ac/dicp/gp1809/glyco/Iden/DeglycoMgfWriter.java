/* 
 ******************************************************************************
 * File: DeglycoMgfWriter.java * * * Created on 2014��6��6��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzDataStaxReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * @author ck
 */
public class DeglycoMgfWriter {

	private IRawSpectraReader reader;
	private GlycoJudgeParameter jpara;

	private double intenThres;
	private double mzThresPPM;
	private double mzThresAMU;

	private static final double nGlycanCoreFuc = 1038.375127;
	protected static final double Hex = Glycosyl.Hex.getMonoMass();
	protected static final double HexNAc = Glycosyl.HexNAc.getMonoMass();
	private static final String lineSeparator = IOConstant.lineSeparator;

	public DeglycoMgfWriter(String in) throws IOException, XMLStreamException {

		if (in.endsWith("mzXML")) {

			this.reader = new MzXMLReader(in);

		} else if (in.endsWith("mzData")) {

			this.reader = new MzDataStaxReader(in);

		} else {
			throw new IOException("Unknown file type: " + in);
		}

		this.jpara = GlycoJudgeParameter.defaultParameter();
	}

	public void write(String out) throws IOException {

		PrintWriter writer = new PrintWriter(out);

		double[] mzs = new double[5];

		// 162.052824; 204.086649; 274.08741263499996; 292.102692635;
		// 366.139472; 657.2348890000001
		mzs[0] = Glycosyl.Hex.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[1] = Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[2] = Glycosyl.NeuAc_H2O.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[3] = Glycosyl.NeuAc.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[4] = Glycosyl.Hex.getMonoMass() + Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;

		intenThres = jpara.getIntenThres();
		mzThresPPM = jpara.getMzThresPPM();
		mzThresAMU = jpara.getMzThresAMU();

		ISpectrum spectrum;
		int id = 0;

		while ((spectrum = reader.getNextSpectrum()) != null) {

			int msLevel = spectrum.getMSLevel();
			double totIonCurrent = spectrum.getTotIonCurrent();
			boolean deglyco = false;

			if (msLevel > 1) {

				MS2Scan ms2 = (MS2Scan) spectrum;
				id++;

				float preMz = (float) ms2.getPrecursorMZ();
				short preCharge = ms2.getCharge();
				int snum = ms2.getScanNum();

				if (preMz * preCharge <= nGlycanCoreFuc)
					deglyco = true;

				int count = 0;
				IMS2PeakList peaklist = ms2.getPeakList();

				IPeak[] peaks = peaklist.getPeakArray();
				double[] symbolPeakIntensity = new double[5];

				L: for (int i = 0; i < peaks.length; i++) {

					double mz = peaks[i].getMz();
					double inten = peaks[i].getIntensity();
					if (inten / totIonCurrent < intenThres)
						continue;

					if ((mz - mzs[4]) > mzThresAMU) {
						break;
					}

					for (int j = 0; j < mzs.length; j++) {

						if ((mzs[j] - mz) > mzThresAMU)
							continue L;

						if (Math.abs(mz - mzs[j]) <= mzThresAMU) {
							if (symbolPeakIntensity[j] == 0) {
								symbolPeakIntensity[j] = inten;
								count++;
							} else {
								if (inten > symbolPeakIntensity[j]) {
									symbolPeakIntensity[j] = inten;
								}
							}
						}
					}
				}

				if (count < 2) {
					deglyco = true;
				}

				if (deglyco) {

					StringBuilder sb = new StringBuilder();
					sb.append("BEGIN IONS" + lineSeparator);
					sb.append("PEPMASS=" + preMz + lineSeparator);
					sb.append("CHARGE=" + preCharge + "+" + lineSeparator);
					sb.append("TITLE=Spectrum ").append(id).append(" scans: ").append(snum).append("," + lineSeparator);

					for (int i = 0; i < peaks.length; i++) {
						sb.append(peaks[i].getMz() + "\t" + peaks[i].getIntensity() + lineSeparator);
					}
					sb.append("END IONS" + lineSeparator);
					writer.write(sb.toString());
				}
			}
		}
		reader.close();
		writer.close();
	}

}
