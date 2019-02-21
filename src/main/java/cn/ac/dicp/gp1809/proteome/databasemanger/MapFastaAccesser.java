/* 
 ******************************************************************************
 * File: MapFastaAccesser.java * * * Created on 2013-3-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.File;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * @author ck
 *
 * @version 2013-3-16, 16:37:50
 */
public class MapFastaAccesser implements IFastaAccesser {
	
	private File file;
	private HashMap <String, ProteinSequence> psmap;
	private IDecoyReferenceJudger judger;
	
	public MapFastaAccesser(String file, IDecoyReferenceJudger judger){
		this(new File(file), judger);
	}
	
	public MapFastaAccesser(File file, IDecoyReferenceJudger judger){
		this.file = file;
		this.psmap = new HashMap <String, ProteinSequence>();
		this.judger = judger;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getFastaFile()
	 */
	@Override
	public File getFastaFile() {
		// TODO Auto-generated method stub
		return file;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSequence(java.lang.String)
	 */
	@Override
	public ProteinSequence getSequence(String proteinReference)
			throws ProteinNotFoundInFastaException,
			MoreThanOneRefFoundInFastaException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSequence(int)
	 */
	@Override
	public ProteinSequence getSequence(int proteinIdx)
			throws ProteinNotFoundInFastaException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSequence(cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference)
	 */
	@Override
	public ProteinSequence getSequence(ProteinReference ref)
			throws ProteinNotFoundInFastaException,
			MoreThanOneRefFoundInFastaException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSplitLength()
	 */
	@Override
	public int getSplitLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getSplitRevLength()
	 */
	@Override
	public int getSplitRevLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getNumberofProteins()
	 */
	@Override
	public int getNumberofProteins() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getNamesofProteins()
	 */
	@Override
	public String[] getNamesofProteins() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#renewReference(cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference)
	 */
	@Override
	public void renewReference(ProteinReference ref)
			throws ProteinNotFoundInFastaException,
			MoreThanOneRefFoundInFastaException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#getDecoyJudger()
	 */
	@Override
	public IDecoyReferenceJudger getDecoyJudger() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser#setSubRef(cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail)
	 */
	@Override
	public void setSubRef(IReferenceDetail ref) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
