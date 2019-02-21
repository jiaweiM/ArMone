/* 
 ******************************************************************************
 * File: CSVPeptideReader.java * * * Created on 09-02-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import com.opencsv.CSVReader;

import cn.ac.dicp.gp1809.exceptions.UnSupportingMethodException;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.OMSSAPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.dbsearch.IModification;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Reader for OMSSA peptide from exported csv file
 * 
 * @author Xinning
 * @version 0.3, 05-02-2010, 10:15:43
 */
public class CSVPeptideReader extends AbstractOMSSAPeptideReader {

	private OMSSAParameter parameter;

	private CSVReader reader;

	private int scanColumn = -1;
	// Current useless
	// private int sequenceColumn;
	private int massColumn = -1;
	private int chargeColumn = -1;
	private int evalueColumn = -1;
	private int pvalueColumn = -1;
	private int theoMassColumn = -1;

	private int proidxColumn = -1;
	private int proteinColumn = -1;

	private int modifColumn = -1;

	// The start of sequence in the protein
	private int seqStartColumn = -1;
	private int seqEndColumn = -1;
	
	private ProteinReferencePool pool;
	
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;
	
	/**
	 * Create a reader for the csv OMSSA output file
	 * 
	 * @param filename
	 * @param parameter
	 * 
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public CSVPeptideReader(String filename, OMSSAParameter parameter) throws IOException {

		super(new File(filename));
		
		if (parameter == null)
			throw new NullPointerException(
			        "The OMSSA parameter must not be null.");
		
		this.parameter = parameter;

		this.reader = new CSVReader(new BufferedReader(new FileReader(filename)));
		
		this.parseTitle(this.reader.readNext());
		
		this.accesser = parameter.getFastaAccesser(getDecoyJudger());
		
		this.proNameAccesser = new ProteinNameAccesser(accesser);
	}

	/**
	 * Pars the title for different columns
	 * 
	 * @param title
	 */
	protected void parseTitle(String[] title) {
		
		if(title == null || title.length == 0)
			throw new NullPointerException("The file is empty.");

		
		for (int i = 0; i < title.length; i++) {
			String column = title[i].trim();

			if (column.equals("Filename/id")) {
				this.scanColumn = i;
				continue;
			}
			/*
			 * if (column.equals("Peptide")) { this.sequenceColumn = i; return; }
			 */

			if (column.equals("E-value")) {
				this.evalueColumn = i;
				continue;
			}

			if (column.equals("Mass")) {
				this.massColumn = i;
				continue;
			}

			if (column.equals("Accession")) {
				this.proidxColumn = i;
				continue;
			}

			if (column.equals("Start")) {
				this.seqStartColumn = i;
				continue;
			}

			if (column.equals("Stop")) {
				this.seqEndColumn = i;
				continue;
			}

			if (column.equals("Defline")) {
				this.proteinColumn = i;
				continue;
			}

			if (column.equals("Mods")) {
				this.modifColumn = i;
				continue;
			}

			if (column.equals("Charge")) {
				this.chargeColumn = i;
				continue;
			}

			if (column.equals("Theo Mass")) {
				this.theoMassColumn = i;
				continue;
			}

			if (column.equals("P-value")) {
				this.pvalueColumn = i;
				continue;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .AbstractPeptideReader#getPeptideImp()
	 */
	@Override
	protected OMSSAPeptide getPeptideImp() throws PeptideParsingException {

		try {
			String[] columns = reader.readNext();
			
			
			if (columns == null || columns.length < 3)
				return null;
			

			String scanNum = columns[this.scanColumn];

			// String rawseq = columns[this.sequenceColumn];

			int start = Integer.parseInt(columns[this.seqStartColumn]);
			int end = Integer.parseInt(columns[this.seqEndColumn]);
			//The neutral mass
			double mass = Double.parseDouble(columns[this.massColumn]);
			short charge = Short.parseShort(columns[this.chargeColumn]);
			double evalue = Double.parseDouble(columns[this.evalueColumn]);
			double pvalue = Double.parseDouble(columns[this.pvalueColumn]);
			
			//The neutral theoretical mass
			double theomass = Double.parseDouble(columns[this.theoMassColumn]);
			double deltamh = mass - theomass;

			HashSet<ProteinReference> references = new HashSet<ProteinReference>();
			String name = columns[this.proteinColumn];
			
			// OMSSA use the protein index from 0 - n
			int idx = Integer.parseInt(columns[this.proidxColumn]) + 1;
			
			if(this.pool == null)
				this.pool = new ProteinReferencePool(this.getDecoyJudger());
			
			ProteinReference ref = this.pool.get(idx, name);
			
			references.add(ref);

			String sequence = this.parseSequence(ref, start, end,
			        columns[this.modifColumn]);

			OMSSAPeptide pep =  new OMSSAPeptide(scanNum, sequence, charge, theomass+1.00782d, deltamh, (short)0,
			        evalue, pvalue, references, this.getPeptideFormat());
			pep.setEnzyme(this.parameter.getEnzyme());
			
			ProteinSequence ps = accesser.getSequence(ref);
			this.proNameAccesser.addRef(name, ps);

			return pep;
			
		} catch (Exception e) {
			throw new PeptideParsingException(e);
		}
	}

	/**
	 * Parse the raw sequence after OMSSA peptide identification into the
	 * overall formatted peptide sequence. The format is:
	 * <p>
	 * A.AAAAAA#AAAA.A
	 * <p>
	 * where A. and .A indicate the previous and next aminoacids after database
	 * search, and the symbol of # (and so on) is the variable modification
	 * symbol which indicates a variable modification.
	 * 
	 * 
	 * @param ProteinReference
	 *            protein reference
	 * @param start:
	 *            peptide start in OMSSA search
	 * @param end:
	 *            peptide stop in OMSSA search
	 * @param modif_descriptions:
	 *            modifs string in csv Mod column. (modification 1: index,
	 *            modification 2: index)
	 * @return
	 * @throws PeptideParsingException
	 */
	protected final String parseSequence(ProteinReference reference, int start,
	        int end, String modif_descriptions) throws PeptideParsingException {

		ProteinSequence pseq = null;

		try {
			pseq = this.getFastaAccesser().getSequence(reference);
		} catch (Exception e) {
			throw new PeptideParsingException(e);
		}

		PeptideSequence pepseq = pseq.getPeptide(start - 1, end);

		IModification[] modifs = null;
		int[] modif_at = null;
		
		/*
		 * If this peptide is modified.
		 */
		if(modif_descriptions != null && modif_descriptions.length() > 1 ){
			String[] dess = StringUtil.split(modif_descriptions, ',');
			int dlen = dess.length;
			modifs = new IModification[dlen];
			modif_at = new int[dlen];

			for (int i = 0; i < dlen; i++) {
				String modif_des = dess[i];
				String des = modif_des.trim();
				int idx = des.indexOf(':');
				String mod_des = des.substring(0, idx);
				int index = Integer.parseInt(des.substring(idx + 1).trim());
				IModification mod = this.parameter.getMod(mod_des);

				if (mod == null)
					throw new NullPointerException(
					        "Can't find the modification with description: "
					                + mod_des);

				modifs[i] = mod;
				modif_at[i] = index;
			}
		}


		return this.parseSequence(pepseq, modifs, modif_at);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#close()
	 */
	@Override
	public void close() {

		try {
			this.reader.close();
		} catch (IOException e) {
			System.err.println("Error in closing the file after reading, "
			        + "but it doesn't matter :)");
		}
	}

	@Override
	public OMSSAParameter getSearchParameter() {
		return this.parameter;
	}
	
	/**
	 * Always return the maximum integer value
	 */
	@Override
	public int getTopN() {
		return Integer.MAX_VALUE;
	}

	/**
	 * throw new UnSupportingMethodException
	 */
	@Override
	public void setTopN(int topn) {
		throw new UnSupportingMethodException(
        	"Cannot limit the top n for peptide reading.");
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
