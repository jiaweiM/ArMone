/* 
 ******************************************************************************
 * File:StatQFilter.java * * * Created on 2010-9-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.statQuan;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2010-9-21, 10:46:29
 */
public class StatQFilter {
	
	private ExcelReader reader;
	private ExcelWriter writer;
	
	private DecimalFormat dfPer = new DecimalFormat("#.###%");
	private DecimalFormat df4 = new DecimalFormat("#.####");

	public StatQFilter(String in, String out) throws IOException, JXLException{
		this.reader = new ExcelReader(in, 1);
		this.writer = new ExcelWriter(out);
		reader.skip(1);
	}
	
	public void getInfo() throws RowsExceededException, WriteException, IOException{
		ArrayList <PepStatInfo> infoList = new ArrayList <PepStatInfo>();
		ExcelFormat f1 = new ExcelFormat(false,0);
		ExcelFormat f2 = new ExcelFormat(false,1);
		String [] strs;
		while((strs=reader.readLine())!=null && strs.length>1){
			int length = strs.length;
			String seq = strs[0];
			String mod = strs[1];
			int num = Integer.parseInt(strs[2]);
			double ave = Double.parseDouble(strs[3]);
			double rsd = Double.parseDouble(strs[4].substring(0,strs[4].length()-1))/100;
			double [] ratios = new double [length-6];
			for(int i=0;i<length-6;i++){
				ratios[i] = Double.parseDouble(strs[i+5]);
			}
			String ref = strs[length-1];
			PepStatInfo info = new PepStatInfo(seq, mod, ref, num, ave, rsd, ratios);
			if(num>2){
				if(rsd<0.5){
					writer.addContent(info.toString(), 0, f1);
				}else{
					if(num>3){
						PepStatInfo newInfo = propose(info);
						if(newInfo!=null){
							infoList.add(newInfo);
						}
					}					
				}
			}			
		}
		Iterator <PepStatInfo> it = infoList.iterator();
		while(it.hasNext()){
			PepStatInfo info = it.next();
			writer.addContent(info.toString(), 0, f2);
		}
		this.writer.close();
	}
	
	public PepStatInfo propose(PepStatInfo info){
		double [] ratios = info.ratios;
		ArrayList <Double> data = new ArrayList <Double>();
		for(int i=0;i<ratios.length;i++){
			if(ratios[i]!=0)
				data.add(ratios[i]);
		}
		Double [] r = data.toArray(new Double[data.size()]);	
		Arrays.sort(r);
		
		ArrayList <Double> small = new ArrayList <Double>();
		ArrayList <Double> big = new ArrayList <Double>();
		
		for(int i=0;i<r.length;i++){
			if(i==0){
				small.add(r[i]);
			}else if(i==r.length-1){
				big.add(r[i]);
			}else{
				small.add(r[i]);
				big.add(r[i]);
			}
		}
		
		double rsdSmall = MathTool.getRSDInDouble(small);
		double rsdBig = MathTool.getRSDInDouble(big);
		boolean [] select = new boolean [ratios.length];
		Arrays.fill(select, false);

		if(rsdSmall<0.5){
			double ave = MathTool.getAveInDouble(small);
			for(int i=0;i<ratios.length;i++){
				if(ratios[i]==r[r.length-1]){
					select[i] = true;
				}
			}
			
			PepStatInfo newInfo = new PepStatInfo(info.seq, info.mod, info.ref, info.num-1, ave, rsdSmall, ratios);
			newInfo.setSelect(select);
			return newInfo;
		}else{
			if(rsdBig<0.5){
				double ave = MathTool.getAveInDouble(big);
				for(int i=0;i<ratios.length;i++){
					if(ratios[i]==r[0]){
						select[i] = true;
					}
				}
				PepStatInfo newInfo = new PepStatInfo(info.seq, info.mod, info.ref, info.num-1, ave, rsdBig, ratios);
				newInfo.setSelect(select);
				return newInfo;
			}
		}
		
		return null;
	}
	
	public class PepStatInfo{

		private String seq;
		private String mod;
		private String ref;
		private int num;
		private double ave;
		private double rsd;
		private double[] ratios;
		private boolean [] select;
	
		public PepStatInfo(String seq, String mod, String ref, int num, double ave, 
				double rsd, double[] ratios){
			this.seq = seq;
			this.mod = mod;
			this.ref = ref;
			this.num = num;
			this.ave = ave;
			this.rsd = rsd;
			this.ratios = ratios;
			this.select = new boolean [ratios.length];
		}
		
		public void setSelect(boolean [] select){
			this.select = select;
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(seq).append("\t").append(mod).append("\t").append(num).append("\t")
			.append(ave).append("\t").append(dfPer.format(rsd)).append("\t");
			for(int i=0;i<ratios.length;i++){
				if(select[i])
					sb.append("(").append(df4.format(ratios[i])).append(")").append("\t");
				else
					sb.append(df4.format(ratios[i])).append("\t");
			}
			sb.append(ref);
			return sb.toString();
		}
		
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

		
		String in = "E:\\Data\\control-1-dimension-FINAL\\total.xls";
		String out = "E:\\Data\\control-1-dimension-FINAL\\filter_total.xls";
		StatQFilter filter = new StatQFilter(in,out);
		filter.getInfo();
	}

}
