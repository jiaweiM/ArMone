/* 
 ******************************************************************************
 * File: VelosCombiner.java * * * Created on 2012-12-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.combiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2012-12-18, 14:59:09
 */
public class VelosCombiner {
	
	private File peptide;
	private File site;
	private HashMap <String, ProteinSequence> psmap;
	
	private HashMap <String, Integer> countmap;
	private HashMap <String, Double> scoremap;
	private HashMap <String, HashMap <String, String []>> contentmap;
	
	public VelosCombiner(File peptide, File site, HashMap <String, ProteinSequence> psmap){
		this.peptide = peptide;
		this.site = site;
		this.psmap = psmap;
		this.countmap = new HashMap <String, Integer>();
		this.scoremap = new HashMap <String, Double>();
		this.contentmap = new HashMap <String, HashMap <String, String []>>();
	}
	
	public void extract() throws IOException, SequenceGenerationException{
		
//		HashMap <String, Integer> posimap = new HashMap <String,Integer>();
		HashMap <String, String> windowmap = new HashMap <String, String>();
		BufferedReader sitereader = new BufferedReader(new FileReader(site));
		String [] sitetitle = sitereader.readLine().split("\t");
		int siteid = -1;
		int siteposid = -1;
		int windowid = -1;
		for(int i=0;i<sitetitle.length;i++){
			if(sitetitle[i].equals("id")){
				siteid = i;
			}else if(sitetitle[i].equals("Position in peptide")){
				siteposid = i;
			}else if(sitetitle[i].equals("Sequence Window")){
				windowid = i;
			}
		}
		
		String siteline = null;
		while((siteline=sitereader.readLine())!=null){
			String [] site = siteline.split("\t");
//			posimap.put(site[siteid], Integer.parseInt(site[siteposid]));
			windowmap.put(site[siteid], site[windowid]);
		}
		sitereader.close();
		
		BufferedReader pepreader = new BufferedReader(new FileReader(peptide));
		String [] peptitle = pepreader.readLine().split("\t");
		int pepaccessid = -1;
		int pepseqid = -1;
		int pepmodid = -1;
		int pepscoreid = -1;

		for(int i=0;i<peptitle.length;i++){
			
			if(peptitle[i].equals("Sequence")){
				pepseqid = i;
				
			}else if(peptitle[i].equals("Leading Razor Protein")){
				pepaccessid = i;
				
			}else if(peptitle[i].equals("Deamidation (N) Site IDs")){
				pepmodid = i;
				
			}else if(peptitle[i].equals("Score")){
				pepscoreid = i;
				
			}
		}
		
		String pepline = null;
		while((pepline=pepreader.readLine())!=null){
			
			String [] pep = pepline.split("\t");
			
			String mod = pep[pepmodid];
			if(mod.trim().length()==0) continue;
			
			String ref = pep[pepaccessid];
			if(ref.startsWith("REV") || ref.startsWith("CON")) continue;

			double pepscore = Double.parseDouble(pep[pepscoreid]);
			
			if(ref.length()!=11){
				ref = ref.substring(5, 16);
			}
			
			HashMap <String, String []> proinfomap = new HashMap <String, String []>();
			
			String seq = pep[pepseqid];
			if(psmap.containsKey(ref)){
				
				ProteinSequence proseq = psmap.get(ref);
				int begin = proseq.indexOf(seq);
				StringBuilder modseqsb = new StringBuilder(seq);
				StringBuilder sitesb = new StringBuilder();
				StringBuilder windowsb = new StringBuilder();
				StringBuilder motifsb = new StringBuilder();

				String [] mods = mod.split(";");

				int count = 0;
				for(int i=0;i<mods.length;i++){

					String window = windowmap.get(mods[i]);
					int position = -1;
					if(window.startsWith("_")){
						
						String realwindow = window.replaceAll("_", "");
						int miss = window.length()-realwindow.length();
						position = proseq.indexOf(realwindow)+7-miss;
						
					}else if(window.endsWith("_")){
						
						String realwindow = window.replaceAll("_", "");
						position = proseq.indexOf(realwindow)+7;
						
					}else{
						position = proseq.indexOf(window)+7;
					}
					
					String aaround = proseq.getSeqAround(position-1);
					
					if(position+2>proseq.length()){
						continue;
					}
					
					if(proseq.getAminoaicdAt(position)!='N'){
						continue;
					}
					
					if(proseq.getAminoaicdAt(position+1)=='P'){
						continue;
					}
					
					char aa2 = proseq.getAminoaicdAt(position+2);
					if(aa2=='S'){
						motifsb.append("N-X-[S|T]").append(',');
					}else if(aa2=='T'){
						motifsb.append("N-X-[S|T]").append(',');
					}else if(aa2=='C'){
						motifsb.append("N-X-C").append(',');
					}else{
						continue;
					}

					modseqsb.insert(count+position-begin, '*');
					sitesb.append(position).append(',');
					windowsb.append(aaround).append(',');
					count++;
				}
				
				if(count==0) continue;
				
				String [] content = new String []{sitesb.substring(0, sitesb.length()-1), 
						windowsb.substring(0, windowsb.length()-1), motifsb.substring(0, motifsb.length()-1)};
				
				proinfomap.put(ref, content);
				String modseq = modseqsb.toString();
				
				if(this.contentmap.containsKey(modseq)){
					this.countmap.put(modseq, countmap.get(modseq)+1);
					if(pepscore>this.scoremap.get(modseq)){
						this.scoremap.put(modseq, pepscore);
					}
				}else{
					this.countmap.put(modseq, 1);
					this.contentmap.put(modseq, proinfomap);
					this.scoremap.put(modseq, pepscore);
				}
			}
		}
		pepreader.close();
	}

	public HashMap <String, Integer> getCountMap(){
		return countmap;
	}
	
	public HashMap <String, Double> getScoreMap(){
		return scoremap;
	}
	
	public HashMap <String, HashMap <String, String[]>> getContentMap(){
		return contentmap;
	}
	
	public static void combine(String in, HashMap <String, Integer> totalCountmap,
			HashMap <String, Double> totalScoremap, HashMap <String, HashMap <String, String[]>> totalContentmap,
			HashMap <String, ProteinSequence> psmap, HashMap <String, String> genemap, HashMap <String, String> uniprotmap) 
					throws IOException, SequenceGenerationException{

		File [] dirs = (new File(in)).listFiles();
		for(int i=0;i<dirs.length;i++){
			
			File [] files = dirs[i].listFiles();
			VelosCombiner combiner;
			System.out.println(dirs[i].getAbsolutePath());
			if(files[0].getName().equals("peptides.txt")){
				combiner = new VelosCombiner(files[0], files[1], psmap);
			}else{
				combiner = new VelosCombiner(files[1], files[0], psmap);
			}
			combiner.extract();
			
			HashMap <String, Integer> countmap = combiner.getCountMap();
			HashMap <String, Double> scoremap = combiner.getScoreMap();
			HashMap <String, HashMap <String, String[]>> contentmap = combiner.getContentMap();
			
			Iterator <String> pepit = countmap.keySet().iterator();
			while(pepit.hasNext()){
				String pep = pepit.next();
				if(totalCountmap.containsKey(pep)){
					totalCountmap.put(pep, totalCountmap.get(pep)+countmap.get(pep));
					if(scoremap.get(pep)>totalScoremap.get(pep)){
						totalScoremap.put(pep, scoremap.get(pep));
					}
					totalContentmap.get(pep).putAll(contentmap.get(pep));
				}else{
					totalCountmap.put(pep, countmap.get(pep));
					totalScoremap.put(pep, scoremap.get(pep));
					totalContentmap.put(pep, contentmap.get(pep));
				}
			}
		}
		
		System.out.println(in+"\t"+totalCountmap.size());
	}
	
	public static void totalCombine(String s1, String s2, String s3, String fasta, String out) 
			throws IOException, SequenceGenerationException, RowsExceededException, WriteException{
		
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
		
		HashMap <String, Integer> totalCountmap1 = new HashMap <String, Integer>();
		HashMap <String, Double> totalScoremap1 = new HashMap <String, Double>();
		HashMap <String, HashMap <String, String[]>> totalContentmap1 = new HashMap <String, HashMap <String, String[]>>();
		VelosCombiner.combine(s1, totalCountmap1, totalScoremap1, totalContentmap1, psmap, genemap, uniprotmap);
		
		HashMap <String, Integer> totalCountmap2 = new HashMap <String, Integer>();
		HashMap <String, Double> totalScoremap2 = new HashMap <String, Double>();
		HashMap <String, HashMap <String, String[]>> totalContentmap2 = new HashMap <String, HashMap <String, String[]>>();
		VelosCombiner.combine(s2, totalCountmap2, totalScoremap2, totalContentmap2, psmap, genemap, uniprotmap);
		
		HashMap <String, Integer> totalCountmap3 = new HashMap <String, Integer>();
		HashMap <String, Double> totalScoremap3 = new HashMap <String, Double>();
		HashMap <String, HashMap <String, String[]>> totalContentmap3 = new HashMap <String, HashMap <String, String[]>>();
		VelosCombiner.combine(s3, totalCountmap3, totalScoremap3, totalContentmap3, psmap, genemap, uniprotmap);
		
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
		writer.addTitle(titlesb.toString(), 0, format);
		
		HashSet <String> set = new HashSet <String>();
		set.addAll(totalCountmap1.keySet());
		set.addAll(totalCountmap2.keySet());
		set.addAll(totalCountmap3.keySet());
		
		Iterator <String> it = set.iterator();
		while(it.hasNext()){
			
			String key = it.next();
			HashMap <String, String[]> contentmap = new HashMap <String, String[]>();
			if(totalContentmap1.containsKey(key)){
				contentmap.putAll(totalContentmap1.get(key));
			}
			if(totalContentmap2.containsKey(key)){
				contentmap.putAll(totalContentmap2.get(key));
			}
			if(totalContentmap3.containsKey(key)){
				contentmap.putAll(totalContentmap3.get(key));
			}
			
			double score = 0;
			if(totalScoremap1.containsKey(key) && totalScoremap1.get(key)>score) score = totalScoremap1.get(key);
			if(totalScoremap2.containsKey(key) && totalScoremap2.get(key)>score) score = totalScoremap2.get(key);
			if(totalScoremap3.containsKey(key) && totalScoremap3.get(key)>score) score = totalScoremap3.get(key);
			
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
				ipisb.append(acc).append(';');
				namesb.append(namemap.get(acc)).append(';');
				genesb.append(genemap.get(acc)).append(';');
				protsb.append(uniprotmap.get(acc)).append(';');
				
				String [] content = contentmap.get(acc);
				locsb.append(content[0]).append(';');
				windowsb.append(content[1]).append(';');
			}
			
			sb.append(ipisb.substring(0, ipisb.length()-1)).append("\t");
			sb.append(namesb.substring(0, namesb.length()-1)).append("\t");
			sb.append(genesb.substring(0, genesb.length()-1)).append("\t");
			sb.append(protsb.substring(0, protsb.length()-1)).append("\t");
			
			sb.append(locsb.substring(0, locsb.length()-1)).append("\t");
			sb.append(key).append("\t");
			sb.append(score).append("\t");
			
			if(totalCountmap1.containsKey(key)){
				sb.append(totalCountmap1.get(key)).append("\t");
			}else{
				sb.append("\t");
			}
			
			if(totalCountmap2.containsKey(key)){
				sb.append(totalCountmap2.get(key)).append("\t");
			}else{
				sb.append("\t");
			}
			
			if(totalCountmap3.containsKey(key)){
				sb.append(totalCountmap3.get(key)).append("\t");
			}else{
				sb.append("\t");
			}
			
			sb.append(windowsb.substring(0, windowsb.length()-1));
			writer.addContent(sb.toString(), 0, format);
		}
		writer.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SequenceGenerationException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws IOException, SequenceGenerationException, 
		RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		
		String s1 = "H:\\glyco_combine\\Hydrazide\\velos\\trypsin\\protein";
		String s2 = "H:\\glyco_combine\\Hydrazide\\velos\\T+C\\protein";
		String s3 = "H:\\glyco_combine\\Hydrazide\\velos\\chymotrypsin\\protein";

		String out = "H:\\glyco_combine\\Hydrazide.velos.protein.xls";

		VelosCombiner.totalCombine(s1, s2, s3, "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta", out);
	}

}
