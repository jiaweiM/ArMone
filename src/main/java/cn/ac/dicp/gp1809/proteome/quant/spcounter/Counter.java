/*
 * *****************************************************************************
 * File: Counter.java * * * Created on 12-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.IProteinGroupSimplifier;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.MostLocusSimplifier;

/**
 * Count the number of spectra for protein identification.
 * 
 * @author Xinning
 * @version 0.1, 12-08-2008, 13:34:10
 */
public class Counter {
	
	private static final IProteinGroupSimplifier SIMPLIFIER = new MostLocusSimplifier();
	
	private Map<String,ProStatistic> map;
	
	private File[] infiles;
	
	private IFastaAccesser accesser;
	
	/**
	 * @param files the file(s) which conatins protein information. 
	 * 		  Currently, this file should be "unduplicated" file
	 * @param isCountPep if count peptide information(the spectra 
	 * 		  number for a unique peptide identification)
	 */
//	public Counter(String[] files, boolean isCountPep){
//		try {
//			this.count(files,isCountPep);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * @param files the file(s) which conatins protein information. 
	 * 		  Currently, this file should be "unduplicated" file
	 * @param isCountPep if count peptide information(the spectra 
	 * 		  number for a unique peptide identification)
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 * @throws IOException 
	 * @throws ProteinIOException 
	 * @throws IllegalFormaterException 
	 */
	public Counter(File[] files, boolean isCountPep, boolean isAvg) throws IOException,
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IllegalFormaterException, ProteinIOException{
		map = new HashMap<String,ProStatistic>();
		this.infiles = files;
		this.count(files,isCountPep,isAvg);
	}

/*	
	public Counter(File[] files, boolean isCountPep, boolean isAvg, String database) throws IOException,
		ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IllegalFormaterException, 
		ProteinIOException, FastaDataBaseException{
		
		map = new HashMap<String,ProStatistic>();
		this.infiles = files;
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		this.accesser = new FastaAccesser(database, judger);
		this.count(files,isCountPep,isAvg);
	}
*/	
	public void count(File[] files, boolean isCountPep, boolean isAvg) throws IOException, 
					ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, 
					IllegalFormaterException, ProteinIOException{
		int len = files.length;
		for(int i=0;i<len;i++){
			System.out.println("Reading "+files[i].getName());
			NoredundantReader reader = new NoredundantReader(files[i]);
			Protein pro;
			while((pro= reader.getProtein())!=null){
				String ref = SIMPLIFIER.simplify(pro.getReferences()).getName();
				ProStatistic prostat = map.get(ref);
				if(prostat == null){
					prostat = new ProStatistic(len,isCountPep,isAvg);
					prostat.set(pro, i);
					map.put(ref,prostat);
				}
				else{
					prostat.set(pro, i);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param files
	 * @param isCountPep
	 * @param isAvg
	 * @throws IOException
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws IllegalFormaterException
	 * @throws ProteinIOException
	 * @throws FastaDataBaseException
	 */
/*	
	public void count(File[][] files, boolean isCountPep, boolean isAvg) throws IOException, 
		ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, 
		IllegalFormaterException, ProteinIOException, FastaDataBaseException{
		
		int len = files.length;
		for(int i=0;i<len;i++){
			Proteins pros = new Proteins(accesser);
			for(int j=0;j<files[i].length;j++){
				System.out.println("Reading "+files[i][j].getName());
				NoredundantReader reader = new NoredundantReader(files[i][j]);
				Protein pro;
				while((pro= reader.getProtein())!=null){
					IPeptide [] peps = pro.getAllPeptides();
					for(int k=0;k<peps.length;k++){
						pros.addPeptide(peps[k]);
					}
					
				}
			}
			
			Protein [] prolist = pros.getProteins();
			for(int l=0;l<prolist.length;l++){
				Protein pro = prolist[l];
				String ref = SIMPLIFIER.simplify(pro.getReferences()).getName();
				ProStatistic prostat = map.get(ref);
				if(prostat == null){
					prostat = new ProStatistic(len,isCountPep,isAvg);
					prostat.set(pro, i);
					map.put(ref,prostat);
				}
				else{
					prostat.set(pro, i);
				}
			}
		}
	}
*/	
	/**
	 * @return the proStats after counter.
	 */
	public ProStatistic[] getProStats(){
		return map.values().toArray(new ProStatistic[0]);
	}
	
	/**
	 * @return the map of prostatistic after counter.
	 * 		   the key is the name of prostats, and the value is the instence of pro.
	 */
	public Map<String,ProStatistic> getProStatsMap(){
		return this.map;
	}
	
	/**
	 * Print this counter to a file;
	 * @param filename
	 * @throws IOException 
	 */
	public void printTo(String filename) throws IOException{
		String out = filename.toLowerCase().endsWith(".cnt") ? filename : filename+".cnt";
		File outf = new File(out);
		outf.getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outf)));
		StringBuilder sb = new StringBuilder();
		sb.append("Idx\t");
		for(int i=0;i<infiles.length;i++){
			sb.append("SpC_").append(infiles[i].getName()).append("\t");
		}
		sb.append("Avg_SpC").append("\t");
		sb.append("RSD").append("\t");
		sb.append("UniqueSeqC").append("\t");
		sb.append("Ref").append("\t");
		sb.append("MW").append("\t");
		sb.append("pI");
		
		pw.println(sb.toString());
		ProStatistic[] pros = this.getProStats();
		for(int i=0;i<pros.length;i++){
			pros[i].setIndex(i+1);
			pw.print(pros[i]);
		}
		
		pw.close();
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws IllegalFormaterException
	 * @throws ProteinIOException
	 */
	public static void main(String[] args) throws IOException, 
					ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IllegalFormaterException, ProteinIOException{
		if(args.length!=4){
			System.out.println("Counter folder_with_Unduplicated_files outputfile true_or_false_for_count_peptide" +
					" average_or_median_spectrum_count");
		}
		else{
			File folder = new File(args[0]);
			File[] files = folder.listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
//					if(name.toLowerCase().endsWith(".unduplicated"))
					if(name.toLowerCase().endsWith(".nord"))
						return true;
					return false;
				}
			});
			
			if(files.length==0){
				System.out.println("Please select a folder containing unduplicated files");
			}
			else{
				Counter counter = new Counter(files,Boolean.parseBoolean(args[2]),Boolean.parseBoolean(args[3]));
				counter.printTo(args[1]);
			}
		}
	}
}
