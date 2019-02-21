/* 
 ******************************************************************************
 * File: MS2_MS3_ID_Comparator.java * * * Created on 03-28-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.usefulTools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListAccesser;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;

/**
 * Compare the identification from MS2 and MS3. This is used for 
 * the getting of phosphopeptides identified by the same MS2&MS3 pair
 * 
 * @author Xinning
 * @version 0.1, 03-28-2010, 22:09:14
 */
public class MS2_MS3_ID_Comparator {
	
	private String ms2ppl, ms3ppl;
	private int ms2_ms3_events;
	private String outputppl;
	
	private HashMap<Integer, Info> ms2map;
	private HashMap<Integer, Info> ms3map;
	
	public MS2_MS3_ID_Comparator(String ms2ppl, String ms3ppl, int ms2_ms3_events, String outputppl) {
		this.ms2_ms3_events = ms2_ms3_events;
		this.ms2ppl = ms2ppl;
		this.ms3ppl = ms3ppl;
		this.outputppl = outputppl;
	}
	
	
	public void process() throws FileDamageException, IOException {
		String output_match = outputppl.substring(0,outputppl.length()-3)+"match.txt";
		String output_ms2 = outputppl.substring(0,outputppl.length()-3)+"ms2_only.txt";
		String output_ms3 = outputppl.substring(0,outputppl.length()-3)+"ms3_only.txt";
		
		PrintWriter pw_match = new PrintWriter(output_match);
		PrintWriter pw_ms2 = new PrintWriter(output_ms2);
		PrintWriter pw_ms3 = new PrintWriter(output_ms3);
		
		this.buildingIndex(this.ms2ppl, this.ms3ppl, ms2_ms3_events);
		
		PeptideListAccesser ms2accesser = new PeptideListAccesser(this.ms2ppl);
		PeptideListAccesser ms3accesser = new PeptideListAccesser(this.ms3ppl);
		
		Info[] infos = this.ms2map.values().toArray(new Info[this.ms2map.size()]);
		
		for(Info info: infos) {
			int idx = info.idx;
			boolean match = info.match;
			IPeptide pep = ms2accesser.getPeptide(idx);
			
			if(match) {
				String useq = pep.getPeptideSequence().getUniqueSequence();
				IPeptide pep3 = ms3accesser.getPeptide(info.idx2);
				
				boolean same = useq.equals(pep3.getPeptideSequence().getUniqueSequence());
				
				pw_match.print(pep);
				pw_match.print(pep3);
				pw_match.println("\t"+same);
				
			}else {
				pw_ms2.println(pep);
			}
		}
		
		infos = this.ms3map.values().toArray(new Info[this.ms3map.size()]);
		
		for(Info info: infos) {
			int idx = info.idx;
			boolean match = info.match;
			IPeptide pep = ms3accesser.getPeptide(idx);
			
			if(match) {
				//do nothing, already processed before
				
			}else {
				pw_ms3.println(pep);
			}
		}
		
		ms2accesser.close();
		ms3accesser.close();
		pw_match.close();
		pw_ms2.close();
		pw_ms3.close();
		
	}
	
	/**
	 * @throws IOException 
	 * @throws FileDamageException 
	 * 
	 */
	private void buildingIndex(String ms2ppl, String ms3ppl, int ms2_ms3_events) throws FileDamageException, IOException {
		
		ms2map = this.buildMap(ms2ppl);
		ms3map = this.buildMap(ms3ppl);
		
		Integer[] scans = ms3map.keySet().toArray(new Integer[ms3map.size()]);
		
		for(Integer scan : scans) {
			int v = scan;
			Info ms3info = ms3map.get(scan);
			double mz = ms3info.mz;
			for(int i=1; i<= ms2_ms3_events; i++) {
				int ms2v = v -i;
				Info info;
				if((info = ms2map.get(ms2v))!=null) {
					double loss = info.mz - mz;
					
					if(this.equals(loss)) {
						info.match = true;
						ms3info.match = true;
						ms3info.idx2 = info.idx;
						ms3info.scan2 = info.scan;
						info.idx2 = ms3info.idx;
						info.scan2 = ms3info.scan;
						
						break;
					}
				}
			}
		}
	}
	
	
	private boolean equals(double loss) {
		
		if(Math.abs(loss - 98)<=0.8) {
			return true;
		}
		
		if(Math.abs(loss - 49)<=0.8) {
			return true;
		}
		
		if(Math.abs(loss - 32.6)<=0.8) {
			return true;
		}
		
		if(Math.abs(loss - 24.5)<=0.8) {
			return true;
		}
		
		return false;
	}
	
	
	private HashMap<Integer, Info> buildMap(String ppl) throws FileDamageException, IOException{
		HashMap<Integer, Info> map = new HashMap<Integer, Info>();
		PeptideListReader reader = new PeptideListReader(ppl);
		
		IPeptide pep;
		while((pep = reader.getPeptide())!=null) {
			double mh = pep.getDeltaMH()+pep.getMH();
			double mz = SpectrumUtil.getMZ(mh, pep.getCharge());
			Info info = new Info();
			info.mz = mz;
			info.idx = reader.getCurtPeptideIndex();
			int scan = pep.getScanNumBeg();
			
			if(map.get(scan)!=null) {
				System.out.println("wahoo+"+scan);
			}
			
			map.put(scan, info);
		}
		
		reader.close();
		return map;
	}
	
	private static class Info{
		private double mz;
		private boolean match;
		private int scan;
		private int idx;
		private int scan2;
		private int idx2;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException {
		String ms2 = "E:\\APIVASEII\\cd3\\results\\human_liver_new\\sequest\\ms3\\ms2+ms3\\ms2_all.1%.phos.ppl";
		String ms3 = "E:\\APIVASEII\\cd3\\results\\human_liver_new\\sequest\\ms3\\ms2+ms3\\ms3_all.1%.ppl";
		String output = "E:\\APIVASEII\\cd3\\results\\human_liver_new\\sequest\\ms3\\ms2+ms3\\ms2+ms3.txt";
		int events = 3;
		
		MS2_MS3_ID_Comparator comp = new MS2_MS3_ID_Comparator(ms2, ms3, events, output);
		comp.process();
	}

}
