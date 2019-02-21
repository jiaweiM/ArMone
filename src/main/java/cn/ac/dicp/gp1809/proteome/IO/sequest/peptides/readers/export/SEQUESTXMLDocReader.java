/*
 * *****************************************************************************
 * File: SEQUESTXMLDocReader.java * * * Created on 09-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ProReaderConstant;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * A Reader for XML file outputted by Biowroks in protein list form. This reader
 * is implemented in text file reader form, that is, the XML file is read as a
 * totally none formated plain text file. In this condition, the reading should
 * be very fast, as it is a direct reading.
 * 
 * @author Xinning
 * @version 0.2.5, 05-02-2010, 10:33:01
 */
public class SEQUESTXMLDocReader extends SEQUESTXMLReader implements
        ProReaderConstant {

	/*
	 * when parse the xml string how many byte to forward
	 * eg.\t\t<sequence>K.S]EIAHR.F</sequence> 2 byte must be ignore;
	 */
	private static final int PEPTIDEFORWARD = 1;

	/**
	 * The label position of peptide and protein
	 */
	protected int[] proteinlabelposition, peptidelabelposition;

	private int[] proteinlabellength, peptidelabellength;
	private BufferUtil bfutil;
	private RandomAccessFile raf;

	//name of current protein for the peptides
	private String currentProtein = null;

	SEQUESTXMLDocReader(String filename, SequestParameter parameter)
	        throws ImpactReaderTypeException, ReaderGenerateException {

		this(new File(filename), parameter);
	}

	SEQUESTXMLDocReader(File file, SequestParameter parameter)
	        throws ImpactReaderTypeException, ReaderGenerateException {
		super(file, parameter);

		try {
			raf = new RandomAccessFile(file, "r");
			bfutil = new BufferUtil(raf.getChannel().map(
			        FileChannel.MapMode.READ_ONLY, 0, raf.length()));
		} catch (FileNotFoundException fne) {
			throw new ReaderGenerateException(
			        "Error in creating reader, make sure "
			                + "the file is valid and reachable");
		} catch (IOException e) {
			close();
			throw new ImpactReaderTypeException("XMLReader unsuit Exception");
		}

		this.trigerPositionInfo();
		bfutil.rewind();

		this.getReadeyToRead();
	}

	@Override
	protected void trigerPositionInfo() {
		String line;
		while (!(line = bfutil.readLine()).equals("<protein>")) {
			if (line.startsWith("<sequestresults")) {
				//no used??
			} else if (line.startsWith("<origfilename")) {
				this.originfile = line.substring(14, line.indexOf('<', 14) - 4);
				usefilename = (originfile.length()==0);
			} else if (line.startsWith("<bioworksinfo")) {
				this.bioworksinfo = line.substring(14, line.indexOf('<', 14));
			}
		}

		/*
		 * get the reference type of the file;
		 */
		List<String> proteinheader = new ArrayList<String>();
		List<String> peptideheader = new ArrayList<String>();
		String tempindex;

		while (!(line = bfutil.readLine()).equals("\t<peptide>")) {
			tempindex = line.substring(2, line.indexOf('>'));
			proteinheader.add(tempindex);
		}
		proteinlabellength = new int[proteinheader.size()];//array of length of protein label
		proteinlabelposition = new int[proteinheader.size()];//array of location of label
		for (int i = 0; i < proteinheader.size(); i++) {
			String temp = proteinheader.get(i);
			proteinlabellength[i] = temp.length() + 2 + 1;//<>+1

			if (temp.equals("reference"))
				proteinlabelposition[i] = 0;

			/*
			 * used for other info, else if(temp.equals("consensus_score"))
			 * proteinlabelposition[i] = 1; else if(temp.equals("pI"))
			 * proteinlabelposition[i] = 2; else if(temp.equals("weight"))
			 * proteinlabelposition[i] = 3; else if(temp.equals("accession"))
			 * proteinlabelposition[i] = 4;
			 */
			else
				proteinlabelposition[i] = -1;

		}

		while (!(line = bfutil.readLine()).equals("\t</peptide>")) {
			tempindex = line.substring(PEPTIDEFORWARD + 2, line.indexOf('>'));
			peptideheader.add(tempindex);
		}

		peptidelabellength = new int[peptideheader.size()];//array of length of protein label
		peptidelabelposition = new int[peptideheader.size()];//array of location of label

		for (int i = 0; i < peptideheader.size(); i++) {
			String temp = peptideheader.get(i);
			peptidelabellength[i] = temp.length() + 2 + 1;//<>+1

			if (temp.equals("file"))
				peptidelabelposition[i] = scanColumn;
			else if (temp.equals("sequence"))
				peptidelabelposition[i] = sequenceColumn;
			else if (temp.equals("mass"))
				peptidelabelposition[i] = massColumn;
			else if (temp.equals("deltamass"))
				peptidelabelposition[i] = deltaMassColumn;
			else if (temp.equals("charge"))
				peptidelabelposition[i] = chargeColumn;
			else if (temp.equals("xcorr"))
				peptidelabelposition[i] = xcorrColumn;
			else if (temp.equals("deltacn"))
				peptidelabelposition[i] = deltaCnColumn;
			else if (temp.equals("sp"))
				peptidelabelposition[i] = spColumn;
			else if (temp.equals("rsp"))
				peptidelabelposition[i] = rspColumn;
			else if (temp.equals("ions"))
				peptidelabelposition[i] = ionsColumn;
			else
				peptidelabelposition[i] = -1;
		}

	}

	@Override
	protected void getReadeyToRead() {
		while (!bfutil.readLine().equals("<protein>"))
			;

		bfutil.position(bfutil.getBSLinePosition());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .AbstractPeptideReader#getPeptideImp()
	 */
	@Override
	protected SequestPeptide getPeptideImp() {

		String line;
		String peptideArray[] = new String[peptideIndexLength];
		int count = 0, tempposition;
		while (!(line = bfutil.readLine()).equals("</sequestresults>")) {

			if (line.equals("\t<peptide>")) {

				while (!(line = bfutil.readLine()).equals("\t</peptide>")) {

					if ((tempposition = peptidelabelposition[count]) >= 0) {
						peptideArray[tempposition] = xmlPeptideStringParse(
						        line, peptidelabellength[count], PEPTIDEFORWARD);
					}
					count++;
				}

				if (this.currentProtein.length() < MIN_PRO_REF_LEN) {
					System.out.println("Peptide: \""
					        + peptideArray[sequenceColumn]
					        + "\" with illegal protein reference: \""
					        + this.currentProtein + "\", was ignored");
					count = 0;
					continue;
				}
			} else if (line.equals("<protein>")) {
				while (!(line = bfutil.readLine()).equals("\t<peptide>")) {
					if ((tempposition = proteinlabelposition[count]) >= 0) {
						this.currentProtein = xmlProteinStringParse(line,
						        proteinlabellength[count]);
					}
					count++;
				}

				count = 0;

				while (!(line = bfutil.readLine()).equals("\t</peptide>")) {
					if ((tempposition = peptidelabelposition[count]) >= 0)
						peptideArray[tempposition] = xmlPeptideStringParse(
						        line, peptidelabellength[count], PEPTIDEFORWARD);

					count++;
				}

				if (this.currentProtein.length() < MIN_PRO_REF_LEN) {
					System.out.println("Peptide: \""
					        + peptideArray[sequenceColumn]
					        + "\" with illegal protein reference: \""
					        + this.currentProtein + "\", was ignored");
					count = 0;
					continue;
				}
			} else if (line.equals("</protein>")) {
				continue;
			} else {
				System.out.println(line);
				throw new RuntimeException("Xml Reading Pattern Exception");
			}

			peptideArray[proteinColumn] = this.currentProtein;

			/*
			 * The original file need to be added before the scan number
			 */
			
				peptideArray[scanColumn] = this.usefilename ? this.filename + ", "
				        + peptideArray[scanColumn] : originfile + ", "
				        + peptideArray[scanColumn];
			

			SequestPeptide pep = new SequestPeptide(peptideArray, this.getPeptideFormat());
			pep.setEnzyme(this.getSearchParameter().getEnzyme());
			return pep;
		}

		this.close();
		return null;// null
	}

	/*
	 * take xml string as input,
	 */
	private static String xmlPeptideStringParse(String xmlstring,
	        int labellength, int forward) {
		return xmlstring.substring(labellength + forward, xmlstring.length()
		        - labellength);
	}

	private static String xmlProteinStringParse(String xmlstring,
	        int labellength) {
		return xmlstring.substring(labellength, xmlstring.length()
		        - labellength);
	}

	public void close() {
		if (this.raf != null) {
			try {
				this.raf.close();
			} catch (IOException e) {
				System.out.println("Error in closing the file after reading."
				        + " But it doesn't matter :)");
			}
		}
		System.out.println("Finished reading.");
	}

	public String getScanNum() {
		return null;
	}

	public static void main(String[] args) throws ImpactReaderTypeException,
	        ReaderGenerateException, ParameterParseException,
	        PeptideParsingException {

		SequestParameter parameter = new SequestParameter()
		        .readFromFile(new File("D:\\human_0.params"));

		SEQUESTXMLDocReader reader = new SEQUESTXMLDocReader("d:\\50mM.xml",
		        parameter);

		IPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			System.out.println(pep);
		}

		reader.close();
	}
}
