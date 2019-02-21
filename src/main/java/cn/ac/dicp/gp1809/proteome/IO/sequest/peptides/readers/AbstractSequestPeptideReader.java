/* 
 ******************************************************************************
 * File: AbstractSequestPeptideReader.java * * * Created on 09-08-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.readers;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.DefaultSequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.ISequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;

/**
 * Abstract Sequest Peptide reader
 * 
 * @author Xinning
 * @version 0.1.1, 09-09-2008, 16:55:46
 */
public abstract class AbstractSequestPeptideReader extends
        AbstractPeptideReader implements ISequestPeptideReader {

	private ISequestPeptideFormat<?> formatter;

	protected AbstractSequestPeptideReader(String filename,
	        ISequestPeptideFormat<?> formatter) {
		this(new File(filename), formatter);
	}

	protected AbstractSequestPeptideReader(File file,
	        ISequestPeptideFormat<?> formatter) {
		super(file, formatter);
	}
	
	/**
	 * Use DefaultSequestPeptideFormat as formatter
	 * 
	 * @param filename
	 */
	protected AbstractSequestPeptideReader(String filename) {
		this(new File(filename), new DefaultSequestPeptideFormat());
	}

	/**
	 * Use DefaultSequestPeptideFormat as formatter
	 * 
	 * @param filename
	 */
	protected AbstractSequestPeptideReader(File file) {
		super(file, new DefaultSequestPeptideFormat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .AbstractPeptideReader#getPeptide()
	 */
	@Override
	public SequestPeptide getPeptide() throws PeptideParsingException {
		return (SequestPeptide) super.getPeptide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getPeptideType
	 * ()
	 */
	@Override
	public PeptideType getPeptideType() {
		return PeptideType.SEQUEST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getPeptideFormat()
	 */
	@Override
	public ISequestPeptideFormat getPeptideFormat() {
		return this.formatter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#setPeptideFormat(
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat)
	 */
	@Override
	public void setPeptideFormat(IPeptideFormat<?> format) {

		if (format == null) {
			throw new NullPointerException("The format is null.");
		}

		if (format instanceof ISequestPeptideFormat<?>) {
			this.formatter = (ISequestPeptideFormat<?>) format;
			super.setPeptideFormat(format);
		} else
			throw new IllegalArgumentException(
			        "The formater for set must be sequest formater");
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
	 */
	@Override
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return null;
	}
}
