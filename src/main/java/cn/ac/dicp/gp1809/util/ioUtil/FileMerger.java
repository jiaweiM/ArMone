/* 
 ******************************************************************************
 * File: FileMerger.java * * * Created on 06-18-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Merge files one by one into a single file.
 * 
 * <b>If the last file is end with "\n" or "\r\n", the next
 * file will be directly merged into. Otherwise, "\r\n" will be
 * printed first before the print of next file</b>
 * 
 * @author Xinning
 * @version 0.1, 06-18-2008, 15:14:42
 */
public class FileMerger {

	private FileMerger(){
		
	}
	
	/**
	 * Merge
	 * 
	 * @param out
	 * @param files
	 * @throws IOException 
	 */
	public static void merge(String out, File[] files) throws IOException{
		merge(new File(out),files);
	}
	
	/**
	 * Merge files
	 * 
	 * @param out
	 * @param files
	 * @throws IOException 
	 */
	public static void merge(File out, File[] files) throws IOException{
		
		out.getParentFile().mkdirs();
		
		if(out.exists()&&!out.canWrite()){
			throw new IllegalArgumentException("The output file can not be written");
		}
		
		if(files==null||files.length==0){
			throw new IllegalArgumentException("None input.");
		}
		
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out)));
		
		for(int i=0;i<files.length;i++){
			BufferedReader reader = new BufferedReader(new FileReader(files[i]));
			
			String line = null;
			while((line = reader.readLine())!= null){
				pw.println(line);
			}
			
			reader.close();
		}
		
		pw.close();
	}
	
	private static void usage(){
		System.out.println("Merger output input_dir");
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if(args.length!=2){
			usage();
		}
		else{
			
			final String extension = "mgf";
			
			File[] files = new File(args[1]).listFiles(new FileFilter(){

				@Override
                public boolean accept(File pathname) {
					if(pathname.getName().toLowerCase().endsWith(extension))
						return true;
	                return false;
                }
			});
			
			merge(new File(args[0]),files);
		}
		
		
	}
}
