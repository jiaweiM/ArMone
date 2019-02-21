/* 
 ******************************************************************************
 * File: SimPro.java * * * Created on 2011-8-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * Contains all the information for the construction of protein.
 * 
 * At first this is an inner class in ProteinGroup.
 * 
 * @author ck
 * @version 0.1, 12-27-2007, 14:02:38
 * @version 2011-8-10, 09:30:05
 */
public class SimPro {

	String ref;
	HashSet<UniPep> set;
	int size = 1;

	/*
	 * Allele reference list; Not including the original ref;
	 */
	private LinkedList<String> reflist = null;

	SimPro(String ref, HashSet<UniPep> set) {
		this.ref = ref;
		this.set = set;
	}

	/**
	 * @return Peptide index set containing all peptides and index
	 *         informations;
	 */
	HashSet<UniPep> getUniPeps() {
		return set;
	}

	/**
	 * @return the number of references containing in this SimRef;
	 */
	int size() {
		return this.size;
	}

	/**
	 * @return the first reference; If reference number is bigger than one,
	 *         other references can get via getAlleneRefList(); Otherwise,
	 *         getAlleneRefList() will return null; Commonly, size() of the
	 *         reference should be get first, and these two method excuted
	 *         accordingly
	 */
	String getReference() {
		return this.ref;
	}

	/**
	 * @return If reference number is bigger than one, other references can
	 *         get via this method; Evidently, the first reference must also
	 *         be get from getReference() method; Otherwise,
	 *         getAlleneRefList() will return null; Commonly, size() of the
	 *         reference should be get first, and these two method excute
	 *         accordingly
	 */
	LinkedList<String> getAlleneRefList() {
		return this.reflist;
	}

	/**
	 * Add a allele reference to this simRef;
	 * 
	 * @param allele
	 */
	void add(String allele) {
		if (this.reflist == null)
			reflist = new LinkedList<String>();

		reflist.add(allele);
		this.size++;
	}

	/**
	 * Compare current ref with another reference;
	 * 
	 * @param ref2
	 * @return equal return 0, bigger return 1, small return -1 and cross
	 *         return 2; (include another)
	 */
	int comparewith(SimPro ref2) {
		HashSet<UniPep> s2 = ref2.set;
		int size1 = set.size();
		int size2 = s2.size();

		if (size1 == size2) {
			if (set.containsAll(s2))
				return 0;// equal
		} else {
			if (size1 < size2) {
				if (s2.containsAll(set))
					return -1;// this protein is small than that one
			} else {
				if (set.containsAll(s2))
					return 1;// bigger
			}
		}
		return 2;// cross
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(ref).append("\t");
		Iterator <UniPep> it = set.iterator();
		while(it.hasNext()){
			UniPep up =it.next();
			sb.append(up.getKeyString()).append("\t").append(up.getPeptideCount()).append("\t");
		}
		return sb.toString();
	}

}
