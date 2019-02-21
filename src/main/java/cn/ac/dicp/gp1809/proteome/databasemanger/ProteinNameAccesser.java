/*
 ******************************************************************************
 * File: MapFastaAccesser.java * * * Created on 2011-8-9
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.GRAVY.GRAVYCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.pi.PICalculator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 * @version 2011-8-9, 14:07:54
 */
public class ProteinNameAccesser implements Serializable
{
    private static final long serialVersionUID = -909887714390133425L;

    private HashMap<String, SimpleProInfo> proInfoMap;
    private int splitlen;
    private int splitlenRev;
    private boolean usePattern;
    private Pattern pattern;
    private IDecoyReferenceJudger judger;

    public ProteinNameAccesser(IFastaAccesser accesser)
    {
        this.judger = accesser.getDecoyJudger();
        this.splitlen = accesser.getSplitLength();
        this.splitlenRev = accesser.getSplitRevLength();

        if (accesser instanceof AccessionFastaAccesser) {
            this.usePattern = true;
            this.pattern = ((AccessionFastaAccesser) accesser).getPattern();
        } else {
            this.usePattern = false;
            this.pattern = null;
        }

        this.proInfoMap = new HashMap<>();
    }

    public ProteinNameAccesser(int splitlen, int splitlenRev, boolean usePattern, Pattern pattern,
            IDecoyReferenceJudger judger)
    {
        this.splitlen = splitlen;
        this.splitlenRev = splitlenRev;
        this.usePattern = usePattern;
        this.pattern = pattern;
        this.judger = judger;
        this.proInfoMap = new HashMap<>();
    }

    public static void main(String[] args)
    {
        Pattern p = Pattern.compile("IPI:([^| .]*)");
        String ref = "IPI:IPI00330804.4|SWISS-PROT:P07901|TREMBL:A0PJ91;Q3TJU7;Q3TKA2;Q3TKB9;Q3TKG0;Q3UIF3;Q80Y52;Q8C2A9;Q8C5U3|ENSEMBL:ENSMUSP00000021698;ENSMUSP00000091921|REFSEQ:NP_034610|VEGA:OTTMUSP00000021333;OTTMUSP00000021334 Tax_Id=10090 Gene_Symbol=Hsp90aa1 Heat shock protein HSP 90-alpha";
        Matcher m = p.matcher(ref);
        if (m.find()) {
            System.out.println(m.groupCount());
            System.out.println(m.group(1));
        }
    }

    public void addRef(String partName, SimpleProInfo pInfo)
    {
        this.proInfoMap.put(partName, pInfo);
    }

    public void addRef(String partName, ProteinSequence ps)
    {
        if (!proInfoMap.containsKey(partName)) {

            String ref = ps.getReference();
            String seq = ps.getUniqueSequence();
            int length = seq.length();

            MwCalculator mwc = new MwCalculator();
            double mw = mwc.getMonoIsotopeMh(seq) - AminoAcidProperty.PROTON_W;
            double hydroScore = GRAVYCalculator.calculate(seq);
            double PI = PICalculator.compute(seq);
            boolean isDecoy = this.judger.isDecoy(ref);

            SimpleProInfo pInfo = new SimpleProInfo(partName, ref, length, mw, hydroScore, PI, isDecoy);
            this.proInfoMap.put(partName, pInfo);
        }
    }

    public int getSplitLength()
    {
        return this.splitlen;
    }

    public int getSplitRevLength()
    {
        return this.splitlenRev;
    }

    public boolean usePattern()
    {
        return this.usePattern;
    }

    public Pattern getPattern()
    {
        return this.pattern;
    }

    public int getNumberofProteins()
    {
        return this.proInfoMap.size();
    }

    public SimpleProInfo[] getInfosofProteins()
    {
        SimpleProInfo[] infos = new SimpleProInfo[this.proInfoMap.size()];
        infos = this.proInfoMap.values().toArray(infos);
        return infos;
    }

    public String[] getAllKeys()
    {
        String[] keys = new String[this.proInfoMap.size()];
        keys = this.proInfoMap.keySet().toArray(keys);
        return keys;
    }

    public SimpleProInfo getProInfo(String ref)
    {
        return this.proInfoMap.get(ref);
    }

    public void appand(ProteinNameAccesser accesser)
    {
        this.proInfoMap.putAll(accesser.proInfoMap);
    }

}
