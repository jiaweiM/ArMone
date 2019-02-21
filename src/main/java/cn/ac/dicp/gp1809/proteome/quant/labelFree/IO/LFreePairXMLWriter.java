/*
 ******************************************************************************
 * File: LFreeMutilReader.java * * * Created on 2011-7-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.IO;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.IO.AbstractFeaturesXMLWriter;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2011-7-4, 13:48:53
 */
public class LFreePairXMLWriter extends AbstractFeaturesXMLWriter
{

    private static final String post = "xml";
    private File[] files;
    private int num;

    public LFreePairXMLWriter(File dir, String file) throws IOException
    {

        super(file);

        File[] files = dir.listFiles(new FileFilter()
        {

            @Override
            public boolean accept(File pathname)
            {
                // TODO Auto-generated method stub

                String name = pathname.getName();
                if (name.endsWith(post)) {
                    return true;
                }
                return false;
            }

        });

        this.files = files;
        this.file = new File(file);
        this.getProfileData();
    }

    public LFreePairXMLWriter(File[] files, String file) throws IOException
    {
        super(file);
        this.files = files;
        this.file = new File(file);
        this.num = files.length;
        this.getProfileData();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        // TODO Auto-generated method stub

        File f1 = new File("D:\\My Documents\\110323_110124_AHSG_dimethyl_deglyco_CID_1.pxml");
        File f2 = new File("D:\\My Documents\\110323_110124_AHSG_dimethyl_deglyco_CID_1.pxml");
        String out = "D:\\My Documents\\labelfree.pxml";

        LFreePairXMLWriter writer = new LFreePairXMLWriter(new File[]{f1, f2}, out);
        writer.write();
        writer.close();
    }

    protected void initial()
    {
        this.root = DocumentFactory.getInstance().createElement("ProFeatures");
        this.document.setRootElement(root);
        root.addAttribute("Label_Type", "Label_Free");
    }

    private void getProfileData() throws IOException
    {
        ProteinNameAccesser accesser = null;
        HashSet<String> keyset = new HashSet<String>();

        HashMap<String, IPeptide>[] pepMaps = new HashMap[num];
        HashMap<String, FreeFeatures>[] feaMaps = new HashMap[num];

        ModInfo[] mods = null;

        for (int i = 0; i < num; i++) {

            LFFeasXMLReader reader;

            try {

                reader = new LFFeasXMLReader(files[i]);

                if (accesser == null) {
                    accesser = reader.getProNameAccesser();
                } else {
                    accesser.appand(reader.getProNameAccesser());
                }
                String name = reader.getFileName();
                double tic = reader.getMS1TotalCurrent();
                Element eFile = DocumentFactory.getInstance().createElement("File");
                eFile.addAttribute("Name", name);
                eFile.addAttribute("TIC", String.valueOf(tic));
                root.add(eFile);

                pepMaps[i] = reader.getPepMap();
                feaMaps[i] = reader.getFeasMap();

                mods = reader.getMods();
                keyset.addAll(pepMaps[i].keySet());

                reader.close();

            } catch (DocumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (mods != null)
            this.addModification(mods);

        this.addProNameInfo(accesser);

        Iterator<String> it = keyset.iterator();
        while (it.hasNext()) {

            String key = it.next();
            ArrayList<FreeFeatures> feaslist = new ArrayList<FreeFeatures>();
            ArrayList<String> srclist = new ArrayList<String>();

            String ref = new String();
            IPeptide pep = null;
            for (int i = 0; i < num; i++) {

                if (pepMaps[i].containsKey(key)) {

                    pep = pepMaps[i].get(key);
                    FreeFeatures fea = feaMaps[i].get(key);
                    feaslist.add(fea);
                    srclist.add(pep.getBaseName());

                    String[] prefs = pep.getProteinReferenceString().split("$");
                    for (int j = 0; j < prefs.length; j++) {
                        if (!ref.contains(prefs[j])) {
                            ref += "$";
                            ref += prefs[j];
                        }
                    }
                }
            }
            ref = ref.substring(1, ref.length());
            if (feaslist.size() == num) {
                this.addFeatures(feaslist, srclist, pep, ref);
            } else {
                this.addIdenPep(pep);
            }
        }
    }

    private void addFeatures(ArrayList<FreeFeatures> feas, ArrayList<String> srcs, IPeptide pep, String ref)
    {

        Element eFeaPair = DocumentFactory.getInstance().createElement("Features_Pair");

        String scanName = pep.getBaseName();
        int beg = pep.getScanNumBeg();
        int end = pep.getScanNumEnd();
        String seq = pep.getSequence();
        int charge = pep.getCharge();

        eFeaPair.addAttribute("BaseName", scanName);
        eFeaPair.addAttribute("ScanBeg", String.valueOf(beg));
        eFeaPair.addAttribute("ScanEnd", String.valueOf(end));
        eFeaPair.addAttribute("Sequence", seq);
        eFeaPair.addAttribute("Charge", String.valueOf(charge));
        eFeaPair.addAttribute("Reference", ref);

        double[] ratios = this.getRatio(feas);
        StringBuilder ratiosb = new StringBuilder();
        for (int i = 0; i < ratios.length; i++) {
            ratiosb.append(ratios[i]).append("_");
        }
        eFeaPair.addAttribute("Ratios", ratiosb.substring(0, ratiosb.length() - 1));

        for (int i = 0; i < feas.size(); i++) {
            Element eFeas = DocumentFactory.getInstance().createElement("Features");
            FreeFeatures fs = feas.get(i);
            String src = srcs.get(i);
            double pepMr = fs.getPepMass();

            eFeas.addAttribute("pepMr", String.valueOf(pepMr));
            eFeas.addAttribute("file", src);

            HashMap<Integer, FreeFeature> feaMap = fs.getFeaMap();
            Iterator<Integer> iit = feaMap.keySet().iterator();
            while (iit.hasNext()) {
                Integer scan = iit.next();
                FreeFeature f = feaMap.get(scan);
                Element ef = DocumentFactory.getInstance().createElement("Feature");
                ef.addAttribute("scannum", String.valueOf(scan));
                double rt = f.getRT();
                ef.addAttribute("retention_time", String.valueOf(rt));
                double inten = f.getIntensity();
                ef.addAttribute("intensity", String.valueOf(inten));
                eFeas.add(ef);
            }
            eFeaPair.add(eFeas);
        }
        root.add(eFeaPair);
    }

    private double[] getRatio(ArrayList<FreeFeatures> feas)
    {

        int length = Integer.MAX_VALUE;
        for (int i = 0; i < feas.size(); i++) {
            if (length > feas.get(i).getLength()) {
                length = feas.get(i).getLength();
            }
        }

        int ratiolen = feas.size() * (feas.size() - 1) / 2;
        double[] ratio = new double[ratiolen];
        int id = 0;
        for (int i = 0; i < feas.size(); i++) {
            double ii = feas.get(i).getTopNInten(length);
            for (int j = i + 1; j < feas.size(); j++) {
                double jj = feas.get(j).getTopNInten(length);
                if (ii != 0)
                    ratio[id] = Double.parseDouble(df4.format(jj / ii));
            }
        }
        return ratio;
    }

}
