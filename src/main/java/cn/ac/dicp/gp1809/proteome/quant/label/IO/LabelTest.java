/* 
 ******************************************************************************
 * File: LabelTest.java * * * Created on 2014��5��28��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.JXLException;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2014��5��28��, ����2:06:46
 */
public class LabelTest {

	private static void xlsPepCompare(String s1, String s2) throws IOException, JXLException{
		
		HashMap<String, Double> m1 = new HashMap<String, Double>();
		ExcelReader r1 = new ExcelReader(s1, 1);
		String[] l1 = r1.readLine();
		int length1 = l1.length;
		while((l1=r1.readLine())!=null && l1.length==length1){
			m1.put(l1[0], Double.parseDouble(l1[1]));
		}
		r1.close();
		
		HashMap<String, Double> m2 = new HashMap<String, Double>();
		ExcelReader r2 = new ExcelReader(s2, 1);
		String[] l2 = r2.readLine();
		int length2 = l2.length;
		while((l2=r2.readLine())!=null && l2.length==length2){
			m2.put(l2[0], Double.parseDouble(l2[1]));
		}
		r2.close();
		
		HashSet<String> total = new HashSet<String>();
		total.addAll(m1.keySet());
		total.addAll(m2.keySet());
		System.out.println(m1.size()+"\t"+m2.size()+"\t"+(m1.size()+m2.size()-total.size()));
		
		Iterator<String> it = total.iterator();
		while(it.hasNext()){
			String key = it.next();
			if(m1.containsKey(key)){
				if(!m2.containsKey(key)){
					System.out.println(key+"\t");
				}
			}else{
				System.out.println("\t"+key);
			}
		}
	}
	
	/**
	 * glycopeptide
	 * @param maxquant
	 * @param result
	 * @throws IOException
	 * @throws JXLException
	 */
	private static void compareMaxquant(String maxquant, String result) throws IOException, JXLException{
		
		HashMap<String, Double> maxmap = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new FileReader(maxquant));
		String line = br.readLine();
		
		int sequenceid = -1;
		int ratioid = -1;
		String[] title = line.split("\t");
		for(int i=0;i<title.length;i++){
			if(title[i].equals("Modified Sequence")){
				sequenceid = i;
			}
			if(title[i].equals("Ratio H/L Normalized")){
				ratioid = i;
			}
		}

		while((line=br.readLine())!=null){
			String[] content = line.split("\t");
			if(content[ratioid].trim().length()==0)
				continue;
			
			String sequence = content[sequenceid];
			double ratio = Double.parseDouble(content[ratioid]);
			String[] cs = sequence.split("[_\\(\\)]");
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<cs.length;i++){
				if(cs[i].equals("de")){
					sb.append("*");
				}else if(cs[i].equals("ox")){
					sb.append("#");
				}else{
					sb.append(cs[i]);
				}
			}
			maxmap.put(sb.toString(), ratio);
		}
		br.close();
		
		System.out.println(maxmap.size());
		
		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		HashMap<String, Double> map = new HashMap<String, Double>();
		ExcelReader reader = new ExcelReader(result, 1);
		String[] cs = reader.readLine();
		int length = cs.length;
		while((cs=reader.readLine())!=null && cs.length==length){
			String sequence = cs[0].substring(2, cs[0].length()-2);
			Matcher matcher = N_GLYCO.matcher(sequence);
			if(matcher.find()){
				map.put(sequence, Double.parseDouble(cs[1]));
			}
		}
		reader.close();
		
		System.out.println(map.size());
		
		HashSet<String> totalset = new HashSet<String>();
		totalset.addAll(maxmap.keySet());
		totalset.addAll(map.keySet());
		
		System.out.println(maxmap.size()+map.size()-totalset.size());
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

//		LabelTest.xlsPepCompare("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\20130805_4p_di-labeling_CID_quantification_1_1.xls", 
//				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\old\\20130805_4p_di-labeling_CID_quantification_1_1-1.xls");
		
		LabelTest.compareMaxquant("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\iden\\MaxQuant\\Deamidation (N)Sites_normal-1.txt", 
				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\iden\\20130805_serum_di-labeling_Normal_CID_quantification.xls");
	}

}
