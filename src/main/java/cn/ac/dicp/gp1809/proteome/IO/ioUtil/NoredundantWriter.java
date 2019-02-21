/*
 * *****************************************************************************
 * File: NoredundantWriter.java * * * Created on 08-07-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.ioUtil.SimpleFilenameChecker;

/**
 * Write the proteins to a formated noredundant file.
 * 
 * @author Xinning
 * @version 0.3.4, 03-17-2010, 17:13:14
 */
public class NoredundantWriter implements IProteinWriter {

	private static final DecimalFormat percentf ;
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		percentf = new DecimalFormat("#0.##%");
		
		Locale.setDefault(def);
	}
	
	private static final String PRIOR_EXT = NoredundantConstants.NORED_EXTENSION ;
	private static final String[] EXTENSIONs = new String[] {"noredundant", "unduplicated", 
		NoredundantConstants.NORED_EXTENSION, NoredundantConstants.UNDUP_EXTENSION};

	private HashSet <IPeptide> pepset;
	private int decoy;
	private int total;

	protected static final String lineSeparator = IOConstant.lineSeparator;

	private PrintWriter writer;

	/**
	 * The protein format
	 */
	protected IProteinFormat pformat;

	/**
	 * Number of proteins which have been written.
	 */
	protected int proCount = 0;

	protected int decoyProCount = 0;
	
	/**
	 * Counter of unique peptides in each protein. The index is the number of
	 * unique peptides in a protein and the value is the number of proteins with
	 * this number of unique peptides
	 */
	protected int[] counter;

	/**
	 * Comments upon the Protein list for output. These comments can be criteria
	 * used for the identification or any other informations. These informations
	 * will be print at the end of the noredundant file. Please make sure the
	 * format of comment is readable as no translation will be made to the
	 * comment.
	 */
	protected String comments;

	public NoredundantWriter(String outname, IProteinFormat formatter)
	        throws IOException {
		this(outname, formatter, null);
	}

	/**
	 * Create a writer for the outname and
	 * 
	 * @param outname
	 * @param comments
	 * @throws IOException
	 */
	public NoredundantWriter(String outname, IProteinFormat formatter,
	        String comments) throws IOException {
		try {

			if (outname == null)
				throw new NullPointerException(
				        "The filename for output the noredundant file must not be null.");

			writer = new PrintWriter(
					new BufferedWriter(new FileWriter(SimpleFilenameChecker.check(outname, EXTENSIONs, PRIOR_EXT))));
		} catch (IOException e) {
			throw new IOException("Error in opening the file:\r\n\t" + outname
			        + "\r\n\t for writing the Noredundant file.");
		}

		this.pformat = formatter;
		writer.println(this.getTitle());

		this.pepset = new HashSet<IPeptide>();

		// Default can parse protein with 99 unique peptides
		this.counter = new int[100];
		this.comments = comments;
	}

	/**
	 * Get the title for noredundant output
	 * 
	 * @return
	 */
	protected String getTitle() {
		StringBuilder sb = new StringBuilder();

		sb.append(pformat.getReferenceFormat().getTitleString())
		        .append(lineSeparator);
		IPeptideFormat<?> pepformat = pformat.getPeptideFormat();
		
		sb.append('[').append(pepformat.type()).append(']').append(
		        pepformat.getTitleString());

		return sb.toString();
	}

	/**
	 * Write a protein into the noredundant file.
	 * 
	 * @param protein
	 */
	public void write(Protein protein) {
		// Skip the null protein
		if (protein == null)
			return;

		this.proCount++;
		protein.setIndex(this.proCount);
		this.counter(protein);
			
		this.writer.println(this.getProteinString(protein, this.pformat,
		        this.proCount));
	}

	/**
	 * Merge the protein string.
	 * 
	 * @param protein
	 * @param formatter
	 * @param proidx
	 * @return
	 */
	private String getProteinString(Protein protein, IProteinFormat formatter,
	        int proidx) {
		StringBuilder sb = new StringBuilder(300);
		LinkedList<Protein> allenproteins = protein.getAlleneProteinList();
		boolean gr = !(allenproteins == null || allenproteins.size() == 0);
		String idxstr = String.valueOf(proidx);
		if (gr) {
			sb.append('$').append(protein.getIndexStr()).append(" - 0\t")
			        .append("ProteinGroup: ").append(protein.getGroupIndex())
			        .append("\t").append(protein.getSpectrumCount()).append(
			                "\t").append(protein.getPeptideCount())
			        .append("\t").append("Proteins: ").append(
			                allenproteins.size() + 1).append("\t").append(
			                protein.getFormatedProbability()).append("\t\t\t")
			        .append(protein.getGroupIndex()).append("\t").append(
			                protein.getCrossProteinCount()).append('\t')
			        .append(protein.isTarget() ? "1" : "-1").append(
			                lineSeparator);

			sb.append(formatter.format(protein, idxstr + "a"));
		} else
			sb.append(formatter.format(protein, idxstr));

		if (gr) {
			// The additional index
			char add = 'a';
			for (Iterator<Protein> iterator = allenproteins.iterator(); iterator
			        .hasNext();)
				sb.append(formatter.format(iterator.next(), idxstr + (++add)));
		}

		return sb.toString();
	}

	/**
	 * Print the summary and close the file for writing
	 */
	public void close() {
		this.writeSummary();
		this.writer.close();
	}

	/**
	 * Count the protein by the number of unique peptides, spectra and so on.
	 * This method is for the writing of the summary list after finish the
	 * writing of protein.
	 * 
	 * <p>
	 * default: unique peptide count.
	 * 
	 * @param protein
	 */
	protected void counter(Protein protein) {
		int uc = protein.getPeptideCount();
		if (uc >= this.counter.length) {
			int[] tem = new int[uc + 1];
			System.arraycopy(counter, 1, tem, 1, counter.length - 1);
			counter = tem;
		}
		counter[uc]++;
		IPeptide [] peps = protein.getAllPeptides();
		for(int i=0;i<peps.length;i++){
			if(!pepset.contains(peps[i])){
				pepset.add(peps[i]);
				if(!peps[i].isTP())
					this.decoy++;
			}
		}
	}

	/**
	 * Write the summary of protein informations after finishing the writing of
	 * protein list. And also, the comments will be print in the same time
	 */
	protected void writeSummary() {
		this.total = pepset.size();
		double pepFDR = decoy * 2d / total;
		StringBuilder sb = new StringBuilder(200);
		sb.append(lineSeparator).append("-----summary------").append(
		        lineSeparator).append("Total Protein group :").append(
		        this.proCount).append(lineSeparator).append("Total Peptide number : ").append(total)
		        .append(lineSeparator).append("Decoy Peptide number : ").append(decoy).append(lineSeparator)
		        .append("FDR : ") .append(percentf.format(pepFDR)).append(lineSeparator)
		        .append("UniPepCount\tProteinGroupCount\tPercent").append(lineSeparator);

		if (this.proCount == 0) {
			System.out.println("No peptide matched!");
			this.writer.print(sb.toString());
		}

		for (int i = 1; i < counter.length; i++) {
			int c = counter[i];
			if (c == 0)
				continue;

			sb.append(i).append('\t').append(c).append('\t').append(
			        percentf.format(((double) c) / this.proCount)).append(
			        lineSeparator);
		}

		this.writer.println(sb.toString());

		if (this.comments != null) {
			this.writer.println();
			this.writer.println();
			this.writer.println("Comments: ");
			this.writer.println(this.comments);
		}
	}

	/**
	 * Write protein to the writer using default sorter (sort the protein by the
	 * number of unique peptides). Null not permitted.
	 * 
	 * @param outname
	 * @param protein
	 * @throws IOException
	 * @throws NullPointerException
	 *             if the protein[] is null
	 */
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein) throws IOException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		Arrays.sort(protein);

		NoredundantWriter nwriter = new NoredundantWriter(outname, formatter);
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];

//			System.out.println("noredundantwriter\t"+pro.getGroupIndex());
			nwriter.write(pro);
		}

		nwriter.close();
	}

	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein, int [] filter) throws IOException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		Arrays.sort(protein);

		NoredundantWriter nwriter = new NoredundantWriter(outname, formatter);
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];
			int upc = pro.getPeptideCount();
			int pc = pro.getSpectrumCount();
			if(pc>filter[0] && upc>filter[1]){
//				System.out.println("noredundantwriter\t"+pro.getGroupIndex());
				nwriter.write(pro);
			}
		}
		nwriter.close();
	}
	
	/**
	 * Write protein to the writer using default sorter (sort the protein by the
	 * number of unique peptides). Null not permitted.
	 * 
	 * @param outname
	 * @param protein
	 * @throws IOException
	 * @throws NullPointerException
	 *             if the protein[] is null
	 */
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein, String comments) throws IOException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		Arrays.sort(protein);

		NoredundantWriter nwriter = new NoredundantWriter(outname, formatter,
		        comments);
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];

			// System.out.println(pro.getGroupIndex());
			nwriter.write(pro);
		}

		nwriter.close();
	}

	/**
	 * wirte protein[] to the writer. <b>If comparator == null, it means that
	 * the proteins will be wrote directly without sort.</b>
	 * 
	 * @param outname:
	 *            the output file name
	 * @param protein[]
	 *            proteins null not permitted
	 * @param comparator
	 *            sort the protein for output using the specific comparator
	 *            (Null permitted)
	 * @param total 
	 * 			  total peptide number
	 * @throws IOException
	 * 
	 */
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein, Comparator<Protein> comparator)
	        throws IOException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		if (comparator != null)
			Arrays.sort(protein, comparator);
		else {
			System.out
			        .println("The input protein comparator is null, using default sort order.");
			Arrays.sort(protein);
		}

		NoredundantWriter nwriter = new NoredundantWriter(outname, formatter);
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];
			nwriter.write(pro);
		}

		nwriter.close();
	}
}
