/* 
 ******************************************************************************
 * File: MgfFullScanNameGenerator.java * * * Created on 03-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import java.io.FileNotFoundException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfScanDta;
import cn.ac.dicp.gp1809.proteome.util.IScanName;

/**
 * Get the full name from the index of scan after the 
 * 
 * @author Xinning
 * @version 0.1, 03-25-2009, 14:08:05
 */
public class MgfFullScanNameGenerator implements IFullScanNameGenerator {

	private HashMap<Integer, IScanName> indexMap;

	public MgfFullScanNameGenerator(String corMgf)
	        throws FileNotFoundException, DtaFileParsingException {
		indexMap = this.buildIndexMap(corMgf);
	}

	private HashMap<Integer, IScanName> buildIndexMap(String corMgf)
	        throws FileNotFoundException, DtaFileParsingException {

		MgfReader reader = new MgfReader(corMgf);
		HashMap<Integer, IScanName> indexMap = new HashMap<Integer, IScanName>();
		MgfScanDta dta;
		int idx = 0;
		while ((dta = reader.getNextDta(false)) != null) {
			indexMap.put(idx++, dta.getScanName());
		}
		
		reader.close();
		return indexMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader.IFullScanNameGenerator
	 * #getFullScanName(int)
	 */
	public IScanName getFullScanName(int idxorNum) {
		return this.indexMap.get(idxorNum);
	}

}
