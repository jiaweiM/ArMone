/* 
 ******************************************************************************
 * File: PhosSiteStatisticTask.java * * * Created on 05-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import java.io.IOException;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.PTM.ModificationSiteStatisticer;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * 
 * @author Xinning
 * @version 0.1, 05-19-2009, 16:38:50
 */
public class PhosSiteStatisticTask implements IStatisticTask{

	private ModificationSiteStatisticer mstatic;
	private IPeptide[] peptides;

	public PhosSiteStatisticTask(String database, char[] symbols,
	        IPeptide[] peptides, IDecoyReferenceJudger judger) throws IOException, FastaDataBaseException {
		mstatic = new ModificationSiteStatisticer(database, judger, symbols);
		mstatic.writePhosTitle();

		this.peptides = peptides;
	}

	/**
	 * Process the task
	 * 
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 * @throws SequenceGenerationException
	 * @throws IOException
	 */
	public void process() throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException,
	        BioException, SequenceGenerationException, IOException {
		Proteins proteins = new Proteins(this.mstatic.getFastaAccesser());

		for (IPeptide pep : peptides) {
			if(pep.isTP())
				proteins.addPeptide(pep);
		}

		Protein[] pros = proteins.getProteins();

		for (Protein pro : pros) {
			this.mstatic.addPhos(pro);
		}

		this.mstatic.finish();
	}

	/**
	 * Print the details
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void printDetails(String output) throws IOException {
		this.mstatic.printDetails(output);
	}

	/**
	 * The site map with key of modified aminoacid and the value of number of
	 * modification count.
	 * 
	 * @return
	 */
	public HashMap<String, Integer> getSiteMap() {

		return this.mstatic.getSiteMap();
	}

	/**
	 * Number of distinct peptide vs the modification site count. The number of
	 * peptides with specific modification count can be accessed by the index of
	 * the array. For example, the int[1] is the number of singly modified
	 * peptides
	 * 
	 * @return
	 */
	public int[] getNumDistinctPepsVsSiteCount() {
		return this.mstatic.getNumDistinctPepsVsSiteCount();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IStatisticTask#getKinaseProp()
	 */
	@Override
	public String getKinasePepProp() {
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IStatisticTask#getKinaseSitesProp()
	 */
	@Override
	public String getKinaseSitesProp() {
		// TODO Auto-generated method stub
		return "";
	}

}
