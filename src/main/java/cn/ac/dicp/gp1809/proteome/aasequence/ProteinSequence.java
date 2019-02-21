/*
 ******************************************************************************
 * File: ProteinSequence.java * * * Created on 12-11-2007
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * A package class for protein sequence from database; Containing reference
 * name, sequence of the protein. This class can be easily write to fasta file.
 * 
 * <p>
 * Changes:
 * <li>0.4.1, 02-20-2009: The getUniqueSequence to instead getSequence to avoid
 * the ambiguous
 * <li>0.4.2, 02-22-2009: Add method {@link #getAminoaicdAt(int)}.
 * 
 * @author Xinning
 * @version 0.4.2, 02-22-2009, 21:04:00
 */
public class ProteinSequence implements IAminoacidSequence {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * For scanSite, you must specify the modification site and aminoacids
	 * around it to generate the modification probability. This value specify
	 * how many aminoacid will be returned if the method getSeqAround(int) is
	 * called.
	 * <p>
	 * <b>If the number of aminoacids around the specific location is less than
	 * specified, all the remained aminoacids will be returned.</b>
	 * 
	 * <p>
	 * If want to specify the number of additional aminoacids around it, call
	 * the method getSeqAround(int loc, int ad_aa_num);
	 */
	public static final int Addition_aa_Num = 7;

	/**
	 * PlatForm dependent turn for file writing.
	 */
	private static final String lineSeparator = IOConstant.lineSeparator;

	private String sequence = null;
	private int index = 0;
	private String reference;

	/**
	 * @param reference
	 *            name of the protein
	 * @param sequence
	 *            aa sequence
	 * @param index
	 *            in the fasta file;
	 */
	public ProteinSequence(String reference, String sequence, int index) {
		this.sequence = sequence;
		this.reference = reference;
		this.index = index;
	}

	/**
	 * Construct a ProteinSequence by the reference and sequence.
	 * <p>
	 * <b>Note: the sequence for the protein is not checked! If not sure whether
	 * there are illegal characters (not a word or aminoacid with lower case)
	 * are contained in the aminoacid sequence, you should use the static method
	 * parseSequence(String reference, String seqnotsure)</b>
	 * 
	 * @param reference
	 *            name of the protein
	 * @param sequence
	 *            aa sequence
	 */
	public ProteinSequence(String reference, String sequence) {
		this.sequence = sequence;
		this.reference = reference;
	}

	/**
	 * The aminoacid sequence for the protein can contain illegal words, e.g.
	 * ;,:\n or other assii word, or lower cased amino acids. All the lower case
	 * aminoacid will be covered to upper case and the illegal assii code will
	 * be ignored.
	 * 
	 * 
	 * 
	 * @param reference
	 * @param seqnotsure
	 * @return ProteinSequence.
	 */
	public static ProteinSequence parseSequence(String reference,
	        String seqnotsure) {
		if (seqnotsure == null)
			return null;

		int len = seqnotsure.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = seqnotsure.charAt(i);
			char uc = Character.toTitleCase(c);
			if (Aminoacids.isAminoacid(uc)) {
				sb.append(uc);
			}
		}
		return new ProteinSequence(reference, sb.toString());
	}

	/**
	 * The index of protein in the fasta database;
	 * <p>
	 * <b>Notice: The index is from 1 - n. And if this value is 0, it means that
	 * the index is not assigned </b>
	 * 
	 * @return the index in fasta database
	 */
	public int index() {
		return index;
	}

	/**
	 * Name of protein with the sequence;
	 * 
	 * @return
	 */
	public String getReference() {
		return this.reference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.aasequence.IAminoacidSequence#getSequence()
	 */
	@Override
	public String getUniqueSequence() {
		return this.sequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.aasequence.IAminoacidSequence#length()
	 */
	@Override
	public int length() {
		return this.sequence.length();
	}

	/**
	 * Get the formatted peptide sequence with aminoacids from start to end.
	 * <p>
	 * The value of start is from 0 - n. This equals
	 * getSequence().subString(start, end) and then add the terminal aminoacids.
	 * 
	 * 
	 * @param start
	 *            from 0 - n
	 * @param end
	 *            exclusive
	 * @return the PeptideSequence
	 * 
	 * @since 0.3.1
	 */
	public PeptideSequence getPeptide(int start, int end) {
		int len = this.sequence.length();
		if (start < 0 || end > len)
			throw new IllegalArgumentException(
			        "The boundary of peptide is invalid. Start: " + start
			                + ", end: " + end);

		char pre = start == 0 ? '-' : this.sequence.charAt(start - 1);
		char next = end == len ? '-' : this.sequence.charAt(end);

		return new PeptideSequence(this.sequence.substring(start, end), pre,
		        next);
	}
	
	/**
	 * Get the aminoacid sequence from begin to end, the value of start is from 0 - n.
	 * @param beg
	 * @param end
	 * @return
	 */
	public String getAASequence(int beg, int end){
		return this.sequence.substring(beg, end);
	}

	/**
	 * Get the sequence around the specified location with the default number of
	 * additional aminoacids (Addition_aa_Num, commonly, 7);
	 * 
	 * @param loc
	 *            the location of aminoacid in this protein sequence
	 * @return
	 * @throws SequenceGenerationException
	 */
	public String getSeqAround(int loc) throws SequenceGenerationException {
		return this.getSeqAround(loc, Addition_aa_Num);
	}
	
	public String getSeqAroundWithBlank(int loc) throws SequenceGenerationException {
		return this.getSeqAroundWithBlank(loc, Addition_aa_Num);
	}
	
	/**
	 * Get the sequence around the specified location with the default number of
	 * additional aminoacids (Addition_aa_Num, commonly, 7);
	 * 
	 * @param loc
	 *            the location of aminoacid in this protein sequence
	 * @return
	 * @throws SequenceGenerationException
	 */
	public String getSeqAroundStatic(int loc) throws SequenceGenerationException {
		return this.getSeqAroundStatic(loc, Addition_aa_Num);
	}

	/**
	 * Get the sequence around the specified location with with the specific
	 * number of additional aminoacids;
	 * 
	 * @param loc
	 *            the location of aminoacid in this protein sequence
	 * @param add_aa_num
	 * @return
	 * @throws SequenceGenerationException
	 */
	public String getSeqAround(int loc, int add_aa_num)
	        throws SequenceGenerationException {
		int len = this.sequence.length();
		if (loc < 0 || loc >= len) {
			throw new SequenceGenerationException("The location: " + loc
			        + " is smaller than 0 or bigger"
			        + "than the protein sequence length.");
		}

		if (add_aa_num < 0) {
			throw new SequenceGenerationException(
			        "The number of additional aminoacids around the "
			                + "specific location must not be smaller than 0.");
		}

		int start = loc - add_aa_num;
		if (start < 0)
			start = 0;
		int end = loc + add_aa_num + 1;
		if (end > len)
			end = len;

		return this.sequence.substring(start, end);
	}
	
	public String getSeqAroundWithBlank(int loc, int add_aa_num)
	        throws SequenceGenerationException {

		if (add_aa_num < 0) {
			throw new SequenceGenerationException(
			        "The number of additional aminoacids around the "
			                + "specific location must not be smaller than 0.");
		}

		StringBuilder sb = new StringBuilder();
		for(int i=0;i<add_aa_num*2+1;i++){
			sb.append("_");
		}
		String allSequence = sb+this.sequence+sb;
		
		int start = loc+add_aa_num+1;
		int end = loc + add_aa_num*3+2;

		return allSequence.substring(start, end);
	}
	
	/**
	 * Get the sequence around the specified location with with the specific
	 * number of additional aminoacids;
	 * 
	 * @param loc
	 *            the location of aminoacid in this protein sequence
	 * @param add_aa_num
	 * @return
	 * @throws SequenceGenerationException
	 */
	public String getSeqAroundStatic(int loc, int add_aa_num)
	        throws SequenceGenerationException {
		int len = this.sequence.length();
		if (loc < 0 || loc >= len) {
			throw new SequenceGenerationException("The location: " + loc
			        + " is smaller than 0 or bigger"
			        + "than the protein sequence length.");
		}

		if (add_aa_num < 0) {
			throw new SequenceGenerationException(
			        "The number of additional aminoacids around the "
			                + "specific location must not be smaller than 0.");
		}

		int before = -1;
		int after = -1;
		
		int start = loc - add_aa_num;
		if (start < 0){
			before = -start;
			start = 0;
		}
			
		int end = loc + add_aa_num + 1;
		if (end > len){
			after = end-len;
			end = len;
		}
			
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<before;i++){
			sb.append("_");
		}
		sb.append(this.sequence.substring(start, end));
		for(int i=0;i<after;i++){
			sb.append("_");
		}
		
		return sb.toString();
	}
	
	public String getIPI(){
//		Pattern pat = Pattern.compile("(IPI[\\d\\.]+)");
		Pattern pat = Pattern.compile("(IPI[\\d]+)");
		Matcher matcher = pat.matcher(reference);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public static String getIPI(String ref){
//		Pattern pat = Pattern.compile("(IPI[\\d\\.]+)");
		Pattern pat = Pattern.compile("(IPI[\\d]+)");
		Matcher matcher = pat.matcher(ref);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public String getSWISS(){
		Pattern pat = Pattern.compile("SWISS-PROT:([\\w-]+)");
		Matcher matcher = pat.matcher(reference);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public String getSWISS4Uniprot(){
		Pattern pat = Pattern.compile("..\\|([^|]*)");
		Matcher matcher = pat.matcher(reference);
		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public static String getSWISS(String ref){
		Pattern pat = Pattern.compile("SWISS-PROT:([\\w-]+)");
		Matcher matcher = pat.matcher(ref);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public static String getSWISS4Uniprot(String ref){
		Pattern pat = Pattern.compile("..\\|\\([^|]*\\)");
		Matcher matcher = pat.matcher(ref);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public String getGene(){
		Pattern pat = Pattern.compile("Gene_Symbol=([\\w-]+)");
		Matcher matcher = pat.matcher(reference);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public static String getGene(String ref){
		Pattern pat = Pattern.compile("Gene_Symbol=([\\w-]+)");
		Matcher matcher = pat.matcher(ref);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName(){
		
		String [] ss = reference.split("Gene_Symbol=[\\w;-]+");
		if(ss.length==2){
			return ss[1].trim();
		}
		return "-";
	}
	
	public static String getName(String ref){
		
		String [] ss = ref.split("Gene_Symbol=[\\w;-]+");
		if(ss.length==2){
			return ss[1].trim();
		}
		return "-";
	}

	public String getTrembl(){
		Pattern pat = Pattern.compile("TREMBL:([\\w-]+)");
		Matcher matcher = pat.matcher(reference);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	public static String getTrembl(String ref){
		Pattern pat = Pattern.compile("TREMBL:([\\w-]+)");
		Matcher matcher = pat.matcher(ref);

		if(matcher.find()){
			return matcher.group(1);
		}
		return "-";
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDes(){
		
		int beg = reference.indexOf("Tax_Id");
		if(beg>0){
			return reference.substring(beg);
		}
		return "-";
	}
	
	public static String getDes(String ref){
		
		int beg = ref.indexOf("Tax_Id");
		if(beg>0){
			return ref.substring(beg);
		}
		return "-";
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAccess(){
		
		String [] ss = reference.split("[|\\s]");
		if(ss.length>1){
			String [] sss = ss[1].split("[:;]");
			if(sss.length>1){
				return sss[1];
			}
		}

		return "-";
	}
	
	/**
	 * Test if this peptide matches the protein. If match, return the match
	 * position, otherwise, return -1.
	 * Note: In fasta database 'X' can represents any amino acid
	 * @param pep
	 *            peptide sequence
	 * @return the start position of this peptide in the protein
	 */
	public int indexOf(String pep) {
		
		char [] proChar = sequence.toCharArray();
		char [] pepChar = PeptideUtil.getUniqueSequence(pep).toCharArray();
		for(int i = 0;i <= proChar.length-pepChar.length;i++){
			
			int j;
			int haveX = 0;
			for(j=0;j<pepChar.length;j++){
				
				if(proChar[i+j] == pepChar[j]){
					continue;
					
				}else{
					
					if(proChar[i+j]=='X' || proChar[i+j]=='*'){
						haveX++;
						continue;
						
					}else if(proChar[i+j] == 'B'){
						if(pepChar[j]=='D' || pepChar[j]=='N'){
							continue;
						}else{
							break;
						}
					}else if(proChar[i+j] == 'Z'){
						if(pepChar[j]=='Q' || pepChar[j]=='E'){
							continue;
						}else{
							break;
						}
					}else if(proChar[i+j] == 'I' || proChar[i+j] == 'L'){
						if(pepChar[j]=='I' || pepChar[j]=='L'){
							continue;
						}else{
							break;
						}
					}else{
						break;
					}
				}
			}
			if(j==pepChar.length){
				if(haveX<=3)
					return i;
			}
		}
		return -1;
//		return this.sequence.indexOf(pep);
	}
	
	public Integer [] findSeq(String pep) {
		
		ArrayList <Integer> list = new ArrayList <Integer>();
		char [] proChar = sequence.toCharArray();
		char [] pepChar = PeptideUtil.getUniqueSequence(pep).toCharArray();
		for(int i = 0;i <= proChar.length-pepChar.length;i++){
			int j;
			for(j=0;j<pepChar.length;j++){
				if(proChar[i+j] != pepChar[j] && proChar[i+j]!='X')
					break;
			}
			if(j==pepChar.length){
				list.add(i);
			}
		}
		
		Integer [] site = list.toArray(new Integer[list.size()]);
		return site;
	}

	/**
	 * Generate the match informations for the peptide array.
	 * 
	 * @param peps
	 * @return
	 */
	public ProSeqCover matches(String[] peps) {
		return new ProSeqCover(this).matches(peps);
	}

	/*
	 * The fasta format output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int seqlen = this.sequence.length();
		int rown = seqlen / 80;// each row has 80 aa, with "\r\n" ending
		int len = rown * 2 + seqlen;
		len += this.reference.length() + 3 + 2;
		StringBuilder sb = new StringBuilder(len);

		sb.append('>').append(this.reference).append(lineSeparator);

		for (int i = 0; i < rown; i++)
			sb.append(sequence.substring(i * 80, (i + 1) * 80)).append(
			        lineSeparator);

		int end = rown * 80;
		if (end < seqlen)
			sb.append(sequence.substring(end, seqlen)).append(lineSeparator);

		return sb.toString();
	}

	@Override
	public int hashCode() {
		return this.reference.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProteinSequence) {
			if (this.reference.equals(((ProteinSequence) obj).reference))
				return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.aasequence.IAminoacidSequence#getAminoaicdAt
	 * (int)
	 */
	@Override
	public char getAminoaicdAt(int loc) throws IndexOutOfBoundsException {
		return this.sequence.charAt(loc-1);
	}

	/**
	 * {@inheritDoc}}
	 */
    @Override
	public ProteinSequence clone() {
	    try {
	        return (ProteinSequence) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
    }
	
	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.lang.IDeepCloneable#deepClone()
	 */
	@Override
    public ProteinSequence deepClone() {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			oos.writeObject(this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			return (ProteinSequence) ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
	
	public static void main(String [] args){
		
		ProteinSequence seq = new ProteinSequence(">IPI:IPI00000006.1|SWISS-PROT:P01112|" +
				"ENSEMBL:ENSP00000309845;ENSP00000380723;ENSP00000407586|REFSEQ:NP_001123914;" +
				"NP_005334|H-INV:HIT000080764|VEGA:OTTHUMP00000162769;OTTHUMP00000166055 " +
				"Tax_Id=9606 Gene_Symbol=HRAS GTPase HRas",
				"MTVKICDC*GECCKDSCHCGSTCLPSCSGGEKCKCDHSTGSPQCKSCGEKCKCETTCTCEKSKCNCEKC*");
		
		String pep = "ICDCXGECCK";
		System.out.println(seq.indexOf(pep));
		System.out.println(seq.getDes());
	}
	
	
}
