/* 
 ******************************************************************************
 * File: MaxQuantEvidenceReader.java * * * Created on 2012-9-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.JXLException;
import jxl.write.WriteException;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2012-9-25, 18:46:48
 */
public class MaxQuantEvidenceReader {
	
	private static int modId = -1;
	private static int seqId = -1;
	private static int modSeqId = -1;
	private static int scoreId = -1;
	private static int ratioId = -1;
	private static int ratioNorId = -1;
	private static int refId = -1;
	private static int probId = -1;
	private static int scoreDiffId = -1;
	private static int revId = -1;
	private static int conId = -1;
	private static int scanId = -1;
	
	private static Pattern pat = Pattern.compile("[A-Z]*(\\([\\d.]*\\))[A-Z]*");

	public static void combine(String in, String out) throws IOException, WriteException{
		
		BufferedReader reader = new BufferedReader(new FileReader(in));
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String line = reader.readLine();
		writer.addTitle(line, 0, ef);
		
		String [] title = line.split("\t");
		for(int i=0;i<title.length;i++){
			if(title[i].startsWith("Modifications")){
				modId = i;System.out.println(i);
			}else if(title[i].startsWith("Modified")){
				modSeqId = i;System.out.println(i);
			}else if(title[i].startsWith("Score")){
				scoreId = i;System.out.println(i);
			}else if(title[i].equals("Ratio H/L")){
				ratioId = i;System.out.println(i);
			}else if(title[i].equals("Ratio H/L Normalized")){
				ratioNorId = i;System.out.println(i);
			}else if(title[i].equals("Leading Razor Protein")){
				refId = i;System.out.println(i);
			}
		}
		
		HashMap <String, String []> contentMap = new HashMap <String, String[]>();
		HashMap <String, Double> scoreMap = new HashMap <String, Double>();
		HashMap <String, ArrayList <Double>> ratioMap = new HashMap <String, ArrayList <Double>>();
		HashMap <String, ArrayList <Double>> ratioNorMap = new HashMap <String, ArrayList <Double>>();
		while((line=reader.readLine())!=null){
			
			String [] cs = line.split("\t");
			if(cs.length<=ratioId) continue;
			
			String mod = cs[modId];
			String seq = cs[modSeqId];
			String ref = cs[refId];
			
			if(ref.startsWith("REV") || ref.startsWith("CON")) continue;
			
			double score = Double.parseDouble(cs[scoreId]);
			double ratio = cs[ratioId].length()>0 ? Double.parseDouble(cs[ratioId]) : 0;
			double ratioNor = cs[ratioNorId].length()>0 ? Double.parseDouble(cs[ratioNorId]) : 0;
			
			if(mod.contains("Phospho")){

				if(contentMap.containsKey(seq)){
					if(score>scoreMap.get(seq)){
						contentMap.put(seq, cs);
					}
					if(ratio!=0) ratioMap.get(seq).add(ratio);
					if(ratioNor!=0) ratioNorMap.get(seq).add(ratioNor);
				}else{
					contentMap.put(seq, cs);
					scoreMap.put(seq, score);
					ArrayList <Double> ratioList = new ArrayList <Double>();
					ArrayList <Double> ratioNorList = new ArrayList <Double>();
					if(ratio!=0) ratioList.add(ratio);
					if(ratioNor!=0) ratioNorList.add(ratioNor);
					ratioMap.put(seq, ratioList);
					ratioNorMap.put(seq, ratioNorList);
				}
			}
		}
		
		Iterator <String> it = contentMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			ArrayList <Double> ratioList = ratioMap.get(key);
			ArrayList <Double> ratioNorList = ratioNorMap.get(key);
			double ratio = MathTool.getAveInDouble(ratioList);
			double ratioNor = MathTool.getAveInDouble(ratioNorList);
			String [] content = contentMap.get(key);
			
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<content.length;i++){
				if(i==ratioId){
					if(ratio!=0) sb.append(ratio).append("\t");
					else sb.append("\t");
				}else if(i==ratioNorId){
					if(ratioNor!=0) sb.append(ratioNor).append("\t");
					else sb.append("\t");
				}else{
					sb.append(content[i]).append("\t");
				}
			}
			writer.addContent(sb.toString(), 0, ef);
		}
		
		reader.close();
		writer.close();
	}
	
	public static void combine(String evidence, String site, String out) throws IOException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		
		BufferedReader sitereader = new BufferedReader(new FileReader(site));
		HashMap <String, String[]> sitemap = new HashMap <String, String[]>();
		String siteline = sitereader.readLine();
		String [] sitetitle = siteline.split("\t");
		StringBuilder sitetitlesb = new StringBuilder();
		for(int i=0;i<sitetitle.length;i++){
			if(i==2){
				sitetitlesb.append(sitetitle[i]).append("\t\t\t\t\t\t");
			}else{
				sitetitlesb.append(sitetitle[i]).append("\t");
			}
		}
		writer.addTitle(sitetitlesb.toString(), 0, ef);
		
		int delete = 0;
		while((siteline=sitereader.readLine())!=null){
			String [] ss = siteline.split("\t");
			if(ss[29].contains("+") || ss[30].contains("+")){
				delete++;
				continue;
			}
			
			String seq = ss[0];
			String scan = ss[18];
			String key = seq+"_"+scan;
			if(sitemap.containsKey(key)) System.out.println(key);
			sitemap.put(key, ss);
		}
		sitereader.close();
		System.out.println(sitemap.size());
		
		BufferedReader reader = new BufferedReader(new FileReader(evidence));
		String line = reader.readLine();
		String [] title = line.split("\t");
		for(int i=0;i<title.length;i++){
			if(title[i].startsWith("Modifications")){
				modId = i;System.out.println(i);
			}else if(title[i].startsWith("Modified")){
				modSeqId = i;System.out.println(i);
			}else if(title[i].startsWith("Score")){
				scoreId = i;System.out.println(i);
			}else if(title[i].equals("Ratio H/L")){
				ratioId = i;System.out.println(i);
			}else if(title[i].equals("Ratio H/L Normalized")){
				ratioNorId = i;System.out.println(i);
			}else if(title[i].equals("Leading Razor Protein")){
				refId = i;System.out.println(i);
			}else if(title[i].equals("Reverse")){
				revId = i;System.out.println(i);
			}else if(title[i].equals("Contaminant")){
				conId = i;System.out.println(i);
			}
			else if(title[i].contains("Scan Number")){
				scanId = i;System.out.println(i);
			}
		}

		HashSet <String> usedset = new HashSet <String>();
		while((line=reader.readLine())!=null){
			
			String [] cs = line.split("\t");
			if(cs.length<=ratioId) continue;

//			String ref = cs[refId];
//			if(ref.startsWith("REV") || ref.startsWith("CON")) continue;

			String key = cs[0]+"_"+cs[scanId];
			if(sitemap.containsKey(key)){
				if(usedset.contains(key)){
					System.out.println(key);
					continue;
				}
				String [] content = sitemap.get(key);
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<3;i++){
					sb.append(content[i]).append("\t");
				}
				for(int i=4;i<9;i++){
					sb.append(cs[i]).append("\t");
				}
				for(int i=3;i<content.length;i++){
					sb.append(content[i]).append("\t");
				}
				writer.addContent(sb.toString(), 0, ef);
				usedset.add(key);
			}
		}

		System.out.println("delete\t"+delete);
		reader.close();
		writer.close();
	}
	
	public static void SiteExtractor(String in, String out, String fasta) throws IOException, 
		JXLException, SequenceGenerationException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Site\tSequence window\tReference\t" +
				"Modified Sequence\tPhospho(STY) Probabilities\tPhospho(STY) Score Diffs\tScore\t" +
				"Position\tLocalization Probabilities\tScore Diffs\tRatio H/L\tRatio H/L Normalized";
		writer.addTitle(title, 0, ef);
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String key = ref.substring(4, 15);
			psmap.put(key, ps);
		}
		fr.close();
		
		ExcelReader reader = new ExcelReader(in);
		String [] line = reader.readLine();
		
		for(int i=0;i<line.length;i++){
			if(line[i].startsWith("Modified")){
				modSeqId = i;System.out.println(i);
			}else if(line[i].equals("Sequence")){
				seqId = i;System.out.println(i);
			}else if(line[i].equals("Leading Razor Protein")){
				refId = i;System.out.println(i);
			}else if(line[i].equals("Ratio H/L")){
				ratioId = i;System.out.println(i);
			}else if(line[i].equals("Ratio H/L Normalized")){
				ratioNorId = i;System.out.println(i);
			}else if(line[i].equals("Phospho (STY) Probabilities")){
				probId = i;System.out.println(i);
			}else if(line[i].equals("Phospho (STY) Score Diffs")){
				scoreDiffId = i;System.out.println(i);
			}else if(line[i].startsWith("Score")){
				scoreId = i;System.out.println(i);
			}
		}
		
		HashMap <String, String> contentMap = new HashMap <String, String>();
		HashMap <String, Double> scoreMap = new HashMap <String, Double>();
		HashMap <String, ArrayList <Double>> ratioMap = new HashMap <String, ArrayList <Double>>();
		HashMap <String, ArrayList <Double>> ratioNorMap = new HashMap <String, ArrayList <Double>>();
		
		while((line=reader.readLine())!=null){
			
			String ref = line[refId];
			String seq = line[seqId];
			String modseq = line[modSeqId];
			String pepscore = line[scoreId];
			double ratio = line[ratioId].length()>0 ? Double.parseDouble(line[ratioId]) : 0;
			double ratioNor = line[ratioNorId].length()>0 ? Double.parseDouble(line[ratioNorId]) : 0;
			String probStr = line[probId];
			String scoreDiffStr = line[scoreDiffId];
			
			if(psmap.containsKey(ref)){

				ProteinSequence proseq = psmap.get(ref);
				int beg = proseq.indexOf(seq);
				int id = 0;

				for(int i=1;i<modseq.length()-1;i++){
					
					char aa = modseq.charAt(i);
					if(aa>='A' && aa<='Z'){
						id++;
						
					}else if(aa=='p'){
						
						int loc = id+beg;
						String window = proseq.getSeqAround(loc, 6);
						String site = modseq.charAt(i+1)+""+(loc+1);

						String key = site+"\t"+window+"\t"+ref;

						int proLength = 0;
						double prob = 0;
						String [] probss = probStr.split("[()]");
						for(int j=0;j<probss.length;j+=2){
							proLength += probss[j].length();
							if(proLength==(id+1)){
								prob = Double.parseDouble(probss[j+1]);
								break;
							}
						}
						
						int scoreLength = 0;
						double score = 0;
						String [] scoress = scoreDiffStr.split("[()]");
						for(int j=0;j<scoress.length;j+=2){
							scoreLength += scoress[j].length();
							if(scoreLength==(id+1)){
								score = Double.parseDouble(scoress[j+1]);
								break;
							}
						}
						
						String content = site+"\t"+window+"\t"+psmap.get(ref).getReference()+"\t"+modseq
								+"\t"+probStr+"\t"+scoreDiffStr+"\t"+pepscore
								+"\t"+(id+1)+"\t"+prob+"\t"+score;

						if(scoreMap.containsKey(key)){
							
							if(score>scoreMap.get(key)){
								scoreMap.put(key, score);
								contentMap.put(key, content);
							}
							
							if(ratio!=0) ratioMap.get(key).add(ratio);
							if(ratioNor!=0) ratioNorMap.get(key).add(ratioNor);
							
						}else{
							
							scoreMap.put(key, score);
							contentMap.put(key, content);
							
							ArrayList <Double> ratioList = new ArrayList <Double>();
							ArrayList <Double> ratioNorList = new ArrayList <Double>();
							if(ratio!=0) ratioList.add(ratio);
							if(ratioNor!=0) ratioNorList.add(ratioNor);
							ratioMap.put(key, ratioList);
							ratioNorMap.put(key, ratioNorList);
						}
					}
				}
			}else{
				System.out.println(ref);
			}
		}
		reader.close();
		System.out.println(ratioMap.size());

		Iterator <String> it = scoreMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();

			StringBuilder sb = new StringBuilder();
/*			String [] ss = key.split("\t");
			sb.append(ss[0]).append("\t");
			sb.append(ss[1]).append("\t");
			sb.append(psmap.get(ss[2]).getReference()).append("\t");
*/
			sb.append(contentMap.get(key)).append("\t");
			ArrayList <Double> ratioList = ratioMap.get(key);
			ArrayList <Double> ratioNorList = ratioNorMap.get(key);
			double ratio = MathTool.getAveInDouble(ratioList);
			double ratioNor = MathTool.getAveInDouble(ratioNorList);

			if(ratio!=0) sb.append(ratio).append("\t");
			else sb.append("\t");
			
			if(ratioNor!=0) sb.append(ratioNor).append("\t");
			else sb.append("\t");
			
			writer.addContent(sb.toString(), 0, ef);
		}
		
		writer.close();
	}

}
