/* 
 ******************************************************************************
 * File: OGlycoPossiForm.java * * * Created on 2011-6-23
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
 * @version 2011-6-23, 09:26:54
 */
public class OGlycoPossiForm extends GlycoForm {

	/**
	 * @param comp
	 * @param dm
	 */
	public OGlycoPossiForm(int[] comp, double dm) {
		super(comp, dm);
		// TODO Auto-generated constructor stub
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
//		if(comp[4]>0)
//			sb.append("(NeuGc)").append(comp[4]);

		return sb.toString();
	}

	public boolean equals(Object obj){
		
		if(obj instanceof OGlycoPossiForm){
			
			OGlycoPossiForm o1 = (OGlycoPossiForm) obj;
			
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

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.glyco.GlycoForm#getCompDes()
	 */
	@Override
	public String getCompDes() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		if(comp[1]>0)
			sb.append("(Hex)").append(comp[1]);
		if(comp[2]>0)
			sb.append("(HexNAc)").append(comp[2]);
		if(comp[0]>0)
			sb.append("(dHex)").append(comp[0]);
		if(comp[3]>0)
			sb.append("(NeuAc)").append(comp[3]);
//		if(comp[4]>0)
//			sb.append("(NeuGc)").append(comp[4]);

		return sb.toString();
	}

}
