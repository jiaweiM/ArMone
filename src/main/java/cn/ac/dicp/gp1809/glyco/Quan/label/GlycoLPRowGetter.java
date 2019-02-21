/* 
 ******************************************************************************
 * File: GlycoLPRowGetter.java * * * Created on 2011-3-31
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.quant.profile.IO.FeaturesPagedRowGetter;
import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * @author ck
 *
 * @version 2011-3-31, 09:07:49
 */
public class GlycoLPRowGetter extends FeaturesPagedRowGetter
{

	public GlycoLPRowGetter(String file) throws Exception{
		this(new File(file));
	}
	
	public GlycoLPRowGetter(File file) throws Exception{
		this(new GlycoLabelFeaturesXMLReader(file));
	}
	
	public GlycoLPRowGetter(GlycoLabelFeaturesXMLReader reader){
		this(reader, null);
	}
	
	public GlycoLPRowGetter(GlycoLabelFeaturesXMLReader reader, int[] usedIndexs){
		super(reader, usedIndexs);
	}

	/*public HashMap <String, ArrayList <GlycoPeptideLabelPair>> getPairMap(){
		
		PeptidePair [] pairs = this.getAllSelectedFeatures();
		
		HashMap <String, ArrayList <GlycoPeptideLabelPair>> pairMap = 
			new HashMap <String, ArrayList <GlycoPeptideLabelPair>>();
		
		for(int i=0;i<pairs.length;i++){
			String seq = pairs[i].getSequence();
			if(pairMap.containsKey(seq)){
				pairMap.get(seq).add((GlycoPeptideLabelPair) pairs[i]);
			}else{
				ArrayList <GlycoPeptideLabelPair> list = new ArrayList <GlycoPeptideLabelPair>();
				list.add((GlycoPeptideLabelPair) pairs[i]);
				pairMap.put(seq, list);
			}
		}
		
		return pairMap;
	}*/
	
	public HashMap <String, ArrayList <GlycoQuanResult>> getQuanMap(){
		
		GlycoQuanResult [] results = ((GlycoLabelFeaturesXMLReader)reader).getAllResult();
		
		HashMap <String, ArrayList <GlycoQuanResult>> pairMap = 
			new HashMap <String, ArrayList <GlycoQuanResult>>();
		
		for(int i=0;i<results.length;i++){
			String seq = results[i].getPeptide().getSequence();
			if(pairMap.containsKey(seq)){
				pairMap.get(seq).add(results[i]);
			}else{
				ArrayList <GlycoQuanResult> list = new ArrayList <GlycoQuanResult>();
				list.add(results[i]);
				pairMap.put(seq, list);
			}
		}
		
		return pairMap;
	}
	
	public boolean isGlyco(){
		return true;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
