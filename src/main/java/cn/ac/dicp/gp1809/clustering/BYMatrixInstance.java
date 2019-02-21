/* 
 ******************************************************************************
 * File:BYMatrixInstance.java * * * Created on 2012-7-25
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering;

import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * @author ck
 *
 * @version 2012-7-25, 16:35:10
 */
public class BYMatrixInstance extends DenseInstance {

	/**
	 * @param numAttributes
	 */
	public BYMatrixInstance(int numAttributes) {
		super(numAttributes);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int idx;
	
//	public BYMatrixInstance(double[] attValues, int idx){
		
//	}

}
