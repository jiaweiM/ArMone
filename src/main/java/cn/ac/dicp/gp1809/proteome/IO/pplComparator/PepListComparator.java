/* 
 ******************************************************************************
 * File: PepListComparator.java * * * Created on 2011-3-3
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.pplComparator;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2011-3-3, 10:01:16
 */
public class PepListComparator {

	private String [] fileName;
	private File [] fileList;
	private int fileNum;
	private ExcelWriter writer;
	private int [] pepSummary;
	
	public PepListComparator(String peplist, String output) throws IOException{
		this(new File(peplist), output);
	}
	
	public PepListComparator(File peplist, String output) throws IOException{
		if(!peplist.isDirectory())
			throw new IOException("The file "+peplist+" is not seem as a directory.");
		
		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".nord")){
	                return true;
	            }
	            return false;
	        }
	    };
		
		File [] filelist = peplist.listFiles(fileFilter);
		if(filelist==null || filelist.length==0)
	    	throw new FileNotFoundException("There are no *.nord file in this directory : "+peplist);

		this.fileList = filelist;
		this.fileNum = filelist.length;
		this.fileName = new String [fileNum];
		for(int i=0;i<fileNum;i++){
			String name = filelist[i].getName();
			fileName[i] = name.substring(0,name.length()-4);
		}
		
		this.writer = new ExcelWriter(output);
		this.pepSummary = new int [fileNum];
	}
	
	private void getStatInfo() throws IOException, JXLException, FileDamageException, 
			IllegalFormaterException, ProteinIOException{
		
		HashSet [] setlist = new HashSet [fileNum];
		HashSet <String> totalset = new HashSet <String>();
		for(int i=0;i<fileNum;i++){
			setlist[i] = new HashSet <String> ();
			NoredundantReader reader = new NoredundantReader(fileList[i]);
			Protein pro = null;
			while((pro=reader.getProtein())!=null){
				IPeptide [] peps = pro.getAllPeptides();
				for(int j=0;j<peps.length;j++){
					String seq = PeptideUtil.getSequence(peps[j].getSequence());
					totalset.add(seq);
				}
			}
		}
		Iterator <String> it = totalset.iterator();
		while(it.hasNext()){
			String seq = it.next();
			int count = 0;
			for(int i=0;i<fileNum;i++){
				if(setlist[i].contains(seq)){
					count++;
				}
			}
			pepSummary[count-1]++;
		}
		for(int i=0;i<fileNum;i++){
			System.out.println("(i+1)\t"+pepSummary[i]);
		}
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		
	}
	
	private void addSummary() throws RowsExceededException, WriteException{
		
	}
	
	public void write() throws Exception{
		this.addTitle();
		this.getStatInfo();
		this.addSummary();
		this.writer.close();
		System.out.println("Finish!");
		System.gc();
	}
	
	public static void temp(String s1, String s2) throws FileDamageException, IOException, 
			IllegalFormaterException, ProteinIOException{
		
		HashSet [] setlist = new HashSet [2];
		setlist[0] = new HashSet<String>();
		setlist[1] = new HashSet<String>();
		HashSet <String> totalset = new HashSet <String>();
		
		NoredundantReader reader1 = new NoredundantReader(s1);
		Protein p1;
		
		while((p1=reader1.getProtein())!=null){
			IPeptide [] pep1 = p1.getAllPeptides();
			for(int i=0;i<pep1.length;i++){
				String seq = PeptideUtil.getSequence(pep1[i].getSequence());
				setlist[0].add(seq);
				totalset.add(seq);
			}
		}

		NoredundantReader reader2 = new NoredundantReader(s2);
		Protein p2;
		
		while((p2=reader2.getProtein())!=null){
			IPeptide [] pep1 = p2.getAllPeptides();
			for(int i=0;i<pep1.length;i++){
				String seq = PeptideUtil.getSequence(pep1[i].getSequence());
				setlist[1].add(seq);
				totalset.add(seq);
			}
		}

		int [] pepSummary = new int [2];
		Iterator <String> it = totalset.iterator();
		while(it.hasNext()){
			String seq = it.next();
			int count = 0;
			for(int i=0;i<2;i++){
				if(setlist[i].contains(seq)){
					count++;
				}
			}
			pepSummary[count-1]++;
		}
		
		int d1 = setlist[0].size();
		int d2 = setlist[1].size();
		int d11 = d1-pepSummary[1];
		int d22 = d2-pepSummary[1];
		
		System.out.println(d1+"\t"+d11);
		System.out.println(d2+"\t"+d22);
		System.out.println(totalset.size()+"\t"+pepSummary[1]);
	}
	
	public static void temp2(String s1, String s2) throws FileDamageException, IOException, 
		IllegalFormaterException, ProteinIOException{

		HashSet [] setlist = new HashSet [2];
		setlist[0] = new HashSet<String>();
		setlist[1] = new HashSet<String>();
		HashSet <String> totalset = new HashSet <String>();

		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".nord")){
	                return true;
	            }
	            return false;
	        }
	    };
	    
	    File [] filelist1 = new File(s1).listFiles(fileFilter);
	    for(int j=0;j<filelist1.length;j++){
	    	NoredundantReader reader1 = new NoredundantReader(filelist1[j]);
			Protein p1;

			while((p1=reader1.getProtein())!=null){
				IPeptide [] pep1 = p1.getAllPeptides();
				for(int i=0;i<pep1.length;i++){
					String seq = PeptideUtil.getSequence(pep1[i].getSequence());
					setlist[0].add(seq);
					totalset.add(seq);
				}
			}
	    }
		
	    File [] filelist2 = new File(s2).listFiles(fileFilter);
	    for(int j=0;j<filelist2.length;j++){
	    	NoredundantReader reader2 = new NoredundantReader(filelist2[j]);
			Protein p2;

			while((p2=reader2.getProtein())!=null){
				IPeptide [] pep1 = p2.getAllPeptides();
				for(int i=0;i<pep1.length;i++){
					String seq = PeptideUtil.getSequence(pep1[i].getSequence());
					setlist[1].add(seq);
					totalset.add(seq);
				}
			}
	    }

		int [] pepSummary = new int [2];
		Iterator <String> it = totalset.iterator();
		while(it.hasNext()){
			String seq = it.next();
			int count = 0;
			for(int i=0;i<2;i++){
				if(setlist[i].contains(seq)){
					count++;
				}
			}
			pepSummary[count-1]++;
		}

		int d1 = setlist[0].size();
		int d2 = setlist[1].size();
		int d11 = d1-pepSummary[1];
		int d22 = d2-pepSummary[1];

		System.out.println(d1+"\t"+d11);
		System.out.println(d2+"\t"+d22);
		System.out.println(totalset.size()+"\t"+pepSummary[1]);
	}
	
	public static void temp3(String s1, String s2, String out) throws FileDamageException, IOException{
		PeptideListReader r1 = new PeptideListReader(s1);
		HashMap <String, IPeptide> pepMap = new HashMap <String, IPeptide>();
		IPeptide p1;
		while((p1=r1.getPeptide())!=null){
			Integer scanNum = p1.getScanNumBeg();
			short charge = p1.getCharge();
			String key = scanNum+""+charge;
			pepMap.put(key, p1);
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter(out));
		PeptideListReader r2 = new PeptideListReader(s2);
		IPeptide p2;
		while((p2=r2.getPeptide())!=null){
			Integer scanNum = p2.getScanNumBeg();
			short charge = p2.getCharge();
			String key = scanNum+""+charge;
			if(pepMap.containsKey(key)){
				String seq1 = PeptideUtil.getUniqueSequence(pepMap.get(key).getSequence());
				String seq2 = PeptideUtil.getUniqueSequence(p2.getSequence());
				if(seq1.equals(seq2)){
					pw.write(p2+"\n");
				}
			}
		}
		pw.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 	 * @throws FileDamageException 
	 * @throws ProteinIOException 
	 * @throws IllegalFormaterException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, IllegalFormaterException, ProteinIOException {
		// TODO Auto-generated method stub

		String s1 = "F:\\data\\ModDatabase\\SCX_Obi_Phos\\F10_1\\boz";
		String s2 = "F:\\data\\ModDatabase\\SCX_Obi_Phos\\F10_1\\general";
		PepListComparator.temp2(s1, s2);
		
//		String p1 = "F:\\data\\ModDatabase\\SCX_Obi_Phos\\F10_1\\boz\\ms3.ppl";
//		String p2 = "F:\\data\\ModDatabase\\SCX_Obi_Phos\\F10_1\\general\\ms3.ppl";
//		String out = "F:\\data\\ModDatabase\\SCX_Obi_Phos\\F10_1\\overlap_ms3.txt";
//		PepListComparator.temp3(p1, p2, out);
	}

}
