/*
 * *****************************************************************************
 * File: ProteinReferencePool.java * * * Created on 08-18-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.util.HashMap;

import cn.ac.dicp.gp1809.exceptions.MyIllegalArgumentException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.util.ObjectPool;

/**
 * A ProteinReference pool used for get the ProteinReference from the protein
 * name or the indexed protein name (index & name)
 * 
 * @author Xinning
 * @version 0.3, 05-21-2010, 14:18:38
 */
public class ProteinReferencePool implements
        ObjectPool<String, ProteinReference> {

	/*
	 * Pool with key of protein name String
	 */
	private HashMap<String, ProteinReference> pool;

	/*
	 * Pool with key of protein index
	 */
	private HashMap<Integer, ProteinReference> intpool;
	
	private IDecoyReferenceJudger judger;

	/**
	 * Create a empty pool for protein reference
	 */
	public ProteinReferencePool(IDecoyReferenceJudger judger) {
		this(512, judger);
	}
	
	/**
	 * Create a empty pool for protein reference
	 */
	public ProteinReferencePool() {
		this(512, null);
	}

	/**
	 * Create a empty pool for protein reference
	 */
	public ProteinReferencePool(int est_count,IDecoyReferenceJudger judger) {
		this.pool = new HashMap<String, ProteinReference>(est_count);
		this.intpool = new HashMap<Integer, ProteinReference>(est_count);
		this.judger = judger;
	}

	/**
	 * Clear the pool before another usage.
	 */
	public void clear() {
		this.pool = new HashMap<String, ProteinReference>(512);
		this.intpool = new HashMap<Integer, ProteinReference>(512);
	}

	/**
	 * Get the ProteinReference for the protein name with index and name.
	 * 
	 * <p>
	 * <b>Notice: if two distinct references (same reference with different
	 * length of partial names will NOT be considered as distinct) are with the
	 * same index, MyIllegalArgumentException will be threw.</b>
	 * </p>
	 * 
	 * <p>
	 * If the index is not unknown please use get(String) method. These two
	 * method using different buffer pools.
	 * 
	 * @param index
	 * @param name
	 * @return throws MyIllegalArgumentException if the two distinct references
	 *         are found with the same index.
	 */
	public ProteinReference get(int index, String name)
	        throws MyIllegalArgumentException {

		if (index < 0) {
			System.out.println(name);
			throw new MyIllegalArgumentException(
			        "The legal index of protein reference is [1, n], current: "
			                + index
			                + ". If the index of current protein is unknown, "
			                + "please use get(String) method.");
		}
		
		//The index == 0 indicate the index of reference is not assigned
		if(index ==0)
			return this.get(name);
		

		ProteinReference ref = null;
		Integer in = index;
		if ((ref = this.intpool.get(in)) == null) {
			
			boolean isfalse = false;
			if(this.judger!=null) {
				isfalse = this.judger.isDecoy(name);
			}
			
			ref = new ProteinReference(in, name, isfalse);
			this.intpool.put(in, ref);
		} else {
			String name2 = ref.getName();
			boolean legal = name2.length() >= name.length() ? name2
			        .startsWith(name) : name.startsWith(name2);
			if (!legal)
				throw new MyIllegalArgumentException(
				        "We got two distinct references with the same index: "
				                + "index. (\"" + name + "\" and \"" + name2
				                + "\")");
		}

		return ref;

	}
	
	/**
	 * If contains the specific protein reference
	 * 
	 * @param index
	 * @param name
	 * @return
	 */
	public boolean contains(int index, String name) {
		ProteinReference ref = this.intpool.get(index);
		if(ref == null)
			return false;
		
		String name2 = ref.getName();
		boolean legal = name2.length() >= name.length() ? name2
		        .startsWith(name) : name.startsWith(name2);
		if (!legal)
			throw new MyIllegalArgumentException(
			        "We got two distinct references with the same index: "
			                + "index. (\"" + name + "\" and \"" + name2
			                + "\")");
		
		return true;
	}
	
	/**
	 * If contains the specific protein reference
	 * 
	 * @param name
	 * @return
	 */
	public boolean contains(String name) {
		return this.pool.get(name) != null;
	}

	@Override
	public ProteinReference get(String name) {
		ProteinReference ref = null;
		if ((ref = this.pool.get(name)) == null) {
			boolean isfalse = false;
			if(this.judger!=null) {
				isfalse = this.judger.isDecoy(name);
			}
			
			ref = new ProteinReference(name, isfalse);
			this.pool.put(name, ref);
		}
		return ref;
	}
}
