/* 
 ******************************************************************************
 * File: GlycoSiteGetter.java * * * Created on 2010-12-15
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;

/**
 * @author ck
 *
 * @version 2010-12-15, 10:22:36
 */
public class GlycoSiteGetter {

	private final Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
	
	public GlycoSiteGetter(){
		
	}
	
	public String getSiteInfo(ProteinSequence pro, IPeptide pep){
		
		String ref = pro.getReference();
		String proSeq = pro.getUniqueSequence();
		String [] sites = getGlycoSite(proSeq, pep);
		
		if(sites==null || sites.length==0)
			return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(pep.getSequence()).append("\t");
		sb.append(pep.getCharge()).append("\t");
		sb.append(pep.getPrimaryScore()).append("\t");
		for(int i=0;i<sites.length;i++){
			sb.append(sites[i]).append(";");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("\t");
		sb.append(ref).append("\t");
		sb.append(pro.getIPI()).append("\t");
		sb.append(pro.getSWISS());
		
		return sb.toString();
	}
	
	public String [] getGlycoSite(String proSeq, IPeptide pep){
		return getGlycoSite(proSeq, pep.getSequence());
	}
	
	public String [] getGlycoSite(String proSeq, String pepSeq){
		
		HashMap <Integer, Integer> locMap = new HashMap<Integer, Integer>();
		char [] pepChars = pepSeq.toCharArray();
		int aaloc = -1;
		for(int i=1;i<pepChars.length;i++){
			if(pepChars[i]>='A' && pepChars[i]<='Z'){
				aaloc++;
			}
			locMap.put(i, aaloc);
		}
		
		String aaseq = PeptideUtil.getUniqueSequence(pepSeq);
		int idx = proSeq.indexOf(aaseq);
		if (idx == -1) {
			System.err.println("Peptide: \""
			        + pepSeq
			        + "\" is not found in protein.");
		}
		
		ArrayList <String> siteInfo = new ArrayList<String>();
		Matcher m = N_GLYCO.matcher(pepSeq);
		while(m.find()){
			int start = m.start();
			int site = locMap.get(start) + idx + 1;
			char aa = pepChars[start];
			siteInfo.add(String.valueOf(aa)+String.valueOf(site));
		}
		
		return siteInfo.toArray(new String[siteInfo.size()]);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GlycoSiteGetter getter = new GlycoSiteGetter();
		String s = "N@XSTTTTTTTTTTTN*ITTTTTTTTTTTTT";
		Matcher m = getter.N_GLYCO.matcher(s);
		while(m.find()){
			System.out.println(m.start());
		}
	}

}
