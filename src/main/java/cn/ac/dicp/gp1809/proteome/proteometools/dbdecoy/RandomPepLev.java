/*
 * *****************************************************************************
 * File: RandomPepLev.java * * * Created on 03-24-2008
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
 * Protein sequence is first digest in silico by trypsin, then aminacids except the 
 * C and N terminal aminoacids are disordered randomly to form a new peptide.
 * 
 * @author Xinning
 * @version 0.1, 03-24-2008, 15:51:20
 */
public class RandomPepLev implements DecoyFasta{
	private static final byte TURN = '\n';
	private static final byte ENTER = '\r';
	
	private static Random random = new Random();
	
	private String inputfilename,outputfilename;
	private DBDecoy randomer;
	
	public RandomPepLev(String inputfilename,String outputfilename,DBDecoy randomer){
		this.inputfilename = inputfilename;
		this.outputfilename = outputfilename;
		this.randomer = randomer;
	}
	
	public void makeDecoy()throws IOException{
		byte temp =0;
		int count = 1;
		
		
		File reversefile = new File(outputfilename);
		if(reversefile.exists())
		reversefile.delete();
		
		RandomAccessFile raf = new RandomAccessFile(inputfilename,"r");
		ByteBuffer buffer = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
		BufferUtil bfutil = new BufferUtil(buffer);
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputfilename)));
		StringBuilder sb = new StringBuilder();
		StringBuilder peptide = new StringBuilder();
		
		randomer.jProgressBar.setMaximum(buffer.capacity());
		
		out.print(bfutil.readLine().replaceFirst(">",">REV_RANPEP_").concat("\r\n"));
		
		while(buffer.position()<buffer.capacity()){
			temp = buffer.get();
			
			if(temp == '>'){
				count++;
				addRandomPeptide(sb,peptide);
				out.print(split(sb));
				sb.delete(0,sb.capacity());
				out.print(new String(">REV_RANPEP_").concat(bfutil.readLine()).concat("\r\n"));
			}
			else{
				if(temp == ENTER||temp == TURN){
					continue;
				}
				else if(temp == 'K'||temp == 'R'){
Line1:				while(buffer.position()<buffer.capacity()){
						byte intemp = buffer.get();

						if(intemp == 'P'){
							if(temp =='K')
								peptide.append('U');//U==KP
							else
								peptide.append('J');//J==RP
							
							break;
						}
						else if(intemp == TURN||intemp == ENTER){
							continue;
						}
						else if(intemp == '>'){
							count++;
							peptide.append((char)temp);
							addRandomPeptide(sb,peptide);
							out.print(split(sb));
							sb.delete(0,sb.capacity());
							
							out.print(">REV_RANPEP_".concat(bfutil.readLine()).concat("\n"));
							
							break;
						}
						else if(intemp == 'K'||intemp == 'R'){
							peptide.append((char)temp);
							addRandomPeptide(sb,peptide);
							
							temp = intemp;
							continue Line1;
						}
						else{
							peptide.append((char)temp);
							addRandomPeptide(sb,peptide);
							
							
							peptide.append((char)intemp);
							break;
						}
					}
				}
				else{
					peptide.append((char)temp);
				}
			}
			
			randomer.jProgressBar.setValue(buffer.position());
		}
		
		addRandomPeptide(sb,peptide);
		out.print(split(sb));
		
		randomer.jProgressBar.setValue(buffer.capacity());
		randomer.jLabel.setText(count+" entries randomlized!");
		
		raf.close();
		out.close();
	}

	
	private static void addRandomPeptide(StringBuilder sb,StringBuilder peptide){
		char tempchararray[];
		int temp=0,point;
		int count=0;
		
		if(peptide.length()==1){
			char tempchar = peptide.charAt(0);
			
			if(tempchar=='U')
				sb.append("KP");
			else if(tempchar == 'J')
				sb.append("RP");
			else
				sb.append(tempchar);
		}
		else{
			tempchararray = peptide.toString().toCharArray();
			int length = tempchararray.length;
			int[] index = new int[length];
			for(int i=0;i<length;i++){
				index[i] = i;
			}

			char tempchar = tempchararray[tempchararray.length-1];
			if(tempchar!='K'&&tempchar!='R'){//end peptide;
				while(tempchararray[index[0]=random.nextInt(index.length)]=='P'){//P mustnt in the begin of the peptide
					if(count++>10)
						break;
				}
				index[index[0]]=0;
				
				for(int i=1;i<length;i++){
					point = random.nextInt(length-i);
					temp = index[i];
					index[i] = index[point+i];
					index[point+i] = temp;
				}
				
				for(int i=0;i<index.length;i++){
					tempchar = tempchararray[index[i]];
					if(tempchar=='U')
						sb.append("KP");
					else if(tempchar == 'J')
						sb.append("RP");
					else
						sb.append(tempchar);
				}
			}
			else{
				while(tempchararray[index[0]=random.nextInt(index.length-1)]=='P'){//P mustnt in the begin of the peptide
					if(count++>10)
						break;
				}
				index[index[0]]=0;
				
				for(int i=1;i<length-1;i++){
					point = random.nextInt(length-i-1);
					temp = index[i];
					index[i] = index[point+i];
					index[point+i] = temp;
				}
				
				for(int i=0;i<index.length;i++){
					tempchar = tempchararray[index[i]];
					if(tempchar=='U')
						sb.append("KP");
					else if(tempchar == 'J')
						sb.append("RP");
					else
						sb.append(tempchar);
				}
			}
		}
		
		peptide.delete(0,peptide.length());
	}
	
	private static String split(StringBuilder stringbuilder){
		int line = stringbuilder.length()/80;
		int mode = stringbuilder.length()%80;
		
		for (int i = 0;i<line;i++)
		{   
			stringbuilder.insert(i*81+80,"\n");
		}
		if(mode!=0){//if it has just 80n aa,'\n' is not needed
			stringbuilder.append('\n');
		}
		
		return stringbuilder.toString();
	}
}
