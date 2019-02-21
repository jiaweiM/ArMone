/* 
 ******************************************************************************
 * File: PhosConstants.java * * * Created on 02-24-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation;

/**
 * The constants used for APIVASE
 * 
 * @author Xinning
 * @version 0.1, 02-24-2009, 14:11:35
 */
public interface PhosConstants {
	/**
	 * The added mass for phosphate when an aminoaicd is phosphorylated, 79.966331
	 */
	public static final double PHOS_ADD = 79.966331;

	/**
	 * The added mass dehydrate (loss of phosphate), -18.010565
	 */
	public static final double NEU_ADD = -18.010565;
	
	/**
	 * The mass of phosphate, 97.976896
	 */
	public static final double PHOSPHATE_MASS = 97.976896;
	
	/**
	 * The default phosphorylation symbol, 'p'
	 */
	public static final char PHOS_SYMBOL = 'p';
	
	/**
	 * The default phosphorylation symbol with neutral loss, 'n'
	 */
	public static final char NEU_SYMBOL = 'n';
}
