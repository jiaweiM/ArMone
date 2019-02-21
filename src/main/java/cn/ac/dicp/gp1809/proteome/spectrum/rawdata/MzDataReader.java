/*
 ******************************************************************************
 * File: MzDataReader.java * * * Created on 11-21-2007 
 *
 * Copyright (c) 2007 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * Document reader for Mzdata file;
 * @deprecated
 * @author Xinning
 * @version 0.2, 02-25-2009, 14:17:03
 */
public class MzDataReader implements IMzDataReader, IBatchDtaReader {
	/**
	 * Reader of this type is to read a file as text format;
	 */
	public static final int TYPE_TEXT = 0;
	/**
	 * Reader of this type if to read a file using Dom4j as a xml format;
	 */
//	public static final int TYPE_DOM4J = 1;
	/**
	 * Reader of this type if to read a file using STAX (XMLStreamReader) as a
	 * xml format;
	 */
	public static final int TYPE_STAX = 2;

	private IMzDataReader mzreader;

	/**
	 * Default using STAX xml reading strategy
	 * 
	 * @param filename
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public MzDataReader(String filename) throws FileNotFoundException, XMLStreamException {
		this(filename, TYPE_STAX);
	}

	public MzDataReader(String filename, int type) throws FileNotFoundException, XMLStreamException {
		this(new File(filename), type);
	}

	public MzDataReader(File file, int type) throws FileNotFoundException, XMLStreamException {
		this.mzreader = createMzDataReader(file, type);
	}

	/**
	 * Static method to creat a reader using reader type specified;
	 * 
	 * @param filename
	 * @param type
	 *            : TYPE_TEXT TYPE_DOM4J TYPE_STAX
	 * @return reader
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public static IMzDataReader createMzDataReader(String filename, int type) throws FileNotFoundException, XMLStreamException {
		IMzDataReader reader = null;
		if (type == 0)
			reader = new MzDataStaxReader(filename);
		else if (type == 1) {
			throw new RuntimeException("Current, Dom4jReader is not used yet.");
			/*
			 * try { reader = new MzDataDom4jReader(filename); } catch
			 * (DocumentException e) { throw new
			 * RuntimeException("Error in creating MzDataDom4jReader!"); }
			 */
		} else
			try {
				reader = new MzDataStaxReader(filename);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Can't find file: " + filename);
			} catch (XMLStreamException e) {
				throw new RuntimeException(
				        "Error in creating MzDataStaxReader!");
			}

		return reader;
	}

	/**
	 * Static method to creat a reader using reader type specified;
	 * 
	 * @param file
	 * @param type
	 *            : TYPE_TEXT TYPE_DOM4J TYPE_STAX
	 * @return reader
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public static IMzDataReader createMzDataReader(File file, int type) throws FileNotFoundException, XMLStreamException {
		IMzDataReader reader = null;
		if (type == 0)
			reader = new MzDataStaxReader(file);
		else if (type == 1) {
			throw new RuntimeException("Current, Dom4jReader is not used yet.");
			/*
			 * try { reader = new MzDataDom4jReader(filename); } catch
			 * (DocumentException e) { throw new
			 * RuntimeException("Error in creating MzDataDom4jReader!"); }
			 */
		} else
			try {
				reader = new MzDataStaxReader(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Can't find file: " + file);
			} catch (XMLStreamException e) {
				throw new RuntimeException(
				        "Error in creating MzDataStaxReader!");
			}

		return reader;
	}

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader#close()
     */
    @Override
    public void close() {
    	this.mzreader.close();
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
