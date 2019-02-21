/* 
 ******************************************************************************
 * File:LabelType.java * * * Created on 2010-5-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;

/**
 * @author ck
 *
 * @version 2010-5-17, 09:30:48
 */
public enum LabelType {

	Dimethyl("Dimethyl", new LabelInfo[][] {{},{},{}}),
	
	SILAC("SILAC", new LabelInfo [][] {}),
	
	ICPL("ICPL", new LabelInfo [][] {}),

	User_Defined("User_Defined", new LabelInfo [][] {}),
	
	FiveLabel("FiveLabel", new LabelInfo [][]{}),
	
	SixLabel("SixLabel", new LabelInfo [][]{}),
	
	LabelFree("Label_Free", null)
	
	;

	private String name;
	private LabelInfo [][] infos;
	private short [] used;
	private HashMap <ModSite, float[]> massMap;
	private HashMap <Character, Double> symbolMap;
	private String [] fileNames;
	
	LabelType(String name, LabelInfo [][] infos){
		this.name = name;
		
		if(infos!=null)
			this.setInfo(infos);
	}
	
	public void setInfo(LabelInfo [][] infos){
		this.infos = infos;
		this.massMap = new HashMap <ModSite, float[]>();
		this.symbolMap = new HashMap <Character, Double>();
		HashSet <ModSite> modSet = new HashSet <ModSite>();
		for(int i=0;i<infos.length;i++){
			for(int j=0;j<infos[i].length;j++){
				modSet.add(infos[i][j].getSite());
				symbolMap.put(infos[i][j].getSymbol(), infos[i][j].getMass());
			}
		}
		Iterator <ModSite> it = modSet.iterator();
		while(it.hasNext()){
			ModSite site = it.next();
			float [] mass = new float[infos.length];
			Arrays.fill(mass, 0);
			for(int i=0;i<infos.length;i++){
				for(int j=0;j<infos[i].length;j++){
					if(site.equals(infos[i][j].getSite())){
						mass[i] = (float) infos[i][j].getMass();
						break;
					}
				}
			}
			massMap.put(site, mass);
		}
	}
	
	public LabelInfo [][] getInfo(){
		return infos;
	}
	
	public void setUsed(short [] used){
		this.used = used;
	}
	
	public short [] getUsed(){
		if(this==LabelType.SixLabel){
			return new short []{1, 2, 3, 4, 5, 6};
			
		}else if(this==LabelType.FiveLabel){
			return new short []{1, 2, 3, 4, 5};
			
		}else{
			return used;
		}
	}
	
	public String getLabelName(){
		return name;
	}

	public int getLabelNum(){
		return getUsed().length;
	}
	
	public int getRatioNum(){
		int labelNum = getLabelNum();
		return labelNum*(labelNum-1)/2;
	}
	
	public static LabelType getLabelType(String name){
		for(LabelType type : LabelType.values()){
			if(type.name.equalsIgnoreCase(name)){
				return type;
			}
		}
		return null;
	}
	
	public int getIsoIndex(ModSite site, double mass){
		if(massMap.containsKey(site)){
			float [] masses = massMap.get(site);
			for(int i=0;i<masses.length;i++){
				if(Math.abs(masses[i]-mass)<0.1){
					return i;
				}
			}
			return -1;
		}else{
			return -1;
		}
	}

	public int getIsoType(ModSite site, float mass){
		if(massMap.containsKey(site)){
			float [] masses = massMap.get(site);
			for(int i=0;i<masses.length;i++){
				if(Math.abs(masses[i]-mass)<0.1){
					return used[i];
				}
			}
			return 0;
		}else{
			return 0;
		}
	}
	
	public HashMap <ModSite, float[]> getMassMap(){
		return massMap;
	}
	
	public void setSymbolSet(HashMap <Character, Double> symbolMap){
		this.symbolMap = symbolMap;
	}
	
	public HashMap <Character, Double> getSymbolMap(){
		return symbolMap;
	}
	
	public double getAddMass(char symbol){
		if(symbolMap.containsKey(symbol)){
			double d = symbolMap.get(symbol);
			return d;
		}else{
			return -1;
		}
	}
	
	public void setFileNames(String [] fileNames){
		this.fileNames = fileNames;
	}
	
	public String [] getFilesNames(){
		return fileNames;
	}

	public static void main(String [] args){
		LabelType type = LabelType.LabelFree;
		System.out.println(type);
	}
	
/*		
		LabelFree("LabelFree",null,null,null,null,null),
		
		SILAC("SILAC", new short[]{1,2,3}, new short[]{0,0,0}, new String []{"","",""},
				new float []{0, 6.020126, 0}, 
				new ModSite[]{ModSite.newInstance_aa('K')}),
		
		Dimethyl("Dimethyl",new short[]{1,2,3}, new short[]{0,0,0}, new String []{"","",""},
				new float []{28.0313, 32.0564, 36.0815},
				new ModSite[]{ModSite.newInstance_aa('K'),ModSite.newInstance_PepNterm()}),
		
		iTRAQ("iTRAQ",null,null,null,null,null),
		
		MRM("MRM",null,null,null,null,null),
		
		UserDefault("UserDefault",new short[]{1,2}, new short[]{0,0}, new String []{"",""},
				new float []{28.031296, 34.068954},
				new ModSite[]{ModSite.newInstance_aa('K'),ModSite.newInstance_aa('R')});
		
		private String name;
		private short [] type;
		private short [] used;
		private String [] symbols;
		private float [] mass;
		private ModSite [] mods;
		
		LabelType(String name, short [] type, short [] used, String [] symbols, float [] mass, ModSite [] mods){
			this.name = name;
			this.type = type;
			this.used = used;
			this.symbols = symbols;
			this.mass = mass;
			this.mods = mods;
		}
		
		public String getLabelName(){
			return name;
		}
		
		public short[] getIsoType(){
			return type;
		}
		
		public short [] getUsed(){
			return used;
		}
		
		public String [] getSymbols(){
			return symbols;
		}
		
		public ModSite[] getMods(){
			return mods;
		}
		
		public float [] getMass(){
			return mass;
		}
		
		public float getDiff(){
			return mass[1]-mass[0];
		}
		
		public void setUse(short [] used){
			this.used = used;
		}
	
		public void setSymbol(String [] symbol){
			this.symbols = symbol;
		}
		
		public short [] selectUsed(){
			ArrayList <Short> use = new ArrayList<Short>();
			for(int i=0;i<used.length;i++){
				if(used[i]>0){
					use.add(type[i]);
				}
			}
			short [] result = new short[use.size()];
			for(int j=0;j<result.length;j++){
				result[j] = use.get(j);
			}
			return result;
		}
		
		public short getLabelNum(){
			return (short) type.length;
		}
		
		public String getUsedStr(){
			StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(used[0]));
			for(int i=1;i<used.length;i++){
				sb.append("_");
				sb.append(used[i]);
			}
			return sb.toString();
		}
		
		public static LabelType getLabelType(String name){
			for(LabelType type : LabelType.values()){
				if(type.name.equalsIgnoreCase(name)){
					return type;
				}
			}
			return null;
		}
		
		public String toString(){
			return name;
		}
		
*/		
}
