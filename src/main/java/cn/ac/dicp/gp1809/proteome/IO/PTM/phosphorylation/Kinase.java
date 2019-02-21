/* 
 ******************************************************************************
 * File:Kinase.java * * * Created on 2009-12-22
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import java.util.HashSet;

/**
 * @author ck
 *
 * @version 2009-12-22, 18:36:44
 */
public class Kinase {
	
/*	
	Akt_kinase ("Akt", new String [] {"RXRXX[pS/pT][F/L]","RXRXX[pS/pT]", "GRART[S/T]pSFAE", 
			"[R/Q/K][R/K/N/Q/P/H][R/K][R/S/T][N/K/Q/H/D/P]pS[F/W/I/M/N/S][S/T/H][R/S/K][S/T/P/Q]",
			"[R/K]X[R/K][S/T]XpS"}),
			
	AMP_activated_protein_kinase ("AMP_activated_protein", new String [] {"[M/V/L/I/F][R/K/H]XXX[pS/pT]XXX[M/V/L/I/F]",
			"[M/V/L/I]XX[R/K/H]X[pS/pT]XXX[M/V/L/I]", "[M/V/L/I/F][R/K/H]XX[pS/pT]XXX[M/V/L/I/F]",
			"[R/K]XRXXpSXXX[R/K]"}),
			
	ATM_kinase ("ATM_kinase", new String [] {"[P/L/I/M]X[L/I/D/E]pSQ", "LpSQE", "pSQ",}),
	
	Aurora_A_kinase ("Aurora_A_kinase", new String [] {"[R/K/N]RX[pS/pT][M/L/V/I]"}),
	
	b_Adrenergic_Receptor_kinase ("b_Adrenergic_Receptor_kinase", new String [] {"[D/E][pS/pT]XXX"}),
	
	Branched_chain_alpha_ketoacid_dehydrogenase_kinase ("Branched_chain_alpha_ketoacid_dehydrogenase_kinase", new String [] {
			"HpSTSDD", "YRpSVDE"}),
	
	Calmodulin_dependent_protein_kinase_I ("Calmodulin_dependent_protein_kinase_I", new String [] {
			"[M/V/L/I/F]XRXX[pS/pT]XXX[M/V/L/I/F]"}),
			
	Calmodulin_dependent_protein_kinase_II ("Calmodulin_dependent_protein_kinase_II", new String [] {
			"[M/I/L/V/F/Y]XRXX[pS/pT][M/I/L/V/F/Y]", "RXX[pS/pT]", "[K/F][R/K][Q/M][Q/M/K/L/F]pS[F/I/M/L/V][D/E/I][L/M/K/I][F/K]",
			"[M/V/L/I/F]X[R/K]XX[pS/pT]XX", "RXXpS" 
	}),		
	
	Calmodulin_dependent_protein_kinase_IV ("Calmodulin_dependent_protein_kinase_IV", new String [] {
			"VPGKARKKpSSCQLL", "PLARTLpSVAGLP", "[M/I/L/V/F/Y]XRXX[pS/pT]"
	}),
	
	Casein_Kinase_I ("Casein_Kinase_I", new String [] {"E[F/E]D[T/A/G]GpSI[I/F/Y/G][I/G/F][F/G][F/P/L]",
			"Y[Y/E][D/Y][A/D][A/G]pSI[I/Y/F/G][I/G/F][F/G][F/P/L]", "pS*PXX[pS/pT]",
			"[D/E]XX[pS/pT]", "[pS/pT]XX[S/T]", "[pS/pT]XXX[S/T][M/L/V/I/F]"}),
	
	Casein_Kinase_II ("Casein_Kinase_II", new String [] {"[E/D/A][D/E][E/D][E/D]pS[E/D/A][D/E/A][E/D][E/D]",
			"pS*X[E/pS/pT]", "pS*XX[E/pS/pT]", "[pS/pT]XX[E/D]", "pSDXE", "pSXX[E/D]", 
			"pS[D/E]X[D/E]X[D/E]", "[D/E]pS[D/E]X[D/E]", "pS[D/E][D/E][D/E]", "[pS/pT]XX[D/E]",
			"[pS/pT]*XX[E/D/pS/pY]", "[S/E/P/G][D/S/N/E/P][E/D/G/Q/W][Y/E/D/S/W/T][W/E/D]pS[D/E][D/E/W/N][E/D][E/D/N/Q]"
	}),
	
	Cdc2_kinase ("Cdc2_kinase", new String [] {"[R/K]pSP[R/P][R/K/H]",
			"[pS/pT]PX[R/K]", "HHH[R/K]pSPR[R/K]R"
	}),
	
	Cdc2_like_protein_kinase ("Cdc2_like_protein_kinase", new String [] {"PX[pS/pT]PKKXKK"}),
	
	CDK1_2_4_6_kinase ("CDK1_2_4_6_kinase", new String [] {"[pS/pT]PX[R/K]"}),
			
	TC_PTP_phosphatase ("", new String [] {});
*/
	
	private String description;
	private String [] motifs;
	private String catalogue;
	private HashSet <String> motifSet;
	
	
	public Kinase(String catalogue, String description, String [] motifs){
		this.catalogue = catalogue;
		this.description = description;
		this.motifs = motifs;
	}
	
	public Kinase(String catalogue, String description, HashSet <String> motifSet){
		this.catalogue = catalogue;
		this.description = description;
		this.motifSet = motifSet;
		motifs = this.getMotifArrays();
	}
	
	public Kinase(String catalogue, String description){
		this.catalogue = catalogue;
		this.description = description;
		this.motifSet = new HashSet <String> ();
	}
	
	
	public String getDescription(){
		return description;
	}
	
	public String [] getMotifArrays(){
		String [] motifs = new String [motifSet.size()];
		motifs = motifSet.toArray(motifs);
		return motifs;
	}
	
	public String [] getmotifs(){
		return motifs;
	}
	
	public String getCatalogue(){
		return catalogue;
	}
	
	public HashSet <String> getMotifSet(){
		return motifSet;
	}
	
	public void addMotif(String motif){
		this.motifSet.add(motif);
	}
	
	
}
