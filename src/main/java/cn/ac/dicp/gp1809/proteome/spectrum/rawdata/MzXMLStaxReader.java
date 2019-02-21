/*
 * *****************************************************************************
 * File: MzXMLStaxReader.java * * * Created on 07-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Stax stream reader for MzXML file
 * 
 * @author Xinning
 * @version 0.2.1, 06-26-2009, 17:05:12
 */
public class MzXMLStaxReader implements IMzXMLReader, IBatchDtaReader {
	
	private XMLStreamReader reader;
	
	private int precise = 0;
	private boolean isBigEndian, notparsed = true;
	
	private int curtpcount = 0;
	private double curtpreinten;

	public MzXMLStaxReader(String file) throws IOException, XMLStreamException{
		this(new File(file));
	}
	
	public MzXMLStaxReader(File file) throws IOException, XMLStreamException{
		XMLInputFactory fac = XMLInputFactory.newInstance();
		reader = fac.createXMLStreamReader(new FileInputStream(file));
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1ScanList()
	 */
	@Override
	public MS1ScanList getMS1ScanList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1TotalCurrent()
	 */
	@Override
	public double getMS1TotalCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS2PeakList(int)
	 */
	@Override
	public IMS2PeakList getMS2PeakList(int scan_num) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS2ScanList()
	 */
	@Override
	public MS2ScanList getMS2ScanList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextMS1Scan()
	 */
	@Override
	public MS1Scan getNextMS1Scan() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextMS2Scan()
	 */
	@Override
	public MS2Scan getNextMS2Scan() {
		// TODO Auto-generated method stub
		
		Des des = new Des();

		des.scannum = Integer.decode(reader.getAttributeValue(0));
		des.mslevel = Integer.parseInt(reader.getAttributeValue(1));
		this.curtpcount = Integer.parseInt(reader.getAttributeValue(2)) << 1;

		try {
			
			if (des.mslevel != 1) {
				
				reader.nextTag();
				this.curtpreinten = Double.parseDouble(reader.getAttributeValue(0));
				short charge = 0;
				if(reader.getAttributeCount() >= 2) {
					charge = Short.parseShort(reader.getAttributeValue(1));
					des.charge = charge;
				}
				
				reader.next();
				double prems = Double.parseDouble(reader.getText());

				if (des.mslevel == 2) {
					des.precursornum = 1;
					des.prems = prems;
				} else if (des.mslevel == 3) {			
					/*
					 * The MzXML file only give the precursor ion mass in MS1 (same as MS2)
					 */
					des.precursornum = 2;
					des.prems = prems;
				}
				reader.next();
				
				reader.nextTag();
			}

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextSpectrum()
	 */
	@Override
	public ISpectrum getNextSpectrum() {
		// TODO Auto-generated method stub
		
		Des des = new Des();

		des.scannum = Integer.decode(reader.getAttributeValue(0));
		des.mslevel = Integer.parseInt(reader.getAttributeValue(1));
		this.curtpcount = Integer.parseInt(reader.getAttributeValue(2)) << 1;

		try {
			
			if (des.mslevel != 1) {
				
				reader.nextTag();
				this.curtpreinten = Double.parseDouble(reader.getAttributeValue(0));
				short charge = 0;
				if(reader.getAttributeCount() >= 2) {
					charge = Short.parseShort(reader.getAttributeValue(1));
					des.charge = charge;
				}
				
				reader.next();
				double prems = Double.parseDouble(reader.getText());

				if (des.mslevel == 2) {
					des.precursornum = 1;
					des.prems = prems;
				} else if (des.mslevel == 3) {			
					/*
					 * The MzXML file only give the precursor ion mass in MS1 (same as MS2)
					 */
					des.precursornum = 2;
					des.prems = prems;
				}
				reader.next();
				
				reader.nextTag();
				
				if (notparsed) {
					precise = Integer.parseInt(reader.getAttributeValue(0));
					this.isBigEndian = reader.getAttributeValue(1).equals("network") ? true
					        : false;
					notparsed = false;
				}

				reader.next();
				String mzs = reader.getText().trim();

				/*
				 * For unknown reason, the base64 string may not be fully read. That is
				 * only a part of the string can be reader, thus, this statement is
				 * used.
				 */
				if (reader.next() != XMLStreamConstants.END_ELEMENT) {//</data>
					String t = reader.getText().trim();
					if(t.length() > 0)
					mzs += t;
				}

				double precurseMZ = des.mslevel == 2 ? des.prems : des.prems2;
				IMS2PeakList peaklist = Base64Peak.decode(mzs, this.curtpcount, precise, isBigEndian,
				        precurseMZ, this.curtpreinten);
				
//				return Base64Peak.decode(mzs, this.curtpcount, precise, isBigEndian,
//				        precurseMZ, this.curtpreinten);
				
			}else{
				
			}

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getPeakList(int)
	 */
	@Override
	public IPeakList getPeakList(int scan_num) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapMS1ScanList()
	 */
	@Override
	public void rapMS1ScanList() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapMS2ScanList()
	 */
	@Override
	public void rapMS2ScanList() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapScanList()
	 */
	@Override
	public void rapScanList() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getFile()
	 */
	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNameofCurtDta()
	 */
	@Override
	public String getNameofCurtDta() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNextDta(boolean)
	 */
	@Override
	public IScanDta getNextDta(boolean isIncludePeakList)
			throws DtaFileParsingException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNumberofDtas()
	 */
	@Override
	public int getNumberofDtas() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private class Des{
		
		int scannum;
		int mslevel;
		int precursornum;
		double prems;
		double prems2;
		short charge;
		
		Des(){
			
		}
		
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException {
		// TODO Auto-generated method stub

		long startTime=System.currentTimeMillis();
		BufferedReader reader = new BufferedReader(new FileReader("H:\\wiff_control\\120401_FSGP_L_H_in_situ_1mg\\" +
				"120401_FSGP_L_H_in_situ_1mg_2-20120401.mgf"));
		PrintWriter pw = new PrintWriter("H:\\wiff_control\\120401_FSGP_L_H_in_situ_1mg\\proteo_100.txt");
		
		String line = null;
		int count = 0;
		while((line=reader.readLine())!=null){
//			if(line.contains("basePeakMz")){
				pw.write(line+"\n");
				count++;
				if(count>100){
					break;
				}
//			}
			
		}
		reader.close();
		pw.close();
		
		long endTime=System.currentTimeMillis(); 
		System.out.println("Run time:\t"+(endTime-startTime)+"ms"); 
	}

}

