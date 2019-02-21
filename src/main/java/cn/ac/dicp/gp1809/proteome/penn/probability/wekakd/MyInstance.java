/* 
 ******************************************************************************
 * File: MyInstance.java * * * Created on 12-24-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
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
package cn.ac.dicp.gp1809.proteome.penn.probability.wekakd;

import weka.core.DenseInstance;

/**
 * 
 * @author Xinning
 * @version 0.1, 12-24-2007, 22:27:15
 */
public class MyInstance extends DenseInstance {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idx;
	private boolean isRev;
	private float score;
	private int clusterType;
	private int rank;
	private float qValue;
	
	public MyInstance(double[] attValues, int idx, boolean isRev) {
		super(1, attValues);
		this.idx = idx;
		this.isRev = isRev;
	}
	
	/**
	 * @param weight
	 * @param attValues
	 */
	public MyInstance(double[] attValues, int idx, boolean isRev, float score) {
		super(1, attValues);
		this.idx = idx;
		this.isRev = isRev;
		this.score = score;
	}
	
	/**
	 * 
	 * @param attValues
	 * @param idx
	 * @param isRev
	 * @param score
	 * @param rank
	 */
	public MyInstance(double[] attValues, int idx, boolean isRev, float score, int rank) {
		super(1, attValues);
		this.idx = idx;
		this.isRev = isRev;
		this.score = score;
		this.rank = rank;
	}
	
	public MyInstance(MyInstance instance) {
		super(instance);
		this.idx = instance.getIdx();
		this.isRev = instance.isRev;
	}

	public int getIdx(){
		return this.idx;
	}

	  @Override
	public Object copy() {
		  return this;
	}
	  
	public boolean isRev(){
		return this.isRev;
	}
	
	public float getScore(){
		return this.score;
	}
	
	public void setClusterType(int clusterType){
		this.clusterType = clusterType;
	}
	
	public int getClusterType(){
		return clusterType;
	}
	
	public int getRank(){
		return rank;
	}
	
	public void setQValue(float qValue){
		this.qValue = qValue;
	}
	
	public float getQValue(){
		return qValue;
	}
	
}
