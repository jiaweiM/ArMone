/*
 ******************************************************************************
 * File: GlycoLFFeasXMLReader2.java * * * Created on 2013-6-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree2;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFFeasXMLReader;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2013-6-13, 13:47:17
 */
public class GlycoLFFeasXMLReader2 extends LFFeasXMLReader
{
    private Iterator<Element> glycospectraIt;
    private Iterator<Element> glycopeptideIt;

    private IGlycoPeptide[] peps;
    private NGlycoSSM[] matchedssms;
    private NGlycoSSM[] unmatchedssms;
    private double[] bestEstimate;

    public GlycoLFFeasXMLReader2(String file) throws DocumentException
    {
        this(new File(file));
    }

    public GlycoLFFeasXMLReader2(File file) throws DocumentException
    {
        super(file);
    }

    protected void getProfileData()
    {
        System.out.println("Reading " + file.getName() + " ......");

        this.glycospectraIt = root.elementIterator("GlycoSpectra");
        this.glycopeptideIt = root.elementIterator("GlycoPeptides");

        String ss = root.attributeValue("BestEstimates");
        if (ss != null) {
            String[] sss = ss.split("_");
            this.bestEstimate = new double[]{Double.parseDouble(sss[0]), Double.parseDouble(sss[1])};
        }

        try {

            this.setProNameAccesser();
            this.setMods();
            this.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse()
    {
        ArrayList<IGlycoPeptide> peplist = new ArrayList<>();

        while (glycopeptideIt.hasNext()) {

            Element eGlycoPeptide = glycopeptideIt.next();

            String baseName = eGlycoPeptide.attributeValue("BaseName");
            int scanBeg = Integer.parseInt(eGlycoPeptide.attributeValue("ScanNum"));
            String seq = eGlycoPeptide.attributeValue("Sequence");
            short pepCharge = Short.parseShort(eGlycoPeptide.attributeValue("Charge"));
            double peprt = Double.parseDouble(eGlycoPeptide.attributeValue("rt"));
            double pepMass = Double.parseDouble(eGlycoPeptide.attributeValue("pepMr"));
            float score = Float.parseFloat(eGlycoPeptide.attributeValue("Score"));
            String ref = eGlycoPeptide.attributeValue("Reference");

            HashSet<ProteinReference> refset = new HashSet<>();
            HashMap<String, SeqLocAround> locAroundMap = new HashMap<>();
            HashMap<String, SimpleProInfo> proInfoMap = new HashMap<>();

            String proref = "";
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

                String refname = pr.getName();
                SimpleProInfo info = this.accesser.getProInfo(refname);
                proInfoMap.put(pr.toString(), info);
                proref += info.getPartRef();
                proref += ";";
            }

            GlycoPeptide peptide = new GlycoPeptide(seq, pepCharge, refset, baseName,
                    scanBeg, scanBeg, locAroundMap, pepMass);

            peptide.setPrimaryScore(score);
            peptide.setRetentionTime(peprt);
            peptide.setProInfoMap(proInfoMap);
            peptide.setDelegateReference(proref.substring(0, proref.length() - 1));

            Iterator<Element> itF = eGlycoPeptide.nodeIterator();

            while (itF.hasNext()) {

                Element ef = itF.next();

                ModSite site = ModSite.newInstance_aa(ef.attributeValue("Site").charAt(0));
                int loc = Integer.parseInt(ef.attributeValue("Loc"));
                char sym = ef.attributeValue("Symbol").charAt(0);
                double mass = Double.parseDouble(ef.attributeValue("Mass"));

                peptide.addGlycoSite(new GlycoSite(site, loc, sym));
            }

            peplist.add(peptide);
        }

        this.peps = peplist.toArray(new IGlycoPeptide[peplist.size()]);

        ArrayList<NGlycoSSM> matchedlist = new ArrayList<>();
        ArrayList<NGlycoSSM> unmatchedlist = new ArrayList<>();

        while (glycospectraIt.hasNext()) {

            Element eGlycoSpectrum = glycospectraIt.next();
            Integer scannum = Integer.parseInt(eGlycoSpectrum.attributeValue("ScanNum"));
            double rt = Double.parseDouble(eGlycoSpectrum.attributeValue("RT"));

            String peakOneLine = eGlycoSpectrum.attributeValue("PeakOneLine");
            MS2PeakList ms2PeakList = MS2PeakList.parsePeaksOneLine(peakOneLine);
            IPeak[] peaks = ms2PeakList.getPeakArray();

            int rank = Integer.parseInt(eGlycoSpectrum.attributeValue("Rank"));
            double score = Double.parseDouble(eGlycoSpectrum.attributeValue("Score"));
            double preMz = Double.parseDouble(eGlycoSpectrum.attributeValue("PrecursorMz"));
            int preCharge = ms2PeakList.getPrecursePeak().getCharge();
            double mass = Double.parseDouble(eGlycoSpectrum.attributeValue("GlycoMass"));
            double pepmass = Double.parseDouble(eGlycoSpectrum.attributeValue("PeptideMassExperiment"));
            int peptideID = Integer.parseInt(eGlycoSpectrum.attributeValue("PeptideID"));
            String matched = eGlycoSpectrum.attributeValue("MatchedPeaks");
            String glycoCT = eGlycoSpectrum.attributeValue("GlycoCT").replaceAll(" ", "\n");
            String name = eGlycoSpectrum.attributeValue("Name");

            HashSet<Integer> matchedPeaks = new HashSet<Integer>();
            String[] ss = matched.split("_");
            for (int i = 0; i < ss.length; i++) {
                matchedPeaks.add(Integer.parseInt(ss[i]));
            }

            GlycoTree tree = new GlycoTree(glycoCT);
            tree.setIupacName(name);
            tree.setMonoMass(mass);
            tree.parseInfo();

            NGlycoSSM ssm = new NGlycoSSM(scannum, preCharge, preMz, pepmass, peaks, rank, matchedPeaks, tree, score);
            ssm.setRT(rt);
            ssm.setPeptideid(peptideID);

            if (peptideID == -1) {
                unmatchedlist.add(ssm);
            } else {
                matchedlist.add(ssm);
            }
        }

        this.matchedssms = matchedlist.toArray(new NGlycoSSM[matchedlist.size()]);
        this.unmatchedssms = unmatchedlist.toArray(new NGlycoSSM[unmatchedlist.size()]);
    }

    public IGlycoPeptide[] getAllGlycoPeptides()
    {
        return peps;
    }

    public NGlycoSSM[] getMatchedGlycoSpectra()
    {
        return matchedssms;
    }

    public NGlycoSSM[] getUnmatchedGlycoSpectra()
    {
        return unmatchedssms;
    }

    public double[] getBestEstimate()
    {
        return bestEstimate;
    }

    public int getPairNum()
    {
        return matchedssms.length;
    }

    public GlycoPepObject2 getMatchObject(int index, boolean[] selected)
    {
        NGlycoSSM ssm = matchedssms[index];
        IGlycoPeptide peptide = peps[ssm.getPeptideid()];
        GlycoPepObject2 obj2 = new GlycoPepObject2(ssm, peptide, index, selected);
        return obj2;
    }

    /**
     * @param args
     * @throws DocumentException
     */
    public static void main(String[] args) throws DocumentException
    {
        GlycoLFFeasXMLReader2 reader = new GlycoLFFeasXMLReader2("Z:\\WangShuyue\\数据\\my_everything\\170722_pre_g_2.pxml");
        System.out.println(reader.getMatchedGlycoSpectra().length + "\t" + reader.getAllGlycoPeptides().length);
    }
}
