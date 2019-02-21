/* 
 ******************************************************************************
 * File: PplMergeTask.java * * * Created on 05-20-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * The peptide list merge task
 * 
 * @author Xinning
 * @version 0.1.2, 05-20-2010, 16:38:37
 */
public class PplMergeTask implements ITask {

	private PeptideListWriter writer;
	private String output;
	private String inputs[];
	private boolean unique_scancharge;
	private ISearchParameter parameter;
	private PeptideType type;
	private IDecoyReferenceJudger judger;

	private float totalProgress;
	private float proportion;

	private int curtIdx;
	private int total;
	private PeptideListReader curtreader;
	private IPeptide curtPeptide;

	public PplMergeTask(String output, String[] inputs,
	        boolean unique_scancharge) throws FileDamageException, IOException {
		this.output = output;

		if (inputs == null || inputs.length == 0)
			throw new NullPointerException("No input ppl file.");

		this.inputs = inputs;
		this.total = this.inputs.length;
		this.proportion = 1f / this.total;
		this.unique_scancharge = unique_scancharge;

		this.createNextReader();
	}

	private void createNextReader() throws FileDamageException, IOException {
		this.curtreader = new PeptideListReader(this.inputs[curtIdx]);

		if (!this.validateMerge(this.curtreader)) {
			throw new IllegalArgumentException(
			        "The peptide list files are not with the same search "
			                + "parameter, and cannot be merged together");
		}

		this.totalProgress = this.curtIdx / (float) this.total;
		this.curtIdx++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		return (this.curtreader.getCurtPeptideIndex() / (float) this.curtreader
		        .getNumberofPeptides())
		        * this.proportion + this.totalProgress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		try {
			this.writer.close();
		} catch (ProWriterException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {

		if ((curtPeptide = this.curtreader.getPeptide()) == null) {

			if (this.curtIdx < this.total) {
				try {
					this.createNextReader();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return this.hasNext();
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		this.writer.write(this.curtPeptide, this.curtreader.getPeakLists());
	}

	/**
	 * Validate the current ppl file can be merged. A ppl file which can be
	 * merged should be with the same search parameters. And other limitations.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	private boolean validateMerge(IPeptideListReader reader)
	        throws FileNotFoundException {

		if (this.parameter == null) {
			this.parameter = reader.getSearchParameter();
			this.type = reader.getPeptideType();
			this.judger = reader.getDecoyJudger();
			this.writer = new PeptideListWriter(output, reader
			        .getPeptideFormat(), this.parameter, judger, unique_scancharge, reader.getProNameAccesser());
			return true;
		}

		if(this.type != reader.getPeptideType()) {
			System.err.println("Not the same database search algorithm for search.");
			return false;
		}
		
		if (!new File(parameter.getDatabase()).getName().equals(
		        new File(this.parameter.getDatabase()).getName())) {
			System.err.println("Not the same database for search.");
			return false;
		}

		if (!parameter.getStaticInfo().toString().equals(
		        this.parameter.getStaticInfo().toString())) {
			System.err.println("The fix modifications are not the same.");
			return false;
		}

		if (!parameter.getVariableInfo().toString().equals(
		        this.parameter.getVariableInfo().toString())) {
			System.err.println("The variable modifications are not the same.");
			return false;
		}

		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.output;
	}

}
