/* 
 ******************************************************************************
 * File: ProteinFilterTask.java * * * Created on 05-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * The protein filtering task
 * 
 * @author Xinning
 * @version 0.1, 06-07-2010, 15:47:57
 */
public class ProteinFilterTask implements ITask {

	private IPeptideCriteria[] pepcriteria;
	private IProteinCriteria[] procriteria;
	private NoredundantReader reader;
	private NoredundantWriter writer;
	private Protein curtProtein;

	public ProteinFilterTask(String input, String output,
	        IProteinCriteria[] procriteria) throws FastaDataBaseException,
	        IOException, IllegalFormaterException {
		this(input, output, null, procriteria);
	}

	public ProteinFilterTask(String input, String output,
	        IPeptideCriteria[] pepcriteria, IProteinCriteria[] procriteria)
	        throws IOException, IllegalFormaterException {
		this.reader = new NoredundantReader(input);
		this.writer = new NoredundantWriter(output, reader.getProteinFormat());

		this.pepcriteria = pepcriteria;
		this.procriteria = procriteria;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		this.reader.close();
		this.writer.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		boolean has;
		try {
			has = (this.curtProtein = this.reader.getProtein()) != null;
			if (has)
				return true;

		} catch (ProteinIOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {

		if (this.curtProtein != null) {

			boolean pass = true;

			if (procriteria != null) {
				for (IProteinCriteria cri : procriteria) {
					if (!cri.filter(this.curtProtein)) {
						pass = false;
						break;
					}
				}
			}

			if (pass)
				this.writer.write(this.curtProtein);

		}
	}

}
