/* 
 ******************************************************************************
 * File: DataType.java * * * Created on 2010-12-14
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

/**
 * @author ck
 *
 * @version 2010-12-14, 18:43:57
 */
public enum DataType {
	
	Generic ("Generic", 0),
	
	Phospho ("Phosphoproteome", 1);
	
	private String name;
	private int type;
	
	private DataType(String name, int type){
		this.name = name;
		this.type = type;
	}

	public String getName(){
		return name;
	}
	
	public int getType(){
		return type;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public static DataType getTypebyIndex(int index) {
		DataType [] types = DataType.values();
		for (DataType type : types) {
			//The name and index must both equals
			if (type.getType() == index) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unkown type for index: " + index);
	}
	
	public static DataType getTypebyName(String name) {
		DataType [] types = DataType.values();
		for (DataType type : types) {
			//The name and index must both equals
			if (type.getName().equals(name)) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unkown type for name: " + name);
	}
	
	
}
