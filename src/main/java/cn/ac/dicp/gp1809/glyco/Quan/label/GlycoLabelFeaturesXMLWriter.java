/*
 ******************************************************************************
 * File: GlycoQuanXMLWriter.java * * * Created on 2011-3-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import org.dom4j.*;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author ck
 * @version 2011-3-16, 10:40:38
 */
public class GlycoLabelFeaturesXMLWriter
{

    private HashMap<Integer, String> peakOneLineMap;
    private Document document;
    private XMLWriter writer;
    private Element root;
    private LabelType type;
    private boolean gradient;
    private File file;
    private double[] totalIntensity;

    private DecimalFormat df2 = DecimalFormats.DF0_2;
    private DecimalFormat df4 = DecimalFormats.DF0_4;
    private DecimalFormat df5 = DecimalFormats.DF0_5;
    private DecimalFormat dfe4 = DecimalFormats.DF_E4;

    public GlycoLabelFeaturesXMLWriter(String file, LabelType type, boolean gradient) throws IOException
    {
        this(new File(file), type, gradient);
    }

    public GlycoLabelFeaturesXMLWriter(File file, LabelType type, boolean gradient) throws IOException
    {
        this.file = file;
        this.type = type;
        this.gradient = gradient;
        this.document = DocumentHelper.createDocument();
        this.writer = new XMLWriter(new FileWriter(file));
        this.initial();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.profile.IO.AbstractPairXMLWriter#initial()
     */
    protected void initial()
    {
        // TODO Auto-generated method stub
        this.root = DocumentFactory.getInstance().createElement("Glyco_Features");
        this.document.setRootElement(root);
        root.addAttribute("Label_Type", type.getLabelName());
        root.addAttribute("Gradient", String.valueOf(gradient));
        this.addLabelInfo();
        this.totalIntensity = new double[type.getLabelNum()];
        this.peakOneLineMap = new HashMap<Integer, String>();
    }

    private void addLabelInfo()
    {

        LabelInfo[][] infos = type.getInfo();
        String name = type.getLabelName();
        short[] used = type.getUsed();
        for (int i = 0; i < infos.length; i++) {
            Element eType = DocumentFactory.getInstance().createElement(name + "_" + used[i]);
            if (infos[i].length > 0) {
                for (int j = 0; j < infos[i].length; j++) {
                    Element eInfo = DocumentFactory.getInstance().createElement("Label_Infomation");
                    LabelInfo info = infos[i][j];
                    String des = info.getDes();
                    ModSite site = info.getSite();
                    double mass = info.getMass();
                    char symbol = info.getSymbol();
                    String symbolStr = symbol != '\u0000' ? String.valueOf(symbol) : "";

                    eInfo.addAttribute("Description", des);
                    eInfo.addAttribute("Site", site.getModifAt());
                    eInfo.addAttribute("Mass", String.valueOf(mass));
                    eInfo.addAttribute("Symbol", symbolStr);
                    eType.add(eInfo);
                }
            } else {
                Element eInfo = DocumentFactory.getInstance().createElement("Label_Infomation");
                eType.add(eInfo);
            }
            root.add(eType);
        }
    }

    public void addGlycoSpectra(NGlycoSSM ssm)
    {

        Element eGlyscan = DocumentFactory.getInstance().createElement("GlycoSpectra");

        String peakOneLine = ssm.getPeakOneLine();
        int scannum = ssm.getScanNum();
        double rt = ssm.getRT();

        eGlyscan.addAttribute("ScanNum", String.valueOf(scannum));
        eGlyscan.addAttribute("RT", df4.format(rt));
        this.peakOneLineMap.put(scannum, peakOneLine);

        GlycoTree tree = ssm.getGlycoTree();
        StringBuilder labelSb = new StringBuilder();
        HashSet<Integer> matchedPeaks = ssm.getMatchedPeaks();
        Iterator<Integer> it = matchedPeaks.iterator();

        while (it.hasNext()) {
            Integer id = it.next();
            labelSb.append(id).append("_");
        }

        eGlyscan.addAttribute("Rank", String.valueOf(ssm.getRank()));
        eGlyscan.addAttribute("Score", df2.format(ssm.getScore()));
        eGlyscan.addAttribute("PrecursorMz", df4.format(ssm.getPreMz()));
        eGlyscan.addAttribute("GlycoMass", df4.format(ssm.getGlycoMass()));
        eGlyscan.addAttribute("PeptideMassExperiment", df4.format(ssm.getPepMassExperiment()));
        eGlyscan.addAttribute("DeltaMz", df4.format(ssm.getDeltaMz()));
        eGlyscan.addAttribute("PeptideID", String.valueOf(ssm.getPeptideid()));
        eGlyscan.addAttribute("LabelTypeID", String.valueOf(ssm.getPepLabelType(ssm.getPeptideid())));
        eGlyscan.addAttribute("StructureID", ssm.getGlycanid()[0] + "_" + ssm.getGlycanid()[1]);
        eGlyscan.addAttribute("MatchedPeaks", labelSb.substring(0, labelSb.length() - 1));
        eGlyscan.addAttribute("GlycoCT", tree.getGlycoCT());
        eGlyscan.addAttribute("Name", tree.getIupacName());
        eGlyscan.addAttribute("PeakOneLine", peakOneLine);
/*
		Element eFeas = DocumentFactory.getInstance().createElement("Features");
		FreeFeatures feas = ssm.getFeatures();
		if(feas!=null){
			HashMap <Integer, FreeFeature> feaMap = feas.getFeaMap();
			Iterator <Integer> iit = feaMap.keySet().iterator();
			while(iit.hasNext()){
				Integer scan = iit.next();
				FreeFeature f = feaMap.get(scan);
				Element ef = DocumentFactory.getInstance().createElement("Feature");
				ef.addAttribute("scannum", String.valueOf(scan));
				double feart = f.getRT();
				ef.addAttribute("retention_time", df4.format(feart));
				double inten = f.getIntensity();
				ef.addAttribute("intensity", df4.format(inten));
				eFeas.add(ef);
			}

			eGlyscan.add(eFeas);
		}
*/

        root.add(eGlyscan);
    }

    /**
     * Add the glycopeptides.
     *
     * @param pep
     */
    public void addIdenPep(IGlycoPeptide pep)
    {

        Element eIdenPep = DocumentFactory.getInstance().createElement("GlycoPeptides");

        eIdenPep.addAttribute("BaseName", pep.getBaseName());
        eIdenPep.addAttribute("ScanNum", String.valueOf(pep.getScanNumBeg()));
        eIdenPep.addAttribute("Sequence", pep.getSequence());
        eIdenPep.addAttribute("Charge", String.valueOf(pep.getCharge()));
        eIdenPep.addAttribute("Score", String.valueOf(pep.getPrimaryScore()));
        eIdenPep.addAttribute("rt", df4.format(pep.getRetentionTime()));
        eIdenPep.addAttribute("pepMr", df4.format(pep.getPepMrNoGlyco()));
        eIdenPep.addAttribute("Reference", pep.getProteinReferenceString());

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

            eIdenPep.add(eSite);
        }

        root.add(eIdenPep);
    }

    public void addFeatures(GlycoPeptideLabelPair glycoFeas)
    {

        LabelFeatures feas = glycoFeas.getFeatures();
        int pepid = glycoFeas.getPeptideId();
        int deleglycoid = glycoFeas.getDeleSSMId();
        ArrayList<Integer> ssmids = glycoFeas.getSsmsIds();

        Element eFeaPair = DocumentFactory.getInstance().createElement("Features_Pair");
        eFeaPair.addAttribute("PeptideId", String.valueOf(pepid));

        StringBuilder idsb = new StringBuilder();
        idsb.append(deleglycoid).append("_");
        for (int i = 0; i < ssmids.size(); i++) {
            idsb.append(ssmids.get(i)).append("_");
        }
        idsb.deleteCharAt(idsb.length() - 1);
        eFeaPair.addAttribute("GlycanId", idsb.toString());

        double[] masses = feas.getMasses();
        double[] totalIntens = feas.getTotalIntens();

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        for (int i = 0; i < masses.length; i++) {
            s1.append(df4.format(masses[i])).append("_");
            s2.append(df4.format(totalIntens[i])).append("_");
        }

        eFeaPair.addAttribute("Masses", s1.substring(0, s1.length() - 1));
        eFeaPair.addAttribute("TotalIntensity", s2.substring(0, s2.length() - 1));

        double[] ratios = feas.getRatios();
        double[] rias = feas.getRias();
        StringBuilder s3 = new StringBuilder();
        StringBuilder s4 = new StringBuilder();
        for (int i = 0; i < ratios.length; i++) {
            s3.append(df4.format(ratios[i])).append("_");
            s4.append(df4.format(rias[i])).append("_");
        }
        eFeaPair.addAttribute("Ratio", s3.substring(0, s3.length() - 1));
        eFeaPair.addAttribute("RIA", s4.substring(0, s4.length() - 1));

        int[] scans = feas.getScanList();
        double[] rt = feas.getRTList();
        double[][] labelIntens = feas.getIntenList();
        for (int i = 0; i < scans.length; i++) {
            Element eFeas = DocumentFactory.getInstance().createElement("Scan");
            eFeas.addAttribute("Scannum", String.valueOf(scans[i]));
            eFeas.addAttribute("Rt", df4.format(rt[i]));
            StringBuilder s5 = new StringBuilder();
            for (int j = 0; j < labelIntens[i].length; j++) {
                s5.append(df4.format(labelIntens[i][j])).append("_");
                this.totalIntensity[j] += labelIntens[i][j];
            }
            eFeas.addAttribute("Intensity", s5.substring(0, s5.length() - 1));
            eFeaPair.add(eFeas);
        }
        boolean accurate = feas.isValidate();
        eFeaPair.addAttribute("Accurate", accurate ? "1" : "0");

        root.add(eFeaPair);
    }

    public void addModification(AminoacidModification aamods)
    {

        Modif[] mods = aamods.getModifications();
        for (int i = 0; i < mods.length; i++) {
            Modif m = mods[i];
            String name = m.getName();
            double mass = m.getMass();
            char symbol = m.getSymbol();
            HashSet<ModSite> sites = aamods.getModifSites(symbol);
            Iterator<ModSite> it = sites.iterator();
            while (it.hasNext()) {
                String site = it.next().getModifAt();
                Element eMod = DocumentFactory.getInstance().createElement("Modification");
                eMod.addAttribute("Name", name);
                eMod.addAttribute("Mass", String.valueOf(mass));
                eMod.addAttribute("Symbol", String.valueOf(symbol));
                eMod.addAttribute("ModSite", site);
                root.add(eMod);
            }
        }
    }

    public void addModification(ModInfo[] mods)
    {

        for (int i = 0; i < mods.length; i++) {
            ModInfo m = mods[i];
            String name = m.getName();
            double mass = m.getMass();
            char symbol = m.getSymbol();
            ModSite site = mods[i].getModSite();

            Element eMod = DocumentFactory.getInstance().createElement("Modification");
            eMod.addAttribute("Name", name);
            eMod.addAttribute("Mass", String.valueOf(mass));
            eMod.addAttribute("Symbol", String.valueOf(symbol));
            eMod.addAttribute("ModSite", site.getModifAt());
            root.add(eMod);
        }
    }

    public void addProNameInfo(ProteinNameAccesser accesser)
    {

        int lt = accesser.getSplitLength();
        int lr = accesser.getSplitRevLength();
        boolean usePattern = accesser.usePattern();

        Element eProName = DocumentFactory.getInstance().createElement("Protein_Info");
        eProName.addAttribute("Use_Pattern", String.valueOf(usePattern));
        eProName.addAttribute("Target_Length", String.valueOf(lt));
        eProName.addAttribute("Decoy_Length", String.valueOf(lr));
        if (usePattern) {
            Pattern pattern = accesser.getPattern();
            eProName.addAttribute("Pattern", pattern.pattern());
        }

        SimpleProInfo[] infos = accesser.getInfosofProteins();
        for (int i = 0; i < infos.length; i++) {

            Element eInfo = DocumentFactory.getInstance().createElement("Info");

            String ref = infos[i].getRef();
            int length = infos[i].getLength();
            double mw = infos[i].getMw();
            double hydro = infos[i].getHydroScore();
            double PI = infos[i].getPI();
            boolean isDecoy = infos[i].isDecoy();

            eInfo.addAttribute("Reference", ref);
            eInfo.addAttribute("Length", String.valueOf(length));
            eInfo.addAttribute("MW", String.valueOf(mw));
            eInfo.addAttribute("Hydro", String.valueOf(hydro));
            eInfo.addAttribute("PI", String.valueOf(PI));
            eInfo.addAttribute("isDecoy", String.valueOf(isDecoy));

            eProName.add(eInfo);
        }
        root.add(eProName);
    }

    public void addBestEstimate(double[] fit)
    {
        this.root.addAttribute("BestEstimates", fit[0] + "_" + fit[1]);
    }

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

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalIntensity.length; i++) {
            sb.append(dfe4.format(totalIntensity[i])).append("_");
        }
        root.addAttribute("TotalIntensity", sb.substring(0, sb.length() - 1));

        writer.write(document);
    }

    public GlycoLabelFeaturesXMLReader createReader() throws DocumentException
    {
        return new GlycoLabelFeaturesXMLReader(file);
    }

    public void close() throws IOException
    {
        writer.close();
        System.gc();
    }


}
