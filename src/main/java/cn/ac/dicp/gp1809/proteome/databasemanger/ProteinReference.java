/* 
 ******************************************************************************
 * File: ProteinReference.java * * * Created on 05-11-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.exceptions.MyIllegalArgumentException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * Reference of protein containing name and index informations
 * 
 * @author Xinning
 * @version 0.3.3, 05-21-2010, 22:32:24
 */
public class ProteinReference {

	// The protein index decimal format.
	private static final String PROIDXF = "0000000";

	// The length of the formatted protein index and the bracket
	private static final int IDX_LEN = PROIDXF.length() + 1;

	// The PROIDXF
	private static final DecimalFormat DF = new DecimalFormat(PROIDXF);

	private int index = 0;
	// The partial name directly from out
	private String name;

	private boolean isDecoy;
	
	
	/**
	 * A static instance.
	 */
	private static ProteinReferencePool refpool = new ProteinReferencePool();
	
	private static IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();

	/**
	 * Get the ProteinReference for the protein name with index and name.
	 * 
	 * <p>
	 * <b>Notice: if two distinct references (same reference with different
	 * length of partial names will NOT be considered as distinct) are with the
	 * same index, this method will consider the different database has been
	 * used, and will clear the reference pool to return the legal
	 * ProteinReference </b>
	 * </p>
	 * 
	 * <p>
	 * If the index is not unknown please use get(String) method. These two
	 * method using different buffer pools.
	 * </p>
	 * 
	 * @param index
	 * @param name
	 * @return throws MyIllegalArgumentException if the index is illegal (<= 0)
	 */
	private static ProteinReference getReferenceFromPool(int index, String name) {
		try {
			return refpool.get(index, name);
		} catch (MyIllegalArgumentException e) {
			clearPool();
			return refpool.get(index, name);
		}
	}
	
	/**
	 * If the pool contains the protein
	 * 
	 * @param index
	 * @param name
	 * @return
	 */
	private static boolean contains(int index, String name) {
		try {
			return refpool.contains(index, name);
		} catch (MyIllegalArgumentException e) {
			clearPool();
			return false;
		}
	}
	
	/**
	 * Get the ProteinReference for the protein name with name.
	 * 
	 * <p>
	 * If the index is not unknown please use get(String) method. These two
	 * method using different buffer pools.
	 * </p>
	 * 
	 * @param name
	 */
	private static ProteinReference getReferenceFromPool(String name) {
		return refpool.get(name);
	}
	

	/**
	 * Clear the pool. You may need this method if another database is used. Or
	 * alternatively, you can create different instance of ProteinReference pool
	 * yourself or different database.
	 */
	private static void clearPool() {
		refpool.clear();
	}
	

	/**
	 * Parse a formatted protein reference into the protein reference. The
	 * formatted reference: name0(0000000);
	 * 
	 * @param ref
	 * @return
	 */
	public static ProteinReference parse(String ref) {
		int idx = 0;
		String name = ref;
		int len = ref.length() - 1;
		int p1 = len - IDX_LEN;
		int p2 = len - IDX_LEN - 2;
		if (p1 > 0) {
			
			//old version
			if (ref.charAt(len) == ')') {
				//old version
				if(ref.charAt(p1) == '(') {
					try {
						idx = Integer.parseInt(ref.substring(p1 + 1, len));
						name = ref.substring(0, p1);
					} catch (Exception e) {
						throw new IllegalArgumentException(
						        "Error in parsing the reference: \"" + ref + "\".",
						        e);
					}
					
					boolean contains = contains(idx, name);
					
					ProteinReference refs = getReferenceFromPool(idx, name);
					
					if(!contains)
						refs.setDecoy(judger.isDecoy(name));
					
					return refs;
				}
				
				//new
				if(p2 > 0) {
					if(ref.charAt(p2) == '(' && ref.charAt(len-2)=='.') {
						int decoy = 0;
						try {
							idx = Integer.parseInt(ref.substring(p2 + 1, len-2));
							name = ref.substring(0, p2);
							decoy = Integer.parseInt(ref.substring(len-1, len));
						} catch (Exception e) {
							throw new IllegalArgumentException(
							        "Error in parsing the reference: \"" + ref + "\".",
							        e);
						}

						ProteinReference refs = getReferenceFromPool(idx, name);
						refs.setDecoy(decoy == 2);
						
						return refs;
					}
				}

			}
		}

		return getReferenceFromPool(name);
	}

	public ProteinReference(int index, String name, boolean isDecoy) {
		this.index = index;
		this.setName(name);
		this.isDecoy = isDecoy;
	}
	
	public static ProteinReference parseProReference(String ref){
		
		int idbeg = ref.lastIndexOf("(");
		String name = ref.substring(0, idbeg);
		
		String [] ss = ref.substring(idbeg).split("[()/.]");
		
		int index = -1;
		boolean isDecoy = false;
		if(ss.length==3){
	
			index = Integer.parseInt(ss[1]);
			isDecoy = ss[2].equals("1") ? false : true;
			
			return new ProteinReference(index, name, isDecoy);
		}else{
			throw new IllegalArgumentException("Can't parse "+ref);
		}
	}

	/**
	 * Create a ProteinReference for the name. The index will leave at
	 * None_defined (0000000).
	 * 
	 * @param name
	 *            name of protein, can be partial name.
	 */
	public ProteinReference(String name, boolean isDecoy) {
		this.setName(name);
		this.isDecoy = isDecoy;
	}

	/**
	 * From 1 - n. If not set, the initial value is 0 indicating that no index
	 * of protein is determined, then the value will not print to out file
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * If not set, the initial value is 0 indicating that no index of protein is
	 * determined, then the value will not print to out file
	 * 
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * If this protein reference a decoy protein. <b>Must be set before use</b>
	 * 
	 * @see #setDecoy(boolean)
	 * @return
	 */
	public boolean isDecoy() {
		return this.isDecoy;
	}
	
	/**
	 * If this protein reference a decoy protein (random or reversed.)
	 * 
	 * @return
	 */
	public boolean setDecoy(boolean isDecoy) {
		return this.isDecoy = isDecoy;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {

		if (name == null)
			throw new NullPointerException(
			        "The name of the protein reference can not be null.");

		this.name = name;
	}

	/**
	 * The hash code equals the index of this protein. If the index of protein
	 * is not assigned (idx == 0), this value is the hash code of the name.
	 * 
	 * <p>
	 * <b>Warning: some problems need to be solved if the index is not assigned
	 * and the same reference split into different partial names</b>
	 * 
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.index <= 0 ? this.name.hashCode() : this.index;
	}

	/**
	 * If the name of two ProteinReference is the same or one startsWith()
	 * another, these two protein references are equal
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
//		if (obj == this)
//			return true;

		if (obj instanceof ProteinReference) {
			String name = ((ProteinReference) obj).getName();

			return name.length() > this.name.length() ? name
			        .startsWith(this.name) : this.name.startsWith(name);

		}

		return false;
	}

	/*
	 * Format into "Reference(0000000)" index (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name).append('(').append(DF.format(this.index)).append('.').append(this.isDecoy?2:1).append(
		        ')');
		return sb.toString();
	}

}
