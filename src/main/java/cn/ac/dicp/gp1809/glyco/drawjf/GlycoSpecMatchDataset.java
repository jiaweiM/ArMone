/* 
 ******************************************************************************
 * File: GlycoSpecMatchDataset.java * * * Created on 2011-12-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.drawjf;

import cn.ac.dicp.gp1809.glyco.oglycan.OGlycanPepInfo;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.drawjf.AbstractSpectrumDataset;
import cn.ac.dicp.gp1809.proteome.drawjf.Annotations;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author ck
 *
 * @version 2011-12-20, 15:55:14
 */
public class GlycoSpecMatchDataset extends AbstractSpectrumDataset {

	private static final Color[] COLORS = new Color[] { Color.black, Color.red,
        Color.blue, Color.cyan, Color.green, Color.magenta };
	
	public GlycoSpecMatchDataset(int scannum){
		
		TextTitle title = new TextTitle("Scannum: "+scannum);
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		this.setTitle(title);
		this.setColors(COLORS);
		this.setLegend(false);
	}
	
	public GlycoSpecMatchDataset(String scannum){
		
		TextTitle title = new TextTitle("Scannum: "+scannum);
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		this.setTitle(title);
		this.setColors(COLORS);
		this.setLegend(false);
	}

	public void createDataset(NGlycoSSM ssm){

		XYSeriesCollection collection = new XYSeriesCollection();
		Annotations ans = new Annotations(true);
		
		XYSeries seriesCore = new XYSeries("Matched peak");
		XYSeries seriesOther = new XYSeries("Unmatched peaks");

		IMS2PeakList peaklist = MS2PeakList.parsePeaksOneLine(ssm.getPeakOneLine());
		HashSet <Integer> matchedPeaks = ssm.getMatchedPeaks();
		
		IPeak [] peaks = peaklist.getPeakArray();

		for(int i=0;i<peaks.length;i++){
			
			double mz = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			
			if(matchedPeaks.contains(i)){
				
				seriesCore.add(mz, inten);
//				ans.add(String.valueOf(mz), new String []{matchMap.get(mz)}, new Color[]{COLORS[1]}, mz, inten);
				ans.add3(String.valueOf(mz), new String []{String.valueOf(mz)}, new Color[]{COLORS[1]}, mz, inten);
//				ans.add("", new String []{matchMap.get(mz)}, new Color[]{COLORS[1]}, mz, inten);
				
			}else{
				seriesOther.add(mz, inten);
			}
		}
		
		collection.addSeries(seriesOther);
		collection.addSeries(seriesCore);
		
		this.setDataset(new XYBarDataset(collection, BarWidth));
		this.setAnnotations(ans.getAnnotations3());
	
	}
	
	public void createDataset(OGlycanPepInfo opInfo){

		XYSeriesCollection collection = new XYSeriesCollection();
		Annotations ans = new Annotations(true);
		
		XYSeries seriesBG = new XYSeries("Noise peak");
		XYSeries seriesBY = new XYSeries("BY peak");
		XYSeries seriesBYGlyco = new XYSeries("BY-glycan peaks");
		
		IPeak [] peaks = opInfo.getPeaks();
		HashMap <Double, String> matchedPeaks = opInfo.getMatchMap();
		
		for(int i=0;i<peaks.length;i++){
			
			double mz = peaks[i].getMz();
			double inten = peaks[i].getIntensity();
			
			if(matchedPeaks.containsKey(mz)){
				
				String ann = matchedPeaks.get(mz);
				if(ann.contains("(")){
					seriesBYGlyco.add(mz, inten);
					ans.add3(String.valueOf(mz), new String []{String.valueOf(mz)}, new Color[]{COLORS[2]}, mz, inten);
				}else{
					seriesBY.add(mz, inten);
					ans.add3(String.valueOf(mz), new String []{String.valueOf(mz)}, new Color[]{COLORS[1]}, mz, inten);
				}
			}else{
				seriesBG.add(mz, inten);
			}
		}
		
		collection.addSeries(seriesBG);
		collection.addSeries(seriesBY);
		collection.addSeries(seriesBYGlyco);
		
		this.setDataset(new XYBarDataset(collection, BarWidth));
		this.setAnnotations(ans.getAnnotations3());
	
	}
	
}
