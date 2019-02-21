/*
 ******************************************************************************
 * File: NoredundantReader.java * * * Created on 03-17-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.ProteinFormatFactory;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Reader for Noredundant protein out files
 * 
 * @author Xinning
 * @version 0.6.2, 04-27-2010, 11:11:07
 */
public class NoredundantReader implements IProteinReader, NoredundantConstants{
	/*
	 * If a protein reference is started with this string, then the protein
	 * group mode will on.
	 * 
	 * @see Protein.alleneProtein
	 */
	private static final String GROUP_MODE = "ProteinGroup";

	/*
	 * After a protein group is read, the following protein will be the allene
	 * protein until another protein is read.
	 */
	private boolean allene = false;

	/*
	 * The idx String for a protein group. In a protein group, all sub proteins
	 * are with the same index, but with different extensions. e.g. ProteinGourp
	 * with index of $115, and the index of proteins in this ProteinGroup will
	 * be $115a $115b ...
	 */
	private String idx = null;

	private IFastaAccesser accesser;

	private BufferedReader reader;
	private String preline;

	private IProteinFormat proformat;
	
	private PeptideType type;

	public NoredundantReader(String file) throws IOException,
	        IllegalFormaterException {
		this(new File(file));
	}

	public NoredundantReader(File file) throws IOException,
	        IllegalFormaterException {
		this.reader = new BufferedReader(new FileReader(file));

		// titles;
		String reftitle = reader.readLine();
		String peptitle = reader.readLine();

		/*
		 * For the compatibility of old version of Nord file
		 */
		try {
			type = this.getPeptideType(peptitle);
		} catch (Exception e) {
			System.err.println(e);
			System.err
			        .println("Error while parsing the peptide type, use default: SEQUEST");
			type = PeptideType.SEQUEST;
		}

		this.proformat = ProteinFormatFactory.createFormat(reftitle, peptitle,
		        type);

		preline = reader.readLine();
	}

	/*
	 * Get the peptide type from the title of peptide. The format should be
	 * print at the begin of title with the format of [<i>peptidetype</i>]
	 * 
	 * @param peptitle
	 * @return
	 */
	private PeptideType getPeptideType(String peptitle) {

		int idx = peptitle.indexOf(']');
		if (idx != -1) {
			return PeptideType.typeOfFormat(peptitle.substring(1, idx));
		}

		throw new IllegalArgumentException("Peptide type exception.");
	}

	public NoredundantReader(File file, IFastaAccesser accesser)
	        throws IOException, IllegalFormaterException {
		this(file);
		this.accesser = accesser;
	}
	

	public NoredundantReader(String file, IFastaAccesser accesser)
	        throws IOException, IllegalFormaterException {
		this(new File(file), accesser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IProteinReader#getProtein()
	 */
	@Override
	public Protein getProtein() throws ProteinIOException {

		try {
			String line = preline;

			// End of the information
			if (line == null || line.length() < 2 || line.charAt(1) == '\t')
				return null;

			boolean groupmode = false;
			float groupprob = 0f;
			if (allene) {
				if (!line.startsWith(idx)) {
					allene = false;
					return null;
				}
			} else {
				if (line.startsWith(GROUP_MODE, line.indexOf('\t') + 1)) {
					groupmode = true;
					idx = line.substring(0, line.indexOf(' '));
					String[] strs = StringUtil.split(line, '\t');
					// Must have probability
					groupprob = Float.parseFloat(strs[5]);
					line = reader.readLine();
				}
			}

			ArrayList<String> refs = new ArrayList<String>();
			do {
				refs.add(line);
				line = reader.readLine();
			} while (line.charAt(0) == '$');

			LinkedList<String> peps = new LinkedList<String>();
			do {
				peps.add(line);
				line = reader.readLine();

				// end of the protein information
				if (line == null || line.length() < 5)
					break;
			} while (line.charAt(0) == '\t');
			preline = line;

			Protein pro = this.proformat.parseProtein(refs
			        .toArray(new String[refs.size()]), peps
			        .toArray(new String[peps.size()]));
			pro.setFastaAccesser(accesser);

			if (groupmode) {
				pro.setGroupProb(groupprob);
				Protein allenePro = null;
				this.allene = true;
				while ((allenePro = this.getProtein()) != null) {
					pro.addAlleneProtein(allenePro);
				}
			}

			return pro;
		} catch (Exception e) {
			throw new ProteinIOException("Error while reading the protein.", e);
		}

	}
	
	/**
	 * The protein format of this noredundant file
	 * 
	 * @return
	 */
	public IProteinFormat getProteinFormat(){
		return this.proformat;
	}
	
	/**
	 * The peptide type
	 * 
	 * @return
	 */
	public PeptideType getPeptideType() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IProteinReader#close()
	 */
	@Override
	public void close() {
		try {
			this.reader.close();
		} catch (IOException e) {
			System.err
			        .println("Error closing the file, but it doesn't matter.");
		}
	}
}
