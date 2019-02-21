/*
 * *****************************************************************************
 * File: AminoAcidProperty.java * * * Created on 03-05-2008 
 * Copyright (c) 2008 Xinning Jiang vext@163.com 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aaproperties;

import java.util.ArrayList;

/**
 * Several properties for amino acids
 * 
 * @author Xinning
 * @version 0.1, 03-05-2008, 11:10:38
 * @version 0.1.1, 02-21-2011, add aminoacid J and U as user defined aminoacid
 */
public final class AminoAcidProperty {
	
	/*
	 * name of amino acids
	 */
	public static final String[] AMINOACIDS = new String[]{
		"Ala(A)","Asx(B)","Cys(C)","Asp(D)","Glu(E)","Phe(F)",
		"Gly(G)","His(H)","Ile(I)","Jaa(J)","Lys(K)","Leu(L)",
		"Met(M)","Asn(N)","Orn(O)","Pro(P)","Gln(Q)","Arg(R)",
		"Ser(S)","Thr(T)","U(Uaa)","Val(V)","Trp(W)","Xaa(X)",
		"Tyr(Y)","Glx(Z)"
	};
	
	public static final char[] AACHAR = new char[]{
		'A','B','C','D','E','F','G','H','I','J','K','L','M',
		'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
	};
	
	
	/*
	 * weigtht of elements form amino acids
	 */
	
	public static final double MONOW_C = 12;
	public static final double MONOW_C13 = 13.003354;
	
	public static final double MONOW_H = 1.007825;
	public static final double MONOW_D = 2.014101;
	
	public static final double MONOW_N = 14.003074;
	public static final double MONOW_N15 = 15.000108;
	
	public static final double MONOW_O = 15.994915;
	public static final double MONOW_O17 = 16.999132;
	public static final double MONOW_O18 = 17.9991603;
	
	public static final double MONOW_S32 = 31.972071;
	public static final double MONOW_S33 = 32.971458;
	public static final double MONOW_S34 = 33.967867;
	public static final double MONOW_S36 = 35.967081;
	
	public static final double AVERAGEW_C = 12.0107;
	public static final double AVERAGEW_H = 1.00794;
	public static final double AVERAGEW_N = 14.00674;
	public static final double AVERAGEW_O = 15.9994;
	public static final double AVERAGEW_S = 32.066;
	
	/**
	 * The proton mass (without electron)
	 */
	public static final double PROTON_W = 1.007276;
	
	
	// fomular of all aminoacid,in order,        C,H,N,O,S  specifically;
	// B = the oxidation of M;
	// C is the alkylate form and K is the guanidylate form;	
	private static final int[] A = new int[]{3,5,1,1,0};
	private static final int[] B = new int[]{0,0,0,0,0};
	private static final int[] C = new int[]{5,8,2,2,1};
	private static final int[] D = new int[]{4,5,1,3,0};
	private static final int[] E = new int[]{5,7,1,3,0};
	private static final int[] F = new int[]{9,9,1,1,0};
	private static final int[] G = new int[]{2,3,1,1,0};
	private static final int[] H = new int[]{6,7,3,1,0};
	private static final int[] I = new int[]{6,11,1,1,0};
	private static final int[] J = new int[]{0,0,0,0,0};
	private static final int[] K = new int[]{7,14,4,1,0};
	private static final int[] L = new int[]{6,11,1,1,0};
	private static final int[] M = new int[]{5,9,1,1,1};
	private static final int[] N = new int[]{4,6,2,2,0};
	private static final int[] O = new int[]{5,10,2,1,0};
	private static final int[] P = new int[]{5,7,1,1,0};
	private static final int[] Q = new int[]{5,8,2,2,0};
	private static final int[] R = new int[]{6,12,4,1,0};
	private static final int[] S = new int[]{3,5,1,2,0};
	private static final int[] T = new int[]{4,7,1,2,0};
	private static final int[] U = new int[]{0,0,0,0,0};
	private static final int[] V = new int[]{5,9,1,1,0};
	private static final int[] W = new int[]{11,10,2,1,0};
	private static final int[] X = new int[]{0,0,0,0,0};
	private static final int[] Y = new int[]{9,9,1,2,0};
	private static final int[] Z = new int[]{0,0,0,0,0};
	

//2D array of amino acid molecular formular,the index of a specific amino acid is its name minus 'A';	
	public static final int[][] AMINOACID_FORMULAR = new int[][]{
		A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,
		Q,R,S,T,U,V,W,X,Y,Z
		};

	/**
	 * GRAVY (Grand Average of Hydropathy) 
	 * see: http://us.expasy.org/tools/pscale/Hphob.Doolittle.html
	 * 
	 * Symbol: A-Z
	 */
	public static final double[] AAGRAVY = new double[]{
		1.8,-3.5,2.5,-3.5,-3.5,2.8,-0.4,-3.2,4.5,0,-3.9,3.8,
		1.9,-3.5,0,-1.6,-3.5,-4.5,-0.8,-0.7,0,4.2,-0.9,4.15,
		-1.3,-3.5
	};
	
	public static final double [][] Isotope_Abundance = new double[][]{
		/* C */		{0.988944, 0.011056} ,
		/* H */		{0.999844, 0.000155} ,
		/* O */		{0.997621, 0.000379, 0.002000} ,
		/* N */		{0.996337, 0.003663} ,
		/* S */		{0.950396, 0.007486, 0.0419712, 0.000146}
	};
	
	/**
	 * pk value table for amino acids at different conditions
	 *              
	 *              Ct     Nt    Sm    Sc     Sn
	 */
	public static final double[][] AAPK = new double[][]{
		/* A */    {3.55, 7.59, 0.0  , 0.0  , 0.0  } ,
		/* B */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* C */    {3.55, 7.50, 9.00 , 9.00 , 9.00 } ,
		/* D */    {4.55, 7.50, 4.05 , 4.05 , 4.05 } ,
		/* E */    {4.75, 7.70, 4.45 , 4.45 , 4.45 } ,
		/* F */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* G */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* H */    {3.55, 7.50, 5.98 , 5.98 , 5.98 } ,
		/* I */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* J */    {0.00, 0.00, 0.0  , 0.0  , 0.0  } ,
		/* K */    {3.55, 7.50, 10.00, 10.00, 10.00} ,
		/* L */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* M */    {3.55, 7.00, 0.0  , 0.0  , 0.0  } ,
		/* N */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* O */    {0.00, 0.00, 0.0  , 0.0  , 0.0  } ,
		/* P */    {3.55, 8.36, 0.0  , 0.0  , 0.0  } ,
		/* Q */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* R */    {3.55, 7.50, 12.0 , 12.0 , 12.0 } ,
		/* S */    {3.55, 6.93, 0.0  , 0.0  , 0.0  } ,
		/* T */    {3.55, 6.82, 0.0  , 0.0  , 0.0  } ,
		/* U */    {0.00, 0.00, 0.0  , 0.0  , 0.0  } ,
		/* V */    {3.55, 7.44, 0.0  , 0.0  , 0.0  } ,
		/* W */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* X */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } ,
		/* Y */    {3.55, 7.50, 10.00, 10.00, 10.00} ,
		/* Z */    {3.55, 7.50, 0.0  , 0.0  , 0.0  } 
	};
	
	public static void main(String [] args){
		
		/*int [] com = AminoAcidProperty.T;
		double mw = 0;
		mw += com[0]*AminoAcidProperty.MONOW_C;
		mw += com[1]*AminoAcidProperty.MONOW_H;
		mw += com[2]*AminoAcidProperty.MONOW_N;
		mw += com[3]*AminoAcidProperty.MONOW_O;
		mw += com[4]*AminoAcidProperty.MONOW_S32;
		System.out.println(mw);
		
		double mass = AminoAcidProperty.AVERAGEW_C*5+
		AminoAcidProperty.AVERAGEW_H*8 + AminoAcidProperty.AVERAGEW_O*4;
		System.out.println(mass);
		
		double mass1 = AminoAcidProperty.MONOW_C*0+
		AminoAcidProperty.MONOW_H*2 + AminoAcidProperty.MONOW_O*1;
		System.out.println(mass1);
		
		System.out.println(AminoAcidProperty.MONOW_H*2+AminoAcidProperty.MONOW_O);*/
		
		double mw = 0;
		/*mw -= AminoAcidProperty.MONOW_C*2;
		mw-=AminoAcidProperty.MONOW_H*5;
		mw -= AminoAcidProperty.MONOW_O*2;
		mw += AminoAcidProperty.MONOW_N;
		System.out.println(mw);*/
		
		/*mw -= AminoAcidProperty.AVERAGEW_C*2;
		mw-=AminoAcidProperty.AVERAGEW_H*5;
		mw -= AminoAcidProperty.AVERAGEW_O*2;
		mw += AminoAcidProperty.AVERAGEW_N;
		System.out.println(mw);*/
		
		mw -= AminoAcidProperty.MONOW_O*3;
		mw +=AminoAcidProperty.MONOW_C*4;
		mw += AminoAcidProperty.MONOW_H;
		mw += AminoAcidProperty.MONOW_N;
		System.out.println(mw);
		
		/*mw -= AminoAcidProperty.AVERAGEW_O*3;
		mw += AminoAcidProperty.AVERAGEW_C*4;
		mw += AminoAcidProperty.AVERAGEW_H;
		mw += AminoAcidProperty.AVERAGEW_N;
		System.out.println(mw);*/
/*		
		ArrayList <double[]> datalist = new ArrayList <double[]> ();
		for(int i=0;i<A.length;i++){
			for(int j=0;j<A[i];j++){
				datalist.add(Isotope_Abundance[i]);
			}
		}
		double total1 = 1.0;
		for(int i=0;i<datalist.size();i++){
			total1 *= datalist.get(i)[0];
		}
		System.out.println(total1);
		
		double total2 = 0;
		for(int i=0;i<datalist.size();i++){
			double c = 1.0;
			for(int j=0;j<datalist.size();j++){
				if(j==i){
					c *= datalist.get(j)[1];
				}else{
					c *= datalist.get(j)[0];
				}
			}
			total2 += c;
		}
		System.out.println(total2);
*/
		
	}
	
}
