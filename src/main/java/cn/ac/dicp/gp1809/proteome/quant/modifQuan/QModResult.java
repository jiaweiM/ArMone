/* 
 ******************************************************************************
 * File:QModProtein.java * * * Created on 2010-7-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.modifQuan;

import java.util.Arrays;
import java.util.HashMap;

/**
 * The quantitative result of one mod for one protein reference.
 * 
 * @author ck
 *
 * @version 2010-7-12, 21:23:54
 */
public class QModResult {

	private ModInfo mod;
	private String modDes;
	private HashMap <Integer, ModifLabelPair> pairMap;
	
	public QModResult(ModInfo mod){
		this.mod = mod;
		this.pairMap = new HashMap <Integer, ModifLabelPair>();
	}
	
	public QModResult(String modDes){
		this.modDes = modDes;
		this.pairMap = new HashMap <Integer, ModifLabelPair>();
	}
	
	public QModResult(ModInfo mod, HashMap <Integer, ModifLabelPair> pairMap){
		this.mod = mod;
		this.pairMap = pairMap;		
	}

	public void addModPair(ModifLabelPair pair){
		if(pair.use()){
			int loc = pair.getLoc();
			if(pairMap.containsKey(loc)){
				this.pairMap.get(loc).add(pair);
			}else{
				this.pairMap.put(loc, pair);
			}
		}		
	}

	public ModInfo getModDes(){
		return mod;
	}
	
	public HashMap <Integer, ModifLabelPair> getPairMap(){
		return pairMap;
	}
	
	public boolean validata(){
		return this.pairMap.size()>0;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(mod==null){
			sb.append(modDes).append("\t");
		}else{
			sb.append(mod.getDes()).append("\t");
		}		
		Integer [] locs = pairMap.keySet().toArray(new Integer[pairMap.size()]);
		Arrays.sort(locs);
		
		ModifLabelPair pair = pairMap.get(locs[0]);
		sb.append(pair+"\n");
		for(int i=1;i<locs.length;i++){
			ModifLabelPair pair1 = pairMap.get(locs[i]);
			sb.append("\t"+pair1+"\n");
		}		
		return sb.toString();
	}
	
}
