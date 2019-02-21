/*
 * *****************************************************************************
 * File: FastaReader.java * * * Created on 03-05-2008 
 * Copyright (c) 2008 Xinning Jiang vext@163.com 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.util.ioUtil.OutPrint;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

/**
 * This is reader for .fasta protein database file.
 * Protein sequence will be read one by one until the end.
 * 
 * @author Xinning
 * @version 0.1.1, 03-18-2008, 15:30:54
 */
public class FastaReader {
	private static DecimalFormat df = new DecimalFormat("##%");
	
	private ByteBuffer mappeddatabase;
	private BufferUtil database = null;
	private String name = null;
	
	private int length = 0;
	
	/**
	 * Creating a fasta reader using the mapped database
	 * @param _mappeddatabase
	 */
	public FastaReader(ByteBuffer _mappeddatabase){
		mappeddatabase = _mappeddatabase;
		database = new BufferUtil(mappeddatabase);
		length = mappeddatabase.capacity();
		
		this.preRead();
	}
	
	/**
	 * Creating a fasta reader for specific fasta database
	 * 
	 * @param fastaname
	 * @throws IOException
	 */
	public FastaReader(String fastaname) throws IOException{
		FileInputStream input = new FileInputStream(fastaname);
		this.length = input.available();
		FileChannel channel = input.getChannel();
		
		this.mappeddatabase = channel.map(FileChannel.MapMode.READ_ONLY, 0l, input.available());
		this.database = new BufferUtil(this.mappeddatabase);
		
		this.preRead();
	}
	
	public FastaReader(File file) throws IOException{
		FileInputStream input = new FileInputStream(file);
		this.length = input.available();
		FileChannel channel = input.getChannel();
		this.mappeddatabase = channel.map(FileChannel.MapMode.READ_ONLY, 0l, input.available());
		this.database = new BufferUtil(this.mappeddatabase);
		
		this.preRead();
	}
	
	/**
	 * @return the mapped database.
	 */
	public ByteBuffer getMappedDataBase(){
		return this.mappeddatabase;
	}
	
	private void preRead(){
		
		
		String line = null;

		while((line=database.readLine())!=null){
			
			if(line.trim().length()!=0 && line.charAt(0)=='>'){
				
				this.name = line.substring(1,line.length());
				return;
			}
		}
		
		throw new IllegalArgumentException("Invalid DataBase Input!");
	}
	
	/**
	 * Read protein sequence from fasta file one by one until the end.
	 * @return next protein sequence (if none, return null)
	 */
	public ProteinSequence nextSequence(){
		double pcount = 0.0;
		StringBuilder sb = new StringBuilder();
		ProteinSequence pseq = null;
		String line;
		int count = 0;
		
		while((line = database.readLine())!=null){
			if(line.length()!=0){
				if(line.charAt(0) =='>'){
					pseq = new ProteinSequence(this.name,sb.toString(),count++);
					this.name = line.substring(1,line.length());
					
					double percent = this.database.position()/length;
					if(percent>pcount){
						OutPrint.refreshPrint(df.format(percent));
						pcount+=0.01;
					}
					return pseq;
				}
				else{
					if(line.endsWith("\\*")){
						line = line.substring(0, line.length()-1);
					}
					sb.append(line);
				}
			}
		}
		
		if(this.name!=null){
			pseq = new ProteinSequence(this.name,sb.toString(),count++);
			this.name = null;
			return pseq;
		}
		else
			return null;
	}
	
	public static void count(String in, String out, String fasta) throws IOException{
		
		HashMap <String, ProteinSequence> map = new HashMap <String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference().substring(4, 15);
			map.put(ref, ps);
		}
		fr.close();
		
		PrintWriter pw = new PrintWriter(out);
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = reader.readLine();
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			if(map.containsKey(ss[1])){
				ProteinSequence proseq = map.get(ss[1]);
				pw.write(proseq.length()+"\n");
			}else{
				System.out.println(line);
			}
		}
		reader.close();
		pw.close();
	}
	
	/**
	 * Close
	 */
	public void close() {
		this.database.close();
		System.gc();
	}
	
	public void findProteinWithSequence(String sequence){
		ProteinSequence ps = null;
		while((ps=this.nextSequence())!=null){
			int begin = ps.indexOf(sequence);
			if(begin>=0){
				System.out.println(ps.getReference());
			}
		}
		this.close();
	}
	
	public void findProteinWithAA(char aa){

		ProteinSequence ps = null;
		while((ps=this.nextSequence())!=null){
			char[] aas = ps.getUniqueSequence().toCharArray();
			for(int i=0;i<aas.length;i++){
				if(aa==aas[i]){
					System.out.println(ps.getReference());
				}
			}
			
		}
		this.close();	
	}
	
	private void writeSwissprot(String out) throws IOException{
		PrintWriter writer = new PrintWriter(out);
		ProteinSequence ps = null;
		while((ps=this.nextSequence())!=null){
			String swiss = ps.getSWISS4Uniprot();
			if(swiss.length()>5){
				writer.write(swiss+"\n");
			}
		}
		this.close();
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
/*		FastaReader reader = new FastaReader("E:\\DataBase\\TAIR10_pep_20101214\\" +
				"Final_TAIR10_pep_20101214.fasta");
		
		ProteinSequence pseq;
		int count = 0;
		while((pseq = reader.nextSequence())!=null) {
			count ++;
			String ref = pseq.getReference();
			if(!ref.contains("| chr")){
				System.out.println(ref);
			}
		}
		
		System.out.println(count);
		
		reader.close();
*/		
//		FastaReader.count("H:\\byy\\ref.txt", "H:\\byy\\aanum.txt", 
//				"F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta");
		
		FastaReader reader = new FastaReader("F:\\DataBase\\uniprot\\uniprot-human-20131211_0.fasta");
		reader.writeSwissprot("F:\\DataBase\\uniprot\\human_swissprot.txt");
//		reader.findProteinWithSequence("ANLTCTLTGLR");
//		reader.findProteinWithSequence("RLGTLTCT");
//		reader.findProteinWithSequence("ANLTCTLTGLR");
//		reader.findProteinWithAA('B');
	}
}
