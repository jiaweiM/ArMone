/* 
 ******************************************************************************
 * File: MutilLabelPairXMLReader.java * * * Created on 2012-6-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;

/**
 * @author ck
 *
 * @version 2012-6-14, 21:02:40
 */
public class MutilLabelFeaturesXMLReader extends LabelFeaturesXMLReader {

	/**
	 * @param file
	 * @throws DocumentException
	 */
	public MutilLabelFeaturesXMLReader(String file) throws DocumentException {
		super(file);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param file
	 * @throws DocumentException
	 */
	public MutilLabelFeaturesXMLReader(File file) throws DocumentException {
		super(file);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLReader#getProfileData()
	 */
	@Override
	protected void getProfileData() {
		// TODO Auto-generated method stub
		
		ArrayList <String> ratioNameList = new ArrayList<String>();
		ArrayList <String> allRatioNameList = new ArrayList<String>();

		String sType = root.attributeValue("Label_Type");
		this.type = LabelType.getLabelType(sType);
		
		if(type==LabelType.FiveLabel){
			for(int i=1;i<=5;i++){
				for(int j=i+1;j<=5;j++){
					String s1 = j+"/"+i;
					String s2 = i+"/"+j;
					ratioNameList.add(s1);
					allRatioNameList.add(s1);
					allRatioNameList.add(s2);
				}
			}
		}else if(type==LabelType.SixLabel){
			for(int i=1;i<=6;i++){
				for(int j=i+1;j<=6;j++){
					String s1 = j+"/"+i;
					String s2 = i+"/"+j;
					ratioNameList.add(s1);
					allRatioNameList.add(s1);
					allRatioNameList.add(s2);
				}
			}
		}
		
		String sGrad = root.attributeValue("Gradient");
		this.gradient = sGrad.equals("true");
		
		this.ratioNames = ratioNameList.toArray(new String[ratioNameList.size()]);
		this.allRatioNames = allRatioNameList.toArray(new String[ratioNameList.size()]);
//		this.totalFeaturesMedian = new double [allRatioNames.length];

		this.setMods();
	}
/*
	private static void compare2(String s1, String s2) throws DocumentException{
		
		HashSet <Integer> total = new HashSet <Integer>();
		HashSet <Integer> set1 = new HashSet <Integer>();
		HashSet <Integer> set2 = new HashSet <Integer>();
		
		MutilLabelPairXMLReader reader1 = new MutilLabelPairXMLReader(s1);
		reader1.readAllPairs();
		
		MutilLabelPairXMLReader reader2 = new MutilLabelPairXMLReader(s2);
		reader2.readAllPairs();
		
		PeptidePair [] pairs1 = reader1.getAllSelectedPairs();
		for(int i=0;i<pairs1.length;i++){

			int count = pairs1[i].getFindFeasNum();
			if(count==6){
				IPeptide pep = pairs1[i].getPeptide();
				total.add(pep.getScanNumBeg());
				set1.add(pep.getScanNumBeg());
			}
		}
		reader1.close();
		
		PeptidePair [] pairs2 = reader2.getAllSelectedPairs();
		for(int i=0;i<pairs2.length;i++){

			int count = pairs2[i].getFindFeasNum();
			if(count==6){
				IPeptide pep = pairs2[i].getPeptide();
				total.add(pep.getScanNumBeg());
				set2.add(pep.getScanNumBeg());
			}
		}
		reader2.close();
//System.out.println(total.size()+"\t"+set1.size()+"\t"+set2.size());		
		Iterator <Integer> it = total.iterator();
		while(it.hasNext()){
			Integer scannum = it.next();
			StringBuilder sb = new StringBuilder();
			if(set1.contains(scannum)){
				sb.append(scannum);
			}
			sb.append("\t");
			if(set2.contains(scannum)){
				sb.append(scannum);
			}
			sb.append("\t");
			System.out.println(sb);
		}
	}
	
	private static void compareSeq(String s1, String s2) throws DocumentException{
		
		HashSet <String> total = new HashSet <String>();
		HashSet <String> set1 = new HashSet <String>();
		HashSet <String> set2 = new HashSet <String>();
		
		MutilLabelPairXMLReader reader1 = new MutilLabelPairXMLReader(s1);
		reader1.readAllPairs();
		
		MutilLabelPairXMLReader reader2 = new MutilLabelPairXMLReader(s2);
		reader2.readAllPairs();
		
		PeptidePair [] pairs1 = reader1.getAllSelectedPairs();
		for(int i=0;i<pairs1.length;i++){

			int count = pairs1[i].getFindFeasNum();
			if(count==6){
				IPeptide pep = pairs1[i].getPeptide();
				String seq = pep.getSequence();
				total.add(seq.substring(2, seq.length()-2));
				set1.add(seq.substring(2, seq.length()-2));
			}
		}
		reader1.close();
		
		PeptidePair [] pairs2 = reader2.getAllSelectedPairs();
		for(int i=0;i<pairs2.length;i++){

			int count = pairs2[i].getFindFeasNum();
			if(count==6){
				IPeptide pep = pairs2[i].getPeptide();
				String seq = pep.getSequence();
				total.add(seq.substring(2, seq.length()-2));
				set2.add(seq.substring(2, seq.length()-2));
			}
		}
		reader2.close();
//System.out.println(total.size()+"\t"+set1.size()+"\t"+set2.size());		
		Iterator <String> it = total.iterator();
		while(it.hasNext()){
			String scannum = it.next();
			StringBuilder sb = new StringBuilder();
			if(set1.contains(scannum)){
				sb.append(scannum);
			}
			sb.append("\t");
			if(set2.contains(scannum)){
				sb.append(scannum);
			}
			sb.append("\t");
			System.out.println(sb);
		}
	}
	
	private static void test(String [] ppls, String pxml) throws FileDamageException, IOException, DocumentException{
		
		HashMap <Integer, String> [] pepMap = new HashMap [ppls.length];
		PeptideListReader [] pr = new PeptideListReader [ppls.length];
		for(int i=0;i<pr.length;i++){
			pr[i] = new PeptideListReader(ppls[i]);
			pepMap[i] = new HashMap <Integer, String>();
			IPeptide pep = null;
			while((pep=pr[i].getPeptide())!=null){
				pepMap[i].put(pep.getScanNumBeg(), PeptideUtil.getUniqueSequence(pep.getSequence()));
			}
			pr[i].close();
		}
		
		MutilLabelPairXMLReader reader = new MutilLabelPairXMLReader(pxml);
		reader.readAllPairs();
		
		PeptidePair [] pairs = reader.getAllSelectedPairs();
		for(int i=0;i<pairs.length;i++){
			if(pairs[i].getFindFeasNum()==6){
				IPeptide pep = pairs[i].getPeptide();
				int scannum = pep.getScanNumBeg();
				String seq = pep.getSequence();
				int type = 0;
				boolean same = false;
				String qs = "";
				for(int j=0;j<pepMap.length;j++){
					if(pepMap[j].containsKey(scannum)){
						type = j+1;
						qs = pepMap[j].get(scannum);
						if(PeptideUtil.getUniqueSequence(seq).equals(qs)){
							same = true;
						}
					}
				}
				System.out.println(scannum+"\t"+type+"\t"+PeptideUtil.getUniqueSequence(seq)+"\t"+qs+"\t"+same);
			}
		}
		reader.close();
	}
*/	
	private void write(String output){
		
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
/*		MutilLabelPairXMLReader.compareSeq("H:\\WFJ_mutiple_label\\2D_trypsin\\20120531Mix1_150mM_dat.pxml", 
				"H:\\WFJ_mutiple_label\\2D_trypsin\\150\\new\\6com_20120531Mix1_150mM_dat.pxml");
		String [] ppls = new String [] {"H:\\WFJ_mutiple_label\\2D\\150\\dat\\1_F002478.dat.ppl", 
				"H:\\WFJ_mutiple_label\\2D\\150\\dat\\2_F002479.dat.ppl", 
				"H:\\WFJ_mutiple_label\\2D\\150\\dat\\3_F002480.dat.ppl", 
				"H:\\WFJ_mutiple_label\\2D\\150\\dat\\4_F002481.dat.ppl", 
				"H:\\WFJ_mutiple_label\\2D\\150\\dat\\5_F002482.dat.ppl", 
				"H:\\WFJ_mutiple_label\\2D\\150\\dat\\6_F002483.dat.ppl", };
		try {
			MutilLabelPairXMLReader.test(ppls, "H:\\WFJ_mutiple_label\\2D\\20120531Mix1_150mM_csv.pxml");
		} catch (FileDamageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
//		MutilLabelPairXMLReader reader = new MutilLabelPairXMLReader("H:\\WFJ_mutiple_label\\2D_trypsin\\150\\new" +
//			"\\20120531Mix1_150mM_dat.pxml");
		
		MutilLabelFeaturesXMLReader reader = null;
		try {
			reader = new MutilLabelFeaturesXMLReader("H:\\WFJ_mutiple_label\\turnover\\0_3_6\\percolator\\new" +
					"\\20120805PT0_3_6h_700mM.pxml");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		reader.readAllPairs();
		
		String [] names = reader.ratioNames;
		for(int i=0;i<names.length;i++){
//			System.out.print(names[i]+"\t");
		}
//		System.out.println("");
		
		PeptidePair [] feas = reader.getAllSelectedPairs();
		System.out.println("\t"+feas.length);
		int c6 = 0;
		for(int i=0;i<feas.length;i++){
/*			double [] ratios = pairs[i].getSelectRatio();
			for(int j=0;j<ratios.length;j++){
				System.out.print(ratios[j]+"\t");
			}
			System.out.println("");

			double [] intens = pairs[i].getIntens();
			int count = 0;
			System.out.print("\t");
			for(int j=0;j<intens.length;j++){
				if(intens[j]>0)
					count++;
				System.out.print(intens[j]+"\t");
			}
*/			

//			System.out.println(pep.getScanNumBeg()+"\t"+pep.getSequence());
			
		}
		System.out.println(c6);
		
	}
	
}
