/* 
 ******************************************************************************
 * File: DtaFormatConverter.java * * * Created on 09-28-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.format;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaReaderFactory;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2.MS2DtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaWriter;

/**
 * Utilities for conversion between different DtaFile formats.
 * 
 * @author Xinning
 * @version 0.2, 08-11-2009, 09:10:21
 */
public class DtaFormatConverter {

	/**
	 * Convert to Mgf
	 * 
	 * @param dtadir
	 * @param tomgf
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 * @throws IOException
	 */
	public static void dta2Mgf(String dtadir, String tomgf)
	        throws DtaFileParsingException, DtaWritingException {

		IBatchDtaReader reader = new SequestBatchDtaReader(dtadir);

		toMgf(reader, tomgf);

		reader.close();
	}

	/**
	 * Convert to ms2
	 * 
	 * @param dtadir
	 * @param tomgf
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
	public static void mgf2MS2(String mgffile, String tofile)
	        throws DtaFileParsingException, DtaWritingException, FileNotFoundException {

		MgfReader reader = new MgfReader(mgffile);

		toMS2(reader, tofile);
		
		reader.close();
	}

	/**
	 * Convert the dtas of sequest input format into Mgf
	 * 
	 * @param dtadir
	 * @param tomgf
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 */
	public static void dta2MS2(String dtadir, String tofile)
	        throws DtaFileParsingException, DtaWritingException {

		IBatchDtaReader reader = new SequestBatchDtaReader(dtadir);
		
		toMS2(reader, tofile);
		
		reader.close();
	}

	/**
	 * Covert the dta from the reader to MS2 file
	 * 
	 * @param reader
	 * @param tofile
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 */
	public static void toMS2(IBatchDtaReader reader, String tofile)
	        throws DtaFileParsingException, DtaWritingException {

		MS2DtaWriter.MS2Header params = new MS2DtaWriter.MS2Header();
		
		params.addParameter("Extractor", "DtaFormatConverter");
		params.addParameter("ExtractorVersion", "1.0");
		params.addParameter("Comments", "DtaFormatConverter written by Xinning Jiang");
		params.addParameter("ExtractorOptions", "MS2");

		MS2DtaWriter writer = new MS2DtaWriter(tofile, params);

		IScanDta dtafile;
		while ((dtafile = reader.getNextDta(true)) != null) {
			writer.write(dtafile);
		}

		writer.close();
	}

	/**
	 * Covert the dta from the reader to MGF file
	 * 
	 * @param reader
	 * @param tofile
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 */
	public static void toMgf(IBatchDtaReader reader, String tofile)
	        throws DtaFileParsingException, DtaWritingException {

		MgfWriter.MgfParameters params = new MgfWriter.MgfParameters();
		params
		        .addComments("Converted to Matrix generic format DtaFormatConverter by Xinning Jiang.");
		params.addParameter("COM", "Conversion of "+reader.getFile().getAbsolutePath()+" to matrix generic");
		MgfWriter writer = new MgfWriter(tofile, params);

		IScanDta dtafile;
		while ((dtafile = reader.getNextDta(true)) != null) {
			writer.write(dtafile);
		}

		writer.close();
	}
	
	
	/**
	 * Covert the dta from the reader to MGF file with limited file size;
	 * 
	 * @param reader
	 * @param tofile
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 */
	public static void toMgf(IBatchDtaReader reader, String tofile, long size)
	        throws DtaFileParsingException, DtaWritingException {

		MgfWriter.MgfParameters params = new MgfWriter.MgfParameters();
		params
		        .addComments("Converted to Matrix generic format DtaFormatConverter by Xinning Jiang.");
		params.addParameter("COM", "Conversion of "+reader.getFile().getAbsolutePath()+" to matrix generic");
		
		String name = tofile;
		if(!name.toLowerCase().endsWith(".mgf")) {
			name += ".mgf";
		}
		
		int count = 1;
		while(true) {
			String name1;
			if(count != 1)
				name1 = name.substring(0, name.length()-4)+"_"+count+".mgf";
			else
				name1 = name;
				
			MgfWriter writer = null;
			
			System.out.println(name1);

			IScanDta dtafile;
			while ((dtafile = reader.getNextDta(true)) != null) {
				if(writer==null)
					writer = new MgfWriter(name1, params);
				
				writer.write(dtafile);
				
				if(writer.size() >= size)
					break;
			}

			//no scan left
			if(writer != null)
				writer.close();
			else
				break;
			
			count ++;
			
			if(dtafile == null)
				break;
		}

	}
	
	/**
	 * Covert the dta from the reader to MGF file
	 * 
	 * @param reader
	 * @param tofile
	 * @throws IOException
	 * @throws DtaFileParsingException
	 * @throws DtaWritingException 
	 */
	public static void toDta(IBatchDtaReader reader, String tofile)
	        throws DtaFileParsingException, DtaWritingException {
		
		SequestBatchDtaWriter writer = new SequestBatchDtaWriter(tofile);

		IScanDta dtafile;
		while ((dtafile = reader.getNextDta(true)) != null) {
			writer.write(dtafile);
		}

		writer.close();
		
	}

	public static void main(String[] args) throws DtaFileParsingException,
	        DtaWritingException, FileNotFoundException, XMLStreamException {
		if (args.length == 4) {
			String sf = args[0].toLowerCase();
			String tf = args[1].toLowerCase();

			DtaType stype = DtaType.forTypeName(sf);
			DtaType ttype = DtaType.forTypeName(tf);
			IBatchDtaReader reader = DtaReaderFactory.createReader(stype, args[2]);

			switch(ttype) {
			case MGF: toMgf(reader,args[3]);
				return;
			case MS2: toMS2(reader, args[3]);
				return;
			case DTA: toDta(reader, args[3]);
				return;
			}

		} else if(args.length == 5) {
			String sf = args[0].toLowerCase();
			String tf = args[1].toLowerCase();
			
			DtaType stype = DtaType.forTypeName(sf);
			DtaType ttype = DtaType.forTypeName(tf);
			
			long size = Long.parseLong(args[4]);
			
			IBatchDtaReader reader = DtaReaderFactory.createReader(stype, args[2]);
			
			switch(ttype) {
			case MGF: toMgf(reader,args[3], size); return;
			case MS2: throw new RuntimeException("Not designed yet");
			case DTA: throw new RuntimeException("Not designed yet");
			}
		}
		
		else {
			
			System.out.println("DtaFormatConverter source_format target_format source_path target_path [maximum_size]");
			System.out.println("    formats: dta, mgf, ms2");
			
		}
	}
}
