/* 
 ******************************************************************************
 * File: Substituents.java * * * Created on 2012-3-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.glycoCT;

import java.util.HashMap;

/**
 * @author ck
 *
 * @version 2012-3-20, 22:06:55
 */
public enum Substituents {

	// fomular of all aminoacid,in order,        C,H,N,O,S,F,Cl,Br,I,P  specifically;
	
	acetyl ("acetyl", new int [] {2, 3, 0, 1, 0, 0, 0, 0, 0, 0}),
	
	bromo ("bromo", new int [] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0}),
	
	chloro ("chloro", new int [] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0}),
	
	ethyl ("ethyl", new int [] {2, 5, 0, 0, 0, 0, 0, 1, 0, 0}),
	
	ethanolamine ("ethanolamine", new int [] {2, 6, 1, 1, 0, 0, 0, 0, 0, 0}),
	
	flouro ("fluoro", new int [] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0}),
	
	formyl ("formyl", new int [] {1, 1, 0, 1, 0, 0, 0, 0, 0, 0}),
	
	glycolyl ("glycolyl", new int [] {2, 3, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	hydroxymethyl ("hydroxymethyl", new int [] {1, 3, 0, 1, 0, 0, 0, 0, 0, 0}),
	
	imino ("imino", new int [] {0, 1, 1, 0, 0, 0, 0, 0, 0, 0}),
	
	iodo ("iodo", new int [] {0, 0, 0, 0, 0, 0, 0, 0, 1, 0}),
	
	r_lactate ("(r)-lactate", new int [] {3, 5, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	s_lactate ("(s)-lactate", new int [] {3, 5, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	x_lactate ("(x)-lactate", new int [] {3, 5, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	succinate ("succinate", new int [] {4, 5, 0, 3, 0, 0, 0, 0, 0, 0}),
	
	lactate ("lactate", new int [] {3, 5, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	methyl ("methyl", new int [] {1, 3, 0, 0, 0, 0, 0, 0, 0, 0}),
	
	n ("n", new int [] {0, 2, 1, 0, 0, 0, 0, 0, 0, 0}),
	
	amino ("amino", new int [] {0, 2, 1, 0, 0, 0, 0, 0, 0, 0}),
	
	n_acetyl ("n-acetyl", new int [] {2, 4, 1, 1, 0, 0, 0, 0, 0, 0}),
	
	aminoacetyl ("n-acetyl", new int [] {2, 4, 1, 1, 0, 0, 0, 0, 0, 0}),
	
	n_alanine ("n-alanine", new int [] {3, 7, 2, 1, 0, 0, 0, 0, 0, 0}),
	
	aminoalanine ("n-alanine", new int [] {3, 7, 2, 1, 0, 0, 0, 0, 0, 0}),
	
	n_dimethyl ("n-dimethyl", new int [] {2, 6, 1, 0, 0, 0, 0, 0, 0, 0}),
	
	aminoformyl ("aminoformyl", new int [] {2, 6, 1, 0, 0, 0, 0, 0, 0, 0}),
	
	n_formyl ("n-formyl", new int [] {1, 2, 1, 1, 0, 0, 0, 0, 0, 0}),
	
	n_glycolyl ("n-glycolyl", new int [] {2, 3, 1, 2, 0, 0, 0, 0, 0, 0}),
	
	n_methyl ("n-methyl", new int [] {1, 4, 1, 0, 0, 0, 0, 0, 0, 0}),
	
	n_succinate ("n-succinate", new int [] {4, 6, 1, 3, 0, 0, 0, 0, 0, 0}),
	
	n_sulfate ("n-sulfate", new int [] {0, 2, 1, 3, 1, 0, 0, 0, 0, 0}),
	
	n_triflouroacetyl ("n-triflouroacetyl", new int [] {2, 1, 1, 1, 0, 3, 0, 0, 0, 0}),
	
	nitrat ("nitrat", new int [] {0, 0, 1, 2, 0, 0, 0, 0, 0, 0}),
	
	phosphate ("phosphate", new int [] {0, 2, 0, 3, 0, 0, 0, 0, 0, 1}),
	
	pyruvate ("pyruvate", new int [] {3, 3, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	r_pyruvate ("(r)-pyruvate", new int [] {3, 3, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	s_pyruvate ("(s)-pyruvate", new int [] {3, 3, 0, 2, 0, 0, 0, 0, 0, 0}),
	
	sulfate ("sulfate", new int [] {0, 1, 0, 3, 1, 0, 0, 0, 0, 0}),
	
	thio ("thio", new int [] {0, 0, 0, 0, 1, 0, 0, 0, 0, 0}),
	
	anhydro ("anhydro", new int [] {0, -2, 0, -1, 0, 0, 0, 0, 0, 0}),
	
	lactone ("lactone", new int [] {0, -2, 0, -2, 0, 0, 0, 0, 0, 0}),
	
	epoxy ("epoxy", new int [] {0, -2, 0, -1, 0, 0, 0, 0, 0, 0}),
	
//	R_pyruvate ("(r)-pyruvate", new int [] {3, 5, 0, 2, 0, 0, 0, 0, 0, 0}),
	
//	S_pyruvate ("(s)-pyruvate", new int [] {3, 5, 0, 2, 0, 0, 0, 0, 0, 0}),
	;
	
	Substituents(String symbol, int [] formula){
		this.symbol = symbol;
		this.formula = formula;
	}
	
	private static double [] mono_element = new double [] {12.0, 1.0078250319, 14.0030740074,
		15.9949146223, 31.97207073, 18.99840320, 34.96885271, 78.9183379, 126.904468, 30.97376149};
	
	private static double [] ave_element = new double [] {12.0107, 1.00794, 14.0067,
		15.9994, 32.065, 18.9984032, 35.453, 79.904, 126.90447, 30.973761};
	
	private String symbol;
	private int [] formula;
	private char parentLinkType;
	private char childLinkType;
	private String linkPosition;
	
	public double getMonoMass(){
		
		double mass = 0;
		for(int i=0;i<mono_element.length;i++){
			mass += this.formula[i]*mono_element[i];
		}

		return mass;
	}
	
	public double getAveMass(){
		
		double mass = 0;
		for(int i=0;i<ave_element.length;i++){
			mass += this.formula[i]*ave_element[i];
		}
		mass -= 1.00794;
		mass -= 15.9994;
		
		return mass;
	}
	
	public String getSymbol(){
		return this.symbol;
	}
	
	public void setParentLinkType(char parentLinkType){
		this.parentLinkType = parentLinkType;
	}
	
	public char getParentLinkType(){
		return parentLinkType;
	}
	
	public void setChildLinkType(char childLinkType){
		this.childLinkType = childLinkType;
	}
	
	public char getChildLinkType(){
		return childLinkType;
	}
	
	public void setLinkPosition(String linkPosition){
		this.linkPosition = linkPosition;
	}
	
	public String getLinkPosition(){
		return linkPosition;
	}
	
	public static HashMap <String, Substituents> getSubMap(){
		
		HashMap <String, Substituents> submap = new HashMap <String, Substituents>();
		
		Substituents [] subs = Substituents.values();
		for(int i=0;i<subs.length;i++){
			submap.put(subs[i].getSymbol(), subs[i]);
		}
		
		return submap;
	}
	
	public static void main(String [] args){
		
/*		Substituents [] sss = Substituents.values();
		for(int i=0;i<sss.length;i++){
			System.out.println(sss[i].getSymbol()+"\t"+sss[i].getMonoMass());
		}
*/		
		Substituents s1 = Substituents.valueOf(Substituents.class, "(s)-lactate");
//		Substituents ss = Substituents.valueOf("Phospate");
		System.out.println(s1==null);
/*		
		int [] glyco = new int [] {3, 4, 0, 2};
		double mass1 = 0;
		double mass2 = 0;
		for(int i=0;i<glyco.length;i++){
			mass1 += glyco[i]*mono_element[i];
			mass2 += glyco[i]*ave_element[i];
		}
		System.out.println(mass1);
		System.out.println(mass2);
*/		
	}
	
}
