/* 
 ******************************************************************************
 * File: NoredundantExcelWriter.java * * * Created on 2011-9-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.DefaultProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.DefaultReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IReferenceDetailFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2011-9-16, 14:45:27
 */
public class NoredundantExcelWriter implements IProteinWriter {

	private static final DecimalFormat percentf ;
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		percentf = new DecimalFormat("#0.##%");
		
		Locale.setDefault(def);
	}

	private HashSet <IPeptide> pepset;
	private int decoy;
	private int total;

	protected static final String lineSeparator = IOConstant.lineSeparator;

	private ExcelWriter writer;

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

	public NoredundantExcelWriter(String outname, IProteinFormat formatter)
	        throws IOException, RowsExceededException, WriteException {
		this(outname, formatter, null);
	}

	/**
	 * Create a writer for the outname and
	 * 
	 * @param outname
	 * @param comments
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public NoredundantExcelWriter(String outname, IProteinFormat formatter,
	        String comments) throws IOException, RowsExceededException, WriteException {
		try {

			if (outname == null)
				throw new NullPointerException(
				        "The filename for output the noredundant file must not be null.");

			writer = new ExcelWriter(outname, new String []{"Protein", "Peptide", "Unique Peptide"});
		} catch (IOException e) {
			throw new IOException("Error in opening the file:\r\n\t" + outname
			        + "\r\n\t for writing the Noredundant file.");
		}

		this.pformat = formatter;
		writer.addTitle(this.getTitle(), 0, ExcelFormat.normalFormat);

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
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public void write(Protein protein) throws RowsExceededException, WriteException {
		// Skip the null protein
		if (protein == null)
			return;

		this.proCount++;
		protein.setIndex(this.proCount);
		this.counter(protein);
			
		this.writer.addContent(this.getProteinString(protein, this.pformat,
		        this.proCount), 0, ExcelFormat.indexFormat);
	}
	
	private void addPepTitle(String title) throws RowsExceededException, WriteException{
		this.writer.addTitle(title, 1, ExcelFormat.normalFormat);
		this.writer.addTitle(title, 2, ExcelFormat.normalFormat);
	}
	
	private void writePep(IPeptide pep) throws RowsExceededException, WriteException{
		this.writer.addContent(pep.getPepInfo(), 1, ExcelFormat.normalFormat);
	}
	
	private void writeUniPep(IPeptide pep) throws RowsExceededException, WriteException{
		this.writer.addContent(pep.getPepInfo(), 2, ExcelFormat.normalFormat);
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
		} else {
			sb.append(formatter.format(protein, idxstr));
		}
			
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
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public void close() throws WriteException, IOException {
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
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	protected void writeSummary() throws RowsExceededException, WriteException {
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
			writer.addContent(sb.toString(), 0, ExcelFormat.normalFormat);
		}

		for (int i = 1; i < counter.length; i++) {
			int c = counter[i];
			if (c == 0)
				continue;

			sb.append(i).append('\t').append(c).append('\t').append(
			        percentf.format(((double) c) / this.proCount)).append(
			        lineSeparator);
		}

		writer.addContent(sb.toString(), 0, ExcelFormat.normalFormat);

		if (this.comments != null) {
			this.writer.addBlankRow(0);
			this.writer.addBlankRow(0);
			writer.addContent("Comments: ", 0, ExcelFormat.normalFormat);
			writer.addContent(comments, 0, ExcelFormat.normalFormat);
		}
	}

	/**
	 * Write protein to the writer using default sorter (sort the protein by the
	 * number of unique peptides). Null not permitted.
	 * 
	 * @param outname
	 * @param protein
	 * @throws IOException
	 * @throws WriteException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 * @throws NullPointerException
	 *             if the protein[] is null
	 */
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein) throws IOException, WriteException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		Arrays.sort(protein);
		HashMap <String, IPeptide> pepMap = new HashMap <String, IPeptide>();
		HashMap <String, IPeptide> uniPepMap = new HashMap <String, IPeptide>();
		IPeptide pep = null;

		NoredundantExcelWriter excelWriter = new NoredundantExcelWriter(outname, formatter);
		for (int i = 0; i < protein.length; i++) {
			
			Protein pro = protein[i];
			excelWriter.write(pro);
			
			IReferenceDetail ref = pro.getRefwithSmallestMw();
			IPeptide [] peps = pro.getAllPeptides();
			for(int j=0;j<peps.length;j++){
				pep = peps[j];
				peps[j].setDelegateReference(ref.getName());
				String key = peps[j].getScanNum()+"\t"+peps[j].getCharge();
				
				if(!pepMap.containsKey(key)){
					pepMap.put(key, peps[j]);
					
					String seq = peps[j].getSequence();
					float priScore = peps[j].getPrimaryScore();
					if(uniPepMap.containsKey(seq)){
						float priScore0 = uniPepMap.get(seq).getPrimaryScore();
						if(priScore>priScore0){
							uniPepMap.put(seq, peps[j]);
						}
					}else{
						uniPepMap.put(seq, peps[j]);
					}
				}
			}
		}

		IPeptideFormat <?> pepformat = pep.getPeptideFormat();
		String title = "Reference"+pepformat.getTitleString();
		excelWriter.addPepTitle(title);
		
		Iterator <String> it1 = pepMap.keySet().iterator();
		while(it1.hasNext()){
			String key = it1.next();
			IPeptide p1 = pepMap.get(key);
			excelWriter.writePep(p1);
		}
		
		Iterator <String> it2 = uniPepMap.keySet().iterator();
		while(it2.hasNext()){
			String key = it2.next();
			IPeptide p2 = uniPepMap.get(key);
			excelWriter.writeUniPep(p2);
		}

		excelWriter.close();
	}
/*
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein, int [] filter) throws IOException, RowsExceededException, WriteException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		Arrays.sort(protein);

		NoredundantExcelWriter excelWriter = new NoredundantExcelWriter(outname, formatter);
		
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];
			int upc = pro.getPeptideCount();
			int pc = pro.getSpectrumCount();
			if(pc>filter[0] && upc>filter[1]){
//				System.out.println("noredundantwriter\t"+pro.getGroupIndex());
				excelWriter.write(pro);
			}
		}
		excelWriter.close();
	}
*/	
	/**
	 * Write protein to the writer using default sorter (sort the protein by the
	 * number of unique peptides). Null not permitted.
	 * 
	 * @param outname
	 * @param protein
	 * @throws IOException
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 * @throws NullPointerException
	 *             if the protein[] is null
	 */
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein, String comments) throws IOException, RowsExceededException, WriteException {

		if (protein == null) {
			throw new NullPointerException("The Proteins for writing is null.");
		}

		Arrays.sort(protein);

		NoredundantExcelWriter excelWriter = new NoredundantExcelWriter(outname, formatter,
		        comments);
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];

			// System.out.println(pro.getGroupIndex());
			excelWriter.write(pro);
		}

		excelWriter.close();
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
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 * 
	 */
	public static void write(String outname, IProteinFormat formatter,
	        Protein[] protein, Comparator<Protein> comparator)
	        throws IOException, RowsExceededException, WriteException {

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

		NoredundantExcelWriter excelWriter = new NoredundantExcelWriter(outname, formatter);
		for (int i = 0; i < protein.length; i++) {
			Protein pro = protein[i];
			excelWriter.write(pro);
		}

		excelWriter.close();
	}
	
	public static void batchWrite(String in) throws FileDamageException, IOException, 
		PeptideParsingException, ProteinNotFoundInFastaException, 
		MoreThanOneRefFoundInFastaException, FastaDataBaseException, WriteException{
		
		IReferenceDetailFormat refFormat = new DefaultReferenceDetailFormat();

		File [] files = (new File(in)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("ppl"))
					return true;
				return false;
			}
			
		});
		
		for(int i=0;i<files.length;i++){
			
			IPeptideListReader reader = new PeptideListReader(files[i]);
			IProteinFormat format = new DefaultProteinFormat(refFormat, reader.getPeptideFormat());
			Proteins2 pros = new Proteins2(reader.getProNameAccesser());
			
			String output = files[i].getAbsolutePath().replace("ppl", "peptide.xls");

			IPeptide pep;
			while((pep=reader.getPeptide())!=null) {
				pros.addPeptide(pep);
			}
			
//			Protein [] protein = pros.getAllProteins();
			Protein [] protein = pros.getProteins();
			NoredundantExcelWriter.write(output, format, protein);
			
			reader.close();
			System.gc();
		}
	}
	
	public static void main(String [] args) throws FileDamageException, PeptideParsingException, 
		ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, 
		WriteException, IOException, FastaDataBaseException{
		
		NoredundantExcelWriter.batchWrite("\\\\searcher7\\E\\BYY\\BYY-RP-RP\\Mascot-CSV\\5600");
	}
	
}
