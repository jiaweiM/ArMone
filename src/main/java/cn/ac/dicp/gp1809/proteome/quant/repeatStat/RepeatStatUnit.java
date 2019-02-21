/* 
 ******************************************************************************
 * File: StatUnit.java * * * Created on 2011-11-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.repeatStat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2011-11-10, 16:32:59
 */
public class RepeatStatUnit {
	
	private IPeptide pep;
	private ArrayList <double[]> ratiolist;
	private double [] averatio;
	private double [] rsd;

	public RepeatStatUnit(IPeptide pep, ArrayList <double[]> ratiolist){
		
		this.pep = pep;
		this.ratiolist = ratiolist;
		
		double [] r0 = ratiolist.get(0);
		ArrayList <Double> [] dlist = new ArrayList [r0.length];
		for(int i=0;i<dlist.length;i++){
			dlist[i] = new ArrayList <Double>();
		}
		
		this.averatio = new double [r0.length];
		this.rsd = new double [r0.length];
		
		for(int i=0;i<ratiolist.size();i++){
			double [] ri = ratiolist.get(i);
			for(int j=0;j<ri.length;j++){
				dlist[j].add(ri[j]);
			}
		}
		
		for(int i=0;i<r0.length;i++){
			averatio[i] = MathTool.getAveInDouble(dlist[i]);
			rsd[i] = MathTool.getRSDInDouble(dlist[i]);
		}
	}
	
	public IPeptide getPeptide(){
		return pep;
	}
	
	public double [] getAveRatio(){
		return averatio;
	}
	
	public double [] getRsd(){
		return rsd;
	}
	
	public ArrayList <double[]> getRatioList(){
		return ratiolist;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}
	
}
