/* 
 ******************************************************************************
 * File: OGlycanByonicTest.java * * * Created on 2014年3月20日
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author ck
 *
 * @version 2014年3月20日, 上午8:54:08
 */
public class OGlycanByonicTest {
	
	private static void getTypes(String in) throws IOException{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
//		String[] title = line.split("\t");
		int glycanid = 3;
//		for(int i=0;i<title.length;i++){
//			if(title[i].startsWith("Glycans")){
//				glycanid = i;
//			}
//		}
//		System.out.println(glycanid);
		while((line=reader.readLine())!=null){
			String glycan = line.split("\t")[glycanid];
			if(glycan.trim().length()==0) continue;
			String[] glycans = glycan.split(",");
			for(int i=0;i<glycans.length;i++){
				if(map.containsKey(glycans[i])){
					map.put(glycans[i], map.get(glycans[i])+1);
				}else{
					map.put(glycans[i], 1);
				}
			}
		}
		reader.close();
		
		String[] keys = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(keys);
		for(int i=0;i<keys.length;i++){
			System.out.println(keys[i]+"\t"+map.get(keys[i]));
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		OGlycanByonicTest.getTypes("H:\\OGLYCAN\\OGlycan_final_20140312\\20140314\\fetuin\\"
				+ "Byonic\\fetuin.alloriginal.mgf(Byonic_14-03-18_10.49.00.381)\\新建文本文档.txt");
	}

}
