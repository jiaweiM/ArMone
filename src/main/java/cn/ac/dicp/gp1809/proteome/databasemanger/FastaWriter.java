/* 
 ******************************************************************************
 * File: FastaWriter.java * * * Created on 10-13-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Write the protein sequences into formatted fasta database file.
 * 
 * @author Xinning
 * @version 0.1.0.1, 02-20-2009, 10:57:03
 */
public class FastaWriter {

	private static final int lineLength = 60;
	private static final String lineSeparator = IOConstant.lineSeparator;

	private PrintWriter writer;

	public FastaWriter(String output) throws IOException {
		this.writer = new PrintWriter(
		        new BufferedWriter(new FileWriter(output)));
	}

	/**
	 * Write a proseq to the output fasta file.
	 * 
	 * @param proseq
	 */
	public void write(ProteinSequence proseq) {
		this.writer.println(format(proseq, lineSeparator));
	}

	/**
	 * Write a protein sequence with name of reference and sequence of rawSeq
	 * 
	 * @param reference
	 * @param rawSeq
	 */
	public void write(String reference, String rawSeq) {
		this.writer.println(format(reference, rawSeq, lineSeparator));
	}
	
	/**
	 * Close the writer and finish writing
	 */
	public void close(){
		this.writer.close();
	}

	/**
	 * Format the raw protein sequence into formatted fasta sequence. The raw
	 * sequence is splitted so that each line has 80 characters.
	 * 
	 * @param rawSeq
	 *            the raw sequence with no tags (raw amino acid sequence)
	 * @param lineSeparator
	 *            the separator indicating a separate line
	 * @return
	 */
	public static String formatSequence(String rawSeq, String lineSeparator) {
		int seqlen = rawSeq.length();
		int rown = seqlen / lineLength;// each row has 80 aa, with "\r\n" ending
		int len = rown * 2 + seqlen;
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < rown; i++)
			sb.append(rawSeq.substring(i * lineLength, (i + 1) * lineLength)).append(
			        lineSeparator);

		int end = rown * lineLength;
		if (end < seqlen)
			sb.append(rawSeq.substring(end, seqlen));

		return sb.toString();
	}

	/**
	 * Format the ProteinSequence into formatted fasta sequence. The raw
	 * sequence is splitted so that each line has 80 characters. And the start
	 * of reference will be '>'. For example,
	 * <p>
	 * >IPI:IPI001222 .....
	 * <p>
	 * SRTEWEIOIPIKKK ...
	 * 
	 * @param proseq
	 * @param lineSeparator
	 *            the separator indicating a separate line
	 * @return
	 */
	public static String format(ProteinSequence proseq, String lineSeparator) {
		return format(proseq.getReference(), proseq.getUniqueSequence(),
		        lineSeparator);
	}

	/**
	 * Format the ProteinSequence with name of reference and sequence of rawSeq
	 * into formatted fasta sequence. The raw sequence is splitted so that each
	 * line has 80 characters. And the start of reference will be '>'. For
	 * example,
	 * <p>
	 * >IPI:IPI001222 .....
	 * <p>
	 * SRTEWEIOIPIKKK ...
	 * 
	 * @param proseq
	 * @param lineSeparator
	 *            the separator indicating a separate line
	 * @return
	 */
	public static String format(String reference, String rawSeq,
	        String lineSeparator) {
		String seq = formatSequence(rawSeq, lineSeparator);
		StringBuilder sb = new StringBuilder(seq.length() + reference.length()
		        + 2 + 3);
		sb.append('>').append(reference).append(lineSeparator).append(seq);
		return sb.toString();
	}
}
