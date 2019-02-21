/* 
 ******************************************************************************
 * File: QuantitationTest.java * * * Created on 2013-7-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;

/**
 * @author ck
 *
 * @version 2013-7-10, 15:35:22
 */
public class QuantitationTest {
	
	private static void modTest(String in) throws FileDamageException, IOException{
		
		HashMap<Double, Integer> map = new HashMap <Double, Integer>();
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			if(!files[i].getName().endsWith("ppl"))
				continue;
			
			HashMap<String,HashSet<String>> uniquemap = new HashMap <String,HashSet<String>>();
			HashMap<String,IModifSite[]> modmap = new HashMap<String,IModifSite[]>();
			
			PeptideListReader reader = new PeptideListReader(files[i]);
			AminoacidModification aam = reader.getSearchParameter().getVariableInfo();
			IPeptide peptide = null;
			while((peptide=reader.getPeptide())!=null){
				String sequence = peptide.getSequence();
				String uniqueSeq = PeptideUtil.getUniqueSequence(sequence);
				
				if(uniquemap.containsKey(uniqueSeq)){
					uniquemap.get(uniqueSeq).add(sequence);
				}else{
					HashSet <String> set = new HashSet <String>();
					set.add(sequence);
					uniquemap.put(uniqueSeq, set);
				}
				
				IModifSite[] sites = peptide.getPeptideSequence().getModifications();
				if(sites!=null)
					modmap.put(sequence, sites);
			}
			
			Iterator <String> it = uniquemap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				HashSet <String> set = uniquemap.get(key);
				if(set.size()==1){
					String [] ss = set.toArray(new String[set.size()]);
					IModifSite[] sites = modmap.get(ss[0]);
					if(sites!=null)
					for(int j=0;j<sites.length;j++){
						char symbol = sites[j].symbol();
						double mass = aam.getAddedMassForModif(symbol);
						if(map.containsKey(mass)){
							map.put(mass, map.get(mass)+1);
						}else{
							map.put(mass, 1);
						}
					}
				}
			}
			
			reader.close();
		}
		
		Double [] dds = map.keySet().toArray(new Double[map.size()]);
		Arrays.sort(dds);
		for(int i=0;i<dds.length;i++){
			System.out.println(dds[i]+"\t"+map.get(dds[i]));
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException {
		// TODO Auto-generated method stub

		QuantitationTest.modTest("K:\\Data\\sixple\\turnover\\dat\\0_3_6");
	}

}
