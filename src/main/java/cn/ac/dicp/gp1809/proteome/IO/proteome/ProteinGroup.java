/*
 * *****************************************************************************
 * File: ProteinGroup.java * * * Created on 09-16-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.NoPeptideProbabilityException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.probability.IProteinProbCalculator;

/**
 * A temporary class used for storage of proteins which may be grouped together
 * 
 * @author Xinning
 * @version 0.8.2, 08-18-2008, 15:06:24
 */
public class ProteinGroup {

	// private static char spliter = Peptide.ProteinNameSpliter;

	/*
	 * key is the protein reference name, and the value is the index of peptides
	 * which identified this protein; Since the peptide list is unremovable,
	 * thus, through the index, specific peptide can be get.
	 */
	private HashMap<String, HashSet<UniPep>> refmap;

	private IFastaAccesser accesser;

	// The index of proteingroup in proteins
	private int groupIdx;

	ProteinGroup(IFastaAccesser accesser, int groupIdx) {
		this.refmap = new HashMap<String, HashSet<UniPep>>(5);
		this.accesser = accesser;
		this.groupIdx = groupIdx;
	}

	/**
	 * Add a peptide to the protein group
	 * 
	 * @param pep
	 *            peptide
	 * @param proteinrefs
	 *            names of the proteins which are identified by this peptide;
	 */
	void addPeptide(UniPep upep, String[] proteinrefs) {
		for (int i = 0, n = proteinrefs.length; i < n; i++) {
			String ref = proteinrefs[i];
			HashSet<UniPep> tempset;

			if ((tempset = this.refmap.get(ref)) == null) {
				tempset = new HashSet<UniPep>();
				refmap.put(proteinrefs[i], tempset);
			}
			tempset.add(upep);
		}
	}

	/**
	 * Add a peptide to the protein group
	 * 
	 * @param pep
	 *            peptide
	 */
	// void addPeptide(Peptide pep){
	// String[] proteinrefs = StringUtil.split(pep.getProteinReference(),
	// spliter);
	// this.addPeptide(pep, proteinrefs);
	// }
	/**
	 * Add another protein group in. This situation appears when two distinct
	 * protein group have been formed and they must be merged together as a new
	 * peptide describes identification of proteins from this group and the
	 * group to be added. Therefore, the reference and peptides in these two
	 * groups are absolutely different.
	 * 
	 * @param group
	 *            another proteingroup (<b>the reference and peptides in these
	 *            two groups are absolutely different </b>)
	 * @return this proteingroup contains new information from another
	 *         proteingroup
	 */
	ProteinGroup addAll(ProteinGroup group) {
		for (Iterator<Entry<String, HashSet<UniPep>>> iterator = group.refmap
		        .entrySet().iterator(); iterator.hasNext();) {
			Entry<String, HashSet<UniPep>> entry = iterator.next();
			HashSet<UniPep> set = entry.getValue();
			this.refmap.put(entry.getKey(), set);
		}
		return this;
	}

	/**
	 * @return number of reference in this protein group;(Before filter of
	 *         peptides)
	 */
	int getRefCount() {
		return this.refmap.size();
	}

	/**
	 * Get the iterator for protein references containing in this protein group;
	 * 
	 * @return Reference iterator
	 */
	Iterator<String> getRefIterator() {
		return this.refmap.keySet().iterator();
	}

	/**
	 * Return the protein instance included in this protein group; While peptide
	 * may be filtered with criteria before, thus, null may be returned when
	 * there was no peptide in this protein group after filter;
	 * 
	 * @return protein (null);
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	public Protein[] getProtein() throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {

		LinkedList<SimPro> list = null;
		for (Iterator<Entry<String, HashSet<UniPep>>> iterator = this.refmap
		        .entrySet().iterator(); iterator.hasNext();) {
			Entry<String, HashSet<UniPep>> entry = iterator.next();
			SimPro ref = this.getSimPro(entry);
			if (ref != null) {
				if (list == null)
					list = new LinkedList<SimPro>();
				list.add(ref);
			}
		}
		if (list == null)
			return null;

		if (list.size() == 1)
			return new Protein[] { new Protein(list.iterator().next(),
			        accesser, this.groupIdx, 1) };

		LinkedList<Protein> prolist = new LinkedList<Protein>();
		while (list.size() > 0) {
			Iterator<SimPro> iterator = list.iterator();
			SimPro ref = iterator.next();
			iterator.remove();
			/*
			 * when the main reference (used as comparator and generated the
			 * cross event) is smaller than the iterator one, then the iterator
			 * one would instead the main reference. Therefore, if cross event
			 * has occurred, the newly generated main reference(iterator one)
			 * may be bigger than the cross event. i.e. a != b, a < c, and b <
			 * c. The c should be the correct identification and a and b should
			 * be removed. Thus, a new iteration from the beginning is needed.
			 */
			boolean iscross = false;
			while (iterator.hasNext()) {
				SimPro temp = iterator.next();
				int value = ref.comparewith(temp);
				switch (value) {
				case 0:
					ref.add(temp.ref);
					iterator.remove();
					break;// equals
				case 1:
					iterator.remove();
					break;// include
				case -1:
					ref = temp;
					iterator.remove();
					// only when cross occurred, reiteration needs;
					if (iscross) {
						iterator = list.iterator();
						iscross = false;
					}
					break;// small
				default:
					iscross = true;
					break;// 2
				}
			}

			prolist.add(new Protein(ref, accesser, groupIdx));
		}

		/*
		 * Remove the protein contains no distinct peptide.
		 * 
		 * This is mainly for the removal of proteins containing only crossed
		 * peptides. For example, protein 1 contains peptide a and b, while
		 * protein 2 contains peptide c and d, if protein 3 only contains
		 * peptide b and c, the protein 3 will be removed at this step.
		 */
		for (Iterator<Protein> iterator = prolist.iterator(); iterator
		        .hasNext();) {
			Protein p = iterator.next();
			if (p.isDistinct())
				continue;

			iterator.remove();
		}

		int len = prolist.size();
		Protein[] pros = prolist.toArray(new Protein[len]);
		for (int i = 0; i < len; i++) {
			pros[i].setCrossProteinCount(len);
		}

		return pros;
	}

	/**
	 * In some cases, such as protein quantitation, all the proteins in this proteinGroup will be needed, that means, the protein contains
	 * no distinct peptide will be reserve. 
	 * @return
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 */
	public Protein[] getAllProtein() throws ProteinNotFoundInFastaException,
		MoreThanOneRefFoundInFastaException, FastaDataBaseException {

		LinkedList<SimPro> list = null;
		for (Iterator<Entry<String, HashSet<UniPep>>> iterator = this.refmap
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, HashSet<UniPep>> entry = iterator.next();
			SimPro ref = this.getSimPro(entry);
			if (ref != null) {
				if (list == null)
					list = new LinkedList<SimPro>();
				list.add(ref);
			}
		}
		if (list == null)
			return null;

		if (list.size() == 1){
			Protein p = new Protein(list.iterator().next(),
					accesser, this.groupIdx, 1);
			p.setUnique(true);
			return new Protein[] {p};
		}

		LinkedList<Protein> prolist = new LinkedList<Protein>();
		
		SimPro [] sims = list.toArray(new SimPro[list.size()]);
		HashSet <Integer> used = new HashSet <Integer>();
		
		for(int i=0;i<sims.length;i++){
			if(used.contains(i))
				continue;
			
			boolean unique = true;
			SimPro refi = sims[i];
			
			
			for(int j=0;j<sims.length;j++){
				if(i==j)
					continue;
				
				if(used.contains(j))
					continue;
				
				SimPro refj = sims[j];
				int value = refi.comparewith(refj);
				
				switch (value) {
				case 0:
					refi.add(refj.ref);
					used.add(j);
					break;// equals
				case 1:
					break;// include
				case -1:
//					System.out.println(refi+"\t"+refj);
//					System.out.println(refj);
					unique = false;
					break;// small
				}
			}
			
//			System.out.println(refi.ref+"\t"+refi.size());
			Protein p = new Protein(refi, accesser, groupIdx);
			p.setUnique(unique);
			prolist.add(p);
		}

		int len = prolist.size();
		Protein[] pros = prolist.toArray(new Protein[len]);
		for (int i = 0; i < len; i++) {
			pros[i].setCrossProteinCount(len);
		}
		return pros;
	}

	
	/**
	 * Iterative until the ambiguous list contains no proteins
	 * 
	 * @param ambiguatePros 
	 * @param otherpepset 
	 * @param fproteins
	 */
	private void iterateAmbiguousList(HashSet<Protein> ambiguatePros,
	        HashSet<UniPep> otherpepset, LinkedList<Protein> fproteins) {
		if (ambiguatePros.size() > 0) {
			Protein[] aproteins = ambiguatePros.toArray(new Protein[0]);
			Protein maxp = null;
			float maxprob = 0f;
			for (Protein pro : aproteins) {

				UniPep[] upeps = pro.getAllUniPeps();
				float otherprob = 1f;
				for (UniPep upep : upeps) {
					if (otherpepset.contains(upep))
						continue;
					otherprob *= (1f - upep.getProbability());
				}

				float prb = 1f - otherprob;
				pro.setProbability(prb);
				if (prb > maxprob) {
					maxprob = prb;
					maxp = pro;
				}

				if (pro.getReferences()[0].getName().startsWith(
				        "IPI:IPI00384369.2")) {
					System.out.println("ddddd\\\\\\\\\\\\\\\\\\\\\\\\\\" + prb);
					System.out.println(pro.toUniPepString());
				}
			}
			ambiguatePros.remove(maxp);// remove the protein with max
										// probability

			if (maxp.getReferences()[0].getName().startsWith(
			        "IPI:IPI00384369.2")) {
				System.out.println("ggggg\\\\\\\\\\\\\\\\\\\\\\\\\\" + maxprob);
				System.out.println(maxp.toUniPepString());
			}

			if (maxprob >= MinConfidence) {
				if (maxp.getReferences()[0].getName().startsWith(
				        "IPI:IPI00384369.2")) {
					System.out.println("hhhhh\\\\\\\\\\\\\\\\\\\\\\\\\\"
					        + maxprob);
					System.out.println(maxp.toUniPepString());
				}
				fproteins.add(maxp);
				UniPep[] upeps = maxp.getAllUniPeps();
				for (UniPep upep : upeps) {
					otherpepset.add(upep);
				}

				iterateAmbiguousList(ambiguatePros, otherpepset, fproteins);
			} else {
				if (fproteins.size() == 0) {// Add the ambiguous protein as a
											// single protein
					for (Iterator<Protein> iterator = ambiguatePros.iterator(); iterator
					        .hasNext();) {
						maxp.addAlleneProtein(iterator.next());
						iterator.remove();
					}

					fproteins.add(maxp);
				}
			}
		}
		return;
	}

	/**
	 * Return the protein instance included in this protein group; While peptide
	 * may be filtered with criteria before, thus, null may be returned when
	 * there was no peptide in this protein group after filter;
	 * 
	 * Additionally to getProtein(); Proteins generated from this method are
	 * with protein probabilities calculated from peptides for their
	 * identification by specific probcalculator.
	 * 
	 * @return protein (null);
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws NoPeptideProbabilityException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	public Protein[] getProtein(IProteinProbCalculator probcalor)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, NoPeptideProbabilityException,
	        FastaDataBaseException {

		Protein[] proteins = this.getProtein();
		if (proteins == null)
			return null;

		int len = proteins.length;
		if (len > 1) {
			LinkedList<Protein> fproteins = new LinkedList<Protein>();
			HashSet<Protein> ambiguatePros = new HashSet<Protein>();
			// The unambiguate protein contained peptides
			HashSet<UniPep> otherpepset = new HashSet<UniPep>();
			boolean tps = false;
			for (int i = 0; i < len; i++) {
				Protein p = proteins[i];
				/*
				if (p.getReferences()[0].getName().startsWith(
				        "IPI:IPI00384369.2")) {
					System.out.println("11111111111\\\\\\\\\\\\\\\\\\\\\\\\\\"
					        + proteins.length);
					System.out.println(p);
					tps = true;
					System.out.println("22222222222222222222");
				}
				*/
				if (this.checkExist(p)) {
					fproteins.add(p);
					UniPep[] upeps = p.getAllUniPeps();
					for (UniPep upep : upeps) {
						otherpepset.add(upep);
					}
				} else {
					ambiguatePros.add(p);
				}
			}

			if (ambiguatePros.size() > 0) {
				/*
				if (tps) {
					System.out.println("221\\\\\\\\\\\\\\\\\\\\\\\\\\"
					        + fproteins.size());
				}
				*/
				this
				        .iterateAmbiguousList(ambiguatePros, otherpepset,
				                fproteins);
			}

			// reset the proteins for unique peptide
			for (int i = 0, n = proteins.length; i < n; i++) {
				Protein tp = proteins[i];
				UniPep[] tupeps = tp.getAllUniPeps();
				for (int j = 0; j < tupeps.length; j++)
					tupeps[j].clearProteins();
			}

			for (Iterator<Protein> iter = fproteins.iterator(); iter.hasNext();) {
				Protein pro = iter.next();
				UniPep[] tupeps = pro.getAllUniPeps();
				for (int i = 0; i < tupeps.length; i++)
					tupeps[i].addProtein(pro);
			}

			proteins = fproteins.toArray(new Protein[fproteins.size()]);
			if (tps) {
				for (Protein pp : proteins)
					System.out.println(pp.toUniPepString());
			}
		}

		/*
		 * There are proteins with distinct probability less than specific, so
		 * that these proteins would not be declared existing without doubt.
		 */

		len = proteins.length;
		if (len == 1) {
			Protein pro = proteins[0];
			pro.setCrossProteinCount(1);
			float prob = probcalor.getProbability(pro);
			if (pro.getAlleneProteinList() == null)
				pro.setProbability(prob);
			else
				pro.setGroupProb(prob);
		} else {
			float[] probs = probcalor.getProbability(proteins);
			for (int i = 0; i < len; i++) {
				Protein p = proteins[i];
				if (p.getAlleneProteinList() != null) {
					p.setGroupProb(probs[i]);
				} else {
					p.setProbability(probs[i]);
				}
			}
		}

		for (int i = 0; i < len; i++)
			proteins[i].setCrossProteinCount(len);

		return proteins;
	}

	/**
	 * Return the protein instance included in this protein group; While peptide
	 * may be filtered with criteria before, thus, null may be returned when
	 * there was no peptide in this protein group after filter;
	 * 
	 * Additionally to getProtein(); Proteins generated from this method are
	 * with protein probabilities calculated from peptides for their
	 * identification by specific probcalculator.
	 * 
	 * @return protein (null);
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws NoPeptideProbabilityException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	public Protein[] getProtein1(IProteinProbCalculator probcalor)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, NoPeptideProbabilityException,
	        FastaDataBaseException {

		Protein[] proteins = this.getProtein();
		if (proteins == null)
			return null;

		int len = proteins.length;
		if (len > 1) {
			LinkedList<Protein> fproteins = new LinkedList<Protein>();
			LinkedList<Protein> ambiguatePros = new LinkedList<Protein>();
			for (int i = 0; i < len; i++) {
				Protein p = proteins[i];
				if (this.checkExist(p))
					fproteins.add(p);
				else
					ambiguatePros.add(p);
			}

			// reset the proteins for unique peptide
			for (int i = 0, n = proteins.length; i < n; i++) {
				Protein tp = proteins[i];
				UniPep[] tupeps = tp.getAllUniPeps();
				for (int j = 0; j < tupeps.length; j++)
					tupeps[j].clearProteins();
			}

			for (Iterator<Protein> iter = fproteins.iterator(); iter.hasNext();) {
				Protein pro = iter.next();
				UniPep[] tupeps = pro.getAllUniPeps();
				for (int i = 0; i < tupeps.length; i++)
					tupeps[i].addProtein(pro);
			}

			/*
			 * There are proteins with distinct probability less than specific,
			 * so that these proteins would not be declared existing without
			 * doubt.
			 */
			if (ambiguatePros.size() != 0) {
				int sz = ambiguatePros.size();
				Iterator<Protein> iterator = ambiguatePros.iterator();
				Protein[] pros = new Protein[sz];
				for (int i = 0; iterator.hasNext(); i++) {
					Protein tpro = iterator.next();
					tpro.setProbability(probcalor.getProbability(tpro));
					pros[i] = tpro;
				}

				if (sz > 1) {
					Arrays.sort(pros, new Comparator<Protein>() {
						public int compare(Protein o1, Protein o2) {
							float f1 = o1.getProbability();
							float f2 = o2.getProbability();

							if (f1 == f2) {
								int p1p = o1.getPeptideCount();
								int p2p = o2.getPeptideCount();

								if (p1p == p2p) {
									int p1s = o1.getSpectrumCount();
									int p2s = o2.getSpectrumCount();
									if (p1s == p2s)
										return 0;

									return p1s > p2s ? -1 : 1;
								}

								return p1p > p2p ? -1 : 1;
							}

							return f1 < f2 ? 1 : -1;
						}
					});

					Protein ts = pros[0];
					for (int i = 1; i < sz; i++)
						ts.addAlleneProtein(pros[i]);
				}

				/*
				 * If there have been more than one proteins with distinct
				 * peptides therefore these proteins needn't to be exist in the
				 * final list as we can't actually know that these proteins
				 * exists, and a minimum subset is listed in the final protein
				 * list
				 */
				boolean exist = false;
				Protein ambigPro = pros[0];
				UniPep[] upeps = ambigPro.getGroupedUniPeps();

				if (fproteins.size() == 0)
					exist = true;

				// -----------------------------------------------------------
				// Need to be checking, whether peptides belong to different
				// protein but to the
				// same protein group can be used together for probability
				// evalution for protein group?
				// ------------------------------------------------------------

				else {
					/*
					 * The ambigulate protein is rechecked for existance. all
					 * the UniPeps in this protein group (no matter which
					 * protein it belongs to) will be considered as distinct
					 * peptides for this protein group (ambiguate protein) and
					 * used for checking of existance for this protein group.
					 */
					float existProb = 0f;
					for (int j = 0, n = upeps.length; j < n; j++) {
						UniPep upep = upeps[j];
						if (upep.getCountOfSharedPro() == 0)
							existProb = 1f - (1f - existProb)
							        * (1f - upep.getProbability());

						if (existProb >= MinConfidence) {
							exist = true;
							break;
						}
					}
				}

				if (exist) {
					fproteins.add(ambigPro);
					for (int i = 0; i < upeps.length; i++)
						upeps[i].addProtein(ambigPro);
				}
			}

			proteins = fproteins.toArray(new Protein[fproteins.size()]);
		}

		len = proteins.length;
		if (len == 1) {
			Protein pro = proteins[0];
			pro.setCrossProteinCount(1);
			float prob = probcalor.getProbability(pro);
			if (pro.getAlleneProteinList() == null)
				pro.setProbability(prob);
			else
				pro.setGroupProb(prob);
		} else {
			float[] probs = probcalor.getProbability(proteins);
			for (int i = 0; i < len; i++) {
				Protein p = proteins[i];
				if (p.getAlleneProteinList() != null) {
					p.setGroupProb(probs[i]);
				} else {
					p.setProbability(probs[i]);
				}
			}
		}

		for (int i = 0; i < len; i++)
			proteins[i].setCrossProteinCount(len);

		return proteins;
	}

	private static final float MinConfidence = 0.95f;

	/*
	 * Check whether the conclusion that this protein exists can be made.
	 * 
	 * Since for probability based protein identification, all peptides will be
	 * used for protein identification regardless how low their probability is.
	 * Therefore, if a crossed protein in a proteingroup does not containing a
	 * unique (but no shared)peptide with high enough probability, it can be
	 * hard to make the decision that this protein exist.
	 * 
	 * Currently, only if the combined probability of all unique peptides for
	 * this protein identification bigger than 0.95 its existance can be
	 * concluded.
	 * 
	 * @param upeps UniPep in the protein for which the existance to be checked.
	 */
	private boolean checkExist(Protein p) {
		UniPep[] upeps = p.getAllUniPeps();
		float existProb = 0f;
		boolean exist = false;
		for (int j = 0, n = upeps.length; j < n; j++) {
			UniPep upep = upeps[j];
			if (upep.getCountOfSharedPro() == 1) {
				existProb = 1f - (1f - existProb)
				        * (1f - upep.getProbability());
			}

			if (existProb >= MinConfidence) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	/*
	 * Get simPro for protein construction from the refmap
	 */
	private SimPro getSimPro(Entry<String, HashSet<UniPep>> entry) {
		String ref = entry.getKey();
		HashSet<UniPep> set = entry.getValue();
		HashSet<UniPep> set1 = new HashSet<UniPep>(set.size());
		for (Iterator<UniPep> iterator = set.iterator(); iterator.hasNext();) {
			UniPep tu = iterator.next();
			for (Iterator<IPeptide> iterator1 = tu.getPeptideIterator(); iterator1
			        .hasNext();) {
				// at least one peptide is used
				if (iterator1.next().isUsed()) {
					// when new proteins are constructed, old proteins need to
					// be removed.
					tu.clearProteins();
					set1.add(tu);
					break;
				}
			}
		}
		if (set1.size() == 0)
			return null;

		return new SimPro(ref, set1);
	}

}
