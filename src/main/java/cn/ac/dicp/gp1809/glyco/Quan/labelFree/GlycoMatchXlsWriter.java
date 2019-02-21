/* 
 ******************************************************************************
 * File: GlycoMatchXlsWriter.java * * * Created on 2012-5-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.DocumentException;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2012-5-25, 15:27:39
 */
public class GlycoMatchXlsWriter {

	private ExcelWriter writer;
	private ExcelFormat format;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public GlycoMatchXlsWriter(String file) throws IOException, RowsExceededException, WriteException{
		
		this.writer = new ExcelWriter(file);
		this.format = ExcelFormat.normalFormat;
		
		this.addTitle();
	}
	
	/**
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 * 
	 */
	private void addTitle() throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("Pep Scannum\t");
		sb.append("Sequence\t");
		sb.append("Retention Time\t");
		sb.append("Pep Charge\t");
		sb.append("Pep Mass\t");

		sb.append("Glycan Scannum\t");
		sb.append("Glycan Rt\t");
		sb.append("Glycan Charge\t");
		sb.append("Pep Mass\t");
		sb.append("Delta Mass (Da)\t");
		sb.append("Delta Mass (PPM)\t");
		sb.append("Glycan Mass\t");
		sb.append("Score\t");
		sb.append("Rank\t");
		sb.append("IUPAC Name\t");
		sb.append("Protein\t");
		sb.append("Site\t");

		this.writer.addTitle(sb.toString(), 0, format);
	}

	public void write(IGlycoPeptide [] peps) throws RowsExceededException, WriteException{
		
		for(int i=0;i<peps.length;i++){
			
			this.write(peps[i]);
		}
	}

	/**
	 * @param glycoPeptide
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	private void write(IGlycoPeptide pep) throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		
		int pepScannum = pep.getScanNumBeg();
		String sequence = pep.getSequence();
		int rt = (int) pep.getRetentionTime();
		int charge = pep.getCharge();
		double pepMass = pep.getPepMrNoGlyco();
		
		NGlycoSSM ssm = pep.getDeleStructure();
		int glycoScannum = ssm.getScanNum();
		double glycoRt = ssm.getRT();
		int glycoCharge = ssm.getPreCharge();
		double glycoPepMass = ssm.getPepMass();
		double deltaMass = glycoPepMass - pepMass;
		double glycoMass = ssm.getGlycoMass();
		double score = ssm.getScore();
		int rank = ssm.getRank();
		String name = ssm.getName();
		
		StringBuilder sb = new StringBuilder();
		sb.append(pepScannum).append("\t");
		sb.append(sequence).append("\t");
		sb.append(rt).append("\t");
		sb.append(charge).append("\t");
		sb.append(pepMass).append("\t");
		
		sb.append(glycoScannum).append("\t");
		sb.append(glycoRt).append("\t");
		sb.append(glycoCharge).append("\t");
		sb.append(glycoPepMass).append("\t");
		sb.append(df4.format(deltaMass)).append("\t");
		sb.append(df4.format(deltaMass/glycoPepMass*1E6)).append("\t");
		sb.append(glycoMass).append("\t");
		sb.append(score).append("\t");
		sb.append(rank).append("\t");
		sb.append(name).append("\t");
		
		HashMap <String, SimpleProInfo> proInfoMap = pep.getProInfoMap();
		HashMap <String, SeqLocAround> slaMap = pep.getPepLocAroundMap();
		GlycoSite [] sites = pep.getAllGlycoSites();
		
		Iterator <String> it = proInfoMap.keySet().iterator();

		while(it.hasNext()){
			
			String key = it.next();
			SimpleProInfo info = proInfoMap.get(key);
			SeqLocAround sla = slaMap.get(key);
			
			sb.append(info.getRef()).append("\t");
			
			for(int k=0;k<sites.length;k++){
				
				int loc = sites[k].modifLocation();
				int beg = sla.getBeg();
				int proloc = loc+beg-1;
				
				sb.append(proloc).append("\t");
			}
			break;
		}
		
		writer.addContent(sb.toString(), 0, format);
		
	}
	
	public void close() throws WriteException, IOException{
		this.writer.close();
	}
	
	public static void batchWrite(String in) throws DocumentException, RowsExceededException, 
		WriteException, IOException{
		
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			GlycoLFFeasXMLReader reader = new GlycoLFFeasXMLReader(files[i]);
			IGlycoPeptide [] peps = reader.getAllSelectedPeps();
			String out = files[i].getAbsolutePath().replace("pxml", "xls");
			GlycoMatchXlsWriter writer = new GlycoMatchXlsWriter(out);
			writer.write(peps);
			writer.close();
		}
	}
	
	public static void test(String in) throws DocumentException, RowsExceededException, WriteException, IOException{
		
		GlycoLFFeasXMLReader reader = new GlycoLFFeasXMLReader(in);
		IGlycoPeptide [] peps = reader.getAllSelectedPeps();
		String out = in.replace("pxml", "xls");
		GlycoMatchXlsWriter writer = new GlycoMatchXlsWriter(out);
		writer.write(peps);
		writer.close();
	}
	
	public static void combine(String in, String out) throws IOException, JXLException{
		
		HashMap <String, String[]> map = new HashMap <String, String[]>();
		HashMap <String, Double> deltamap = new HashMap <String, Double>();
		String [] title = null;
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("xls")){
				ExcelReader reader = new ExcelReader(files[i]);
				String [] line = reader.readLine();
				if(title==null){
					title = new String [line.length];
					System.arraycopy(line, 0, title, 0, line.length);
				}
				while((line=reader.readLine())!=null){
					if(line[line.length-4].equals("2")) continue;
					String key = line[line.length-1]+line[line.length-2]+line[line.length-3];
					double delta = Math.abs(Double.parseDouble(line[10]));
					if(map.containsKey(key)){
						if(delta<deltamap.get(key)){
							map.put(key, line);
							deltamap.put(key, delta);
						}
					}else{
						map.put(key, line);
						deltamap.put(key, delta);
					}
				}
			}
		}
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		writer.addTitle(title, 0, format);
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String [] line = map.get(key);
			writer.addContent(line, 0, format);
		}
		writer.close();
	}
	
	public static void combineSite(String in1, String in2, String out) throws IOException, JXLException{
		
		HashMap <String, String[]> map = new HashMap <String, String[]>();
		String [] title = null;

		ExcelReader reader1 = new ExcelReader(in1, 2);
		String [] line1 = reader1.readLine();
		if(title==null){
			title = new String [line1.length];
			System.arraycopy(line1, 0, title, 0, line1.length);
		}
		
		while((line1=reader1.readLine())!=null){
//			String key = line1[0]+line1[1]+line1[2];
			String key = line1[0]+line1[1];
			if(!map.containsKey(key)){
				map.put(key, line1);
			}
		}
		reader1.close();
		
		ExcelReader reader2 = new ExcelReader(in2, 2);
		String [] line2 = reader2.readLine();

		while((line2=reader2.readLine())!=null){
//			String key = line2[0]+line2[1]+line2[2];
			String key = line2[0]+line2[1];
			if(!map.containsKey(key)){
				map.put(key, line2);
			}
		}
		reader1.close();

		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		writer.addTitle(title, 0, format);
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String [] line = map.get(key);
			writer.addContent(line, 0, format);
		}
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DocumentException 
	 * @throws JXLException 
	 */
	public static void main(String[] args) throws DocumentException, IOException, JXLException {
		// TODO Auto-generated method stub

//		GlycoMatchXlsWriter.batchWrite("F:\\P\\glyco_quant\\final20130227\\20130202_glyco");
//		GlycoMatchXlsWriter.test("F:\\P\\glyco_quant\\final20130227\\" +
//				"20120206_Hela_HILIC_intact_HCD_100min.pxml");
//		GlycoMatchXlsWriter.combine("F:\\P\\glyco_quant\\final20130227\\20130202_glyco_hek", 
//				"F:\\P\\glyco_quant\\final20130227\\20130202_glyco_combine_total.xls");
		GlycoMatchXlsWriter.combineSite("F:\\P\\glyco_quant\\final20130227\\20130202_glyco_hek\\HEK_site.xls", 
				"F:\\P\\glyco_quant\\final20130227\\20130202_glyco_hek\\Hela_site.xls", 
				"F:\\P\\glyco_quant\\final20130227\\20130202_glyco_hek\\combine_site2.xls");
	}

}
