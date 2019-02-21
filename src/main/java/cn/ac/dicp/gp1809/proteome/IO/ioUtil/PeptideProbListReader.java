/* 
 ******************************************************************************
 * File: PeptideProbListReader.java * * * Created on 01-07-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;

/**
 * A peptide probability list reader (.ppls). A ppls file is similar to a ppl
 * file except a little difference. A column of probability is contained in ppls
 * but not ppl file, and the returned peptide from ppls is a peptide which is
 * the subclass of peptide with probability.
 * 
 * @author Xinning
 * @version 0.5, 08-08-2009, 13:49:28
 */

public class PeptideProbListReader extends PeptideListReader {

	public PeptideProbListReader(File file) throws FileDamageException,
	        IOException {
		super(file);
	}

	public PeptideProbListReader(String listfile) throws FileDamageException,
	        IOException {
		super(listfile);
	}
	
	/*
	 * Here the ppls header is not parsed. Just use the ppl header instead.!
	 */
}
