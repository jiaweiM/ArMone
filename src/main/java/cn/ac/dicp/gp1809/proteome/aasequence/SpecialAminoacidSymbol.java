/* 
 ******************************************************************************
 * File: SpecialAminoacidSymbol.java * * * Created on 11-16-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

/**
 * In some cases, you may need characters to indicated special aminoacids or
 * aminoaicds at special localization (C-term, C-term Q, Protein C-term and so
 * on). This method provides a standard.
 * 
 * @author Xinning
 * @version 0.1, 11-16-2008, 15:10:17
 */
public class SpecialAminoacidSymbol {
	
	/**
	 * This indicated aminaocids at the Protein N terminus.
	 */
	public final static int PRO_N_TERM = 0;
	
	/**
	 * This indicated aminaocids at the Protein C terminus.
	 */
	public final static int PRO_C_TERM = 1;
	
	/**
	 * This indicated aminaocids at the Peptide N terminus.
	 */
	public final static int PEP_N_TERM = 2;
	
	/**
	 * This indicated aminaocids at the Peptide C terminus.
	 */
	public final static int PEP_C_TERM = 3;
	
	
	
	
}
