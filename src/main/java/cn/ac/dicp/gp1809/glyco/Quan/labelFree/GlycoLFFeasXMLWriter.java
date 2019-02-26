/*
 ******************************************************************************
 * File: GlycoLFFeasXMLWriter.java * * * Created on 2011-11-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFFeasXMLWriter;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2011-11-8, 19:37:17
 */
public class GlycoLFFeasXMLWriter extends LFFeasXMLWriter
{
    private HashMap<Integer, String> peakOneLineMap;
    private DecimalFormat df4 = DecimalFormats.DF0_4;

    /**
     * @param file
     * @throws IOException
     */
    public GlycoLFFeasXMLWriter(String file) throws IOException
    {
        super(file);
    }

    /**
     * @param file
     * @throws IOException
     */
    public GlycoLFFeasXMLWriter(File file) throws IOException
    {
        super(file);
    }

    protected void initial()
    {
        this.root = DocumentFactory.getInstance().createElement("Glyco_Features");
        this.document.setRootElement(root);
        root.addAttribute("Label_Type", "Label_Free");
        this.peakOneLineMap = new HashMap<Integer, String>();
    }

    /**
     * @param feasList
     * @param pep
     */
    public void addFeature(FreeFeatures feas, IGlycoPeptide pep)
    {
        Element eFeas = DocumentFactory.getInstance().createElement("Features");

        String baseName = pep.getBaseName();
        int beg = pep.getScanNumBeg();
        int end = pep.getScanNumEnd();
        String ref = pep.getProteinReferenceString();
        String seq = pep.getSequence();

        double pepMr = pep.getPepMrNoGlyco();

        StringBuilder sb = new StringBuilder();
        double[] percent = pep.getGlycoPercents();
        for (int i = 0; i < percent.length; i++) {
            sb.append(df2.format(percent[i])).append("_");
        }

        eFeas.addAttribute("BaseName", baseName);
        eFeas.addAttribute("scanBeg", String.valueOf(beg));
        eFeas.addAttribute("scanEnd", String.valueOf(end));
        eFeas.addAttribute("Sequence", seq);
        eFeas.addAttribute("Charge", String.valueOf(pep.getCharge()));
        eFeas.addAttribute("rt", df4.format(pep.getRetentionTime()));
        eFeas.addAttribute("pepMr", df4.format(pepMr));
        eFeas.addAttribute("Glyco_Percents", sb.substring(0, sb.length() - 1));
        eFeas.addAttribute("Reference", ref);

        HashMap<String, ArrayList<NGlycoSSM>> hcdPsmInfoMap = pep.getHcdPsmInfoMap();

        Iterator<String> scanit = hcdPsmInfoMap.keySet().iterator();
        while (scanit.hasNext()) {

            String glycanid = scanit.next();
            Element eGS = DocumentFactory.getInstance().createElement("GlycoSpectra");
            ArrayList<NGlycoSSM> list = hcdPsmInfoMap.get(glycanid);
            for (int i = 0; i < list.size(); i++) {

                Element eGlyscan = DocumentFactory.getInstance().createElement("Glycan");
                NGlycoSSM ssm = list.get(i);

                String peakOneLine = ssm.getPeakOneLine();
                double rt = ssm.getRT();

                eGlyscan.addAttribute("ScanNum", String.valueOf(ssm.getScanNum()));
                eGlyscan.addAttribute("RT", df4.format(rt));
                this.peakOneLineMap.put(ssm.getScanNum(), peakOneLine);

                GlycoTree tree = ssm.getGlycoTree();
                StringBuilder labelSb = new StringBuilder();
                HashSet<Integer> matchedPeaks = ssm.getMatchedPeaks();
                Iterator<Integer> it = matchedPeaks.iterator();

                while (it.hasNext()) {
                    Integer id = it.next();
                    labelSb.append(id).append("_");
                }

                eGlyscan.addAttribute("Rank", String.valueOf(ssm.getRank()));
                eGlyscan.addAttribute("Score", df4.format(ssm.getScore()));
                eGlyscan.addAttribute("GlycoMass", df4.format(ssm.getGlycoMass()));
                eGlyscan.addAttribute("PeptideMassExperiment", df4.format(ssm.getPepMassExperiment()));
                eGlyscan.addAttribute("BestMatchPep", String.valueOf(ssm.getBestPepScannum()));
                eGlyscan.addAttribute("MatchedPeaks", labelSb.substring(0, labelSb.length() - 1));
                eGlyscan.addAttribute("GlycoCT", tree.getGlycoCT());
                eGlyscan.addAttribute("Name", tree.getIupacName());

                eGS.add(eGlyscan);
            }
            eFeas.add(eGS);
        }

        HashMap<Integer, FreeFeature> feaMap = feas.getFeaMap();
        Iterator<Integer> iit = feaMap.keySet().iterator();
        while (iit.hasNext()) {
            Integer scan = iit.next();
            FreeFeature f = feaMap.get(scan);
            Element ef = DocumentFactory.getInstance().createElement("Feature");
            ef.addAttribute("scannum", String.valueOf(scan));
            double rt = f.getRT();
            ef.addAttribute("retention_time", df4.format(rt));
            double inten = f.getIntensity();
            ef.addAttribute("intensity", df4.format(inten));
            eFeas.add(ef);
        }

        GlycoSite[] sites = pep.getAllGlycoSites();

        for (int i = 0; i < sites.length; i++) {

            Element eSite = DocumentFactory.getInstance().createElement("Site_Info");
            GlycoSite gsite = sites[i];
            ModSite ms = gsite.modifiedAt();
            int loc = gsite.modifLocation();
            char sym = gsite.symbol();
            double mass = gsite.getModMass();

            eSite.addAttribute("Site", ms.toString());
            eSite.addAttribute("Loc", String.valueOf(loc));
            eSite.addAttribute("Symbol", String.valueOf(sym));
            eSite.addAttribute("Mass", String.valueOf(mass));

            eFeas.add(eSite);
        }

        root.add(eFeas);
    }

    /**
     * Write the content.
     */
    public void write() throws IOException
    {
        Iterator<Integer> it = this.peakOneLineMap.keySet().iterator();

        while (it.hasNext()) {

            Integer scannum = it.next();
            String peakOneLine = this.peakOneLineMap.get(scannum);

            Element eSpectrum = DocumentFactory.getInstance().createElement("Spectrum");
            eSpectrum.addAttribute("Scannum", String.valueOf(scannum));
            eSpectrum.addAttribute("PeakOneLine", peakOneLine);

            this.root.add(eSpectrum);
        }

        writer.write(document);
    }

    public GlycoLFFeasXMLReader createReader()
    {
        try {
            return new GlycoLFFeasXMLReader(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
