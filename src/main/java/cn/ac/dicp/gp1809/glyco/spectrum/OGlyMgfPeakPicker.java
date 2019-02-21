/* 
 ******************************************************************************
 * File: OGlyMgfPeakPicker.java * * * Created on 2012-4-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.spectrum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;

/**
 * @author ck
 *
 * @version 2012-4-1, 12:47:14
 */
public class OGlyMgfPeakPicker {
	
	private BufferedReader reader;
	
	private double markTorelance = 0.01;
	
	private double torelance = 0.05;
	
	/**
	 * nbt.1511-S1, p9
	 */
	protected static final double dm = 1.00286864;
	
	private static double [] marks = new double []{204.086649, 274.08741263499996, 292.102692635, 366.139474};
	
	private static double [] marks1 = new double []{204.086649, 366.139474};
	
	private static double [] marks2 = new double []{274.08741263499996, 292.102692635};
	
	private static double [] delete = new double []{186.07136899999998, 204.086649, 274.08741263499996, 
		292.102692635, 366.139474, 657.2348890000001};
	
	private static double [] delete2 = new double []{291.095416635, 453.148241635, 656.227614635, 
		947.32303127, 582.19083327, 744.24365827};
	
	public static double [] units = 
		new double[]{162.052825, 203.079373, 291.095416635};
	
	public OGlyMgfPeakPicker(String file) throws IOException{
		this.reader = new BufferedReader(new FileReader(file));
	}
	
	public OGlyMgfPeakPicker(String file, double tolerance) throws IOException{
		this.reader = new BufferedReader(new FileReader(file));
		this.torelance = tolerance;
	}
	
	public void write(String output) throws IOException{
		
		PrintWriter pw = new PrintWriter(output);
		
		boolean beg = false;
		boolean end = false;
		
		StringBuilder sb = null;
		double markInten = 0;
		double baseInten = 0;
		
		String line;
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("BEGIN")){
				
				beg = true;
				end = false;
				sb = new StringBuilder();
				sb.append("BEGIN ION\n");
				
				markInten = 0;
				baseInten = 0;
				
				continue;
				
			}else if(line.startsWith("END")){
				
				beg = false;
				end = true;
				sb.append("BEGIN ION\n");
				
				if(markInten>baseInten*0.1){
					pw.write(sb.toString());
				}

				continue;
			}
			
			if(beg){
				
				if(line.contains("=")){
					sb.append(line).append("\n");
					
				}else{
					
					String [] ss = line.split("\\s");
					double mz = Double.parseDouble(ss[0]);
					double inten = Double.parseDouble(ss[1]);
					
					if(inten>baseInten){
						baseInten = inten;
					}
					
					for(int i=0;i<marks.length;i++){
						if(Math.abs(mz-marks[i])<torelance){
							markInten = inten;
						}
					}
				}
				
			}else if(end){
				
			}else{
				pw.write(line);
				pw.write("\n");
			}
		}
		
		reader.close();
		pw.close();
	}

	public void write(String output, String output2) throws IOException{

		PrintWriter pw = new PrintWriter(output);
		PrintWriter pw2 = new PrintWriter(output2);
		
		boolean beg = false;
		boolean end = false;
		
		StringBuilder sb = null;
		StringBuilder sb2 = null;
		
		double markInten = 0;
		double baseInten = 0;
		int charge = 0;
		double premz = 0;
		Double [] calDelete = null;
		
		String line;
L:		while((line=reader.readLine())!=null){
			
			if(line.startsWith("BEGIN")){

				beg = true;
				end = false;
				
				sb = new StringBuilder();
				sb.append(line).append("\n");
				
				sb2 = new StringBuilder();
				sb2.append(line).append("\n");
				
				markInten = 0;
				baseInten = 0;
				charge = 0;
				premz = 0;
				calDelete = null;
				
				continue;
				
			}else if(line.startsWith("END")){
				
				beg = false;
				end = true;
				sb.append(line).append("\n");
				sb2.append(line).append("\n");
				
				if(markInten>baseInten*0.1){
					pw.write(sb.toString());
					pw2.write(sb2.toString());
				}

				continue;
			}
			
			if(beg){
				
				if(line.contains("=")){
					
					sb.append(line).append("\n");
					sb2.append(line).append("\n");
					
					if(line.startsWith("PEPMASS")){
						
						String [] ss = line.split("[=\\s]");
						premz = Double.parseDouble(ss[1]);
						
					}else if(line.startsWith("CHARGE")){
						
						String [] ss = line.split("[=+]");
						charge = Integer.parseInt(ss[1]);
						
					}else if(line.startsWith("RTINSECONDS")){
						
						calDelete = this.getDeletePeaks(premz, charge);
						System.out.println(calDelete.length);
					}
					
				}else{
					
					String [] ss = line.split("\\s");
					double mz = Double.parseDouble(ss[0]);
					double inten = Double.parseDouble(ss[1]);
					
					if(inten>baseInten){
						baseInten = inten;
					}
					
					for(int i=0;i<marks.length;i++){
						if(Math.abs(mz-marks[i])<torelance){
							if(inten>markInten){
								markInten = inten;
							}
						}
					}
					
					sb.append(line).append("\n");
					
					for(int i=0;i<delete.length;i++){
						if(Math.abs(delete[i]-mz)<torelance){
//							System.out.println(mz);
							continue L;
						}
					}
					
					for(int i=0;i<calDelete.length;i++){
						if(Math.abs(calDelete[i]-mz)<torelance){
//							System.out.println(mz);
							continue L;
						}
					}
					
					sb2.append(line).append("\n");
				}
				
			}else if(end){
				
			}else{
				
				pw.write(line);
				pw.write("\n");
				
				pw2.write(line);
				pw2.write("\n");
			}
		}
		
		reader.close();
		pw.close();
		pw2.close();
	}
	
	private void write2(String output1, String output2, String output3, String output4, int topn) throws IOException{

		int totalcount = 0;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		
		PrintWriter pw1 = new PrintWriter(output1);
		PrintWriter pw2 = new PrintWriter(output2);
		PrintWriter pw3 = new PrintWriter(output3);
		PrintWriter pw4 = new PrintWriter(output4);
		
		boolean beg = false;
		boolean end = false;
		
		StringBuilder sb1 = null;
		StringBuilder sb2 = null;

		double inten204 = 0;
		double inten366 = 0;
		double inten274 = 0;
		double inten292 = 0;
		
		boolean type1 = false;
		boolean type2 = false;
		boolean type3 = false;
		
		ArrayList <double[]> peaklist = new ArrayList <double[]>();
		
		double baseInten = 0;
		int charge = 0;
		double premz = 0;
		
		String line;
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("BEGIN")){
				
				totalcount++;
				
				beg = true;
				end = false;
				
				sb1 = new StringBuilder();
				sb1.append(line).append("\n");
				
				sb2 = new StringBuilder();
				sb2.append(line).append("\n");

				baseInten = 0;
				charge = 0;
				premz = 0;
				
				inten204 = 0;
				inten366 = 0;
				inten274 = 0;
				inten292 = 0;
				
				type1 = false;
				type2 = false;
				type3 = false;
				
				peaklist = new ArrayList <double[]>();
				
				continue;
				
			}else if(line.startsWith("END")){
				
				beg = false;
				end = true;
				
				if(baseInten<100)
					continue;
				
				if(inten204/baseInten>0.05){
					
					if(inten366/baseInten>0.05){
						
						if(inten274/baseInten>0.1){
							
							type3 = true;
							
						}else{
							
							type2 = true;
						}
						
					}else{
						
						if(inten274/baseInten>0.1){
							
							type3 = true;
							
						}else{
							
							type1 = true;
						}
					}
				}else{
					continue;
				}

				double [][] list = peaklist.toArray(new double[peaklist.size()][]);
				Arrays.sort(list, new Comparator<double[]>(){

					@Override
					public int compare(double[] o1, double[] o2) {
						// TODO Auto-generated method stub
						if(o1[1]>o2[1])
							return -1;
						
						else if(o1[1]<o2[1])
							return 1;
						
						return 0;
					}
					
				});
				
				HashMap <Double, Double> deleteMap = new HashMap <Double, Double>();
				
				double parentMass = (premz - AminoAcidProperty.PROTON_W)*charge;
				
				for(int i=0;i<list.length;i++){
					
					for(int j=0;j<delete.length;j++){
						if(Math.abs(list[i][0]-delete[j])<this.torelance){
							deleteMap.put(list[i][0], 0.0);
						}
					}
					
					if(i<topn){
						
						for(int j=1;j<=charge;j++){
							
							double fragMass = (list[i][0]-AminoAcidProperty.PROTON_W)*(double)j;
							
							if(this.isGlycoPeak(parentMass-fragMass)){
								
								double iso = list[i][0] + dm/(double) j;
								deleteMap.put(list[i][0], iso);
							}
						}
					}
				}
				
//				Double [] deleteIdList = new Double[deleteMap.size()];
//				deleteIdList = deleteMap.keySet().toArray(deleteIdList);
//				Arrays.sort(deleteIdList);
				
				double isotopeMass = 0;
				
				for(int i=0;i<peaklist.size();i++){
					
					double [] pp = peaklist.get(i);
					double mz = pp[0];
					double inten = pp[1];
					
					sb2.append(mz).append("\t").append(inten);
					if(pp.length==3){
						sb2.append("\t").append((int)pp[2]);
					}
					
					sb2.append("\n");
					
					if(deleteMap.containsKey(mz)){
						
						isotopeMass = deleteMap.get(mz);
						
					}else{
						
						if(Math.abs(isotopeMass-mz)>this.torelance){
							sb1.append(mz).append("\t").append(inten);
							if(pp.length==3){
								sb1.append("\t").append((int)pp[2]);
							}
							sb1.append("\n");
						}
					}
				}
				
				sb1.append(line).append("\n");
				sb2.append(line).append("\n");
				
				if(type3){
					
					count3++;
					pw3.write(sb1.toString());
					pw4.write(sb2.toString());
					
				}else{
					
					if(type1 || type2){
						
						count1++;
						pw1.write(sb1.toString());
						pw2.write(sb2.toString());
						
					}
				}
				
				continue;
			}
			
			if(beg){
				
				if(line.contains("=")){
					
					sb1.append(line).append("\n");
					sb2.append(line).append("\n");
					
					if(line.startsWith("PEPMASS")){
						
						String [] ss = line.split("[=\\s]");
						premz = Double.parseDouble(ss[1]);
						
					}else if(line.startsWith("CHARGE")){
						
						String [] ss = line.split("[=+]");
						charge = Integer.parseInt(ss[1]);
						
					}else if(line.startsWith("RTINSECONDS")){

					}
					
				}else{
					
					String [] ss = line.split("\\s");
					double mz = Double.parseDouble(ss[0]);
					double inten = Double.parseDouble(ss[1]);
					double dd;
					
					if(ss.length==3){
						
						dd = Double.parseDouble(ss[2]);
						peaklist.add(new double[]{mz, inten, dd});
						
					}else if(ss.length==2){
						peaklist.add(new double[]{mz, inten});
					}

					if(inten>baseInten){
						baseInten = inten;
					}
					
					if(Math.abs(mz-204.086649)<this.markTorelance){
						inten204 = inten;
					}
					
					if(Math.abs(mz-366.139474)<this.markTorelance){
						inten366 = inten;
					}
					
					if(Math.abs(mz-274.08741263499996)<this.markTorelance){
						inten274 = inten;
					}
					
					if(Math.abs(mz-292.102692635)<this.markTorelance){
						inten292 = inten;
					}
				}
				
			}else if(end){
				
			}else{
				
				pw1.write(line);
				pw1.write("\n");
				
				pw2.write(line);
				pw2.write("\n");
				
				pw3.write(line);
				pw3.write("\n");
				
				pw4.write(line);
				pw4.write("\n");
			}
		}
		
		reader.close();
		
		pw1.close();
		pw2.close();
		pw3.close();
		pw4.close();
		
		System.out.println("totalcount\t"+totalcount);
		System.out.println("count1\t"+count1);
		System.out.println("count2\t"+count2);
		System.out.println("count3\t"+count3);
		System.out.println("count4\t"+count4);
	}
	
	private void write2(String output1, String output2, String output3, String output4) throws IOException{

		int totalcount = 0;
		int count1 = 0;
		int count2 = 0;
		int count3 = 0;
		int count4 = 0;
		
		PrintWriter pw1 = new PrintWriter(output1);
		PrintWriter pw2 = new PrintWriter(output2);
		PrintWriter pw3 = new PrintWriter(output3);
		PrintWriter pw4 = new PrintWriter(output4);
		
		boolean beg = false;
		boolean end = false;
		
		StringBuilder sb1 = null;
		StringBuilder sb2 = null;

		double inten204 = 0;
		double inten366 = 0;
		double inten274 = 0;
		double inten292 = 0;
		
		boolean type1 = false;
		boolean type2 = false;
		boolean type3 = false;
		
		ArrayList <double[]> peaklist = new ArrayList <double[]>();
		
		double baseInten = 0;
		int charge = 0;
		double premz = 0;
		
		String line;
		while((line=reader.readLine())!=null){
			
			if(line.startsWith("BEGIN")){
				
				totalcount++;
				
				beg = true;
				end = false;
				
				sb1 = new StringBuilder();
				sb1.append(line).append("\n");
				
				sb2 = new StringBuilder();
				sb2.append(line).append("\n");

				baseInten = 0;
				charge = 0;
				premz = 0;
				
				inten204 = 0;
				inten366 = 0;
				inten274 = 0;
				inten292 = 0;
				
				type1 = false;
				type2 = false;
				type3 = false;
				
				peaklist = new ArrayList <double[]>();
				
				continue;
				
			}else if(line.startsWith("END")){
				
				beg = false;
				end = true;
				
				if(baseInten<100)
					continue;
				
				if(inten204/baseInten>0.1){
					
					if(inten366/baseInten>0.1){
						
						if(inten274/baseInten>0.1){
							
							type3 = true;
							
						}else{
							
							type2 = true;
						}
						
					}else{
						
						if(inten274/baseInten>0.1){
							
							type3 = true;
							
						}else{
							
							type1 = true;
						}
					}
				}else{
					continue;
				}
		
				double parentMass = (premz - AminoAcidProperty.PROTON_W)*charge;

				double isotopeMass = 0;
				
L:				for(int i=0;i<peaklist.size();i++){
					
					double [] pp = peaklist.get(i);
					double mz = pp[0];
					double inten = pp[1];
					
					sb2.append(mz).append("\t").append(inten);
					if(pp.length==3){
						sb2.append("\t").append((int)pp[2]);
					}
					
					sb2.append("\n");
					
					for(int j=0;j<delete.length;j++){
						if(Math.abs(mz-delete[j])<this.torelance){
							continue L;
						}
					}
					
					if(inten>baseInten*0.1){
						
						boolean delete = false;
						
						for(int j=1;j<=charge;j++){
							
							double fragMass = (mz-AminoAcidProperty.PROTON_W)*j;
							
							if(this.isGlycoPeak(parentMass-fragMass)){
								
								isotopeMass = mz + dm/(double) j;
								delete = true;
								break;
							}
						}
						
						if(!delete){
							
							if(Math.abs(isotopeMass-mz)>this.torelance){
							
								sb1.append(mz).append("\t").append(inten);
								if(pp.length==3){
									sb1.append("\t").append((int)pp[2]);
								}
								sb1.append("\n");
							}
						}
						
					}else{
						
						if(Math.abs(isotopeMass-mz)>this.torelance){
							sb1.append(mz).append("\t").append(inten);
							if(pp.length==3){
								sb1.append("\t").append((int)pp[2]);
							}
							sb1.append("\n");
						}
					}
				}
				
				sb1.append(line).append("\n");
				sb2.append(line).append("\n");
				
				if(type3){
					
					count3++;
					pw3.write(sb1.toString());
					pw4.write(sb2.toString());
					
				}else{
					
					if(type1 || type2){
						
						count1++;
						pw1.write(sb1.toString());
						pw2.write(sb2.toString());
//						pw1.write("\n");
						
					}
				}

				continue;
			}
			
			if(beg){
				
				if(line.contains("=")){
					
					sb1.append(line).append("\n");
					sb2.append(line).append("\n");
					
					if(line.startsWith("PEPMASS")){
						
						String [] ss = line.split("[=\\s]");
						premz = Double.parseDouble(ss[1]);
						
					}else if(line.startsWith("CHARGE")){
						
						String [] ss = line.split("[=+]");
						charge = Integer.parseInt(ss[1]);
						
					}else if(line.startsWith("RTINSECONDS")){

					}
					
				}else{
					
					String [] ss = line.split("\\s");
					double mz = Double.parseDouble(ss[0]);
					double inten = Double.parseDouble(ss[1]);
					double dd;
					
					if(ss.length==3){
						
						dd = Double.parseDouble(ss[2]);
						peaklist.add(new double[]{mz, inten, dd});
						
					}else if(ss.length==2){
						peaklist.add(new double[]{mz, inten});
					}

					if(inten>baseInten){
						baseInten = inten;
					}
					
					if(Math.abs(mz-204.086649)<this.markTorelance){
						inten204 = inten;
					}
					
					if(Math.abs(mz-366.139474)<this.markTorelance){
						inten366 = inten;
					}
					
					if(Math.abs(mz-274.08741263499996)<this.markTorelance){
						inten274 = inten;
					}
					
					if(Math.abs(mz-292.102692635)<this.markTorelance){
						inten292 = inten;
					}
				}
				
			}else if(end){
				
			}else{
				
				pw1.write(line);
				pw1.write("\n");
				
				pw2.write(line);
				pw2.write("\n");
				
				pw3.write(line);
				pw3.write("\n");
				
				pw4.write(line);
				pw4.write("\n");
			}
		}
		
		reader.close();
		
		pw1.close();
		pw2.close();
		pw3.close();
		pw4.close();
		
		System.out.println("totalcount\t"+totalcount);
		System.out.println("count1\t"+count1);
		System.out.println("count2\t"+count2);
		System.out.println("count3\t"+count3);
		System.out.println("count4\t"+count4);
	}
	
	private boolean isGlycoPeak(double deltaMass){
		
		for(int i1=0;i1<=4;i1++){
			for(int i2=0;i2<=4;i2++){
				for(int i3=0;i3<=4;i3++){
					double m = i1*units[0]+i2*units[1]+i3*units[2];
					if(Math.abs(m-deltaMass)<this.torelance){
						return true;
					}
				}
			}
		}
		return false;
	}

	public Double [] getDeletePeaks(double premz, int charge){
		
		ArrayList <Double> list = new ArrayList <Double>();
		double mass = (premz-AminoAcidProperty.PROTON_W)*charge;
		
		for(int i=1;i<=charge;i++){
			
			for(int j=0;j<delete2.length;j++){
				
				double mz = (mass-delete2[j])/(double)i + AminoAcidProperty.PROTON_W;
				double isotope1 = mz + dm/(double)i;
				
				list.add(mz);
				list.add(isotope1);
//				System.out.println(mz);
//				System.out.println(isotope1);
			}
		}

		Double [] peaks = list.toArray(new Double[list.size()]);
		Arrays.sort(peaks);
		return peaks;
	}
	
	/**
	 * @param args
	 * @throws DtaFileParsingException 
	 * @throws IOException 
	 * @throws DtaWritingException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		double dd = Glycosyl.Hex.getMonoMass();
//		System.out.println(dd);
		OGlyMgfPeakPicker picker = new OGlyMgfPeakPicker("D:\\OGlycanMgfPicker\\kapaa-casein\\" +
				"20120326_kappa-casein_after-enrichment_5ug-03.mgf");
//		Double [] d2 = picker.getDeletePeaks(722.80825, 2);
//		System.out.println(d2.length);
//		picker.write("H:\\OGlycanMgfPicker\\20120329_Fetuin_elastase_HILIC_5ug-01_all.mgf", 
//				"H:\\OGlycanMgfPicker\\20120329_Fetuin_elastase_HILIC_5ug-01_delete.mgf");
		
//		picker.write2("H:\\OGlycanMgfPicker\\20120329_Fetuin_elastase_HILIC_5ug-01_1.mgf", 
//				"H:\\OGlycanMgfPicker\\20120329_Fetuin_elastase_HILIC_5ug-01_2.mgf", 
//				"H:\\OGlycanMgfPicker\\20120329_Fetuin_elastase_HILIC_5ug-01_3.mgf", 10);
//		
		picker.write2("D:\\OGlycanMgfPicker\\kapaa-casein\\20120326_kappa-casein_after-enrichment_5ug-03_1_delete.mgf", 
				"D:\\OGlycanMgfPicker\\kapaa-casein\\20120326_kappa-casein_after-enrichment_5ug-03_1_all.mgf", 
				"D:\\OGlycanMgfPicker\\kapaa-casein\\20120326_kappa-casein_after-enrichment_5ug-03_2_delete.mgf",
				"D:\\OGlycanMgfPicker\\kapaa-casein\\20120326_kappa-casein_after-enrichment_5ug-03_2_all.mgf", 20);
		
//		File file = new File("D:\\OGlycanMgfPicker\\20120329-fetuin");
//		File [] fs = file.listFiles();
//		for(int i=0;i<fs.length;i++){
//			String name = fs[i].getAbsolutePath();
//			OGlyMgfPeakPicker pi = new OGlyMgfPeakPicker(name);
//			String ss = name.substring(0, name.length()-4);
//			pi.write2(ss+"_1_delete.mgf", ss+"_1_all.mgf", ss+"_2_delete.mgf", ss+"_2_all.mgf", 20);
//		}
		
	}

}
