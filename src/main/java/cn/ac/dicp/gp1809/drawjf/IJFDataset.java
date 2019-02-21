/*
 ******************************************************************************
 * File: IJFDataset.java * * * Created on 03-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.drawjf;

import java.awt.Color;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;

/**
 * Containing all informations for the draw by JFreeChart. Including the data
 * and draw informations.
 * 
 * @author Xinning
 * @version 0.1, 03-27-2009, 10:49:21
 */
public interface IJFDataset {

	/**
	 * 
	 * @return
	 */
	public boolean createLegend();

	/**
	 * Dataset containing all data point for draw.
	 * 
	 * @return XYBarDataset used for draw of XYBarChart
	 */
	public XYDataset getDataset();

	/**
	 * Get series count in this data set. equals getDataset().getSeriesCount;
	 * 
	 * @return
	 */
	public int getSeriesCount();

	/**
	 * The color used for draw. This is related to the dataset. The number of
	 * the color content must be equal to that of the colors.
	 * 
	 * @return color used for draw.
	 */
	public Color[] getColorForDataset();

	/**
	 * Get the annotations for data points.
	 * 
	 * @return annotations for the data points needing to be annotated
	 */
	public XYAnnotation[] getAnnotations();

	/**
	 * The title text;
	 * 
	 * @return title text (org.jfree.chart.title.TextTitle);
	 */
	public TextTitle getTextTitle();

	/**
	 * @return the X label (null permited)
	 */
	public String getXAxisLabel();

	/**
	 * @return the Y label (null permited)
	 */
	public String getYAxisLabel();
}
