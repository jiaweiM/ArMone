/*
 ******************************************************************************
 * File: Annotations.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf;

import cn.ac.dicp.gp1809.drawjf.AnnotationLabel;
import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation2;
import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation3;
import org.jfree.chart.ui.TextAnchor;

import java.awt.*;
import java.util.HashMap;

/**
 * The annotations for the peptide match
 *
 * @author Xinning
 * @version 0.1.1, 06-01-2010, 10:24:04
 */
public class Annotations
{

    private static Font DEFAULTFONT = new Font("SansSerif", Font.PLAIN, 10);


    private HashMap<String, MyXYPointerAnnotation2> labelMap;

    private HashMap<String, MyXYPointerAnnotation3> labelMap3;

    public Annotations()
    {
        this.labelMap = new HashMap<String, MyXYPointerAnnotation2>();
    }

    public Annotations(boolean noMz)
    {
        if (noMz)
            this.labelMap3 = new HashMap<String, MyXYPointerAnnotation3>();
    }

    /**
     * Add an annotation.
     *
     * @param label
     * @param x
     * @param y
     * @param color
     * @param angle
     */
    public void add(String mz, String[] labels, Color[] colors, double x, double y)
    {
        if (labels == null || labels.length == 0)
            return;

        if (labels.length != colors.length)
            throw new IllegalArgumentException("The color should correspondent to the lable.");

        MyXYPointerAnnotation2 annotation = null;
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];

            MyXYPointerAnnotation2 tmpannotation = this.labelMap.get(label);
            if (tmpannotation == null) {
                if (annotation == null) {
                    annotation = new MyXYPointerAnnotation2(mz, x, y);
                    annotation.setFont(DEFAULTFONT);
                    annotation.setTextAnchor(TextAnchor.BOTTOM_CENTER);
                }

                AnnotationLabel alabel = new AnnotationLabel(label, colors[i]);
                annotation.addLabel(alabel);
                this.labelMap.put(label, annotation);

            } else {
                double ty = tmpannotation.getY();
                //label with the highest peak
                if (y > ty) {

                    if (annotation == null) {
                        annotation = new MyXYPointerAnnotation2(mz, x, y);
                        annotation.setFont(DEFAULTFONT);
                        annotation.setTextAnchor(TextAnchor.BOTTOM_CENTER);
                    }

                    AnnotationLabel alabel = new AnnotationLabel(label, colors[i]);
                    annotation.addLabel(alabel);
                    this.labelMap.put(label, annotation);

                    tmpannotation.removeLabel(alabel);
                }
            }

        }
    }

    public void add3(String mz, String[] labels, Color[] colors, double x, double y)
    {
        if (labels == null || labels.length == 0)
            return;

        if (labels.length != colors.length)
            throw new IllegalArgumentException("The color should correspondent to the lable.");

        MyXYPointerAnnotation3 annotation = null;
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];

            MyXYPointerAnnotation3 tmpannotation = this.labelMap3.get(label);
            if (tmpannotation == null) {
                if (annotation == null) {
                    annotation = new MyXYPointerAnnotation3(mz, x, y);
                    annotation.setFont(DEFAULTFONT);
                    annotation.setTextAnchor(org.jfree.chart.ui.TextAnchor.BOTTOM_CENTER);
                }

                AnnotationLabel alabel = new AnnotationLabel(label, colors[i]);
                annotation.addLabel(alabel);
                this.labelMap3.put(label, annotation);

            } else {
                double ty = tmpannotation.getY();
                //label with the highest peak
                if (y > ty) {

                    if (annotation == null) {
                        annotation = new MyXYPointerAnnotation3(mz, x, y);
                        annotation.setFont(DEFAULTFONT);
                        annotation.setTextAnchor(org.jfree.chart.ui.TextAnchor.BOTTOM_CENTER);
                    }

                    AnnotationLabel alabel = new AnnotationLabel(label, colors[i]);
                    annotation.addLabel(alabel);
                    this.labelMap3.put(label, annotation);

                    tmpannotation.removeLabel(alabel);
                }
            }

        }
    }

    /**
     * The annotations
     *
     * @return
     */
    public MyXYPointerAnnotation2[] getAnnotations()
    {

        MyXYPointerAnnotation2[] anns =
                this.labelMap.values().toArray(new MyXYPointerAnnotation2[this.labelMap.size()]);

        return anns;
    }

    public MyXYPointerAnnotation3[] getAnnotations3()
    {

        MyXYPointerAnnotation3[] anns =
                this.labelMap3.values().toArray(new MyXYPointerAnnotation3[this.labelMap3.size()]);

        return anns;
    }

    /**
     * The number of annotations
     *
     * @return
     */
    public int getAnnotationCount()
    {
        return this.labelMap.size();
    }

}
