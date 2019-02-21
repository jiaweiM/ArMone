/* 
 ******************************************************************************
 * File:CounterReader.java * * * Created on 2010-6-10
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.spcounter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author ck
 *
 * @version 2010-6-10, 14:01:33
 */
public class CounterReader {

	private BufferedReader reader;
	
	public CounterReader(String file) throws IOException{
		this.reader = new BufferedReader(new FileReader(file));
	}
	
	public void read() throws IOException{
		String line;
		reader.readLine();
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			double d1 = Double.parseDouble(ss[1]);
			double d2 = Double.parseDouble(ss[4]);
			double d3 = 0;
			if(d1==0||d2==0||d1==0.1||d2==0.1)
				d3=0;
			else
				d3 = d2/d1;
			System.out.println(d3);
		}
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String file = "F:\\data\\SIn\\Armone_SIn\\counter\\Compare.cmp";
		CounterReader reader = new CounterReader(file);
		reader.read();
	}

}
