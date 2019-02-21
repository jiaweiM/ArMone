/* 
 ******************************************************************************
 * File: PTMStatComparator.java * * * Created on 2011-3-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 *
 * @version 2011-3-7, 10:07:34
 */
public class PTMStatComparator {

	public PTMStatComparator(String s1, String s2) throws IOException{
		
		PTMStatReader r1 = new PTMStatReader(s1);
		PTMStatReader r2 = new PTMStatReader(s2);
		HashMap <String, HashSet<ModifSite>> m1 = r1.getModMap();
		HashMap <String, HashSet<ModifSite>> m2 = r2.getModMap();
		HashMap <String, HashSet<ModifSite>> totalMap = new HashMap <String, HashSet<ModifSite>>();
		
		System.out.println("\t"+m1.size());
		System.out.println("\t"+m2.size());
		
		Iterator <String> it1 = m1.keySet().iterator();
		while(it1.hasNext()){
			String ref = it1.next();
			if(totalMap.containsKey(ref)){
				totalMap.get(ref).addAll(m1.get(ref));
			}else{
				totalMap.put(ref, m1.get(ref));
			}
		}
		
		Iterator <String> it2 = m2.keySet().iterator();
		while(it2.hasNext()){
			String ref = it2.next();
			if(totalMap.containsKey(ref)){
				totalMap.get(ref).addAll(m2.get(ref));
			}else{
				totalMap.put(ref, m2.get(ref));
			}
		}
		
		System.out.println("\t"+totalMap.size());
		
		int count1 = 0;
		int count2 = 0;
		int countall = 0;
		Iterator <String> tit = totalMap.keySet().iterator();
		while(tit.hasNext()){
			String ref = tit.next();
			HashSet <ModifSite> mset = totalMap.get(ref);
			
			HashSet <ModifSite> ms1 = new HashSet <ModifSite>();
			HashSet <ModifSite> ms2 = new HashSet <ModifSite>();
			if(m1.containsKey(ref)){
				ms1 = m1.get(ref);
			}
				
			if(m2.containsKey(ref)){
				ms2 = m2.get(ref);
			}

			Iterator <ModifSite> mit = mset.iterator();
			while(mit.hasNext()){
				ModifSite ms = mit.next();
				countall++;
				if(ms2.contains(ms)){
					count2++;
				}
					
				if(ms1.contains(ms)){
					count1++;
				}
			}
		}
		
		System.out.println("1\t"+count1);
		System.out.println("2\t"+count2);
		System.out.println("Total\t"+countall);
		System.out.println("Overlap\t"+(count1+count2-countall));
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String s1 = "F:\\data\\ModDatabase\\Phos_ZMY\\D3\\phos_site";
		String s2 = "F:\\data\\ModDatabase\\Phos_ZMY\\general_ipi_3.80\\site";
		PTMStatComparator pc = new PTMStatComparator(s1, s2);
	}

}
