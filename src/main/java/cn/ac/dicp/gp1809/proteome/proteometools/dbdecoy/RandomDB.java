/*
 * *****************************************************************************
 * File: RandomDB.java * * * Created on 03-24-2008
 * 
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * Creating a decoy database by randomly assign the same probability for all the amino acid.
 * 
 * @author Xinning
 * @version 0.1, 03-24-2008, 15:49:48
 */
public class RandomDB implements DecoyFasta{
	private static final byte TURN = '\n';
	private static final byte ENTER = '\r';
	
	private static final char[] AMINOACID = new char[]{'A','C','D','E','F','G','H',
		'I','L','M','N','O','Q','S','T','V','W','Y','P'};
	
	private static Random random = new Random();
	
	private String inputfilename,outputfilename;
	private DBDecoy randomer;
	
	public RandomDB(String inputfilename,String outputfilename,DBDecoy randomer){
		this.inputfilename = inputfilename;
		this.outputfilename = outputfilename;
		this.randomer = randomer;
	}
	
	public void makeDecoy()throws IOException{
		byte temp =0;
		int count = 0;
		
		
		File reversefile = new File(outputfilename);
		if(reversefile.exists())
		reversefile.delete();
		
		RandomAccessFile raf = new RandomAccessFile(inputfilename,"r");
		ByteBuffer buffer = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
		BufferUtil bfutil = new BufferUtil(buffer);
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfilename)));
		StringBuilder sb = new StringBuilder();
		
		randomer.jProgressBar.setMaximum(buffer.capacity());
		
		while(buffer.position()<buffer.capacity()){
			temp = buffer.get();
			
			if(temp == '>'){
				count++;
				out.print(sb.toString());
				sb.delete(0,sb.capacity());
				
				out.print(new String(">REV_RAN_").concat(bfutil.readLine()).concat("\r\n"));
			}
			else{
				if(temp == ENTER||temp == TURN){
					sb.append((char)temp);
				}
				else if(temp == 'K'||temp == 'R'){
					sb.append((char)temp);
					while(buffer.position()<buffer.capacity()){
						temp = buffer.get();

						if(temp == 'P'){
							sb.append((char)temp);
							break;
						}
						else if(temp == 'K'||temp == 'R'||temp == TURN||temp == ENTER){
							sb.append((char)temp);
						}
						else if(temp == '>'){
							count++;
							out.print(sb.toString());
							sb.delete(0,sb.capacity());
							
							out.print(">REV_RAN_".concat(bfutil.readLine()).concat("\n"));
							
							break;
						}
						else{
							sb.append(AMINOACID[random.nextInt(17)]);
							break;
						}
					}
				}
				else{
					sb.append(AMINOACID[random.nextInt(18)]);
				}
			}
			
			randomer.jProgressBar.setValue(buffer.position());
		}
		
		out.print(sb.toString());
		
		randomer.jProgressBar.setValue(buffer.capacity());
		randomer.jLabel.setText(count+" entries randomlized!");
		
		raf.close();
		out.close();
	}
}
