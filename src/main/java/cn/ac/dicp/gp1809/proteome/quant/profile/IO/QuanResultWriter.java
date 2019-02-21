/* 
 ******************************************************************************
 * File: QuanResultWriter.java * * * Created on 2011-7-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile.IO;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanUnit;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 *
 * @version 2011-7-4, 10:09:45
 */
public class QuanResultWriter {

	private LabelType type;
	private boolean grad;
	private ModInfo [] mods;
	private String [] ratioNames;
	private int index;
	private int ratioNum;
//	private int labelNum;
	private int totalPair;
	private int totalPro;
	private int [][] ProRatioStat;
	private int [][] PepRatioStat;
	private double [][] range;
	private HashSet <LabelQuanUnit> feasSet;
	private HashMap <String, HashSet <LabelQuanUnit>> feasMap;
	
	private ExcelWriter writer;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;

	public QuanResultWriter(String output, LabelType type, boolean grad, ModInfo [] mods, 
			String [] ratioNames, double [] theoryRatio) 
		throws IOException, RowsExceededException, WriteException{

		this.type = type;
		this.grad = grad;
		this.mods = mods;
		this.ratioNames = ratioNames;

		this.ratioNum = ratioNames.length;
		ProRatioStat = new int[ratioNum][6];
		PepRatioStat = new int[ratioNum][6];
		range = new double [ratioNum][5];
		this.iniRange(theoryRatio);
		
		this.feasSet = new HashSet <LabelQuanUnit>();
		this.feasMap = new HashMap <String, HashSet <LabelQuanUnit>>();
		this.writer = new ExcelWriter(output, new String[]{ "Protein", "Peptide", "Uncertain peptide" });
		this.addHeader();
		this.addTitle();
	}

	private void iniRange(double [] theoryRatio){
		for(int i=0;i<theoryRatio.length;i++){
			double tr = theoryRatio[i];
			range[i][0] = tr*0.5;
			range[i][1] = tr*0.8;
			range[i][2] = tr*1.2;
			range[i][3] = tr*2.0;
		}
	}
	
	private void addHeader() throws RowsExceededException, WriteException{

		ExcelFormat f1 = new ExcelFormat(false,0);
		this.writer.addContent("\n\t--------------------Header--------------------", 0, f1);
		this.writer.addBlankRow(0);
		String s1 = "Label Type\t"+type.getLabelName();
		this.writer.addContent(s1, 0, f1);
		
		if(type.name().equals("LabelFree")){
			String [] files = type.getFilesNames();
			for(int i=0;i<files.length;i++){
				this.writer.addContent("Sample_"+(i+1)+"\t"+files[i]+"\n", 0, f1);
			}
		}else{
			this.writer.addContent("Label type\tModSite\tMass\tDescription\n", 0, f1);
			LabelInfo [][] infos = type.getInfo();
			String labelName = type.getLabelName();
			for(int i=0;i<infos.length;i++){
				for(int j=0;j<infos[i].length;j++){
					StringBuilder sb = new StringBuilder();
					sb.append(labelName).append("_").append(i+1).append("\t");
					sb.append(infos[i][j].getNoSymbolDescription()).append("\n");
					this.writer.addContent(sb.toString(), 0, f1);
				}
			}
		}

		this.writer.addContent("\n\t--------------------Variable modifications--------------------", 0, f1);
		this.writer.addContent("ModSite\tMass\tSymbol\tDescription\n", 0, f1);
		for(int i=0;i<mods.length;i++){
			this.writer.addContent(mods[i].toString()+"\n", 0, f1);
		}
		this.writer.addBlankRow(0);
		this.writer.addBlankRow(0);
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		int num = type.getLabelNum();
		sb.append("Index\t").append("Reference\tIsUnique?\t");
		
		for(int i=0;i<this.ratioNames.length;i++){
			sb.append(ratioNames[i]).append("\t").append("RSD\t");
		}
		for(int i=0;i<num;i++){
			sb.append(type.getLabelName()).append("_"+(i+1)).append("\t");
		}
		sb.append("\n");		
		sb.append("  \t").append("Sequence\t");
		for(int i=0;i<this.ratioNames.length;i++){
			sb.append(ratioNames[i]).append("\t");
		}
		
		if(type.name().equals("LabelFree")){
			
			String [] fileNames = type.getFilesNames();
			for(int i=0;i<fileNames.length;i++){
				sb.append(fileNames[i]).append("\t");
			}
			sb.append("References\t");
			
		}else{
			
			for(int i=0;i<num;i++){
				sb.append(type.getLabelName()).append("_"+(i+1)).append("\t");
			}
			sb.append("References\tFile");
		}

		ExcelFormat format = new ExcelFormat(false,0);
		this.writer.addTitle(sb.toString(), 0, format);
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("Sequence\t");
		for(int i=0;i<this.ratioNames.length;i++){
			sb2.append(ratioNames[i]).append("\t");
		}
		
		if(type.name().equals("LabelFree")){
			String [] fileNames = type.getFilesNames();
			for(int i=0;i<fileNames.length;i++){
				sb2.append(fileNames[i]).append("\t");
			}
			sb2.append("Identified num\tReferences\t");
		}else{
			for(int i=0;i<num;i++){
				sb2.append(type.getLabelName()).append("_"+(i+1)).append("\t");
			}
			sb2.append("References\t");
		}

		ExcelFormat format2 = new ExcelFormat(false,0);
		this.writer.addTitle(sb2.toString(), 1, format2);
		this.writer.addTitle(sb2.toString(), 2, format2);
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IProteinWriter#close()
	 */
	public void close() throws WriteException, IOException {
		// TODO Auto-generated method stub
		this.writer.close();
		System.gc();
	}
	
	public void write(QuanResult result) throws RowsExceededException, WriteException{
		if(grad){
			this.writeGrad(result);
		}else{
			this.writeSingle(result);
		}
	}

	public void writeSingle(QuanResult result) throws RowsExceededException, WriteException{

		if(result.getUnique()){
			
			index++;
			totalPro++;
			double [] proR = result.getRatio();
			for(int i=0;i<proR.length;i++){
				if(proR[i]==0)
					ProRatioStat[i][5]++;
				else if(proR[i]<range[i][0])
					ProRatioStat[i][0]++;
				else if(proR[i]>=range[i][0] && proR[i]<range[i][1])
					ProRatioStat[i][1]++;
				else if(proR[i]>=range[i][1] && proR[i]<range[i][2])
					ProRatioStat[i][2]++;
				else if(proR[i]>=range[i][2] && proR[i]<range[i][3])
					ProRatioStat[i][3]++;
				else if(proR[i]>=range[i][3])
					ProRatioStat[i][4]++;
			}
			
			result.setIndex(index);
			ExcelFormat f1 = new ExcelFormat(true, 0);
			this.writer.addContent(result.getQRef(), 0, f1);
			
			LabelQuanUnit [] units = result.getUnits();
			for(int k=0;k<units.length;k++){
//				if(feass[k].getUse()){
					ExcelFormat f2 = new ExcelFormat(false, 0);
					this.writer.addContent(" \t"+units[k].toString(), 0, f2);
//				}else{
//					ExcelFormat f3 = new ExcelFormat(false,2);
//					this.writer.addContent(" \t"+feass[k].toString(), 0, f3);
//				}
				
				if(!this.feasSet.contains(units[k])){
					
//					if(feass[k].getUse()){
						feasSet.add(units[k]);
						totalPair++;
						double [] pepR = units[k].getRatio();
						for(int l=0;l<pepR.length;l++){
							if(pepR[l]==0)
								PepRatioStat[l][5]++;
							else if(pepR[l]<range[l][0])
								PepRatioStat[l][0]++;
							else if(pepR[l]>=range[l][0] && pepR[l]<range[l][1])
								PepRatioStat[l][1]++;
							else if(pepR[l]>=range[l][1] && pepR[l]<range[l][2])
								PepRatioStat[l][2]++;
							else if(pepR[l]>=range[l][2] && pepR[l]<range[l][3])
								PepRatioStat[l][3]++;
							else if(pepR[l]>=range[l][3])
								PepRatioStat[l][4]++;
						}

						this.writer.addContent(units[k].toString(), 1, f2);
//					}else{
//						ExcelFormat f3 = new ExcelFormat(false,2);
//						this.writer.addContent(feass[k].toString(), 1, f3);
//					}
				}			
			}		
		}else{
			
			ExcelFormat f = new ExcelFormat(true, 0);
			result.setIndex(index);
			this.writer.addContent(result.getQRef(), 0, f);
			LabelQuanUnit [] units = result.getUnits();
			for(int k=0;k<units.length;k++){
//				if(feass[k].getUse()){
					ExcelFormat f2 = new ExcelFormat(false, 0);
					this.writer.addContent(" \t"+units[k].toString(), 0, f2);
//				}else{
//					ExcelFormat f3 = new ExcelFormat(false,2);
//					this.writer.addContent(" \t"+feass[k].toString(), 0, f3);
//				}
			}
		}		
	}
	
	public void writeGrad(QuanResult result) throws RowsExceededException, WriteException{

		if(result.getUnique()){
			
			index++;
			totalPro++;
			double [] proR = result.getRatio();
			for(int i=0;i<proR.length;i++){
				if(proR[i]==0)
					ProRatioStat[i][5]++;
				else if(proR[i]<range[i][0])
					ProRatioStat[i][0]++;
				else if(proR[i]>=range[i][0] && proR[i]<range[i][1])
					ProRatioStat[i][1]++;
				else if(proR[i]>=range[i][1] && proR[i]<range[i][2])
					ProRatioStat[i][2]++;
				else if(proR[i]>=range[i][2] && proR[i]<range[i][3])
					ProRatioStat[i][3]++;
				else if(proR[i]>=range[i][3])
					ProRatioStat[i][4]++;
			}
			
			result.setIndex(index);
			ExcelFormat f1 = new ExcelFormat(true, 0);
			this.writer.addContent(result.toString(), 0, f1);
				
		}else{
			
			if(index==0){
				
			}
			
			ExcelFormat f = new ExcelFormat(true, 0);
			result.setIndex(index);
			this.writer.addContent(result.toString(), 0, f);
			
		}		
		
		LabelQuanUnit [] units = result.getUnits();
		for(int k=0;k<units.length;k++){
//			if(feass[k].getUse()){
				String seq = units[k].getSequence();
				if(this.feasMap.containsKey(seq)){
					feasMap.get(seq).add(units[k]);
				}else{
					HashSet <LabelQuanUnit> unitset = new HashSet <LabelQuanUnit>();
					unitset.add(units[k]);
					feasMap.put(seq, unitset);
				}
//			}
		}
	}
	
	public void write(PeptidePair pair) throws RowsExceededException, WriteException{
		ExcelFormat f = new ExcelFormat(false, 0);
		this.writer.addContent(pair.toString(), 2, f);
	}
	
	public void write(IPeptide peptide) throws RowsExceededException, WriteException{
		ExcelFormat f = new ExcelFormat(false, 0);
		StringBuilder sb = new StringBuilder();
		sb.append(peptide.getSequence()).append("\t");
		sb.append("0\t0\t0\t");
		sb.append(peptide.getDelegateReference()).append("\t");
		this.writer.addContent(sb.toString(), 2, f);
	}
	
	private void addPeptideInfo() throws RowsExceededException, WriteException{
		
		ExcelFormat f2 = new ExcelFormat(false, 0);
		DecimalFormat df4 = DecimalFormats.DF0_4;
		this.totalPair = feasMap.size();

		Iterator <String> it = this.feasMap.keySet().iterator();
		while(it.hasNext()){
			String seq = it.next();
			HashSet <LabelQuanUnit> feasset = feasMap.get(seq);
			Iterator <LabelQuanUnit> ppit = feasset.iterator();
			
			if(feasset.size()==1){
				if(ppit.hasNext()){
					
					LabelQuanUnit unit = ppit.next();
					double [] pepR = unit.getRatio();
					for(int l=0;l<pepR.length;l++){
						if(pepR[l]==0)
							PepRatioStat[l][5]++;
						else if(pepR[l]<range[l][0])
							PepRatioStat[l][0]++;
						else if(pepR[l]>=range[l][0] && pepR[l]<range[l][1])
							PepRatioStat[l][1]++;
						else if(pepR[l]>=range[l][1] && pepR[l]<range[l][2])
							PepRatioStat[l][2]++;
						else if(pepR[l]>=range[l][2] && pepR[l]<range[l][3])
							PepRatioStat[l][3]++;
						else if(pepR[l]>=range[l][3])
							PepRatioStat[l][4]++;
					}
					
					StringBuilder sb = new StringBuilder();
					sb.append(unit.getSequence()).append("\t");
					
					for(int i=0;i<pepR.length;i++){
						sb.append(pepR[i]).append("\t");
					}
						
					double [] intens = unit.getIntensity();
					for(int j=0;j<intens.length;j++){
						sb.append(df4.format(intens[j])).append("\t");	
					}

//					sb.append(unit.getPresentFeasNum()).append("\t");
					String deleRef = unit.getDelegateRef();
					if(deleRef==null){
						sb.append(unit.getRefs());
					}else{
						sb.append(deleRef);
					}
					
					this.writer.addContent(sb.toString(), 1, f2);

				}
			}else{
				
				double [] ratio = new double[ratioNames.length];
				ArrayList <Double> [] ratiolist = new ArrayList [ratioNames.length];
				for(int i=0;i<ratiolist.length;i++){
					ratiolist[i] = new ArrayList <Double>();
				}
				LabelQuanUnit unit = null;
				int feasNum = 0;
				while(ppit.hasNext()){
					
					unit = ppit.next();
					double [] pepR = unit.getRatio();
					for(int i=0;i<pepR.length;i++){
						if(pepR[i]>0) ratiolist[i].add(pepR[i]);
					}
//					if(feas.getPresentFeasNum()>feasNum){
//						feasNum = feas.getPresentFeasNum();
//					}
				}
				for(int i=0;i<ratio.length;i++){
					ratio[i] = MathTool.getMedianInDouble(ratiolist[i]);
					if(ratio[i]==0)
						PepRatioStat[i][5]++;
					else if(ratio[i]<range[i][0])
						PepRatioStat[i][0]++;
					else if(ratio[i]>=range[i][0] && ratio[i]<range[i][1])
						PepRatioStat[i][1]++;
					else if(ratio[i]>=range[i][1] && ratio[i]<range[i][2])
						PepRatioStat[i][2]++;
					else if(ratio[i]>=range[i][2] && ratio[i]<range[i][3])
						PepRatioStat[i][3]++;
					else if(ratio[i]>=range[i][3])
						PepRatioStat[i][4]++;
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append(unit.getSequence()).append("\t");
				
				for(int i=0;i<ratio.length;i++){
					sb.append(ratio[i]).append("\t");
				}
					
				double [] intens = unit.getIntensity();
				for(int j=0;j<intens.length;j++){
					sb.append(df4.format(intens[j])).append("\t");	
				}
//				sb.append(feasNum).append("\t");
				String deleRef = unit.getDelegateRef();
				if(deleRef==null){
					sb.append(unit.getRefs());
				}else{
					sb.append(deleRef);
				}
				
				this.writer.addContent(sb.toString(), 1, f2);
			}
		}
	}

	public void writeSummary() throws RowsExceededException, WriteException{

		if(grad){
			this.addPeptideInfo();
		}
		
		ExcelFormat f1 = new ExcelFormat(true, 0);
		writer.addBlankRow(0);
		writer.addBlankRow(0);
		writer.addBlankRow(0);
		writer.addTitle("----------Summary----------\n", 0, f1);	
		
		writer.addBlankRow(1);
		writer.addBlankRow(1);
		writer.addBlankRow(1);
		writer.addTitle("----------Summary----------\n", 1, f1);		

		for(int i=0;i<ratioNames.length;i++){

			StringBuilder sb1 = new StringBuilder();
			sb1.append(ratioNames[i]).append("\t").append("Protein Number\t").append("Percent\n");
			
			sb1.append("Ratio: >=200%\t").append(ProRatioStat[i][4]).append("\t")
				.append(dfPer.format((float)ProRatioStat[i][4]/totalPro)).append("\n");			
			sb1.append("Ratio: 120%~200%\t").append(ProRatioStat[i][3]).append("\t")
				.append(dfPer.format((float)ProRatioStat[i][3]/totalPro)).append("\n");			
			sb1.append("Ratio: 80%~120%\t").append(ProRatioStat[i][2]).append("\t")
				.append(dfPer.format((float)ProRatioStat[i][2]/totalPro)).append("\n");			
			sb1.append("Ratio: 50%~80%\t").append(ProRatioStat[i][1]).append("\t")
				.append(dfPer.format((float)ProRatioStat[i][1]/totalPro)).append("\n");			
			sb1.append("Ratio: <50%\t").append(ProRatioStat[i][0]).append("\t")
				.append(dfPer.format((float)ProRatioStat[i][0]/totalPro)).append("\n");
			sb1.append("Not quantified\t").append(ProRatioStat[i][5]).append("\t")
				.append(dfPer.format((float)ProRatioStat[i][5]/totalPro)).append("\n");
			sb1.append("Total Number\t").append(String.valueOf(totalPro)).append("\t")
				.append("100%\n");
				
			writer.addContent(sb1.toString(), 0, f1);
			writer.addBlankRow(0);
			
			StringBuilder sb2 = new StringBuilder();
			sb2.append(ratioNames[i]).append("\t").append("Peptide Number\t").append("Percent\n");		
			
			sb2.append("Ratio: >=200%\t").append(PepRatioStat[i][4]).append("\t")
				.append(dfPer.format((float)PepRatioStat[i][4]/totalPair)).append("\n");
			sb2.append("Ratio: 120%~200%\t").append(PepRatioStat[i][3]).append("\t")
				.append(dfPer.format((float)PepRatioStat[i][3]/totalPair)).append("\n");
			sb2.append("Ratio: 80%~120%\t").append(PepRatioStat[i][2]).append("\t")
				.append(dfPer.format((float)PepRatioStat[i][2]/totalPair)).append("\n");
			sb2.append("Ratio: 50%~80%\t").append(PepRatioStat[i][1]).append("\t")
				.append(dfPer.format((float)PepRatioStat[i][1]/totalPair)).append("\n");
			sb2.append("Ratio: <50%\t").append(PepRatioStat[i][0]).append("\t")
				.append(dfPer.format((float)PepRatioStat[i][0]/totalPair)).append("\n");
			sb2.append("Not quantified\t").append(PepRatioStat[i][5]).append("\t")
				.append(dfPer.format((float)PepRatioStat[i][5]/totalPair)).append("\n");
			sb2.append("Total Number\t").append(String.valueOf(totalPair)).append("\t")
				.append("100%\n");
			
			writer.addContent(sb2.toString(), 1, f1);
			writer.addBlankRow(1);
		}

	}
	
	private static void batchWrite(String in, double normalratio) throws Exception{
		File[] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(!files[i].getName().endsWith("pxml"))
				continue;
			
			String pxml = files[i].getAbsolutePath();
			String xls = pxml.replace("pxml", "quan.xls");
			
			double [] theRatio = new double [1];
			Arrays.fill(theRatio, normalratio);
			
			LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(pxml);
			reader.setTheoryRatio(theRatio);
			
			QuanResultWriter writer = new QuanResultWriter(xls, reader.type, true, reader.mods, 
					reader.ratioNames, theRatio);
			QuanResult[] reslist = reader.getAllResult(false, true, null);
			PeptidePair[] pairs = reader.getAllSelectedPairs();
//			System.out.println(reslist.length);
			for(int j=0;j<reslist.length;j++){
				writer.write(reslist[j]);
			}
			for(int j=0;j<pairs.length;j++){
				if(!pairs[j].isAccurate()){
					writer.write(pairs[j]);
				}
			}
			writer.writeSummary();
			writer.close();
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String in = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\20130805_4p_di-labeling_CID_quantification_1_1-2.pxml";
		String out = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\20130805_4p_di-labeling_CID_quantification_1_1-2.xls";
		
//		QuanResultWriter.batchWrite("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\iden", 1.0);
		QuanResultWriter.batchWrite("I:\\SCX-online labeling", 1.0);

/*		LabelFeaturesXMLReader reader1 = new LabelFeaturesXMLReader("H:\\WFJ_mutiple_label\\turnover\\1024\\pxml\\0_3_6.pxml");
		LabelFeaturesXMLReader reader2 = new LabelFeaturesXMLReader("H:\\WFJ_mutiple_label\\turnover\\1024\\pxml\\0_3_48.pxml");
		LabelFeaturesXMLReader reader3 = new LabelFeaturesXMLReader("H:\\WFJ_mutiple_label\\turnover\\1024\\pxml\\6_12_24.pxml");
		LabelFeaturesXMLReader reader4 = new LabelFeaturesXMLReader("H:\\WFJ_mutiple_label\\turnover\\1024\\pxml\\12_24_48.pxml");
		
		HashSet <String> set = new HashSet <String>();
		HashSet <String> pepset = new HashSet <String>();
		reader1.setTheoryRatio(theRatio);
		QuanResult[] reslist1 = reader1.getAllResult(false, false, null);
		System.out.println(reslist1.length);
		for(int i=0;i<reslist1.length;i++){
			String ref = reslist1[i].getDeleRef();
			if(!ref.contains("REV_")){
				set.add(ref);
				PeptidePair [] pps = reslist1[i].getPepFeatures();
				for(int j=0;j<pps.length;j++){
					pepset.add(pps[j].getSequence());
				}
			}
		}
		reader1.close();
		reader2.setTheoryRatio(theRatio);
		QuanResult[] reslist2 = reader2.getAllResult(false, false, null);
		System.out.println(reslist2.length);
		for(int i=0;i<reslist2.length;i++){
			String ref = reslist2[i].getDeleRef();
			if(!ref.contains("REV_")){
				set.add(ref);
				PeptidePair [] pps = reslist2[i].getPepFeatures();
				for(int j=0;j<pps.length;j++){
					pepset.add(pps[j].getSequence());
				}
			}
		}
		reader2.close();
		reader3.setTheoryRatio(theRatio);
		QuanResult[] reslist3 = reader3.getAllResult(false, false, null);
		System.out.println(reslist3.length);
		for(int i=0;i<reslist3.length;i++){
			String ref = reslist3[i].getDeleRef();
			if(!ref.contains("REV_")){
				set.add(ref);
				PeptidePair [] pps = reslist3[i].getPepFeatures();
				for(int j=0;j<pps.length;j++){
					pepset.add(pps[j].getSequence());
				}
			}
		}
		reader3.close();
		reader4.setTheoryRatio(theRatio);
		QuanResult[] reslist4 = reader4.getAllResult(false, false, null);
		System.out.println(reslist4.length);
		for(int i=0;i<reslist4.length;i++){
			String ref = reslist4[i].getDeleRef();
			if(!ref.contains("REV_")){
				set.add(ref);
				PeptidePair [] pps = reslist4[i].getPepFeatures();
				for(int j=0;j<pps.length;j++){
					pepset.add(pps[j].getSequence());
				}
			}
		}
		reader4.close();
		System.out.println(set.size()+"\t"+pepset.size());
*/
//		reader.setProNameAccesser();
//ProteinNameAccesser accesser = reader.accesser;
//System.out.println(accesser.getAllKeys().length);
//System.out.println(accesser.getAllKeys()[0]);

/*		String in = "D:\\quatification_data_standard\\1_4_1\\1024\\1_4_1.pxml";
		String out = "D:\\quatification_data_standard\\1_4_1\\1024\\1_4_1.2.xls";
		LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(in);
		reader.readAllFeatures();
		double [] theRatio = new double []{4.0, 1.0, 0.25};
//		Arrays.fill(theRatio, 1.0);
		reader.setTheoryRatio(theRatio);
		
		QuanResultWriter writer = new QuanResultWriter(out, reader.type, true, reader.mods, 
				reader.ratioNames, theRatio);
		QuanResult[] reslist = reader.getAllResult(false, true, null);
		System.out.println(reslist.length);
		for(int i=0;i<reslist.length;i++){
			String ref = reslist[i].getDeleRef();
			if(!ref.contains("REV_IPI"))
				writer.write(reslist[i]);
		}
		writer.writeSummary();
		writer.close();
*/		
	}
}
