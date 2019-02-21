/* 
 ******************************************************************************
 * File:PPMDataset.java * * * Created on 2011-9-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cn.ac.dicp.gp1809.drawjf.AbstractJFDataset;

/**
 * @author ck
 *
 * @version 2011-9-25, 15:04:40
 */
public class PPMDataset extends AbstractJFDataset {

	public PPMDataset(ArrayList <Double> target, ArrayList <Double> decoy){
		
		this.setTitle(new TextTitle("Mass Deviation"));
		this.setColors(new Color[]{Color.red, Color.blue});
		XYSeriesCollection collection = new XYSeriesCollection();
		
		XYSeries tseries = new XYSeries("Target");
		for(int i=0;i<target.size();i++){
			tseries.add(i+1, target.get(i));
		}
		collection.addSeries(tseries);
		
		XYSeries dseries = new XYSeries("Decoy");
		for(int i=0;i<decoy.size();i++){
			dseries.add(i+1, decoy.get(i));
		}
		collection.addSeries(dseries);
		
		this.setDataset(collection);
		this.setXlabel("Number");
		this.setYlabel("Deviation");
		
	}
}
