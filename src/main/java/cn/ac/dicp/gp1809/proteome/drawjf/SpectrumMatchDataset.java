/*
 ******************************************************************************
 * File: SpectrumMatchDataset.java * * * Created on 04-07-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf;

import cn.ac.dicp.gp1809.proteome.drawjf.pepinfo.IPeptideInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.*;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Match the actual spectrum to theoretical ions. b and y type ions are determined, if match, the matched ions will be
 * set as the second and third series, and can be printed using other color.
 *
 * @author Xinning
 * @version 0.3.2, 06-08-2010, 20:03:44
 */
public class SpectrumMatchDataset extends AbstractSpectrumDataset
{

    /**
     *
     */
    private static final Color[] COLORS = new Color[]{Color.black, Color.red,
            Color.blue, Color.cyan, Color.green, Color.magenta};
    private static DecimalFormat DF = DecimalFormats.DF0_4;
    private static DecimalFormat DF0 = DecimalFormats.DF0_1;
    protected IPeptideInfo pepinfor;
    private PeakForMatch[] peaks;

    /**
     * @param peaklist
     * @param pepinfor
     */
    public SpectrumMatchDataset(IMS2PeakList peaklist, IPeptideInfo pepinfor)
    {
        this(peaklist, pepinfor, null);
    }

    /**
     * @param peaklist
     * @param pepinfor
     * @param threshold spectrum match threshold
     */
    public SpectrumMatchDataset(IMS2PeakList peaklist, IPeptideInfo pepinfor,
            ISpectrumThreshold threshold)
    {

        TextTitle title = new TextTitle("\n                      "
                + pepinfor.getSequence() + " (z: " + pepinfor.getCharge()
                + "; m/z: " + DF.format(pepinfor.getMZ()) + ")");
        title.setHorizontalAlignment(org.jfree.chart.ui.HorizontalAlignment.LEFT);

        this.setTitle(title);
        this.setColors(COLORS);

        this.createDataset(peaklist, pepinfor, threshold);
    }

    /**
     * Create the data set and the annotation
     *
     * @param peaklist
     * @param pepinfo
     */
    protected void createDataset(IMS2PeakList peaklist, IPeptideInfo pepinfo,
            ISpectrumThreshold threshold)
    {
        XYSeriesCollection collection = new XYSeriesCollection();
        XYSeries series = new XYSeries("UnMatchedPeaks");
        XYSeries seriesb = new XYSeries("bPeaks");
        XYSeries seriesy = new XYSeries("yPeaks");
        XYSeries seriesc = new XYSeries("cPeaks");
        XYSeries seriesz = new XYSeries("zPeaks");
        XYSeries seriesneu = new XYSeries("neuPeaks");

        Ions ions = pepinfo.getIons();
        PeakForMatch[] peaks = SpectrumMatcher.match(peaklist, pepinfo.getMZ(), ions, pepinfo
                .getLosses(), pepinfo.getCharge(), threshold, ions.getTypes());
        Annotations ans = new Annotations();

        int size = peaks.length;
        this.peaks = peaks;

        for (int i = 0; i < size; i++) {
            PeakForMatch peak = peaks[i];
            double inten = peak.getIntensity();
            double mz = peak.getMz();

            if (peak.isMatched()) {
                int[] types = peak.getMatchedTypes();

                ArrayList<Color> colorlist = new ArrayList<Color>();
                ArrayList<String> labellist = new ArrayList<String>();
                for (int type : types) {
                    Color color = null;

                    switch (type) {
                        case Ion.TYPE_B:
                            color = COLORS[1];
                            seriesb.add(mz, inten);
                            break;
                        case Ion.TYPE_Y:
                            color = COLORS[2];
                            seriesy.add(mz, inten);
                            break;
                        case Ion.TYPE_C:
                            color = COLORS[3];
                            seriesc.add(mz, inten);
                            break;
                        case Ion.TYPE_Z:
                            color = COLORS[4];
                            seriesz.add(mz, inten);
                            break;
                        case Ion.TYPE_NEU:
                            color = COLORS[5];
                            seriesneu.add(mz, inten);
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Unsupported ion type: " + type);
                    }

                    Ion[] ins = peak.getMatchIons(type);
                    for (int j = 0; j < ins.length; j++) {
                        Ion ion = ins[j];
                        /*
                         * Only the lowest charge state is labeled in the
                         * spectrum
                         */
                        if (ion != null) {
                            labellist.add(ion.getName(j + 1));
                            colorlist.add(color);
                            peak.setLabel(ion.getName(j + 1));
                        }
                    }
                }

                String[] labels = labellist
                        .toArray(new String[labellist.size()]);
                Color[] colors = colorlist.toArray(new Color[colorlist.size()]);

                ans.add(DF0.format(peak.getMz()), labels, colors, mz, inten);
            } else
                series.add(mz, inten);
        }

        collection.addSeries(series);
        collection.addSeries(seriesb);
        collection.addSeries(seriesy);
        collection.addSeries(seriesc);
        collection.addSeries(seriesz);
        collection.addSeries(seriesneu);

        this.setDataset(new XYBarDataset(collection, BarWidth));
        this.setAnnotations(ans.getAnnotations());
    }

    public PeakForMatch[] getMatchedPeaks()
    {
        return this.peaks;
    }

}
