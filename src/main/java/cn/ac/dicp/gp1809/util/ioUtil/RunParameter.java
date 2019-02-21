/* 
 ******************************************************************************
 * File: RunParameter.java * * * Created on 2010-12-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.ioUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

/**
 * @author ck
 *
 * @version 2010-12-2, 13:32:41
 */
public class RunParameter {

	private static final String dir = "Run_Parameter.xml";
	
	private Document document;
	private Element root;
	private XMLWriter writer;
	
	public RunParameter(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
