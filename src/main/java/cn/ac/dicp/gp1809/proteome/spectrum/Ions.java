/* 
 ******************************************************************************
 * File: Ions.java * * * Created on 05-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * The ions after the theoretical fragment
 * 
 * @author Xinning
 * @version 0.2, 05-30-2009, 14:20:44
 */
public class Ions {

	private IModifiedPeptideSequence mseq = null;
	//Mh+
	private double mh;

	private HashMap<Integer, Ion[]> ionsMap;

	private IntArrayList typelist;

	/**
	 * A null ions
	 * 
	 * @param sequence
	 * @param mh
	 */
	public Ions(IModifiedPeptideSequence mseq, double mh) {
		this.mseq = mseq;
		this.mh = mh;

		this.ionsMap = new HashMap<Integer, Ion[]>();
		this.typelist = new IntArrayList();
	}

	/**
	 * Add ions of the specific type
	 * 
	 * @param type
	 * @param ions
	 */
	public void add(int type, Ion[] ions) {
		if (this.ionsMap.get(type) != null) {
			System.err
			        .println("The ions of type: "
			                + type
			                + " have been added twice, the first one will be overrided.");
		}

		this.ionsMap.put(type, ions);
		this.typelist.add(type);
	}

	/**
	 * @return b type ions (containing b neutral lost ions b*) with charge of 1;
	 *         The b series ions are arranged from 1 to n. For b*, they are from
	 *         bp to n;
	 */
	public Ion[] bIons() {
		return this.getIons(Ion.TYPE_B);
	}

	/**
	 * @return y type ions (containing y neutral lost ions y*) with charge of 1;
	 *         The y series ions are arranged from 1 to n. For y*, they are from
	 *         yp to n;
	 */
	public Ion[] yIons() {
		return this.getIons(Ion.TYPE_Y);
	}

	/**
	 * Get the ions of the specific type, if there is no ion with this type,
	 * return null.
	 * 
	 * @param type
	 * @return
	 */
	public Ion[] getIons(int type) {
		return this.ionsMap.get(type);
	}

	/**
	 * @return MH+ value of this sequence;
	 */
	public double getMH() {
		return this.mh;
	}

	/**
	 * All of the ions of different types
	 * 
	 * @return
	 */
	public Ion[] getTotalIons() {

		if (this.ionsMap.size() == 0)
			return null;

		LinkedList<Ion> list = new LinkedList<Ion>();

		Collection<Ion[]> collection = this.ionsMap.values();

		for (Iterator<Ion[]> it = collection.iterator(); it.hasNext();) {
			Ion[] ions = it.next();
			if (ions != null) {
				for (Ion ion : ions) {
					list.add(ion);
				}
			}
		}

		return list.size() == 0 ? null : list.toArray(new Ion[list.size()]);
	}
	
	public Ion[] getSiteIons(int site) {

		if (this.ionsMap.size() == 0)
			return null;

		LinkedList<Ion> list = new LinkedList<Ion>();

		Iterator <Integer> it = this.ionsMap.keySet().iterator();
		while(it.hasNext()){
			Integer type = it.next();
			Ion [] ions = ionsMap.get(type);
			
			switch (type){
				case Ion.TYPE_B: {
					if(ions==null)
						break;
					
					for(int i=0;i<ions.length;i++){
						int series = ions[i].getSeries();
						if(series >= site)
							list.add(ions[i]);
					}
					break;
				}
				case Ion.TYPE_Y: {
					if(ions==null)
						break;
					
					int right = ions.length-site+1;
					for(int i=0;i<ions.length;i++){
						int series = ions[i].getSeries();
						if(series >= right)
							list.add(ions[i]);
					}
					break;
				}
				case Ion.TYPE_C: {
					if(ions==null)
						break;
					
					for(int i=0;i<ions.length;i++){
						int series = ions[i].getSeries();
						if(series >= site)
							list.add(ions[i]);
					}
					break;
				}
				case Ion.TYPE_Z: {
					if(ions==null)
						break;
					
					int right = ions.length-site+1;
					for(int i=0;i<ions.length;i++){
						int series = ions[i].getSeries();
						if(series >= right)
							list.add(ions[i]);
					}
					break;
				}
				default : {
					
				}
			}
		}

		return list.size() == 0 ? null : list.toArray(new Ion[list.size()]);
	}

	/**
	 * The modified sequence of this peptide sequence
	 * 
	 * @return
	 */
	public IModifiedPeptideSequence getModifiedSequence() {
		return this.mseq;
	}

	/**
	 * @return sequence for the ions series, (with no terminal, but with
	 *         modification symbols e.g. #)
	 */
	public String sequence() {
		return this.mseq.getFormattedSequence();
	}

	/**
	 * Get the types of ions in this Ions instance. If no ions, return null
	 * 
	 * @return
	 */
	public int[] getTypes() {
		int size = this.typelist.size();
		if (size == 0)
			return null;

		return this.typelist.toArray();
	}
}
