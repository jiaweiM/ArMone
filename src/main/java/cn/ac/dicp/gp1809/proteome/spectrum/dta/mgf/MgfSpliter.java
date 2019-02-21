/* 
 ******************************************************************************
 * File: MgfSpliter.java * * * Created on 2012-7-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfWriter.MgfParameters;

/**
 * @author ck
 *
 * @version 2012-7-13, 17:38:10
 */
public class MgfSpliter {
	
	private File file;
	private MgfReader reader;
	private String basename;
	
	public MgfSpliter(String file) throws DtaFileParsingException, FileNotFoundException{
		this.reader = new MgfReader(file);
		this.file = (new File(file)).getParentFile();
		this.basename = reader.getFileName();
	}
	
	public MgfSpliter(File file) throws DtaFileParsingException, FileNotFoundException{
		this.reader = new MgfReader(file);
		this.file = file.getParentFile();
		this.basename = reader.getFileName();
	}
	
	public void split() throws DtaWritingException{
		
		MgfWriter writer = new MgfWriter("", new MgfParameters());
	}

	private static void split(String in, String out) throws IOException{
		
		int filecount = 1;
		int scancount = 0;
		
		File filein = new File(in);
		String name = filein.getName().substring(0, filein.getName().length()-4);
		PrintWriter writer = new PrintWriter(out+"\\"+name+"."+filecount+".mgf");

		BufferedReader reader = new BufferedReader(new FileReader(filein));
		String line = null;
		while((line=reader.readLine())!=null){
			writer.write(line+"\n");
			if(line.startsWith("END")){
				scancount++;
				if(scancount%60000==0){
					writer.close();
					filecount++;
					writer = new PrintWriter(out+"\\"+name+"."+filecount+".mgf");
				}
			}
		}
		reader.close();
		writer.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MgfSpliter.split("H:\\OGLYCAN2\\20141024_15glyco\\2D_elastase\\2D_elastase.oglycan.mgf", 
				"H:\\OGLYCAN2\\20141024_15glyco\\2D_elastase");

	}

}
