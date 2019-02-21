/* 
 ******************************************************************************
 * File: WangDataProcessor.java * * * Created on 2012-8-16
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
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;
import cn.ac.dicp.gp1809.util.math.curvefit.CurveFitting;
import cn.ac.dicp.gp1809.util.math.curvefit.IFunction;
import cn.ac.dicp.gp1809.util.math.curvefit.SLineFunction;
import flanagan.analysis.Regression;

/**
 * @author JiaweiMao
 * @version Jan 19, 2016, 9:54:08 AM
 */
public class WangDataProcessor {
	
	private HashMap <String, ArrayList<Double> []> map;
	private HashMap <String, ArrayList<Double> []> pepRatioMap;
	private HashMap <String, double []> lightmap1;
	private HashMap <String, double []> heavymap1;
	private HashMap <String, double []> lightmap2;
	private HashMap <String, double []> heavymap2;
	private HashMap <String, ArrayList <String>> promap;
	private HashMap <String, String> peppromap;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private static int iii = 0;
	
	public WangDataProcessor(){
		this.map = new HashMap <String, ArrayList <Double>[]>();
		this.lightmap1 = new HashMap <String, double []>();
		this.heavymap1 = new HashMap <String, double []>();
		this.lightmap2 = new HashMap <String, double []>();
		this.heavymap2 = new HashMap <String, double []>();
		this.promap = new HashMap <String, ArrayList <String>>();
		this.peppromap = new HashMap <String, String>();
		this.pepRatioMap = new HashMap <String, ArrayList<Double>[]>();
	}
	
	public void read(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in);
		boolean begin = false;
		String [] line = null;
		while((line=reader.readLine())!=null){
			if(begin){
				if(line.length>0 && line[0].trim().length()>0){
					
					if(line.length>0 && line[0].startsWith("-"))
						break;
					
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
				}
			}else{
				if(line.length>0 && line[0].startsWith("Index")){
					begin = true;
				}
			}
		}
		reader.close();
	}
	
	public void readPepRatio(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();
		int titlelength = line.length;
		while((line=reader.readLine())!=null && line.length==titlelength){
			
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
			}
		}
		reader.close();
	}
	
	public void readPep1(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();

		while((line=reader.readLine())!=null){

			if(line.length>0 && line[0].trim().length()>0){
				
				if(line.length>0 && line[0].startsWith("---"))
					break;
				
				String key = line[0].substring(2, line[0].length()-2);
				this.peppromap.put(key, line[22]);
				
				if(lightmap1.containsKey(key)){
					
					double [] lightlist = lightmap1.get(key);
					double [] heavylist = heavymap1.get(key);
					
					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;

					lightmap1.put(key, lightlist);
					heavymap1.put(key, heavylist);
					
				}else{
					
					double [] lightlist = new double [6];
					double [] heavylist = new double [6];

					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;
					
					lightmap1.put(key, lightlist);
					heavymap1.put(key, heavylist);
				}
			}
		}
		reader.close();
	}
	
	public void readPep2(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();

		while((line=reader.readLine())!=null){

			if(line.length>0 && line[0].trim().length()>0){
				
				if(line.length>0 && line[0].startsWith("---"))
					break;
				
				String key = line[0].substring(2, line[0].length()-2);
				this.peppromap.put(key, line[22]);
				
				if(lightmap2.containsKey(key)){
					
					double [] lightlist = lightmap2.get(key);
					double [] heavylist = heavymap2.get(key);
					
					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;
					
					lightmap2.put(key, lightlist);
					heavymap2.put(key, heavylist);

				}else{
					
					double [] lightlist = new double [6];
					double [] heavylist = new double [6];

					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;
					
					lightmap2.put(key, lightlist);
					heavymap2.put(key, heavylist);
				}
			}
		}
		reader.close();
	}

	public void readPro1(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();

		while((line=reader.readLine())!=null){

			if(line.length>0 && line[0].trim().length()>0){
				
				if(line.length>0 && line[0].startsWith("---"))
					break;
				
				String key = line[0].substring(2, line[0].length()-2);
				String ref = line[22];
				if(this.promap.containsKey(ref)){
					this.promap.get(ref).add(key);
				}else{
					ArrayList <String> list = new ArrayList <String>();
					list.add(key);
					this.promap.put(ref, list);
				}
				
				if(lightmap1.containsKey(key)){
					
					double [] lightlist = lightmap1.get(key);
					double [] heavylist = heavymap1.get(key);
					
					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;

					lightmap1.put(key, lightlist);
					heavymap1.put(key, heavylist);
					
				}else{
					
					double [] lightlist = new double [6];
					double [] heavylist = new double [6];

					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;
					
					lightmap1.put(key, lightlist);
					heavymap1.put(key, heavylist);
				}
			}
		}
		reader.close();
	}

	public void readPro2(String in, int i1, int i2, int i3) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(in, 1);
		String [] line = reader.readLine();

		while((line=reader.readLine())!=null){

			if(line.length>0 && line[0].trim().length()>0){
				
				if(line.length>0 && line[0].startsWith("---"))
					break;
				
				String key = line[0].substring(2, line[0].length()-2);
				String ref = line[22];
				if(this.promap.containsKey(ref)){
					this.promap.get(ref).add(key);
				}else{
					ArrayList <String> list = new ArrayList <String>();
					list.add(key);
					this.promap.put(ref, list);
				}
				
				if(lightmap2.containsKey(key)){
					
					double [] lightlist = lightmap2.get(key);
					double [] heavylist = heavymap2.get(key);
					
					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;

					lightmap2.put(key, lightlist);
					heavymap2.put(key, heavylist);
					
				}else{
					
					double [] lightlist = new double [6];
					double [] heavylist = new double [6];

					double d1 = line[16].length()==0? 0 : Double.parseDouble(line[16]);
					double d2 = line[17].length()==0? 0 : Double.parseDouble(line[17]);
					double d3 = line[18].length()==0? 0 : Double.parseDouble(line[18]);
					double d4 = line[19].length()==0? 0 : Double.parseDouble(line[19]);
					double d5 = line[20].length()==0? 0 : Double.parseDouble(line[20]);
					double d6 = line[21].length()==0? 0 : Double.parseDouble(line[21]);

					lightlist[i1]=d1;
					lightlist[i2]=d3;
					lightlist[i3]=d5;
					
					heavylist[i1]=d2;
					heavylist[i2]=d4;
					heavylist[i3]=d6;
					
					lightmap2.put(key, lightlist);
					heavymap2.put(key, heavylist);
				}
			}
		}
		reader.close();
	}

	@SuppressWarnings("finally")
	public void calculate(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\ta\tb\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		
		Iterator <String> it = this.map.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			String key = it.next();
			sb.append(key).append("\t");
			int count = 0;
			ArrayList<Double> [] list = map.get(key);
			ArrayList <Double> timelist = new ArrayList <Double>();
			ArrayList <Double> ratiolist = new ArrayList <Double>();
			
			if(list[0].size()>0){
				double ratio = MathTool.getAveInDouble(list[0]);
				sb.append(ratio).append("\t");
				sb2.append(ratio).append("\t");
			}else{
				sb.append("\t");
				sb2.append("\t");
			}
			
			for(int i=1;i<list.length;i++){
				if(list[i].size()>0){
					count++;
					double ratio = MathTool.getAveInDouble(list[i]);
					ratiolist.add(ratio);
					sb.append(ratio).append("\t");
					sb2.append(ratio).append("\t");
					switch (i){
					case 0:
						timelist.add(0.0);
						break;
					
					case 1:
						timelist.add(3.0);
						break;
					
					case 2:
						timelist.add(6.0);
						break;
						
					case 3:
						timelist.add(12.0);
						break;
					
					case 4:
						timelist.add(24.0);
						break;
					
					case 5:
						timelist.add(48.0);
						break;
					}
				}else{
					sb.append("\t");
					sb2.append("\t");
				}
			}
			
			String uk = sb2.toString();
			if(usedset.contains(uk)){
				continue;
			}
			
			usedset.add(uk);
			
			if(count>=3){
				
				double [] ratios = new double [timelist.size()];
				double [] times = new double [timelist.size()];
				for(int i=0;i<ratios.length;i++){
					ratios[i] = ratiolist.get(i);
					times[i] = timelist.get(i);
//					System.out.println(ratios[i]+"\t"+times[i]);
				}
//				System.out.println(count);
				
				try{
					
					Regression reg = new Regression(times, ratios);
//					reg.setYscaleFactor(1);
//					reg.exponentialMultiple(1);
					reg.exponentialSimple();
					
					double a = reg.getBestEstimates()[1];
					double b = -reg.getBestEstimates()[0];
					double yyr = reg.getYYcorrCoeff();

					if(count>=4 && yyr<0.9){
						
						String recal = this.reCal(timelist, ratiolist, yyr, sb.toString());
						if(recal!=null){
							writer.addContent(recal, 0, ef);
							
						}else{
							
							sb.append(df4.format(a)).append("\t").append(df4.format(b)).append("\t").append(df4.format(yyr)).append("\t");
							double half = Math.log(a)/b;
							sb.append(df4.format(half));
							writer.addContent(sb.toString(), 0, ef);
						}
					}else{
						sb.append(df4.format(a)).append("\t").append(df4.format(b)).append("\t").append(df4.format(yyr)).append("\t");
						double half = Math.log(a)/b;
						sb.append(df4.format(half));
						writer.addContent(sb.toString(), 0, ef);
					}

				}finally{
					continue;
				}
			}
		}
		writer.close();
	}
	
	public void calculateLinear(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\ta\tb\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		int count = 0;
		int halfcount = 0;

		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.map.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			String key = it.next();
			ArrayList <Double> [] list = map.get(key);

			double ratio = 0;
			if(list[0].size()>0){
				ratio = MathTool.getAveInDouble(list[0]);
				ratio = Double.parseDouble(df4.format(ratio/(ratio+1.0)));
				sb.append(ratio).append("\t");
				sb3.append(ratio).append("\t");
			}else{
				sb.append("\t");
				sb3.append("\t");
			}
			
			double [] ratios = new double [6];
			for(int i=1;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAveInDouble(list[i]);
					ratioi = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
					ratios[i] = Math.log(ratioi);
					sb.append(ratioi).append("\t");
					sb3.append(ratios[i]).append("\t");
				}else{
					ratios[i] = 0;
					sb.append("\t");
					sb3.append("\t");
				}
			}

			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
			
			double [] newRatios = new double [6];
			for(int i=1;i<ratios.length;i++){

				if(ratios[i]>ratios[i-1] && ratios[i-1]!=0){
					
					newRatios[i] = 0;
//					if(key.startsWith("IPI:IPI00012535.1"))
//					System.out.println(key+"\t"+newRatios[i]+"\t"+i);
				}else{
					newRatios[i] = ratios[i];
//					if(key.startsWith("IPI:IPI00012535.1"))
//						System.out.println(key+"\t"+newRatios[i]+"\t"+i);
				}
			}
			
//			double [] linear = calLinear(ratios, times);
			double [] linear = calLinearSelf(newRatios, times);
			if(linear[9]==0) continue;
			count++;
			StringBuilder sb2 = new StringBuilder();
			sb2.append(key).append("\t");
			if(ratio==0 || ratio<Math.exp(linear[1])) sb2.append("\t");
			else sb2.append(ratio).append("\t");
			for(int i=1;i<7;i++){
				if(linear[i]==0) sb2.append("\t");
				else sb2.append(Math.exp(linear[i])).append("\t");
			}
			sb2.append(linear[7]).append("\t");
			sb2.append(linear[8]).append("\t");
//			double half = -(Math.log(2.0)+linear[6])/linear[7];
			double half = -(Math.log(2.0))/linear[7];
			sb2.append(half).append("\t");
			if(half>0 && linear[7]<0){
				if(linear[8]<=0.8 && linear[9]==3) continue;
				writer.addContent(sb2.toString(), 0, ef);
				halfcount++;
			}		
		}
		writer.close();
	}
	
	public void calculatePepSD(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\ta\tb\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		int count = 0;
		int halfcount = 0;

		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.pepRatioMap.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			String key = it.next();
			ArrayList <Double> [] list = pepRatioMap.get(key);

			double ratio = 0;
			if(list[0].size()>0){
				ratio = MathTool.getAveInDouble(list[0]);
				ratio = Double.parseDouble(df4.format(ratio/(ratio+1.0)));
				sb.append(ratio).append("\t");
				sb3.append(ratio).append("\t");
			}else{
				sb.append("\t");
				sb3.append("\t");
			}
			
			double [] ratios = new double [6];
			for(int i=1;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAveInDouble(list[i]);
					ratioi = Double.parseDouble(df4.format(ratioi/(ratioi+1.0)));
					ratios[i] = Math.log(ratioi);
					sb.append(ratioi).append("\t");
					sb3.append(ratios[i]).append("\t");
				}else{
					ratios[i] = 0;
					sb.append("\t");
					sb3.append("\t");
				}
			}

			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
			
			double [] newRatios = new double [6];
			for(int i=1;i<ratios.length;i++){

				if(ratios[i]>ratios[i-1] && ratios[i-1]!=0){
					
					newRatios[i] = 0;
//					if(key.startsWith("IPI:IPI00012535.1"))
//					System.out.println(key+"\t"+newRatios[i]+"\t"+i);
				}else{
					newRatios[i] = ratios[i];
//					if(key.startsWith("IPI:IPI00012535.1"))
//						System.out.println(key+"\t"+newRatios[i]+"\t"+i);
				}
			}
			
//			double [] linear = calLinear(ratios, times);
			double [] linear = calLinearSelf(newRatios, times);
			if(linear[9]==0) continue;
			count++;
			StringBuilder sb2 = new StringBuilder();
			sb2.append(key).append("\t");
			if(ratio==0 || ratio<Math.exp(linear[1])) sb2.append("\t");
			else sb2.append(ratio).append("\t");
			for(int i=1;i<7;i++){
				if(linear[i]==0) sb2.append("\t");
				else sb2.append(Math.exp(linear[i])).append("\t");
			}
			sb2.append(linear[7]).append("\t");
			sb2.append(linear[8]).append("\t");
//			double half = -(Math.log(2.0)+linear[6])/linear[7];
			double half = -(Math.log(2.0))/linear[7];
			sb2.append(half).append("\t");
			if(half>0 && linear[7]<0){
				if(linear[8]<=0.8 && linear[9]==3) continue;
				writer.addContent(sb2.toString(), 0, ef);
				halfcount++;
			}		
		}
		writer.close();
	}
	
	public void calculateLinearWith0(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\ta\tb\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		int count = 0;
		int halfcount = 0;

		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.map.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			String key = it.next();
			ArrayList <Double> [] list = map.get(key);

			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAveInDouble(list[i]);
					ratios[i] = Math.log(ratioi);
					sb.append(ratioi).append("\t");
					sb3.append(ratios[i]).append("\t");
				}else{
					sb.append("\t");
					sb3.append("\t");
				}
			}
			
			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
			
			double [] linear = calLinearSelf(ratios, times);
			if(linear[8]==0) continue;
			count++;
			StringBuilder sb2 = new StringBuilder();
			sb2.append(key).append("\t");
			for(int i=0;i<7;i++){
				if(linear[i]==0) sb2.append("\t");
				else sb2.append(Math.exp(linear[i])).append("\t");
			}
			sb2.append(linear[7]).append("\t");
			sb2.append(linear[8]).append("\t");
			double half = -linear[6]/linear[7];
			sb2.append(half).append("\t");
			if(half>0 && linear[7]<0){
				writer.addContent(sb2.toString(), 0, ef);
				halfcount++;
			}		
		}
		System.out.println(count+"\t"+halfcount+"\t"+iii);
		writer.close();
	}
	
	public void calculateLinearAll(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\ta\tb\tCorrCoeff\tHalf", 0, ef);
		HashSet <String> usedset = new HashSet <String>();
		int count = 0;
		int halfcount = 0;

		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		Iterator <String> it = this.map.keySet().iterator();
		while(it.hasNext()){
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb3 = new StringBuilder();
			String key = it.next();
			ArrayList <Double> [] list = map.get(key);

			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(list[i].size()>0){
					double ratioi = MathTool.getAveInDouble(list[i]);
					ratios[i] = Math.log(ratioi);
					sb.append(ratioi).append("\t");
					sb3.append(ratios[i]).append("\t");
				}else{
					sb.append("\t");
					sb3.append("\t");
				}
			}
			
			String uk = sb.toString();
			if(usedset.contains(uk)){
				continue;
			}
			usedset.add(uk);
			
			double [] linear = calLinearSelf(ratios, times);
			if(linear[8]==0){
				
				StringBuilder sb2 = new StringBuilder();
				sb2.append(key).append("\t");
				for(int i=0;i<ratios.length;i++){
					if(ratios[i]==0) sb2.append("\t");
					else sb2.append(Math.exp(ratios[i])).append("\t");
				}
				sb2.append("0\t0\t0\t");
				writer.addContent(sb2.toString(), 0, ef);
				
			}else{
				
				count++;
				StringBuilder sb2 = new StringBuilder();
				sb2.append(key).append("\t");
				for(int i=0;i<7;i++){
					if(linear[i]==0) sb2.append("\t");
					else sb2.append(Math.exp(linear[i])).append("\t");
				}
				sb2.append(linear[7]).append("\t");
				sb2.append(linear[8]).append("\t");
				double half = -linear[6]/linear[7];
				sb2.append(half).append("\t");
				if(half>0 && linear[7]<0){
					writer.addContent(sb2.toString(), 0, ef);
					halfcount++;
				}	
			}
		}
		System.out.println(count+"\t"+halfcount+"\t"+iii);
		writer.close();
	}
	
	public void calculateLinearManual(String in, String out) throws IOException, JXLException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		writer.addTitle("Reference\t0\t3\t6\t12\t24\t48\ta\tb\tCorrCoeff\tHalf", 0, ef);

		int count = 0;
		int halfcount = 0;
		int zero = 0;
		double [] times = new double [] {0, 3, 6, 12, 24, 48};
		
		ExcelReader reader = new ExcelReader(in);
		String [] line = null;
		while((line=reader.readLine())!=null){
			double [] ratios = new double [6];
			for(int i=0;i<ratios.length;i++){
				if(line[i+1]!=null && line[i+1].length()>0){
					ratios[i] = Double.parseDouble(line[i+1]);
					ratios[i] = Math.log(ratios[i]);
				}
			}
			double [] linear = calLinearSelf(ratios, times);
			if(linear[8]==0){
				zero++;
/*				StringBuilder sb2 = new StringBuilder();
				sb2.append(line[0]).append("\t");
				for(int i=0;i<ratios.length;i++){
					if(ratios[i]==0) sb2.append("\t");
					else sb2.append(Math.exp(ratios[i])).append("\t");
				}
				sb2.append("0\t0\t0\t");
				writer.addContent(sb2.toString(), 0, ef);
*/				
			}else{
				
				count++;
				StringBuilder sb2 = new StringBuilder();
				sb2.append(line[0]).append("\t");
				for(int i=0;i<7;i++){
					if(linear[i]==0) sb2.append("\t");
					else sb2.append(Math.exp(linear[i])).append("\t");
				}
				sb2.append(linear[7]).append("\t");
				sb2.append(linear[8]).append("\t");
				double half = -linear[6]/linear[7];
				sb2.append(half).append("\t");
				if(half>0 && linear[7]<0){
					writer.addContent(sb2.toString(), 0, ef);
					halfcount++;
				}	
			}
		}
		System.out.println(count+"\t"+halfcount+"\t"+iii+"\t"+zero);
		writer.close();
	}
	
	public static void compare(String s1, String s2) throws IOException, JXLException{
		
		PrintWriter pw = new PrintWriter("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\com.txt");
		pw.println("Reference\tNoZero\tWithZero");
		ExcelReader r1 = new ExcelReader(s1);
		HashMap <String, String> m1 = new HashMap <String, String>();
		String [] l1 = r1.readLine();
		while((l1=r1.readLine())!=null){
			m1.put(l1[0], l1[l1.length-1]);
		}
		r1.close();
		
		ExcelReader r2 = new ExcelReader(s2);
		HashMap <String, String> m2 = new HashMap <String, String>();
		String [] l2 = r2.readLine();
		while((l2=r2.readLine())!=null){
			m2.put(l2[0], l2[l2.length-1]);
		}
		r2.close();
		
		HashSet <String> keyset = new HashSet <String>();
		keyset.addAll(m1.keySet());
		keyset.addAll(m2.keySet());
		
		Iterator <String> it = keyset.iterator();
		while(it.hasNext()){
			String key = it.next();
			if(m1.containsKey(key) && m2.containsKey(key)){
				pw.println(key+"\t"+m1.get(key)+"\t"+m2.get(key));
			}
		}
		pw.close();
	}

	@SuppressWarnings("finally")
	public void calculatePep(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Peptide\t0\t3\t6\t12\t24\t48\ta\tb\tcoe\t0\t3\t6\t12\t24\t48\ta\tb\tcoe\thalf";
		writer.addTitle(title, 0, ef);
		System.out.println(this.lightmap1.size()+"\t"+this.lightmap2.size());
		HashSet <String> keyset = new HashSet <String>();
		keyset.addAll(this.lightmap1.keySet());
		keyset.addAll(this.lightmap2.keySet());
		System.out.println(keyset.size());
		
		Iterator <String> it = keyset.iterator();
		int misscount = 0;
		int misslight = 0;
		int missheavy = 0;
		int [] countlist = new int[25];
//		PrintWriter pw = new PrintWriter("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\peps_all.txt");
		double [] times = new double []{0, 3, 6, 12, 24, 48};
		while(it.hasNext()){
			
			String key = it.next();
			double [] light1 = new double[6];
			double [] light2 = new double[6];
			double [] heavy1 = new double[6];
			double [] heavy2 = new double[6];

			if(lightmap1.containsKey(key)){light1 = lightmap1.get(key);}
			if(lightmap2.containsKey(key)){light2 = lightmap2.get(key);}
			if(heavymap1.containsKey(key)){heavy1 = heavymap1.get(key);}
			if(heavymap2.containsKey(key)){heavy2 = heavymap2.get(key);}
			
			double kl1 = light1[0]*light2[0]==0 ? 0 : light2[0]/light1[0];
			double kl2 = light1[1]*light2[1]==0 ? 0 : light2[1]/light1[1];
			double kl3 = light1[2]*light2[2]==0 ? 0 : light2[2]/light1[2];
			double kl4 = light1[3]*light2[3]==0 ? 0 : light2[3]/light1[3];
			double kl5 = light1[4]*light2[4]==0 ? 0 : light2[4]/light1[4];
			double kl6 = light1[5]*light2[5]==0 ? 0 : light2[5]/light1[5];
			
			double kh1 = heavy1[0]*heavy2[0]==0 ? 0 : heavy2[0]/heavy1[0];
			double kh2 = heavy1[1]*heavy2[1]==0 ? 0 : heavy2[1]/heavy1[1];
			double kh3 = heavy1[2]*heavy2[2]==0 ? 0 : heavy2[2]/heavy1[2];
			double kh4 = heavy1[3]*heavy2[3]==0 ? 0 : heavy2[3]/heavy1[3];
			double kh5 = heavy1[4]*heavy2[4]==0 ? 0 : heavy2[4]/heavy1[4];
			double kh6 = heavy1[5]*heavy2[5]==0 ? 0 : heavy2[5]/heavy1[5];
			
			ArrayList <Double> listn = new ArrayList <Double>();
			ArrayList <Double> listm = new ArrayList <Double>();
			
			if(kl1*kl3!=0) {listn.add(kl1/kl3);}
			if(kl2*kl3!=0) {listn.add(kl2/kl3);}
			if(kl4*kl6!=0) {listn.add(kl6/kl4);}
			if(kl5*kl6!=0) {listn.add(kl6/kl5);}
			
			if(kh1*kh3!=0) {listn.add(kh1/kh3);}
			if(kh2*kh3!=0) {listn.add(kh2/kh3);}
			if(kh4*kh6!=0) {listn.add(kh6/kh4);}
			if(kh5*kh6!=0) {listn.add(kh6/kh5);}
			
			if(kl1*kl6!=0) {listm.add(kl6/kl1);}
			if(kl2*kl6!=0) {listm.add(kl6/kl2);}
			if(kl3*kl4!=0) {listm.add(kl4/kl3);}
			if(kl3*kl5!=0) {listm.add(kl5/kl3);}
			
			if(kh1*kh6!=0) {listm.add(kh6/kh1);}
			if(kh2*kh6!=0) {listm.add(kh6/kh2);}
			if(kh3*kh4!=0) {listm.add(kh4/kh3);}
			if(kh3*kh5!=0) {listm.add(kh5/kh3);}
			
			double mk = 0.57145;
			double nk = 0.9448;
			
			if(listm.size()!=0) mk = MathTool.getMedianInDouble(listm);
			if(listn.size()!=0) nk = MathTool.getMedianInDouble(listn);
			
			double [] flight = new double [6];
			flight[0] = this.getValue(light1[0], 1, light2[0], 1);
			flight[1] = this.getValue(light1[1], 1, light2[1], 1);
			flight[2] = this.getValue(light1[2], 1, light2[2], nk);
			flight[3] = this.getValue(light1[3], mk, light2[3], nk);
			flight[4] = this.getValue(light1[4], mk, light2[4], nk);
			flight[5] = this.getValue(light1[5], mk, light2[5], 1);
			
			double [] fheavy = new double [6];
			fheavy[0] = this.getValue(heavy1[0], 1, heavy2[0], 1);
			fheavy[1] = this.getValue(heavy1[1], 1, heavy2[1], 1);
			fheavy[2] = this.getValue(heavy1[2], 1, heavy2[2], nk);
			fheavy[3] = this.getValue(heavy1[3], mk, heavy2[3], nk);
			fheavy[4] = this.getValue(heavy1[4], mk, heavy2[4], nk);
			fheavy[5] = this.getValue(heavy1[5], mk, heavy2[5], 1);

/*			int countlight = 0;
			int countheavy = 0;
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			for(int i=0;i<flight.length;i++){
				sb.append(flight[i]).append("\t");
				if(flight[i]>0)countlight++;
			}
			for(int i=0;i<fheavy.length;i++){
				sb.append(fheavy[i]).append("\t");
				if(fheavy[i]>0)countheavy++;
			}
			
			if(countlight>=3 && countheavy>=3)
			writer.addContent(sb.toString(), 0, ef);
*/			

			try{
				
				double [] valueLight = this.calReg(flight, times);
				double [] valueHeavy = this.calReg(fheavy, times);
				
				StringBuilder lightsb = new StringBuilder();
				StringBuilder heavysb = new StringBuilder();

				if(valueLight!=null){
					for(int i=0;i<valueLight.length;i++){
						lightsb.append(valueLight[i]).append("\t");
					}
				}else{
					misslight++;
				}
				
				if(valueHeavy!=null){
					for(int i=0;i<valueHeavy.length;i++){
						heavysb.append(valueHeavy[i]).append("\t");
					}
				}else{
					missheavy++;
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append(key).append("\t");
				if(lightsb.length()>0 && heavysb.length()>0){
					sb.append(lightsb).append(heavysb);
					double half = (Math.log(valueHeavy[6])-Math.log(valueLight[6]))/(valueLight[7]-valueHeavy[7]);
					writer.addContent(sb.toString()+half, 0, ef);
				}

			}finally{
				continue;
			}
						
/*			StringBuilder sbl1 = new StringBuilder();
			StringBuilder sbl2 = new StringBuilder();
			StringBuilder sbh1 = new StringBuilder();
			StringBuilder sbh2 = new StringBuilder();
			int count24 = 0;
			for(int i=0;i<light1.length;i++){
				sbl1.append(light1[i]).append("\t");
				sbl2.append(light2[i]).append("\t");
				sbh1.append(heavy1[i]).append("\t");
				sbh2.append(heavy2[i]).append("\t");
				if(light1[i]>0)count24++;
				if(light2[i]>0)count24++;
				if(heavy1[i]>0)count24++;
				if(heavy2[i]>0)count24++;
			}
			countlist[count24]++;
			pw.write(key+"\t"+sbl1+sbl2+sbh1+sbh2+"\n");
//			pw.write(key+"\t"+sbl1+"\n");
//			pw.write(key+"\t"+sbl2+"\n");
//			pw.write(key+"\t"+sbh1+"\n");
//			pw.write(key+"\t"+sbh2+"\n");
*/			
		}
		System.out.println(misslight+"\t"+missheavy);
//		for(int i=0;i<countlist.length;i++){
//			System.out.println(i+"\t"+countlist[i]);
//		}
//		pw.close();
		writer.close();
	}
/*	
	@SuppressWarnings("finally")
	public void calculatePepLinear(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Peptide\tReference\tCount\t0\t3\t6\t12\t24\t48\ta\tb\tcoe\t0\t3\t6\t12\t24\t48\ta\tb\tcoe\thalf";
		writer.addTitle(title, 0, ef);
		System.out.println(this.lightmap1.size()+"\t"+this.lightmap2.size());
		HashSet <String> keyset = new HashSet <String>();
		keyset.addAll(this.lightmap1.keySet());
		keyset.addAll(this.lightmap2.keySet());
		System.out.println(keyset.size());
		
		Iterator <String> it = keyset.iterator();
		int misscount = 0;
		int misslight = 0;
		int missheavy = 0;
		int [] countlist = new int[25];
//		PrintWriter pw = new PrintWriter("H:\\WFJ_mutiple_label\\turnover\\new\\peps_all.txt");
		double [] times = new double []{0, 3, 6, 12, 24, 48};
		while(it.hasNext()){
			
			String key = it.next();
			
			String ref = this.peppromap.get(key);
			if(ref.contains("REV_") || ref.contains("CON_"))
				continue;
			
			double [] light1 = new double[6];
			double [] light2 = new double[6];
			double [] heavy1 = new double[6];
			double [] heavy2 = new double[6];

			int idCount = 0;
			if(lightmap1.containsKey(key)){light1 = lightmap1.get(key);idCount++;}
			if(lightmap2.containsKey(key)){light2 = lightmap2.get(key);idCount++;}
			if(heavymap1.containsKey(key)){heavy1 = heavymap1.get(key);}
			if(heavymap2.containsKey(key)){heavy2 = heavymap2.get(key);}
			
			double kl1 = light1[0]*light2[0]==0 ? 0 : Math.log(light2[0]/light1[0]);
			double kl2 = light1[1]*light2[1]==0 ? 0 : Math.log(light2[1]/light1[1]);
			double kl3 = light1[2]*light2[2]==0 ? 0 : Math.log(light2[2]/light1[2]);
			double kl4 = light1[3]*light2[3]==0 ? 0 : Math.log(light2[3]/light1[3]);
			double kl5 = light1[4]*light2[4]==0 ? 0 : Math.log(light2[4]/light1[4]);
			double kl6 = light1[5]*light2[5]==0 ? 0 : Math.log(light2[5]/light1[5]);
			
			double kh1 = heavy1[0]*heavy2[0]==0 ? 0 : Math.log(heavy2[0]/heavy1[0]);
			double kh2 = heavy1[1]*heavy2[1]==0 ? 0 : Math.log(heavy2[1]/heavy1[1]);
			double kh3 = heavy1[2]*heavy2[2]==0 ? 0 : Math.log(heavy2[2]/heavy1[2]);
			double kh4 = heavy1[3]*heavy2[3]==0 ? 0 : Math.log(heavy2[3]/heavy1[3]);
			double kh5 = heavy1[4]*heavy2[4]==0 ? 0 : Math.log(heavy2[4]/heavy1[4]);
			double kh6 = heavy1[5]*heavy2[5]==0 ? 0 : Math.log(heavy2[5]/heavy1[5]);
			
			ArrayList <Double> listn = new ArrayList <Double>();
			ArrayList <Double> listm = new ArrayList <Double>();
			
			if(kl1*kl3!=0) {listn.add(kl1-kl3);}
			if(kl2*kl3!=0) {listn.add(kl2-kl3);}
			if(kl4*kl6!=0) {listn.add(kl6-kl4);}
			if(kl5*kl6!=0) {listn.add(kl6-kl5);}
			
			if(kh1*kh3!=0) {listn.add(kh1-kh3);}
			if(kh2*kh3!=0) {listn.add(kh2-kh3);}
			if(kh4*kh6!=0) {listn.add(kh6-kh4);}
			if(kh5*kh6!=0) {listn.add(kh6-kh5);}
			
			if(kl1*kl6!=0) {listm.add(kl6-kl1);}
			if(kl2*kl6!=0) {listm.add(kl6-kl2);}
			if(kl3*kl4!=0) {listm.add(kl4-kl3);}
			if(kl3*kl5!=0) {listm.add(kl5-kl3);}
			
			if(kh1*kh6!=0) {listm.add(kh6-kh1);}
			if(kh2*kh6!=0) {listm.add(kh6-kh2);}
			if(kh3*kh4!=0) {listm.add(kh4-kh3);}
			if(kh3*kh5!=0) {listm.add(kh5-kh3);}
			
//			double mk = -0.562350808;
//			double nk = -0.057162863;
			double mk = -0.5301;
			double nk = -0.0029;

//			if(listm.size()!=0 && listn.size()!=0) pw.write(MathTool.getMedian(listm)+"\t"+MathTool.getMedian(listn)+"\n");
			if(listm.size()!=0) mk = MathTool.getAve(listm);
			if(listn.size()!=0) nk = MathTool.getAve(listn);
			
			double [] flight = new double [6];
			flight[0] = this.getValueIn(light1[0], 0, light2[0], 0);
			flight[1] = this.getValueIn(light1[1], 0, light2[1], 0);
			flight[2] = this.getValueIn(light1[2], 0, light2[2], nk);
			flight[3] = this.getValueIn(light1[3], mk, light2[3], nk);
			flight[4] = this.getValueIn(light1[4], mk, light2[4], nk);
			flight[5] = this.getValueIn(light1[5], mk, light2[5], 0);
			
			double [] fheavy = new double [6];
			fheavy[0] = this.getValueIn(heavy1[0], 0, heavy2[0], 0);
			fheavy[1] = this.getValueIn(heavy1[1], 0, heavy2[1], 0);
			fheavy[2] = this.getValueIn(heavy1[2], 0, heavy2[2], nk);
			fheavy[3] = this.getValueIn(heavy1[3], mk, heavy2[3], nk);
			fheavy[4] = this.getValueIn(heavy1[4], mk, heavy2[4], nk);
			fheavy[5] = this.getValueIn(heavy1[5], mk, heavy2[5], 0);

			int countlight = 0;
			int countheavy = 0;
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			for(int i=0;i<flight.length;i++){
				sb.append(flight[i]).append("\t");
				if(flight[i]>0)countlight++;
			}
			for(int i=0;i<fheavy.length;i++){
				sb.append(fheavy[i]).append("\t");
				if(fheavy[i]>0)countheavy++;
			}
			
			if(countlight>=3 && countheavy>=3)
			writer.addContent(sb.toString(), 0, ef);
			
			double [] valueLight = calLinearSelf(flight, times);
			double [] valueHeavy = calLinearSelf(fheavy, times);

			StringBuilder lightsb = new StringBuilder();
			StringBuilder heavysb = new StringBuilder();

			if(valueLight[7]<=0 || valueLight[8]<0.6 || valueLight[9]<3)
				continue;
			
			for(int i=0;i<6;i++){
				if(valueLight[i]!=0)
					lightsb.append(Math.exp(valueLight[i])).append("\t");
				else
					lightsb.append("\t");
			}
			lightsb.append(Math.exp(valueLight[6])).append("\t").append(valueLight[7]).append("\t")
				.append(valueLight[8]).append("\t");
			
			if(valueHeavy[7]>=0 || valueHeavy[8]<0.6 || valueHeavy[9]<3)
				continue;
			
			for(int i=0;i<6;i++){
				if(valueHeavy[i]!=0)
					heavysb.append(Math.exp(valueHeavy[i])).append("\t");
				else
					heavysb.append("\t");
			}
			heavysb.append(Math.exp(valueHeavy[6])).append("\t").append(valueHeavy[7]).append("\t")
				.append(valueHeavy[8]).append("\t");
			
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t").append(this.peppromap.get(key)).append("\t").append(idCount).append("\t");
			if(lightsb.length()>0 && heavysb.length()>0){
				sb.append(lightsb).append(heavysb);
				double half = (valueLight[6]-valueHeavy[6])/(valueHeavy[7]-valueLight[7]);
				if(half>0)
					writer.addContent(sb.toString()+half, 0, ef);
			}
						
/*			StringBuilder sbl1 = new StringBuilder();
			StringBuilder sbl2 = new StringBuilder();
			StringBuilder sbh1 = new StringBuilder();
			StringBuilder sbh2 = new StringBuilder();
			int count24 = 0;
			for(int i=0;i<light1.length;i++){
				sbl1.append(light1[i]).append("\t");
				sbl2.append(light2[i]).append("\t");
				sbh1.append(heavy1[i]).append("\t");
				sbh2.append(heavy2[i]).append("\t");
				if(light1[i]>0)count24++;
				if(light2[i]>0)count24++;
				if(heavy1[i]>0)count24++;
				if(heavy2[i]>0)count24++;
			}
			countlist[count24]++;
			pw.write(key+"\t"+sbl1+sbl2+sbh1+sbh2+"\n");
//			pw.write(key+"\t"+sbl1+"\n");
//			pw.write(key+"\t"+sbl2+"\n");
//			pw.write(key+"\t"+sbh1+"\n");
//			pw.write(key+"\t"+sbh2+"\n");
			
		}
		System.out.println(misslight+"\t"+missheavy);
//		for(int i=0;i<countlist.length;i++){
//			System.out.println(i+"\t"+countlist[i]);
//		}
//		pw.close();
		writer.close();
	}

	public void calculatePepLinear() throws IOException, RowsExceededException, WriteException{


		System.out.println(this.lightmap1.size()+"\t"+this.lightmap2.size());
		HashSet <String> keyset = new HashSet <String>();
		keyset.addAll(this.lightmap1.keySet());
		keyset.addAll(this.lightmap2.keySet());
		System.out.println(keyset.size());
		
		Iterator <String> it = keyset.iterator();
		int misscount = 0;
		int misslight = 0;
		int missheavy = 0;
		int [] countlist = new int[25];
		int [] count = new int [4];
//		PrintWriter pw = new PrintWriter("H:\\WFJ_mutiple_label\\turnover\\new\\peps_all.txt");
		double [] times = new double []{0, 3, 6, 12, 24, 48};
		while(it.hasNext()){
			
			String key = it.next();
			
			String ref = this.peppromap.get(key);
			if(ref.contains("REV_") || ref.contains("CON_"))
				continue;
			
			double [] light1 = new double[6];
			double [] light2 = new double[6];
			double [] heavy1 = new double[6];
			double [] heavy2 = new double[6];

			int idCount = 0;
			if(lightmap1.containsKey(key)){light1 = lightmap1.get(key);idCount++;}
			if(lightmap2.containsKey(key)){light2 = lightmap2.get(key);idCount++;}
			if(heavymap1.containsKey(key)){heavy1 = heavymap1.get(key);}
			if(heavymap2.containsKey(key)){heavy2 = heavymap2.get(key);}
			
			double kl1 = light1[0]*light2[0]==0 ? 0 : Math.log(light2[0]/light1[0]);
			double kl2 = light1[1]*light2[1]==0 ? 0 : Math.log(light2[1]/light1[1]);
			double kl3 = light1[2]*light2[2]==0 ? 0 : Math.log(light2[2]/light1[2]);
			double kl4 = light1[3]*light2[3]==0 ? 0 : Math.log(light2[3]/light1[3]);
			double kl5 = light1[4]*light2[4]==0 ? 0 : Math.log(light2[4]/light1[4]);
			double kl6 = light1[5]*light2[5]==0 ? 0 : Math.log(light2[5]/light1[5]);
			
			double kh1 = heavy1[0]*heavy2[0]==0 ? 0 : Math.log(heavy2[0]/heavy1[0]);
			double kh2 = heavy1[1]*heavy2[1]==0 ? 0 : Math.log(heavy2[1]/heavy1[1]);
			double kh3 = heavy1[2]*heavy2[2]==0 ? 0 : Math.log(heavy2[2]/heavy1[2]);
			double kh4 = heavy1[3]*heavy2[3]==0 ? 0 : Math.log(heavy2[3]/heavy1[3]);
			double kh5 = heavy1[4]*heavy2[4]==0 ? 0 : Math.log(heavy2[4]/heavy1[4]);
			double kh6 = heavy1[5]*heavy2[5]==0 ? 0 : Math.log(heavy2[5]/heavy1[5]);
			
			ArrayList <Double> listn = new ArrayList <Double>();
			ArrayList <Double> listm = new ArrayList <Double>();
			
			if(kl1*kl3!=0) {listn.add(kl1-kl3);}
			if(kl2*kl3!=0) {listn.add(kl2-kl3);}
			if(kl4*kl6!=0) {listn.add(kl6-kl4);}
			if(kl5*kl6!=0) {listn.add(kl6-kl5);}
			
			if(kh1*kh3!=0) {listn.add(kh1-kh3);}
			if(kh2*kh3!=0) {listn.add(kh2-kh3);}
			if(kh4*kh6!=0) {listn.add(kh6-kh4);}
			if(kh5*kh6!=0) {listn.add(kh6-kh5);}
			
			if(kl1*kl6!=0) {listm.add(kl6-kl1);}
			if(kl2*kl6!=0) {listm.add(kl6-kl2);}
			if(kl3*kl4!=0) {listm.add(kl4-kl3);}
			if(kl3*kl5!=0) {listm.add(kl5-kl3);}
			
			if(kh1*kh6!=0) {listm.add(kh6-kh1);}
			if(kh2*kh6!=0) {listm.add(kh6-kh2);}
			if(kh3*kh4!=0) {listm.add(kh4-kh3);}
			if(kh3*kh5!=0) {listm.add(kh5-kh3);}
			
//			double mk = -0.562350808;
//			double nk = -0.057162863;
			double mk = -0.5301;
			double nk = -0.0029;

//			if(listm.size()!=0 && listn.size()!=0) pw.write(MathTool.getMedian(listm)+"\t"+MathTool.getMedian(listn)+"\n");
			if(listm.size()!=0) {
				mk = MathTool.getAve(listm);
				count[0]++;
			}
			count[1]++;
			if(listn.size()!=0) {
				nk = MathTool.getAve(listn);
				count[2]++;
			}
			count[3]++;
			
			double [] flight = new double [6];
			flight[0] = this.getValueIn(light1[0], 0, light2[0], 0);
			flight[1] = this.getValueIn(light1[1], 0, light2[1], 0);
			flight[2] = this.getValueIn(light1[2], 0, light2[2], nk);
			flight[3] = this.getValueIn(light1[3], mk, light2[3], nk);
			flight[4] = this.getValueIn(light1[4], mk, light2[4], nk);
			flight[5] = this.getValueIn(light1[5], mk, light2[5], 0);
			
			double [] fheavy = new double [6];
			fheavy[0] = this.getValueIn(heavy1[0], 0, heavy2[0], 0);
			fheavy[1] = this.getValueIn(heavy1[1], 0, heavy2[1], 0);
			fheavy[2] = this.getValueIn(heavy1[2], 0, heavy2[2], nk);
			fheavy[3] = this.getValueIn(heavy1[3], mk, heavy2[3], nk);
			fheavy[4] = this.getValueIn(heavy1[4], mk, heavy2[4], nk);
			fheavy[5] = this.getValueIn(heavy1[5], mk, heavy2[5], 0);

/*			int countlight = 0;
			int countheavy = 0;
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t");
			for(int i=0;i<flight.length;i++){
				sb.append(flight[i]).append("\t");
				if(flight[i]>0)countlight++;
			}
			for(int i=0;i<fheavy.length;i++){
				sb.append(fheavy[i]).append("\t");
				if(fheavy[i]>0)countheavy++;
			}
			
			if(countlight>=3 && countheavy>=3)
			writer.addContent(sb.toString(), 0, ef);
			
			double [] valueLight = calLinearSelf(flight, times);
			double [] valueHeavy = calLinearSelf(fheavy, times);

			StringBuilder lightsb = new StringBuilder();
			StringBuilder heavysb = new StringBuilder();

			if(valueLight[7]<=0 || valueLight[8]<0.6 || valueLight[9]<3)
				continue;
			
			for(int i=0;i<6;i++){
				if(valueLight[i]!=0)
					lightsb.append(Math.exp(valueLight[i])).append("\t");
				else
					lightsb.append("\t");
			}
			lightsb.append(Math.exp(valueLight[6])).append("\t").append(valueLight[7]).append("\t")
				.append(valueLight[8]).append("\t");
			
			if(valueHeavy[7]>=0 || valueHeavy[8]<0.6 || valueHeavy[9]<3)
				continue;
			
			for(int i=0;i<6;i++){
				if(valueHeavy[i]!=0)
					heavysb.append(Math.exp(valueHeavy[i])).append("\t");
				else
					heavysb.append("\t");
			}
			heavysb.append(Math.exp(valueHeavy[6])).append("\t").append(valueHeavy[7]).append("\t")
				.append(valueHeavy[8]).append("\t");
			
			StringBuilder sb = new StringBuilder();
			sb.append(key).append("\t").append(this.peppromap.get(key)).append("\t").append(idCount).append("\t");
			if(lightsb.length()>0 && heavysb.length()>0){
				sb.append(lightsb).append(heavysb);
				double half = (valueLight[6]-valueHeavy[6])/(valueHeavy[7]-valueLight[7]);
			}
						
/*			StringBuilder sbl1 = new StringBuilder();
			StringBuilder sbl2 = new StringBuilder();
			StringBuilder sbh1 = new StringBuilder();
			StringBuilder sbh2 = new StringBuilder();
			int count24 = 0;
			for(int i=0;i<light1.length;i++){
				sbl1.append(light1[i]).append("\t");
				sbl2.append(light2[i]).append("\t");
				sbh1.append(heavy1[i]).append("\t");
				sbh2.append(heavy2[i]).append("\t");
				if(light1[i]>0)count24++;
				if(light2[i]>0)count24++;
				if(heavy1[i]>0)count24++;
				if(heavy2[i]>0)count24++;
			}
			countlist[count24]++;
			pw.write(key+"\t"+sbl1+sbl2+sbh1+sbh2+"\n");
//			pw.write(key+"\t"+sbl1+"\n");
//			pw.write(key+"\t"+sbl2+"\n");
//			pw.write(key+"\t"+sbh1+"\n");
//			pw.write(key+"\t"+sbh2+"\n");
			
		}
		System.out.println(misslight+"\t"+missheavy);
		System.out.println(count[0]+"\t"+count[1]+"\t"+(double)count[0]/(double)count[1]+"\t"+
		count[2]+"\t"+count[3]+"\t"+(double)count[2]/(double)count[3]+"\t");
//		for(int i=0;i<countlist.length;i++){
//			System.out.println(i+"\t"+countlist[i]);
//		}
//		pw.close();
	}

	@SuppressWarnings("finally")
	public void calculatePro(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Peptide\t0\t3\t6\t12\t24\t48\ta\tb\tcoe\t0\t3\t6\t12\t24\t48\ta\tb\tcoe\thalf";
		writer.addTitle(title, 0, ef);
		double [] times = new double []{0, 3, 6, 12, 24, 48};
		
		int misscount = 0;
		int misslight = 0;
		int missheavy = 0;
		
		Iterator <String> proit = this.promap.keySet().iterator();
		while(proit.hasNext()){
			String ref = proit.next();
			ArrayList <String> peps = this.promap.get(ref);
			ArrayList <Double> [] lightintens = new ArrayList [6];
			ArrayList <Double> [] heavyintens = new ArrayList [6];

			for(int i=0;i<6;i++){
				lightintens[i] = new ArrayList <Double>();
				heavyintens[i] = new ArrayList <Double>();
			}
			
			for(int i=0;i<peps.size();i++){
				
				String key = peps.get(i);
				double [] light1 = new double[6];
				double [] light2 = new double[6];
				double [] heavy1 = new double[6];
				double [] heavy2 = new double[6];

				if(lightmap1.containsKey(key)){light1 = lightmap1.get(key);}
				if(lightmap2.containsKey(key)){light2 = lightmap2.get(key);}
				if(heavymap1.containsKey(key)){heavy1 = heavymap1.get(key);}
				if(heavymap2.containsKey(key)){heavy2 = heavymap2.get(key);}
				
				double [] flight = new double [6];
				flight[0] = this.getValue(light1[0], 1, light2[0], 1);
				flight[1] = this.getValue(light1[1], 1, light2[1], 1);
				flight[2] = this.getValue(light1[2], 1, light2[2], 1.015107051);
				flight[3] = this.getValue(light1[3], 0.5965, light2[3], 1.015107051);
				flight[4] = this.getValue(light1[4], 0.5965, light2[4], 1.015107051);
				flight[5] = this.getValue(light1[5], 0.5965, light2[5], 1);
				
				double [] fheavy = new double [6];
				fheavy[0] = this.getValue(heavy1[0], 1, heavy2[0], 1);
				fheavy[1] = this.getValue(heavy1[1], 1, heavy2[1], 1);
				fheavy[2] = this.getValue(heavy1[2], 1, heavy2[2], 1.015107051);
				fheavy[3] = this.getValue(heavy1[3], 0.5965, heavy2[3], 1.015107051);
				fheavy[4] = this.getValue(heavy1[4], 0.5965, heavy2[4], 1.015107051);
				fheavy[5] = this.getValue(heavy1[5], 0.5965, heavy2[5], 1);
				
				for(int j=0;j<6;j++){
					lightintens[j].add(flight[j]);
					heavyintens[j].add(fheavy[j]);
				}
			}
			double [] flight = new double [6];
			double [] fheavy = new double [6];
			for(int i=0;i<6;i++){
				flight[i] = MathTool.getMedian(lightintens[i]);
				fheavy[i] = MathTool.getMedian(heavyintens[i]);
			}
			
			try{
				
				double [] valueLight = this.calReg(flight, times);
				double [] valueHeavy = this.calReg(fheavy, times);
				
				StringBuilder lightsb = new StringBuilder();
				StringBuilder heavysb = new StringBuilder();

				if(valueLight!=null){
					for(int i=0;i<valueLight.length;i++){
						lightsb.append(valueLight[i]).append("\t");
					}
				}else{
					misslight++;
				}
				
				if(valueHeavy!=null){
					for(int i=0;i<valueHeavy.length;i++){
						heavysb.append(valueHeavy[i]).append("\t");
					}
				}else{
					missheavy++;
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append(ref).append("\t");
				if(lightsb.length()>0 && heavysb.length()>0){
					sb.append(lightsb).append(heavysb);
					double half = (Math.log(valueHeavy[6])-Math.log(valueLight[6]))/(valueLight[7]-valueHeavy[7]);
					writer.addContent(sb.toString()+half, 0, ef);
				}

			}finally{
				continue;
			}
		}

		System.out.println(misslight+"\t"+missheavy);
		writer.close();
	}
	
	@SuppressWarnings("finally")
	public void calculateProLinear(String out) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Peptide\t0\t3\t6\t12\t24\t48\tcoe\t0\t3\t6\t12\t24\t48\tcoe\thalf";
		writer.addTitle(title, 0, ef);
		double [] times = new double []{0, 3, 6, 12, 24, 48};
		
		int misscount = 0;
		int misslight = 0;
		int missheavy = 0;
		
		double mk = -0.562350808;
		double nk = -0.057162863;
		
		Iterator <String> proit = this.promap.keySet().iterator();
		while(proit.hasNext()){
			String ref = proit.next();
			ArrayList <String> peps = this.promap.get(ref);
			ArrayList <Double> [] lightintens = new ArrayList [6];
			ArrayList <Double> [] heavyintens = new ArrayList [6];

			for(int i=0;i<6;i++){
				lightintens[i] = new ArrayList <Double>();
				heavyintens[i] = new ArrayList <Double>();
			}
			
			for(int i=0;i<peps.size();i++){
				
				String key = peps.get(i);
				double [] light1 = new double[6];
				double [] light2 = new double[6];
				double [] heavy1 = new double[6];
				double [] heavy2 = new double[6];

				if(lightmap1.containsKey(key)){light1 = lightmap1.get(key);}
				if(lightmap2.containsKey(key)){light2 = lightmap2.get(key);}
				if(heavymap1.containsKey(key)){heavy1 = heavymap1.get(key);}
				if(heavymap2.containsKey(key)){heavy2 = heavymap2.get(key);}
				
				double kl1 = light1[0]*light2[0]==0 ? 0 : Math.log(light2[0]/light1[0]);
				double kl2 = light1[1]*light2[1]==0 ? 0 : Math.log(light2[1]/light1[1]);
				double kl3 = light1[2]*light2[2]==0 ? 0 : Math.log(light2[2]/light1[2]);
				double kl4 = light1[3]*light2[3]==0 ? 0 : Math.log(light2[3]/light1[3]);
				double kl5 = light1[4]*light2[4]==0 ? 0 : Math.log(light2[4]/light1[4]);
				double kl6 = light1[5]*light2[5]==0 ? 0 : Math.log(light2[5]/light1[5]);
				
				double kh1 = heavy1[0]*heavy2[0]==0 ? 0 : Math.log(heavy2[0]/heavy1[0]);
				double kh2 = heavy1[1]*heavy2[1]==0 ? 0 : Math.log(heavy2[1]/heavy1[1]);
				double kh3 = heavy1[2]*heavy2[2]==0 ? 0 : Math.log(heavy2[2]/heavy1[2]);
				double kh4 = heavy1[3]*heavy2[3]==0 ? 0 : Math.log(heavy2[3]/heavy1[3]);
				double kh5 = heavy1[4]*heavy2[4]==0 ? 0 : Math.log(heavy2[4]/heavy1[4]);
				double kh6 = heavy1[5]*heavy2[5]==0 ? 0 : Math.log(heavy2[5]/heavy1[5]);
				
				ArrayList <Double> listn = new ArrayList <Double>();
				ArrayList <Double> listm = new ArrayList <Double>();
				
				if(kl1*kl3!=0) {listn.add(kl1-kl3);}
				if(kl2*kl3!=0) {listn.add(kl2-kl3);}
				if(kl4*kl6!=0) {listn.add(kl6-kl4);}
				if(kl5*kl6!=0) {listn.add(kl6-kl5);}
				
				if(kh1*kh3!=0) {listn.add(kh1-kh3);}
				if(kh2*kh3!=0) {listn.add(kh2-kh3);}
				if(kh4*kh6!=0) {listn.add(kh6-kh4);}
				if(kh5*kh6!=0) {listn.add(kh6-kh5);}
				
				if(kl1*kl6!=0) {listm.add(kl6-kl1);}
				if(kl2*kl6!=0) {listm.add(kl6-kl2);}
				if(kl3*kl4!=0) {listm.add(kl4-kl3);}
				if(kl3*kl5!=0) {listm.add(kl5-kl3);}
				
				if(kh1*kh6!=0) {listm.add(kh6-kh1);}
				if(kh2*kh6!=0) {listm.add(kh6-kh2);}
				if(kh3*kh4!=0) {listm.add(kh4-kh3);}
				if(kh3*kh5!=0) {listm.add(kh5-kh3);}

//				if(listm.size()!=0 && listn.size()!=0) pw.write(MathTool.getMedian(listm)+"\t"+MathTool.getMedian(listn)+"\n");
				if(listm.size()!=0) mk = MathTool.getMedian(listm);
				if(listn.size()!=0) nk = MathTool.getMedian(listn);
				
				double [] flight = new double [6];
				flight[0] = this.getValueIn(light1[0], 0, light2[0], 0);
				flight[1] = this.getValueIn(light1[1], 0, light2[1], 0);
				flight[2] = this.getValueIn(light1[2], 0, light2[2], nk);
				flight[3] = this.getValueIn(light1[3], mk, light2[3], nk);
				flight[4] = this.getValueIn(light1[4], mk, light2[4], nk);
				flight[5] = this.getValueIn(light1[5], mk, light2[5], 0);
				
				double [] fheavy = new double [6];
				fheavy[0] = this.getValueIn(heavy1[0], 0, heavy2[0], 0);
				fheavy[1] = this.getValueIn(heavy1[1], 0, heavy2[1], 0);
				fheavy[2] = this.getValueIn(heavy1[2], 0, heavy2[2], nk);
				fheavy[3] = this.getValueIn(heavy1[3], mk, heavy2[3], nk);
				fheavy[4] = this.getValueIn(heavy1[4], mk, heavy2[4], nk);
				fheavy[5] = this.getValueIn(heavy1[5], mk, heavy2[5], 0);
				
				for(int j=0;j<6;j++){
					lightintens[j].add(flight[j]);
					heavyintens[j].add(fheavy[j]);
				}
			}
			
			double [] flight = new double [6];
			double [] fheavy = new double [6];
			for(int i=0;i<6;i++){
				flight[i] = MathTool.getMedian(lightintens[i]);
				fheavy[i] = MathTool.getMedian(heavyintens[i]);
			}

			try{
				
				double [] valueLight = this.calLinearSelf(flight, times);
				double [] valueHeavy = this.calLinearSelf(fheavy, times);
				
				StringBuilder lightsb = new StringBuilder();
				StringBuilder heavysb = new StringBuilder();

				if(valueLight!=null){
					for(int i=0;i<6;i++){
						if(valueLight[i]!=0)
							lightsb.append(Math.exp(valueLight[i])).append("\t");
						else
							lightsb.append("0").append("\t");
					}
					lightsb.append(valueLight[valueLight.length-1]).append("\t");
				}else{
					misslight++;
				}
				
				if(valueHeavy!=null){
					for(int i=0;i<6;i++){
						if(valueHeavy[i]!=0)
							heavysb.append(Math.exp(valueHeavy[i])).append("\t");
						else
							heavysb.append("0").append("\t");
					}
					heavysb.append(valueHeavy[valueHeavy.length-1]).append("\t");
				}else{
					missheavy++;
				}

				StringBuilder sb = new StringBuilder();
				sb.append(ref).append("\t");
				if(lightsb.length()>0 && heavysb.length()>0){
					sb.append(lightsb).append(heavysb);
					double half = (valueLight[6]-valueHeavy[6])/(valueHeavy[7]-valueLight[7]);
					writer.addContent(sb.toString()+half, 0, ef);
				}

			}finally{
				continue;
			}
		}

		System.out.println(misslight+"\t"+missheavy);
		writer.close();
	}

	@SuppressWarnings("finally")
	public static void calculateProLinear(String in, String out) throws IOException, JXLException{
		
		ExcelWriter writer = new ExcelWriter(out);
		ExcelFormat ef = ExcelFormat.normalFormat;
		String title = "Peptide\t0\t3\t6\t12\t24\t48\tcoe\t0\t3\t6\t12\t24\t48\tcoe\thalf";
		writer.addTitle(title, 0, ef);
		double [] times = new double []{0, 3, 6, 12, 24, 48};
		
		int misscount = 0;
		int misslight = 0;
		int missheavy = 0;
		
		double mk = -0.562350808;
		double nk = -0.057162863;
		
		HashMap <String, ArrayList <String>> promap = new HashMap <String, ArrayList<String>>();
		HashMap <String, double[]> lightmap = new HashMap <String, double[]>();
		HashMap <String, double[]> heavymap = new HashMap <String, double[]>();
		ExcelReader reader = new ExcelReader(in);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			String seq = line[0];
			String ref = line[1];
			if(promap.containsKey(ref)){
				promap.get(ref).add(seq);
			}else{
				ArrayList <String> list = new ArrayList <String>();
				list.add(seq);
				promap.put(ref, list);
			}
			double [] light = new double[6];
			double [] heavy = new double[6];
			for(int i=0;i<6;i++){
				light[i] = Double.parseDouble(line[i+3]);
				heavy[i] = Double.parseDouble(line[i+10]);
			}
			lightmap.put(seq, light);
			heavymap.put(seq, heavy);
		}
		
		Iterator <String> proit = promap.keySet().iterator();
		while(proit.hasNext()){
			String ref = proit.next();
			ArrayList <String> peps = promap.get(ref);
			ArrayList <Double> [] lightintens = new ArrayList [6];
			ArrayList <Double> [] heavyintens = new ArrayList [6];

			for(int i=0;i<6;i++){
				lightintens[i] = new ArrayList <Double>();
				heavyintens[i] = new ArrayList <Double>();
			}
			
			for(int i=0;i<peps.size();i++){
				
				String key = peps.get(i);

				double [] flight = lightmap.get(key);
				double [] fheavy = heavymap.get(key);

				for(int j=0;j<6;j++){
					if(flight[j]==0) lightintens[j].add(0.0);
					else lightintens[j].add(Math.log(flight[j]));
					if(fheavy[j]==0) heavyintens[j].add(0.0);
					else heavyintens[j].add(Math.log(fheavy[j]));
				}
			}
			
			double [] flight = new double [6];
			double [] fheavy = new double [6];
			for(int i=0;i<6;i++){
				flight[i] = MathTool.getMedian(lightintens[i]);
				fheavy[i] = MathTool.getMedian(heavyintens[i]);
			}

			try{
				
				double [] valueLight = calLinearSelf(flight, times);
				double [] valueHeavy = calLinearSelf(fheavy, times);
				
				StringBuilder lightsb = new StringBuilder();
				StringBuilder heavysb = new StringBuilder();

				if(valueLight!=null){
					for(int i=0;i<6;i++){
						if(valueLight[i]!=0)
							lightsb.append(Math.exp(valueLight[i])).append("\t");
						else
							lightsb.append("0").append("\t");
					}
					lightsb.append(valueLight[valueLight.length-1]).append("\t");
				}else{
					misslight++;
				}
				
				if(valueHeavy!=null){
					for(int i=0;i<6;i++){
						if(valueHeavy[i]!=0)
							heavysb.append(Math.exp(valueHeavy[i])).append("\t");
						else
							heavysb.append("0").append("\t");
					}
					heavysb.append(valueHeavy[valueHeavy.length-1]).append("\t");
				}else{
					missheavy++;
				}

				StringBuilder sb = new StringBuilder();
				sb.append(ref).append("\t");
				if(lightsb.length()>0 && heavysb.length()>0){
					sb.append(lightsb).append(heavysb);
					double half = (valueLight[6]-valueHeavy[6])/(valueHeavy[7]-valueLight[7]);
					writer.addContent(sb.toString()+half, 0, ef);
				}

			}finally{
				continue;
			}
		}

		System.out.println(misslight+"\t"+missheavy);
		writer.close();
	}
*/
	@SuppressWarnings("finally")
	private double [] calReg(double [] values, double [] times){
		
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
			
			Regression reg = new Regression(calTimes, calValues);
//			reg.setYscaleFactor(1);
//			reg.exponentialMultiple(1);
			reg.exponentialSimple();
			
			double a = reg.getBestEstimates()[1];
			double b = reg.getBestEstimates()[0];
			double yyr = reg.getYYcorrCoeff();
			
			if(yyr<0.9 && valuelist.size()>=4){
				
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
					
					try{
						
						Regression newReg = new Regression(newTimes, newValues);
//						reg.setYscaleFactor(1);
//						reg.exponentialMultiple(1);
						newReg.exponentialSimple();
						
						double newa = newReg.getBestEstimates()[1];
						double newb = newReg.getBestEstimates()[0];
						double newyyr = newReg.getYYcorrCoeff();

						if(newyyr>yyr){
							yyr = newyyr;
							a = newa;
							b = newb;
							tp = i;
						}

					}finally{
						continue;
					}
				}
				
				if(tp==-1){
					
					double [] dd = new double [9];
					
					System.arraycopy(values, 0, dd, 0, dd.length);
					dd[6] = a;
					dd[7] = b;
					dd[8] = yyr;
					
					return dd;
					
				}else{
					
					double [] dd = new double [9];
					for(int i=0;i<values.length;i++){
						if(i==tp){
							dd[i] = 0;
						}else{
							dd[i] = values[i];
						}
					}
					dd[6] = a;
					dd[7] = b;
					dd[8] = yyr;
					
					return dd;
				}
				
			}else{
				
				double [] dd = new double [9];
				
				System.arraycopy(values, 0, dd, 0, dd.length);
				dd[6] = a;
				dd[7] = b;
				dd[8] = yyr;
				
				return dd;
			}
		}
		return null;
	}
/*	
	private static double [] calLinear(double [] values, double [] times){
		
		ArrayList <Double> valuelist = new ArrayList <Double>();
		ArrayList <Double> timelist = new ArrayList <Double>();
		for(int i=0;i<values.length;i++){
			if(values[i]!=0){
				valuelist.add(values[i]);
				timelist.add(times[i]);
			}
		}

		if(valuelist.size()>=4){
			
			double [] calValues = new double[valuelist.size()];
			double [] calTimes = new double[timelist.size()];
			
			for(int i=0;i<calValues.length;i++){
				calValues[i] = valuelist.get(i);
				calTimes[i] = timelist.get(i);
			}
			
			Regression reg = new Regression(calTimes, calValues);
			reg.polynomial(1);

			double a = reg.getBestEstimates()[0];
			double b = reg.getBestEstimates()[1];
			double yyr = reg.getYYcorrCoeff();
			
			if(valuelist.size()>=5){
				
				if(yyr<=0.8){

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
						
						Regression newReg = new Regression(newTimes, newValues);
						newReg.polynomial(1);
						
						double newa = newReg.getBestEstimates()[0];
						double newb = newReg.getBestEstimates()[1];
						double newyyr = newReg.getYYcorrCoeff();

						if(newyyr>yyr){
							yyr = newyyr;
							a = newa;
							b = newb;
							tp = i;
						}
					}
					
					if(tp==-1){
						
						double [] dd = new double [9];
						
						System.arraycopy(values, 0, dd, 0, values.length);
						dd[6] = a;
						dd[7] = b;
						dd[8] = yyr;
						
						return dd;
						
					}else{
						
						double [] dd = new double [9];
						for(int i=0;i<values.length;i++){
							if(values[i]==valuelist.get(tp)){
								dd[i] = 0;
							}else{
								dd[i] = values[i];
							}
						}
						dd[6] = a;
						dd[7] = b;
						dd[8] = yyr;
						
						return dd;
						
					}				
					
				}else{
					
					double [] dd = new double [9];
					
					System.arraycopy(values, 0, dd, 0, values.length);
					dd[6] = a;
					dd[7] = b;
					dd[8] = yyr;
					
					return dd;
				}
			}else{
				
				double [] dd = new double [9];
				
				System.arraycopy(values, 0, dd, 0, values.length);
				dd[6] = a;
				dd[7] = b;
				dd[8] = yyr;
				
				return dd;
			}
		}
		iii++;
		return new double [9];
	}
*/	
	private static double [] calLinearSelf(double [] values, double [] times){
		
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
			
			IFunction function = new SLineFunction();
			CurveFitting fit = new CurveFitting(calTimes, calValues, function);
			fit.fit();
			
			double [] para = fit.getBestParams();

			double b = para[0];
			double a = para[1];
			double yyr = fit.getFitGoodness();
//			if(fit.getFitGoodness()==0)System.out.println("mipa"+"\t"+a+"\t"+b);
			
			if(valuelist.size()>=4){
				
				if(yyr<=0.9){

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

						double newb = para2[0];
						double newa = para2[1];
						double newyyr = fit2.getFitGoodness();

						if(newyyr>yyr){
							yyr = newyyr;
							a = newa;
							b = newb;
							tp = i;
						}
					}
					
					if(tp==-1){
						
						double [] dd = new double [10];
						
						System.arraycopy(values, 0, dd, 0, values.length);
						dd[6] = a;
						dd[7] = b;
						dd[8] = yyr;
						dd[9] = valuelist.size();
						
						return dd;
						
					}else{
						
						double [] dd = new double [10];
						for(int i=0;i<values.length;i++){
							if(values[i]==valuelist.get(tp)){
								dd[i] = 0;
							}else{
								dd[i] = values[i];
							}
						}
						dd[6] = a;
						dd[7] = b;
						dd[8] = yyr;
						dd[9] = valuelist.size()-1;
						
						return dd;
						
					}				
					
				}else{
					
					double [] dd = new double [10];
					
					System.arraycopy(values, 0, dd, 0, values.length);
					dd[6] = a;
					dd[7] = b;
					dd[8] = yyr;
					dd[9] = valuelist.size();
					
					return dd;
				}
			}else{
				
				double [] dd = new double [10];
				
				System.arraycopy(values, 0, dd, 0, values.length);
				dd[6] = a;
				dd[7] = b;
				dd[8] = yyr;
				dd[9] = valuelist.size();
				
				return dd;
			}
		}
		iii++;
		return new double [10];
	}

	private double getValue(double d1, double k1, double d2, double k2){
		if(d1==0){
			if(d2==0){
				return 0;
			}else{
				return d2*k2;
			}
		}else{
			if(d2==0){
				return d1*k1;
			}else{
				return (d1*k1+d2*k2)/2.0;
			}
		}
	}
	
	private double getValueIn(double d1, double k1, double d2, double k2){
		if(d1==0){
			if(d2==0){
				return 0;
			}else{
				return Math.log(d2)+k2;
			}
		}else{
			if(d2==0){
				return Math.log(d1)+k1;
			}else{
				return (Math.log(d1)+k1+Math.log(d2)+k2)/2.0;
			}
		}
	}
	
	@SuppressWarnings({ "unused", "finally" })
	private String reCal(ArrayList <Double> timelist, ArrayList <Double> ratiolist, double oriCoeff, String rs){
		
		String output = null;
		int tp = -1;
		for(int i=0;i<timelist.size();i++){
			StringBuilder sb = new StringBuilder();
			double [] ratios = new double [timelist.size()-1];
			double [] times = new double [timelist.size()-1];
			for(int j=0;j<ratios.length;j++){
				if(j<i){
					ratios[j] = ratiolist.get(j);
					times[j] = timelist.get(j);
				}else{
					ratios[j] = ratiolist.get(j+1);
					times[j] = timelist.get(j+1);
				}
			}
			
			try{
				
				Regression reg = new Regression(times, ratios);
//				reg.setYscaleFactor(1);
//				reg.exponentialMultiple(1);
				reg.exponentialSimple();
				
				double a = reg.getBestEstimates()[1];
				double b = -reg.getBestEstimates()[0];
				double yyr = reg.getYYcorrCoeff();
				sb.append(df4.format(a)).append("\t").append(df4.format(b)).append("\t").append(df4.format(yyr)).append("\t");
				double half = Math.log(a)/b;
				sb.append(df4.format(half));
				
				if(yyr>oriCoeff){
					oriCoeff = yyr;
					output = sb.toString();
					tp = (int) times[i];
				}

			}finally{
				continue;
			}
		}

		if(output!=null){
			
			StringBuilder sb = new StringBuilder();
			String [] ss = rs.split("\t");
			int id = -1;
			
			switch (tp){
			case 3:
				id = 2;
				break;
			case 6:
				id = 3;
				break;
			case 12:
				id = 4;
				break;
			case 24:
				id = 5;
				break;
			case 48:
				id = 6;
				break;
			}
			
			for(int i=0;i<ss.length;i++){
				if(i==id){
					sb.append("\t");
				}else{
					sb.append(ss[i]).append("\t");
				}
			}
			sb.append(output);
			return sb.toString();
			
		}else{
			return null;
		}
	}
	
	public static void selectProtein(String refs, String pro) throws IOException, JXLException{
		
		HashSet <String> refset = new HashSet <String>();
		ExcelReader proreader = new ExcelReader(refs);
		String [] proline = proreader.readLine();
		while((proline=proreader.readLine())!=null){
			refset.add(proline[0]);
		}
		proreader.close();
//System.out.println(refset.size());		
		ExcelReader pepreader = new ExcelReader(pro);
		String [] pepline = pepreader.readLine();
		while((pepline=pepreader.readLine())!=null){
			String ref = pepline[0];
			if(refset.contains(ref)){
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<pepline.length;i++){
					sb.append(pepline[i]).append("\t");
				}
				System.out.println(sb);
			}
		}
		pepreader.close();
	}
	
	public static void selectPeptide(String pep, String pro, String peps) throws IOException, JXLException{
		
		HashMap <String, String[]> pepratiomap = new HashMap <String, String[]>();
		ExcelReader pepRatioReader = new ExcelReader(pep);
		String [] pepratioline = pepRatioReader.readLine();
		while((pepratioline=pepRatioReader.readLine())!=null){
			pepratiomap.put(pepratioline[0], pepratioline);
		}
		pepRatioReader.close();
		
		HashMap <String, HashSet <String>> promap = new HashMap <String, HashSet <String>>();
		ExcelReader proreader = new ExcelReader(pro);
		String [] proRefLine = proreader.readLine();
		while((proRefLine=proreader.readLine())!=null){
			String ref = proRefLine[0];
			promap.put(ref, new HashSet <String>());
		}
		proreader.close();
		System.out.println(promap.size());

		File [] pepRatios = (new File(peps)).listFiles();
		for(int i=0;i<pepRatios.length;i++){
			
			ExcelReader pepreader = new ExcelReader(pepRatios[i]);
			pepreader.skip(13);
			String [] pepProLine = null;
			ArrayList <String> reflist = new ArrayList <String>();
			HashSet <String> pepset = new HashSet <String>();
			boolean complete = false;
			
			while((pepProLine=pepreader.readLine())!=null){
				
				if(pepProLine.length>1){
					
					if(pepProLine[0].trim().length()>0){
						
						String ref = pepProLine[1];
						if(complete){
							for(int j=0;j<reflist.size();j++){
								String refj = reflist.get(j);
								if(promap.containsKey(refj)){
									promap.get(refj).addAll(pepset);
								}
							}
							reflist = new ArrayList <String>();
							pepset = new HashSet <String>();
							reflist.add(ref);
							complete = false;
						}else{
							reflist.add(ref);
						}
					}else{
						complete = true;
						String unique = PeptideUtil.getUniqueSequence(pepProLine[1]);
						pepset.add(unique);
					}
				}
				
				for(int j=0;j<reflist.size();j++){
					String refj = reflist.get(j);
					if(promap.containsKey(refj)){
						promap.get(refj).addAll(pepset);
					}
				}
			}
			pepreader.close();
//			break;
		}
		
		Iterator <String> proit = promap.keySet().iterator();
		while(proit.hasNext()){
			String ref = proit.next();
			HashSet <String> pepset = promap.get(ref);

			Iterator <String> pepit = pepset.iterator();
			while(pepit.hasNext()){
				String pepseq = pepit.next();
				if(pepratiomap.containsKey(pepseq)){
					StringBuilder sb = new StringBuilder();
					sb.append(ref).append("\t");
					String [] ss = pepratiomap.get(pepseq);
					for(int i=0;i<ss.length;i++){
						sb.append(ss[i]).append("\t");
					}
					System.out.println(sb);
				}
			}
		}
		
	}

	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub
		
//		WangDataProcessor.selectProtein("H:\\WFJ_mutiple_label\\turnover\\select\\Book1.xls", 
//				"H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\final.pros.ratio.self.xls");
//		WangDataProcessor.selectPeptide("H:\\WFJ_mutiple_label\\turnover\\select\\final.peps.linear.xls", 
//				"H:\\WFJ_mutiple_label\\turnover\\select\\Book1.xls", 
//				"H:\\WFJ_mutiple_label\\turnover\\select\\peps");
		WangDataProcessor processor = new WangDataProcessor();
//		processor.readPepRatio("H:\\WFJ_mutiple_label\\turnover\\new\\0_3_6.rev.xls", 0, 1, 2);
//		processor.readPepRatio("H:\\WFJ_mutiple_label\\turnover\\new\\0_3_48.rev.xls", 0, 1, 5);
//		processor.readPepRatio("H:\\WFJ_mutiple_label\\turnover\\new\\6_12_24.rev.xls", 3, 4, 2);
//		processor.readPepRatio("H:\\WFJ_mutiple_label\\turnover\\new\\12_24_48.rev.xls", 3, 4, 5);
//		processor.calculatePepSD("H:\\WFJ_mutiple_label\\turnover\\new\\pep.sd..xls");
		processor.read("H:\\WFJ_mutiple_label\\turnover\\new\\0_3_6.rev.xls", 0, 1, 2);
		processor.read("H:\\WFJ_mutiple_label\\turnover\\new\\0_3_48.rev.xls", 0, 1, 5);
		processor.read("H:\\WFJ_mutiple_label\\turnover\\new\\6_12_24.rev.xls", 3, 4, 2);
		processor.read("H:\\WFJ_mutiple_label\\turnover\\new\\12_24_48.rev.xls", 3, 4, 5);
//		System.out.println(processor.map.size());
		processor.calculateLinear("H:\\WFJ_mutiple_label\\turnover\\new\\final.pros.rela.ratio.self.xls");
//		WangDataProcessor.compare("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\final.pros.ratio.xls", 
//				"H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\final.pros.ratio.with0.xls");
//		processor.calculateLinearManual("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\manual.xls",
//				"H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\re2.final.pros.ratio.manual.xls");
//		processor.readPep1("H:\\WFJ_mutiple_label\\turnover\\new\\0_3_6.rev.xls", 0, 1, 2);
//		processor.readPep1("H:\\WFJ_mutiple_label\\turnover\\new\\12_24_48.rev.xls", 3, 4, 5);
//		processor.readPep2("H:\\WFJ_mutiple_label\\turnover\\new\\0_3_48.rev.xls", 0, 1, 5);
//		processor.readPep2("H:\\WFJ_mutiple_label\\turnover\\new\\6_12_24.rev.xls", 3, 4, 2);
//		processor.calculatePepLinear();
//		processor.readPro1("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\0_3_6.intensity.rev.xls", 0, 1, 2);
//		processor.readPro1("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\12_24_48.intensity.rev.xls", 3, 4, 5);
//		processor.readPro2("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\0_3_48.intensity.rev.xls", 0, 1, 5);
//		processor.readPro2("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\6_12_24.intensity.rev.xls", 3, 4, 2);
//		processor.calculatePepLinear("H:\\WFJ_mutiple_label\\turnover\\new\\final.peps.noRev.linear.xls");
//		processor.calculateProLinear("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\re.pros.linear.xls");
//		WangDataProcessor.calculateProLinear("H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\re.peps.linear.xls", 
//				"H:\\WFJ_mutiple_label\\turnover\\percolator_intensity_rev\\final.pros.linear.xls");
	}

}
