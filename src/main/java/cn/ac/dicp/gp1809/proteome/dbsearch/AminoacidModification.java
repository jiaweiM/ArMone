/*
 * *****************************************************************************
 * File: AminoacidModification.java * * * Created on 03-05-2008 
 * Copyright (c) 2008 Xinning Jiang vext@163.com 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Variable modification (with assigned character (#@* etc));
 * 
 * <p>
 * Changes:
 * <li>0.5.2, 03-04-2009: Add {@link #getModficationDescription()}
 * <li>0.5.3, 06-13-2009: add
 * {@link #getSymbolForMassWithTolerance(double, double)}
 * 
 * @author Xinning
 * @version 0.5.3, 06-13-2009, 14:53:29
 */
public class AminoacidModification implements Serializable, IDeepCloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The platform dependent line separator. For Windows, this value is "\r\n"
	 * while for linux this value will be "\n".
	 */
	private final static String lineSeparator = IOConstant.lineSeparator;

	private final static DecimalFormat DA;

	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);

		DA = new DecimalFormat("+0.#### Da;-0.#### Da");

		Locale.setDefault(def);
	}

	/**
	 * Key = site which was modified, Value = modifications
	 */
	private HashMap<ModSite, HashSet<Modif>> siteMap;

	/**
	 * Key = symbol, value = HashSet<ModSite>
	 */
	private HashMap<Character, HashSet<ModSite>> modmap;

	/**
	 * Value = symbol Key = added mass
	 */
	private HashMap<Character, Modif> symbolMap;

	/**
	 * Here, one mass can only with one symbol and one symbol can only indicate
	 * one mass.
	 * 
	 * Key = added mass Value = symbol
	 */
	private HashMap<Double, Modif> massMap;

	public AminoacidModification() {
		this.siteMap = new HashMap<ModSite, HashSet<Modif>>();
		this.symbolMap = new HashMap<Character, Modif>();
		this.massMap = new HashMap<Double, Modif>();
		this.modmap = new HashMap<Character, HashSet<ModSite>>();
	}

	/**
	 * Set the modification for specific aminoacid with specific modification
	 * symbol.
	 * 
	 * <p>
	 * <b>Note: one mass can only with one symbol and one symbol can only
	 * indicate one mass. Otherwise, IllegalArgumentException will be threw</b>
	 * 
	 * @param aminoacid
	 *            the aminoacid on which the modification occurs (if n terminal
	 *            the char is 'n', if c terminal the char is 'c')
	 * @param modifsymbol
	 *            the symbol indicating the variable modification (#@ and so on)
	 * @param addmass
	 *            the mass added when the modification occurred.
	 * @throws NullPointerException
	 *             if the specific character is not an aminoacid or 'n' and 'c'.
	 */
	public void addModification(ModSite site, char modifsymbol, double addmass, String name) {

		validateSymbol(modifsymbol);
		Modif modif = this.symbolMap.get(modifsymbol);

		if (modif != null) {
			if (modif.getMass() != addmass) {
				throw new IllegalArgumentException("The symbol " + modifsymbol
				        + " has been assigned to another modification: "
				        + modif.getMass());
			}
			//else do nothing
		} else {

			/*
			 * Test the mass map
			 */
			modif = this.massMap.get(addmass);

			if (modif != null) {
				if (modif.getSymbol() != modifsymbol) {
					throw new IllegalArgumentException("The added mass "
					        + addmass
					        + " has been assigned to another modification: "
					        + modif.getSymbol());
				}
				//else do nothing
			} else {
				modif = new Modif(modifsymbol, addmass, name);
				this.symbolMap.put(modifsymbol, modif);
				this.massMap.put(addmass, modif);
			}
		}

		/*
		 * Fill into site map
		 */
		if (site != null) {
			HashSet<Modif> modifset;
			if ((modifset = this.siteMap.get(site)) == null) {
				modifset = new HashSet<Modif>();
				this.siteMap.put(site, modifset);
			}
			modifset.add(modif);

			HashSet<ModSite> set = this.modmap.get(modifsymbol);
			if (set == null) {
				set = new HashSet<ModSite>();
				this.modmap.put(modifsymbol, set);
			}

			set.add(site);
		}
	}

	/**
	 * Set the modification for specific aminoacid with specific modification
	 * symbol.
	 * 
	 * <p>
	 * <b>Note: one mass can only with one symbol and one symbol can only
	 * indicate one mass. Otherwise, IllegalArgumentException will be threw</b>
	 * 
	 * @param aminoacid
	 *            the aminoacid on which the modification occurs (if n terminal
	 *            the char is 'n', if c terminal the char is 'c')
	 * @param modifsymbol
	 *            the symbol indicating the variable modification (#@ and so on)
	 * @param addmass
	 *            the mass added when the modification occurred.
	 * @throws NullPointerException
	 *             if the specific character is not an aminoacid or 'n' and 'c'.
	 */
	public void addModifications(ModSite[] sites, char modifsymbol,
	        double addmass, String name) {
		for (ModSite site : sites) {
			this.addModification(site, modifsymbol, addmass, name);
		}
	}

	/**
	 * Add the modification with specific modification symbol.
	 * 
	 * @param modifsymbol
	 *            symbol for the modification (#@ and so on)
	 * @param addmass
	 *            varible for the modif can below zero;
	 * @throws NullPointerException
	 */
	public void addModification(char modifsymbol, double addmass, String name) {
		this.addModification(null, modifsymbol, addmass, name);
	}

	/**
	 * Get the probably variable modifications for the specific aminoacid. <b>If
	 * this aminoacid may have more than one variable modification, then the
	 * returned String will contain two chars, e.g. #@. The added mass for the
	 * modification can then be generated by getValueForModif(char symbol).</b>
	 * 
	 * @see #getModificationSymbol(ModSite);
	 * 
	 * @param aminoacid
	 *            (can NOT be n or c for the n terminal and c terminal)
	 * @return the modification String. If none, return Null.
	 */
	public HashSet<Modif> getModifSymbols(char aminoacid) {
		return this.getModifSymbols(ModSite.newInstance_aa(aminoacid));
	}

	/**
	 * Get the probability variable modifications for the specific aminoacid.
	 * <b>If this aminoacid may have more than one variable modification, then
	 * the returned String will contain two chars, e.g. #@. The added mass for
	 * the modification can then be generated by getValueForModif(char
	 * symbol).</b>
	 * 
	 * @param site
	 * @return the modification String. If none, return Null.
	 */
	public HashSet<Modif> getModifSymbols(ModSite site) {
		return this.siteMap.get(site);
	}

	/**
	 * Get the sites on which the variable modification of this symbol may be
	 * occurred.
	 * 
	 * @param symbol
	 * @return
	 */
	public HashSet<ModSite> getModifSites(char symbol) {
		return this.modmap.get(symbol);
	}

	/**
	 * @return the ModSite with predefined modifications.
	 */
	public ModSite[] getModifiedSites() {
		HashSet<ModSite> set = new HashSet<ModSite>();
		set.addAll(this.siteMap.keySet());
		return set.toArray(new ModSite[set.size()]);
//		return this.siteMap.keySet().toArray(new ModSite[this.siteMap.size()]);
	}

	/**
	 * Get all the modifications
	 * 
	 * @return
	 */
	public Modif[] getModifications() {
		HashSet<Modif> set = new HashSet<Modif>();
		set.addAll(this.symbolMap.values());
		return set.toArray(new Modif[set.size()]);
	}

	/**
	 * Set the new added mass for the modification with specific symbol
	 * 
	 * @param modifsymbol
	 *            symbol for the modification (#@ and so on)
	 * @param newmass
	 *            varible for the modif can below zero;
	 * @throws NullPointerException
	 */
	public void setMassForModif(char modifsymbol, double newmass) {

		Modif modif = this.symbolMap.get(modifsymbol);
		if (modif == null)
			throw new NullPointerException(
			        "Cann't find the modification to be changed with symbol of "
			                + modifsymbol);

		this.massMap.remove(modif.getMass());
		this.massMap.put(newmass, modif);

		modif.setMass(newmass);

	}

	/**
	 * Get the value (mass to be added) for the modifsymbol;
	 * 
	 * @param modifsymbol
	 * @return
	 */
	public double getAddedMassForModif(char modifsymbol) {
		Modif modif = this.symbolMap.get(modifsymbol);
		if (modif == null)
			throw new NullPointerException(
			        "Cannot find the modification for symbol " + modifsymbol);

		return modif.getMass();
	}

	/**
	 * Get the symbol for the added mass.
	 * 
	 * @param addmass
	 * @return
	 * @throws NullPointerException
	 *             if cannot find the modification with added mass
	 */
	public char getSymbolForMass(double addmass) {
		Modif modif = this.massMap.get(addmass);

		if (modif == null)
			throw new NullPointerException(
			        "Cannot find the modification with mass of " + addmass);

		return modif.getSymbol();
	}

	/**
	 * Get the symbol for the added mass. If the given mass and the mass in the
	 * parameter is within the specified tolerance, the symbol will be
	 * considered as match. If there are more than one symbol within the
	 * tolerance, IllegalArgumentExpecton will be threw. If no modification with
	 * specific addmass, (char)0 will be returned.
	 * 
	 * @param addmass
	 * @param tol
	 *            tol must be bigger than 0
	 * @return the symbol of modification with specific added mass. If no
	 *         modification with added mass, (char)0 will be returned.
	 */
	public char getSymbolForMassWithTolerance(double addmass, double tol) {
		Modif[] symbols = this.getModifications();
		char symbol = (char) 0;
		boolean find = false;
		for (int i = 0; i < symbols.length; i++) {
			Modif mod = symbols[i];
			char sym = mod.getSymbol();
			double add = mod.getMass();

			if (Math.abs(add - addmass) < tol) {
				if (!find) {
					find = true;
					symbol = sym;
				} else {
					throw new IllegalArgumentException(
					        "More than one modification with specific added mass within mass tolerance.");
				}
			}
		}

		return symbol;
	}

	/**
	 * Change the modification symbol
	 * 
	 * @param old
	 *            the original symbol
	 * @param to
	 *            the new symbol
	 * @return
	 */
	public boolean changeModifSymbol(char old, char to) {

		validateSymbol(to);

		Modif modif = this.symbolMap.get(old);
		if (modif == null)
			throw new NullPointerException(
			        "Cann't find the modification to be changed with symbol of "
			                + old);

		Modif tomodif = this.symbolMap.get(to);

		if (tomodif != null) {
			throw new IllegalArgumentException(
			        "The symbol "
			                + to
			                + " for changing has been assigned to another modification.");
		}

		modif.setSymbol(to);

		this.symbolMap.remove(old);
		this.symbolMap.put(to, modif);

		this.modmap.put(to, this.modmap.get(old));
		this.modmap.remove(old);

		return true;
	}

	/**
	 * Get the description of the modified aminoacids with the format of
	 * "# = 156 Da" or "@ = -18 Da"
	 * 
	 * @since 0.5.1
	 * @param isMono
	 *            is the search performed using Monoisotope mass
	 * @return
	 */
	public String getModficationDescription() {
		StringBuilder sb = new StringBuilder(30);
		Modif[] modifs = this.getModifications();
		for (Modif modif : modifs) {
			sb.append(modif.getSymbol());
			sb.append(" = ").append(DA.format(modif.getMass()));
			sb.append(lineSeparator);
		}

		return modifs.length==0 ? "No modification." : sb.toString();
	}

	/**
	 * Whether the symbol is a valid modification symbol. Currently, the valid
	 * symbol should not be 'A' - 'Z'. If illegal symbol, throw
	 * IllegalArgumentExcpeiton
	 * 
	 * @param symbol
	 * @return
	 */
	private static void validateSymbol(char symbol) {
		if (symbol >= 'A' && symbol <= 'Z')
			throw new IllegalArgumentException(
			        "The symbol can not be used to indicate a variable modification : "
			                + symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AminoacidModification clone() {
		try {
			return (AminoacidModification) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deep clone
	 */
	public AminoacidModification deepClone() {
		AminoacidModification modif = new AminoacidModification();

		if (this.siteMap.size() > 0) {
			ModSite[] sites = this.siteMap.keySet().toArray(
			        new ModSite[this.siteMap.size()]);

			for (ModSite site : sites) {
				HashSet<Modif> set = this.siteMap.get(site);
				for (Iterator<Modif> iterator = set.iterator(); iterator
				        .hasNext();) {
					Modif mod = iterator.next();
					modif.addModification(site, mod.getSymbol(), mod.getMass(), mod.getName());
				}
			}
		}

		Modif[] mods = this.getModifications();
		for (Modif mod : mods) {
			modif.addModification(mod.getSymbol(), mod.getMass(), mod.getName());
		}

		return modif;
	}

	/**
	 * The modif
	 * 
	 * @author Xinning
	 * @version 0.1, 02-26-2009, 14:09:50
	 */
	public static class Modif implements Serializable, Comparable<Modif> {
		/**
         * 
         */
		private static final long serialVersionUID = 1L;
		private char symbol;
		private double mass;
		private String name;

		/**
		 * @param symbol
		 * @param mass
		 */
		private Modif(char symbol, double mass, String name) {
			this.symbol = symbol;
			this.mass = mass;
			this.name = name;
		}

		@Override
		public int hashCode() {
			return symbol;
		}

		@Override
		public boolean equals(Object obj) {
			Modif mod = (Modif) obj;
			if (mod.symbol == this.symbol && mod.mass == this.mass)
				return true;
			return false;
		}

		/**
		 * @return the symbol
		 */
		public char getSymbol() {
			return symbol;
		}

		/**
		 * @param symbol
		 *            the symbol to set
		 */
		private void setSymbol(char symbol) {
			this.symbol = symbol;
		}

		/**
		 * @return the mass
		 */
		public double getMass() {
			return mass;
		}

		/**
		 * @param mass
		 *            the mass to set
		 */
		private void setMass(double mass) {
			this.mass = mass;
		}
		
		public void setName(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(name).append("\t");
			sb.append(mass).append("\t");
			sb.append(symbol).append("\t");
			return sb.toString();
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Modif o) {
			// TODO Auto-generated method stub
			Double mass0 = new Double(this.mass);
			Double mass1 = new Double(o.mass);
			return mass0.compareTo(mass1);
		}
		
	}
}
