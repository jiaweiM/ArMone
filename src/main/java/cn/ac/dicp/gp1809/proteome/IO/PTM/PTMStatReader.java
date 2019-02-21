/* 
 ******************************************************************************
 * File: PTMStatReader.java * * * Created on 2011-3-7
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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.opencsv.CSVReader;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * @author ck
 *
 * @version 2011-3-7, 08:47:54
 */
public class PTMStatReader {

	private HashMap <String, HashSet<ModifSite>> siteMap;
	
	public PTMStatReader(String file) throws IOException{
		this(new File(file));
	}

	public PTMStatReader(File file) throws IOException{
		this.siteMap = new HashMap <String, HashSet<ModifSite>>();
		if(file.isDirectory()){
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
			File [] files = file.listFiles(filter);
			for(int i=0;i<files.length;i++){
				CSVReader reader = new CSVReader(new FileReader(files[i]));
				read(reader);
			}
		}else if(file.isFile()){
			CSVReader reader = new CSVReader(new FileReader(file));
			read(reader);
		}
	}
	
	private void read(CSVReader reader) throws IOException{
		String [] values = reader.readNext();
		while((values=reader.readNext())!=null && values.length>0){
			for(int i=7;i<values.length;i+=2){
				String ref = values[2];
				char s = values[i].charAt(0);
				ModSite ms = ModSite.newInstance_aa(s);
				int loc = Integer.parseInt(values[i].substring(1, values[i].length()));
				ModifSite site = new ModifSite(ms, loc, ' ');
				if(siteMap.containsKey(ref)){
					siteMap.get(ref).add(site);
				}else{
					HashSet <ModifSite> sset = new HashSet<ModifSite>();
					sset.add(site);
					siteMap.put(ref, sset);
				}
			}
		}
		reader.close();
	}
	
	public HashMap <String, HashSet<ModifSite>> getModMap(){
		return siteMap;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String file = "F:\\data\\ModDatabase\\Phos_ZMY\\D3\\total_phos_site.csv";
		PTMStatReader reader = new PTMStatReader(file);
		System.out.println(reader.getModMap().size());
	}

}
