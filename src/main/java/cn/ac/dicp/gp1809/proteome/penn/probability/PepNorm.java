/*
 * *****************************************************************************
 * File: file_name * * * Created on 10-10-2007 Copyright (c) 2007 Xinning Jiang
 * vext@163.com This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;

/**
 * Containing normalized values for a peptide.
 * 
 * @author Xinning
 * @version 0.3.5, 05-14-2008, 19:54:45
 */
public class PepNorm implements Comparable<PepNorm>{
	/**
	 * The index of this peptide.
	 * The index is from 0-n, where it is read from.
	 * 0 indicating that this pep is read from the file firstly.
	 */
	int idx;
	
	/**
	 * Is this pep from reversed db?
	 */
	boolean isRev;
	/**
	 * The normalized xcorr
	 */
	float xcn;
	/**
	 * The normalized delta cn
	 */
	float dcn;
	/**
	 * The normalized Sp
	 */
	float spn;
	/**
	 * The normalized Rsp
	 */
	float rspn;
	
	/**
	 * Delta mass
	 */
	float dMS;
	
	/**
	 * The probablity to be true
	 */
	float probablity = -1f;
	
	/*
	 * The ions percent
	 */
	float ions;
	
	/*
	 * The distance away to the origin (O).
	 */
	float away;
	
	/*
	 * The protein mobile proton factor
	 */
	float MPF;
	
	/*
	 * Sim score in kineticModel
	 */
	float sim;
	
	/**
	 * The standard derivation of the probability
	 */
//	float SD;
	
	/**
	 * Count used for probability calculation. tmp
	 */
	int count;
	
	@Override
	public int compareTo(PepNorm o) {
		if(this.idx == o.idx)
			return 0;
		
		return idx > o.idx ? 1 : -1;
	}
	
	
	/**
	 * The distant of current peptide and the specific peptide.
	 */
	final static class Distant implements Comparable<Distant>{
		public float distant;
		public int idx;
		public Distant(float distant, int idx){
			this.distant = distant;
			this.idx = idx;
		}

		public int compareTo(Distant o) {
			float d = distant-o.distant;
			if(d==0)
				return 0;
			return d<0 ? -1 : 1;
		}
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ID:").append(this.idx).append(";Xc:").append(this.xcn).append(";Dcn:")
		.append(this.dcn).append(";Sp:").append(this.spn).append(";Rsp:").append(this.rspn)
		.append(";DeltaMS:").append(this.dMS).append(";Ions:").append(this.ions).append(";MPF:")
		.append(this.MPF).append(";Sim:").append(this.sim);
		return sb.toString();
	}

	/**
	 * @return the idx
	 */
	public int getIdx() {
		return idx;
	}

	/**
	 * @return the isRev
	 */
	public boolean isRev() {
		return isRev;
	}

	/**
	 * @return the xcn
	 */
	public float getXcn() {
		return xcn;
	}

	/**
	 * @return the dcn
	 */
	public float getDcn() {
		return dcn;
	}

	/**
	 * @return the spn
	 */
	public float getSpn() {
		return spn;
	}

	/**
	 * @return the rspn
	 */
	public float getRspn() {
		return rspn;
	}

	/**
	 * @return the dMS
	 */
	public float getDMS() {
		return dMS;
	}

	/**
	 * @return the probablity
	 */
	public float getProbablity() {
		return probablity;
	}

	/**
	 * @return the sD
	 */
//	public float getSD() {
//		return SD;
//	}
	
	/**
	 * @return the matched peak ratio
	 */
	public float getIons(){
		return this.ions;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * @return the distance away from the origin
	 */
	public float getAway(){
		return this.away;
	}
	
	/**
	 * The peptide mobile proton factor.
	 */
	public float getMPF(){
		return this.MPF;
	}

	/**
     * @return the sim
     */
    public float getSim() {
    	return sim;
    }
}
