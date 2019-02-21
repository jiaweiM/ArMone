/*
 ******************************************************************************
 * File: KineticModelUtil.java * * * Created on 05-08-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim;

/**
 * 
 * 
 * Utilities of native methods using Kinetic Models developed by Z.Q. Zhang
 * 
 * The sim value is defined as described in paper: 
 * 	1.  Z. Zhang, "Prediction of low-energy collision-induced dissociation spectra of peptides". 
 *		Anal. Chem. (2004), 76(14), 3908-3922.
 *	2.  Z. Zhang, "Prediction of Low-Energy Collision-Induced Dissociation Spectra of Peptides 
 *		with Three or More Charges", Anal. Chem. (2005), 77(19), 6364-6373.
 * 
 * 
 * @author Xinning
 * @version 0.1, 05-09-2008, 20:17:25
 */
public class KineticModelUtil {
	
	static {
		System.loadLibrary("KineticModelUtil");
	}
	
	
	/**
	 * 
	 * 	Calculated the similarity score for the experimental and theoretical spectrum. 
	 * 	Describe in the demo of KineticModel by Z Q Zhang. 
	 * 
	 * 	<p> Parameters used for theoretical spectrum prediction:
	 * 	<p>		String sequence, float mono_mh, int charge,short instrument, float StartMass, 
	 *	<p>		float EndMass, float CollisionEnergy, float ReactionTime, float IsolationWidth,
	 *	<p>		float Resolution
	 *	<p>
	 *	<p> Parameters used for Similarity score calculation:
	 * 	<p>		float[] mzs, float[] intensities, int num_exper_peaks, float mass_tolerance
	 * 
	 *  @param sequence is a string containing the sequence of the peptide with one-letter code (uppercase). 
	 *  In addition: 
	 *				J - carboxymethylated cys
	 *				U - carboxyamidomethylated cys
	 *				O - oxidized methionine
	 *				s,t,y - phosphoserine, phosphothreonine and phosphotyrosine (phosphopeptides 
	 *						not fully trained yet)
	 *	For modification, put it in parenthesis after the sequence. For example AADECFGHK(C5+250)(H8-9) 
	 *		means cys-5 is modified by +250u, and His-8 is modified by -9u
	 *	For disulfide bond, put it in parenthesis too. For example, ADCAGHTYCHPEK(C3-C9) means cys-3 and 
	 *		cys-9 are linked by disulfide.
	 *
	 *	@param mono_mh mass is the monoisotopic mass of the peptide. If not known, set it as 0.0
	 *	@param CollisionEnergy is the normalized collision energy. For example, for a 35% collision 
	 *			energy, CollisionEnergy = 35.0
	 * 	@param ReactionTime is the the ion activation time in the unit of second, for LCQ/LTQ, 
	 * 			the default value is 0.03 second.
	 * 	@param IsolationWidth is the isolation width used for acquiring the spectrum, in the unit of Da.
	 * 	@param Resolution is M/delta_M at m/z 400. For LCQ and LTQ, the resolution is about 800
	 * 	@param StartMass and EndMass is the mass range of the spectra to be predicted. 
	 * 							For LCQ/LTQ, you can simply set to 50 and 2000
	 * 	@param instrument: the mass spectrometer type.
	 * 
	 * 
	 * 	
	 * 	@param mzs[], mz values of experimental peaks.
	 * 	@param intensities[], intensity values of experimental peaks.
	 * 	@param num_exper_peaks, number of experimental peaks.
	 * 	@param mass_tolerance: tolerance for peak matches (in Da). If the experimental and theoretical
	 *  		ions are with mz differences less than this value, they are considered as match.
	 */
	static native float getSim(
			//Used for the predication of theoretical spectrum
			String sequence, float mono_mh, int charge,short instrument, float startMass, 
			float endMass, float collisionEnergy, float reactionTime, float isolationWidth,
			float resolution,
			
			//The following parameters are used for Sim calculation
			float[] mzs, float[] intensities, int num_exper_peaks, float mass_tolerance);
	
	
	
	/**
	 * 
	 * 	Calculated the similarity score for the experimental and theoretical spectrum. 
	 * 	Describe in the demo of KineticModel by Z Q Zhang. 
	 * 
	 * 	<p> Parameters used for theoretical spectrum prediction:
	 * 	<p>		String sequence, float mono_mh, int charge,short instrument, float StartMass, 
	 *	<p>		float EndMass, float CollisionEnergy, float ReactionTime, float IsolationWidth,
	 *	<p>		float Resolution
	 *	<p>
	 *	<p> Parameters used for Similarity score calculation:
	 * 	<p>		float[] mzs, float[] intensities, int num_exper_peaks, float mass_tolerance
	 * 
	 *  @param sequence is a string containing the sequence of the peptide with one-letter code (uppercase). 
	 *  In addition: 
	 *				J - carboxymethylated cys
	 *				U - carboxyamidomethylated cys
	 *				O - oxidized methionine
	 *				s,t,y - phosphoserine, phosphothreonine and phosphotyrosine (phosphopeptides 
	 *						not fully trained yet)
	 *	For modification, put it in parenthesis after the sequence. For example AADECFGHK(C5+250)(H8-9) 
	 *		means cys-5 is modified by +250u, and His-8 is modified by -9u
	 *	For disulfide bond, put it in parenthesis too. For example, ADCAGHTYCHPEK(C3-C9) means cys-3 and 
	 *		cys-9 are linked by disulfide.
	 *
	 *	@param mono_mh mass is the monoisotopic mass of the peptide. If not known, set it as 0.0
	 *	@param CollisionEnergy is the normalized collision energy. For example, for a 35% collision 
	 *			energy, CollisionEnergy = 35.0
	 * 	@param ReactionTime is the the ion activation time in the unit of second, for LCQ/LTQ, 
	 * 			the default value is 0.03 second.
	 * 	@param IsolationWidth is the isolation width used for acquiring the spectrum, in the unit of Da.
	 * 	@param Resolution is M/delta_M at m/z 400. For LCQ and LTQ, the resolution is about 800
	 * 	@param StartMass and EndMass is the mass range of the spectra to be predicted. 
	 * 							For LCQ/LTQ, you can simply set to 50 and 2000
	 * 	@param instrument: the mass spectrometer type.
	 * 
	 * 
	 * 	
	 * 	@param mzs[], mz values of experimental peaks.
	 * 	@param intensities[], intensity values of experimental peaks.
	 * 	@param num_exper_peaks, number of experimental peaks.
	 * 	@param ppm_mass_tolerance: tolerance for peak matches (in ppm). If the experimental and 
	 * 		theoretical ions are with mz differences less than this value, they are considered as match.
	 */
	static native float getSim(
			//Used for the predication of theoretical spectrum
			String sequence, float mono_mh, int charge,short instrument, float startMass, 
			float endMass, float collisionEnergy, float reactionTime, float isolationWidth,
			float resolution,
			
			//The following parameters are used for Sim calculation
			float[] mzs, float[] intensities, int num_exper_peaks, int ppm_mass_tolerance);
	
	
	public void main(String[] args) {
		//for confuse
	}
}
