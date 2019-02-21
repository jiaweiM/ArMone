/* 
 ******************************************************************************
 * File: GlycoIdenResultWriter.java * * * Created on 2011-12-27
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.glyco.Quan.label.GlycoPeptideLabelPair;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2011-12-27, 15:39:02
 */
public class GlycoIdenResultWriter {
	
	private ExcelWriter writer;
	private HashMap <String, ArrayList <GlycoPepFreeFeatures>> pairMap;
	
	private ExcelFormat format = ExcelFormat.indexFormat;

	public GlycoIdenResultWriter(String in, String out) throws IOException, DocumentException, 
		RowsExceededException, WriteException{
		
		GlycoLFFeasXMLReader reader = new GlycoLFFeasXMLReader(in);
		
		this.writer = new ExcelWriter(out);
		this.pairMap = reader.getPairMap();
		
		this.addTitle();
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Index\t");
		sb.append("Sequence\t");
		sb.append("Pep_Mass\t");
		sb.append("Glyco_Mass\t");
		sb.append("HCD scans\t");
		sb.append("Glycans_Ratio\t");
		sb.append("Reference\t");
		sb.append("Site\t");
		sb.append("Glycan composition\t");

		writer.addTitle(sb.toString(), 0, format);
	}

	public void write() throws RowsExceededException, WriteException, IOException{
		
		Iterator <String> it = this.pairMap.keySet().iterator();
		int idx = 0;
		while(it.hasNext()){
			
			String key = it.next();
			ArrayList <GlycoPepFreeFeatures> list = pairMap.get(key);
			StringBuilder sb = new StringBuilder();
			sb.append(++idx);
			
			for(int i=0;i<list.size();i++){
				
				GlycoPepFreeFeatures pair = list.get(i);
				sb.append("\t").append(pair.getPepInfo()).append(pair.getSiteInfo()).append("\n");
			}
			
			this.writer.addContent(sb.toString(), 0, format);
		}
		
		this.writer.close();
	}
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws IOException, DocumentException, RowsExceededException, WriteException {
		// TODO Auto-generated method stub

		String in = "I:\\glyco\\label-free\\20111123_HILIC_1105_HCD_111124034738_30.pxml";
		String out = "I:\\glyco\\label-free\\20111123_HILIC_1105_HCD_111124034738_30.xls";
		GlycoIdenResultWriter writer = new GlycoIdenResultWriter(in, out);
		writer.write();
		
	}

}
