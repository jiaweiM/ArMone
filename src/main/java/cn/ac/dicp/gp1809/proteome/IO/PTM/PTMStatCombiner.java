/* 
 ******************************************************************************
 * File: PTMStatCombiner.java * * * Created on 2011-3-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.opencsv.CSVWriter;

/**
 * @author ck
 *
 * @version 2011-3-7, 09:17:35
 */
public class PTMStatCombiner {
	
	private File [] files;
	private CSVWriter writer;
	
	public PTMStatCombiner(String dir, String out) throws IOException{
		FileFilter filter = new FileFilter(){
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if(pathname.getName().endsWith("csv")){
					return true;
				}
				return false;
			}
		};
		
		this.files = new File(dir).listFiles(filter);
		this.writer = new CSVWriter(new FileWriter(out));
	}
	
	public PTMStatCombiner(String [] filename, String out) throws IOException{
		this.files = new File[filename.length];
		for(int i=0;i<filename.length;i++){
			this.files[i] = new File(filename[i]);
		}
		this.writer = new CSVWriter(new FileWriter(out));
	}
	
	public PTMStatCombiner(File [] files, String out) throws IOException{
		this.files = files;
		this.writer = new CSVWriter(new FileWriter(out));
	}
	
	public void combine() throws IOException{
		HashMap <String, HashSet<ModifSite>> totalMap = new HashMap <String, HashSet<ModifSite>>();
		for(int i=0;i<files.length;i++){
			PTMStatReader reader = new PTMStatReader(files[i]);
			HashMap <String, HashSet<ModifSite>> modMap = reader.getModMap();
			Iterator <String> it = modMap.keySet().iterator();
			while(it.hasNext()){
				String ref = it.next();
				if(totalMap.containsKey(ref)){
					totalMap.get(ref).addAll(modMap.get(ref));
				}else{
					totalMap.put(ref, modMap.get(ref));
				}
			}
		}
		
		Iterator <String> it = totalMap.keySet().iterator();
		while(it.hasNext()){
			String ref = it.next();
			HashSet <ModifSite> sset = totalMap.get(ref);
			ModifSite [] sites = sset.toArray(new ModifSite[sset.size()]);
			Arrays.sort(sites);

			writer.writeNext(new String []{ref});
			String [] ss = new String [sites.length];
			for(int i=0;i<ss.length;i++){
				ss[i] = sites[i].modifiedAt().getModifAt()+""+sites[i].modifLocation();
			}
			writer.writeNext(ss);
		}
		writer.close();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String dir = "F:\\data\\ModDatabase\\Phos_ZMY\\general_ipi_3.80\\site";
		String out = "F:\\data\\ModDatabase\\Phos_ZMY\\general_ipi_3.80\\gerenal_phos_site.csv";
		PTMStatCombiner com = new PTMStatCombiner(dir, out);
		com.combine();
	}

}
