/* 
 ******************************************************************************
 * File: GlycoLQResultWriter.java * * * Created on 2011-4-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFreePairXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;

import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2011-4-7, 18:45:50
 */
public class GlycoLQResultWriter {
	
	private ExcelWriter writer;
	private LabelType type;
	private boolean useRelaProRatio;
	
	private String [] ratioNames;
	private double [][] range;
	private double [][] abRatio;
	private double [][] reRatio;

	private int totalPair;
	
	private HashMap <String, ArrayList <GlycoQuanResult>> pairMap;
	private HashMap <String, QuanResult> proQuanMap;
	private HashMap <String, Integer> glyFormMap;
	
	private ExcelFormat format = ExcelFormat.indexFormat;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_2;

	public GlycoLQResultWriter(String output, boolean useRelaProRatio, String relaProRatio,
			GlycoLPRowGetter getter, boolean normal, String [] ratioNames, 
			int [] outputRatio, double [] theoryRatio, double [] usedTheoryRatio)
	
		throws IOException, RowsExceededException, WriteException{
	
		this.type = getter.getType();
		this.writer = new ExcelWriter(output);
		
		int ratioNum = ratioNames.length;
		this.ratioNames = ratioNames;
		this.abRatio = new double [ratioNum][5];
		this.reRatio = new double [ratioNum][5];
		
		getter.setTheoryRatio(theoryRatio);
		
		this.glyFormMap = new HashMap <String, Integer>();
		
		this.range = new double [ratioNum][4];
		for(int i=0;i<usedTheoryRatio.length;i++){
			double tr = usedTheoryRatio[i];
			range[i][0] = tr*0.5;
			range[i][1] = tr*0.8;
			range[i][2] = tr*1.2;
			range[i][3] = tr*2.0;
		}
		
		this.pairMap = getter.getQuanMap();
		this.useRelaProRatio = useRelaProRatio;
		
		if(useRelaProRatio){
			
			this.proQuanMap = new HashMap <String, QuanResult>();
			
			QuanResult [] results = null;
			
			if(type==LabelType.LabelFree){
				
				try {
					
					LFreePairXMLReader reader = new LFreePairXMLReader(relaProRatio);
					results = reader.getAllResult(true, normal);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else{

				try {
					
					LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(relaProRatio);
					reader.setTheoryRatio(theoryRatio);
					results = reader.getAllResult(true, normal, outputRatio);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			for(int i=0;i<results.length;i++){
				String [] refs = results[i].getRefs();
				for(int j=0;j<refs.length;j++){
					proQuanMap.put(refs[j], results[i]);
				}
			}
		}
		
		this.addTitle();
		
	}

	public void addTitle() throws RowsExceededException, WriteException{
		
		StringBuilder sb = new StringBuilder();
		short [] used = type.getUsed();
		
		sb.append("Index\t");
		sb.append("Sequence\t");
		sb.append("Pep_Mass\t");
		sb.append("Glyco_Mass\t");
//		sb.append("HCD scans\t");
		
		/*for(int i=0;i<used.length;i++){
			sb.append("Glycans_Ratio_"+used[i]).append("\t");
		}*/
		
		for(int i=0;i<ratioNames.length;i++){
			sb.append("GlycoPep_"+ratioNames[i]).append("\t");
		}
		if(useRelaProRatio){
			for(int i=0;i<ratioNames.length;i++){
				sb.append("Relative_GlycoPep_"+ratioNames[i]).append("\t");
			}
			for(int i=0;i<ratioNames.length;i++){
				sb.append("GlycoPro_"+ratioNames[i]).append("\t");
			}
		}
		for(int i=0;i<used.length;i++){
			sb.append(type.getLabelName()+"_"+used[i]).append("\t");
		}

		sb.append("Glycan\t");
		sb.append("Score\t");
		sb.append("Rank\t");
		sb.append("Reference\t");
		sb.append("Site\t");
		
		writer.addTitle(sb.toString(), 0, format);
	}
	
	public void write() throws RowsExceededException, WriteException, IOException{

		Iterator <String> it = this.pairMap.keySet().iterator();
		int idx = 0;
		while(it.hasNext()){
			
			String key = it.next();
			ArrayList <GlycoQuanResult> list = pairMap.get(key);
			StringBuilder sb = new StringBuilder();
			sb.append(++idx);
			
			for(int i=0;i<list.size();i++){
				
				totalPair++;
				GlycoQuanResult pair = list.get(i);
				double [] ratios = pair.getRatio();
				
				for(int l=0;l<ratios.length;l++){
					if(ratios[l]<range[l][0])
						abRatio[l][0]++;
					else if(ratios[l]>=range[l][0] && ratios[l]<range[l][1])
						abRatio[l][1]++;
					else if(ratios[l]>=range[l][1] && ratios[l]<range[l][2])
						abRatio[l][2]++;
					else if(ratios[l]>=range[l][2] && ratios[l]<range[l][3])
						abRatio[l][3]++;
					else if(ratios[l]>=range[l][3])
						abRatio[l][4]++;
				}
				
				if(useRelaProRatio){
					
					HashMap <String, SimpleProInfo> proInfoMap = ((IGlycoPeptide)pair.getPeptide()).getProInfoMap();
					HashMap <String, SeqLocAround> slaMap = pair.getPeptide().getPepLocAroundMap();

					Iterator <String> proIt = proInfoMap.keySet().iterator();
					while(proIt.hasNext()){
						
						String prokey = proIt.next();
						SimpleProInfo info = proInfoMap.get(prokey);
						String ref = info.getRef();
						IGlycoPeptide peptide = (IGlycoPeptide) pair.getPeptide();
						
						if(this.proQuanMap.containsKey(ref)){
							
							SeqLocAround sla = slaMap.get(prokey);
							double [] proRatio = proQuanMap.get(ref).getRatio();
							
							sb.append("\t").append(peptide.getPepInfo());
							
							for(int j=0;j<ratios.length;j++){
								
								double rela = 0;
								if(proRatio[j]!=0){
									
									rela = ratios[j]/proRatio[j];
									
									if(rela<range[j][0])
										reRatio[j][0]++;
									else if(rela>=range[j][0] && rela<range[j][1])
										reRatio[j][1]++;
									else if(rela>=range[j][1] && rela<range[j][2])
										reRatio[j][2]++;
									else if(rela>=range[j][2] && rela<range[j][3])
										reRatio[j][3]++;
									else if(rela>=range[j][3])
										reRatio[j][4]++;
									
								}
								sb.append(df4.format(rela)).append("\t");
							}
							
							for(int j=0;j<proRatio.length;j++){
								sb.append(proRatio[j]).append("\t");
							}
							
							sb.append(pair.getGlycanInfo()).append(pair.getSiteInfo(info, sla)).append("\n");
							
						}else{
							
							int num = pair.getRatio().length*2;
							sb.append("\t").append(peptide.getPepInfo());
							for(int j=0;j<num;j++){
								sb.append("\t");
							}
							sb.append(pair.getGlycanInfo());
							sb.append(pair.getSiteInfo());							
						}
					}
					
				}else{
					
					sb.append("\t").append(pair.getPepInfo()).
						append(pair.getGlycanInfo()).append(pair.getSiteInfo()).append("\n");

				}
			}
			
			this.writer.addContent(sb.toString(), 0, format);
		}
		
		writeSummary();
	}

	/**
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 * 
	 */
	public void writeSummary() throws RowsExceededException, WriteException {
		
		ExcelFormat f1 = new ExcelFormat(true, 0);
		writer.addBlankRow(0);
		writer.addBlankRow(0);
		writer.addBlankRow(0);
		writer.addTitle("----------Summary----------\n", 0, f1);

		if(useRelaProRatio){
			
			for(int i=0;i<ratioNames.length;i++){

				StringBuilder sb = new StringBuilder();
				sb.append(ratioNames[i]+"\t").append("Absolute Ratio\t").append("Percent\t")
					.append("Relative Ratio\t").append("Percent\n");
				sb.append("Ratio: >=200%\t")
					.append(abRatio[i][4]).append("\t")
					.append(dfPer.format((float)abRatio[i][4]/totalPair)).append("\t")
					.append(reRatio[i][4]).append("\t")
					.append(dfPer.format((float)reRatio[i][4]/totalPair)).append("\n");
				sb.append("Ratio: 120%~200%\t")
					.append(abRatio[i][3]).append("\t")
					.append(dfPer.format((float)abRatio[i][3]/totalPair)).append("\t")
					.append(reRatio[i][3]).append("\t")
					.append(dfPer.format((float)reRatio[i][3]/totalPair)).append("\n");
				sb.append("Ratio: 80%~120%\t")
					.append(abRatio[i][2]).append("\t")
					.append(dfPer.format((float)abRatio[i][2]/totalPair)).append("\t")
					.append(reRatio[i][2]).append("\t")
					.append(dfPer.format((float)reRatio[i][2]/totalPair)).append("\n");
				sb.append("Ratio: 50%~80%\t")
					.append(abRatio[i][1]).append("\t")
					.append(dfPer.format((float)abRatio[i][1]/totalPair)).append("\t")
					.append(reRatio[i][1]).append("\t")
					.append(dfPer.format((float)reRatio[i][1]/totalPair)).append("\n");
				sb.append("Ratio: <50%\t")
					.append(abRatio[i][0]).append("\t")
					.append(dfPer.format((float)abRatio[i][0]/totalPair)).append("\t")
					.append(reRatio[i][0]).append("\t")
					.append(dfPer.format((float)reRatio[i][0]/totalPair)).append("\n");
				
				writer.addContent(sb.toString(), 0, f1);
				writer.addBlankRow(0);		
			}
			
		}else{
			
			for(int i=0;i<ratioNames.length;i++){

				StringBuilder sb1 = new StringBuilder();
				sb1.append(ratioNames[i]).append("\t").append("Peptide Number\t").append("Percent\n");
				
				sb1.append("Ratio: >=200%\t").append(abRatio[i][4]).append("\t")
					.append(dfPer.format((float)abRatio[i][4]/totalPair)).append("\n");			
				sb1.append("Ratio: 120%~200%\t").append(abRatio[i][3]).append("\t")
					.append(dfPer.format((float)abRatio[i][3]/totalPair)).append("\n");			
				sb1.append("Ratio: 80%~120%\t").append(abRatio[i][2]).append("\t")
					.append(dfPer.format((float)abRatio[i][2]/totalPair)).append("\n");			
				sb1.append("Ratio: 50%~80%\t").append(abRatio[i][1]).append("\t")
					.append(dfPer.format((float)abRatio[i][1]/totalPair)).append("\n");			
				sb1.append("Ratio: <50%\t").append(abRatio[i][0]).append("\t")
					.append(dfPer.format((float)abRatio[i][0]/totalPair)).append("\n");
				sb1.append("Total Number\t").append(String.valueOf(totalPair)).append("\t")
					.append("100%\n");
					
				writer.addContent(sb1.toString(), 0, f1);

			}
		}
	}

	/**
	 * @throws IOException 
	 * @throws WriteException 
	 * 
	 */
	public void close() throws WriteException, IOException {
		writer.close();
		System.gc();
	}
}
