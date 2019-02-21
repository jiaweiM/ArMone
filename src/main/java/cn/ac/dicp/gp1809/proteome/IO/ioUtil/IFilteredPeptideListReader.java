/* 
 ******************************************************************************
 * File: IFilteredPeptideListReader.java * * * Created on 05-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatInfo;

/**
 * The filtered peptide list reader
 * 
 * @author Xinning
 * @version 0.1, 05-27-2009, 09:18:42
 */
public interface IFilteredPeptideListReader extends IPeptideListReader {

	/**
	 * The filter applied while the reading of peptides
	 * 
	 * @return
	 */
	public IPeptideCriteria<?> getCriteria();

	public PeptideStatInfo getPeptideStatInfo();
	
	public void setProNameAccesser(ProteinNameAccesser proNameAccesser);
}
