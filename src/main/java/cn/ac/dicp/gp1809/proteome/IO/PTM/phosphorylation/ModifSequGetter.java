/* 
 ******************************************************************************
 * File:ModifSequGetter.java * * * Created on 2009-11-19
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2009-11-19, 20:54:19
 */
public class ModifSequGetter implements ITask{
	
	private Pattern pattern;
	private String description;
	private String proSeq;
	private Kinase kinase;
	
	/**
	 * @param Description is a string which is similar with a regular expression but without a
	 * rigorous format.
	 */
	public ModifSequGetter(String description) throws IOException{
		this.description = description;
	}
	
	public ModifSequGetter(String proSeq, Kinase kinase){
		this.proSeq = proSeq;
		this.kinase = kinase;
	}
	
	public void writeSites(String dbfile, String file) throws IOException{
		
		FastaReader reader = new FastaReader(dbfile);
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		ProteinSequence sequence;
		
		while((sequence=reader.nextSequence())!=null){
			String str = getModifInfo(sequence, this.description);
			if(str!=null)
				pw.println(str);
		}
		
		pw.close();
	}
	
	public void writeSites2(String dbfile, String file, Kinase kinase) throws IOException, XMLStreamException{
		
		FastaReader reader = new FastaReader(dbfile);
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		ProteinSequence sequence;
		
		while((sequence=reader.nextSequence())!=null){
			String str = getModifInfo2(sequence, kinase);
			if(str!=null)
				pw.println(str);
		}

		pw.close();
	}
	
	public void writePeps(String dbfile, String file) throws IOException{
		
		FastaReader reader = new FastaReader(dbfile);
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		ProteinSequence sequence;
		
		while((sequence=reader.nextSequence())!=null){
			String str = getModifPep(sequence, Enzyme.TRYPSIN, 0, 1,this.description);
			if(str!=null)
				pw.println(str);
		}

		pw.close();
	}
	
	public String getModifPep(ProteinSequence ps, Enzyme enzyme, int missCleaveSite, int enzymaticType, String description){
		
		String name = ps.getReference();
		String sequence = ps.getUniqueSequence();
		
		HashMap <Integer [], String> pepMap = enzyme.getCleaveLoca(sequence, missCleaveSite, enzymaticType);
		Set <Integer []> loca = pepMap.keySet();
		HashSet <String> peps = new HashSet <String> ();
		
		Integer [] modifList = getModifList(ps, description);
		Iterator <Integer[]> it = loca.iterator();
		while(it.hasNext()){
			Integer [] posi = it.next();
			int i = posi[0].intValue();
			int j = posi[1].intValue();
			for(int m:modifList){
				if(m>i && m<j){
					peps.add(pepMap.get(posi));
				}
			}
		}

		if(peps.size()>0){
			StringBuilder sb = new StringBuilder();
			sb.append('"').append(name).append('"').append(',').append(peps.size()).append(',');
			Iterator <String> pepIt = peps.iterator();
			while(pepIt.hasNext()){
				String p = pepIt.next();
				sb.append(p).append(',');
			}
			return sb.toString();
		}
		return null;
	}
	
	public String getModifInfo(ProteinSequence ps, String description){
		
		ModifDescrib md = getDes(description);
		HashMap <Integer, Boolean> sites = md.getSites();
		Set <Integer> iset = sites.keySet();

		Pattern pattern = md.getPattern();
		
		String name = ps.getReference();
		String sequence = ps.getUniqueSequence();
		Matcher match = pattern.matcher(sequence);
		
		int sitesNum = 0;
		ArrayList <String> s = new ArrayList <String> ();
		StringBuilder sb = new StringBuilder();
		
		sb.append('"').append(name).append('"').append(',');

			while(match.find()){
				
				int local = match.start();
				StringBuilder isb = new StringBuilder();
				
				for(int i:iset){
					if(sites.get(i)){
						sitesNum++;
						i+=local;
						isb.append(i).append(';');
					}
					else{
						i+=local;
						isb.append(i).append('*').append(';');
					}
				}
				if(isb.capacity()>0){
					s.add(isb.toString());
		//			System.out.println(isb);
				}
				
			}
		
		
		if(sitesNum>0){
			sb.append(sitesNum).append(',');
			for(String i:s){
				sb.append(i);
				sb.append(',');
			}
			return sb.toString();
		}
		
		return null;
	}
	
	public String getModifInfo2(ProteinSequence ps, Kinase kinase){
		
		StringBuilder result = new StringBuilder();
		String [] motifs = kinase.getMotifArrays();
		
		StringBuilder sb = new StringBuilder();
		String name = ps.getReference();
		result.append('"').append(name).append('"').append(',');
		String sequence = ps.getUniqueSequence();
		Set <Integer> num = new HashSet <Integer> ();
		
		for(String description : motifs){
			ModifDescrib md = getDes(description);
			HashMap <Integer, Boolean> sites = md.getSites();
			Set <Integer> iset = sites.keySet();
			

			Pattern pattern = md.getPattern();
			Matcher match = pattern.matcher(sequence);

			int sitesNum = 0;
			ArrayList <String> s = new ArrayList <String> ();
			s.add(description);

				while(match.find()){
					
					int local = match.start();
					StringBuilder isb = new StringBuilder();
					
					for(int i:iset){
						if(sites.get(i)){
							sitesNum++;
							i+=local;
							String sub = getAroundAA(sequence, i);
							isb.append(i).append(',').append(sub).append(';');
							num.add(i);
						}
						else{
							i+=local;
							String sub = getAroundAA(sequence, i);
							isb.append(i).append('*').append(',').append(sub).append(';');
							num.add(i);
						}
					}
					if(isb.capacity()>0){
						s.add(isb.toString());
			//			System.out.println(isb);
					}else{
						s.add("null");
					}
					
				}
			
			if(sitesNum>0){
				sb.append(sitesNum).append(',');
				for(String i:s){
					sb.append(i);
					sb.append(',');
				}
				sb.append("\n");
				sb.append("\t").append(',').append("\t").append(',');
			}
		}
		
		result.append(num.size()).append(',').append(sb);
		return result.toString();
	}
	
	public String getAroundAA(String sequence, int site){
		int s = (site-7)>=0 ? site-7 : 0;
		int e = (site+6)<= sequence.length() ? site+6 : sequence.length();
		String sub = sequence.substring(s, e);
		return sub;
	}
	
	public Integer [] getModifList(ProteinSequence ps, String description){
		
		ModifDescrib md = getDes(description);
		HashMap <Integer, Boolean> sites = md.getSites();
		Set <Integer> iset = sites.keySet();

		Pattern pattern = md.getPattern();
	
		String sequence = ps.getUniqueSequence();
		Matcher match = pattern.matcher(sequence);

		ArrayList <Integer> s = new ArrayList <Integer> ();
		
		while(match.find()){
				
			int local = match.start();
				
			for(int i:iset){
				if(sites.get(i)){
					i+=local;
					s.add(i);
//					System.out.println(i);
				}
			}
		}
		
		Integer [] modifs = new Integer [s.size()];
		modifs = s.toArray(modifs);
		return modifs;
	}
	
	public Integer [] getModifList(String sequence, String description){
		
		ModifDescrib md = getDes(description);
		HashMap <Integer, Boolean> sites = md.getSites();
		Set <Integer> iset = sites.keySet();

		Pattern pattern = md.getPattern();	
		Matcher match = pattern.matcher(sequence);

		ArrayList <Integer> s = new ArrayList <Integer> ();
		
		while(match.find()){
				
			int local = match.start();
				
			for(int i:iset){
				if(sites.get(i)){
					i+=local;
					s.add(i);
//					System.out.println(i);
				}
			}
		}
		
		Integer [] modifs = new Integer [s.size()];
		modifs = s.toArray(modifs);
		return modifs;
	}
	
	public ModifDescrib getDes(String str){
		char [] charArray = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		int loca = 0;
		HashMap <Integer, Boolean> locaMap = new HashMap <Integer, Boolean> ();
		boolean incre = true;
		
		for(int i=0;i<charArray.length;i++){
			char aa = charArray[i];
			if(aa=='['){
				sb.append(aa);
				loca++;
				incre = false;
			}else if(aa==']'){
				sb.append(aa);
				incre = true;
			}else if(aa=='/'){
				continue;
			}else if(aa=='p'){
				if(incre){
					locaMap.put(loca+1, true);
				}else{
					locaMap.put(loca,true);
				}
			}else if(aa=='*'){
				locaMap.put(loca, false);
				continue;
			}else if(aa=='X'){
				sb.append('.');
				if(incre){
					loca++;
				}
			}else{
				sb.append(aa);
				if(incre){
					loca++;
				}
			}
		}
		
		String strPat = sb.toString();
		Pattern pattern = Pattern.compile(strPat);
		return new ModifDescrib(pattern, locaMap);
	}
	
	public ModifDescrib getModInstance(Pattern pattern, HashMap <Integer, Boolean> locaMap){
		return this.new ModifDescrib(pattern, locaMap);
	}
	
	public Pattern getPattern(){
		return this.pattern;
	}
	
	public class ModifDescrib{
		
		private Pattern pattern;
		private HashMap <Integer, Boolean> sites;
		
		ModifDescrib(Pattern pattern, HashMap <Integer, Boolean> sites){
			this.pattern = pattern;
			this.sites = sites;
		}
		
		public Pattern getPattern(){
			return pattern;
		}
		
		public HashMap <Integer, Boolean> getSites(){
			return sites;
		}
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
			ModifSequGetter mg = new ModifSequGetter("[R/K]X[pS/pT]");
			mg.writePeps("E:\\DataBase\\Final_ipi.HUMAN.v3.17.fasta"
					,"E:\\DataBase\\peps.csv");
			mg.writeSites("E:\\DataBase\\Final_ipi.HUMAN.v3.17.fasta"
					,"E:\\DataBase\\sites.csv");
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub
	}

	public void processNext(String out) throws IOException, XMLStreamException{
		writeSites2(proSeq, out, kinase);
	}
	
}
