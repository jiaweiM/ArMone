/*
 * *****************************************************************************
 * File: ProteinListWriter.java * * * Created on 09-09-2008
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
 * Protein list writer
 * 
 * @author Xinning
 * @version 0.1, 09-09-2008, 16:50:39
 */
public class ProteinListWriter extends PeptideListWriter {

	public ProteinListWriter(String outputFilename,
	        IPeptideFormat<?> formatter, IDecoyReferenceJudger judger, ISearchParameter parameter, 
	        ProteinNameAccesser proNameAccesser)
	        throws ProWriterException, FileNotFoundException {
		super(outputFilename, formatter, parameter, judger, proNameAccesser);
		// TODO Auto-generated constructor stub
	}

}
