/* 
 ******************************************************************************
 * File: GlycoCompose.java * * * Created on 2011-4-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2011-4-20, 13:31:43
 */
public class NGlycoCompose {

	public static double [] units = 
		new double[]{146.057909, 162.052825, 203.079373, 291.095416635, 307.090331};
	
	public static double [] units2 = 
		new double[]{132.04226, 146.057909, 162.052825, 203.079373, 291.095416635, 307.090331};
	
	private static int [] upperLimit = new int [] {6, 15, 15, 5, 5};
	
	private static float ppm = 50f;
	private static float deltaMz = 0.05f;
	
	private static DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public static GlycoForm [] calForm(double mass){
		
		int max = (int) (mass/units2[0]);

		ArrayList <GlycoForm> formlist = new ArrayList <GlycoForm>();
		
		for(int i1=0;i1<=max;i1++){
			for(int i2=0;i2<=max;i2++){
				for(int i3=0;i3<=max;i3++){
					for(int i4=0;i4<=max;i4++){
						for(int i5=0;i5<=max;i5++){
							for(int i6=0;i6<=max;i6++){

								double m = i1*units2[0]+i2*units2[1]+i3*units2[2]+i4*units2[3]
										+i5*units2[4]+i6*units2[5];
								
								if(Math.abs(m-mass)<deltaMz){
//									System.out.println("comp99\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//									System.out.println("comp100\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

									int [] comp = new int []{i1, i2, i3, i4, i5};
									double dm = Double.parseDouble(df4.format(mass-m));
									GlycoForm form = new GlycoForm(comp, dm);
									formlist.add(form);
								}
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		GlycoForm [] forms = formlist.toArray(new GlycoForm[formlist.size()]);
//		System.out.println("compose120"+"\t"+forms[0].getCompDes()+"\t"+forms.length+"\t"+forms[0].getStrComp());
		Arrays.sort(forms);
		return forms;
	}

	public static NGlycoPossiForm [] calWithCore(double mass, boolean fuc){
		
		int max = (int) (mass/units[0]);
		int min = (int) (mass/units[4]);
		
		min = min < 5 ? 5: min;
		
		int [] ul = new int [upperLimit.length];
		for(int i=0;i<ul.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}
		
		int [] lowerLimit = null;
		
		if(fuc)
			lowerLimit = new int [] {1, 3, 2, 0, 0};
		else
			lowerLimit = new int [] {0, 3, 2, 0, 0};
		
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							int numtotal = i1+i2+i3+i4+i5;
							if(numtotal<min || numtotal>max)
								continue;
							
							double m = i1*units[0]+i2*units[1]+i3*units[2]+i4*units[3]+i5*units[4];
							if(Math.abs(m-mass)<deltaMz){
//								System.out.println("comp99\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//								System.out.println("comp100\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

								int [] comp = new int []{i1, i2, i3, i4, i5};
								double dm = Double.parseDouble(df4.format(mass-m));
								NGlycoPossiForm form = new NGlycoPossiForm(comp, fuc, dm);
								if(form.isRationality()){
									formlist.add(form);
//									System.out.println("comp107\t"+form.getCompDes());
								}
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose120"+"\t"+forms[0].getCompDes()+"\t"+forms.length+"\t"+forms[0].getStrComp());
		Arrays.sort(forms);
		return forms;
	}
	
	public static NGlycoPossiForm [] calWithCore2(double mass, boolean fuc){
		
		int max = (int) (mass/units2[0]);
		int min = (int) (mass/units2[5]);
		
		min = min < 5 ? 5: min;
		
		int [] ul = new int [upperLimit.length];
		for(int i=0;i<ul.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}
		
		int [] lowerLimit = null;
		
		if(fuc)
			lowerLimit = new int [] {0, 1, 3, 2, 0, 0};
		else
			lowerLimit = new int [] {0, 0, 3, 2, 0, 0};
		
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							for(int i6=lowerLimit[5];i6<=ul[5];i6++){
								
								int numtotal = i1+i2+i3+i4+i5+i6;
								if(numtotal<min || numtotal>max)
									continue;
								
								double m = i1*units2[0]+i2*units2[1]+i3*units2[2]+i4*units2[3]+i5*units2[4]+i6*units2[5];
								if(Math.abs(m-mass)<deltaMz){
//									System.out.println("comp99\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//									System.out.println("comp100\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

									int [] comp = new int []{i1, i2, i3, i4, i5};
									double dm = Double.parseDouble(df4.format(mass-m));
									NGlycoPossiForm form = new NGlycoPossiForm(comp, fuc, dm);
//									if(form.isRationality()){
										formlist.add(form);
//										System.out.println("comp107\t"+form.getCompDes());
//									}
								}
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose120"+"\t"+forms[0].getCompDes()+"\t"+forms.length+"\t"+forms[0].getStrComp());
		Arrays.sort(forms);
		return forms;
	}

	public static NGlycoPossiForm [] calWithCore(double mass, boolean fuc, double deltaMz){
		
		int max = (int) (mass/units[0]);
		int min = (int) (mass/units[4]);
		
		min = min < 5 ? 5: min;
		
		int [] ul = new int [upperLimit.length];
		for(int i=0;i<ul.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}
		
		int [] lowerLimit = null;
		
		if(fuc)
			lowerLimit = new int [] {1, 3, 2, 0, 0};
		else
			lowerLimit = new int [] {0, 3, 2, 0, 0};
		
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							int numtotal = i1+i2+i3+i4+i5;
							if(numtotal<min || numtotal>max)
								continue;
							
							double m = i1*units[0]+i2*units[1]+i3*units[2]+i4*units[3]+i5*units[4];
							if(Math.abs(m-mass)<deltaMz){
//								System.out.println("comp99\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//								System.out.println("comp100\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

								int [] comp = new int []{i1, i2, i3, i4, i5};
								double dm = Double.parseDouble(df4.format(mass-m));
								NGlycoPossiForm form = new NGlycoPossiForm(comp, fuc, dm);
								if(form.isRationality()){
									formlist.add(form);
//									System.out.println("comp107\t"+form.getCompDes());
								}
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose120"+"\t"+forms[0].getCompDes()+"\t"+forms.length+"\t"+forms[0].getStrComp());
		Arrays.sort(forms);
		return forms;
	}

	public static NGlycoPossiForm [] calNoCore(double mass){
		
		int max = (int) (mass/units[0]);
		int min = (int) (mass/units[4]);

		int [] ul = new int [upperLimit.length];
		
		for(int i=0;i<ul.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}

		int [] lowerLimit = new int [] {0, 0, 0, 0, 0};
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							int numtotal = i1+i2+i3+i4+i5;
							if(numtotal<min || numtotal>max)
								continue;
							
							double m = i1*units[0]+i2*units[1]+i3*units[2]+i4*units[3]+i5*units[4];
							if(Math.abs(m-mass)<deltaMz){
//								System.out.println("comp147\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//								System.out.println("comp148\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

								int [] comp = new int []{i1, i2, i3, i4, i5};
								double dm = mass-m;
								NGlycoPossiForm form = new NGlycoPossiForm(comp, dm);

								formlist.add(form);
							}
						}
					}
				}
			}
		}

		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose166\t"+forms[0].getCompDesNoCore()+"\t"+mass+"\t"+forms.length);
		Arrays.sort(forms);
		return forms;
	}
	
	public static NGlycoPossiForm [] calWithCorePPM(double mass){
		
		int max = (int) (mass/units[0]);
		int min = (int) (mass/units[4]);
		
		min = min < 5 ? 5: min;
		
		int [] ul = new int [upperLimit.length];
		for(int i=0;i<ul.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}
		
		int [] lowerLimit = null;

		lowerLimit = new int [] {0, 3, 2, 0, 0};
		
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							int numtotal = i1+i2+i3+i4+i5;
							if(numtotal<min || numtotal>max)
								continue;
							
							double m = i1*units[0]+i2*units[1]+i3*units[2]+i4*units[3]+i5*units[4];
//System.out.println(m+"\t"+Math.abs(m-mass)+"\t"+mass*ppm/1000000.0);							
							if(Math.abs(m-mass)<mass*ppm/1000000.0){
//								System.out.println("comp99\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//								System.out.println("comp100\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

								int [] comp = new int []{i1, i2, i3, i4, i5};
								double dm = mass-m;
								NGlycoPossiForm form = new NGlycoPossiForm(comp, false, dm);
//								if(form.isRationality()){
									formlist.add(form);
//									System.out.println("comp107\t"+form.getCompDes());
//								}
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose120"+"\t"+forms[0].getCompDes()+"\t"+forms.length+"\t"+forms[0].getStrComp());
		Arrays.sort(forms);
		return forms;
	}

	public static NGlycoPossiForm [] calWithCorePPM(double mass, boolean fuc){
		
		int max = (int) (mass/units[0]);
		int min = (int) (mass/units[4]);
		
		min = min < 5 ? 5: min;
		
		int [] ul = new int [upperLimit.length];
		for(int i=0;i<ul.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}
		
		int [] lowerLimit = null;
		
		if(fuc)
			lowerLimit = new int [] {1, 3, 2, 0, 0};
		else
			lowerLimit = new int [] {0, 3, 2, 0, 0};
		
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							int numtotal = i1+i2+i3+i4+i5;
							if(numtotal<min || numtotal>max)
								continue;
							
							double m = i1*units[0]+i2*units[1]+i3*units[2]+i4*units[3]+i5*units[4];
//System.out.println(m+"\t"+Math.abs(m-mass)+"\t"+mass*ppm/1000000.0);							
							if(Math.abs(m-mass)<mass*ppm/1000000.0){
//								System.out.println("comp99\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//								System.out.println("comp100\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

								int [] comp = new int []{i1, i2, i3, i4, i5};
								double dm = mass-m;
								NGlycoPossiForm form = new NGlycoPossiForm(comp, fuc, dm);
//								if(form.isRationality()){
									formlist.add(form);
//									System.out.println("comp107\t"+form.getCompDes());
//								}
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose120"+"\t"+forms[0].getCompDes()+"\t"+forms.length+"\t"+forms[0].getStrComp());
		Arrays.sort(forms);
		return forms;
	}

	public static NGlycoPossiForm [] calNoCorePPM(double mass){
		
		int max = (int) (mass/units[0]);
		int min = (int) (mass/units[4]);

		int [] ul = new int [upperLimit.length];
		for(int i=0;i<upperLimit.length;i++){
			ul[i] = max > upperLimit[i] ? upperLimit[i] : max;
		}
		
		ArrayList <NGlycoPossiForm> formlist = new ArrayList <NGlycoPossiForm>();
		int [] lowerLimit = new int [] {0, 0, 0, 0, 0};
		
		for(int i1=lowerLimit[0];i1<=ul[0];i1++){
			for(int i2=lowerLimit[1];i2<=ul[1];i2++){
				for(int i3=lowerLimit[2];i3<=ul[2];i3++){
					for(int i4=lowerLimit[3];i4<=ul[3];i4++){
						for(int i5=lowerLimit[4];i5<=ul[4];i5++){
							int numtotal = i1+i2+i3+i4+i5;
							if(numtotal<min || numtotal>max)
								continue;
							
							double m = i1*units[0]+i2*units[1]+i3*units[2]+i4*units[3]+i5*units[4];

							if(Math.abs(m-mass)<mass*ppm/1000000.0){
//								System.out.println("comp147\t"+mass+"\t"+m+"\t"+Math.abs(m-mass));
//								System.out.println("comp148\t"+i1+"\t"+i2+"\t"+i3+"\t"+i4+"\t"+i5);

								int [] comp = new int []{i1, i2, i3, i4, i5};
								double dm = mass-m;
								NGlycoPossiForm form = new NGlycoPossiForm(comp, dm);

								formlist.add(form);
							}
						}
					}
				}
			}
		}
		
		if(formlist.size()==0)
			return null;
		
		NGlycoPossiForm [] forms = formlist.toArray(new NGlycoPossiForm[formlist.size()]);
//		System.out.println("compose166\t"+forms[0].getCompDesNoCore()+"\t"+mass+"\t"+forms.length);
		Arrays.sort(forms);
		return forms;
	}
	
	public static int [] getCertain(NGlycoPossiForm [] forms){
		int [] coms = new int [units.length];
		Arrays.fill(coms, 20);
		if(forms.length==1){
			return forms[0].getComposition();
		}else{
			for(int i=0;i<forms.length;i++){
				int [] cs = forms[i].getComposition();
				for(int j=0;j<cs.length;j++){
					if(coms[j]>cs[j]){
						coms[j] = cs[j];
					}
				}
			}
		}
		return coms;
	}
	
	public static double calGlycoMass(int [] com){
		double mass = 0;
		for(int i=0;i<com.length;i++){
			mass += units[i] * com[i];
		}
		return mass;
	}
	
	public static double calGlycoMass2(int [] com){
		double mass = 0;
		for(int i=0;i<com.length;i++){
			mass += units2[i] * com[i];
		}
		return mass;
	}
	
	public static void filetest(String in) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null){
			double mass = Double.parseDouble(line.split("\t")[1]);
			NGlycoPossiForm[] ffs2 = NGlycoCompose.calNoCore(mass);
			if(ffs2!=null){
				System.out.println(mass+"\t"+ffs2[0].getCompDes());
			}
		}
		reader.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		NGlycoCompose.filetest("H:\\20130519_glyco\\HCD\\Centroid\\25_glycomass.txt");
//		double mass = (993.3762-AminoAcidProperty.PROTON_W)*3.0 - (841.3467-AminoAcidProperty.PROTON_W)*2.0;
//		double mass = (983.4460-AminoAcidProperty.PROTON_W)*3.0 - 2176.00212525689;
//		System.out.println(mass);
		
//		NGlycoPossiForm[] ffs2 = NGlycoCompose.calNoCore(1805.630172);
//		NGlycoPossiForm[] ffs2 = NGlycoCompose.calWithCore(655.6081, false);
		
		/*		if(ffs2!=null){
			for(int i=0;i<ffs2.length;i++)
				System.out.println((ffs2!=null)+"\t"+ffs2[i].getCompDesNoCore()+"\t"+ffs2[i].getDeltaMass()+"\t"
					+ffs2.length);
		}else{
			System.out.println(ffs2!=null);
		}
*/
//		int [] com = new int []{0, 5, 2, 0, 0};
//		double mass2 = NGlycoCompose.calGlycoMass(com);
//		System.out.println(mass2);
		
/*		int [] com2 = new int []{0, 0, 6, 2, 0, 0};
		double mass3 = NGlycoCompose.calGlycoMass2(com2);
		System.out.println("2\t"+mass3);
				
		double delta = mass-mass2;
		System.out.println(mass2);
		System.out.println(delta);
		
		NGlycoPossiForm[] nf3 = NGlycoCompose.calNoCorePPM(delta);
		System.out.println(nf3==null);
//		g.calWithCore(2878.0654469375);
//		System.out.println((int)0.5);
*/
	}

}
