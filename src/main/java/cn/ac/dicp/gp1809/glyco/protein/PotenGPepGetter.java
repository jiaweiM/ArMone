/* 
 ******************************************************************************
 * File: PotenGPepGetter.java * * * Created on 2012-4-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.protein;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.Combinator;

/**
 * @author Administrator
 *
 * @version 2012-4-13, 14:46:55
 */
public class PotenGPepGetter {

	private final Pattern N_GLYCO = Pattern.compile("(N)[A-OQ-Z][ST]");
	
	private int leastLength = 6;
	private static final int MAX_NUM_COMBINATION = 5000;
	private Pattern oxiPattern = Pattern.compile("M");
	private char oxiSym = '*';
	
	private Enzyme enzyme;
	private int miss;
	private int type;
	private MwCalculator calculator;
	private HashMap <String, PotentialGlyPeptide> pepmap;
	
	public PotenGPepGetter(Enzyme enzyme, int miss, int type, MwCalculator calculator){
		
		this.enzyme = enzyme;
		this.miss = miss;
		this.type = type;
		this.calculator = calculator;
		this.pepmap = new HashMap <String, PotentialGlyPeptide>();
	}

	public void find(ProteinSequence proseq){
		
		String ref = proseq.getReference();
		ArrayList <Integer> list = new ArrayList <Integer>();
		String uniqueseq = proseq.getUniqueSequence();
		Matcher m = N_GLYCO.matcher(uniqueseq);
		
		while(m.find()){
			int loc = m.start()+1;
			list.add(loc);
		}
		
		String [] peps = this.enzyme.cleave(uniqueseq, miss, type);
		
		for(int i=0;i<peps.length;i++){
			
			if(peps[i].length()<=this.leastLength)
				continue;
			
			double mass = this.calculator.getMonoIsotopeMZ(peps[i]);
			if(mass>=5000)
				continue;
			
			int beg = uniqueseq.indexOf(peps[i]);
			int end = beg + peps[i].length();
			
			ArrayList <Integer> peplist = new ArrayList <Integer>();
			for(int j=0;j<list.size();j++){
				
				int loc = list.get(j);
				if(loc>beg && loc<=end){
					peplist.add(loc);
				}
			}
			
			if(peplist.size()>0){
				
				int [] locs = new int [peplist.size()];
				for(int j=0;j<locs.length;j++){
					
					locs[j] = peplist.get(j);			
				}

				Matcher matcher = this.oxiPattern.matcher(peps[i]);
				ArrayList <Integer> oxilist = new ArrayList<Integer>();
				int st = 0;
				while (matcher.find(st)) {
					
					int findend = matcher.end();
					oxilist.add(new Integer(findend));
					st = findend;
				}
				
				Integer [] oxisites = oxilist.toArray(new Integer[oxilist.size()]);
				
				if(oxisites.length!=0){
					
					String [] oxiModSeq = getMOxiSeq(peps[i], oxisites, oxiSym);
//					System.out.println("len\t"+oxiModSeq.length+"\t"+oxisites.length);
					for(int j=0;j<oxiModSeq.length;j++){
						
						double modmass = this.calculator.getMonoIsotopeMZ(oxiModSeq[j]);
						PotentialGlyPeptide pep = new PotentialGlyPeptide(oxiModSeq[j], modmass);
						
						if(this.pepmap.containsKey(oxiModSeq[j])){
							
							this.pepmap.get(oxiModSeq[j]).addRef(ref, locs);
							
						}else{
							
							this.pepmap.put(oxiModSeq[j], pep);
							pep.addRef(ref, locs); 
						}
					}
				}
				
				PotentialGlyPeptide pep = new PotentialGlyPeptide(peps[i], mass);
				
				if(this.pepmap.containsKey(peps[i])){
					
					this.pepmap.get(peps[i]).addRef(ref, locs);
					
				}else{
					
					this.pepmap.put(peps[i], pep);
					pep.addRef(ref, locs); 
				}
			}
		}
	}
	
	public void tempFind(String refs, String db, String output) throws IOException, RowsExceededException, WriteException{
		
//		ExcelWriter writer = new ExcelWriter(output);
		PrintWriter writer = new PrintWriter(output);
//		ExcelFormat ef = ExcelFormat.normalFormat;
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		
		FastaReader fr = new FastaReader(db);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String key = ps.getReference().substring(4, 15);
			psmap.put(key, ps);
		}
		fr.close();
		
		BufferedReader reader = new BufferedReader(new FileReader(refs));
		String line = null;
		while((line=reader.readLine())!=null){
			
			if(psmap.containsKey(line)){
				
				ProteinSequence pro = psmap.get(line);
				this.find(pro);
				
			}else{
				System.out.println(line);
			}
		}
		System.out.println(this.pepmap.size());
		reader.close();
		
		Iterator <String> it = this.pepmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			PotentialGlyPeptide pep = pepmap.get(key);
//			writer.addContent(pep.toString(), 0, ef);
			writer.write(pep.toString()+"\n");
		}
		
		writer.close();
	}
	
	private static String [] getMOxiSeq(String sequence, Integer [] oxisites,
	        char sym) {

		int siteNum = oxisites.length;
		ArrayList <String> modlist = new ArrayList <String>();
		
		// one site
		for (int i = 0; i < siteNum; i++) {
			
			modlist.add(getModSequence(sequence,
			        new Integer[] { oxisites[i] }, sym));
		}
		
		// multiple sites
		for(int i=2;i<=siteNum;i++){
			
			Object[][] combines = Combinator.getCombination(oxisites,
			        i);
			
			if(combines.length > MAX_NUM_COMBINATION) {
				
				String [] result = modlist.toArray(new String [modlist.size()]);
				return result;
			}
			
			int len = combines.length;

			for (int j = 0; j < len; j++) {
				
				Object [] tobj = combines[j];
				int l = tobj.length;
				Integer [] tints = new Integer[l];
				
				for (int k = 0; k < l; k++) {
					tints[k] = (Integer) tobj[k];
				}
				modlist.add(getModSequence(sequence, tints, sym));
			}
		}

		String [] result = modlist.toArray(new String [modlist.size()]);
		return result;
	}
	
	private static String getModSequence(String sequence, Integer [] sites,
	        char sym) {
		
		int psnum = sites.length;
		StringBuilder sb = new StringBuilder(sequence.length() + psnum);
		int start = 0;
		
		for (int i = 0; i < psnum; i++) {
			int p = sites[i].intValue();
			sb.append(sequence.substring(start, p));
			sb.append(sym);
			start = p;
		}

		if (start != sequence.length())
			sb.append(sequence.substring(start));

		return sb.toString();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws IOException, RowsExceededException, WriteException {
		// TODO Auto-generated method stub

		Enzyme enzyme = Enzyme.TRYPSIN;
		int miss = 2;
		int type = 1;
		MwCalculator calculator = new MwCalculator();
		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		calculator.setAacids(aas);
		
		AminoacidModification aamodif = new AminoacidModification();
		aamodif.addModification(ModSite.newInstance_aa('M'), '*', 15.994919, "");
		calculator.setAamodif(aamodif);
		
		PotenGPepGetter getter = new PotenGPepGetter(enzyme, miss, type, calculator);
		getter.tempFind("H:\\Protein_IPI.txt", 
				"F:\\DataBase\\ipi.HUMAN.v3.68\\ipi.HUMAN.v3.68.fasta", 
				"H:\\glycopep_new_oxi.txt");
	}

}
