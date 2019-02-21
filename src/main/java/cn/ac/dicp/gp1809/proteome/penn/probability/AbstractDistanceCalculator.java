/* 
 ******************************************************************************
 * File: AbstractDistanceCalculator.java * * * Created on 04-08-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;


/**
 * Distance calculator used in KNN for the similarity computation.
 * 
 * @author Xinning
 * @version 0.2.1, 05-14-2008, 20:14:48
 */
public abstract class AbstractDistanceCalculator {
	
	//Other weight are normalized by xcorr weight
	private float xcweight = 1f;
	private float dcnweight = 1f;
	private float spweight = 0f;
	private float rspweight = 0f;
	private float dmsweight = 0f;
	private float ionsweight = 0f;
	private float mpfweight = 0f;
	private float simweight = 0f;
	
	private float xcw2 = 0.25f;
	private float dcnw2 = 0.25f;
	private float spw2 = 0.25f;
	private float rspw2 = 0.25f;
	private float dmsw2 = 0f;
	private float ionsw2 = 0f;
	private float mpfw2 = 0f;
	private float simw2 = 0f;
	
	public AbstractDistanceCalculator(PepNorm[] peps){
		this.calculateWeights(peps);
		
		
//		this.simw2 = 0f;
//		this.mpfw2 = 0f;
//		this.dmsw2 = 0f;
		
//		this.ionsw2 = 0f;
//		this.spw2 = 0f;
//		this.rspw2 = 0f;
//		this.dcnw2 = 0f;
//		this.xcw2 = 0f;
		
		this.normalizeWeight2();
		
		System.out.println("Attribute weights: ");
		System.out.println("Xcorr': "+this.getXcw2());
		System.out.println("DeltaCn: "+this.getDcnw2());
		System.out.println("Sp: "+this.getSpw2());
		System.out.println("ln(Rsp): "+this.getRspw2());
		System.out.println("DeltaMH+: "+this.getDmsw2());
		System.out.println("Ions: "+this.getIonsw2());
		System.out.println("MPF: "+this.getMPFw2());
		System.out.println("Sim: "+this.getSimw2());
	}
	
	/**
	 * Main method for the distance calculator, the weights of different dimension
	 * can be calculated in different algorithms which can be implemented in the 
	 * inherit classes. 
	 * 
	 * @param peps the total instances.
	 */
	protected abstract void calculateWeights(PepNorm[] peps);
	
	
	
	/**
	 * After each reassignment of the attribute weights, this method should be
	 * called so that the sum of weight2 is alway 1. (To make sure that the distance
	 * between O{0, ..., 0} and {1, ..., 1} is always 1)
	 */
	protected void normalizeWeight2(){
		float sumall2 = this.xcw2 + this.dcnw2+this.rspw2+this.spw2+this.dmsw2
							+this.ionsw2+this.mpfw2+this.simw2;
		
		this.xcw2/=sumall2;
		this.dcnw2/=sumall2;
		this.spw2/=sumall2;
		this.rspw2/=sumall2;
		this.dmsw2/=sumall2;
		this.ionsw2/=sumall2;
		this.mpfw2/=sumall2;
		this.simw2/=sumall2;
		
		
		///////////Remove attribute with weight smaller than 0.01
		this.xcw2 = xcw2 >= 0.01f ? xcw2 : 0f;
		this.dcnw2 = dcnw2 >= 0.01f ? dcnw2 : 0f;
		this.spw2 = spw2 >= 0.01f ? spw2 : 0f;
		this.rspw2 = rspw2 >= 0.01f ? rspw2 : 0f;
		this.dmsw2 = dmsw2 >= 0.01f ? dmsw2 : 0f;
		this.ionsw2 = ionsw2 >= 0.01f ? ionsw2 : 0f;
		this.mpfw2  = mpfw2 >= 0.01 ? mpfw2 : 0f;
		this.simw2  = simw2 >= 0.01 ? simw2 : 0f;
		
		sumall2 = this.xcw2 + this.dcnw2+this.rspw2+this.spw2+this.dmsw2
		+this.ionsw2+this.mpfw2+this.simw2;

		this.xcw2/=sumall2;
		this.dcnw2/=sumall2;
		this.spw2/=sumall2;
		this.rspw2/=sumall2;
		this.dmsw2/=sumall2;
		this.ionsw2/=sumall2;
		this.mpfw2/=sumall2;
		this.simw2/=sumall2;
		
		
		this.xcweight = (float) Math.sqrt(this.xcw2);
		this.dcnweight = (float) Math.sqrt(this.dcnw2);
		this.spweight = (float) Math.sqrt(this.spw2);
		this.rspweight = (float) Math.sqrt(this.rspw2);
		this.dmsweight = (float) Math.sqrt(this.dmsw2);
		this.ionsweight = (float) Math.sqrt(this.ionsw2);
		this.mpfweight = (float) Math.sqrt(this.mpfw2);
		this.simweight = (float) Math.sqrt(this.simw2);
	}
	
	/**
	 * <b>Weighted distance between the two peptides. </b>
	 * 
	 * @return The square of distance between the two peptides.
	 * 		   Since the weights in different dimension are different, therefore,
	 * 		   this distance is weighted distance.
	 */
	public float calculateD2(PepNorm pep1, PepNorm pep2){
		float dist2 = 0f;
		if(this.isXcorrUsed()){
			float xcd = pep1.xcn-pep2.xcn;
			dist2 += xcd*xcd*xcw2;
		}
		
		if(this.isDcnUsed()){
			float dcnd = pep1.dcn-pep2.dcn;
			dist2 += dcnd*dcnd*dcnw2;
		}
		
		if(this.isSpUsed()){
			float spd = pep1.spn-pep2.spn;
			dist2 += spw2*spd*spd;
		}
		
		if(this.isRspUsed()){
			float rspd = pep1.rspn-pep2.rspn;
			dist2 += rspw2*rspd*rspd;
		}
		
		if(this.isDeltaMsUsed()){
			float dmsd = pep1.dMS-pep2.dMS;
			dist2 += dmsw2*dmsd*dmsd;
		}
		
		if(this.isIonsUsed()){
			float ionsd = pep1.ions - pep2.ions;
			dist2 += ionsw2*ionsd*ionsd;
		}
		
		if(this.isMPFUsed()){
			float mpfd = pep1.MPF - pep2.MPF;
			dist2 += mpfw2*mpfd*mpfd;
		}
		
		if(this.isSimUsed()){
			float simd = pep1.sim - pep2.sim;
			dist2 += simw2*simd*simd;
		}

		return dist2;
	}
	
	/**
	 * @return if the xcorr scale is used in distance calculation
	 */
	public boolean isXcorrUsed(){
		return this.xcweight < 0.01f ? false : true;
	}
	/**
	 * @return if the Dcn scale is used in distance calculation
	 */
	public boolean isDcnUsed(){
		return this.dcnweight < 0.01f ? false : true;
	}
	/**
	 * @return if the Sp scale is used in distance calculation
	 */
	public boolean isSpUsed(){
		return this.spweight < 0.01f ? false : true;
	}
	/**
	 * @return if the Rsp scale is used in distance calculation
	 */
	public boolean isRspUsed(){
		return this.rspweight < 0.01f ? false : true;
	}
	/**
	 * @return if the DeltaMs scale is used in distance calculation
	 */
	public boolean isDeltaMsUsed(){
		return this.dmsweight < 0.01f ? false : true;
	}
	
	/**
	 * @return if the Ions scale is used in distance calculation
	 */
	public boolean isIonsUsed(){
		return this.ionsweight < 0.01f ? false : true;
	}
	
	/**
	 * @return if the Ions scale is used in distance calculation
	 */
	public boolean isMPFUsed(){
		return this.mpfweight < 0.01f ? false : true;
	}
	
	/**
	 * @return if the Sim scale is used in distance calculation
	 */
	public boolean isSimUsed(){
		return this.simweight < 0.01f ? false : true;
	}
	
	/**
	 * The count of scales.
	 */
	public int getAllScaleCount(){
		return 8;
	}
	
	/**
	 * The count of scales used in distance calculation (the weight > 0).
	 * @return the count of scores used in distance calculation
	 */
	public int getScaleCount(){
		int count = 0;
		
		if (this.isXcorrUsed()) count++;
		if (this.isDcnUsed()) count++;
		if (this.isSpUsed()) count++;
		if (this.isRspUsed()) count++;
		if (this.isDeltaMsUsed()) count++;
		if (this.isIonsUsed()) count ++;
		if (this.isMPFUsed()) count ++;
		if (this.isSimUsed()) count ++;
		
		return count;
	}
	
	/**
	 * Cancel the use of Xcorr for distance calculation
	 */
	public void calcelXcorr(){
		this.xcw2 = 0f;
		this.normalizeWeight2();
		System.out.println("Xcorr scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * Cancel the use of DeltaCn for distance calculation
	 */
	public void calcelDcn(){
		this.dcnw2 = 0f;
		this.normalizeWeight2();
		System.out.println("DeltaCn scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * Cancel the usage of Sp for distance calculation
	 */
	public void cancelSp(){
		this.spw2 = 0f;
		this.normalizeWeight2();
		System.out.println("Sp scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * Cancel the usage of Rsp for distance calculation
	 */
	public void cancelRsp(){
		this.rspw2 = 0f;
		this.normalizeWeight2();
		System.out.println("Rsp scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * Cancel the usage of Dms for distance calculation
	 */
	public void cancelDeltaMs(){
		this.dmsw2 = 0f;
		this.normalizeWeight2();
		System.out.println("DeltaMs scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * Cancel the usage of ions for distance calculation
	 */
	public void cancelIons(){
		this.ionsw2 = 0f;
		this.normalizeWeight2();
		System.out.println("Ions scale canceled for distance calculation, set to 0.");
	}

	/**
	 * Cancel the usage of MPF for distance calculation
	 */
	public void cancelMPF(){
		this.mpfw2 = 0f;
		this.normalizeWeight2();
		System.out.println("MPF scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * Cancel the usage of MPF for distance calculation
	 */
	public void cancelSim(){
		this.simw2 = 0f;
		this.normalizeWeight2();
		System.out.println("Sim scale canceled for distance calculation, set to 0.");
	}
	
	/**
	 * @return the xcw2 square of xcweight
	 */
	public float getXcw2() {
		return xcw2;
	}

	
	/**
	 * @return the dcnw2 square of dcnweight
	 */
	public float getDcnw2() {
		return dcnw2;
	}

	
	/**
	 * @return the spw2 square of spweight
	 */
	public float getSpw2() {
		return spw2;
	}

	
	/**
	 * @return the rspw2 square of rspweight
	 */
	public float getRspw2() {
		return rspw2;
	}

	
	/**
	 * @return the dmsw2 square of dmsweight
	 */
	public float getDmsw2() {
		return dmsw2;
	}


	/**
	 * @return the ionsw2 square of ionsweight
	 */
	public float getIonsw2() {
		return ionsw2;
	}
	
	/**
	 * @return the mobile proton factor
	 */
	public float getMPFw2() {
		return this.mpfw2;
	}
	
	/**
	 * @return the Sim
	 */
	public float getSimw2() {
		return this.simw2;
	}

	/**
	 * @return the xcweight equals sqrt of xcw2;
	 */
	public float getXcweight() {
		return xcweight;
	}

	
	/**
	 * @return the dcnweight
	 */
	public float getDcnweight() {
		return dcnweight;
	}

	
	/**
	 * @return the spweight
	 */
	public float getSpweight() {
		return spweight;
	}

	
	/**
	 * @return the rspweight
	 */
	public float getRspweight() {
		return rspweight;
	}

	
	/**
	 * @return the dmsweight
	 */
	public float getDmsweight() {
		return dmsweight;
	}

	/**
	 * @return the dmsweight
	 */
	public float getIonsweight() {
		return this.ionsweight;
	}
	
	/**
	 * @return the dmsweight
	 */
	public float getMPFweight() {
		return this.mpfweight;
	}
	
	/**
	 * @return the dmsweight
	 */
	public float getSimweight() {
		return this.simweight;
	}
	/**
	 * Set the square weights for different dimensions. Then the weights will
	 * be normalized to 1 automatically. 
	 * 
	 * @param xcw2 xcorr weight2
	 * @param dcnw2 dcn weight2
	 * @param spw2 sp weight2
	 * @param rspw2 rsp weight2
	 * @param dmsw2 delta mass weight2
	 * @param ionsw2 ions percent weight2
	 * @param mpfw2 mpfw2 weight2
	 * @param simw2 sim weight2
	 */
	public final void setWeights2(float xcw2, float dcnw2, float spw2, float rspw2,
			float dmsw2, float ionsw2, float mpfw2, float simw2){
		this.xcw2 = xcw2 > 0f ? xcw2 : 0f;
		this.dcnw2 = dcnw2 > 0f ? dcnw2 : 0f;
		this.spw2 = spw2 > 0f ? spw2 : 0f;
		this.rspw2 = rspw2 > 0f ? rspw2 : 0f;
		this.dmsw2 = dmsw2 > 0f ? dmsw2 : 0f;
		this.ionsw2 = ionsw2 > 0f ? ionsw2 : 0f;
		this.mpfw2  = mpfw2 > 0 ? mpfw2 : 0f;
		this.simw2  = simw2 > 0 ? simw2 : 0f;
		
		this.normalizeWeight2();
	}
	
	/**
	 * Set the square weights for different dimensions. Then the weights will
	 * be normalized to 1 automatically. 
	 * 
	 * @param xcw2 xcorr weight2
	 * @param dcnw2 dcn weight2
	 * @param spw2 sp weight2
	 * @param rspw2 rsp weight2
	 * @param dmsw2 delta mass weight2
	 * @param ionsw2 ions percent weight2
	 * @param mpfw2 peptide mobile proton factor
	 */
	public final void setWeights2(double xcw2, double dcnw2, double spw2, double rspw2,
			double dmsw2, double ionsw2, double mpfw2, double simw2){
		this.setWeights2((float)xcw2, (float)dcnw2, (float)spw2, (float)rspw2, 
				(float)dmsw2, (float)ionsw2, (float)mpfw2, (float)simw2);
	}
}
