/* 
 ******************************************************************************
 * File: Parameter.java * * * Created on 02-11-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import java.io.*;

/**
 * Parameters for SFOER
 * 
 * @author Xinning
 * @version 0.1.1, 09-14-2010, 14:51:16
 */
class Parameter {
	private String inifile = "SFOER.ini";
	
	int populationsize = 100;
	int maxgeneration = 500;
	/**
	 * If these is no improvement in the fitness, the optimization will be terminated. 
	 */
	int no_improv_generations = 100;
	float populationcross = 0.2f;
	float populationmutate = 0.01f;
	double maxfalseratio = 0.01;
	double xcorr1min=0.3,xcorr1max=5,xcorr2min=0.5,xcorr2max=8,xcorr3min=1,xcorr3max=8,
			dcn1min=0.03,dcn1max=0.8,dcn2min=0.03,dcn2max=0.8,dcn3min=0.03,dcn3max=0.8,
			sp1min=20,sp2min=20,sp3min=20,sp1max=1000,sp2max=1000,sp3max=1000,
			ion1min=0,ion1max=1,ion2min=0,ion2max=1,ion3min=0,ion3max=1,
			rspmin=1,rspmax=250, dmsmin = 0, dmsmax = 2000;
	
	//length of gene;
	short xcorrlen=9,dcnlen=9,splen=12,rsplen=8,ionlen=10, dmslen= 12;
	
	boolean read(){
		String line;
		String[] temp;
		try {
			BufferedReader bf = new BufferedReader(new FileReader(inifile));
			while((line = bf.readLine())!=null){
				if(line.equals("[Xcorr]")){
					line = bf.readLine();
					temp = line.split(":");
					xcorr1min = Double.parseDouble(temp[1].trim());
					xcorr1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					xcorr2min = Double.parseDouble(temp[1].trim());
					xcorr2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					xcorr3min = Double.parseDouble(temp[1].trim());
					xcorr3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Delta Cn]")){
					line = bf.readLine();
					temp = line.split(":");
					dcn1min = Double.parseDouble(temp[1].trim());
					dcn1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					dcn2min = Double.parseDouble(temp[1].trim());
					dcn2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					dcn3min = Double.parseDouble(temp[1].trim());
					dcn3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Sp]")){
					line = bf.readLine();
					temp = line.split(":");
					sp1min = Double.parseDouble(temp[1].trim());
					sp1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					sp2min = Double.parseDouble(temp[1].trim());
					sp2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					sp3min = Double.parseDouble(temp[1].trim());
					sp3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Rsp]")){
					line = bf.readLine();
					temp = line.split(":");
					rspmin = Double.parseDouble(temp[0].trim());
					rspmax = Double.parseDouble(temp[1].trim());
				}
				else if(line.equals("[Ions]")){
					line = bf.readLine();
					temp = line.split(":");
					ion1min = Double.parseDouble(temp[1].trim());
					ion1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					ion2min = Double.parseDouble(temp[1].trim());
					ion2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					ion3min = Double.parseDouble(temp[1].trim());
					ion3max = Double.parseDouble(temp[2].trim());
				}
				
				else if(line.equals("[DeltaMZ]")){
					line = bf.readLine();
					temp = line.split(":");
					this.dmsmin = Double.parseDouble(temp[0].trim());
					this.dmsmax = Double.parseDouble(temp[1].trim());
				}
				else if(line.equals("[Max FP]")){
					line = bf.readLine();
					maxfalseratio = Double.parseDouble(line.trim());
				}
				else if(line.startsWith("Population Size")){
					temp = line.split(":");
					populationsize = Integer.parseInt(temp[1].trim());
				}
				else if(line.startsWith("Max Generation")){
					temp = line.split(":");
					maxgeneration = Integer.parseInt(temp[1].trim());
				}
				else if(line.startsWith("Cross Probility")){
					temp = line.split(":");
					populationcross = Float.parseFloat(temp[1].trim());
				}
				else if(line.startsWith("Mutate Probility")){
					temp = line.split(":");
					populationmutate = Float.parseFloat(temp[1].trim());
				}
				else if(line.startsWith("GeneLength")){
					temp= line.split(":");
					this.xcorrlen = Short.parseShort(temp[1]);
					this.dcnlen = Short.parseShort(temp[2]);
					this.splen = Short.parseShort(temp[3]);
					this.rsplen = Short.parseShort(temp[4]);
					this.ionlen = Short.parseShort(temp[5]);
				}
				
			}
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	void write(){
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(inifile)));
			
			pw.println("Gene Patamter:");
			pw.println("Population Size :"+this.populationsize);
			pw.println("Max Generation :"+this.maxgeneration);
			pw.println("Cross Probility :"+this.populationcross);
			pw.println("Mutate Probility :"+this.populationmutate);
			pw.println();
			
			pw.println("GeneLength :"+this.xcorrlen+":"+this.dcnlen+":"+this.splen+":"+this.rsplen+":"
					+this.ionlen+":(Xcorr,Dcn,Sp,Rsp,Ions)");
			pw.println();
			
			pw.println("Sequest Paramter:");
			pw.println("[Xcorr]");
			pw.println("charge1 :"+this.xcorr1min+" : "+this.xcorr1max);
			pw.println("charge2 :"+this.xcorr2min+" : "+this.xcorr2max);
			pw.println("charge3 :"+this.xcorr3min+" : "+this.xcorr3max);
			pw.println("[Delta Cn]");
			pw.println("charge1 :"+this.dcn1min+" : "+this.dcn1max);
			pw.println("charge2 :"+this.dcn2min+" : "+this.dcn2max);
			pw.println("charge3 :"+this.dcn3min+" : "+this.dcn3max);
			pw.println("[Sp]");
			pw.println("charge1 :"+this.sp1min+" : "+this.sp1max);
			pw.println("charge2 :"+this.sp2min+" : "+this.sp2max);
			pw.println("charge3 :"+this.sp3min+" : "+this.sp3max);
			pw.println("[Ions]");
			pw.println("charge1 :"+this.ion1min+" : "+this.ion1max);
			pw.println("charge2 :"+this.ion2min+" : "+this.ion2max);
			pw.println("charge3 :"+this.ion3min+" : "+this.ion3max);
			pw.println("[Rsp]");
			pw.println(this.rspmin+" : "+this.rspmax);
			pw.println("[DeltaMZ]");
			pw.println(this.dmsmin+" : "+this.dmsmax);
			pw.println("[Max FP]");
			pw.println(this.maxfalseratio);
			pw.close();
		} catch (IOException e) {
			System.out.println("error write paramter");
		}
	}
	
}
