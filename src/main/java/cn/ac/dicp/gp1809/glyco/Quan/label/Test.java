/* 
 ******************************************************************************
 * File: Test.java * * * Created on 2013-11-29
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;

import jxl.JXLException;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2013-11-29, 19:40:04
 */
public class Test {
	
	private static void countSite(String in) throws IOException, JXLException{
		
		int count = 0;
		HashSet<String> set = new HashSet<String>();
		ExcelReader reader =  new ExcelReader(in, 2);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			System.out.println(line[1]);
			if(line[1].charAt(8)!='P' && (line[1].charAt(9)=='S' || line[1].charAt(9)=='T')){
				count++;
				set.add(line[1]);
			}
		}
		reader.close();
		System.out.println(count+"\t"+set.size());
	}
	
	private static void cao(String in, String iden) throws IOException, JXLException{
		
		int count = 0;
		ExcelReader reader =  new ExcelReader(in, 2);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			System.out.println(line[1]);
			if(line[1].charAt(8)!='P' && (line[1].charAt(9)=='S' || line[1].charAt(9)=='T')){
				count++;
			}
		}
		reader.close();
		System.out.println(count);
	}
	
	private static void ratioTestDeglyco(String in) throws IOException, JXLException{
		DecimalFormat dfp = DecimalFormats.DF_PRECENT0_2;
		int [] count = new int[2];
		ExcelReader reader = new ExcelReader(in, 1);
		String[] line = reader.readLine();
		int length = line.length;
		while((line=reader.readLine())!=null){
			if(line.length!=length) break;
			double ratio = Double.parseDouble(line[1]);
			count[0]++;
			if(ratio>0.5 && ratio<2)
				count[1]++;
		}
		reader.close();
		System.out.println(count[0]+"\t"+count[1]+"\t"+dfp.format((double)count[1]/(double)count[0]));
	}
	
	private static void ratioTestGlyco(String in) throws IOException, JXLException{
		DecimalFormat dfp = DecimalFormats.DF_PRECENT0_2;
		int [] count = new int[3];
		ExcelReader reader = new ExcelReader(in);
		String[] line = reader.readLine();
		int length = line.length;
		while((line=reader.readLine())!=null){
			if(line.length!=length) break;
			count[0]++;
			
			double ratio = Math.log(Double.parseDouble(line[8]))/Math.log(2);
			double ratio2 = Math.log(Double.parseDouble(line[16]))/Math.log(2);
			
			if(ratio>=-2 && ratio<0){
				count[1]++;
				if(ratio2>=-1 && ratio2<=1){
					count[2]++;
				}
			}
		}
		reader.close();
		System.out.println(count[0]+"\t"+count[1]+"\t"+dfp.format((double)count[1]/(double)count[0])
				+"\t"+count[2]+"\t"+dfp.format((double)count[2]/(double)count[0]));
	}
	
	private static void ratioTestRelative(String in) throws IOException, JXLException{
		DecimalFormat dfp = DecimalFormats.DF_PRECENT0_2;
		int [] count = new int[3];
		ExcelReader reader = new ExcelReader(in);
		String[] line = reader.readLine();
		int length = line.length;
		while((line=reader.readLine())!=null){
			if(line.length!=length) break;
			count[0]++;
			
			double rsd = Double.parseDouble(line[5]);
			double ratio = Math.log(Double.parseDouble(line[4]))/Math.log(2);
			
			if(rsd<0.5){
				count[1]++;
				if(ratio>=-1 && ratio<=1){
					count[2]++;
				}
			}
		}
		reader.close();
		System.out.println(count[0]+"\t"+count[1]+"\t"+dfp.format((double)count[1]/(double)count[0])
				+"\t"+count[2]+"\t"+dfp.format((double)count[2]/(double)count[1]));
	}

	
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

//		Test.countSite("H:\\NGLYCO_QUAN\\NGlycan_quan_20131111\\CID_iden\\percolator_rt\\20131109_serum_HCC_Normal_CID.site.quan.xls");
		Test.ratioTestDeglyco("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\serum\\iden\\20130805_serum_di-labeling_Normal_CID_quantification-2.xls");
//		Test.ratioTestGlyco("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Glycan\\"
//				+ "20130805_4p_di-labeling_HCD_N-glycan_quantification_2_1-2.relative.xls");
//		Test.ratioTestRelative("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Glycan\\"
//				+ "2_1.xls");
	}

}
