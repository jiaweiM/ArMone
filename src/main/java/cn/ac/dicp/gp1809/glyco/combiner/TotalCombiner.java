/* 
 ******************************************************************************
 * File: TotalCombiner.java * * * Created on 2012-12-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.combiner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.JXLException;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2012-12-19, 13:46:17
 */
public class TotalCombiner {
	
	public static void siteCombine(String s1, String s2, String s3, String s4, String out) 
			throws IOException, JXLException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Protein\t");
		titlesb.append("Protein Name\t");
		titlesb.append("Gene Name\t");
		titlesb.append("Uniprot\t");
		titlesb.append("Position\t");
		titlesb.append("sequence window\t");
		titlesb.append("Identification counts\t");
		titlesb.append("5600\t");
		titlesb.append("Velos\t");
		titlesb.append("HILIC\t");
		titlesb.append("Hydrazide\t");
		titlesb.append("N-X-[S|T]\t");
		titlesb.append("N-X-C\t");
		writer.addTitle(titlesb.toString(), 0, format);
		writer.addTitle(titlesb.toString(), 1, format);
		
		HashMap <String, String []> contentmap = new HashMap <String, String []>();
//		HashMap <String, Integer> countmap = new HashMap <String, Integer>();
		HashSet <String> m1 = new HashSet <String>();
		HashSet <String> m2 = new HashSet <String>();
		HashSet <String> m3 = new HashSet <String>();
		HashSet <String> m4 = new HashSet <String>();
		
		ExcelReader r1 = new ExcelReader(s1);
		String [] l1 = r1.readLine();
		while((l1=r1.readLine())!=null){
			String key = l1[2]+l1[5];
			m1.add(key);
			if(contentmap.containsKey(key)){
				String [] ori = contentmap.get(key);
				if(l1[3].length()>1 && l1[3].length()<ori[3].length()){
					contentmap.put(key, l1);
				}
//				countmap.put(key, countmap.get(key)+Integer.parseInt(l1[7]));
			}else{
				contentmap.put(key, l1);
//				countmap.put(key, Integer.parseInt(l1[7]));
			}
		}
		
		ExcelReader r2 = new ExcelReader(s2);
		String [] l2 = r2.readLine();
		while((l2=r2.readLine())!=null){
			String key = l2[2]+l2[5];
			m2.add(key);
			if(contentmap.containsKey(key)){
				String [] ori = contentmap.get(key);
				if(l2[3].length()>1 && l2[3].length()<ori[3].length()){
					contentmap.put(key, l2);
				}
//				countmap.put(key, countmap.get(key)+Integer.parseInt(l2[7]));
			}else{
				contentmap.put(key, l2);
//				countmap.put(key, Integer.parseInt(l2[7]));
			}
		}
		
		/*ExcelReader r3 = new ExcelReader(s3);
		String [] l3 = r3.readLine();
		while((l3=r3.readLine())!=null){
			String key = l3[2]+l3[5];
			m3.add(key);
			if(contentmap.containsKey(key)){
				String [] ori = contentmap.get(key);
				if(l3[3].length()>1 && l3[3].length()<ori[3].length()){
					contentmap.put(key, l3);
				}
				countmap.put(key, countmap.get(key)+Integer.parseInt(l3[7]));
			}else{
				contentmap.put(key, l3);
				countmap.put(key, Integer.parseInt(l3[7]));
			}
		}
		
		ExcelReader r4 = new ExcelReader(s4);
		String [] l4 = r4.readLine();
		while((l4=r4.readLine())!=null){
			String key = l4[2]+l4[5];
			m4.add(key);
			if(contentmap.containsKey(key)){
				String [] ori = contentmap.get(key);
				if(l4[3].length()>1 && l4[3].length()<ori[3].length()){
					contentmap.put(key, l4);
				}
				countmap.put(key, countmap.get(key)+Integer.parseInt(l4[7]));
			}else{
				contentmap.put(key, l4);
				countmap.put(key, Integer.parseInt(l4[7]));
			}
		}*/
		
		Iterator <String> it = contentmap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String [] content = contentmap.get(key);
			StringBuilder sb = new StringBuilder();
			if(content[0].contains(":")){
				sb.append(content[0].substring(4, 15)).append("\t");
			}else{
				sb.append(content[0]).append("\t");
			}
			for(int i=1;i<6;i++){
				sb.append(content[i]).append("\t");
			}
//			sb.append(countmap.get(key)).append("\t");
			/*if(m1.contains(key) || m3.contains(key)){
				sb.append("+\t");
			}else{
				sb.append("\t");
			}
			if(m2.contains(key) || m4.contains(key)){
				sb.append("+\t");
			}else{
				sb.append("\t");
			}
			if(m1.contains(key) || m2.contains(key)){
				sb.append("+\t");
			}else{
				sb.append("\t");
			}
			if(m3.contains(key) || m4.contains(key)){
				sb.append("+\t");
			}else{
				sb.append("\t");
			}*/
			
			if(content[6].equals("N-X-C")){
				sb.append("\t+\t");
				writer.addContent(sb.toString(), 0, format);
			}else if(content[6].equals("Other")){
				sb.append("Other");
				writer.addContent(sb.toString(), 1, format);
			}else{
				sb.append("+\t\t");
				writer.addContent(sb.toString(), 0, format);
			}
		}
		
		writer.close();
	}
	
	public static void combine(String s1, String s2, String out, String fasta) throws IOException, JXLException{
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		HashMap <String, String> namemap = new HashMap <String, String>();
		HashMap <String, String> genemap = new HashMap <String, String>();
		HashMap <String, String> uniprotmap = new HashMap <String, String>();
		
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String name = ps.getName();
			String gene = ps.getGene();
			String uniprot = ps.getSWISS();
			String key = ref.substring(4, 15);
			
			psmap.put(key, ps);
			namemap.put(key, name);
			genemap.put(key, gene);
			uniprotmap.put(key, uniprot);
		}
		fr.close();
		
		HashMap <String, HashMap <String, String []>> totalContentMap = new HashMap <String, HashMap <String, String[]>>();
		HashMap <String, Double> totalScoreMap = new HashMap <String, Double>();
		HashMap <String, Integer> totalCountMap1 = new HashMap <String, Integer>();
		HashMap <String, Integer> totalCountMap2 = new HashMap <String, Integer>();
		HashMap <String, Integer> totalCountMap3 = new HashMap <String, Integer>();
		HashMap <String, String> totalExistMap = new HashMap <String, String>();
		
		ExcelReader r1 = new ExcelReader(s1);
		String [] l1 = r1.readLine();
		while((l1=r1.readLine())!=null){
			
			String [] accs = l1[0].split(";");
			String [] pros = l1[4].split(";");
			String [] windows = l1[10].split(";");
			for(int i=0;i<accs.length;i++){
				if(accs[i].length()>11){
					accs[i] = accs[i].substring(4, 15);
				}
			}

			double score = Double.parseDouble(l1[6]);
			int count1 = l1[7].length()==0 ? 0 : Integer.parseInt(l1[7]);
			int count2 = l1[8].length()==0 ? 0 : Integer.parseInt(l1[8]);
			int count3 = l1[9].length()==0 ? 0 : Integer.parseInt(l1[9]);
			
			HashMap <String, String []> contentmap = new HashMap <String, String []>();
			for(int i=0;i<accs.length;i++){
				contentmap.put(accs[i], new String []{namemap.get(accs[i]), genemap.get(accs[i]), 
						uniprotmap.get(accs[i]), pros[i], windows[i]});
			}
			
			totalContentMap.put(l1[5], contentmap);
			totalScoreMap.put(l1[5], score);
			totalCountMap1.put(l1[5], count1);
			totalCountMap2.put(l1[5], count2);
			totalCountMap3.put(l1[5], count3);
			totalExistMap.put(l1[5], "+");
		}
		r1.close();
		
		ExcelReader r2 = new ExcelReader(s2);
		String [] l2 = r2.readLine();
		while((l2=r2.readLine())!=null){
			
			String [] accs = l2[0].split(";");
			String [] pros = l2[4].split(";");
			String [] windows = l2[10].split(";");
			for(int i=0;i<accs.length;i++){
				if(accs[i].length()>11){
					accs[i] = accs[i].substring(4, 15);
				}
			}
			
			double score = Double.parseDouble(l2[6]);
			int count1 = l2[7].length()==0 ? 0 : Integer.parseInt(l2[7]);
			int count2 = l2[8].length()==0 ? 0 : Integer.parseInt(l2[8]);
			int count3 = l2[9].length()==0 ? 0 : Integer.parseInt(l2[9]);
			
			HashMap <String, String []> contentmap = new HashMap <String, String []>();
			for(int i=0;i<accs.length;i++){
				contentmap.put(accs[i], new String []{namemap.get(accs[i]), genemap.get(accs[i]), 
						uniprotmap.get(accs[i]), pros[i], windows[i]});
			}
			
			if(totalContentMap.containsKey(l2[5])){
				totalContentMap.get(l2[5]).putAll(contentmap);
				if(score>totalScoreMap.get(l2[5])) totalScoreMap.put(l2[5], score);
				totalCountMap1.put(l2[5], totalCountMap1.get(l2[5])+count1);
				totalCountMap2.put(l2[5], totalCountMap2.get(l2[5])+count2);
				totalCountMap3.put(l2[5], totalCountMap3.get(l2[5])+count3);
				totalExistMap.put(l2[5], "+\t+");
			}else{
				totalContentMap.put(l2[5], contentmap);
				totalScoreMap.put(l2[5], score);
				totalCountMap1.put(l2[5], count1);
				totalCountMap2.put(l2[5], count2);
				totalCountMap3.put(l2[5], count3);
				totalExistMap.put(l2[5], "\t+");
			}
		}
		r2.close();
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Protein\t");
		titlesb.append("Protein Name\t");
		titlesb.append("Gene Name\t");
		titlesb.append("Uniprot\t");
		titlesb.append("Position\t");
		titlesb.append("sequence\t");
		titlesb.append("score\t");
		titlesb.append("number of identifications (trypsin)\t");
		titlesb.append("number of identifications (trypsin+Glu-C)\t");
		titlesb.append("number of identifications (chymotrypsin)\t");
		titlesb.append("sequence window\t");
		titlesb.append("HILIC\t");
		titlesb.append("Hydrazide\t");
		writer.addTitle(titlesb.toString(), 0, format);
		
		Iterator <String> it = totalContentMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			HashMap <String, String[]> contentmap = totalContentMap.get(key);
			double score = totalScoreMap.get(key);
			
			StringBuilder sb = new StringBuilder();
			StringBuilder ipisb = new StringBuilder();
			StringBuilder namesb = new StringBuilder();
			StringBuilder genesb = new StringBuilder();
			StringBuilder protsb = new StringBuilder();
			
			StringBuilder locsb = new StringBuilder();
			StringBuilder windowsb = new StringBuilder();
			
			Iterator <String> proit = contentmap.keySet().iterator();
			while(proit.hasNext()){
				String acc = proit.next();
				String [] content = contentmap.get(acc);
				ipisb.append(acc).append(';');
				namesb.append(content[0]).append(';');
				genesb.append(content[1]).append(';');
				protsb.append(content[2]).append(';');				
				locsb.append(content[3]).append(';');
				windowsb.append(content[4]).append(';');
			}
			
			sb.append(ipisb.substring(0, ipisb.length()-1)).append("\t");
			sb.append(namesb.substring(0, namesb.length()-1)).append("\t");
			sb.append(genesb.substring(0, genesb.length()-1)).append("\t");
			sb.append(protsb.substring(0, protsb.length()-1)).append("\t");
			
			sb.append(locsb.substring(0, locsb.length()-1)).append("\t");
			sb.append(key).append("\t");
			sb.append(score).append("\t");
			
			if(totalCountMap1.get(key)==0){
				sb.append("\t");
			}else{
				sb.append(totalCountMap1.get(key)).append("\t");
			}
			
			if(totalCountMap2.get(key)==0){
				sb.append("\t");
			}else{
				sb.append(totalCountMap2.get(key)).append("\t");
			}
			
			if(totalCountMap3.get(key)==0){
				sb.append("\t");
			}else{
				sb.append(totalCountMap3.get(key)).append("\t");
			}
			
			sb.append(windowsb.substring(0, windowsb.length()-1)).append("\t");
			sb.append(totalExistMap.get(key));
			writer.addContent(sb.toString(), 0, format);
		}
		writer.close();
	}

	public static void siteExtract(String s1, String s2, String out, String fasta) throws IOException, JXLException{
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		HashMap <String, String> namemap = new HashMap <String, String>();
		HashMap <String, String> genemap = new HashMap <String, String>();
		HashMap <String, String> uniprotmap = new HashMap <String, String>();
		HashMap <String, Boolean> hilicmap = new HashMap <String, Boolean>();
		HashMap <String, Boolean> hydrazidemap = new HashMap <String, Boolean>();
		
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String name = ps.getName();
			String gene = ps.getGene();
			String uniprot = ps.getSWISS();
			String key = ref.substring(4, 15);
			
			psmap.put(key, ps);
			namemap.put(key, name);
			genemap.put(key, gene);
			uniprotmap.put(key, uniprot);
		}
		fr.close();
		
		HashMap <String, HashMap <String, String []>> totalContentMap = 
				new HashMap <String, HashMap <String, String[]>>();
		HashMap <String, String> totalExistMap = new HashMap <String, String>();
		
		ExcelReader r1 = new ExcelReader(s1);
		String [] l1 = r1.readLine();
		while((l1=r1.readLine())!=null){
			
			String [] accs = l1[0].split(";");
			String [] pros = l1[4].split(";");
			String [] windows = l1[10].split(";");

			HashMap <String, String []> contentmap = new HashMap <String, String []>();
			
			for(int i=0;i<accs.length;i++){
				contentmap.put(accs[i], new String []{namemap.get(accs[i]), genemap.get(accs[i]), 
						uniprotmap.get(accs[i]), pros[i], windows[i]});
			}

			totalContentMap.put(l1[5], contentmap);
			totalExistMap.put(l1[5], "+\t");
			if(l1[11].contains("+")){
				hilicmap.put(l1[5], true);
			}else{
				hilicmap.put(l1[5], false);
			}
			if(l1.length>12 && l1[12].contains("+")){
				hydrazidemap.put(l1[5], true);
			}else{
				hydrazidemap.put(l1[5], false);
			}
		}
		r1.close();
		
		ExcelReader r2 = new ExcelReader(s2);
		String [] l2 = r2.readLine();
		while((l2=r2.readLine())!=null){
			
			String [] accs = l2[0].split(";");
			String [] pros = l2[4].split(";");
			String [] windows = l2[10].split(";");

			HashMap <String, String []> contentmap = new HashMap <String, String []>();
			for(int i=0;i<accs.length;i++){
				contentmap.put(accs[i], new String []{namemap.get(accs[i]), genemap.get(accs[i]), 
						uniprotmap.get(accs[i]), pros[i], windows[i]});
			}
			
			if(totalContentMap.containsKey(l2[5])){
				totalContentMap.get(l2[5]).putAll(contentmap);
				totalExistMap.put(l2[5], "+\t+");
			}else{
				totalContentMap.put(l2[5], contentmap);
				totalExistMap.put(l2[5], "\t+");
			}
			
			if(l2[11].contains("+")){
				hilicmap.put(l2[5], true);
			}else{
				hilicmap.put(l2[5], false);
			}
			if(l2.length>12 && l2[12].contains("+")){
				hydrazidemap.put(l2[5], true);
			}else{
				hydrazidemap.put(l2[5], false);
			}
		}
		r2.close();
		System.out.println("peptide\t"+totalContentMap.size());
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Protein\t");
		titlesb.append("Protein Name\t");
		titlesb.append("Gene Name\t");
		titlesb.append("Uniprot\t");
		titlesb.append("Position\t");
		titlesb.append("sequence window\t");
		titlesb.append("5600\t");
		titlesb.append("Velos\t");
		titlesb.append("HILIC\t");
		titlesb.append("Hydrazide\t");
		titlesb.append("N-X-[S|T]\t");
		titlesb.append("N-X-C\t");
		writer.addTitle(titlesb.toString(), 0, format);
		
		HashMap <String, String[]> finalmap = new HashMap <String, String[]>();
		
		Iterator <String> it = totalContentMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			HashMap <String, String[]> contentmap = totalContentMap.get(key);
			String [] accs = contentmap.keySet().toArray(new String [contentmap.size()]);
			
			String hh = "";
			if(hilicmap.get(key)){
				if(hydrazidemap.get(key)){
					hh = "+\t+";
				}else{
					hh = "+\t";
				}
			}else{
				if(hydrazidemap.get(key)){
					hh = "\t+";
				}else{
					System.out.println("hh\t"+key);
				}
			}
			
			if(contentmap.size()>1){
				
				int selectid = -1;
				String prot = "";

				for(int i=0;i<accs.length;i++){
					String [] content = contentmap.get(accs[i]);
					if(content[2].length()>1){
						if(prot.length()==0){
							prot = content[2];
							selectid = i;
						}else{
							if(content[2].compareTo(prot)<0){
								prot = content[2];
								selectid = i;
							}
						}
					}
				}
				
				String name = "";
				if(selectid==-1){
					for(int i=0;i<accs.length;i++){
						String [] content = contentmap.get(accs[i]);
						if(content[0].length()>1){
							if(name.length()==0){
								name = content[0];
								selectid = i;
							}else{
								if(name.contains("protein")){
									if(content[0].contains("protein")){
										if(content[0].length()<name.length()){
											name = content[0];
											selectid = i;
										}
									}else{
										name = content[0];
										selectid = i;
									}
								}else{
									if(!content[0].contains("protein")){
										if(content[0].length()<name.length()){
											name = content[0];
											selectid = i;
										}
									}
								}
							}
						}
					}
				}

				String [] content = contentmap.get(accs[selectid]);
				String [] positions = content[3].split(",");
				String [] windows = content[4].split(",");
				
				for(int i=0;i<windows.length;i++){
					
					String motif = "";
					if(windows[i].length()==15){
						if(windows[i].charAt(9)=='C'){
							motif = "\t+";
						}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
							motif = "+\t";
						}
					}else{
						if(windows[i].charAt(7)=='N'){
							if(windows[i].charAt(9)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
								motif = "+\t";
							}
						}else{
							if(windows[i].charAt(windows[i].length()-6)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(windows[i].length()-6)=='S' || 
									windows[i].charAt(windows[i].length()-6)=='T'){
								motif = "+\t";
							}
						}
					}
					if(!motif.contains("+")){
						System.out.println(windows[i]);
						continue;
					}
					
					if(finalmap.containsKey(windows[i])){
						
						String [] oricontent = finalmap.get(windows[i]);
						if(!oricontent[3].equals(content[3])){
							if(content[3].length()>1){
								if(oricontent[3].length()>1){
									
									if(content[3].compareTo(oricontent[3])<0){

										String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
												positions[i], totalExistMap.get(key), hh, motif};
										
										finalmap.put(windows[i], finalcontent);
									}
									
								}else{

									String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
											positions[i], totalExistMap.get(key), hh, motif};
									
									finalmap.put(windows[i], finalcontent);
								}
							}
						}
						
					}else{

						String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
								positions[i], windows[i], totalExistMap.get(key), hh, motif};
						
						finalmap.put(windows[i]+"_"+content[1], finalcontent);
					}
				}
				
			}else{
				
				String [] content = contentmap.get(accs[0]);
				String [] positions = content[3].split(",");
				String [] windows = content[4].split(",");
				
				for(int i=0;i<windows.length;i++){
					
					String motif = "";
					if(windows[i].length()==15){
						if(windows[i].charAt(9)=='C'){
							motif = "\t+";
						}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
							motif = "+\t";
						}
					}else{
						if(windows[i].charAt(7)=='N'){
							if(windows[i].charAt(9)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
								motif = "+\t";
							}
						}else{
							if(windows[i].charAt(windows[i].length()-6)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(windows[i].length()-6)=='S' || 
									windows[i].charAt(windows[i].length()-6)=='T'){
								motif = "+\t";
							}
						}
					}
					if(!motif.contains("+")){
						System.out.println(windows[i]);
						continue;
					}
					
					if(finalmap.containsKey(windows[i])){
						
						String [] oricontent = finalmap.get(windows[i]);
						if(!oricontent[3].equals(content[3])){
							if(content[3].length()>1){
								if(oricontent[3].length()>1){
									
									if(content[3].compareTo(oricontent[3])<0){

										String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
												positions[i], totalExistMap.get(key), hh, motif};
										
										finalmap.put(windows[i], finalcontent);
									
									}
									
								}else{
									String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
											positions[i], totalExistMap.get(key), hh, motif};
									
									finalmap.put(windows[i], finalcontent);
								}
							}
						}
						
					}else{
						String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
								positions[i], windows[i], totalExistMap.get(key), hh, motif};
						
						finalmap.put(windows[i]+"_"+content[1], finalcontent);
					}
				}
			}
		}
		
//		PrintWriter pw = new PrintWriter("H:\\glyco_combine\\test.txt");
		Iterator <String> finit = finalmap.keySet().iterator();
		while(finit.hasNext()){
			String key = finit.next();
			StringBuilder sb = new StringBuilder();
			String [] content = finalmap.get(key);
//			System.out.println(content.length);
			for(int i=0;i<content.length;i++){
				sb.append(content[i]).append("\t");
			}
			if(key.equals("LIRVILYNRTRLDCP_NFASC")){
				System.out.println(content[6]);
				System.out.println(content[7]);
			}
//			System.out.println(content[content.length-2]);
//			if(content.length!=10) System.out.println("mipa");
			writer.addContent(sb.toString(), 0, format);
//			pw.write(sb+"\n");
		}
		writer.close();
//		pw.close();
	}
	
	public static void siteExtract(String s1, String out, String fasta) throws IOException, JXLException{
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		HashMap <String, String> namemap = new HashMap <String, String>();
		HashMap <String, String> genemap = new HashMap <String, String>();
		HashMap <String, String> uniprotmap = new HashMap <String, String>();

		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String name = ps.getName();
			String gene = ps.getGene();
			String uniprot = ps.getSWISS();
//			String key = ref.substring(4, 15);
			String key = ref.substring(0, ref.indexOf("|"));
			
			psmap.put(key, ps);
			namemap.put(key, name);
			genemap.put(key, gene);
			uniprotmap.put(key, uniprot);
		}
		fr.close();
		
		HashMap <String, HashMap <String, String []>> totalContentMap = 
				new HashMap <String, HashMap <String, String[]>>();
		
		ExcelReader r1 = new ExcelReader(s1);
		String [] l1 = r1.readLine();
		while((l1=r1.readLine())!=null){
			
			String [] accs = l1[0].split(";");
			String [] pros = l1[4].split(";");
			String [] windows = l1[10].split(";");

			HashMap <String, String []> contentmap = new HashMap <String, String []>();
			
			for(int i=0;i<accs.length;i++){
				contentmap.put(accs[i], new String []{namemap.get(accs[i]), genemap.get(accs[i]), 
						uniprotmap.get(accs[i]), pros[i], windows[i]});
			}
			
			totalContentMap.put(l1[5], contentmap);
		}
		r1.close();

		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Protein\t");
		titlesb.append("Protein Name\t");
		titlesb.append("Gene Name\t");
		titlesb.append("Uniprot\t");
		titlesb.append("Position\t");
		titlesb.append("sequence window\t");
		titlesb.append("5600\t");
		titlesb.append("Velos\t");
		titlesb.append("HILIC\t");
		titlesb.append("Hydrazide\t");
		titlesb.append("N-X-[S|T]\t");
		titlesb.append("N-X-C\t");
		writer.addTitle(titlesb.toString(), 0, format);
		
		HashMap <String, String[]> finalmap = new HashMap <String, String[]>();
		
		Iterator <String> it = totalContentMap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			HashMap <String, String[]> contentmap = totalContentMap.get(key);
			String [] accs = contentmap.keySet().toArray(new String [contentmap.size()]);
			
			String hh = "";

			if(contentmap.size()>1){
				
				int selectid = -1;
				String prot = "";

				for(int i=0;i<accs.length;i++){
					String [] content = contentmap.get(accs[i]);
					if(content[2].length()>1){
						if(prot.length()==0){
							prot = content[2];
							selectid = i;
						}else{
							if(content[2].compareTo(prot)<0){
								prot = content[2];
								selectid = i;
							}
						}
					}
				}
				
				String name = "";
				if(selectid==-1){
					for(int i=0;i<accs.length;i++){
						String [] content = contentmap.get(accs[i]);
						if(content[0].length()>1){
							if(name.length()==0){
								name = content[0];
								selectid = i;
							}else{
								if(name.contains("protein")){
									if(content[0].contains("protein")){
										if(content[0].length()<name.length()){
											name = content[0];
											selectid = i;
										}
									}else{
										name = content[0];
										selectid = i;
									}
								}else{
									if(!content[0].contains("protein")){
										if(content[0].length()<name.length()){
											name = content[0];
											selectid = i;
										}
									}
								}
							}
						}
					}
				}

				String [] content = contentmap.get(accs[selectid]);
				String [] positions = content[3].split(",");
				String [] windows = content[4].split(",");
				
				for(int i=0;i<windows.length;i++){
					
					String motif = "";
					if(windows[i].length()==15){
						if(windows[i].charAt(9)=='C'){
							motif = "\t+";
						}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
							motif = "+\t";
						}
					}else{
						if(windows[i].charAt(7)=='N'){
							if(windows[i].charAt(9)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
								motif = "+\t";
							}
						}else{
							if(windows[i].charAt(windows[i].length()-6)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(windows[i].length()-6)=='S' || 
									windows[i].charAt(windows[i].length()-6)=='T'){
								motif = "+\t";
							}
						}
					}
					if(!motif.contains("+")){
						System.out.println(windows[i]);
						continue;
					}
					
					if(finalmap.containsKey(windows[i])){
						
						String [] oricontent = finalmap.get(windows[i]);
						if(!oricontent[3].equals(content[3])){
							if(content[3].length()>1){
								if(oricontent[3].length()>1){
									
									if(content[3].compareTo(oricontent[3])<0){

										String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
												positions[i], hh, motif};
										
										finalmap.put(windows[i], finalcontent);
									}
									
								}else{

									String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
											positions[i], hh, motif};
									
									finalmap.put(windows[i], finalcontent);
								}
							}
						}
						
					}else{

						String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
								positions[i], windows[i], hh, motif};
						
						finalmap.put(windows[i]+"_"+content[1], finalcontent);
					}
				}
				
			}else{
				
				String [] content = contentmap.get(accs[0]);
				String [] positions = content[3].split(",");
				String [] windows = content[4].split(",");
				
				for(int i=0;i<windows.length;i++){
					
					String motif = "";
					if(windows[i].length()==15){
						if(windows[i].charAt(9)=='C'){
							motif = "\t+";
						}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
							motif = "+\t";
						}
					}else{
						if(windows[i].charAt(7)=='N'){
							if(windows[i].charAt(9)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(9)=='S' || windows[i].charAt(9)=='T'){
								motif = "+\t";
							}
						}else{
							if(windows[i].charAt(windows[i].length()-6)=='C'){
								motif = "\t+";
							}else if(windows[i].charAt(windows[i].length()-6)=='S' || 
									windows[i].charAt(windows[i].length()-6)=='T'){
								motif = "+\t";
							}
						}
					}
					if(!motif.contains("+")){
						System.out.println(windows[i]);
						continue;
					}
					
					if(finalmap.containsKey(windows[i])){
						
						String [] oricontent = finalmap.get(windows[i]);
						if(!oricontent[3].equals(content[3])){
							if(content[3].length()>1){
								if(oricontent[3].length()>1){
									
									if(content[3].compareTo(oricontent[3])<0){

										String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
												positions[i], hh, motif};
										
										finalmap.put(windows[i], finalcontent);
									
									}
									
								}else{
									String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
											positions[i], hh, motif};
									
									finalmap.put(windows[i], finalcontent);
								}
							}
						}
						
					}else{
						String [] finalcontent = new String []{accs[0], content[0], content[1], content[2],
								positions[i], windows[i], hh, motif};
						
						finalmap.put(windows[i]+"_"+content[1], finalcontent);
					}
				}
			}
		}
		
//		PrintWriter pw = new PrintWriter("H:\\glyco_combine\\test.txt");
		Iterator <String> finit = finalmap.keySet().iterator();
		while(finit.hasNext()){
			String key = finit.next();
			StringBuilder sb = new StringBuilder();
			String [] content = finalmap.get(key);
//			System.out.println(content.length);
			for(int i=0;i<content.length;i++){
				sb.append(content[i]).append("\t");
			}
			if(key.equals("LIRVILYNRTRLDCP_NFASC")){
				System.out.println(content[6]);
				System.out.println(content[7]);
			}
//			System.out.println(content[content.length-2]);
//			if(content.length!=10) System.out.println("mipa");
			writer.addContent(sb.toString(), 0, format);
//			pw.write(sb+"\n");
		}
		writer.close();
//		pw.close();
	}
	
	public static void test(String file, String pep, String site) throws IOException, JXLException{
		
		ExcelReader pepreader = new ExcelReader(pep);
		String [] pepline = pepreader.readLine();
		HashSet <String> pepset = new HashSet <String>();
		while((pepline=pepreader.readLine())!=null){
			String seq = pepline[5].replace("*", "g");
//			System.out.println(seq);
			pepset.add(seq);
		}
		pepreader.close();
		
		ExcelReader sitereader = new ExcelReader(site);
		String [] siteline = sitereader.readLine();
		HashSet <String> siteset = new HashSet <String>();
		while((siteline=sitereader.readLine())!=null){
//			System.out.println(seq);
			siteset.add(siteline[5]);
		}
		sitereader.close();
		
		File [] xls = (new File(file)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("xls"))
					return true;
				return false;
			}
			
		});
		
		for(int i=0;i<xls.length;i++){
			ExcelReader ri = new ExcelReader(xls[i]);
			String [] li = ri.readLine();
			while((li=ri.readLine())!=null){
				if(!pepset.contains(li[0])){
					System.out.println(li[0]+"\n"+xls[i].getName());
				}
				if(!siteset.contains(li[5])){
					System.out.println("site\t"+li[5]+"\t"+li[0]+"\t"+xls[i].getName());
				}
			}
			ri.close();
		}
	}
	
	private static void compare(String s1, String s2) throws IOException, JXLException{
		HashSet<String> set = new HashSet<String>();
		ExcelReader r1 = new ExcelReader(s1);
		String[] l1 = r1.readLine();
		while((l1=r1.readLine())!=null){
			set.add(l1[2]+"\t"+l1[5]);
		}
		r1.close();
		
		ExcelReader r2 = new ExcelReader(s2);
		String[] l2 = r2.readLine();
		while((l2=r2.readLine())!=null){
			set.add(l2[2]+"\t"+l2[5]);
		}
		r2.close();
		System.out.println(set.size());
	}

	private static void test2(String result, String in, String out) throws IOException, JXLException{
		HashSet<String> set = new HashSet<String>();
		ExcelReader r1 = new ExcelReader(result);
		String[] line = r1.readLine();
		while((line=r1.readLine())!=null){
			if(line[10].equals("+")){
				set.add("\t"+line[5]);
			}
		}
		r1.close();
		System.out.println(set.size());
		
		ExcelReader r2 = new ExcelReader(in);
		line = r2.readLine();
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		writer.addTitle(line, 0, format);
		while((line=r2.readLine())!=null){
			String key = "\t"+line[5];
			if(set.contains(key)){
				writer.addContent(line, 0, format);
			}
		}
		r2.close();
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

//		String s1 = "H:\\glyco_combine\\2014.01.07\\Hydrazide.5600.protein.xls";
//		String s2 = "H:\\glyco_combine\\Hydrazide.velos.combine.2.xls";
//		String out = "H:\\glyco_combine\\2014.01.07\\Hydrazide.protein.site.xls";
		String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta";
//		TotalCombiner.combine(s1, s2, out, fasta);

		String s1 = "H:\\glyco_combine\\2014.01.07\\Hydrazide.velos.protein.xls";
		String s2 = "H:\\glyco_combine\\2014.01.07\\Hydrazide.5600.protein.xls";
//		String out = "H:\\glyco_combine\\HILIC\\5600\\chymotrypsin\\test.site.xls";
//		TotalCombiner.siteExtract(s1, s2, out, fasta);
//		TotalCombiner.siteExtract(s1, out, fasta);
//		TotalCombiner.test("H:\\glyco_combine\\HILIC\\5600\\chymotrypsin\\test\\1", 
//				"H:\\glyco_combine\\HILIC\\5600\\chymotrypsin\\test.xls",
//				"H:\\glyco_combine\\HILIC\\5600\\chymotrypsin\\test.site.xls");
		
		String ns1 = "H:\\glyco_combine\\2014.01.07\\Hydrazide.5600.peptide.site.xls";
		String ns2 = "H:\\glyco_combine\\2014.01.07\\Hydrazide.velos.peptide.site.xls";
		String ns3 = "H:\\glyco_combine\\new\\Hydrazide.5600.site.2.xls";
		String ns4 = "H:\\glyco_combine\\new\\Hydrazide.velos.site.2.xls";
		String out = "H:\\glyco_combine\\2014.01.07\\Hydrazide.peptide.site.xls";
//		TotalCombiner.siteCombine(ns1, ns2, ns3, ns4, out);
		TotalCombiner.compare("H:\\glyco_combine\\2014.01.07\\Hydrazide.peptide.site2.xls", "H:\\glyco_combine\\2014.01.07\\Hydrazide.protein.site2.xls");
//		TotalCombiner.test2("H:\\glyco_combine\\2014.01.07\\Table S2.xls", 
//				"H:\\glyco_combine\\2014.01.07\\Hydrazide.peptide.site.xls", "H:\\glyco_combine\\2014.01.07\\Hydrazide.peptide.site2.xls");
	}

}
