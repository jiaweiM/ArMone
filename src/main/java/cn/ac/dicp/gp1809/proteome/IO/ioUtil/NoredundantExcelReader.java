/* 
 ******************************************************************************
 * File: NoredundantExcelReader.java * * * Created on 2011-10-19
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.JXLException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProteinIOException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IProteinFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.ProteinFormatFactory;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2011-10-19, 15:38:37
 */
public class NoredundantExcelReader implements IProteinReader {

	/*
	 * After a protein group is read, the following protein will be the allene
	 * protein until another protein is read.
	 */
	private boolean allene = false;

	/*
	 * The idx String for a protein group. In a protein group, all sub proteins
	 * are with the same index, but with different extensions. e.g. ProteinGourp
	 * with index of $115, and the index of proteins in this ProteinGroup will
	 * be $115a $115b ...
	 */
	private String idx = null;
	private String [] preline;
	private ExcelReader reader;
	private IProteinFormat proformat;
	private int refColumnNum;
	private int pepColumnNum;
	private PeptideType type;
	
	public NoredundantExcelReader(String file) throws IOException, JXLException, 
		IllegalFormaterException, NullPointerException{
		
		this(new File(file));
	}
	
	public NoredundantExcelReader(File file) throws IOException, JXLException, 
		IllegalFormaterException, NullPointerException{
		
		this.reader = new ExcelReader(file);

		String [] reftitle = reader.readLine();
		String [] peptitle = reader.readLine();

		this.refColumnNum = reftitle.length;
		this.pepColumnNum = peptitle.length;
		this.type = this.getPeptideType(peptitle[0]);

		peptitle[0] = "";
		this.proformat = ProteinFormatFactory.createFormat(reftitle, peptitle,
		        type);

		preline = reader.readLine();

	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IProteinReader#getProtein()
	 */
	@Override
	public Protein getProtein() throws ProteinIOException {
		// TODO Auto-generated method stub
		
		ArrayList <String []> reflist = new ArrayList <String []>();
		ArrayList <String []> peplist = new ArrayList <String []>();
		
		if(preline.length!=refColumnNum)
			return null;
		else
			reflist.add(preline);
		
		boolean next = false;
		String [] line = null;
		
		while((line=reader.readLine())!=null){

			int length = line.length;
			
			if(length==pepColumnNum){
				
				peplist.add(line);
				next = true;
				
			}else if(length==refColumnNum){
				
				if(next){
					
					this.preline = line;
					
					String [][] refs = reflist.toArray(new String [reflist.size()][]);
					String [][] peps = peplist.toArray(new String [peplist.size()][]);
					
					Protein pro = this.proformat.parseProtein(refs, peps);
					
					return pro;
					
				}else{
					reflist.add(line);
				}
			}else{
				
				this.preline = line;
				if(reflist.size()>0 && peplist.size()>0){
					
					String [][] refs = reflist.toArray(new String [reflist.size()][]);
					String [][] peps = peplist.toArray(new String [peplist.size()][]);
					
					Protein pro = this.proformat.parseProtein(refs, peps);
					
					return pro;
				}
				return null;
			}
		}
		return null;
	}
	
	private PeptideType getPeptideType(String type) {
		
		type = type.substring(1, type.length()-1);
		return PeptideType.typeOfFormat(type);
	}
	
	/**
	 * The protein format of this noredundant file
	 * 
	 * @return
	 */
	public IProteinFormat getProteinFormat(){
		return this.proformat;
	}
	
	/**
	 * The peptide type
	 * 
	 * @return
	 */
	public PeptideType getPeptideType() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IProteinReader#close()
	 */
	@Override
	public void close() {
		this.reader.close();
	}
	
	/**
	 * @param args
	 * @throws JXLException 
	 * @throws IOException 
	 * @throws NullPointerException 
	 * @throws IllegalFormaterException 
	 * @throws ProteinIOException 
	 */
	public static void main(String[] args) throws IllegalFormaterException, NullPointerException, IOException, JXLException, ProteinIOException {
		// TODO Auto-generated method stub

		String file = "D:\\My Documents\\gen.xls";
		NoredundantExcelReader reader = new NoredundantExcelReader(file);
		Protein pro;
		while((pro=reader.getProtein())!=null){
			System.out.println(pro);
		}
	}

}
