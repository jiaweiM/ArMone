/* 
 ******************************************************************************
 * File: MascotParameter.java * * * Created on 2011-8-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.mascot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ck
 *
 * @version 2011-8-31, 14:47:19
 */
public class MascotParameter {

	private String inifile = "SFOER_mascot.ini";
	
	int populationsize = 100;
	int maxgeneration = 500;
	/**
	 * If these is no improvement in the fitness, the optimization will be terminated. 
	 */
	int no_improv_generations = 100;
	float populationcross = 0.2f;
	float populationmutate = 0.01f;
	double maxfalseratio = 0.01;
	double ionScore1min=0.01,ionScore1max=500,ionScore2min=0.01,ionScore2max=500,ionScore3min=0.01,ionScore3max=500,
			deltaIS1min=0.01,deltaIS1max=0.8,deltaIS2min=0.01,deltaIS2max=0.8,deltaIS3min=0.01,deltaIS3max=0.8,
			mht1min=-50,mht2min=-50,mht3min=-50,mht1max=50,mht2max=50,mht3max=50,
			mit1min=-50,mit2min=-50,mit3min=-50,mit1max=50,mit2max=50,mit3max=50,
			evalue1min=0,evalue2min=0,evalue3min=0,evalue1max=1,evalue2max=1,evalue3max=1,
			ion1min=0,ion1max=1,ion2min=0,ion2max=1,ion3min=0,ion3max=1,
			dmsmin = 0, dmsmax = 2000;
	
	//length of gene;
	short ionScorelen=9, deltaISlen=9, mhtlen=9, mitlen=9, evaluelen=12, ionlen=10, dmslen= 12;
	
	boolean read(){
		String line;
		String[] temp;
		try {
			BufferedReader bf = new BufferedReader(new FileReader(inifile));
			while((line = bf.readLine())!=null){
				if(line.equals("[Ion Score]")){
					line = bf.readLine();
					temp = line.split(":");
					ionScore1min = Double.parseDouble(temp[1].trim());
					ionScore1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					ionScore2min = Double.parseDouble(temp[1].trim());
					ionScore2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					ionScore3min = Double.parseDouble(temp[1].trim());
					ionScore3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Delta Ion Score]")){
					line = bf.readLine();
					temp = line.split(":");
					deltaIS1min = Double.parseDouble(temp[1].trim());
					deltaIS1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					deltaIS2min = Double.parseDouble(temp[1].trim());
					deltaIS2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					deltaIS3min = Double.parseDouble(temp[1].trim());
					deltaIS3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Ion Score - MHT]")){
					line = bf.readLine();
					temp = line.split(":");
					mht1min = Double.parseDouble(temp[1].trim());
					mht1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					mht2min = Double.parseDouble(temp[1].trim());
					mht2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					mht3min = Double.parseDouble(temp[1].trim());
					mht3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Ion Score - MIT]")){
					line = bf.readLine();
					temp = line.split(":");
					mit1min = Double.parseDouble(temp[1].trim());
					mit1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					mit2min = Double.parseDouble(temp[1].trim());
					mit2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					mit3min = Double.parseDouble(temp[1].trim());
					mit3max = Double.parseDouble(temp[2].trim());
				}
				else if(line.equals("[Evalue]")){
					line = bf.readLine();
					temp = line.split(":");
					evalue1min = Double.parseDouble(temp[1].trim());
					evalue1max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					evalue2min = Double.parseDouble(temp[1].trim());
					evalue2max = Double.parseDouble(temp[2].trim());
					line = bf.readLine();
					temp = line.split(":");
					evalue3min = Double.parseDouble(temp[1].trim());
					evalue3max = Double.parseDouble(temp[2].trim());
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
					this.ionScorelen = Short.parseShort(temp[1]);
					this.deltaISlen = Short.parseShort(temp[2]);
					this.mhtlen = Short.parseShort(temp[3]);
					this.mitlen = Short.parseShort(temp[4]);
					this.evaluelen = Short.parseShort(temp[5]);
					this.ionlen = Short.parseShort(temp[6]);
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
			
			pw.println("GeneLength :"+this.ionScorelen+":"+this.deltaISlen+":"+this.mhtlen+":"+this.mitlen+":"
					+this.evaluelen+":"+this.ionlen+":(Ion Score,DeltaIS,MHT,MIT,Evalue,Ions)");
			pw.println();
			
			pw.println("Mascot Paramter:");
			pw.println("[Ion Score]");
			pw.println("charge1 :"+this.ionScore1min+" : "+this.ionScore1max);
			pw.println("charge2 :"+this.ionScore2min+" : "+this.ionScore2max);
			pw.println("charge3 :"+this.ionScore3min+" : "+this.ionScore3max);
			pw.println("[Delta Ion Score]");
			pw.println("charge1 :"+this.deltaIS1min+" : "+this.deltaIS1max);
			pw.println("charge2 :"+this.deltaIS2min+" : "+this.deltaIS2max);
			pw.println("charge3 :"+this.deltaIS3min+" : "+this.deltaIS3max);
			pw.println("[Ion Score - MHT]");
			pw.println("charge1 :"+this.mht1min+" : "+this.mht1max);
			pw.println("charge2 :"+this.mht2min+" : "+this.mht2max);
			pw.println("charge3 :"+this.mht3min+" : "+this.mht3max);
			pw.println("[Ion Score - MIT]");
			pw.println("charge1 :"+this.mit1min+" : "+this.mit1max);
			pw.println("charge2 :"+this.mit2min+" : "+this.mit2max);
			pw.println("charge3 :"+this.mit3min+" : "+this.mit3max);
			pw.println("[Ions]");
			pw.println("[Evalue]");
			pw.println("charge1 :"+this.evalue1min+" : "+this.evalue1max);
			pw.println("charge2 :"+this.evalue2min+" : "+this.evalue2max);
			pw.println("charge3 :"+this.evalue3min+" : "+this.evalue3max);
			pw.println("[Ions]");
			pw.println("charge1 :"+this.ion1min+" : "+this.ion1max);
			pw.println("charge2 :"+this.ion2min+" : "+this.ion2max);
			pw.println("charge3 :"+this.ion3min+" : "+this.ion3max);
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
