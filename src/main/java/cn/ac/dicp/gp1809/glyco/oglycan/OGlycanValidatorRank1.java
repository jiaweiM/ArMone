/* 
 ******************************************************************************
 * File: OGlycanValidatorRank1.java * * * Created on 2013-2-1
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
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.itextpdf.text.DocumentException;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.DatReader;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParsingException;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotScanDta;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers.AbstractMascotPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.AccessionFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.Arrangmentor;

/**
 * @author ck
 *
 * @version 2013-2-1, 9:19:27
 */
public class OGlycanValidatorRank1 {

	private AccessionFastaAccesser accesser;
	private IDecoyReferenceJudger judger;
	private HashMap<String, OGlycanScanInfo>[] infomap;
	private HashMap<String, OGlycanPepInfo> glycoMap;
	private HashMap<String, String> fragNameMap;
	private int[] ionType;
	private OGlycanPepInfo[] glycoinfolist;
	private double[] fdrs;
	private AminoacidFragment aaf;
	private MwCalculator mwc;

	private static final double ionscoreThres = 5;
	private static final double tolerance = 0.1;
	private static final DecimalFormat df2 = DecimalFormats.DF0_2;
	private static final DecimalFormat df4 = DecimalFormats.DF0_4;
	private static final double H2O = 18.010565;

	public OGlycanValidatorRank1(String fasta, String regex, IDecoyReferenceJudger judger, String pepinfo)
			throws IOException, FastaDataBaseException {

		this.accesser = new AccessionFastaAccesser(fasta, Pattern.compile(regex), judger);
		this.judger = judger;
		this.glycoMap = new HashMap<String, OGlycanPepInfo>();
		this.fragNameMap = new HashMap<String, String>();
		String[] glycoFragNames = OGlycanUnit.getTotalFragmentNames();
		for (int i = 0; i < glycoFragNames.length; i++) {
			fragNameMap.put("f" + (i + 1), glycoFragNames[i]);
			fragNameMap.put("f" + (i + 1) + "-H2O", glycoFragNames[i] + "(-H2O)");
		}

		this.initial(pepinfo);
	}

	private void initial(String pepinfo) throws IOException {

		this.infomap = new HashMap[2];
		this.infomap[0] = new HashMap<String, OGlycanScanInfo>();
		this.infomap[1] = new HashMap<String, OGlycanScanInfo>();

		BufferedReader reader = new BufferedReader(new FileReader(pepinfo));
		String line = null;
		while ((line = reader.readLine()) != null) {
			OGlycanScanInfo info = new OGlycanScanInfo(line);
			if (infomap[0].containsKey(info.getScanname())) {
				this.infomap[1].put(info.getScanname(), info);
			} else {
				this.infomap[0].put(info.getScanname(), info);
			}
		}
		reader.close();

		System.out.println(this.infomap[0].size() + "\t" + this.infomap[1].size());

		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('*', 15.994915, "Oxidation");
		this.ionType = new int[] { Ion.TYPE_B, Ion.TYPE_Y };
		this.aaf = new AminoacidFragment(aas, aam);
		this.mwc = new MwCalculator(aas, aam);
	}

	public void validate(String file) throws MascotDatParsingException, ModsReadingException,
			InvalidEnzymeCleavageSiteException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException,
			FastaDataBaseException, IOException, PeptideParsingException, DtaFileParsingException {

		HashMap<String, IMascotPeptide> pepmap = new HashMap<String, IMascotPeptide>();
		HashMap<String, IMS2PeakList> peakmap = new HashMap<String, IMS2PeakList>();

		AbstractMascotPeptideReader reader = new DatReader(file, accesser, judger);
		reader.setTopN(10);
		IMascotPeptide pep = null;
		String thisScanName = "";

		while ((pep = reader.getPeptide()) != null) {

			if (pep.getIonscore() < ionscoreThres)
				continue;

			MascotScanDta scan = ((DatReader) reader).getCurtDta(true);
			String scanname = scan.getScanName().getScanName();

			if (scanname.endsWith(", "))
				scanname = scanname.substring(0, scanname.length() - 2);
			IMS2PeakList peaklist = scan.getPeakList();

			if (pepmap.containsKey(scanname)) {
				if (pep.getIonscore() > pepmap.get(scanname).getIonscore()) {
					pepmap.put(scanname, pep);
					peakmap.put(scanname, peaklist);
				}
			} else {
				pepmap.put(scanname, pep);
				peakmap.put(scanname, peaklist);
			}
		}

		Iterator<String> it = pepmap.keySet().iterator();
		while (it.hasNext()) {

			String scanname = it.next();
			IMascotPeptide peptide = pepmap.get(scanname);
			IMS2PeakList peaklist = peakmap.get(scanname);

			String sequence = peptide.getSequence();

			StringBuilder sb = new StringBuilder();
			StringBuilder unisb = new StringBuilder();
			int stcount = 0;

			for (int i = 0; i < sequence.length(); i++) {
				if (sequence.charAt(i) >= 'A' && sequence.charAt(i) <= 'Z') {
					sb.append(sequence.charAt(i));
					if (i >= 2 && i < sequence.length() - 2) {
						unisb.append(sequence.charAt(i));
						if (sequence.charAt(i) == 'S' || sequence.charAt(i) == 'T')
							stcount++;
					}
				} else if (sequence.charAt(i) == '*') {
					sb.append(sequence.charAt(i));
				} else if (sequence.charAt(i) == '.') {
					sb.append(sequence.charAt(i));
				} else if (sequence.charAt(i) == '-') {
					sb.append(sequence.charAt(i));
				}
			}
			OGlycanScanInfo info = this.infomap[0].get(scanname);
			if (Math.abs(info.getPepMw() - mwc.getMonoIsotopeMZ(sb.substring(2, sb.length() - 2))) > 5) {
				if (this.infomap[1].containsKey(scanname)) {
					info = this.infomap[1].get(scanname);
					if (Math.abs(info.getPepMw() - mwc.getMonoIsotopeMZ(sb.substring(2, sb.length() - 2))) > 5) {
						continue;
					}
				} else {
					continue;
				}
			}

			String uniPepSeq = unisb.toString();
			if (scanname.equals(thisScanName)) {
				continue;
			} else {
				thisScanName = scanname;
			}

			OGlycanUnit[] units = info.getUnits();
			Ions ions = aaf.fragment(sb.toString(), ionType, true);
			Ion[] bs = ions.getIons(Ion.TYPE_B);
			Ion[] ys = ions.getIons(Ion.TYPE_Y);

			IPeak[] peaks = peaklist.getPeaksSortByIntensity();

			// if(scanname.startsWith("Locus:1.1.1.5166.5")){
			OGlycanPepInfo pepinfo = this.validate(units, stcount, bs, ys, uniPepSeq, peaks);
			if (pepinfo == null)
				continue;

			pepinfo.setPeptide(peptide);
			pepinfo.setRefs(this.getRefs(peptide));
			pepinfo.setScanname(scanname);

			if (this.glycoMap.containsKey(scanname)) {
				if (pepinfo.getGlycoScore() > glycoMap.get(scanname).getGlycoScore()) {
					this.glycoMap.put(scanname, pepinfo);
				}
			} else {
				this.glycoMap.put(scanname, pepinfo);
			}

			if (units.length == stcount)
				continue;

			if (info.getCansplit() == 1) {

				ArrayList<OGlycanUnit> singleList = new ArrayList<OGlycanUnit>();
				OGlycanUnit[][] splitUnits = null;

				for (int i = 0; i < units.length; i++) {
					if (splitUnits == null) {
						if (units[i].getSplitUnits() != null) {
							splitUnits = units[i].getSplitUnits();
						} else {
							singleList.add(units[i]);
						}
					} else {
						singleList.add(units[i]);
					}
				}

				for (int i = 0; i < splitUnits.length; i++) {

					OGlycanUnit[] splitCombUnits = new OGlycanUnit[singleList.size() + 2];
					for (int j = 0; j < singleList.size(); j++) {
						splitCombUnits[j] = singleList.get(j);
					}
					splitCombUnits[singleList.size()] = splitUnits[i][0];
					splitCombUnits[singleList.size() + 1] = splitUnits[i][1];
					OGlycanPepInfo splitPepinfo = this.validate(splitCombUnits, stcount, bs, ys, uniPepSeq, peaks);
					if (splitPepinfo == null)
						continue;

					splitPepinfo.setPeptide(peptide);
					splitPepinfo.setRefs(this.getRefs(peptide));
					splitPepinfo.setScanname(scanname);

					if (this.glycoMap.containsKey(scanname)) {

						if (splitPepinfo.getGlycoScore() > glycoMap.get(scanname).getGlycoScore()) {
							this.glycoMap.put(scanname, splitPepinfo);

						} else if (splitPepinfo.getGlycoScore() == glycoMap.get(scanname).getGlycoScore()) {
							if (info.getMarkpeaks()[4] + info.getMarkpeaks()[5] + info.getMarkpeaks()[6] > 0) {
								this.glycoMap.put(scanname, splitPepinfo);
							}
						}

					} else {
						this.glycoMap.put(scanname, splitPepinfo);
					}
				}
			}
		}

		reader.close();
	}

	private OGlycanPepInfo validate(OGlycanUnit[] units, int stcount, Ion[] bs, Ion[] ys, String uniPepSeq,
			IPeak[] peaks) {

		int glycoCount = units.length;
		int[] bionSTCount = new int[uniPepSeq.length()];
		int[] yionSTCount = new int[uniPepSeq.length()];
		for (int i = 0; i < uniPepSeq.length(); i++) {
			if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
				for (int j = i; j < uniPepSeq.length(); j++)
					bionSTCount[j]++;
			}
		}
		for (int i = 0; i < bionSTCount.length - 1; i++) {
			yionSTCount[i] = bionSTCount[bionSTCount.length - 1] - bionSTCount[bionSTCount.length - i - 2];
		}

		int[] initialList = new int[stcount];
		for (int i = 0; i < stcount; i++) {
			if (i < glycoCount) {
				initialList[i] = i;
			} else {
				initialList[i] = -1;
			}
		}
		int[][] positionList = Arrangmentor.arrangementArrays(initialList);
		double[] scoreList = new double[positionList.length];

		double[] binten = new double[bs.length];
		double[] bglyinten = new double[bs.length];
		double[] yinten = new double[ys.length];
		double[] yglyinten = new double[ys.length];

		int[][] glycoFragmentId = new int[units.length][];
		for (int i = 0; i < glycoFragmentId.length; i++) {
			glycoFragmentId[i] = new int[units[i].getFragid().length];
			System.arraycopy(units[i].getFragid(), 0, glycoFragmentId[i], 0, units[i].getFragid().length);
		}
		int[][] comFragIds = Arrangmentor.arrangeAll(glycoFragmentId);

		String[] fragnames = OGlycanUnit.getTotalFragmentNames();
		double[] fragmasses = OGlycanUnit.getTotalFragmentMasses();
		HashMap<Double, String> matchmap = new HashMap<Double, String>();
		HashSet<String> usedset = new HashSet<String>();
		HashMap<String, String> annotionMap1 = new HashMap<String, String>();
		HashMap<String, String> annotionMap2 = new HashMap<String, String>();
		HashMap<String, Boolean> annotionMap3 = new HashMap<String, Boolean>();
		for (int i = 0; i < bs.length; i++) {
			annotionMap1.put("bRow" + (i + 1), "b" + (i + 1));
			annotionMap2.put("mzRow" + (i + 1), df4.format(bs[i].getMz()));
			annotionMap1.put("yRow" + (i + 1), "y" + (i + 1));
			annotionMap2.put("mzRow" + (i + 1) + "_3", df4.format(ys[i].getMz()));
		}
		// boolean coreb = false;

		L: for (int i = 0; i < peaks.length; i++) {

			double mzi = Double.parseDouble(df4.format(peaks[i].getMz()));
			double inteni = (peaks.length - i) / (double) peaks.length;

			for (int j = 0; j < bs.length; j++) {

				double bfragmz = bs[j].getMz();
				double yfragmz = ys[j].getMz();

				if (Math.abs(mzi - yfragmz) < tolerance) {
					if (usedset.contains("y" + (j + 1)))
						continue L;

					usedset.add("y" + (j + 1));
					yinten[j] = inteni;
					matchmap.put(mzi, "y" + (j + 1));
					annotionMap3.put("yRow" + (j + 1), true);
					annotionMap3.put("mzRow" + (j + 1) + "_3", true);
					continue L;
				}

				if (Math.abs(mzi - bfragmz) < tolerance) {
					if (usedset.contains("b" + (j + 1)))
						continue L;

					usedset.add("b" + (j + 1));
					binten[j] = inteni;
					matchmap.put(mzi, "b" + (j + 1));
					annotionMap3.put("bRow" + (j + 1), true);
					annotionMap3.put("mzRow" + (j + 1), true);
					continue L;
				}

				for (int k = 0; k < comFragIds.length; k++) {
					double glycoMasses = 0;
					StringBuilder glycoSb = new StringBuilder();
					for (int l = 0; l < comFragIds[k].length; l++) {
						glycoMasses += fragmasses[comFragIds[k][l]];
						glycoSb.append(fragnames[comFragIds[k][l]] + "+");
					}
					glycoSb.deleteCharAt(glycoSb.length() - 1);

					if (comFragIds[k].length <= yionSTCount[j]) {
						if (Math.abs(mzi - yfragmz - glycoMasses) < tolerance) {
							if (usedset.contains("y" + (j + 1) + "+(" + glycoSb + ")"))
								continue L;

							annotionMap1.put("y  glycanRow" + (j + 1), "y" + (j + 1) + "+(" + glycoSb + ")");
							annotionMap2.put("mzRow" + (j + 1) + "_4", df4.format(mzi));
							annotionMap3.put("y  glycanRow" + (j + 1), true);
							annotionMap3.put("mzRow" + (j + 1) + "_4", true);

							if (inteni > yglyinten[j])
								yglyinten[j] += inteni;
							usedset.add("y" + (j + 1) + "+(" + glycoSb + ")");
							matchmap.put(mzi, "y" + (j + 1) + "+(" + glycoSb + ")");
							this.match(positionList, scoreList, comFragIds[k], units, stcount - yionSTCount[j], inteni,
									1);
							// continue L;
						}
						for (int l = 1; l <= units.length && l <= comFragIds[k].length; l++) {
							if (Math.abs(mzi - yfragmz - (glycoMasses + H2O * l)) < tolerance) {
								if (usedset.contains("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")"))
									continue L;

								annotionMap1.put("y  glycanRow" + (j + 1),
										"y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
								annotionMap2.put("mzRow" + (j + 1) + "_4", df4.format(mzi));
								annotionMap3.put("y  glycanRow" + (j + 1), true);
								annotionMap3.put("mzRow" + (j + 1) + "_4", true);

								if (inteni > yglyinten[j])
									yglyinten[j] += inteni;
								usedset.add("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
								matchmap.put(mzi, "y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
								this.match(positionList, scoreList, comFragIds[k], units, stcount - yionSTCount[j],
										inteni, 1);
								// continue L;
							}
						}
					}
					if (comFragIds[k].length <= bionSTCount[j]) {
						if (Math.abs(mzi - bfragmz - glycoMasses) < tolerance) {
							if (usedset.contains("b" + (j + 1) + "+(" + glycoSb + ")"))
								continue L;

							annotionMap1.put("b  glycanRow" + (j + 1), "b" + (j + 1) + "+(" + glycoSb + ")");
							annotionMap2.put("mzRow" + (j + 1) + "_2", df4.format(mzi));
							annotionMap3.put("b  glycanRow" + (j + 1), true);
							annotionMap3.put("mzRow" + (j + 1) + "_2", true);

							if (inteni > bglyinten[j])
								bglyinten[j] += inteni;
							usedset.add("b" + (j + 1) + "+(" + glycoSb + ")");
							matchmap.put(mzi, "b" + (j + 1) + "+(" + glycoSb + ")");
							this.match(positionList, scoreList, comFragIds[k], units, bionSTCount[j], inteni, 0);
							// continue L;
						}
						for (int l = 1; l <= units.length && l <= comFragIds[k].length; l++) {
							if (Math.abs(mzi - bfragmz - (glycoMasses + H2O * l)) < tolerance) {
								if (usedset.contains("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")"))
									continue L;

								annotionMap1.put("b  glycanRow" + (j + 1),
										"b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
								annotionMap2.put("mzRow" + (j + 1) + "_2", df4.format(mzi));
								annotionMap3.put("b  glycanRow" + (j + 1), true);
								annotionMap3.put("mzRow" + (j + 1) + "_2", true);

								if (inteni > bglyinten[j])
									bglyinten[j] += inteni;
								usedset.add("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
								matchmap.put(mzi, "b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
								this.match(positionList, scoreList, comFragIds[k], units, bionSTCount[j], inteni, 0);
								// continue L;
							}
						}
					}
				}
			}
		}

		double totalModScore = 0;
		double maxScore = 0;

		int maxId = -1;
		double[][] positionTypeModScore = new double[glycoCount][stcount];
		for (int i = 0; i < scoreList.length; i++) {

			totalModScore += scoreList[i];

			for (int j = 0; j < stcount; j++) {
				if (positionList[i][j] >= 0)
					positionTypeModScore[positionList[i][j]][j] += scoreList[i];
			}
			// System.out.println();
			if (scoreList[i] > maxScore) {
				maxScore = scoreList[i];
				maxId = i;
			}
		}
		// System.out.println();

		Arrays.sort(scoreList);
		double peptideScore = 0;
		if (maxScore != 0) {
			if (scoreList.length == 1) {
				peptideScore = 1.0;
			} else {
				peptideScore = (maxScore - scoreList[scoreList.length - 2]) / maxScore;
			}
		}

		double[] positionModScore = new double[stcount];
		if (totalModScore == 0) {
			for (int i = 0; i < positionModScore.length; i++) {
				positionModScore[i] = glycoCount / (double) stcount;
			}
		} else {
			for (int i = 0; i < positionModScore.length; i++) {
				for (int j = 0; j < glycoCount; j++) {
					positionModScore[i] += positionTypeModScore[j][i];
				}
				positionModScore[i] = positionModScore[i] * (double) glycoCount / totalModScore;
			}
			initialList = positionList[maxId];
		}

		for (int j = 0; j < positionTypeModScore[0].length; j++) {
			double posiTotal = 0;
			for (int i = 0; i < positionTypeModScore.length; i++) {
				posiTotal += positionTypeModScore[i][j];
			}
			for (int i = 0; i < positionTypeModScore.length; i++) {
				positionTypeModScore[i][j] = posiTotal == 0 ? 0 : positionTypeModScore[i][j] / posiTotal;
			}
		}

		int stid = 0;
		int glycoid = 0;
		int[] locList = new int[glycoCount];
		OGlycanUnit[] newUnits = new OGlycanUnit[glycoCount];
		double[][] newPositionTypeModScore = new double[glycoCount][stcount];
		StringBuilder finalsb = new StringBuilder();
		StringBuilder scoresb = new StringBuilder();
		for (int i = 0; i < uniPepSeq.length(); i++) {
			finalsb.append(uniPepSeq.charAt(i));
			scoresb.append(uniPepSeq.charAt(i));
			if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
				if (positionModScore[stid] != 0) {
					scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
					if (initialList[stid] >= 0) {
						// finalsb.append("(").append(units[initialList[stid]].getName())
						// .append(",").append(df2.format(positionModScore[stid])).append(")");
						finalsb.append("[").append(units[initialList[stid]].getComposition()).append("]");
						locList[glycoid] = (i + 1);
						newUnits[glycoid] = units[initialList[stid]];
						newPositionTypeModScore[glycoid] = positionTypeModScore[initialList[stid]];
						glycoid++;
					} else {
						// scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
					}
				}
				stid++;
			}
		}
		// if(uniPepSeq.equals("SSTTKPPFKPHGSR")){
		for (int i = 0; i < positionModScore.length; i++) {
			// System.out.print(positionModScore[i]+"\t");
		}
		// System.out.println("positionModScore");
		for (int i = 0; i < newPositionTypeModScore.length; i++) {
			for (int j = 0; j < newPositionTypeModScore[i].length; j++) {
				// System.out.print(newPositionTypeModScore[i][j]+"\t");
			}
			// System.out.println();
		}
		// System.out.println("newPositionTypeModScore\n");
		// }

		OGlycanPepInfo pepInfo = new OGlycanPepInfo(uniPepSeq, finalsb.toString(), scoresb.toString(), maxScore,
				peptideScore, locList, newUnits, null, null, null, newPositionTypeModScore, matchmap, peaks);
		pepInfo.setAnnotionMap1(annotionMap1);
		pepInfo.setAnnotionMap2(annotionMap2);
		pepInfo.setAnnotionMap3(annotionMap3);
		return pepInfo;
	}

	private void match(int[][] positionList, double[] scoreList, int[] glycoFragId, OGlycanUnit[] units, int position,
			double intensity, int by) {

		int[][] allArrFragId = Arrangmentor.arrangementArrays(glycoFragId);

		// b ion
		if (by == 0) {
			for (int i = 0; i < positionList.length; i++) {

				for (int j = 0; j < allArrFragId.length; j++) {

					int mci = 0;
					boolean match = false;
					for (int k = 0; k < position; k++) {

						if (positionList[i][k] >= 0) {
							if (units[positionList[i][k]].getFragmentIdSet().contains(allArrFragId[j][mci])) {
								mci++;
								if (mci == glycoFragId.length) {
									match = true;
									break;
								}
							}
						}
					}
					if (match) {
						scoreList[i] += intensity;
						break;
					}
				}
			}
		} else if (by == 1) {// y ion

			for (int i = 0; i < positionList.length; i++) {

				for (int j = 0; j < allArrFragId.length; j++) {

					int mci = 0;
					boolean match = false;
					for (int k = position; k < positionList[i].length; k++) {
						if (positionList[i][k] >= 0) {
							if (units[positionList[i][k]].getFragmentIdSet().contains(allArrFragId[j][mci])) {
								mci++;
								if (mci == glycoFragId.length) {
									match = true;
									break;
								}
							}
						}
					}
					if (match) {
						scoreList[i] += intensity;
						break;
					}
				}
			}
		}
	}

	public ArrayList<OGlycanPepInfo> getList(double fdr) {

		this.glycoinfolist = this.glycoMap.values().toArray(new OGlycanPepInfo[glycoMap.size()]);
		Arrays.sort(glycoinfolist, new Comparator<OGlycanPepInfo>() {

			@Override
			public int compare(OGlycanPepInfo p0, OGlycanPepInfo p1) {
				// TODO Auto-generated method stub
				if (p0.getPeptide().getPrimaryScore() < p1.getPeptide().getPrimaryScore()) {
					return 1;
				} else if (p0.getPeptide().getPrimaryScore() > p1.getPeptide().getPrimaryScore()) {
					return -1;
				}
				return 0;
			}

		});

		this.fdrs = new double[glycoinfolist.length];
		int target = 0;
		int decoy = 0;
		for (int i = 0; i < glycoinfolist.length; i++) {
			if (glycoinfolist[i].getPeptide().isTP()) {
				target++;
			} else {
				decoy++;
			}
			if (target != 0)
				fdrs[i] = (double) decoy / (double) target;
			else
				fdrs[i] = 0;
		}

		ArrayList<OGlycanPepInfo> list = new ArrayList<OGlycanPepInfo>();

		int i = fdrs.length - 1;
		for (; i >= 0; i--) {
			if (fdrs[i] <= fdr) {
				break;
			}
		}

		for (int j = 0; j <= i; j++) {
			list.add(this.glycoinfolist[j]);
			// if(glycoinfolist[i].getPeptide().isTP())
			// System.out.println(this.glycoinfolist[j].getMatchScore());
		}

		return list;
	}

	private String[] getRefs(IPeptide peptide)
			throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {

		ProteinReference[] reflist = peptide.getProteinReferences()
				.toArray(new ProteinReference[peptide.getProteinReferences().size()]);
		String[] refs = new String[reflist.length];
		for (int i = 0; i < refs.length; i++) {

			ProteinSequence ps = this.accesser.getSequence(reflist[i]);
			refs[i] = ps.getReference();
			// System.out.println(refs[i]);
		}

		Arrays.sort(refs);
		return refs;
	}

	private void glycoTest() {
		Iterator<String> it = this.glycoMap.keySet().iterator();
		while (it.hasNext()) {

			String key = it.next();
			OGlycanPepInfo pepInfo = this.glycoMap.get(key);
			IPeptide pep = pepInfo.getPeptide();
			if (pep.isTP()) {
				continue;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(pep.getScanNum()).append("\t");
			sb.append(pepInfo.getModseq()).append("\t");
			sb.append(pepInfo.getUniseq()).append("\t");
			sb.append(pepInfo.getGlycoScore()).append("\t");
			sb.append(pep.getSequence()).append("\t");
			sb.append(pep.isTP()).append("\t");
			sb.append(pep.getPrimaryScore()).append("\t");
			sb.append(pep.getRank()).append("\t");
			System.out.println(sb);
		}
	}

	public void writeTest(String out) throws RowsExceededException, WriteException, IOException {
		OGlycanXlsWriter writer = new OGlycanXlsWriter(out);
		writer.write(this.getList(0.01));
		writer.close();
	}

	public void pdfTest(String out) throws RowsExceededException, WriteException, IOException, DocumentException {
		OGlycanPdfWriter writer = new OGlycanPdfWriter(out);
		ArrayList<OGlycanPepInfo> list = this.getList(0.01);
		for (int i = 0; i < list.size(); i++) {
			writer.write(list.get(i));
		}
		writer.close();
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws FastaDataBaseException
	 * @throws DtaFileParsingException
	 * @throws PeptideParsingException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws ModsReadingException
	 * @throws MascotDatParsingException
	 * @throws JXLException
	 * @throws DocumentException
	 */
	public static void main(String[] args)
			throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException,
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, PeptideParsingException,
			DtaFileParsingException, FastaDataBaseException, IOException, JXLException, DocumentException {
		// TODO Auto-generated method stub

		String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\Final_ipi.HUMAN.v3.80.fasta";
		// String fasta = "H:\\OGlycan_0417_standard\\O-glycoprotein_0.fasta";
		String regex = "([^| ]*)";
		// String regex = "([^ ]*)";
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		// String pepinfo =
		// "H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\" +
		// "20120328_humaneserum_trypsin_HILIC_8uL-03.peps.info";
		// String pepinfo =
		// "H:\\OGlycan\\20130305\\1\\20130307\\20130301_O-glycosylation_10mg-HILICtip_10uLserum-2.peps.info";
		String pepinfo = "H:\\OGlycan_0530_2D\\2D_T+C\\OGlycan_0530_2D_TC\\" + "peps.info";
		OGlycanValidatorRank1 validator = new OGlycanValidatorRank1(fasta, regex, judger, pepinfo);

		// String t1 =
		// "H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\t1-F003866.dat";
		// String t2 =
		// "H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\t2-F003867.dat";
		// String t3 =
		// "H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\t3-F003868.dat";
		// String t4 =
		// "H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\t4-F003869.dat";
		// String t5 =
		// "H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\t5-F003870.dat";

		/*
		 * String t1 =
		 * "H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\type1_F004468.dat";
		 * String t2 =
		 * "H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\type2_F004467.dat";
		 * String t3 =
		 * "H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\type3_F004471.dat";
		 * String t4 =
		 * "H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\type4_F004472.dat";
		 * String t5 =
		 * "H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\type5_F004473.dat";
		 * // validator.valiType12(new File(t1), 2); validator.validate(t1);
		 * validator.validate(t2); validator.validate(t3);
		 * validator.validate(t4); validator.validate(t5);
		 */
		File[] files = (new File(
				"H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\20120328_humaneserum_trypsin_HILIC_8uL-02"))
						.listFiles();
		// for(int i=0;i<files.length;i++){
		// if(files[i].getName().endsWith("dat")){
		// validator.validate(files[i].getAbsolutePath());
		// }
		// }
		validator
				.validate("H:\\OGlycan_0530_2D\\2D_T+C\\OGlycan_0530_2D_TC\\" + "OGlycan_0530_2D_TC_type1_F004950.dat");
		System.out.println(validator.glycoMap.size());
		// System.out.println(validator.getList(0.01).size());
		validator.glycoTest();
		// validator.writeTest("H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\20120328_humaneserum_trypsin_HILIC_8uL-02"
		// +
		// "\\20120328_humaneserum_trypsin_HILIC_8uL-02.xls");
		// validator.pdfTest("H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\3-combine-NC2\\test5.pdf");
	}

}
