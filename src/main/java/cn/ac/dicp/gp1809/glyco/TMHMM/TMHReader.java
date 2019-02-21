/* 
 ******************************************************************************
 * File: TMHMMAnalysis.java * * * Created on 2013-7-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.TMHMM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import jxl.JXLException;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 * 
 * @version 2013-7-28, 13:26:03
 * 
 * @see
 * http://www.cbs.dtu.dk/services/TMHMM/
 */
public class TMHReader {
	
	private ArrayList<TMHProtein> prolist;
	
	public TMHReader(){
		this.prolist = new ArrayList<TMHProtein>();
	}
	
	public void parseFileXls(String xls) throws IOException, JXLException{
		
		ExcelReader reader = new ExcelReader(xls);
		String [] line = null;
		TMHProtein tmhProtein = null;
		
		while((line=reader.readLine())!=null){
			
			if(line.length==0){
				continue;
				
			}else if(line.length==1){
				
				if(line[0].contains("plot in postscript")){
					
					if(tmhProtein.getNumOfTHMs()==0){
						if(tmhProtein.getInside().size()==0 && tmhProtein.getOutside().size()==1){
							tmhProtein.setNumOfTHMs(-1);
						}
					}
					this.prolist.add(tmhProtein);
					continue;
				}
				
				String [] cs = line[0].split("[^A-Za-z^0-9^.]+");

				if(cs[1].startsWith("IPI")){
					
					String ipi = cs[2];
					String attibute = cs[cs.length-2];

					if(attibute.equals("Length")){

						tmhProtein = new TMHProtein(ipi);
						tmhProtein.setLength(Integer.parseInt(cs[cs.length-1]));
						
					}else if(attibute.equals("TMHs")){
						
						if(cs[cs.length-3].equals("predicted")){
							
							tmhProtein.setNumOfTHMs(Integer.parseInt(cs[cs.length-1]));
							
						}else if(cs[cs.length-3].equals("in")){
							
							tmhProtein.setExpAAs(Double.parseDouble(cs[cs.length-1]));
						}
					}else if(attibute.equals("AAs")){
						
						tmhProtein.setFirst60AAs(Double.parseDouble(cs[cs.length-1]));
						
					}else if(attibute.equals("N-in")){
						
						tmhProtein.setProbOfN_in(Double.parseDouble(cs[cs.length-1]));
					}

				}else if(cs[0].startsWith("plot")){
					this.prolist.add(tmhProtein);
				}
				
			}else{
				
				if(line[2].startsWith("inside")){

					String [] cs = line[3].split("[\\W]+");
					int begin = Integer.parseInt(cs[0]);
					int end = Integer.parseInt(cs[1]);
					tmhProtein.addInsite(new int []{begin, end});
					
				}else if(line[2].startsWith("TMhelix")){

					String [] cs = line[3].split("[\\W]+");
					int begin = Integer.parseInt(cs[0]);
					int end = Integer.parseInt(cs[1]);
					tmhProtein.addTMhelix(new int []{begin, end});
					
				}else if(line[2].startsWith("outside")){
					
					String [] cs = line[3].split("[\\W]+");
					int begin = Integer.parseInt(cs[0]);
					int end = Integer.parseInt(cs[1]);
					tmhProtein.addOutside(new int []{begin, end});
				}
			}
		}
		
		reader.close();
	}

	public ArrayList<TMHProtein> getTMHProteinList(){
		return this.prolist;
	}
	
	private static void test(String in) throws IOException, JXLException{
		
		TMHReader reader = new TMHReader();
		reader.parseFileXls(in);
		
		HashMap<Integer, ArrayList<TMHProtein>> map = new HashMap<Integer, ArrayList<TMHProtein>>();
		ArrayList<TMHProtein> list = reader.getTMHProteinList();
		for(int i=0;i<list.size();i++){
			TMHProtein pro = list.get(i);
			int thms = pro.getNumOfTHMs();
			if(map.containsKey(thms)){
				map.get(thms).add(pro);
			}else{
				ArrayList <TMHProtein> l0 = new ArrayList<TMHProtein>();
				l0.add(pro);
				map.put(thms, l0);
			}
		}
		
		
		for(Integer thms : map.keySet()){
			System.out.println(thms+"\t"+map.get(thms).size()+"\t"+(double)map.get(thms).size()/(double)list.size());
			ArrayList <TMHProtein> l0 = map.get(thms);
//			if(thms==0){
//				for(TMHProtein pro : l0){
//					System.out.println(pro.getIpi()+"\t"+thms);
//				}
//			}
		}
	}
	
	private static void resultTypeTest(String tmh, String result) throws IOException, JXLException{

		TMHReader reader = new TMHReader();
		reader.parseFileXls(tmh);
		
		HashMap<String, TMHProtein> map = new HashMap<String, TMHProtein>();
		ArrayList<TMHProtein> list = reader.getTMHProteinList();
		for(int i=0;i<list.size();i++){
			TMHProtein pro = list.get(i);
			int thms = pro.getNumOfTHMs();
			if(thms>=1){
				map.put(pro.getIpi(), pro);
//				System.out.println(pro.getIpi());
			}
		}
		
		System.out.println(map.size());
		
		HashMap <String, HashSet <String>> [] transMap = new HashMap [4];
		for(int i=0;i<transMap.length;i++){
			transMap[i] = new HashMap<String, HashSet <String>>();
		}
		
		/**
		 * high mannose, inside, outside, trans
		 * complex with NeuAc, inside, outside, trans
		 * complex without NeuAc, inside, outside, trans
		 */
		int [][] count = new int [3][3];
		ExcelReader xlsreader = new ExcelReader(result, 1);
		String [] line = xlsreader.readLine();
		while((line=xlsreader.readLine())!=null){
			
			String ref = line[16];
			String type = line[9];
			String name = line[8];
			int site = Integer.parseInt(line[17]);
			
			String refsite = ref+"_"+site;
			String ipi = ref.substring(4, ref.indexOf("|"));

			if(map.containsKey(ipi)){

				TMHProtein pro = map.get(ipi);
				ArrayList <int[]> inside = pro.getInside();
				ArrayList <int[]> outside = pro.getOutside();
				ArrayList <int[]> trans = pro.getTMhelix();
				
//				System.out.println(inside.size()+"\t"+outside.size()+"\t"+trans.size());
				
				for(int i=0;i<inside.size();i++){
					int [] range = inside.get(i);
					if(site>=range[0] && site<=range[1]){
						if(transMap[1].containsKey(refsite)){
							transMap[1].get(refsite).add(name);
						}else{
							HashSet<String> set = new HashSet<String>();
							set.add(name);
							transMap[1].put(refsite, set);
						}
						if(type.equals("High mannose")){
							count[0][0]++;
						}else{
							if(name.contains("NeuAc")){
								System.out.println(Arrays.toString(line));
								count[1][0]++;
							}else{
								count[2][0]++;
							}
						}
						break;
					}
				}
				
				for(int i=0;i<outside.size();i++){
					int [] range = outside.get(i);
					if(site>=range[0] && site<=range[1]){
						if(transMap[3].containsKey(refsite)){
							transMap[3].get(refsite).add(name);
						}else{
							HashSet<String> set = new HashSet<String>();
							set.add(name);
							transMap[3].put(refsite, set);
						}
						if(type.equals("High mannose")){
							count[0][1]++;
						}else{
							if(name.contains("NeuAc")){
								count[1][1]++;
							}else{
								count[2][1]++;
							}
						}
						break;
					}
				}
				
				for(int i=0;i<trans.size();i++){
					int [] range = trans.get(i);
					if(site>=range[0] && site<=range[1]){
						if(transMap[2].containsKey(refsite)){
							transMap[2].get(refsite).add(name);
						}else{
							HashSet<String> set = new HashSet<String>();
							set.add(name);
							transMap[2].put(refsite, set);
						}
						if(type.equals("High mannose")){
							count[0][2]++;
						}else{
							if(name.contains("NeuAc")){
								count[1][2]++;
							}else{
								count[2][2]++;
							}
						}
						break;
					}
				}
				
			}else{
				if(transMap[0].containsKey(refsite)){
					transMap[0].get(refsite).add(name);
				}else{
					HashSet<String> set = new HashSet<String>();
					set.add(name);
					transMap[0].put(refsite, set);
				}
			}
		}
	
		for(int i=0;i<count.length;i++){
			for(int j=0;j<count[i].length;j++){
				System.out.print(count[i][j]+"\t");
			}
			System.out.println();
		}
		
		int [] cc = new int [transMap.length];
		for(int i=0;i<transMap.length;i++){
			System.out.println(transMap[i].size());
			
			HashMap<String, Integer> namemap = new HashMap<String, Integer>();
			for(String key : transMap[i].keySet()){
				cc[i]+=transMap[i].get(key).size();
				HashSet <String> nameset = transMap[i].get(key);
				for(String name : nameset){
					if(namemap.containsKey(name)){
						namemap.put(name, namemap.get(name)+1);
					}else{
						namemap.put(name, 1);
					}
				}
			}

			for(String name : namemap.keySet()){
				System.out.println(i+"\t"+name+"\t"+namemap.get(name));
			}
		}
		System.out.println(Arrays.toString(cc));
	}
	
	private static void resultTestSite(String tmh, String result) throws IOException, JXLException{

		TMHReader reader = new TMHReader();
		reader.parseFileXls(tmh);
		
		HashMap<String, TMHProtein> map = new HashMap<String, TMHProtein>();
		ArrayList<TMHProtein> list = reader.getTMHProteinList();
		for(int i=0;i<list.size();i++){
			TMHProtein pro = list.get(i);
			int thms = pro.getNumOfTHMs();
			if(thms>=1){
				map.put(pro.getIpi(), pro);
//				System.out.println(pro.getIpi());
			}
		}
		
		System.out.println(map.size());

		HashSet <String> set = new HashSet <String>();
		ExcelReader xlsreader = new ExcelReader(result, 2);
		String [] line = xlsreader.readLine();
		HashMap<String, int[]> promap = new HashMap<String ,int[]>();
		
		while((line=xlsreader.readLine())!=null){
			
			String ipi = line[0].substring(4, line[0].indexOf("|"));
			int site = Integer.parseInt(line[1]);
			int count = Integer.parseInt(line[4]);

			if(map.containsKey(ipi)){
				
				if(!promap.containsKey(ipi)){
					promap.put(ipi, new int[4]);
				}

				TMHProtein pro = map.get(ipi);
				ArrayList <int[]> inside = pro.getInside();
				ArrayList <int[]> outside = pro.getOutside();
				ArrayList <int[]> trans = pro.getTMhelix();

				int [] counts = promap.get(ipi);
				
				for(int i=0;i<inside.size();i++){
					int [] range = inside.get(i);
					if(site>=range[0] && site<=range[1]){
						set.add(line[0]+"\t"+line[1]);
						counts[0]++;
						counts[1]+=count;
					}
				}
				
				for(int i=0;i<outside.size();i++){
					int [] range = outside.get(i);
					if(site>=range[0] && site<=range[1]){
						counts[2]++;
						counts[3]+=count;
					}
				}
				
				for(int i=0;i<trans.size();i++){
					int [] range = trans.get(i);
					if(site>=range[0] && site<=range[1]){
						
					}
				}
				
			}else{
				
			}
		}
		
		for(String inside : promap.keySet()){
			System.out.println(inside+"\t"+Arrays.toString(promap.get(inside)));
		}
	}
	
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, JXLException {
		// TODO Auto-generated method stub

		TMHReader.test("H:\\NGLYCO\\NGlyco_final_20130725\\TMHMM\\TMHMM_10_1.xls");
//		TMHReader.resultTypeTest("H:\\NGlyco_final_20130725\\TMHMM\\TMHMM_10_1.xls", 
//				"H:\\NGlyco_final_20130725\\All16_filtered_10.combine.xls");
		
//		TMHReader.resultTestSite("H:\\NGlyco_final_20130730\\Fuction\\TMHMM.xls", 
//				"H:\\NGlyco_final_20130730\\RT10.2D.xls");
	}

}
