/* 
 ******************************************************************************
 * File: cn.ac.dicp.gp1809.util.math.curvefit * * * Created on 2010-11-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math.curvefit;

/**
 * @author ck
 *
 * @version 2010-11-18, 16:35:48
 */
public interface IFunction {

	public double fx(double x);
	
	public double [] getPara();
	
	public void setPara(double[] para);
	
	public int getParaNum();

	public double [] getInitialValue(double[] x, double[] xData);
	
}
