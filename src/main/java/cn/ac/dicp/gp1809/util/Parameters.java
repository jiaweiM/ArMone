/*
 ******************************************************************************
 * File: Parameters.java * * * Created on 07-28-2008
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import cn.ac.dicp.gp1809.exceptions.MyIllegalArgumentException;
import cn.ac.dicp.gp1809.exceptions.MyNullPointerException;

/**
 * Parameters which can be used to save different type of parameters. This class
 * extends Java.Util.Properties and can be easily output as file or to stream.
 * 
 * @author Xinning
 * @version 0.1.1, 07-28-2008, 16:50:45
 */
public class Parameters extends Properties implements IParameters {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5784162042134970497L;

	/**
	 * 
	 */
	public Parameters() {
		super();
	}

	/**
	 * @param defaults
	 */
	public Parameters(Properties defaults) {
		super(defaults);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.IParameters#saveToFile(java.lang.String,
	 *      java.lang.String)
	 */
	public File saveToFile(String filename, String comments) throws IOException {

		if (filename == null)
			throw new MyNullPointerException("The input file name is Null");

		File file = new File(filename);
		if (file.isDirectory())
			throw new MyIllegalArgumentException("The input file is illegal");

		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
		        file)));

		this.store(writer, comments);

		if (writer != null)
			writer.close();

		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.IParameters#saveToXmlFile(java.lang.String,
	 *      java.lang.String)
	 */
	public File saveToXmlFile(String filename, String comments)
	        throws IOException {

		if (filename == null)
			throw new MyNullPointerException("The input file name is Null");

		File file = new File(filename);
		if (file.isDirectory())
			throw new MyIllegalArgumentException("The input file is illegal");

		BufferedOutputStream outstream = new BufferedOutputStream(
		        new FileOutputStream(file));

		this.storeToXML(outstream, comments);

		if (outstream != null)
			outstream.close();

		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.IParameters#loadFromFile(java.lang.String)
	 */
	public void loadFromFile(String filename) throws IOException {
		if (filename == null)
			throw new MyNullPointerException("The input file name is Null");

		File file = new File(filename);
		if (!file.exists() || file.isDirectory())
			throw new MyIllegalArgumentException("The input file is illegal");

		FileReader reader = new FileReader(filename);

		this.load(reader);

		if (reader != null)
			reader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.IParameters#loadFromXmlFile(java.lang.String)
	 */
	public void loadFromXmlFile(String xmlfilename) throws IOException {
		if (xmlfilename == null)
			throw new MyNullPointerException("The input file name is Null");

		File file = new File(xmlfilename);
		if (!file.exists() || file.isDirectory())
			throw new MyIllegalArgumentException("The input file is illegal");

		BufferedInputStream instream = new BufferedInputStream(
		        new FileInputStream(xmlfilename));

		this.loadFromXML(instream);

		if (instream != null)
			instream.close();
	}

	/**
	 * The map between the key and value will be cloned. That is, reset the map
	 * between the key and map in original Parameters will not influence the map
	 * in the cloned one. For Parameters, this can be considered as the deep
	 * clone as both the keys and the values in the map are String which can not
	 * be changeable.
	 */
	@Override
	public Parameters clone() {
		return (Parameters) super.clone();
	}
	
	public static void main(String[] args){
		Parameters param = new Parameters();
		param.setProperty("hello", "1");
		Parameters param2 = param.clone();
		param2.setProperty("hello", "2");
		
		System.out.println(param);
	}
}
