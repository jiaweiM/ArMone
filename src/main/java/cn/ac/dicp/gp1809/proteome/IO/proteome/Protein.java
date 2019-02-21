/*
 * *****************************************************************************
 * File: Protein.java * * * Created on 09-16-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.IProteinGroupSimplifier;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.MostLocusSimplifier;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.GRAVY.GRAVYCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.pi.PICalculator;

/**
 * The Protein containing informations of peptides, coverage, reference and so
 * on. This is an basic unit in shotgun proteomics.
 * 
 * @author Xinning
 * @version 1.2.1.1, 06-02-2010, 11:36:03
 */
public class Protein implements Comparable<Protein>, IProtein {

	/**
	 * The simplifier
	 */
	private static IProteinGroupSimplifier<IReferenceDetail> DEFAULT_SIMPLIFIER = 
		new MostLocusSimplifier<IReferenceDetail>();
	
	/**
	 * The current used simplifier. If this protein has not been 
	 * simplified, this value will be null;
	 */
	protected IProteinGroupSimplifier<IReferenceDetail> simplifier;
	
	private static Aminoacids acds = Aminoacids.getInstance();
	
	private static DecimalFormat df8 = new DecimalFormat("0.###E0");

	// The fasta accesser may be null.
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;

	private ProteinInfo info;

	// Static, will not be changed for a protein
	private IPeptide[] peptides;

	private UniPep[] uniPeps;

	/*
	 * An array contains the string value of all the unique peptides. This value
	 * will be changed after the calling of refreshProtein.
	 */
	// private String[] uniqueSeq;
	private boolean isTarget = true;

	// Which protein group does this protein belong to?
	private int groupIndex = -1;

	/*
	 * As there may be shared peptides for proteins which came from the same
	 * ProteinGroup. Therefore, this count indicating how may proteins come from
	 * the same protein group(with the same group index).
	 */
	private int crossProteinCount = 0;

	/*
	 * This is only used when the protein are generated using a probability
	 * based strategy. In this condition, even though there are distinct peptide
	 * for protein identification, however, if all the unique peptide for this
	 * protein identification with a combined probability less than a specific
	 * value, this protein will be think as an ambiguate protein. Then all the
	 * ambiguate proteins will be merged together and outputted as a single
	 * protein group.
	 */
	private LinkedList<Protein> alleneProteins;

	/*
	 * This only used when the alleneProteins exists, and this protein will be
	 * consider as a "ProteinGroup (all proteins in this group will be consider
	 * as one and indicated as "indexa")"
	 */
	private float groupProb;

	// for output
	private String index;

	private int spcount;
	private int pepcount;

	private boolean itUnique;

	/**
	 * Generator for ProteinGroup;
	 * 
	 * @param ref
	 * @param accesser
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	Protein(SimPro ref, IFastaAccesser accesser)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {

		this.accesser = accesser;

		HashSet<UniPep> tset = ref.getUniPeps();
		int len = tset.size();
		this.uniPeps = tset.toArray(new UniPep[len]);

		ArrayList<IPeptide> peplist = new ArrayList<IPeptide>();
		for (int i = 0; i < len; i++) {
			UniPep tp = uniPeps[i];

			// set relationship with this protein
			tp.addProtein(this);

			for (Iterator<IPeptide> iterator1 = tp.getPeptideIterator(); iterator1
			        .hasNext();) {
				IPeptide pep = iterator1.next();
				if (pep.isUsed())
					peplist.add(pep);
			}
		}
		this.peptides = peplist.toArray(new IPeptide[peplist.size()]);

		this.refreshPeptideCount();

		String[] useqs = this.getUniqueSequences0(this.uniPeps);

		int size = ref.size();
		IReferenceDetail[] references = new ReferenceDetail[size];
		references[0] = this.getRef(ref.getReference(), useqs, accesser);

		if (size > 1) {
			LinkedList<String> list = ref.getAlleneRefList();
			Iterator<String> iterator = list.iterator();
			for (int i = 1; iterator.hasNext(); i++) {
				references[i] = this.getRef(iterator.next(), useqs, accesser);
			}
		}

		this.isTarget = this.judgeTarget(references);
		this.info = new ProteinInfo(references);
		

		/*
		 * Arrange peptides by scan number;
		 */
		Arrays.sort(peptides, new Comparator<IPeptide>() {
			public int compare(IPeptide o1, IPeptide o2) {
				String s1 = o1.getScanNum();
				String s2 = o2.getScanNum();
				return s1.compareTo(s2);
			}
		});
	}

	/**
	 * Generator for ProteinGroup;
	 * 
	 * @param ref
	 * @param accesser
	 * @param groupIdx
	 * @param crossProteinCount
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	Protein(SimPro ref, IFastaAccesser accesser, int groupIdx,
	        int crossProteinCount) throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		this(ref, accesser, groupIdx);
		this.crossProteinCount = crossProteinCount;
	}

	/**
	 * Generator for ProteinGroup;
	 * 
	 * @param ref
	 * @param accesser
	 * @param groupIdx
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	Protein(SimPro ref, IFastaAccesser accesser, int groupIdx)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		this(ref, accesser);
		this.groupIndex = groupIdx;
	}

	public Protein(IReferenceDetail[] refdetails, IPeptide[] peptides) throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {
		this(refdetails, peptides, null);
	}

	public Protein(IReferenceDetail[] refdetails, IPeptide[] peptides,
	        IFastaAccesser accesser) throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {

		if (refdetails == null || refdetails.length == 0)
			throw new IllegalArgumentException(
			        "The Reference details must not be null.");

		if (peptides == null || peptides.length == 0)
			throw new IllegalArgumentException("The peptides must not be null.");

		this.peptides = peptides;
		this.accesser = accesser;
		this.getUniPeps0();

		this.refreshPeptideCount();
		

		/*
		 * Judge this protein with the first reference
		 */
		this.isTarget = this.judgeTarget(refdetails);
		this.info = new ProteinInfo(refdetails);

	}

	/*
	 * Get the UniPep from the peptides
	 */
	private void getUniPeps0() {
		HashMap<String, UniPep> map = new HashMap<String, UniPep>(
		        this.peptides.length / 5);
		for (IPeptide pep : peptides) {
			UniPeps.addPeptide(pep, map);
		}
		this.uniPeps = map.values().toArray(new UniPep[map.size()]);
	}

	/*
	 * Get the unique sequences form the UniPep
	 */
	private String[] getUniqueSequences0(UniPep[] upeps) {
		HashSet<String> set = new HashSet<String>();

		for (UniPep upep : upeps)
			if (upep.isUsed())
				set.add(upep.getUniqueSequence());

		return set.toArray(new String[set.size()]);
	}

	/**
	 * Get the reference details for this protein
	 * 
	 * @param refer
	 * @param accesser
	 * @return
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 */
	private ReferenceDetail getRef(String refer, String[] useqs,
	        IFastaAccesser accesser) throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		
		ProteinSequence sequence = accesser.getSequence(refer);
		String name = sequence.getReference();
		String aaseq = sequence.getUniqueSequence();
		double hyproScore = GRAVYCalculator.calculate(aaseq);
		ReferenceDetail refDetail = new ReferenceDetail(sequence.getReference(), getSpectrumCount(),
		        getPeptideCount(), sequence.matches(useqs).getCoverage(),
		        (float) PICalculator.compute(aaseq), acds
		                .getAveragePeptideMass(aaseq), sequence.length(), !accesser.getDecoyJudger().isDecoy(name));
		
		refDetail.setHyproScore(hyproScore);
		accesser.setSubRef(refDetail);		
		return refDetail;
	}

	private ReferenceDetail getRef(String refer, ProteinNameAccesser accesser 
			) throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		
		/*String [] keys = accesser.getAllKeys();
		for(int i=0;i<keys.length;i++){
			System.out.println(keys[i]);
		}
System.out.println(keys.length+"\t!!!!!!!!\t"+refer);*/
		SimpleProInfo info = accesser.getProInfo(refer);
		double hyproScore = info.getHydroScore();
		ReferenceDetail refDetail = new ReferenceDetail(info.getRef(), getSpectrumCount(),
		        getPeptideCount(), info.getCoverage(peptides),
		        (float) info.getPI(), info.getMw(), info.getLength(), !info.isDecoy());
		
		refDetail.setHyproScore(hyproScore);

		return refDetail;
	}
	
	/*
	 * Judge this protein with the first reference. In very few cases, target
	 * protein and decoy protein may have duplicated peptides, then the protein
	 * will be considered as a decoy protein.
	 */
	private boolean judgeTarget(IReferenceDetail[] references) {
		boolean tar = true;
		for (IReferenceDetail reft : references) {
			if (!reft.isTarget()) {
				tar = false;
				break;
			}
		}
		return tar;
	}

	/**
	 * Set index for this protein in the final protein list; This number will
	 * reflect as the index of proteins in noredundant file. All the reference
	 * of this protein will be assigned as "$index-n"
	 * 
	 * @param index
	 *            index;
	 */
	public void setIndex(int index) {
		String idxstr = String.valueOf(index);
		char allenidx = 'a';
		if (this.alleneProteins != null) {
			this.info.setIndex(idxstr + (allenidx++));
			for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
			        .hasNext();)
				iterator.next().info.setIndex(idxstr + (allenidx++));
		} else
			this.info.setIndex(idxstr);
		this.index = idxstr;
	}

	/**
	 * The index for this protein in the final protein list. All the reference
	 * of this protein will be assigned as $index-n; To set the index of this
	 * protein, you can use {@link #setIndex(int)} method. If the protein index
	 * has not been set, the returned value will be null.
	 * 
	 * @return
	 */
	public String getIndexStr() {
		return this.index;
	}

	/**
	 * Which protein group does this protein belong to;
	 * 
	 * @param gidx
	 */
	public void setGroupIndex(int gidx) {
		this.groupIndex = gidx;

		this.info.setGroupIdx(gidx);
	}

	/**
	 * @return the index of ProteinGroup from which this protein comes.
	 */
	public int getGroupIndex() {
		return this.groupIndex;
	}

	/**
	 * Set how many proteins with shared peptides in this protein (the count
	 * includes this protein). This count equals to the count of proteins from
	 * the same ProteinGroup.
	 * 
	 * @param count
	 */
	public void setCrossProteinCount(int count) {
		this.crossProteinCount = count;

		this.info.setCrossProteinCount(count);
	}

	/**
	 * @return the count of proteins have shared peptides from the same
	 *         ProteinGroup. (Include this protein).
	 */
	public int getCrossProteinCount() {
		return this.crossProteinCount;
	}

	/**
	 * @return The count of ambiguous reference count;
	 */
	public int getReferenceCount() {
		return this.getReferences().length;
	}

	/**
	 * Set the probability for the protein (Reference) with the specific idx;
	 * The index must less than the number of getReferenceCount();
	 */
	public void setProbability(int idx, float prob) {
		if (idx >= this.getReferenceCount())
			throw new RuntimeException("Index out of range: " + idx);

		float p = prob > 1f ? 1f : prob;
		p = p < 0f ? 0f : p;

		this.getReferences()[idx].setProbability(p);
	}

	/**
	 * Set the same probability for the all the Reference in this protein;
	 */
	public void setProbability(float prob) {
		float p = prob > 1f ? 1f : prob;
		p = p < 0f ? 0f : p;

		IReferenceDetail[] refs = this.getReferences();
		for (int i = 0; i < refs.length; i++) {
			refs[i].setProbability(p);
		}
	}

	/**
	 * Used only when there are allene proteins.
	 * 
	 * @param prob
	 */
	public void setGroupProb(float prob) {
		float p = prob > 1f ? 1f : prob;
		p = p < 0f ? 0f : p;

		this.groupProb = p;
	}

	/**
	 * only when protein probability was calculated, this method can be called.
	 * If this protein contains no allene protein, this value is the probability
	 * of this protein. Otherwise, the returned probability is the groupProb.
	 * 
	 * @return the probability.
	 */
	public float getProbability() {
		return (this.alleneProteins != null) ? this.groupProb : this
		        .getReferences()[0].getProbability();
	}

	/**
	 * The formated probability with constant accuracy; If two proteins with the
	 * same probability, this value should be the same. Obviously, the bit count
	 * of the formatted value should less than that of a float value.
	 *
	 * <b>only when protein probability was calculated, this method can be
	 * called. If this protein contains no allene protein, this value is the
	 * probability of this protein. Otherwise, this method will iterate
	 * probabilities of all allene proteins, and the returned value is the max
	 * probability of this protein and its allene proteins</b>
	 * 
	 * @see getProbability();
	 */
	public String getFormatedProbability() {
		if (this.alleneProteins != null)
			return PROBDF.format(this.groupProb);
		else
			return PROBDF.format(this.getReferences()[0]);
	}

	/**
	 * @return protein reference;
	 */
	public IReferenceDetail[] getReferences() {
		return this.info.reflist;
	}

	/**
	 * The count of spectra for this protein identification. If the peptides
	 * were filtered after the creation of this protein, <b>use refresh() method
	 * to refresh the new informations first</b>
	 * 
	 * @return getSpectrum count
	 */
	public int getSpectrumCount() {
		return this.spcount;
	}

	/**
	 * The count of unique peptides for this protein identification. If the
	 * peptides were filtered after the creation of this protein, <b>use
	 * refresh() method to refresh the new informations first</b>
	 * 
	 * @return unique peptide count;
	 */
	public int getPeptideCount() {
		return this.pepcount;
	}

	/**
	 * Get the maximum coverage of this protein. That is the covered aminoacid
	 * percent of the shortest protein.
	 * 
	 * @return
	 */
	public float getMaxCoveredPercent() {
		return this.info.getRefWithHighestCoverage().getCoverage();
	}

	/**
	 * If this protein a target one. If it is a decoy one, return false;
	 */
	public boolean isTarget() {
		return this.isTarget;
	}

	/**
	 * Test whether this protein contains distinct peptides which can only be
	 * digest from this protein.
	 * 
	 * @return if this protein has distinct peptides
	 */
	public boolean isDistinct() {
		for (UniPep upep : this.uniPeps) {
			if (upep.getCountOfSharedPro() == 1) {
				if (upep.isUsed())
					return true;
			}
		}
		return false;
	}

	/**
	 * All the peptides in this protein.
	 * <p>
	 * <b>Notice: the peptides returned may be not used (used == false) if the
	 * peptides have been filtered after the creation of protein</b>
	 * 
	 * @return peptides in this protein
	 */
	public IPeptide[] getAllPeptides() {
		return this.peptides;
	}

	/**
	 * The String array of unique sequences. If the peptides have been filtered
	 * after the creation of this protein, calling of this method will return
	 * the currently used unique sequence for this protein identification
	 * 
	 * @return
	 */
	public String[] getUniqueSequences() {
		return this.getUniqueSequences0(this.uniPeps);
	}

	/**
	 * Get all the UniPep[] in this protein. *
	 * <p>
	 * <b>Notice: the UniPeps returned may be not used (used == false) if the
	 * UniPeps have been filtered after the creation of protein</b>
	 * 
	 * @return the UniPep[] containing in this protein
	 * @see also ProteinGroup.UniPep
	 */
	public UniPep[] getAllUniPeps() {
		return this.uniPeps;
	}

	/**
	 * The fasta accesser. May be null.
	 * 
	 * @return
	 */
	public IFastaAccesser getFastaAccesser() {
		return this.accesser;
	}

	/**
	 * Set the fasta accesser;
	 * 
	 * @param accesser
	 */
	public void setFastaAccesser(IFastaAccesser accesser) {
		this.accesser = accesser;
	}

	/**
	 * Refresh the reference details for the protein. You may want to filter the
	 * peptides in this protein by setting {@link #IPeptide.setUsed(boolean)}
	 * using specific filtering criteria (the peptides failed to pass the
	 * cirteria will not be removed from this protein but only set as unused).
	 * Use this method to refresh the reference details after the filtering.
	 * 
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 * 
	 * @since 1.1
	 */
	public void refresh() throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {

		this.refreshPeptideCount();

		String[] useqs = this.getUniqueSequences0(this.uniPeps);

		IReferenceDetail[] refs = this.info.bakeReflist;
		for (IReferenceDetail ref : refs) {
			
			ProteinSequence sequence = accesser.getSequence(ref.getName());

			ref.setSpectrumCount(spcount);
			ref.setPeptideCount(pepcount);

			ref.setCoverage(sequence.matches(useqs).getCoverage());

			// Refresh probability ?
		}
	}

	/*
	 * Refresh the peptide and spectrum count for the protein identification
	 */
	private void refreshPeptideCount() {
		/*
		 * The peptide and the spectrum count
		 */
		int spcount = 0;
		for (IPeptide pep : this.peptides)
			if (pep.isUsed())
				spcount++;
		this.spcount = spcount;

		int pepcount = 0;
		for (UniPep upep : this.uniPeps) {
			if (upep.isUsed()){
				pepcount++;
			}
		}
		this.pepcount = pepcount;
	}

	/**
	 * Remove a peptide from the protein
	 * 
	 * @param peptide
	 */
	public void removePeptide(IPeptide peptide) {
		int idx = 0;
		int len = this.peptides.length;
		IPeptide[] newpeps = new IPeptide[len];
		boolean removed = false;
		for (int i = 0; i < len; i++) {
			IPeptide pep = this.peptides[i];
			if (peptide.hashCode() != pep.hashCode()) {
				newpeps[idx++] = pep;
			} else
				removed = true;
		}
		if (removed) {
			this.peptides = Arrays.copyOf(newpeps, len - 1);

			this.getUniPeps0();

			String[] useqs = this.getUniqueSequences0(this.uniPeps);

			// Refesh the references
			IReferenceDetail[] refs = this.info.reflist;
			for (IReferenceDetail ref : refs) {
				ref.setSpectrumCount(this.getSpectrumCount());
				ref.setPeptideCount(this.getPeptideCount());

				if (this.accesser != null)
					try {
						ref.setCoverage(this.accesser
						        .getSequence(ref.getName()).matches(useqs)
						        .getCoverage());
					} catch (Exception e) {
						ref.setCoverage(0f);
						System.err
						        .println("Error while updating the new protein coverage, set as 0");
						System.err.println(e);
					}
				else {
					System.err
					        .println("The database accesser is null, set protein coverage as 0.");
					ref.setCoverage(0f);
				}
			}
		} else {
			throw new NullPointerException(
			        "Can't find the given peptide for removing.");
		}
	}

	/**
	 * If there are alleneproteins, the return unipeps is all unipeps in all
	 * proteins in this proteingroup; Otherwise, this value equals to
	 * getUniPeps();
	 * 
	 * @see getUniPeps();
	 * @return all the unipep in this protein group.
	 */
	public UniPep[] getGroupedUniPeps() {
		if (this.alleneProteins == null) {
			return this.getAllUniPeps();
		}

		HashSet<UniPep> set = new HashSet<UniPep>();
		for (int i = 0; i < this.uniPeps.length; i++)
			set.add(uniPeps[i]);

		for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
		        .hasNext();) {
			Protein alpro = iterator.next();
			UniPep[] alupep = alpro.getAllUniPeps();

			for (int i = 0; i < alupep.length; i++)
				set.add(alupep[i]);
		}

		return set.toArray(new UniPep[set.size()]);
	}

	/**
	 * To reduce the protein references in a protein to one. As there may be
	 * same peptides identifying some different proteins, to use a minimum set
	 * strategy, only one proteins can be considered. Which one should be used?
	 * This is the same strategy as Zeng Rong paper published in proteomics.
	 * 
	 * <p>
	 * <b>Note: This method works only when the reference is from ipi database.
	 * </b>
	 * 
	 * <p>
	 * <b>In some cases, proteins other than IPI proteins will be inserted into
	 * ipi database for some usage, e.g. Trypsin, if these proteins have no
	 * grouped reference, then they can be considered as ipi database too.</b>
	 * 
	 * <p>
	 * <b>A decoy protein will also be considered as an ipi protein, and the
	 * decoy protein with smallest molecular weight will be retained. </b>
	 * 
	 * <p>
	 * <b>After that the original reference list will also be sorted by the same
	 * strategy with the same order.</b>
	 * 
	 * @throws BioException
	 *             when the dabase is not a ipi dabase
	 */
	public void simplify() throws BioException {
		this.simplify(DEFAULT_SIMPLIFIER);
	}
	
	/**
	 * Use specified protein group simplifier to simplify the protein group.
	 * 
	 * @see #simplify()
	 */
	public void simplify(IProteinGroupSimplifier<IReferenceDetail> simplifier) throws BioException {
		this.info.simplify(simplifier);
		this.simplifier = simplifier;

		if (this.alleneProteins != null) {
			for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
			        .hasNext();)
				iterator.next().simplify(simplifier);
		}
	}
	
	/**
	 * This method is used for the SIn quantitation. To reduce the references list to only one 
	 * reference which has the biggest SIn. Because the references have same peptide list, so the one
	 * which has the shortest aminoacid sequence has the biggest SIn.
	 */
	public void simplify2(){
		this.info.simplify2();
		
		if (this.alleneProteins != null) {
			for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
			        .hasNext();)
				iterator.next().simplify2();
		}
	}
	
	/**
	 * Rewind the original references. If this protein has not been simplified, 
	 * nothing will happen. Otherwise, the original references will be used.
	 */
	public void rewindOriginalReferences() {
		this.info.rewindOriginalRef();
		this.simplifier = null;

		if (this.alleneProteins != null) {
			for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
			        .hasNext();)
				iterator.next().rewindOriginalReferences();
		}
	}
	
	/**
	 * Get the reference with smallest molecular. The reference list will not be
	 * changed. <b>Notice: If both target and deocy protein references are in
	 * the protein group only decoy protein with smallest molecular will be
	 * returned.</b>
	 * 
	 * @return the Reference with minumn molecular.
	 */
	public IReferenceDetail getRefwithSmallestMw() {
		return this.info.getRefWithSmallestMw();
	}

	/**
	 * Add an protein as an allene protein of this protein.
	 * 
	 * The allene protein is different from an allene reference in a protein.
	 * Allene references are proteins that can't be distinguished from the
	 * peptides as the identified peptides are all contained by these allene
	 * references. However, allene proteins are different proteins, they have
	 * their distinct peptide identifications even though there are shared
	 * peptides. But the unique peptides for the allene proteins are not power
	 * enough to show the protein identifications. Therefore, they are grouped
	 * together.
	 * 
	 * <b>Only one of this allene proteins (that is "this" protein) can appear
	 * in the final protein list. And all other allene proteins are put into the
	 * allene protein list of this protein. It should be noted that other allene
	 * proteins will not "have" allene proteins (allene protein list == null).
	 * </b>
	 * 
	 * @see ProteinGourp.getProtein(IProteinProbCalcuator)
	 * 
	 * @param p
	 */
	public void addAlleneProtein(Protein p) {
		if (this.alleneProteins == null)
			this.alleneProteins = new LinkedList<Protein>();

		p.setCrossProteinCount(this.getCrossProteinCount());
		p.setGroupIndex(this.getGroupIndex());
		this.alleneProteins.add(p);
	}

	/**
	 * The allene protein is different from an allene reference in a protein.
	 * Allene references are proteins that can't be distinguished from the
	 * peptides as the identified peptides are all contained by these allene
	 * references. However, allene proteins are different proteins, they have
	 * their distinct peptide identifications even though there are shared
	 * peptides. But the unique peptides for the allene proteins are not power
	 * enough to show the protein identifications. Therefore, they are grouped
	 * together.
	 * 
	 * <b>Only one of this allene proteins (that is "this" protein) can appear
	 * in the final protein list. And all other allene proteins are put into the
	 * allene protein list of this protein. It should be noted that other allene
	 * proteins will not "have" allene proteins (allene protein list == null).
	 * </b>
	 * 
	 * @see ProteinGourp.getProtein(IProteinProbCalcuator)
	 * 
	 * @return the allene proteins of this protein, may be null.
	 */
	public LinkedList<Protein> getAlleneProteinList() {
		return this.alleneProteins;
	}
	
	/**
	 *  SIn is spectral index, which combines three MS abundance features: peptide count, 
	 *  spectral count and fragment-ion (tandem MS or MS/MS) intensity.
	 *  
	 *  <p>See nature biotechnology volume 28 number 1 january 2010
	 */
	private double getSIn(double totalInten, int sequenceLength){
		
		double fragInten = 0;
		if(totalInten==0||sequenceLength==0)
			return 0;
		
		IPeptide [] peps = this.getAllPeptides();
		for(IPeptide pep :peps){
			fragInten += pep.getFragInten();
		}
		
		double SIn = Double.parseDouble(df8.format(fragInten/totalInten/sequenceLength));
		return SIn;
	}

	/**
	 * This method can calculate the SIn for the protein
	 * @param totalInten
	 */
	public void setTotalInten(double totalInten){

		IReferenceDetail[] refs = this.info.reflist;
		for(IReferenceDetail ref:refs){
			int length = ref.getNumAminoacids();
			double SIn = getSIn(totalInten,length);
			ref.setSIn(SIn);
		}
	}
	
	/**
	 * Compared with toString(), Peptide informations will be described in
	 * UniPep type. That is, redundant peptides will not be printed, instead,
	 * UniPep information will be printed.
	 * 
	 * @see UniPep.toString().
	 */
	public String toUniPepString() {
		StringBuilder sb = new StringBuilder(300);
		boolean gr = this.alleneProteins != null;
		if (gr) {
			sb.append('$').append(index).append(" - 0\t").append(
			        "ProteinGroup: ").append(this.groupIndex).append('\t')
			        .append(this.getSpectrumCount()).append("\t").append(
			                this.getPeptideCount()).append('\t').append(
			                "Proteins: ").append(
			                this.getAlleneProteinList().size() + 1)
			        .append('\t').append(this.getFormatedProbability()).append(
			                "\t\t\t").append(this.groupIndex).append('\t')
			        .append(this.crossProteinCount).append('\t').append(
			                isTarget ? "1" : "-1").append(lineSeparator);
		}

		sb.append(this.info.toString());

		for (int i = 0, n = uniPeps.length; i < n; i++) {
			UniPep upep = uniPeps[i];
			sb.append(upep).append("\r\n");
		}

		if (gr) {
			for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
			        .hasNext();)
				sb.append(iterator.next().toString());
		}

		return sb.toString();
	}

	public void setUnique(boolean unique){
		this.itUnique = unique;
	}
	
	public boolean getUnique(){
		return itUnique;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(300);
		boolean gr = this.alleneProteins != null;
		if (gr) {
			sb.append("$").append(index).append(" - 0\t").append(
			        "ProteinGroup: ").append(this.groupIndex).append("\t")
			        .append(this.getSpectrumCount()).append("\t").append(
			                this.getPeptideCount()).append("\t").append(
			                "Proteins: ").append(
			                this.getAlleneProteinList().size() + 1)
			        .append("\t").append(this.getFormatedProbability()).append(
			                "\t\t\t").append(this.groupIndex).append("\t")
			        .append(this.crossProteinCount).append("\t").append(
			                isTarget ? "1" : "-1").append(lineSeparator);
		}

		sb.append(this.info.toString());
		for (int i = 0, n = peptides.length; i < n; i++) {
			sb.append(peptides[i]);
			sb.append(lineSeparator);
		}

		if (gr) {
			for (Iterator<Protein> iterator = this.alleneProteins.iterator(); iterator
			        .hasNext();)
				sb.append(iterator.next().toString());
		}

		return sb.toString();
	}

	/**
	 * Class of protein reference informations
	 */
	private class ProteinInfo {
		private IReferenceDetail[] reflist;
		/*
		 * Index of this protein;
		 */
		private String index;

		/*
		 * The original Reference list. This reference list will not changed so
		 * that it will always be the originla one. If the protein group
		 * simplify is used, then a simplified reference list containing only
		 * one simplified reference will be generated, and the original
		 * reference list will be stored in the bake reference list for
		 * potential usage
		 */
		private final IReferenceDetail[] bakeReflist;

		/*
		 * If use the simplified reference.
		 */
		private boolean isUsedSimple = false;

		private ProteinInfo(IReferenceDetail[] reflist) {
			this.bakeReflist = this.reflist = reflist;
		}

		/**
		 * @param index
		 * @param num
		 */
		void setIndex(String index) {
			StringBuilder sb = new StringBuilder(10);
			this.index = sb.append('$').append(index).append(" - ").toString();
		}

		/**
		 * Set the group index
		 * 
		 * @param index
		 */
		void setGroupIdx(int index) {
			for (IReferenceDetail ref : this.bakeReflist) {
				ref.setGroupIndex(index);
			}
		}

		/**
		 * Count of crossed protein
		 * 
		 * @param count
		 */
		void setCrossProteinCount(int count) {
			for (IReferenceDetail ref : this.bakeReflist) {
				ref.setCrossProtein(count);
			}
		}

		/**
		 * To reduce the protein references in a protein to one. As there may be
		 * same peptides identifying some different proteins, to use a minimum
		 * set strategy, only one proteins can be considered. Which one should
		 * be used? This is the same strategy as Zeng Rong paper published in
		 * proteomics.
		 * 
		 * <p>
		 * <b>In some cases, proteins other than IPI proteins will be inserted
		 * into ipi database for some usage, e.g. Trypsin, if these proteins
		 * have no grouped reference, then they can be considered as ipi
		 * database too.</b>
		 * 
		 * <p>
		 * <b>A decoy protein will also be considered as an ipi protein, and the
		 * decoy protein with smallest molecular weight will be retained. </b>
		 * 
		 * @throws BioException
		 *             when the database is not a ipi database
		 */
		protected void simplify(IProteinGroupSimplifier<IReferenceDetail> simplifier) throws BioException {
			if (this.isUsedSimple) {
				//rewind and restart the simplifier
				this.rewindOriginalRef();
			}
			
			if (!this.isSimplifiable())
				throw new BioException(
				        "Cann't Simplify references not from a IPI database");

			int len = this.bakeReflist.length;
			if (len != 1) {
				this.reflist = new IReferenceDetail[] { simplifier
				        .simplify(this.bakeReflist) };
			}
			this.isUsedSimple = true;
		}
		/**
		 * This method is used for the SIn quantitation. To reduce the references list to only one 
		 * reference which has the biggest SIn. Because the references have same peptide list, so the one
		 * which has the shortest aminoacid sequence has the biggest SIn.
		 */
		public void simplify2(){
			int len = this.bakeReflist.length;
			if (len != 1) {
				IReferenceDetail ref = bakeReflist[0];
				int length = ref.getNumAminoacids();
				for(int i=1;i<bakeReflist.length;i++){
					if(bakeReflist[i].getNumAminoacids()<length){
						ref = bakeReflist[i];
					}
				}
/*				
				double SIn = ref.getSIn();
				for(int i=1;i<bakeReflist.length;i++){
					if(bakeReflist[i].getSIn()>SIn){
						SIn = bakeReflist[i].getSIn();
						ref = bakeReflist[i];
					}
				}
*/				
				this.reflist = new IReferenceDetail[] {ref};
			}
		}
		
		/**
		 * Get the reference with smallest molecular. The reference list will
		 * not be changed. <b>Notice: If both target and deocy protein
		 * references are in the protein group only decoy protein with smallest
		 * molecular will be returned.</b>
		 * 
		 * @return the Reference with minumn molecular.
		 */
		public IReferenceDetail getRefWithSmallestMw() {
			int len = this.bakeReflist.length;
			if (len == 1)
				return this.bakeReflist[0];

			double minmw = Double.MAX_VALUE;
			IReferenceDetail ret = null;
			for (int i = 0; i < len; i++) {
				IReferenceDetail tmp = this.bakeReflist[i];
				double mw = tmp.getMW();
				if (mw < minmw) {
					minmw = mw;
					ret = tmp;
				}
			}
			return ret;
		}

		/**
		 * Get the max covered protein reference.
		 * <p>
		 * This reference may or may NOT the same with getRefWithSmallestMw();
		 * 
		 * @return the reference with the max covered aminoacid
		 */
		public IReferenceDetail getRefWithHighestCoverage() {
			int len = this.bakeReflist.length;
			if (len == 1)
				return this.bakeReflist[0];

			double maxCover = 0d;
			IReferenceDetail ret = null;
			for (int i = 0; i < len; i++) {
				IReferenceDetail tmp = this.bakeReflist[i];
				float cov = tmp.getCoverage();
				if (cov > maxCover) {
					maxCover = cov;
					ret = tmp;
				}
			}
			return ret;
		}

		/*
		 * Checkout if this database is a ipi database or a single reference.
		 * 
		 * In some cases, proteins other than IPI proteins will be inserted into
		 * ipi database for some usage, e.g. Trypsin, if these proteins have no
		 * grouped reference, then they can be considered as ipi database too.
		 * 
		 * If this is a decoy proteins, it can also be considered as IPI protein
		 * 
		 * 
		 * ********************ipi not checked. currently*******************8
		 */
		private boolean isSimplifiable() {

			return true;

			/*
			 * if(!isTarget()) return true;
			 * 
			 * int len = this.reflist.length; if(len==1) return true;
			 * 
			 * 
			 * for(int i=0;i<len;i++){
			 * if(!reflist[i].getName().startsWith("IPI")){ return false; } }
			 * 
			 * return true;
			 */
		}

		/**
		 * Rewind to the original references. If it has been original
		 * references, none will happen.
		 * 
		 * <b>After that the original reference list will also be sorted by the
		 * same strategy with the same order. Therefore, the rewind reference
		 * list may has different order with original reference list;</b>
		 */
		public void rewindOriginalRef() {
			if (this.isUsedSimple) {
				this.reflist = this.bakeReflist;
				this.isUsedSimple = false;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(200);
			for (int i = 0, n = reflist.length; i < n; i++) {
				sb.append(this.index).append(i + 1);
				sb.append(reflist[i].toString()).append(lineSeparator);
			}
			return sb.toString();
		}
	}

	/**
	 * First arranged by unique peptide count, then by peptide count,
	 */
	public int compareTo(Protein p2) {
		int p1p = getPeptideCount();
		int p2p = p2.getPeptideCount();

		if (p1p == p2p) {
			int p1s = getSpectrumCount();
			int p2s = p2.getSpectrumCount();
			if (p1s == p2s)
				return 0;
			return p1s > p2s ? -1 : 1;
		}

		return p1p > p2p ? -1 : 1;
	}

	/**
	 * Generator for ProteinGroup;
	 * 
	 * @param ref
	 * @param accesser
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	Protein(SimPro ref, ProteinNameAccesser accesser)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {

		this.proNameAccesser = accesser;
		HashSet<UniPep> tset = ref.getUniPeps();
		int len = tset.size();
		this.uniPeps = tset.toArray(new UniPep[len]);

		ArrayList<IPeptide> peplist = new ArrayList<IPeptide>();
		for (int i = 0; i < len; i++) {
			UniPep tp = uniPeps[i];

			// set relationship with this protein
			tp.addProtein(this);

			for (Iterator<IPeptide> iterator1 = tp.getPeptideIterator(); iterator1
			        .hasNext();) {
				IPeptide pep = iterator1.next();
				if (pep.isUsed())
					peplist.add(pep);
			}
		}
		this.peptides = peplist.toArray(new IPeptide[peplist.size()]);

		this.refreshPeptideCount();

//		String[] useqs = this.getUniqueSequences0(this.uniPeps);

		int size = ref.size();
		IReferenceDetail[] references = new ReferenceDetail[size];
		references[0] = this.getRef(ref.getReference(), accesser);

		if (size > 1) {
			LinkedList<String> list = ref.getAlleneRefList();
			Iterator<String> iterator = list.iterator();
			for (int i = 1; iterator.hasNext(); i++) {
				references[i] = this.getRef(iterator.next(), accesser);
			}
		}

		this.isTarget = this.judgeTarget(references);
		this.info = new ProteinInfo(references);
		

		/*
		 * Arrange peptides by scan number;
		 */
		Arrays.sort(peptides, new Comparator<IPeptide>() {
			public int compare(IPeptide o1, IPeptide o2) {
				String s1 = o1.getScanNum();
				String s2 = o2.getScanNum();
				return s1.compareTo(s2);
			}
		});
	}

	/**
	 * Generator for ProteinGroup;
	 * 
	 * @param ref
	 * @param accesser
	 * @param groupIdx
	 * @param crossProteinCount
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	Protein(SimPro ref, ProteinNameAccesser accesser, int groupIdx,
	        int crossProteinCount) throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		this(ref, accesser, groupIdx);
		this.crossProteinCount = crossProteinCount;
	}

	/**
	 * Generator for ProteinGroup;
	 * 
	 * @param ref
	 * @param accesser
	 * @param groupIdx
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	Protein(SimPro ref, ProteinNameAccesser accesser, int groupIdx)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		this(ref, accesser);
		this.groupIndex = groupIdx;
	}

}
