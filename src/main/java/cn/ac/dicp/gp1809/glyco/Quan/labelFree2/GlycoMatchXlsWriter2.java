/* 
 ******************************************************************************
 * File: GlycoMatchXlsWriter2.java * * * Created on 2013-6-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree2;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.dom4j.DocumentException;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.drawjf.JFChartDrawer;
import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.TMHMM.TMHProtein;
import cn.ac.dicp.gp1809.glyco.TMHMM.TMHReader;
import cn.ac.dicp.gp1809.glyco.drawjf.GlycoSpecMatchDataset;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.FileCopy;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import flanagan.analysis.Regression;

/**
 * @author ck
 * 
 * @version 2013-6-13, 13:48:54
 */
public class GlycoMatchXlsWriter2 {

	private ExcelWriter writer;
	private ExcelFormat format;
	private static DecimalFormat df4 = DecimalFormats.DF0_4;

	public GlycoMatchXlsWriter2(String file) throws IOException,
			RowsExceededException, WriteException {
		this.writer = new ExcelWriter(file, new String[] {
				"Identified glycopeptides", "Matched glycopeptides",
				"Unmatched glycopeptides" });
		this.format = ExcelFormat.normalFormat;

		this.addTitle();
	}

	/**
	 * @throws WriteException
	 * @throws RowsExceededException
	 * 
	 */
	private void addTitle() throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub

		StringBuilder sb1 = new StringBuilder();
		sb1.append("Reference\t");
		sb1.append("Site\t");
		sb1.append("Scan\t");
		sb1.append("Sequence\t");
		sb1.append("Precursor MW\t");
		sb1.append("Charge\t");
		sb1.append("Retention time\t");
		sb1.append("Score\t");
		sb1.append("Matched glyco spectra count\t");
		sb1.append("Matched glyco type count\t");

		writer.addTitle(sb1.toString(), 0, format);

		StringBuilder sb2 = new StringBuilder();

		sb2.append("Glycopep scannum\t");
		// sb2.append("Rank\t");
		sb2.append("Glycopep rt\t");
		sb2.append("Precursor m/z\t");
		sb2.append("Precursor mw\t");
		sb2.append("Precursor charge\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Calc peptide mw\t");
		sb2.append("Glycopep score\t");
		sb2.append("IUPAC Name\t");
		sb2.append("Type\t");
		writer.addTitle(sb2.toString(), 2, format);

		sb2.append("Peptide scannum\t");
		sb2.append("Sequence\t");
		sb2.append("Peptide rt\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Delta mw\t");
		sb2.append("Delta mw ppm\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");
		sb2.append("DeltaRT");
		
		writer.addTitle(sb2.toString(), 1, format);
	}

	public void write(IGlycoPeptide[] peps, NGlycoSSM[] matchedssms, NGlycoSSM[] unmatchedssms, double[] bestEstimate,
			ProteinNameAccesser accesser, double ppm, double rtTolerance)
			throws RowsExceededException, WriteException, IOException {

		HashMap<Integer, HashSet<String>> namemap = new HashMap<Integer, HashSet<String>>();
		HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < unmatchedssms.length; i++) {
			
			StringBuilder sb = new StringBuilder();

			NGlycoSSM ssm = unmatchedssms[i];

			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");
			
			writer.addContent(sb.toString(), 2, format);
		}

		for (int i = 0; i < matchedssms.length; i++) {

			StringBuilder sb = new StringBuilder();

			NGlycoSSM ssm = matchedssms[i];
			int peptideid = ssm.getPeptideid();
			IGlycoPeptide peptide = peps[peptideid];
			
			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
			double peprt = peptide.getRetentionTime();
			double calrt = bestEstimate[0] + bestEstimate[1] * ssm.getRT();
			
//			if(Math.abs(peprt-calrt)>rtTolerance){
//				continue;
//			}
			
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			if (countmap.containsKey(peptideid)) {
				countmap.put(peptideid, countmap.get(peptideid) + 1);
				namemap.get(peptideid).add(
						ssm.getGlycoTree().getIupacName());
			} else {
				countmap.put(peptideid, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getIupacName());
				namemap.put(peptideid, set);
			}

			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int j = 0; j < loc.length; j++) {
				loc[j] = sites[j].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide
					.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide
					.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
					sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");
			sb.append(df4.format(Math.abs(peprt-calrt))).append("\t");

			writer.addContent(sb.toString(), 1, format);
		}

		ExcelFormat format2 = new ExcelFormat(false, 2);
		for (int i = 0; i < peps.length; i++) {
			StringBuilder sb = new StringBuilder();
			IGlycoPeptide peptide = peps[i];
			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int j = 0; j < loc.length; j++) {
				loc[j] = sites[j].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				sb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
					sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\t");
			sb.append(sitesb).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(i)) {
				sb.append(countmap.get(i)).append("\t");
				sb.append(namemap.get(i).size());
			}

			boolean confuse = false;
			if (i == 0) {
				if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
						.getPepMrNoGlyco() * ppm * 1E-6) {
					confuse = true;
				}
			} else if (i == peps.length - 1) {
				if (peptide.getPepMrNoGlyco() - peps[i - 1].getPepMrNoGlyco() < peptide
						.getPepMrNoGlyco() * ppm * 1E-6) {
					confuse = true;
				}
			} else {
				if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
						.getPepMrNoGlyco() * ppm * 1E-6
						|| peptide.getPepMrNoGlyco()
								- peps[i - 1].getPepMrNoGlyco() < peptide
								.getPepMrNoGlyco() * ppm * 1E-6) {
					confuse = true;
				}
			}

			if (confuse) {
				writer.addContent(sb.toString(), 0, format2);
			} else {
				writer.addContent(sb.toString(), 0, format);
			}
		}

		writer.close();
	}

	private void writeRtCorr(IGlycoPeptide[] peps, NGlycoSSM[] matchedssms, NGlycoSSM[] unmatchedssms,
			ProteinNameAccesser accesser, double ppm)
			throws RowsExceededException, WriteException, IOException {

		HashMap<Integer, HashSet<String>> namemap = new HashMap<Integer, HashSet<String>>();
		HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();
		ArrayList<Double> peprtlist = new ArrayList<Double>();
		ArrayList<Double> gprtlist = new ArrayList<Double>();

		for (int i = 0; i < unmatchedssms.length; i++) {
			
			StringBuilder sb = new StringBuilder();

			NGlycoSSM ssm = unmatchedssms[i];

			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			writer.addContent(sb.toString(), 2, format);
		}
		
		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();

		for (int i = 0; i < matchedssms.length; i++) {

			NGlycoSSM ssm = matchedssms[i];

			IGlycoPeptide peptide = peps[ssm.getPeptideid()];
			peprtlist.add(peptide.getRetentionTime());
			gprtlist.add(ssm.getRT());

			ssmlist.add(ssm);
		}

		double[] glycoRtList = new double[peprtlist.size()];
		double[] pepRtList = new double[gprtlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = peprtlist.get(i);
			pepRtList[i] = gprtlist.get(i);
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();

		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			int peptideid = ssm.getPeptideid();
			IGlycoPeptide peptide = peps[peptideid];
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			if (Math.abs(peprt - calrt) > 30)
				continue;

			if (countmap.containsKey(peptideid)) {
				countmap.put(peptideid, countmap.get(peptideid) + 1);
				namemap.get(peptideid).add(ssm.getGlycoTree().getIupacName());
			} else {
				countmap.put(peptideid, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getIupacName());
				namemap.put(peptideid, set);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int j = 0; j < loc.length; j++) {
				loc[j] = sites[j].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
					sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");

			writer.addContent(sb.toString(), 1, format);
		}

		ExcelFormat format2 = new ExcelFormat(false, 2);
		for (int i = 0; i < peps.length; i++) {
			StringBuilder sb = new StringBuilder();
			IGlycoPeptide peptide = peps[i];
			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int j = 0; j < loc.length; j++) {
				loc[j] = sites[j].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				sb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
					sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\t");
			sb.append(sitesb).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(i)) {
				sb.append(countmap.get(i)).append("\t");
				sb.append(namemap.get(i).size());
			}

			boolean confuse = false;
			if (i == 0) {
				if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
						.getPepMrNoGlyco() * ppm * 1E-6) {
					confuse = true;
				}
			} else if (i == peps.length - 1) {
				if (peptide.getPepMrNoGlyco() - peps[i - 1].getPepMrNoGlyco() < peptide
						.getPepMrNoGlyco() * ppm * 1E-6) {
					confuse = true;
				}
			} else {
				if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
						.getPepMrNoGlyco() * ppm * 1E-6
						|| peptide.getPepMrNoGlyco()
								- peps[i - 1].getPepMrNoGlyco() < peptide
								.getPepMrNoGlyco() * ppm * 1E-6) {
					confuse = true;
				}
			}

			if (confuse) {
				writer.addContent(sb.toString(), 0, format2);
			} else {
				writer.addContent(sb.toString(), 0, format);
			}
		}

		writer.close();
	}

	private static void test(String in, String out) throws Exception {
		GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(in);
		GlycoMatchXlsWriter2 writer = new GlycoMatchXlsWriter2(out);
		IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
		ProteinNameAccesser accesser = reader.getProNameAccesser();
//		writer.writeRtCorr(peps, reader.getMatchedGlycoSpectra(), reader.getUnmatchedGlycoSpectra(), accesser, 10);
		writer.write(peps, reader.getMatchedGlycoSpectra(), reader.getUnmatchedGlycoSpectra(), reader.getBestEstimate(), accesser, 10, 30);
	}

	private static void batchtest(String dir) throws Exception {
		File[] files = (new File(dir)).listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith("pxml")) {
				String in = files[i].getAbsolutePath();
				String out = in.replace("pxml", "xls");
				test(in, out);
			}
		}
	}
	
	private static void combineXML(String in) throws DocumentException{

		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();
		ArrayList<IGlycoPeptide> peplist = new ArrayList<IGlycoPeptide>();
		ArrayList<Double>[] list = new ArrayList[20];
		for(int i=0;i<list.length;i++){
			list[i]= new ArrayList <Double>();
		}
		int total = 0;
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {

			if (!files[id].getName().endsWith("pxml"))
				continue;

			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			total+=ssms.length;
//			total+=reader.getUnmatchedGlycoSpectra().length;
			for (int i = 0; i < ssms.length; i++) {

				NGlycoSSM ssm = ssms[i];
				int peptideid = ssm.getPeptideid();
				IGlycoPeptide peptide = peps[peptideid];
				ssmlist.add(ssm);
				peplist.add(peptide);
				
				double deltaMz = peptide.getPepMrNoGlyco()
						- ssm.getPepMassExperiment();
				double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
				int deltaid = (int)(deltaMzPPM+10);
				list[deltaid].add(deltaMz);
			}
		}
		
		for(int i=0;i<list.length;i++){
			System.out.println(i+"\t"+list[i].size());
		}
		System.out.println("total\t"+total);
		/*double[] glycoRtList = new double[peplist.size()];
		double[] pepRtList = new double[ssmlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = ssmlist.get(i).getRT();
			pepRtList[i] = peplist.get(i).getRetentionTime();
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();
System.out.println(reg.getCoeff()[0]+"\n"+Arrays.toString(fit));
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			if (Math.abs(peprt - calrt) >=3 && Math.abs(peprt - calrt) < 5){
				System.out.println(ssm.getRT()+"\t"+peprt);
			}
		}*/
	}

	private static void combineXML(String in, String out)
			throws DocumentException, IOException, RowsExceededException,
			WriteException {

		ExcelWriter writer = new ExcelWriter(out, new String[] {
				"Identified glycopeptides", "Matched glycopeptides", "Glycosylation site"});
		ExcelFormat format = ExcelFormat.normalFormat;

		StringBuilder sb1 = new StringBuilder();
		sb1.append("Reference\t");
		sb1.append("Site\t");
		sb1.append("Scan\t");
		sb1.append("Sequence\t");
		sb1.append("Precursor MW\t");
		sb1.append("Charge\t");
		sb1.append("Retention time\t");
		sb1.append("Score\t");
		sb1.append("Matched glyco spectra count\t");
		sb1.append("Matched glyco type count\t");

		writer.addTitle(sb1.toString(), 0, format);

		StringBuilder sb2 = new StringBuilder();

		sb2.append("File\t");
		sb2.append("Glycopep scannum\t");
		// sb2.append("Rank\t");
		sb2.append("Glycopep rt\t");
		sb2.append("Precursor m/z\t");
		sb2.append("Precursor mw\t");
		sb2.append("Precursor charge\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Calc peptide mw\t");
		sb2.append("Glycopep score\t");
		sb2.append("IUPAC Name\t");
		sb2.append("Type\t");
		sb2.append("Peptide scannum\t");
		sb2.append("Sequence\t");
		sb2.append("Peptide rt\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Delta mw\t");
		sb2.append("Delta mw ppm\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");
		sb2.append("Identified count");

		writer.addTitle(sb2.toString(), 1, format);
		
		StringBuilder sb3 = new StringBuilder();
		sb3.append("Reference\t");
		sb3.append("Site\t");
		sb3.append("IUPAC Name\t");
		sb3.append("Type\t");
		sb3.append("Identified count");
		
		writer.addTitle(sb3.toString(), 2, format);

		HashMap<String, IGlycoPeptide> pepmap = new HashMap<String, IGlycoPeptide>();
		HashMap<String, Double> psmap = new HashMap<String, Double>();
		HashMap<String, String> refmap = new HashMap<String, String>();

		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();
		ArrayList<String> filelist = new ArrayList<String>();
		ArrayList<IGlycoPeptide> peplist = new ArrayList<IGlycoPeptide>();
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {
			String filename = files[id].getName();
			if (!filename.endsWith("pxml"))
				continue;

			String name = filename.substring(0, filename.length()-5);
			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			ProteinNameAccesser accesser = reader.getProNameAccesser();
//System.out.println(files[id].getName()+"\t"+(ssms.length+reader.getUnmatchedGlycoSpectra().length));			
			for(int i=0;i<peps.length;i++){

				IGlycoPeptide peptide = peps[i];
				GlycoSite[] sites = peptide.getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

				StringBuilder refsb = new StringBuilder();
				StringBuilder sitesb = new StringBuilder();
				HashSet<ProteinReference> refset = peptide.getProteinReferences();
				for (ProteinReference ref : refset) {
					SimpleProInfo info = accesser.getProInfo(ref.getName());
					refsb.append(info.getRef()).append(";");

					SeqLocAround sla = slamap.get(ref.toString());
					for (int j = 0; j < loc.length; j++) {
						loc[j] = sites[j].modifLocation();
						sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
					}
					sitesb.deleteCharAt(sitesb.length() - 1);
					sitesb.append(";");
				}
				refsb.deleteCharAt(refsb.length() - 1);
				sitesb.deleteCharAt(sitesb.length() - 1);
				refsb.append("\t").append(sitesb);

				String sequence = peptide.getSequence();
				if (psmap.containsKey(sequence)) {
					if(peptide.getPrimaryScore()>psmap.get(sequence)){
						pepmap.put(sequence, peptide);
					}
				}else{
					psmap.put(sequence, (double)peptide.getPrimaryScore());
					pepmap.put(sequence, peptide);
					refmap.put(sequence, refsb.toString());
				}
			}

			for (int i = 0; i < ssms.length; i++) {

				NGlycoSSM ssm = ssms[i];
				if(ssm.getScore()<5) continue;
				
				/*if(name.contains("Rui_20130604_HEK_HILIC_F5") && ssm.getScanNum()==9864) {
					continue;
				}
				if(name.contains("Rui_20130604_HEK_HILIC_F6") && ssm.getScanNum()==14375) {
					continue;
				}*/
				
				int peptideid = ssm.getPeptideid();
				IGlycoPeptide peptide = peps[peptideid];
				ssmlist.add(ssm);
				peplist.add(peptide);
				filelist.add(name);
			}
		}
		
		double[] glycoRtList = new double[peplist.size()];
		double[] pepRtList = new double[ssmlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = ssmlist.get(i).getRT();
			pepRtList[i] = peplist.get(i).getRetentionTime();
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();
//System.out.println(fit[0]+"\t"+fit[1]+"\t"+reg.getCoefficientOfDetermination());
		HashMap<String, HashSet<String>> namemap = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, String> contentmap = new HashMap<String, String>();
		HashMap<String, Double> scoremap = new HashMap<String, Double>();
		HashMap<String, Integer> uniquecountmap = new HashMap<String, Integer>();
		HashMap<String, String> sitemap = new HashMap<String, String>();
		HashMap<String, Integer> sitecountmap = new HashMap<String, Integer>();
		
		PrintWriter p1 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\1.txt");
		PrintWriter p2 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\2.txt");
		PrintWriter p3 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\3.txt");
		PrintWriter p4 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\4.txt");
		PrintWriter p5 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\5.txt");
		PrintWriter p6 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\6.txt");
		PrintWriter p7= new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\7.txt");
		
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			double delta = Math.abs(peprt - calrt);
			if(delta<=1){
				p1.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>1 && delta<=3){
				p2.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>3 && delta<=5){
				p3.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>5 && delta<=10){
				p4.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>10 && delta<=15){
				p5.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>15 && delta<=20){
				p6.println(ssm.getRT()+"\t"+peprt);
			}else{
				p7.println(ssm.getRT()+"\t"+peprt);
			}
			
//			if (Math.abs(peprt - calrt) > 10)
//				continue;

			String sequence = peptide.getSequence();
			if (countmap.containsKey(sequence)) {
				countmap.put(sequence, countmap.get(sequence) + 1);
				namemap.get(sequence).add(ssm.getGlycoTree().getIupacName());
			} else {
				countmap.put(sequence, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getIupacName());
				namemap.put(sequence, set);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(filelist.get(i)).append("\t");
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
			
//System.out.println(deltaMzPPM);			
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refmap.get(sequence)).append("\t");
//System.out.println(deltaMzPPM);
			String key = sequence+ssm.getGlycoTree().getIupacName();
			if(scoremap.containsKey(key)){
				if(ssm.getScore()>scoremap.get(key)){
					scoremap.put(key, ssm.getScore());
					contentmap.put(key, sb.toString());
				}
				uniquecountmap.put(key, uniquecountmap.get(key)+1);
			}else{
				scoremap.put(key, ssm.getScore());
				contentmap.put(key, sb.toString());
				uniquecountmap.put(key, 1);
			}
			
			String sitekey = refmap.get(sequence)+"\t"+ssm.getGlycoTree().getIupacName();
			String sitevalue = sitekey+"\t"+ssm.getGlycoTree().getType()+"\t";
			if(sitemap.containsKey(sitekey)){
				sitecountmap.put(sitekey, sitecountmap.get(sitekey)+1);
			}else{
				sitemap.put(sitekey, sitevalue);
				sitecountmap.put(sitekey, 1);
			}
		}
		
		Iterator <String> it1 = pepmap.keySet().iterator();
		
		while(it1.hasNext()){
			
			String key = it1.next();
			IGlycoPeptide peptide = pepmap.get(key);
			String sequence = peptide.getSequence();
			String refinfo = refmap.get(sequence);
			StringBuilder sb = new StringBuilder();
			sb.append(refinfo).append("\t");
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(sequence)) {
				sb.append(countmap.get(sequence)).append("\t");
				sb.append(namemap.get(sequence).size());
			}
			
			writer.addContent(sb.toString(), 0, format);
		}
		
		Iterator <String> it2 = contentmap.keySet().iterator();
		
		while(it2.hasNext()){
			
			String key = it2.next();
			String content = contentmap.get(key);
			
			writer.addContent(content+uniquecountmap.get(key), 1, format);
		}
		
		Iterator <String> it3 = sitemap.keySet().iterator();
		while(it3.hasNext()){
			String key = it3.next();
			String content = sitemap.get(key);
			
			writer.addContent(content+sitecountmap.get(key), 2, format);
		}

		writer.close();
		
		p1.close();
		p2.close();
		p3.close();
		p4.close();
		p5.close();
		p6.close();
		p7.close();
	}

	private static void combineXMLComp(String in, String out)
			throws DocumentException, IOException, RowsExceededException,
			WriteException {

		ExcelWriter writer = new ExcelWriter(out, new String[] {
				"Identified glycopeptides", "Matched glycopeptides", "Glycosylation site"});
		ExcelFormat format = ExcelFormat.normalFormat;

		StringBuilder sb1 = new StringBuilder();
		sb1.append("Reference\t");
		sb1.append("Site\t");
		sb1.append("Scan\t");
		sb1.append("Sequence\t");
		sb1.append("Precursor MW\t");
		sb1.append("Charge\t");
		sb1.append("Retention time\t");
		sb1.append("Score\t");
		sb1.append("Matched glyco spectra count\t");
		sb1.append("Matched glyco type count\t");

		writer.addTitle(sb1.toString(), 0, format);

		StringBuilder sb2 = new StringBuilder();

		sb2.append("File\t");
		sb2.append("Glycopep scannum\t");
		// sb2.append("Rank\t");
		sb2.append("Glycopep rt\t");
		sb2.append("Precursor m/z\t");
		sb2.append("Precursor mw\t");
		sb2.append("Precursor charge\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Calc peptide mw\t");
		sb2.append("Glycopep score\t");
		sb2.append("IUPAC Name\t");
		sb2.append("Type\t");
		sb2.append("Peptide scannum\t");
		sb2.append("Sequence\t");
		sb2.append("Peptide rt\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Delta mw\t");
		sb2.append("Delta mw ppm\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");
		sb2.append("Identified count");

		writer.addTitle(sb2.toString(), 1, format);
		
		StringBuilder sb3 = new StringBuilder();
		sb3.append("Reference\t");
		sb3.append("Site\t");
		sb3.append("IUPAC Name\t");
		sb3.append("Type\t");
		sb3.append("Identified count");
		
		writer.addTitle(sb3.toString(), 2, format);

		HashMap<String, IGlycoPeptide> pepmap = new HashMap<String, IGlycoPeptide>();
		HashMap<String, Double> psmap = new HashMap<String, Double>();
		HashMap<String, String> refmap = new HashMap<String, String>();

		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();
		ArrayList<String> filelist = new ArrayList<String>();
		ArrayList<IGlycoPeptide> peplist = new ArrayList<IGlycoPeptide>();
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {
			String filename = files[id].getName();
			if (!filename.endsWith("pxml"))
				continue;

			String name = filename.substring(0, filename.length()-5);
			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			ProteinNameAccesser accesser = reader.getProNameAccesser();
//System.out.println(files[id].getName()+"\t"+(ssms.length+reader.getUnmatchedGlycoSpectra().length));			
			for(int i=0;i<peps.length;i++){

				IGlycoPeptide peptide = peps[i];
				GlycoSite[] sites = peptide.getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

				StringBuilder refsb = new StringBuilder();
				StringBuilder sitesb = new StringBuilder();
				HashSet<ProteinReference> refset = peptide.getProteinReferences();
				for (ProteinReference ref : refset) {
					SimpleProInfo info = accesser.getProInfo(ref.getName());
					refsb.append(info.getRef()).append(";");

					SeqLocAround sla = slamap.get(ref.toString());
					for (int j = 0; j < loc.length; j++) {
						loc[j] = sites[j].modifLocation();
						sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
					}
					sitesb.deleteCharAt(sitesb.length() - 1);
					sitesb.append(";");
				}
				refsb.deleteCharAt(refsb.length() - 1);
				sitesb.deleteCharAt(sitesb.length() - 1);
				refsb.append("\t").append(sitesb);

				String sequence = peptide.getSequence();
				if (psmap.containsKey(sequence)) {
					if(peptide.getPrimaryScore()>psmap.get(sequence)){
						pepmap.put(sequence, peptide);
					}
				}else{
					psmap.put(sequence, (double)peptide.getPrimaryScore());
					pepmap.put(sequence, peptide);
					refmap.put(sequence, refsb.toString());
				}
			}

			for (int i = 0; i < ssms.length; i++) {

				NGlycoSSM ssm = ssms[i];
				if(!ssm.getGlycoTree().isMammal()) continue;
//				System.out.println(ssm.getGlycoTree().isMammal());
				if(ssm.getScore()<5) continue;
				
				/*if(name.contains("Rui_20130604_HEK_HILIC_F5") && ssm.getScanNum()==9864) {
					continue;
				}
				if(name.contains("Rui_20130604_HEK_HILIC_F6") && ssm.getScanNum()==14375) {
					continue;
				}*/
				
				int peptideid = ssm.getPeptideid();
				IGlycoPeptide peptide = peps[peptideid];
				ssmlist.add(ssm);
				peplist.add(peptide);
				filelist.add(name);
			}
		}
		
		double[] glycoRtList = new double[peplist.size()];
		double[] pepRtList = new double[ssmlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = ssmlist.get(i).getRT();
			pepRtList[i] = peplist.get(i).getRetentionTime();
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();
//System.out.println(fit[0]+"\t"+fit[1]+"\t"+reg.getCoefficientOfDetermination());
		HashMap<String, HashSet<String>> namemap = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, String> contentmap = new HashMap<String, String>();
		HashMap<String, Double> scoremap = new HashMap<String, Double>();
		HashMap<String, Integer> uniquecountmap = new HashMap<String, Integer>();
		HashMap<String, String> sitemap = new HashMap<String, String>();
		HashMap<String, Integer> sitecountmap = new HashMap<String, Integer>();
		
		PrintWriter p1 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\1.txt");
		PrintWriter p2 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\2.txt");
		PrintWriter p3 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\3.txt");
		PrintWriter p4 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\4.txt");
		PrintWriter p5 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\5.txt");
		PrintWriter p6 = new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\6.txt");
		PrintWriter p7= new PrintWriter("H:\\NGLYCO\\NGlyco_final_20140408\\7.txt");
		
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			double delta = Math.abs(peprt - calrt);
			if(delta<=1){
				p1.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>1 && delta<=3){
				p2.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>3 && delta<=5){
				p3.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>5 && delta<=10){
				p4.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>10 && delta<=15){
				p5.println(ssm.getRT()+"\t"+peprt);
			}else if(delta>15 && delta<=20){
				p6.println(ssm.getRT()+"\t"+peprt);
			}else{
				p7.println(ssm.getRT()+"\t"+peprt);
			}
			
//			if (Math.abs(peprt - calrt) > 10)
//				continue;

			String sequence = peptide.getSequence();
			if (countmap.containsKey(sequence)) {
				countmap.put(sequence, countmap.get(sequence) + 1);
				namemap.get(sequence).add(ssm.getGlycoTree().getCompositionString());
			} else {
				countmap.put(sequence, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getCompositionString());
				namemap.put(sequence, set);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(filelist.get(i)).append("\t");
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getCompositionString()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
			
//System.out.println(deltaMzPPM);			
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refmap.get(sequence)).append("\t");
//System.out.println(deltaMzPPM);
			String key = sequence+ssm.getGlycoTree().getCompositionString();
			if(scoremap.containsKey(key)){
				if(ssm.getScore()>scoremap.get(key)){
					scoremap.put(key, ssm.getScore());
					contentmap.put(key, sb.toString());
				}
				uniquecountmap.put(key, uniquecountmap.get(key)+1);
			}else{
				scoremap.put(key, ssm.getScore());
				contentmap.put(key, sb.toString());
				uniquecountmap.put(key, 1);
			}
			
			String sitekey = refmap.get(sequence)+"\t"+ssm.getGlycoTree().getCompositionString();
			String sitevalue = sitekey+"\t"+ssm.getGlycoTree().getType()+"\t";
			if(sitemap.containsKey(sitekey)){
				sitecountmap.put(sitekey, sitecountmap.get(sitekey)+1);
			}else{
				sitemap.put(sitekey, sitevalue);
				sitecountmap.put(sitekey, 1);
			}
		}
		
		Iterator <String> it1 = pepmap.keySet().iterator();
		
		while(it1.hasNext()){
			
			String key = it1.next();
			IGlycoPeptide peptide = pepmap.get(key);
			String sequence = peptide.getSequence();
			String refinfo = refmap.get(sequence);
			StringBuilder sb = new StringBuilder();
			sb.append(refinfo).append("\t");
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(sequence)) {
				sb.append(countmap.get(sequence)).append("\t");
				sb.append(namemap.get(sequence).size());
			}
			
			writer.addContent(sb.toString(), 0, format);
		}
		
		Iterator <String> it2 = contentmap.keySet().iterator();
		
		while(it2.hasNext()){
			
			String key = it2.next();
			String content = contentmap.get(key);
			
			writer.addContent(content+uniquecountmap.get(key), 1, format);
		}
		
		Iterator <String> it3 = sitemap.keySet().iterator();
		while(it3.hasNext()){
			String key = it3.next();
			String content = sitemap.get(key);
			
			writer.addContent(content+sitecountmap.get(key), 2, format);
		}

		writer.close();
		
		p1.close();
		p2.close();
		p3.close();
		p4.close();
		p5.close();
		p6.close();
		p7.close();
	}
	
	private static void combineXMLCompManual(String in, String out, String manual)
			throws DocumentException, IOException, JXLException {
		
		HashSet<String> manualSet = new HashSet<String>();
		ExcelReader mreader = new ExcelReader(manual, 1);
		String[] mline = mreader.readLine();
		while((mline=mreader.readLine())!=null){
			manualSet.add(mline[9]+"_"+mline[12]);
		}
		mreader.close();

		ExcelWriter writer = new ExcelWriter(out, new String[] {
				"Identified glycopeptides", "Matched glycopeptides", "Glycosylation site"});
		ExcelFormat format = ExcelFormat.normalFormat;
		DecimalFormat df2 = DecimalFormats.DF0_2;

		StringBuilder sb1 = new StringBuilder();
		sb1.append("Reference\t");
		sb1.append("Site\t");
		sb1.append("Scan\t");
		sb1.append("Sequence\t");
		sb1.append("Precursor MW\t");
		sb1.append("Charge\t");
		sb1.append("Retention time\t");
		sb1.append("Score\t");
		sb1.append("Matched glyco spectra count\t");
		sb1.append("Matched glyco type count\t");

		writer.addTitle(sb1.toString(), 0, format);

		StringBuilder sb2 = new StringBuilder();

		sb2.append("File\t");
		sb2.append("Glycopep scannum\t");
		// sb2.append("Rank\t");
		sb2.append("Glycopep rt\t");
		sb2.append("Precursor m/z\t");
		sb2.append("Precursor mw\t");
		sb2.append("Precursor charge\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Calc peptide mw\t");
		sb2.append("Glycopep score\t");
		sb2.append("Composition\t");
		sb2.append("IUPAC Name\t");
		sb2.append("Type\t");
		sb2.append("Peptide scannum\t");
		sb2.append("Sequence\t");
		sb2.append("Peptide rt\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Delta mw\t");
		sb2.append("Delta mw ppm\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");
		sb2.append("Identified count");

		writer.addTitle(sb2.toString(), 1, format);
		
		StringBuilder sb3 = new StringBuilder();
		sb3.append("Reference\t");
		sb3.append("Site\t");
		sb3.append("Composition\t");
		sb3.append("IUPAC Name\t");
		sb3.append("Type\t");
		sb3.append("Identified count");
		
		writer.addTitle(sb3.toString(), 2, format);

		HashMap<String, IGlycoPeptide> pepmap = new HashMap<String, IGlycoPeptide>();
		HashMap<String, Double> psmap = new HashMap<String, Double>();
		HashMap<String, String> refmap = new HashMap<String, String>();

		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();
		ArrayList<String> filelist = new ArrayList<String>();
		ArrayList<IGlycoPeptide> peplist = new ArrayList<IGlycoPeptide>();
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {
			String filename = files[id].getName();
			if (!filename.endsWith("pxml"))
				continue;

			String name = filename.substring(0, filename.length()-5);
			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			ProteinNameAccesser accesser = reader.getProNameAccesser();
//System.out.println(files[id].getName()+"\t"+(ssms.length+reader.getUnmatchedGlycoSpectra().length));			
			for(int i=0;i<peps.length;i++){

				IGlycoPeptide peptide = peps[i];
				GlycoSite[] sites = peptide.getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

				StringBuilder refsb = new StringBuilder();
				StringBuilder sitesb = new StringBuilder();
				HashSet<ProteinReference> refset = peptide.getProteinReferences();
				for (ProteinReference ref : refset) {
					SimpleProInfo info = accesser.getProInfo(ref.getName());
					refsb.append(info.getRef()).append(";");

					SeqLocAround sla = slamap.get(ref.toString());
					for (int j = 0; j < loc.length; j++) {
						loc[j] = sites[j].modifLocation();
						sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
					}
					sitesb.deleteCharAt(sitesb.length() - 1);
					sitesb.append(";");
				}
				refsb.deleteCharAt(refsb.length() - 1);
				sitesb.deleteCharAt(sitesb.length() - 1);
				refsb.append("\t").append(sitesb);

				String sequence = peptide.getSequence();
				if (psmap.containsKey(sequence)) {
					if(peptide.getPrimaryScore()>psmap.get(sequence)){
						pepmap.put(sequence, peptide);
					}
				}else{
					psmap.put(sequence, (double)peptide.getPrimaryScore());
					pepmap.put(sequence, peptide);
					refmap.put(sequence, refsb.toString());
				}
			}

			for (int i = 0; i < ssms.length; i++) {

				NGlycoSSM ssm = ssms[i];
				if(!ssm.getGlycoTree().isMammal()) continue;
				if(ssm.getScore()<5) continue;

				int peptideid = ssm.getPeptideid();
				IGlycoPeptide peptide = peps[peptideid];
				ssmlist.add(ssm);
				peplist.add(peptide);
				filelist.add(name);
			}
		}
		
		double[] glycoRtList = new double[peplist.size()];
		double[] pepRtList = new double[ssmlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = ssmlist.get(i).getRT();
			pepRtList[i] = peplist.get(i).getRetentionTime();
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();
//System.out.println(fit[0]+"\t"+fit[1]+"\t"+reg.getCoefficientOfDetermination());
		HashMap<String, HashSet<String>> namemap = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, String> contentmap = new HashMap<String, String>();
		HashMap<String, Double> scoremap = new HashMap<String, Double>();
		HashMap<String, Integer> uniquecountmap = new HashMap<String, Integer>();
		HashMap<String, String> sitemap = new HashMap<String, String>();
		HashMap<String, Integer> sitecountmap = new HashMap<String, Integer>();
//System.out.println("list size\t"+ssmlist.size());
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();
			
			if (Math.abs(peprt - calrt) > 10)
				continue;

			String sequence = peptide.getSequence();
			if (countmap.containsKey(sequence)) {
				countmap.put(sequence, countmap.get(sequence) + 1);
				namemap.get(sequence).add(ssm.getGlycoTree().getCompositionString());
			} else {
				countmap.put(sequence, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getCompositionString());
				namemap.put(sequence, set);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(filelist.get(i)).append("\t");
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(df2.format(ssm.getRT())).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(df2.format(ssm.getScore())).append("\t");
			sb.append(ssm.getGlycoTree().getCompositionString()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
			
//System.out.println(deltaMzPPM);			
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(df2.format(peptide.getRetentionTime())).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df2.format(deltaMzPPM)).append("\t");
			sb.append(refmap.get(sequence)).append("\t");
//System.out.println(deltaMzPPM);
			String key = sequence+ssm.getGlycoTree().getCompositionString();
			if(scoremap.containsKey(key)){
				if(ssm.getScore()>scoremap.get(key)){
					scoremap.put(key, ssm.getScore());
					contentmap.put(key, sb.toString());
				}
				uniquecountmap.put(key, uniquecountmap.get(key)+1);
			}else{
				scoremap.put(key, ssm.getScore());
				contentmap.put(key, sb.toString());
				uniquecountmap.put(key, 1);
			}

			String mkey = ssm.getGlycoTree().getCompositionString()+"_"+peptide.getSequence();
			if(!manualSet.contains(mkey)) {
				System.out.println(sb.toString());
			}
			
			String sitekey = refmap.get(sequence)+"\t"+ssm.getGlycoTree().getCompositionString();
			String sitevalue = sitekey+"\t"+ssm.getGlycoTree().getIupacName()+"\t"+ssm.getGlycoTree().getType()+"\t";
			if(sitemap.containsKey(sitekey)){
				sitecountmap.put(sitekey, sitecountmap.get(sitekey)+1);
			}else{
				sitemap.put(sitekey, sitevalue);
				sitecountmap.put(sitekey, 1);
			}
		}
		
		Iterator <String> it1 = pepmap.keySet().iterator();
		
		while(it1.hasNext()){
			
			String key = it1.next();
			IGlycoPeptide peptide = pepmap.get(key);
			String sequence = peptide.getSequence();
			String refinfo = refmap.get(sequence);
			StringBuilder sb = new StringBuilder();
			sb.append(refinfo).append("\t");
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(sequence)) {
				sb.append(countmap.get(sequence)).append("\t");
				sb.append(namemap.get(sequence).size());
			}
			
			writer.addContent(sb.toString(), 0, format);
		}
		
		Iterator <String> it2 = contentmap.keySet().iterator();
		
		while(it2.hasNext()){
			
			String key = it2.next();
			String content = contentmap.get(key);
			
			writer.addContent(content+uniquecountmap.get(key), 1, format);
		}
		
		Iterator <String> it3 = sitemap.keySet().iterator();
		while(it3.hasNext()){
			String key = it3.next();
			String content = sitemap.get(key);
			
			writer.addContent(content+sitecountmap.get(key), 2, format);
		}

		writer.close();
	}
	
	private static void combineXML(String in, String out, String TMHMM)
			throws DocumentException, IOException, JXLException {
		
		TMHReader tmhReader = new TMHReader();
		tmhReader.parseFileXls(TMHMM);
		ArrayList <TMHProtein> tmhlist = tmhReader.getTMHProteinList();
		HashMap<String, TMHProtein> tmhmap = new HashMap<String, TMHProtein>();
		for(int i=0;i<tmhlist.size();i++){
			tmhmap.put(tmhlist.get(i).getIpi(), tmhlist.get(i));
//			System.out.println(tmhlist.get(i).getIpi());
		}

		ExcelWriter writer = new ExcelWriter(out, new String[] {
				"Identified glycopeptides", "Matched glycopeptides", "Glycosylation site"});
		ExcelFormat format = ExcelFormat.normalFormat;

		StringBuilder sb1 = new StringBuilder();
		sb1.append("Reference\t");
		sb1.append("Site\t");
		sb1.append("Scan\t");
		sb1.append("Sequence\t");
		sb1.append("Precursor MW\t");
		sb1.append("Charge\t");
		sb1.append("Retention time\t");
		sb1.append("Score\t");
		sb1.append("Matched glyco spectra count\t");
		sb1.append("Matched glyco type count\t");

		writer.addTitle(sb1.toString(), 0, format);

		StringBuilder sb2 = new StringBuilder();

		sb2.append("Glycopep scannum\t");
		// sb2.append("Rank\t");
		sb2.append("Glycopep rt\t");
		sb2.append("Precursor m/z\t");
		sb2.append("Precursor mw\t");
		sb2.append("Precursor charge\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Calc peptide mw\t");
		sb2.append("Glycopep score\t");
		sb2.append("IUPAC Name\t");
		sb2.append("Type\t");
		sb2.append("Peptide scannum\t");
		sb2.append("Sequence\t");
		sb2.append("Peptide rt\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Delta mw\t");
		sb2.append("Delta mw ppm\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");
		sb2.append("Identified count");

		writer.addTitle(sb2.toString(), 1, format);
		
		StringBuilder sb3 = new StringBuilder();
		sb3.append("Reference\t");
		sb3.append("Site\t");
		sb3.append("IUPAC Name\t");
		sb3.append("Type\t");
		sb3.append("TMHMM result\t");
		sb3.append("Identified count\t");
		
		writer.addTitle(sb3.toString(), 2, format);

		HashMap<String, IGlycoPeptide> pepmap = new HashMap<String, IGlycoPeptide>();
		HashMap<String, Double> psmap = new HashMap<String, Double>();
		HashMap<String, String> refmap = new HashMap<String, String>();
		HashMap<String, String> sitelocmap = new HashMap<String, String>();

		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();
		ArrayList<IGlycoPeptide> peplist = new ArrayList<IGlycoPeptide>();
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {

			if (!files[id].getName().endsWith("pxml"))
				continue;

			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			ProteinNameAccesser accesser = reader.getProNameAccesser();
System.out.println(files[id].getName()+"\t"+(ssms.length+reader.getUnmatchedGlycoSpectra().length)+"\t"+peps.length);			
			for(int i=0;i<peps.length;i++){

				IGlycoPeptide peptide = peps[i];
				GlycoSite[] sites = peptide.getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

				StringBuilder refsb = new StringBuilder();
				StringBuilder sitesb = new StringBuilder();
				HashSet<ProteinReference> refset = peptide.getProteinReferences();
				String ipi = "";
				int glycositeloc = -1;
				for (ProteinReference ref : refset) {
					SimpleProInfo info = accesser.getProInfo(ref.getName());
					refsb.append(info.getRef()).append(";");
					ipi = info.getRef().substring(4, info.getRef().indexOf("|"));

					SeqLocAround sla = slamap.get(ref.toString());
					for (int j = 0; j < loc.length; j++) {
						loc[j] = sites[j].modifLocation();
						glycositeloc = sla.getBeg() + loc[j] - 1;
						sitesb.append(glycositeloc).append("/");
					}
					sitesb.deleteCharAt(sitesb.length() - 1);
					sitesb.append(";");
				}
				refsb.deleteCharAt(refsb.length() - 1);
				sitesb.deleteCharAt(sitesb.length() - 1);
				refsb.append("\t").append(sitesb);

				if(tmhmap.containsKey(ipi)){
					
					TMHProtein tmhprotein = tmhmap.get(ipi);
					int numOfTHMs = tmhprotein.getNumOfTHMs();
					if(numOfTHMs==-1){
						sitelocmap.put(refsb.toString(), "Non-transmembrane protein, outside");
					}else if(numOfTHMs==0){
						sitelocmap.put(refsb.toString(), "Non-transmembrane protein, inside");
					}else{
						int loctype = tmhprotein.judge(glycositeloc);
						if(loctype==0){
							sitelocmap.put(refsb.toString(), "Transmembrane protein, inside");
						}else if(loctype==1){
							sitelocmap.put(refsb.toString(), "Transmembrane protein, TMhelix");
						}else if(loctype==2){
							sitelocmap.put(refsb.toString(), "Transmembrane protein, outside");
						}
					}
				}
				
				String sequence = peptide.getSequence();
				if (psmap.containsKey(sequence)) {
					if(peptide.getPrimaryScore()>psmap.get(sequence)){
						pepmap.put(sequence, peptide);
					}
				}else{
					psmap.put(sequence, (double)peptide.getPrimaryScore());
					pepmap.put(sequence, peptide);
					refmap.put(sequence, refsb.toString());
				}
			}

			for (int i = 0; i < ssms.length; i++) {

				NGlycoSSM ssm = ssms[i];
				int peptideid = ssm.getPeptideid();
				IGlycoPeptide peptide = peps[peptideid];
				ssmlist.add(ssm);
				peplist.add(peptide);
			}
		}
		
		double[] glycoRtList = new double[peplist.size()];
		double[] pepRtList = new double[ssmlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = ssmlist.get(i).getRT();
			pepRtList[i] = peplist.get(i).getRetentionTime();
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();
//System.out.println(fit[0]+"\t"+fit[1]+"\t"+reg.getCoefficientOfDetermination());
		HashMap<String, HashSet<String>> namemap = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, String> contentmap = new HashMap<String, String>();
		HashMap<String, Double> scoremap = new HashMap<String, Double>();
		HashMap<String, Integer> uniquecountmap = new HashMap<String, Integer>();
		HashMap<String, String> sitemap = new HashMap<String, String>();
		HashMap<String, Integer> sitecountmap = new HashMap<String, Integer>();

		PrintWriter pw = new PrintWriter("H:\\NGlyco_final_20130730\\2D_4\\fuc.txt");
		
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			double delta = Math.abs(peprt - calrt);
			
			if (Math.abs(peprt - calrt) > 10)
				continue;

			pw.write((peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment())/peptide.getPepMrNoGlyco() * 1E6+"\n");
			
			String sequence = peptide.getSequence();
			if (countmap.containsKey(sequence)) {
				countmap.put(sequence, countmap.get(sequence) + 1);
				namemap.get(sequence).add(ssm.getGlycoTree().getIupacName());
			} else {
				countmap.put(sequence, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getIupacName());
				namemap.put(sequence, set);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refmap.get(sequence)).append("\t");
//System.out.println(deltaMzPPM);
			String key = sequence+ssm.getGlycoTree().getIupacName();
			if(scoremap.containsKey(key)){
				if(ssm.getScore()>scoremap.get(key)){
					scoremap.put(key, ssm.getScore());
					contentmap.put(key, sb.toString());
				}
				uniquecountmap.put(key, uniquecountmap.get(key)+1);
			}else{
				scoremap.put(key, ssm.getScore());
				contentmap.put(key, sb.toString());
				uniquecountmap.put(key, 1);
			}
			
			String sitekey = refmap.get(sequence)+"\t"+ssm.getGlycoTree().getIupacName();
			String sitevalue = sitekey+"\t"+ssm.getGlycoTree().getType()+"\t"+sitelocmap.get(refmap.get(sequence))+"\t";
			if(sitemap.containsKey(sitekey)){
				sitecountmap.put(sitekey, sitecountmap.get(sitekey)+1);
			}else{
				sitemap.put(sitekey, sitevalue);
				sitecountmap.put(sitekey, 1);
			}
		}
		pw.close();
		Iterator <String> it1 = pepmap.keySet().iterator();
		
		while(it1.hasNext()){
			
			String key = it1.next();
			IGlycoPeptide peptide = pepmap.get(key);
			String sequence = peptide.getSequence();
			String refinfo = refmap.get(sequence);
			StringBuilder sb = new StringBuilder();
			sb.append(refinfo).append("\t");
//			sb.append(peptide.getBaseName()).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(sequence)) {
				sb.append(countmap.get(sequence)).append("\t");
				sb.append(namemap.get(sequence).size());
			}
			
			writer.addContent(sb.toString(), 0, format);
		}
		
		Iterator <String> it2 = contentmap.keySet().iterator();
		
		while(it2.hasNext()){
			
			String key = it2.next();
			String content = contentmap.get(key);
			
			writer.addContent(content+uniquecountmap.get(key), 1, format);
		}
		
		Iterator <String> it3 = sitemap.keySet().iterator();
		while(it3.hasNext()){
			String key = it3.next();
			String content = sitemap.get(key);
			
			writer.addContent(content+sitecountmap.get(key), 2, format);
		}

		writer.close();
	}
	
	private static void combineXMLWithDraw(String in, String out, String dir) throws RowsExceededException, 
		WriteException, IOException, DocumentException{

		ExcelWriter writer = new ExcelWriter(out, new String[] {
				"Identified glycopeptides", "Matched glycopeptides" });
		ExcelFormat format = ExcelFormat.normalFormat;

		StringBuilder sb1 = new StringBuilder();
		sb1.append("Reference\t");
		sb1.append("Site\t");
		sb1.append("Scan\t");
		sb1.append("Sequence\t");
		sb1.append("Precursor MW\t");
		sb1.append("Charge\t");
		sb1.append("Retention time\t");
		sb1.append("Score\t");
		sb1.append("Matched glyco spectra count\t");
		sb1.append("Matched glyco type count\t");

		writer.addTitle(sb1.toString(), 0, format);

		StringBuilder sb2 = new StringBuilder();

		sb2.append("Glycopep scannum\t");
		// sb2.append("Rank\t");
		sb2.append("Glycopep rt\t");
		sb2.append("Precursor m/z\t");
		sb2.append("Precursor mw\t");
		sb2.append("Precursor charge\t");
		sb2.append("Theor glycan mw\t");
		sb2.append("Calc peptide mw\t");
		sb2.append("Glycopep score\t");
		sb2.append("IUPAC Name\t");
		sb2.append("Type\t");
		sb2.append("Peptide scannum\t");
		sb2.append("Sequence\t");
		sb2.append("Peptide rt\t");
		sb2.append("Theor peptide mw\t");
		sb2.append("Delta mw\t");
		sb2.append("Delta mw ppm\t");
		sb2.append("Reference\t");
		sb2.append("Site\t");

		writer.addTitle(sb2.toString(), 1, format);

		HashMap<String, IGlycoPeptide> pepmap = new HashMap<String, IGlycoPeptide>();
		HashMap<String, Double> psmap = new HashMap<String, Double>();
		HashMap<String, String> refmap = new HashMap<String, String>();

		ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();
		ArrayList<IGlycoPeptide> peplist = new ArrayList<IGlycoPeptide>();
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {

			if (!files[id].getName().endsWith("pxml"))
				continue;

			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			ProteinNameAccesser accesser = reader.getProNameAccesser();
			
			for(int i=0;i<peps.length;i++){

				IGlycoPeptide peptide = peps[i];
				GlycoSite[] sites = peptide.getAllGlycoSites();
				int[] loc = new int[sites.length];
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
				}
				HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

				StringBuilder refsb = new StringBuilder();
				StringBuilder sitesb = new StringBuilder();
				HashSet<ProteinReference> refset = peptide.getProteinReferences();
				for (ProteinReference ref : refset) {
					SimpleProInfo info = accesser.getProInfo(ref.getName());
					refsb.append(info.getRef()).append(";");

					SeqLocAround sla = slamap.get(ref.toString());
					for (int j = 0; j < loc.length; j++) {
						loc[j] = sites[j].modifLocation();
						sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
					}
					sitesb.deleteCharAt(sitesb.length() - 1);
					sitesb.append(";");
				}
				refsb.deleteCharAt(refsb.length() - 1);
				sitesb.deleteCharAt(sitesb.length() - 1);
				refsb.append("\t").append(sitesb);

				String sequence = peptide.getSequence();
				if (psmap.containsKey(sequence)) {
					if(peptide.getPrimaryScore()>psmap.get(sequence)){
						pepmap.put(sequence, peptide);
					}
				}else{
					psmap.put(sequence, (double)peptide.getPrimaryScore());
					pepmap.put(sequence, peptide);
					refmap.put(sequence, refsb.toString());
				}
			}

			for (int i = 0; i < ssms.length; i++) {

				NGlycoSSM ssm = ssms[i];
				int peptideid = ssm.getPeptideid();
				IGlycoPeptide peptide = peps[peptideid];
				ssmlist.add(ssm);
				peplist.add(peptide);
			}
		}
		
		double[] glycoRtList = new double[peplist.size()];
		double[] pepRtList = new double[ssmlist.size()];
		for (int i = 0; i < glycoRtList.length; i++) {
			glycoRtList[i] = ssmlist.get(i).getRT();
			pepRtList[i] = peplist.get(i).getRetentionTime();
		}

		Regression reg = new Regression(glycoRtList, pepRtList);
		reg.linear();
		double[] fit = reg.getBestEstimates();

		HashMap<String, HashSet<String>> namemap = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();
		HashMap<String, String> contentmap = new HashMap<String, String>();
		HashMap<String, Double> scoremap = new HashMap<String, Double>();
		HashMap<String, GlycoSpecMatchDataset> datasetmap = new HashMap<String, GlycoSpecMatchDataset>();
		
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			if (Math.abs(peprt - calrt) > 10)
				continue;

			String sequence = peptide.getSequence();
			if (countmap.containsKey(sequence)) {
				countmap.put(sequence, countmap.get(sequence) + 1);
				namemap.get(sequence).add(ssm.getGlycoTree().getIupacName());
			} else {
				countmap.put(sequence, 1);
				HashSet<String> set = new HashSet<String>();
				set.add(ssm.getGlycoTree().getIupacName());
				namemap.put(sequence, set);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			double deltaMz = peptide.getPepMrNoGlyco()
					- ssm.getPepMassExperiment();
			double deltaMzPPM = deltaMz / peptide.getPepMrNoGlyco() * 1E6;
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refmap.get(sequence)).append("\t");
			
			GlycoSpecMatchDataset dataset = new GlycoSpecMatchDataset(ssm.getScanNum());
			dataset.createDataset(ssm);

			String key = sequence+ssm.getGlycoTree().getIupacName();
			if(scoremap.containsKey(key)){
				if(ssm.getScore()>scoremap.get(key)){
					scoremap.put(key, ssm.getScore());
					contentmap.put(key, sb.toString());
					datasetmap.put(key, dataset);
				}
			}else{
				scoremap.put(key, ssm.getScore());
				contentmap.put(key, sb.toString());
				datasetmap.put(key, dataset);
			}
		}
		
		Iterator <String> it1 = pepmap.keySet().iterator();
		
		while(it1.hasNext()){
			
			String key = it1.next();
			IGlycoPeptide peptide = pepmap.get(key);
			String sequence = peptide.getSequence();
			String refinfo = refmap.get(sequence);
			StringBuilder sb = new StringBuilder();
			sb.append(refinfo).append("\t");
			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(peptide.getCharge()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPrimaryScore()).append("\t");
			if (countmap.containsKey(sequence)) {
				sb.append(countmap.get(sequence)).append("\t");
				sb.append(namemap.get(sequence).size());
			}
			
			writer.addContent(sb.toString(), 0, format);
		}
		
		Iterator <String> it2 = contentmap.keySet().iterator();
		int id = 0;
		
		while(it2.hasNext()){
			
			String key = it2.next();
			String content = contentmap.get(key);
			
			writer.addContent(content, 1, format);
			
			GlycoSpecMatchDataset dataset = datasetmap.get(key);
			BufferedImage spectrum = JFChartDrawer.createXYBarChart(dataset)
					.createBufferedImage(900, 600);
			
			ImageIO.write(spectrum, "png", new File(dir, String.valueOf(++id)+".png"));
		}

		writer.close();
	}
	
	private static void combine(String in, String out) throws IOException,
			JXLException {

		HashMap<String, String[]> contentmap1 = new HashMap<String, String[]>();
		HashMap<String, Double> scoremap1 = new HashMap<String, Double>();

		HashMap<String, String[]> contentmap2 = new HashMap<String, String[]>();
		HashMap<String, Double> scoremap2 = new HashMap<String, Double>();

		HashMap<String, HashSet<String>> glycotypemap = new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> countmap = new HashMap<String, Integer>();

		ExcelWriter writer = new ExcelWriter(out, new String[] {
				"Identified glycopeptides", "Matched glycopeptides" });
		ExcelFormat format = ExcelFormat.normalFormat;

		boolean t1 = false;
		boolean t2 = false;

		File[] files = (new File(in)).listFiles();
		for (int i = 0; i < files.length; i++) {

			if (!files[i].getName().endsWith("xls"))
				continue;

			ExcelReader reader = new ExcelReader(files[i],
					new int[] { 0, 1, 2 });
			String[] line = reader.readLine(0);
			String[] title = new String[line.length + 1];
			title[0] = "File";
			System.arraycopy(line, 0, title, 1, line.length);
			if (!t1) {
				writer.addTitle(title, 0, format);
				t1 = true;
			}

			while ((line = reader.readLine(0)) != null) {
				String[] content = new String[line.length + 1];
				content[0] = files[i].getName().substring(0,
						files[i].getName().length() - 4);
				System.arraycopy(line, 0, content, 1, line.length);
				String key = line[3];
				double score = Double.parseDouble(line[7]);
				if (scoremap1.containsKey(key)) {
					if (score > scoremap1.get(key)) {
						contentmap1.put(key, content);
						scoremap1.put(key, score);
					}
				} else {
					contentmap1.put(key, content);
					scoremap1.put(key, score);
				}
			}

			line = reader.readLine(1);
			title = new String[line.length + 1];
			title[0] = "File";
			System.arraycopy(line, 0, title, 1, line.length);

			if (!t2) {
				writer.addTitle(title, 1, format);
				t2 = true;
			}

			while ((line = reader.readLine(1)) != null) {

				String[] content = new String[line.length + 1];
				content[0] = files[i].getName().substring(0,
						files[i].getName().length() - 4);
				System.arraycopy(line, 0, content, 1, line.length);
				String key = line[8] + line[11];
				double score = Double.parseDouble(line[7]);
				if (scoremap2.containsKey(key)) {
					if (score > scoremap2.get(key)) {
						contentmap2.put(key, content);
						scoremap2.put(key, score);
					}
				} else {
					contentmap2.put(key, content);
					scoremap2.put(key, score);
				}

				if (glycotypemap.containsKey(line[11])) {
					glycotypemap.get(line[11]).add(line[8]);
					countmap.put(line[11], countmap.get(line[11]) + 1);
				} else {
					HashSet<String> set = new HashSet<String>();
					set.add(line[8]);
					glycotypemap.put(line[11], set);
					countmap.put(line[11], 1);
				}
			}
			reader.close();
		}

		Iterator<String> it1 = contentmap1.keySet().iterator();
		while (it1.hasNext()) {
			String key = it1.next();
			String[] content = contentmap1.get(key);
			if (countmap.containsKey(key)) {
				content[content.length - 2] = String.valueOf(countmap.get(key));
				content[content.length - 1] = String.valueOf(glycotypemap.get(
						key).size());
			}
			writer.addContent(content, 0, format);
		}

		Iterator<String> it2 = contentmap2.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			String[] content = contentmap2.get(key);
			writer.addContent(content, 1, format);
		}

		writer.close();
	}

	private static void read(String in) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();
		HashMap <String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
		while((line=reader.readLine())!=null){
			
			String ref = line[16];
			String site = line[17];
			String sequence = line[11];
			String name = line[8];
			
			String key = ref+site;
			if(map.containsKey(key)){
				map.get(key).add(name);
			}else{
				HashSet <String> set = new HashSet<String>();
				set.add(name);
				map.put(key, set);
			}
		}
		reader.close();
		System.out.println(map.size());
		
		HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();
		for(String key : map.keySet()){
			int count = map.get(key).size();
			if(countmap.containsKey(count)){
				countmap.put(count, countmap.get(count)+1);
			}else{
				countmap.put(count, 1);
			}
		}
		
		for(Integer count : countmap.keySet()){
			System.out.println(count+"\t"+countmap.get(count));
		}
	}
	
	private static void read(String in, String refs) throws IOException, JXLException{
		
		HashSet <String> refset = new HashSet <String>();
		BufferedReader br = new BufferedReader(new FileReader(refs));
		String s = null;
		while((s=br.readLine())!=null){
			refset.add(s);
		}
		br.close();
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();
		HashMap <String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
		while((line=reader.readLine())!=null){
			
			String ref = line[16];
			String site = line[17];
			String sequence = line[11];
			String name = line[8];
			
			String ipi = ref.substring(4, ref.indexOf("|"));
			if(!refset.contains(ipi)) continue;
			
			String key = ref+site;
			if(map.containsKey(key)){
				map.get(key).add(name);
			}else{
				HashSet <String> set = new HashSet<String>();
				set.add(name);
				map.put(key, set);
			}
		}
		reader.close();
		System.out.println(map.size());
		
		HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();
		for(String key : map.keySet()){
			int count = map.get(key).size();
			if(countmap.containsKey(count)){
				countmap.put(count, countmap.get(count)+1);
			}else{
				countmap.put(count, 1);
			}
		}
		
		for(Integer count : countmap.keySet()){
			System.out.println(count+"\t"+countmap.get(count));
		}
	}
	
	private static void readSite(String in) throws IOException, JXLException{
		int[] count0 = new int[2];
		HashMap<String, Boolean> fucmap = new HashMap<String, Boolean>();
		ExcelReader reader = new ExcelReader(in, 2);
		String[] line = reader.readLine();
		while((line=reader.readLine())!=null){
			String key = line[0].substring(0, line[0].indexOf("|"))+"_"+line[1];
			boolean value = line[3].contains("(Fuc-)GlcNAc-Asn");
			if(value) count0[0]++;
			count0[1]++;
			if(fucmap.containsKey(key)){
				if(value){
					if(!fucmap.get(key)){
						fucmap.put(key, value);
					}
				}
			}else{
				fucmap.put(key, value);
			}
		}
		reader.close();
		System.out.println(count0[0]+"\t"+count0[1]);
		int [] count = new int[2];
		for(String key : fucmap.keySet()){
//			System.out.println(key+"\t"+fucmap.get(key));
			if(fucmap.get(key)){
				count[0]++;
			}else{
				count[1]++;
			}
		}
		System.out.println(count[0]+"\t"+count[1]);
	}
	
	private static void testCopy(String in, String pngs, String out) throws IOException, JXLException{
		HashSet<String> set = new HashSet<String>();
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line =reader.readLine();
		while((line=reader.readLine())!=null){
			String key = line[0]+"_"+line[1];
			set.add(key);
		}
		reader.close();
		System.out.println(set.size());
		int count = 0;
		File[] files = (new File(pngs)).listFiles();
		for(int i=0;i<files.length;i++){
			String name = files[i].getName();
			name = name.substring(0, name.length()-4);
			if(!set.contains(name)){
				File nf = new File(out+"//"+name+".png");
				FileCopy.Copy(files[i], nf);
				count++;
			}
		}
		System.out.println(count);
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// String in =
		// "H:\\20130613_glyco_all\\2D\\Rui_20130604_HEK_HILIC_F1.pxml";
		// String out =
		// "H:\\20130613_glyco_all\\2D\\Rui_20130604_HEK_HILIC_F1.1.xls";
		// GlycoMatchXlsWriter2.test(in, out);
//		 GlycoMatchXlsWriter2.batchtest("H:\\NGLYCO\\NGlyco_final_20140401\\decoy_match");

//		GlycoMatchXlsWriter2.combineXML("H:\\NGLYCO\\NGlyco_final_20140408");
//		GlycoMatchXlsWriter2.combineXML("H:\\NGLYCO\\NGlyco_final_20140408",
//				"H:\\NGLYCO\\NGlyco_final_20140408\\20140430.name.all.xls");
		
//		GlycoMatchXlsWriter2.combineXMLComp("H:\\NGLYCO\\NGlyco_final_20140408",
//				"H:\\NGLYCO\\NGlyco_final_20140408\\20140430.comp.all.xls");
		
		GlycoMatchXlsWriter2.testCopy("D:\\P\\n-glyco\\2014.05.01\\Supplementary dataset.xls", 
				"H:\\NGLYCO\\NGlyco_final_20140408\\glycopeptide", "H:\\NGLYCO\\NGlyco_final_20140408\\filtered");
//		GlycoMatchXlsWriter2.combineXMLCompManual("H:\\NGLYCO\\NGlyco_final_20140408", 
//				"H:\\NGLYCO\\NGlyco_final_20140408\\20140430.test.xls", 
//				"D:\\P\\n-glyco\\2014.04.29\\20140409.xls");
		
//		GlycoMatchXlsWriter2.readSite("H:\\NGLYCO\\NGlyco_final_20140408\\20140430.comp.manual.xls");
		
//		GlycoMatchXlsWriter2.combineXML("H:\\NGlyco_final_20130730\\2D_4", 
//				"H:\\NGlyco_final_20130730\\final.RT10.2D.xls", "H:\\NGlyco_final_20130730\\Fuction\\TMHMM.xls");
		
//		GlycoMatchXlsWriter2.combineXML("H:\\NGlyco_final_20130725\\2D\\F1-F8");
		
//		GlycoMatchXlsWriter2.combineXMLWithDraw("H:\\NGlyco_final_20130730\\2D_4", 
//				"H:\\NGlycan_final_20130704\\All16_filtered_10.combine.xls", 
//				"H:\\NGlycan_final_20130704\\Spectra");
		
//		GlycoMatchXlsWriter2.read("H:\\NGlyco_final_20130725\\All16_filtered_10.combine.xls");
		
//		GlycoMatchXlsWriter2.read("H:\\NGlyco_final_20130725\\All16_filtered_10.combine.xls", 
//				"H:\\NGlyco_final_20130725\\TMHMM\\trans_2.txt");
		
	}

}
