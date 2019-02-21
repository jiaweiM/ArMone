/* 
 ******************************************************************************
 * File: TritofSiteCombiner.java * * * Created on 2012-12-26
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
 * @version 2012-12-26, 14:00:39
 */
public class TritofSiteCombiner {
	
	private File peptide;
	private File protein;
	private HashMap <String, ProteinSequence> psmap;
	private double conf;
	private HashMap <String, HashMap <String, String []>> contentmap;
	private HashMap <String, HashMap <String, Integer>> countmap;
	
	public TritofSiteCombiner(String peptide, String protein, double conf, HashMap <String, ProteinSequence> psmap){
		
	}
	
	public TritofSiteCombiner(File peptide, File protein, double conf, HashMap <String, ProteinSequence> psmap){
		this.peptide = peptide;
		this.protein = protein;
		this.conf = conf;
		this.psmap = psmap;
		this.contentmap = new HashMap <String, HashMap <String, String []>>();
		this.countmap = new HashMap <String, HashMap <String, Integer>>();
	}
	
	public void extract() throws IOException, SequenceGenerationException{
		
		HashMap <String, Double> proScoreMap = new HashMap <String, Double>();

		BufferedReader proreader = new BufferedReader(new FileReader(protein));
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
		
		BufferedReader pepreader = new BufferedReader(new FileReader(peptide));
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
			
			String [] accs = pep[pepaccess].contains(";") ? pep[pepaccess].split(";") : 
				new String []{pep[pepaccess]};
			
			String seq = pep[pepseq];
			String mod = pep[pepmod];

			String acc = "";
			double accscore = 0;
			for(int i=0;i<accs.length;i++){
				double ss = -1;
				if(proScoreMap.containsKey(accs[i]))
					ss = proScoreMap.get(accs[i]);
				
				if(ss>accscore){
					acc = accs[i];
					accscore = ss;
				}
			}
			
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
			
			if(psmap.containsKey(acc)){

				ProteinSequence proseq = this.psmap.get(acc);

				int beg = proseq.indexOf(seq);

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

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(acc)){
								int c = this.countmap.get(aaround).get(acc);
								this.countmap.get(aaround).put(acc, c+1);
							}else{
								String [] content = new String [] {String.valueOf(modsite), "Other"};
								this.contentmap.get(aaround).put(acc, content);
								this.countmap.get(aaround).put(acc, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(modsite), "Other"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(acc, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(acc, 1);
							this.countmap.put(aaround, cmap);
						}
					
						continue;
					}
					
					char aa2 = proseq.getAminoaicdAt(modsite+2);
					if(aa2=='S'){

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(acc)){
								int c = this.countmap.get(aaround).get(acc);
								this.countmap.get(aaround).put(acc, c+1);
							}else{
								String [] content = new String [] {String.valueOf(modsite), "N-X-[S|T]"};
								this.contentmap.get(aaround).put(acc, content);
								this.countmap.get(aaround).put(acc, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(modsite), "N-X-[S|T]"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(acc, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(acc, 1);
							this.countmap.put(aaround, cmap);
						}
						
					}else if(aa2=='T'){

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(acc)){
								int c = this.countmap.get(aaround).get(acc);
								this.countmap.get(aaround).put(acc, c+1);
							}else{
								String [] content = new String [] {String.valueOf(modsite), "N-X-[S|T]"};
								this.contentmap.get(aaround).put(acc, content);
								this.countmap.get(aaround).put(acc, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(modsite), "N-X-[S|T]"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(acc, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(acc, 1);
							this.countmap.put(aaround, cmap);
						}
						
					}else if(aa2=='C'){

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(acc)){
								int c = this.countmap.get(aaround).get(acc);
								this.countmap.get(aaround).put(acc, c+1);
							}else{
								String [] content = new String [] {String.valueOf(modsite), "N-X-C"};
								this.contentmap.get(aaround).put(acc, content);
								this.countmap.get(aaround).put(acc, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(modsite), "N-X-C"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(acc, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(acc, 1);
							this.countmap.put(aaround, cmap);
						}
					}else{
						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(acc)){
								int c = this.countmap.get(aaround).get(acc);
								this.countmap.get(aaround).put(acc, c+1);
							}else{
								String [] content = new String [] {String.valueOf(modsite), "Other"};
								this.contentmap.get(aaround).put(acc, content);
								this.countmap.get(aaround).put(acc, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(modsite), "Other"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(acc, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(acc, 1);
							this.countmap.put(aaround, cmap);
						}
					}
				}
			}
		}
		pepreader.close();
	}
	
	public HashMap <String, HashMap <String, Integer>> getCountMap(){
		return countmap;
	}
	
	public HashMap <String, HashMap <String, String[]>> getContentMap(){
		return contentmap;
	}
	
	public static void combine(String in, HashMap <String, HashMap <String, Integer>> totalCountmap,
			HashMap <String, HashMap <String, String[]>> totalContentmap,
			HashMap <String, ProteinSequence> psmap) throws IOException, SequenceGenerationException{
		
		HashMap <String, Double> thresmap = new HashMap <String, Double>();
		HashMap <String, File> pepmap = new HashMap <String, File>();
		HashMap <String, File> promap = new HashMap <String, File>();
		File [] dirs = (new File(in)).listFiles();
		for(int i=0;i<dirs.length;i++){
			System.out.println(dirs[i].getAbsolutePath());
			File [] files = dirs[i].listFiles();
			for(int j=0;j<files.length;j++){
				if(files[j].getName().endsWith("mgf"))
					continue;
				String name = files[j].getName().substring(0, files[j].getName().length()-4);
				System.out.println(files[j].getName());
				if(name.endsWith("PeptideSummary")){
					pepmap.put(name, files[j]);
				}else if(name.endsWith("ProteinSummary")){
					promap.put(name.replace("ProteinSummary", "PeptideSummary"), files[j]);
				}else{
					int id = name.lastIndexOf("_");
					double thres = Double.parseDouble(name.substring(id+1));
					thresmap.put(name.substring(0, id), thres);
				}
			}
		}
		System.out.println(pepmap.size()+"\t"+promap.size()+"\t"+thresmap.size());
		Iterator <String> it = thresmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			TritofSiteCombiner combiner = new TritofSiteCombiner(pepmap.get(key), promap.get(key), thresmap.get(key),
					psmap);
			combiner.extract();
			
			HashMap <String, HashMap <String, Integer>> countmap = combiner.getCountMap();
			HashMap <String, HashMap <String, String[]>> contentmap = combiner.getContentMap();
			
			Iterator <String> siteit = countmap.keySet().iterator();
			while(siteit.hasNext()){
				String around = siteit.next();
				if(totalContentmap.containsKey(around)){
					
					HashMap <String, Integer> acccountmap = countmap.get(around);
					HashMap <String, String[]> acccontentmap = contentmap.get(around);
					
					Iterator <String> accit = acccountmap.keySet().iterator();
					while(accit.hasNext()){
						String acc = accit.next();
						if(totalCountmap.get(around).containsKey(acc)){
							int c = totalCountmap.get(around).get(acc);
							totalCountmap.get(around).put(acc, c+acccountmap.get(acc));
						}else{
							totalContentmap.get(around).put(acc, acccontentmap.get(acc));
							totalCountmap.get(around).put(acc, acccountmap.get(acc));
						}
					}
					
				}else{
					totalContentmap.put(around, contentmap.get(around));
					totalCountmap.put(around, countmap.get(around));
				}
			}
		}
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
		
		HashMap <String, HashMap <String, Integer>> totalCountmap1 = new HashMap <String, HashMap <String, Integer>>();
		HashMap <String, HashMap <String, String[]>> totalContentmap1 = new HashMap <String, HashMap <String, String[]>>();
		TritofSiteCombiner.combine(s1, totalCountmap1, totalContentmap1, psmap);
		
		HashMap <String, HashMap <String, Integer>> totalCountmap2 = new HashMap <String, HashMap <String, Integer>>();
		HashMap <String, HashMap <String, String[]>> totalContentmap2 = new HashMap <String, HashMap <String, String[]>>();
		TritofSiteCombiner.combine(s2, totalCountmap2, totalContentmap2, psmap);
		
		HashMap <String, HashMap <String, Integer>> totalCountmap3 = new HashMap <String, HashMap <String, Integer>>();
		HashMap <String, HashMap <String, String[]>> totalContentmap3 = new HashMap <String, HashMap <String, String[]>>();
		TritofSiteCombiner.combine(s3, totalCountmap3, totalContentmap3, psmap);
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Protein\t");
		titlesb.append("Protein Name\t");
		titlesb.append("Gene Name\t");
		titlesb.append("Uniprot\t");
		titlesb.append("Position\t");
		titlesb.append("sequence window\t");
		titlesb.append("Motif\t");
		titlesb.append("Count\t");
//		titlesb.append("number of identifications (trypsin)\t");
//		titlesb.append("number of identifications (trypsin+Glu-C)\t");
//		titlesb.append("number of identifications (chymotrypsin)\t");
		writer.addTitle(titlesb.toString(), 0, format);
		
		HashSet <String> set = new HashSet <String>();
		set.addAll(totalCountmap1.keySet());
		set.addAll(totalCountmap2.keySet());
		set.addAll(totalCountmap3.keySet());
		
		Iterator <String> it = set.iterator();
		while(it.hasNext()){
			
			String key = it.next();

			HashMap <String, Integer> countmap = new HashMap <String, Integer>();
			if(totalCountmap1.containsKey(key)){
				HashMap <String, Integer> cmap = totalCountmap1.get(key);
				Iterator <String> accit = cmap.keySet().iterator();
				while(accit.hasNext()){
					String acc = accit.next();
					if(countmap.containsKey(acc)){
						countmap.put(acc, countmap.get(acc)+cmap.get(acc));
					}else{
						countmap.put(acc, cmap.get(acc));
					}
				}
			}
			if(totalCountmap2.containsKey(key)){
				HashMap <String, Integer> cmap = totalCountmap2.get(key);
				Iterator <String> accit = cmap.keySet().iterator();
				while(accit.hasNext()){
					String acc = accit.next();
					if(countmap.containsKey(acc)){
						countmap.put(acc, countmap.get(acc)+cmap.get(acc));
					}else{
						countmap.put(acc, cmap.get(acc));
					}
				}
			}
			if(totalCountmap3.containsKey(key)){
				HashMap <String, Integer> cmap = totalCountmap3.get(key);
				Iterator <String> accit = cmap.keySet().iterator();
				while(accit.hasNext()){
					String acc = accit.next();
					if(countmap.containsKey(acc)){
						countmap.put(acc, countmap.get(acc)+cmap.get(acc));
					}else{
						countmap.put(acc, cmap.get(acc));
					}
				}
			}
			
			String maxacc = "";
			int count = 0;
			int totalCount = 0;
			Iterator <String> accit = countmap.keySet().iterator();
			while(accit.hasNext()){
				String acc = accit.next();
				totalCount += countmap.get(acc);
				if(countmap.get(acc)>count){
					count = countmap.get(acc);
					maxacc = acc;
				}
			}
			
			String [] content = null;
			if(totalContentmap1.containsKey(key) && totalContentmap1.get(key).containsKey(maxacc)){
				content = totalContentmap1.get(key).get(maxacc);
			}else if(totalContentmap2.containsKey(key) && totalContentmap2.get(key).containsKey(maxacc)){
				content = totalContentmap2.get(key).get(maxacc);
			}else if(totalContentmap3.containsKey(key) && totalContentmap3.get(key).containsKey(maxacc)){
				content = totalContentmap3.get(key).get(maxacc);
			}

			StringBuilder sb = new StringBuilder();
			sb.append(maxacc).append("\t");
			sb.append(namemap.get(maxacc)).append("\t");
			sb.append(genemap.get(maxacc)).append("\t");
			sb.append(uniprotmap.get(maxacc)).append("\t");
			sb.append(content[0]).append("\t");
			sb.append(key).append("\t");
			sb.append(content[1]).append("\t");
			sb.append(totalCount).append("\t");

			writer.addContent(sb.toString(), 0, format);
		}
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws SequenceGenerationException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws RowsExceededException, SequenceGenerationException, WriteException, IOException {
		// TODO Auto-generated method stub
		
		String s1 = "H:\\20130902_glyco\\5600_1D\\protein level\\trypsin";
		String s2 = "H:\\20130902_glyco\\5600_1D\\protein level\\trypsin+GluC";
		String s3 = "H:\\20130902_glyco\\5600_1D\\protein level\\chymotrypsin";

		String out = "H:\\20130902_glyco\\5600_1D\\protein level\\protein level.5600.1D.xls";

		TritofSiteCombiner.totalCombine(s1, s2, s3, "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta", out);
	}

}
