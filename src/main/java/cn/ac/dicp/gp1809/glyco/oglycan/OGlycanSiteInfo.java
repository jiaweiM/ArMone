/* 
 ******************************************************************************
 * File: OGlycanSiteInfo.java * * * Created on 2013-2-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.text.DecimalFormat;
import java.util.Arrays;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2013-2-25, 15:52:30
 */
public class OGlycanSiteInfo {
	
	private double score;
	private double glycoFormDeltaScore;
	private String [] refs;
	private String deleRef;
	private String modseq;
	private String scoreseq;
	private String seqAround;
	private String site;
	private String scanname;
	private String glycoName;
	private double glycoMass;
	private boolean determined;
	
	private DecimalFormat df2 = DecimalFormats.DF0_2;

	/**
	 * @param refs
	 * @param deleRef
	 * @param modseq
	 * @param seqAround
	 * @param site
	 * @param scanname
	 * @param glycoName
	 * @param glycoMass
	 */
	public OGlycanSiteInfo(double score, double glycoFormDeltaScore, String[] refs, String deleRef, String modseq, String scoreseq,
			String seqAround, String site, String scanname, String glycoName,
			double glycoMass, boolean determined) {

		this.score = score;
		this.glycoFormDeltaScore = glycoFormDeltaScore;
		this.refs = refs;
		this.deleRef = deleRef;
		this.modseq = modseq;
		this.scoreseq = scoreseq;
		this.seqAround = seqAround;
		this.site = site;
		this.scanname = scanname;
		this.glycoName = glycoName;
		this.glycoMass = glycoMass;
		this.determined = determined;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the glycoFormDeltaScore
	 */
	public double getGlycoFormDeltaScore() {
		return glycoFormDeltaScore;
	}

	/**
	 * @param glycoFormDeltaScore the glycoFormDeltaScore to set
	 */
	public void setGlycoFormDeltaScore(double glycoFormDeltaScore) {
		this.glycoFormDeltaScore = glycoFormDeltaScore;
	}

	/**
	 * @return the refs
	 */
	public String[] getRefs() {
		return refs;
	}
	/**
	 * @param refs the refs to set
	 */
	public void setRefs(String[] refs) {
		this.refs = refs;
	}
	/**
	 * @return the deleRef
	 */
	public String getDeleRef() {
		return deleRef;
	}
	/**
	 * @param deleRef the deleRef to set
	 */
	public void setDeleRef(String deleRef) {
		this.deleRef = deleRef;
	}

	/**
	 * @return the modseq
	 */
	public String getModseq() {
		return modseq;
	}

	/**
	 * @param modseq the modseq to set
	 */
	public void setModseq(String modseq) {
		this.modseq = modseq;
	}

	/**
	 * @return the seqAround
	 */
	public String getSeqAround() {
		return seqAround;
	}
	/**
	 * @param seqAround the seqAround to set
	 */
	public void setSeqAround(String seqAround) {
		this.seqAround = seqAround;
	}
	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}
	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}
	/**
	 * @return the scanname
	 */
	public String getScanname() {
		return scanname;
	}
	/**
	 * @param scanname the scanname to set
	 */
	public void setScanname(String scanname) {
		this.scanname = scanname;
	}
	/**
	 * @return the glycoName
	 */
	public String getGlycoName() {
		return glycoName;
	}
	/**
	 * @param glycoName the glycoName to set
	 */
	public void setGlycoName(String glycoName) {
		this.glycoName = glycoName;
	}
	/**
	 * @return the glycoMass
	 */
	public double getGlycoMass() {
		return glycoMass;
	}
	/**
	 * @param glycoMass the glycoMass to set
	 */
	public void setGlycoMass(double glycoMass) {
		this.glycoMass = glycoMass;
	}
	
	/**
	 * @return the scoreseq
	 */
	public String getScoreseq() {
		return scoreseq;
	}

	/**
	 * @param scoreseq the scoreseq to set
	 */
	public void setScoreseq(String scoreseq) {
		this.scoreseq = scoreseq;
	}

	/**
	 * @return the determined
	 */
	public boolean isDetermined() {
		return determined;
	}

	/**
	 * @param determined the determined to set
	 */
	public void setDetermined(boolean determined) {
		this.determined = determined;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OGlycanSiteInfo [refs=" + Arrays.toString(refs) + ", deleRef="
				+ deleRef + ", seqAround=" + seqAround + ", site=" + site
				+ ", scanname=" + scanname + ", glycoName=" + glycoName
				+ ", glycoMass=" + glycoMass + "]";
	}
	
	public String toStringOutput(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(site).append("\t");
		sb.append(seqAround).append("\t");
		sb.append(glycoName).append("\t");
		sb.append(glycoMass).append("\t");
		sb.append(df2.format(glycoFormDeltaScore)).append("\t");
		sb.append(df2.format(score)).append("\t");
		for(int i=0;i<refs.length;i++){
			sb.append(refs[i]).append(";");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("\t");
		sb.append(deleRef).append("\t");
		sb.append(scanname).append("\t");
		sb.append(modseq).append("\t");
		sb.append(scoreseq).append("\t");
		/*if(determined){
			sb.append("Yes\t");
		}else{
			sb.append("No\t");
		}*/
		
		return sb.toString();
	}

}
