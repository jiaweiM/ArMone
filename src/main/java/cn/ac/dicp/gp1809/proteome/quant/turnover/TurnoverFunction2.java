/* 
 ******************************************************************************
 * File: TurnoverFunction2.java * * * Created on 2012-10-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover;

import cn.ac.dicp.gp1809.util.math.curvefit.IFunction;

/**
 * @author ck
 *
 * @version 2012-10-24, 9:27:44
 */
public class TurnoverFunction2 implements IFunction {

private double [] para;
	
	public TurnoverFunction2(){
		this.para = new double[]{1.0, 1.0};
	}
	
	public TurnoverFunction2(double [] para){
		this.para = para;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#fx(double)
	 */
	@Override
	public double fx(double x) {
		// TODO Auto-generated method stub
		double fx = para[0]*Math.exp(-para[1]*x);
		return fx;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#getPara()
	 */
	@Override
	public double[] getPara() {
		// TODO Auto-generated method stub
		return para;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#setPara(double[])
	 */
	@Override
	public void setPara(double[] para) {
		// TODO Auto-generated method stub
		this.para = para;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#getParaNum()
	 */
	@Override
	public int getParaNum() {
		// TODO Auto-generated method stub
		return para.length;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#getInitialValue(double[], double[])
	 */
	@Override
	public double[] getInitialValue(double[] x, double[] xData) {
		// TODO Auto-generated method stub
		return new double[]{1.0, 1.0};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
