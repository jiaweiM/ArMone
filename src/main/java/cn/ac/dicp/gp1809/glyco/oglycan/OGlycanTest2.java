/* 
 ******************************************************************************
 * File: OGlycanTest2.java * * * Created on 2013-6-18
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import jxl.JXLException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-6-18, 8:41:45
 */
public class OGlycanTest2 {
	
	private static final double tolerance = 0.1;
	private static final String lineSeparator = IOConstant.lineSeparator;
	
	private static void deGlycoPeak(String in, String out) throws DtaFileParsingException, IOException{
		
		File [] files = (new File(in)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("mgf"))
					return true;
				return false;
			}
			
		});
		
		Arrays.sort(files);
		
		FileWriter writer = new FileWriter(out);
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());
			deGlycoPeak(files[i].getAbsolutePath(), out + "." + (i + 1)
					+ ".temp", (i + 1));
			FileReader reader = new FileReader(out + "." + (i + 1) + ".temp");
			int flag = 0;
			while ((flag = reader.read()) != -1) {
				writer.write(flag);
			}
			reader.close();
			File fi = new File(out + "." + (i + 1) + ".temp");
			fi.delete();
		}
		writer.close();
	}
	
	private static void deGlycoPeak(String in, String out, int fileid) throws DtaFileParsingException, FileNotFoundException{
		
		PrintWriter writer = new PrintWriter(out);
		
		MgfReader reader = new MgfReader(in);
		MS2Scan ms2scan = null;
		while((ms2scan=reader.getNextMS2Scan())!=null){
			
			IMS2PeakList peaklist = ms2scan.getPeakList();
			PrecursePeak pp = peaklist.getPrecursePeak();
			double mh = pp.getMH()-AminoAcidProperty.PROTON_W;
			double mz = pp.getMz();
			short charge = pp.getCharge();
			String name = ms2scan.getScanName().getScanName();
			if(name.endsWith(", ")) name = name.substring(0, name.length()-2);
			String newname = name.replace("1.1.1", "1.1."
					+ ((fileid-1)*2 + 1));
			
			double intenthres1 = 0;
			double intenthres2 = 0;
/*			
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
*/
			StringBuilder sb = new StringBuilder();
			sb.append("BEGIN IONS"+lineSeparator);
			sb.append("PEPMASS="+mz+lineSeparator);
			sb.append("CHARGE="+charge+"+"+lineSeparator);
			sb.append("TITLE="+newname+lineSeparator);

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
				continue;
			}

			if (highIntenList.size() <= 5)
				continue;
			
			for(int i=0;i<highIntenList.size();i++){
				IPeak peaki = highIntenList.get(i);
				sb.append(peaki.getMz()+"\t"+peaki.getIntensity()+lineSeparator);
			}
			sb.append("END IONS"+lineSeparator);
			writer.write(sb.toString());
		}
		
		reader.close();
		writer.close();
	}
	
	private static void compare(String type1, String all, String my) throws FileDamageException, IOException, JXLException{
		
		HashMap <String, String> map = new HashMap <String, String>();
		PeptideListReader r1 = new PeptideListReader(type1);
		IPeptide p1 = null;
		while((p1=r1.getPeptide())!=null){
			String scanname = p1.getBaseName();
			map.put(scanname, p1.getSequence());
		}
		r1.close();
		
		HashMap <String, String[]> map2 = new HashMap <String, String[]>();
		ExcelReader er = new ExcelReader(my);
		String [] line2 = er.readLine();
		while((line2=er.readLine())!=null){
			map2.put(line2[0], line2);
		}
		er.close();
		
		int commonscan = 0;
		int commonseq = 0;
		int other = 0;
		
		PeptideListReader r2 = new PeptideListReader(all);
		IPeptide p2 = null;
		while((p2=r2.getPeptide())!=null){
			String scanname = p2.getBaseName();
			String sequence = p2.getSequence();
			if(map.containsKey(scanname)){
				commonscan++;
				if(map.get(scanname).equals(sequence)){
					commonseq++;
				}
			}else{
				if(sequence.contains("#")){
					if(map2.containsKey(scanname)){
						System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr()
								+"\t"+map2.get(scanname)[4]+"\t"+map2.get(scanname)[2]);
					}else{
						System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr());
					}
					other++;
				}
			}
		}
		r2.close();
		
		System.out.println(r1.getNumberofPeptides()+"\t"+r2.getNumberofPeptides()+"\t"+commonscan+"\t"+commonseq+"\t"+other);
	}

	private static void compareType1(String allppl, String pepinfo) throws IOException, FileDamageException{
		
		HashMap <String, Double> map = new HashMap <String, Double>();
		BufferedReader inforeader = new BufferedReader(new FileReader(pepinfo));
		String pline = null;
L:		while((pline=inforeader.readLine())!=null){
			String [] cs = pline.split("\t");
			int tt = Integer.parseInt(cs[0]);
			int count = Integer.parseInt(cs[1]);
			if(tt==1){
				map.put(cs[cs.length-2], Double.parseDouble(cs[cs.length-3]));
			}else if(tt==5){
				for(int i=0;i<count;i++){
					if(!cs[2+i*2].equals("core1_4") && !cs[2+i*2].equals("core2_5")){
						continue L;
					}
				}
				map.put(cs[cs.length-2], Double.parseDouble(cs[cs.length-3]));
			}
		}
		inforeader.close();
		
		int commonscan = 0;
		int commonseq = 0;
		int other = 0;
		
		PeptideListReader r2 = new PeptideListReader(allppl);
		IPeptide p2 = null;
		while((p2=r2.getPeptide())!=null){
			
			if(p2.getRank()>1)
				continue;
			
			String scanname = p2.getBaseName();
			String sequence = p2.getSequence();
			double pepmw = p2.getMr();
			int modcount = sequence.length()-sequence.replaceAll("#", "").length();
			pepmw = pepmw-modcount*656.227614635;
			
			if(map.containsKey(scanname)){
				commonscan++;
				if(Math.abs(pepmw-map.get(scanname))<2){
					commonseq++;
//					System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr());
				}else{
					System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr()
							+"\t"+map.get(scanname));
				}
			}else{
//				if(sequence.contains("#")){
					System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr());
					other++;
//				}
			}
		}
		r2.close();
		
		System.out.println(map.size()+"\t"+r2.getNumberofPeptides()+"\t"+commonscan+"\t"+commonseq+"\t"+other);
	}
	
	private static void compareType2(String allppl, String pepinfo) throws IOException, FileDamageException{
		
		HashMap <String, Double> map = new HashMap <String, Double>();
		BufferedReader inforeader = new BufferedReader(new FileReader(pepinfo));
		String pline = null;
L:		while((pline=inforeader.readLine())!=null){
			String [] cs = pline.split("\t");
			int tt = Integer.parseInt(cs[0]);
			int count = Integer.parseInt(cs[1]);
			if(tt==2){
				map.put(cs[cs.length-2], Double.parseDouble(cs[cs.length-3]));
			}else if(tt==5){
				for(int i=0;i<count;i++){
					if(!cs[2+i*2].equals("core1_5")){
						continue L;
					}
				}
				map.put(cs[cs.length-2], Double.parseDouble(cs[cs.length-3]));
			}
		}
		inforeader.close();
		
		int commonscan = 0;
		int commonseq = 0;
		int other = 0;
		
		PeptideListReader r2 = new PeptideListReader(allppl);
		IPeptide p2 = null;
		while((p2=r2.getPeptide())!=null){
			
			if(p2.getRank()!=1){
				continue;
			}
			
			String scanname = p2.getBaseName();
			String sequence = p2.getSequence();
			double pepmw = p2.getMr();
			int modcount = sequence.length()-sequence.replaceAll("#", "").length();
			pepmw = pepmw-modcount*947.32303127;
			
			if(map.containsKey(scanname)){
				commonscan++;
				if(Math.abs(pepmw-map.get(scanname))<2){
					commonseq++;
				}else{
					System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr()
							+"\t"+map.get(scanname));
				}
			}else{
				if(sequence.contains("#")){
					System.out.println(scanname+"\t"+sequence+"\t"+p2.getPrimaryScore()+"\t"+p2.getExperimentalMZ()+"\t"+p2.getCharge()+"\t"+p2.getMr());
					other++;
				}
			}
		}
		r2.close();
		
		System.out.println(map.size()+"\t"+r2.getNumberofPeptides()+"\t"+commonscan+"\t"+commonseq+"\t"+other);
	}
	
	private static void combine(String in1, String in2, String out) throws IOException{
		
		File [] files1 = (new File(in1)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("mgf"))
					return true;
				
				return false;
			}
			
		});
		
		File [] files2 = (new File(in2)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("mgf"))
					return true;
				
				return false;
			}
			
		});
		
		Pattern pattern = Pattern.compile("TITLE=Locus:1\\.1\\.(\\d+)\\.(\\d+)\\.(\\d+)");
		
		for(int i=0;i<files1.length;i++){
			
			String name1 = files1[i].getName();
			System.out.println(name1);
			
			for(int j=0;j<files2.length;j++){
				
				String name2 = files2[j].getName();
				System.out.println(name2);
				
				if(name1.equals(name2)){
					
					BufferedReader reader1 = new BufferedReader(new FileReader(files1[i]));
					BufferedReader reader2 = new BufferedReader(new FileReader(files2[j]));
					PrintWriter writer = new PrintWriter(out+"\\"+name1);
					
					String line= null;
					while((line=reader1.readLine())!=null){
						writer.write(line+"\n");
					}
					reader1.close();
					
					while((line=reader2.readLine())!=null){
						writer.write(line+"\n");
					}
					reader2.close();
					writer.close();
				}
			}
		}
	}
	
	private static void fdrtest(String in, String pepinfo) throws FileDamageException, IOException{
		
		HashMap<String, OGlycanScanInfo>[] infomap = new HashMap[2];
		infomap[0] = new HashMap<String, OGlycanScanInfo>();
		infomap[1] = new HashMap<String, OGlycanScanInfo>();
		
		BufferedReader reader =  new BufferedReader(new FileReader(pepinfo));
		String line = null;
		while((line=reader.readLine())!=null){
			OGlycanScanInfo info = new OGlycanScanInfo(line);
			if(infomap[0].containsKey(info.getScanname())){
				infomap[1].put(info.getScanname(), info);
			}else{
				infomap[0].put(info.getScanname(), info);
			}
		}
		reader.close();
		System.out.println(infomap[0].size());
		System.out.println(infomap[1].size());
		
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('*', 15.994915, "Oxidation");
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		MwCalculator mwc = new MwCalculator(aas, aam);
		
		ArrayList <Double> targetlist = new ArrayList <Double>();
		ArrayList <Double> decoylist = new ArrayList <Double>();
		PeptideListReader pepreader = new PeptideListReader(in);
		IPeptide peptide = null;
		while((peptide=pepreader.getPeptide())!=null){
			
			String scanname = peptide.getBaseName();
			if(scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length()-2);
			String sequence = peptide.getSequence();

			StringBuilder sb = new StringBuilder();
			StringBuilder unisb = new StringBuilder();
			int stcount = 0;

			for(int i=0;i<sequence.length();i++){
				if(sequence.charAt(i)>='A' && sequence.charAt(i)<='Z'){
					sb.append(sequence.charAt(i));
					if(i>=2 && i<sequence.length()-2){
						unisb.append(sequence.charAt(i));
						if(sequence.charAt(i)=='S' || sequence.charAt(i)=='T')
							stcount++;
					}
				}else if(sequence.charAt(i)=='*'){
					sb.append(sequence.charAt(i));
				}else if(sequence.charAt(i)=='.'){
					sb.append(sequence.charAt(i));
				}else if(sequence.charAt(i)=='-'){
					sb.append(sequence.charAt(i));
				}
			}
			
			OGlycanScanInfo info = infomap[0].get(scanname);
			if(Math.abs(info.getPepMw()-mwc.getMonoIsotopeMZ(sb.substring(2, sb.length()-2)))>5){
				if(infomap[1].containsKey(scanname)){
					info = infomap[1].get(scanname);
					if(Math.abs(info.getPepMw()-mwc.getMonoIsotopeMZ(sb.substring(2, sb.length()-2)))>5){
						continue;
					}
				}else{
					continue;
				}
			}
			
			int count = sequence.length()-sb.length();

			if(peptide.getRank()>1)
				continue;
			
			double score = peptide.getPrimaryScore();
			if(peptide.isTP()){
				targetlist.add(score);
			}else{
				decoylist.add(score);
			}
		}
		pepreader.close();
		
		Double [] t = targetlist.toArray(new Double[targetlist.size()]);
		Arrays.sort(t);
		
		Double [] d = decoylist.toArray(new Double[decoylist.size()]);
		Arrays.sort(d);
		
//		for(int i=0;i<t.length;i++){
//			System.out.println(t[i]);
//		}
		
		for(int i=1;i<=40;i++){
			int i1 = Arrays.binarySearch(t, (double)i);
			if(i1<0) i1 = -i1-1;
			
			int i2 = Arrays.binarySearch(d, (double)i);
			if(i2<0) i2 = -i2-1;
			
			int target = t.length-i1;
			int decoy = d.length-i2;
			double fdr = (double)decoy/(double)target;
			System.out.println(i+"\t"+target+"\t"+decoy+"\t"+fdr);
		}
	}

	private static void fdrtestAll(String in, String pepinfo) throws FileDamageException, IOException{
		
		HashMap<String, OGlycanScanInfo>[] infomap = new HashMap[2];
		infomap[0] = new HashMap<String, OGlycanScanInfo>();
		infomap[1] = new HashMap<String, OGlycanScanInfo>();
		
		BufferedReader reader =  new BufferedReader(new FileReader(pepinfo));
		String line = null;
		while((line=reader.readLine())!=null){
			OGlycanScanInfo info = new OGlycanScanInfo(line);
			if(infomap[0].containsKey(info.getScanname())){
				infomap[1].put(info.getScanname(), info);
			}else{
				infomap[0].put(info.getScanname(), info);
			}
		}
		reader.close();
		System.out.println(infomap[0].size());
		System.out.println(infomap[1].size());
		
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		aam.addModification('*', 15.994915, "Oxidation");
		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		MwCalculator mwc = new MwCalculator(aas, aam);
		
		ArrayList <Double> targetlist = new ArrayList <Double>();
		ArrayList <Double> decoylist = new ArrayList <Double>();
		ArrayList <Double> targetGlycanList = new ArrayList <Double>();
		PeptideListReader pepreader = new PeptideListReader(in);
		IPeptide peptide = null;
		ArrayList <IPeptide> oglycolist = new ArrayList <IPeptide>();
		
		while((peptide=pepreader.getPeptide())!=null){
			
			String scanname = peptide.getBaseName();
			if(scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length()-2);
			String sequence = peptide.getSequence();

			StringBuilder sb = new StringBuilder();
			StringBuilder unisb = new StringBuilder();
			int stcount = 0;

			for(int i=0;i<sequence.length();i++){
				if(sequence.charAt(i)>='A' && sequence.charAt(i)<='Z'){
					sb.append(sequence.charAt(i));
					if(i>=2 && i<sequence.length()-2){
						unisb.append(sequence.charAt(i));
						if(sequence.charAt(i)=='S' || sequence.charAt(i)=='T')
							stcount++;
					}
				}else if(sequence.charAt(i)=='*'){
					sb.append(sequence.charAt(i));
				}else if(sequence.charAt(i)=='.'){
					sb.append(sequence.charAt(i));
				}else if(sequence.charAt(i)=='-'){
					sb.append(sequence.charAt(i));
				}
			}
/*			
			OGlycanScanInfo info = infomap[0].get(scanname);
			if(Math.abs(info.getPepMw()-mwc.getMonoIsotopeMZ(sb.substring(2, sb.length()-2)))>5){
				if(infomap[1].containsKey(scanname)){
					info = infomap[1].get(scanname);
					if(Math.abs(info.getPepMw()-mwc.getMonoIsotopeMZ(sb.substring(2, sb.length()-2)))>5){
						continue;
					}
				}else{
					continue;
				}
			}
*/			

			int count = sequence.length()-sb.length();
			if(peptide.getRank()>1)
				continue;
			
			double score = peptide.getPrimaryScore();
			if(peptide.isTP()){
				targetlist.add(score);
				if(count==1){
					oglycolist.add(peptide);
					targetGlycanList.add(score);
					if(!infomap[0].containsKey(scanname) && !infomap[1].containsKey(scanname) && score>=40){
//						System.out.println(scanname+"\t"+sequence+"\t"+score+"\t"+peptide.getMr());
					}
				}
			}else{
				decoylist.add(score);
			}
		}
		pepreader.close();
		
		Double [] t = targetlist.toArray(new Double[targetlist.size()]);
		Arrays.sort(t);
		
		Double [] d = decoylist.toArray(new Double[decoylist.size()]);
		Arrays.sort(d);
		
		Double [] o = targetGlycanList.toArray(new Double[targetGlycanList.size()]);
		Arrays.sort(o);
		
//		for(int i=0;i<t.length;i++){
//			System.out.println(t[i]);
//		}
		
		for(int i=1;i<=40;i++){
			int i1 = Arrays.binarySearch(t, (double)i);
			if(i1<0) i1 = -i1-1;
			
			int i2 = Arrays.binarySearch(d, (double)i);
			if(i2<0) i2 = -i2-1;
			
			int i3 = Arrays.binarySearch(o, (double)i);
			if(i3<0) i3 = -i3-1;
			
			int target = t.length-i1;
			int decoy = d.length-i2;
			int targeto = o.length-i3;
			double fdr = (double)decoy/(double)target;
			System.out.println(i+"\t"+target+"\t"+decoy+"\t"+fdr+"\t"+targeto);
		}
		
		for(int i=0;i<oglycolist.size();i++){
			IPeptide op = oglycolist.get(i);
			if(op.getPrimaryScore()>=23){
				System.out.println(op.getScanNum()+"\t"+op.getSequence()+"\t"+op.getPrimaryScore()+"\t"+op.getMr());
			}
		}
	}
	
	private static void resultReader(String in) throws IOException, JXLException{
		
		ArrayList <Double> list = new ArrayList <Double>();
		ExcelReader reader = new ExcelReader(in);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			double score = Double.parseDouble(line[2]);
			boolean type1 = true;
			for(int i=8;i<line.length;i++){
				String mod = line[i].substring(0, line[i].indexOf("@"));
				if(mod.equals("Hex-(NeuAc-)HexNAc")){
					type1 = false;
					break;
				}
			}
			if(type1){
				list.add(score);
			}
		}
		System.out.println(list.size());
	}
	
	private static void allModCompare(String type1, String type2) throws FileDamageException, IOException{
		
		HashMap <String, IPeptide> pepmap1 = new HashMap <String, IPeptide>();
		PeptideListReader reader1 = new PeptideListReader(type1);
		IPeptide peptide = null;
		while((peptide=reader1.getPeptide())!=null){
			String scanname = peptide.getBaseName();
			if(scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length()-2);
			String sequence = peptide.getSequence();

			if(peptide.getRank()>1)
				continue;
			
			if(sequence.contains("#")){
				pepmap1.put(scanname, peptide);
			}
		}
		reader1.close();
		
		HashMap <String, IPeptide> pepmap2 = new HashMap <String, IPeptide>();
		PeptideListReader reader2 = new PeptideListReader(type2);
		while((peptide=reader2.getPeptide())!=null){
			
			String scanname = peptide.getBaseName();
			if(scanname.endsWith(", ")) scanname = scanname.substring(0, scanname.length()-2);
			String sequence = peptide.getSequence();

			if(peptide.getRank()>1)
				continue;
			
			if(sequence.contains("#") && pepmap1.containsKey(scanname)){
				pepmap2.put(scanname, peptide);
			}
		}
		reader2.close();
		
		Iterator <String> it = pepmap2.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			IPeptide p1 = pepmap1.get(key);
			IPeptide p2 = pepmap2.get(key);
			double s1 = p1.getPrimaryScore();
			double s2 = p2.getPrimaryScore();
			
			if(s1>10 && s2>10)
			System.out.println(key+"\t"+p1.getSequence()+"\t"+p2.getSequence()+"\t"+s1+"\t"+s2);
		}
	}
	
	private static void resultCompare(String all, String type) throws IOException{
		
		HashMap <String, String[]> map1 = new HashMap <String, String[]>();
		BufferedReader reader1 = new BufferedReader(new FileReader(all));
		String line1 = null;
		while((line1=reader1.readLine())!=null){
			String [] cs = line1.split("\t");
			map1.put(cs[0], cs);
		}
		reader1.close();
		
		System.out.println(map1.size());
		
		int typecount = 0;
		int commonscan = 0;
		int commonsequence = 0;
		BufferedReader reader2 = new BufferedReader(new FileReader(type));
		String line2 = null;
		while((line2=reader2.readLine())!=null){
			String [] cs = line2.split("\t");
			typecount++;
			if(map1.containsKey(cs[0])){
				commonscan++;
				if(map1.get(cs[0])[1].equals(cs[1])){
					commonsequence++;
				}
				System.out.println(line2+"\t"+map1.get(cs[0])[2]);
				map1.remove(cs[0]);
			}else{
//				System.out.println(line2);
			}
		}
		reader2.close();
		
		System.out.println(map1.size()+"\t"+typecount+"\t"+commonscan+"\t"+commonsequence);
		
		Iterator <String> it = map1.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String [] cs = map1.get(key);
//			System.out.println(cs[0]+"\t"+cs[1]+"\t"+cs[2]+"\t"+cs[3]);
		}
	}

	/**
	 * @param args
	 * @throws DtaFileParsingException 
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws DtaFileParsingException, FileDamageException, IOException, JXLException {
		// TODO Auto-generated method stub

		String in = "H:\\OGlycan_final_20130719\\serum_1D";
		String out = "H:\\OGlycan_final_20130719\\serum_1D\\combine\\" +
				"serum_1D.deglyco.Allpeak.mgf";
		OGlycanTest2.deGlycoPeak(in, out);
		
//		OGlycanTest2.compare("H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\type2_F004467.dat.ppl", 
//				"H:\\OGlycan_0417_standard\\allscan\\type2\\20120329_Fetuin_elastase_HILIC_5ug-01.allscans_F004844.dat.ppl",
//				"H:\\OGlycan_0417_standard\\20120329_Fetuin_elastase_HILIC_5ug-01\\fetuin_1.xls");
		
//		OGlycanTest2.compareType1("H:\\OGlycan_0417_standard\\allscan\\type1\\20120329_Fetuin_elastase_HILIC_5ug-03.allscans_F004831.dat.ppl", 
//				"H:\\OGlycan_0417_standard\\test\\20120329_Fetuin_elastase_HILIC_5ug-03.peps.info", 1);
		
//		OGlycanTest2.compareType1("H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02_type1_F004929.dat.ppl", 
//				"H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\20120328_humaneserum_trypsin_HILIC_8uL-02\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02.peps.info");
		
//		OGlycanTest2.combine("H:\\OGlycan_0530_2D\\2D_T+C\\part1\\OGlycan_2D_TC", 
//				"H:\\OGlycan_0530_2D\\2D_T+C\\part2\\OGlycan_2D_TC", 
//				"H:\\OGlycan_0530_2D\\2D_T+C\\OGlycan_0530_2D_TC");
		
//		OGlycanTest2.fdrtest("H:\\OGlycan_final\\1D_complex\\20120328_humaneserum_trypsin_HILIC_8uL-02\\" +
//		OGlycanTest2.fdrtestAll("H:\\OGlycan_final\\1D_complex\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02_type1_F004958.dat.ppl",
//				"H:\\OGlycan_final\\1D_complex\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02.peps.info");
//		OGlycanTest2.resultReader("H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\20120328_humaneserum_trypsin_HILIC_8uL-02\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02.xls");
		
//		OGlycanTest2.allModCompare("H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02_type1_F004929.dat.ppl", 
//				"H:\\OGlycan\\Ѫ��\\20120328-humanserum-trypsin\\20130621\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02_type2_F004928.dat.ppl");
		
//		OGlycanTest2.resultCompare("H:\\OGlycan_final\\1D_complex\\20120328_humaneserum_trypsin_HILIC_8uL-02.glyco_type2_F004962.dat.txt", 
//				"H:\\OGlycan_final\\1D_complex\\20120328_humaneserum_trypsin_HILIC_8uL-02\\" +
//				"20120328_humaneserum_trypsin_HILIC_8uL-02_type2_F004964.dat.txt");
	}

}
