/* 
 ******************************************************************************
 * File: PeptideListFilteredReader.java * * * Created on 04-12-2009
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
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * Reader for peptide list with filters. Not completed
 * 
 * @author Xinning
 * @version 0.1, 04-12-2009, 14:31:02
 */
public class PeptideListFilteredReader extends PeptideListReader {
	
	private IPeptideCriteria criteria;
	
	/**
     * @param file
     * @throws FileDamageException
     * @throws IOException
     */
    public PeptideListFilteredReader(File file) throws FileDamageException,
            IOException {
	    super(file);
    }

	/**
     * @param listfile
     * @throws FileDamageException
     * @throws IOException
     */
    public PeptideListFilteredReader(String listfile)
            throws FileDamageException, IOException {
	    super(listfile);
    }
    
    
	/**
     * @param file
     * @throws FileDamageException
     * @throws IOException
     */
    public PeptideListFilteredReader(File file, IPeptideCriteria criteria) throws FileDamageException,
            IOException {
	    super(file);
	    this.criteria = criteria;
    }

	/**
     * @param listfile
     * @throws FileDamageException
     * @throws IOException
     */
    public PeptideListFilteredReader(String listfile, IPeptideCriteria criteria)
            throws FileDamageException, IOException {
	    super(listfile);
	    this.criteria = criteria;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
