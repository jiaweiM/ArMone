/*
 * *****************************************************************************
 * File: ProphetXmlProReader.java * * * Created on 08-29-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import cn.ac.dicp.gp1809.exceptions.UnSupportingMethodException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.DefaultSequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.util.SmartTimer;

/**
 * Getting peptide from PeptideProphet outputted xml file.
 * 
 * @author Xinning
 * @version 0.4.9, 05-25-2010, 15:12:30
 */
public class ProphetXmlProReader extends AbstractSequestPeptideReader {

	private XMLStreamReader reader;
	private SequestParameter parameter;
	private Enzyme enzyme;

	private ProteinReferencePool pool;
	
	private DefaultSequestPeptideFormat formatter = new DefaultSequestPeptideFormat();

	public ProphetXmlProReader(String filename, SequestParameter parameter)
	        throws ImpactReaderTypeException {
		this(new File(filename), parameter);
	}

	public ProphetXmlProReader(File file, SequestParameter parameter)
	        throws ImpactReaderTypeException {
		super(file);

		XMLInputFactory fac = XMLInputFactory.newInstance();
		try {
			reader = fac.createXMLStreamReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File: " + file.getName()
			        + " unreachable!");
		} catch (XMLStreamException e) {
			close();
			throw new ImpactReaderTypeException(
			        "ProphetXmlReader unsuit Exception");
		}

		this.pool = new ProteinReferencePool();

		this.enzyme = parameter.getEnzyme();
		this.parameter = parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .AbstractPeptideReader#getPeptideImp()
	 */
	@Override
	protected SequestPeptide getPeptideImp() {
		float probability = -1f;
		int event;
		try {
			while ((event = reader.next()) != XMLStreamConstants.END_DOCUMENT) {// end
				// of
				// document
				if (event == XMLStreamConstants.START_ELEMENT
				        && reader.getName().getLocalPart().equals(
				                "spectrum_query")) {

					String scan = this.getFormatedScan(reader
					        .getAttributeValue(0));// scan
					short charge = Short
					        .parseShort(reader.getAttributeValue(4));// charge

					reader.next();//
					reader.next();// "search_result"
					reader.next();//
					reader.next();// "search_hit"

					char left = reader.getAttributeValue(2).charAt(0);
					if (left == '.')
						left = '-';
					char right = reader.getAttributeValue(3).charAt(0);
					if (right == '.')
						right = '-';

					String sequence = PeptideUtil.formatSequence(reader
					        .getAttributeValue(1), left, right);
					double dms = Double
					        .parseDouble(reader.getAttributeValue(9));// diff
					// mh
					double mh = Double.parseDouble(reader.getAttributeValue(8)) + 1f;// mh
					String ions = reader.getAttributeValue(6) + "/"
					        + reader.getAttributeValue(7);// ions

					int pronum = Integer.parseInt(reader.getAttributeValue(5));

					HashSet<ProteinReference> refs = new HashSet<ProteinReference>();
					refs.add(this.pool.get(reader.getAttributeValue(4)));// protein
					reader.next();
					if (pronum > 1) {

						for (int i = 1; i < pronum; i++) {
							reader.next();// alternative pro
							refs
							        .add(this.pool.get(reader
							                .getAttributeValue(0)));
							reader.next();
							event = reader.next();
						}
					}

					while (!(reader.next() == XMLStreamConstants.START_ELEMENT && reader
					        .getName().getLocalPart().equals("search_score")))
						;

					float xcorr = Float.parseFloat(reader.getAttributeValue(1));// xcorr
					reader.next();
					reader.next();
					reader.next();

					float dcn = Float.parseFloat(reader.getAttributeValue(1));// dcn
					reader.next();
					reader.next();
					reader.next();

					reader.next();
					reader.next();
					reader.next();

					float sp = Float.parseFloat(reader.getAttributeValue(1));// sp
					reader.next();
					reader.next();
					reader.next();

					short rsp = Short.parseShort(reader.getAttributeValue(1));// rsp
					reader.next();
					reader.next();
					reader.next();
					reader.next();
					reader.next();
					String prb = reader.getAttributeValue(0);
					probability = Float.parseFloat(prb);// probability

					//The rank is considered as 1
					SequestPeptide pep = new SequestPeptide(scan, sequence,
					        charge, rsp, mh, dms, (short)1, dcn, xcorr, sp, ions, refs, this.formatter);

					pep.setProbability(probability);
					pep.setEnzyme(enzyme);
					
					return pep;
				}
			}
		} catch (NumberFormatException ne) {
			throw new RuntimeException("NumberFormatException");
		} catch (XMLStreamException xmle) {
			throw new RuntimeException("XMLStreamException");
		}

		return null;
	}

	/*
	 * Format the scan number from RawInfo.scan1.scan2.charge to RawInfo, scan1[ -
	 * scan2];
	 */
	private String getFormatedScan(String scan) {
		int len = scan.length();
		int point1 = 0, point2 = 0;
		for (int i = len - 4; i >= 0; i--) {
			char c = scan.charAt(i);
			if (c == '.') {
				if (point2 == 0)
					point2 = i;
				else {
					point1 = i;
					break;// The two points have been determined.
				}
			}
		}
		StringBuilder sb = new StringBuilder(scan.length());
		sb.append(scan.substring(0, point1)).append(", ");
		String scan1 = scan.substring(point1 + 1, point2);
		String scan2 = scan.substring(point2 + 1, len - 2);
		sb.append(scan1);
		if (!scan1.equals(scan2)) {
			sb.append(" - ").append(scan2);
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.readers
	 *      .AbstractSequestPeptideReader#getSearchParameter()
	 */
	@Override
	public SequestParameter getSearchParameter() {
		return this.parameter;
	}

	@Override
	public void close() {
		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (XMLStreamException e) {
				System.out
				        .println("Error in closing the excel file after reading."
				                + " But it doesn't matter :)");
			}
		}
		System.out.println("Finished reading.");
	}
	
	/**
	 * Always return the maximum integer value
	 */
	@Override
	public int getTopN() {
		return Integer.MAX_VALUE;
	}

	/**
	 * throw new UnSupportingMethodException
	 */
	@Override
	public void setTopN(int topn) {
		throw new UnSupportingMethodException(
        	"Cannot limit the top n for peptide reading.");
	}

	public static void main(String[] args) throws ReaderGenerateException,
	        ImpactReaderTypeException, ProWriterException,
	        PeptideParsingException, ParameterParseException, FileNotFoundException {
		if (args.length != 3) {
			System.out.println(" inputfilename pplfile searchparam");
			return;
		}

		SmartTimer timer = new SmartTimer();

		ISequestPeptideReader reader = new ProphetXmlProReader(args[0],
		        new SequestParameter().readFromFile(new File(args[2])));
		DefaultSequestPeptideFormat formatter = new DefaultSequestPeptideFormat();
		reader.setDecoyJudger(new DefaultDecoyRefJudger());
		
		IPeptideWriter pwriter = new PeptideListWriter(args[1],
		       formatter, reader.getSearchParameter(), reader.getDecoyJudger(), reader.getProNameAccesser());

		SequestPeptide peptide = null;

		while ((peptide = reader.getPeptide()) != null) {
			///should not be null
			pwriter.write(peptide, null);
		}

		pwriter.close();

		System.out.println("Finished writing in " + timer);
	}

}
