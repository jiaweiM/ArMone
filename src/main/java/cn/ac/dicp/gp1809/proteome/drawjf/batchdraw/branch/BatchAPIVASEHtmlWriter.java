/* 
 ******************************************************************************
 * File: BatchAPIVASEHtmlWriter.java * * * Created on 04-16-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch;

import java.io.IOException;
import java.io.InputStream;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.BatchDrawHtmlWriter;

/**
 * 
 * @author Xinning
 * @version 0.1, 04-16-2009, 20:55:29
 */
public class BatchAPIVASEHtmlWriter extends BatchDrawHtmlWriter {

	
	
	
	
	
	/**
	 * @param output
	 * @param parameter
	 * @param headerstream
	 * @param type
	 * @throws IOException
	 */
	public BatchAPIVASEHtmlWriter(String output, ISearchParameter parameter,
			InputStream headerstream, PeptideType type) throws IOException {
		super(output, parameter, headerstream, type);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
