/*
 * *****************************************************************************
 * File: UniPep.java * * * Created on 09-23-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * <p>
 * Unique peptide which describes the protein identification. A unique peptide
 * is defined as the unique sequence (including the modification) with the
 * unique charge state. for example a peptide AERK#EPNK with charge state 2+,
 * another peptide AERK#EPNK with charge state 3+ will be considered as two
 * different unique peptides.
 * 
 * <p>
 * <b>This is mainly designed for the convenience of probability computation.
 * However, the strategy of unique peptide statistic in the final protein
 * identification is not the same!</b>
 * 
 * @author Xinning
 * @version 0.6, 09-23-2008, 16:31:00
 */
public class UniPep {

	/**
	 * Because the protein group is consisted by UniPep, it is necessary to
	 * determine how well this peptide is identified. So, a delegate peptide for
	 * the unique peptide is needed. This delegate peptide should be used to
	 * indicate the exist of UniPep. Therefore, the delegate peptide can be
	 * peptide with biggest <b>PrimaryScore</b> value or with most
	 * <b>probability</b> (if probability has been computed).
	 * 
	 * If this option is selected, peptide with biggest primary score will be
	 * selected as the delegate peptide. <b>Default</b>
	 */
	public static final int UNIPEP_DELEGATE_PEP_BIG_Prim_score = 1;

	/**
	 * Because the protein group is consisted by UniPep, it is necessary to
	 * determine how well this peptide is identified. So, a delegate peptide for
	 * the unique peptide is needed. This delegate peptide should be used to
	 * indicate the exist of UniPep. Therefore, the delegate peptide can be
	 * peptide with biggest <b>PrimaryScore</b> value or with most
	 * <b>probability</b> (if probability has been computed).
	 * 
	 * If this option is selected, peptide with biggest probability value will
	 * be selected as the delegate peptide.
	 */
	public static final int UNIPEP_DELEGATE_PEP_BIGPROBABILITY = 2;

	private static int delegateType = UNIPEP_DELEGATE_PEP_BIG_Prim_score;
	private static boolean typechanged = false;

	private int hashcode;
	private String key;
	// The unique sequence string
	private String uniqueseq;

	// only used (affect) for the probability calculation.
	private boolean changed1 = false;
	// delegate peptide
	private boolean changed2 = false;
	private float probability = -1f;

	/*
	 * Just divide the summed probability of all spectra identifications by the
	 * spectra number.
	 */
	private float avgProbability = -1f;

	/*
	 * If the protein reference has been formatted to the minimum length with
	 * which the protein references in the database are distinguishable.
	 */
	private boolean formattedReference;

	private ArrayList<IPeptide> peptides;
	private HashSet<Protein> proteins;

	private HashSet <ProteinReference> reflist;
	private HashMap <String, SeqLocAround> locAroundMap;

	// how many times the protein list was cleared.
	// private int clearcount = 0;

	/*
	 * A peptide indicated this unique peptide. Commonly, this peptide is the
	 * peptide with biggest xcorr value. If needed, one can specify the
	 * Comparatror<Peptide>, then the peptide will be the peptide with index of
	 * 0 after sorted by the specific comparator.
	 */
	private IPeptide peptide;

	/**
	 * Create a UniPep.
	 * 
	 * <p>
	 * It is assumed that the peptide with same unique sequence should with the
	 * same protein reference list. This will always be true if the database
	 * search algorithm has no bug.
	 * </p>
	 * 
	 * @param key
	 *            key of the UniPep which is the unique identifier for this
	 *            UniPep. This is the key in UniPeps which may be "z_sequence"
	 *            and others.
	 * @param reflist
	 *            the reference list which will be used to replace all the
	 *            reference list for IPeptide added by add(IPeptide) method.
	 */
	UniPep(String key, String uniqueseq, HashSet<ProteinReference> reflist) {
		this.key = key;
		this.uniqueseq = uniqueseq;
		this.peptides = new ArrayList<IPeptide>();
		this.hashcode = key.hashCode();
		this.reflist = reflist;
	}
	
	UniPep(String key, String uniqueseq, HashSet <ProteinReference> reflist, HashMap <String, SeqLocAround> locAroundMap) {
		this.key = key;
		this.uniqueseq = uniqueseq;
		this.peptides = new ArrayList<IPeptide>();
		this.hashcode = key.hashCode();
		this.reflist = reflist;
		this.locAroundMap = locAroundMap;
	}

	/**
	 * Set the index of unipep
	 */
	// public void setIndex(int idx){
	// this.idx = idx;
	// }
	/**
	 * Set the type for delegate peptide selection.
	 * 
	 * @param type
	 *            currently must be
	 *            ProteinGroup.UNIPEP_DELEGATE_PEP_BIGPROBABILITY or
	 *            ProteinGroup.UNIPEP_DELEGATE_PEP_BIGXC
	 */
	public static void setDelegatePepType(int type) {
		if (type == UNIPEP_DELEGATE_PEP_BIGPROBABILITY) {
			if (type != delegateType) {
				typechanged = true;
				delegateType = UNIPEP_DELEGATE_PEP_BIGPROBABILITY;
			}
		} else if (type == UNIPEP_DELEGATE_PEP_BIG_Prim_score) {
			if (type != delegateType) {
				typechanged = true;
				delegateType = UNIPEP_DELEGATE_PEP_BIG_Prim_score;
			}
		} else
			throw new RuntimeException(
			        "The inputed delegate peptide type is not valid, see also "
			                + "ProteinGroup.UNIPEP_DELEGATE_PEP_..");
	}

	/**
	 * Key of this UniPep which is the unique identifier for this UniPep, e.g.
	 * 2_ERLIK.
	 * 
	 * @return the uniseqcharge
	 */
	public String getKeyString() {
		return key;
	}

	/**
	 * The unique sequence of this UniPep. At least the unique sequence is the
	 * same for a UniPep
	 * 
	 * @return
	 */
	public String getUniqueSequence() {
		return this.uniqueseq;
	}

	/**
	 * Used only when calculation of protein probability. This value is the
	 * maximum value of all peptides with this unique sequence and charge state.
	 * 
	 * @return the probability
	 */
	public float getProbability() {
		if (changed1)
			this.iterateProbability();

		return probability;
	}

	/**
	 * If this UniPep is used for the identification of protein. this.isUsed() ==
	 * at leas one of the IPeptide in this UniPep isUsed().
	 * 
	 * <p>This value is dynamically determined.
	 * 
	 * @return
	 */
	public boolean isUsed() {
		
		for(IPeptide peptide : this.peptides){
			if(peptide .isUsed())
				return true;
		}
		
		return false;
	}

	/**
	 * Just divide the summed probability of all spectra identifications by the
	 * spectra number.
	 * 
	 * @return the average probability for this UniPep.
	 */
	public float getAverageProbability() {
		if (changed1)
			this.iterateProbability();
		return this.avgProbability;
	}

	/*
	 * If changed1, the probability information must be re-calculated.
	 */
	private void iterateProbability() {
		float prob = -1f;
		float total = 0f;
		for (Iterator<IPeptide> iterator = this.peptides.iterator(); iterator
		        .hasNext();) {
			IPeptide pep = iterator.next();
			float tprob = pep.getProbabilty();
			if (tprob < -0f) {
				if (tprob < -0.5f)
					throw new RuntimeException(
					        "Cann't calculate probability information for UniPep from"
					                + "peptides without probability! Current Probabiltiy: "
					                + tprob);

				tprob = 0f;
			}

			total += tprob;
			if (prob < tprob)
				prob = tprob;
		}

		this.probability = prob;
		this.avgProbability = total / this.getPeptideCount();

		changed1 = false;
	}

	/**
	 * A peptide indicated this unique peptide. Commonly, this peptide is the
	 * peptide with biggest xcorr value. If needed, one can specify the
	 * Comparatror<Peptide>, then the peptide will be the peptide with index of
	 * 0 after sorted by the specific comparator. (use getPeptide(Comparator<Peptide>),
	 * currently not used);
	 * 
	 * @return peptide with biggest Xcorr value.
	 * @throws RuntimeException
	 *             if using probability delegate peptide selection mode on
	 *             peptide without probability.
	 */
	public IPeptide getDelegratePeptide() {
		if (peptide == null || typechanged || changed2) {
			int size = this.peptides.size();
			if (size == 1)
				peptide = this.peptides.iterator().next();
			else {
				if (delegateType == UNIPEP_DELEGATE_PEP_BIG_Prim_score) {
					float bxc = -1f;

					// Sequest identification should be parsed separately

					for (Iterator<IPeptide> iterator = this.peptides.iterator(); iterator
					        .hasNext();) {
						IPeptide pe = iterator.next();
						float txc = pe.getPrimaryScore();
						if (txc > bxc) {
							peptide = pe;
							bxc = txc;
						}
					}
				} else {
					float bprob = -1f;
					for (Iterator<IPeptide> iterator = this.peptides.iterator(); iterator
					        .hasNext();) {
						IPeptide pe = iterator.next();
						float tprob = pe.getProbabilty();
						if (tprob > bprob) {
							peptide = pe;
							bprob = tprob;
						}
					}

					if (bprob < -0f) {
						throw new RuntimeException(
						        "Cann't get delegated peptide by probability from"
						                + "peptides without probability Current Probabiltiy: "
						                + bprob);
					} else
						this.probability = bprob;
				}
			}
			changed2 = false;
			typechanged = false;
		}

		return this.peptide;
	}

	/**
	 * The iterator of peptide with this unique sequence and charge state
	 */
	public Iterator<IPeptide> getPeptideIterator() {
		return peptides.iterator();
	}

	/**
	 * How many peptides are with this unique sequence.
	 * 
	 * @return the count of peptides with this unique sequence.
	 */
	public int getPeptideCount() {
		return this.peptides.size();
	}

	/**
	 * Proteins identified by this unique sequence
	 * 
	 * @return the proteins
	 */
	public HashSet<Protein> getProteins() {
		return proteins;
	}

	/**
	 * How many proteins shared this peptide for their identification
	 * 
	 * equals getProteins.size();
	 * 
	 */
	public int getCountOfSharedPro() {
		return this.proteins == null ? 0 : this.proteins.size();
	}

	/**
	 * Add a peptide with this unique sequence and charge state in
	 */
	void addPeptide(IPeptide peptide) {
		
		changed1 = true;
		changed2 = true;
		if (this.peptides.add(peptide)) {
//			this.reflist.addAll(peptide.getProteinReferences());
			peptide.setProteinReference(this.reflist);
			peptide.setPepLocAroundMap(locAroundMap);
		}
	}

	/**
	 * Remove a peptide from the unipeptide. Only affect the while this peptide
	 * is contained in the unipep list.
	 * <p>
	 * <b>Make sure there are remained peptide in this unipep</b>
	 * 
	 * @param peptide
	 */
	public void removePeptide(IPeptide peptide) {
		changed1 = true;
		changed2 = true;
		this.peptides.remove(peptide);
	}

	/**
	 * Add a protein identified by this unique sequence in
	 */
	public void addProtein(Protein protein) {
		if (this.proteins == null)
			proteins = new HashSet<Protein>();
		this.proteins.add(protein);
	}

	/**
	 * Remove the protein from the protein identification list. If this protein
	 * is not contained in the protein list, nothing will happended.
	 * 
	 * @param protein
	 *            the protein to be removed.
	 */
	public void removeProtein(Protein protein) {
		if (this.proteins != null)
			this.proteins.remove(protein);
	}

	/**
	 * If this unique peptide can result in the specific protein identification.
	 * 
	 * @param protein
	 * @return true if this peptide belongs to the protein
	 */
	public boolean belongsTo(Protein protein) {
		return this.proteins == null ? false : this.proteins.contains(protein);
	}

	/**
	 * Remove all the corresponding proteins.
	 */
	public void clearProteins() {
		if (this.proteins != null) {
			this.proteins = null;
			// this.clearcount ++;
		}
	}

	/**
	 * Format the protein reference to minimize the storage. The reference will
	 * be shorten to the length of minimum distinguishable length
	 * 
	 * @param accesser
	 */
	public void formatReference(IFastaAccesser accesser) {
		if (!this.formattedReference) {
			// throw new IllegalArgumentException("The reference of peptides can
			// only be formatted once.");

			for (Iterator<ProteinReference> it = this.reflist.iterator(); it
			        .hasNext();) {
				ProteinReference pref = it.next();
				String ref = pref.getName();
				
				/*
				 * In very few cases, the partial reference of the same protein
				 * outputted by sequest may be with different lengths, e.g.
				 * IPI:IPI00218816.4|SWISS-PROT:P68871|TREMBL and
				 * IPI:IPI00218816.4|SWISS-PROT:, to avoid assamble them as two
				 * different proteins , the reference must be formatted into the
				 * same length.
				 */
				if (accesser.getDecoyJudger().isDecoy(ref)) {
					if (ref.length() > accesser.getSplitRevLength())
						ref = ref.substring(0, accesser.getSplitRevLength());
				} else {
					if (ref.length() > accesser.getSplitLength())
						ref = ref.substring(0, accesser.getSplitLength());
				}

				// -----------------------------------------------------------//
				// Refresh the name by using the reference with minimum length
				pref.setName(ref);
			}
			
			this.formattedReference = true;
		}
	}

	/**
	 * Used in cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2
	 * @see in cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2
	 * 
	 * @param nameAccesser
	 */
/*	
	public void formatReference(ProteinNameAccesser nameAccesser) {
		// TODO Auto-generated method stub
		if (!this.formattedReference) {
			// throw new IllegalArgumentException("The reference of peptides can
			// only be formatted once.");

			for (Iterator<ProteinReference> it = this.reflist.iterator(); it
			        .hasNext();) {
				ProteinReference pref = it.next();
				String ref = pref.getName();

				if (nameAccesser.getDecoyJudger().isDecoy(ref)) {
					if (ref.length() > nameAccesser.getSplitRevLength())
						ref = ref.substring(0, nameAccesser.getSplitRevLength());
				} else {
					if (ref.length() > nameAccesser.getSplitLength())
						ref = ref.substring(0, nameAccesser.getSplitLength());
				}

				pref.setName(ref);
			}
			
			this.formattedReference = true;
		}
	}
*/	
	/**
	 * The reference list
	 * 
	 * @return
	 */
	public Set<ProteinReference> getProteinReferences() {
		return this.reflist;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass() && key.equals(((UniPep) obj).key))
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return this.hashcode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\t').append(this.key).append('\t').append(
		        this.getPeptideCount()).append('\t').append(
		        this.getProbability()).append('\t').append(
		        this.getAverageProbability());
		if (this.getCountOfSharedPro() > 1) {
			sb.append("\t*");
		}

		return sb.toString();
	}

	
}
