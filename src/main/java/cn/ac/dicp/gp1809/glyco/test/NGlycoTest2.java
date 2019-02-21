/* 
 ******************************************************************************
 * File: NGlycoTest2.java * * * Created on 2013-6-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.test;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;

import jxl.JXLException;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFFeasXMLReader2;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoDatabaseReader;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaWriter;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import flanagan.analysis.Regression;

/**
 * @author ck
 * 
 * @version 2013-6-6, 9:01:50
 */
public class NGlycoTest2 {

	private static void ABPeptideListReader(String in, double scorethres)
			throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = reader.readLine();
		String[] title = line.split("\t");
		int confid = -1;
		int sequenceid = -1;
		int modid = -1;
		int theormwid = -1;

		for (int i = 0; i < title.length; i++) {
			if (title[i].equals("Conf")) {
				confid = i;
			}
			if (title[i].equals("Sequence")) {
				sequenceid = i;
			}
			if (title[i].equals("Modifications")) {
				modid = i;
			}
			if (title[i].equals("Theor MW")) {
				theormwid = i;
			}
		}

		HashSet<Double> set = new HashSet<Double>();
		HashSet<String> modset = new HashSet<String>();
		ArrayList<Double> list = new ArrayList<Double>();
		while ((line = reader.readLine()) != null) {
			String[] cs = line.split("\t");
			double conf = Double.parseDouble(cs[confid]);
			String sequence = cs[sequenceid];
			String mod = cs[modid];
			double theormw = Double.parseDouble(cs[theormwid]);

			if (conf < scorethres)
				continue;
			if (!mod.contains("Deamidated(N)"))
				continue;

			String[] mods = mod.split(";[\\W]*");
			for (int i = 0; i < mods.length; i++) {
				mods[i] = mods[i].split("[@\\(]")[0];
			}
			Arrays.sort(mods);

			String modNoSite = "";
			for (int i = 0; i < mods.length; i++) {
				modNoSite += mods[i];
			}

			set.add(theormw);
			if (!modset.contains(sequence + modNoSite)) {
				modset.add(sequence + modNoSite);
				list.add(theormw);
				System.out
						.println(sequence + "\t" + modNoSite + "\t" + theormw);
			}
		}

		Double[] mws = list.toArray(new Double[list.size()]);
		Arrays.sort(mws);
		System.out.println(mws.length + "\t" + modset.size() + "\t"
				+ list.size());
		reader.close();

		double ppm = 20;
		int count = 0;
		for (int i = 0; i < mws.length; i++) {
			if (i == 0) {
				if (mws[i + 1] - mws[i] < mws[i] * ppm * 1E-6) {
					count++;
				}
			} else if (i == mws.length - 1) {
				if (mws[i] - mws[i - 1] < mws[i] * ppm * 1E-6) {
					count++;
				}
			} else {
				if (mws[i + 1] - mws[i] < mws[i] * ppm * 1E-6
						|| mws[i] - mws[i - 1] < mws[i] * ppm * 1E-6) {
					count++;
					System.out.println(mws[i]);
				}
			}
		}
		System.out.println(count);
	}

	private static void testPeptideDiff(String in) throws IOException,
			JXLException {
		ExcelReader reader = new ExcelReader(in);
		String[] column = reader.getColumn(3);
		double[] mzs = new double[column.length - 1];
		for (int i = 0; i < mzs.length; i++) {
			mzs[i] = Double.parseDouble(column[i + 1]);
		}

		for (int ppmid = 0; ppmid < 20; ppmid++) {
			int count = 0;
			double ppm = (ppmid + 1) * 10;
			for (int i = 0; i < mzs.length; i++) {
				if (i == 0) {
					if (Math.abs(mzs[i + 1] - mzs[i]) < mzs[i] * ppm * 1E-6) {
						count++;
					}
				} else if (i == mzs.length - 1) {
					if (Math.abs(mzs[i] - mzs[i - 1]) < mzs[i] * ppm * 1E-6) {
						count++;
					}
				} else {
					if (Math.abs(mzs[i + 1] - mzs[i]) < mzs[i] * ppm * 1E-6
							|| Math.abs(mzs[i + 1] - mzs[i]) < mzs[i] * ppm
									* 1E-6) {
						count++;
					}
				}
			}
			System.out.println(ppm + "\t" + count + "\t" + mzs.length + "\t"
					+ (double) count / (double) mzs.length);
		}

	}

	private static void getSiteInfo(String dir) throws IOException,
			JXLException {

		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
		File[] files = (new File(dir)).listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith("xls")) {
				System.out.println(files[i].getName());
				ExcelReader reader = new ExcelReader(files[i], 1);
				String[] line0 = reader.readLine();

				while ((line0 = reader.readLine()) != null) {
					String ref = line0[16].substring(0, line0[16].indexOf("|"));
					String site = line0[17];
					String glycan = line0[8];

					String key = ref + "_" + site;
					if (map.containsKey(key)) {
						map.get(key).add(glycan);
					} else {
						HashSet<String> set = new HashSet<String>();
						set.add(glycan);
						map.put(key, set);
					}
				}
			}
		}

		int count = 0;
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			HashSet<String> set = map.get(key);
			count += set.size();
		}

		System.out.println(map.size() + "\t" + count);
	}

	private static void fdrtest(String glyco, String deglyco)
			throws IOException, JXLException {

		ExcelReader gr = new ExcelReader(glyco);
		String[] grscoreline = gr.getColumn(6);
		double[] grscore = new double[grscoreline.length - 1];
		for (int i = 1; i < grscoreline.length; i++) {
			grscore[i - 1] = Double.parseDouble(grscoreline[i]);
		}
		gr.close();

		ExcelReader dgr = new ExcelReader(deglyco);
		String[] dgrscoreline = dgr.getColumn(6);
		double[] dgrscore = new double[dgrscoreline.length - 1];
		for (int i = 1; i < dgrscoreline.length; i++) {
			dgrscore[i - 1] = Double.parseDouble(dgrscoreline[i]);
		}
		dgr.close();

		Arrays.sort(grscore);
		Arrays.sort(dgrscore);

		for (int i = 1; i <= 15; i++) {
			int i1 = Arrays.binarySearch(grscore, (double) i);
			if (i1 < 0)
				i1 = -i1 - 1;

			int i2 = Arrays.binarySearch(dgrscore, (double) i);
			if (i2 < 0)
				i2 = -i2 - 1;

			int target = grscore.length - i1;
			int decoy = dgrscore.length - i2;
			double fdr = (double) decoy / (double) target;
			System.out.println(i + "\t" + target + "\t" + decoy + "\t" + fdr);
		}
	}

	private static void highComplexTest(String in) throws IOException,
			JXLException {

		ExcelReader reader = new ExcelReader(in, new int[] { 0, 1 });
		String[] sequences1 = reader.getColumn(0, 3);
		// System.out.println(column.length);

		String[] sequences2 = reader.getColumn(1, 11);
		String[] types = reader.getColumn(1, 9);

		HashMap<String, Integer> map0 = new HashMap<String, Integer>();
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		for (int i = 1; i < sequences2.length; i++) {
			if (map0.containsKey(sequences2[i])) {
				if (types[i].equals("High mannose")) {
					map0.put(sequences2[i], map0.get(sequences2[i])+1);
				} else {
					map1.put(sequences2[i], map1.get(sequences2[i])+1);
				}
			} else {
				if (types[i].equals("High mannose")) {
					map0.put(sequences2[i], 1);
					map1.put(sequences2[i], 0);
				} else {
					map0.put(sequences2[i], 0);
					map1.put(sequences2[i], 1);
				}
			}
		}

		DecimalFormat df4 = DecimalFormats.DF0_4;
		for (int i = 1; i < sequences1.length; i++) {
			if (map0.containsKey(sequences1[i])) {
				int count0 = map0.get(sequences1[i]);
				int count1 = map1.get(sequences1[i]);
				System.out.println(count0
						+ "\t"
						+ df4.format((double) count0
								/ (double) (count0 + count1))
						+ "\t"
						+ count1
						+ "\t"
						+ df4.format((double) count1
								/ (double) (count0 + count1)));
			}else{
				System.out.println();
			}
		}

		reader.close();
	}
	
	private static void noMotifTest(String in) throws IOException, JXLException{
		
		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		int t = 0;
		int d = 0;
		int target = 0;
		int decoy = 0;
		int targetSP = 0;
		int decoySP = 0;
		ExcelReader reader = new ExcelReader(in);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			Matcher matcher = N_GLYCO.matcher(line[3]);
			if(matcher.find()){
				t++;
				if(line.length==10){
					int count = Integer.parseInt(line[8]);
					target++;
					targetSP+=count;
				}
			}else{
				d++;
				if(line.length==10){
					int count = Integer.parseInt(line[8]);
					decoy++;
					decoySP+=count;
				}
			}
		}
		System.out.println(t+"\t"+d+"\t"+target+"\t"+decoy+"\t"+targetSP+"\t"+decoySP);
		reader.close();
	}
	
	private static void glycanTypeStat(String in) throws DocumentException{

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
		
		for (int i = 0; i < ssmlist.size(); i++) {

			NGlycoSSM ssm = ssmlist.get(i);
			IGlycoPeptide peptide = peplist.get(i);
			double peprt = peptide.getRetentionTime();
			double calrt = fit[0] + fit[1] * ssm.getRT();

			if (Math.abs(peprt - calrt) > 20)
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
			sb.append(refmap.get(sequence)).append("\t");

			String key = sequence+ssm.getGlycoTree().getIupacName();
			if(scoremap.containsKey(key)){
				if(ssm.getScore()>scoremap.get(key)){
					scoremap.put(key, ssm.getScore());
					contentmap.put(key, sb.toString());
				}
			}else{
				scoremap.put(key, ssm.getScore());
				contentmap.put(key, sb.toString());
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
			
		}
		
		Iterator <String> it2 = contentmap.keySet().iterator();
		
		while(it2.hasNext()){
			
			String key = it2.next();
			String content = contentmap.get(key);
			
		}
	}

	private static void glycanTypeStat2(String in) throws IOException, DocumentException{
		
		int matchedHigh = 0;
		int matchComp = 0;
		int unMatchedHigh = 0;
		int unMatchedComp = 0;
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {

			if (!files[id].getName().endsWith("pxml"))
				continue;

			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			NGlycoSSM[] ssms2 = reader.getUnmatchedGlycoSpectra();
			
			for(int i=0;i<ssms.length;i++){
				
				NGlycoSSM ssm = ssms[i];
				String name = ssm.getName();
				double score = ssm.getScore();
				
			}

			for(int i=0;i<ssms2.length;i++){
				
				NGlycoSSM ssm = ssms2[i];
				String name = ssm.getName();
				double score = ssm.getScore();
				
			}
			reader.close();
		}
	}
	
	private static void neuGcTest(String in) throws IOException, DocumentException{
		
		File[] files = (new File(in)).listFiles();
		for (int id = 0; id < files.length; id++) {

			if (!files[id].getName().endsWith("pxml"))
				continue;

			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(files[id]);
			NGlycoSSM[] ssms = reader.getMatchedGlycoSpectra();
			NGlycoSSM[] ssms2 = reader.getUnmatchedGlycoSpectra();
			
			for(int i=0;i<ssms2.length;i++){
				
				NGlycoSSM ssm = ssms2[i];
				String name = ssm.getName();
				double score = ssm.getScore();
				if(score>10 && name.contains("NeuGc")){
					
					StringBuilder sb = new StringBuilder();
					sb.append(files[id].getName()).append("\t");
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
					
					System.out.println(sb);
				}
			}
			reader.close();
		}
	}
	
	private static void proteinSeuqnce(String in, String out, String fasta) throws IOException, JXLException{
		
		HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			psmap.put(ps.getReference(), ps);
		}
		fr.close();
		
		HashSet<String> set = new HashSet<String>();
		/*ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			set.add(line[16]);
		}
		reader.close();*/
		
		ExcelReader reader = new ExcelReader(in, 0);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			if(line.length==8) {
				if(set.add(line[0])){
					System.out.println(line[0].substring(4, 15));
				}
			}
		}
		
		
System.out.println(set.size());		
		FastaWriter writer = new FastaWriter(out);
		for(String ref : set){
			ProteinSequence proseq = psmap.get(ref);
			writer.write(proseq);
		}
		writer.close();
	}
	
	private static void getIPI(String in, String out) throws IOException{
		PrintWriter pw = new PrintWriter(out);
		FastaReader fr = new FastaReader(in);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			pw.write(ref.substring(4, ref.indexOf("|"))+"\n");
		}
		fr.close();
		pw.close();
	}
	
	private static void getGene(String in, String out) throws IOException{
		
		HashSet <String> set = new HashSet <String>();
		PrintWriter pw = new PrintWriter(out);
		FastaReader fr = new FastaReader(in);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String gene = ps.getGene();
			if(gene.length()>1){
				if(!set.contains(gene)){
					pw.write(gene+"\n");
					set.add(gene);
				}
			}
		}
		fr.close();
		pw.close();
	}
	
	private static void getSite(String ref, String result) throws IOException, JXLException{
		
		HashSet <String> set = new HashSet <String>();
		BufferedReader reader = new BufferedReader(new FileReader(ref));
		String line = null;
		while((line=reader.readLine())!=null){
			set.add("IPI00"+line);
		}
		reader.close();
		
		HashSet <String> usedset = new HashSet <String>();
		ExcelReader er = new ExcelReader(result, 2);
		String [] cs = er.readLine();
		while((cs=er.readLine())!=null){
			String ipi = cs[0].substring(4, 15);
			if(set.contains(ipi)){
				String content = cs[0]+"\t"+cs[1]+"\t"+cs[cs.length-1];
				usedset.add(content);
			}
			if(ipi.equals("IPI00022462")){
				System.out.println("Glycan\t"+cs[2]);
			}
		}
		er.close();
		
		String [] contents = usedset.toArray(new String[usedset.size()]);
		Arrays.sort(contents);
		for(int i=0;i<contents.length;i++){
//			System.out.println(contents[i]);
		}
	}
	
	private static void typeTest(String in) throws IOException, JXLException{
		
		HashMap<String, int []> map = new HashMap<String, int []>();
		ExcelReader reader = new ExcelReader(in, 2);
		String [] line = reader.readLine();
		int [] totalcount = new int[3];
		while((line=reader.readLine())!=null){
			String name = line[2];
			String tmh = line[4];
			if(map.containsKey(name)){
				int [] count = map.get(name);
				if(tmh.equals("Non-transmembrane protein, outside")){
					count[0]++;
					totalcount[0]++;
				}else if(tmh.equals("Transmembrane protein, inside")){
					count[1]++;
					totalcount[1]++;
				}else if(tmh.equals("Transmembrane protein, outside")){
					count[2]++;
					totalcount[2]++;
				}
				map.put(name, count);
			}else{
				int [] count = new int [3];
				if(tmh.equals("Non-transmembrane protein, outside")){
					count[0]++;
					totalcount[0]++;
				}else if(tmh.equals("Transmembrane protein, inside")){
					count[1]++;
					totalcount[1]++;
				}else if(tmh.equals("Transmembrane protein, outside")){
					count[2]++;
					totalcount[2]++;
				}
				map.put(name, count);
			}
		}
		reader.close();
		System.out.println(totalcount[0]+"\t"+totalcount[1]+"\t"+totalcount[2]);
		for(String name : map.keySet()){
			int [] count = map.get(name);
			System.out.println("Glycan\t"+name+"\t"+count[0]+"\t"+count[1]+"\t"+count[2]+"\t"+MathTool.getTotal(count));
		}
	}
	
	private static void typeTestFuc(String in) throws IOException, JXLException{
		
		HashMap<String, int []> map = new HashMap<String, int []>();
		ExcelReader reader = new ExcelReader(in, 2);
		String [] line = reader.readLine();
		int [] totalcount = new int[3];
		while((line=reader.readLine())!=null){
			String name = line[2];
			String tmh = line[4];
			if(map.containsKey(name)){
				int [] count = map.get(name);
				if(tmh.equals("Non-transmembrane protein, outside")){
					count[0]++;
					totalcount[0]++;
				}else if(tmh.equals("Transmembrane protein, inside")){
					count[1]++;
					totalcount[1]++;
				}else if(tmh.equals("Transmembrane protein, outside")){
					count[2]++;
					totalcount[2]++;
				}
				map.put(name, count);
			}else{
				int [] count = new int [3];
				if(tmh.equals("Non-transmembrane protein, outside")){
					count[0]++;
					totalcount[0]++;
				}else if(tmh.equals("Transmembrane protein, inside")){
					count[1]++;
					totalcount[1]++;
				}else if(tmh.equals("Transmembrane protein, outside")){
					count[2]++;
					totalcount[2]++;
				}
				map.put(name, count);
			}
		}
		reader.close();
		System.out.println(totalcount[0]+"\t"+totalcount[1]+"\t"+totalcount[2]);
		for(String name : map.keySet()){
			int [] count = map.get(name);
//			System.out.println("Glycan\t"+name+"\t"+count[0]+"\t"+count[1]+"\t"+count[2]+"\t"+MathTool.getTotal(count));
		}
		
		GlycoDatabaseReader dbreader = new GlycoDatabaseReader();
		GlycoTree[] trees = dbreader.getUnits();
		
		HashMap<String, String> fucmap = new HashMap<String, String>();
		HashMap<String, String> nofucmap = new HashMap<String, String>();
		HashMap<String, String> keymap = new HashMap<String, String>();
		for(int i=0;i<trees.length;i++){
			String name = trees[i].getIupacName();
			if(map.containsKey(name)){
				int [] composition = trees[i].getComposition();
				if(composition[18]>0){
					int [] c2 = new int[20];
					System.arraycopy(composition, 0, c2, 0, composition.length);
					c2[18] = 0;
					String k1 = Arrays.toString(composition);
					String k2 = Arrays.toString(c2);
					fucmap.put(k1, name);
					nofucmap.put(k2, null);
					keymap.put(k1, k2);
				}
			}
		}
		
		int count = 0;
		for(int i=0;i<trees.length;i++){
			String name2 = trees[i].getIupacName();
			if(map.containsKey(name2)){
				int [] composition = trees[i].getComposition();
				String key = Arrays.toString(composition);
				if(nofucmap.containsKey(key)){
					nofucmap.put(key, name2);
					Iterator <String> it = keymap.keySet().iterator();
					
					while(it.hasNext()){
						String k1 = it.next();
						String k2 = keymap.get(k1);
						String name1 = fucmap.get(k1);
						if(key.equals(k2)){
							
							int [] count1 = map.get(name1);
							int [] count2 = map.get(name2);
							
							System.out.println(name1+"\t"+count1[0]+"\t"+count1[1]+"\t"+count1[2]+"\t"
									+name2+"\t"+count2[0]+"\t"+count2[1]+"\t"+count2[2]);
						}
					}
				}
			}
		}
		
//		System.out.println(fucmap.size()+"\t"+count);
	}

	private static void select(String in, String ipi) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 2);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			if(line[0].startsWith(ipi)){
//				System.out.println("Glycan\t"+line[2]);
				for(int i=0;i<line.length;i++){
					System.out.print(line[i]+"\t");
				}
				System.out.println();
			}
		}
		reader.close();
	}
	
	private static void cao(String ppl, String xls) throws PeptideParsingException, FileDamageException, IOException, JXLException{
		
		HashMap<String, String>map = new HashMap<String, String>();
		HashMap<String, Float> map2 = new HashMap<String, Float>();
		IPeptideListReader pr = new PeptideListReader(ppl);
		IPeptide peptide = null;
		while((peptide=pr.getPeptide())!=null){
			if(peptide.getPrimaryScore()<15) continue;
			String scanname = peptide.getBaseName();
			String oriseq = peptide.getSequence();
			String sequence = oriseq.substring(2, oriseq.length()-2);
			sequence = sequence.replaceAll("#", "[Gal-GalNAc]");
			sequence = sequence.replaceAll("@", "[NeuAc-Gal-GalNAc]");
			sequence = sequence.replaceAll("\\$", "[NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc]");
			sequence = sequence.replaceAll("\\*", "");
			sequence = sequence.replaceAll("~", "[NeuAc-Gal-(Gal-GlcNAc-)GalNAc]");
			sequence = sequence.replaceAll("\\^", "[NeuAc-Gal-(NeuAc-)GalNAc]");
			if(sequence.length()-oriseq.length()>5){
				map.put(scanname, sequence);
				map2.put(scanname, peptide.getPrimaryScore());
//				System.out.println(oriseq+"\t"+sequence);
			}
		}
		pr.close();
		System.out.println(map.size());
		
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		ExcelReader er = new ExcelReader(xls);
		String [] line = er.readLine();
		while((line=er.readLine())!=null){
			if(map.containsKey(line[0])){
				count1++;
				String sequence = line[6];
				if(sequence.equals(map.get(line[0]))){
					count2++;
					System.out.println(Double.parseDouble(line[2])-map2.get(line[0]));
				}
			}
		}
		System.out.println(count1+"\t"+count2);
		er.close();
	}
	
	private static void struc2comp(String in, String out) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, new int[]{0, 1, 2});
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;

		HashMap<String, String[]> map2 = new HashMap<String, String[]>();
		HashMap<String, Integer> count2 = new HashMap<String, Integer>();
		HashMap<String, HashSet<String>> map4 = new HashMap<String, HashSet<String>>();
		String [] line = reader.readLine(1);
		writer.addTitle(line, 1, format);
		while((line=reader.readLine(1))!=null){
			String struc = line[9];
			if(line[9].contains("Xyl")) continue;
			if(line[9].equals("NeuAc-Gal-GlcN-Man-(Gal-GlcNAc-Man-)Man-GlcNAc-GlcNAc-Asn")) continue;
			String comp = struc2comp(struc);
			line[9] = comp;
			String key = line[9]+line[12];
			if(map2.containsKey(key)){
				count2.put(key, count2.get(key)+Integer.parseInt(line[19]));
			}else{
				map2.put(key, line);
				count2.put(key, Integer.parseInt(line[19]));
			}
			String key2 = line[12]+line[18];
			if(map4.containsKey(key2)){
				map4.get(key2).add(comp);
			}else{
				HashSet<String> set = new HashSet<String>();
				set.add(comp);
				map4.put(key2, set);
			}
		}
		Iterator<String> it2 = map2.keySet().iterator();
		while(it2.hasNext()){
			String key = it2.next();
			String [] content = map2.get(key);
			content[19] = String.valueOf(count2.get(key));
			writer.addContent(content, 1, format);
		}
		
		HashMap<String, String[]> map3 = new HashMap<String, String[]>();
		HashMap<String, Integer> count3 = new HashMap<String, Integer>();
		
		line = reader.readLine(2);
		writer.addTitle(line, 2, format);
		while((line=reader.readLine(2))!=null){
			String struc = line[2];
			if(line[2].contains("Xyl")) continue;
			if(line[2].equals("NeuAc-Gal-GlcN-Man-(Gal-GlcNAc-Man-)Man-GlcNAc-GlcNAc-Asn")) continue;
			String comp = struc2comp(struc);
			line[2] = comp;
			String key = line[0]+line[1]+line[2];
			if(map3.containsKey(key)){
				count3.put(key, count3.get(key)+Integer.parseInt(line[4]));
			}else{
				map3.put(key, line);
				count3.put(key, Integer.parseInt(line[4]));
			}
			String key2 = line[0]+line[1];
			if(map4.containsKey(key2)){
				map4.get(key2).add(line[2]);
			}else{
				HashSet<String> set = new HashSet<String>();
				set.add(line[2]);
				map4.put(key2, set);
			}
		}
		
		Iterator<String> it3 = map3.keySet().iterator();
		while(it3.hasNext()){
			String key = it3.next();
			String [] content = map3.get(key);
			content[4] = String.valueOf(count3.get(key));
			writer.addContent(content, 2, format);
		}
		
		line = reader.readLine(0);
		writer.addTitle(line, 0, format);
		while((line=reader.readLine(0))!=null){
			String key = line[3]+line[1];
			if(map4.containsKey(key)){
				line[9] = String.valueOf(map4.get(key).size());
				writer.addContent(line, 0, format);
			}else{
				writer.addContent(line, 0, format);
			}
		}
		
		writer.close();
	}
	
	private static String struc2comp(String struc){
		int [] count = new int[4];
		String [] units = struc.split("[-()]");
		for(int i=0;i<units.length;i++){
			if(units[i].length()>0){
				if(units[i].equals("Gal") || units[i].equals("Glc") || units[i].equals("Man")){
					count[0]++;
				}else if(units[i].equals("GalNAc") || units[i].equals("GlcNAc") || units[i].equals("ManNAc")){
					count[1]++;
				}else if(units[i].equals("Fuc")){
					count[2]++;
				}else if(units[i].equals("NeuAc")){
					count[3]++;
				}else if(units[i].equals("Asn")){

				}else{
					System.out.println("967\t"+units[i]+"\n"+struc);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("HexNAc(").append(count[1]).append(")");
		sb.append("Hex(").append(count[0]).append(")");
		if(count[3]>0)
			sb.append("NeuAc(").append(count[3]).append(")");
		if(count[2]>0)
			sb.append("Fuc(").append(count[2]).append(")");

		return sb.toString();
	}
	
	private static void struc2comp2(String in, String out) throws IOException, JXLException{
		ExcelWriter writer = new ExcelWriter(out, new String[]{"1", "2", "3", "4"});
		ExcelFormat format = ExcelFormat.normalFormat;
		ExcelReader reader = new ExcelReader(in, new int[]{0, 1, 2, 3});
		for(int i=0;i<4;i++){
			String[] line = null;
			while((line=reader.readLine(i))!=null){
				line[2] = struc2comp(line[2]);
				writer.addContent(line, i, format);
			}
		}
		writer.close();
		reader.close();
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws JXLException
	 * @throws DocumentException 
	 * @throws FileDamageException 
	 * @throws PeptideParsingException 
	 */
	public static void main(String[] args) throws IOException, JXLException, DocumentException, PeptideParsingException, FileDamageException {
		// TODO Auto-generated method stub

		// String in =
		// "H:\\20130519_glyco\\iden\\AB\\Centroid_Rui_20130515_fetuin_HILIC_deglyco_HCD_AB_PeptideSummary.txt";
		// NGlycoTest2.ABPeptideListReader(in, 95);

		// NGlycoTest2
		// .testPeptideDiff("H:\\20130613_glyco_all\\2D\\Rui_20130604_HEK_HILIC_F1.xls");

		// NGlycoTest2.getSiteInfo("H:\\20130613_glyco_all\\2D");

		// NGlycoTest2.fdrtest("H:\\20130613_glyco_all\\2D\\test\\top5\\Rui_20130604_HEK_HILIC_F2.idenall.xls",
		// "H:\\20130613_glyco_all\\2D\\test\\top5\\Rui_20130604_HEK_HILIC_F2_deglyco.idenall.xls");

//		NGlycoTest2
//				.highComplexTest("H:\\NGlycan_final_20130704\\All16_filtered_20.combine.xls");
		
//		NGlycoTest2.noMotifTest("H:\\NGlycan_final_20130704\\F1-F8_allN_match\\Rui_20130604_HEK_HILIC_F8.xls"); 
		
//		NGlycoTest2.glycanTypeStat2("H:\\NGlycan_final_20130704\\All16_target_iden.xls");
		
//		NGlycoTest2.neuGcTest("H:\\NGlycan_final_20130704\\2D");
		
//		NGlycoTest2.proteinSeuqnce("H:\\NGlyco_final_20130730\\RT10.2D.xls", 
//				"H:\\NGlyco_final_20130730\\Fuction\\protein.fasta", 
//				"F:\\DataBase\\ipi.HUMAN.v3.80\\Final_ipi.HUMAN.v3.80.fasta");
//		NGlycoTest2.getGene("H:\\NGlyco_final_20130730\\Fuction\\protein.fasta", 
//				"H:\\NGlyco_final_20130730\\Fuction\\gene.txt");
		
//		NGlycoTest2.getSite("H:\\NGlyco_final_20130730\\Fuction\\nuclear mambrane ipi.txt", 
//				"H:\\NGlyco_final_20130730\\RT10.2D.xls");
		
//		NGlycoTest2.typeTestFuc("H:\\NGlyco_final_20130730\\final.RT10.2D.xls");
		
		NGlycoTest2.select("D:\\P\\n-glyco\\2014.05.01\\20140430.comp.manual.xls", "IPI:IPI00289819.5");
		
//		NGlycoTest2.proteinSeuqnce("H:\\NGlyco_final_20130730\\RT10.2D.xls", 
//				"H:\\NGlyco_final_20130730\\Fuction\\no_glycan_protein.fasta", 
//				"F:\\DataBase\\ipi.HUMAN.v3.80\\Final_ipi.HUMAN.v3.80.fasta");
		
//		NGlycoTest2.cao("H:\\OGlycan_final_20131120\\20131121_NQ\\fetuin.oglycan.type5.F006030.dat.ppl", 
//				"H:\\OGlycan_final_20131120\\20131121_NQ\\combine.oglycan.F005985.xls");
//		NGlycoTest2.struc2comp("H:\\NGLYCO\\NGlyco_final_20140408\\20140430.match.xls", 
//				"H:\\NGLYCO\\NGlyco_final_20140408\\20140430.comp.xls");
//		NGlycoTest2.struc2comp2("H:\\NGlyco_final_20130730\\20130909\\Strucutre drawing.xls", 
//				"H:\\NGlyco_final_20130730\\20130909\\Strucutre drawing 20140115.xls");
	}

}
