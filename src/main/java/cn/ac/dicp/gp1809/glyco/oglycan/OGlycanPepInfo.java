/* 
 ******************************************************************************
 * File: GlycoPepInfo.java * * * Created on 2013-1-23
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
import java.util.HashMap;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 * @version 2013-1-23, 14:25:07
 */
public class OGlycanPepInfo {

	private String[] refs;
	private String deleRef;
	private String uniseq;
	private String modseq;
	private String scoreseq;
	private double glycoScore;
	private double baseScore;
	private double pepDeltaScore;
	private double formDeltaScore;
	private int[] position;
	private OGlycanUnit[] units;
	private double[] scores;
	private double[] positionScores;
	private boolean[] determined;
	private double[][] scoreArrays;
	private int[] oxoniumPeaks;
	private double pepmr;
	// private double peptideScore;
	private HashMap<Double, String> matchMap;
	private HashMap<String, String> annotionMap1;
	private HashMap<String, String> annotionMap2;
	private HashMap<String, Boolean> annotionMap3;
	private IPeak[] peaks;
	private IPeptide peptide;
	private String scanname;
	private DecimalFormat df2 = DecimalFormats.DF0_2;
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	private DecimalFormat dfE = DecimalFormats.DF_E3;

	public OGlycanPepInfo(String uniseq, String modseq, String scoreseq, double matchScore, double baseScore,
			int[] position, OGlycanUnit[] units, double[] scores, boolean[] determined, double[] positionScores,
			double[][] scoreArrays, HashMap<Double, String> matchMap, IPeak[] peaks) {
		this.uniseq = uniseq;
		this.modseq = modseq;
		this.scoreseq = scoreseq;
		this.glycoScore = matchScore;
		this.baseScore = baseScore;
		this.position = position;
		this.units = units;
		this.scores = scores;
		this.determined = determined;
		this.positionScores = positionScores;
		this.scoreArrays = scoreArrays;
		this.matchMap = matchMap;
		this.peaks = peaks;
	}

	/**
	 * @return the peptide
	 */
	public IPeptide getPeptide() {
		return peptide;
	}

	/**
	 * @param peptide the peptide to set
	 */
	public void setPeptide(IPeptide peptide) {
		this.peptide = peptide;
	}

	/**
	 * @return the peaks
	 */
	public IPeak[] getPeaks() {
		return peaks;
	}

	/**
	 * @param peaks the peaks to set
	 */
	public void setPeaks(IPeak[] peaks) {
		this.peaks = peaks;
	}

	/**
	 * @return the glycoScore
	 */
	public double getGlycoScore() {
		return glycoScore;
	}

	/**
	 * @param glycoScore the glycoScore to set
	 */
	public void setGlycoScore(double glycoScore) {
		this.glycoScore = glycoScore;
	}

	/**
	 * @return the glycoScore
	 */
	public double getTotalScore() {
		return baseScore + glycoScore;
	}

	/**
	 * @return the baseScore
	 */
	public double getBaseScore() {
		return baseScore;
	}

	/**
	 * @param baseScore the baseScore to set
	 */
	public void setBaseScore(double baseScore) {
		this.baseScore = baseScore;
	}

	/**
	 * @return the matchMap
	 */
	public HashMap<Double, String> getMatchMap() {
		return matchMap;
	}

	/**
	 * @param matchMap the matchMap to set
	 */
	public void setMatchMap(HashMap<Double, String> matchMap) {
		this.matchMap = matchMap;
	}

	/**
	 * @return the uniseq
	 */
	public String getUniseq() {
		return uniseq;
	}

	/**
	 * @param uniseq the uniseq to set
	 */
	public void setUniseq(String uniseq) {
		this.uniseq = uniseq;
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
	 * @return the position
	 */
	public int[] getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int[] position) {
		this.position = position;
	}

	/**
	 * @return the units
	 */
	public OGlycanUnit[] getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(OGlycanUnit[] units) {
		this.units = units;
	}

	/**
	 * @return the scores
	 */
	public double[] getScores() {
		return scores;
	}

	/**
	 * @param scores the scores to set
	 */
	public void setScores(double[] scores) {
		this.scores = scores;
	}

	/**
	 * @return the positionScores
	 */
	public double[] getPositionScores() {
		return positionScores;
	}

	/**
	 * @param positionScores the positionScores to set
	 */
	public void setPositionScores(double[] positionScores) {
		this.positionScores = positionScores;
	}

	/**
	 * @return the scoreArrays
	 */
	public double[][] getScoreArrays() {
		return scoreArrays;
	}

	/**
	 * @param scoreArrays the scoreArrays to set
	 */
	public void setScoreArrays(double[][] scoreArrays) {
		this.scoreArrays = scoreArrays;
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
		this.setDeleRef(refs[0]);
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
	 * @return the annotionMap
	 */
	public HashMap<String, String> getAnnotionMap1() {
		return annotionMap1;
	}

	/**
	 * @param annotionMap the annotionMap to set
	 */
	public void setAnnotionMap1(HashMap<String, String> annotionMap1) {
		this.annotionMap1 = annotionMap1;
	}

	/**
	 * @return the annotionMap2
	 */
	public HashMap<String, String> getAnnotionMap2() {
		return annotionMap2;
	}

	/**
	 * @param annotionMap2 the annotionMap2 to set
	 */
	public void setAnnotionMap2(HashMap<String, String> annotionMap2) {
		this.annotionMap2 = annotionMap2;
	}

	/**
	 * @return the annotionMap3
	 */
	public HashMap<String, Boolean> getAnnotionMap3() {
		return annotionMap3;
	}

	/**
	 * @param annotionMap3 the annotionMap3 to set
	 */
	public void setAnnotionMap3(HashMap<String, Boolean> annotionMap3) {
		this.annotionMap3 = annotionMap3;
	}

	/**
	 * @return the pepDeltaScore
	 */
	public double getPepDeltaScore() {
		return pepDeltaScore;
	}

	/**
	 * @param pepDeltaScore the pepDeltaScore to set
	 */
	public void setPepDeltaScore(double pepDeltaScore) {
		this.pepDeltaScore = pepDeltaScore;
	}

	/**
	 * @return the formDeltaScore
	 */
	public double getFormDeltaScore() {
		return formDeltaScore;
	}

	/**
	 * @param formDeltaScore the formDeltaScore to set
	 */
	public void setFormDeltaScore(double formDeltaScore) {
		this.formDeltaScore = formDeltaScore;
	}

	/**
	 * @return the oxoniumPeaks
	 */
	public int[] getOxoniumPeaks() {
		return oxoniumPeaks;
	}

	/**
	 * @param oxoniumPeaks the oxoniumPeaks to set
	 */
	public void setOxoniumPeaks(int[] oxoniumPeaks) {
		this.oxoniumPeaks = oxoniumPeaks;
	}

	/**
	 * @return the pepmr
	 */
	public double getPepmr() {
		return pepmr;
	}

	/**
	 * @param pepmr the pepmr to set
	 */
	public void setPepmr(double pepmr) {
		this.pepmr = pepmr;
	}

	/**
	 * @return the determined
	 */
	public boolean[] getDetermined() {
		return determined;
	}

	/**
	 * @param determined the determined to set
	 */
	public void setDetermined(boolean[] determined) {
		this.determined = determined;
	}

	public boolean isAllCore14() {
		for (int i = 0; i < this.units.length; i++) {
			if (units[i] != OGlycanUnit.core1_4 && units[i] != OGlycanUnit.core1_4b) {
				return false;
			}
		}
		return true;
	}

	/*	*//**
			 * @return the peptideScore
			 */

	/*
	 * public double getPeptideScore() { return peptideScore; }
	 *//**
		 * @param peptideScore the peptideScore to set
		 */
	/*
	 * public void setPeptideScore(double peptideScore) { this.peptideScore =
	 * peptideScore; }
	 */

	public OGlycanSiteInfo[] getSiteInfo() {

		HashMap<String, SeqLocAround> locmap = peptide.getPepLocAroundMap();
		ProteinReference[] reflist = peptide.getProteinReferences()
				.toArray(new ProteinReference[peptide.getProteinReferences().size()]);

		SeqLocAround sla = null;
		for (int i = 0; i < reflist.length; i++) {
			String name = reflist[i].getName();
			if (refs[0].contains(name)) {
				sla = locmap.get(reflist[i].toString());
				break;
			}
		}
		int seqbeg = sla.getBeg();
		String pre = sla.getPre();
		String nex = sla.getNext();
		if (pre.length() < 7) {
			for (int i = 0; i < 7 - sla.getPre().length(); i++) {
				pre = "_" + pre;
			}
		}
		if (nex.length() < 7) {
			for (int i = 0; i < 7 - sla.getNext().length(); i++) {
				nex = nex + "_";
			}
		}
		String totalseq = pre + uniseq + nex;
		// System.out.println(totalseq+"\t"+sla.getPre()+"\t"+pre+"\t"+sla.getNext()+"\t"+nex);
		OGlycanSiteInfo[] siteInfos = new OGlycanSiteInfo[units.length];
		for (int i = 0; i < siteInfos.length; i++) {
			String site = uniseq.charAt(position[i] - 1) + "" + (seqbeg + position[i] - 1);
			String around = totalseq.substring(position[i] - 1, position[i] + 14);
			// System.out.println(around);
			/*
			 * int begin = position[i]-1>0 ? position[i]-1 : 0; int end =
			 * position[i]+14>totalseq.length() ? totalseq.length() :
			 * position[i]+14; String around = totalseq.substring(begin, end);
			 * 
			 * int ss = 15-around.length(); if(ss>0){ StringBuilder sbadd = new
			 * StringBuilder(); for(int j=0;j<ss;j++){ sbadd.append('_'); }
			 * if(begin==0){ around = sbadd+around; }else{ around =
			 * around+sbadd; } }else if(ss<0){
			 * System.out.println(around+"\t"+sla
			 * .getPre()+"\t"+uniseq+"\t"+sla.getNext
			 * ()+"\t"+begin+"\t"+end+"\t"+position[i]); }
			 */

			double modscore = scores[i];
			// if(this.formDeltaScore<0.2 && this.determined[i] == false){
			// modscore = modscore/(2.0-this.formDeltaScore);
			// }

			siteInfos[i] = new OGlycanSiteInfo(modscore, formDeltaScore, refs, refs[0], modseq, scoreseq, around, site,
					scanname, units[i].getComposition(), units[i].getMass(), determined[i]);
		}

		return siteInfos;
	}

	public void writeAnnotion() {

		Double[] mzs = this.matchMap.keySet().toArray(new Double[this.matchMap.size()]);
		Arrays.sort(mzs);
		for (int i = 0; i < mzs.length; i++) {
			System.out.println(mzs[i] + "\t" + this.matchMap.get(mzs[i]));
		}

		/*
		 * Iterator <String> it1 = this.annotionMap1.keySet().iterator();
		 * while(it1.hasNext()){ String key = it1.next();
		 * System.out.println("1\t"+key+"\t"+this.annotionMap1.get(key)); }
		 * 
		 * Iterator <String> it2 = this.annotionMap2.keySet().iterator();
		 * while(it2.hasNext()){ String key = it2.next();
		 * System.out.println("2\t"+key+"\t"+this.annotionMap2.get(key)); }
		 * 
		 * Iterator <String> it3 = this.annotionMap3.keySet().iterator();
		 * while(it3.hasNext()){ String key = it3.next();
		 * System.out.println("3\t"+key+"\t"+this.annotionMap3.get(key)); }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OGlycanPepInfo [refs=" + Arrays.toString(refs) + ", deleRef=" + deleRef + ", uniseq=" + uniseq
				+ ", modseq=" + modseq + ", glycoScore=" + glycoScore + ", position=" + Arrays.toString(position)
				+ ", units=" + Arrays.toString(units) + ", scoreArrays=" + Arrays.toString(scoreArrays) + ", matchMap="
				+ matchMap + ", peaks=" + Arrays.toString(peaks) + ", peptide=" + peptide + ", scanname=" + scanname
				+ "]";
	}

	public String toStringOutput() {

		StringBuilder sb = new StringBuilder();
		sb.append(scanname).append("\t");
		sb.append(df4.format(peptide.getMH() - AminoAcidProperty.PROTON_W)).append("\t");
		// sb.append(peptide.getDeltaMZppm()).append("\t");
		sb.append(df2.format(peptide.getPrimaryScore())).append("\t");
		sb.append(dfE.format(((MascotPeptide) peptide).getEvalue())).append("\t");
		sb.append(uniseq.length()).append("\t");
		for (int i = 0; i < refs.length; i++) {
			sb.append(refs[i]).append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\t");
		sb.append(deleRef).append("\t");
		sb.append(modseq).append("\t");
		sb.append(scoreseq).append("\t");

		for (int i = 0; i < units.length; i++) {
			sb.append(units[i].getComposition() + "@");
			sb.append(position[i]).append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("\t");
		sb.append(df2.format(formDeltaScore)).append("\t");
		// sb.append(df2.format(glycoScore)).append("\t");

		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
