/* 
 ******************************************************************************
 * File: Enzymes.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

/**
 * This class contains all the parsed data from the 'enzyme' section of the
 * datfile.
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 18:31:45
 */
public class Enzymes {

	/**
	 * The title (name)
	 */
	private String title;

	/**
	 * The cleave sites
	 */
	private String cleavage;

	/**
	 * The enzyme will not cleave if these aminoacids followed the cleavage
	 * site. Null if no restrict.
	 */
	private String restrict;

	/**
	 * The sense to be cleavage.
	 */
	private String sense;

	public Enzymes() {
	}
	
	public static final Enzymes Trypsin = new Enzymes("Trypsin", "KR", "P", "CTERM" );
	
	public Enzymes(String title, String cleavage, String restrict, String sense) {
		this.setTitle(title);
		this.setCleavage(cleavage);
		this.setRestrict(restrict);
		this.setSense(sense);
	}

	/**
	 * @return the title (name)
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title (name) to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The cleave sites
	 * 
	 * @return the cleavage
	 */
	public final String getCleavage() {
		return cleavage;
	}

	/**
	 * The cleave sites
	 * 
	 * @param cleavage
	 *            the cleavage to set
	 */
	public final void setCleavage(String cleavage) {
		this.cleavage = cleavage;
	}

	/**
	 * The enzyme will not cleave if these aminoacids followed the cleavage
	 * site. Null if no restrict.
	 * 
	 * @return the restrict
	 */
	public final String getRestrict() {
		return restrict;
	}

	/**
	 * The enzyme will not cleave if these aminoacids followed the cleavage
	 * site. Null if no restrict.
	 * 
	 * @param restrict
	 *            the restrict to set
	 */
	public final void setRestrict(String restrict) {
		this.restrict = restrict;
	}

	/**
	 * The sense to be cleavage.
	 * 
	 * @return the sense
	 */
	public final String getSense() {
		return sense;
	}

	/**
	 * The sense to be cleavage.
	 * 
	 * @param sense
	 *            the sense to set
	 */
	public final void setSense(String sense) {
/*
		if (sense == null)
			throw new NullPointerException(
			        "The sense for enzymatical cleavage must not be null.");
*/
		this.sense = sense;
	}

	/**
	 * If the cleavage is c terminal sense (cleave at c terminal of target
	 * aminoacid).
	 * 
	 * @return
	 */
	public final boolean isSense_Cterm() {

		if (this.sense.equalsIgnoreCase("Cterm"))
			return true;

		if (this.sense.equalsIgnoreCase("Nterm"))
			return false;
		
		return false;

//		throw new IllegalArgumentException("Unkown cleavage sense: "
//		        + this.sense + "; must be Cterm or Nterm");
	}
}
