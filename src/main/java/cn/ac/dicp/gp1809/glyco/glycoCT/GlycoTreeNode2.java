/* 
 ******************************************************************************
 * File: GlycoTreeNode.java * * * Created on 2012-3-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.glycoCT;

import java.io.Serializable;
import java.util.ArrayList;

import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;

/**
 * @author ck
 *
 * @deprecated
 * @version 2012-3-21, 14:46:00
 */
public class GlycoTreeNode2 implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String selfid;
	private String parentid;
//	private String selfPosition;
//	private String parentPosition;
	/**
	 * e.g. 2+1, x+x
	 */
	private String parentLinkPosition;
	
	private GlycoTreeNode2 parentNode;
	private ArrayList <GlycoTreeNode2> childNodeList;
	private ArrayList <Substituents> substituentList;
	
	private StemType stemType;
	private Glycosyl glycosyl;
//	private Substituents [] subs;
//	private char [] parentLinkTypes;
//	private char [] childLinkTypes;
	private double monoMass;
	private double aveMass;
	private String MonosaccharideName;
	
	/**
	 * a, b, o, x
	 */
	private char anomeric = '\u0000';
	
	/**
	 * d, l, x
	 */
	private char stereo = '\u0000';

	public GlycoTreeNode2(String selfid, StemType stemType, char anomeric, char stereo){
		
		this.selfid = selfid;
		this.childNodeList = new ArrayList <GlycoTreeNode2>();
		this.substituentList = new ArrayList <Substituents>();
		this.stemType = stemType;
		this.monoMass = stemType.getMonoMass();
		this.aveMass = stemType.getAveMass();
		this.anomeric = anomeric;
		this.stereo = stereo;
		
//		this.subs = new Substituents[stemType.getSuperClassType()];
//		this.parentLinkTypes = new char[stemType.getSuperClassType()];
//		this.parentLinkTypes = new char[stemType.getSuperClassType()];
	}
	
	public String getSelfid(){
		return selfid;
	}
	
	public String getSuperClass(){
		return stemType.getSuperClass();
	}
	
	/**
	 * C3~C6
	 * @return
	 */
	public int getSuperClassType(){
		return stemType.getSuperClassType();
	}
	
	public char getAnomeric(){
		return anomeric;
	}
	
	public char getStereo(){
		return stereo;
	}
	
	public void setParentId(String parentid){
		this.parentid = parentid;
	}
	
	public String getParentId(){
		return parentid;
	}
/*	
	public void setParentPosition(String parentPosition){
		this.parentPosition = parentPosition;
	}
	
	public String getParentPosition(){
		return parentPosition;
	}
	
	public void setChildPosition(String selfPosition){
		this.selfPosition = selfPosition;
	}
	
	public String getSelfPosition(){
		return selfPosition;
	}
*/	
	public void setParentNode(GlycoTreeNode2 parentNode, String parentLinkPosition){
		this.parentNode = parentNode;
		this.parentLinkPosition = parentLinkPosition;
	}
	
	public GlycoTreeNode2 getParentNode(){
		return parentNode;
	}

	public ArrayList<GlycoTreeNode2> getChildNodeList(){
		return childNodeList;
	}
	
//	public void setSubstituents(Substituents sub, int parentPosition){
//		this.subs[parentPosition-1] = sub;
//	}
	
	public void addSubstituents(Substituents sub, String linkPosition, char parentLinkType, char childLinkType){
		
//		this.subs[parentPosition-1] = sub;
//		this.parentLinkTypes[parentPosition-1] = parentLinkType;
//		this.childLinkTypes[parentPosition-1] = childLinkType;
		
		sub.setLinkPosition(linkPosition);
		sub.setParentLinkType(parentLinkType);
		sub.setChildLinkType(childLinkType);
		
		this.monoMass += sub.getMonoMass();
		this.aveMass += sub.getAveMass();
		
		switch (parentLinkType){
		
		case 'o' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		case 'd' :
			monoMass -= AminoAcidProperty.MONOW_H;
			monoMass -= AminoAcidProperty.MONOW_O;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_O;
			break;
			
		case 'h' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		case 'n' :
			break;
			
		case 'x' :
			break;
			
		case 'r' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		case 's' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		default:
			break;
		}
		
		switch (childLinkType){
		
		case 'o' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		case 'd' :
			monoMass -= AminoAcidProperty.MONOW_H;
			monoMass -= AminoAcidProperty.MONOW_O;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_O;
			break;
			
		case 'h' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		case 'n' :
			break;
			
		case 'x' :
			break;
			
		case 'r' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		case 's' :
			monoMass -= AminoAcidProperty.MONOW_H;
			aveMass -= AminoAcidProperty.AVERAGEW_H;
			break;
			
		default:
			break;
		}
	}
	
	public void addChildTreeNode(GlycoTreeNode2 childNode, String linkPosition){
		this.childNodeList.add(childNode);
		childNode.setParentNode(this, linkPosition);
	}
	
	public double getMonoMass(){

		return monoMass;
	}
	
	public double getAveMass(){

		return aveMass;
	}
	
	public String getMonosaccharideName(){
		
		StringBuilder sb = new StringBuilder();
		
		
		
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
