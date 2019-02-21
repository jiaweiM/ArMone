/* 
 ******************************************************************************
 * File: SearchParameterHelper.java * * * Created on 02-09-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch;

/**
 * To get some information from the database search parameter
 * 
 * @author Xinning
 * @version 0.1, 02-09-2010, 10:46:34
 * 
 */
public class SearchParameterHelper {
	
	/**
	 * The default mass tolerance for the getting of PTM symbol.
	 * {@link #getSymbolForAddedMass(ISearchParameter, double)}
	 * {@link #getSymbolForAddedMass(ISearchParameter, double, double)}
	 */
	private static final double TOL_SYM_GOT = 0.5;

	/**
	 * Get the PTM symbol (variable modification) for the specified mass within
	 * the tolerance.
	 * 
	 * @param add
	 * @param tol
	 * @return
	 */
	public static char getSymbolForAddedMass(ISearchParameter searchparam,
	        double add, double tol) {
		AminoacidModification aamodif = searchparam.getVariableInfo();
		char symbol = aamodif.getSymbolForMassWithTolerance(add, tol);
		return symbol;
	}

	/**
	 * Get the PTM symbol (variable modification) for the specified mass within
	 * default tolerance. The default tolerance is {@link #TOL_SYM_GOT} [0.5]
	 * 
	 * @param add
	 * @param tol
	 * @return
	 */
	public static char getSymbolForAddedMass(ISearchParameter searchparam,
	        double add) {
		return getSymbolForAddedMass(searchparam, add, TOL_SYM_GOT);
	}

}
