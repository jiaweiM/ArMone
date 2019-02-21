/* 
 ******************************************************************************
 * File: WangDataProcessor2.java * * * Created on 2012-10-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.turnover.TurnoverFunction;
import cn.ac.dicp.gp1809.proteome.quant.turnover.TurnoverFunction2;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import cn.ac.dicp.gp1809.util.math.curvefit.CurveFitting;
import cn.ac.dicp.gp1809.util.math.curvefit.IFunction;

/**
 * @author ck
 *
 * @version 2012-10-23, 16:08:56
 */
public class WangDataProcessor2 {
	
	private HashMap <String, ArrayList<Double> []> map;
	private HashMap <String, Integer> countmap;
	private HashMap <String, ArrayList<Double> []> pepRatioMap;
	private HashMap <String, String> peppromap;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private static int iii = 0;
	
	public WangDataProcessor2(){
		this.map = new HashMap <String, ArrayList <Double>[]>();
		this.countmap = new HashMap <String, Integer>();
		this.pepRatioMap = new HashMap <String, ArrayList<Double>[]>();
		this.peppromap = new HashMap <String, String>();
	}

	public void read(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in);
		boolean begin = false;
		String [] line = null;
		ArrayList <String> namelist = new ArrayList <String>();
		int pepcount = 0;
		while((line=reader.readLine())!=null){
			if(begin){
				if(line.length>1){
					if(line[0].trim().length()>0){
						
						if(pepcount==0){
							namelist.add(line[1]);
						}else{
							for(int i=0;i<namelist.size();i++){
								String proteinName = namelist.get(i);
								if(countmap.containsKey(proteinName)){
									countmap.put(proteinName, countmap.get(proteinName)+pepcount);
								}else{
									countmap.put(proteinName, pepcount);
								}
							}
							namelist = new ArrayList <String>();
							namelist.add(line[1]);
							pepcount = 0;
						}

						if(map.containsKey(line[1])){
							
							ArrayList<Double> [] list = map.get(line[1]);
							double d1 = Double.parseDouble(line[3]);
							double d2 = Double.parseDouble(line[21]);
							double d3 = Double.parseDouble(line[31]);
							
							if(d1!=0 && d1!=1000){
								list[i1].add(d1);
							}
							if(d2!=0 && d2!=1000){
								list[i2].add(d2);
							}
							if(d3!=0 && d3!=1000){
								list[i3].add(d3);
							}
							
						}else{
							ArrayList<Double> [] list = new ArrayList [6];
							for(int i=0;i<list.length;i++){
								list[i] = new ArrayList <Double>();
							}
							double d1 = Double.parseDouble(line[3]);
							double d2 = Double.parseDouble(line[21]);
							double d3 = Double.parseDouble(line[31]);
							
							if(d1!=0 && d1!=1000){
								list[i1].add(d1);
							}
							if(d2!=0 && d2!=1000){
								list[i2].add(d2);
							}
							if(d3!=0 && d3!=1000){
								list[i3].add(d3);
							}
							map.put(line[1], list);
						}
						
					}else{
						pepcount++;
					}
				}else{
					for(int i=0;i<namelist.size();i++){
						String proteinName = namelist.get(i);
						if(countmap.containsKey(proteinName)){
							countmap.put(proteinName, countmap.get(proteinName)+pepcount);
						}else{
							countmap.put(proteinName, pepcount);
						}
					}
					break;
				}
			}else{
				if(line.length>0 && line[0].startsWith("Index")){
					begin = true;
				}
			}
		}
		reader.close();
	}

	public HashSet <String> readPepRatio(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		HashSet <String> pepset = new HashSet <String>();
		ExcelReader reader = new ExcelReader(in, new int []{0, 1});
		String [] line0 = null;
		int ccount = -1;
		while((line0=reader.readLine(0))!=null){
			if(line0.length>2){
				if(line0[1].startsWith("Sequence")){
					ccount = line0.length;
					reader.readLine(0);
					System.out.println(reader.readLine(0).length+"\t"+ccount);
				}
			}
			if(line0.length>1){
				if(countmap.containsKey(line0[1])){
					countmap.put(line0[1], countmap.get(line0[1])+1);
				}else{
					countmap.put(line0[1], 1);
				}
			}
		}
		String [] line = reader.readLine(1);
		int titlelength = line.length;
		while((line=reader.readLine(1))!=null && line.length==titlelength){
			
			pepset.add(line[0]);
			if(pepRatioMap.containsKey(line[0])){

				ArrayList<Double> [] list = pepRatioMap.get(line[0]);
				double d1 = Double.parseDouble(line[1]);
				double d2 = Double.parseDouble(line[10]);
				double d3 = Double.parseDouble(line[15]);
				
				if(d1!=0 && d1!=1000){
					list[i1].add(d1);
				}
				if(d2!=0 && d2!=1000){
					list[i2].add(d2);
				}
				if(d3!=0 && d3!=1000){
					list[i3].add(d3);
				}
				
			}else{
				ArrayList<Double> [] list = new ArrayList [6];
				for(int i=0;i<list.length;i++){
					list[i] = new ArrayList <Double>();
				}
				double d1 = Double.parseDouble(line[1]);
				double d2 = Double.parseDouble(line[10]);
				double d3 = Double.parseDouble(line[15]);
				
				if(d1!=0 && d1!=1000){
					list[i1].add(d1);
				}
				if(d2!=0 && d2!=1000){
					list[i2].add(d2);
				}
				if(d3!=0 && d3!=1000){
					list[i3].add(d3);
				}
				pepRatioMap.put(line[0], list);
				peppromap.put(line[0], line[23]);
			}
		}
		reader.close();
		
		return pepset;
	}
	
/*	public void calculatePro(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		int count1 = 0;
		int count2 = 0;
		int halfcount = 0;

		PrintWriter pw = new PrintWriter(out+".txt");
		pw.write("Reference\t0\t3\t6\t12\t24\t48\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf\n");
		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.map.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			String key = it.next();
			if(key.contains("CON_")) continue;

			ArrayList <Double> [] list = map.get(key);
			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAve(list[i]);
					ratios[i] = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
					sb.append(ratios[i]).append("\t");
				}else{
					ratios[i] = 0;
					sb.append("\t");
				}
			}
			if(ratios[0]<ratios[1]) ratios[0] = 0;

			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
//			pw.println(key.substring(0, key.indexOf("|"))+"\t"+uk);

			double [] linear = cal(ratios, times);
			count1++;
			
			if(linear[6]>2 || linear[8]<0.001 || linear[10]<4) continue;
			
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			
			sb2.append(key).append("\t");
			sb3.append(key).append("\t");

			for(int i=0;i<linear.length-1;i++){
				if(linear[i]==0 && i!=9) {
					sb2.append("\t");
					sb3.append("\t");
				}else {
					sb2.append(df4.format(linear[i])).append("\t");
					sb3.append(linear[i]).append("\t");
				}
			}

//			double half = -(Math.log(2.0)+linear[6])/linear[7];
			double dd = (linear[6]-2*linear[7])/(2*linear[6]-2*linear[7]);
			if(dd<=0) continue;
			
			count2++;
			double half = -Math.log(dd)/linear[8];
			sb2.append(df4.format(half)).append("\t");
			sb3.append(half).append("\t");
			
			if(half>0){
				if(linear[9]<=0.8) continue;
				writer.addContent(sb2.toString(), 0, ef);
				pw.write(sb3.toString()+"\n");
				halfcount++;
			}		
		}
		writer.close();
		pw.close();
		System.out.println(count1+"\t"+count2+"\t"+halfcount);
	}
*/	
	public void calculatePro2(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\tRSD\t3\tRSD\t6\tRSD\t12\tRSD\t24\tRSD\t48\tRSD\t" +
				"Quantification num\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		int count1 = 0;
		int count2 = 0;
		int halfcount = 0;

		PrintWriter pw = new PrintWriter(out+".txt");
		pw.write("Reference\t0\t3\t6\t12\t24\t48\tIdentification num\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf\n");
		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.map.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			String key = it.next();
			if(key.contains("CON_")) continue;

			ArrayList <Double> [] list = map.get(key);
			double [] ratios = new double [6];
			double [] rsds = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAveInDouble(list[i]);
					rsds[i] = MathTool.getRSDInDouble(list[i]);
					ratios[i] = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
					sb.append(ratios[i]).append("\t");
					
				}else{
					ratios[i] = 0;
					sb.append("\t");
				}
			}
			if(ratios[0]<ratios[1]) ratios[0] = 0;

			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
//			pw.println(key.substring(0, key.indexOf("|"))+"\t"+uk);
			
			int no0Count = 0;
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]>0){
					no0Count++;
				}
			}

			double [] linear = new double [11];
			if(no0Count==3){
				linear = cal2(ratios, times);
			}else if(no0Count>3){
				linear = cal(ratios, times);
				if(linear[7]<0) linear = cal2(ratios, times);
			}
			
			count1++;
			
			if(linear[6]>2 || linear[8]<0.001  || linear[8]>1 || linear[10]<3) continue;
			
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			
			sb2.append(key).append("\t");
			sb3.append(key).append("\t");

			for(int i=0;i<linear.length-1;i++){
				if(i<=5){
					if(linear[i]!=0){
						sb2.append(df4.format(linear[i])).append("\t");
						sb3.append(linear[i]).append("\t");
						sb2.append(rsds[i]).append("\t");
					}else{
						sb2.append("\t\t");
						sb3.append("\t");
					}
				}else if(i==6){
					sb2.append(countmap.get(key)).append("\t");
					sb3.append(countmap.get(key)).append("\t");
					sb2.append(linear[i]).append("\t");
					sb3.append(linear[i]).append("\t");
				}else{
					if(linear[i]==0 && i!=9) {
						sb2.append("\t");
						sb3.append("\t");
					}else {
						sb2.append(df4.format(linear[i])).append("\t");
						sb3.append(linear[i]).append("\t");
					}
				}
			}

//			double half = -(Math.log(2.0)+linear[6])/linear[7];
			double dd = (linear[6]-2*linear[7])/(2*linear[6]-2*linear[7]);
			if(dd<=0) continue;
			
			count2++;
			double half = -Math.log(dd)/linear[8];
			sb2.append(df4.format(half)).append("\t");
			sb3.append(half).append("\t");
			
			if(half>0){
				if(linear[9]<=0.8) continue;
				writer.addContent(sb2.toString(), 0, ef);
				pw.write(sb3.toString()+"\n");
				halfcount++;
			}		
		}
		writer.close();
		pw.close();
		System.out.println(count1+"\t"+count2+"\t"+halfcount);
	}

/*	public void calculatePep(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf", 0, ef);
		
		PrintWriter pw = new PrintWriter(out+".txt");
		pw.write("Reference\t0\t3\t6\t12\t24\t48\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf\n");

		
		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.pepRatioMap.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			String key = it.next();
			ArrayList <Double> [] list = pepRatioMap.get(key);

			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAve(list[i]);
					ratios[i] = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
					sb.append(ratios[i]).append("\t");
				}else{
					ratios[i] = 0;
					sb.append("\t");
				}
			}
			if(ratios[0]<ratios[1]) ratios[0] = 0;

			double [] linear = cal(ratios, times);
			if(linear[6]>2 || linear[8]<0.001 || linear[10]<4) continue;
			
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			
			sb2.append(key).append("\t");
			sb3.append(key).append("\t");

			for(int i=0;i<linear.length-1;i++){
				if(linear[i]==0 && i!=9) {
					sb2.append("\t");
					sb3.append("\t");
				}else {
					sb2.append(df4.format(linear[i])).append("\t");
					sb3.append(linear[i]).append("\t");
				}
			}

//			double half = -(Math.log(2.0)+linear[6])/linear[7];
			double dd = (linear[6]-2*linear[7])/(2*linear[6]-2*linear[7]);
			if(dd<=0) continue;
			
			double half = -Math.log(dd)/linear[8];
			sb2.append(df4.format(half)).append("\t");
			sb3.append(half).append("\t");
			
			if(half>0){
				if(linear[9]<=0.8) continue;
				writer.addContent(sb2.toString(), 0, ef);
				pw.write(sb3.toString()+"\n");
			}		
		}
		pw.close();
		writer.close();
	}
*/
	public void calculatePep2(String out)
			throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Sequence\tReference\tQuantification num\t0\t3\t6\t12\t24\t48\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf", 0, ef);
		
		PrintWriter pw = new PrintWriter(out+".txt");
		pw.write("Sequence\tReference\tIdentification num\t0\t3\t6\t12\t24\t48\tRIA0\tRIA1\tKloss\tCorrCoeff\tHalf\n");

		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.pepRatioMap.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			String key = it.next();
			ArrayList <Double> [] list = pepRatioMap.get(key);

			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAveInDouble(list[i]);
//					ratios[i] = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
//					sb.append(ratios[i]).append("\t");
					
					double rsd = MathTool.getRSDInDouble(list[i]);
					if(rsd>0.5){
						ratios[i] = 0;
						sb.append("\t");
					}else{
						ratios[i] = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
						sb.append(ratios[i]).append("\t");
					}
					
				}else{
					ratios[i] = 0;
					sb.append("\t");
				}
			}
			if(ratios[0]<ratios[1]) ratios[0] = 0;

			int no0Count = 0;
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]>0){
					no0Count++;
				}
			}
			
			double [] linear = new double [11];
			if(no0Count==3){
				linear = cal2(ratios, times);
			}else if(no0Count>3){
				linear = cal(ratios, times);
				if(linear[7]<0) linear = cal2(ratios, times);
			}
						
			if(linear[6]>2 || linear[8]<0.001 || linear[10]<3) continue;
			
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			
			sb2.append(key).append("\t");
			sb2.append(peppromap.get(key)).append("\t");
			sb2.append(countmap.get(key)).append("\t");
			sb3.append(key).append("\t");
			sb3.append(peppromap.get(key)).append("\t");
			
			for(int i=0;i<linear.length-1;i++){
				if(linear[i]==0 && i!=9) {
					sb2.append("\t");
					sb3.append("\t");
				}else {
					sb2.append(df4.format(linear[i])).append("\t");
					sb3.append(linear[i]).append("\t");
				}
			}

//			double half = -(Math.log(2.0)+linear[6])/linear[7];
			double dd = (linear[6]-2*linear[7])/(2*linear[6]-2*linear[7]);
			if(dd<=0) continue;
			
			double half = -Math.log(dd)/linear[8];
			sb2.append(df4.format(half)).append("\t");
			sb3.append(half).append("\t");
			
			if(half>0){
				if(linear[9]<=0.8) continue;
				writer.addContent(sb2.toString(), 0, ef);
				pw.write(sb3.toString()+"\n");
			}		
		}
		pw.close();
		writer.close();
	}

	private static double [] cal(double [] values, double [] times){
		
		ArrayList <Double> valuelist = new ArrayList <Double>();
		ArrayList <Double> timelist = new ArrayList <Double>();
		for(int i=0;i<values.length;i++){
			if(values[i]!=0){
				valuelist.add(values[i]);
				timelist.add(times[i]);
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
//			if(fit.getFitGoodness()==0)System.out.println("mipa"+"\t"+a+"\t"+b);
			
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
						
						double [] dd = new double [11];
						
						System.arraycopy(values, 0, dd, 0, values.length);
						dd[6] = ria0;
						dd[7] = ria1;
						dd[8] = kloss;
						dd[9] = yyr;
						dd[10] = valuelist.size();
						
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
						dd[6] = ria0;
						dd[7] = ria1;
						dd[8] = kloss;
						dd[9] = yyr;
						dd[10] = valuelist.size()-1;
						
						return dd;
						
					}				
					
				}else{
					
					double [] dd = new double [11];
					
					System.arraycopy(values, 0, dd, 0, values.length);
					dd[6] = ria0;
					dd[7] = ria1;
					dd[8] = kloss;
					dd[9] = yyr;
					dd[10] = valuelist.size();
					
					return dd;
				}
			}else{
				
				double [] dd = new double [11];
				
				System.arraycopy(values, 0, dd, 0, values.length);
				dd[6] = ria0;
				dd[7] = ria1;
				dd[8] = kloss;
				dd[9] = yyr;
				dd[10] = valuelist.size();
				
				return dd;
			}
		}
		iii++;
		return new double [11];
	}

	private static double [] cal2(double [] values, double [] times){
		
		ArrayList <Double> valuelist = new ArrayList <Double>();
		ArrayList <Double> timelist = new ArrayList <Double>();
		for(int i=0;i<values.length;i++){
			if(values[i]!=0){
				valuelist.add(values[i]);
				timelist.add(times[i]);
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
//			if(fit.getFitGoodness()==0)System.out.println("mipa"+"\t"+a+"\t"+b);
			
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
						dd[6] = ria0;
						dd[7] = ria1;
						dd[8] = kloss;
						dd[9] = yyr;
						dd[10] = valuelist.size();
						
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
						dd[6] = ria0;
						dd[7] = ria1;
						dd[8] = kloss;
						dd[9] = yyr;
						dd[10] = valuelist.size()-1;
						
						return dd;
						
					}				
					
				}else{
					
					double [] dd = new double [11];
					
					System.arraycopy(values, 0, dd, 0, values.length);
					dd[6] = ria0;
					dd[7] = ria1;
					dd[8] = kloss;
					dd[9] = yyr;
					dd[10] = valuelist.size();
					
					return dd;
				}
			}else{
				
				double [] dd = new double [11];
				
				System.arraycopy(values, 0, dd, 0, values.length);
				dd[6] = ria0;
				dd[7] = ria1;
				dd[8] = kloss;
				dd[9] = yyr;
				dd[10] = valuelist.size();
				
				return dd;
			}
		}
		iii++;
		return new double [11];
	}
	
	public static void cocomplex(String coco, String pro, String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Swissprot\tGene\tHalf\tAverage\tRSD", 0, ef);
		
		HashMap <String, Double> halfmap = new HashMap <String, Double>();
		BufferedReader proreader = new BufferedReader(new FileReader(pro));
		String proline = proreader.readLine();
		while((proline=proreader.readLine())!=null){
			String [] ss = proline.split("\t");
			String swiss = ProteinSequence.getSWISS(ss[0]);
			if(swiss.length()>1){
				double half = Double.parseDouble(ss[ss.length-1]);
				swiss = swiss.substring(0, 6);
				halfmap.put(swiss, half);
			}
		}
		proreader.close();
		System.out.println(halfmap.size());
		
		BufferedReader cocoreader = new BufferedReader(new FileReader(coco));
		String cocoline = cocoreader.readLine();
		while((cocoline = cocoreader.readLine())!=null){
			String [] ss = cocoline.split("\t");
			String [] sss = ss[2].split(",");
			String [] genes = ss[3].split(",");
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			ArrayList <Double> list = new ArrayList <Double>();
			for(int i=0;i<sss.length;i++){
				if(halfmap.containsKey(sss[i])){
					sb1.append(sss[i]).append(",");
					sb2.append(genes[i]).append(",");
					sb3.append(halfmap.get(sss[i])).append(",");
					list.add(halfmap.get(sss[i]));
				}
			}
			StringBuilder sb = new StringBuilder();
			if(list.size()>1){
				sb.append(sb1.substring(0, sb1.length()-1)).append("\t");
				sb.append(sb2.substring(0, sb2.length()-1)).append("\t");
				sb.append(sb3.substring(0, sb3.length()-1)).append("\t");
				sb.append(MathTool.getAveInDouble(list)).append("\t");
				sb.append(MathTool.getRSDInDouble(list)).append("\t");
				writer.addContent(sb.toString(), 0, ef);
			}
		}
		cocoreader.close();
		writer.close();
	}

	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

		WangDataProcessor2 processor = new WangDataProcessor2();
		
//		processor.read("J:\\Data\\sixple\\turnover\\dat\\final\\0_3_6.xls", 0, 1, 2);
//		processor.read("J:\\Data\\sixple\\turnover\\dat\\final\\0_3_48.xls", 0, 1, 5);
//		processor.read("J:\\Data\\sixple\\turnover\\dat\\final\\6_12_24.xls", 3, 4, 2);
//		processor.read("J:\\Data\\sixple\\turnover\\dat\\final\\12_24_48.xls", 3, 4, 5);
//		processor.calculatePro2("J:\\Data\\sixple\\turnover\\dat\\final\\final2.pros.xls");
		
		processor.readPepRatio("J:\\Data\\sixple\\turnover\\dat\\final\\0_3_6.xls", 0, 1, 2);
		processor.readPepRatio("J:\\Data\\sixple\\turnover\\dat\\final\\0_3_48.xls", 0, 1, 5);
		processor.readPepRatio("J:\\Data\\sixple\\turnover\\dat\\final\\6_12_24.xls", 3, 4, 2);
		processor.readPepRatio("J:\\Data\\sixple\\turnover\\dat\\final\\12_24_48.xls", 3, 4, 5);
		processor.calculatePep2("J:\\Data\\sixple\\turnover\\dat\\final\\final.peps.xls");
		
//		WangDataProcessor2.cocomplex("H:\\WFJ_mutiple_label\\turnover\\20121205net\\coco.txt", 
//				"H:\\WFJ_mutiple_label\\turnover\\20121205net\\pro.txt", 
//				"H:\\WFJ_mutiple_label\\turnover\\20121205net\\cocomplex.xls");
	}

}
