/* 
 ******************************************************************************
 * File: PepNormComparator.java * * * Created on 04-08-2008
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

import java.util.Comparator;

/**
 * Comparator used for array sort of the PepNorm
 * 
 * @author Xinning
 * @version 0.2, 06-02-2008, 21:35:50
 */
public class PepNormComparator implements Comparator<PepNorm> {
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_XCORR = 0;
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_DCN = 1;
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_SP = 2;
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_RSP = 3;
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_DMS = 4;

	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_IONS = 5;
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_MPF = 6;
	
	/**
	 * Using Xcorr attribute to sort the PepNorm array.
	 */
	public static final int SORT_BY_SIM = 7;
	
	/**
	 * Sort by the distance away from the origin.
	 */
	public static final int SORT_BY_AWAY = 8;
	
	private Comparator<PepNorm> comparator;
	
	public PepNormComparator(int sortby){
		
		switch (sortby){
		case SORT_BY_XCORR: this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getXcn()==o2.getXcn())
										return 0;
									return o1.getXcn()>o2.getXcn()?1:-1;
								}
							}; break;
							
		case SORT_BY_DCN:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getDcn()==o2.getDcn())
										return 0;
									return o1.getDcn()>o2.getDcn()?1:-1;
								}
							}; break;
							
		case SORT_BY_SP:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getSpn()==o2.getSpn())
										return 0;
									return o1.getSpn()>o2.getSpn()?1:-1;
								}
							}; break;
							
		case SORT_BY_RSP:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getRspn()==o2.getRspn())
										return 0;
									return o1.getRspn()>o2.getRspn()?1:-1;
								}
							}; break;
							
		case SORT_BY_DMS:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getDMS()==o2.getDMS())
										return 0;
									return o1.getDMS()>o2.getDMS()?1:-1;
								}
								
							}; break;
							
		case SORT_BY_IONS:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getIons()==o2.getIons())
										return 0;
									return o1.getIons()>o2.getIons()?1:-1;
								}
							}; break;
		case SORT_BY_MPF:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getMPF()==o2.getMPF())
										return 0;
									return o1.getMPF()>o2.getMPF()?1:-1;
								}
							}; break;
		case SORT_BY_SIM:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getSim()==o2.getSim())
										return 0;
									return o1.getSim()>o2.getSim()?1:-1;
								}
							}; break;
		case SORT_BY_AWAY:	this.comparator = new Comparator<PepNorm>(){
								public int compare(PepNorm o1, PepNorm o2) {
									if(o1.getAway()==o2.getAway())
										return 0;
									return o1.getAway()>o2.getAway()?1:-1;
								}
							}; break;				
		default : throw new NullPointerException("A unknown sorter type.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(PepNorm o1, PepNorm o2) {
		return comparator.compare(o1, o2);
	}

}
