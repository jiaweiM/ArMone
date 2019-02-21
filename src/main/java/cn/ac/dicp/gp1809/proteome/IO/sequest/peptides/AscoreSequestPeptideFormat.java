/*
 ******************************************************************************
 * File: AscoreSequestPeptideFormat.java * * * Created on 06-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * Default PeptideFormat for Peptides with Ascore values
 * 
 * @author Xinning
 * @version 0.1, 06-13-2009, 15:37:45
 */
public class AscoreSequestPeptideFormat extends DefaultSequestPeptideFormat {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	public static AscoreSequestPeptideFormat newInstance(){
		AscoreSequestPeptideFormat format = null;
		try {
			format = new AscoreSequestPeptideFormat();
        } catch (IllegalFormaterException e) {
	        e.printStackTrace();
        }
        
        return format;
		
	}
	
	public static AscoreSequestPeptideFormat newInstance(HashMap<String, Integer> idMap){
		AscoreSequestPeptideFormat format = null;
		try {
			int i = idMap.size();
			idMap.put(ASCORE, i+1);
			format = new AscoreSequestPeptideFormat(idMap);
        } catch (IllegalFormaterException e) {
	        e.printStackTrace();
        }
        
        return format;
		
	}
	
	/**
	 * Creation of SimPeptideFormat
	 * 
	 * @throws IllegalFormaterException
	 */
	protected AscoreSequestPeptideFormat() throws IllegalFormaterException {
		super(iniIndexMap());
	}
	
	/**
	 * Creation of SimPeptideFormat
	 * 
	 * @throws IllegalFormaterException
	 */
	protected AscoreSequestPeptideFormat(HashMap<String, Integer> idMap) throws IllegalFormaterException {
		super(idMap);
	}
	
	/**
	 * The index map of the key position of different attributes for a peptide format.
	 * 
	 * @return
	 */
	protected final static HashMap<String, Integer> iniIndexMap(){
		HashMap<String, Integer> peptideIndexMap= new HashMap<String, Integer>(16);
		
		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);
		peptideIndexMap.put(XCORR, 7);
		peptideIndexMap.put(DELTACN, 8);
		peptideIndexMap.put(SP, 9);
		peptideIndexMap.put(RSP, 10);
		peptideIndexMap.put(IONS, 11);
		peptideIndexMap.put(PROTEINS, 12);
		peptideIndexMap.put(PI, 13);
		peptideIndexMap.put(NUM_TERMS, 14);
		
		peptideIndexMap.put(ASCORE,	15);
		peptideIndexMap.put(PROB, 16);
		peptideIndexMap.put(fragmentInten, 17);
		peptideIndexMap.put(HydroScore, 18);
		
		return peptideIndexMap;
	}
}
