/*
 ******************************************************************************
 * File: AscoreMascotPeptideFormat.java * * * Created on 06-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * Default PeptideFormat for Peptides with Ascore values
 * 
 * @author Xinning
 * @version 0.1, 06-13-2009, 15:37:45
 */
public class AscoreMascotPeptideFormat extends DefaultMascotPeptideFormat {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	public static AscoreMascotPeptideFormat newInstance(){
		AscoreMascotPeptideFormat format = null;
		try {
			format = new AscoreMascotPeptideFormat();
        } catch (IllegalFormaterException e) {
	        e.printStackTrace();
        }
        
        return format;
		
	}
	
	public static AscoreMascotPeptideFormat newInstance(HashMap<String, Integer> peptideIndexMap){
		AscoreMascotPeptideFormat format = null;
		try {
			int i = peptideIndexMap.size();
			peptideIndexMap.put(ASCORE, i+1);
			format = new AscoreMascotPeptideFormat(peptideIndexMap);
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
	protected AscoreMascotPeptideFormat() throws IllegalFormaterException {
		super(iniIndexMap());
	}
	
	/**
	 * Creation of SimPeptideFormat
	 * 
	 * @throws IllegalFormaterException
	 */
	protected AscoreMascotPeptideFormat(HashMap<String, Integer> peptideIndexMap) throws IllegalFormaterException {
		super(peptideIndexMap);
	}
	
	/**
	 * The index map of the key position of different attributes for a peptide format.
	 * 
	 * @return
	 */
	protected final static HashMap<String, Integer> iniIndexMap(){
		HashMap<String, Integer> peptideIndexMap= new HashMap<String, Integer>(17);
		peptideIndexMap.put(SCAN, 1);
		peptideIndexMap.put(SEQUENCE, 2);
		peptideIndexMap.put(MH, 3);
		peptideIndexMap.put(DELTAMH, 4);
		peptideIndexMap.put(CHARGE, 5);
		peptideIndexMap.put(RANK, 6);
		peptideIndexMap.put(IONSCORE, 7);
		peptideIndexMap.put(E_VALUE, 8);
		peptideIndexMap.put(ASCORE, 9);

		peptideIndexMap.put(PROTEINS, 10);
		peptideIndexMap.put(PI, 11);
		peptideIndexMap.put(NUM_TERMS, 12);

		peptideIndexMap.put(PROB, 13);
		peptideIndexMap.put(fragmentInten, 14);
		peptideIndexMap.put(inten, 15);
		peptideIndexMap.put(HydroScore, 16);
		peptideIndexMap.put(retentionTime, 17);
		
		return peptideIndexMap;
	}
}
