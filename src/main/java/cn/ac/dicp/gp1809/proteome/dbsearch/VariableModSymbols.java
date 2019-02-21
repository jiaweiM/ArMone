/* 
 ******************************************************************************
 * File: VariableModSymbols.java * * * Created on 10-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.IPeptideSequence;

/**
 * For some database search algorithm, the variable modifications may be
 * displayed in mass format, but not the symbol format as SEQUEST (e.g.
 * AAAAA#AA). This class will automatically assign symbols for these modified
 * masses. And the assigned symbols are with the same order as SEQUEST, that is,
 * {'*', '#', '@', '^', '~', '$'}
 * 
 * @author Xinning
 * @version 0.1.1.1, 02-20-2009, 10:59:41
 */
public class VariableModSymbols implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Variable modification symbols: '*', '#', '@', '^', '~', '$'
	 */
	public static final char[] symbol = new char[] { '*', '#', '@', '^', '~',
	        '$', '[', ']' };

	private HashMap<Double, Character> massmap;

	private HashMap<Character, IModification> symbolmap;

	private boolean mono_precursor;

	public VariableModSymbols(){
		this.massmap = new HashMap<Double, Character>();
		this.symbolmap = new HashMap<Character, IModification>();
	}
	
	/**
	 * Create the variable modification symbol assigner.
	 * 
	 * @param variable_mods
	 *            all potential variable modification.
	 * @param mono_precursor
	 */
	public VariableModSymbols(IModification[] variable_mods,
	        boolean mono_precursor) {
		variable_mods = variable_mods.clone();
		
		//sort by order
		orderSort(variable_mods, mono_precursor);
		
		
		this.massmap = new HashMap<Double, Character>();
		this.symbolmap = new HashMap<Character, IModification>();
		this.mono_precursor = mono_precursor;

		for (IModification mod : variable_mods) {
			double mass;
			if (mono_precursor) {
				mass = mod.getAddedMonoMass();
			} else
				mass = mod.getAddedAvgMass();

			if (massmap.get(mass) == null) {
				int assigned = massmap.size();
				if (assigned >= symbol.length) {
					throw new IllegalArgumentException("Currently, only "
					        + symbol.length
					        + " variable modification is allowed.");
				}

				massmap.put(mass, symbol[assigned]);
				symbolmap.put(symbol[assigned], mod);
			}
		}
	}

	/**
	 * Sort by the added mass from small to big
	 * 
	 * <p>
	 * In order to make the same modification is with the same symbol for all
	 * search algorithms and modification assignment order, the modifications
	 * should be sorted. The symbol at a specific position in the modification
	 * array will keep the same:
	 * <li>mods[0] = '*'
	 * <li>mods[1] = '#'
	 * <li>...
	 * <p>regardless the modification mass and the sites.
	 * 
	 * @param mods
	 */
	private static void orderSort(IModification[] mods, final boolean mono) {
		
		Arrays.sort(mods, new Comparator<IModification>(){

			@Override
            public int compare(IModification o1, IModification o2) {
				double m1, m2;
				if(mono){
					m1 = o1.getAddedMonoMass();
					m2 = o2.getAddedMonoMass();
				}
				else{
					m1 = o1.getAddedAvgMass();
					m2 = o2.getAddedAvgMass();
				}
				
				if(m1 < m2)
					return -1;
				
	            return m1 == m2 ? 0 : 1;
            }
			
		});
		
		
	}

	/**
	 * Test whether the specified modification is a variable modification
	 * contained in the predefined modifications
	 * 
	 * @param mod
	 * @return
	 */
	public boolean isVariableMod(IModification mod) {
		double mass = this.mono_precursor ? mod.getAddedMonoMass() : mod
		        .getAddedAvgMass();

		return this.massmap.get(mass) != null;
	}

	/**
	 * Get the symbol used to indicate this modification
	 * 
	 * @param mod
	 * @return
	 */
	public char getModSymbol(IModification mod) {
		double mass = this.mono_precursor ? mod.getAddedMonoMass() : mod
		        .getAddedAvgMass();
		Character ch = this.massmap.get(mass);
		if (ch == null)
			throw new NullPointerException(
			        "Cannot find the symbol for modified mass: " + mass
			                + ". May no be defined at the configuration.");
		return ch;
	}

	/**
	 * Get the added mass for the symbol. Whether this mass is the mono isotope
	 * mass or the average mass is determined by the precursor mass type (mono
	 * or average).
	 * 
	 * @param symbol
	 * @return
	 */
	public double getAddedMass(char symbol) {
		IModification mod = this.symbolmap.get(symbol);
		return this.mono_precursor ? mod.getAddedMonoMass() : mod
		        .getAddedAvgMass();
	}

	/**
	 * Parse the sequence into formatted peptide sequence.
	 * 
	 * A.AAAAA#AAAA.A
	 * 
	 * @param raw
	 *            the raw PeptideSequence
	 * @param modifs
	 *            the modifications on this peptide
	 * @param modif_at
	 *            the modified aminoacid indexes according to the modifs. These
	 *            are 1- based indexes. For example, PEIMSDKL, if M is modified,
	 *            the index is 4
	 * @return
	 */
	public String parseSequence(IPeptideSequence raw, IModification[] modifs,
	        int[] modif_at) {

		if (modifs == null || modifs.length == 0)
			return raw.getFormattedSequence();

		int len = raw.length();
		String seq = raw.getUniqueSequence();
		StringBuilder sb = new StringBuilder(len + 10);// addition 6 modif symbol

		int dlen = modifs.length;

		if (modif_at == null || modif_at.length != dlen)
			throw new IllegalArgumentException(
			        "The modifications and the indexes of modified aminoacids"
			                + " should be corresponding to each other.");

		HashSet<ParsedModif> modifset = new HashSet<ParsedModif>();

		for (int i = 0; i < dlen; i++) {
			int index = modif_at[i];
			IModification mod = modifs[i];

			/*
			 * Only the pre defined variable modifications are used. Other
			 * modifications will be automatically ignored.
			 */
			if (this.isVariableMod(mod)) {
				char sym = this.getModSymbol(mod);
				modifset.add(new ParsedModif(index, sym));
			} else {
				System.out.println("Added mass: " + mod.getAddedAvgMass()
				        + " is not a pre- defined variable modification, "
				        + "and will be ignored.");
			}

		}

		ParsedModif[] pmodifs = modifset.toArray(new ParsedModif[modifset
		        .size()]);

		// From small to big
		if (pmodifs.length > 1)
			Arrays.sort(pmodifs);

		sb.append(raw.getPreviousAA()).append('.');
		int pre = 0;
		for (ParsedModif pmodif : pmodifs) {
			int idx = pmodif.getIndex();
			
			if(idx==0){
				sb.append(pmodif.symbol());
			}else{
				if(idx>seq.length()){
					sb.append(seq.substring(pre));
					sb.append(pmodif.symbol());
					pre = seq.length();
				}
				else{
					sb.append(seq.substring(pre, idx));
					sb.append(pmodif.symbol());
					pre = idx;
				}
			}
		}

		sb.append(seq.substring(pre)).append('.').append(raw.getNextAA());
		return sb.toString();
	}
	
	
	/**
	 * Parse the sequence into formatted peptide sequence.
	 * 
	 * A.AAAAA#AAAA.A
	 * 
	 * @param raw
	 *            the raw PeptideSequence
	 * @param modifs
	 *            the modifications on this peptide
	 * @param modif_at
	 *            the modified aminoacid indexes according to the modifs. These
	 *            are 1- based indexes. For example, PEIMSDKL, if M is modified,
	 *            the index is 4
	 * @return
	 */
	public String parseSequence(IModifiedPeptideSequence raw, IModification[] modifs) {
		
		//No designed.
		
		return null;
	}
	

	/**
	 * The parsed modification
	 * 
	 * @author Xinning
	 * @version 0.1, 09-04-2008, 22:24:34
	 */
	private static class ParsedModif implements Comparable<ParsedModif> {
		private int idx;
		private char symbol;

		ParsedModif(int idx, char symbol) {
			this.idx = idx;
			this.symbol = symbol;
		}

		/**
		 * The index where modification occurred
		 * 
		 * @return
		 */
		public int getIndex() {
			return this.idx;
		}

		/**
		 * Symbol of modification;
		 * 
		 * @return
		 */
		public char symbol() {
			return this.symbol;
		}

		@Override
		public int hashCode() {
			return this.idx;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ParsedModif) {
				if (((ParsedModif) obj).symbol == this.symbol)
					return true;
			}

			return false;

		}

		@Override
		public int compareTo(ParsedModif o) {

			if (idx > o.idx)
				return 1;

			return idx < o.idx ? -1 : 0;
		}
	}

}
