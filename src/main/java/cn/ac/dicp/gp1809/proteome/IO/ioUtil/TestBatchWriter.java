/* 
 ******************************************************************************
 * File: TestBatchWriter.java * * * Created on 04-22-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-22-2009, 21:18:50
 */
public class TestBatchWriter {

	/**
	 * @param args
	 * @throws IOException
	 * @throws FastaDataBaseException
	 * @throws ProWriterException
	 * @throws PeptideParsingException
	 * @throws ImpactReaderTypeException
	 * @throws ReaderGenerateException
	 */
	public static void main(String[] args) throws ReaderGenerateException,
	        ImpactReaderTypeException, PeptideParsingException,
	        ProWriterException, FastaDataBaseException, IOException {

		String input = "D:\\APIVASEII\\alpha_casein\\crux\\percolator\\ms2\\ms2_percolator.sqt";
		String output = "D:\\APIVASEII\\alpha_casein\\crux\\percolator\\ms2\\ms2_percolator.sqt.new.ppl";
		int topn = 1;
		String database = "d:\\database\\Final_my7protein_yeast.fasta";
		PeptideType type = PeptideType.CRUX;
		DtaType dtaType = DtaType.MZDATA;
		String dtaPath = "D:\\APIVASEII\\alpha_casein\\70403_acasein_1p_070403182353.xml";

//		BatchPplCreator.create(input, output, topn, database, type, dtaType,
//		        dtaPath);
	}
}
