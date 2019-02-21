/* 
 ******************************************************************************
 * File: MutilDataProcessor.java * * * Created on 2013-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelFeaturesXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.label.IO.LabelQuanUnit;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.proteome.quant.turnover.TurnoverDrawer;
import cn.ac.dicp.gp1809.proteome.quant.turnover.TurnoverFunction;
import cn.ac.dicp.gp1809.proteome.quant.turnover.TurnoverFunction2;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import cn.ac.dicp.gp1809.util.math.curvefit.CurveFitting;
import cn.ac.dicp.gp1809.util.math.curvefit.IFunction;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @deprecated
 * @version 2013-3-21, 8:25:53
 */
public class MutilDataProcessor {
	
	private double [] totalTimeList;
	private HashMap <String, ArrayList<Double> []> map;
	private HashMap <String, Integer> procountmap;
	private HashMap <String, Integer> pepcountmap;
	private HashMap <String, ArrayList<Double> []> pepRatioMap;
	private HashMap <String, HashSet<String>> peppromap;
	
	private int ria0Id;
	private int ria1Id;
	private int klossId;
	private int corrId;
	private int halfId;
	
	private String out;
	private String pro_png;
	private String pep_png;
	
	private TurnoverDrawer drawer;
	private boolean drawPic;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public MutilDataProcessor(double [] totalTimeList, String out, boolean drawPic){
		this.totalTimeList = totalTimeList;
		this.map = new HashMap <String, ArrayList <Double>[]>();
		this.procountmap = new HashMap <String, Integer>();
		this.pepcountmap = new HashMap <String, Integer>();
		this.pepRatioMap = new HashMap <String, ArrayList<Double>[]>();
		this.peppromap = new HashMap <String, HashSet<String>>();
		
		this.ria0Id = totalTimeList.length;
		this.ria1Id = totalTimeList.length+1;
		this.klossId = totalTimeList.length+2;
		this.corrId = totalTimeList.length+3;
		this.halfId = totalTimeList.length+4;
		
		this.out = out;
		this.drawer = new TurnoverDrawer(totalTimeList);
		this.drawPic = drawPic;
		if(drawPic){
			File f1 = new File(out.replace(".xls", "_pro"));
			if(!f1.exists()){
				f1.mkdir();
				this.pro_png = f1.getAbsolutePath();
			}else{
				this.pro_png = f1.getAbsolutePath();
			}
			File f2 = new File(out.replace(".xls", "_pep"));
			if(!f2.exists()){
				f2.mkdir();
				this.pep_png = f2.getAbsolutePath();
			}else{
				this.pep_png = f2.getAbsolutePath();
			}
		}
	}
	
	public void read(HashMap <String, double[]> map) throws Exception{
		Iterator <String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			double [] value = map.get(key);
			this.read(key, value);
		}
	}
	
	public void read(String in, double [] timeList) throws Exception{
		
		int [] idx = new int [timeList.length];
		for(int j=0;j<timeList.length;j++){
			for(int k=0;k<totalTimeList.length;k++){
				if(timeList[j]==totalTimeList[k]){
					idx[j] = k;
				}
			}
		}
		
		LabelFeaturesXMLReader reader = new LabelFeaturesXMLReader(in);
		QuanResult [] results = reader.getAllResult(false, false, null);
		for(int i=0;i<results.length;i++){
			
			LabelQuanUnit [] units = results[i].getUnits();
			ArrayList <Double> [] riaList = new ArrayList [timeList.length];
			for(int j=0;j<riaList.length;j++){
				riaList[j] = new ArrayList <Double>();
			}
			int usePepCount = 0;
			for(int j=0;j<units.length;j++){
				double [] ria = units[j].getRIA();
				for(int k=0;k<ria.length;k++){
					if(ria[k]>0) riaList[k].add(ria[k]);
				}
				boolean use = false;
				for(int k=0;k<ria.length;k++){
					if(ria[k]>0){
						use = true;
						break;
					}
				}
				if(use){
					usePepCount++;
					String sequence = units[j].getSequence();
					if(this.pepRatioMap.containsKey(sequence)){
						ArrayList<Double> [] list = pepRatioMap.get(sequence);
						for(int k=0;k<timeList.length;k++){
							list[idx[k]].add(ria[k]);
						}
						this.pepcountmap.put(sequence, this.pepcountmap.get(sequence)+1);
					}else{
						ArrayList <Double> [] list = new ArrayList [totalTimeList.length];
						for(int k=0;k<list.length;k++){
							list[k] = new ArrayList <Double>();
						}
						for(int k=0;k<timeList.length;k++){
							list[idx[k]].add(ria[k]);
						}
						this.pepRatioMap.put(sequence, list);
						this.pepcountmap.put(sequence, 1);
					}
					String [] refs = results[i].getRefs();
					for(String ref : refs){
						if(this.peppromap.containsKey(sequence)){
							this.peppromap.get(sequence).add(ref);
						}else{
							HashSet <String> set = new HashSet <String>();
							set.add(ref);
							this.peppromap.put(sequence, set);
						}
					}
				}
			}
			double [] prorias = new double [timeList.length];
			for(int j=0;j<timeList.length;j++){
				prorias[j] = MathTool.getMedianInDouble(riaList[j]);
			}
			String [] refs = results[i].getRefs();
			for(String ref : refs){
				if(this.map.containsKey(ref)){
					ArrayList<Double> [] list = map.get(ref);
					for(int j=0;j<timeList.length;j++){
						list[idx[j]].add(prorias[j]);
					}
					this.procountmap.put(ref, this.procountmap.get(ref)+usePepCount);
				}else{
					ArrayList <Double> [] list = new ArrayList [totalTimeList.length];
					for(int j=0;j<list.length;j++){
						list[j] = new ArrayList <Double>();
					}
					for(int j=0;j<timeList.length;j++){
						list[idx[j]].add(prorias[j]);
					}
					this.map.put(ref, list);
					this.procountmap.put(ref, usePepCount);
				}
			}
		}
//		System.out.println("map\t"+pepRatioMap.size());
	}
	
	public void write() throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out, new String []{"Protein", "Peptide"});
		ExcelFormat ef = ExcelFormat.normalFormat;
		StringBuilder protitlesb = new StringBuilder();
		protitlesb.append("Reference\t");
		for(int i=0;i<this.totalTimeList.length;i++){
			protitlesb.append(this.totalTimeList[i]).append(" h\t");
			protitlesb.append("RSD\t");
		}
		protitlesb.append("Quantification num\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf");
		writer.addTitle(protitlesb.toString(), 0, ef);
		
		int proid = 1;
		HashSet <String> usedset = new HashSet <String>();
		Iterator <String> proit = this.map.keySet().iterator();
		while(proit.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			String key = proit.next();

			ArrayList <Double> [] list = map.get(key);
			double [] ratios = new double [totalTimeList.length];
			double [] rsds = new double [totalTimeList.length];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					ratios[i] = MathTool.getAveInDouble(list[i]);
					rsds[i] = MathTool.getRSDInDouble(list[i]);
					sb.append(ratios[i]).append("\t");
					
				}else{
					ratios[i] = 0;
					sb.append("\t");
				}
			}

			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
			
			int no0Count = 0;
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]>0){
					no0Count++;
				}
			}

			double [] linear = new double [totalTimeList.length+5];
			if(no0Count==3){
				linear = calculate2(ratios);
			}else if(no0Count>3){
				linear = calculate(ratios);
				if(linear[ria1Id]<0) linear = calculate2(ratios);
			}

			if(linear[ria0Id]>2 || linear[klossId]<0.001  || linear[klossId]>1) continue;
			
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append(key).append("\t");

			for(int i=0;i<linear.length-1;i++){
				if(i<this.totalTimeList.length){
					if(linear[i]!=0){
						sb2.append(linear[i]).append("\t");
						sb2.append(rsds[i]).append("\t");
					}else{
						sb2.append("\t\t");
					}
				}else if(i==ria0Id){
					sb2.append(procountmap.get(key)).append("\t");
					sb2.append(df4.format(linear[i])).append("\t");
				}else{
					if(linear[i]==0 && i!=corrId) {
						sb2.append("\t");
					}else {
						sb2.append(df4.format(linear[i])).append("\t");
					}
				}
			}

			double dd = (linear[ria0Id]-2*linear[ria1Id])/(2*linear[ria0Id]-2*linear[ria1Id]);
			if(dd<=0) continue;
			
			double half = -Math.log(dd)/linear[klossId];
			sb2.append(df4.format(half)).append("\t");
			
			if(half>0){
				if(linear[corrId]<=0.8) continue;
				writer.addContent(sb2.toString(), 0, ef);
				if(this.drawPic)
					this.drawer.drawPro(proid++, ratios, key, linear[ria0Id], linear[ria1Id], linear[klossId], 
						half, pro_png);
			}
		}
		
		StringBuilder peptitlesb = new StringBuilder();
		peptitlesb.append("Sequence\tReference\tQuantification num\t");
		for(int i=0;i<this.totalTimeList.length;i++){
			peptitlesb.append(this.totalTimeList[i]).append(" h\t");
		}
		peptitlesb.append("RIA0\tRIA1\tKloss\tCorrCoeff\tHalf");
		writer.addTitle(peptitlesb.toString(), 1, ef);

		int pepid = 1;
		Iterator <String> pepit = this.pepRatioMap.keySet().iterator();
		while(pepit.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			String key = pepit.next();
			ArrayList <Double> [] list = pepRatioMap.get(key);

			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					ratios[i] = MathTool.getAveInDouble(list[i]);
					if(ratios[i]==0){
						sb.append("\t");
					}else{
						sb.append(ratios[i]).append("\t");
					}
					
				}else{
					ratios[i] = 0;
					sb.append("\t");
				}
			}

			int no0Count = 0;
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]>0){
					no0Count++;
				}
			}
			
			double [] linear = new double [11];
			if(no0Count==3){
				linear = calculate2(ratios);
			}else if(no0Count>3){
				linear = calculate(ratios);
				if(linear[ria1Id]<0) linear = calculate2(ratios);
			}
						
			if(linear[ria0Id]>2 || linear[klossId]<0.001 || linear[klossId]>1) continue;
			
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append(key).append("\t");
			HashSet <String> refs = peppromap.get(key);
			for(String ref : refs){
				sb2.append(ref).append(";");
			}
			sb2.deleteCharAt(sb2.length()-1);
			sb2.append("\t");
			sb2.append(pepcountmap.get(key)).append("\t");

			for(int i=0;i<linear.length-1;i++){
				if(linear[i]==0 && i!=corrId) {
					sb2.append("\t");
				}else {
					sb2.append(df4.format(linear[i])).append("\t");
				}
			}

			double dd = (linear[ria0Id]-2*linear[ria1Id])/(2*linear[ria0Id]-2*linear[ria1Id]);
			if(dd<=0) continue;
			
			double half = -Math.log(dd)/linear[klossId];
			sb2.append(df4.format(half)).append("\t");
			
			if(half>0){
				if(linear[corrId]<=0.8) continue;
				writer.addContent(sb2.toString(), 1, ef);
				if(this.drawPic)
					this.drawer.drawPro(pepid++, ratios, key, linear[ria0Id], linear[ria1Id], linear[klossId], 
						half, pep_png);
			}		
		}
		
		writer.close();
//		System.out.println(count1+"\t"+count2+"\t"+halfcount);
	}

	private double [] calculate(double [] values){
		
		ArrayList <Double> valuelist = new ArrayList <Double>();
		ArrayList <Double> timelist = new ArrayList <Double>();
		for(int i=0;i<values.length;i++){
			if(values[i]!=0){
				valuelist.add(values[i]);
				timelist.add(totalTimeList[i]);
			}
		}

		if(valuelist.size()>=3){
			
			double [] calValues = new double[valuelist.size()];
			double [] calTimes = new double[timelist.size()];
			
			for(int i=0;i<calValues.length;i++){
				calValues[i] = valuelist.get(i);
				calTimes[i] = timelist.get(i);
			}
			
			IFunction function = new TurnoverFunction();
			CurveFitting fit = new CurveFitting(calTimes, calValues, function);
			fit.fit();
			
			double [] para = fit.getBestParams();

			double ria0 = para[0];
			double ria1 = para[1];
			double kloss = para[2];
			double yyr = fit.getFitGoodness();
			
			if(valuelist.size()>=4){
				
				if(yyr<=0.9 || ria1<-3){

					int tp = -1;
					for(int i=0;i<timelist.size();i++){
						
						double [] newValues = new double [timelist.size()-1];
						double [] newTimes = new double [timelist.size()-1];
						
						for(int j=0;j<newValues.length;j++){
							if(j<i){
								newValues[j] = valuelist.get(j);
								newTimes[j] = timelist.get(j);
							}else{
								newValues[j] = valuelist.get(j+1);
								newTimes[j] = timelist.get(j+1);
							}
						}
						
						CurveFitting fit2 = new CurveFitting(newTimes, newValues, function);
						fit2.fit();
						
						double [] para2 = fit2.getBestParams();

						double newria0 = para2[0];
						double newria1 = para2[1];
						double newkloss = para2[2];
						double newyyr = fit2.getFitGoodness();

						if(newyyr>yyr){
							yyr = newyyr;
							ria0 = newria0;
							ria1 = newria1;
							kloss = newkloss;
							tp = i;
						}
					}
					
					if(tp==-1){
						
						double [] dd = new double [totalTimeList.length+5];
						
						System.arraycopy(values, 0, dd, 0, values.length);
						dd[ria0Id] = ria0;
						dd[ria1Id] = ria1;
						dd[klossId] = kloss;
						dd[corrId] = yyr;
						dd[halfId] = valuelist.size();
						
						return dd;
						
					}else{
						
						double [] dd = new double [11];
						for(int i=0;i<values.length;i++){
							if(values[i]==valuelist.get(tp)){
								dd[i] = 0;
							}else{
								dd[i] = values[i];
							}
						}
						dd[ria0Id] = ria0;
						dd[ria1Id] = ria1;
						dd[klossId] = kloss;
						dd[corrId] = yyr;
						dd[halfId] = valuelist.size()-1;
						
						return dd;
						
					}				
					
				}else{
					
					double [] dd = new double [11];
					
					System.arraycopy(values, 0, dd, 0, values.length);
					dd[ria0Id] = ria0;
					dd[ria1Id] = ria1;
					dd[klossId] = kloss;
					dd[corrId] = yyr;
					dd[halfId] = valuelist.size();
					
					return dd;
				}
			}else{
				
				double [] dd = new double [11];
				
				System.arraycopy(values, 0, dd, 0, values.length);
				dd[ria0Id] = ria0;
				dd[ria1Id] = ria1;
				dd[klossId] = kloss;
				dd[corrId] = yyr;
				dd[halfId] = valuelist.size();
				
				return dd;
			}
		}
		return new double [11];
	}

	private double [] calculate2(double [] values){
		
		ArrayList <Double> valuelist = new ArrayList <Double>();
		ArrayList <Double> timelist = new ArrayList <Double>();
		for(int i=0;i<values.length;i++){
			if(values[i]!=0){
				valuelist.add(values[i]);
				timelist.add(totalTimeList[i]);
			}
		}

		if(valuelist.size()>=3){
			
			double [] calValues = new double[valuelist.size()];
			double [] calTimes = new double[timelist.size()];
			
			for(int i=0;i<calValues.length;i++){
				calValues[i] = valuelist.get(i);
				calTimes[i] = timelist.get(i);
			}
			
			IFunction function = new TurnoverFunction2();
			CurveFitting fit = new CurveFitting(calTimes, calValues, function);
			fit.fit();
			
			double [] para = fit.getBestParams();

			double ria0 = para[0];
			double ria1 = 0.0;
			double kloss = para[1];
			double yyr = fit.getFitGoodness();
			
			if(valuelist.size()>=4){
				
				if(yyr<=0.9 || ria1<-3){

					int tp = -1;
					for(int i=0;i<timelist.size();i++){
						
						double [] newValues = new double [timelist.size()-1];
						double [] newTimes = new double [timelist.size()-1];
						
						for(int j=0;j<newValues.length;j++){
							if(j<i){
								newValues[j] = valuelist.get(j);
								newTimes[j] = timelist.get(j);
							}else{
								newValues[j] = valuelist.get(j+1);
								newTimes[j] = timelist.get(j+1);
							}
						}
						
						CurveFitting fit2 = new CurveFitting(newTimes, newValues, function);
						fit2.fit();
						
						double [] para2 = fit2.getBestParams();

						double newria0 = para2[0];
						double newria1 = 0.0;
						double newkloss = para2[1];
						double newyyr = fit2.getFitGoodness();

						if(newyyr>yyr){
							yyr = newyyr;
							ria0 = newria0;
							ria1 = newria1;
							kloss = newkloss;
							tp = i;
						}
					}
					
					if(tp==-1){
						
						double [] dd = new double [11];
						
						System.arraycopy(values, 0, dd, 0, values.length);
						dd[ria0Id] = ria0;
						dd[ria1Id] = ria1;
						dd[klossId] = kloss;
						dd[corrId] = yyr;
						dd[halfId] = valuelist.size();
						
						return dd;
						
					}else{
						
						double [] dd = new double [11];
						for(int i=0;i<values.length;i++){
							if(values[i]==valuelist.get(tp)){
								dd[i] = 0;
							}else{
								dd[i] = values[i];
							}
						}
						dd[ria0Id] = ria0;
						dd[ria1Id] = ria1;
						dd[klossId] = kloss;
						dd[corrId] = yyr;
						dd[halfId] = valuelist.size()-1;
						
						return dd;
						
					}				
					
				}else{
					
					double [] dd = new double [11];
					
					System.arraycopy(values, 0, dd, 0, values.length);
					dd[ria0Id] = ria0;
					dd[ria1Id] = ria1;
					dd[klossId] = kloss;
					dd[corrId] = yyr;
					dd[halfId] = valuelist.size();
					
					return dd;
				}
			}else{
				
				double [] dd = new double [11];
				
				System.arraycopy(values, 0, dd, 0, values.length);
				dd[ria0Id] = ria0;
				dd[ria1Id] = ria1;
				dd[klossId] = kloss;
				dd[corrId] = yyr;
				dd[halfId] = valuelist.size();
				
				return dd;
			}
		}
		return new double [11];
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		MutilDataProcessor processor = new MutilDataProcessor(new double[]{0, 3, 6, 12, 24, 48},
				"J:\\Data\\sixple\\turnover\\dat\\final4\\turnover.xls", true);
		processor.read("J:\\Data\\sixple\\turnover\\dat\\0_3_6_peak\\0_3_6.pxml", new double []{0, 3, 6});
		processor.read("J:\\Data\\sixple\\turnover\\dat\\0_3_48_peak\\0_3_48.pxml", new double []{0, 3, 48});
		processor.read("J:\\Data\\sixple\\turnover\\dat\\6_12_24_peak\\6_12_24.pxml", new double []{12, 24, 6});
		processor.read("J:\\Data\\sixple\\turnover\\dat\\12_24_48_peak\\12_24_48.pxml", new double []{12, 24, 48});
		processor.write();
	}

}
