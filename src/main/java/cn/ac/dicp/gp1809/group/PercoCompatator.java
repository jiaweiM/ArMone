/* 
 ******************************************************************************
 * File:PercoCompatator.java * * * Created on 2012-9-11
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
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;

/**
 * @author ck
 *
 * @version 2012-9-11, 13:47:46
 */
public class PercoCompatator {
	
	private IPeptideListReader reader;
	private BufferedReader br;
	
	public PercoCompatator(String percolator, String clustering) throws FileDamageException, IOException{
		this.reader = new PeptideListReader(percolator);
		this.br = new BufferedReader(new FileReader(clustering));
	}
	
	public void compare() throws PeptideParsingException, IOException{
		
		int percount = 0;
		HashMap <String, String> seqmap = new HashMap <String, String>();
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			int scannum = pep.getScanNumBeg();
			short charge = pep.getCharge();
			String key = scannum+"";
			String seq = PeptideUtil.getUniqueSequence(pep.getSequence());
			seqmap.put(key, seq);
			percount++;
		}
		
		int clucount = 0;
		int count = 0;
		String line = null;
		while((line=br.readLine())!=null){
			String [] ss = line.split("\t");
			String [] s0 = ss[0].split("[ ,]");
//			System.out.println(s0[2]);
			int scannum = Integer.parseInt(s0[2]);
			int charge = Integer.parseInt(ss[1]);
			String seq = PeptideUtil.getUniqueSequence(ss[3]);
			clucount++;
			String key = scannum+"";
			if(seqmap.containsKey(key)){
				System.out.println(key+"\t"+seq+"\t"+seqmap.get(key));
				if(seqmap.get(key).equals(seq)){
					count++;
				}
			}
		}
		
		this.reader.close();
		this.br.close();
		
		System.out.println(percount+"\t"+clucount+"\t"+count);
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws PeptideParsingException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, PeptideParsingException {
		// TODO Auto-generated method stub

		String per = "H:\\Validation\\2D_phos_new\\200_per_F003066.per.dat.ppl";
		String clu = "H:\\Validation\\2D_phos_new\\clustering\\F003065_200.pep.txt";
		PercoCompatator com = new PercoCompatator(per, clu);
		com.compare();
	}

}
