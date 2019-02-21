/* 
 ******************************************************************************
 * File:Comparator.java * * * Created on 2010-4-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spectralIndex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2010-4-13, 13:29:06
 */
public class Comparator {
	
	private String dir;
	private int compareNum;
	private String [] fileName = null;
	private File [] files = null;
	private DecimalFormat dfSIn = DecimalFormats.DF_E3;
	
	public Comparator(String dirPath) throws Exception{
		this.dir = dirPath;
		validate();
		compareNum = fileName.length;
	}

	public Comparator(String [] fileStr){
		files = new File[fileStr.length];
		fileName = new String[fileStr.length];
		for(int i=0;i<fileStr.length;i++){
			files[i] = new File(fileStr[i]);
			fileName[i] = files[i].getName();
		}
	}
	
	public void validate() throws Exception{
		File file = new File(dir);
		ArrayList <String> graFileList = new ArrayList<String>();
		String [] graList = null;
		if(file.isDirectory())
			graList=file.list();
		
		for(int i=0;i<graList.length;i++){
			File graFile = new File(dir+"\\"+graList[i]);
			if(graFile.isDirectory()){
				String [] noRedList = graFile.list();
				for(int j=0;j<noRedList.length;j++){
					if(noRedList[j].endsWith(".noredundant")){
						graFileList.add(graList[i]);
						break;
					}
				}
			}
		}
		
		if(graFileList.size()==0)
			throw new Exception("There is no .noredundant file in "+dir);
		
		fileName = graFileList.toArray(new String[graFileList.size()]);
	}
	
	public HashMap <String,Protein> getTotalGradient(String graFilePath, String outFile) throws Exception{
		File file = new File(graFilePath);
		ArrayList <String> nameList = new ArrayList<String>();

		String [] graList = file.list(); 
		for(String name:graList){
			if(name.endsWith(".noredundant")){
				nameList.add(name);
			}
		}

		HashMap <String,Protein> proMap = new HashMap <String,Protein> ();
		NoredundantReader proReader = null;
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		
		for(String readName:nameList){
			proReader = new NoredundantReader(graFilePath+"\\"+readName);
			Protein pro;
			while((pro= proReader.getProtein())!=null){
				pro.simplify2();
				IReferenceDetail refDetail = pro.getReferences()[0];
				String ref = refDetail.getName();
				if(judger.isDecoy(ref))
					continue;
				
				if(proMap.containsKey(ref)){
					Protein pro1 = proMap.get(ref);
					double SIn1 = pro1.getReferences()[0].getSIn();
					double SIn = refDetail.getSIn();
					refDetail.setSIn(Double.parseDouble(dfSIn.format(SIn1+SIn)));
					IReferenceDetail [] refFin = new IReferenceDetail[]{refDetail};
					
					IPeptide [] pep = pro.getAllPeptides();
					IPeptide [] pep1 = pro1.getAllPeptides();
					IPeptide [] pepFin = new IPeptide[pep.length+pep1.length];
/*					
					int i;
					for(i=0;i<pep.length;i++){
						pepFin[i] = pep[i];
					}
					for(int j=0;j<pep1.length;j++){
						pepFin[i+j+1] = pep1[j];
					}
*/
					System.arraycopy(pep, 0, pepFin, 0, pep.length);
					System.arraycopy(pep1, 0, pepFin, pep.length, pep1.length);
					Protein proFin = new Protein(refFin,pepFin);
					proMap.put(ref, proFin);
					
				}else{
					proMap.put(ref, pro);
				}
			}
		}

		IProteinFormat proFormat = proReader.getProteinFormat();
		Protein [] pros = new Protein[proMap.size()];
		int proNum = 0;
		Iterator <Protein> values = proMap.values().iterator();
		while(values.hasNext()){
			pros[proNum]=values.next();
			proNum++;
		}
//		NoredundantWriter.write(outFile, proFormat, pros);
		
		return proMap;
	}

	public void compare() throws Exception{

		HashSet <String> refset = new HashSet<String>();
		refset.add("P62739");
		refset.add("P00634");
		refset.add("P06278");
		refset.add("P00711");
		refset.add("P02666");
		refset.add("P00722");
		refset.add("P02754");
		refset.add("P00921");
		refset.add("P00432");
		refset.add("P62984");
		refset.add("P46406");
		refset.add("P00489");
		refset.add("P00946");
		refset.add("P68082");
		refset.add("P02602");
		refset.add("P01012");
		refset.add("Q29443");
		refset.add("P02769");
		
		HashMap <String,Protein> [] mapList = new HashMap [compareNum];
		HashSet <String> refSet = new HashSet <String> ();
		for(int i=0;i<compareNum;i++){
			String out = dir+"\\"+fileName[i]+".noredundant";
			mapList[i] = getTotalGradient(dir+"\\"+fileName[i],out);
			refSet.addAll(mapList[i].keySet());
		}
		
		CompareInfo [] comInfo = new CompareInfo [refSet.size()];
		int comN = 0;

		for(String ref:refSet){
//			StringBuilder sb = new StringBuilder();
//			int iiiii = 0;
//			sb.append(ref).append("\t");
			double [] SIns = new double [compareNum];
			for(int i=0;i<compareNum;i++){
				if(mapList[i].containsKey(ref)){
					SIns[i]=mapList[i].get(ref).getReferences()[0].getSIn();
//					sb.append(mapList[i].get(ref).getReferences()[0].getCoverage()).append("\t");
//					iiiii++;
				}else{
//					sb.append("\t");
					SIns[i]=0.0;
				}
			}
			comInfo [comN] = new CompareInfo(ref,SIns);
			Iterator <String> it = refset.iterator();
			while(it.hasNext()){
				String r18 = it.next();
				if(ref.contains(r18))
					System.out.println(comInfo[comN]);
			}
//			System.out.println(iiiii+"\t"+sb);
			comN++;
		}

		Arrays.sort(comInfo);
		PrintWriter pw = new PrintWriter(new BufferedWriter(
		        new FileWriter(new File(dir, "SIN_Compare.cmp"))));
		pw.println(getTitle());
		
		for(int j=0;j<comInfo.length;j++){
			comInfo[j].setID(j+1);
			pw.println(comInfo[j].toString());
		}
		pw.close();
		
	}
	
	public void compare(String dir) throws Exception{
		compareNum = files.length;
		HashMap <String,Protein> [] mapList = new HashMap [compareNum];
		HashSet <String> refSet = new HashSet <String> ();
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		
		for(int i=0;i<compareNum;i++){
			mapList[i] = new HashMap <String,Protein> ();
			NoredundantReader reader = new NoredundantReader(files[i]);
			Protein pro;
			while((pro= reader.getProtein())!=null){
				pro.simplify2();
				IReferenceDetail refDetail = pro.getReferences()[0];
				String ref = refDetail.getName();
				if(judger.isDecoy(ref))
					continue;
				mapList[i].put(ref, pro);
				refSet.add(ref);
			}
		}
		CompareInfo [] comInfo = new CompareInfo [refSet.size()];
		int comN = 0;
		Iterator <String> it = refSet.iterator();
		while(it.hasNext()){
			String ref = it.next();
			double [] SIns = new double [compareNum];
			for(int i=0;i<compareNum;i++){
				if(mapList[i].containsKey(ref)){
					SIns[i]=mapList[i].get(ref).getReferences()[0].getSIn();
				}else{
					SIns[i]=0.0;
				}
			}
			comInfo [comN] = new CompareInfo(ref,SIns);
			comN++;
		}
		Arrays.sort(comInfo);
		PrintWriter pw = new PrintWriter(new BufferedWriter(
		        new FileWriter(new File(dir))));
		pw.println(getTitle());
		for(int j=0;j<comInfo.length;j++){
			comInfo[j].setID(j+1);
			pw.println(comInfo[j].toString());
		}
		pw.close();
	}
	
	public void common(String f1, String f2) throws IllegalFormaterException, 
		IOException, ProteinIOException{
		
		NoredundantReader r1 = new NoredundantReader(f1);
		NoredundantReader r2 = new NoredundantReader(f2);
		int num1 = 0;
		int num2 = 0;
		int common = 0;
		HashSet <String> refSet = new HashSet<String>();
		Protein pro1;
		while((pro1= r1.getProtein())!=null){
			num1++;
			pro1.simplify2();
			String ref = pro1.getReferences()[0].getName();
			refSet.add(ref);
		}
		Protein pro2;
		while((pro2= r2.getProtein())!=null){
			num2++;
			pro2.simplify2();
			String ref = pro2.getReferences()[0].getName();
			if(refSet.contains(ref))
				common++;
		}
		System.out.println("num1\t"+num1);
		System.out.println("num2\t"+num2);
		System.out.println("common\t"+common);
	}
	
	public String getTitle(){
		StringBuilder sb = new StringBuilder();
		sb.append("id\t");
		sb.append("Reference\t");
		for(int i=0;i<compareNum;i++){
			sb.append(fileName[i]).append("_SIn\t");
		}
		sb.append("Ratio\t");
		sb.append("RSD\t");
		
		return sb.toString();
	}
	
	public void write(String [] info) throws IOException{
		PrintWriter pw = new PrintWriter(new BufferedWriter(
		        new FileWriter(new File(dir, "Compare.cmp"))));
		pw.println(getTitle());
		for(String s:info){
			pw.println(s);
		}
		pw.close();
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		Comparator c = new Comparator();
//		String file = "E:\\Data\\�½��ļ��� (2)";
//		String total = "E:\\Data\\�½��ļ��� (2)\\H1_1\\H1_1_total.noredundant";
//		c.getTotalGradient(file, total);
//		Comparator c = new Comparator(file);
//		c.compare();
		String s1 = "E:\\Data\\H1_1_total.noredundant";
		String s2 = "E:\\Data\\H1_2_total.noredundant";
		String s3 = "E:\\Data\\H2_1_total.noredundant";
		String s = "E:\\Data\\�½��ļ��� (2)\\H1_1_vs_H1_2.cmp";
//		Comparator c = new Comparator("E:\\Data\\�½��ļ��� (2)");
//		c.compare();
		Comparator d = new Comparator("E:\\Data\\��άSIN");
		
		d.compare();
	}

}
