/* 
 ******************************************************************************
 * File:MSQTxtReader.java * * * Created on 2010-4-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.diMethyl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author ck
 *
 * @version 2010-4-10, 10:22:36
 */
public class MSQTxtReader {
	
	public final static DecimalFormat df3= new DecimalFormat(".###");
	
	public static String read(String fileName) throws IOException{	
		StringBuffer sf = new StringBuffer();
		BufferedReader in = 
			new BufferedReader(new FileReader(fileName));
		String s;
		while((s=in.readLine())!=null){
			sf.append(s);
			sf.append("\n");
		}
		in.close();
		return sf.toString();
	}
	
	public static String [] readList(String fileName) throws IOException{
		String [] list;
		list=(read(fileName).split("\n"));
		return list;
	}

	public static void ProRatio(String [] strList){
		for(int i=10;i<strList.length;i++){
			String [] sp = strList[i].split("\t");
			if(sp[0].equalsIgnoreCase("PROTEIN")){
				System.out.println(sp[2]+"\t"+sp[14]);
			}
		}
	}
	
	public static void ProRatio2(String [] strList){
		int total = 0;
		int t0812 = 0;
		int t0520 = 0;
		for(int i=2;i<strList.length;i++){
			String [] sp = strList[i].split("\t");
			if(sp[0].trim().length()>0){
				total++;
				System.out.println(sp[1]+"\t"+sp[3]);
				double ratio = Double.parseDouble(sp[3].trim());
				if(ratio>0.5 && ratio<2){
					t0520++;
				}
				if(ratio>0.8 && ratio<1.2){
					t0812++;
				}
			}
		}
		System.out.println(total);
		System.out.println(t0520);
		System.out.println(t0812);
	}
	
	public static String [] reverseInten(String [] strList) throws Exception{
		for(int i=10;i<strList.length;i++){
			String [] sp = strList[i].split("\t");

			if(sp.length>120){
				
				if(sp.length!=127)
					throw new Exception("Incorrect column number.");
				
				if(sp[98].startsWith("Int"))
					continue;
//				if(sp[109].equalsIgnoreCase("true")){
					String temp;
					temp = sp[98];
					sp[98] = sp[108];
					sp[108] = temp;
//				}
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(sp[0]);
			for(int j=1;j<sp.length;j++){
				sb.append("\t");
				sb.append(sp[j]);
			}
			
			strList[i] = sb.toString();
		}
		
		return strList;
	}
	

	public static String [] reverseXIC(String [] strList) throws Exception{
		for(int i=10;i<strList.length;i++){
			String [] sp = strList[i].split("\t");

			if(sp.length>120){
				
				if(sp.length!=127)
					throw new Exception("Incorrect column number.");
				if(sp[97].startsWith("XIC"))
					continue;
//				if(sp[109].equalsIgnoreCase("true")){
					String temp;
					temp = sp[97];
					sp[97] = sp[107];
					sp[107] = temp;
//				}
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(sp[0]);
			for(int j=1;j<sp.length;j++){
				sb.append("\t");
				sb.append(sp[j]);
			}
			
			strList[i] = sb.toString();
		}
		
		return strList;
	}
	
	public static void writeList(String [] list,String fileName) throws IOException{
		PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(fileName)));

		for(int i=0;i<list.length;i++){
			String str=list[i];
			out.println(str);
		}
		out.close();
	}
	
	public static void batchRW(String filePath) throws Exception{
		File file = new File(filePath);
		ArrayList <String> txtList = new ArrayList<String>();
		String [] filelist = null;
		
		if(file.isDirectory()){
			filelist = file.list(); 
			for(String name:filelist){
				if(name.endsWith(".txt")){
					txtList.add(name);
				}
			}
		}
		for(String tName:txtList){
//			File newFile = new File(filePath+"\\Int-W-D-X-H-1\\");
//			newFile.mkdir();
			String xName = filePath+"\\XIC-"+tName;		
			String iName = filePath+"\\Int-"+tName;		
			String [] readlistX = readList(filePath+"\\"+tName);
			String [] readlistI = readList(filePath+"\\"+tName);
			writeList(reverseXIC(readlistX), xName);
			writeList(reverseInten(readlistI), iName);
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String [] readlist = readList("E:\\Data\\SCX-ONLINE-DIMETHYL\\FDR1%\\RES.txt");
		ProRatio2(readlist);
//		System.out.println(readlist.length);

//		writeList(reverseInten(readlist),"D:\\My Documents\\120mM-2.txt");
//		batchRW("E:\\Data\\W-D-X-H-3");

	}

}
