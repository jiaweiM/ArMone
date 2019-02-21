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

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2010-11-18, 16:37:18
 */
public class WeibullFunction implements IFunction {

	private double [] para;

	private DecimalFormat df6 = DecimalFormats.DF0_6;
	
	public WeibullFunction(){
		this.para = new double []{1.0,1.0,1.0};
	}
	
	public WeibullFunction(double a, double b, double c){
		this.para = new double [3];
		this.para[0] = a;
		this.para[1] = b;
		this.para[2] = c;
	}
	
	public WeibullFunction(double [] para){
		this.para = para;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#fx(double)
	 */
	@Override
	public double fx(double x) {
		// TODO Auto-generated method stub
		double d1 = Math.pow(x/para[0], para[1]);
		double d2 = Math.exp(-d1);
		double y = para[2]*d1*d2;
		return Double.parseDouble(df6.format(y));
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
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#getInitialValue(double[])
	 */
	@Override
	public double[] getInitialValue(double[] x, double [] y) {
		// TODO Auto-generated method stub
		return null;
	}

}
