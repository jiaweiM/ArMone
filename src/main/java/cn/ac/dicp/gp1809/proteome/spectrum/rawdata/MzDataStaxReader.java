/*
 ******************************************************************************
 * File: MzDataStaxReader.java * * * Created on 11-21-2007
 *
 * Copyright (c) 2007 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.MS1PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2.MS2ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.util.SpectrumUtil;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.SmartTimer;

/**
 * StaxReader for Mzdata file
 * 
 * @author Xinning
 * @version 0.2.2, 05-02-2010, 22:18:26
 * @version 0.3, 06-07-2011
 */
public class MzDataStaxReader implements IMzDataReader, IBatchDtaReader {
	
	private XMLStreamReader reader;
	private File file;
	
	private MS1ScanList ms1ScanList;
	private MS2ScanList ms2ScanList;
	
	private String baseName;
	
	private double MS1TotalCurrent;
	
	public MzDataStaxReader(String filename) throws FileNotFoundException,
    	XMLStreamException {
		this(new File(filename));
	}
	
	public MzDataStaxReader(File file) throws FileNotFoundException,
	        XMLStreamException {
		
		XMLInputFactory fac = XMLInputFactory.newInstance();
		reader = fac.createXMLStreamReader(new FileInputStream(file));
		
		this.file = file;
		this.ms1ScanList = new MS1ScanList(DtaType.MZDATA);
		this.ms2ScanList = new MS2ScanList(DtaType.MZDATA);
		
		String name = file.getName();
		int loc = name.lastIndexOf(".");
		if(loc==-1){
			this.baseName = name;
		}else{
			this.baseName = name.substring(0,loc);
		}
	}

	/**
	 * The describtion of the scans;
	 * 
	 * @param reader
	 * @return
	 * @throws XMLStreamException
	 */
	private Description getDescripsion()
	        throws XMLStreamException {
		
		Description des = new Description();

		while (reader.hasNext()) {
			//currently, only one element is used
			
			int next = reader.next();
			
			if(next == XMLStreamConstants.START_ELEMENT){
				
				if(reader.getName().getLocalPart().equals("acquisition")){
					int scannum = Integer.parseInt(reader.getAttributeValue(0));
					des.setScanNum(scannum);
				}
				
				if(reader.getName().getLocalPart().equals("spectrumInstrument")){
					int mslev = Integer.parseInt(reader.getAttributeValue(0));
					des.setLevel(mslev);
				}
				
				if(reader.getName().getLocalPart().equals("cvParam")){
					if(reader.getAttributeValue(2).equals("TimeInMinutes")){
						double rt = Double.parseDouble(reader.getAttributeValue(3));
						des.setRenTimeMinute(rt);
						break;
					}
				}
			}
		}

		/*
		 * The MS1 contains no precursor ions
		 */
		if (des.getLevel() > 1){
			
			this.getPrecursorList(des);
			return des;
			
		}else if(des.getLevel()==1){
			return des;
			
		}else{
			return null;
		}
		
//		reader.next();//end of spectrumDes

	}
/*
	private void getSpectrumSetting(XMLStreamReader reader, Description des)
	        throws XMLStreamException {
		
		String end = "spectrumSettings";

		while (true) {
			//currently, only one element is used
			if (reader.next() == XMLStreamConstants.START_ELEMENT
			        && reader.getName().getLocalPart().equals(
			                "spectrumInstrument")) {
				
				int mslev = Integer.parseInt(reader.getAttributeValue(0));
				des.setLevel(mslev);
				break;
			}
		}

		//interat to the end;
		while (!(reader.next() == XMLStreamConstants.END_ELEMENT && reader
		        .getName().getLocalPart().equals(end)))
			;

	}
*/
	/**
	 * The terms in cvParam
	 * 
	 * @param reader
	 * @param des
	 * @throws XMLStreamException
	 */
	private void getPrecursorList(Description des)
	        throws XMLStreamException {

		boolean preLevel1 = false;
		
		while (reader.hasNext()) {
			//currently, only one element is used
			
			int next = reader.next();
			
			if(next == XMLStreamConstants.START_ELEMENT){
				
				if(reader.getName().getLocalPart().equals("precursor")){
					int prelevel = Integer.parseInt(reader.getAttributeValue(0));
					if(prelevel==1){
						preLevel1 = true;
					}else{
						preLevel1 = false;
					}
				}
				
				if (reader.getName().getLocalPart().equals("cvParam")) {
					
					if(des.getLevel()==2){
						
						if(reader.getAttributeValue(2).equals("MassToChargeRatio")){
							
							double mz = Double.parseDouble(reader.getAttributeValue(3));
							des.setPreMs(mz);
							
						}else if(reader.getAttributeValue(2).equals("ChargeState")){
							
							int charge = Integer.parseInt(reader.getAttributeValue(3));
							des.setCharge(charge);
							
						}else if(reader.getAttributeValue(2).equals("Intensity")){
							
							double preInten = Double.parseDouble(reader.getAttributeValue(3));
							des.setPrecursorInten(preInten);						
						}
						
					}else if(des.getLevel()==3){
						
						if(preLevel1){
							
							if(reader.getAttributeValue(2).equals("MassToChargeRatio")){
								
								double mz = Double.parseDouble(reader.getAttributeValue(3));
								des.setPreMs(mz);
								
							}else if(reader.getAttributeValue(2).equals("ChargeState")){
								
								int charge = Integer.parseInt(reader.getAttributeValue(3));
								des.setCharge(charge);
								
							}else if(reader.getAttributeValue(2).equals("Intensity")){
								
								double preInten = Double.parseDouble(reader.getAttributeValue(3));
								des.setPrecursorInten(preInten);						
							}
							
						}else{
							
							if(reader.getAttributeValue(2).equals("MassToChargeRatio")){
								
								double mz = Double.parseDouble(reader.getAttributeValue(3));
								des.setPreMs2(mz);
								
							}else if(reader.getAttributeValue(2).equals("ChargeState")){
								
								int charge = Integer.parseInt(reader.getAttributeValue(3));
								des.setCharge(charge);
								
							}else if(reader.getAttributeValue(2).equals("Intensity")){
								
								double preInten = Double.parseDouble(reader.getAttributeValue(3));
								des.setPrecursorInten2(preInten);						
							}
						}
						
						des.setPreNum(2);
					}

				}else if(reader.getName().getLocalPart().equals("precursor")){

					String att0 = reader.getAttributeLocalName(0);
					if(att0.equals("spectrumRef")){
						
						int preScanNum = Integer.parseInt(reader.getAttributeValue(0));
						des.setPrecursorScanNum(preScanNum);
						
					}else if(att0.equals("msLevel")){
						
						int prelevel = Integer.parseInt(reader.getAttributeValue(0));
						if(prelevel==1){
							preLevel1 = true;
						}else{
							preLevel1 = false;
						}
					}
				}
			}else if(next==XMLStreamConstants.END_ELEMENT){
				if (reader.getName().getLocalPart().equals("spectrumDesc")) {
					break;
				}
			}
		}
	}

	private IPeakList getPeaks(Description des)
	        throws XMLStreamException {

		while (reader.hasNext()) {
			//currently, only one element is used
			
			int next = reader.next();

			if (next == XMLStreamConstants.START_ELEMENT
			        && reader.getName().getLocalPart().equals("mzArrayBinary")) {

				//Skip useless info
				while (reader.next() != XMLStreamConstants.START_ELEMENT
				        || !reader.getName().getLocalPart().equals("data"))
					;

				int precise = Integer.parseInt(reader.getAttributeValue(0));
				boolean isBigEndian = reader.getAttributeValue(1).equals(
				        "little") ? false : true;

				int pcount = Integer.parseInt(reader.getAttributeValue(2));

				if(reader.next()!=XMLStreamConstants.CHARACTERS) {
					System.err.println();
					System.err.println("Scan "+des.getScanNum()+" contains no peak list");

					/*
					 * No peak in this scan
					 */
					IPeakList peaklist = new MS1PeakList();
					peaklist.add(new Peak(0,0));
					return peaklist;
				}
				
				String mzs = reader.getText().trim();

				/*
				 * For unknown reason, the base64 string may not be fully read.
				 * That is only a part of the string can be reader, thus, this
				 * statement is used.
				 */
				while (reader.next() != XMLStreamConstants.END_ELEMENT) {//</data>
					String t = reader.getText().trim();
					if(t.length() > 0)
						mzs += t;
				}

				//The intensity
				while (reader.next() != XMLStreamConstants.START_ELEMENT
				        || !reader.getName().getLocalPart().equals(
				                "data")) ;

				reader.next();
				String intens = reader.getText().trim();

				/*
				 * For unknown reason, the base64 string may not be fully read.
				 * That is only a part of the string can be reader, thus, this
				 * statement is used.
				 */
				while (reader.next() != XMLStreamConstants.END_ELEMENT) {
					String t = reader.getText().trim();
					if(t.length() > 0)
						intens += t;
				}
				
				int level = des.getLevel();
				if(level==1){
					
					return Base64Peak.decodeMS1(mzs, intens, pcount, precise, isBigEndian);
					
				}else if(level==2){
					
					int preScanNum = des.getPrecursorScanNum();
					double precurseMZ = des.getPreMs();
					double precurseInten = des.getPrecursorInten();
					int charge = des.getCharge();
					double rt = des.getRenTimeMinute();
					PrecursePeak ppeak = new PrecursePeak(preScanNum, precurseMZ, precurseInten);
					ppeak.setCharge((short) charge);
					ppeak.setRT(rt);

					return Base64Peak.decodeMS2(mzs, intens, pcount, precise,
					        isBigEndian, ppeak);
					
				}else{
					
					int preScanNum = des.getPrecursorScanNum();
					double precurseMZ = des.getPreMs2();
					double precurseInten = des.getPrecursorInten2();
					int charge = des.getCharge();
					double rt = des.getRenTimeMinute();
					PrecursePeak ppeak = new PrecursePeak(preScanNum, precurseMZ, precurseInten);
					ppeak.setCharge((short) charge);
					ppeak.setRT(rt);

					return Base64Peak.decodeMS2(mzs, intens, pcount, precise,
					        isBigEndian, ppeak);
				}
			}
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#close()
	 */
	@Override
	public void close() {
		try {
			this.reader.close();
			this.ms1ScanList = null;
			this.ms2ScanList = null;
			System.gc();
		} catch (XMLStreamException e) {
			System.err
			        .println("Error while closing the reader, but it doesn't matter :)");
		}
	}

	@Override
	public IPeakList getPeakList(int scan_num) {
		// TODO Auto-generated method stub
		return this.ms1ScanList.getScan(scan_num).getPeakList();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS2PeakList(int)
	 */
	@Override
	public IMS2PeakList getMS2PeakList(int scan_num) {
		// TODO Auto-generated method stub

		return (IMS2PeakList) this.ms2ScanList.getScan(scan_num).getPeakList();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextMS1Scan()
	 */
	@Override
	public MS1Scan getNextMS1Scan() {
		// TODO Auto-generated method stub
		
		try {
			Description des = this.getDescripsion();
			
			if(des==null)
				return null;
			
			if(des.getLevel()==1){
				
				IPeakList peaklist = this.getPeaks(des);
				MS1Scan spec = new MS1Scan(des, peaklist);
				
				this.MS1TotalCurrent += peaklist.getTotIonCurrent();

				return spec;
				
			}else{
				return getNextMS1Scan();
			}
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextMS2Scan()
	 */
	@Override
	public MS2Scan getNextMS2Scan() {
		// TODO Auto-generated method stub
		
		try {
			Description des = this.getDescripsion();
			
			if(des==null)
				return null;
			
			if(des.getLevel()==1){
				
				return getNextMS2Scan();
				
			}else{

				IPeakList peaklist = this.getPeaks(des);
				MS2Scan spec = new MS2Scan(des, (IMS2PeakList) peaklist);
				
				return spec;
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
		try {
			
			Description des = this.getDescripsion();
			if(des==null)
				return null;
			
			IPeakList peaklist = this.getPeaks(des);
			ISpectrum spec;
			
			if(des.getLevel()==1){
				spec = new MS1Scan(des, peaklist);
			}else{
				spec = new MS2Scan(des, (IMS2PeakList) peaklist);
			}
			
			return spec;
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapMS1ScanList()
	 */
	@Override
	public void rapMS1ScanList() {
		// TODO Auto-generated method stub
		MS1Scan spec;
		while((spec=getNextMS1Scan())!=null){
			this.ms1ScanList.add(spec);
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapMS2ScanList()
	 */
	@Override
	public void rapMS2ScanList() {
		// TODO Auto-generated method stub
		MS2Scan spec;
		while((spec=getNextMS2Scan())!=null){
			this.ms2ScanList.add(spec);
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#rapScanList()
	 */
	@Override
	public void rapScanList() {
		// TODO Auto-generated method stub
		ISpectrum spec;
		while((spec=getNextSpectrum())!=null){
			if(spec.getMSLevel()==1)
				this.ms1ScanList.add(spec);
			else
				this.ms2ScanList.add(spec);
		}
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1ScanList()
	 */
	@Override
	public MS1ScanList getMS1ScanList() {
		// TODO Auto-generated method stub
		return this.ms1ScanList;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS2ScanList()
	 */
	@Override
	public MS2ScanList getMS2ScanList() {
		// TODO Auto-generated method stub
		return this.ms2ScanList;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getDtaType()
	 */
	@Override
	public DtaType getDtaType() {
		// TODO Auto-generated method stub
		return DtaType.MZDATA;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getFile()
	 */
	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return file;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNameofCurtDta()
	 */
	@Override
	public String getNameofCurtDta() {
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNextDta(boolean)
	 */
	@Override
	public IScanDta getNextDta(boolean isIncludePeakList)
			throws DtaFileParsingException {
		// TODO Auto-generated method stub
		
		MS2Scan ms2scan = this.getNextMS2Scan();
		int scanNum = ms2scan.getScanNum();
		double premz = ms2scan.getPrecursorMZ();
		short charge = ms2scan.getCharge();
		double mh = SpectrumUtil.getMH(premz, charge);
		IMS2PeakList peaklist = ms2scan.getPeakList();
		
		IScanName parseName = new SequestScanName(baseName, scanNum, scanNum, charge, "dta");

		MS2ScanDta ms2dta;
		if(isIncludePeakList){
			ms2dta = new MS2ScanDta(parseName, peaklist);
		}else{
			ms2dta = new MS2ScanDta(parseName, mh);
		}

		return ms2dta;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNumberofDtas()
	 */
	@Override
	public int getNumberofDtas() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1TotalCurrent()
	 */
	@Override
	public double getMS1TotalCurrent() {
		// TODO Auto-generated method stub
		return  MS1TotalCurrent;
	}

	public static void main(String[] args) throws XMLStreamException,
	        FileNotFoundException {
		SmartTimer timer = new SmartTimer();
		MzDataStaxReader reader = new MzDataStaxReader(
		        "F:\\data\\ModDatabase\\Phos_ZMY\\20110224_HelaD_1_bioworks.xml");
		
//		reader.getNextMS1Scan();
		reader.rapMS2ScanList();

		System.out.println(timer);
	}

	

}
