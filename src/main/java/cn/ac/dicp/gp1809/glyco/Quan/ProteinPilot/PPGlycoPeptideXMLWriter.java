/* 
 ******************************************************************************
 * File: PPGlycoPeptideXMLWriter.java * * * Created on 2013-6-7
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.ProteinPilot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

/**
 * @author ck
 *
 * @version 2013-6-7, 10:06:32
 */
public class PPGlycoPeptideXMLWriter {
	
	protected Document document;
	protected XMLWriter writer;
	protected Element root;
	
	public PPGlycoPeptideXMLWriter(String file) throws IOException{
		this(new File(file));
	}
	
	public PPGlycoPeptideXMLWriter(File file) throws IOException{
		this.document = DocumentHelper.createDocument();
		this.writer = new XMLWriter(new FileWriter(file));
//		this.file = file;
		this.initial();
	}

	/**
	 * 
	 */
	private void initial() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
