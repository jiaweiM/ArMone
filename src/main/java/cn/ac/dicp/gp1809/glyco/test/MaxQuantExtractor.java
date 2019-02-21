/* 
 ******************************************************************************
 * File: MaxQuantExtractor.java * * * Created on 2013-11-14
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2013-11-14, ����9:04:55
 */
public class MaxQuantExtractor {
	
	private static void extract(String in, String out) throws IOException{
		
		PrintWriter writer = new PrintWriter(out);
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = reader.readLine();
		String [] title = line.split("\t");
		int windowid = -1;
		for(int i=0;i<title.length;i++){
			if(title[i].equals("Sequence Window")){
				windowid = i;
			}
		}
		writer.write(line+"\n");
		while((line=reader.readLine())!=null){
			String [] cs = line.split("\t");
			String window = cs[windowid];
			if(window.charAt(7)=='P') continue;
			if(window.charAt(8)!='S' && window.charAt(8)!='T') continue;
			
			writer.write(line+"\n");
		}
		reader.close();
		writer.close();
	}

	private static HashMap<String, String[]> cao(String pep, String site, String fasta) throws IOException{
		
		HashMap<String, String[]> proNameMap = new HashMap<String, String[]>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence proseq = null;
		while((proseq=fr.nextSequence())!=null){
			String ref = proseq.getReference();
			String uniprot = proseq.getSWISS();
			String gene = proseq.getGene();
			String name = proseq.getName();
			proNameMap.put(ref.substring(4, 15), new String[]{name, gene, uniprot});
		}
		fr.close();
		
		ArrayList<String[]> slist = new ArrayList<String[]>();
		BufferedReader sbr = new BufferedReader(new FileReader(site));
		String sline = sbr.readLine();
		String [] stitle = sline.split("\t");
		int glycoid = -1;
		int windowid = -1;
		int posiid = -1;
		for(int i=0;i<stitle.length;i++){
			if(stitle[i].equals("Known Site")){
				glycoid = i;
			}
			if(stitle[i].equals("Sequence Window")){
				windowid = i;
			}
			if(stitle[i].equals("Position")){
				posiid = i;
			}
		}
		while((sline=sbr.readLine())!=null){
			slist.add(sline.split("\t"));
		}
		sbr.close();
		
		BufferedReader pbr = new BufferedReader(new FileReader(pep));
		String pline = pbr.readLine();
		String [] ptitle = pline.split("\t");
		int sid = -1;
		int seqid = -1;
		int refid = -1;
		int scoreid = -1;
		int massid = -1;
		for(int i=0;i<ptitle.length;i++){
			if(ptitle[i].equals("Deamidation (N) Site IDs")){
				sid = i;
			}
			if(ptitle[i].equals("Sequence")){
				seqid = i;
			}
			if(ptitle[i].equals("Leading Razor Protein")){
				refid = i;
			}
			if(ptitle[i].equals("Score")){
				scoreid = i;
			}
			if(ptitle[i].equals("Mass")){
				massid = i;
			}
		}

		HashMap<String, String[]> map = new HashMap<String, String[]>();
		while((pline=pbr.readLine())!=null){
			
			String [] ps = pline.split("\t");
			if(ps[sid].trim().length()==0) continue;
			String ref = ps[refid];
			if(!proNameMap.containsKey(ref)) continue;
			
			StringBuilder posisb = new StringBuilder();
			StringBuilder winsb = new StringBuilder();
			ArrayList<String> windowlist = new ArrayList<String>();
			String [] strids = ps[sid].split(";");
			for(int i=0;i<strids.length;i++){
				int id = Integer.parseInt(strids[i]);
				String [] scontent = slist.get(id);
				if(!scontent[glycoid].equals("Glycosylation")) continue;
				windowlist.add(scontent[windowid]);
				posisb.append(scontent[posiid]).append(";");
				winsb.append(scontent[windowid]).append(";");
			}
			if(windowlist.size()==0) continue;
			
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<ps[seqid].length();i++){
				char aa = ps[seqid].charAt(i);
				if(aa=='N'){
					sb.append(aa);
					boolean match = true;
					for(int j=0;j<windowlist.size();j++){
						String window = windowlist.get(j);
						for(int k=1;k<=6;k++){
							char wk0 = window.charAt(6-k);
							if(wk0=='_') break;
							if(i-k>=0){
								if(ps[seqid].charAt(i-k)!=wk0){
									match = false;
									break;
								}
							}
						}
						for(int k=1;k<=6;k++){
							char wk0 = window.charAt(6+k);
							if(wk0=='_') break;
							if(i+k<ps[seqid].length()){
								if(ps[seqid].charAt(i+k)!=wk0){
									match = false;
									break;
								}
							}
						}
						if(match==true){
							break;
						}else{
							match = true;
						}
					}
					if(match){
						sb.append("(de)");
					}
				}else{
					sb.append(aa);
				}
			}

			String [] proinfo = proNameMap.get(ref);
			String [] content = new String[9];
			content[0] = ref;
			content[1] = proinfo[0];
			content[2] = proinfo[1];
			content[3] = proinfo[2];
			content[4] = sb.toString();
			content[5] = ps[massid];
			content[6] = ps[scoreid];
			content[7] = posisb.substring(0, posisb.length()-1);
			content[8] = winsb.substring(0, winsb.length()-1);
			map.put(sb.toString(), content);
		}
		pbr.close();
		System.out.println(map.size());
		return map;
	}
	
	private static void cao2(String in1, String in2, String out, String fasta) throws IOException, RowsExceededException, WriteException{
		HashMap<String, String[]> map1 = cao(in1+"\\peptides.txt", in1+"\\Deamidation (N)Sites.txt", fasta);
		HashMap<String, String[]> map2 = cao(in2+"\\peptides.txt", in2+"\\Deamidation (N)Sites.txt", fasta);
		Iterator<String> it = map2.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			if(map1.containsKey(key)){
				double s1 = Double.parseDouble(map1.get(key)[6]);
				double s2 = Double.parseDouble(map2.get(key)[6]);
				if(s2>s1){
					map1.put(key, map2.get(key));
				}
			}else{
				map1.put(key, map2.get(key));
			}
		}
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		writer.addTitle("Protein\tProtein Name\tGene Name\tUniprot\tSequence\tMass\tScore\tPosition\tSequence Window", 0, format);
		for(String [] ss : map1.values()){
			writer.addContent(ss, 0, format);
		}
		writer.close();
	}
	
	private static void extractPeptideFromEvidence(String evidencefile, String out) throws IOException, WriteException{

		BufferedReader reader = new BufferedReader(new FileReader(evidencefile));
		String [] title = reader.readLine().split("\t");
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		HashMap<String, Double> scoremap = new HashMap<String, Double>();
		HashMap<String, String[]> contentmap = new HashMap<String, String[]>();
		int seqid = -1;
		int scoreid = -1;
		int revid = -1;
		int conid = -1;
		for(int i=0;i<title.length;i++){
			if(title[i].equals("Score")){
				scoreid = i;
			}
			if(title[i].equals("Modified sequence")){
				seqid = i;
			}
			if(title[i].equals("Reverse")){
				revid = i;
			}
			if(title[i].equals("Contaminant")){
				conid = i;
			}
		}
		writer.addTitle(title, 0, format);
		
		Pattern pattern = Pattern.compile("N\\(de\\)[A-OQ-Z][STC]");
		
		String line = null;
		while((line=reader.readLine())!=null){
			String [] cs = line.split("\t");
			if(cs[revid].equals("+") || cs[conid].equals("+"))
				continue;
			
			String seq = cs[seqid];
			double score = Double.parseDouble(cs[scoreid]);

			if(scoremap.containsKey(seq)){
				if(score<scoremap.get(seq))
					continue;
				
				Matcher matcher = pattern.matcher(seq);
				if(matcher.find()){
					scoremap.put(seq, score);
					contentmap.put(seq, cs);
				}
			}else{
				Matcher matcher = pattern.matcher(seq);
				if(matcher.find()){
					scoremap.put(seq, score);
					contentmap.put(seq, cs);
				}
			}
		}
		
		String[] keys = contentmap.keySet().toArray(new String[contentmap.size()]);
		Arrays.sort(keys);
		for(int i=0;i<keys.length;i++){
			writer.addContent(contentmap.get(keys[i]), 0, format);
		}
		
		reader.close();
		writer.close();
	
	}
	
	private static void batchExtractPeptideFromEvidence(String in) throws IOException, WriteException{

		File[] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isFile()) continue;
			System.out.println(files[i]);
			File[] fff = files[i].listFiles();
			for(int j=0;j<fff.length;j++){
				System.out.println(fff[j]);
				if(fff[j].getName().endsWith("evidence.txt")){
					String evidence = fff[j].getAbsolutePath();
					String out = files[i].getAbsolutePath()+".xls";
					MaxQuantExtractor.extractPeptideFromEvidence(evidence, out);
				}
			}
		}
	}
	
	private static void extractPeptideFromModification(String modfile, String evidencefile, String out) throws IOException, WriteException{

		BufferedReader reader = new BufferedReader(new FileReader(evidencefile));
		String [] title = reader.readLine().split("\t");
		HashMap<String, String> seqmap = new HashMap<String, String>();
		int seqid = -1;
		int idid = -1;
		int revid = -1;
		int conid = -1;
		for(int i=0;i<title.length;i++){
			if(title[i].equals("id")){
				idid = i;
			}
			if(title[i].equals("Modified sequence")){
				seqid = i;
			}
			if(title[i].equals("Reverse")){
				revid = i;
			}
			if(title[i].equals("Contaminant")){
				conid = i;
			}
		}
		
		Pattern pattern = Pattern.compile("N\\(de\\)[A-OQ-Z][STC]");
		
		String line = null;
		while((line=reader.readLine())!=null){
			String [] cs = line.split("\t");
			if(cs[revid].equals("+") || cs[conid].equals("+"))
				continue;
			
			Matcher matcher = pattern.matcher(cs[seqid]);
			if(matcher.find()){
				seqmap.put(cs[idid], cs[seqid]);
			}
		}
		reader.close();
		
		BufferedReader modreader = new BufferedReader(new FileReader(modfile));
		title = modreader.readLine().split("\t");
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		writer.addTitle(title, 0, format);
		int eviid = -1;
		int modseqid = -1;
		for(int i=0;i<title.length;i++){
			if(title[i].equals("Sequence")){
				modseqid = i;
			}
			if(title[i].equals("Evidence IDs")){
				eviid = i;
			}
		}
		while((line=modreader.readLine())!=null){
			String [] cs = line.split("\t");
			String[] eviids = cs[eviid].split(";");
			for(int i=0;i<eviids.length;i++){
				if(seqmap.containsKey(eviids[i])){
					String modseq = seqmap.get(eviids[i]);
					cs[modseqid] = modseq;
					writer.addContent(cs, 0, format);
					break;
				}
			}
		}
		
		writer.close();
		modreader.close();
	}
	
	private static void batchExtractPeptideFromModification(String in) throws IOException, WriteException{

		File[] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].isFile()) continue;
			File[] fff = files[i].listFiles();
			String evidence = "";
			String mod = "";
			System.out.println(files[i]);
			
			for(int j=0;j<fff.length;j++){
				if(fff[j].getName().endsWith("evidence.txt")){
					evidence = fff[j].getAbsolutePath();
				}
				if(fff[j].getName().endsWith("modificationSpecificPeptides.txt")){
					mod = fff[j].getAbsolutePath();
				}
			}
			String out = files[i].getAbsolutePath()+".xls";
			MaxQuantExtractor.extractPeptideFromModification(mod, evidence, out);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws IOException, RowsExceededException, WriteException {

//		MaxQuantExtractor.extract("H:\\xubo\\20131114_glyco\\hilic_1", "H:\\xubo\\20131114_glyco\\hilic_1.site");
//		MaxQuantExtractor.cao("H:\\sun\\20131206\\K562A\\2\\peptides.txt", "H:\\sun\\20131206\\K562A\\2\\Deamidation (N)Sites.txt", 
//				"F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta");
//		MaxQuantExtractor.cao2("H:\\sun\\20131206\\K562S\\1", "H:\\sun\\20131206\\K562S\\2", 
//				"H:\\sun\\20131206\\K562S.xls", "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta");
//		MaxQuantExtractor.batchExtractPeptideFromEvidence("F:\\txt");
		MaxQuantExtractor.batchExtractPeptideFromModification("F:\\txt");
	}

}
