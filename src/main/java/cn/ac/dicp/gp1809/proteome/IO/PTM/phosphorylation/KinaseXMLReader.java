/* 
 ******************************************************************************
 * File:KinaseXMLReader.java * * * Created on 2009-12-24
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;


/**
 * @author ck
 *
 * @version 2009-12-24, 18:12:45
 */
public class KinaseXMLReader {

	private XMLEventReader reader ;
	private int index = 0;
	
	public KinaseXMLReader(String file) throws FileNotFoundException, XMLStreamException{

		InputStream stream = new FileInputStream(new File(file));
		XMLInputFactory f = XMLInputFactory.newInstance();
		reader = f.createXMLEventReader(stream);
	}
	
	public KinaseXMLReader(String file, Boolean relative) throws FileNotFoundException, XMLStreamException{
		if(relative){
			
			ClassLoader classLoader = KinaseXMLReader.class.getClassLoader();
//			InputStream stream = ClassLoader.getSystemResourceAsStream(file);
			InputStream stream = classLoader.getSystemResourceAsStream(file);
//			InputStream stream = new FileInputStream(file);
			
			XMLInputFactory f = XMLInputFactory.newInstance();
			reader = f.createXMLEventReader(stream);
		}else{
			throw new FileNotFoundException();
		}
	}
	
	public Kinase getKinase() throws XMLStreamException{
		
		String description = "";
		String catalogue = "";
		Kinase kinase = null;
		
		while(reader.hasNext()){
			
			XMLEvent e = reader.nextEvent();
			if(e.isStartElement()){
				if(e.asStartElement().getName().getLocalPart().equals("motif")){
					
					index++;
					
					Iterator <Attribute> it = e.asStartElement().getAttributes();
					while(it.hasNext()){
						Attribute ab = it.next();
						if(ab.getName().getLocalPart().equals("description")){
							description = ab.getValue().trim();
						}
						if(ab.getName().getLocalPart().equals("catalogue")){
							catalogue = ab.getValue().trim();
						}
					}
					
					kinase = new Kinase(catalogue, description);
				}
				
				if(e.asStartElement().getName().getLocalPart().equals("patterns")){
					Iterator <Attribute> it = e.asStartElement().getAttributes();
					while(it.hasNext()){
						Attribute ab = it.next();
						if(ab.getName().getLocalPart().equals("pattern")){
							String pattern = ab.getValue().trim();
							kinase.addMotif(pattern);
						}
					}	
				}
			}
			else if(e.isEndElement()){
				if(e.asEndElement().getName().getLocalPart().equals("motif")){
					return kinase;
				}
			}
			
		}	
		return null;
	}
	
	public HashMap <String, Kinase> getKinaseNameMap() throws XMLStreamException{
		HashMap <String, Kinase> kinaseMap = new HashMap <String, Kinase> ();
		Kinase kinase = this.getKinase();
		while(kinase!=null){
			kinaseMap.put(kinase.getDescription(), kinase);
			kinase = this.getKinase();
		}
		return kinaseMap;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	
		String file = "E:\\public\\workspace3\\ArCommon\\src\\PhosphoMotif" +
				"\\PhosphoMotif.xml";
//		KinaseXMLReader reader = new KinaseXMLReader(file);
		KinaseXMLReader reader = new KinaseXMLReader("resources\\PhosphoMotif.xml", true);

		HashMap <String, Kinase> kmap = reader.getKinaseNameMap();
		System.out.println(kmap.size());
		Iterator <String> it = kmap.keySet().iterator();
		while(it.hasNext()){
			String name = it.next();
			Kinase kinase = kmap.get(name);
			String [] motifs = kinase.getMotifArrays();
			if(motifs!=null)
			for(int i=0;i<motifs.length;i++){
				System.out.println(motifs[i]);
			}
		}
	}

}
