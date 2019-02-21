/*
 ******************************************************************************
 * File: GlycoIdenXMLReader.java * * * Created on 2012-5-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2012-5-16, 08:52:53
 */
public class GlycoIdenXMLReader
{

    private Element root;
    private Iterator<Element> glycanIt;
    private HashMap<Integer, String> peakOneLineMap;
    private NGlycoSSM[] ssms;
    private File file;

    public GlycoIdenXMLReader(String file) throws Exception
    {
        this(new File(file));
    }

    public GlycoIdenXMLReader(File file) throws Exception
    {

        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        this.root = document.getRootElement();
        this.file = file;
        getProfileData();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        // TODO Auto-generated method stub

        GlycoIdenXMLReader reader1 = new GlycoIdenXMLReader("D:\\hulianghai\\" +
                "20130526_HLH_glyco-antibody_HCD_10ms_35%.iden.pxml");
        HashMap<Integer, Double> map1 = new HashMap<Integer, Double>();
        HashSet<String> set0 = new HashSet<String>();
        HashSet<String> set = new HashSet<String>();
        NGlycoSSM[] s1 = reader1.getAllMatches();
        int count = 0;
        int com = 0;
        for (int i = 0; i < s1.length; i++) {
            set0.add(s1[i].getName());
            if (s1[i].getScore() >= 20 && s1[i].getRank() == 1) {
                System.out.println(s1[i].getRT() + "\t" + s1[i].getPepMass());
/*				map1.put(s1[i].getScanNum(), s1[i].getGlycoMass());
				set.add(s1[i].getName());
				count++;
				if(s1[i].getGlycoTree().getType().equals("Complex/Hybrid")){
					com++;
				}
*/
            }
        }
    }

    /**
     *
     */
    private void getProfileData()
    {
        // TODO Auto-generated method stub

        this.peakOneLineMap = new HashMap<Integer, String>();
        Iterator<Element> spIt = root.elementIterator("Spectrum");
        while (spIt.hasNext()) {

            Element sp = spIt.next();
            Integer scannum = Integer.parseInt(sp.attributeValue("Scannum"));
            String peakOneLine = sp.attributeValue("PeakOneLine");

            this.peakOneLineMap.put(scannum, peakOneLine);
        }

        this.glycanIt = root.elementIterator("Glycan");
        this.getAllGlycans();
    }

    private void getAllGlycans()
    {

        ArrayList<NGlycoSSM> list = new ArrayList<NGlycoSSM>();

        while (glycanIt.hasNext()) {

            Element glycan = glycanIt.next();
            double rt = Double.parseDouble(glycan.attributeValue("RT"));
            int scannum = Integer.parseInt(glycan.attributeValue("ScanNum"));

            String peakOneLine = this.peakOneLineMap.get(scannum);
            MS2PeakList peaklist = MS2PeakList.parsePeaksOneLine(peakOneLine);
            double preMz = peaklist.getPrecursePeak().getMz();
            int charge = peaklist.getPrecursePeak().getCharge();
            IPeak[] peaks = peaklist.getPeakArray();

            int rank = Integer.parseInt(glycan.attributeValue("Rank"));
            double score = Double.parseDouble(glycan.attributeValue("Score"));
            double mass = Double.parseDouble(glycan.attributeValue("GlycoMass"));
            double pepMass = Double.parseDouble(glycan.attributeValue("PeptideMass"));
            double pepMassExp = Double.parseDouble(glycan.attributeValue("PeptideMassExperiment"));
            String matched = glycan.attributeValue("MatchedPeaks");
            String glycoCT = glycan.attributeValue("GlycoCT").replaceAll(" ", "\n");
            String name = glycan.attributeValue("Name");

            HashSet<Integer> matchedPeaks = new HashSet<Integer>();
            String[] ss = matched.split("_");
            for (int i = 0; i < ss.length; i++) {
                matchedPeaks.add(Integer.parseInt(ss[i]));
            }

            GlycoTree tree = new GlycoTree(glycoCT);
            tree.setIupacName(name);
            tree.setMonoMass(mass);

            NGlycoSSM ssm = new NGlycoSSM(scannum, charge, preMz, pepMass, pepMassExp, peaks, rank, matchedPeaks, tree, score);
            ssm.setRT(rt);
            String sequence = glycan.attributeValue("Sequence");
            if (sequence != null) {
                ssm.setSequence(sequence);
            }
            list.add(ssm);
        }

        this.ssms = list.toArray(new NGlycoSSM[list.size()]);
    }

    public NGlycoSSM[] getAllMatches()
    {
        return ssms;
    }

    public String[] getTitle()
    {

        String[] title = new String[10];

        title[0] = "Selected";
        title[1] = "Scannum";
        title[2] = "Charge";
        title[3] = "Mz";
        title[4] = "Retention Time";
        title[5] = "Rank";
        title[6] = "IUPAC Name";
        title[7] = "Score";
        title[8] = "Glyco Mass";
        title[9] = "Peptide Mass";

        return title;
    }

    public int getGlycanNum()
    {
        return ssms.length;
    }

    public NGlycoSSM getGlycoMatch(int id)
    {
        return ssms[id];
    }

    public String getFileName()
    {
        return file.getAbsolutePath();
    }

    public File getParentFile()
    {
        return file.getParentFile();
    }

    public NGlycoSSM[] getAllSelectedMatches(int[] idx)
    {

        ArrayList<NGlycoSSM> list = new ArrayList<NGlycoSSM>();
        for (int i = 0; i < idx.length; i++) {
            list.add(ssms[idx[i]]);
        }

        return list.toArray(new NGlycoSSM[list.size()]);
    }

    public void close()
    {
        this.glycanIt = null;
        this.root = null;
        System.gc();
    }

}
