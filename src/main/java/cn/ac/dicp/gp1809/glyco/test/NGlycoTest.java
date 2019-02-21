/* 
 ******************************************************************************
 * File: NGlycoTest.java * * * Created on 2013-5-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import com.csvreader.CsvReader;

import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.glyco.NGlycoCompose;
import cn.ac.dicp.gp1809.glyco.NGlycoPossiForm;
import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLReader;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoConstructor;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 * @version 2013-5-22, 9:49:35
 */
public class NGlycoTest {

	private final static double dm = 1.00286864;

	private static void testY1Intensity(String in) throws IOException, XMLStreamException {

		MzXMLReader reader = new MzXMLReader(in);

		HashMap<Integer, IMS2Scan> ms2ScanMap = new HashMap<Integer, IMS2Scan>();

		double ms1TotalCurrent = reader.getMS1TotalCurrent();

		double[] mzs = new double[5];

		// 162.052824; 204.086649; 274.08741263499996; 292.102692635;
		// 366.139472; 657.2348890000001
		mzs[0] = Glycosyl.Hex.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[1] = Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[2] = Glycosyl.NeuAc_H2O.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[3] = Glycosyl.NeuAc.getMonoMass() + AminoAcidProperty.PROTON_W;
		mzs[4] = Glycosyl.Hex.getMonoMass() + Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;

		double intenThres = 0.001;
		double mzThresPPM = 20;
		double mzThresAMU = 0.02;

		ISpectrum spectrum;

		while ((spectrum = reader.getNextSpectrum()) != null) {

			int msLevel = spectrum.getMSLevel();
			double totIonCurrent = spectrum.getTotIonCurrent();

			if (msLevel > 1) {
				MS2Scan ms2 = (MS2Scan) spectrum;

				float preMz = (float) ms2.getPrecursorMZ();
				short preCharge = ms2.getCharge();
				int snum = ms2.getScanNum();

				if (preMz * preCharge <= 1038.375127)
					continue;

				int count = 0;
				IMS2PeakList peaklist = ms2.getPeakList();
				IPeak[] peaks = peaklist.getPeakArray();
				int loc = 0;

				L: for (int i = 0; i < peaks.length; i++) {

					double mz = peaks[i].getMz();
					double inten = peaks[i].getIntensity();
					if (inten / totIonCurrent < intenThres)
						continue;

					if ((mz - mzs[4]) > mzThresAMU) {
						break;
					}

					for (int j = loc; j < mzs.length; j++) {

						if ((mzs[j] - mz) > mzThresAMU)
							continue L;

						if (Math.abs(mz - mzs[j]) <= mzThresAMU) {
							count++;

						} else if ((mz - mzs[j]) > mzThresAMU) {
							loc = j + 1;
						}
					}
				}

				if (count >= 2) {

					ms2ScanMap.put(snum, ms2);

				}

			} else if (msLevel == 1) {
				// this.scanlist.add(spectrum);
			}
		}

		int tt = 0;
		int dd = 0;
		int ttt = 0;
		int ddd = 0;
		// this.pixGetter = new MS1PixelGetter(scanlist, ms1TotalCurrent);
		Iterator<Integer> it = ms2ScanMap.keySet().iterator();
		while (it.hasNext()) {

			Integer scannum = it.next();
			IMS2Scan ms2 = ms2ScanMap.get(scannum);

			float preMz = (float) ms2.getPrecursorMZ();
			float preInten = (float) ms2.getPrecursorInten();
			short preCharge = ms2.getCharge();

			IMS2PeakList peaklist = ms2.getPeakList();
			PrecursePeak ppeak = peaklist.getPrecursePeak();
			int preScanNum0 = ppeak.getScanNum();
			double rt = ppeak.getRT();
			int scanKey = (int) rt;

			NGlycoConstructor gc = new NGlycoConstructor(preMz, preCharge, mzThresPPM);
			gc.setScanNum(scannum);

			IPeak[] peaks = peaklist.getPeakArray();

			double[] peakinten = new double[peaks.length];
			for (int i = 0; i < peaks.length; i++) {
				peakinten[i] = peaks[i].getIntensity();
			}
			Arrays.sort(peakinten);

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
			if (gc.getPossiblePepMasses()[0] > 0) {
				tt++;

				IPeak peak800 = new Peak(800.0, 0.0);
				int id800 = Arrays.binarySearch(peaks, peak800);
				if (id800 < 0) {
					id800 = -id800 - 1;
				}

				IPeak[] peaks800 = new IPeak[peaks.length - id800];
				System.arraycopy(peaks, id800, peaks800, 0, peaks800.length);
				Arrays.sort(peaks800, new Comparator<IPeak>() {

					@Override
					public int compare(IPeak arg0, IPeak arg1) {
						if (arg0.getIntensity() > arg1.getIntensity()) {
							return -1;
						} else if (arg0.getIntensity() < arg1.getIntensity()) {
							return 1;
						}
						return 0;
					}

				});

				L: for (int i = 0; i < peaks800.length; i++) {
					for (int j = 1; j <= preCharge; j++) {
						double y1mz = (gc.getPossiblePepMasses()[0] + Glycosyl.HexNAc.getMonoMass()) / (double) j
								+ AminoAcidProperty.PROTON_W;

						double y1mzIso2 = (gc.getPossiblePepMasses()[0] + Glycosyl.HexNAc.getMonoMass() + dm)
								/ (double) j + AminoAcidProperty.PROTON_W;

						if (Math.abs(peaks800[i].getMz() - y1mz) < 0.1
								|| Math.abs(peaks800[i].getMz() - y1mzIso2) < 0.1) {
							System.out.println(peaks800[i].getMz() + "\t" + (i + 1));
							// if (i == 1 &&
							// Math.abs(peaks800[0].getMz()-peaks800[1].getMz())<2)
							// {
							// System.out.println(peaks800[0].getMz() + "\t"
							// + peaks800[1].getMz() + "\t" + y1mz);
							// }
							break L;
						}
					}

				}

			}

		}

	}

	private static void testProtein(String in) throws IOException {

		Pattern N_GLYCO = Pattern.compile("N[A-OQ-Z][ST]");
		HashSet<String> set = new HashSet<String>();
		FastaReader fr = new FastaReader(in);
		ProteinSequence ps = null;
		while ((ps = fr.nextSequence()) != null) {
			Enzyme en = Enzyme.TRYPSIN;
			String[] peps = en.cleave(ps.getUniqueSequence(), 3, 1);
			for (int i = 0; i < peps.length; i++) {
				Matcher matcher = N_GLYCO.matcher(peps[i]);
				if (matcher.find()) {
					set.add(peps[i]);
				}
			}
		}
		fr.close();

		double premass = 3277.3859;
		double premass2 = 3730.541;
		MwCalculator mwcal = new MwCalculator();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		mwcal.setAacids(aas);
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String seq = it.next();
			double mw = mwcal.getMonoIsotopeMh(seq) - AminoAcidProperty.PROTON_W;
			System.out.println(seq + "\t" + mw);
			NGlycoPossiForm[] forms = NGlycoCompose.calWithCorePPM(premass - mw);
			if (forms != null) {
				// for(int i=0;i<forms.length;i++)
				// System.out.println("1\t"+seq+"\t"+mw+"\t"+(premass-mw)+"\t"+forms[i].getCompDes()+"\t"+i);
			}
			// System.out.println(seq+"\t"+mw);
			NGlycoPossiForm[] forms1 = NGlycoCompose.calWithCorePPM(premass2 - mw);
			if (forms1 != null) {
				// for(int i=0;i<forms1.length;i++)
				// System.out.println("2\t"+seq+"\t"+mw+"\t"+(premass2-mw)+"\t"+forms1[i].getCompDes()+"\t"+i);
			}
		}
	}

	private static void compareIden(String proteinMatch, String iden) throws Exception {

		GlycoIdenXMLReader preoMatchReader = new GlycoIdenXMLReader(proteinMatch);
		NGlycoSSM[] prossms = preoMatchReader.getAllMatches();
		GlycoIdenXMLReader idenReader = new GlycoIdenXMLReader(iden);
		NGlycoSSM[] idenssms = idenReader.getAllMatches();

		HashMap<Integer, NGlycoSSM> map = new HashMap<Integer, NGlycoSSM>();
		for (int i = 0; i < prossms.length; i++) {
			if (prossms[i].getRank() == 1)
				map.put(prossms[i].getScanNum(), prossms[i]);
		}

		int r1 = 0;
		int r2 = 0;
		for (int i = 0; i < idenssms.length; i++) {
			int scannum = idenssms[i].getScanNum();
			double glycomass = idenssms[i].getGlycoMass();
			double pepmass = idenssms[i].getPepMass();
			if (map.containsKey(scannum)) {
				r1++;
				// if(map.get(scannum).getGlycoMass()==glycomass){
				if (Math.abs(pepmass - map.get(scannum).getPepMass()) < 2) {
					r2++;
				} else {
					System.out.println(scannum + "\t" + glycomass + "\t" + map.get(scannum).getGlycoMass() + "\t"
							+ pepmass + "\t" + map.get(scannum).getPepMass() + "\t"
							+ map.get(scannum).getPepMassExperiment() + "\t" + map.get(scannum).getPreMz());
				}
				map.remove(scannum);
			}
		}
		System.out.println(prossms.length + "\t" + idenssms.length + "\t" + r1 + "\t" + r2);
		for (Integer integer : map.keySet()) {
			System.out.println(integer);
		}
	}

	private static void getsequence(String in) throws FileDamageException, IOException {
		HashSet<String> set = new HashSet<String>();
		NGlycoPepCriteria nc = new NGlycoPepCriteria(true);
		PeptideListReader reader = new PeptideListReader(in);

		MwCalculator mwcal = new MwCalculator(reader.getSearchParameter().getStaticInfo(),
				reader.getSearchParameter().getVariableInfo());

		IPeptide peptide = null;
		while ((peptide = reader.getPeptide()) != null) {
			if (nc.filter(peptide)) {
				String sequence = peptide.getSequence().substring(2, peptide.getSequence().length() - 2);
				sequence = sequence.replaceAll("\\*", "");

				if (set.contains(sequence)) {
					continue;
				}
				set.add(sequence);
				double mw = mwcal.getMonoIsotopeMh(sequence) - AminoAcidProperty.PROTON_W;
				System.out.println(sequence + "\t" + mw);
			}
		}
	}

	private static void ddd(String in) throws IOException {
		CsvReader reader = new CsvReader(in);
		while (reader.readRecord()) {
			String s0 = reader.get(0);
			if (s0.equals("Protein hits")) {
				reader.readHeaders();
				break;
			}
		}
		int[] count = new int[10];
		String[] name = new String[10];
		while (reader.readRecord()) {
			Integer s1 = Integer.parseInt(reader.get("prot_hit_num"));
			String proname = reader.get("prot_desc").split("Gene_Symbol=[\\w]*")[1];
			String mod = reader.get("pep_var_mod_pos");
			if (s1 == 11)
				break;
			if (mod != null && mod.contains("1")) {
				count[s1 - 1]++;
				name[s1 - 1] = proname;
			}
		}
		for (int i = 0; i < name.length; i++) {
			System.out.println(name[i] + "\t" + count[i]);
		}
	}

	private static void toABmgf(String in, String out) throws DtaFileParsingException, IOException {
		MgfReader reader = new MgfReader(in);
		PrintWriter writer = new PrintWriter(out);
		MS2Scan scan = null;
		while ((scan = reader.getNextMS2Scan()) != null) {
			short charge = scan.getCharge();
			IPeak[] peaks = scan.getPeakList().getPeakArray();
			double mz = scan.getPrecursorMZ();
			int scannum = scan.getScanNum();
			int rt = (int) (scannum / 10.0 + 1);

			writer.write("BEGIN IONS\n");
			writer.write("TITLE=Locus:1.1.1." + scannum + ".1\n");
			writer.write("CHARGE=" + charge + "+\n");
			writer.write("PEPMASS=" + mz + "\n");
			writer.write("RTINSECONDS=" + rt + "\n");
			for (int i = 0; i < peaks.length; i++) {
				writer.write(peaks[i].getMz() + "\t" + peaks[i].getIntensity() + "\n");
			}
			writer.write("END IONS\n");
		}
		reader.close();
		writer.close();
	}

	private static void abResultTest(String in) throws IOException {
		HashSet<String> modset = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] cs = line.split("\t");
			if (cs[12].contains("LCPDCPLLAPLNDSR")) {
				if (!modset.contains(cs[13])) {
					modset.add(cs[13]);
					System.out.println(cs[12] + "\t" + cs[13] + "\t" + cs[18] + "\t" + cs[11]);
				}
			}
		}
		reader.close();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		NGlycoTest.testY1Intensity("H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.mzXML");

		// NGlycoTest.testProtein("H:\\20130519_glyco\\fetuin.fasta");

		// NGlycoTest.compareIden("H:\\20130519_glyco\\HCD20130523\\" +
		// "Rui_20130515_fetuin_HILIC_HCD_30%_5ms.proteinmatch.pxml",
		// "H:\\20130519_glyco\\HCD20130523\\" +
		// "Rui_20130515_fetuin_HILIC_HCD_30%_5ms.iden.pxml");

		// NGlycoTest.getsequence("H:\\20130519_glyco\\iden\\F004707.csv.ppl");

		// NGlycoTest.ddd("H:\\20130519_glyco\\iden\\F004707.csv");

		// NGlycoTest.toABmgf("H:\\20130519_glyco\\Centroid_Rui_20130515_fetuin_HILIC_deglyco_HCD.mgf",
		// "H:\\20130519_glyco\\Centroid_Rui_20130515_fetuin_HILIC_deglyco_HCD_AB.mgf");

		// NGlycoTest.abResultTest("H:\\20130519_glyco\\iden\\AB\\Centroid_Rui_20130515_fetuin_HILIC_deglyco_HCD_AB_PeptideSummary.txt");
	}

}
