/* 
 ******************************************************************************
 * File:BYMatrixDistanceFunction.java * * * Created on 2012-7-25
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering;

import cn.ac.dicp.gp1809.util.math.MathTool;

import weka.core.*;

/**
 * @author ck
 *
 * @version 2012-7-25, 15:56:35
 */
public class BYMatrixDistanceFunction extends EuclideanDistance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see weka.core.DistanceFunction#distance(weka.core.Instance, weka.core.Instance)
	 */
	@Override
	public double distance(Instance arg0, Instance arg1) {
		// TODO Auto-generated method stub
		double [] values0 = arg0.toDoubleArray();
		double [] values1 = arg1.toDoubleArray();
		double [][] d0 = new double[4][values0.length/4];
		double [][] d1 = new double[4][values1.length/4];
		for(int i=0;i<d0.length;i++){
			for(int j=0;j<d0[i].length;j++){
				d0[i][j] = values0[i*d0[i].length+j];
				d1[i][j] = values1[i*d0[i].length+j];
			}
		}
		return MathTool.getCorr2(d0, d1);
	}

	/* (non-Javadoc)
	 * @see weka.core.NormalizableDistance#globalInfo()
	 */
	@Override
	public String globalInfo() {
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see weka.core.RevisionHandler#getRevision()
	 */
	@Override
	public String getRevision() {
		// TODO Auto-generated method stub
		return "1.0";
	}

}
