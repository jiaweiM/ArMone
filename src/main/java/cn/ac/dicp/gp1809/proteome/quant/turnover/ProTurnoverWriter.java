/* 
 ******************************************************************************
 * File: ProTurnoverWriter.java * * * Created on 2011-11-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.repeatStat.PepStatInfo;
import cn.ac.dicp.gp1809.proteome.quant.repeatStat.ProStatInfo;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.LFreePeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreePairXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2011-11-21, 09:54:27
 */
public class ProTurnoverWriter {
	
	private File [][] files;
	private String output;
	private LabelType type;
	
	private String [] ratioNames;
	private ExcelWriter writer;
	
	public ProTurnoverWriter(File [][] files, String output, LabelType type){
		this.files = files;
		this.output = output;
		this.type = type;
	}
	
	public void write(boolean normal, String [] ratioNames, double [] theoryRatios, 
			double [] usedTheoryRatios, int [] outputRatio, double [] timePoints) throws Exception {
		
		this.ratioNames = ratioNames;
		this.writer = new ExcelWriter(output);
		ExcelFormat f = ExcelFormat.normalFormat;
		this.addTitle();
		
		int ratioNum = ratioNames.length;
		int fileNum = files.length;
		HashMap <String, double[]> [] refRatioMap = new HashMap [fileNum];
		HashSet <String> totalset = new HashSet <String>();
		
		for(int i=0;i<files.length;i++){
			
			ProteinNameAccesser accesser = null;
			HashMap <String, IPeptide> pepmap = new HashMap <String, IPeptide>();
			HashMap <String, double[]> [] pepRatioMaps = new HashMap [files[i].length];
			refRatioMap[i] = new HashMap <String, double[]>();

			for(int j=0;j<files[i].length;j++){
				
				pepRatioMaps[j] = new HashMap <String, double[]>();
				
//				AbstractPairXMLReader xmlreader = null;
				if(type == LabelType.LabelFree){
					
					LFreePairXMLReader xmlreader = new LFreePairXMLReader(files[i][j]);
					xmlreader.getAllMods();
					
					xmlreader.readAllPairs();
					xmlreader.setTheoryRatio(theoryRatios);
					
					if(accesser==null){
						accesser = xmlreader.getProNameAccesser();
					}else{
						accesser.appand(xmlreader.getProNameAccesser());
					}

					LFreePeptidePair [] pairs = xmlreader.getAllSelectedPairs(normal, outputRatio);
					
					for(int k=0;k<pairs.length;k++){
						
						double [] ratio = pairs[k].getSelectRatio();
						IPeptide pep = pairs[k].getPeptide();
						String seq = PeptideUtil.getSequence(pairs[k].getSequence());
						pepRatioMaps[k].put(seq, ratio);
						
						if(pepmap.containsKey(seq)){
							
							IPeptide p0 = pepmap.get(seq);
							p0.getProteinReferences().addAll(pep.getProteinReferences());
							p0.getPepLocAroundMap().putAll(pep.getPepLocAroundMap());
							
						}else{
							
							pepmap.put(seq, pep);
						}
					}
					
					xmlreader.close();
					
				}else{
					
					LabelFeaturesXMLReader xmlreader = new LabelFeaturesXMLReader(files[i][j]);
					
					xmlreader.readAllPairs();
					xmlreader.setTheoryRatio(theoryRatios);
					
					if(accesser==null){
						accesser = xmlreader.getProNameAccesser();
					}else{
						accesser.appand(xmlreader.getProNameAccesser());
					}

					PeptidePair [] pairs = xmlreader.getAllSelectedPairs(normal, outputRatio);
					
					for(int k=0;k<pairs.length;k++){
						
						double [] ratio = pairs[k].getFeatures().getSelectRatio();
						IPeptide pep = pairs[k].getPeptide();
						String seq = PeptideUtil.getSequence(pairs[k].getSequence());
						pepRatioMaps[k].put(seq, ratio);
						
						if(pepmap.containsKey(seq)){
							
							IPeptide p0 = pepmap.get(seq);
							p0.getProteinReferences().addAll(pep.getProteinReferences());
							p0.getPepLocAroundMap().putAll(pep.getPepLocAroundMap());
							
						}else{
							
							pepmap.put(seq, pep);
						}
					}
					
					xmlreader.close();
				}
			}
			
			Proteins2 pros = new Proteins2(accesser);
			int count = 0;
			Iterator <String> it = pepmap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				IPeptide pep = pepmap.get(key);
				pros.addPeptide(pep);
			}
			
			Protein [] prolist = pros.getAllProteins();
			for(int j=0;j<prolist.length;j++){

				Protein pro = prolist[j];			
				IReferenceDetail [] refs = pro.getReferences();
				String [] refName = new String [refs.length];
				for(int k=0;k<refs.length;k++){
					refName [k] = refs[k].getName();
				}
				
				IPeptide [] peps = pro.getAllPeptides();
				HashSet <String> seqSet = new HashSet <String> ();
				ArrayList <PepStatInfo> pepInfoList = new ArrayList <PepStatInfo> ();
				boolean [] have = new boolean [fileNum];
				Arrays.fill(have, false);
				
				for(int k=0;k<peps.length;k++){
					
					IPeptide p = peps[j];
					String seq = PeptideUtil.getSequence(p.getSequence());
					if(seqSet.contains(seq))
						continue;

					seqSet.add(seq);
					double [][] ratios = new double[ratioNum][fileNum];
					int num = 0;
					
					for(int l=0;l<fileNum;l++){
						if(pepRatioMaps[l].containsKey(seq)){
							double [] pairRatio = pepRatioMaps[l].get(seq);
							for(int m=0;m<pairRatio.length;m++){
								ratios[m][l] = pairRatio[m];
							}
							have[l] = true;
							num++;
						}else{
							for(int m=0;m<ratioNum;m++){
								ratios[m][l] = 0;
							}
						}
					}

					PepStatInfo pepInfo = new PepStatInfo(p.getSequence(), num, ratios);
					pepInfoList.add(pepInfo);
				}
				
				PepStatInfo [] pepInfos = pepInfoList.toArray(new PepStatInfo [pepInfoList.size()]);
				int num = 0;
				for(int k=0;k<have.length;k++){
					if(have[k])
						num++;
				}
				
				if(num!=0){
					count++;
					ProStatInfo info = new ProStatInfo(count, refName, num, pepInfos, ratioNum, fileNum);
					double [] ave = info.getAve();
					for(int k=0;k<refName.length;k++){
						refRatioMap[i].put(refName[k], ave);
						totalset.add(refName[k]);
					}
				}
			}
		}
		
		Iterator <String> it = totalset.iterator();
		while(it.hasNext()){
			
			String ref = it.next();
			
			ArrayList <Double> [] list = new ArrayList [ratioNum];
			ArrayList <Double> [] time = new ArrayList [ratioNum];
			for(int i=0;i<list.length;i++){
				list[i] = new ArrayList <Double>();
				time[i] = new ArrayList <Double>();
			}
			
			for(int i=0;i<fileNum;i++){
				if(refRatioMap[i].containsKey(ref)){
					double [] ratios = refRatioMap[i].get(ref);
					for(int j=0;j<ratios.length;j++){
						list[j].add(ratios[j]);
						time[j].add(timePoints[j]);
					}
				}
			}
			
			double [][] totalRatios = new double[ratioNum][];
			double [][] totalTimes = new double[ratioNum][];
			for(int i=0;i<totalRatios.length;i++){
				
				ArrayList <Double> rlist = list[i];
				totalRatios[i] = new double[rlist.size()];
				for(int j=0;j<rlist.size();j++){
					totalRatios[i][j] = rlist.get(j);
				}
				
				ArrayList <Double> tlist = time[i];
				totalTimes[i] = new double[tlist.size()];
				for(int j=0;j<tlist.size();j++){
					totalTimes[i][j] = tlist.get(j);
				}
			}
			
			ProTurnoverUnit unit = new ProTurnoverUnit(ref, totalRatios, totalTimes);
			this.writer.addContent(unit.toString(), 0, f);
		}
		
		writer.close();
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		sb.append("Reference\t");
		for(int i=0;i<ratioNames.length;i++){
			sb.append(ratioNames[i]+"\t");
			sb.append("K\t");
			sb.append("R2\t");
			sb.append("Ratios\t");
		}
		
		ExcelFormat ef = ExcelFormat.normalFormat;
		this.writer.addTitle(sb.toString(), 0, ef);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
