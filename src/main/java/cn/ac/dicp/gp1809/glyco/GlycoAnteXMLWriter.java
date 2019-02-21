/* 
 ******************************************************************************
 * File: GlycoAnteXMLWriter.java * * * Created on 2011-6-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

/**
 * @author ck
 *
 * @version 2011-6-2, 13:01:48
 */
public class GlycoAnteXMLWriter {
	
	private Document document;
	private XMLWriter writer;
	private Element root;
	private Element eNAnte;
	private Element eOAnte;
	
	private File file;

	public GlycoAnteXMLWriter(String file) throws IOException{
		this(new File(file));
	}
	
	public GlycoAnteXMLWriter(File file) throws IOException{
		this.file = file;
		this.document = DocumentHelper.createDocument();
		this.writer = new XMLWriter(new FileWriter(file));
		this.root = DocumentFactory.getInstance().createElement("Glycan_Antenna");
		
		this.eNAnte = DocumentFactory.getInstance().createElement("N_Glycan_Antenna");
		this.eOAnte = DocumentFactory.getInstance().createElement("O_Glycan_Antenna");
		
		this.document.setRootElement(root);
		root.add(eNAnte);
		root.add(eOAnte);
	}
	
	public void addNAntenna(GlycoAntenna ante){
		Element eAnte = DocumentFactory.getInstance().createElement("Antenna");
		String comp = ante.getCompStr();
		double mono = ante.getMonoMass();
		
		eAnte.addAttribute("Composition", comp);
		eAnte.addAttribute("Mono_Mass", String.valueOf(mono));
		eNAnte.add(eAnte);
	}
	
	public void addOAntenna(GlycoAntenna ante){
		Element eAnte = DocumentFactory.getInstance().createElement("Antenna");
		String comp = ante.getCompStr();
		double mono = ante.getMonoMass();
		
		eAnte.addAttribute("Composition", comp);
		eAnte.addAttribute("Mono_Mass", String.valueOf(mono));
		eOAnte.add(eAnte);
	}
	
	public void write() throws IOException{
		writer.write(document);
	}
	
	public void close() throws IOException{					
		writer.close();
	}
	
	public void ini() throws IOException{
		
		String [] sss = new String []{
				"0_1_1_0_0", "0_1_1_1_0", "0_0_2_0_0", "0_0_2_1_0", 
				"1_1_1_0_0", "1_0_2_0_0", "1_1_1_1_0", "2_1_1_0_0",
				"1_2_2_1_0", "0_3_3_0_0"
		};
		
		for(int i=0;i<sss.length;i++){
			GlycoAntenna ante = new GlycoAntenna(sss[i]);
			this.addNAntenna(ante);
		}
		
		String [] sss2 = new String []{
				"0_1_1_0_0"
		};
		
		for(int i=0;i<sss2.length;i++){
			GlycoAntenna ante = new GlycoAntenna(sss2[i]);
			this.addOAntenna(ante);
		}
		
		this.write();
		this.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GlycoAnteXMLWriter writer = new GlycoAnteXMLWriter("E:\\public\\workspace3\\N_Glycan_Antenna_1.xml");
		writer.ini();
	}

}
