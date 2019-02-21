/**
 * 
 */
package cn.ac.dicp.gp1809.drawjf;

import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.text.TextUtilities;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

/**
 * @author ck
 *
 */
public class MyXYPointerAnnotation3 extends XYPointerAnnotation {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashSet<AnnotationLabel> labelset = new HashSet<AnnotationLabel>();
	private String mz;

	/**
	 * 
	 * @param mz
	 * @param label
	 * @param x
	 * @param y
	 */
	public MyXYPointerAnnotation3(String mz, AnnotationLabel label, double x,
	        double y) {
		this(mz, x, y);
		this.addLabel(label);
	}

	/**
	 * 
	 * @param mz
	 * @param x
	 * @param y
	 */
	public MyXYPointerAnnotation3(String mz, double x, double y) {
		super(mz, x, y, -1.571d);

		this.mz = mz;
		this.setBaseRadius(25d);

		this.setArrowStroke(new BasicStroke(0, BasicStroke.CAP_ROUND,
		        BasicStroke.JOIN_ROUND, 1, new float[] { 3, 1 }, 0));

		this.setArrowWidth(1.5);
	}

	/**
	 * Add an annotation, the duplicated label will be removed.
	 * 
	 * @param label
	 */
	public void addLabel(AnnotationLabel label) {
		this.labelset.add(label);
	}

	/**
	 * remove an annotation
	 * 
	 * @param label
	 */
	public void removeLabel(AnnotationLabel label) {
		this.labelset.remove(label);
	}

	/**
	 * As the label may be removed, test whether there are labels remained in
	 * this annotation, if not, the annotation will not be printed.
	 * 
	 * @return
	 */
	public boolean isUsedAnnotation() {
		return this.labelset.size() > 0;
	}

	/**
	 * Draws the annotation.
	 * 
	 * @param g2
	 *            the graphics device.
	 * @param plot
	 *            the plot.
	 * @param dataArea
	 *            the data area.
	 * @param domainAxis
	 *            the domain axis.
	 * @param rangeAxis
	 *            the range axis.
	 * @param rendererIndex
	 *            the renderer index.
	 * @param info
	 *            the plot rendering info.
	 */
	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
	        ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex,
	        PlotRenderingInfo info) {

		if (this.isUsedAnnotation()) {
			PlotOrientation orientation = plot.getOrientation();
            RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot
			        .getDomainAxisLocation(), orientation);
			RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot
			        .getRangeAxisLocation(), orientation);
			double j2DX = domainAxis
			        .valueToJava2D(getX(), dataArea, domainEdge);
			double j2DY = rangeAxis.valueToJava2D(getY(), dataArea, rangeEdge);
			if (orientation == PlotOrientation.HORIZONTAL) {
				double temp = j2DX;
				j2DX = j2DY;
				j2DY = temp;
			}

			//Draw the mz value
			g2.setFont(getFont());
//			g2.setPaint(Color.black);
//			Rectangle2D hotspot = TextUtilities.drawAlignedString(this.mz, g2,
//			        (float) j2DX, (float) j2DY, getTextAnchor());
//			j2DY -= hotspot.getHeight();

			double startX = j2DX + Math.cos(this.getAngle())
			        * this.getBaseRadius();
			double startY = j2DY + Math.sin(this.getAngle())
			        * this.getBaseRadius();

			double endX = j2DX + Math.cos(this.getAngle())
			        * this.getTipRadius();
			double endY = j2DY + Math.sin(this.getAngle())
			        * this.getTipRadius();

			double arrowBaseX = endX + Math.cos(this.getAngle())
			        * this.getArrowLength();
			double arrowBaseY = endY + Math.sin(this.getAngle())
			        * this.getArrowLength();

			double arrowLeftX = arrowBaseX
			        + Math.cos(this.getAngle() + Math.PI / 2.0)
			        * this.getArrowWidth();
			double arrowLeftY = arrowBaseY
			        + Math.sin(this.getAngle() + Math.PI / 2.0)
			        * this.getArrowWidth();

			double arrowRightX = arrowBaseX
			        - Math.cos(this.getAngle() + Math.PI / 2.0)
			        * this.getArrowWidth();
			double arrowRightY = arrowBaseY
			        - Math.sin(this.getAngle() + Math.PI / 2.0)
			        * this.getArrowWidth();

			GeneralPath arrow = new GeneralPath();
			arrow.moveTo((float) endX, (float) endY);
			arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
			arrow.lineTo((float) arrowRightX, (float) arrowRightY);
			arrow.closePath();

			g2.setStroke(this.getArrowStroke());
			g2.setPaint(this.getArrowPaint());
			Line2D line = new Line2D.Double(startX, startY, endX, endY);
			g2.draw(line);
			g2.fill(arrow);

			// draw the label
			double labelX = j2DX + Math.cos(this.getAngle())
			        * (this.getBaseRadius() + this.getLabelOffset());
			double labelY = j2DY + Math.sin(this.getAngle())
			        * (this.getBaseRadius() + this.getLabelOffset());

			/*
			 * ================================== Changed region, not fully
			 * optimized.
			 */

			//Print the text nearest to the arrow.
			double hight = 0;
			Rectangle2D hotspot = null;
			for (AnnotationLabel label : this.labelset) {
				g2.setPaint(label.getColor());
				hotspot = TextUtils.drawAlignedString(label
				        .getLabelString(), g2, (float) labelX,
				        (float) (labelY - hight), getTextAnchor());

				hight += hotspot.getHeight();
			}

			String toolTip = getToolTipText();
			String url = getURL();
			if (toolTip != null || url != null) {
				addEntity(info, hotspot, rendererIndex, toolTip, url);
			}
		}
	}


}
