/*
 ******************************************************************************
 * File: NGlycoTargetGetter.java * * * Created on 2013-5-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.TargetIden;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.Glycosyl;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoDatabaseMatcher;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.ISpectrum;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.*;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.math.MathTool;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 * @version 2013-5-28, 8:41:13
 */
public class NGlycoTargetGetter
{

    private IRawSpectraReader reader;
    private GlycoJudgeParameter jpara;
    private GlycoDatabaseMatcher matcher;

    private HashMap<Integer, NGlycoSSM[]> glySpecMap;
    private HashMap<Integer, Integer> isotopeMap;

    private double intenThres;
    private double mzThresPPM;
    private double mzThresAMU;
    private int topn;

    private Aminoacids aas;
    private AminoacidModification aam;
    private Enzyme enzyme = Enzyme.TRYPSIN;
    private int missCleave = 2;
    private double ppm;

    private HashMap<Double, String> pepmap;

    private static final double nGlycanCoreFuc = 1038.375127;
    protected static final double Hex = Glycosyl.Hex.getMonoMass();
    protected static final double HexNAc = Glycosyl.HexNAc.getMonoMass();
    protected static final double dHex = Glycosyl.Fuc.getMonoMass();

    protected static final double[] core1 = new double[]{HexNAc, HexNAc * 2,
            HexNAc * 2 + Hex, HexNAc * 2 + Hex * 2, HexNAc * 2 + Hex * 3};

    protected static final double[] core2 = new double[]{HexNAc + dHex,
            HexNAc * 2 + dHex, HexNAc * 2 + Hex + dHex,
            HexNAc * 2 + Hex * 2 + dHex, HexNAc * 2 + Hex * 3 + dHex};

    protected static final int[][] ms2Charge = new int[][]{{1}, {1, 2}, {1, 2}, {2, 3}, {3, 4}};

    /**
     * nbt.1511-S1, p9
     */
    protected static final double dm = 1.00286864;

    private DecimalFormat df4 = DecimalFormats.DF0_4;

    public NGlycoTargetGetter(String peakfile, String fasta)
            throws IOException, XMLStreamException
    {

        if (peakfile.endsWith("mzXML")) {

            this.reader = new MzXMLReader(peakfile);

        } else if (peakfile.endsWith("mzData")) {

            this.reader = new MzDataStaxReader(peakfile);

        } else {
            throw new IOException("Unknown file type: " + peakfile);
        }

        this.jpara = GlycoJudgeParameter.defaultParameter();
        this.matcher = new GlycoDatabaseMatcher(jpara.getMzThresPPM());
        this.glySpecMap = new HashMap<Integer, NGlycoSSM[]>();
        this.isotopeMap = new HashMap<Integer, Integer>();
        this.pepmap = new HashMap<Double, String>();
        this.initialPeptideList(fasta);
        this.ppm = jpara.getMzThresPPM();
        this.parseNGlyco();
    }

    public NGlycoTargetGetter(String peakfile, String fasta, GlycoJudgeParameter parameter)
            throws IOException, XMLStreamException
    {

        if (peakfile.endsWith("mzXML")) {

            this.reader = new MzXMLReader(peakfile);

        } else if (peakfile.endsWith("mzData")) {

            this.reader = new MzDataStaxReader(peakfile);

        } else {
            throw new IOException("Unknown file type: " + peakfile);
        }

        this.jpara = parameter;
        this.ppm = jpara.getMzThresPPM();
        this.matcher = new GlycoDatabaseMatcher(jpara.getMzThresPPM());
        this.glySpecMap = new HashMap<Integer, NGlycoSSM[]>();
        this.isotopeMap = new HashMap<Integer, Integer>();
        this.pepmap = new HashMap<Double, String>();
        this.initialPeptideList(fasta);
        this.parseNGlyco();
    }

    public NGlycoTargetGetter(String peakfile, HashMap<Double, String> pepmap, GlycoJudgeParameter parameter)
            throws IOException, XMLStreamException
    {

        if (peakfile.endsWith("mzXML")) {

            this.reader = new MzXMLReader(peakfile);

        } else if (peakfile.endsWith("mzData")) {

            this.reader = new MzDataStaxReader(peakfile);

        } else {
            throw new IOException("Unknown file type: " + peakfile);
        }

        this.jpara = parameter;
        this.ppm = jpara.getMzThresPPM();
        this.matcher = new GlycoDatabaseMatcher(jpara.getMzThresPPM());
        this.glySpecMap = new HashMap<Integer, NGlycoSSM[]>();
        this.isotopeMap = new HashMap<Integer, Integer>();
        this.pepmap = pepmap;
        this.parseNGlyco();
    }

    private void initialPeptideList(String fasta) throws IOException
    {

        Pattern N_GLYCO = Pattern.compile("N[A-OQ-Z][ST]");
        HashSet<String> set = new HashSet<String>();
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {

            String uniseq = ps.getUniqueSequence();
            ArrayList<Integer> siteloc = new ArrayList<Integer>();
            Matcher glycomatch = N_GLYCO.matcher(uniseq);
            while (glycomatch.find()) {
                siteloc.add(glycomatch.start());
            }

            char[] cs = uniseq.toCharArray();
            int[] cleaveloc = this.enzyme.getCleaveIdx(cs);

            for (int i = 1; i <= this.missCleave + 1; i++) {
                for (int j = 0; j < cleaveloc.length - i; j++) {
                    int start = cleaveloc[j];
                    int len = cleaveloc[j + i] - start;
                    for (int k = 0; k < siteloc.size(); k++) {
                        int loc = siteloc.get(k);
                        if (loc >= start && loc < cleaveloc[j + i]) {
                            String unipepseq = new String(cs, start, len);
                            String pepseq;
                            if (start == 0) {
                                if (cleaveloc[j + i] == uniseq.length()) {
                                    pepseq = "-." + unipepseq + ".-";
                                } else {
                                    pepseq = "-." + unipepseq + "." + uniseq.charAt(cleaveloc[j + i]);
                                }
                            } else {
                                if (cleaveloc[j + i] == uniseq.length()) {
                                    pepseq = uniseq.charAt(start - 1) + "." + unipepseq + ".-";
                                } else {
                                    pepseq = uniseq.charAt(start - 1) + "." + unipepseq + "." + uniseq.charAt(cleaveloc[j + i]);
                                }
                            }
                            set.add(pepseq);
                            break;
                        }
                    }
                }
            }
        }
        fr.close();

        MwCalculator mwcal = new MwCalculator();
        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        mwcal.setAacids(aas);

        ModSite[] modsites = aam.getModifiedSites();
        HashMap<ModSite, Double> modmassmap = new HashMap<ModSite, Double>();
        for (int i = 0; i < modsites.length; i++) {
            HashSet<Modif> modifs = aam.getModifSymbols(modsites[i]);
            for (Modif m : modifs) {
                modmassmap.put(modsites[i], m.getMass());
            }
        }

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String seq = it.next();
            for (int i = 0; i < seq.length(); i++) {
                char aa = seq.charAt(i);
                if (i == 0 && aa == '-') {

                }
            }
            double mw = mwcal.getMonoIsotopeMh(seq)
                    - AminoAcidProperty.PROTON_W;
            this.pepmap.put(mw, seq);
//			System.out.println(seq+"\t"+mw);
        }
    }

    private void parseNGlyco() throws IOException
    {

        HashMap<Integer, IMS2Scan> ms2ScanMap = new HashMap<Integer, IMS2Scan>();

        double ms1TotalCurrent = reader.getMS1TotalCurrent();

        double[] mzs = new double[5];

        // 162.052824; 204.086649; 274.08741263499996; 292.102692635;
        // 366.139472; 657.2348890000001
        mzs[0] = Glycosyl.Hex.getMonoMass() + AminoAcidProperty.PROTON_W;
        mzs[1] = Glycosyl.HexNAc.getMonoMass() + AminoAcidProperty.PROTON_W;
        mzs[2] = Glycosyl.NeuAc_H2O.getMonoMass() + AminoAcidProperty.PROTON_W;
        mzs[3] = Glycosyl.NeuAc.getMonoMass() + AminoAcidProperty.PROTON_W;
        mzs[4] = Glycosyl.Hex.getMonoMass() + Glycosyl.HexNAc.getMonoMass()
                + AminoAcidProperty.PROTON_W;

        intenThres = jpara.getIntenThres();
        mzThresPPM = jpara.getMzThresPPM();
        mzThresAMU = jpara.getMzThresAMU();
        topn = jpara.getTopnStructure();

        ISpectrum spectrum;
        IPeak[] ms1Peaks = null;

        while ((spectrum = reader.getNextSpectrum()) != null) {

            int msLevel = spectrum.getMSLevel();
            double totIonCurrent = spectrum.getTotIonCurrent();

            if (msLevel > 1) {

                MS2Scan ms2 = (MS2Scan) spectrum;

                float preMz = (float) ms2.getPrecursorMZ();
                short preCharge = ms2.getCharge();
                int snum = ms2.getScanNum();

                if (preMz * preCharge <= nGlycanCoreFuc)
                    continue;

                int count = 0;
                IMS2PeakList peaklist = ms2.getPeakList();
                IPeak[] peaks = peaklist.getPeakArray();
                int loc = 0;

                L:
                for (int i = 0; i < peaks.length; i++) {

                    double mz = peaks[i].getMz();
                    double inten = peaks[i].getIntensity();
                    if (inten / totIonCurrent < intenThres)
                        continue;

                    if ((mz - mzs[4]) > mzThresAMU) {
                        break;
                    }

                    for (int j = loc; j < mzs.length; j++) {

                        if ((mzs[j] - mz) > mzThresAMU)
                            continue L;

                        if (Math.abs(mz - mzs[j]) <= mzThresAMU) {
                            count++;

                        } else if ((mz - mzs[j]) > mzThresAMU) {
                            loc = j + 1;
                        }
                    }
                }

                if (count >= 2) {
                    this.findIsotope(ms2, ms1Peaks);
                    ms2ScanMap.put(snum, ms2);
                }

            } else if (msLevel == 1) {
                ms1Peaks = spectrum.getPeakList().getPeakArray();
            }
        }

        Double[] pepmasses = this.pepmap.keySet().toArray(new Double[pepmap.size()]);
        Arrays.sort(pepmasses);

        Iterator<Integer> it = ms2ScanMap.keySet().iterator();
        while (it.hasNext()) {

            Integer scannum = it.next();
            IMS2Scan ms2 = ms2ScanMap.get(scannum);

//			float preMz = (float) ms2.getPrecursorMZ();
            if (scannum == 20420) {
                float preMz = (float) ms2.getPeakList().getPrecursePeak().getMz();
                short preCharge = ms2.getCharge();
                double rt = ms2.getRTMinute();
                int preScannum = ms2.getPrecursorScannum();

                IPeak[] peaks = ms2.getPeakList().getPeakArray();

                ArrayList<NGlycoSSM> ssmlist = new ArrayList<NGlycoSSM>();

                for (int i = 0; i < pepmasses.length; i++) {
                    if (pepmasses[i] > preMz * preCharge)
                        break;

                    if (!this.matchCore(peaks, pepmasses[i], preCharge)) {
                        continue;
                    }

                    String sequence = this.pepmap.get(pepmasses[i]);
                    NGlycoSSM[] ssms = this.matcher.match(pepmasses[i], preMz, preCharge, scannum, peaks, isotopeMap.get(scannum));
                    System.out.println(scannum + "\t" + preMz + "\t" + isotopeMap.get(scannum));
                    if (ssms != null) {
                        for (int j = 0; j < ssms.length; j++) {
                            ssmlist.add(ssms[j]);
                            ssms[j].setSequence(sequence);
                            System.out.println(scannum + "\t" + preMz + "\t" + isotopeMap.get(scannum) + "\t" + ssms.length + "\t" + ssms[j].getPepMass());
                        }
                    } else {
                    }
                }

                if (ssmlist.size() > 0) {

                    NGlycoSSM[] totallist = ssmlist.toArray(new NGlycoSSM[ssmlist.size()]);
                    Arrays.sort(totallist, new Comparator<NGlycoSSM>()
                    {

                        @Override
                        public int compare(NGlycoSSM arg0, NGlycoSSM arg1)
                        {
                            // TODO Auto-generated method stub

                            if (arg0.getScore() > arg1.getScore()) {

                                return -1;

                            } else if (arg0.getScore() < arg1.getScore()) {

                                return 1;
                            }

                            return 0;
                        }

                    });

                    for (int i = 0; i < totallist.length; i++) {

                        totallist[i].setRank(i + 1);
                        totallist[i].setMS1Scannum(preScannum);
                        totallist[i].setRT(rt);

                        System.out.println(totallist[i].getPepMass() + "\t" + totallist[i].getPepMassExperiment() + "\t" + totallist[i].getScore()
                                + "\t" + totallist[i].getGlycoMass() + "\t" + totallist[i].getPreMz() + "\t" + totallist[i].getPreMr());
                    }

                    if (totallist.length <= topn) {
                        this.glySpecMap.put(scannum, totallist);
                    } else {
                        NGlycoSSM[] toplist = new NGlycoSSM[topn];
                        System.arraycopy(totallist, 0, toplist, 0, topn);
                        this.glySpecMap.put(scannum, toplist);
                    }
                }
                break;
            }
        }
    }

    private boolean matchCore(IPeak[] peaks, double pepmass, int preCharge)
    {

        int[] charge;
        if (preCharge < 6) {
            charge = ms2Charge[preCharge - 1];
        } else {
            charge = new int[]{(preCharge - 3), (preCharge - 2),
                    (preCharge - 1)};
        }

        double dd = 0;
        for (int i = charge.length - 1; i >= 0; i--) {

            int[] c1 = new int[5];
            int[] c2 = new int[5];

            for (int j = 0; j < peaks.length; j++) {

                double peakjmass = (peaks[j].getMz() - dm) * (double) charge[i];
                if (peakjmass > dd) dd = peakjmass;

                if (peakjmass > (pepmass + core2[4] + 3))
                    break;

                for (int k = 0; k < core1.length; k++) {

                    if (Math.abs(peakjmass - pepmass - core1[k]) < peakjmass
                            * ppm * 1E-6
                            || Math.abs(peakjmass - pepmass - core1[k] - dm) < peakjmass
                            * ppm * 1E-6) {
                        c1[k] = 1;
                    }
                }
                for (int k = 0; k < core2.length; k++) {

                    if (Math.abs(peakjmass - pepmass - core2[k]) < peakjmass
                            * ppm * 1E-6
                            || Math.abs(peakjmass - pepmass - core2[k] - dm) < peakjmass
                            * ppm * 1E-6) {
                        c2[k] = 1;
                    }
                }
            }

            int c1total = MathTool.getTotal(c1);
            int c2total = MathTool.getTotal(c2);

            if (c1total >= 2) {
                if (c1total >= 3 && (c1[0]) > 0) {
                    return true;
                } else {
                    if (c1total + c2total >= 5
                            && (c1[0] + c2[0]) > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void findIsotope(IMS2Scan scan, IPeak[] ms1Peaks)
    {

        IPeak precursorPeak = scan.getPeakList().getPrecursePeak();
        int isoloc = Arrays.binarySearch(ms1Peaks, precursorPeak);
        if (isoloc < 0) isoloc = -isoloc - 1;
        int charge = scan.getCharge();
        double mz = precursorPeak.getMz();
        double intensity = precursorPeak.getIntensity();
        int k = 1;
        int i = isoloc;

        for (; i >= 0; i--) {
            double delta = mz - ms1Peaks[i].getMz() - k * dm / (double) charge;
            double loginten = Math.log10(intensity / ms1Peaks[i].getIntensity());
            if (Math.abs(delta) <= mz * mzThresPPM * 1E-6) {
                if (Math.abs(loginten) < 1.2) {
                    k++;
                    if (intensity < ms1Peaks[i].getIntensity())
                        intensity = ms1Peaks[i].getIntensity();
                }
            } else if (delta > mz * mzThresPPM * 1E-6) {
                break;
            }
//System.out.println("NGlycoTargetGetter\t"+mz+"\t"+ms1Peaks[i].getMz()+"\t"+delta+"\t"+mz*mzThresPPM*1E-6+"\t"+loginten+"\t"+k);
        }
        if (k > 7) k = 7;
        this.isotopeMap.put(scan.getScanNumInteger(), k - 1);
    }

    public HashMap<Integer, NGlycoSSM[]> getGlySpecMap()
    {
        return glySpecMap;
    }

    public HashMap<Double, String> getPepMap()
    {
        return pepmap;
    }

    private static void peptideTest(String peptide, String peak) throws NumberFormatException, IOException, XMLStreamException
    {
        HashMap<Double, String> map = new HashMap<Double, String>();
        BufferedReader br = new BufferedReader(new FileReader(peptide));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] cs = line.split("\t");
            map.put(Double.parseDouble(cs[1]), cs[0]);
        }
        br.close();

        GlycoJudgeParameter jpara =
                new GlycoJudgeParameter(0.001f, 50f, 0.15f, 500, 0.3f, 60.0f, 1);

        NGlycoTargetGetter getter = new NGlycoTargetGetter(peak, map, jpara);
    }

    /**
     * @param args
     * @throws XMLStreamException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException,
            XMLStreamException
    {
        GlycoJudgeParameter jpara =
                new GlycoJudgeParameter(0.001f, 20f, 0.15f, 500, 0.3f, 60.0f, 3);

        NGlycoTargetGetter getter = new NGlycoTargetGetter(
                "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.mzXML",
                "H:\\20130519_glyco\\fetuin.fasta", jpara);

//		NGlycoTargetGetter.peptideTest("H:\\20130519_glyco\\HCD20130523\\peps.txt", 
//				"H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.mzXML");
    }
}
