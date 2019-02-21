/*
 ******************************************************************************
 * File: MyXYPointerAnnotation.java * * * Created on 04-07-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
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

/**
 * A easy used XYPointerAnnotation which can print out multiple line of labels.
 *
 * @author Xinning
 * @version 0.1, 04-07-2008, 13:37:26
 */
public class MyXYPointerAnnotation extends XYPointerAnnotation
{
    private static final long serialVersionUID = 1L;
    private String[] labels;
    private double hightPercent;

    /**
     * @param labels the String array of the lables
     * @param x
     * @param y
     * @param angle
     */
    public MyXYPointerAnnotation(String[] labels, double x, double y, double angle)
    {
        super((labels != null && labels.length > 0 ? labels[labels.length - 1] : null), x, y, angle);
        this.labels = labels;
        this.setBaseRadius(36d);
    }

    public void setHightPercent(double hightPercent)
    {
        this.hightPercent = hightPercent;
    }

    /**
     * @return The string array of the labels
     */
    public String[] getTexts()
    {
        return this.labels;
    }

    /**
     * Set the String array for labels
     *
     * @param labels
     */
    public void setTexts(String[] labels)
    {
        if (labels == null || labels.length < 1) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }

        this.labels = labels;
        super.setText(labels[labels.length - 1]);
    }

    /**
     * Sets the text for the last annotation (The annotation which just near the arrow). Mostly for the inherit.
     *
     * @param text the text (<code>null</code> not permitted).
     * @see #getText()
     */
    @Override
    public void setText(String text)
    {
        super.setText(text);
        this.labels[this.labels.length - 1] = text;
    }

    /**
     * Draws the annotation.
     *
     * @param g2            the graphics device.
     * @param plot          the plot.
     * @param dataArea      the data area.
     * @param domainAxis    the domain axis.
     * @param rangeAxis     the range axis.
     * @param rendererIndex the renderer index.
     * @param info          the plot rendering info.
     */
    @Override
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
            int rendererIndex, PlotRenderingInfo info)
    {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        double j2DX = domainAxis.valueToJava2D(getX(), dataArea, domainEdge);
        double j2DY = rangeAxis.valueToJava2D(getY(), dataArea, rangeEdge);
        if (orientation == PlotOrientation.HORIZONTAL) {
            double temp = j2DX;
            j2DX = j2DY;
            j2DY = temp;
        }
        double startX = j2DX + Math.cos(this.getAngle()) * this.getBaseRadius();
        double startY = j2DY + Math.sin(this.getAngle()) * this.getBaseRadius();

        double endX = j2DX + Math.cos(this.getAngle()) * this.getTipRadius();
        double endY = j2DY + Math.sin(this.getAngle()) * this.getTipRadius();

        double arrowBaseX = endX + Math.cos(this.getAngle()) * this.getArrowLength();
        double arrowBaseY = endY + Math.sin(this.getAngle()) * this.getArrowLength();

        double arrowLeftX = arrowBaseX + Math.cos(this.getAngle() + Math.PI / 2.0) * this.getArrowWidth();
        double arrowLeftY = arrowBaseY + Math.sin(this.getAngle() + Math.PI / 2.0) * this.getArrowWidth();

        double arrowRightX = arrowBaseX - Math.cos(this.getAngle() + Math.PI / 2.0) * this.getArrowWidth();
        double arrowRightY = arrowBaseY - Math.sin(this.getAngle() + Math.PI / 2.0) * this.getArrowWidth();

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
        g2.setFont(getFont());
        g2.setPaint(getPaint());
        double labelX = j2DX + Math.cos(this.getAngle()) * (this.getBaseRadius() + this.getLabelOffset());
        double labelY = j2DY + Math.sin(this.getAngle()) * (this.getBaseRadius() + this.getLabelOffset());

        /*
         * ================================== Changed region, not fully
         * optimized.
         */

        // Print the text nearest to the arrow.
        Rectangle2D hotspot = TextUtils.drawAlignedString(getText(), g2, (float) labelX, (float) labelY, getTextAnchor());
        for (int i = this.labels.length - 2; i >= 0; i--) {
            hotspot = TextUtils.drawAlignedString(this.labels[i], g2, (float) labelX,
                    (float) (labelY - hotspot.getHeight()), getTextAnchor());
        }

        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, hotspot, rendererIndex, toolTip, url);
        }
    }

}
