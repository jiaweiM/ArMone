/* 
 ******************************************************************************
 * File: ModSite.java * * * Created on 11-19-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.lang.IDeepCloneable;

/**
 * The site where modification occurs. This site may be a specific aminoacid,
 * n/c peptide terminus, n/c protein terminus or the specific aminoacid at n/c
 * terminus of peptide or protein
 * 
 * @see IModification.ModType
 * 
 * @author Xinning
 * @version 0.1.1, 06-13-2009, 20:49:07
 */
public class ModSite implements java.io.Serializable, IDeepCloneable{


	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	/**
	 * The valid symbol pattern
	 */
	private static final Pattern pattern = Pattern
	        .compile("[A-Z&&[^JU]]|_[cCnN][A-Z&&[^JU]]?");

	/**
	 * If the input symbol a valid string presenting the modification site.
	 * <p>
	 * If this modification site is a specific aminoacid, the string value of
	 * the one-letter-symbol of this aminoacid will be returned (e.g. "K"). The
	 * terminus of peptide will be presented as "_n" or "_c" for peptide
	 * terminus and "_N" or "_C" for protein terminus. If this site is a
	 * specific aminoaicd at terminus, String like "_cQ" (Q at peptide c
	 * terminus) will be used.
	 * 
	 * @see #getSymbol();
	 * @param symbol
	 * @return
	 */
	public static boolean isValidSiteSymbol(String symbol) {
		if (symbol == null)
			return false;

		return pattern.matcher(symbol).matches();
	}

	/**
	 * Parse the valid site symbol to ModSite instance. If the symbol is
	 * invalid, IllegalArgumentException will be threw.
	 * 
	 * <p>
	 * If this modification site is a specific aminoacid, the string value of
	 * the one-letter-symbol of this aminoacid will be returned (e.g. "K"). The
	 * terminus of peptide will be presented as "_n" or "_c" for peptide
	 * terminus and "_N" or "_C" for protein terminus. If this site is a
	 * specific aminoaicd at terminus, String like "_cQ" (Q at peptide c
	 * terminus) will be used.
	 * 
	 * <p>
	 * If you don't have these formatted string, use the newInstance_XX method
	 * to construct the ModSite
	 * 
	 * @param site
	 * @return
	 */
	public static ModSite parseSite(String symbol)
	        throws IllegalArgumentException {

		if (isValidSiteSymbol(symbol)) {
			int len = symbol.length();
			if (len == 1)
				return newInstance_aa(symbol.charAt(0));

			if (len == 2)
				return new ModSite(symbol.charAt(1), (char) 0);

			if (len == 3)
				return new ModSite(symbol.charAt(1), symbol.charAt(2));

			throw new IllegalArgumentException(symbol + " is illegal.");

		} else
			throw new IllegalArgumentException("The input symbol \"" + symbol
			        + "\" is invalid.");
	}

/*	public static ModSite parseSiteNew(String symbol){
		
		if(symbol.length()==1){
			
		}else if(symbol.length()==2){
			
		}
		
		ModSite site = null;
		int length = symbol.length();
		char aa = symbol.charAt(0);
		if(length==1)
			site = ModSite.newInstance_aa(aa);
		else if(length>1){
			if(aa=='N')
				site = ModSite.newInstance_PepNterm();
			else if(aa=='C')
				site = ModSite.newInstance_PepCterm();
			else
				throw new IllegalArgumentException(symbol + " is illegal.");
		}
		else
			throw new IllegalArgumentException(symbol + " is illegal.");
		
		return site;
	}
*/	
	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_aa(char aa) {
		if (validateAA(aa))
			return new ModSite(aa);
		else
			throw new IllegalArgumentException("The input aminoaicd \"" + aa
			        + "\" is invalid.");
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_PepNterm() {
		ModSite site = new ModSite('n', (char) 0);
		return site;
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_PepNterm_aa(char aa) {
		if (validateAA(aa))
			return new ModSite('n', aa);
		else
			throw new IllegalArgumentException("The input aminoaicd \"" + aa
			        + "\" is invalid.");
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_PepCterm() {
		return new ModSite('c', (char) 0);
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_PepCterm_aa(char aa) {
		if (validateAA(aa))
			return new ModSite('c', aa);
		else
			throw new IllegalArgumentException("The input aminoaicd \"" + aa
			        + "\" is invalid.");
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_ProNterm() {
		ModSite site =  new ModSite('N', (char) 0);
		return site;
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_ProNterm_aa(char aa) {
		if (validateAA(aa))
			return new ModSite('N', aa);
		else
			throw new IllegalArgumentException("The input aminoaicd \"" + aa
			        + "\" is invalid.");
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_ProCterm() {
		return new ModSite('C', (char) 0);
	}

	/**
	 * Create an instance of ModSite at specific aminoaicd
	 * 
	 * @param aa
	 * @return
	 */
	public static ModSite newInstance_ProCterm_aa(char aa) {
		if (validateAA(aa))
			return new ModSite('C', aa);
		else
			throw new IllegalArgumentException("The input aminoaicd \"" + aa
			        + "\" is invalid.");
	}

	public static ModSite newInstance_Not_Defined(){
		return null;
	}
	/**
	 * If this is a valid aminoacid character
	 * 
	 * @param aa
	 * @return
	 */
	private static boolean validateAA(char aa) {
		if (aa <= 'Z' && aa >= 'A')
//		if (aa <= 'Z' && aa >= 'A' && aa != 'J' && aa != 'U')
			return true;
		else
			return false;
	}

	private char aa = 0;
	private char term = 0;
	private String symbol;
	private ModType type;

	/**
	 * 
	 */
	private ModSite(char aminoacid) {
		aa = aminoacid;
		this.type = getType((char) 0, aminoacid);
	}

	private ModSite(char term, char aminoacid) {
		this.term = term;
		this.aa = aminoacid;

		type = getType(term, aminoacid);
	}

	/**
	 * Set the type of modification for this site.
	 * 
	 * @param type
	 */
	private static ModType getType(char term, char aminoacid) {

		if (aminoacid == 0) {
			switch (term) {
			case 'c':
				return ModType.modcp;
			case 'n':
				return ModType.modnp;
			case 'C':
				return ModType.modc;
			case 'N':
				return ModType.modn;
			default:
				throw new IllegalArgumentException(
				        "Unkown expression for ModType: terminus-" + term
				                + ", aminoacid-" + aminoacid);
			}
		} else {
			switch (term) {
			case 0:
				return ModType.modaa;
			case 'c':
				return ModType.modcpaa;
			case 'n':
				return ModType.modnpaa;
			case 'C':
				return ModType.modcaa;
			case 'N':
				return ModType.modnaa;
			default:
				throw new IllegalArgumentException(
				        "Unkown expression for ModType: terminus-" + term
				                + ", aminoacid-" + aminoacid);
			}
		}
	}
	
	/**
	 * Is this modification must be variable modification
	 * 
	 * @return
	 */
	public boolean isMustVaribleMod(){
		return this.type.isMust_var_mod();
	}

	/**
	 * The modification type for this site
	 * 
	 * @return
	 */
	public ModType getModType() {
		return this.type;
	}
	
	/**
	 * The location of the modification
	 */
	
	public String getModifAt(){
		String local = "";
		if(type==ModType.modaa){
			local = String.valueOf(aa);
		}else if(type==ModType.modnp){
			local = "_n";
		}else if(type==ModType.modcp){
			local = "_c";
		}else if(type==ModType.modc){
			local = "_C";
		}else if(type==ModType.modn){
			local = "_N";
		}else if(type==ModType.modnpaa){
			local = "_n"+aa;
		}else if(type==ModType.modcpaa){
			local = "_c"+aa;
		}else if(type==ModType.modcaa){
			local = "_C"+aa;
		}else if(type==ModType.modnaa){
			local = "_N"+aa;
		}
		else{
			System.err.println("ModType mistake!");
		}
		return local;
	}

	/**
	 * The symbol of this modification site. If this modification site is a
	 * specific aminoacid, the string value of the one-letter-symbol of this
	 * aminoacid will be returned (e.g. "K"). The terminus of peptide will be
	 * presented as "_n" or "_c" for peptide terminus and "_N" or "_C" for
	 * protein terminus. If this site is a specific aminoaicd at terminus,
	 * String like "_cQ" (Q at peptide c terminus) will be used.
	 * 
	 * @return the symbol of this site
	 */
	public String getSymbol() {
		if (this.symbol == null) {
			if (this.type == ModType.modaa) {
				return String.valueOf(this.aa);
			} else {
				StringBuilder sb = new StringBuilder(3);
				sb.append('_').append(this.term).append(this.aa);
				return sb.toString();
			}
		}
		return this.symbol;
	}

	@Override
	public int hashCode() {
		return this.getSymbol().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModSite) {
			if (this.getSymbol().equals(((ModSite) obj).getSymbol()))
				return true;
		}

		return false;
	}

	/**
	 * Same as {@link #getSymbol()};
	 * 
	 * @see #getSymbol();
	 */
	@Override
	public String toString() {
		return this.getSymbol();
	}

	/**
	 * The type of modification or where the modification occurs. These types
	 * are translated from OMSSA
	 * 
	 * @author Xinning
	 * @version 0.1, 09-04-2008, 16:10:58
	 */
	public static enum ModType {
		/** ModType_modaa (0, "at particular amino acids"), */
		modaa("Modify at specific amino acids", false),

		/**
		 * ModType_modn (1, "at the N terminus of a protein", must be variable
		 * modification),
		 */
		modn(
		        "Modify at the N terminus of a protein",
		        true),

		/**
		 * ModType_modnaa (2, "at the N terminus of a protein at particular
		 * amino acids", must be variable modification),
		 */
		modnaa(
		        "Modify at the N terminus of a protein at specific amino acids",
		        true),

		/**
		 * ModType_modc (3, "at the C terminus of a protein"), must be variable
		 * modification
		 */
		modc(
		        "Modify at the C terminus of a protein",
		        true),

		/**
		 * ModType_modcaa (4,"at the C terminus of a protein at particular amino
		 * acids", must be variable modification),
		 */
		modcaa(
		        "Modify at the C terminus of a protein at specific amino acids",
		        true),

		/** ModType_modnp (5, "at the N terminus of a peptide"), */
		modnp("Modify at the N terminus of a peptide", false),

		/**
		 * ModType_modnpaa (6, "at the N terminus of a peptide at particular
		 * amino acids", must be variable modification),
		 */
		modnpaa(
		        "Modify at the N terminus of a peptide at specific amino acids",
		        true),

		/** ModType_modcp (7, "at the C terminus of a peptide"), */
		modcp("Modify at the C terminus of a peptide", false),

		/**
		 * ModType_modcpaa (8, "at the C terminus of a peptide at particular
		 * amino acids", must be variable modification)
		 */
		modcpaa(
		        "Modify at the C terminus of a peptide at specific amino acids",
		        true),

		/** ModType_modmax (9, "the max number of modification types") */
		modmax("Not a modification, the max number", false);

		/*
		 * The description of this mod type
		 */
		private String description;
		/*
		 * If this type of mod must be variable modification
		 */
		private boolean must_var_mod;
		
		private ModType(String description, boolean must_var_mod) {
			this.description = description;
			this.must_var_mod = must_var_mod;
		}

		/**
		 * The description of this type
		 * 
		 * @return the description
		 */
		public final String getDescription() {
			return description;
		}

		/**
		 * If true, modifications of this type must be variable modification.
		 * 
		 * @return the must_var_mod
		 */
		public final boolean isMust_var_mod() {
			return must_var_mod;
		}

		@Override
		public String toString() {
			return this.description
			        + (this.must_var_mod ? ", must be variable modification."
			                : ".");
		}
	}

	/**
	 * {@inheritDoc}}
	 * 
	 * @return
	 */
	public ModSite cline() {
		try {
	        return (ModSite) super.clone();
        } catch (CloneNotSupportedException e) {
	        throw new RuntimeException(e);
        }
	}
	
	/**
	 * {@inheritDoc}}
	 */
	@Override
    public ModSite deepClone() {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
			oos.writeObject(this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			return (ModSite) ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
	
	public static void main(String [] args){
		ModSite ss = new ModSite('c',(char)0);
		ModSite aa = new ModSite('c', 'T');
		System.out.println(ss.getModType());
		System.out.println("aa"+aa.getModType());
		
		ModSite mipa = ss.deepClone();
		mipa.symbol = "MIPA";
		System.out.println(ss.symbol);
		
	}
	
	
}
