/*
 * *****************************************************************************
 * File: ISearchParameter.java * * * Created on 09-20-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.io.Serializable;
import java.util.HashMap;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Parameter interface consisting all information for database search
 * 
 * <p>
 * Changes:
 * <li>0.4.1, 03-04-2009: Add method {@link #isMonoPeptideMass()}
 * <li>0.4.2, 04-30-2009: {@link #getPeptideType()}
 * <li>0.4.3, 06-13-2009: add {@link #setStaticInfo(Aminoacids)},
 * {@link #setVariableInfo(AminoacidModification)}
 * 
 * @author Xinning
 * @version 0.4.4, 05-20-2010, 16:49:42
 */
public interface ISearchParameter extends Serializable, IDeepCloneable {

	/**
	 * @return static information of the amino acids; the mass of each contains
	 *         static modificaiton which doesn't display from the sequence
	 *         outputed by sequest;
	 */
	public Aminoacids getStaticInfo();

	/**
	 * @return the differential modification information, each char of symbol
	 *         indicated a modification
	 */
	public AminoacidModification getVariableInfo();

	/**
	 * @return static information of the amino acids; the mass of each contains
	 *         static modificaiton which doesn't display from the sequence
	 *         outputed by sequest;
	 */
	public void setStaticInfo(Aminoacids aas);

	/**
	 * @return the differential modification information, each char of symbol
	 *         indicated a modification
	 */
	public void setVariableInfo(AminoacidModification aamodif);

	/**
	 * Get the enzyme used to digest proteins;
	 */
	public Enzyme getEnzyme();

	/**
	 * @return the name of database.
	 */
	public String getDatabase();

	/**
	 * Set a specific database to instead the initial database. This should be
	 * very useful when the search files are moved to another computer on which
	 * the original database is unreachable.
	 * 
	 * <p>
	 * <b>The input must be fasta database and should not be NULL.</b>
	 * 
	 * @param database
	 */
	public void setDatabase(String database);

	/**
	 * Set the database path and the indexed fasta accesser. This should be very
	 * useful when the searched output file is moved to other place where the
	 * fasta database is unreachable.
	 * 
	 * @param accesser
	 */
	public void setFastaAccesser(IFastaAccesser accesser);

	/**
	 * Get the fasta accesser
	 * 
	 * @return
	 */
	public IFastaAccesser getFastaAccesser(IDecoyReferenceJudger judger);

	/**
	 * If the mono peptide mass is used for database search.
	 * 
	 * @since 0.4.1
	 * @return
	 */
	public boolean isMonoPeptideMass();
	
	public void restore(HashMap<Character, Character> replaceAA);

	/**
	 * The type of the search engine
	 * 
	 * @return
	 */
	public PeptideType getPeptideType();

	/**
	 * {@inheritDoc}
	 */
	public ISearchParameter deepClone();
}
