/* 
 ******************************************************************************
 * File: PossiComposition.java * * * Created on 2011-5-18
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

/**
 * @author ck
 *
 * @version 2011-5-18, 14:38:05
 */
public class NGlycoPossiForm extends GlycoForm {

	private boolean fuc;
	private boolean rationality;
	private String type;
	
	public NGlycoPossiForm(int [] comp, double dm){
		super(comp, dm);
		this.judge();
	}
	
	public NGlycoPossiForm(int [] comp, boolean fuc, double dm){
		super(comp, dm);
		this.fuc = fuc;
		this.judge();
	}
	
	public NGlycoPossiForm(int [] comp, boolean fuc, double dm, float score){
		super(comp, dm, score);
		this.fuc = fuc;
		this.judge();
	}

	public void setFuc(boolean fuc){
		this.fuc = fuc;
	}
	
	public boolean hasFuc(){
		return fuc;
	}

	private void judge(){
		
		boolean rationality = true;
		
		if(comp[1]<3 || comp[2]<2)
			rationality = false;
		
		if(fuc){
			if(comp[0]>(comp[1]+comp[2]-5))
				rationality = false;
		}else{
			if(comp[0]+1>(comp[1]+comp[2]-5))
				rationality = false;
		}

		if(comp[2]<=2 && comp[1]>2){
			if(comp[3]>0 || comp[4]>0)
				rationality = false;
		}

		this.rationality = rationality;
		
		if(comp[2]==2 && comp[1]>=5)
			this.type = "high mannose";
		
		if(comp[2]>=3 && comp[1]>=3)
			this.type = "hybrid/complex";
	}
	
	public boolean isRationality(){
		return this.rationality;
	}
	
	public String getType(){
		return type;
	}
	
	public String getCompDes(){
		
		StringBuilder sb = new StringBuilder();
		
		if(fuc){
			sb.append("(Man)3(GlcNAc)2(Fucose)+");
			if(comp[1]-3>0)
				sb.append("(Hex)").append(comp[1]-3);
			if(comp[2]-2>0)
				sb.append("(HexNAc)").append(comp[2]-2);
			if(comp[0]>1)
				sb.append("(dHex)").append(comp[0]-1);
			if(comp[3]>0)
				sb.append("(NeuAc)").append(comp[3]);
			if(comp[4]>0)
				sb.append("(NeuGc)").append(comp[4]);
		}else{
			sb.append("(Man)3(GlcNAc)2+");
			if(comp[1]-3>0)
				sb.append("(Hex)").append(comp[1]-3);
			if(comp[2]-2>0)
				sb.append("(HexNAc)").append(comp[2]-2);
			if(comp[0]>0)
				sb.append("(dHex)").append(comp[0]);
			if(comp[3]>0)
				sb.append("(NeuAc)").append(comp[3]);
			if(comp[4]>0)
				sb.append("(NeuGc)").append(comp[4]);
		}

		sb.append(" ").append(type);
		
		return sb.toString();
	}
	
	public String getCompDesNoCore(){
		
		StringBuilder sb = new StringBuilder();

		if(comp[1]>0)
			sb.append("(Hex)").append(comp[1]);
		if(comp[2]>0)
			sb.append("(HexNAc)").append(comp[2]);
		if(comp[0]>0)
			sb.append("(dHex)").append(comp[0]);
		if(comp[3]>0)
			sb.append("(NeuAc)").append(comp[3]);
		if(comp[4]>0)
			sb.append("(NeuGc)").append(comp[4]);
		
		sb.append(" ").append(type);
		
		return sb.toString();
	}

	public boolean equals(Object obj){
		
		if(obj instanceof NGlycoPossiForm){
			
			NGlycoPossiForm o1 = (NGlycoPossiForm) obj;
			
			String s0 = this.getStrComp();
			String s1 = o1.getStrComp();
			
			return s0.equals(s1);
			
		}else{
			return false;
		}
		
	}
	
	public int hashCode(){
		return this.getStrComp().hashCode();
	}
	
}
