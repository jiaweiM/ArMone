/* 
 ******************************************************************************
 * File: SimPeptideFormat.java * * * Created on 07-17-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides;

import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * Default PeptideFormat for Peptides with Sim score values
 * 
 * @author Xinning
 * @version 0.1, 07-17-2008, 21:58:57
 */
public class SimSequestPeptideFormat extends DefaultSequestPeptideFormat {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	public static SimSequestPeptideFormat newInstance(){
		SimSequestPeptideFormat format = null;
		try {
			format = new SimSequestPeptideFormat();
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
	protected SimSequestPeptideFormat() throws IllegalFormaterException {
		super(iniIndexMap());
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

		peptideIndexMap.put(PROB, 15);
		peptideIndexMap.put(SIM, 16);
		
		return peptideIndexMap;
	}
}
