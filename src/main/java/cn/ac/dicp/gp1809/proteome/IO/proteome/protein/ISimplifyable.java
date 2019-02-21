/* 
 ******************************************************************************
 * File: ISimplifyable.java * * * Created on 05-05-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.protein;

/**
 * The simplifiable object for protein simplifier
 * 
 * @author Xinning
 * @version 0.1, 05-05-2010, 13:29:33
 */
public interface ISimplifyable extends Cloneable{

	
	/**
	 * The full name of the protein
	 * 
	 * @return
	 */
	public String getName();
	
	
	/**
	 * The number of aminoacids in the protein sequence
	 * 
	 * @return
	 */
	public int getNumAminoacids();
	
}
