/* 
 ******************************************************************************
 * File: PplMerger.java * * * Created on 03-08-2009
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
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;

/**
 * Merger more than one ppl files together.
 * 
 * @author Xinning
 * @version 0.1, 03-08-2009, 20:09:32
 */
public class PplMerger {

	private PeptideListWriter writer;
	private String output;
	private boolean unique_scancharge;
	private ISearchParameter parameter;
	private PeptideType type;
	private IDecoyReferenceJudger judger;

	private boolean stop;

	/**
	 * 
	 * @param output
	 * @param unique_scancharge
	 *            if only maintain one top matched peptide for each scan charge.
	 * @param first
	 * @throws IOException
	 * @throws FileDamageException
	 * @throws PeptideParsingException
	 */
	public PplMerger(String output, boolean unique_scancharge)
	        throws FileDamageException, IOException, PeptideParsingException {
		this.output = output;
		this.unique_scancharge = unique_scancharge;
	}

	/**
	 * Merge the ppl file into the merger
	 * 
	 * @param ppl4merge
	 * @throws FileDamageException
	 * @throws IOException
	 * @throws PeptideParsingException
	 * @throws ProWriterException
	 */
	public void merge(String ppl4merge) throws FileDamageException,
	        IOException, PeptideParsingException, ProWriterException {

		if (ppl4merge == null) {
			System.err.println("The input ppl file is null. skip.");
			return;
		}

		PeptideListReader reader = new PeptideListReader(ppl4merge);
		this.merge(reader);
		reader.close();
	}

	/**
	 * merge the ppl file into the merger. The peptide will be read at the
	 * current position, if some peptide has been read in.
	 * 
	 * @param reader
	 * @throws IOException
	 * @throws ProWriterException
	 */
	public void merge(IPeptideListReader reader)
	        throws PeptideParsingException, IOException, ProWriterException {

		if (reader == null) {
			System.err.println("The reader is null. skip.");
			return;
		}

		if (this.validateMerge(reader)) {
			IPeptide peptide;
			while ((peptide = reader.getPeptide()) != null) {
				if (this.stop)
					break;

				this.writer.write(peptide, reader.getPeakLists());
			}
		} else {
			this.finish();
			throw new IllegalArgumentException(
			        "Ppl files with different search parameters can not be merged together.");
		}
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
		        new File(reader.getSearchParameter().getDatabase()).getName())) {
			System.err.println("Not the same database for search.");
			return false;
		}

		if (!parameter.getStaticInfo().toString().equals(
		        reader.getSearchParameter().getStaticInfo().toString())) {
			System.err.println("The fix modifications are not the same.");
			return false;
		}

		if (!parameter.getVariableInfo().toString().equals(
		        reader.getSearchParameter().getVariableInfo().toString())) {
			System.err.println("The variable modifications are not the same.");
			return false;
		}
		

		return true;
	}

	/**
	 * Merge the inputs peptide list file to a single new peptide list file in a
	 * new thread
	 * 
	 * @param inputs
	 * @param output
	 * @param unique_scancharge
	 * @return
	 * @throws IOException
	 * @throws PeptideParsingException
	 * @throws FileDamageException
	 */
	public static Thread merge(final String[] inputs, String output,
	        boolean unique_scancharge) throws FileDamageException,
	        PeptideParsingException, IOException {
		final PplMerger merger = new PplMerger(output, unique_scancharge);

		Thread thread = new Thread() {
			private boolean stop;

			@Override
			public void run() {
				try {
					for (String input : inputs) {
						if (stop) {
							break;
						}

						merger.merge(input);
					}

					merger.finish();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void interrupt() {
				merger.stop = true;
				this.stop = true;
			}
		};

		return thread;
	}

	/**
	 * Finish merge
	 * 
	 * @throws ProWriterException
	 */
	public void finish() throws ProWriterException {
		this.writer.close();
	}

	private static String usage() {
		return "PplMerger output unique_scanCharge input1 input2 ...\r\n"
		        + "\tOptions:\r\n"
		        + "\t         output the output ppl after merging\r\n"
		        + "\t         unique_scanCharge true if only maintain one top \r\n"
		        + "\t                      matche peptide for each scan charge\r\n"
		        + "\t         inputn the input ppl file for merge";
	}

	/**
	 * @param args
	 * @throws Exception
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 4)
			System.err.println(usage());
		else {
			int len = args.length;

			try {
				PplMerger merger = new PplMerger(args[0], Boolean
				        .parseBoolean(args[1]));
				for (int i = 2; i < len; i++)
					merger.merge(args[i]);
				merger.finish();
			} catch (Exception e) {
				System.err.println(usage());
				throw e;
			}
		}
	}

}
