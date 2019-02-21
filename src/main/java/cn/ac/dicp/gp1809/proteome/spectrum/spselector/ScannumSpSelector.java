/* 
 ******************************************************************************
 * File: ScannumSpSelector.java * * * Created on 2012-9-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.spselector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import jxl.JXLException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2012-9-17, 16:57:29
 */
public class ScannumSpSelector {
	
	private MzXMLReader reader;
	private MgfWriter writer;
	private HashSet <Integer> scans;
	private String baseName;
	
	public ScannumSpSelector(String in, String out, HashSet <Integer> scans) 
			throws DtaWritingException, FileNotFoundException, XMLStreamException{
		
		File file = new File(in);
		this.reader = new MzXMLReader(file);
		this.writer = new MgfWriter(out, null);
		this.scans = scans;
		this.baseName = file.getName().substring(0, file.getName().length()-6);
		
		this.write();
	}
	
	private void write(){
		
		ISpectrum spectrum = null;
		while((spectrum=reader.getNextSpectrum())!=null){
			int scannum = spectrum.getScanNum();
			if(scans.contains(scannum)){
				
				IMS2Scan ms2scan = (IMS2Scan) spectrum;
				short charge = ms2scan.getCharge();
				
				SequestScanName scanname = new SequestScanName(baseName, scannum, scannum, charge, "dta");
				IMS2PeakList peaklist = ms2scan.getPeakList();
				
				ScanDta scandta = new ScanDta(scanname, peaklist);
				writer.write(scandta);
			}
		}
		
		this.reader.close();
		this.writer.close();
	}
	
	public static void processWFJ120917() throws IOException, JXLException, DtaWritingException, XMLStreamException{
		
		String file = "H:\\Kerwin0603\\120917.xls";
		HashMap <String, HashSet <Integer>> scanmap = new HashMap <String, HashSet<Integer>>(); 
		ExcelReader reader = new ExcelReader(file);
		String [] line = reader.readLine();
		while((line=reader.readLine())!=null){
			if(scanmap.containsKey(line[0])){
				scanmap.get(line[0]).add(Integer.parseInt(line[1]));
			}else{
				HashSet <Integer> set = new HashSet <Integer>();
				set.add(Integer.parseInt(line[1]));
				scanmap.put(line[0], set);
			}
		}
		
		int count = 0;
		String base = "H:\\Kerwin0603\\";
		Iterator <String> it = scanmap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			HashSet <Integer> set = scanmap.get(key);
			count+=set.size();
			System.out.println(key+"\t"+set.size());
			ScannumSpSelector sss = new ScannumSpSelector(base+key+".mzXML", base+key+".mgf", set);
		}
		System.out.println(count);
	}

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws JXLException 
	 * @throws IOException 
	 * @throws DtaWritingException 
	 */
	public static void main(String[] args) throws DtaWritingException, IOException, JXLException, XMLStreamException {
		// TODO Auto-generated method stub

		ScannumSpSelector.processWFJ120917();
	}

}
