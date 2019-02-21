/* 
 ******************************************************************************
 * File:MascotPercolatorDataReader.java * * * Created on 2012-8-23
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.group;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParser;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.MascotDatParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;

/**
 * @author ck
 *
 * @version 2012-8-23, 10:28:33
 */
public class MascotPercolatorDataReader {
	
	private BufferedReader reader;
	
	public MascotPercolatorDataReader(String file) throws FileNotFoundException{
		this.reader = new BufferedReader(new FileReader(file));
	}
	
	public static HashSet <Integer> getQuerySet(String file, float maxQ) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		HashSet <Integer> queryset = new HashSet <Integer>();
		String line = reader.readLine();
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("PSMId"))
				break;
			
			String [] cs = line.split("\t");
			String [] ss = cs[0].split("[;:]");
			
			int query = Integer.parseInt(ss[1]);
			float qValue = Float.parseFloat(cs[2]);

			if(qValue<maxQ && !queryset.contains(query)){
				queryset.add(query);
			}
		}
		reader.close();
		return queryset;
	}
	
	public static HashMap <Integer, Integer> getQueryMap(String file, float maxQ) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		HashMap <Integer, Integer> querymap = new HashMap <Integer, Integer>();
		String line = reader.readLine();
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("PSMId"))
				break;

			String [] cs = line.split("\t");
			String [] ss = cs[0].split("[;:]");
//			System.out.println(line);
			int query = Integer.parseInt(ss[1]);
			int rank = Integer.parseInt(ss[3]);
			float qValue = Float.parseFloat(cs[2]);

			if(qValue<maxQ && !querymap.containsKey(query)){
				querymap.put(query, rank);
			}
		}
		reader.close();
		return querymap;
	}
	
	public static HashMap <Integer, Integer> getQueryMap(String file, float maxQ, float minScore) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		HashMap <Integer, Integer> querymap = new HashMap <Integer, Integer>();
		String line = reader.readLine();
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("PSMId"))
				break;

			String [] cs = line.split("\t");
			String [] ss = cs[0].split("[;:]");
			
			int query = Integer.parseInt(ss[1]);
			int rank = Integer.parseInt(ss[3]);
			float score = Float.parseFloat(cs[1]);
			float qValue = Float.parseFloat(cs[2]);

			if(qValue<maxQ && !querymap.containsKey(query) && score>=minScore){
				querymap.put(query, rank);
			}
		}
		reader.close();
		return querymap;
	}
	
	public MascotPercolatorData [] getDatas(float maxQ) throws IOException{
		
		HashSet <Integer> queryset = new HashSet <Integer>();
		ArrayList <MascotPercolatorData> list = new ArrayList<MascotPercolatorData>();
		String line = reader.readLine();
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("PSMId"))
				break;
			
			String [] cs = line.split("\t");
			String [] ss = cs[0].split("[;:]");
			
			int query = Integer.parseInt(ss[1]);
			int rank = Integer.parseInt(ss[3]);
			
			float score = Float.parseFloat(cs[1]);
			float qValue = Float.parseFloat(cs[2]);
			double PEP = Float.parseFloat(cs[3]);
			String seq = cs[4].substring(2, cs[4].length()-2);
			String [] refs = new String[cs.length-5];
			System.arraycopy(cs, 5, refs, 0, refs.length);
			
			if(qValue<maxQ && !queryset.contains(query)){
				MascotPercolatorData data = new MascotPercolatorData(query, rank, score, 
						qValue, PEP, seq, refs);
				list.add(data);
				queryset.add(query);
			}
		}
		
		MascotPercolatorData [] datas = new MascotPercolatorData[list.size()];
		datas = list.toArray(datas);
/*		Arrays.sort(datas, new Comparator<MascotPercolatorData>(){

			@Override
			public int compare(MascotPercolatorData arg0,
					MascotPercolatorData arg1) {
				// TODO Auto-generated method stub
				if(arg0.getScore()<arg1.getScore())
					return 1;
				else if(arg0.getScore()>arg1.getScore())
					return -1;
				return 0;
			}
			
		});
*/		
		return datas;
	}
	
	public void close() throws IOException{
		this.reader.close();
		System.gc();
	}
	
	public void phosDownTest(String in, String id) throws IOException{
		
		HashSet <String> set = this.getPepSet(in, id);
		
		int target = 0;
		MascotPercolatorData [] datas = this.getDatas(0.01f);

		for(int i=0;i<datas.length;i++){
			String seq = datas[i].getSeq();
			if(set.contains(seq))
				target++;
		}
		System.out.println(datas.length+"\t"+target+"\t"+(datas.length-target));
	}
	
	public void shufTest() throws IOException{
		
		int target = 0;
		MascotPercolatorData [] datas = this.getDatas(0.01f);
L:		for(int i=0;i<datas.length;i++){
			String [] refs = datas[i].getRefs();
			for(int j=0;j<refs.length;j++){
				if(refs[j].startsWith("REV")){
					continue L;
				}
			}
			target++;
		}
		System.out.println(datas.length+"\t"+target+"\t"+(datas.length-target));
	}

	private HashSet <String> getPepSet(String in, String id) throws IOException{
		
		HashSet <String> set = new HashSet <String>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null && line.trim().length()>0){
			String [] ss = line.split("\t");
			if(ss[0].startsWith(id))
				set.add(ss[5]);
		}
		reader.close();
		return set;
	}
	
	public void getScan(String dat) throws FileDamageException, IOException, MascotDatParsingException{
		
		HashSet <Integer> querySet = new HashSet <Integer>();
		MascotPercolatorData [] datas = this.getDatas(0.01f);
		System.out.println(datas.length);
		for(int i=0;i<datas.length;i++){
			int query = datas[i].getQuery();
			if(querySet.contains(query)){
				System.out.println("~~~~~\t"+query);
			}else
				querySet.add(query);
		}
		System.out.println(querySet.size());
		
		HashSet <Integer> scanSet = new HashSet <Integer>();
		MascotDatParser parser = new MascotDatParser(dat);
		int num = parser.getQueryNum();
		for(int i=1; i<num; i++){
			String qName = parser.getQueryIdx(i).getQname();
			IScanName scanName = ScanNameFactory.parseName(qName);
			int scannum = scanName.getScanNumBeg();
//			System.out.println(i+"\t"+parser.getQueryIdx(i).getQidx()+"\t"+parser.getQueryIdx(i).getQname());
			if(querySet.contains(i)){
				scanSet.add(scannum);
			}
		}
		System.out.println(scanSet.size());
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MascotDatParsingException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws IOException, FileDamageException, MascotDatParsingException {
		// TODO Auto-generated method stub

//		String file = "H:\\Validation\\Byy_phos_5600_velos\\1_F002908_NoRev_percolator_percolator.dat.tab.txt";
		String file = "H:\\xubo\\120911_DAT_files\\" +
				"run2_DAT\\50ughela-4-run2.dat.tab.txt";
		
//		String dat = "H:\\Validation\\SILAC\\2_F002909_NoRev_percolator.dat";
//		MascotPercolatorDataReader reader = new MascotPercolatorDataReader(file);
//		System.out.println(reader.getDatas(0.01f).length);
		
//		reader.phosDownTest("H:\\Validation\\phospho_download\\" +
//				"Literature\\peptide.txt", "5");
		
//		reader.shufTest();
//		reader.getScan(dat);
//		reader.close();
		HashMap<Integer, Integer> querymap = MascotPercolatorDataReader.getQueryMap("L:\\Data_DICP\\turnover" +
				"\\20130916-HeLa_PT_turnover_orbit\\DATfiles_130916_HeLa_PT\\20130916HeLaPT0_3_48h_100mM_F001536.dat.tab.txt", 0.01f);
		System.out.println("~~~~~~~~~~\t"+querymap.get(8348));
	}

}
