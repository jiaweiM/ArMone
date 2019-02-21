/*
 * *****************************************************************************
 * File: ChargeCalculator.java * * * Created on 12-09-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.pi;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.math.ComputeObject;

/**
 * The charge calculator for aminoacid sequences at specific pH value
 * 
 * @author Xinning
 * @version 0.1, 12-09-2008, 21:21:36
 */
public class ChargeCalculator implements ComputeObject{
	
	//pk value of didnt at termine aa
	private static final double noneTermineAAPk[] = PICalculator.noneTermineAAPk;
	
	/* the 7 amino acid which matter */
	private static int R = 'R' - 'A',
	                     H = 'H' - 'A',
	                     K = 'K' - 'A',
	                     D = 'D' - 'A',
	                     E = 'E' - 'A',
	                     C = 'C' - 'A',
	                     Y = 'Y' - 'A';
	
	
    private boolean hasFreeNTerm = true;
    private boolean hasFreeCTerm = true;
    private double[] termpk = null;// pk value for the terminal
    private int[] composition;// aa composition for the sequence
     
     /*
      * @param: termpk array contains two elements: pk for N terminal aa and C terminal aa
      */
     public ChargeCalculator(double[] termpk, int[] composition, boolean hasFreeNTerm, boolean hasFreeCTerm)
     {
         this.termpk = termpk;
         this.hasFreeNTerm = hasFreeNTerm;
         this.hasFreeCTerm = hasFreeCTerm;
         this.composition = composition;
     }

     public ChargeCalculator(double[] termpk, int[] composition){
     	this.termpk = termpk;
     	this.composition = composition;
     }
     
     
     /*
      * Used for calculate charge for peptide in specfic pH values.
      */  
     public ChargeCalculator(String sequence){
    	 String parsedsequence = PeptideUtil.getUniqueSequence(sequence);
    	 this.termpk = PICalculator.getTerminalPk(parsedsequence);
     }
     
     
     private static double EXP10(double value){
    	 return Math.pow(10,value);
     }

     /**
      * Compute the charge state at specific pH value.
      */
     @Override
     public double compute(double pH)
     {
         double charge = 0.0;

         // I use a convention that positive pK values reflect bases and negative pK values reflect acids.
         
       
         double carg = composition[R] * EXP10(-pH) / (EXP10(-noneTermineAAPk[R]) + EXP10(-pH));
         double chis = composition[H] * EXP10(-pH) / (EXP10(-noneTermineAAPk[H]) + EXP10(-pH));
         double clys = composition[K] * EXP10(-pH) / (EXP10(-noneTermineAAPk[K]) + EXP10(-pH));
       
         double casp = composition[D] * EXP10(-noneTermineAAPk[D]) / (EXP10(-noneTermineAAPk[D]) + EXP10(-pH));
         double cglu = composition[E] * EXP10(-noneTermineAAPk[E]) / (EXP10(-noneTermineAAPk[E]) + EXP10(-pH));
         double ccys = composition[C] * EXP10(-noneTermineAAPk[C]) / (EXP10(-noneTermineAAPk[C]) + EXP10(-pH));
         

         double ctyr = composition[Y] * EXP10(-noneTermineAAPk[Y]) / (EXP10(-noneTermineAAPk[Y]) + EXP10(-pH));
       
         charge = carg + clys + chis  - (casp + cglu + ctyr + ccys);
     

         // account for end charges
         if (hasFreeNTerm) {
        	 double  nter = EXP10(-pH) / (EXP10(-termpk[0]) + EXP10(-pH));
             charge += nter;
         }

         if (hasFreeCTerm) {
             double cter = EXP10(-termpk[1]) / (EXP10(-termpk[1]) + EXP10(-pH));
             charge -= cter;
         }

         return charge;
     }
 }
