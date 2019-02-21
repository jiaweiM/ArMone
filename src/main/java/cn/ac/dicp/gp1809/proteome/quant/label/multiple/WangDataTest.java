/* 
 ******************************************************************************
 * File: WangDataTest.java * * * Created on 2013-3-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.JXLException;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-3-19, 16:40:20
 */
public class WangDataTest {
	
	public static void test1(String ppl, String pxml, int labelCount) throws FileDamageException, 
		IOException, DocumentException{
		
		HashSet <String> pepset = new HashSet <String>();
		File [] files1 = (new File(ppl)).listFiles();
		for(int i=0;i<files1.length;i++){
			String name = files1[i].getName();
			if(name.endsWith("ppl")){
				PeptideListReader reader = new PeptideListReader(files1[i]);
				IPeptide pep = null;
				while((pep=reader.getPeptide())!=null){
					String seq = pep.getSequence();
					String uniseq = PeptideUtil.getUniqueSequence(seq);
					pepset.add(uniseq);
				}
				reader.close();
			}
		}
		System.out.println("Peptide number\t"+pepset.size());
		
		HashSet <String> totalset = new HashSet <String>();
		HashSet <String> [] pairset = new HashSet [labelCount];
		for(int i=0;i<pairset.length;i++){
			pairset[i] = new HashSet <String>();
		}
		File [] files2 = (new File(pxml)).listFiles();
		for(int i=0;i<files2.length;i++){
			String name = files2[i].getName();
			if(name.endsWith("pxml")){
				LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(files2[i]);
				PeptidePair [] allPairs = reader.getAllSelectedPairs();
				for(int j=0;j<allPairs.length;j++){
					String seq = allPairs[j].getSequence();
					String uniseq = PeptideUtil.getUniqueSequence(seq);
					pairset[allPairs[j].getFeatures().getPresentFeasNum()-1].add(uniseq);
					totalset.add(uniseq);
				}
				reader.close();
			}
		}
		int total = 0;
		for(int i=0;i<labelCount;i++){
			System.out.println("Features number\t"+(i+1)+"\t"+pairset[i].size());
			total+=pairset[i].size();
		}
		System.out.println("Total\t\t"+total+"\t"+totalset.size());
	}
	
	public static void test2(String ppl, String pxml) throws FileDamageException, 
		IOException, DocumentException{
		
		HashSet <String> pepset = new HashSet <String>();
		File [] files1 = (new File(ppl)).listFiles();
		for(int i=0;i<files1.length;i++){
			String name = files1[i].getName();
			if(name.endsWith("ppl")){
				PeptideListReader reader = new PeptideListReader(files1[i]);
				IPeptide pep = null;
				while((pep=reader.getPeptide())!=null){
					String seq = pep.getSequence();
					String uniseq = PeptideUtil.getUniqueSequence(seq);
					pepset.add(uniseq);
				}
				reader.close();
			}
		}
		System.out.println("Peptide number\t"+pepset.size());
		
		HashSet <String> [] pairset = new HashSet [6];
		for(int i=0;i<pairset.length;i++){
			pairset[i] = new HashSet <String>();
		}
		
		HashMap <String, Integer> usedmap = new HashMap <String,Integer>();
		LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(pxml);
		PeptidePair [] allPairs = reader.getAllSelectedPairs();
		for(int j=0;j<allPairs.length;j++){
			String seq = allPairs[j].getSequence();
			String uniseq = PeptideUtil.getUniqueSequence(seq);
			if(usedmap.containsKey(uniseq)){
				if(allPairs[j].getFeatures().getPresentFeasNum()>usedmap.get(uniseq)){
					usedmap.put(uniseq, allPairs[j].getFeatures().getPresentFeasNum());
				}
			}else{
				usedmap.put(uniseq, allPairs[j].getFeatures().getPresentFeasNum());
			}
		}
		reader.close();
		
		Iterator <String> it = usedmap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			int num = usedmap.get(key);
			pairset[num-1].add(key);
		}
		
		int total = 0;
		for(int i=0;i<6;i++){
			System.out.println("Features number\t"+(i+1)+"\t"+pairset[i].size());
			total+=pairset[i].size();
		}
		System.out.println("Total\t\t"+total);
	}

	public static void select(String in) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			if(line[0].contains("histone")){
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<line.length;i++){
					sb.append(line[i]).append("\t");
				}
				System.out.println(sb);
			}
		}
		reader.close();
	}
	
	public static void testWrong(String pxml, String xls) throws DocumentException, IOException, JXLException{
		
		HashMap <String, Integer> usedmap = new HashMap <String,Integer>();
		LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(pxml);
		PeptidePair [] allPairs = reader.getAllSelectedPairs();
		for(int j=0;j<allPairs.length;j++){
			String seq = allPairs[j].getSequence();
//			String uniseq = PeptideUtil.getUniqueSequence(seq);
			if(usedmap.containsKey(seq)){
				if(allPairs[j].getFeatures().getPresentFeasNum()>usedmap.get(seq)){
					usedmap.put(seq, allPairs[j].getFeatures().getPresentFeasNum());
				}
			}else{
				usedmap.put(seq, allPairs[j].getFeatures().getPresentFeasNum());
			}
		}
		reader.close();
		
		ExcelReader xlsreader = new ExcelReader(xls, 1);
		String [] line = xlsreader.readLine();
		while((line=xlsreader.readLine())!=null && line.length>0){
			if(usedmap.containsKey(line[0])){
				if(Integer.parseInt(line[22])!=usedmap.get(line[0])){
					System.out.println(line[0]+"\t"+line[22]+"\t"+usedmap.get(line[0]));
				}
			}
		}
	}
	
	public static void mcpCompare(String fasta368, String mcp, String turnover) throws IOException, JXLException{
		
		HashMap <String, ProteinSequence> ipimap = new HashMap <String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta368);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			ipimap.put(ref.substring(4, 15), ps);
		}
		fr.close();
		
		HashMap <String, Double> mcpturnmap = new HashMap <String, Double>();
		Pattern pat = Pattern.compile(".*REFSEQ:(\\w+).*");
		BufferedReader mcpreader = new BufferedReader(new FileReader(mcp));
		String line = mcpreader.readLine();
		while((line=mcpreader.readLine())!=null){
			String [] cs = line.split("\t");
			if(ipimap.containsKey(cs[0])){
				
				String ref = ipimap.get(cs[0]).getReference();
				Matcher mat = pat.matcher(ref);
				if(mat.matches()){
//					System.out.println(mat.group(1));
					if(cs.length>1 && cs[1].length()>0){
						mcpturnmap.put(mat.group(1), Double.parseDouble(cs[1]));
					}
				}
				
			}else{
//				System.out.println("no this protein\t"+cs[0]);
			}
		}
		mcpreader.close();
//		System.out.println(mcpturnmap.size());
		System.out.println("ref\tmcp\tarmone\trsd");
		
		Pattern pat2 = Pattern.compile(".*ref\\|(\\w+).*");
		int common = 0;
		ExcelReader reader = new ExcelReader(turnover, 0);
		String [] exline = reader.readLine();
		while((exline=reader.readLine())!=null){
			
			Matcher mat2 = pat2.matcher(exline[0]);
			if(mat2.matches()){
//				System.out.println(mat.group(1));
				String ref = mat2.group(1);
//				System.out.println(ref);
				if(mcpturnmap.containsKey(ref)){
					common++;
					double [] halfs = new double [2];
					halfs[0] = mcpturnmap.get(ref);
					halfs[1] = Double.parseDouble(exline[18]);
					double rsd = MathTool.getRSD(halfs);
					StringBuilder sb = new StringBuilder();
					sb.append(ref).append("\t");
					sb.append(halfs[0]).append("\t");
					sb.append(halfs[1]).append("\t");
					sb.append(rsd).append("\t");
					sb.append(exline[13]).append("\t");
					System.out.println(sb);
				}
			}
		}
//		System.out.println(common);
		
		
	}
	
	public static void deIntensity(String in, String out) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, new int []{0, 1});
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		
		String [] line1 = null;
		boolean begin = false;
		int prolength = -1;
		int peplength = -1;
		while((line1=reader.readLine(0))!=null){
			if(line1.length>0){
				if(line1[0].equals("Index")){
					begin = true;
					prolength = line1.length;
					String [] newpro = new String [prolength-6];
					System.arraycopy(line1, 0, newpro, 0, newpro.length);
					writer.addTitle(newpro, 0, format);
					
					line1=reader.readLine(0);
					peplength = line1.length+1;
					String [] newpep = new String [peplength-5];
					System.arraycopy(line1, 0, newpep, 0, newpep.length-3);
					System.arraycopy(line1, 13, newpep, 7, 2);
					writer.addTitle(newpep, 0, format);
					continue;
				}
				
				if(begin){
					
					if(line1.length==prolength){
						
						String [] newpro = new String [prolength-6];
						System.arraycopy(line1, 0, newpro, 0, newpro.length);
						writer.addContent(newpro, 0, format);
						
					}else if(line1.length==peplength){
						
						String [] newpep = new String [peplength-5];
						System.arraycopy(line1, 0, newpep, 0, newpep.length-3);
						System.arraycopy(line1, 13, newpep, 7, 3);
						writer.addContent(newpep, 0, format);
						
					}else{
						writer.addContent(line1, 0, format);
					}
					
				}else{
					writer.addContent(line1, 0, format);
				}
				
			}else{
				writer.addContent(line1, 0, format);
			}
		}
		
		String [] line2 = reader.readLine(1);
		int peplength2 = line2.length;
		String [] newtitle = new String [peplength2-6];
		System.arraycopy(line2, 0, newtitle, 0, newtitle.length-2);
		System.arraycopy(line2, 12, newtitle, 6, 2);
		writer.addTitle(newtitle, 1, format);
		while((line2=reader.readLine(1))!=null){
			if(line2.length==peplength2){
				String [] newpep = new String [peplength2-6];
				System.arraycopy(line2, 0, newpep, 0, newpep.length-2);
				System.arraycopy(line2, 12, newpep, 6, 2);
				writer.addContent(newpep, 1, format);
			}else{
				writer.addContent(line2, 1, format);
			}
		}
		writer.close();
	}
	
	public static void selectPro(String oldpro, String newpro, String turnover) throws IOException, JXLException{
		
		HashSet <String> set = new HashSet <String>();
		BufferedReader reader = new BufferedReader(new FileReader(oldpro));
		String line = null;
		while((line=reader.readLine())!=null){
			set.add(line.split("\t")[0]);
		}
		reader.close();
		
		PrintWriter pw = new PrintWriter(newpro);
		ExcelReader er = new ExcelReader(turnover);
		String [] cs = er.readLine();
		while((cs=er.readLine())!=null){
			if(set.contains(cs[0])){
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<cs.length;i++){
					sb.append(cs[i]).append("\t");
				}
				pw.write(sb+"\n");
			}
		}
		er.close();
		pw.close();
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, DocumentException, JXLException {
		// TODO Auto-generated method stub

//		WangDataTest.test1("J:\\Data\\sixple\\control2\\pep1", 
//				"D:\\quatification_data_standard\\1_4_1\\20130319", 6);
//		WangDataTest.select("J:\\Data\\sixple\\turnover\\dat\\final\\final.pros.xls");
		WangDataTest.test2("J:\\Data\\sixple\\control2\\pep1", 
				"J:\\Data\\sixple\\control2\\peak1\\20130405\\C1_0405.pxml");
//		WangDataTest.testWrong("J:\\Data\\sixple\\turnover\\dat\\0_3_6_peak\\0_3_6.pxml", 
//				"J:\\Data\\sixple\\turnover\\dat\\0_3_6_peak\\0_3_6.xls");
		
//		WangDataTest.mcpCompare("J:\\Data\\sixple\\turnover\\dat\\final3\\ipi.HUMAN.v3.68.fasta", 
//				"J:\\Data\\sixple\\turnover\\dat\\final3\\mcp.txt",
//				"J:\\Data\\sixple\\turnover\\dat\\final5\\turnover.xls");
		
//		WangDataTest.deIntensity("J:\\Data\\sixple\\control\\peaks\\trypsin\\trypsin_5.xls", 
//				"J:\\Data\\sixple\\control\\peaks\\trypsin\\trypsin_noIntensity.xls");
//		WangDataTest.selectPro("J:\\Data\\sixple\\turnover\\dat\\final4\\�½� �ı��ĵ�.txt", 
//				"J:\\Data\\sixple\\turnover\\dat\\final5\\�½� �ı��ĵ�.txt", 
//				"J:\\Data\\sixple\\turnover\\dat\\final5\\turnover5.xls");
	}

}
