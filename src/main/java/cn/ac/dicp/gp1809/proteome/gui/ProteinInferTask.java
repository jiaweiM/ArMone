/* 
 ******************************************************************************
 * File: ProteinInferTask.java * * * Created on 05-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantExcelWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.DefaultProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.DefaultReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * The protein infering task
 * 
 * @author Xinning
 * @version 0.2, 03-25-2010, 22:29:50
 */
public class ProteinInferTask implements ITask {

	private IReferenceDetailFormat refFormat = new DefaultReferenceDetailFormat();
	private IPeptideFormat pepFromat;

	private String output;
	private IFilteredPeptideListReader reader;
	private IPeptideCriteria[] pepcriteria;
	private IProteinCriteria[] procriteria;
	// private Proteins proteins;
	private Proteins2 proteins2;

	private IPeptide curtPeptide;
	private int total;
	private float totalf;
	private double totalFragInten;
	private int curt;

	private boolean integration = false;

	public ProteinInferTask(String output, ProteinNameAccesser accesser, IFilteredPeptideListReader reader,
			IPeptideCriteria[] pepcriteria, IProteinCriteria[] procriteria) {

		this.output = output;
		this.reader = reader;

		this.total = reader.getNumberofPeptides();
		this.totalf = total;

		this.pepFromat = reader.getPeptideFormat();
		this.proteins2 = new Proteins2(accesser);

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
		return this.curt / this.totalf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		try {

			boolean has = (curtPeptide = this.reader.getPeptide()) != null;

			if (has) {

				return true;
			} else {
				if (!this.integration) {
					this.integration = true;
					return true;
				} else
					return false;
			}
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
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

		if (this.integration) {
			try {

				Protein[] pros = proteins2.getProteins();

				ArrayList<Protein> prolist = new ArrayList<Protein>(pros.length);

				for (Protein pro : pros) {
					boolean pass = true;

					if (procriteria != null) {
						for (IProteinCriteria cri : procriteria) {
							if (!cri.filter(pro)) {
								pass = false;
								break;
							}
						}
					}

					if (pass) {
						pro.setTotalInten(totalFragInten);
						prolist.add(pro);
					}
				}

				NoredundantExcelWriter.write(this.output, new DefaultProteinFormat(this.refFormat, this.pepFromat),
						prolist.toArray(new Protein[prolist.size()]));

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {

			if (this.curtPeptide == null)
				throw new NullPointerException("Null peptide. No more peptide?");

			boolean pass = true;
			if (this.pepcriteria != null) {
				for (IPeptideCriteria criteria : pepcriteria) {
					// Ignore the criteria with different type
					PeptideType type = criteria.getPeptideType();
					if (type != PeptideType.GENERIC && type != this.curtPeptide.getPeptideType())
						continue;

					if (!criteria.filter(this.curtPeptide)) {
						pass = false;
						break;
					}
				}
			}

			if (pass) {
				proteins2.addPeptide(this.curtPeptide);
				totalFragInten += curtPeptide.getFragInten();
			}

			this.curt = this.reader.getCurtPeptideIndex();
		}
	}

}
