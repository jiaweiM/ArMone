/* 
 ******************************************************************************
 * File: IProteinGroupSimplifier.java * * * Created on 05-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.protein;


/**
 * In a very common case, proteins which are homologous with each other may be
 * not distinguishable when there are very few peptides identified for these
 * proteins. Use this class for the simplification of these proteins to retain
 * only one of them.
 * 
 * @author Xinning
 * @version 0.1, 05-19-2009, 14:31:06
 */
public interface IProteinGroupSimplifier <T extends ISimplifyable>{

	/**
	 * Simplify the homologous protein references to one.
	 * 
	 * @param details
	 * @return
	 */
	public T simplify(T[] details);
}
