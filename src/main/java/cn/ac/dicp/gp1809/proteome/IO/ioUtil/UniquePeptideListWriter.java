/*
 * *****************************************************************************
 * File: UniquePeptideListWriter.java * * * Created on 09-22-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.FileNotFoundException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;

/**
 * 
 * @author Xinning
 * @version 0.1, 03-13-2008, 10:13:47
 */
public class UniquePeptideListWriter extends PeptideListWriter {

	/**
	 * @param outputFilename
	 * @throws ProWriterException
	 * @throws FileNotFoundException 
	 */
	public UniquePeptideListWriter(String outputFilename,
	        IPeptideFormat<?> formatter, ISearchParameter parameter, IDecoyReferenceJudger judger,
	        ProteinNameAccesser accesser)
	        throws ProWriterException, FileNotFoundException {
		super(outputFilename, formatter, parameter, judger, accesser);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
