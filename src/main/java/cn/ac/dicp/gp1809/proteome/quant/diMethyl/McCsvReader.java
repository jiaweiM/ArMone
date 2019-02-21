/* 
 ******************************************************************************
 * File:McCsvReader.java * * * Created on 2010-4-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.diMethyl;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotVariableMod;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers.MascotCSVPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.quant.profile.Pixel;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ck
 *
 * @version 2010-4-20, 09:02:10
 */
public class McCsvReader {

	public final DecimalFormat df4 = new DecimalFormat(".####");
	public final double H = 1.0079;
	
	private String file;
	private BufferedReader reader;
	private MascotCSVPeptideReader msreader;
	
	private double [] varModif = new double[10];
	
	public McCsvReader(String file) throws FileDamageException, ModsReadingException, InvalidEnzymeCleavageSiteException, IOException{
		this.file = file;
		reader = new BufferedReader(new FileReader(file));
//		msreader = new MascotCSVPeptideReader(file);
//		reader = msreader.getReader();
		getReadyToRead();
	}
	
	public void getReadyToRead() throws IOException{
		String s;
		
		while((s=reader.readLine())!=null){		
			String [] strs = s.split(",");
			if(strs[0].contains("Identifier")){
				break;
			}
		}
		
		while((s=reader.readLine())!=null){		
			String [] strs = s.split(",");
			if(strs.length==1)
				break;
			Integer index = Integer.parseInt(strs[0]);
			double mod_delta = Double.parseDouble(strs[2]);
			varModif[index] = mod_delta;
		}
		
		while((s=reader.readLine())!=null){
			
			String [] strs = s.split(",");
			if(strs.length>=23)
				break;
		}

	}
	
	public McQResult [] getResList() throws IOException{
		String s;
		ArrayList <McQResult> resList = new ArrayList <McQResult> ();
		Map<Integer,MascotVariableMod> varModMap= msreader.getVariMod();
		
		String ref = "";
		String seq = "";
		String des = "";
		int isotope = 0;
		double cal_mr = 0;
		int exp_z = 0;
		int count [] = new int []{1,1,1,1,1};
		double [] exp_mz = new double []{0,0,0,0,0};
		boolean [] z_value = new boolean []{false,false,false,false,false};
		int scanNum = 0;

		while((s=reader.readLine())!=null){
			String [] strs1 = s.split("\",");
			
			if(strs1.length==2){
				String [] strs2 = strs1[0].split(",");
				String [] scanTitle = strs1[1].split(",")[1].split("\\s");
				
				exp_z = Integer.parseInt(strs2[11]);
				if(cal_mr!=Double.parseDouble(strs2[12])){
					
					for(int i=1;i<5;i++){
						
						if(z_value[i]){
							double ave_mz = Double.parseDouble(df4.format(exp_mz[i]/count[i]));
							McQResult res = new McQResult(ref,ave_mz,i,cal_mr,seq,des,scanNum/count[0]);
							res.setIso(isotope);
							resList.add(res);
						}
					}

					cal_mr = Double.parseDouble(strs2[12]);

					count = new int []{1,0,0,0,0};
					count[exp_z] = 1;
					exp_mz = new double []{0,0,0,0,0};
					exp_mz[exp_z] = Double.parseDouble(strs2[9]);
					z_value = new boolean []{false,false,false,false,false};
					z_value[exp_z] = true;
					isotope = 0;
					
					des = strs1[1].split(",")[0];
					seq = msreader.getPepSeq(strs2[17],strs2[19],strs2[18],des);
					char [] pos = des.toCharArray();
					for(int j=0;j<pos.length;j++){
						if(pos[j]=='.'||pos[j]=='0')
							continue;
						int pj = pos[j]-48;
						double m = varModMap.get(pj).getAddedMonoMass();
						if(m==28.031296){
							isotope = 1;
							break;
						}else if(m==32.056412){
							isotope = 2;
							break;
						}
					}
					
					scanNum = Integer.parseInt(scanTitle[14]);
					
				}else{
					exp_mz[exp_z] += Double.parseDouble(strs2[9]);
					count[exp_z]++;
					z_value[exp_z] = true;
					count[0]++;
					scanNum += Integer.parseInt(scanTitle[14]);
				}
				cal_mr = Double.parseDouble(strs2[12]);
			}else if(strs1.length==4){
				
				ref = strs1[0].split("\"")[1];
				String [] strs4 = strs1[2].split(",");
				String [] scanTitle = strs1[3].split(",")[1].split("\\s");
	
				exp_z = Integer.parseInt(strs4[8]);
				
				if(cal_mr!=Double.parseDouble(strs4[9])){
					
					for(int i=1;i<5;i++){
						if(z_value[i]){
							double ave_mz = Double.parseDouble(df4.format(exp_mz[i]/count[i]));
							McQResult res = new McQResult(ref,ave_mz,i,cal_mr,seq,des,scanNum/count[0]);
							res.setIso(isotope);
							resList.add(res);
						}
					}

					cal_mr = Double.parseDouble(strs4[9]);
					

					count = new int []{1,0,0,0,0};
					count[exp_z] = 1;
					exp_mz = new double []{0,0,0,0,0};
					exp_mz[exp_z] = Double.parseDouble(strs4[6]);
					z_value = new boolean []{false,false,false,false,false};
					z_value[exp_z] = true;
					isotope = 0;
					
					des = strs1[3].split(",")[0];
					seq = msreader.getPepSeq(strs4[14],strs4[16],strs4[15],des);
					char [] pos = des.toCharArray();
					for(int j=0;j<pos.length;j++){
						if(pos[j]=='.'||pos[j]=='0')
							continue;
						int pj = pos[j]-48;
						double m = varModMap.get(pj).getAddedMonoMass();
						if(m==28.031296){
							isotope = 1;
							break;
						}else if(m==32.056412){
							isotope = 2;
							break;
						}
					}
					scanNum = Integer.parseInt(scanTitle[14]);
				}else{
					exp_mz[exp_z] += Double.parseDouble(strs4[6]);
					count[exp_z]++;
					z_value[exp_z] = true;
					count[0]++;
					scanNum += Double.parseDouble(strs4[6]);
				}
				cal_mr = Double.parseDouble(strs4[9]);
			}
		}
		
		McQResult [] result = resList.toArray(new McQResult[resList.size()]);
		Arrays.sort(result);
//		for(int i=0;i<resList.size();i++){
//			System.out.println(i+"\t"+result[i].getPepSeq());
//		}
		reader.close();

		return result;
	}
	
	/**
	 * These pixels is in ms2 scan.
	 * @param resList
	 * @return
	 */
	public Pixel[] createPixList(McQResult[] resList){
		
		Pixel [] pixList = new Pixel [resList.length];

		for(int i=0;i<resList.length;i++){
			McQResult res = resList[i];
			float exp_mz = (float) res.getExpMz();
			int scanNum = res.getAveScanNum();
			Pixel p = new Pixel(scanNum,exp_mz,0,0);
			p.setCharge(res.getExpZ());
			pixList[i]=p;
//			System.out.println(p);
		}
		
		return pixList;
	}
	
	public void DtaSelect(String dir) throws IOException{
		File file = new File(dir);
		
		FileFilter fileFilter=new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp = pathname.getName().toLowerCase();
	            if(tmp.endsWith(".dta")){
	                return true;
	            }
	            return false;
	        }
	    };
		
	    File [] dtaFiles = null;
		HashMap <String, File> scanNumMap = new HashMap<String, File>();
		HashMap <String, String> chargeMap = new HashMap<String, String>();
		HashMap <File, Integer> booMap = new HashMap<File,Integer>();
		if(file.isDirectory()){
			dtaFiles = file.listFiles(fileFilter);
		}
/*		
		for(int i=0;i<dtaFiles.length;i++){
			String [] name = dtaFiles[i].getName().split("\\.");
			if(name.length!=5)
				throw new IOException("Incorrect file name: "+dtaFiles[i].getName());
			
			scanNumMap.put(name[1], dtaFiles[i]);
			scanNumMap.put(name[2], dtaFiles[i]);
			chargeMap.put(name[1], name[3]);
			chargeMap.put(name[2], name[3]);
			booMap.put(dtaFiles[i], 0);
		}
*/
		HashMap <String,String> scMap = new HashMap <String, String>();
		String s;
		while((s=reader.readLine())!=null && s.length()>0){			
			String [] strs1 = s.split("\",");
			String charge = "";
			String scanNum = "";
			if(strs1.length==2){
				String [] strs2 = strs1[0].split(",");
				charge = strs2[11];
				String [] scanTitle = strs1[1].split(",")[1].split("\\s");				
				scanNum = scanTitle[14];
				scMap.put(scanNum, charge);
			}else if(strs1.length==4){
				String [] strs5 = strs1[2].split(",");
				charge = strs5[8];
				String [] scanTitle = strs1[3].split(",")[1].split("\\s");
				scanNum = scanTitle[14];
				scMap.put(scanNum, charge);
			}
		}
		System.out.println(scMap.size());
		for(int i=0;i<dtaFiles.length;i++){
			String [] name = dtaFiles[i].getName().split("\\.");
			if(name.length!=5)
				throw new IOException("Incorrect file name: "+dtaFiles[i].getName());
			
			boolean delete = true;
			if(scMap.containsKey(name[1])){
//				if(scMap.get(name[1]).equals(name[3]))
					delete = false;
			}
			if(scMap.containsKey(name[2])){
//				if(scMap.get(name[2]).equals(name[3]))
					delete = false;
			}
			if(delete){
				dtaFiles[i].delete();
			}
		}	
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidEnzymeCleavageSiteException 
	 * @throws ModsReadingException 
	 * @throws FileDamageException 
	 */
	public static void main(String[] args) throws IOException, FileDamageException, ModsReadingException, InvalidEnzymeCleavageSiteException {
		// TODO Auto-generated method stub

		long startTime=System.currentTimeMillis();
		
		McCsvReader mReader  = new McCsvReader("E:\\Data\\csv��ȡdta" +
				"\\F7.csv");
		String dir = "E:\\Data\\csv��ȡdta\\F7\\DTA_input";
//		mReader.createPixList(mReader.getResList());
		mReader.DtaSelect(dir);
		long endTime=System.currentTimeMillis(); 
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ms");   
	}

}
