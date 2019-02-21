/*
 ******************************************************************************
 * File: JFChartDrawer.java * * * Created on 03-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.drawjf;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.RectangleInsets;

/**
 * The main class or drawing the data set into chart
 * 
 * 
 * @author Xinning
 * @version 0.2, 05-19-2010, 16:14:01
 */
public class JFChartDrawer {
	
	public static Font DefaultFont = new Font(null, 0, 0);
	
	public static JFreeChart createXYBarChartNoLegend(IJFDataset dataset){
		
		if(dataset == null)
			return null;
		
		NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis(dataset.getYAxisLabel());
        numberaxis1.setAutoRangeIncludesZero(false);
        numberaxis1.setRange(0, 1.2);
        
        XYBarRenderer renderer = new XYBarRenderer();
        Color[] colors = dataset.getColorForDataset();
        int size = dataset.getSeriesCount();
        for(int i=0;i<size;i++) {
        	renderer.setSeriesPaint(i, colors[i]);
        	renderer.setSeriesFillPaint(i, null);
        }
		
        XYAnnotation  [] annotations = dataset.getAnnotations(); 
        if(annotations!=null){
        	for(int i=0;i<annotations.length;i++) {
        		XYTextAnnotation ann = (XYTextAnnotation)annotations[i];
        		Font font = ann.getFont();
        		ann.setFont(font);
        		renderer.addAnnotation(ann);
        	}
        }
        
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        
        XYPlot xyplot = new XYPlot(dataset.getDataset(), numberaxis, numberaxis1, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
        xyplot.setRenderer(renderer);
        
        JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(dataset.getTextTitle());
        
        return jfreechart;
	}
	
	/**
	 * Create a customized chart for the data set
	 * 
	 * @param dataset
	 * @return
	 */
	
	public static JFreeChart createXYBarChart(IJFDataset dataset){
		
		if(dataset == null)
			return null;
		
		NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis(dataset.getYAxisLabel());
        numberaxis1.setAutoRangeIncludesZero(false);
        
        XYBarRenderer renderer = new XYBarRenderer();
        Color[] colors = dataset.getColorForDataset();
        int size = dataset.getSeriesCount();
        for(int i=0;i<size;i++) {
        	renderer.setSeriesPaint(i, colors[i]);
        	renderer.setSeriesFillPaint(i, null);
        }
		
        XYAnnotation  [] annotations = dataset.getAnnotations(); 
        if(annotations!=null){
        	for(int i=0,n=annotations.length;i<n;i++) {
        		XYTextAnnotation ann = (XYTextAnnotation)annotations[i];
        		Font font = ann.getFont();
        		ann.setFont(font);
        		renderer.addAnnotation(ann);
        	}
        }
        
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        
        XYPlot xyplot = new XYPlot(dataset.getDataset(), numberaxis, numberaxis1, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
        
        JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, xyplot, dataset.createLegend());
        jfreechart.setTitle(dataset.getTextTitle());
        jfreechart.setBackgroundPaint(Color.WHITE);
		jfreechart.setBorderPaint(Color.WHITE);
//        ChartUtilities.applyCurrentTheme(jfreechart);
        
        return jfreechart;
	}
	
	public static JFreeChart createLineChart(IJFDataset dataset){
		if(dataset == null)
			return null;
		
		NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis(dataset.getYAxisLabel());
        numberaxis1.setAutoRangeIncludesZero(false);
        XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
        
        Color[] colors = dataset.getColorForDataset();
        int size = dataset.getSeriesCount();
        for(int i=0;i<size;i++) {
        	xysplinerenderer.setSeriesPaint(i, colors[i]);
        	xysplinerenderer.setSeriesFillPaint(i, null);
        }
        
        XYAnnotation annotations[] = dataset.getAnnotations(); 
        if(annotations!=null){
        	for(int i=0,n=annotations.length;i<n;i++) {
        		XYTextAnnotation ann = (XYTextAnnotation)annotations[i];
        		Font font = ann.getFont();
        		ann.setFont(font);
        		xysplinerenderer.addAnnotation(ann);
        	}
        }
        
        XYPlot xyplot = new XYPlot(dataset.getDataset(), numberaxis, numberaxis1, xysplinerenderer);

        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
        
        JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
        jfreechart.setTitle(dataset.getTextTitle());
        ChartUtils.applyCurrentTheme(jfreechart);

		return jfreechart;
	}
	
	public static JFreeChart createFastScatterChart(float [][] dataset, String xlabel, String ylabel, String chartname){
		
		if(dataset == null)
			return null;
		
		NumberAxis numberXaxis = new NumberAxis(xlabel);
		numberXaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberYaxis = new NumberAxis(ylabel);
        numberYaxis.setAutoRangeIncludesZero(false);
        
        FastScatterPlot plot = new FastScatterPlot(dataset, numberXaxis, numberYaxis);
        JFreeChart chart = new JFreeChart(chartname, plot);
        chart.getRenderingHints().put
        	(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        return chart;
	}
	
	public static JFreeChart createScatterChart(IJFDataset dataset){
		
		if(dataset == null)
			return null;
		
		JFreeChart jfreechart = 
        ChartFactory.createScatterPlot("",   
                "", "", dataset.getDataset(), PlotOrientation.VERTICAL, true, false,   
                false); 
		
		NumberAxis numberaxis = new NumberAxis(dataset.getXAxisLabel());
        numberaxis.setAutoRangeIncludesZero(false);
        NumberAxis numberaxis1 = new NumberAxis(dataset.getYAxisLabel());
        numberaxis1.setAutoRangeIncludesZero(false);
        numberaxis1.setAutoRangeMinimumSize(1.0);

        XYDotRenderer renderer = new XYDotRenderer();
        int size = dataset.getSeriesCount();
        for(int i=0;i<size;i++) {
        	renderer.setSeriesFillPaint(i, null);
        }
        
        renderer.setDotHeight(2);
        renderer.setDotWidth(2);
        
        XYAnnotation [] annotations = dataset.getAnnotations(); 
        if(annotations!=null){
        	for(int i=0,n=annotations.length;i<n;i++) {
        		XYTextAnnotation ann = (XYTextAnnotation)annotations[i];
        		Font font = ann.getFont();
        		ann.setFont(font);
        		renderer.addAnnotation(ann);
        	}
        }

        XYPlot xyplot = 
        	jfreechart.getXYPlot();
        	
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setRenderer(renderer);
        xyplot.setDomainAxis(numberaxis);
        xyplot.setRangeAxis(numberaxis1);
        
        jfreechart.setTitle(dataset.getTextTitle());
        jfreechart.setBackgroundPaint(Color.white);

        ChartUtils.applyCurrentTheme(jfreechart);

		return jfreechart;
	}
	
}
