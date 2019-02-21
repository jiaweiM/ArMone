/* 
 ******************************************************************************
 * File: PepxmlWriterFactory.java * * * Created on 07-23-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.pepxml;

import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPepxmlWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlWritingException;

/**
 * The factory for the creation of pepxmlwriter
 * 
 * @author Xinning
 * @version 0.1.1, 05-20-2010, 20:44:58
 */
public class PepxmlWriterFactory {

	/**
	 * Create a pepxml writer for the specific type of search engine
	 * 
	 * @param type
	 * @param output
	 * @param parameter
	 * @return
	 * @throws XmlWritingException
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	public static IPepxmlWriter createWriter(PeptideType type, String output,
	        ISearchParameter parameter, IDecoyReferenceJudger judger) throws XmlWritingException,
	        IOException, FastaDataBaseException {

		switch (type) {
		case SEQUEST:
			return new SEQUESTPepxmlWriter(output, (SequestParameter) parameter, judger);
		default:
			throw new IllegalArgumentException(
			        "In beta version, only sequest is supported for writting of pepxml");
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
