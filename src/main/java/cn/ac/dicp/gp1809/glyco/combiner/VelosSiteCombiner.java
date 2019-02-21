/* 
 ******************************************************************************
 * File: VelosSiteCombiner.java * * * Created on 2012-12-26
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaWriter;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2012-12-26, 15:56:28
 */
public class VelosSiteCombiner {
	
	private File peptide;
	private File site;
	private HashMap <String, ProteinSequence> psmap;
	private HashMap <String, HashMap <String, Integer>> countmap;
	private HashMap <String, HashMap <String, String []>> contentmap;
	
	public VelosSiteCombiner(File peptide, File site, HashMap <String, ProteinSequence> psmap){
		this.peptide = peptide;
		this.site = site;
		this.psmap = psmap;
		this.contentmap = new HashMap <String, HashMap <String, String []>>();
		this.countmap = new HashMap <String, HashMap <String, Integer>>();
	}

	public void extract() throws IOException, SequenceGenerationException{
		
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

			if(ref.length()!=11){
				ref = ref.substring(5, 16);
			}

			if(psmap.containsKey(ref)){
				
				ProteinSequence proseq = psmap.get(ref);
				ref = proseq.getReference().substring(0, proseq.getReference().indexOf("|"));
				String [] mods = mod.split(";");

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

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(ref)){
								int c = this.countmap.get(aaround).get(ref);
								this.countmap.get(aaround).put(ref, c+1);
							}else{
								String [] content = new String [] {String.valueOf(position), "Other"};
								this.contentmap.get(aaround).put(ref, content);
								this.countmap.get(aaround).put(ref, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(position), "Other"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(ref, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(ref, 1);
							this.countmap.put(aaround, cmap);
						}
					
						continue;
					}
					
					char aa2 = proseq.getAminoaicdAt(position+2);
					if(aa2=='S'){

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(ref)){
								int c = this.countmap.get(aaround).get(ref);
								this.countmap.get(aaround).put(ref, c+1);
							}else{
								String [] content = new String [] {String.valueOf(position), "N-X-[S|T]"};
								this.contentmap.get(aaround).put(ref, content);
								this.countmap.get(aaround).put(ref, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(position), "N-X-[S|T]"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(ref, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(ref, 1);
							this.countmap.put(aaround, cmap);
						}
						
					}else if(aa2=='T'){

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(ref)){
								int c = this.countmap.get(aaround).get(ref);
								this.countmap.get(aaround).put(ref, c+1);
							}else{
								String [] content = new String [] {String.valueOf(position), "N-X-[S|T]"};
								this.contentmap.get(aaround).put(ref, content);
								this.countmap.get(aaround).put(ref, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(position), "N-X-[S|T]"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(ref, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(ref, 1);
							this.countmap.put(aaround, cmap);
						}
						
					}else if(aa2=='C'){

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(ref)){
								int c = this.countmap.get(aaround).get(ref);
								this.countmap.get(aaround).put(ref, c+1);
							}else{
								String [] content = new String [] {String.valueOf(position), "N-X-C"};
								this.contentmap.get(aaround).put(ref, content);
								this.countmap.get(aaround).put(ref, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(position), "N-X-C"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(ref, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(ref, 1);
							this.countmap.put(aaround, cmap);
						}
					}else{

						if(this.contentmap.containsKey(aaround)){
							if(this.contentmap.get(aaround).containsKey(ref)){
								int c = this.countmap.get(aaround).get(ref);
								this.countmap.get(aaround).put(ref, c+1);
							}else{
								String [] content = new String [] {String.valueOf(position), "Other"};
								this.contentmap.get(aaround).put(ref, content);
								this.countmap.get(aaround).put(ref, 1);
							}
						}else{
							String [] content = new String [] {String.valueOf(position), "Other"};
							HashMap <String, String[]> conmap = new HashMap <String, String[]>();
							conmap.put(ref, content);
							this.contentmap.put(aaround, conmap);
							HashMap <String, Integer> cmap = new HashMap <String, Integer>();
							cmap.put(ref, 1);
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

		File [] dirs = (new File(in)).listFiles();
		for(int i=0;i<dirs.length;i++){
			
			File [] files = dirs[i].listFiles();
			VelosSiteCombiner combiner;
			System.out.println(dirs[i].getAbsolutePath());
			if(files[0].getName().equals("peptides.txt")){
				combiner = new VelosSiteCombiner(files[0], files[1], psmap);
			}else{
				combiner = new VelosSiteCombiner(files[1], files[0], psmap);
			}
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
			String key = ref.substring(4, 15);
			String key2 = ref.substring(0, ref.indexOf("|"));
			
			psmap.put(key, ps);
			namemap.put(key2, name);
			genemap.put(key2, gene);
			uniprotmap.put(key2, uniprot);
		}
		fr.close();
		
		HashMap <String, HashMap <String, Integer>> totalCountmap1 = new HashMap <String, HashMap <String, Integer>>();
		HashMap <String, HashMap <String, String[]>> totalContentmap1 = new HashMap <String, HashMap <String, String[]>>();
		VelosSiteCombiner.combine(s1, totalCountmap1, totalContentmap1, psmap);
		
		HashMap <String, HashMap <String, Integer>> totalCountmap2 = new HashMap <String, HashMap <String, Integer>>();
		HashMap <String, HashMap <String, String[]>> totalContentmap2 = new HashMap <String, HashMap <String, String[]>>();
		VelosSiteCombiner.combine(s2, totalCountmap2, totalContentmap2, psmap);
		
		HashMap <String, HashMap <String, Integer>> totalCountmap3 = new HashMap <String, HashMap <String, Integer>>();
		HashMap <String, HashMap <String, String[]>> totalContentmap3 = new HashMap <String, HashMap <String, String[]>>();
		VelosSiteCombiner.combine(s3, totalCountmap3, totalContentmap3, psmap);
		
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
	
	public static void prodatabase(String in, String out, String fasta) throws IOException, JXLException{
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			psmap.put(ref.substring(0, ref.indexOf("|")), ps);
		}
		fr.close();
		
		HashMap <String, ProteinSequence> usemap = new HashMap <String, ProteinSequence>();
		ExcelReader reader = new ExcelReader(in);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			if(psmap.containsKey(line[0])){
				usemap.put(line[0], psmap.get(line[0]));
			}
		}
		
		System.out.println(usemap.size());
		
		FastaWriter writer = new FastaWriter(out);
		Iterator <String> it = usemap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			writer.write(usemap.get(key));
		}
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SequenceGenerationException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws SequenceGenerationException, IOException, JXLException {
		// TODO Auto-generated method stub
		
		String s1 = "H:\\glyco_combine\\HILIC\\velos\\trypsin";
		String s2 = "H:\\glyco_combine\\HILIC\\velos\\T+C";
		String s3 = "H:\\glyco_combine\\HILIC\\velos\\chymotrypsin";

		String out = "H:\\glyco_combine\\new\\HILIC.velos.site.2.xls";

		VelosSiteCombiner.totalCombine(s1, s2, s3, "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta", out);
//		VelosSiteCombiner.prodatabase("H:\\glyco_combine\\new\\new.site.xls", 
//				"H:\\glyco_combine\\new\\glyco.protein.fasta", 
//				"F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta");
	}

}
