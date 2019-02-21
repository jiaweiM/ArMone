/* 
 ******************************************************************************
 * File:MascotDeltaData.java * * * Created on 2012-8-15
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.group;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;

/**
 * @author ck
 *
 * @version 2012-8-15, 18:06:34
 */
public class MascotDeltaData {
	
	private BufferedReader reader;
	
	public MascotDeltaData(String file) throws IOException{
		this.reader = new BufferedReader(new FileReader(file));
	}
	
	private void testShuf() throws IOException{
		
		String line = null;
		int ipi = 0;
		int shf = 0;
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			String ref = ss[5];
			if(ref.startsWith("SHF")){
				shf++;
			}else if(ref.startsWith("IPI")){
				ipi++;
			}
		}
		System.out.println(ipi+"\t"+shf);
		reader.close();
	}
	
	public void phosDownTest(String in, String id) throws IOException{
		
		HashSet <String> set = this.getPepSet(in, id);
		
		int target = 0;
		int decoy = 0;
		String line = null;
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			String seq = ss[3];
			String uq = PeptideUtil.getUniqueSequence(seq);
			if(ss[5].startsWith("REV"))
				continue;
			if(set.contains(uq)){
				target++;
			}else{
				decoy++;
			}
		}
		System.out.println((target+decoy)+"\t"+target+"\t"+decoy);
	}
	
	private HashSet <String> getPepSet(String in, String id) throws IOException{
		
		HashSet <String> set = new HashSet <String>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null && line.trim().length()>0){
			String [] ss = line.split("\t");
			if(ss[0].startsWith(id))
				set.add(ss[5]);
		}
		reader.close();
		return set;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String file = "H:\\Validation\\Byy_phos_5600_velos\\F002934_1_human_shuf.pep.txt";
//		String file = "H:\\Validation\\" +
//				"phospho_download\\Orbitrap_mgf\\CID_mgf\\120917\\mix5_F002973.pep.txt";
		
		MascotDeltaData data = new MascotDeltaData(file);
//		data.phosDownTest("H:\\Validation\\phospho_download\\" +
//				"Literature\\peptide.txt", "5");
		data.testShuf();
	}

}
