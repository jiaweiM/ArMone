/* 
 ******************************************************************************
 * File:MascotHtmlQueryGetter.java * * * Created on 2012-8-17
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.group;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParser;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParsingException;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2012-8-17, 15:48:30
 */
public class MascotHtmlQueryGetter {
	
	private BufferedReader reader;
	private Pattern pattern = Pattern.compile(".*query=([\\d]+).*");
	private Pattern pattern2 = Pattern.compile("<TD><TT>Top scoring peptide matches to query[\\s]+([\\d]+)" +
			".*Score greater than ([\\d]+) indicates identity.*</B><BR>[\\s]*([\\d\\.]+).*");
	private HashSet <Integer> querySet;
	private HashSet <Integer> scanSet;
	private HashMap <Integer, String> seqmap;
	private HashMap <String, HashSet <String>> promap;
	
	public MascotHtmlQueryGetter(String in) throws FileNotFoundException{
		this.reader = new BufferedReader(new FileReader(in));
		this.querySet = new HashSet <Integer>();
		this.scanSet = new HashSet <Integer>();
		this.seqmap = new HashMap <Integer, String>();
		this.promap = new HashMap <String, HashSet <String>>();
	}
	
	public MascotHtmlQueryGetter(File in) throws FileNotFoundException{
		this.reader = new BufferedReader(new FileReader(in));
		this.querySet = new HashSet <Integer>();
		this.seqmap = new HashMap <Integer, String>();
	}
	
	public HashMap <Integer, String> getSeqMap(){
		return this.seqmap;
	}
	
	public HashSet <Integer> getScanset(){
		return this.scanSet;
	}
	
	public void read() throws IOException{
		
		int count = 0;
		int target = 0;
		int decoy = 0;
		String line = null;
		StringBuilder sb = new StringBuilder();
		boolean begin = false;
		while((line=reader.readLine())!=null){
			if(line.trim().startsWith("href")){
				Matcher matcher = pattern.matcher(line);
				if(matcher.matches()){
					String id = matcher.group(1);
					this.querySet.add(Integer.parseInt(id));
				}
			}else if(line.trim().startsWith("<DIV")){
				
				begin = true;

				Matcher matcher = pattern2.matcher(sb);
				int query = -1;
				double iden = -1;
				double score = -1;
				if(matcher.matches()){

//					System.out.println(matcher.groupCount());
					query = Integer.parseInt(matcher.group(1));
					iden = Double.parseDouble(matcher.group(2));
					score = Double.parseDouble(matcher.group(3));
//					System.out.println(iden+"\t"+score+"\t"+query);
//					System.out.println(querySet.size());

					if(score<iden){
						if(querySet.contains(query)){
							querySet.remove(query);
						}

					}else{
						count++;
//System.out.println("---\t"+score);						
						if(querySet.contains(query)){
							String [] ss = sb.toString().split("<[A-TV-Z]+>");
							if(ss.length>7){
//								System.out.println(ss[7]);
								String [] sss = ss[7].split("[\\s]+");
//								System.out.println(sss[sss.length-1]+"\t"+this.parseSequence(sss[sss.length-1])
//										+"\t"+sss[sss.length-2]);
								String ref = sss[sss.length-2];
								String seq = this.parseSequence(sss[sss.length-1]);
								
								if(ref.startsWith("REV")){
									decoy++;
								}else{
									target++;
								}
								
								seqmap.put(query, seq);
								if(this.promap.containsKey(ref)){
									this.promap.get(ref).add(seq);
								}else{
									HashSet <String> set = new HashSet <String>();
									set.add(seq);
									this.promap.put(ref, set);
								}
							}
						}
					}
				}
				
				sb = new StringBuilder();
				
			}else if(line.trim().startsWith("<TD><TT>")){
				if(begin){
					sb = new StringBuilder();
					sb.append(line.trim()+" ");
				}
			}else{
				if(begin){
					sb.append(line.trim()+" ");
				}
			}
		}
		
		Matcher matcher = pattern2.matcher(sb);
		int query = -1;
		double iden = -1;
		double score = -1;
		if(matcher.matches()){

//			System.out.println(matcher.groupCount());
			query = Integer.parseInt(matcher.group(1));
			iden = Double.parseDouble(matcher.group(2));
			score = Double.parseDouble(matcher.group(3));
//			System.out.println(iden+"\t"+score+"\t"+query);
//			System.out.println(querySet.size());

			if(score<=iden){
				if(querySet.contains(query)){
					querySet.remove(query);
				}

			}else{
				count++;

//				if(querySet.contains(query)){
					String [] ss = sb.toString().split("<[A-TV-Z]+>");
					if(ss.length>7){
//						System.out.println(ss[7]);
						String [] sss = ss[7].split("[\\s]+");
//						System.out.println(sss[sss.length-1]+"\t"+this.parseSequence(sss[sss.length-1])
//								+"\t"+sss[sss.length-2]);
						System.out.println(sss[0]);
						String ref = sss[sss.length-2];
						String seq = this.parseSequence(sss[sss.length-1]);
						
						if(ref.startsWith("REV")){
							decoy++;
						}else{
							target++;
						}
						
						seqmap.put(query, seq);
						if(this.promap.containsKey(ref)){
							this.promap.get(ref).add(seq);
						}else{
							HashSet <String> set = new HashSet <String>();
							set.add(seq);
							this.promap.put(ref, set);
						}
					}
//				}
			}
		}
		System.out.println(target+"\t"+decoy+"\t"+(target+decoy));
		System.out.println(this.querySet.size()+"\t"+this.seqmap.size()+"\t"+count);
		
/*		HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
				"Literature\\peptide.txt", "5");
		
		int find = 0;
		int not = 0;
		Iterator <Integer> it = this.seqmap.keySet().iterator();
		while(it.hasNext()){
			Integer id = it.next();
			String seq = seqmap.get(id);
			if(set.contains(seq)){
				find++;
			}else{
				not++;
			}
		}
		System.out.println(find+"\t"+not);
*/		
	}
	
	public HashSet <String> getPepSet(String in, String id) throws IOException{
		
		HashSet <String> set = new HashSet <String>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null && line.trim().length()>0){
			String [] ss = line.split("\t");
			if(ss[0].startsWith(id))
				set.add(ss[5]);
		}
		reader.close();
		return set;
	}

	private String parseSequence(String seq){
		StringBuilder sb = new StringBuilder();
		boolean b = true;
		for(int i=2;i<seq.length()-2;i++){
			if(seq.charAt(i)=='<'){
				b = false;
			}else if(seq.charAt(i)=='>'){
				b = true;
			}else if(seq.charAt(i)=='/'){
				
			}else{
				if(b)
					sb.append(seq.charAt(i));
			}
		}
		return sb.toString();
	}
	
	public HashSet <Integer> getQuerySet(){
		return this.querySet;
	}
	
	public void getScan(String dat) throws FileDamageException, IOException, MascotDatParsingException{
		
		MascotDatParser parser = new MascotDatParser(dat);
		int num = parser.getQueryNum();
		for(int i=1; i<num; i++){
			String qName = parser.getQueryIdx(i).getQname();
			IScanName scanName = ScanNameFactory.parseName(qName);
			int scannum = scanName.getScanNumBeg();
//			System.out.println(i+"\t"+parser.getQueryIdx(i).getQidx()+"\t"+parser.getQueryIdx(i).getQname());
			if(this.querySet.contains(i)){
//				System.out.println(i+"\t"+scannum);
				scanSet.add(scannum);
			}
		}
		System.out.println(scanSet.size());
	}
	
	public void coverCalculate(String database) throws IOException{
		
		DecimalFormat df3 = DecimalFormats.DF0_2;
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		FastaReader fr = new FastaReader(database);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String key = ref.substring(0, ref.indexOf("|"));
			psmap.put(key, ps);
			
		}
		fr.close();
		
		Iterator <String> it = this.promap.keySet().iterator();
		while(it.hasNext()){
			String ref = it.next();
			if(psmap.containsKey(ref)){
				HashSet <String> set = promap.get(ref);
				ProteinSequence proseq = psmap.get(ref);
				
				int [] aas = new int[proseq.length()];
				Arrays.fill(aas, 0);
				Iterator <String> seqit = set.iterator();
				while(seqit.hasNext()){
					String seq = seqit.next();
					int beg = proseq.indexOf(seq);
					for(int i=0;i<seq.length();i++){
						aas[i+beg] = 1;
					}
				}
				
				int count = 0;
				for(int i=0;i<aas.length;i++){
					count+=aas[i];
				}
				double cover = (double)count/(double)aas.length;
				System.out.println(ref+"\t"+df3.format(cover));
			}
		}
	}
	
	public void close() throws IOException{
		this.reader.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MascotDatParsingException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws IOException, FileDamageException, MascotDatParsingException {
		// TODO Auto-generated method stub

		MascotHtmlQueryGetter getter = new MascotHtmlQueryGetter("H:\\Validation\\" +
				"Byy_phos_5600_velos\\20120730_Human_liver_tryptic_50ug_2_2998.htm");
		
		getter.read();
//		getter.getScan("H:\\Validation\\Byy_phos_5600_velos\\final_20120730_Human_liver_tryptic_50ug_F002997_percolator.dat");
//		getter.coverCalculate("H:\\Validation\\18mix\\db\\18mix.fasta");
		getter.close();

	}

}
