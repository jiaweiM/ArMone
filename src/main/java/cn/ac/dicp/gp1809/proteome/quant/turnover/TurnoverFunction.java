/* 
 ******************************************************************************
 * File: TurnoverFunction.java * * * Created on 2012-10-23
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.turnover;

import cn.ac.dicp.gp1809.util.math.curvefit.CurveFitting;
import cn.ac.dicp.gp1809.util.math.curvefit.IFunction;

/**
 * @author ck
 *
 * @version 2012-10-23, 15:46:22
 */
public class TurnoverFunction implements IFunction {

	private double [] para;
	
	public TurnoverFunction(){
		this.para = new double[]{1.0, 0.0, 1.0};
	}
	
	public TurnoverFunction(double [] para){
		this.para = para;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.math.curvefit.IFunction#fx(double)
	 */
	@Override
	public double fx(double x) {
		// TODO Auto-generated method stub
		double fx = para[1]+(para[0]-para[1])*Math.exp(-para[2]*x);
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
		return new double[]{1.0, 0.0, 1.0};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double begin = System.currentTimeMillis();
		
		double [] xdata = new double[]{0, 3, 6, 12, 24, 48};
		double [] ydata = new double[]{0.9896, 0.9655, 0.8054, 0.5876, 0.3976, 0.1259};
		
		double [] xdata2 = new double[]{0, 6, 24, 48};
		double [] ydata2 = new double[]{0.9896, 0.8054, 0.3976, 0.1259};
		
		IFunction gFunction = new TurnoverFunction();
		CurveFitting fite = new CurveFitting(xdata, ydata, gFunction);
		fite.fit();
		
		double [] dde = fite.getBestParams();
		System.out.println(dde[0]+"\t"+dde[1]+"\t"+dde[2]+"\t"+fite.getFitGoodness());
		
		double dd1 = (dde[0]-2*dde[1])/(2*dde[0]-2*dde[1]);
		double half1 = -Math.log(dd1)/dde[2];
		System.out.println(dd1+"\t"+half1);
		
		CurveFitting fite2 = new CurveFitting(xdata2, ydata2, gFunction);
		fite2.fit();
		
		double [] dde2 = fite2.getBestParams();
		System.out.println(dde2[0]+"\t"+dde2[1]+"\t"+dde2[2]+"\t"+fite2.getFitGoodness());
		
		double dd2 = (dde2[0]-2*dde2[1])/(2*dde2[0]-2*dde2[1]);
		double half2 = -Math.log(dd2)/dde2[2];
		System.out.println(dd2+"\t"+half2);
		
		double end = System.currentTimeMillis();
		System.out.println("Run time:\t"+(end-begin)/1000.0);
	}

}
