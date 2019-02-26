/*
 ******************************************************************************
 * File: LFFeasXMLReader.java * * * Created on 2011-7-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.labelFree.IO;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanPeptide;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 * @version 2011-7-1, 18:53:34
 */
public class LFFeasXMLReader
{
    protected Element root;
    protected File file;
    protected double totalCurrent;
    protected ModInfo[] mods;
    protected ProteinNameAccesser accesser;
    private Iterator<Element> feasIt;
    private HashMap<String, IPeptide> pepMap;
    private HashMap<String, FreeFeatures> feaMap;


    public LFFeasXMLReader(String file) throws DocumentException
    {
        this(new File(file));
    }

    /**
     * @param file
     * @throws DocumentException
     */
    public LFFeasXMLReader(File file) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        this.root = document.getRootElement();
        this.file = file;

        getProfileData();
    }

    /**
     * @param args
     * @throws DocumentException
     */
    public static void main(String[] args) throws Exception
    {
        LFFeasXMLReader reader = new LFFeasXMLReader("D:\\My Documents\\110323_110124_AHSG_dimethyl_deglyco_CID_1.pxml");
        System.out.println(reader.mods.length);
    }

    /**
     *
     */
    protected void getProfileData()
    {
        System.out.println("Reading " + file.getName() + " ......");

        this.feasIt = root.elementIterator("Features");
        this.totalCurrent = Double.parseDouble(root.attributeValue("TotalCurrent"));
        this.pepMap = new HashMap<>();
        this.feaMap = new HashMap<>();

        try {

            this.setProNameAccesser();
            this.setMods();
            this.getAllFeas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProNameAccesser()
    {
        Iterator<Element> proIt = root.elementIterator("Protein_Info");
        if (proIt.hasNext()) {
            Element ePro = proIt.next();
            int i1 = Integer.parseInt(ePro.attributeValue("Target_Length"));
            int i2 = Integer.parseInt(ePro.attributeValue("Decoy_Length"));
            boolean usePattern = Boolean.parseBoolean(ePro.attributeValue("Use_Pattern"));
            Pattern pattern = null;
            if (usePattern) {
                pattern = Pattern.compile(ePro.attributeValue("Pattern"));
            }

            ProteinNameAccesser accesser = new ProteinNameAccesser(i1, i2, usePattern, pattern, new DefaultDecoyRefJudger());

            Iterator<Element> it = ePro.elementIterator();
            while (it.hasNext()) {
                Element eInfo = it.next();
                String ref = eInfo.attributeValue("Reference");
                int length = Integer.parseInt(eInfo.attributeValue("Length"));
                double mw = Double.parseDouble(eInfo.attributeValue("MW"));
                double hydro = Double.parseDouble(eInfo.attributeValue("Hydro"));
                double PI = Double.parseDouble(eInfo.attributeValue("PI"));
                boolean isDecoy = Boolean.parseBoolean(eInfo.attributeValue("isDecoy"));
                String partRef = "";
                if (usePattern) {
                    Matcher matcher = pattern.matcher(ref);
                    if (matcher.find()) {

                        if (matcher.groupCount() == 0) {
                            partRef = matcher.group(0);
                        }
                        if (matcher.groupCount() == 1) {
                            partRef = matcher.group(1);
                        }
                    }
                } else {
                    if (isDecoy) {
                        partRef = ref.substring(0, i2 > ref.length() ? ref.length() : i2);
                    } else {
                        partRef = ref.substring(0, i1 > ref.length() ? ref.length() : i1);
                    }
                }

                SimpleProInfo pInfo = new SimpleProInfo(partRef, ref, length, mw, hydro, PI, isDecoy);
                accesser.addRef(partRef, pInfo);

            }
            this.accesser = accesser;
        }
    }

    protected void setMods() throws Exception
    {
        Iterator<Element> modIt = root.elementIterator("Modification");
        ArrayList<ModInfo> modlist = new ArrayList<ModInfo>();

        while (modIt.hasNext()) {
            Element eMod = modIt.next();
            String name = eMod.attributeValue("Name");
            double mass = Double.parseDouble(eMod.attributeValue("Mass"));
            char symbol = eMod.attributeValue("Symbol").charAt(0);
            String modStr = eMod.attributeValue("ModSite");
            ModSite site = ModSite.parseSite(modStr);
            ModInfo info = new ModInfo(name, mass, symbol, site);
            modlist.add(info);
        }
        ModInfo[] modArray = modlist.toArray(new ModInfo[modlist.size()]);
        this.mods = modArray;
    }

    private void getAllFeas()
    {
        while (feasIt.hasNext()) {

            Element eFeas = feasIt.next();

            String baseName = eFeas.attributeValue("BaseName");
            int scanBeg = Integer.parseInt(eFeas.attributeValue("scanBeg"));
            int scanEnd = Integer.parseInt(eFeas.attributeValue("scanEnd"));
            String seq = eFeas.attributeValue("Sequence");
            short charge = Short.parseShort(eFeas.attributeValue("Charge"));
            double pepMr = Double.parseDouble(eFeas.attributeValue("pepMr"));
            String ref = eFeas.attributeValue("Reference");

            HashSet<ProteinReference> refset = new HashSet<ProteinReference>();
            HashMap<String, SeqLocAround> locAroundMap = new HashMap<String, SeqLocAround>();
            String[] reflist = ref.split("\\$");
            for (int i = 0; i < reflist.length; i++) {
                String[] sss = reflist[i].split("\\+");
                ProteinReference pr = ProteinReference.parse(sss[0]);
                refset.add(pr);

                int beg = Integer.parseInt(sss[1]);
                int end = Integer.parseInt(sss[2]);

                if (sss.length == 3) {
                    SeqLocAround sla = new SeqLocAround(beg, end, "", "");
                    locAroundMap.put(pr.toString(), sla);
                } else if (sss.length == 4) {
                    SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
                    locAroundMap.put(pr.toString(), sla);
                } else if (sss.length == 5) {
                    SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
                    locAroundMap.put(sss[0], sla);
                }
            }

            IPeptide pep = new QuanPeptide(seq, charge, refset, baseName, scanBeg, scanEnd, locAroundMap);
            this.pepMap.put(seq, pep);

            FreeFeatures fs = new FreeFeatures();
            Iterator<Element> itF = eFeas.elementIterator("Feature");
            while (itF.hasNext()) {
                Element ef = itF.next();
                int scannum = Integer.parseInt(ef.attributeValue("scannum"));
                double rt = Double.parseDouble(ef.attributeValue("retention_time"));
                double[] intens = new double[3];
                intens[0] = Float.parseFloat(ef.attributeValue("intensity_1"));
                intens[1] = Float.parseFloat(ef.attributeValue("intensity_2"));
                intens[2] = Float.parseFloat(ef.attributeValue("intensity_3"));

                FreeFeature f = new FreeFeature(scannum, pepMr, rt, intens);
                fs.addFeature(f);
            }
            fs.setInfo();
            this.feaMap.put(seq, fs);
        }
    }

    /**
     * @return
     */
    public ModInfo[] getMods()
    {
        return this.mods;
    }

    public HashMap<String, IPeptide> getPepMap()
    {
        return this.pepMap;
    }

    public HashMap<String, FreeFeatures> getFeasMap()
    {
        return this.feaMap;
    }

    public double getMS1TotalCurrent()
    {
        return totalCurrent;
    }

    public String getFileName()
    {
        String name = file.getName();
        name = name.substring(0, name.length() - 5);
        return name;
    }

    public ProteinNameAccesser getProNameAccesser()
    {
        return this.accesser;
    }

    /**
     * @return
     */
    public File getParentFile()
    {
        return file.getParentFile();
    }

    public void close()
    {
        this.feasIt = null;
        this.root = null;
        System.gc();
    }
}
