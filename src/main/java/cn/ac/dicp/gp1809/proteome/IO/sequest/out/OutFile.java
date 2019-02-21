/*
 * *****************************************************************************
 * File: OutFile.java * * * Created on 11-01-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import java.util.Arrays;
import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * A container class for the out file. Containing informations of the identified
 * peptides.
 * 
 * 
 * <p>Changes: 
 * <li>0.3.1, 03-07-2009: Improved the deltaCn calculation. Peptides with different
 * modification sites will not with very small dcn (#calculateDcn_from_xcorr())
 * <li>0.3.2, 03-08-2009: fix bugs for deltaCn calculation.
 * 
 * 
 * @author Xinning
 * @version 0.3.3, 06-29-2009, 22:24:01
 */
public class OutFile {

	/*
	 * This value used to generate the deltaCn value using the same 
	 * algorithm as TPP
	 */
	private static final float PERCENTAGE = 0.75f;
	
	private OutHeader header;

	private boolean has_pro_id;

	private Hit[] hits;

	private ProteinReference[] pro_full_name;

	/**
	 * Given the values to form an OutFile
	 * 
	 * 
	 * 
	 * @param header
	 *            Header informations
	 * @param has_pro_id
	 *            if the out file containing column of "# id". The newer version
	 *            of sequest output .out with "# id".
	 * @param hits
	 *            hits of peptide identifications
	 * @param pros
	 *            the displayed full name of top hit proteins. The number is
	 *            influenced by the "display_top_n_pro" in header. If 0 proteins
	 *            should be output, give an array with 0 length.
	 */
	public OutFile(OutHeader header, boolean has_pro_id, Hit[] hits,
	        ProteinReference[] pro_full_name) {
		this.header = header;
		this.has_pro_id = has_pro_id;
		this.hits = hits;
		this.pro_full_name = pro_full_name;

		this.calculateDcn_from_xcorr_TPP(hits);
	}

	/*
	 * Recalculate the dcn from xcorr values. This is because that the dcn
	 * values initially from out file is not correct.
	 * 
	 * @param hits
	 */
	private void calculateDcn_from_xcorr(Hit[] hits) {
		int len = hits.length;
		if (len == 0)// no hit
			return;

		/*
		 * 1. Use the rank to make sure that peptides identified with same score
		 * but different sequences (e.g. peptides with I and L at the same
		 * positiion) are not with dcn of 0
		 * 
		 * 2. Use the unique sequence (not same) to make sure that peptides with
		 * modification at different positions are not with dcn of 0 or a very
		 * small value
		 */
		
		int num = len - 1;
L1:		for(int m=0; m <= num; m++){
			Hit curtHit = hits[m];
			String seq = curtHit.getUniqueSequence();
			
			//The last one
			if(m == num){
				curtHit.setDcn_from_next_xcorr(1f, true);
				break;
			}
			
			for(int i = m+1; i <= num; i++){
				Hit hit = hits[i];
				String seq2 = hit.getUniqueSequence();
				
				int diff = this.seq_equals(seq, seq2);
				
				//Not the same unique peptide (& KQ IL)
				if(diff > 0){
					float dcn = (curtHit.getXcorr() - hit.getXcorr()) / curtHit.getXcorr();
					boolean special = i - m > 1 ? true : false;
					curtHit.setDcn_from_next_xcorr(dcn, special);
					
					continue L1;
				}
			}
			
			//All the left peptide identifications are with the same sequence
			for(; m<=num; m++)
				hits[m].setDcn_from_next_xcorr(1f, true);
			
			break;
		}
	}

	/*
	 * Use the same algorithm for deltaCn calculation as TPP. The C++ codes of
	 * TPP:
	 * void SequestOut::getDeltaCn() {
	 * for (int i = 0; i < sequestHits_.size()-1; i++) {
	 * int minLen = strlen(sequestHits_[i]->szPlainPep);
	 * int pepLen = minLen;
	 * int noDeltaCnYet = 1;
	 * for (int j = i + 1; j < sequestHits_.size(); j++) {
	 * if ((int)strlen(sequestHits_[j]->szPlainPep) < minLen)
	 * int diffs = 0;
	 * for (int k = 0; k < minLen; k++) {
	 * // K/Q and I/L don't count as differences 
	 * if (sequestHits_[i]->szPlainPep[k] != sequestHits_[j]->szPlainPep[k]) {
	 * if (!((sequestHits_[i]->szPlainPep[k] == 'K' || sequestHits_[i]->szPlainPep[k] == 'Q')
	 * && (sequestHits_[j]->szPlainPep[k] == 'K' || sequestHits_[j]->szPlainPep[k] == 'Q')) &&
	 * !((sequestHits_[i]->szPlainPep[k] == 'I' || sequestHits_[i]->szPlainPep[k] == 'L')
	 * && (sequestHits_[j]->szPlainPep[k] == 'I' || sequestHits_[j]->szPlainPep[k] == 'L'))) {
	 * diffs++;
	 * }
	 * }
	 * }
	 * 
	 *
     * Calculate deltCn only if sequences are less than
     * PERCENTAGE similar;  PERCENTAGE=0.75 for no good reason
     *
     *if ((double) ((double) (pepLen - 3.0 - diffs) /
     *(double) (pepLen - 3.0)) < PERCENTAGE) {
     *sequestHits_[i]->dDeltCn = sequestHits_[j]->dDeltCn;
     *sequestHits_[j]->iDeltCnIdxDiff = i-j;
     *noDeltaCnYet = 0;
     *if (j - i > 1)
     *	sequestHits_[i]->bSpecialDeltCn = 1;
     *else
     *   sequestHits_[i]->bSpecialDeltCn = 0;
     *  break;
     * }
     * }
     * if (noDeltaCnYet == 1)  {
     * // Special dCn because there wasn't a second ranked score
     *   sequestHits_[i]->dDeltCn = 1.0;
     *   sequestHits_[i]->bSpecialDeltCn = 1;
     *  }
     * }
	 * 
	 * 
	 * @param hits
	 */
	private void calculateDcn_from_xcorr_TPP(Hit[] hits) {
		int len = hits.length;
		if (len == 0)// no hit
			return;

		int num = len - 1;
L2:		for(int m=0; m <= num; m++){
			Hit curtHit = hits[m];
			String seq = curtHit.getUniqueSequence();
			
			//The last one
			if(m == num){
				curtHit.setDcn_from_next_xcorr(1f, true);
				break;
			}
			
			//I don't know why TPP use minus 3, just the same as it
			double curtl3 = seq.length() - 3f;
			
			for(int i = m+1; i <= num; i++){
				Hit hit = hits[i];
				String seq2 = hit.getUniqueSequence();
				
				int diff = this.seq_equals(seq, seq2);
				
				//Not the same unique peptide (& KQ IL)
				if((curtl3 - diff) / curtl3 < PERCENTAGE){
					float dcn = (curtHit.getXcorr() - hit.getXcorr()) / curtHit.getXcorr();
					boolean special = i - m > 1 ? true : false;
					curtHit.setDcn_from_next_xcorr(dcn, special);
					
					continue L2;
				}
			}
			//All the left peptide identifications are with the same sequence
			for(; m<=num; m++)
				hits[m].setDcn_from_next_xcorr(1f, true);
			
			break;
		}
	}
	
	/*
	 * Compare the difference between the two unique sequence and return the 
	 * number of different sites
	 */
	private int seq_equals(String useq1, String useq2){
		int diff = 0;
		int minlen = Math.min(useq1.length(), useq2.length());
		
		for(int i=0; i< minlen; i++){
			char c1 = useq1.charAt(i);
			char c2 = useq2.charAt(i);
			
			if(c1 != c2){
				if(!((c1=='K'||c1=='Q')&&(c2=='K'||c2=='Q')) 
						&& !((c1=='I'||c1=='L')&&(c2=='I'||c2=='L')))
					diff ++;
			}
		}
		
		return diff;
	}

	/**
	 * The header of this out file.
	 * 
	 * @return
	 */
	public OutHeader getHeader() {
		return this.header;
	}

	/**
	 * The experimental MH+ value of precursor ion
	 * 
	 * @return
	 */
	public double getExperimentMH() {
		return this.header.getMh();
	}

	/**
	 * The sequest scan name of this out file
	 * 
	 * @return
	 */
	public SequestScanName getscanName() {
		return this.header.getScanName();
	}

	/**
	 * The name of out file for creation of this OutFile instance.
	 * <p>
	 * <b>Note: If this OutFile is created from srf file, this name will be the
	 * formatted out filename: "Basename.scanNumBeg.scanNumEnd.charge.out"</b>
	 * 
	 * @return
	 */
	public String getFilename() {
		return this.header.getFilename();
	}

	/**
	 * The hit peptides. If the out file contains no hit, the return is a hit
	 * array with 0 length. <b>Not null</b>
	 * 
	 * @return
	 */
	public Hit[] getHits() {
		return this.hits;
	}

	/**
	 * The best matched peptide hit. (The top 1 peptide) If there is no match
	 * for the spectrum, return null.
	 * 
	 * @return
	 */
	public Hit getBestHit() {
		if (hits.length == 0)
			return null;

		return hits[0];
	}

	/**
	 * Get topn matched peptide hits for this spectrum. If the topn bigger than
	 * the peptide hits in out file, the returned peptides will be all the
	 * peptides in out file. Otherwise, topn peptides will be returned. If there
	 * is no match for the spectrum, return null.
	 * 
	 * 
	 * @param topn
	 * @return
	 */
	public Hit[] getHits(int topn) {
		int len = hits.length;
		if (len == 0 || topn == 0)
			return null;

		len = Math.min(len, topn);

		return Arrays.copyOfRange(this.hits, 0, len);
	}

	/**
	 * The full name of topn proteins at the end of the out file. The number of
	 * pro full name related to the variable int header.display_topn_pro
	 * 
	 * 
	 * @return the pro_full_name
	 */
	public ProteinReference[] getPro_full_name() {
		return pro_full_name;
	}

	/*
	 * Not completed!
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (this.has_pro_id == true)
			;

		return sb.toString();
	}

	/**
	 * A hit of peptide identification in out file
	 * 
	 * <p>
	 * Changes:
	 * <li>0.1.1, add getUniqueSequence(), add isSpecialDcn_xcorr();
	 * <li>0.1.2, 06-29-2009: add sf score for v.28 and latter
	 * 
	 * @author Xinning
	 * @version 0.1.2, 06-29-2009, 22:23:27
	 */
	public static class Hit {

		private short rank;
		private short rsp;
		private double mh;
		// The original dcn from out file (The 1st ranked peptide is with dcn of
		// 0)
		// used for the output of out file.
		private float dcn;
		private float xcorr;

		private float dcn_from_next_xcorr;
		//Dcn not equals the original (prexcorr-nextxcorr)/prexcorr
		private boolean special_dcn;
		private float sp;
		/**
		 * sf score for SEUEST v.28 and latter
		 */
		private float sf = -1f;
		private String ions;
		private String sequence;
		private String uniseq;
		
		private String labelInfo;

		/*
		 * Note: if print_duplicate_references value equals to 0, no additional
		 * protein reference will be print to out file, then the num_refs will
		 * not equal to the length of printed_refs
		 */
		private int num_refs;

		private HashSet<ProteinReference> refs;

		/**
		 * Create a hit. <b>Note: if print_duplicate_references value equals to
		 * 0, no additional protein reference will be print to out file, then
		 * the num_refs will no equal to the length of printed_refs</b>
		 * 
		 * 
		 * 
		 * @param rank
		 * @param rsp
		 * @param mh
		 * @param dcn
		 * @param xcorr
		 * @param sp
		 * @param ions
		 * @param sequence
		 * @param num_refs
		 *            : number of references containing this peptide. This value
		 *            may not equal to the length of printed_refs.
		 * @param printed_refs
		 *            the hit references which have been output into the out
		 *            file.
		 */
		public Hit(short rank, short rsp, double mh, float dcn, float xcorr,
		        float sp, String ions, String sequence, int num_refs,
		        HashSet<ProteinReference> printed_refs) {

			this.rank = rank;
			this.rsp = rsp;
			this.mh = mh;
			this.dcn = dcn;
			this.xcorr = xcorr;
			this.sp = sp;
			this.ions = ions;
			this.sequence = sequence;
			this.num_refs = num_refs;
			this.refs = printed_refs;
		}
		
		
		/**
		 * Create a hit. <b>Note: if print_duplicate_references value equals to
		 * 0, no additional protein reference will be print to out file, then
		 * the num_refs will no equal to the length of printed_refs</b>
		 * 
		 * <p>For v.28 and latter
		 * 
		 * @param rank
		 * @param rsp
		 * @param mh
		 * @param dcn
		 * @param xcorr
		 * @param sp
		 * @param sf
		 * @param ions
		 * @param sequence
		 * @param num_refs
		 *            : number of references containing this peptide. This value
		 *            may not equal to the length of printed_refs.
		 * @param printed_refs
		 *            the hit references which have been output into the out
		 *            file.
		 */
		public Hit(short rank, short rsp, double mh, float dcn, float xcorr,
		       float sf, float sp, String ions, String sequence, int num_refs,
		        HashSet<ProteinReference> printed_refs) {

			this(rank, rsp, mh, dcn, xcorr, sp, ions, sequence, num_refs, printed_refs);
			
			this.sf = sf;
		}
		
		public Hit(short rank, short rsp, double mh, float dcn, float xcorr,
			       float sf, float sp, String ions, String sequence, int num_refs,
			        HashSet<ProteinReference> printed_refs, String labelInfo) {

				this(rank, rsp, mh, dcn, xcorr, sf, sp, ions, sequence, num_refs, printed_refs);
				this.labelInfo = labelInfo;
			}
		
		/**
		 * Create a hit. Reference can be set afterward
		 * 
		 * @param rank
		 * @param rsp
		 * @param mh
		 * @param dcn
		 * @param xcorr
		 * @param sp
		 * @param ions
		 * @param sequence
		 */
		public Hit(short rank, short rsp, double mh, float dcn, float xcorr,
		        float sp, String ions, String sequence) {

			this.rank = rank;
			this.rsp = rsp;
			this.mh = mh;
			this.dcn = dcn;
			this.xcorr = xcorr;
			this.sp = sp;
			this.ions = ions;
			this.sequence = sequence;
		}

		/**
		 * @param dcn_from_next_xcorr
		 */
		public void setDcn_from_next_xcorr(float dcn_from_next_xcorr, boolean isSpecial) {
			this.dcn_from_next_xcorr = dcn_from_next_xcorr;
			this.special_dcn = isSpecial;
		}

		/**
		 * @return the rank
		 */
		public short getRank() {
			return rank;
		}

		/**
		 * @return the rsp
		 */
		public short getRsp() {
			return rsp;
		}

		/**
		 * @return the mh
		 */
		public double getMh() {
			return mh;
		}

		/**
		 * The original dcn from out file (The 1st ranked peptide is with dcn of
		 * 0)
		 * 
		 * @return the dcn
		 */
		public float getDcn() {
			return dcn;
		}

		/**
		 * This is the true value of dcn for a peptide identification. Equals
		 * (xcorr - next_xcorr)/next_xcorr. if the next_xcorr is 0, the dcn will
		 * be 1;
		 * 
		 * @return
		 */
		public float getDcn_from_next_xcorr() {
			return this.dcn_from_next_xcorr;
		}
		
		/**
		 * The dcn may not from the next xcorr or may be the 1 when this peptide 
		 * is the last identified one
		 * 
		 * @return
		 */
		public boolean isSpecialDcn_xcorr(){
			return this.special_dcn;
		}

		/**
		 * @return the xcorr
		 */
		public float getXcorr() {
			return xcorr;
		}

		/**
		 * @return the sp
		 */
		public float getSp() {
			return sp;
		}

		/**
		 * @return the ions
		 */
		public String getIons() {
			return ions;
		}

		/**
		 * @return the sequence
		 */
		public String getSequence() {
			return sequence;
		}
		
		/**
		 * A lazy method to get the unique sequence
		 * 
		 * @return
		 */
		public String getUniqueSequence(){
			if(this.uniseq == null)
				this.uniseq = PeptideUtil.getUniqueSequence(this.sequence);
			
			return this.uniseq;
		}

		/**
		 * @return the refs
		 */
		public HashSet<ProteinReference> getRefs() {
			return refs;
		}

		/**
		 * <b>Note: if print_duplicate_references value equals to 0, no
		 * additional protein reference will be print to out file, then the
		 * num_refs will not equal to the length of printed_refs</b>
		 * 
		 * @return the num_refs
		 */
		public int getNum_refs() {
			return num_refs;
		}

		/**
		 * <b>Note: if print_duplicate_references value equals to 0, no
		 * additional protein reference will be print to out file, then the
		 * num_refs will not equal to the length of printed_refs</b>
		 * 
		 * @param num_refs
		 *            the num_refs to set
		 */
		public void setNum_refs(int num_refs) {
			this.num_refs = num_refs;
		}

		/**
		 * @param refs
		 *            the refs to set
		 */
		public void setRefs(HashSet<ProteinReference> refs) {
			this.refs = refs;
		}
		
		/**
		 * The sf score, for v.28 and latter. If not set, return -1
		 * 
		 * @return
		 */
		public float getSf() {
			return this.sf;
		}
		
		/**
		 * The sf score, for v.28 and latter
		 * 
		 * @param sf
		 */
		public void setSf(float sf) {
			this.sf = sf;
		}

		public String getLabelInfo(){
			return labelInfo;
		}
		
		public void setLabelInfo(String labelInfo){
			this.labelInfo = labelInfo;
		}
		
		/*
		 * Not complete!
		 * 
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();

			return sb.toString();
		}
	}
}
