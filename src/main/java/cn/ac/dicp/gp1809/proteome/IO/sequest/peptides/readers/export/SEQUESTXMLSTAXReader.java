/*
 * *****************************************************************************
 * File: SEQUESTXMLSTAXReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlReadingException;

/**
 * 
 * Reader for SEQUEST export xml files by Bioworks
 * 
 * @author Xinning
 * @version 0.2.7, 05-03-2009, 20:24:17
 */
@SuppressWarnings("unchecked")
class SEQUESTXMLSTAXReader extends SEQUESTXMLReader {

	private XMLEventReader reader;

	private HashSet<ProteinReference> references;
	
	private ProteinReferencePool pool;

	SEQUESTXMLSTAXReader(String filename, SequestParameter parameter)
	        throws XmlReadingException, ReaderGenerateException {
		this(new File(filename), parameter);
	}

	SEQUESTXMLSTAXReader(File file, SequestParameter parameter)
	        throws XmlReadingException, ReaderGenerateException {
		super(file, parameter);

		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			this.reader = factory
			        .createXMLEventReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new ReaderGenerateException("The file: " + file.getName()
			        + " is unreachable.", e);
		} catch (XMLStreamException e) {
			throw new ReaderGenerateException(
			        "Exception while generating the reader.", e);
		}
		this.getReadeyToRead();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.sequest.peptides.SEQUESTXMLReader#getReadeyToRead
	 * ()
	 */
	@Override
	protected void getReadeyToRead() throws XmlReadingException {
		XMLEvent event;

		try {
			while (true) {
				if ((event = reader.peek()).isStartElement()) {
					String nodename = ((StartElement) event).getName()
					        .getLocalPart();

					if (nodename.equals("bioworksinfo")) {
						reader.nextEvent();
						/*
						 * If not characters, indicate that the text is null.
						 */
						if (event.isCharacters())
							this.bioworksinfo = ((Characters) reader
							        .nextEvent()).getData();
						continue;
					}

					if (nodename.equals("origfilename")) {
						event = reader.nextEvent();
						/*
						 * If not characters, indicate that the text is null.
						 */
						if (event.isCharacters())
							this.originfile = ((Characters) reader.nextEvent())
							        .getData();

						continue;
					}

					if (nodename.equals("protein")) {
						break;
					}
				}

				reader.nextEvent();
			}
		} catch (Exception e) {
			throw new XmlReadingException("Reading exception.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.sequest.peptides.SEQUESTXMLReader#
	 * trigerPositionInfo()
	 */
	@Override
	protected void trigerPositionInfo() {
		//do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.ProReader#close()
	 */
	@Override
	public void close() {

		try {
			this.reader.close();
		} catch (XMLStreamException e) {
			System.err
			        .println("Error in closing the xml file, but it doesn't matter.");
		}

		System.out.println("Finished reading.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.readers
	 * .AbstractSequestPeptideReader#getPeptide()
	 */
	@Override
	protected SequestPeptide getPeptideImp() throws PeptideParsingException {
		try {

			String scanNum = null;
			String sequence = null;
			short charge = -1;
			short rsp = -1;
			double mh = -1f;
			double deltamass = -10f;
			float deltaCn = -1f;
			float xcorr = -1f;
			float sp = -1f;
			String ions = null;
			boolean start = false;

			XMLEvent event;
			while (!(event = reader.nextEvent()).isEndDocument()) {
				if (event.isStartElement()) {
					String nodename = ((StartElement) event).getName()
					        .getLocalPart();

					/*
					 * To skip the partial info which resulted from the
					 * exception of peptide while reading. This is a complete
					 * peptide
					 */
					if (nodename.equals("peptide")) {
						start = true;
						continue;
					}

					if (nodename.equals("file")) {
						scanNum = ((Characters) reader.nextEvent()).getData();
						continue;
					}

					if (nodename.equals("sequence")) {
						sequence = ((Characters) reader.nextEvent()).getData();
						continue;
					}

					if (nodename.equals("mass")) {
						mh = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("deltamass")) {
						deltamass = Double.parseDouble(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("charge")) {
						charge = Short.parseShort(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("xcorr")) {
						xcorr = Float.parseFloat(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("deltacn")) {
						deltaCn = Float.parseFloat(((Characters) reader
						        .nextEvent()).getData());
						continue;
					}

					if (nodename.equals("sp")) {
						sp = Float.parseFloat(((Characters) reader.nextEvent())
						        .getData());
						continue;
					}

					if (nodename.equals("rsp")) {
						rsp = Short
						        .parseShort(((Characters) reader.nextEvent())
						                .getData());
						continue;
					}

					if (nodename.equals("ions")) {
						ions = " "
						        + ((Characters) reader.nextEvent()).getData();
						continue;
					}

					//tic
					//count

					if (nodename.equals("protein")) {
						reader.nextTag();//Reference tag
						this.references = new HashSet<ProteinReference>();
						
						if(this.pool == null)
							this.pool = new ProteinReferencePool(this.getDecoyJudger());
						
						references.add(this.pool.get(((Characters) reader.nextEvent())
						                .getData()));
						continue;
					}

				} else if (event.isEndElement()) {
					String nodename = ((EndElement) event).getName()
					        .getLocalPart();

					if (nodename.equals("peptide")) {

						/*
						 * To skip the partial info which resulted from the
						 * exception of peptide while reading. This is a complete
						 * peptide
						 */
						if (start) {
							String scanname = this.originfile == null
							        || this.originfile.length() == 0 ? this.filename
							        + ", " + scanNum
							        : this.originfile + ", " + scanNum;

							SequestPeptide pep = new SequestPeptide(scanname, sequence,
							        charge, rsp, mh, deltamass, (short) 0,
							        deltaCn, xcorr, sp, ions, references, this
							                .getPeptideFormat());
							pep.setEnzyme(this.getSearchParameter().getEnzyme());
							return pep;
						} else {
							return this.getPeptideImp();
						}

					}
				}
			}

			return null;
		} 
		/*
		 * Skip the invalid peptides
		 */
		catch(ClassCastException e) {
			return getPeptideImp();
		}
		/*
		 * Skip the invalid peptides
		 */
		catch(NumberFormatException e) {
			return getPeptideImp();
		}
		catch (Exception e) {
			this.close();
			throw new PeptideParsingException("Reading exception.", e);
		}
	}

	public static void main(String[] args) throws XmlReadingException,
	        ReaderGenerateException, ParameterParseException,
	        PeptideParsingException {

		SequestParameter parameter = new SequestParameter()
		        .readFromFile(new File(
		        		"D:\\human_0.params"));

		SEQUESTXMLSTAXReader reader = new SEQUESTXMLSTAXReader("d:\\50mM.xml",
		        parameter);

		IPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			System.out.println(pep);
		}

		reader.close();
	}
}
