/* 
 ******************************************************************************
 * File:IStatisticTask.java * * * Created on 2009-12-25
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;

/**
 * @author ck
 *
 * @version 2009-12-25, 09:49:11
 */
public interface IStatisticTask {

	public int[] getNumDistinctPepsVsSiteCount(); 
	
	public void printDetails(String output) throws IOException ;
	
	public HashMap<String, Integer> getSiteMap();
	
	public void process() throws ProteinNotFoundInFastaException,
    MoreThanOneRefFoundInFastaException, FastaDataBaseException,
    BioException, SequenceGenerationException, IOException;
	
	public String getKinasePepProp();
	
	public String getKinaseSitesProp();
}
