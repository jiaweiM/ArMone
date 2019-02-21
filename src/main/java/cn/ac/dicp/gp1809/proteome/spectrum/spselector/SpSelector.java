/* 
 ******************************************************************************
 * File:SpSelector.java * * * Created on 2010-6-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.spselector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * @author ck
 *
 * @version 2010-6-4, 09:16:07
 */
public class SpSelector {

	private BufferedReader reader;
	private MzXMLReader spReader;
	private IBatchDtaWriter writer;
	private int maxScan;
	private String baseName;
	
	private double intenThres;
	private double mzTole;
	
	public SpSelector(String in, String src, String out, double intenThres, double mzTole) throws IOException, XMLStreamException{
		this.reader = new BufferedReader(new FileReader(in));
		this.spReader = new MzXMLReader(src);
		this.spReader.rapMS2ScanList();
		this.writer = new SequestBatchDtaWriter(out);
		this.maxScan = spReader.getMaxScan();
		String s = (new File(src)).getName();
		int dot = s.lastIndexOf(".");
		baseName = s.substring(0,dot);
		
		this.intenThres = intenThres;
		this.mzTole = mzTole;
	}

	public SpSelector(File in, String src, File out, double intenThres, double mzTole) throws IOException, XMLStreamException{
		this.reader = new BufferedReader(new FileReader(in));
		this.spReader = new MzXMLReader(src);
		this.spReader.rapMS2ScanList();
		this.writer = new SequestBatchDtaWriter(out);
		this.maxScan = spReader.getMaxScan();
		String s = (new File(src)).getName();
		int dot = s.lastIndexOf(".");
		baseName = s.substring(0,dot);
		
		this.intenThres = intenThres;
		this.mzTole = mzTole;
	}
	
	public Double [] getMZs() throws IOException{
		ArrayList <Double> mzlist = new ArrayList<Double>();
		String line;
		while((line=reader.readLine())!=null){
			double d = Double.parseDouble(line.trim());
			mzlist.add(d);
		}
		Double [] mzArray = mzlist.toArray(new Double[mzlist.size()]);
		return mzArray;
	}
	
	public HashSet <Integer> getScans() throws IOException{
		HashSet <Integer> scanList = new HashSet<Integer>();
		String line;
		while((line=reader.readLine())!=null && line.trim().length()>0){
			Integer d = Integer.parseInt(line.trim());
			scanList.add(d);
		}
		return scanList;
	}

	public ArrayList <ScanDta> select() throws IOException{
		ArrayList <ScanDta> scans = new ArrayList <ScanDta> ();
		Double [] mzs = getMZs();
		for(int i=0;i<maxScan;i++){
			IMS2PeakList peaklist = spReader.getMS2PeakList(i);
			IPeak basepeak = peaklist.getBasePeak();
			boolean use = false;
			if(peaklist!=null){				
				IPeak [] peaks = peaklist.getPeakArray();
				double baseInten = basepeak.getIntensity();
Label:			for(int j=0;j<peaks.length;j++){
					double mz = peaks[j].getMz();
					double inten = peaks[j].getIntensity();
					for(int k=0;k<mzs.length;k++){
						if(Math.abs(mz-mzs[k])<mzTole){
							if(inten/baseInten > intenThres){
								use = true;
								break Label;
							}							
						}
					}
				}									
			}
			if(use){
				short charge = peaklist.getPrecursePeak().getCharge();
				SequestScanName scanname = new SequestScanName(baseName,i,i,charge,"dta");
				ScanDta scandta = new ScanDta(scanname,peaklist);
				scans.add(scandta);
			}
		}
		return scans;
	}
	
	public ArrayList <ScanDta> select2() throws IOException{
		ArrayList <ScanDta> scans = new ArrayList <ScanDta> ();
		String line;
		boolean begin = false;
		while((line=reader.readLine())!=null){
			if(begin){
				String [] strs1 = line.split("\",");
				
				if(strs1.length==2){
					String [] strs2 = strs1[0].split(",");
					String [] scanTitle = strs1[1].split(",")[1].split("\\s");
					if(scanTitle.length>15){
						int scanNumBeg = Integer.parseInt(scanTitle[14]);
						int scanNumEnd = scanNumBeg;
						short charge = Short.parseShort(strs2[11]);
						SequestScanName scanname = new SequestScanName(baseName,scanNumBeg,scanNumEnd,charge,"dta");
						IMS2PeakList peaklist = spReader.getMS2PeakList(scanNumBeg);
						ScanDta scandta = new ScanDta(scanname,peaklist);
						scans.add(scandta);
					}else{
						String [] scanTitle2 = strs1[1].split("\\.");
						int scanNumBeg = Integer.parseInt(scanTitle2[scanTitle2.length-4]);
						int scanNumEnd = Integer.parseInt(scanTitle2[scanTitle2.length-3]);
						short charge = Short.parseShort(scanTitle2[scanTitle2.length-2]);
						SequestScanName scanname = new SequestScanName(baseName,scanNumBeg,scanNumEnd,charge,"dta");
						IMS2PeakList peaklist = spReader.getMS2PeakList(scanNumBeg);
						ScanDta scandta = new ScanDta(scanname,peaklist);
						scans.add(scandta);
					}
				}else if(strs1.length==4){
					String [] scanTitle = strs1[3].split(",")[1].split("\\s");
					if(scanTitle.length>15){
						int scanNumBeg = Integer.parseInt(scanTitle[14]);
						int scanNumEnd = scanNumBeg;
						String [] strs5 = strs1[2].split(",");
						short charge = Short.parseShort(strs5[8]);
						SequestScanName scanname = new SequestScanName(baseName,scanNumBeg,scanNumEnd,charge,"dta");
						IMS2PeakList peaklist = spReader.getMS2PeakList(scanNumBeg);
						ScanDta scandta = new ScanDta(scanname,peaklist);
						scans.add(scandta);
					}else{
						String [] scanTitle2 = strs1[3].split("\\.");
						int scanNumBeg = Integer.parseInt(scanTitle2[scanTitle2.length-4]);
						int scanNumEnd = Integer.parseInt(scanTitle2[scanTitle2.length-3]);
						short charge = Short.parseShort(scanTitle2[scanTitle2.length-2]);
						SequestScanName scanname = new SequestScanName(baseName,scanNumBeg,scanNumEnd,charge,"dta");
						IMS2PeakList peaklist = spReader.getMS2PeakList(scanNumBeg);
						ScanDta scandta = new ScanDta(scanname,peaklist);
						scans.add(scandta);
					}				
				}
			}
			if(line.startsWith("prot_hit_num"))
				begin = true;
		}
		return scans;
	}
	
	public ArrayList <ScanDta> select3() throws IOException{
		ArrayList <ScanDta> scans = new ArrayList <ScanDta> ();
		HashSet <Integer> intset = this.getScans();
		Iterator <Integer> it = intset.iterator();
		while(it.hasNext()){
			int i = it.next();
			IMS2PeakList peaklist = spReader.getMS2PeakList(i);
			short charge = peaklist.getPrecursePeak().getCharge();
			SequestScanName scanname = new SequestScanName(baseName,i,i,charge,"dta");
			ScanDta scandta = new ScanDta(scanname,peaklist);
			scans.add(scandta);
		}
		return scans;
	}
	
	public void write(ArrayList <ScanDta> scans) throws DtaWritingException{
		Iterator <ScanDta> it = scans.iterator();
		while(it.hasNext()){
			ScanDta dta = it.next();
			writer.write(dta);
		}
		writer.close();
	}
	
	public void write() throws DtaWritingException, IOException{
		ArrayList <ScanDta> scans = this.select();
		write(scans);
	}

	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DtaWritingException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws IOException, DtaWritingException, XMLStreamException {
		long startTime=System.currentTimeMillis();
		
		String mz = "C:\\Inetpub\\wwwroot\\ISB\\data\\20110408dimeth_18O_L1+H3.txt";
		String src = "C:\\Inetpub\\wwwroot\\ISB\\data\\20110408_dime_18O_L1+H3.mzXML";
		String out = "C:\\Inetpub\\wwwroot\\ISB\\data\\20110408dimeth_18O_L1+H3";
		SpSelector select = new SpSelector(mz,src,out, 0.1, 1.0);
		ArrayList <ScanDta> scans = select.select3();
		select.write(scans);

/*		
		File file = new File("F:\\data\\result");
		FileFilter fileFilter = new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".csv")){
	                return true;
	            }
	            return false;
	        }
	    };
	    
	    File [] files = file.listFiles(fileFilter);
	    for(int i=0;i<files.length;i++){
	    	String s = files[i].getName();
	    	String name = s.substring(0, s.length()-4);
	    	String peak = "F:\\data\\result\\"+name+".mzXML";
	    	String dir = "F:\\data\\result\\"+name;
	    	File out = new File(dir);
	    	if(!out.exists())
	    		out.mkdir();
	    	SpSelector select = new SpSelector(files[i],peak,out);
	    	ArrayList <ScanDta> scans = select.select2();
			select.write(scans);
	    }
		
*/		
		long endTime=System.currentTimeMillis(); 
	}

}
