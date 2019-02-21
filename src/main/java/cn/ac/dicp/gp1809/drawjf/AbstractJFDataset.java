package cn.ac.dicp.gp1809.drawjf;

import java.awt.Color;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;

/**
 * The abstract JFDataset
 * 
 * @author Xinning
 * @version 0.1, 04-13-2009, 14:33:18
 */
public abstract class AbstractJFDataset implements IJFDataset {

	private String xlabel;
	private String ylabel;
	private Color[] colors;
	private XYDataset dataset;
	private TextTitle title;
	private XYAnnotation[] annotations;
	private boolean createLegend;

	protected AbstractJFDataset() {}

	public boolean createLegend() {
		return this.createLegend;
	}

	@Override
	public XYAnnotation[] getAnnotations() {
		return this.annotations;
	}

	@Override
	public Color[] getColorForDataset() {
		return this.colors;
	}

	@Override
	public XYDataset getDataset() {
		return this.dataset;
	}

	@Override
	public int getSeriesCount() {
		return this.dataset.getSeriesCount();
	}

	@Override
	public TextTitle getTextTitle() {
		return this.title;
	}

	@Override
	public String getXAxisLabel() {
		return this.xlabel;
	}

	@Override
	public String getYAxisLabel() {
		return this.ylabel;
	}

	/**
	 * @param xlabel the xlabel to set
	 */
	public void setXlabel(String xlabel) {
		this.xlabel = xlabel;
	}

	/**
	 * @param ylabel the ylabel to set
	 */
	public void setYlabel(String ylabel) {
		this.ylabel = ylabel;
	}

	/**
	 * @param colors the colors to set
	 */
	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(XYDataset dataset) {
		this.dataset = dataset;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(TextTitle title) {
		this.title = title;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(XYAnnotation[] annotations) {
		this.annotations = annotations;
	}

	public void setLegend(boolean createLegend) {
		this.createLegend = createLegend;
	}

}
