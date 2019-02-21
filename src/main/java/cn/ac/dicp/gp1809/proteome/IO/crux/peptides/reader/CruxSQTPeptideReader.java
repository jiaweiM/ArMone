/*
 ******************************************************************************
 * File: CruxSQTPeptideReader.java * * * Created on 04-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.crux.peptides.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.crux.CruxParameter;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.CruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.DefaultCruxPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatch;
import cn.ac.dicp.gp1809.proteome.IO.sqt.IPepMatches;
import cn.ac.dicp.gp1809.proteome.IO.sqt.ISpectrumInfo;
import cn.ac.dicp.gp1809.proteome.IO.sqt.ProMatch;
import cn.ac.dicp.gp1809.proteome.IO.sqt.SQTReader;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;

/**
 * Reader for crux SQT output peptides
 * 
 * @author Xinning
 * @version 0.1.3, 05-21-2010, 14:29:03
 */
public class CruxSQTPeptideReader extends AbstractCruxPeptideReader {

	/**
	 * More than one modifications on one aminoacid
	 */
	private Pattern multiModif = Pattern.compile("[^A-Z\\.\\-]{2,}");

	private SQTReader reader;

	private String baseName;

	private int curtPepIdx = 1;// current peptide count in an out file, for
	// more than one peptide accepted in an out file
	private int curtPepCount;

	// Current peptide list from out file
	private CruxPeptide[] curtPeptides = new CruxPeptide[0];

	// The current out file
	private IPepMatches curtMatch;

	private CruxParameter parameter;

	/**
	 * Only when the output contains xcorr and deltacn scores, this will be true
	 */
	private boolean reCalculateDeltaCn;
	
	private ProteinReferencePool pool;
	
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;

	/**
	 * In sqt file, there are three scores which originally be deltaCn Xcorr and
	 * Sp. However, Crux extends this to fit the different score ouput, such as
	 * dcn, pvalue and xcorr, this method parses the index of each field for
	 * score reading.
	 * <p>
	 * The index of the scores are arranged as: Xcorr, DeltaCn, Sp, Pvalue,
	 * Qvalue, perColator_score. The index is the index of each score in the
	 * IPepMatch.getScores(). And is from 0 - 3.
	 * <p>
	 * It is assume that all the scores are close to each other.
	 * 
	 * @param matchTitle
	 * @return
	 */
	private int[] scoreIndeces;

	public CruxSQTPeptideReader(String name) throws ParameterParseException,
	        IOException {
		this(new File(name));
	}

	public CruxSQTPeptideReader(File file) throws ParameterParseException,
	        IOException {
		super(file);

		this.baseName = file.getName();

		this.reader = new SQTReader(file);
		this.parameter = new CruxParameter(this.reader.getHeader());

		//Set the formatter
		this.setPeptideFormat(new DefaultCruxPeptideFormat(this.parameter
		        .getMatchColumnTitle()));

		this.scoreIndeces = this.parseIndexofScore(this.parameter
		        .getMatchColumnTitle());
	}

	/**
	 * 
	 * @param file
	 * @param accesser
	 * @throws ParameterParseException
	 * @throws IOException
	 */
	public CruxSQTPeptideReader(File file, IFastaAccesser accesser)
	        throws ParameterParseException, IOException {
		this(file);

		this.parameter.setFastaAccesser(accesser);
		this.accesser = accesser;
		this.proNameAccesser = new ProteinNameAccesser(accesser);
	}

	/**
	 * 
	 * @param file
	 * @param accesser
	 * @throws ParameterParseException
	 * @throws IOException
	 */
	public CruxSQTPeptideReader(File file, File db)
	        throws ParameterParseException, IOException {
		
		this(file);
		
		this.parameter.setDatabase(db.getAbsolutePath());
		this.accesser = this.parameter.getFastaAccesser(getDecoyJudger());
		this.proNameAccesser = new ProteinNameAccesser(accesser);
	}

	@Override
	protected CruxPeptide getPeptideImp() throws PeptideParsingException {
		try {
			if (this.curtPepIdx >= this.curtPepCount) {
				this.curtPepIdx = 0;

				this.curtMatch = this.reader.getNextMatch();

				if (this.curtMatch == null) {// End of out files
					return null;
				}

				IPepMatch[] hits = this.curtMatch.getPepMatches();

				if (hits == null) {
					this.curtPepCount = 0;
					return this.getPeptideImp();
				}

				this.curtPeptides = this.getPeptide(hits, this.curtMatch, this
				        .getTopN());
				this.curtPepCount = this.curtPeptides.length;
			}

			CruxPeptide pep = this.curtPeptides[this.curtPepIdx++];

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
		} catch (Exception e) {
			throw new PeptideParsingException(e);
		}
	}

	/**
	 * Create a peptide instance.
	 * 
	 * @param hit
	 * @return
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 * @throws PeptideParsingException
	 */
	private CruxPeptide[] getPeptide(IPepMatch[] matches, IPepMatches curtout,
	        int topN) throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, PeptideParsingException {

		int siz = matches.length;
		ArrayList<CruxPeptide> list = new ArrayList<CruxPeptide>();
		ISpectrumInfo info = curtout.getSpectrumInfo();

		short curtRank = 1;
		for (int i = 0; i < siz; i++) {
			IPepMatch match = matches[i];

			String seq = match.getSequence();

			if (multiModif.matcher(seq).find()) {
				System.out
				        .println("Peptide \""
				                + seq
				                + "\" with more than one modification on a single aminoacid, skip.");

				continue;
			}

			String sequence = this.parseSequence(seq);

			//One more variable modifications at one aminoacid
			if (sequence == null) {
				continue;
			}

			int scanBeg = info.getScanNumBeg();
			int scanEnd = info.getScanNumEnd();
			short charge = info.getCharge();
			double expmh = info.getExperimentalMH();
			double theomh = match.getCalculatedMH();
			double deltaMs = expmh - theomh;

			double scores[] = match.getScores();
			float xcorr = this.getScore(this.scoreIndeces[0], scores);
			float deltaCn = this.getScore(this.scoreIndeces[1], scores);
			float sp = this.getScore(this.scoreIndeces[2], scores);
			float pvalue = this.getScore(this.scoreIndeces[3], scores);
			float qvalue = this.getScore(this.scoreIndeces[4], scores);
			float percolatorscore = this.getScore(this.scoreIndeces[5], scores);

			HashSet<ProMatch> set = match.getReferences();
			HashSet<ProteinReference> refs = new HashSet<ProteinReference>();

			for (Iterator<ProMatch> it = set.iterator(); it.hasNext();) {
				
				if(this.pool == null)
					this.pool = new ProteinReferencePool(this.getDecoyJudger());
				
				ProteinReference ref = this.pool.get(it.next().getLocus());

				this.getFastaAccesser().renewReference(ref);

				refs.add(ref);
			}
			
			CruxPeptide pep = new CruxPeptide(this.baseName, scanBeg, scanEnd, sequence,
			        charge, curtRank++, theomh, deltaMs, xcorr, deltaCn, sp,
			        pvalue, qvalue, percolatorscore, match.getRankPrim(), match
			                .getRankPre(), match.getMatchedIons() + "/"
			                + match.getTotalIons(), refs, this.getPeptideFormat());
			
			pep.setEnzyme(this.parameter.getEnzyme());

			list.add(pep);
		}

		int size = list.size();

		//All are illegal identifications
		if (size == 0)
			return null;

		CruxPeptide[] peps = list.toArray(new CruxPeptide[size]);

		if (topN >= size)
			return peps;

		return Arrays.copyOf(peps, topN);
	}

	private float getScore(int idx, double[] scores) {
		if (idx == -1)
			return Float.NaN;

		return (float) scores[idx];
	}

	/**
	 * In sqt file, there are three scores which originally be deltaCn Xcorr and
	 * Sp. However, Crux extends this to fit the different score ouput, such as
	 * dcn, pvalue and xcorr, this method parses the index of each field for
	 * score reading.
	 * <p>
	 * The index of the scores are arranged as: Xcorr, DeltaCn, Sp, Pvalue,
	 * Qvalue, perColator_score. The index is the index of each score in the
	 * IPepMatch.getScores(). And is from 0 - 3.
	 * <p>
	 * It is assume that all the scores are close to each other.
	 * 
	 * @param matchTitle
	 * @return
	 */
	private int[] parseIndexofScore(String[] matchTitle) {
		int[] indeces = new int[6];
		Arrays.fill(indeces, -1);

		int min_idx = 256;
		for (int i = 0; i < matchTitle.length; i++) {
			String tit = matchTitle[i];
			if (tit.equalsIgnoreCase("deltaCn")) {
				indeces[1] = i;
				if (min_idx > i)
					min_idx = i;

				continue;
			}

			if (tit.equalsIgnoreCase("log(p-value) score")) {
				indeces[3] = i;
				if (min_idx > i)
					min_idx = i;

				continue;
			}

			if (tit.equalsIgnoreCase("xcorr score")) {
				indeces[0] = i;
				if (min_idx > i)
					min_idx = i;

				continue;
			}

			if (tit.equalsIgnoreCase("sp score")) {
				indeces[2] = i;
				if (min_idx > i)
					min_idx = i;

				continue;
			}

			if (tit.equalsIgnoreCase("percolator_score score")) {
				indeces[5] = i;
				if (min_idx > i)
					min_idx = i;

				continue;
			}

			if (tit.equalsIgnoreCase("q_value score")) {
				indeces[4] = i;
				if (min_idx > i)
					min_idx = i;

				continue;
			}
		}

		for (int i = 0; i < indeces.length; i++) {
			if (indeces[i] != -1)
				indeces[i] = indeces[i] - min_idx;
		}

		return indeces;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
	 */
	@Override
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return proNameAccesser;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.ProReader#close()
	 */
	public void close() {
		this.reader.close();

		System.out.println("Finished reading.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#getSearchParameter
	 * ()
	 */
	@Override
	public CruxParameter getSearchParameter() {
		return this.parameter;
	}

	public static void main(String[] args) throws ParameterParseException,
	        IOException, PeptideParsingException {
		String dir = "D:\\APIVASEII\\crux\\target.sqt";
		CruxSQTPeptideReader reader = new CruxSQTPeptideReader(new File(dir),
		        new File("d:\\database\\Final_ipi.HUMAN.v3.17.fasta"));
		ICruxPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			System.out.println(pep);
		}

		reader.close();
	}

	
}
