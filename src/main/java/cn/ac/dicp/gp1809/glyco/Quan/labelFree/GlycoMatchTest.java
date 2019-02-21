/* 
 ******************************************************************************
 * File: GlycoMatchTest.java * * * Created on 2013-4-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLReader;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 *
 * @version 2013-4-26, 16:14:37
 */
public class GlycoMatchTest {
	
	private static void find(String ppl, String iden) throws Exception{
		
		HashSet <String> set = new HashSet <String>();
		HashMap <Double, Double> map = new HashMap <Double, Double>();
		
		GlycoIdenXMLReader reader1 = new GlycoIdenXMLReader(iden);
		NGlycoSSM [] s1 = reader1.getAllMatches();
		for(int i=0;i<s1.length;i++){
			if(s1[i].getScore()>=20 && s1[i].getRank()==1){
//				System.out.println(s1[i].getRT()+"\t"+s1[i].getPepMass());
				map.put(s1[i].getRT(), s1[i].getPepMass());
			}
		}
		
		Double [] peprt = map.keySet().toArray(new Double[map.size()]);
		Arrays.sort(peprt);
		
		NGlycoPepCriteria filter = new NGlycoPepCriteria(true);
		PeptideListReader reader = new PeptideListReader(ppl);
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			if(pep.isTP() && pep.getPrimaryScore()>20 && filter.filter(pep)){
				boolean match = false;
				double rtdiff = 0;
				for(int i=0;i<peprt.length;i++){
					if(Math.abs(pep.getMH()-AminoAcidProperty.PROTON_W-map.get(peprt[i]))<pep.getMH()*2.0E-5){
						match = true;
						rtdiff = pep.getRetentionTime()-s1[i].getRT();
						break;
					}
				}
				if(match){
					System.out.println(pep.getRetentionTime()+"\t"+(pep.getMH()-AminoAcidProperty.PROTON_W)+"\t\t"+rtdiff);
				}else{
					System.out.println(pep.getRetentionTime()+"\t\t"+(pep.getMH()-AminoAcidProperty.PROTON_W)+"\t"+rtdiff);
				}
			}
		}
		reader.close();
		
		
		
	}
	
	private static void find2(String iden1, String iden2) throws Exception{
		
		HashSet <String> set = new HashSet <String>();
		HashMap <Double, Double> map = new HashMap <Double, Double>();
		
		GlycoIdenXMLReader reader1 = new GlycoIdenXMLReader(iden1);
		NGlycoSSM [] s1 = reader1.getAllMatches();
		int [] match1 = new int [s1.length];
		
		GlycoIdenXMLReader reader2 = new GlycoIdenXMLReader(iden2);
		NGlycoSSM [] s2 = reader2.getAllMatches();
		int [] match2 = new int [s2.length];
		
		for(int i=0;i<s1.length;i++){
			double m1 = s1[i].getPepMass();
			for(int j=0;j<s2.length;j++){
				double m2 = s2[j].getPepMass();
				if(s1[i].getScore()>=20 && s1[i].getRank()==1 && s2[j].getScore()>=20 && s2[j].getRank()==1){
					if(Math.abs(m1-m2)<m1*2.0E-5){
						match1[i] = 1;
						match2[j] = 1;
					}
				}
			}
		}
		System.out.println(MathTool.getTotal(match1)+"\t"+match1.length);
		System.out.println(MathTool.getTotal(match2)+"\t"+match2.length);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

//		GlycoMatchTest.find("J:\\backup\\P\\glyco_quant\\final20130227\\20130202_glyco_hela\\ppl\\" +
//				"20120204_Hela_HILIC_deglyco_100min.csv.ppl",
//				"J:\\backup\\P\\glyco_quant\\final20130227\\20130202_glyco_hela\\iden\\" +
//				"20120206_Hela_HILIC_intact_HCD_100min.iden.pxml");
		String s1 = "J:\\backup\\P\\glyco_quant\\final20130227\\20130202_glyco_hela\\iden\\" +
				"20120203_Hela_HILIC_intact_HCD_100min.iden.pxml";
		String s2 = "J:\\backup\\P\\glyco_quant\\final20130227\\20130202_glyco_hela\\iden\\" +
				"20120203_Hela_HILIC_intact_HCD_100min_130204114748.iden.pxml";
		GlycoMatchTest.find2(s1, s2);
	}

}
