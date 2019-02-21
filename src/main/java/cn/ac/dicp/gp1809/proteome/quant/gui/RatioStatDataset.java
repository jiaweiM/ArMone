/* 
 ******************************************************************************
 * File: RatioStatDataset.java * * * Created on 2011-9-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cn.ac.dicp.gp1809.drawjf.AbstractJFDataset;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2011-9-22, 13:51:21
 */
public class RatioStatDataset extends AbstractJFDataset {
	
	private Color [] COLORS = new Color[] { Color.red, Color.blue, Color.green, Color.yellow};
	private DecimalFormat df4 = DecimalFormats.DF0_4;

	public RatioStatDataset(PeptidePair [] pairs, boolean normal, String [] names){
		
		this.setTitle(new TextTitle("Log2 Ratio"));
		this.setColors(COLORS);

		XYSeriesCollection collection = new XYSeriesCollection();
		
		double [][] ratios = new double [names.length][];
		for(int i=0;i<ratios.length;i++){
			ratios[i] = new double [pairs.length];
		}
/*		
		if(normal){
			
			for(int i=0;i<pairs.length;i++){
				double [] pairratio = pairs[i].getNormalRatio();
				for(int j=0;j<pairratio.length;j++){
					ratios[j][i] = pairratio[j];
				}
			}
			
		}else{
*/			
			for(int i=0;i<pairs.length;i++){
				double [] pairratio = pairs[i].getFeatures().getSelectRatio();
				for(int j=0;j<pairratio.length;j++){
					ratios[j][i] = pairratio[j];
				}
			}
//		}
		
		XYSeries [] series = new XYSeries[names.length];
		for(int i=0;i<series.length;i++){
			series[i] = new XYSeries(names[i]);
			Arrays.sort(ratios[i]);
			for(int j=0;j<ratios[i].length;j++){
				if(ratios[i][j]==0 || ratios[i][j]==1000)
					continue;
				
				double y = Math.log(ratios[i][j])/Math.log(2);
				series[i].add((double)(j+1), Double.parseDouble(df4.format(y)));
			}
			collection.addSeries(series[i]);
		}
		
		this.setDataset(collection);
		this.setXlabel("Number");
		this.setYlabel("Log2 Ratio");
	}
	
}
