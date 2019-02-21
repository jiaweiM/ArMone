/* 
 ******************************************************************************
 * File: InspectPlainPeptideReader.java * * * Created on 09-02-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.InspectParameter;
import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.IInspectPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.InspectPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Reader for Inspect plain output. This is for the local version of Inspect output
 * 
 * @author Xinning
 * @version 0.1.5, 05-21-2010, 14:32:57
 */
public class InspectPlainPeptideReader extends AbstractInspectPeptideReader {

	private InspectParameter parameter;

	private BufferedReader reader;

	private int scanColumn = -1;
	// Current useless
	// private int sequenceColumn;
	private int mzColumn = -1;
	private int deltaMzColumn = -1;
	private int chargeColumn = -1;
	private int sequenceColumn = -1;

	private int MQScoreColumn = -1;
	private int fscoreColumn = -1;
	private int pvalueColumn = -1;

	private int proteinColumn = -1;
	private int nttColumn = -1;

	private String curtScanNum;
	private String preline;
	//all peptide matches for a scan
	private InspectPeptide[] curtPepsScan;

	private int curtPepIdx = 1;// current peptide count in an out file, for
	// more than one peptide accepted in an out file
	private int curtPepCount;

	private IFullScanNameGenerator generator;

	private ProteinReferencePool pool;
	
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;

	/**
	 * Create a reader for the Inspect output file
	 * 
	 * @param filename
	 * @param parameter
	 * 
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public InspectPlainPeptideReader(String filename, InspectParameter parameter)
	        throws IOException {
		this(new File(filename), parameter);
	}

	/**
	 * Create a reader for the Inspect output file
	 * 
	 * @param filename
	 * @param parameter
	 * 
	 * @throws FastaDataBaseException
	 * @throws IOException
	 */
	public InspectPlainPeptideReader(File filename, InspectParameter parameter)
	        throws IOException {

		super(filename);

		if (parameter == null)
			throw new NullPointerException(
			        "The Inspect parameter must not be null.");

		this.parameter = parameter;

		this.reader = new BufferedReader(new FileReader(filename));

		this.parseTitle(this.reader.readLine());
	}

	/**
	 * Create a reader for the csv OMSSA output file
	 * 
	 * @param filename
	 * @param parameter
	 * 
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws ParameterParseException
	 */
	public InspectPlainPeptideReader(String filename, String paramfile)
	        throws IOException, ParameterParseException {
		this(filename, new InspectParameter().loadFromFile(new File(paramfile)));
	}

	/**
	 * Create a reader for the csv OMSSA output file
	 * 
	 * @param filename
	 * @param parameter
	 * 
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws ParameterParseException
	 * @throws DtaFileParsingException
	 */
	public InspectPlainPeptideReader(String filename, String paramfile,
	        String sourceFile) throws IOException, ParameterParseException,
	        DtaFileParsingException {
		this(filename, new InspectParameter().loadFromFile(new File(paramfile)));

		this.generator = this.getGenerator(sourceFile);
	}

	/**
	 * Create a reader for the csv OMSSA output file
	 * 
	 * @param filename
	 * @param parameter
	 * 
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws ParameterParseException
	 * @throws DtaFileParsingException
	 */
	public InspectPlainPeptideReader(File file, File paramfile,
	        File sourceFile, FastaAccesser accesser) throws IOException,
	        ParameterParseException, DtaFileParsingException {
		this(file, new InspectParameter().loadFromFile(paramfile));

		if (sourceFile != null)
			this.generator = this.getGenerator(sourceFile.getPath());

		this.parameter.setFastaAccesser(accesser);
		this.accesser = accesser;
		this.proNameAccesser = new ProteinNameAccesser(accesser);
	}

	/**
	 * The full scan name generator
	 * 
	 * @param sourceFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws DtaFileParsingException
	 */
	private IFullScanNameGenerator getGenerator(String sourceFile)
	        throws FileNotFoundException, DtaFileParsingException {

		String lowname = sourceFile.toLowerCase();

		if (lowname.endsWith(".mgf")) {
			return new MgfFullScanNameGenerator(sourceFile);
		} else if (lowname.endsWith(".xml")) {
			return null;
		} else {
			return null;
		}

	}

	/**
	 * Pars the title for different columns
	 * 
	 * @param title
	 */
	protected void parseTitle(String titleline) {

		if (titleline == null || titleline.length() == 0)
			throw new NullPointerException("The file is empty.");

		String[] title = StringUtil.split(titleline, '\t');

		for (int i = 0; i < title.length; i++) {
			String column = title[i].trim();

			if (column.equals("Scan#")) {
				this.scanColumn = i;
				continue;
			}
			/*
			 * if (column.equals("Peptide")) { this.sequenceColumn = i; return;
			 * }
			 */

			if (column.equals("Annotation")) {
				this.sequenceColumn = i;
				continue;
			}

			if (column.equals("Mass")) {
				this.mzColumn = i;
				continue;
			}

			if (column.equals("MQScore")) {
				this.MQScoreColumn = i;
				continue;
			}

			if (column.equals("NTT")) {
				this.nttColumn = i;
				continue;
			}

			if (column.equals("F-Score")) {
				this.fscoreColumn = i;
				continue;
			}

			if (column.equals("Protein")) {
				this.proteinColumn = i;
				continue;
			}

			if (column.equals("PrecursorMZ")) {
				this.mzColumn = i;
				continue;
			}

			if (column.equals("PrecursorError")) {
				this.deltaMzColumn = i;
				continue;
			}

			if (column.equals("Charge")) {
				this.chargeColumn = i;
				continue;
			}

			if (column.equals("Theo Mass")) {
				this.mzColumn = i;
				continue;
			}

			if (column.equals("p-value")) {
				this.pvalueColumn = i;
				continue;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 * .AbstractPeptideReader#getPeptideImp()
	 */
	@Override
	protected InspectPeptide getPeptideImp() throws PeptideParsingException {

		if (this.curtPepIdx >= this.curtPepCount) {

			this.curtPepsScan = this.getPeptidesNextScan();

			if (this.curtPepsScan == null) {// End of out files
				return null;
			}
			
			//No peptide identifications for this scan
			if(this.curtPepsScan.length ==0) {
				return this.getPeptideImp();
			}
			
			this.curtPepIdx = 0;

			int topn = this.getTopN();
			if (topn < this.curtPepsScan.length) {
				this.curtPepsScan = Arrays.copyOfRange(this.curtPepsScan, 0,
				        topn);
			}

			this.curtPepCount = this.curtPepsScan.length;
		}

		InspectPeptide pep = this.curtPepsScan[this.curtPepIdx++];
		
		Set <ProteinReference> refsets = pep.getProteinReferences();
		Iterator <ProteinReference> it = refsets.iterator();
		while(it.hasNext()){
			ProteinReference ref = it.next();
			String partName = ref.getName();
			try {
				ProteinSequence ps = accesser.getSequence(ref);
				this.proNameAccesser.addRef(partName, ps);
			} catch (ProteinNotFoundInFastaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MoreThanOneRefFoundInFastaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return pep;

	}

	/**
	 * Get all the peptides for a single spectrum. There may be no peptide
	 * returned for a specific scan when all the peptide identifications are
	 * contains more than one modifications at a single aminoaicds. For example,
	 * A.AAASphos-18SSSS.A. Then return a 0-long peptide array (not null).
	 * 
	 * @return
	 * @throws PeptideParsingException
	 */
	private InspectPeptide[] getPeptidesNextScan()
	        throws PeptideParsingException {
		try {

			ArrayList<InspectPeptide> list = new ArrayList<InspectPeptide>();

			String line = null;

			//The first one
			if (curtPepsScan == null) {
				if ((line = reader.readLine()) == null) {
					System.err.println("No peptide.");
					return null;//The end of file
				}

				String[] columns = StringUtil.split(line, '\t');
				this.curtScanNum = columns[this.scanColumn];
			} else {
				//end of the file
				if (preline == null)
					return null;

				line = preline;
			}

			short rank = 1;
			while (true) {
				String[] columns = StringUtil.split(line, '\t');
				String scan = columns[this.scanColumn];

				if (!scan.equals(curtScanNum)) {
					this.curtScanNum = scan;
					preline = line;
					break;
				}

				InspectPeptide pep = this.parsePeptide(columns);

				/*
				 * In the case there are more than one modifs at one aminoacid
				 */
				if (pep != null) {
					pep.setEnzyme(this.parameter.getEnzyme());
					pep.setRank(rank++);
					list.add(pep);
				}

				line = reader.readLine();
				//end of the file
				if (line == null) {
					preline = null;
					break;
				}
			}

			return list.toArray(new InspectPeptide[list.size()]);

		} catch (Exception e) {
			throw new PeptideParsingException(e);
		}
	}

	/**
	 * Parse peptide from a line
	 * 
	 * @param line
	 * @return
	 * @throws PeptideParsingException
	 */
	private InspectPeptide parsePeptide(String[] columns)
	        throws PeptideParsingException {
		try {
			if (columns.length < 3)
				return null;

			String scanNum = columns[this.scanColumn];

			//The neutral mass
			double mz = Double.parseDouble(columns[this.mzColumn]);
			short charge = Short.parseShort(columns[this.chargeColumn]);
			double pvalue = Double.parseDouble(columns[this.pvalueColumn]);
			float mqscore = Float.parseFloat(columns[this.MQScoreColumn]);
			float fscore = Float.parseFloat(columns[this.fscoreColumn]);
			//For inspect, this value is saved as float (2.000)
			short ntt = (short) Float.parseFloat(columns[this.nttColumn]);
			//The neutral theoretical mass
			double deltaMz = Double.parseDouble(columns[this.deltaMzColumn]);
			double deltaMh = deltaMz;
			double theomh = mz - deltaMz;

			HashSet<ProteinReference> references = new HashSet<ProteinReference>();
			String name = columns[this.proteinColumn];

			if(this.pool == null)
				this.pool = new ProteinReferencePool(this.getDecoyJudger());
			
			// OMSSA use the protein index from 0 - n
			ProteinReference ref = this.pool.get(name);

			this.getFastaAccesser().renewReference(ref);
			references.add(ref);

			String rawseq = columns[this.sequenceColumn];
			String sequence = this.parseSequence(columns[this.sequenceColumn]);
			
			HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
			Iterator <ProteinReference> it = references.iterator();
			while(it.hasNext()){
				
				ProteinReference pref = it.next();
				ProteinSequence proseq = accesser.getSequence(pref);
				int beg = proseq.indexOf(rawseq)+1;
				int end = beg + rawseq.length() - 1;
				int preloc = beg-8<0 ? 0 : beg-8;
				String pre = proseq.getAASequence(preloc, beg-1);
				
				String next = "";
				if(end<proseq.length()){
					int endloc = end+7>proseq.length() ? proseq.length() : end+7;
					next = proseq.getAASequence(end, endloc);
				}

				SeqLocAround sla = new SeqLocAround(beg, end, pre, next);
				locAroundMap.put(pref.toString(), sla);
			}
			
			

			/*
			 * In the case there are more than one modifs at one aminoacid
			 */
			if (sequence == null) {
				return null;
			}

			if (this.generator == null) {
				
				InspectPeptide pep = new InspectPeptide(scanNum, sequence, charge, theomh,
				        deltaMh, (short) 0, mqscore, fscore, pvalue,
				        references, ntt, this.getPeptideFormat());
				
				pep.setPepLocAroundMap(locAroundMap);
				
				return pep;
				
			} else {
				
				InspectPeptide pep = new InspectPeptide(this.generator
				        .getFullScanName(Integer.parseInt(scanNum)), sequence,
				        charge, theomh, deltaMh, (short) 0, mqscore, fscore,
				        pvalue, references, ntt, this.getPeptideFormat());
				
				pep.setPepLocAroundMap(locAroundMap);
				
				return pep;
			}

		} catch (Exception e) {
			throw new PeptideParsingException(e);
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
	 */
	@Override
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return this.proNameAccesser;
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
	public InspectParameter getSearchParameter() {
		return this.parameter;
	}
	

	public static void main(String[] args) throws ParameterParseException,
	        IOException, PeptideParsingException, DtaFileParsingException {
		InspectPlainPeptideReader reader = new InspectPlainPeptideReader(
		        "D:\\APIVASEII\\alpha_casein\\inspect\\ms3.txt",
		        "E:\\Downloads\\chemsoft\\proteome\\Inspect.20090202\\params\\input.txt",
		        "D:\\APIVASEII\\alpha_casein\\ms3.mgf");

		IInspectPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			System.out.println(pep);
		}
		reader.close();
	}

	
}
