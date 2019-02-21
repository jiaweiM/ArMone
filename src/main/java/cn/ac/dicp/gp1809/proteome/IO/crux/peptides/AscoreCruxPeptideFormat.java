/*
 ******************************************************************************
 * File: AscoreCruxPeptideFormat.java * * * Created on 06-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * Default PeptideFormat for Peptides with Ascore values
 * 
 * @author Xinning
 * @version 0.2, 08-23-2009, 16:41:13
 */
public class AscoreCruxPeptideFormat extends DefaultCruxPeptideFormat {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	public static AscoreCruxPeptideFormat newInstance(ICruxPeptideFormat dformat){
		AscoreCruxPeptideFormat format = null;
		try {
			format = new AscoreCruxPeptideFormat(dformat);
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
	protected AscoreCruxPeptideFormat(ICruxPeptideFormat dformat) throws IllegalFormaterException {
		super(getIndexMap(dformat));
	}
	
	/**
	 * The index map of the key position of different attributes for a peptide format.
	 * 
	 * @return
	 */
	protected final static HashMap<String, Integer> getIndexMap(ICruxPeptideFormat dformat){
		HashMap<String, Integer> peptideIndexMap= new HashMap<String, Integer>(16);
		
		String[] titles = dformat.getTitle();
		
		int len = titles.length;
		
		int maxidx = 0;
		for(int i=0; i<len; i++) {
			String title = titles[i];
			Integer indx = dformat.getIndex(title);
			if(indx != null && indx != -1) {
				peptideIndexMap.put(title, indx);
				if (indx > maxidx)
					maxidx = indx;
			}
		}
		
		//always at the last column
		peptideIndexMap.put(ASCORE,		maxidx+1);
		
		return peptideIndexMap;
	}
}
