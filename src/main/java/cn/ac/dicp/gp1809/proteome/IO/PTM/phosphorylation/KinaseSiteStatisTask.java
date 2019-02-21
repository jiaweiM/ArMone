/* 
 ******************************************************************************
 * File:KinaseSiteStatisTask.java * * * Created on 2009-12-22
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
 * @author ck
 *
 * @version 2009-12-22, 19:23:18
 */
public class KinaseSiteStatisTask implements IStatisticTask{


	private ModificationSiteStatisticer mstatic;
	private Kinase kinase;
	private IPeptide [] peptides;
	private boolean phos;

	public KinaseSiteStatisTask(String database, IDecoyReferenceJudger judger, Kinase kinase, IPeptide [] peptides, char[] symbols) 
		throws FastaDataBaseException, IOException, ProteinNotFoundInFastaException, 
			MoreThanOneRefFoundInFastaException{
		this.kinase = kinase;
		mstatic = new ModificationSiteStatisticer(database, judger, symbols);
		mstatic.writeKinaTitle();
		this.phos = true;
		this.peptides = peptides;
	}

	public KinaseSiteStatisTask(String database, IDecoyReferenceJudger judger, Kinase kinase, 
			IPeptide [] peptides, char[] symbols, boolean phos) 
		throws FastaDataBaseException, IOException, ProteinNotFoundInFastaException, 
			MoreThanOneRefFoundInFastaException{
		this.kinase = kinase;
		mstatic = new ModificationSiteStatisticer(database, judger, symbols);
		mstatic.writeKinaTitle();
		this.phos = phos;
		this.peptides = peptides;
	}
	
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
			if(phos)
				this.mstatic.addKinase(pro, kinase);
			else
				this.mstatic.addKinase2(pro, kinase);
		}

		this.mstatic.outputKinaStat();
		this.mstatic.finish();
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

	/**
	 * Print the detail information to the output
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void printDetails(String output) throws IOException {
		this.mstatic.printDetails(output);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IStatisticTask#getKinaseProp()
	 */
	@Override
	public String getKinasePepProp() {
		// TODO Auto-generated method stub
		return mstatic.getKinasePepProp();
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IStatisticTask#getKinaseSitesProp()
	 */
	@Override
	public String getKinaseSitesProp() {
		// TODO Auto-generated method stub
		return mstatic.getKinaseSitesProp();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
