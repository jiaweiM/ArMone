/* 
 ******************************************************************************
 * File: IsoPatternCalculator.java * * * Created on 2010-11-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.isotope;

import java.lang.instrument.IllegalClassFormatException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.util.math.Combinator;

/**
 * @author ck
 *
 * @version 2010-11-26, 13:18:23
 */
public class IsoPatternCalculator {

	public static double [] calculate(String sequence) throws IllegalClassFormatException {

		// the count of C,H,N,O,S
		int [] count = new int [5];
		Arrays.fill(count, 0);
		char [] chars = sequence.toCharArray();
		for(int i=0;i<chars.length;i++){
			if(chars[i]>'Z' || chars[i]<'A')
				throw new IllegalClassFormatException(chars[i]+" is not an aminoacid.");
			int index = chars[i]-65;
			int [] ac = AminoAcidProperty.AMINOACID_FORMULAR[index];
			for(int j=0;j<count.length;j++){
				count[j] += ac[j];
			}
		}
		
		ArrayList <double[]> datalist = new ArrayList <double[]> ();
		for(int i=0;i<count.length;i++){
			for(int j=0;j<count[i];j++){
				datalist.add(AminoAcidProperty.Isotope_Abundance[i]);
			}
		}
		
		double [] pattern = new double [4];
		
		double p1 = 1.0;
		for(int i=0;i<datalist.size();i++){
			p1 *= datalist.get(i)[0];
		}
		pattern [0] = p1;
		
		double p2 = 0;
		for(int i=0;i<datalist.size();i++){
			double c = 1.0;
			for(int j=0;j<datalist.size();j++){
				if(j==i){
					c *= datalist.get(j)[1];
				}else{
					c *= datalist.get(j)[0];
				}
			}
			p2 += c;
		}
		pattern [1] = p2;
		
		double p31 = 0;
		for(int i=0;i<datalist.size();i++){
			double c = 1.0;
			for(int j=0;j<datalist.size();j++){
				if(i==j)
					continue;
				else{
					for(int k=0;k<datalist.size();k++){
						if(k==i){
							c *= datalist.get(k)[1];
						}else if(k==j){
							c *= datalist.get(k)[1];
						}else{
							c *= datalist.get(k)[0];
						}
					}
				}
			}
			p31 += c;
		}
		pattern [2] = p31/2.0;
		double p32 = 0;
		for(int i=0;i<datalist.size();i++){
			double c = 1.0;
			if(datalist.get(i).length==2)
				continue;
			
			for(int j=0;j<datalist.size();j++){
				if(j==i){
					c *= datalist.get(j)[2];
				}else{
					c *= datalist.get(j)[0];
				}
			}
			p32 += c;
		}
		pattern [2] += p32;
		
		double p4 = 0;
		for(int i=0;i<datalist.size();i++){
			double c = 1.0;
			for(int j=0;j<datalist.size();j++){
				if(i==j)
					continue;
				for(int k=0;k<datalist.size();k++){
					if(k==i || k==j)
						continue;
					else{
						for(int h=0;h<datalist.size();h++){
							if(h==i){
								c *= datalist.get(h)[1];
							}else if(h==j){
								c *= datalist.get(h)[1];
							}else if(h==k){
								c *= datalist.get(h)[1];
							}else{
								c *= datalist.get(h)[0];
							}
						}
					}
				}
			}
			p4 += c;
		}
		pattern [3] = p4;
		
		return pattern;
	}
	
	private static void distribution(int cCount, int c12Count, double c12Percent){
		BigDecimal c12 = Combinator.computeBig(c12Count, cCount);
//		double c13 = Combinator.computeBig(cCount-c12Count, cCount);
		BigDecimal result = new BigDecimal(1);
		result = result.multiply(c12);
		for(int i=0;i<c12Count;i++){
			result = result.multiply(new BigDecimal(c12Percent));
		}
		for(int i=0;i<cCount-c12Count;i++){
			result = result.multiply(new BigDecimal(1-c12Percent));
		}
//		double result = c12*Math.pow(c12Percent, c12Count)*Math.pow(1-c12Percent, cCount-c12Count);
		System.out.println(cCount+"\t"+c12Count+"\t"+"\t"+result.doubleValue());
	}
	
	private static double[] getDistribution(int cCount, double c12Percent){
		double [] results = new double[cCount+1];
		for(int i=cCount;i>=0;i--){
			BigDecimal c12 = Combinator.computeBig(i, cCount);
			BigDecimal result = new BigDecimal(1);
			result = result.multiply(c12);
			for(int j=0;j<i;j++){
				result = result.multiply(new BigDecimal(c12Percent));
			}
			for(int j=0;j<cCount-i;j++){
				result = result.multiply(new BigDecimal(1-c12Percent));
			}
			results[cCount-i] = result.doubleValue();
		}
		return results;
	}
	
	private static double[] getDistribution2(double [] dis1, double[] dis2){
		double[] result = new double[dis1.length+dis2.length-1];
		for(int i=0;i<dis1.length;i++){
			for(int j=0;j<dis2.length;j++){
				result[i+j]+=dis1[i]*dis2[j];
			}
		}
		return result;
	}
	
	private static void test(){
		double[] ddd = IsoPatternCalculator.getDistribution(100, 0.988944);
		double[] disPep = new double[6];
		System.arraycopy(ddd, 0, disPep, 0, disPep.length);
		
		double[] disGlyco = IsoPatternCalculator.getDistribution(100, 0.2);
		double[] combine = IsoPatternCalculator.getDistribution2(ddd, disGlyco);
		for(int i=0;i<combine.length;i++){
			System.out.println(i+"\t"+combine[i]);
		}
	}
	
	/**
	 * @param args
	 * @throws IllegalClassFormatException 
	 */
	public static void main(String[] args) throws IllegalClassFormatException {
		// TODO Auto-generated method stub

//		double [] p = IsoPatternCalculator.calculate("AAA");
//		for(int i=0;i<p.length;i++){
//			System.out.println(p[i]);
//		}
		
//		for(int i=100;i>=0;i--){
//			System.out.print(i+"\t");
//			IsoPatternCalculator.distribution(100, i, 0.988944);
//		}
		
//		double[] ddd = IsoPatternCalculator.getDistribution(100, 0.988944);
//		for(int i=0;i<ddd.length;i++)
//			System.out.println(ddd[i]);
		IsoPatternCalculator.test();
	}


}
