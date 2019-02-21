/* 
 ******************************************************************************
 * File: GlycoAnteXMLReader.java * * * Created on 2011-6-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author ck
 *
 * @version 2011-6-2, 13:01:26
 */
public class GlycoAnteXMLReader {

	private Element root;
	private Iterator <Element> anteIt;
	private File file;
	
	private GlycoAntenna [] antes;
	
	public GlycoAnteXMLReader(String file) throws DocumentException{
		this(new File(file));
	}
	
	public GlycoAnteXMLReader(File file) throws DocumentException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		this.root = document.getRootElement();		
		this.anteIt = root.elementIterator("N_Glycan_Antenna");
		this.file = file;
		this.getProfileData();
	}
	
	public void getProfileData(){
		
		ArrayList <GlycoAntenna> antelist = new ArrayList <GlycoAntenna>();
		
		Iterator <Element> labelIt = root.elementIterator();
		while(labelIt.hasNext()){
			Element eAnte = labelIt.next();
			String compstr = eAnte.attributeValue("Composition");
			double monoMass = Double.parseDouble(eAnte.attributeValue("Mono_Mass"));
			GlycoAntenna ante = new GlycoAntenna(compstr, monoMass);
			antelist.add(ante);
		}
		
		this.antes = antelist.toArray(new GlycoAntenna[antelist.size()]);
		Arrays.sort(antes);
	}
	
	public GlycoAntenna [] getAntes(){
		return this.antes;
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws DocumentException {
		// TODO Auto-generated method stub

		GlycoAnteXMLReader reader = new GlycoAnteXMLReader("E:\\public\\workspace3\\N_Glycan_Antenna.xml");
		System.out.println(reader.getAntes().length);
	}

}
