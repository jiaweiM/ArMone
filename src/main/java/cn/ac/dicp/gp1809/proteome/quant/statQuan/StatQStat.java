/* 
 ******************************************************************************
 * File:StatQStat.java * * * Created on 2010-9-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.statQuan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.opencsv.CSVReader;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2010-9-9, 18:14:48
 */
public class StatQStat {

	private IFastaAccesser accesser;
	private ExcelWriter writer;
	private File [] filelist;
	private int fileNum;
	private String ex;
	private HashMap <String, HashSet<String>> refMap;
	
	private int [] proRepNum;
	private int [] pepRepNum;
	private int totalNum;
	private int totalPepNum;
	private int [] proRatio;
	private int [] pepRatio;

	private DecimalFormat dfPer = new DecimalFormat("#.###%");
	private DecimalFormat df4 = new DecimalFormat("#.####");
	
	public StatQStat(String file, String pepfile, String fasta, String out) throws FastaDataBaseException, IOException{
		this(new File(file), new File(pepfile), new FastaAccesser(fasta, new DefaultDecoyRefJudger()), out);
	}
	
	public StatQStat(String file, String pepfile, String fasta, String out, String ex) throws FastaDataBaseException, IOException, JXLException{
		this(new File(file), new File(pepfile), new FastaAccesser(fasta, new DefaultDecoyRefJudger()), out, ex);
	}
	
	public StatQStat(File file, File pepFile, IFastaAccesser accesser, String out) throws IOException{
		this.accesser = accesser;
		this.writer = new ExcelWriter(out, new String [] {"Protein","Peptide"});
		if(!file.isDirectory())
			throw new IOException("The file "+file+" is not seem as a directory.");
		
		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".xls")){
	                return true;
	            }
	            return false;
	        }
	    };
	    
	    this.filelist = file.listFiles(fileFilter);
		if(filelist==null || filelist.length==0)
	    	throw new FileNotFoundException("There are no *.xls file in this directory : "+file);
				
		this.fileNum = filelist.length;
		this.proRepNum = new int [fileNum];
		this.pepRepNum = new int [fileNum];
		this.proRatio = new int [5];
		this.pepRatio = new int [5];
		
		FileFilter csvFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".csv")){
	                return true;
	            }
	            return false;
	        }
	    };
	    File [] csvfiles = pepFile.listFiles(csvFilter);
	    this.getPepMap2(csvfiles);
	    
	}
	
	public StatQStat(File file, File pepFile, IFastaAccesser accesser, String out, String ex) throws IOException, JXLException{
		
		this.ex = ex;
		this.accesser = accesser;
		this.writer = new ExcelWriter(out, new String [] {"Protein","Peptide"});
		if(!file.isDirectory())
			throw new IOException("The file "+file+" is not seem as a directory.");
		
		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".xls")){
	                return true;
	            }
	            return false;
	        }
	    };
	    
	    this.filelist = file.listFiles(fileFilter);
		if(filelist==null || filelist.length==0)
	    	throw new FileNotFoundException("There are no *.xls file in this directory : "+file);
				
		this.fileNum = filelist.length;
		this.proRepNum = new int [fileNum];
		this.pepRepNum = new int [fileNum];
		this.proRatio = new int [5];
		this.pepRatio = new int [5];
		
		this.refMap = new HashMap <String, HashSet <String>>();
		ExcelReader pepRefReader = new ExcelReader(pepFile, 1);
		String [] cs = null;
		while((cs=pepRefReader.readLine())!=null){
			
			if(refMap.containsKey(cs[0])){
				refMap.get(cs[0]).add(cs[8]);
			}else{
				HashSet <String> set = new HashSet <String>();
				set.add(cs[8]);
				refMap.put(cs[0], set);
			}
		}
	    
		pepRefReader.close();
	}
	
	public void getPepMap(File [] csvfiles) throws IOException{
		HashMap <String, HashSet<String>> refMap = new HashMap <String, HashSet<String>> ();
		for(int i=0;i<csvfiles.length;i++){
			String s;
			BufferedReader reader = new BufferedReader(new FileReader(csvfiles[i]));
			while((s=reader.readLine())!=null){
				String [] strs = s.split(",");
				if(strs[0].startsWith("\"Protein")){
					break;
				}
			}
			String ref = null;
			while((s=reader.readLine())!=null){
				String [] strs1 = s.split("\",");
				if(strs1.length==2){
					String [] strs2 = strs1[0].split(",");
					String seq = strs2[18];
					if(refMap.containsKey(seq)){
						refMap.get(seq).add(ref);
					}else{
						HashSet <String> refset = new HashSet <String> ();
						refset.add(ref);
						refMap.put(seq, refset);
					}
				}else if(strs1.length==4){
					String [] strs4 = strs1[0].split(",");
					ref = strs4[1].substring(1,strs4[1].length());
					
					String [] strs5 = strs1[2].split(",");
					String seq = strs5[15];
					if(refMap.containsKey(seq)){
						refMap.get(seq).add(ref);
					}else{
						HashSet <String> refset = new HashSet <String> ();
						refset.add(ref);
						refMap.put(seq, refset);
					}
				}
			}
			reader.close();
			reader = null;
		}
		System.out.println("Map\t"+refMap.size());
		this.refMap = refMap;
		Iterator <String> it = refMap.keySet().iterator();
		while(it.hasNext()){
			System.out.println(it.next()+"\t");
		}
	}
	
	public void getPepMap2(File [] csvfiles) throws IOException{
		HashMap <String, HashSet<String>> refMap = new HashMap <String, HashSet<String>> ();
		for(int i=0;i<csvfiles.length;i++){
			BufferedReader r = new BufferedReader(new FileReader(csvfiles[i]));
			CSVReader reader = new CSVReader(r);
			String [] columns;
			int pep = -1;
			int pro = -1;
			
			String ref = null;
			boolean begin = false;
			while((columns=reader.readNext())!=null){
				
				if(begin){
					String seq = columns[pep];
					String prostr = columns[pro];
					if(prostr!=null && prostr.length()>0)
						ref = prostr;
					
					if(refMap.containsKey(seq)){
						refMap.get(seq).add(ref);
					}else{
						HashSet <String> refset = new HashSet <String> ();
						refset.add(ref);
						refMap.put(seq, refset);
					}
					
				}else{
					if(columns[0].equals("prot_hit_num")){
						begin = true;
						for(int j=0;j<columns.length;j++){
							if(columns[j].equals("prot_acc")){
								pro = j;
							}
							if(columns[j].equals("pep_seq")){
								pep = j;
							}
						}
					}
				}
			}
			r.close();
			reader.close();
			reader = null;
		}
//		System.out.println("Map\t"+refMap.size());
		this.refMap = refMap;
	}
	
	public void addTitle() throws RowsExceededException, WriteException{
		StringBuilder sb = new StringBuilder();
		sb.append("Protein Index\t").append("Protein\t\t\t").
			append("Num\t").append("Ave\t").append("RSD\t");
		for(int i=0;i<fileNum;i++){
			String n = filelist[i].getName();
			sb.append(n.subSequence(0, n.length()-4)).append("\t");
		}
		sb.append("\n\t").append("Peptide Index\t").append("Sequence\t").append("Modifition");
		ExcelFormat f1 = new ExcelFormat(false,0);
		writer.addTitle(sb.toString(),0, f1);
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("Sequence\t").append("Modifition\t").append("Num\t").append("Ave\t")
			.append("RSD\t");
		for(int i=0;i<fileNum;i++){
			String n = filelist[i].getName();
			sb2.append(n.subSequence(0, n.length()-4)).append("\t");
		}
		sb2.append("Reference\t");
		writer.addTitle(sb2.toString(),1, f1);
	}
/*	
	public void getStatInfo() throws IOException, JXLException, FastaDataBaseException, 
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, BioException{
		
		HashSet <String> totalSet = new HashSet <String>();
		HashMap <String, Protein> [] proMap = new HashMap [fileNum];
		for(int i=0;i<fileNum;i++){
			StatQReader reader = new StatQReader(filelist[i], accesser);
//			proMap[i] = reader.getInfo();
			Iterator <String> it = proMap[i].keySet().iterator();
			while(it.hasNext()){
				String ref = it.next();
				String [] refs = ref.split("$");
//				if(refs.length>1)
//					System.out.println(ref);
				for(int j=0;j<refs.length;j++){
					totalSet.add(refs[j]);
				}				
			}
		}
		
		ProStat [] pstat = new ProStat[totalSet.size()];
		Iterator <String> it = totalSet.iterator();
		int index = 0;
		boolean group = false;
		while(it.hasNext()){
			String ref = it.next();
			Protein [] pros = new Protein[fileNum];
			String [] refs = new String[fileNum];
			int num = 0;
			for(int i=0;i<fileNum;i++){
				Iterator <String> mapIt = proMap[i].keySet().iterator();
				while(mapIt.hasNext()){
					String refstr = mapIt.next();
					if(refstr.contains(ref)){
						pros[i] = proMap[i].get(refstr);
						refs[i] = refstr;
						num++;
						if(refstr.contains("$"))
							group = true;
					}
				}
			}
			pstat[index] = new ProStat(ref, refs, num, pros);
			this.repNum[num-1]++;
			index++;			
		}
		this.totalNum = index;
		Arrays.sort(pstat);
		for(int i=0;i<pstat.length;i++){
			ProStat prostat = pstat[i];
			prostat.setIndex(i+1);
			double ave = prostat.ave;
			if(ave>=2){
				this.proRatio[4]++;
			}else if(ave<2 && ave>=1.2){
				this.proRatio[3]++;
			}else if(ave<1.2 && ave>=0.8){
				this.proRatio[2]++;
			}else if(ave<0.8 && ave>=0.5){
				this.proRatio[1]++;
			}else if(ave<0.5){
				this.proRatio[0]++;
			}
			
			PepStat [] peps = prostat.pepInfo;
			for(int j=0;j<peps.length;j++){
				totalPepNum++;
				double pr = peps[j].ave;
				if(pr>=2){
					this.pepRatio[4]++;
				}else if(pr<2 && pr>=1.2){
					this.pepRatio[3]++;
				}else if(pr<1.2 && pr>=0.8){
					this.pepRatio[2]++;
				}else if(pr<0.8 && pr>=0.5){
					this.pepRatio[1]++;
				}else if(pr<0.5){
					this.pepRatio[0]++;
				}
			}
			if(group)
				writer.addContent(prostat.toString(), true, 1);
			else
				writer.addContent(prostat.toString(), true);
		}
	}
*/
	
	public void getStatInfo(boolean filter) throws IOException, JXLException, FastaDataBaseException, 
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException{
		
		HashMap <String, QPeptide> [] pepMap = new HashMap [fileNum];
		Proteins pros = new Proteins(accesser);
		for(int i=0;i<fileNum;i++){
			
			StatQReader reader = new StatQReader(filelist[i], accesser);
//			pepMap[i] = reader.getInfo();
			pepMap[i] = reader.getInfo5(ex);
			
			Iterator <String> it = pepMap[i].keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				QPeptide p = pepMap[i].get(key);
				String seq = p.getSequence();
				
				if(refMap.containsKey(seq)){
					HashSet <String> rset = this.refMap.get(seq);
					HashSet <ProteinReference> refSet = new HashSet <ProteinReference> ();
					Iterator <String> rSetIt = rset.iterator();
					while(rSetIt.hasNext()){
						String r = rSetIt.next();
						ProteinReference pr = new ProteinReference(r, false);
						refSet.add(pr);
					}
					p.setProteinReference(refSet);
//					System.out.println("found\t"+seq);
				}else{
//					System.out.println(i+"\tnotfound\t"+seq);
				}
				
				pros.addPeptide(p);
			}
		}
		
		Protein [] prolist = pros.getAllProteins();
		Arrays.sort(prolist);
		
		HashMap <String, String> refmap = new HashMap <String, String>();
		
		HashSet <String> allSet = new HashSet <String> ();
		for(int i=0;i<prolist.length;i++){
			
			IReferenceDetail [] refs = prolist[i].getReferences();
			String [] refNames = new String [refs.length];
			StringBuilder sb = new StringBuilder();
			for(int j=0;j<refNames.length;j++){
				refNames[j] = refs[j].getName();
				sb.append(refNames[j]).append("$");
			}
			String refStr = sb.toString();
			refStr = refStr.substring(0,refStr.length()-1);
			
			boolean dis = prolist[i].getUnique();
			IPeptide [] peps = prolist[i].getAllPeptides();
			ArrayList <PepStat> pepstats = new ArrayList <PepStat> ();
			HashSet <String> usedSet = new HashSet <String> ();
			for(int j=0;j<peps.length;j++){
				
				QPeptide p = (QPeptide)peps[j];

				String key = p.getKey();
				
				if(!refmap.containsKey(key)){
					refmap.put(key, refStr);
				}
				
				if(!usedSet.contains(key)){
					usedSet.add(key);
					QPeptide [] qps = new QPeptide[fileNum];
					HashMap <String, Integer> modCount = new HashMap <String, Integer>();
					HashMap <String, Double> modScoreMap = new HashMap <String, Double>();
					HashMap <String, String> modInfoMap = new HashMap <String, String>();
					for(int k=0;k<fileNum;k++){
						if(pepMap[k].containsKey(key)){
							qps[k] = pepMap[k].get(key);
							HashMap <String, Double> modScore = qps[k].getModScore();
							HashMap <String, String> modInfo = qps[k].getModInfo();

							Iterator <String> it = modScore.keySet().iterator();
							while(it.hasNext()){
								String mod = it.next();
								if(modCount.containsKey(mod)){
									int count = modCount.get(mod)+1;
									modCount.put(mod, count);
									
									double s0 = modScoreMap.get(mod);
									double s1 = modScore.get(mod);
									if(s1>s0)
										modScoreMap.put(mod, s1);
								}else{
									modCount.put(mod, 1);
									modScoreMap.put(mod, modScore.get(mod));
									modInfoMap.put(mod, modInfo.get(mod));
								}
							}
						}else{
							qps[k] = null;
						}
					}
					
					String usemod = "";
					int modc = 0;
					double ms = 0.0;
					Iterator <String> mapit = modCount.keySet().iterator();
					while(mapit.hasNext()){
						String mod = mapit.next();
						int c = modCount.get(mod);
						if(c>modc){
							usemod = mod;
							modc = c;
						}
						else if(c==modc){
							double s0 = modScoreMap.get(mod);
							if(s0>ms){
								usemod = mod;
								ms = s0;
							}
						}
					}
					
					String finalMod = "";
					if(modCount.size()==0){
						finalMod = "";
					}else{
						finalMod = modScoreMap.get(usemod)+"  "+modInfoMap.get(usemod);
					}
					
					PepStat ps = new PepStat(p.getSequence(), finalMod, qps);
				
					pepstats.add(ps);
					
					if(!allSet.contains(key)){
						allSet.add(key);
						
						totalPepNum++;
						double pr = ps.ave;
						if(pr>=2){
							this.pepRatio[4]++;
						}else if(pr<2 && pr>=1.2){
							this.pepRatio[3]++;
						}else if(pr<1.2 && pr>=0.8){
							this.pepRatio[2]++;
						}else if(pr<0.8 && pr>=0.5){
							this.pepRatio[1]++;
						}else if(pr<0.5){
							this.pepRatio[0]++;
						}
						
						int psn = ps.num;
						this.pepRepNum[psn-1]++;
						
						ExcelFormat fpep = new ExcelFormat(false,0);
//						writer.addContent(ps.toString()+p.getProteinReferenceString(), 1, fpep);
						writer.addContent(ps.toString()+refmap.get(key), 1, fpep);
					}
				}
			}
			ProStat prostat = new ProStat(refNames, fileNum, i+1, pepstats);
			ExcelFormat f1 = new ExcelFormat(true,0);
			ExcelFormat f2 = new ExcelFormat(true,1);
			ExcelFormat f3 = new ExcelFormat(true,2);
			
			if(dis){
				double ave = prostat.ave;
				if(ave>=2){
					this.proRatio[4]++;
				}else if(ave<2 && ave>=1.2){
					this.proRatio[3]++;
				}else if(ave<1.2 && ave>=0.8){
					this.proRatio[2]++;
				}else if(ave<0.8 && ave>=0.5){
					this.proRatio[1]++;
				}else if(ave<0.5 && ave>0){
					this.proRatio[0]++;
				}
				
				this.totalNum++;
				int num = prostat.num;
				this.proRepNum[num-1]++;
				
				if(filter){
					if(ave==0){
						this.writer.addContent(prostat.toString(), 0, f3);
					}else{
						this.writer.addContent(prostat.toString(), 0, f1);
					}
				}else{
					this.writer.addContent(prostat.toString(), 0, f1);
				}
				
			}else{
				this.writer.addContent(prostat.toString(), 0, f2);
			}		
		}
	}
	
	public void addSummary() throws RowsExceededException, WriteException{
		ExcelFormat f1 = new ExcelFormat(false,0);
		writer.addTitle("\n\n\n-------------------Summary-------------------\n",0,f1);
		StringBuilder sb = new StringBuilder("\n");
		sb.append("\tProtein Num\tPercent\t").append("Peptide Num\tPercent\n");
		for(int i=fileNum;i>0;i--){
			sb.append("In "+i+" files:\t").append(proRepNum[i-1]).append("\t")
				.append(dfPer.format((float)proRepNum[i-1]/(float)totalNum))
				.append("\t").append(pepRepNum[i-1]).append("\t")
				.append(dfPer.format((float)pepRepNum[i-1]/(float)totalPepNum)).append("\n");
		}
		
		sb.append("\n\n");
		sb.append(" \t").append("Protein Ratio\t").append("Percent\t").append("Peptide Ratio\t").append("Percent\n");
		sb.append("Ratio: >=2\t").append(proRatio[4]).append("\t").append(dfPer.format((float)proRatio[4]/totalNum)).append("\t")
			.append(pepRatio[4]).append("\t").append(dfPer.format((float)pepRatio[4]/totalPepNum)).append("\n");
		sb.append("Ratio: 1.2~2\t").append(proRatio[3]).append("\t").append(dfPer.format((float)proRatio[3]/totalNum)).append("\t")
			.append(pepRatio[3]).append("\t").append(dfPer.format((float)pepRatio[3]/totalPepNum)).append("\n");
		sb.append("Ratio: 0.8~1.2\t").append(proRatio[2]).append("\t").append(dfPer.format((float)proRatio[2]/totalNum)).append("\t")
			.append(pepRatio[2]).append("\t").append(dfPer.format((float)pepRatio[2]/totalPepNum)).append("\n");
		sb.append("Ratio: 0.5~0.8\t").append(proRatio[1]).append("\t").append(dfPer.format((float)proRatio[1]/totalNum)).append("\t")
			.append(pepRatio[1]).append("\t").append(dfPer.format((float)pepRatio[1]/totalPepNum)).append("\n");
		sb.append("Ratio: <0.5\t").append(proRatio[0]).append("\t").append(dfPer.format((float)proRatio[0]/totalNum)).append("\t")
			.append(pepRatio[0]).append("\t").append(dfPer.format((float)pepRatio[0]/totalPepNum)).append("\n");
		sb.append("Total Number\t").append(totalNum).append("\t100%\t").append(totalPepNum).append("\t100%\t");
		
		ExcelFormat f2 = new ExcelFormat(true,0);
		this.writer .addContent(sb.toString(), 0, f2);
	}
	
	public void write(boolean filter) throws ProteinNotFoundInFastaException, 
			MoreThanOneRefFoundInFastaException, BioException, IOException, 
			JXLException, FastaDataBaseException{
		
		this.addTitle();
		this.getStatInfo(filter);
		this.addSummary();
		this.writer.close();
	}
	
	public class ProStat implements Comparable <ProStat> {
		
		private String ref;
		private String [] refs;
		private int num;
		private int index;
		private double [] ratios;
		private double ave;
		private double RSD;
		private int fileNum;
		private ArrayList <PepStat> pepstats;
		
		public ProStat(String [] refs, int fileNum, int index, ArrayList <PepStat> pepstats){
			this.refs = refs;
			this.fileNum = fileNum;
			this.index = index;
			this.pepstats = pepstats;		
			this.getInfo();
		}
		
		public void getInfo(){
/*			
			ArrayList <Double> [] ratioList = new ArrayList [fileNum];
			for(int i=0;i<ratioList.length;i++)
				ratioList [i] = new ArrayList <Double> ();
				
			for(int i=0;i<pepstats.size();i++){
				PepStat ps = pepstats.get(i);
				if(ps.num>2 && ps.RSD<0.5){
					double [] ratios = ps.ratios;
					int disloc = ps.disLoc;
					for(int j=0;j<ratios.length;j++){
						if(ratios[j]>0){
							if(disloc != i)
								ratioList[j].add(ratios[j]);
						}	
					}
				}
			}
			
			this.ratios = new double [fileNum];
			ArrayList <Double> data = new ArrayList <Double> ();
			for(int i=0;i<ratios.length;i++){
				ratios[i] = MathTool.getAve(ratioList[i]);
				if(ratios[i]>0){
					data.add(ratios[i]);
					num++;
				}					
			}
*/
			int [] nums = new int [fileNum];
			Arrays.fill(nums, 0);
			ArrayList <Double> data = new ArrayList <Double> ();
			for(int i=0;i<pepstats.size();i++){
				PepStat ps = pepstats.get(i);
				
//	Sometimes this filter can be used.				
//				if(ps.num>2 && ps.RSD<0.5){
					data.add(ps.ave);
//				}
				double [] rs = ps.ratios;
				for(int j=0;j<rs.length;j++){
					if(rs[j]>0)
						nums[j] = 1;
				}
			}
			for(int k=0;k<nums.length;k++){
				this.num += nums[k];
			}
			this.ave = MathTool.getAveInDouble(data);
			this.RSD = MathTool.getRSDInDouble(data);
		}
/*		
		public void getInfo(){
			HashMap <String, QPeptide> [] pepMap = new HashMap [fileNum];
			HashSet <String> pepSet = new HashSet <String> ();
			ArrayList <Double> data = new ArrayList <Double> ();
			for(int i=0;i<pros.length;i++){
				if(pros[i]!=null){
					pepMap[i] = new HashMap <String, QPeptide> ();
					IPeptide [] peps = pros[i].getAllPeptides();
					ArrayList <Double> ratiolist = new ArrayList <Double> ();
					for(int j=0;j<peps.length;j++){
						QPeptide pep = (QPeptide) peps[j];
						pepMap[i].put(pep.getSequence(), pep);
						pepSet.add(pep.getSequence());
						ratiolist.add(pep.getRatio());
					}
					ratios[i] = MathTool.getAve(ratiolist);
					data.add(ratios[i]);
				}
				else{
					ratios[i] = 0.0;
				}
			}

			this.ave = MathTool.getAve(data);
			this.RSD = MathTool.getRSD(data);
			
			PepStat [] pepInfo = new PepStat[pepSet.size()];
			int count = 0;
			Iterator <String> setIt = pepSet.iterator();
			while(setIt.hasNext()){
				QPeptide [] peps = new QPeptide [fileNum];
				String seq = setIt.next();
				int num = 0;
				for(int i=0;i<fileNum;i++){
					if(pepMap[i]!=null){
						if(pepMap[i].containsKey(seq)){
							peps[i] = pepMap[i].get(seq);
							num++;
						}else{
							peps[i] = null;
						}
					}else{
						peps[i] = null;
					}
				}
				pepInfo[count] = new PepStat(seq, num, peps);
				count++;
			}			
			Arrays.sort(pepInfo);
			this.pepInfo = pepInfo;
		}
*/
		public void setIndex(int index){
			this.index = index;
		}
		
		public int getReNum(){
			return num;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(ProStat o) {
			// TODO Auto-generated method stub
			int n1 = this.num;
			int n2 = o.num;
			if(n1>n2)
				return -1;
			else if(n1<n2)
				return 1;
			else{
				String r1 = this.ref;
				String r2 = o.ref;
				return r1.compareTo(r2);
			}				
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<refs.length;i++){
				sb.append(index+"-"+(i+1)).append("\t").append(refs[i]).append("\t\t\t").append(num).append("\t")
					.append(ave).append("\t").append(dfPer.format(RSD)).append("\t");
//				for(int j=0;j<ratios.length;j++){
//					sb.append(ratios[j]).append("\t");
//				}
				sb.append("\n");
			}			
			for(int j=0;j<pepstats.size();j++){
				sb.append("\t").append(index+"-"+(j+1)).append("\t").append(pepstats.get(j)).append("\n");
			}
			return sb.toString();
		}
	}
	
	public class PepStat implements Comparable <PepStat> {
		
		private String seq;
		private String mod;
		private QPeptide [] peps;
		private int num;
		private double ave;
		private double RSD;
		private double [] ratios;
		private int disLoc = -1;
		
		public PepStat(String seq, String mod, QPeptide [] peps){
			this.seq = seq;
			this.mod = mod;
			this.peps = peps;
			this.ratios = new double [peps.length];
			this.getInfo();
		}

		public void getInfo(){
			ArrayList <Double> data = new ArrayList <Double>();
			ArrayList <Double> filter = new ArrayList <Double>(); 
			for(int i=0;i<peps.length;i++){
				if(peps[i]!=null){
					ratios[i] = peps[i].getRatio();					
					data.add(ratios[i]);
					num++;
					if(ratios[i]!=0){
						filter.add(ratios[i]);
					}
				}else{
					ratios[i] = 0.0;
				}
			}
			this.ave = MathTool.getAveInDouble(data);
			this.RSD = MathTool.getRSDInDouble(data);

			if(filter.size()>3){
				if(this.RSD >= 0.5){
					Double [] r = filter.toArray(new Double[filter.size()]);	
					Arrays.sort(r);
					
					ArrayList <Double> small = new ArrayList <Double>();
					ArrayList <Double> big = new ArrayList <Double>();
					
					for(int i=0;i<r.length;i++){
						if(i==0){
							small.add(r[i]);
						}else if(i==r.length-1){
							big.add(r[i]);
						}else{
							small.add(r[i]);
							big.add(r[i]);
						}
					}
					
					double rsdSmall = MathTool.getRSDInDouble(small);
					double rsdBig = MathTool.getRSDInDouble(big);
					if(rsdSmall<0.5){
						this.ave = MathTool.getAveInDouble(small);
						this.RSD = MathTool.getRSDInDouble(small);
						for(int i=0;i<ratios.length;i++){
							if(ratios[i]==r[r.length-1]){
								this.disLoc = i;
							}
						}
					}else{
						if(rsdBig<0.5){
							this.ave = MathTool.getAveInDouble(big);
							this.RSD = MathTool.getRSDInDouble(big);
							for(int i=0;i<ratios.length;i++){
								if(ratios[i]==r[0]){
									this.disLoc = i;
								}
							}
						}
					}
				}
			}
		}
		
		public double [] getRatios(){
			return ratios;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(PepStat o) {
			// TODO Auto-generated method stub
			int n1 = this.num;
			int n2 = o.num;
			if(n1>n2)
				return -1;
			else if(n1<n2)
				return 1;
			else{
				String r1 = this.seq;
				String r2 = o.seq;
				return r1.compareTo(r2);
			}
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(seq).append("\t").append(mod).append("\t").append(num).append("\t")
				.append(ave).append("\t").append(dfPer.format(RSD)).append("\t");
			for(int i=0;i<ratios.length;i++){
				if(this.disLoc == i){
					sb.append("(").append(df4.format(ratios[i])).append(")").append("\t");
				}else{
					sb.append(df4.format(ratios[i])).append("\t");
				}
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FastaDataBaseException 
	 * @throws JXLException 
	 * @throws BioException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 */
	public static void main(String[] args) throws FastaDataBaseException, IOException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, BioException, JXLException {
		// TODO Auto-generated method stub

		String in = "D:\\�½��ļ���\\2";
		String pep = "D:\\�½��ļ���\\phosphopeptide_final_results���̿�.xls";
		String ex = "D:\\�½��ļ���\\phosphopeptide_final_results���̿�.xls";
		String out = "D:\\�½��ļ���\\final_results_2.xls";
		String fasta = "F:\\DataBase\\ipi.HUMAN.v3.52\\Final_ipi_human352_0.fasta";
		StatQStat s = new StatQStat(in, pep, fasta, out, ex);
		s.write(false);
	}

}
