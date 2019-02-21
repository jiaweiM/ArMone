/* 
 ******************************************************************************
 * File: SFOERMascotCriteria.java * * * Created on 2011-9-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide;

import java.util.Arrays;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * @author ck
 *
 * @version 2011-9-1, 09:31:37
 */
public class SFOERMascotCriteria implements IPeptideCriteria<IMascotPeptide> {


	/**
	 * The criteria
	 */
	private static final long serialVersionUID = 1L;

	private short maxcharge;
	private float[] ionScore;
	private float[] deltaIS;
	private float[] IS_MHT;
	private float[] IS_MIT;
	private double [] evalue;

	private final String name = "SFOERMascotCriteria";

	/**
	 * for convient, the index of each filter is the charge state. For example,
	 * if the filter is 2, 2.5, 3.8 for charge state of 1+, 2+ and 3+, the xcorr
	 * filter array should be float[]{0, 2, 2.5, 3.8} with length of 4. Peptides
	 * with other charge states will be removed.
	 * 
	 * @param ionScore
	 *            use charge as index
	 * @param deltaIS
	 *            use charge as index
	 */
	public SFOERMascotCriteria(float[] ionScore, float[] deltaIS, float[] IS_MHT,
			float [] IS_MIT) {
		this.maxcharge = 9;

		this.ionScore = new float[maxcharge + 1];
		this.deltaIS = new float[maxcharge + 1];
		this.IS_MHT = new float[maxcharge + 1];
		this.IS_MIT = new float[maxcharge + 1];

		Arrays.fill(this.IS_MIT, (short) 500);

		if (ionScore != null && ionScore.length != 0) {
			int len = ionScore.length;
			System.arraycopy(ionScore, 0, this.ionScore, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = ionScore[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.ionScore[i] = value;
			}
		}

		if (deltaIS != null && deltaIS.length != 0) {
			int len = deltaIS.length;
			System.arraycopy(deltaIS, 0, this.deltaIS, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = deltaIS[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.deltaIS[i] = value;
			}
		}

		if (IS_MHT != null && IS_MHT.length != 0) {
			int len = IS_MHT.length;
			System.arraycopy(IS_MHT, 0, this.IS_MHT, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = IS_MHT[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.IS_MHT[i] = value;
			}
		}

		if (IS_MIT != null && IS_MIT.length != 0) {
			int len = IS_MIT.length;
			System.arraycopy(IS_MIT, 0, this.IS_MIT, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = IS_MIT[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.IS_MIT[i] = value;
			}
		}
/*		
		if (deltaMSppms != null && deltaMSppms.length != 0) {
			int len = deltaMSppms.length;
			System.arraycopy(deltaMSppms, 0, this.deltaMSppms, 1, len);

			float value = deltaMSppms[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.deltaMSppms[i] = value;
			}
		}
*/	
	}

	public SFOERMascotCriteria(float[] ionScore, float[] IS_MHT,
			float [] IS_MIT, double [] evalue) {
		
		this.maxcharge = 9;

		this.ionScore = new float[maxcharge + 1];
		this.IS_MHT = new float[maxcharge + 1];
		this.IS_MIT = new float[maxcharge + 1];
		this.evalue = new double[maxcharge + 1];

		if (ionScore != null && ionScore.length != 0) {
			int len = ionScore.length;
			System.arraycopy(ionScore, 0, this.ionScore, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = ionScore[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.ionScore[i] = value;
			}
		}

		if (IS_MHT != null && IS_MHT.length != 0) {
			int len = IS_MHT.length;
			System.arraycopy(IS_MHT, 0, this.IS_MHT, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = IS_MHT[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.IS_MHT[i] = value;
			}
		}

		if (IS_MIT != null && IS_MIT.length != 0) {
			int len = IS_MIT.length;
			System.arraycopy(IS_MIT, 0, this.IS_MIT, 1, len);

			/*
			 * bigger than the charge in criteria, set as the same
			 */
			float value = IS_MIT[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.IS_MIT[i] = value;
			}
		}
		
		if (evalue != null && evalue.length != 0) {
			int len = evalue.length;
			System.arraycopy(evalue, 0, this.evalue, 1, len);

			double value = evalue[len - 1];
			for (int i = len+1; i <= this.maxcharge; i++) {
				this.evalue[i] = value;
			}
		}
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#filter(cn
	 * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean filter(IMascotPeptide pep) {
		int charge = pep.getCharge();

		if (charge <= this.maxcharge && pep.getIonscore() >= this.ionScore[charge]
		        && (pep.getIonscore()-pep.getHomoThres()) >= this.IS_MHT[charge]
		        && (pep.getIonscore()-pep.getIdenThres()) >= this.IS_MIT[charge] 
		        && pep.getEvalue() <= this.evalue[charge]                                                    
				){
			
			return pep.setUsed(true);
		}

		return pep.setUsed(false);
	}

	@Override
	public PeptideType getPeptideType() {
		return PeptideType.MASCOT;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria#getFilterName()
	 */
	@Override
	public String getFilterName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof SFOERSequestCriteria){
			SFOERMascotCriteria c = (SFOERMascotCriteria) obj;
			String n0 = this.name;
			String n1 = c.name;
			return n0.equals(n1);
		}else{
			return false;
		}
	}

	@Override
	public int hashCode(){
		return this.name.hashCode();
	}


}
