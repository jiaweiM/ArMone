/* 
 ******************************************************************************
 * File: GlycoIdenXlsWriter.java * * * Created on 2012-5-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.DocumentException;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.glyco.Quan.labelFree2.GlycoLFFeasXMLReader2;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2012-5-21, 08:52:30
 */
public class GlycoIdenXlsWriter {
	
	private ExcelWriter writer;
	private ExcelFormat ef;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public GlycoIdenXlsWriter(String file) throws IOException, RowsExceededException, WriteException{
		
		this.writer = new ExcelWriter(file);
		this.ef = ExcelFormat.normalFormat;
		
		this.addTitle();
	}
	
	/**
	 * @param out
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public GlycoIdenXlsWriter(File file) throws IOException, RowsExceededException, WriteException {
		// TODO Auto-generated constructor stub
		this.writer = new ExcelWriter(file);
		this.ef = ExcelFormat.normalFormat;
		
		this.addTitle();
	}

	private void addTitle() throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		sb.append("Scannum\t");
		sb.append("Charge\t");
		sb.append("Mz\t");
		sb.append("Retention Time\t");
		sb.append("Rank\t");
		sb.append("IUPAC Name\t");
		sb.append("Score\t");
		sb.append("Glyco Mass\t");
		sb.append("Peptide Mass\t");
		sb.append("Type\t");
		
		this.writer.addTitle(sb.toString(), 0, ef);
	}
	
	public void write(NGlycoSSM [] ssms) throws RowsExceededException, WriteException{
		
		for(int i=0;i<ssms.length;i++){
			
			this.write(ssms[i]);
		}
		
	}
	
	private void write(NGlycoSSM ssm) throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		sb.append(ssm.getScanNum()).append("\t");
		sb.append(ssm.getPreCharge()).append("\t");
		sb.append(ssm.getPreMz()).append("\t");
		sb.append(ssm.getRT()).append("\t");
		sb.append(ssm.getRank()).append("\t");
		sb.append(ssm.getName()).append("\t");
		sb.append(ssm.getScore()).append("\t");
		sb.append(ssm.getGlycoMass()).append("\t");
		sb.append(df4.format(ssm.getPepMass())).append("\t");
		sb.append(ssm.getGlycoTree().getType());
		
		this.writer.addContent(sb.toString(), 0, ef);
	}
	
	private void write(NGlycoSSM ssm, String file) throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		sb.append(file).append("\t");
		sb.append(ssm.getScanNum()).append("\t");
		sb.append(ssm.getPreCharge()).append("\t");
		sb.append(ssm.getPreMz()).append("\t");
		sb.append(ssm.getRT()).append("\t");
		sb.append(ssm.getRank()).append("\t");
		sb.append(ssm.getName()).append("\t");
		sb.append(ssm.getScore()).append("\t");
		sb.append(ssm.getGlycoMass()).append("\t");
		sb.append(df4.format(ssm.getPepMass())).append("\t");
		sb.append(ssm.getGlycoTree().getType());
		
		this.writer.addContent(sb.toString(), 0, ef);
	}
	
	public void close() throws WriteException, IOException{
		this.writer.close();
	}

	public static void batchWrite(String in) throws Exception{
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("pxml")){
				GlycoIdenXMLReader reader = new GlycoIdenXMLReader(files[i]);
				GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter(files[i].getAbsolutePath().replace("pxml", "xls"));
				NGlycoSSM [] ssm = reader.getAllMatches();
				for(int j=0;j<ssm.length;j++){
					writer.write(ssm[j]);
				}
				writer.close();
			}
		}
	}
	
	public static void batchWrite(String in, String out) throws Exception{
		
		File [] files = (new File(in)).listFiles();
		GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter(out);
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("pxml")){
				
				GlycoIdenXMLReader reader = new GlycoIdenXMLReader(files[i]);
				
				NGlycoSSM [] ssm = reader.getAllMatches();
				for(int j=0;j<ssm.length;j++){
					writer.write(ssm[j], files[i].getName());
				}
				
				System.out.println(files[i].getName()+"\t"+ssm.length);
			}
		}
		
		writer.close();
	}
	
	private static void batchOnlyGlycanWrite(String dir) throws DocumentException, RowsExceededException, WriteException, IOException{
		
		File [] files = (new File(dir)).listFiles();
		for(int i=0;i<files.length;i++){
			
			if(files[i].getName().endsWith("pxml")){
				
				String in = files[i].getAbsolutePath();
				String out = in.replace("pxml", "xls");
				
				GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(in);
				GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter(out);

				NGlycoSSM[] matchedssms = reader.getMatchedGlycoSpectra();

				for(int j=0;j<matchedssms.length;j++){
					writer.write(matchedssms[j]);
				}
				
				NGlycoSSM[] unmatchedssms = reader.getUnmatchedGlycoSpectra();

				for(int j=0;j<unmatchedssms.length;j++){
					writer.write(unmatchedssms[j]);
				}
				writer.close();
			}
		}
	}
	
	private static void batchOnlyGlycanWrite(String dir, String out) throws DocumentException, RowsExceededException, WriteException, IOException{
		
		File [] files = (new File(dir)).listFiles();
		GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter(out);

		for(int i=0;i<files.length;i++){
			
			if(files[i].getName().endsWith("pxml")){
				
				String in = files[i].getAbsolutePath();
				
				GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(in);

				NGlycoSSM[] matchedssms = reader.getMatchedGlycoSpectra();

				for(int j=0;j<matchedssms.length;j++){
					writer.write(matchedssms[j]);
				}
				
				NGlycoSSM[] unmatchedssms = reader.getUnmatchedGlycoSpectra();

				for(int j=0;j<unmatchedssms.length;j++){
					writer.write(unmatchedssms[j]);
				}
				
				System.out.println(files[i].getName()+"\t"+(matchedssms.length+unmatchedssms.length));
			}
		}
		writer.close();
	}
	
	public static void combine(String in, String out) throws IOException, JXLException{
		
		HashMap <String, String[]> map = new HashMap <String, String[]>();
		HashMap <String, Double> deltamap = new HashMap <String, Double>();
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat format = ExcelFormat.normalFormat;
		
		String [] title = null;
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().endsWith("xls")){
				ExcelReader reader = new ExcelReader(files[i]);
				String [] line = reader.readLine();
				if(title==null){
					title = new String [line.length];
					System.arraycopy(line, 0, title, 0, line.length);
					writer.addTitle(title, 0, format);
					writer.addTitle(title, 1, format);
				}
				while((line=reader.readLine())!=null){
					String key = line[5];
					double delta = Double.parseDouble(line[6]);
					if(map.containsKey(key)){
						if(delta>deltamap.get(key)){
							map.put(key, line);
							deltamap.put(key, delta);
						}
					}else{
						map.put(key, line);
						deltamap.put(key, delta);
					}
					writer.addContent(line, 0, format);
				}
			}
		}
		System.out.println(map.size());
		
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String [] line = map.get(key);
			writer.addContent(line, 1, format);
		}
		writer.close();
	}
	
	private static void batchDecoyWriter(String target, String decoy) throws Exception{
		
		HashMap<String, HashMap<Integer, NGlycoSSM>> targetmap = new HashMap<String, HashMap<Integer, NGlycoSSM>>();
		File [] targets = (new File(target)).listFiles();
		for(int i=0;i<targets.length;i++){
			if(!targets[i].getName().endsWith("pxml"))
				continue;
			
			HashMap<Integer, NGlycoSSM> scoremap = new HashMap<Integer, NGlycoSSM>();
			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(targets[i]);
			NGlycoSSM[] matchedssms = reader.getMatchedGlycoSpectra();
			for(int j=0;j<matchedssms.length;j++){
				scoremap.put(matchedssms[j].getScanNum(), matchedssms[j]);
			}
			
			NGlycoSSM[] unmatchedssms = reader.getUnmatchedGlycoSpectra();
			for(int j=0;j<unmatchedssms.length;j++){
				scoremap.put(unmatchedssms[j].getScanNum(), unmatchedssms[j]);
			}
			
			String key = targets[i].getName().substring(0, targets[i].getName().length()-5);
			targetmap.put(key, scoremap);
			reader.close();
			
		}
		
		File [] decoys = (new File(decoy)).listFiles();
		for(int i=0;i<decoys.length;i++){
			if(!decoys[i].getName().endsWith("pxml"))
				continue;
			
			String out = decoys[i].getAbsolutePath().replace("pxml", "xls");
			GlycoIdenXMLReader reader = new GlycoIdenXMLReader(decoys[i]);
			GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter(out);
			
			String key = decoys[i].getName().substring(0, decoys[i].getName().length()-10);

			HashMap<Integer, NGlycoSSM> scoremap = targetmap.get(key);
			NGlycoSSM[] ssms = reader.getAllMatches();
			for(int j=0;j<ssms.length;j++){
				if(scoremap.containsKey(ssms[j].getScanNum())){
					if(scoremap.get(ssms[j].getScanNum()).getScore()>=ssms[j].getScore()){
						continue;
					}
//					NGlycoSSM targetssm = scoremap.get(ssms[j].getScanNum());
//					System.out.println(decoys[i].getName()+"\t"+ssms[j].getScanNum()+"\t"+targetssm.getGlycoMass()+"\t"+ssms[j].getGlycoMass()
//							+"\t"+targetssm.getGlycoTree().getIupacName()+"\t"+ssms[j].getGlycoTree().getIupacName());
				}else{
//					System.out.println(ssms[j].getScanNum()+"\t"+ssms[j].getSocre());
				}
				
				writer.write(ssms[j]);
			}
			reader.close();
			writer.close();
//			break;
		}
	}
	
	private static void batchDecoyWriter(String target, String decoy, String out) throws Exception{
		
		HashMap<String, HashMap<Integer, NGlycoSSM>> targetmap = new HashMap<String, HashMap<Integer, NGlycoSSM>>();
		File [] targets = (new File(target)).listFiles();
		for(int i=0;i<targets.length;i++){
			if(!targets[i].getName().endsWith("pxml"))
				continue;
			
			HashMap<Integer, NGlycoSSM> scoremap = new HashMap<Integer, NGlycoSSM>();
			GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2(targets[i]);
			NGlycoSSM[] matchedssms = reader.getMatchedGlycoSpectra();
			for(int j=0;j<matchedssms.length;j++){
				scoremap.put(matchedssms[j].getScanNum(), matchedssms[j]);
			}
			
			NGlycoSSM[] unmatchedssms = reader.getUnmatchedGlycoSpectra();
			for(int j=0;j<unmatchedssms.length;j++){
				scoremap.put(unmatchedssms[j].getScanNum(), unmatchedssms[j]);
			}
			
			String key = targets[i].getName().substring(0, targets[i].getName().length()-5);
			targetmap.put(key, scoremap);
			reader.close();
			
		}
		
		GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter(out);
		
		File [] decoys = (new File(decoy)).listFiles();
		for(int i=0;i<decoys.length;i++){
			if(!decoys[i].getName().endsWith("pxml"))
				continue;
			
			GlycoIdenXMLReader reader = new GlycoIdenXMLReader(decoys[i]);
			String key = decoys[i].getName().substring(0, decoys[i].getName().length()-10);

			HashMap<Integer, NGlycoSSM> scoremap = targetmap.get(key);
			NGlycoSSM[] ssms = reader.getAllMatches();
			for(int j=0;j<ssms.length;j++){
				if(scoremap.containsKey(ssms[j].getScanNum())){
					if(scoremap.get(ssms[j].getScanNum()).getScore()>=ssms[j].getScore()){
						continue;
					}
//					NGlycoSSM targetssm = scoremap.get(ssms[j].getScanNum());
//					System.out.println(decoys[i].getName()+"\t"+ssms[j].getScanNum()+"\t"+targetssm.getGlycoMass()+"\t"+ssms[j].getGlycoMass()
//							+"\t"+targetssm.getGlycoTree().getIupacName()+"\t"+ssms[j].getGlycoTree().getIupacName());
				}else{
//					System.out.println(ssms[j].getScanNum()+"\t"+ssms[j].getSocre());
				}
				
				writer.write(ssms[j], decoys[i].getName());
			}
			reader.close();
		}
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

/*		GlycoIdenXMLReader reader = new GlycoIdenXMLReader("H:\\glyco\\label-free\\final\\" +
				"20111123_HILIC_1105_HCD_2.pxml");
		GlycoIdenXlsWriter writer = new GlycoIdenXlsWriter("H:\\glyco\\label-free\\final\\" +
				"20111123_HILIC_1105_HCD_2.xls");
		GlycoLFFeasXMLReader reader1 = new GlycoLFFeasXMLReader("H:\\glyco\\label-free\\final\\" +
				"20111123_HILIC_1105_HCD_111124034738_match_1.pxml");
		LabelFeatures [] feas = reader1.
		HashSet <Integer> set = new HashSet <Integer>();
		for(int i=0;i<pairs.length;i++){
			GlycoPepLabelFeatures gp = (GlycoPepLabelFeatures) pairs[i];
			IGlycoPeptide pep = (IGlycoPeptide) gp.getPeptide();
			NGlycoSSM ssm = pep.getDeleStructure();
			if(set.add(ssm.getScanNum())){
				
			}else{
				System.out.println(ssm.getScanNum());
			}
		}
		System.out.println(set.size());
		
		NGlycoSSM [] ssm = reader.getAllMatches();
		for(int i=0;i<ssm.length;i++){
//			if(!set.contains(ssm[i].getScanNum())){
				if(ssm[i].getRank()==1)
					writer.write(ssm[i]);
//			}
		}
		writer.close();
*/
//		GlycoIdenXlsWriter.batchWrite("H:\\NGLYCO\\NGlyco_final_20140401\\decoy");
		GlycoIdenXlsWriter.combine("H:\\NGLYCO\\NGlyco_final_20140408\\decoy_iden_with_oxonium", 
				"H:\\NGLYCO\\NGlyco_final_20140408\\decoy_iden_with_oxonium.combine.xls");

//		GlycoIdenXlsWriter.batchOnlyGlycanWrite("H:\\NGlycan_quan_20131016\\test_20131030\\iden", 
//				"H:\\NGlycan_quan_20131016\\test_20131030\\iden");
//		GlycoIdenXlsWriter.batchWrite("C:\\Inetpub\\wwwroot\\ISB\\data", 
//				"C:\\Inetpub\\wwwroot\\ISB\\data\\test.xls");
		
//		GlycoIdenXlsWriter.batchDecoyWriter("H:\\NGlyco_final_20130730\\2D_4", 
//				"H:\\NGlyco_final_20130730\\iden_decoy_database");
		
//		GlycoIdenXlsWriter.batchDecoyWriter("H:\\NGlyco_final_20130730\\2D_4", 
//				"H:\\NGlyco_final_20130730\\iden_decoy_database", "H:\\NGlyco_final_20130730\\iden_decoy_database.xls");
	}

}
