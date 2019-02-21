/* 
 ******************************************************************************
 * File: TestSQTReader.java * * * Created on 04-20-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sqt;

import java.io.IOException;

/**
 * The tester for SQTReader
 * 
 * @author Xinning
 * @version 0.1, 04-20-2009, 18:56:59
 */
public class TestSQTReader {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQTReadingException 
	 */
	public static void main(String[] args) throws IOException, SQTReadingException {
		SQTReader reader = new SQTReader("d:\\try_sequest.sqt");
		IPepMatches matches;
		while(( matches = reader.getNextMatch())!=null) {
			System.out.println(matches);
		}
		
		reader.close();
	}

}
