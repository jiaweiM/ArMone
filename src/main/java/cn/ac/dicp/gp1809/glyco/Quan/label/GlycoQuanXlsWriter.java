/*
 ******************************************************************************
 * File: GlycoQuanXlsWriter.java * * * Created on 2013-8-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author ck
 * @version 2013-8-21, 8:07:38
 */
public class GlycoQuanXlsWriter
{
    private static DecimalFormat df4 = DecimalFormats.DF0_4;
    private LabelType labeltype;
    private String[] ratioNames;
    private double[] theoryRatio;
    private int[][] PepRatioStat;
    private int ratioNum;
    private int labelNum;
//	private HashMap<String, NGlycoSSM> NoQuanPepMap;
    private double[][] range;
    private ExcelWriter writer;
    private ExcelFormat format;

    public GlycoQuanXlsWriter(String file, LabelType labelType, String[] ratioNames, double[] theoryRatio)
            throws IOException, RowsExceededException, WriteException
    {

        this.writer = new ExcelWriter(file, new String[]{
                "Identified deglycopeptides", "Quantified glycopeptides",
                "Matched glycopeptides", "Unmatched glycopeptides"});
        this.format = ExcelFormat.normalFormat;

        this.labeltype = labelType;
        this.ratioNames = ratioNames;
        this.theoryRatio = theoryRatio;
        this.ratioNum = ratioNames.length;
        this.labelNum = labelType.getLabelNum();
        this.PepRatioStat = new int[ratioNum][6];
        this.range = new double[ratioNum][4];
        for (int i = 0; i < theoryRatio.length; i++) {
            double tr = theoryRatio[i];
            range[i][0] = tr * 0.5;
            range[i][1] = tr * 0.8;
            range[i][2] = tr * 1.2;
            range[i][3] = tr * 2.0;
        }
//		this.NoQuanPepMap = new HashMap<String, NGlycoSSM>();
        this.addTitle();
    }

    private static void combine(String in, String out) throws IOException, JXLException
    {

        String title = null;
        HashMap<String, String> infomap = new HashMap<String, String>();
        HashMap<String, ArrayList<double[]>> ratiomap = new HashMap<String, ArrayList<double[]>>();
        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            ExcelReader reader = new ExcelReader(files[i], 1);
            String[] line = reader.readLine();
            StringBuilder titlesb = new StringBuilder();
            for (int j = 0; j < line.length; j++) {
                if (j != 5) titlesb.append(line[j]).append("\t");
            }
            if (title == null) title = titlesb.toString();

            while ((line = reader.readLine()) != null) {
                String key = line[0] + line[3];
                if (infomap.containsKey(key)) {
                    double[] ratio = new double[line.length - 8];
                    for (int j = 8; j < line.length; j++) {
                        ratio[j - 8] = Double.parseDouble(line[j]);
                    }
                    ratiomap.get(key).add(ratio);
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < 8; j++) {
                        if (j != 5) sb.append(line[j]).append("\t");
                    }
                    double[] ratio = new double[line.length - 8];
                    for (int j = 8; j < line.length; j++) {
                        ratio[j - 8] = Double.parseDouble(line[j]);
                    }
                    ArrayList<double[]> list = new ArrayList<double[]>();
                    list.add(ratio);
                    infomap.put(key, sb.toString());
                    ratiomap.put(key, list);
                }
            }
            reader.close();
        }

        ExcelWriter writer = new ExcelWriter(out);
        ExcelFormat format = ExcelFormat.normalFormat;
        writer.addTitle(title, 0, format);

        Iterator<String> it = infomap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String info = infomap.get(key);
            ArrayList<double[]> list = ratiomap.get(key);

            StringBuilder sb = new StringBuilder();
            sb.append(info);
            double[] ratio0 = list.get(0);

            if (list.size() == 1) {

                for (int i = 0; i < ratio0.length; i++) {
                    sb.append(ratio0[i]).append("\t");
                }
            } else {

                double[] ratio = new double[ratio0.length];
                for (int i = 0; i < ratio0.length; i++) {
                    ratio[i] += ratio0[i];
                }
                for (int i = 1; i < list.size(); i++) {
                    for (int j = 0; j < ratio.length; j++) {
                        ratio[j] += list.get(i)[j];
                    }
                }
                for (int i = 0; i < ratio.length; i++) {
                    sb.append(df4.format(ratio[i] / (double) list.size())).append("\t");
                }
            }
            writer.addContent(sb.toString(), 0, format);
        }
        writer.close();
    }

    /**
     * @param args
     * @throws DocumentException
     * @throws IOException
     * @throws JXLException
     */
    public static void main(String[] args) throws DocumentException, IOException, JXLException
    {
        // TODO Auto-generated method stub

        String in = "H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Glycan\\20130805_4p_di-labeling_HCD_N-glycan_quantification_1_1-1.pxml";
        String out = "H:\\20130613_glyco_all\\2D\\glyco_spectra\\1\\Rui_20130604_HEK_HILIC_F1.iden.xls";

		/*GlycoLabelFeaturesXMLReader reader = new GlycoLabelFeaturesXMLReader(in);
		IGlycoPeptide[] peps = reader.getAllGlycoPeptides();
		GlycoPeptideLabelPair[] pairs = reader.getAllSelectedPairs();
		NGlycoSSM[] matchedssms = reader.getMatchedGlycoSpectra();
		NGlycoSSM[] unmatchedssms = reader.getUnmatchedGlycoSpectra();
		GlycoQuanResult[] results = reader.getAllResult();

		double[] bestEstimate = reader.getBestEstimate();
		ProteinNameAccesser accesser = reader.getProNameAccesser();
		LabelType labelType = reader.getType();
		String [] ratioNames = new String[]{"2/1"};
		double [] theoryRatio = new double[]{1.0};
		System.out.println(matchedssms.length+"\t"+Arrays.toString(bestEstimate));*/
//		GlycoQuanXlsWriter writer = new GlycoQuanXlsWriter(out, labelType, ratioNames, theoryRatio);
//		writer.write(peps, matchedssms, unmatchedssms, pairs, bestEstimate, accesser, 10.0);
//		writer.write(peps, matchedssms, unmatchedssms, results, bestEstimate, accesser, 10.0);

        GlycoQuanXlsWriter.combine("H:\\NGLYCO_QUAN\\NGlycan_Quan_20131111\\HCD\\temp",
                "H:\\NGLYCO_QUAN\\NGlycan_quan_20131111\\HCD\\20131109_serum_HCC_Normal.xls");
    }

    /**
     * @throws WriteException
     * @throws RowsExceededException
     */
    private void addTitle() throws RowsExceededException, WriteException
    {
        // TODO Auto-generated method stub

        StringBuilder sb1 = new StringBuilder();
        sb1.append("Reference\t");
        sb1.append("Site\t");
        sb1.append("Scan\t");
        sb1.append("Sequence\t");
        sb1.append("MW\t");
        sb1.append("Charge\t");
        sb1.append("Retention time\t");
        sb1.append("Score\t");
        sb1.append("Matched glyco spectra count\t");
        sb1.append("Matched glyco type count\t");

        writer.addTitle(sb1.toString(), 0, format);

        StringBuilder sb2 = new StringBuilder();

        sb2.append("Glycopep scannum\t");
        sb2.append("Glycopep rt\t");
        sb2.append("Precursor m/z\t");
        sb2.append("Precursor mw\t");
        sb2.append("Precursor charge\t");
        sb2.append("Theor glycan mw\t");
        sb2.append("Calc peptide mw\t");
        sb2.append("Glycopep score\t");
        sb2.append("IUPAC Name\t");
        sb2.append("Type\t");

        writer.addTitle(sb2.toString(), 3, format);

        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append("Peptide scannum\t");
        sb3.append("Sequence\t");
        sb3.append("Peptide rt\t");
        sb3.append("Theor peptide mw\t");
        sb3.append("Delta mw\t");
        sb3.append("Delta mw ppm\t");
        sb3.append("Reference\t");
        sb3.append("Site\t");
        sb3.append("DeltaRT\t");

        writer.addTitle(sb3.toString(), 2, format);

        StringBuilder sb4 = new StringBuilder();
        sb4.append("Sequence\t");
        sb4.append("Theor peptide mw\t");
        sb4.append("Theor glycan mw\t");
        sb4.append("IUPAC Name\t");
        sb4.append("Type\t");
        sb4.append("Matched glycan scnans\t");
        sb4.append("Reference\t");
        sb4.append("Site\t");
        for (int i = 0; i < ratioNames.length; i++) {
            sb4.append(ratioNames[i]).append("\t");
        }
        for (int i = 0; i < labelNum; i++) {
            sb4.append("Intensity_" + (i + 1)).append("\t");
        }

        writer.addTitle(sb4.toString(), 1, format);
    }

    public void write(IGlycoPeptide[] peps, NGlycoSSM[] matchedssms, NGlycoSSM[] unmatchedssms,
            GlycoPeptideLabelPair[] pairs, double[] bestEstimate, ProteinNameAccesser accesser, double ppm)
            throws RowsExceededException, WriteException, IOException
    {

        HashMap<Integer, HashSet<String>> namemap = new HashMap<Integer, HashSet<String>>();
        HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();

        for (int i = 0; i < unmatchedssms.length; i++) {

            StringBuilder sb = new StringBuilder();

            NGlycoSSM ssm = unmatchedssms[i];

            sb.append(ssm.getScanNum()).append("\t");
            sb.append(ssm.getRT()).append("\t");
            sb.append(ssm.getPreMz()).append("\t");
            sb.append(ssm.getPreMr()).append("\t");
            sb.append(ssm.getPreCharge()).append("\t");
            sb.append(ssm.getGlycoMass()).append("\t");
            sb.append(ssm.getPepMassExperiment()).append("\t");
            sb.append(ssm.getScore()).append("\t");
            sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
            sb.append(ssm.getGlycoTree().getType()).append("\t");

            writer.addContent(sb.toString(), 3, format);
        }

        HashSet<String> quanset = new HashSet<String>();
        for (int i = 0; i < pairs.length; i++) {

            GlycoPeptideLabelPair pair = pairs[i];
            int pepid = pair.getPeptideId();
            int ssmid = pair.getDeleSSMId();

            StringBuilder sb = new StringBuilder();

            NGlycoSSM ssm = matchedssms[ssmid];
            quanset.add(pepid + "" + ssm.getGlycanid()[0] + "" + ssm.getGlycanid()[1]);

            sb.append(ssm.getScanNum()).append("\t");
            sb.append(ssm.getRT()).append("\t");
            sb.append(ssm.getPreMz()).append("\t");
            sb.append(ssm.getPreMr()).append("\t");
            sb.append(ssm.getPreCharge()).append("\t");
            sb.append(ssm.getGlycoMass()).append("\t");
            sb.append(ssm.getPepMassExperiment()).append("\t");
            sb.append(ssm.getScore()).append("\t");
            sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
            sb.append(ssm.getGlycoTree().getType()).append("\t");

            IGlycoPeptide peptide = peps[pepid];
            GlycoSite[] sites = peptide.getAllGlycoSites();
            int[] loc = new int[sites.length];
            for (int j = 0; j < loc.length; j++) {
                loc[j] = sites[j].modifLocation();
            }
            HashMap<String, SeqLocAround> slamap = peptide
                    .getPepLocAroundMap();

            StringBuilder sitesb = new StringBuilder();
            StringBuilder refsb = new StringBuilder();
            HashSet<ProteinReference> refset = peptide
                    .getProteinReferences();
            for (ProteinReference ref : refset) {
                SimpleProInfo info = accesser.getProInfo(ref.getName());
                refsb.append(info.getRef()).append(";");

                SeqLocAround sla = slamap.get(ref.toString());
                for (int j = 0; j < loc.length; j++) {
                    loc[j] = sites[j].modifLocation();
                    sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
                }
                sitesb.deleteCharAt(sitesb.length() - 1);
                sitesb.append(";");
            }
            sitesb.deleteCharAt(sitesb.length() - 1);
            refsb.deleteCharAt(refsb.length() - 1);

            double deltaMz = ssm.getDeltaMz();
            double deltaMzPPM = deltaMz / ssm.getPepMassExperiment() * 1E6;
            double peprt = peptide.getRetentionTime();
            double calrt = bestEstimate[0] + bestEstimate[1] * ssm.getRT();

            sb.append(peptide.getScanNumBeg()).append("\t");
            sb.append(peptide.getSequence()).append("\t");
            sb.append(peptide.getRetentionTime()).append("\t");
            sb.append(peptide.getPepMrNoGlyco()).append("\t");
            sb.append(df4.format(deltaMz)).append("\t");
            sb.append(df4.format(deltaMzPPM)).append("\t");
            sb.append(refsb).append("\t");
            sb.append(sitesb).append("\t");
            sb.append(df4.format(Math.abs(peprt - calrt))).append("\t");

            double[] ratios = pair.getSelectRatios();
            for (int j = 0; j < ratios.length; j++) {
                sb.append(ratios[j]).append("\t");
            }
            double[] intensity = pair.getTotalIntens();
            for (int j = 0; j < intensity.length; j++) {
                sb.append(intensity[j]).append("\t");
            }

            writer.addContent(sb.toString(), 1, format);
        }

        for (int i = 0; i < matchedssms.length; i++) {

            StringBuilder sb = new StringBuilder();

            NGlycoSSM ssm = matchedssms[i];

            sb.append(ssm.getScanNum()).append("\t");
            sb.append(ssm.getRT()).append("\t");
            sb.append(ssm.getPreMz()).append("\t");
            sb.append(ssm.getPreMr()).append("\t");
            sb.append(ssm.getPreCharge()).append("\t");
            sb.append(ssm.getGlycoMass()).append("\t");
            sb.append(ssm.getPepMassExperiment()).append("\t");
            sb.append(ssm.getScore()).append("\t");
            sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
            sb.append(ssm.getGlycoTree().getType()).append("\t");

            int peptideid = ssm.getPeptideid();

            if (countmap.containsKey(peptideid)) {
                countmap.put(peptideid, countmap.get(peptideid) + 1);
                namemap.get(peptideid).add(
                        ssm.getGlycoTree().getIupacName());
            } else {
                countmap.put(peptideid, 1);
                HashSet<String> set = new HashSet<String>();
                set.add(ssm.getGlycoTree().getIupacName());
                namemap.put(peptideid, set);
            }

            IGlycoPeptide peptide = peps[peptideid];
            GlycoSite[] sites = peptide.getAllGlycoSites();
            int[] loc = new int[sites.length];
            for (int j = 0; j < loc.length; j++) {
                loc[j] = sites[j].modifLocation();
            }
            HashMap<String, SeqLocAround> slamap = peptide
                    .getPepLocAroundMap();

            StringBuilder sitesb = new StringBuilder();
            StringBuilder refsb = new StringBuilder();
            HashSet<ProteinReference> refset = peptide
                    .getProteinReferences();
            for (ProteinReference ref : refset) {
                SimpleProInfo info = accesser.getProInfo(ref.getName());
                refsb.append(info.getRef()).append(";");

                SeqLocAround sla = slamap.get(ref.toString());
                for (int j = 0; j < loc.length; j++) {
                    loc[j] = sites[j].modifLocation();
                    sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
                }
                sitesb.deleteCharAt(sitesb.length() - 1);
                sitesb.append(";");
            }
            sitesb.deleteCharAt(sitesb.length() - 1);
            refsb.deleteCharAt(refsb.length() - 1);

            double deltaMz = ssm.getDeltaMz();
            double deltaMzPPM = deltaMz / ssm.getPepMassExperiment() * 1E6;
            double peprt = peptide.getRetentionTime();
            double calrt = bestEstimate[0] + bestEstimate[1] * ssm.getRT();

            sb.append(peptide.getScanNumBeg()).append("\t");
            sb.append(peptide.getSequence()).append("\t");
            sb.append(peptide.getRetentionTime()).append("\t");
            sb.append(peptide.getPepMrNoGlyco()).append("\t");
            sb.append(df4.format(deltaMz)).append("\t");
            sb.append(df4.format(deltaMzPPM)).append("\t");
            sb.append(refsb).append("\t");
            sb.append(sitesb).append("\t");
            sb.append(df4.format(Math.abs(peprt - calrt))).append("\t");

            writer.addContent(sb.toString(), 2, format);

            String key = peptideid + "" + ssm.getGlycanid()[0] + "" + ssm.getGlycanid()[1];
			/*if(!quanset.contains(key)){
				if(this.NoQuanPepMap.containsKey(key)){
					if(ssm.getScore()>this.NoQuanPepMap.get(key).getScore()){
						this.NoQuanPepMap.put(key, ssm);
					}
				}else{
					this.NoQuanPepMap.put(key, ssm);
				}
			}*/
        }

/*		Iterator <String> it = this.NoQuanPepMap.keySet().iterator();
		while(it.hasNext()){

			String key = it.next();
			StringBuilder sb = new StringBuilder();
			NGlycoSSM ssm = this.NoQuanPepMap.get(key);
			int peptideid = ssm.getPeptideid();

			sb.append(ssm.getPepLabelType(peptideid)+1).append("\t");
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");


			IGlycoPeptide peptide = peps[peptideid];
			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int j = 0; j < loc.length; j++) {
				loc[j] = sites[j].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide
					.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide
					.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
					sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			double deltaMz = ssm.getDeltaMz();
			double deltaMzPPM = deltaMz / ssm.getPepMassExperiment() * 1E6;
			double peprt = peptide.getRetentionTime();
			double calrt = bestEstimate[0] + bestEstimate[1] * ssm.getRT();

			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");
			sb.append(df4.format(Math.abs(peprt-calrt))).append("\t");

			writer.addContent(sb.toString(), 2, format);
		}*/

        ExcelFormat format2 = new ExcelFormat(false, 2);
        for (int i = 0; i < peps.length; i++) {
            StringBuilder sb = new StringBuilder();
            IGlycoPeptide peptide = peps[i];
            GlycoSite[] sites = peptide.getAllGlycoSites();
            int[] loc = new int[sites.length];
            for (int j = 0; j < loc.length; j++) {
                loc[j] = sites[j].modifLocation();
            }
            HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

            StringBuilder sitesb = new StringBuilder();
            HashSet<ProteinReference> refset = peptide.getProteinReferences();
            for (ProteinReference ref : refset) {
                SimpleProInfo info = accesser.getProInfo(ref.getName());
                sb.append(info.getRef()).append(";");

                SeqLocAround sla = slamap.get(ref.toString());
                for (int j = 0; j < loc.length; j++) {
                    loc[j] = sites[j].modifLocation();
                    sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
                }
                sitesb.deleteCharAt(sitesb.length() - 1);
                sitesb.append(";");
            }
            sitesb.deleteCharAt(sitesb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\t");
            sb.append(sitesb).append("\t");
            sb.append(peptide.getScanNumBeg()).append("\t");
            sb.append(peptide.getSequence()).append("\t");
            sb.append(peptide.getPepMrNoGlyco()).append("\t");
            sb.append(peptide.getCharge()).append("\t");
            sb.append(peptide.getRetentionTime()).append("\t");
            sb.append(peptide.getPrimaryScore()).append("\t");
            if (countmap.containsKey(i)) {
                sb.append(countmap.get(i)).append("\t");
                sb.append(namemap.get(i).size());
            }

            boolean confuse = false;
            if (i == 0) {
                if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6) {
                    confuse = true;
                }
            } else if (i == peps.length - 1) {
                if (peptide.getPepMrNoGlyco() - peps[i - 1].getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6) {
                    confuse = true;
                }
            } else {
                if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6
                        || peptide.getPepMrNoGlyco()
                        - peps[i - 1].getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6) {
                    confuse = true;
                }
            }

            if (confuse) {
                writer.addContent(sb.toString(), 0, format2);
            } else {
                writer.addContent(sb.toString(), 0, format);
            }
        }

        writer.close();

    }

    public void write(IGlycoPeptide[] peps, NGlycoSSM[] matchedssms, NGlycoSSM[] unmatchedssms,
            GlycoQuanResult[] results, double[] bestEstimate, ProteinNameAccesser accesser, double ppm)
            throws RowsExceededException, WriteException, IOException
    {

        HashMap<Integer, HashSet<String>> namemap = new HashMap<Integer, HashSet<String>>();
        HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();

        for (int i = 0; i < unmatchedssms.length; i++) {

            StringBuilder sb = new StringBuilder();

            NGlycoSSM ssm = unmatchedssms[i];

            sb.append(ssm.getScanNum()).append("\t");
            sb.append(ssm.getRT()).append("\t");
            sb.append(ssm.getPreMz()).append("\t");
            sb.append(ssm.getPreMr()).append("\t");
            sb.append(ssm.getPreCharge()).append("\t");
            sb.append(ssm.getGlycoMass()).append("\t");
            sb.append(ssm.getPepMassExperiment()).append("\t");
            sb.append(ssm.getScore()).append("\t");
            sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
            sb.append(ssm.getGlycoTree().getType()).append("\t");

            writer.addContent(sb.toString(), 3, format);
        }

        HashSet<String> quanset = new HashSet<String>();
        for (int i = 0; i < results.length; i++) {

            GlycoQuanResult result = results[i];
            IGlycoPeptide peptide = result.getPeptide();
            NGlycoSSM ssm = result.getSsm();
            quanset.add(result.getPeptideId() + "" + ssm.getGlycanid()[0] + "" + ssm.getGlycanid()[1]);

            StringBuilder sb = new StringBuilder();
            sb.append(peptide.getSequence()).append("\t");
            sb.append(peptide.getPepMrNoGlyco()).append("\t");
            sb.append(ssm.getGlycoMass()).append("\t");
            sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
            sb.append(ssm.getGlycoTree().getType()).append("\t");

            Integer[] ssmIdList = results[i].getSSMIds();
            for (int j = 0; j < ssmIdList.length; j++) {
                sb.append(matchedssms[ssmIdList[j]].getScanNum()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\t");

            GlycoSite[] sites = peptide.getAllGlycoSites();
            int[] loc = new int[sites.length];
            for (int j = 0; j < loc.length; j++) {
                loc[j] = sites[j].modifLocation();
            }
            HashMap<String, SeqLocAround> slamap = peptide
                    .getPepLocAroundMap();

            StringBuilder sitesb = new StringBuilder();
            StringBuilder refsb = new StringBuilder();
            HashSet<ProteinReference> refset = peptide
                    .getProteinReferences();
            for (ProteinReference ref : refset) {
                SimpleProInfo info = accesser.getProInfo(ref.getName());
                refsb.append(info.getRef()).append(";");

                SeqLocAround sla = slamap.get(ref.toString());
                for (int j = 0; j < loc.length; j++) {
                    loc[j] = sites[j].modifLocation();
                    sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
                }
                sitesb.deleteCharAt(sitesb.length() - 1);
                sitesb.append(";");
            }
            sitesb.deleteCharAt(sitesb.length() - 1);
            refsb.deleteCharAt(refsb.length() - 1);

            sb.append(refsb).append("\t");
            sb.append(sitesb).append("\t");

            double[] ratios = result.getRatio();
            for (int j = 0; j < ratios.length; j++) {
                sb.append(ratios[j]).append("\t");
            }
            double[] intensity = result.getIntensity();
            for (int j = 0; j < intensity.length; j++) {
                sb.append(intensity[j]).append("\t");
            }

            writer.addContent(sb.toString(), 1, format);
        }

        for (int i = 0; i < matchedssms.length; i++) {

            StringBuilder sb = new StringBuilder();

            NGlycoSSM ssm = matchedssms[i];

            sb.append(ssm.getScanNum()).append("\t");
            sb.append(ssm.getRT()).append("\t");
            sb.append(ssm.getPreMz()).append("\t");
            sb.append(ssm.getPreMr()).append("\t");
            sb.append(ssm.getPreCharge()).append("\t");
            sb.append(ssm.getGlycoMass()).append("\t");
            sb.append(ssm.getPepMassExperiment()).append("\t");
            sb.append(ssm.getScore()).append("\t");
            sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
            sb.append(ssm.getGlycoTree().getType()).append("\t");

            int peptideid = ssm.getPeptideid();

            if (countmap.containsKey(peptideid)) {
                countmap.put(peptideid, countmap.get(peptideid) + 1);
                namemap.get(peptideid).add(
                        ssm.getGlycoTree().getIupacName());
            } else {
                countmap.put(peptideid, 1);
                HashSet<String> set = new HashSet<String>();
                set.add(ssm.getGlycoTree().getIupacName());
                namemap.put(peptideid, set);
            }

            IGlycoPeptide peptide = peps[peptideid];
            GlycoSite[] sites = peptide.getAllGlycoSites();
            int[] loc = new int[sites.length];
            for (int j = 0; j < loc.length; j++) {
                loc[j] = sites[j].modifLocation();
            }
            HashMap<String, SeqLocAround> slamap = peptide
                    .getPepLocAroundMap();

            StringBuilder sitesb = new StringBuilder();
            StringBuilder refsb = new StringBuilder();
            HashSet<ProteinReference> refset = peptide
                    .getProteinReferences();
            for (ProteinReference ref : refset) {
                SimpleProInfo info = accesser.getProInfo(ref.getName());
                refsb.append(info.getRef()).append(";");

                SeqLocAround sla = slamap.get(ref.toString());
                for (int j = 0; j < loc.length; j++) {
                    loc[j] = sites[j].modifLocation();
                    sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
                }
                sitesb.deleteCharAt(sitesb.length() - 1);
                sitesb.append(";");
            }
            sitesb.deleteCharAt(sitesb.length() - 1);
            refsb.deleteCharAt(refsb.length() - 1);

            double deltaMz = ssm.getDeltaMz();
            double deltaMzPPM = deltaMz / ssm.getPepMassExperiment() * 1E6;
            double peprt = peptide.getRetentionTime();
            double calrt = bestEstimate[0] + bestEstimate[1] * ssm.getRT();

            sb.append(peptide.getScanNumBeg()).append("\t");
            sb.append(peptide.getSequence()).append("\t");
            sb.append(peptide.getRetentionTime()).append("\t");
            sb.append(peptide.getPepMrNoGlyco()).append("\t");
            sb.append(df4.format(deltaMz)).append("\t");
            sb.append(df4.format(deltaMzPPM)).append("\t");
            sb.append(refsb).append("\t");
            sb.append(sitesb).append("\t");
            sb.append(df4.format(Math.abs(peprt - calrt))).append("\t");

            writer.addContent(sb.toString(), 2, format);

            String key = peptideid + "" + ssm.getGlycanid()[0] + "" + ssm.getGlycanid()[1];
			/*if(!quanset.contains(key)){
				if(this.NoQuanPepMap.containsKey(key)){
					if(ssm.getScore()>this.NoQuanPepMap.get(key).getScore()){
						this.NoQuanPepMap.put(key, ssm);
					}
				}else{
					this.NoQuanPepMap.put(key, ssm);
				}
			}*/
        }

		/*Iterator <String> it = this.NoQuanPepMap.keySet().iterator();
		while(it.hasNext()){

			String key = it.next();
			StringBuilder sb = new StringBuilder();
			NGlycoSSM ssm = this.NoQuanPepMap.get(key);
			int peptideid = ssm.getPeptideid();

			sb.append(ssm.getPepLabelType(peptideid)+1).append("\t");
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMassExperiment()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");

			IGlycoPeptide peptide = peps[peptideid];
			GlycoSite[] sites = peptide.getAllGlycoSites();
			int[] loc = new int[sites.length];
			for (int j = 0; j < loc.length; j++) {
				loc[j] = sites[j].modifLocation();
			}
			HashMap<String, SeqLocAround> slamap = peptide
					.getPepLocAroundMap();

			StringBuilder sitesb = new StringBuilder();
			StringBuilder refsb = new StringBuilder();
			HashSet<ProteinReference> refset = peptide
					.getProteinReferences();
			for (ProteinReference ref : refset) {
				SimpleProInfo info = accesser.getProInfo(ref.getName());
				refsb.append(info.getRef()).append(";");

				SeqLocAround sla = slamap.get(ref.toString());
				for (int j = 0; j < loc.length; j++) {
					loc[j] = sites[j].modifLocation();
					sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
				}
				sitesb.deleteCharAt(sitesb.length() - 1);
				sitesb.append(";");
			}
			sitesb.deleteCharAt(sitesb.length() - 1);
			refsb.deleteCharAt(refsb.length() - 1);

			double deltaMz = ssm.getDeltaMz();
			double deltaMzPPM = deltaMz / ssm.getPepMassExperiment() * 1E6;
			double peprt = peptide.getRetentionTime();
			double calrt = bestEstimate[0] + bestEstimate[1] * ssm.getRT();

			sb.append(peptide.getScanNumBeg()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRetentionTime()).append("\t");
			sb.append(peptide.getPepMrNoGlyco()).append("\t");
			sb.append(df4.format(deltaMz)).append("\t");
			sb.append(df4.format(deltaMzPPM)).append("\t");
			sb.append(refsb).append("\t");
			sb.append(sitesb).append("\t");
			sb.append(df4.format(Math.abs(peprt-calrt))).append("\t");

			writer.addContent(sb.toString(), 2, format);
		}
		*/
        ExcelFormat format2 = new ExcelFormat(false, 2);
        for (int i = 0; i < peps.length; i++) {
            StringBuilder sb = new StringBuilder();
            IGlycoPeptide peptide = peps[i];
            GlycoSite[] sites = peptide.getAllGlycoSites();
            int[] loc = new int[sites.length];
            for (int j = 0; j < loc.length; j++) {
                loc[j] = sites[j].modifLocation();
            }
            HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();

            StringBuilder sitesb = new StringBuilder();
            HashSet<ProteinReference> refset = peptide.getProteinReferences();
            for (ProteinReference ref : refset) {
                SimpleProInfo info = accesser.getProInfo(ref.getName());
                sb.append(info.getRef()).append(";");

                SeqLocAround sla = slamap.get(ref.toString());
                for (int j = 0; j < loc.length; j++) {
                    loc[j] = sites[j].modifLocation();
                    sitesb.append(sla.getBeg() + loc[j] - 1).append("/");
                }
                sitesb.deleteCharAt(sitesb.length() - 1);
                sitesb.append(";");
            }
            sitesb.deleteCharAt(sitesb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\t");
            sb.append(sitesb).append("\t");
            sb.append(peptide.getScanNumBeg()).append("\t");
            sb.append(peptide.getSequence()).append("\t");
            sb.append(peptide.getPepMrNoGlyco()).append("\t");
            sb.append(peptide.getCharge()).append("\t");
            sb.append(peptide.getRetentionTime()).append("\t");
            sb.append(peptide.getPrimaryScore()).append("\t");
            if (countmap.containsKey(i)) {
                sb.append(countmap.get(i)).append("\t");
                sb.append(namemap.get(i).size());
            }

            boolean confuse = false;
            if (i == 0) {
                if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6) {
                    confuse = true;
                }
            } else if (i == peps.length - 1) {
                if (peptide.getPepMrNoGlyco() - peps[i - 1].getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6) {
                    confuse = true;
                }
            } else {
                if (peps[i + 1].getPepMrNoGlyco() - peptide.getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6
                        || peptide.getPepMrNoGlyco()
                        - peps[i - 1].getPepMrNoGlyco() < peptide
                        .getPepMrNoGlyco() * ppm * 1E-6) {
                    confuse = true;
                }
            }

            if (confuse) {
                writer.addContent(sb.toString(), 0, format2);
            } else {
                writer.addContent(sb.toString(), 0, format);
            }
        }

        writer.close();

    }

}
