/* 
 ******************************************************************************
 * File: ProteinFormatFactory.java * * * Created on 09-15-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;

/**
 * Protein format factory
 * 
 * @author Xinning
 * @version 0.1, 09-15-2008, 15:55:10
 */
public class ProteinFormatFactory {
	
	private ProteinFormatFactory(){
		
	}

	/**
	 * Create a protein format from the name of the reference title and the name
	 * of the peptide title
	 * 
	 * @param reftitle title of reference detail
	 * @param peptidetitle title of peptide
	 * @param type type of database search algorithm
	 * @return
	 * @throws NullPointerException 
	 * @throws IllegalFormaterException 
	 */
	public static IProteinFormat createFormat(String reftitle, String peptidetitle,
	        PeptideType type) throws IllegalFormaterException, NullPointerException {

		//Currently only one constructor
		
		return new DefaultProteinFormat(ReferenceDetailFormatFactory
		        .createFormat(reftitle), PeptideFormatFactory
		        .createPeptideFormat(peptidetitle, type));

	}
	
	public static IProteinFormat createFormat(String [] reftitle, String [] peptidetitle,
	        PeptideType type) throws IllegalFormaterException, NullPointerException {

		//Currently only one constructor
		
		return new DefaultProteinFormat(ReferenceDetailFormatFactory
		        .createFormat(reftitle), PeptideFormatFactory
		        .createPeptideFormat(peptidetitle, type));

	}
	
}
