/* 
 ******************************************************************************
 * File: OMSSAOMXEmbedPplCreatorTask.java * * * Created on 05-03-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers.OMXPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;

/**
 * For OMMSA omx file
 * 
 * @author Xinning
 * @version 0.1, 05-03-2010, 09:13:41
 */
public class OMSSAOMXEmbedPplCreatorTask implements IEmbededPplCreationTask {

	private OMXPeptideReader reader;

	private IPeptideWriter pwriter;

	private IPeptide peptide;
	private IScanDta curtPeaks;
	private IMS2PeakList[] peaklist;
	
	private boolean hasNext = true;
	private boolean closed = false;
	
	/**
	 * For others
	 * 
	 * @param output
	 * @param input
	 * @param topn
	 * @param database
	 * @param type
	 * @param dtaType
	 * @param dtaPath
	 * @throws IOException
	 * @throws FastaDataBaseException
	 * @throws SequestFileIllegalException
	 * @throws ImpactReaderTypeException
	 * @throws ImpactReaderTypeException
	 * @throws ParameterParseException 
	 * @throws ReaderGenerateException
	 */
	public OMSSAOMXEmbedPplCreatorTask(String output, String input, int topn,
	        String database, boolean uniq_charge, IDecoyReferenceJudger judger) throws IOException,
	        FastaDataBaseException, ImpactReaderTypeException,
	         ParameterParseException {

		reader = new OMXPeptideReader(input, new FastaAccesser(database, judger));
		reader.setTopN(topn);
		
		ISearchParameter parameter = reader.getSearchParameter();
		pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
		        parameter, judger, uniq_charge, reader.getProNameAccesser());
	}
	

	@Override
	public float completedPercent() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#hasNext()
	 */
	@Override
	public boolean hasNext() {

		if (!hasNext)
			return hasNext;

		try {
			hasNext = (peptide = reader.getPeptide()) != null;
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}

		if (!hasNext)
			this.dispose();

		return hasNext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#processNext()
	 */
	@Override
	public void processNext() {

		try {
		if (this.peptide != null) {

			IScanDta t = this.reader.getCurtDta(true);
			if(this.curtPeaks != t) {
				curtPeaks = t;
				peaklist = new IMS2PeakList[] {t.getPeakList()};
			}
			
			pwriter.write(peptide, this.peaklist);
		} else {
			System.err
			        .println("No next inner task need to be processed or use the hasNext() fist.");
		}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#unDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask1#dispose()
	 */
	@Override
	public void dispose() {
		if (!this.closed) {

			if (this.reader != null)
				this.reader.close();

			if (this.pwriter != null)
				try {
					this.pwriter.close();
				} catch (ProWriterException e) {
					throw new RuntimeException(e);
				}

			this.closed = true;
		}
	}

	@Override
	public String toString() {
		return this.pwriter.toString();
	}
}
