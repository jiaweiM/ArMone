/* 
 ******************************************************************************
 * File:StatQReader.java * * * Created on 2010-9-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.statQuan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
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

/**
 * @author ck
 *
 * @version 2010-9-9, 04:03:31
 */
public class StatQReader {

	private ExcelReader reader;
	private IFastaAccesser accesser;
	private File file;
	
	public StatQReader(String file, String fasta) throws IOException, 
			JXLException, FastaDataBaseException{
		this(new File(file), new FastaAccesser(fasta, new DefaultDecoyRefJudger()));
	}
	
	public StatQReader(File file, IFastaAccesser accesser) throws IOException, 
			JXLException, FastaDataBaseException{
		this.file = file;
		this.reader = new ExcelReader(file);
		this.accesser = accesser;
	}
/*	
	public HashMap <String, Protein> getInfo() throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, 
			FastaDataBaseException, BioException{
		reader.skip(2);
		Proteins pros = new Proteins(accesser);
		String [] row;
		HashMap <String, Double> seqMap = new HashMap <String, Double> ();
		while((row = reader.readLine())!=null){
			if(row.length == 12){
				
				String seq = row[2];
				String scan = row[9];
				double ratio = Double.parseDouble(row[4]);
				seqMap.put(seq+"$"+scan, ratio);
				
				HashSet <ProteinReference> refset = new HashSet <ProteinReference> ();				
				String ref = row[1];
				ProteinReference pr = ProteinReference.newInstance(ref);
				refset.add(pr);
				String seq = row[2];
				
				IPeptide pep = new QPeptide(seq, refset, "", ratio);
				pros.addPeptide(pep);
			}
		}
		
		Iterator <String> mapIt = seqMap.keySet().iterator();
		while(mapIt.hasNext()){
			String key = mapIt.next();
			String [] ss = key.split("$");
			String seq = ke
		}
		Protein [] prolist = pros.getProteins();
		HashMap <String, Protein> proMap = new HashMap <String, Protein> ();
		for(int i=0;i<prolist.length;i++){
//			prolist[i].simplify();
			IReferenceDetail [] proref = prolist[i].getReferences();
			StringBuilder sb = new StringBuilder();
			for(int j=0;j<proref.length;j++){
				String ref = proref[j].getName().substring(4,18);
				sb.append(ref).append("$");
			}
			sb.deleteCharAt(sb.length()-1);
			proMap.put(sb.toString(), prolist[i]);
		}
		System.out.println(file.getName()+"\t"+prolist.length);
		return proMap;
	}	
*/	
	/**
	 * have mod information, such as 38.65 SHIPSEPYEPIpSPPQGPAVHEK b12/y11
	 */
	public HashMap <String, QPeptide> getInfo(){
		reader.skip(2);
		
//		Pattern pattern = Pattern.compile("\\s[\\D]+\\s");
//		Pattern pattern2 = Pattern.compile(".*?\\s*,");
		HashMap <String, ArrayList<QPeptide>> peplistMap = new HashMap <String, ArrayList<QPeptide>> ();
		HashMap <String, QPeptide> pepMap = new HashMap <String, QPeptide> ();
		String [] row;
		while((row = reader.readLine())!=null){

			if(row.length == 12){
				String ref = row[1];
				String seq = row[2];
				String mod = row[3];
				String modkey = "";
//				Matcher m = pattern.matcher(mod);
//				Matcher m2 = pattern2.matcher(mod);
//				System.out.println(seq);
				
				HashMap <String, Double> modScore = new HashMap <String, Double>();
				HashMap <String, String> modInfo = new HashMap <String, String>();
				String [] ss0 = mod.split(",");
				if(ss0.length>0){
					for(int i=0;i<ss0.length;i++){
						
						String [] ss1 = ss0[i].split("\\s");
						if(ss1.length>=3){

							double score = Double.parseDouble(ss1[0]);
							String mseq = ss1[1];
							
							String modchar = "";
							char [] cs = mseq.toCharArray();
							ArrayList <Character> clist = new ArrayList <Character>();
							for(int j=0;j<cs.length;j++){
								if(cs[j]<'A' || cs[j]>'Z'){
									clist.add(cs[j]);
								}
							}
							Character [] carrays = clist.toArray(new Character [clist.size()]);
							Arrays.sort(carrays);
							for(int k=0;k<carrays.length;k++){
								modchar += carrays[k];
							}

							modScore.put(mseq, score);
							String by = "";
							for(int j=1;j<ss1.length;j++){
								by += ss1[j];
								by += " ";
							}
							modInfo.put(mseq, by);
							
							modkey = modchar;
						}
					}
				}
				
/*				
				HashSet <String> modset = new HashSet <String>();
				if(m.find() && m2.find()){
					String group = m.group();
					modset.add(group);
					char [] cs = group.toCharArray();
					ArrayList <Character> clist = new ArrayList <Character>();
					for(int i=0;i<cs.length;i++){
						if(cs[i]<'A' || cs[i]>'Z'){
							clist.add(cs[i]);
						}
					}
					Character [] carrays = clist.toArray(new Character [clist.size()]);
					Arrays.sort(carrays);
					for(int i=0;i<carrays.length;i++){
						modkey += carrays[i];
					}

					while(m.find()){
						modset.add(m.group());
					}
				}else{
					mod = row[3].replace("DiMethD2", "Dimeth");
					mod = mod.replace("DiMeth", "Dimeth");
					modkey = mod;
					modset.add(modkey);
				}
*/
/*				
				if(m.find()){
					keyMap.put(m.group(), m.group());
					while(m.find()){
						keyMap.put(m.group(), m.group());
					}
				}else{
					mod = row[3].replace("DiMethD2", "Dimeth");
					mod = mod.replace("DiMeth", "Dimeth");
					modkey = mod;
				}
*/				
				String scan = row[9];
				double ratio = Double.parseDouble(row[4]);
				
				HashSet <ProteinReference> refset = new HashSet <ProteinReference> ();
				ProteinReference pr = new ProteinReference(ref, false);
				refset.add(pr);
				
				String key = seq+"$"+modkey;
				
				System.out.println(key);
				
				QPeptide pep = new QPeptide(seq, refset, scan, modScore, modInfo, ratio, key);
//				HashMap <ProteinReference, int []> pepLocMap = new HashMap <ProteinReference, int []> ();
//				pepLocMap.put(pr, new int []{0,0});
//				pep.setPepLocMap(pepLocMap);

				if(peplistMap.containsKey(key)){
					peplistMap.get(key).add(pep);
				}else{
					ArrayList <QPeptide> plist = new ArrayList <QPeptide> ();
					plist.add(pep);
					peplistMap.put(key, plist);
				}
			}				
		}
		Iterator <String> it = peplistMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList <QPeptide> peplist = peplistMap.get(key);
			QPeptide p = peplist.get(0);
			double ratio = 0;
			for(int i=0;i<peplist.size();i++){
				QPeptide pi = peplist.get(i);
				ratio += pi.getRatio();
				p.addMod(pi.getModScore(), pi.getModInfo());
			}

			p.setRatio(ratio/peplist.size());
			pepMap.put(key, p);
		}
		
/*		
		Iterator <String> mapit = pepMap.keySet().iterator();
		
		try {
			PrintWriter pw = new PrintWriter("F:\\data\\�½��ļ���\\quantitation-results\\run1.txt");
			while(mapit.hasNext()){
				String key = mapit.next();
				QPeptide pep = pepMap.get(key);
				
				String seq = pep.getSequence();
				double ratio = pep.getRatio();
				HashMap <String, Double> scoreMap = pep.getModScore();
				HashMap <String, String> modMap = pep.getModInfo();
				
				if(scoreMap.size()==0){
					pw.write(seq+"\t"+ratio+"\t"+"\n");
					continue;
				}
				
				Iterator <String> scoreIt = scoreMap.keySet().iterator();
				while(scoreIt.hasNext()){
					String sk = scoreIt.next();
					pw.write(seq+"\t"+ratio+"\t"+modMap.get(sk)+" "+scoreMap.get(sk)
							+"\t"+"\n");
					break;
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		System.out.println(pepMap.size()+"\t"+peplistMap.size());
		return pepMap;
	}
	
	/**
	 * mod information without score and site, such as 1Dimeth_N 1DiMeth_K
	 * @return
	 */
	public HashMap <String, QPeptide> getInfo2(){
		reader.skip(2);
		
//		Pattern pattern = Pattern.compile("\\s[\\D]+\\s");
//		Pattern pattern2 = Pattern.compile(".*?\\s*,");
		HashMap <String, ArrayList<QPeptide>> peplistMap = new HashMap <String, ArrayList<QPeptide>> ();
		HashMap <String, QPeptide> pepMap = new HashMap <String, QPeptide> ();
		String [] row;
		while((row = reader.readLine())!=null){

			if(row.length == 12){
				
//				if(row[0].trim().length()>0)
//					continue;
				
				String ref = row[1];
				String seq = row[2];
				String mod = row[3];
				String modkey = mod;
//				Matcher m = pattern.matcher(mod);
//				Matcher m2 = pattern2.matcher(mod);
//				System.out.println(seq);
/*				
				HashMap <String, Double> modScore = new HashMap <String, Double>();
				HashMap <String, String> modInfo = new HashMap <String, String>();
				String [] ss0 = mod.split(",");
				if(ss0.length>0){
					for(int i=0;i<ss0.length;i++){
						
						String [] ss1 = ss0[i].split("\\s");
						if(ss1.length>=3){
							
							System.out.println(ss0[i]);
							
							double score = Double.parseDouble(ss1[0]);
							String mseq = ss1[1];
							
							String modchar = "";
							char [] cs = mseq.toCharArray();
							ArrayList <Character> clist = new ArrayList <Character>();
							for(int j=0;j<cs.length;j++){
								if(cs[j]<'A' || cs[j]>'Z'){
									clist.add(cs[j]);
								}
							}
							Character [] carrays = clist.toArray(new Character [clist.size()]);
							Arrays.sort(carrays);
							for(int k=0;k<carrays.length;k++){
								modchar += carrays[k];
							}

							modScore.put(mseq, score);
							String by = "";
							for(int j=1;j<ss1.length;j++){
								by += ss1[j];
								by += " ";
							}
							modInfo.put(mseq, by);
							
							modkey = modchar;
						}
					}
				}
				
				
				HashSet <String> modset = new HashSet <String>();
				if(m.find() && m2.find()){
					String group = m.group();
					modset.add(group);
					char [] cs = group.toCharArray();
					ArrayList <Character> clist = new ArrayList <Character>();
					for(int i=0;i<cs.length;i++){
						if(cs[i]<'A' || cs[i]>'Z'){
							clist.add(cs[i]);
						}
					}
					Character [] carrays = clist.toArray(new Character [clist.size()]);
					Arrays.sort(carrays);
					for(int i=0;i<carrays.length;i++){
						modkey += carrays[i];
					}

					while(m.find()){
						modset.add(m.group());
					}
				}else{
					mod = row[3].replace("DiMethD2", "Dimeth");
					mod = mod.replace("DiMeth", "Dimeth");
					modkey = mod;
					modset.add(modkey);
				}
				
				if(m.find()){
					keyMap.put(m.group(), m.group());
					while(m.find()){
						keyMap.put(m.group(), m.group());
					}
				}else{
					mod = row[3].replace("DiMethD2", "Dimeth");
					mod = mod.replace("DiMeth", "Dimeth");
					modkey = mod;
				}
*/				
				String scan = row[9];
				double ratio = Double.parseDouble(row[4]);
				
				HashSet <ProteinReference> refset = new HashSet <ProteinReference> ();
				ProteinReference pr = new ProteinReference(ref, false);
				refset.add(pr);

				String key = seq+"$"+modkey;
				
//				System.out.println(key);
				
				QPeptide pep = new QPeptide(seq, refset, scan, ratio, key);
//				HashMap <ProteinReference, int []> pepLocMap = new HashMap <ProteinReference, int []> ();
//				pepLocMap.put(pr, new int []{0,0});
//				pep.setPepLocMap(pepLocMap);

				if(peplistMap.containsKey(key)){
					peplistMap.get(key).add(pep);
				}else{
					ArrayList <QPeptide> plist = new ArrayList <QPeptide> ();
					plist.add(pep);
					peplistMap.put(key, plist);
				}
			}				
		}
		Iterator <String> it = peplistMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList <QPeptide> peplist = peplistMap.get(key);
			QPeptide p = peplist.get(0);
			double ratio = 0;
			for(int i=0;i<peplist.size();i++){
				QPeptide pi = peplist.get(i);
				ratio += pi.getRatio();
//				p.addMod(pi.getModScore(), pi.getModInfo());
			}
			p.setRatio(ratio/peplist.size());
			pepMap.put(key, p);
		}
		
/*		
		Iterator <String> mapit = pepMap.keySet().iterator();
		
		try {
			PrintWriter pw = new PrintWriter("F:\\data\\�½��ļ���\\quantitation-results\\run1.txt");
			while(mapit.hasNext()){
				String key = mapit.next();
				QPeptide pep = pepMap.get(key);
				
				String seq = pep.getSequence();
				double ratio = pep.getRatio();
				HashMap <String, Double> scoreMap = pep.getModScore();
				HashMap <String, String> modMap = pep.getModInfo();
				
				if(scoreMap.size()==0){
					pw.write(seq+"\t"+ratio+"\t"+"\n");
					continue;
				}
				
				Iterator <String> scoreIt = scoreMap.keySet().iterator();
				while(scoreIt.hasNext()){
					String sk = scoreIt.next();
					pw.write(seq+"\t"+ratio+"\t"+modMap.get(sk)+" "+scoreMap.get(sk)
							+"\t"+"\n");
					break;
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		return pepMap;
	}
	
	/**
	 * If one peptide has two possible site, the first will be reserved.
	 * @return
	 */
	public HashMap <String, QPeptide> getInfo3(){
		
		reader.skip(2);
		
		HashMap <String, ArrayList<QPeptide>> peplistMap = new HashMap <String, ArrayList<QPeptide>> ();
		HashMap <String, QPeptide> pepMap = new HashMap <String, QPeptide> ();
		String [] row;
		while((row = reader.readLine())!=null){

			if(row.length == 12){
				String ref = row[1];
				String seq = row[2];
				String mod = row[3];
				String modkey = "";

				HashMap <String, Double> modScore = new HashMap <String, Double>();
				HashMap <String, String> modInfo = new HashMap <String, String>();
				String [] ss0 = mod.split(",");
				if(ss0.length>0){
					for(int i=0;i<1;i++){
						
						String [] ss1 = ss0[i].split("\\s");
						if(ss1.length>=3){

							double score = Double.parseDouble(ss1[0]);
							String mseq = ss1[1];
							modScore.put(mseq, score);
							String by = "";
							for(int j=1;j<ss1.length;j++){
								by += ss1[j];
								by += " ";
							}
							modInfo.put(mseq, by);
							
							modkey = mseq;
						}
					}
				}
				String scan = row[9];
				double ratio = Double.parseDouble(row[4]);
				
				HashSet <ProteinReference> refset = new HashSet <ProteinReference> ();
				ProteinReference pr = new ProteinReference(ref, false);
				refset.add(pr);

				String key = seq+"$"+modkey;
				
				System.out.println(key);
				
				QPeptide pep = new QPeptide(seq, refset, scan, modScore, modInfo, ratio, key);
//				HashMap <ProteinReference, int []> pepLocMap = new HashMap <ProteinReference, int []> ();
//				pepLocMap.put(pr, new int []{0,0});
//				pep.setPepLocMap(pepLocMap);

				if(peplistMap.containsKey(key)){
					peplistMap.get(key).add(pep);
				}else{
					ArrayList <QPeptide> plist = new ArrayList <QPeptide> ();
					plist.add(pep);
					peplistMap.put(key, plist);
				}
			}				
		}
		Iterator <String> it = peplistMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList <QPeptide> peplist = peplistMap.get(key);
			QPeptide p = peplist.get(0);
			double ratio = 0;
			for(int i=0;i<peplist.size();i++){
				QPeptide pi = peplist.get(i);
				ratio += pi.getRatio();
				p.addMod(pi.getModScore(), pi.getModInfo());
			}
			p.setRatio(ratio/peplist.size());
			pepMap.put(key, p);
		}
		
		System.out.println(pepMap.size());
		
		return pepMap;
	}
	
	/**
	 * The mod site select to the previously identified priority
	 * @param file
	 * @return
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public HashMap <String, QPeptide> getInfo4(String file) throws IOException, JXLException{
		
		HashMap <String, String> preModMap = new HashMap <String, String>();
		ExcelReader exReader = new ExcelReader(file, 0);
		String [] exline = exReader.readLine();
		while((exline=exReader.readLine())!=null){
			preModMap.put(exline[0], exline[2]);
		}
		
		reader.skip(2);
		
//		Pattern pattern = Pattern.compile("\\s[\\D]+\\s");
//		Pattern pattern2 = Pattern.compile(".*?\\s*,");
		HashMap <String, ArrayList<QPeptide>> peplistMap = new HashMap <String, ArrayList<QPeptide>> ();
		HashMap <String, QPeptide> pepMap = new HashMap <String, QPeptide> ();
		String [] row;
		while((row = reader.readLine())!=null){

			if(row.length == 12){
				String ref = row[1];
				String seq = row[2];
				String mod = row[3];
				String modkey = "";
//				Matcher m = pattern.matcher(mod);
//				Matcher m2 = pattern2.matcher(mod);
//				System.out.println(seq);
				String ex = preModMap.get(seq);

				HashMap <String, Double> modScore = new HashMap <String, Double>();
				HashMap <String, String> modInfo = new HashMap <String, String>();
				String [] ss0 = mod.split(",");
				boolean re1 = true;
				
				if(ss0.length>0){
					
					if(ex!=null){
						
						for(int i=0;i<ss0.length;i++){
							
							String [] ss1 = ss0[i].split("\\s");
							if(ss1.length>=3){

								double score = Double.parseDouble(ss1[0]);
								String mseq = ss1[1];

								String by = "";
								for(int j=1;j<ss1.length;j++){
									by += ss1[j];
									by += " ";
								}
								
								if(ex.equals(by.trim())){
									
									
									modScore.put(mseq, score);
//									modScore.put(mseq, Double.MAX_VALUE);
									modInfo.put(mseq, by);
									modkey = mseq;
									re1 = false;
									
									System.out.println(ex+"\t"+re1);
								}
							}
						}
						
					}

					if(re1){
						
						for(int i=0;i<1;i++){
							
							String [] ss1 = ss0[i].split("\\s");
							if(ss1.length>=3){

								double score = Double.parseDouble(ss1[0]);
								String mseq = ss1[1];
								modScore.put(mseq, score);
								String by = "";
								for(int j=1;j<ss1.length;j++){
									by += ss1[j];
									by += " ";
								}
								modInfo.put(mseq, by);
								modkey = mseq;
							}
						}
					}
				}
				
				String scan = row[9];
				double ratio = Double.parseDouble(row[4]);
				
				HashSet <ProteinReference> refset = new HashSet <ProteinReference> ();
				ProteinReference pr = new ProteinReference(ref, false);
				refset.add(pr);
//				System.out.println(modkey);
				String key = seq+"$"+modkey;
				
//				System.out.println(key);
				
				QPeptide pep = new QPeptide(seq, refset, scan, modScore, modInfo, ratio, key);
//				HashMap <ProteinReference, int []> pepLocMap = new HashMap <ProteinReference, int []> ();
//				pepLocMap.put(pr, new int []{0,0});
//				pep.setPepLocMap(pepLocMap);

				if(peplistMap.containsKey(key)){
					peplistMap.get(key).add(pep);
				}else{
					ArrayList <QPeptide> plist = new ArrayList <QPeptide> ();
					plist.add(pep);
					peplistMap.put(key, plist);
				}
			}				
		}
		Iterator <String> it = peplistMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList <QPeptide> peplist = peplistMap.get(key);
			QPeptide p = peplist.get(0);
			double ratio = 0;
			for(int i=0;i<peplist.size();i++){
				QPeptide pi = peplist.get(i);
				ratio += pi.getRatio();
				p.addMod(pi.getModScore(), pi.getModInfo());
			}
			p.setRatio(ratio/peplist.size());
			pepMap.put(key, p);
		}
		
		System.out.println(pepMap.size());

		return pepMap;

	}
	
	public HashMap <String, QPeptide> getInfo5(String file) throws IOException, JXLException{
		
		HashMap <String, String> preModMap = new HashMap <String, String>();
		ExcelReader exReader = new ExcelReader(file, 1);
		String [] exline = exReader.readLine();
		while((exline=exReader.readLine())!=null){

			String modpep = exline[1];
			if(modpep.trim().length()>0){
				
				int beg = exline[1].indexOf(" ");
				modpep = modpep.substring(beg+1);
				preModMap.put(exline[0], modpep);
				
			}else{
				preModMap.put(exline[0], modpep);
			}
		}
		
		reader.skip(2);
		
//		Pattern pattern = Pattern.compile("\\s[\\D]+\\s");
//		Pattern pattern2 = Pattern.compile(".*?\\s*,");
		HashMap <String, ArrayList<QPeptide>> peplistMap = new HashMap <String, ArrayList<QPeptide>> ();
		HashMap <String, QPeptide> pepMap = new HashMap <String, QPeptide> ();
		String [] row;
		while((row = reader.readLine())!=null){

			if(row.length == 12){
				String ref = row[1];
				String seq = row[2];
				String mod = row[3];
				String modkey = "";
//				Matcher m = pattern.matcher(mod);
//				Matcher m2 = pattern2.matcher(mod);
//				System.out.println(seq);
				String ex = preModMap.get(seq);

				HashMap <String, Double> modScore = new HashMap <String, Double>();
				HashMap <String, String> modInfo = new HashMap <String, String>();
				String [] ss0 = mod.split(",");
				boolean re1 = true;
				
				if(ss0.length>0){
					
					if(ex!=null){
						
						for(int i=0;i<ss0.length;i++){
							
							String [] ss1 = ss0[i].split("\\s");
							if(ss1.length>=3){

								double score = Double.parseDouble(ss1[0]);
								String mseq = ss1[1];

								String by = "";
								for(int j=1;j<ss1.length;j++){
									by += ss1[j];
									by += " ";
								}
								
								if(ex.equals(by.trim())){
									
									
									modScore.put(mseq, score);
//									modScore.put(mseq, Double.MAX_VALUE);
									modInfo.put(mseq, by);
									modkey = mseq;
									re1 = false;
									
									System.out.println(ex+"\t"+re1);
								}
							}
						}
						
					}

					if(re1){
						
						for(int i=0;i<1;i++){
							
							String [] ss1 = ss0[i].split("\\s");
							if(ss1.length>=3){

								double score = Double.parseDouble(ss1[0]);
								String mseq = ss1[1];
								modScore.put(mseq, score);
								String by = "";
								for(int j=1;j<ss1.length;j++){
									by += ss1[j];
									by += " ";
								}
								modInfo.put(mseq, by);
								modkey = mseq;
							}
						}
					}
				}
				
				String scan = row[9];
				double ratio = Double.parseDouble(row[4]);
				
				HashSet <ProteinReference> refset = new HashSet <ProteinReference> ();
				ProteinReference pr = new ProteinReference(ref, false);
				refset.add(pr);
//				System.out.println(modkey);
				String key = seq+"$"+modkey;
				
//				System.out.println(key);
				
				QPeptide pep = new QPeptide(seq, refset, scan, modScore, modInfo, ratio, key);
//				HashMap <ProteinReference, int []> pepLocMap = new HashMap <ProteinReference, int []> ();
//				pepLocMap.put(pr, new int []{0,0});
//				pep.setPepLocMap(pepLocMap);

				if(peplistMap.containsKey(key)){
					peplistMap.get(key).add(pep);
				}else{
					ArrayList <QPeptide> plist = new ArrayList <QPeptide> ();
					plist.add(pep);
					peplistMap.put(key, plist);
				}
			}				
		}
		Iterator <String> it = peplistMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList <QPeptide> peplist = peplistMap.get(key);
			QPeptide p = peplist.get(0);
			double ratio = 0;
			for(int i=0;i<peplist.size();i++){
				QPeptide pi = peplist.get(i);
				ratio += pi.getRatio();
				p.addMod(pi.getModScore(), pi.getModInfo());
			}
			p.setRatio(ratio/peplist.size());
			pepMap.put(key, p);
		}
		
		System.out.println(pepMap.size());

		return pepMap;

	}

	public void write(String file, HashMap <String, QPeptide> pepMap) throws IOException, RowsExceededException, WriteException{
		ExcelWriter writer = new ExcelWriter(file);
		ExcelFormat format = new ExcelFormat(false, 0);
		StringBuilder sb = new StringBuilder();
		sb.append("Sequence\t").append("Ratio\t").append("Mod\t").append("Reference\n");
		writer.addTitle(sb.toString(), 0, ExcelFormat.normalFormat);
		Iterator <String> mapit = pepMap.keySet().iterator();
		while(mapit.hasNext()){
			String key = mapit.next();
			QPeptide pep = pepMap.get(key);
//			String ref = pep.getProteinReferenceString();
			
			String seq = pep.getSequence();
			double ratio = pep.getRatio();
			HashMap <String, Double> scoreMap = pep.getModScore();
			HashMap <String, String> modMap = pep.getModInfo();

			if(scoreMap.size()==0){
				StringBuilder sb1 = new StringBuilder();
				sb1.append(seq).append("\t").append(ratio).append("\t\t").append("").append("\n");
				writer.addContent(sb1.toString(), 0, format);
				continue;
			}

			Iterator <String> scoreIt = scoreMap.keySet().iterator();
			while(scoreIt.hasNext()){
				String sk = scoreIt.next();
				StringBuilder sb1 = new StringBuilder();
				sb1.append(seq).append("\t").append(ratio).append("\t").append(modMap.get(sk)).append("\t").append("").append("\n");
				writer.addContent(sb1.toString(), 0, format);
//				break;
			}
		}
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 * @throws FastaDataBaseException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 */
	public static void main(String[] args) throws IOException, JXLException, FastaDataBaseException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {
		// TODO Auto-generated method stub
/*
		Pattern pattern = Pattern.compile("\\s[\\D]+\\s");
		Pattern pattern2 = Pattern.compile(".*?\\s*,");
		String s = "11.68 VRPApSSAASVYAGAGGSGSR b5/y16 ,11.68 VRPASSAApSVYAGAGGSGSR b9/y12 ,";
		Matcher m = pattern.matcher(s);
		Matcher m2 = pattern2.matcher(s);

		while(m.find()){
			System.out.println(m.group());
		}

		System.out.println(s.split(",").length);
*/
		
//		ExcelReader reader = new ExcelReader("F:\\data\\�½��ļ���\\quantitation-results\\run1.xls");
//		String fastaM = "E:\\DataBase\\IPI_mouse\\current\\Final_IPI_mouse_3.26.fasta";
		String fastaH352 = "E:\\DataBase\\ipi.HUMAN.v3.52\\Final_ipi_human352_0.fasta";
//		IFastaAccesser accesser = new FastaAccesser(fasta, new DefaultDecoyRefJudger());
		
		String file = "H:\\final_results\\NSKD1.xls";
		StatQReader reader = new StatQReader(file,
				fastaH352);
		String out = "H:\\quatification_data_standard\\result\\1_4_1\\M_H_fulltable_pep.xls";
		
		String ex = "F:\\�½��ļ���\\U\\Cancer_Normal_1_pep.xls";
//		HashMap <String, QPeptide> pepMap = reader.getInfo4(ex);
		
		HashMap <String, QPeptide> pepMap = reader.getInfo();
//		reader.write(out, pepMap);
		
	}

}
