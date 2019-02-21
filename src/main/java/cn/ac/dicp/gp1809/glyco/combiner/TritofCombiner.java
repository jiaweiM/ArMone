/* 
 ******************************************************************************
 * File: TritofCombiner.java * * * Created on 2012-12-18
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
import java.util.ArrayList;
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
 * @version 2012-12-18, 9:38:45
 */
public class TritofCombiner {
	
	private File peptideSummary;
	private HashMap <String, ProteinSequence> psmap;
	private double conf;
	
	private HashMap <String, Integer> countmap;
	private HashMap <String, Double> scoremap;
	private HashMap <String, HashMap <String, String []>> contentmap;

	public TritofCombiner(File peptideSummary, double conf,
			HashMap <String, ProteinSequence> psmap){
		
		this.peptideSummary = peptideSummary;
		this.conf = conf;
		this.psmap = psmap;
		this.countmap = new HashMap <String, Integer>();
		this.scoremap = new HashMap <String, Double>();
		this.contentmap = new HashMap <String, HashMap <String, String []>>();
	}

	public void extract() throws IOException, SequenceGenerationException{
		
/*		HashMap <String, Double> proScoreMap = new HashMap <String, Double>();
		
		BufferedReader proreader = new BufferedReader(new FileReader(proteinSummary));
		String proline = proreader.readLine();
		String [] protitle = proline.split("\t");
		int proscore = -1;
		int proaccess = -1;
		
		for(int i=0;i<protitle.length;i++){
			if(protitle[i].equals("Total")){
				proscore = i;
				
			}else if(protitle[i].equals("Accession")){
				proaccess = i;
			}
		}
		
		while((proline=proreader.readLine())!=null){
			String [] pro = proline.split("\t");
			double score = Double.parseDouble(pro[proscore]);
			proScoreMap.put(pro[proaccess], score);
		}
		proreader.close();
*/		
		BufferedReader pepreader = new BufferedReader(new FileReader(peptideSummary));
		String [] peptitle = pepreader.readLine().split("\t");
		int pepaccess = -1;
		int pepseq = -1;
		int pepmod = -1;
		int pepconf = -1;

		for(int i=0;i<peptitle.length;i++){
			
			if(peptitle[i].equals("Sequence")){
				pepseq = i;
				
			}else if(peptitle[i].equals("Accessions")){
				pepaccess = i;
				
			}else if(peptitle[i].equals("Modifications")){
				pepmod = i;
				
			}else if(peptitle[i].equals("Conf")){
				pepconf = i;
				
			}
		}

		String pepline = null;
		while((pepline=pepreader.readLine())!=null){

			String [] pep = pepline.split("\t");
			
			double pepconfscore = Double.parseDouble(pep[pepconf]);
			if(pepconfscore<this.conf)
				continue;
			
			String seq = pep[pepseq];
			String mod = pep[pepmod];
			
			if(mod.trim().length()==0)
				continue;
			
			String [] accs = pep[pepaccess].contains(";") ? pep[pepaccess].split("; ") : 
				new String []{pep[pepaccess]};

			String [] mods = mod.split("; ");
			ArrayList <Integer> templist = new ArrayList <Integer>();

			for(int j=0;j<mods.length;j++){
				
				if(mods[j].startsWith("Deamidated(N)")){
					int at = mods[j].indexOf("@");
					templist.add(Integer.parseInt(mods[j].substring(at+1, mods[j].length())));
				}
			}
			
			if(templist.size()==0) 
				continue;
			
			Integer [] locs = templist.toArray(new Integer [templist.size()]);
			
			String modseq = "";
			HashMap <String, String []> proinfomap = new HashMap <String, String []>();
			
			for(int i=0;i<accs.length;i++){
	
				if(psmap.containsKey(accs[i])){

					ProteinSequence proseq = this.psmap.get(accs[i]);
					
					int beg = proseq.indexOf(seq);
					StringBuilder sitesb = new StringBuilder();
					StringBuilder windowsb = new StringBuilder();
					StringBuilder motifsb = new StringBuilder();
					StringBuilder seqsb = new StringBuilder(seq);

					int count = 0;
					for(int j=0;j<locs.length;j++){
						
						int modsite = beg+locs[j];
						String aaround = proseq.getSeqAround(modsite-1);
						
						if(modsite+2>proseq.length()){
							continue;
						}
						
						if(proseq.getAminoaicdAt(modsite)!='N'){
							continue;
						}
						
						if(proseq.getAminoaicdAt(modsite+1)=='P'){
							continue;
						}
						
						char aa2 = proseq.getAminoaicdAt(modsite+2);
						if(aa2=='S'){
							motifsb.append("N-X-[S|T]").append(',');
						}else if(aa2=='T'){
							motifsb.append("N-X-[S|T]").append(',');
						}else if(aa2=='C'){
							motifsb.append("N-X-C").append(',');
						}else{
							continue;
						}
						
						sitesb.append(modsite).append(',');
						windowsb.append(aaround).append(',');
						seqsb.insert(count+locs[j], '*');
						count++;
					}

					if(sitesb.length()==0)
						continue;
					
					if(modseq.length()==0){
						modseq = seqsb.toString();
					}

					String [] content = new String []{sitesb.substring(0, sitesb.length()-1), 
							windowsb.substring(0, windowsb.length()-1), motifsb.substring(0, motifsb.length()-1)};
					
					proinfomap.put(accs[i], content);
				}
			}
			
			if(modseq.length()==0)
				continue;
			
			if(this.contentmap.containsKey(modseq)){
				this.countmap.put(modseq, countmap.get(modseq)+1);
				if(pepconfscore>this.scoremap.get(modseq)){
					this.scoremap.put(modseq, pepconfscore);
				}
			}else{
				this.countmap.put(modseq, 1);
				this.contentmap.put(modseq, proinfomap);
				this.scoremap.put(modseq, pepconfscore);
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

		HashMap <String, File> pepmap = new HashMap <String, File>();
		HashMap <String, Double> thresmap = new HashMap <String, Double>();
		
		File [] dirs = (new File(in)).listFiles();
		for(int i=0;i<dirs.length;i++){
			System.out.println(dirs[i].getAbsolutePath());
			File [] files = dirs[i].listFiles();
			for(int j=0;j<files.length;j++){
				String name = files[j].getName().substring(0, files[j].getName().length()-4);
				if(name.endsWith("PeptideSummary")){
					pepmap.put(name, files[j]);
//					boolean b = false;
					for(int k=0;k<files.length;k++){
						String namek = files[k].getName().substring(0, files[k].getName().length()-4);
						if(k!=j && namek.startsWith(name)){
							String [] ss = namek.split("_");
							double thres = Double.parseDouble(ss[ss.length-1]);
							thresmap.put(name, thres);
//							b = true;
						}
					}
//					if(!b){
//						System.out.println(dirs[i].getAbsolutePath()+"\n"+name);
//					}
				}
			}
		}
		
		Iterator <String> it = pepmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
//			System.out.println("~~~\t"+key);
			TritofCombiner combiner = new TritofCombiner(pepmap.get(key), thresmap.get(key), psmap);
			combiner.extract();
//			System.out.println(pepmap.get(key).getAbsolutePath());
			
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
			String key = ref.substring(0, ref.indexOf("|"));
			
			psmap.put(key, ps);
			namemap.put(key, name);
			genemap.put(key, gene);
			uniprotmap.put(key, uniprot);
		}
		fr.close();
		
		HashMap <String, Integer> totalCountmap1 = new HashMap <String, Integer>();
		HashMap <String, Double> totalScoremap1 = new HashMap <String, Double>();
		HashMap <String, HashMap <String, String[]>> totalContentmap1 = new HashMap <String, HashMap <String, String[]>>();
		TritofCombiner.combine(s1, totalCountmap1, totalScoremap1, totalContentmap1, psmap, genemap, uniprotmap);
		
		HashMap <String, Integer> totalCountmap2 = new HashMap <String, Integer>();
		HashMap <String, Double> totalScoremap2 = new HashMap <String, Double>();
		HashMap <String, HashMap <String, String[]>> totalContentmap2 = new HashMap <String, HashMap <String, String[]>>();
		TritofCombiner.combine(s2, totalCountmap2, totalScoremap2, totalContentmap2, psmap, genemap, uniprotmap);
		
		HashMap <String, Integer> totalCountmap3 = new HashMap <String, Integer>();
		HashMap <String, Double> totalScoremap3 = new HashMap <String, Double>();
		HashMap <String, HashMap <String, String[]>> totalContentmap3 = new HashMap <String, HashMap <String, String[]>>();
		TritofCombiner.combine(s3, totalCountmap3, totalScoremap3, totalContentmap3, psmap, genemap, uniprotmap);
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Protein\t");
		titlesb.append("Protein Name\t");
		titlesb.append("Gene Name\t");
		titlesb.append("Uniprot\t");
		titlesb.append("Position\t");
		titlesb.append("sequence\t");
		titlesb.append("Conf\t");
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
	
	public static void fastatest(String fasta) throws IOException{
		
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String name = ps.getName();
			String gene = ps.getGene();
			String uniprot = ps.getSWISS();
			String key = ref.substring(0, ref.indexOf("|"));
			
			if(name.length()==0 || gene.length()==0 || uniprot.length()==0){
				System.out.println(ref);
			}
		}
		fr.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws SequenceGenerationException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws RowsExceededException, SequenceGenerationException, 
		WriteException, IOException {
		// TODO Auto-generated method stub
		
		String s1 = "H:\\glyco_combine\\Hydrazide\\5600\\trypsin\\protein";
		String s2 = "H:\\glyco_combine\\Hydrazide\\5600\\T+C\\protein";
		String s3 = "H:\\glyco_combine\\Hydrazide\\5600\\chymotrypsin\\protein";

		String out = "H:\\glyco_combine\\HILIC.5600.combine.2.xls";
		
		String test = "H:\\glyco_combine\\HILIC\\5600\\chymotrypsin\\test";
		String testout = "H:\\glyco_combine\\Hydrazide.5600.protein.xls";

		TritofCombiner.totalCombine(s1, s2, s3, "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta", testout);
//		TritofCombiner.fastatest("F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta");
	}

}
