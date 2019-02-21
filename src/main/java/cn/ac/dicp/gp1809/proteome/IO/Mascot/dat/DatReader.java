/* 
 ******************************************************************************
 * File: DatReader.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotDBPattern;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotParameter;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers.AbstractMascotPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.AccessionFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * The reader of peptides and dta informations for mascot dat file
 * 
 * @author Xinning
 * @version 0.2.1, 08-16-2010, 13:07:47
 */
public class DatReader extends AbstractMascotPeptideReader implements
        IBatchDtaReader {

	private MascotDatfile daf;

	private MascotParameter parameter;

	// private ProteinMap proteinMap;

	// current peptide count in an out file, for
	// more than one peptide accepted in an out file
	private int curtQueryIdx = 0;
	private int totalQuery;
	// Current peptide index.
	private int curtPepIdx;
	// Current peptide count for this query
	private int curtPepCount;
	
	private int curtDtaIndx = 0;

	// Current peptide list from out file
	private PeptideHit[] pephits;
	// The query result
	private QueryResult qresult;

	// Only used when the isCalSim is selected as true
	private MascotScanDta curtDta;

	/**
	 * The mass of the hydrogen.
	 */
	private double hydrogen;

	/**
	 * The protein accesser versus parsed accession.
	 */
	private AccessionFastaAccesser accesser;

	/**
	 * 
	 */
	private ProteinNameAccesser proNameAccesser;
	
	private HashMap <Integer, Integer> usedQueryMap;
	
	/**
	 * 
	 * construct a reader
	 * 
	 * @param localName
	 * @param localFasta
	 * @param accession_regex
	 *            the regular expression string in Mascot for parsing the
	 *            database. (The ORIGINAL regex string even through it may be an
	 *            illegal regex string)
	 * 
	 * 
	 * @throws MascotDatParsingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public DatReader(String localName, String localFasta, String accession_regex, IDecoyReferenceJudger judger)
	        throws MascotDatParsingException, ModsReadingException,
	        InvalidEnzymeCleavageSiteException, FastaDataBaseException,
	        IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		this(new File(localName), new File(localFasta), accession_regex, judger);
	}
	
	public DatReader(String localName, String localFasta, String accession_regex, IDecoyReferenceJudger judger,
			HashMap <Integer, Integer> usedQueryMap)
	        throws MascotDatParsingException, ModsReadingException,
	        InvalidEnzymeCleavageSiteException, FastaDataBaseException,
	        IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		this(new File(localName), new File(localFasta), accession_regex, judger);
		this.usedQueryMap = usedQueryMap;
	}

	/**
	 * 
	 * @param localFile
	 * @param localFastaFile
	 * @param accession_regex
	 *            the regular expression string in mascot for parsing the
	 *            database. (The ORIGINAL regex string even through it may be an
	 *            illegal regex string)
	 * 
	 * @throws MascotDatParsingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public DatReader(File localFile, File localFastaFile, String accession_regex, IDecoyReferenceJudger judger)
	        throws MascotDatParsingException, ModsReadingException,
	        InvalidEnzymeCleavageSiteException, FastaDataBaseException,
	        IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		this(new MascotDatfile(localFile), localFastaFile, accession_regex, judger);
	}

	/**
	 * 
	 * @param daf
	 * @param localFastaFile
	 * @param accession_regex
	 *            the regular expression string in mascot for parsing the
	 *            database. (The ORIGINAL regex string even through it may be an
	 *            illegal regex string)
	 * 
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public DatReader(MascotDatfile daf, File localFastaFile,
	        String accession_regex, IDecoyReferenceJudger judger) throws ModsReadingException,
	        InvalidEnzymeCleavageSiteException, FastaDataBaseException,
	        IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		this(daf, localFastaFile, new MascotDBPattern(accession_regex), judger);
	}
	
	
	/**
	 * 
	 * @param daf
	 * @param localFastaFile
	 * @param accession_regex
	 *            the regular expression string in mascot for parsing the
	 *            database. (The ORIGINAL regex string even through it may be an
	 *            illegal regex string)
	 * 
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public DatReader(MascotDatfile daf, File localFastaFile,
			MascotDBPattern pattern, IDecoyReferenceJudger judger) throws ModsReadingException,
	        InvalidEnzymeCleavageSiteException, FastaDataBaseException,
	        IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		
		super(daf.getFileName());

		this.daf = daf;
		this.setDecoyJudger(judger);
		
		Masses masses = this.daf.getMasses();
		this.parameter = this.parseParam(daf.getParameters(), daf.getMasses(),
		        daf.getEnzyme());

		this.hydrogen = masses.getHydrogenMass();

		// Fill the protein names
		accesser = new AccessionFastaAccesser(localFastaFile,
		        pattern.getPattern(), judger);

		this.parameter.setFastaAccesser(accesser);
		this.proNameAccesser = new ProteinNameAccesser(accesser);
		this.totalQuery = this.daf.getNumberOfQueries();
		this.usedQueryMap = new HashMap <Integer, Integer>();
	}
	
	public DatReader(String file, AccessionFastaAccesser accesser, IDecoyReferenceJudger judger) 
			throws ModsReadingException,
	        InvalidEnzymeCleavageSiteException, FastaDataBaseException,
	        IOException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, MascotDatParsingException {
		
		super(file);

		this.daf = new MascotDatfile(file);
		this.setDecoyJudger(judger);
		
		Masses masses = this.daf.getMasses();
		this.parameter = this.parseParam(daf.getParameters(), daf.getMasses(),
		        daf.getEnzyme());

		this.hydrogen = masses.getHydrogenMass();

		// Fill the protein names
		this.accesser = accesser;

		this.parameter.setFastaAccesser(accesser);
		this.proNameAccesser = new ProteinNameAccesser(accesser);
		this.totalQuery = this.daf.getNumberOfQueries();
		this.usedQueryMap = new HashMap <Integer, Integer>();
	}

	/**
	 * Parse the param strings into parameter instance
	 * 
	 * @param params
	 * @param masses
	 * @return
	 * @throws ModsReadingException
	 * @throws ModsReadingException
	 * @throws InvalidEnzymeCleavageSiteException
	 *             s
	 */
	private MascotParameter parseParam(Parameters parameters, Masses masses,
	        Enzymes enzymes) throws ModsReadingException,
	        InvalidEnzymeCleavageSiteException {
		
		return new MascotParameter(parameters, masses, enzymes);
	}

	@Override
	protected IPeptide getPeptideImp() throws PeptideParsingException {

		if (this.curtPepIdx >= this.curtPepCount) {

			while(true){
				
				this.curtQueryIdx++;
				if(this.curtQueryIdx>=this.totalQuery)
					return null;
				
				if(this.usedQueryMap.size()>0){
					if(this.usedQueryMap.containsKey(this.curtQueryIdx)){
						break;
					}else{
						continue;
					}
				}else{
					break;
				}
			}

			this.qresult = this.daf.getQueryResult(this.curtQueryIdx);
			// No hit for this query
			if (qresult == null)
				return this.getPeptideImp();

			this.curtPepIdx = 0;
			
			if(this.usedQueryMap.containsKey(this.curtQueryIdx)){
				this.pephits = this.qresult.getHits(10);
				this.curtPepCount = 1;
			}else{
				this.pephits = this.qresult.getHits(this.getTopN());
				this.curtPepCount = this.pephits.length;
			}
		}

		MascotPeptide pep = null;
		
		if(usedQueryMap.size()>0){
			
			int rank = usedQueryMap.get(this.curtQueryIdx);

			PeptideHit hit = this.pephits[rank-1];

			try {
				pep = this.getPeptide(hit, rank);
				this.curtPepIdx++;
				
			} catch (Exception e) {
				throw new PeptideParsingException(e);
			}
			
		}else{
			
			PeptideHit hit = this.pephits[this.curtPepIdx++];

			try {
				pep = this.getPeptide(hit, this.curtPepIdx);
				
			} catch (Exception e) {
				throw new PeptideParsingException(e);
			}
		}

		if(pep == null)
			return this.getPeptideImp();
		
		pep.setEnzyme(this.parameter.getEnzyme());
		
		return pep;
	}

	/**
	 * Parse the peptide instance.
	 * 
	 * @param hit
	 * @return
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	private MascotPeptide getPeptide(PeptideHit hit, int rank)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		if (hit == null)
			return null;

		String seq = hit.getSequence();
		char[][] terminus = hit.getTeminalaa();
		PeptideSequence pseq = this.parseSequence(seq, terminus);

		IntArrayList modiflist = new IntArrayList();
		IntArrayList atlist = new IntArrayList();

		int[] modifarr = hit.getVariableModificationsArray();
		int len = modifarr.length - 1;
		for (int i = 0; i <= len; i++) {
			int id = modifarr[i];
			if (id > 0) {// modified at this position
				int at;
				if (i == 0) {// The n terminal
					at = i + 1;
				}
				if (i == len) {
					at = len - 1; // The c terminal
				} else {
					at = i;
				}

				// Add to list
				modiflist.add(id);
				atlist.add(at);
			}
		}

		String seq_mod = this.parameter.parseSequence(pseq,
		        modiflist.toArray(), atlist.toArray());
		
		if(seq_mod == null)
			return null;
		
		double deltaMs = hit.getDeltaMass();
		double mh = hit.getPeptideMr() + this.hydrogen;
		String name = this.qresult.getTitle();
		short charge = this.qresult.getCharge();
		double evalue = hit.getExpectancy();
		float ionscore = hit.getIonsScore();
		
		HashMap <String, SeqLocAround> locMap = new HashMap <String, SeqLocAround>();
		HashSet<ProteinReference> refs = this.parseProteinReference(hit
		        .getProteinHits(), locMap, seq);

		MascotPeptide pep = new MascotPeptide(name, seq_mod, charge, mh, deltaMs,
		        (short) rank, ionscore, evalue, refs, this.getPeptideFormat());
		
		pep.setDeltaS(hit.getDeltaS());
		double homo = hit.getHomologyThreshold();
//		if(homo>=hit.getQueryIdentityThreshold()){
//			homo = 0;
//		}
		pep.setHomoThres((float) homo);
		pep.setIndenThres((float) hit.getQueryIdentityThreshold());
		pep.setQueryIdenNum(hit.getQueryIdenNum());
		pep.setPepLocAroundMap(locMap);
		pep.setNumOfMatchedIons(hit.getNumberOfIonsMatched());
		pep.setPeaksUsedFromIons1(hit.getPeaksUsedFromIons1());
		pep.setPeaksUsedFromIons2(hit.getPeaksUsedFromIons2());
		pep.setPeaksUsedFromIons3(hit.getPeaksUsedFromIons3());

		return pep;
	}

	private PeptideSequence parseSequence(String seq, char[][] terminus) {

		if (terminus == null) {
			System.err.println("No terminus, set simply as '-'");

			return new PeptideSequence(seq, '-', '-');
		}

		return new PeptideSequence(seq, terminus[0][0], terminus[0][1]);
	}

	/**
	 * Parse the protein reference
	 * 
	 * @param list
	 * @param seq 
	 * @param locMap 
	 * @return
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	private HashSet<ProteinReference> parseProteinReference(
	        List<ProteinHit> list, HashMap<String, SeqLocAround> locAroundMap, String seq) throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {
		
		HashSet<ProteinReference> set = new HashSet<ProteinReference>();

		for (Iterator<ProteinHit> it = list.iterator(); it.hasNext();) {
			ProteinHit prohit = it.next();

			ProteinSequence pseq = accesser.getSequence(prohit.getAccession());
			String refStr = pseq.getReference();
			
			boolean isDecoy = this.getDecoyJudger().isDecoy(refStr);
			String ref = accesser.getAccession(refStr);
/*			
			accesser.getPattern();
			String ref = "";
			if(isDecoy){
				ref = refStr.substring(0, accesser.getSplitRevLength());
			}else{
				ref = refStr.substring(0, accesser.getSplitLength());
			}
*/			
			ProteinReference pref = new ProteinReference(pseq.index(), ref, isDecoy);
/*
			if(this.pool == null)
				this.pool = new ProteinReferencePool(this.getDecoyJudger());
			
			ProteinReference pref = this.pool.get(pseq.index(), pseq
			        .getReference());
*/
			int beg = pseq.indexOf(seq)+1;
//			if(beg==0){
//				System.out.println(pref.getName()+"\t"+seq+"\n"+pseq.getUniqueSequence());
//			}
			int end = beg + seq.length() - 1;
			int preloc = beg-8<0 ? 0 : beg-8;
			String pre = pseq.getAASequence(preloc, beg-1);
			
			String next = "";
			if(end<pseq.length()){
				int endloc = end+7>pseq.length() ? pseq.length() : end+7;
				next = pseq.getAASequence(end, endloc);
			}

			SeqLocAround sla = new SeqLocAround(beg, end, pre, next);
			locAroundMap.put(pref.toString(), sla);
			set.add(pref);
			
			this.proNameAccesser.addRef(ref, pseq);
		}

		return set;
	}

	@Override
	public MascotParameter getSearchParameter() {
		return this.parameter;
	}

	@Override
	public void close() {
		this.daf.close();
	}
	
	
	/**
	 * The scan data for current readin peptide, if no peptide has been read, return null.
	 * 
	 * @param isIncludePeakList
	 * @return
	 * @throws DtaFileParsingException
	 */
    public MascotScanDta getCurtDta(boolean isIncludePeakList)
            throws DtaFileParsingException {
    	
		if (this.curtQueryIdx >= this.totalQuery || this.curtQueryIdx <= 0)
			return null;
    	
    	return this.daf.getScanDta(this.curtQueryIdx, isIncludePeakList);
    }
	
	@Override
	public String getNameofCurtDta() {
		
		if(this.curtDta == null)
			return null;
		
		return this.curtDta.getScanName().getScanName();
	}

	@Override
	public MascotScanDta getNextDta(boolean isIncludePeakList)
	        throws DtaFileParsingException {
		
		if(++this.curtDtaIndx >= this.totalQuery)
			return this.curtDta = null;
		
		this.curtDta = this.daf.getScanDta(this.curtDtaIndx, isIncludePeakList);
		
		return curtDta;
	}

	@Override
	public int getNumberofDtas() {
		return totalQuery;
	}

	@Override
    public DtaType getDtaType() {
	    return DtaType.SEARCHOUT;
    }

	@Override
	public ProteinNameAccesser getProNameAccesser() {
		return this.proNameAccesser;
	}
	
	public static void main(String [] args) throws MascotDatParsingException, ModsReadingException, InvalidEnzymeCleavageSiteException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException, IOException, PeptideParsingException{
		
		String file = "H:\\OGlycan_final_20130712\\humanserum\\20120328_humaneserum_trypsin_HILIC_8uL-02\\" +
				"20120328_humaneserum_trypsin_HILIC_8uL-02.oglycan.F005042.dat";
		String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\Final_ipi.HUMAN.v3.80.fasta";
		String reg = ">\\([^| ]*\\)";
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		DatReader reader = new DatReader(file, fasta, reg, judger);
		reader.setTopN(10);
		System.out.println(reader.getSearchParameter().getVariableInfo().getModficationDescription());
		
/*		int num = reader.totalQuery;
		for(int i=1;i<num;i++){
			QueryResult qb = reader.daf.getQueryResult(i);
			if(qb!=null && i==2322)
				System.out.println(qb.getTitle());
		}
*/		
		PrintWriter pw = new PrintWriter("H:\\OGlycan_final_20130712\\humanserum\\20120328_humaneserum_trypsin_HILIC_8uL-02\\" +
			"20120328_humaneserum_trypsin_HILIC_8uL-02.oglycan.F005042.txt");
		IPeptideFormat format = reader.getPeptideFormat();
		pw.write(format.getTitleString()+"\n");

		MascotPeptide pep = null;
		while((pep=(MascotPeptide) reader.getPeptideImp())!=null){
//			System.out.println(pep.getSequence()+"\t"+pep.getProteinReferenceString());
			pw.write(pep+"\n");
		}
		pw.close();
		
	}
	
}
