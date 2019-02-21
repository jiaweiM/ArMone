/* 
 ******************************************************************************
 * File:RscGetter.java * * * Created on 2010-9-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.rsc;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.NoredundantReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.UniPep;

/**
 * @author ck
 *
 * @version 2010-9-10, 10:46:18
 */
public class RscGetter {

	private NoredundantReader reader;
	
	private DecimalFormat df4 = new DecimalFormat("#.####");
	
	public RscGetter(String file) throws IllegalFormaterException, IOException{
		this.reader = new NoredundantReader(file);
	}
	
	public RscGetter(File file) throws IllegalFormaterException, IOException{
		this.reader = new NoredundantReader(file);
	}
	
	public HashMap <String, Double> getRscs() throws ProteinIOException{
		Protein pro = null;
		HashMap <String, Double> refmap = new HashMap <String, Double> ();
		while((pro = reader.getProtein())!=null){
			UniPep [] upeps = pro.getAllUniPeps();
			IPeptide [] peps = pro.getAllPeptides();
/*			
			for(int i=0;i<upeps.length;i++){
				int count = upeps[i].getPeptideCount();
				IPeptide dpep = upeps[i].getDelegratePeptide();
			}
*/			
//			System.out.println(pro.getRefwithSmallestMw().getName()+"\t"+
//					df4.format((double)peps.length/upeps.length));
			refmap.put(pro.getRefwithSmallestMw().getName(), 
					Double.parseDouble(df4.format((double)peps.length/upeps.length)));
		}
		return refmap;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
