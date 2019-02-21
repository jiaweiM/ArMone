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

import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2010-11-18, 16:26:22
 */
public class GaussianFunction implements IFunction {

	private double [] para;

	public GaussianFunction(){
		this.para = new double []{1.0,1.0,1.0};
	}
	
	public GaussianFunction(double a, double b, double c){
		this.para = new double [3];
		this.para[0] = a;
		this.para[1] = b;
		this.para[2] = c;
	}
	
	public GaussianFunction(double [] para){
		this.para = para;
	}
	
	public double fx(double x){
		double d1 = Math.pow((x-para[1]), 2)*para[2];
		double y = para[0]*Math.exp(-d1);
		
		if(para[0]<=0)
			return 0;
		else if(para[2]<=0)
			return 0;
		
		return y;
	}
	
	public double [] getPara(){
		return para;
	}
	
	public void setPara(double [] para){
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
		double [] para = new double[3];

        int maxYIndex = MathTool.getMaxIndex(y);
        double dx1 = x[maxYIndex] - x[0];
        double dx2 = x[x.length-1] - x[maxYIndex];
        double dx = dx1>dx2 ? dx1 : dx2;
        
        double ldy1 = Math.log(y[maxYIndex])-Math.log(y[0]);
        double ldy2 = Math.log(y[maxYIndex])-Math.log(y[y.length-1]);
        double ldy = dx1>dx2 ? ldy1 : ldy2;
        
        para[0] = y[maxYIndex];
        para[1] = x[maxYIndex];
        para[2] = ldy/dx/dx;
        
/*        
        System.out.println("a:\t"+para[0]);
        System.out.println("b:\t"+para[1]);
        System.out.println("c:\t"+para[2]);
*/       
		return para;
	}
	
	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GaussianFunction g = new GaussianFunction(1230316.977, 14249.0, 2.374E-4);
		System.out.println(g.fx(14165.0));
	}

}
