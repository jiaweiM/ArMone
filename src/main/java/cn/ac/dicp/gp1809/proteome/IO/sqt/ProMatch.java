/* 
 ******************************************************************************
 * File: ProMatch.java * * * Created on 03-31-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

/**
 * The matched protein
 * 
 * @author Xinning
 * @version 0.1, 03-31-2009, 20:46:05
 */
public class ProMatch {

	private String locus;
	private String description;

	/**
	 * The locus must not be null.
	 * 
	 * @param locus
	 * @param description
	 */
	public ProMatch(String locus, String description) {
		if (locus == null)
			throw new NullPointerException(
			        "The locus for the protein match must not be null.");
		this.locus = locus;
		this.description = description;
	}

	/**
	 * The locus must not be null. In some conditions, the description is null.
	 * Such as the output by crux.
	 * 
	 * @param locus
	 * @param description
	 */
	public ProMatch(String locus) {
		this(locus, null);
	}

	/**
	 * The locus of the protein
	 * 
	 * @return
	 */
	public String getLocus() {
		return this.locus;
	}

	/**
	 * The description of the protein
	 * 
	 * @return
	 */
	public String getDescription() {
		return this.description;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProMatch))
			return false;
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return this.locus.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("L\t").append(this.locus);
		if (this.description != null)
			sb.append('\t').append(this.description);
		
		return sb.toString();
	}
}
