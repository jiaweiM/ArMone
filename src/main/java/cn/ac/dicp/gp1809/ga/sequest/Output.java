/*
 * *****************************************************************************
 * File: Output.java * * * Created on 04-10-2008
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
package cn.ac.dicp.gp1809.ga.sequest;

import java.io.*;

import cn.ac.dicp.gp1809.ga.Chromosome;
import java.text.*;
import java.util.Locale;

/**
 * Output the SFOER optimized criteria
 * 
 * @author Xinning
 * @version 0.1, 04-10-2008, 14:13:17
 */
class Output {
	private static DecimalFormat df;
	
	
	static {
		Locale def = Locale.getDefault();
		Locale.setDefault(Locale.US);
		
		df = new DecimalFormat("0.###");
		
		Locale.setDefault(def);
	}
	
	private PrintWriter out;
	
	Output(String filename,SequestConfiguration config,String[] inputfiles, short charge, short ntt){
		try {
			out = new PrintWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder(500);
		StringBuilder title = new StringBuilder(30);
		
		out.println("SFOER (Sequest Filter Optimizer by Genetic Algorithm) By Xinning Jiang.");
		out.println();
		
		sb.append("Input File(s):\r\n");
		for(int i=0;i<inputfiles.length;i++){
			sb.append(inputfiles[i]);
			sb.append("\r\n");
		}
		
		sb.append("\r\nParameters:\r\n");
		SequestValueLimit vlimit = config.getSequestValueLimit();
		sb.append("Charge: ").append(charge).append("\r\n");
		sb.append("NTT: ").append(ntt).append("\r\n");
		if(config.isXcorrFilter()){
			sb.append("Xcorr: ").append(vlimit.getXcorrLowerlimit()).append(" - ").append(vlimit.getXcorrUpperlimit()).append("\r\n");
			title.append("Xcorr\t");
		}
	
		if(config.isDeltaCnFilter()){
			sb.append("DeltaCn: ").append(vlimit.getDeltaCnLowerlimit()).append(" - ").append(vlimit.getDeltaCnUpperlimit()).append("\r\n");
			title.append("DeltaCn\t");
		}
		
		if(config.isSpFilter()){
			sb.append("Sp: ").append(vlimit.getSpLowerlimit()).append(" - ").append(vlimit.getSpUpperlimit()).append("\r\n");
			title.append("Sp\t");
		}
		
		if(config.isRspFilter()){
			sb.append("Rsp: ").append(vlimit.getRspLowerlimit()).append(" - ").append(vlimit.getRspUpperlimit()).append("\r\n");
			title.append("Rsp\t");
		}
		
		if(config.isIonFilter()){
			sb.append("Ions: ").append(vlimit.getIonPercentLowlimit()).append(" - ").append(vlimit.getIonPercentUpperlimit()).append("\r\n");
			title.append("Ions\t");
		}
		
		sb.append("\r\n");
		
		sb.append("Population Size: ").append(config.getPopulationSize()).append(";\t");
		sb.append("Max Generations: ").append(config.getGenerationSize()).append(";\r\n");
		sb.append("Cross Probability: ").append(config.getCrossRate()).append(";\t");
		sb.append("Mutation Probability: ").append(config.getMutateRate()).append(";\r\n");
		sb.append("Max FDR: ").append(config.getMaxFPR()).append(";\r\n\r\n\r\n\r\n");

		sb.append("Generation\tPeptides\t").append(title).append("\r\n");
		out.print(sb);
	}
	
	
	void print(Chromosome bestchrom, double[] fitnesses,int generation){
		int len = fitnesses.length;
		double total=0d;
		for(int i=0;i<len;i++){
			total+=fitnesses[i];
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(generation);
		sb.append("\t\t");
//		sb.append((int)total/len);
//		sb.append('\t');
		sb.append((int)bestchrom.getFitnessValue());
		sb.append("\t\t");
		
		double[] paramter = bestchrom.values();
		len = paramter.length-1;
		for(int i=0;i<len;i++){
			double value = paramter[i];
			if(value<0)
				continue;
			
			sb.append(df.format(value));
			sb.append('\t');
		}
		double value = paramter[len];
		if(value<5000){
			sb.append((int)value);
		}
		
		out.println(sb);
	}
	
	void close(){
		out.close();
	}
}
