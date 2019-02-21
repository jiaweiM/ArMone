/*
 * *****************************************************************************
 * File: XMLProReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.AbstractSequestPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.ISequestPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.ProphetXmlProReader;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlReadingException;

/**
 * Reader factory for all the xml file. Including bioworks output protein xml
 * and peptide prophet pepxml.
 * 
 * @author Xinning
 * @version 0.2.6, 11-01-2008, 16:49:47
 */
public class XMLProReader extends AbstractSequestPeptideReader{
	
	private ISequestPeptideReader reader;
	
	private SequestParameter parameter;
	
	/**
	 * Create a reader for all the xml file. Including bioworks output protein xml
	 * and peptide prophet pepxml.
	 * @throws XmlReadingException 
	 */
	public XMLProReader(String filename, SequestParameter parameter) throws 
			ReaderGenerateException, ImpactReaderTypeException, XmlReadingException{
		this(new File(filename), parameter);
	}
	
	/**
	 * Create a reader for all the xml file. Including bioworks output protein xml
	 * and peptide prophet pepxml.
	 * @throws XmlReadingException 
	 */
	public XMLProReader(File file, SequestParameter parameter) throws 
			ReaderGenerateException, ImpactReaderTypeException, XmlReadingException{
		super(file);
		
		reader = this.createReader(file, parameter);
		this.parameter = parameter;
	}
	
	private ISequestPeptideReader createReader(File file, SequestParameter parameter) throws 
				ReaderGenerateException, ImpactReaderTypeException, XmlReadingException{
		String line = null;
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			br.readLine();
			line = br.readLine();//the second line
		}catch(FileNotFoundException fe){
			throw new ReaderGenerateException("The target file: "+file.getName()+" is not reachable!");
		}
		catch(IOException e){
			throw new ReaderGenerateException("Error in openning and reading the target file: "+file.getName());
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				throw new ReaderGenerateException("Error occurs while closing the target file: "+file.getName());
			}
		}
		
		if(line==null){
			throw new ReaderGenerateException("The selected file contains no information!");
		}
		
		if(line.contains("http://www.w3.org/2001/XMLSchema-instance")){
			return SEQUESTXMLReader.createReader(file, parameter);
		}
		// TPP format
		else if(line.startsWith("<?xml-stylesheet type=\"text/xsl\" href=")){
			return new ProphetXmlProReader(file, parameter);
		}
		else{
			throw new ReaderGenerateException("Unknown type of xml.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getPeptide()
	 */
	@Override
	public SequestPeptide getPeptide() throws PeptideParsingException {
		return reader.getPeptide();
	}
	
	
	/**
	 * Same as {@link #getPeptide()}
	 * 
	 * @throws PeptideParsingException 
	 */
	@Override
	protected SequestPeptide getPeptideImp() throws PeptideParsingException{
		return reader.getPeptide();
	}
	
	public void close(){
		reader.close();
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader
	 * 		#getSearchParameter()
	 */
    public SequestParameter getSearchParameter() {
	    return this.parameter;
    }
    
    /*
     * (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getTopN()
     */
    @Override
	public int getTopN() {
		return reader.getTopN();
	}

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#setTopN(int)
	 */
	@Override
	public void setTopN(int topn) {
		this.reader.setTopN(topn);
	}
}
