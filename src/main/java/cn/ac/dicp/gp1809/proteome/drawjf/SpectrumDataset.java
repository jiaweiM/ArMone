/*
 ******************************************************************************
 * File: SpectrumDataset.java * * * Created on 05-30-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf;

import java.awt.Color;
import java.util.ArrayList;

import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation3;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;

/**
 * The data set containing matched ions information which can be directly used
 * for the print by JFreeChart
 * 
 * @author Xinning
 * @version 0.2, 04-13-2009, 15:17:35
 */
public class SpectrumDataset extends AbstractSpectrumDataset {

	/**
	 * The color for plot
	 */
	private static final Color[] COLORS = new Color[] { Color.black, Color.red,
        Color.blue, Color.cyan, Color.green, Color.magenta };

	public SpectrumDataset(MS2PeakList peaklist) {
		
		this.setTitle(new TextTitle(""));
		this.setColors(COLORS);
		this.setLegend(true);
		
		XYSeriesCollection collection = new XYSeriesCollection();
		XYSeries series = new XYSeries("PEAKS");
		int size = peaklist.size();
		double max = peaklist.getBasePeak().getIntensity();
		for (int i = 0; i < size; i++) {
			IPeak peek = peaklist.getPeak(i);
			series.add(peek.getMz(), peek.getIntensity() / max);
		}
		collection.addSeries(series);
		this.setDataset(new XYBarDataset(collection, BarWidth));
	}
	
	public SpectrumDataset(MS2PeakList peaklist, double intenthres) {
		
		this.setTitle(new TextTitle(""));
		this.setColors(COLORS);
		this.setLegend(true);
		
		XYSeriesCollection collection = new XYSeriesCollection();
		Annotations ans = new Annotations(true);
		XYSeries series = new XYSeries("PEAKS");
		int size = peaklist.size();
		double max = peaklist.getBasePeak().getIntensity();
		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			double mz = peak.getMz();
			double inten = peak.getIntensity() / max;
			series.add(mz, inten);
			if(inten>intenthres){
				ans.add3(String.valueOf(mz), new String []{String.valueOf(mz)}, new Color[]{COLORS[1]}, mz, inten);
			}
		}
		collection.addSeries(series);

		this.setDataset(new XYBarDataset(collection, BarWidth));
		this.setAnnotations(ans.getAnnotations3());
	}
}
