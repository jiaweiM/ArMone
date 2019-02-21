/* 
 ******************************************************************************
 * File: OGlycanScanInfo.java * * * Created on 2013-6-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

/**
 * @author ck
 *
 * @version 2013-6-26, 20:16:09
 */
public class OGlycanScanInfo {

	private int type;
	private int count;
	private String scanname;
	private int [] markpeaks;
	private double pepMw;
	private OGlycanUnit [] units;
	private int [] typecount;
	private int cannotsplit;
	private int cansplit;
	
	public OGlycanScanInfo(int type, int count, int [] markpeaks){
		this.type = type;
		this.count = count;
		this.markpeaks = markpeaks;
	}
	
	public OGlycanScanInfo(String line){
		String [] cs = line.split("\t");
		this.type = Integer.parseInt(cs[0]);
		this.count = Integer.parseInt(cs[1]);
		this.pepMw = Double.parseDouble(cs[cs.length-3]);
		this.scanname = cs[cs.length-2];
		String [] ps = cs[cs.length-1].split("_");
		this.markpeaks = new int [ps.length];
		for(int i=0;i<markpeaks.length;i++){
			markpeaks[i] = Integer.parseInt(ps[i]);
		}
		this.typecount = new int [10];
		this.units = new OGlycanUnit[count];
		for(int i=0;i<units.length;i++){
			this.units[i] = OGlycanUnit.valueOf(cs[i*2+2]);
			switch (this.units[i]){
			case core1_1 : 
				typecount[0]++;
				cannotsplit++;
				break;
				
			case core1_2 : 
				typecount[1]++;
				cannotsplit++;
				break;
				
			case core1_3 : 
				typecount[2]++;
				cannotsplit++;
				break;
				
			case core1_4 : 
				typecount[3]++;
				cannotsplit++;
				break;
				
			case core1_5 : 
				typecount[4]++;
				cannotsplit++;
				break;
				
			case core2_1 : 
				typecount[5]++;
				cansplit++;
				break;
				
			case core2_2 : 
				typecount[6]++;
				cansplit++;
				break;
				
			case core2_3 : 
				typecount[7]++;
				cansplit++;
				break;
				
			case core2_4 : 
				typecount[8]++;
				cansplit++;
				break;
				
			case core2_5 : 
				typecount[9]++;
				cansplit++;
				break;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}



	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}



	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
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
	 * @return the markpeaks
	 */
	public int[] getMarkpeaks() {
		return markpeaks;
	}



	/**
	 * @param markpeaks the markpeaks to set
	 */
	public void setMarkpeaks(int[] markpeaks) {
		this.markpeaks = markpeaks;
	}



	/**
	 * @return the pepMw
	 */
	public double getPepMw() {
		return pepMw;
	}



	/**
	 * @param pepMw the pepMw to set
	 */
	public void setPepMw(double pepMw) {
		this.pepMw = pepMw;
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
	 * @return the typecount
	 */
	public int[] getTypecount() {
		return typecount;
	}



	/**
	 * @param typecount the typecount to set
	 */
	public void setTypecount(int[] typecount) {
		this.typecount = typecount;
	}



	/**
	 * @return the cannotsplit
	 */
	public int getCannotsplit() {
		return cannotsplit;
	}



	/**
	 * @param cannotsplit the cannotsplit to set
	 */
	public void setCannotsplit(int cannotsplit) {
		this.cannotsplit = cannotsplit;
	}



	/**
	 * @return the cansplit
	 */
	public int getCansplit() {
		return cansplit;
	}



	/**
	 * @param cansplit the cansplit to set
	 */
	public void setCansplit(int cansplit) {
		this.cansplit = cansplit;
	}



	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
