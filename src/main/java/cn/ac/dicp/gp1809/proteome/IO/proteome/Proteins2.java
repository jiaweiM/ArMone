/* 
 ******************************************************************************
 * File: Proteins2.java * * * Created on 2011-8-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.NoPeptideProbabilityException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.probability.IProteinProbCalculator;

/**
 * A redefined class which has the same function with Proteins but don't need IFastaAccesser to 
 * construct it. 
 * 
 * @author ck
 * @see Proteins
 * @version 2011-8-9, 13:58:29
 */
public class Proteins2 {


	/*
	 * Peptide list;
	 */
	private LinkedList<IPeptide> peptideList;
	/*
	 * ProteinGroup container;
	 */
	private Map<String, ProteinGroup2> proteinmap;

	private UniPeps unipeps;

	/*
	 * The index of ProteinGroup that has been constructed in the Proteins.
	 */
	private int groupIdx;
	
	private ProteinNameAccesser accesser;

	public Proteins2(ProteinNameAccesser pNameAccesser) {
		this.peptideList = new LinkedList<IPeptide>();
		this.proteinmap = new HashMap<String, ProteinGroup2>(100);
		this.unipeps = new UniPeps();
		this.accesser = pNameAccesser;
	}

	/**
	 * Add a new peptide to proteins;
	 * 
	 * @param pep
	 * @return always be true;
	 */
	public boolean addPeptide(IPeptide pep) {
		this.peptideList.add(pep);
		this.addToProteinGroup(pep);
		return true;
	}

	/**
	 * Add peptide to protein group;
	 * 
	 * @param pep
	 */
	private void addToProteinGroup(IPeptide pep) {
		
//		pep.formatReference(accesser);
		UniPep upep = this.unipeps.addPeptide(pep);
//		upep.formatReference(accesser);

		Set<ProteinReference> references = upep.getProteinReferences();
		int n = references.size();
		String[] formatrefs = new String[n];

		int i = 0;
		for (Iterator<ProteinReference> it = references.iterator(); it
		        .hasNext(); i++) {
			String ref = it.next().getName();
			formatrefs[i] = ref;
		}

		ProteinGroup2 group = null;

		for (i = 0; i < n; i++) {
			ProteinGroup2 temp = this.proteinmap.get(formatrefs[i]);
			/*
			 * This reference is not contained in this map;
			 */
			if (temp == null)
				continue;

			if (group == null)
				group = temp;
			else {
				/*
				 * Two protein group cross over with each other; That is, the
				 * two protein groups have specific peptide identifications, and
				 * with same peptide too;
				 */
				if (group != temp) {// not the same protein group
					// merge the two group together;
					group = this.mergeProteinGroup(group, temp);

					/*
					 * Refresh the map with the new protein group instance;
					 */
					for (Iterator<String> iterator = group.getRefIterator(); iterator
					        .hasNext();)
						this.proteinmap.put(iterator.next(), group);
				}

				// else is this same protein group instance
			}
		}

		if (group == null)
			group = new ProteinGroup2(this.accesser, this.groupIdx++);

		group.addPeptide(upep, formatrefs);

		/*
		 * Put the new protein references into map;
		 */
		for (i = 0; i < n; i++) {
			String ref = formatrefs[i];
			if (this.proteinmap.get(ref) == null)
				this.proteinmap.put(ref, group);
		}
	}

	/*
	 * Merge two ProteinGroup instance together; @param group1 @param group2
	 * @return a single proteingroup, contains all information from two
	 * proteingroups before;
	 */
	private ProteinGroup2 mergeProteinGroup(ProteinGroup2 group1,
	        ProteinGroup2 group2) {
		return group1.getRefCount() > group2.getRefCount() ? group1
		        .addAll(group2) : group2.addAll(group1);
	}

	/**
	 * Get final proteins from this Proteins instance;
	 * 
	 * @return grouped proteins
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */
	public Protein[] getProteins() throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		System.out.println("Parsing protein groups ...");
		ArrayList<Protein> list = new ArrayList<Protein>(200);
		Iterator<ProteinGroup2> iterator = this.getProteinGroupIterator();

		int count = 0;
		while (iterator.hasNext()) {
//			System.out.println("Proteins\t"+count);
			ProteinGroup2 temp = iterator.next();
			Protein[] proteins = temp.getProtein();
			if (proteins == null)
				continue;

			for (int i = 0, n = proteins.length; i < n; i++) {
				Protein pro = proteins[i];
				pro.setGroupIndex(count);
				list.add(pro);
			}
			count++;
		}

		return list.toArray(new Protein[list.size()]);
	}

	/**
	 * Get all proteins, concluding the proteins with no distinct peptide.
	 * @return
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws FastaDataBaseException
	 */
	public Protein[] getAllProteins() throws ProteinNotFoundInFastaException,
		MoreThanOneRefFoundInFastaException, FastaDataBaseException {
		System.out.println("Parsing protein groups ...");
		ArrayList<Protein> list = new ArrayList<Protein>(200);
		Iterator<ProteinGroup2> iterator = this.getProteinGroupIterator();

		int count = 0;
		while (iterator.hasNext()) {
			//	System.out.println("Proteins\t"+count);
			ProteinGroup2 temp = iterator.next();
			Protein[] proteins = temp.getAllProtein();
			if (proteins == null)
				continue;

			for (int i = 0, n = proteins.length; i < n; i++) {
				Protein pro = proteins[i];
				pro.setGroupIndex(count);
				list.add(pro);
			}
			count++;
		}

		return list.toArray(new Protein[list.size()]);
	}
	
	/**
	 * Get final proteins from this Proteins instance with the probability
	 * calculated.
	 * 
	 * @return grouped proteins
	 * @throws ProteinNotFoundInFastaException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws NoPeptideProbabilityException
	 * @throws FastaDataBaseException
	 * @throws BioException
	 */

	public Protein[] getProteins(IProteinProbCalculator probcalor)
	        throws ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException, NoPeptideProbabilityException,
	        FastaDataBaseException {

		System.out.println("Parsing protein groups ...");
		ArrayList<Protein> list = new ArrayList<Protein>(200);
		Iterator<ProteinGroup2> iterator = this.getProteinGroupIterator();

		int count = 0;
		while (iterator.hasNext()) {
			ProteinGroup2 temp = iterator.next();
			Protein[] proteins = temp.getProtein(probcalor);
			if (proteins == null)
				continue;

			for (int i = 0, n = proteins.length; i < n; i++) {
				Protein pro = proteins[i];
				pro.setGroupIndex(count);
				list.add(pro);
			}
			count++;
		}

		return list.toArray(new Protein[list.size()]);
	}

	/**
	 * @return the protein group iterator in this proteins
	 */
	public Iterator<ProteinGroup2> getProteinGroupIterator() {
		HashSet<ProteinGroup2> set = new HashSet<ProteinGroup2>();
		set.addAll(this.proteinmap.values());
		return set.iterator();
	}

	/**
	 * Get iterator for all the peptides;
	 */
	public Iterator<IPeptide> getPeptideIterator() {
		return this.peptideList.iterator();
	}

	/**
	 * All the peptides in Proteins
	 * 
	 * @return
	 */
	public IPeptide[] getPeptides(){
		return this.peptideList.toArray(new IPeptide[this.peptideList.size()]);
	}
	
	/**
	 * @return the number of peptides in this proteins,(without filtering)
	 */
	public int getPeptideNumber() {
		return this.peptideList.size();
	}

	/**
	 * @return number of UniPep; <b>Peptides identified with different charge
	 *         will be considered as two different UniPep</b>
	 */
	public int getUniPepNumber() {
		return this.unipeps.getUniPepNumber();
	}

	/**
	 * @return The iterator of all the UniPep in the Proteins.
	 */
	public Iterator<UniPep> getUniPepIterator() {
		return this.unipeps.getUniPepIterator();
	}

	/**
	 * @return The collection of all the UniPeps.
	 */
	public Collection<UniPep> getUniPeps() {
		return this.unipeps.getUniPeps();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
