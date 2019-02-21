/* 
 ******************************************************************************
 * File: MzDataSAXReader.java * * * Created on 2011-6-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * @author ck
 *
 * @deprecated
 * @version 2011-6-7, 16:02:12
 */
public class MzDataSAXReader implements IMzDataReader, IBatchDtaReader {

	private Element root;
	private Iterator <Element> spit;
	private File file;

	public MzDataSAXReader(String file) throws DocumentException{
		this(new File(file));
	}
	
	public MzDataSAXReader(File file) throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		this.root = document.getRootElement();
		Iterator <Element> splistit = root.elementIterator("spectrumList");
		if(splistit.hasNext()){
			Element el = splistit.next();
			this.spit = el.elementIterator("spectrum");
		}
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
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
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getNextSpectrum()
	 */
	@Override
	public ISpectrum getNextSpectrum() {
		// TODO Auto-generated method stub

		if(spit.hasNext()){
			
			Element spe = spit.next();
			List <Element> list = spe.elements();
			Iterator <Element> it = list.iterator();
			while(it.hasNext()){
				Element ele = it.next();
				System.out.println(ele.getName());
			}
			
/*			
			Iterator <Element> espit = spe.elementIterator();
			while(espit.hasNext()){

				Element espe = espit.next();
				String name = espe.getName();
				
				System.out.println(name);
				
				if(name.equals("spectrumDesc")){
					
					Iterator <Element> descit = espe.elementIterator();
					while(descit.hasNext()){
						Element desele = descit.next();
						if(desele.getName().equals("spectrumSettings")){
							
							Iterator <Element> settingit = desele.elementIterator();
							while(settingit.hasNext()){
								Element setele = settingit.next();
								if(desele.getName().equals("acqSpecification")){
									
								}else if(desele.getName().equals("spectrumInstrument")){
									
								}
							}
							
						}else if(desele.getName().equals("precursorList")){
							
						}
					}
					
					String s = espe.attributeValue("msLevel");
					System.out.println(s);
				}else if(name.equals("mzArrayBinary")){
					
				}else if(name.equals("mzArrayBinary")){
					
				}
			}
*/			
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

	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws DocumentException {
		// TODO Auto-generated method stub

		String file =  "F:\\data\\GlycoQuant\\20110606\\20110605_ASHG_dimethyl_HCD.mzData";
		MzDataSAXReader reader = new MzDataSAXReader(file);
		reader.getNextSpectrum();
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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1ScanList()
	 */
	@Override
	public MS1ScanList getMS1ScanList() {
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
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#getMS1TotalCurrent()
	 */
	@Override
	public double getMS1TotalCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
