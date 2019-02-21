/*
 ******************************************************************************
 * File: OGlycanShortPeptide.java * * * Created on 2014��3��25��
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.drawjf.MyXYPointerAnnotation3;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.drawjf.Annotations;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.FileCopy;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;
import cn.ac.dicp.gp1809.util.math.Arrangmentor;
import cn.ac.dicp.gp1809.util.math.MathTool;
import jxl.JXLException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 */
public class OGlycanShortPeptideMatcher
{

    private static DecimalFormat df4 = DecimalFormats.DF0_4;
    private double[] itemMasses;
    private DatabaseItem[] items;
    private ArrayList<OGlycanPepInfo> infoList;

    public OGlycanShortPeptideMatcher(String database) throws IOException
    {
        this.parseDatabase(database);
    }

    private static void knownMatch(String scanname, String sequence, String glycan, String mgf, String out)
            throws DtaFileParsingException, IOException
    {

        int[] ionType = new int[]{Ion.TYPE_B, Ion.TYPE_Y};
        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        AminoacidFragment aaf = new AminoacidFragment(aas, aam);
        MwCalculator mwc = new MwCalculator(aas, aam);
        double mass = mwc.getMonoIsotopeMh(sequence);
        Ions ions = aaf.fragment(sequence, ionType, true);
        Ion[] bs = ions.bIons();
        Ion[] ys = ions.yIons();
        double[] bmass = new double[bs.length];
        double[] ymass = new double[ys.length];
        for (int i = 0; i < bs.length; i++) {
            bmass[i] = bs[i].getMzVsCharge(1);
            ymass[i] = ys[i].getMzVsCharge(1);
        }

        OGlycanUnit unit = OGlycanUnit.valueOf(glycan);
        double[] fragmass = OGlycanUnit.getTotalFragmentMasses();
        String[] fragnames = OGlycanUnit.getTotalFragmentNames();
        int[] fragid = unit.getFragid();
        double[] gs = new double[fragid.length + 1];
        String[] gnames = new String[fragid.length + 1];
        gs[0] = mass;
        gnames[0] = "Pep";
        for (int i = 0; i < fragid.length; i++) {
            gs[i + 1] = fragmass[fragid[i]];
            gnames[i + 1] = fragnames[fragid[i]];
        }

        MgfReader mr = new MgfReader(mgf);
        MS2Scan scan = null;
        while ((scan = mr.getNextMS2Scan()) != null) {
            String sn = scan.getScanName().getBaseName();
            if (!(sn + ".3").equals(scanname)) continue;
            int charge = scan.getCharge();
            double mz = scan.getPrecursorMZ();
            double pepmr = (mz - AminoAcidProperty.PROTON_W) * charge;
            IPeak[] peaks = scan.getPeakList().getPeaksSortByIntensity();

            double percent = 0;
            double intenthres1 = 0;
            double intenthres2 = 0;
            int thres = 0;
            DecimalFormat df4 = DecimalFormats.DF0_4;

            if (peaks.length > 100) {

                if (peaks.length >= 400) {
                    percent = 1.0;
                } else if (peaks.length < 400 && peaks.length >= 300) {
                    percent = 0.75;
                } else if (peaks.length < 300 && peaks.length >= 200) {
                    percent = 0.5;
                } else if (peaks.length < 200 && peaks.length >= 100) {
                    percent = 0.25;
                }

                ArrayList<Double> tempintenlist = new ArrayList<Double>();
                double[] rsdlist = new double[peaks.length];

                for (int i = peaks.length - 1; i >= 0; i--) {
                    tempintenlist.add(peaks[i].getIntensity());
                    rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
                }

                for (int i = 0; i < rsdlist.length; i++) {
                    if (rsdlist[i] > 0.5) {
                        intenthres1 = peaks[peaks.length - i].getIntensity();
                    }
                    if (rsdlist[i] > percent) {
                        intenthres2 = peaks[peaks.length - i].getIntensity() / 5.0;
                    }
                    if (intenthres1 > 0 && intenthres2 > 0) {
                        thres = i;
                        break;
                    }
                }

                if (intenthres2 > intenthres1) {
                    intenthres2 = intenthres1;
                }
            }

            IPeak[] peaks2 = new IPeak[peaks.length - thres];
            System.arraycopy(peaks, 0, peaks2, 0, peaks2.length);

            int[] markpeaks = new int[7];
            boolean oglycan = false;
            double tolerance1 = mz * 2E-5 * 3;
            double tolerance2 = 0.1;
            HashMap<Double, String> matchMap1 = new HashMap<Double, String>();

            for (int i = 0; i < peaks2.length; i++) {

                double mzi = Double.parseDouble(df4.format(peaks2[i].getMz()));
                double inteni = peaks2[i].getIntensity();

                if (inteni > intenthres1) {

                    if (Math.abs(mzi - 163.060101) < tolerance2) {
                        matchMap1.put(mzi, "Hex");
                        continue;
                    } else if (Math.abs(mzi - 204.086649) < tolerance2) {
                        matchMap1.put(mzi, "HexNAc");
                        oglycan = true;
                        continue;
                    } else if (Math.abs(mzi - 274.087412635) < tolerance2) {
                        matchMap1.put(mzi, "NeuAc-H2O");
                        markpeaks[0] = 1;
                        continue;
                    } else if (Math.abs(mzi - 292.102692635) < tolerance2) {
                        matchMap1.put(mzi, "NeuAc");
                        markpeaks[0] = 1;
                        continue;
                    } else if (Math.abs(mzi - 366.139472) < tolerance2) {
                        matchMap1.put(mzi, "HexNAc+Hex");
                        markpeaks[1] = 1;
                        continue;
                        // HexNAc+NeuAc
                    } else if (Math.abs(mzi - 495.18269) < tolerance2) {
//						matchMap1.put(mzi, "HexNAc+NeuAc");
//						markpeaks[2] = 1;
                        continue;
                        // NeuAc*2
                    } else if (Math.abs(mzi - 583.19873) < tolerance2) {
//						matchMap1.put(mzi, "NeuAc*2");
//						markpeaks[3] = 1;
                        continue;
                        // HexNAc*2
                    } else if (Math.abs(mzi - 407.16665) < tolerance2) {
//						matchMap1.put(mzi, "HexNAc*2");
//						markpeaks[4] = 1;
                        continue;
                        // HexNAc*2+Hex
                    } else if (Math.abs(mzi - 569.218847) < tolerance2) {
//						matchMap1.put(mzi, "HexNAc*2+Hex");
//						markpeaks[5] = 1;
                        continue;
                        // HexNAc*2+Hex*2
                    } else if (Math.abs(mzi - 731.271672) < tolerance2) {
//						matchMap1.put(mzi, "HexNAc*2+Hex*2");
//						markpeaks[6] = 1;
                        continue;
                        // HexNAc+Hex*2, N-glyco
                    }
                } else {
                    break;
                }
            }

            if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
                return;
            }

            HashMap<Double, String> matchmap2 = new HashMap<Double, String>();
            OGlycanPepInfo opInfo = validate(unit, sequence, peaks2,
                    bmass, ymass, gs, gnames, matchmap2);

            opInfo.setScanname(scanname + ".0");
            BufferedImage image = createImage(opInfo, peaks2);
            ImageIO.write(image, "PNG",
                    new File(out + "\\" + scanname.substring(scanname.indexOf(":") + 1) + ".png"));
        }
    }

    private static OGlycanPepInfo validate(OGlycanUnit unit, String uniPepSeq, IPeak[] peaks, double[] bs, double[] ys,
            double[] gs,
            String[] gNames, HashMap<Double, String> matchmap2)
    {

        double tolerance = 0.1;
        double H2O = 18.010565;
        DecimalFormat df4 = DecimalFormats.DF0_4;
        DecimalFormat df2 = DecimalFormats.DF0_2;

        int stcount = 0;
        int glycoCount = 1;
        int[] bionSTCount = new int[uniPepSeq.length()];
        int[] yionSTCount = new int[uniPepSeq.length()];
        for (int i = 0; i < uniPepSeq.length(); i++) {
            if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
                stcount++;
                for (int j = i; j < uniPepSeq.length(); j++)
                    bionSTCount[j]++;
            }
        }
        for (int i = 0; i < bionSTCount.length - 1; i++) {
            yionSTCount[i] = bionSTCount[bionSTCount.length - 1] - bionSTCount[bionSTCount.length - i - 2];
        }

        int[] initialList = new int[stcount];
        for (int i = 0; i < stcount; i++) {
            if (i < glycoCount) {
                initialList[i] = i;
            } else {
                initialList[i] = -1;
            }
        }
        int[][] positionList = Arrangmentor.arrangementArrays(initialList);
        double[] scoreList = new double[positionList.length];
        double[] binten = new double[bs.length];
        double[] yinten = new double[ys.length];
        double[] ginten = new double[gs.length];
        double[] ginten2 = new double[gs.length];
//System.out.println("391\t"+uniPepSeq+"\t"+ys.length+"\t"+bs.length);
        int[] glycoFragmentId = unit.getFragid();
        String[] fragnames = OGlycanUnit.getTotalFragmentNames();
        double[] fragmasses = OGlycanUnit.getTotalFragmentMasses();
        HashSet<String> usedset1 = new HashSet<String>();
        HashSet<String> usedset2 = new HashSet<String>();
        HashMap<String, String> annotionMap1 = new HashMap<String, String>();
        HashMap<String, String> annotionMap2 = new HashMap<String, String>();
        HashMap<String, Boolean> annotionMap3 = new HashMap<String, Boolean>();
        HashMap<Double, String> matchmap = new HashMap<Double, String>();
//		boolean coreb = false;
//		System.out.println(uniPepSeq+"\t"+annotionMap1+"\n"+annotionMap2);

        double thres = 0;
        for (int i = 0; i < peaks.length; i++) {
            if (i == 20) break;
            thres += peaks[i].getIntensity();
        }
        thres = thres / 20.0;
		/*if(peaks.length%5==0){
			thres = (peaks[peaks.length/5].getIntensity()+peaks[peaks.length/5-1].getIntensity())/2.0;
		}else{
			thres = peaks[peaks.length/5].getIntensity();
		}*/
		/*ArrayList<Double> totalGIntenList = new ArrayList<Double>();
		double[] peakintens = new double[peaks.length];
		for(int i=0;i<peakintens.length;i++){
			peakintens[i] = peaks[i].getIntensity();
		}*/
        L:
        for (int i = 0; i < peaks.length; i++) {

            double mzi = Double.parseDouble(df4.format(peaks[i].getMz()));
			/*double inteni = peaks[i].getIntensity();

			if(inteni>thres){
				inteni = (peaks.length-i)/(double)peaks.length*(2-thres/peaks[i].getIntensity());
			}else{
				inteni = (peaks.length-i)/(double)peaks.length*(peaks[i].getIntensity()/thres);
			}*/

            double inteni = (peaks.length - i) / (double) peaks.length;
            double[] tempScoreList = new double[positionList.length];
            int mzMatchCount = 0;
            if (matchmap2.containsKey(mzi)) continue;

            for (int j = 0; j < gs.length; j++) {
                double gfragmz = gs[j];
                if (Math.abs(mzi - gfragmz) < tolerance) {
                    if (usedset1.contains(gNames[j]))
                        continue L;

                    usedset1.add(gNames[j]);
                    ginten[j] = inteni;
                    matchmap.put(mzi, gNames[j]);
                    continue L;
                }
            }
            for (int j = 1; j < gs.length; j++) {
                double gfragmz2 = gs[j] - H2O;
                if (Math.abs(mzi - gfragmz2) < tolerance) {
                    if (usedset1.contains(gNames[j] + "-H2O"))
                        continue L;

                    usedset1.add(gNames[j] + "-H2O");
                    ginten2[j] = inteni;
                    matchmap.put(mzi, gNames[j] + "-H2O");
                    continue L;
                }
            }

            for (int j = 0; j < bs.length; j++) {

                double bfragmz = bs[j];
                double yfragmz = ys[j];

                if (Math.abs(mzi - yfragmz) < tolerance) {
                    if (usedset1.contains("y" + (j + 1)))
                        continue L;

                    usedset1.add("y" + (j + 1));
                    yinten[j] = inteni;
                    matchmap.put(mzi, "y" + (j + 1));
                    continue L;
                }

                if (Math.abs(mzi - bfragmz) < tolerance) {
                    if (usedset1.contains("b" + (j + 1)))
                        continue L;

                    usedset1.add("b" + (j + 1));
                    binten[j] = inteni;
                    matchmap.put(mzi, "b" + (j + 1));
                    continue L;
                }

                for (int k = 0; k < glycoFragmentId.length; k++) {
                    double glycoMasses = 0;
                    StringBuilder glycoSb = new StringBuilder();
                    glycoMasses += fragmasses[glycoFragmentId[k]];
                    glycoSb.append(fragnames[glycoFragmentId[k]]);

                    if (yionSTCount[j] >= 1) {
                        if (Math.abs(mzi - yfragmz - glycoMasses) < tolerance) {
                            if (usedset2.contains("y" + (j + 1) + "+(" + glycoSb + ")"))
                                continue L;

//							totalGIntenList.add(peaks[i].getIntensity());
                            usedset2.add("y" + (j + 1) + "+(" + glycoSb + ")");
                            matchmap.put(mzi, "y" + (j + 1) + "+(" + glycoSb + ")");
                            match(positionList, tempScoreList, stcount - yionSTCount[j], inteni, 1);
                            mzMatchCount++;
//							System.out.println("434\t"+mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }

                        if (Math.abs(mzi - yfragmz - (glycoMasses - H2O)) < tolerance) {
                            if (usedset2.contains("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + ")"))
                                continue L;

//							totalGIntenList.add(peaks[i].getIntensity());
                            usedset2.add("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + ")");
                            matchmap.put(mzi, "y" + (j + 1) + "+(" + glycoSb + "-H2O*" + ")");
                            match(positionList, tempScoreList, stcount - yionSTCount[j], inteni, 1);
                            mzMatchCount++;
//							System.out.println("452\t"+mzi+"\t"+inteni+"\t"+("y"+(j+1)+"+("+glycoSb+"-H2O*"+")")+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }

                    }
                    if (bionSTCount[j] >= 1) {
                        if (Math.abs(mzi - bfragmz - glycoMasses) < tolerance) {
                            if (usedset2.contains("b" + (j + 1) + "+(" + glycoSb + ")"))
                                continue L;

//							totalGIntenList.add(peaks[i].getIntensity());
                            usedset2.add("b" + (j + 1) + "+(" + glycoSb + ")");
                            matchmap.put(mzi, "b" + (j + 1) + "+(" + glycoSb + ")");
                            match(positionList, tempScoreList, bionSTCount[j], inteni, 0);
                            mzMatchCount++;
//							System.out.println("469\t"+mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }

                        if (Math.abs(mzi - bfragmz - (glycoMasses - H2O)) < tolerance) {
                            if (usedset2.contains("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + ")"))
                                continue L;

//							totalGIntenList.add(peaks[i].getIntensity());
                            usedset2.add("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + ")");
                            matchmap.put(mzi, "b" + (j + 1) + "+(" + glycoSb + "-H2O*" + ")");
                            match(positionList, tempScoreList, bionSTCount[j], inteni, 0);
                            mzMatchCount++;
//							System.out.println("486\t"+mzi+"\t"+inteni+"\t"+"b"+(j+1)+"+("+glycoSb+"-H2O*"+")"+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }
                    }
                }
            }

            if (mzMatchCount > 0) {
                for (int j = 0; j < scoreList.length; j++) {
                    scoreList[j] += tempScoreList[j] / (double) mzMatchCount;
                }
            }
        }

//		if(usedset1.size()<4){
//			return null;
//		}

//System.out.println(matchmap);
        matchmap.putAll(matchmap2);
        double baseScore = MathTool.getTotal(binten) + MathTool.getTotal(yinten) + MathTool.getTotal(ginten) + MathTool.getTotal(ginten2);
        double totalModScore = 0;
        double maxScore = 0;
//System.out.println(MathTool.getTotal(binten)+"\t"+MathTool.getTotal(yinten)+"\t"+MathTool.getTotal(ginten)+"\t"+MathTool.getTotal(ginten2)+"\t"+baseScore);
//System.out.println(Arrays.toString(binten)+"\t"+Arrays.toString(yinten)+"\t"+Arrays.toString(ginten)+"\t"+Arrays.toString(ginten2)+"\t"+Arrays.toString(scoreList));
        if (baseScore == 0) return null;

        int maxId = -1;
        double[][] positionTypeModScore = new double[glycoCount][stcount];
        for (int i = 0; i < scoreList.length; i++) {

            totalModScore += scoreList[i];

            for (int j = 0; j < stcount; j++) {
                if (positionList[i][j] >= 0)
                    positionTypeModScore[positionList[i][j]][j] += scoreList[i];
            }
//			System.out.println();
            if (scoreList[i] > maxScore) {
                maxScore = scoreList[i];
                maxId = i;
            }
        }
//		System.out.println("724\tscorelist\t"+Arrays.toString(scoreList));

        Arrays.sort(scoreList);
        double peptideScore = 0;
        if (maxScore != 0) {
            if (scoreList.length == 1) {
                peptideScore = 1.0;
            } else {
                peptideScore = (maxScore - scoreList[scoreList.length - 2]) / maxScore;
            }
        }

        double[] positionModScore = new double[stcount];
        if (totalModScore == 0) {
            for (int i = 0; i < positionModScore.length; i++) {
                positionModScore[i] = glycoCount / (double) stcount;
            }
        } else {
            for (int i = 0; i < positionModScore.length; i++) {
                for (int j = 0; j < glycoCount; j++) {
                    positionModScore[i] += positionTypeModScore[j][i];
                }
//				positionModScore[i] = positionModScore[i]*(double)glycoCount/totalModScore;
                positionModScore[i] = positionModScore[i] / totalModScore;
            }
            initialList = positionList[maxId];
        }

        for (int j = 0; j < positionTypeModScore[0].length; j++) {
            double posiTotal = 0;
            for (int i = 0; i < positionTypeModScore.length; i++) {
                posiTotal += positionTypeModScore[i][j];
            }
            for (int i = 0; i < positionTypeModScore.length; i++) {
                positionTypeModScore[i][j] = posiTotal == 0 ? 0 : positionTypeModScore[i][j] / posiTotal;
            }
        }

        int stid = 0;
        int glycoid = 0;
        int[] locList = new int[glycoCount];
        double[] locScoreList = new double[glycoCount];
        boolean[] determined = new boolean[glycoCount];
        ArrayList<Double> restScore = new ArrayList<Double>();

        OGlycanUnit[] newUnits = new OGlycanUnit[glycoCount];
        double[][] newPositionTypeModScore = new double[glycoCount][stcount];
        StringBuilder finalsb = new StringBuilder();
        StringBuilder scoresb = new StringBuilder();
        for (int i = 0; i < uniPepSeq.length(); i++) {
            finalsb.append(uniPepSeq.charAt(i));
            scoresb.append(uniPepSeq.charAt(i));
            if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
                if (positionModScore[stid] != 0) {
                    scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
                    if (initialList[stid] >= 0) {
//						finalsb.append("(").append(units[initialList[stid]].getName())
//						.append(",").append(df2.format(positionModScore[stid])).append(")");
                        finalsb.append("[").append(unit.getComposition()).append("]");
                        locList[glycoid] = (i + 1);
                        locScoreList[glycoid] = positionModScore[stid];
                        newUnits[glycoid] = unit;
                        newPositionTypeModScore[glycoid] = positionTypeModScore[initialList[stid]];
                        glycoid++;
                    } else {
//						scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
                        restScore.add(positionModScore[stid]);
                    }
                }
                stid++;
            }
        }

        L:
        for (int i = 0; i < determined.length; i++) {
            for (int j = 0; j < restScore.size(); j++) {
                if (locScoreList[i] == restScore.get(j)) {
                    determined[i] = false;
                    continue L;
                }
            }
            determined[i] = true;
        }

        for (int i = 0; i < newUnits.length; i++) {
            if (newUnits[i] == null) {
                return null;
            }
        }
/*
		if(uniPepSeq.equals("AATVGSLAGQPLQER")){
			for(int i=0;i<positionModScore.length;i++){
				System.out.print(positionModScore[i]+"\t");
			}
			System.out.println("positionModScore");
			for(int i=0;i<newPositionTypeModScore.length;i++){
				for(int j=0;j<newPositionTypeModScore[i].length;j++){
					System.out.print(newPositionTypeModScore[i][j]+"\t");
				}
//				System.out.println();
			}
//			System.out.println("newPositionTypeModScore\n");
		}
*/
//		System.out.println(finalsb);
        OGlycanPepInfo pepInfo = new OGlycanPepInfo(uniPepSeq, finalsb.toString(), scoresb.toString(), maxScore, baseScore,
                locList, newUnits, locScoreList, determined, positionModScore, newPositionTypeModScore, matchmap, peaks);
//		if(peaks.length>=0 && peaks.length<30)
//		System.out.println(maxScore+"\t"+peaks.length);
        pepInfo.setAnnotionMap1(annotionMap1);
        pepInfo.setAnnotionMap2(annotionMap2);
        pepInfo.setAnnotionMap3(annotionMap3);
//		System.out.println(matchmap);
//		pepInfo.writeAnnotion();
/*		Arrays.sort(peakintens);
		if(totalGIntenList.size()>2 && pepInfo.getTotalScore()>4){
			Double[] ddd = totalGIntenList.toArray(new Double[totalGIntenList.size()]);
			Arrays.sort(ddd);
			double i0 = ddd[0];
			double i1 = MathTool.getAve(ddd);
			double i2 = MathTool.getMedian(ddd);
			double i3 = ddd[ddd.length-1];
			int id0 = Arrays.binarySearch(peakintens, i0);
			if(id0<0) id0 = -id0-1;
			int id1 = Arrays.binarySearch(peakintens, i1);
			if(id1<0) id1 = -id1-1;
			int id2 = Arrays.binarySearch(peakintens, i2);
			if(id2<0) id2 = -id2-1;
			int id3 = Arrays.binarySearch(peakintens, i3);
			if(id3<0) id3 = -id3-1;
//			System.out.println(peakintens.length+"\t"+i0+"\t"+id0+"\t"+i1+"\t"+id1+"\t"+i2+"\t"+id2+"\t"+i3+"\t"+id3);
			System.out.println((peakintens.length-id0)/(double)peakintens.length+"\t"+(peakintens.length-id1)/(double)peakintens.length
					+"\t"+(peakintens.length-id2)/(double)peakintens.length+"\t"+(peakintens.length-id3)/(double)peakintens.length);
		}*/
        return pepInfo;
    }

    private static void match(int[][] positionList, double[] scoreList, int position, double intensity, int by)
    {
        // System.out.println("843\tglycoFragId\t"+Arrays.toString(glycoFragId));
//		int[][] allArrFragId = Arrangmentor.arrangementArrays(glycoFragId);

        // b ion
        if (by == 0) {
            for (int i = 0; i < positionList.length; i++) {
                boolean match = false;
                for (int k = 0; k < position; k++) {
                    if (positionList[i][k] >= 0) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    scoreList[i] += intensity;
                    break;
                }
            }
        } else if (by == 1) {// y ion

            for (int i = 0; i < positionList.length; i++) {

                boolean match = false;
                for (int k = position; k < positionList[i].length; k++) {
                    if (positionList[i][k] >= 0) {
                        match = true;
                        break;
                    }
                }
                if (match) {
                    scoreList[i] += intensity;
                    break;
                }
            }
        }
    }

    private static void match(int[][] positionList, double[] scoreList, int[] glycoFragId, OGlycanUnit[] units,
            int position, double intensity, int by)
    {
//System.out.println("843\tglycoFragId\t"+Arrays.toString(glycoFragId));
        int[][] allArrFragId = Arrangmentor.arrangementArrays(glycoFragId);

        // b ion
        if (by == 0) {
            for (int i = 0; i < positionList.length; i++) {

                for (int j = 0; j < allArrFragId.length; j++) {

                    int mci = 0;
                    boolean match = false;
                    for (int k = 0; k < position; k++) {

                        if (positionList[i][k] >= 0) {
                            if (units[positionList[i][k]].getFragmentIdSet().contains(allArrFragId[j][mci])) {
                                mci++;
                                if (mci == glycoFragId.length) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        scoreList[i] += intensity;
                        break;
                    }
                }
            }
        } else if (by == 1) {// y ion

            for (int i = 0; i < positionList.length; i++) {

                for (int j = 0; j < allArrFragId.length; j++) {

                    int mci = 0;
                    boolean match = false;
                    for (int k = position; k < positionList[i].length; k++) {
                        if (positionList[i][k] >= 0) {
                            if (units[positionList[i][k]].getFragmentIdSet().contains(allArrFragId[j][mci])) {
                                mci++;
                                if (mci == glycoFragId.length) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (match) {
                        scoreList[i] += intensity;
                        break;
                    }
                }
            }
        }
    }

    public static BufferedImage createImage(OGlycanPepInfo opInfo, IPeak[] peaks)
    {

        double BarWidth = 2d;
        HashMap<Double, String> matchedPeaks = opInfo.getMatchMap();
        String scanname = opInfo.getScanname();
        scanname = scanname.substring(0, scanname.lastIndexOf("."));
        String sequence = opInfo.getModseq();
        String title = "Scan:" + scanname + "          " + sequence;

		/*Double[] matchedMzs = matchedPeaks.keySet().toArray(new Double[matchedPeaks.size()]);
		for(int i=0;i<matchedMzs.length;i++){
			System.out.println(matchedMzs[i]+"\t"+matchedPeaks.get(matchedMzs[i]));
		}*/
        XYSeriesCollection collection = new XYSeriesCollection();
        Annotations ans = new Annotations(true);

        XYSeries s1 = new XYSeries("m1");
        XYSeries s2 = new XYSeries("m2");
        XYSeries s3 = new XYSeries("m3");

        double baseIntensity = 0;
        for (int i = 0; i < peaks.length; i++) {
            double mzi = peaks[i].getMz();
            double intensityi = peaks[i].getIntensity();
            if (intensityi > baseIntensity)
                baseIntensity = intensityi;
        }

        for (int i = 0; i < peaks.length; i++) {

            double mz = Double.parseDouble(df4.format(peaks[i].getMz()));
            double inten = peaks[i].getIntensity() / baseIntensity;

            if (matchedPeaks.containsKey(mz)) {

                String ann = matchedPeaks.get(mz);
                if (ann.startsWith("Pep")) {
                    s3.add(mz, inten);
                    ans.add3(String.valueOf(mz), new String[]{ann},
                            new Color[]{Color.RED}, mz, inten);
                } else {
                    if (ann.contains("(")) {
                        // s3.add(mz, inten);
                        // ans.add3(String.valueOf(mz), new String
                        // []{String.valueOf(mz)}, new Color[]{Color.RED}, mz,
                        // inten);
                        s3.add(mz, inten);
                        ans.add3(String.valueOf(mz), new String[]{ann},
                                new Color[]{Color.RED}, mz, inten);
                    } else {
                        s2.add(mz, inten);
                        // ans.add3(String.valueOf(mz), new String
                        // []{String.valueOf(mz)}, new Color[]{new Color(40, 80,
                        // 220)}, mz,
                        // inten);
                        ans.add3(String.valueOf(mz), new String[]{ann},
                                new Color[]{new Color(40, 80, 220)}, mz, inten);
                    }
                }

            } else {
                s1.add(mz, inten);
            }
        }
        collection.addSeries(s1);
        collection.addSeries(s2);
        collection.addSeries(s3);

        XYBarDataset dataset = new XYBarDataset(collection, BarWidth);

        NumberAxis numberaxis = new NumberAxis("m/z");
        NumberAxis domainaxis = new NumberAxis("Relative Intensity");

        XYBarRenderer renderer = new XYBarRenderer();
        MyXYPointerAnnotation3[] anns3 = ans.getAnnotations3();
        for (int i = 0; i < anns3.length; i++) {
            renderer.addAnnotation(anns3[i]);
        }
        renderer.setSeriesPaint(0, new Color(150, 150, 150));
        renderer.setSeriesFillPaint(0, null);
        renderer.setSeriesPaint(1, new Color(40, 80, 220));
        renderer.setSeriesFillPaint(1, null);
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesFillPaint(2, null);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);

        XYPlot xyplot = new XYPlot(dataset, numberaxis, domainaxis, renderer);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setDomainGridlinePaint(Color.white);
        xyplot.setRangeGridlinePaint(Color.white);
        xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));

        java.awt.Font titleFont = new java.awt.Font("Times",
//				java.awt.Font.BOLD, 16);
                java.awt.Font.BOLD, 20);
        java.awt.Font legentFont = new java.awt.Font("Times",
                java.awt.Font.PLAIN, 60);
        java.awt.Font labelFont = new java.awt.Font("Times",
                java.awt.Font.BOLD, 20);
        java.awt.Font tickFont = new java.awt.Font("Times", java.awt.Font.BOLD,
                20);

        numberaxis.setAutoRange(true);
        numberaxis.setLabelFont(labelFont);
        NumberTickUnit unit = new NumberTickUnit(200);
        numberaxis.setTickUnit(unit);
//		numberaxis.setUpperBound(1200);
        numberaxis.setTickLabelFont(tickFont);

        domainaxis.setAutoRange(false);
        NumberTickUnit unit2 = new NumberTickUnit(0.2);
        domainaxis.setTickUnit(unit2);
        domainaxis.setLabelFont(labelFont);
        domainaxis.setUpperBound(1.1);
        domainaxis.setTickLabelFont(tickFont);

        JFreeChart jfreechart = new JFreeChart(null,
                JFreeChart.DEFAULT_TITLE_FONT, xyplot, false);
        jfreechart.setTitle(new TextTitle(title, titleFont));

        BufferedImage image = jfreechart.createBufferedImage(1200, 800);
        return image;
    }

    private static void test(String database, String[] mgfs, String out,
            String png) throws DtaFileParsingException, IOException, RowsExceededException, WriteException
    {

        OGlycanShortPeptideMatcher matcher = new OGlycanShortPeptideMatcher(database);
        HashMap<String, OGlycanPepInfo> map = new HashMap<String, OGlycanPepInfo>();
        HashMap<String, String> filemap = new HashMap<String, String>();
        for (int i = 0; i < mgfs.length; i++) {
            matcher.match(mgfs[i]);
            ArrayList<OGlycanPepInfo> list = matcher.getInfoList();
            String filename = mgfs[i].substring(mgfs[i].lastIndexOf("\\") + 1, mgfs[i].lastIndexOf("."));
            for (int j = 0; j < list.size(); j++) {
                OGlycanPepInfo info = list.get(j);
                info.setScanname(info.getScanname() + "." + (i + 1));
                String sequence = info.getModseq();
                double score = info.getTotalScore();

                if (info.getMatchMap().size() < 5) continue;
//				if(score<2.8) continue;
                int stcount = 0;
                String useq = info.getUniseq();
                for (int k = 0; k < useq.length(); k++) {
                    if (useq.charAt(k) == 'S' || useq.charAt(k) == 'T') {
                        stcount++;
                    }
                }
                if (stcount > info.getUnits().length) continue;

                if (map.containsKey(sequence)) {
                    if (map.get(sequence).getTotalScore() < score) {
                        map.put(sequence, info);
                        filemap.put(sequence, filename);
                    }
                } else {
                    map.put(sequence, info);
                    filemap.put(sequence, filename);
                }
            }
        }
        System.out.println(map.size());

        HashMap<String, HashSet<String>> sitemap = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> locmap = new HashMap<String, Integer>();
        HashMap<String, String> refmap = new HashMap<String, String>();
        for (int i = 0; i < matcher.items.length; i++) {
            locmap.put(matcher.items[i].sequence, matcher.items[i].begin);
            refmap.put(matcher.items[i].sequence, matcher.items[i].ref);
        }

        int count = 0;
        DecimalFormat df4 = DecimalFormats.DF0_4;
        ExcelWriter writer = new ExcelWriter(out, new String[]{"Sequence", "Site"});
        ExcelFormat format = ExcelFormat.normalFormat;
        writer.addTitle("File\tScan\tSequence\tLocalization Score\tMatch Score\tLength", 0, format);
        writer.addTitle("Protein\tSite\tGlycan\tSequence\t", 1, format);

        for (String key : map.keySet()) {
            OGlycanPepInfo peptide = map.get(key);
            String filename = filemap.get(key);
            String scanname = peptide.getScanname();
            String modseq = peptide.getModseq();
            StringBuilder sb = new StringBuilder();
            sb.append(filename).append("\t");
            sb.append(scanname).append("\t");
            sb.append(modseq).append("\t");
            sb.append(peptide.getScoreseq()).append("\t");
            sb.append(df4.format(peptide.getTotalScore())).append("\t");
            sb.append(peptide.getUniseq().length()).append("\t");
            sb.append("\n");
            writer.addContent(sb.toString(), 0, format);
            scanname = scanname.substring(scanname.indexOf(":") + 1);
//			if(scanname.contains("2877.6")){
            BufferedImage image = OGlycanShortPeptideMatcher.createImage(peptide, peptide.getPeaks());
            ImageIO.write(image, "PNG",
                    new File(png + "\\" + scanname + ".png"));
            count++;
//				if(count==20) break;
//			}

            String sequence = peptide.getUniseq();
            int begin = locmap.get(sequence);
            String ref = refmap.get(sequence);
            char st = 0;
            int loc = begin - 1;
            StringBuilder sb2 = null;
            boolean b = false;
            for (int i = 0; i < modseq.length(); i++) {
                char aa = modseq.charAt(i);
                if (aa == '[') {
                    st = modseq.charAt(i - 1);
                    sb2 = new StringBuilder();
                    b = true;
                } else if (aa == ']') {
                    String key2 = ref + "\t" + st + "" + loc + "\t" + sb2;
                    if (sitemap.containsKey(key2)) {
                        sitemap.get(key2).add(modseq);
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add(modseq);
                        sitemap.put(key2, set);
                    }
                    b = false;
                } else {
                    if (b) {
                        sb2.append(aa);
                    } else {
                        loc++;
                    }
                }
            }


        }
        System.out.println(sitemap.size());
        for (String key : sitemap.keySet()) {
            HashSet<String> set = sitemap.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            for (String key2 : set) {
                sb.append(key2).append("\t");
            }
            writer.addContent(sb.toString(), 1, format);
        }
        writer.close();
    }

    private static void testIdenSite(String database, String idenfile, String[] mgfs, String out,
            String png) throws DtaFileParsingException, IOException,
            JXLException
    {

        HashMap<String, HashSet<String>> idenmap = new HashMap<String, HashSet<String>>();
        HashSet<String> idenset = new HashSet<String>();
        ExcelReader idenreader = new ExcelReader(idenfile, 2);
        String[] idenline = idenreader.readLine();
        while ((idenline = idenreader.readLine()) != null) {
            String key = idenline[6] + "\t" + idenline[0];
            if (idenmap.containsKey(key)) {
                idenmap.get(key).add(idenline[2]);
            } else {
                HashSet<String> set = new HashSet<String>();
                set.add(idenline[2]);
                idenmap.put(key, set);
            }
            idenset.add(idenline[6] + "\t" + idenline[0] + "\t" + idenline[2]);
//			System.out.println(idenline[6]+"\t"+idenline[0]+"\t"+idenline[2]);
        }
        idenreader.close();

        OGlycanShortPeptideMatcher matcher = new OGlycanShortPeptideMatcher(
                database);
        HashMap<String, OGlycanPepInfo> map = new HashMap<String, OGlycanPepInfo>();
        HashMap<String, String> filemap = new HashMap<String, String>();
        for (int i = 0; i < mgfs.length; i++) {
            matcher.match(mgfs[i]);
            ArrayList<OGlycanPepInfo> list = matcher.getInfoList();
            String filename = mgfs[i].substring(mgfs[i].lastIndexOf("\\") + 1,
                    mgfs[i].lastIndexOf("."));
            for (int j = 0; j < list.size(); j++) {
                OGlycanPepInfo info = list.get(j);
                info.setScanname(info.getScanname() + "." + (i + 1));
                String sequence = info.getModseq();
                double score = info.getTotalScore();

                if (info.getMatchMap().size() < 5)
                    continue;
                if (score < 2.8) continue;
                int stcount = 0;
                String useq = info.getUniseq();
                for (int k = 0; k < useq.length(); k++) {
                    if (useq.charAt(k) == 'S' || useq.charAt(k) == 'T') {
                        stcount++;
                    }
                }
//				if (stcount > info.getUnits().length)
                if (stcount > 1)
                    continue;

                if (map.containsKey(sequence)) {
                    if (map.get(sequence).getTotalScore() < score) {
                        map.put(sequence, info);
                        filemap.put(sequence, filename);
                    }
                } else {
                    map.put(sequence, info);
                    filemap.put(sequence, filename);
                }
            }
        }
        System.out.println(map.size());

        HashMap<String, HashSet<String>> sitemap = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> locmap = new HashMap<String, Integer>();
        HashMap<String, String> refmap = new HashMap<String, String>();
        for (int i = 0; i < matcher.items.length; i++) {
            locmap.put(matcher.items[i].sequence, matcher.items[i].begin);
            refmap.put(matcher.items[i].sequence, matcher.items[i].ref);
        }

        int count = 0;
        DecimalFormat df4 = DecimalFormats.DF0_4;
        ExcelWriter writer = new ExcelWriter(out, new String[]{"Sequence",
                "Site"});
        ExcelFormat format = ExcelFormat.normalFormat;
        writer.addTitle(
                "File\tScan\tSequence\tLocalization Score\tMatch Score\tLength",
                0, format);
        writer.addTitle("Protein\tSite\tGlycan\tSequence\t", 1, format);

        for (String key : map.keySet()) {
            OGlycanPepInfo peptide = map.get(key);
            String filename = filemap.get(key);
            String scanname = peptide.getScanname();
            String modseq = peptide.getModseq();

            String sequence = peptide.getUniseq();
            int begin = locmap.get(sequence);
            String ref = refmap.get(sequence);

            StringBuilder sb = new StringBuilder();
            sb.append(filename).append("\t");
            sb.append(scanname).append("\t");
            sb.append(modseq).append("\t");
            sb.append(peptide.getScoreseq()).append("\t");
            sb.append(df4.format(peptide.getTotalScore())).append("\t");
            sb.append(df4.format(peptide.getBaseScore())).append("\t");
            sb.append(ref).append("\t");
            sb.append(peptide.getUniseq().length()).append("\t");
//			sb.append("\n");

            char st = 0;
            int loc = begin - 1;
            StringBuilder sb2 = null;
            boolean b = false;
            boolean iden = false;
            for (int i = 0; i < modseq.length(); i++) {
                char aa = modseq.charAt(i);
                if (aa == '[') {
                    st = modseq.charAt(i - 1);
                    sb2 = new StringBuilder();
                    b = true;
                } else if (aa == ']') {
                    String key2 = ref + "\t" + st + "" + loc + "\t" + sb2;
                    if (idenset.contains(key2)) {
                        iden = true;
                        sb.append(st + "" + loc + "\t" + sb2).append("\t");

                        if (sitemap.containsKey(key2)) {
                            sitemap.get(key2).add(modseq);
                        } else {
                            HashSet<String> set = new HashSet<String>();
                            set.add(modseq);
                            sitemap.put(key2, set);
                        }
                    }

                    b = false;
                } else {
                    if (b) {
                        sb2.append(aa);
                    } else {
                        loc++;
                    }
                }
            }

            if (iden) {
                writer.addContent(sb.toString(), 0, format);
                scanname = scanname.substring(scanname.indexOf(":") + 1);
                // if(scanname.contains("2877.6")){
				/*BufferedImage image = OGlycanShortPeptideMatcher.createImage(
						peptide, peptide.getPeaks());
				ImageIO.write(image, "PNG",
						new File(png + "\\" + scanname + ".png"));
				count++;*/
            }
        }
        System.out.println(sitemap.size());
        for (String key : sitemap.keySet()) {
            HashSet<String> set = sitemap.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            for (String key2 : set) {
                sb.append(key2).append("\t");
            }
            writer.addContent(sb.toString(), 1, format);
        }
        writer.close();
    }

    private static void testAll(String database, String[] mgfs, String out,
            String png) throws DtaFileParsingException, IOException, RowsExceededException, WriteException
    {

        OGlycanShortPeptideMatcher matcher = new OGlycanShortPeptideMatcher(database);
        HashMap<String, OGlycanPepInfo> map = new HashMap<String, OGlycanPepInfo>();
//		HashMap<String, String> filemap = new HashMap<String, String>();
        for (int i = 0; i < mgfs.length; i++) {
            matcher.match(mgfs[i]);
            ArrayList<OGlycanPepInfo> list = matcher.getInfoList();
            String filename = mgfs[i].substring(mgfs[i].lastIndexOf("\\") + 1, mgfs[i].lastIndexOf("."));
            for (int j = 0; j < list.size(); j++) {
                OGlycanPepInfo info = list.get(j);
                String scan = info.getScanname() + "." + (i + 1);
                info.setScanname(scan);
                String sequence = info.getModseq();
                double score = info.getTotalScore();

                if (info.getMatchMap().size() < 5) continue;
                if (!info.getMatchMap().values().contains("Pep")) {
                    if (info.getMatchMap().size() < 10) continue;
                }
//				if(score<2.8) continue;
                int stcount = 0;
                String useq = info.getUniseq();
                for (int k = 0; k < useq.length(); k++) {
                    if (useq.charAt(k) == 'S' || useq.charAt(k) == 'T') {
                        stcount++;
                    }
                }
//				if(stcount==info.getUnits().length) continue;

                map.put(filename + "\t" + scan, info);
				/*if(map.containsKey(sequence)){
					if(map.get(sequence).getTotalScore()<score){
						map.put(sequence, info);
						filemap.put(sequence, filename);
					}
				}else{
					map.put(sequence, info);
					filemap.put(sequence, filename);
				}*/
            }
        }
        System.out.println(map.size());

        HashMap<String, HashSet<String>> sitemap = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> locmap = new HashMap<String, Integer>();
        HashMap<String, String> refmap = new HashMap<String, String>();
        for (int i = 0; i < matcher.items.length; i++) {
            locmap.put(matcher.items[i].sequence, matcher.items[i].begin);
            refmap.put(matcher.items[i].sequence, matcher.items[i].ref);
        }

        int count = 0;
        DecimalFormat df4 = DecimalFormats.DF0_4;
        ExcelWriter writer = new ExcelWriter(out, new String[]{"Sequence", "Site"});
        ExcelFormat format = ExcelFormat.normalFormat;
        writer.addTitle("File\tScan\tSequence\tLocalization Score\tMatch Score\tLength", 0, format);
        writer.addTitle("Protein\tSite\tGlycan\tSequence\t", 1, format);

        for (String key : map.keySet()) {
            OGlycanPepInfo peptide = map.get(key);
            String filename = key.substring(0, key.indexOf("\t"));
            String scanname = peptide.getScanname();
            String modseq = peptide.getModseq();
            String sequence = peptide.getUniseq();
            String ref = refmap.get(sequence);

            StringBuilder sb = new StringBuilder();
            sb.append(filename).append("\t");
            sb.append(scanname).append("\t");
            sb.append(ref).append("\t");
            sb.append(modseq).append("\t");
            sb.append(peptide.getScoreseq()).append("\t");
            sb.append(df4.format(peptide.getTotalScore())).append("\t");
            sb.append(peptide.getUniseq().length()).append("\t");

            scanname = scanname.substring(scanname.indexOf(":") + 1);
//			if(scanname.contains("2877.6")){
            BufferedImage image = OGlycanShortPeptideMatcher.createImage(peptide, peptide.getPeaks());
            ImageIO.write(image, "PNG",
                    new File(png + "\\" + scanname + ".png"));
            count++;
//				if(count==20) break;
//			}

            int begin = locmap.get(sequence);
            char st = 0;
            int loc = begin - 1;
            StringBuilder sb2 = null;
            boolean b = false;
            for (int i = 0; i < modseq.length(); i++) {
                char aa = modseq.charAt(i);
                if (aa == '[') {
                    st = modseq.charAt(i - 1);
                    sb2 = new StringBuilder();
                    b = true;
                } else if (aa == ']') {
                    String key2 = ref + "\t" + st + "" + loc + "\t" + sb2;
                    sb.append(st + "" + loc + "\t" + sb2).append("\t");
                    if (sitemap.containsKey(key2)) {
                        sitemap.get(key2).add(modseq);
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add(modseq);
                        sitemap.put(key2, set);
                    }
                    b = false;
                } else {
                    if (b) {
                        sb2.append(aa);
                    } else {
                        loc++;
                    }
                }
            }

            writer.addContent(sb.toString(), 0, format);
        }
        System.out.println(sitemap.size());
        for (String key : sitemap.keySet()) {
            HashSet<String> set = sitemap.get(key);
            StringBuilder sb = new StringBuilder();
            sb.append(key).append("\t");
            for (String key2 : set) {
                sb.append(key2).append("\t");
            }
            writer.addContent(sb.toString(), 1, format);
        }
        writer.close();
    }

    private static void compareResult(String result, String shortPep3,
            String shortPep4) throws IOException, JXLException
    {

        HashMap<String, String[]> map3 = new HashMap<String, String[]>();
        BufferedReader reader3 = new BufferedReader(new FileReader(shortPep3));
        String line3 = null;
        while ((line3 = reader3.readLine()) != null) {
            String[] cs = line3.split("\t");
            for (int i = 5; i < cs.length; i++) {
                String key = cs[i] + "\t" + cs[3];
                double score = Double.parseDouble(cs[4]);
                if (map3.containsKey(key)) {
                    String[] cs0 = map3.get(key);
                    if (cs.length == 6) {
                        if (cs0.length == 6) {
                            if (score > Double.parseDouble(cs0[4])) {
                                map3.put(key, cs);
                            }
                        } else {
                            map3.put(key, cs);
                        }
                    } else {
                        if (cs0.length == 6) {

                        }
                    }
                } else {
                    map3.put(key, cs);
                }
            }
        }
        reader3.close();

        HashMap<String, String[]> map4 = new HashMap<String, String[]>();
        BufferedReader reader4 = new BufferedReader(new FileReader(shortPep4));
        String line4 = null;
        while ((line4 = reader4.readLine()) != null) {
            String[] cs = line4.split("\t");
            for (int i = 5; i < cs.length; i++) {
                String key = cs[i] + "\t" + cs[3];
                double score = Double.parseDouble(cs[4]);
                if (map4.containsKey(key)) {
                    String[] cs0 = map4.get(key);
                    if (cs.length == 6) {
                        if (cs0.length == 6) {
                            if (score > Double.parseDouble(cs0[4])) {
                                map4.put(key, cs);
                            }
                        } else {
                            map4.put(key, cs);
                        }
                    } else {
                        if (cs0.length == 6) {

                        }
                    }
                } else {
                    map4.put(key, cs);
                }
            }
        }
        reader4.close();
        System.out.println(map3.size() + "\t" + map4.size());

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(OGlycanUnit.core1_1.getComposition(), OGlycanUnit.core1_1.getName());
        map.put(OGlycanUnit.core1_2.getComposition(), OGlycanUnit.core1_2.getName());
        map.put(OGlycanUnit.core1_3.getComposition(), OGlycanUnit.core1_3.getName());
        map.put(OGlycanUnit.core1_4.getComposition(), OGlycanUnit.core1_4.getName());
        map.put(OGlycanUnit.core1_5.getComposition(), OGlycanUnit.core1_5.getName());
        map.put(OGlycanUnit.core2_1.getComposition(), OGlycanUnit.core2_1.getName());
        map.put(OGlycanUnit.core2_2.getComposition(), OGlycanUnit.core2_2.getName());
        map.put(OGlycanUnit.core2_3.getComposition(), OGlycanUnit.core2_3.getName());
        map.put(OGlycanUnit.core2_4.getComposition(), OGlycanUnit.core2_4.getName());
        map.put(OGlycanUnit.core2_5.getComposition(), OGlycanUnit.core2_5.getName());

        int total = 0;
        int count = 0;
        int unique = 0;
        ExcelReader er = new ExcelReader(result, 2);
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {
            total++;
            String key = cs[0].substring(1) + "\t" + map.get(cs[2]);
            if (cs[4].startsWith("sp0003")) {
                if (map3.containsKey(key)) {
                    count++;
                    if (map3.get(key).length == 6)
                        unique++;
                }
            } else if (cs[4].startsWith("sp0004")) {
                if (map4.containsKey(key)) {
                    count++;
                    if (map4.get(key).length == 6)
                        unique++;
                }
            }
        }
        er.close();
        System.out.println(total + "\t" + count + "\t" + unique);
    }

    private static void compareWithMascot(String s1, String m1) throws IOException, JXLException
    {
        HashMap<String, String> map1 = new HashMap<String, String>();
        HashMap<String, String[]> cm1 = new HashMap<String, String[]>();
        ExcelReader reader1 = new ExcelReader(s1);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            map1.put(line1[1], line1[2]);
            cm1.put(line1[1], line1);
        }
        reader1.close();

        HashMap<String, String> map2 = new HashMap<String, String>();
        ExcelReader reader2 = new ExcelReader(m1);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            map2.put(line2[0], line2[5]);
        }
        reader2.close();

        HashSet<String> set = new HashSet<String>();
        set.addAll(map1.keySet());
        set.addAll(map2.keySet());
        System.out.println(map1.size() + "\t" + map2.size() + "\t" + set.size() + "\t" + (map1.size() + map2.size() - set.size()));
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (map1.containsKey(key) && map2.containsKey(key)) {
                String seq1 = map1.get(key);
                String seq2 = map2.get(key);
                System.out.println(key + "\t" + seq1 + "\t" + seq2 + "\t" + (seq1.equals(seq2)) + "\t" + cm1.get(key)[4]);
            }
        }
    }

    private static void compareWithMascot2(String s1, String m1) throws IOException, JXLException, FileDamageException
    {
        HashMap<String, String> map1 = new HashMap<String, String>();
        HashMap<String, String[]> cm1 = new HashMap<String, String[]>();
        ExcelReader reader1 = new ExcelReader(s1);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            map1.put(line1[1], line1[2]);
            cm1.put(line1[1], line1);
        }
        reader1.close();

        double[] masses = new double[]{365.132198, 656.227614635, 947.32303127, 859.306987635, 1021.359812635, 1312.45522927};
        String[] names = new String[]{"Gal-GalNAc", "NeuAc-Gal-GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        HashMap<String, String> map2 = new HashMap<String, String>();
        HashMap<String, Double> scoremap2 = new HashMap<String, Double>();
        File[] files = (new File(m1)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().endsWith("ppl")) {
                continue;
            }

            PeptideListReader r2 = new PeptideListReader(files[i]);
            AminoacidModification aam = r2.getSearchParameter().getVariableInfo();
            ProteinNameAccesser accesser = r2.getProNameAccesser();
            MascotPeptide peptide = null;
            while ((peptide = (MascotPeptide) r2.getPeptide()) != null) {
                if (peptide.getHomoThres() == 0) {
                    if (peptide.getIonscore() < peptide.getIdenThres()) {
                        continue;
                    }
                } else {
                    if (peptide.getIonscore() < peptide.getIdenThres() && peptide.getIonscore() < peptide.getHomoThres()) {
                        continue;
                    }
                }

                String sequence = peptide.getSequence();
                boolean glycan = false;
                String glycanname = null;
                int stcount = 0;
                int loc = 0;
                StringBuilder sb = new StringBuilder();
                for (int i1 = 2; i1 < sequence.length() - 2; i1++) {
                    char aa = sequence.charAt(i1);
                    if (aa >= 'A' && aa <= 'Z') {
                        if (aa == 'S' || aa == 'T') {
                            stcount++;
                        }
                        sb.append(aa);
                        loc = sb.length();
                    } else {
                        double addmass = aam.getAddedMassForModif(aa);
                        for (int k = 0; k < masses.length; k++) {
                            if (Math.abs(addmass - masses[k]) < 0.1) {
                                sb.append("[" + names[k] + "]");
                                glycanname = names[k];
                                glycan = true;
                                break;
                            }
                        }
                    }
                }
                if (glycan == false) continue;
                if (stcount != 1) continue;

                HashMap<String, SeqLocAround> slamap = peptide.getPepLocAroundMap();
                HashSet<ProteinReference> refset = peptide.getProteinReferences();
                Iterator<ProteinReference> refit = refset.iterator();
                SeqLocAround sla = null;
                String reference = null;
                ProteinReference proref = null;
                if (refit.hasNext()) {
                    proref = refit.next();
                    sla = slamap.get(proref.toString());
                    reference = proref.getName();
                }

                loc = loc + sla.getBeg() - 1;
                String proname = accesser.getProInfo(reference).getRef();
                String key = proname + "\t" + loc + "\t" + glycanname;
                System.out.println(key);

                if (map2.containsKey(key)) {
                    if (peptide.getIonscore() > scoremap2.get(key)) {
                        map2.put(key, sb.toString());
                        scoremap2.put(key, (double) peptide.getIonscore());
                    }
                } else {
                    map2.put(key, sb.toString());
                    scoremap2.put(key, (double) peptide.getIonscore());
                }
            }
        }

        System.out.println(scoremap2.size());
        Iterator<String> it = map2.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String sequence = map2.get(key);
            System.out.println(sequence + "\t" + key);
        }
		/*HashSet<String> set = new HashSet<String>();
		set.addAll(map1.keySet());
		set.addAll(map2.keySet());
		System.out.println(map1.size()+"\t"+map2.size()+"\t"+set.size()+"\t"+(map1.size()+map2.size()-set.size()));
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String key = it.next();
			if(map1.containsKey(key) && map2.containsKey(key)){
				String seq1 = map1.get(key);
				String seq2 = map2.get(key);
				System.out.println(key+"\t"+seq1+"\t"+seq2+"\t"+(seq1.equals(seq2))+"\t"+cm1.get(key)[4]);
			}
		}*/
    }

    private static void compareWithDeglyco(String s1, String s2) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> sset1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(s1, 2);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
//			String site = line1[0].substring(1);
            String site = line1[0];
            set1.add(site + "\t" + line1[2] + "\t" + line1[5]);
            sset1.add(site + "\t" + line1[5]);
        }
        reader1.close();

        HashSet<String> set2 = new HashSet<String>();
        HashSet<String> sset2 = new HashSet<String>();
        ExcelReader reader2 = new ExcelReader(s2, 1);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            set2.add(line2[1] + "\t" + line2[2] + "\t" + line2[0]);
            sset2.add(line2[1] + "\t" + line2[0]);
        }
        reader2.close();

        HashSet<String> total1 = new HashSet<String>();
        total1.addAll(set1);
        total1.addAll(set2);
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + total1.size() + "\t" + (set1.size() + set2.size() - total1.size()));

        HashSet<String> total2 = new HashSet<String>();
        total2.addAll(sset1);
        total2.addAll(sset2);
        System.out.println(sset1.size() + "\t" + sset2.size() + "\t" + total2.size() + "\t" + (sset1.size() + sset2.size() - total2.size()));
    }

    private static void compareWithDeglyco(String s1, String s2, String s3) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> sset1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(s1, 2);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String site = line1[0].substring(1);
            set1.add(site + "\t" + line1[2] + "\t" + line1[5]);
            sset1.add(site + "\t" + line1[5]);
        }
        reader1.close();

        HashSet<String> set2 = new HashSet<String>();
        HashSet<String> sset2 = new HashSet<String>();
        ExcelReader reader2 = new ExcelReader(s2, 1);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            set2.add(line2[1] + "\t" + line2[2] + "\t" + line2[0]);
            sset2.add(line2[1] + "\t" + line2[0]);
        }
        reader2.close();

		/*ExcelReader reader3 = new ExcelReader(s3, 2);
		String[] line3 = reader3.readLine();
		while((line3=reader3.readLine())!=null){
			String site = line3[0].substring(1);
//			set2.add(site+"\t"+line3[1]+"\t"+line3[2]);
//			sset2.add(site+"\t"+line3[2]);
		}
		reader3.close();*/

        HashSet<String> total1 = new HashSet<String>();
        total1.addAll(set1);
        total1.addAll(set2);
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + total1.size() + "\t" + (set1.size() + set2.size() - total1.size()));
        for (String site : total1) {
            if (set1.contains(site) && set2.contains(site)) {
                String glycan = site.split("\t")[1];
                if (glycan.contains("GlcNAc-)GalNAc")) {
                    System.out.println(site + "\tCore2");
                } else {
                    System.out.println(site + "\tCore1");
                }
            }
        }

        HashSet<String> total2 = new HashSet<String>();
        total2.addAll(sset1);
        total2.addAll(sset2);
        System.out.println(sset1.size() + "\t" + sset2.size() + "\t" + total2.size() + "\t" + (sset1.size() + sset2.size() - total2.size()));
        for (String site : total2) {
            if (sset1.contains(site) && !sset2.contains(site)) {
                String glycan = site.split("\t")[1];
                if (glycan.contains("GlcNAc-)GalNAc")) {
                    System.out.println(site + "\tCore2");
                } else {
                    System.out.println(site + "\tCore1");
                }
            }
        }
    }

    private static void compareWithDeglycoCopyPng(String s1, String s2, String fromDir,
            String toDir) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> sset1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(s1, 2);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String site = line1[0];
            set1.add(site + "\t" + line1[2] + "\t" + line1[5]);
            sset1.add(site + "\t" + line1[5]);
        }
        reader1.close();

        HashSet<String> set2 = new HashSet<String>();
        HashSet<String> sset2 = new HashSet<String>();
        HashMap<String, String> seqmap = new HashMap<String, String>();
        HashMap<String, String> scanmap = new HashMap<String, String>();
        HashMap<String, String> sitemap = new HashMap<String, String>();
        HashMap<String, HashSet<String>> totalmap = new HashMap<String, HashSet<String>>();
        HashMap<String, Double> scoremap = new HashMap<String, Double>();
        ExcelReader reader2 = new ExcelReader(s2, new int[]{0, 1});
        String[] line2 = reader2.readLine(1);
        while ((line2 = reader2.readLine(1)) != null) {
            set2.add(line2[1] + "\t" + line2[2] + "\t" + line2[0]);
            sset2.add(line2[1] + "\t" + line2[0]);
            for (int i = 3; i < line2.length; i++)
                seqmap.put(line2[3], line2[1] + "\t" + line2[2] + "\t" + line2[0]);
        }
        line2 = reader2.readLine(0);
        while ((line2 = reader2.readLine(0)) != null) {
            double score = Double.parseDouble(line2[4]);
            scanmap.put(line2[2], line2[1]);
            String site = seqmap.get(line2[2]);
            if (sitemap.containsKey(site)) {
                totalmap.get(site).add(line2[2]);
                if (score > scoremap.get(site)) {
                    sitemap.put(site, line2[2]);
                    scoremap.put(site, score);
                }
            } else {
                sitemap.put(site, line2[2]);
                scoremap.put(site, score);
                HashSet<String> set = new HashSet<String>();
                set.add(line2[2]);
                totalmap.put(site, set);
            }
        }
        reader2.close();

        HashSet<String> total1 = new HashSet<String>();
        total1.addAll(set1);
        total1.addAll(set2);
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + total1.size() + "\t" + (set1.size() + set2.size() - total1.size()));
        for (String site : total1) {
            if (set1.contains(site) && set2.contains(site)) {
                String glycan = site.split("\t")[1];
                if (glycan.contains("GlcNAc-)GalNAc")) {
//					System.out.println(site+"\tCore2");
                } else {
//					System.out.println(site+"\tCore1");
                }

                HashSet<String> set = totalmap.get(site);
                for (String sequence : set) {
                    String scan = scanmap.get(sequence);
                    scan = scan.substring(scan.indexOf(":") + 1);
                    File file = new File(fromDir + "\\" + scan + ".png");
                    if (file.exists()) {
                        File target = new File(toDir + "\\" + scan + ".png");
                        FileCopy.Copy(file, target);
                    }
                }
//				String sequence = sitemap.get(site);
            }
        }

        HashSet<String> total2 = new HashSet<String>();
        total2.addAll(sset1);
        total2.addAll(sset2);
        System.out.println(sset1.size() + "\t" + sset2.size() + "\t" + total2.size() + "\t" + (sset1.size() + sset2.size() - total2.size()));
        for (String site : total2) {
            if (sset1.contains(site) && !sset2.contains(site)) {
                String glycan = site.split("\t")[1];
                if (glycan.contains("GlcNAc-)GalNAc")) {
//					System.out.println(site+"\tCore2");
                } else {
//					System.out.println(site+"\tCore1");
                }
            }
        }
    }

    private static void compareWithDeglycoCopyPngAll(String s1, String s2, String out, String fromDir,
            String toDir) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> sset1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(s1, 2);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            set1.add(line1[0] + "\t" + line1[2] + "\t" + line1[5]);
            sset1.add(line1[0] + "\t" + line1[5]);
        }
        reader1.close();

        ExcelWriter writer = new ExcelWriter(out);
        ExcelFormat format = ExcelFormat.normalFormat;

        HashSet<String> set2 = new HashSet<String>();
        HashSet<String> sset2 = new HashSet<String>();
        ExcelReader reader2 = new ExcelReader(s2);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            String key1 = line2[7] + "\t" + line2[8] + "\t" + line2[2];
            String key2 = line2[7] + "\t" + line2[2];
            if (sset1.contains(key2)) {
                sset2.add(key2);
            }
            if (set1.contains(key1)) {
                set2.add(key1);
                String scan = line2[1].substring(line2[1].indexOf(":") + 1);
                writer.addContent(line2, 0, format);
                File file = new File(fromDir + "\\" + scan + ".png");
                if (file.exists()) {
                    File target = new File(toDir + "\\" + scan + ".png");
//					FileCopy.Copy(file, target);
                }
            }
        }
        reader2.close();
        writer.close();
        System.out.println(set2.size() + "\t" + sset2.size());
    }

    private static void getTheoryPeptideWithGlycan(String fasta, String out) throws IOException
    {

        OGlycanUnit[] units = new OGlycanUnit[]{OGlycanUnit.core1_1,
                OGlycanUnit.core1_2, OGlycanUnit.core1_3, OGlycanUnit.core1_4,
                OGlycanUnit.core1_4b, OGlycanUnit.core1_5, OGlycanUnit.core2_1,
                OGlycanUnit.core2_2, OGlycanUnit.core2_3, OGlycanUnit.core2_4,
                OGlycanUnit.core2_5,
//				OGlycanUnit.core2_6,  OGlycanUnit.core2_7,
                OGlycanUnit.core2_8, OGlycanUnit.core2_9,
                OGlycanUnit.core2_10, OGlycanUnit.core2_11};

        double[] totalFragment = OGlycanUnit.getTotalFragmentMasses();
        String[] totalFragName = OGlycanUnit.getTotalFragmentNames();

        DecimalFormat df4 = DecimalFormats.DF0_4;
        PrintWriter writer = new PrintWriter(out);

        Aminoacids aas = new Aminoacids();
        aas.setCysCarboxyamidomethylation();
        AminoacidModification aam = new AminoacidModification();
        MwCalculator mwc = new MwCalculator(aas, aam);

        AminoacidFragment aaf = new AminoacidFragment(aas, aam);
        int[] types = new int[]{Ion.TYPE_B, Ion.TYPE_Y};

        HashMap<String, Double> massmap = new HashMap<String, Double>();
        HashMap<String, ArrayList<Integer>> locmap = new HashMap<String, ArrayList<Integer>>();
        HashMap<String, Integer> countmap = new HashMap<String, Integer>();
        HashMap<String, String> refmap = new HashMap<String, String>();
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            String sequence = ps.getUniqueSequence();
            String ref = ps.getReference();

            for (int i = 0; i < sequence.length(); i++) {

                StringBuilder sb = new StringBuilder();
                int count = 0;
                char aai = sequence.charAt(i);
                sb.append(aai);
                if (aai == 'S' || aai == 'T') {
                    count++;
                }

                for (int j = i + 1; j < sequence.length(); j++) {
                    char aaj = sequence.charAt(j);
                    sb.append(aaj);
                    if (aaj == 'S' || aaj == 'T') {
                        count++;
                    }

                    if (count == 0) {
                        continue;
                    }

                    String seq = sb.toString();
                    double mw = mwc.getMonoIsotopeMZ(seq);
                    if (mw > 800) {
                        break;
                    }

                    if (massmap.containsKey(seq)) {
                        locmap.get(seq).add(i + 1);
                    } else {
                        massmap.put(seq, mw);
                        countmap.put(seq, count);
                        refmap.put(seq, ref);
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        list.add(i + 1);
                        locmap.put(seq, list);
                    }
                }
            }
        }
        fr.close();

        for (String key : massmap.keySet()) {
            double mw = massmap.get(key);
            ArrayList<Integer> list = locmap.get(key);
            if (list.size() > 1) continue;
            Ions ions = aaf.fragment(key, types, true);
            Ion[] bs = ions.bIons();
            Ion[] ys = ions.yIons();

            StringBuilder sb = new StringBuilder();
            StringBuilder bsb = new StringBuilder();
            StringBuilder ysb = new StringBuilder();
            for (int k = 0; k < bs.length; k++) {
                bsb.append(df4.format(bs[k].getMz())).append("_");
                ysb.append(df4.format(ys[k].getMz())).append("_");
            }
            bsb.deleteCharAt(bsb.length() - 1);
            ysb.deleteCharAt(ysb.length() - 1);

            for (int k = 0; k < units.length; k++) {
                sb.append(key).append("\t");
                sb.append(units[k].getName()).append("\t");
                sb.append(countmap.get(key)).append("\t");
                sb.append(df4.format(mw)).append("\t");
                sb.append(df4.format(mw + units[k].getMass())).append("\t");
                sb.append(list.get(0)).append("\t");
                sb.append(refmap.get(key)).append("\t");
                sb.append(bsb).append("\t");
                sb.append(ysb).append("\t");

                int[] fragId = units[k].getFragid();
                StringBuilder gsb = new StringBuilder();
                StringBuilder fsb = new StringBuilder();
                gsb.append(df4.format(mw + AminoAcidProperty.PROTON_W)).append("_");
                fsb.append("Pep").append("_");
                for (int g = 0; g < fragId.length - 1; g++) {
                    gsb.append(df4.format(mw + totalFragment[fragId[g]] + AminoAcidProperty.PROTON_W)).append("_");
                    fsb.append("Pep+" + totalFragName[fragId[g]]).append("_");
                }

                gsb.deleteCharAt(gsb.length() - 1);
                fsb.deleteCharAt(fsb.length() - 1);
                sb.append(gsb).append("\t");
                sb.append(fsb).append("\n");
            }
            writer.write(sb.toString());
        }

        writer.close();
    }

	/*private static void getTheoryPeptide(String fasta, String out) throws IOException{

		DecimalFormat df4 = DecimalFormats.DF0_4;
		PrintWriter writer = new PrintWriter(out);

		Aminoacids aas = new Aminoacids();
		aas.setCysCarboxyamidomethylation();
		AminoacidModification aam = new AminoacidModification();
		MwCalculator mwc = new MwCalculator(aas, aam);

		AminoacidFragment aaf = new AminoacidFragment(aas, aam);
		int [] types = new int[]{Ion.TYPE_B, Ion.TYPE_Y};

		HashMap<String, Double> map = new HashMap<String, Double>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String ref = ps.getReference();
			String sequence = ps.getUniqueSequence();
			StringBuilder sb = new StringBuilder();
			boolean st = false;
			for(int i=0;i<sequence.length();i++){
				char aai = sequence.charAt(i);
				if(aai=='S' || aai=='T')
					st = true;

				sb.append(aai);
				for(int j=i+1;j<sequence.length();j++){
					char aaj = sequence.charAt(j);
					sb.append(aaj);
					if(aaj=='S' || aaj=='T')
						st = true;

					if(!st){
						continue;
					}else{
						String seq = sb.toString();
						double mw = mwc.getMonoIsotopeMZ(seq);
						if(mw>800){
							break;
						}else{
							if(map.containsKey(seq))
								continue;

							map.put(seq, mw);
							Ions ions = aaf.fragment(seq, types, true);
							Ion[] bys = ions.getTotalIons();
							StringBuilder isb = new StringBuilder();
							isb.append(seq).append("\t");
							isb.append(df4.format(mw)).append("\t");
							for(int k=0;k<bys.length;k++){
								isb.append(df4.format(bys[k].getMz())).append("\t");
							}
							writer.println(isb);
						}
					}
				}

				sb = new StringBuilder();
				st = false;
			}
		}
		fr.close();
		writer.close();
	}
*/

    private static void compare(String s1, String s2) throws IOException
    {
        HashSet<String> set1 = new HashSet<String>();
        BufferedReader r1 = new BufferedReader(new FileReader(s1));
        String line1 = null;
        while ((line1 = r1.readLine()) != null) {
            String[] cs = line1.split("\t");
            set1.add(cs[0] + "\t" + cs[1]);
        }
        r1.close();

        HashSet<String> set2 = new HashSet<String>();
        BufferedReader r2 = new BufferedReader(new FileReader(s2));
        String line2 = null;
        while ((line2 = r2.readLine()) != null) {
            String[] cs = line2.split("\t");
            set2.add(cs[0] + "\t" + cs[1]);
        }
        r2.close();

        HashSet<String> total = new HashSet<String>();
        total.addAll(set1);
        total.addAll(set2);
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + total.size());
    }

    private static void compareScan(String s1, String s2) throws IOException, JXLException
    {

        HashSet<String> set = new HashSet<String>();
        ExcelReader r1 = new ExcelReader(s1);
        String[] line1 = r1.readLine();
        while ((line1 = r1.readLine()) != null) {
            set.add(line1[1]);
        }
        r1.close();

        ExcelReader r2 = new ExcelReader(s2);
        String[] line2 = r2.readLine();
        while ((line2 = r2.readLine()) != null) {

        }
        r2.close();
    }

    private static void itemOverlapTest(String file) throws IOException
    {
        OGlycanShortPeptideMatcher matcher = new OGlycanShortPeptideMatcher(file);
        DatabaseItem[] items = new DatabaseItem[matcher.items.length];
        System.arraycopy(matcher.items, 0, items, 0, items.length);
        Arrays.sort(items, new Comparator<DatabaseItem>()
        {
            @Override
            public int compare(DatabaseItem arg0, DatabaseItem arg1)
            {
                // TODO Auto-generated method stub
                if (arg0.glycoPepMass < arg1.glycoPepMass)
                    return -1;
                else if (arg0.glycoPepMass > arg1.glycoPepMass)
                    return 1;
                return 0;
            }
        });

        ArrayList<DatabaseItem[]> totallist = new ArrayList<DatabaseItem[]>();
        ArrayList<DatabaseItem> templist = new ArrayList<DatabaseItem>();
        templist.add(items[0]);
        for (int i = 0; i < items.length; i++) {
            DatabaseItem last = templist.get(templist.size() - 1);
            double diff = items[i].glycoPepMass - last.glycoPepMass;
            if (diff < 0.1) {
                templist.add(items[i]);
            } else {
                if (templist.size() > 1) {
                    DatabaseItem[] list = templist.toArray(new DatabaseItem[templist.size()]);
                    totallist.add(list);
                }
                templist = new ArrayList<DatabaseItem>();
                templist.add(items[i]);
            }
        }
        System.out.println(totallist.size() + "\t" + items.length);
        for (int i = 0; i < totallist.size(); i++) {
            StringBuilder sb = new StringBuilder();
            DatabaseItem[] listi = totallist.get(i);
            for (int j = 0; j < listi.length; j++) {
                sb.append(listi[j].glycoPepMass).append("\t").append(listi[j].sequence).append("\t").append(listi[j].glycan).append("\t");
            }
            System.out.println(sb);
        }
    }

    private static void selectDatabase(String in, String out) throws IOException, JXLException
    {

        HashSet<String> set = getSiteSet();
        System.out.println("set size\t" + set.size());

        PrintWriter writer = new PrintWriter(out);

        String[] s1 = new String[]{"core1_1", "core1_2", "core1_3", "core1_4", "core1_4b", "core1_5",
                "core2_1", "core2_2", "core2_3", "core2_4", "core2_5"};
        String[] s2 = new String[]{"GalNAc", "Gal-GalNAc", "NeuAc-GalNAc", "NeuAc-Gal-GalNAc", "Gal-(NeuAc-)GalNAc", "NeuAc-Gal-(NeuAc-)GalNAc",
                "Gal-(GlcNAc-)GalNAc", "Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(GlcNAc-)GalNAc",
                "NeuAc-Gal-(Gal-GlcNAc-)GalNAc", "NeuAc-Gal-(NeuAc-Gal-GlcNAc-)GalNAc"};

        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("\t");
            String glycan = null;
            for (int i = 0; i < s1.length; i++) {
                if (cs[1].equals(s1[i])) {
                    glycan = s2[i];
                    int begin = Integer.parseInt(cs[5]);
                    for (int j = 0; j < cs[0].length(); j++) {
                        char aa = cs[0].charAt(j);
                        if (aa == 'S' || aa == 'T') {
//							String key = (begin+j)+"\t"+glycan+"\t"+cs[6];
                            String key = cs[6] + "\t" + aa + "" + (begin + j);
                            if (set.contains(key)) {
                                writer.write(line + "\n");
                                break;
                            }
                        }
                    }
                }
            }
        }
        reader.close();
        writer.close();
    }

    private static void select(String in, String pngs) throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        File[] files = (new File(pngs)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("png")) {
                String scan = files[i].getName().substring(0, files[i].getName().length() - 4);
                set.add("Locus:" + scan);
            }
        }
        ExcelReader reader = new ExcelReader(in, new int[]{0, 1});
        ExcelWriter writer = new ExcelWriter(in.replace("xls", "select.xls"));
        ExcelFormat format = ExcelFormat.normalFormat;
        String[] line = reader.readLine(0);
        writer.addTitle(line, 0, format);
        HashSet<String> set2 = new HashSet<String>();
        while ((line = reader.readLine(0)) != null) {
            if (set.contains(line[1])) {
                String sequence = line[2];
                set2.add(sequence);
                int stcount = 0;
                for (int i = 0; i < sequence.length(); i++) {
                    if (sequence.charAt(i) == 'S' || sequence.charAt(i) == 'T') {
                        stcount++;
                    }
                }
                if (stcount == 1) {
                    writer.addContent(line, 0, format);
                }
            }
        }
        line = reader.readLine(1);
        writer.addTitle(line, 1, format);
        while ((line = reader.readLine(1)) != null) {
            String sequence = null;
            for (int i = 3; i < line.length; i++) {
                if (set2.contains(line[i])) {
                    sequence = line[i];
                }
            }
            if (sequence != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    sb.append(line[i]).append("\t");
                }
                sb.append(sequence);
                writer.addContent(sb.toString(), 1, format);
            }
        }
        writer.close();
    }

    private static void select2(String old, String in, String pngs) throws IOException, JXLException
    {

        HashSet<String> oldset = new HashSet<String>();
        ExcelReader oldreader = new ExcelReader(old, 2);
        String[] oldline = oldreader.readLine();
        while ((oldline = oldreader.readLine()) != null) {
            String key = oldline[5] + "\t" + oldline[0] + "\t" + oldline[2];
            oldset.add(key);
        }
        oldreader.close();

        HashSet<String> set = new HashSet<String>();
        File[] files = (new File(pngs)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("png")) {
                String scan = files[i].getName().substring(0, files[i].getName().length() - 4);
                set.add("Locus:" + scan);
            }
        }
        ExcelReader reader = new ExcelReader(in, new int[]{0, 1});
        ExcelWriter writer = new ExcelWriter(in.replace("xls", "select.old.xls"));
        ExcelFormat format = ExcelFormat.normalFormat;
        HashSet<String> set2 = new HashSet<String>();

        String[] line = reader.readLine(1);
        writer.addTitle(line, 1, format);
        while ((line = reader.readLine(1)) != null) {
            String sequence = null;
            for (int i = 3; i < line.length; i++) {
                if (set2.contains(line[i])) {
                    sequence = line[i];
                }
            }
            if (sequence != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    sb.append(line[i]).append("\t");
                }
                sb.append(sequence);
                writer.addContent(sb.toString(), 1, format);
            }
        }

        line = reader.readLine(0);
        writer.addTitle(line, 0, format);
        while ((line = reader.readLine(0)) != null) {
            if (set.contains(line[1])) {
                String sequence = line[2];
                set2.add(sequence);
                int stcount = 0;
                for (int i = 0; i < sequence.length(); i++) {
                    if (sequence.charAt(i) == 'S' || sequence.charAt(i) == 'T') {
                        stcount++;
                    }
                }
                if (stcount == 1) {
                    writer.addContent(line, 0, format);
                }
            }
        }

        writer.close();
    }

    private static void select3(String in, String out, String fromdir, String todir) throws IOException, JXLException
    {
        HashMap<String, String> filemap = new HashMap<String, String>();
        HashMap<String, Double> scoremap = new HashMap<String, Double>();
        HashMap<String, String[]> contentmap = new HashMap<String, String[]>();

//		HashSet<String> siteset = getSiteSet();

        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String file = line[1].substring(line[1].indexOf(":") + 1);
            double score = Double.parseDouble(line[5]);
            if (score < 1.5) continue;
            if (filemap.containsKey(line[3])) {
                if (score > scoremap.get(line[3])) {
                    filemap.put(line[3], file);
                    scoremap.put(line[3], score);
                    contentmap.put(line[3], line);
                }
            } else {
                filemap.put(line[3], file);
                scoremap.put(line[3], score);
                contentmap.put(line[3], line);
            }
        }
        reader.close();

        ExcelWriter writer = new ExcelWriter(out);
        ExcelFormat format = ExcelFormat.normalFormat;
        Iterator<String> it = filemap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            writer.addContent(contentmap.get(key), 0, format);
            File file = new File(fromdir + "\\" + filemap.get(key) + ".png");
            if (file.exists()) {
                FileCopy.Copy(file, new File(todir + "\\" + filemap.get(key) + ".png"));
            }
        }
        writer.close();
    }

    private static void selectReserve(String in, String pngs) throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        File[] files = (new File(pngs)).listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            set.add("Locus:" + name.substring(0, name.length() - 4));
        }
        System.out.println(set.size());
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> set2 = new HashSet<String>();
        HashSet<String> sset1 = new HashSet<String>();
        HashSet<String> sset2 = new HashSet<String>();

		/*set1.add("sp0004|Q58D62|FETUB_BOVIN Fetuin-B OS=Bos taurus GN=FETUB PE=2 SV=1\tT299");
		set1.add("sp0003|P12763|FETUA_BOVIN Alpha-2-HS-glycoprotein OS=Bos taurus GN=AHSG PE=1 SV=2\tS341");
		set1.add("sp0003|P12763|FETUA_BOVIN Alpha-2-HS-glycoprotein OS=Bos taurus GN=AHSG PE=1 SV=2\tT334");
		set1.add("sp0003|P12763|FETUA_BOVIN Alpha-2-HS-glycoprotein OS=Bos taurus GN=AHSG PE=1 SV=2\tS282");
		set1.add("sp0003|P12763|FETUA_BOVIN Alpha-2-HS-glycoprotein OS=Bos taurus GN=AHSG PE=1 SV=2\tT280");
		set1.add("sp0003|P12763|FETUA_BOVIN Alpha-2-HS-glycoprotein OS=Bos taurus GN=AHSG PE=1 SV=2\tS271");
		set1.add("sp0003|P12763|FETUA_BOVIN Alpha-2-HS-glycoprotein OS=Bos taurus GN=AHSG PE=1 SV=2\tS290");
		set1.add("sp0004|Q58D62|FETUB_BOVIN Fetuin-B OS=Bos taurus GN=FETUB PE=2 SV=1\tT292");
		set1.add("sp0004|Q58D62|FETUB_BOVIN Fetuin-B OS=Bos taurus GN=FETUB PE=2 SV=1\tT295");*/

        ExcelReader reader = new ExcelReader(in);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (set.contains(line[1])) {

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length; i++) {
                    sb.append(line[i]).append("\t");
                }
                System.out.println(sb.toString());

                set1.add(line[2] + "\t" + line[7]);
                set2.add(line[2] + "\t" + line[7] + "\t" + line[8]);
                int stcount = 0;
                for (int i = 0; i < line[3].length(); i++) {
                    char aa = line[3].charAt(i);
                    if (aa == 'S' || aa == 'T') {
                        stcount++;
                    }
                }
                if (stcount == 1) {
                    sset1.add(line[2] + "\t" + line[7]);
                    sset2.add(line[2] + "\t" + line[7] + "\t" + line[8]);
                }
            }
        }
        reader.close();
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + sset1.size() + "\t" + sset2.size());
    }

    private static void caocaocao(String in1, String in2) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        ExcelReader reader1 = new ExcelReader(in1, 1);
        String[] line1 = reader1.readLine();
        while ((line1 = reader1.readLine()) != null) {
            String key = line1[0] + "\t" + line1[1] + "\t" + line1[2];
            set1.add(key);
        }
        reader1.close();

        HashSet<String> set2 = new HashSet<String>();
        ExcelReader reader2 = new ExcelReader(in2, 2);
        String[] line2 = reader2.readLine();
        while ((line2 = reader2.readLine()) != null) {
            String key = line2[2] + "\t" + line2[0] + "\t" + line2[1];
            set2.add(key);
        }
        reader2.close();

        HashSet<String> allset = new HashSet<String>();
        allset.addAll(set1);
        allset.addAll(set2);
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + allset.size());

        Iterator<String> it = allset.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (set2.contains(key) && !set1.contains(key)) {
                System.out.println(key);
            }
        }
    }

    private static void getSite(String fasta, String result) throws IOException, JXLException
    {
        String ref003 = null;
        String ref004 = null;
        String pro003 = null;
        String pro004 = null;
        FastaReader fr = new FastaReader(fasta);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            if (ref003 == null) {
                ref003 = ps.getReference();
                pro003 = ps.getUniqueSequence();
            } else {
                ref004 = ps.getReference();
                pro004 = ps.getUniqueSequence();
            }
        }
        fr.close();

        HashSet<String> set = new HashSet<String>();
        ExcelReader reader = new ExcelReader(result, 2);
        String[] line = null;
        while ((line = reader.readLine()) != null) {
            String modseq = line[0];
            StringBuilder seqsb = new StringBuilder();
            StringBuilder glysb = new StringBuilder();
            boolean begin = true;
            int loc = 0;
            for (int i = 0; i < modseq.length(); i++) {
                char ci = modseq.charAt(i);
                if (ci == '[') {
                    loc = i;
                    begin = false;
                } else if (ci == ']') {
                    begin = true;
                } else {
                    if (begin) {
                        seqsb.append(ci);
                    } else {
                        glysb.append(ci);
                    }
                }
            }
            String seq = seqsb.toString();
            String glyco = glysb.toString();
            int id = pro003.indexOf(seq);
            if (id < 0) {
                id = pro004.indexOf(seq);
                String site = ref004 + "\t" + (loc + id) + "\t" + glyco;
                set.add(site);
            } else {
                String site = ref003 + "\t" + (loc + id) + "\t" + glyco;
                set.add(site);
            }
        }
        for (String s : set) {
            System.out.println(s);
        }
        System.out.println(set.size());
    }

    private static void caocaocaocaocao(String in) throws IOException, JXLException
    {
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> set2 = new HashSet<String>();
        ExcelReader reader = new ExcelReader(in, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            set1.add(line[1]);
            set2.add(line[0] + "\t" + line[1]);
        }
        reader.close();
        System.out.println(set1.size() + "\t" + set2.size());
    }

    private static HashSet<String> getSiteSet() throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        ExcelReader reader = new ExcelReader("H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls", 2);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String site = line[5] + "\t" + line[0];
            set.add(site);
        }
        reader.close();
        return set;
    }

    private static HashSet<String> getSiteGlycanSet() throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        ExcelReader reader = new ExcelReader("H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls", 2);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String site = line[5] + "\t" + line[0] + "\t" + line[2];
            set.add(site);
        }
        reader.close();
        return set;
    }

    private static void testOldResult(String result) throws IOException, JXLException
    {
        HashSet<String> rset = new HashSet<String>();
        HashSet<String> siteset = getSiteSet();
        ExcelReader reader = new ExcelReader(result, 1);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            char aa = line[3].charAt(line[3].indexOf("[") - 1);
            String site = line[0] + "\t" + aa + line[1];
            if (siteset.contains(site)) {
                rset.add(site);
            }
        }
        reader.close();
        System.out.println(rset.size());
    }

    private static void compareOld(String oldresult, String newresult) throws IOException, JXLException
    {

        HashSet<String> siteset = getSiteSet();

        HashSet<String> newsiteset = new HashSet<String>();
        ExcelReader newreader = new ExcelReader(newresult, 1);
        String[] newline = null;
        while ((newline = newreader.readLine()) != null) {
            int stcount = 0;
            for (int i = 7; i < newline.length; i += 2) {
                String site = newline[2] + "\t" + newline[i];
                if (siteset.contains(site)) {
                    for (int j = 0; j < newline[3].length(); j++) {
                        char aa = newline[3].charAt(j);
                        if (aa == 'S' || aa == 'T') {
                            stcount++;
                        }
                    }
                    if (stcount == 1) {
                        newsiteset.add(site);
                    }
                }
            }
        }
        newreader.close();

        HashSet<String> oldsiteset = new HashSet<String>();
        ExcelReader oldreader = new ExcelReader(oldresult, 1);
        String[] oldline = oldreader.readLine();
        while ((oldline = oldreader.readLine()) != null) {
            char aa = oldline[3].charAt(oldline[3].indexOf("[") - 1);
            String site = oldline[0] + "\t" + aa + oldline[1];
            if (siteset.contains(site)) {
                oldsiteset.add(site);
            }
        }
        oldreader.close();

        HashSet<String> total = new HashSet<String>();
        total.addAll(newsiteset);
        total.addAll(oldsiteset);
        System.out.println(oldsiteset.size() + "\t" + newsiteset.size() + "\t" + total.size());

        Iterator<String> it = total.iterator();
        while (it.hasNext()) {
            String site = it.next();
            if (oldsiteset.contains(site) && !newsiteset.contains(site)) {
                System.out.println(site);
            }
        }
    }

    private static void pngCompare(String png1, String png2) throws IOException
    {
        File[] files1 = (new File(png1)).listFiles();
        File[] files2 = (new File(png2)).listFiles();
        HashSet<String> set1 = new HashSet<String>();
        HashSet<String> set2 = new HashSet<String>();
        for (int i = 0; i < files1.length; i++) {
            set1.add(files1[i].getName());
        }
        for (int i = 0; i < files2.length; i++) {
            set2.add(files2[i].getName());
        }
        HashSet<String> total = new HashSet<String>();
        total.addAll(set1);
        total.addAll(set2);
        System.out.println(set1.size() + "\t" + set2.size() + "\t" + (set1.size() + set2.size() - total.size()));

        Iterator<String> it = total.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (set1.contains(key) && !set2.contains(key)) {
                System.out.println(key);
                File source = new File(png1 + "\\" + key);
                File target = new File("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\temp2\\" + key);
                FileCopy.CopyAndDelete(source, target);
            }
        }
    }

    private static void compareAndWrite(String in, String out) throws IOException, JXLException
    {

        ExcelWriter writer = new ExcelWriter(out, 2);
        ExcelFormat format = ExcelFormat.normalFormat;
        writer.addTitle("Scan\tProtein\tSequence\tScore\tSite\tGlycan", 0, format);
        writer.addTitle("Protein\tSite\tGlycan\tUnambiguous?\tSequence", 1, format);

        HashSet<String> siteset = getSiteSet();
        HashSet<String> siteGlycanset = getSiteGlycanSet();

        HashSet<String> newsiteset0 = new HashSet<String>();
        HashSet<String> newglycanset0 = new HashSet<String>();

        HashSet<String> newsiteset1 = new HashSet<String>();
        HashSet<String> newglycanset1 = new HashSet<String>();

        HashMap<String, String> contentmap = new HashMap<String, String>();
        HashMap<String, StringBuilder> sbmap = new HashMap<String, StringBuilder>();
        HashMap<String, Boolean> ambmap = new HashMap<String, Boolean>();

        ExcelReader newreader = new ExcelReader(in, 1);
        String[] newline = null;
        while ((newline = newreader.readLine()) != null) {

            if (newline[1].contains("2266")) continue;

            for (int i = 7; i < newline.length; i += 2) {

                String site = newline[2] + "\t" + newline[i];
                String glycansite = newline[2] + "\t" + newline[i] + "\t" + newline[i + 1];

                if (siteset.contains(site)) {

                    newsiteset0.add(site);

                    if (siteGlycanset.contains(glycansite)) {
                        newglycanset0.add(glycansite);
                    }

                    if (!ambmap.containsKey(glycansite)) {
                        ambmap.put(glycansite, false);
                    }

                    if (sbmap.containsKey(glycansite)) {
                        StringBuilder sb = sbmap.get(glycansite);
                        sb.append(newline[3]);
                        sb.append("\t");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(newline[3]);
                        sb.append("\t");
                        sbmap.put(glycansite, sb);
                    }

                    StringBuilder csb = new StringBuilder();
                    for (int j = 1; j <= 4; j++)
                        csb.append(newline[j]).append("\t");
                    for (int j = 7; j < newline.length; j++)
                        csb.append(newline[j]).append("\t");

                    contentmap.put(newline[1], csb.toString());

                    int stcount = 0;
                    for (int j = 0; j < newline[3].length(); j++) {
                        char aa = newline[3].charAt(j);
                        if (aa == 'S' || aa == 'T') {
                            stcount++;
                        }
                    }
                    if (stcount == 1) {
                        newsiteset1.add(site);
                        newglycanset1.add(glycansite);
                        ambmap.put(glycansite, true);
//						if(siteGlycanset.contains(glycansite)){
//							System.out.println(glycansite);
//						}
                    }
                }
            }
        }
        newreader.close();

        System.out.println(newsiteset0.size() + "\t" + newsiteset1.size() + "\t" + newglycanset0.size() + "\t" + newglycanset1.size());

        Iterator<String> it0 = contentmap.keySet().iterator();
        while (it0.hasNext()) {
            String key = it0.next();
            writer.addContent(contentmap.get(key), 0, format);
        }

        Iterator<String> it = sbmap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            StringBuilder sb = new StringBuilder(key + "\t");
            if (ambmap.get(key)) {
                sb.append("Yes\t");
            } else {
                sb.append("No\t");
            }
            sb.append(sbmap.get(key));
            writer.addContent(sb.toString(), 1, format);
        }
        writer.close();

        Iterator<String> it2 = siteset.iterator();
        while (it2.hasNext()) {
            String site = it2.next();
            if (!newsiteset1.contains(site)) {
                System.out.println(site);
            }
        }
    }

    private static void copyNew(String source, String target, String result) throws IOException, JXLException
    {
        HashSet<String> set = new HashSet<String>();
        ExcelReader reader = new ExcelReader(result);
        String[] line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            set.add(line[0].substring(6));
        }
        reader.close();

        File[] files = (new File(source)).listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName().substring(0, files[i].getName().length() - 4);
            if (set.contains(name)) {
                File targeti = new File(target + "\\" + name + ".png");
                FileCopy.Copy(files[i], targeti);
            }
        }
    }

    private static void readResult(String result) throws IOException, JXLException
    {
        ExcelReader reader = new ExcelReader(result, 1);

    }

    /**
     * @param args
     * @throws IOException
     * @throws DtaFileParsingException
     * @throws JXLException
     * @throws FileDamageException
     */
    public static void main(
            String[] args) throws IOException, DtaFileParsingException, JXLException, FileDamageException
    {
        // TODO Auto-generated method stub
//		OGlycanShortPeptideMatcher.copyNew("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\top",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\20140528",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\out2.xls");
//		OGlycanShortPeptideMatcher.compareAndWrite("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\select.top.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\out2.xls");
//		OGlycanShortPeptideMatcher.pngCompare("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\top",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat all\\top");
//		OGlycanShortPeptideMatcher.testOldResult("H:\\OGLYCAN\\OGlycan_20140324_proteinaseK\\fetuin simple match 11 only.xls");
//		OGlycanShortPeptideMatcher.compareOld("H:\\OGLYCAN\\OGlycan_20140324_proteinaseK\\fetuin simple match 11 only.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\select.top.xls");
//		OGlycanShortPeptideMatcher.caocaocaocaocao("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\allmatch.xls");
//		OGlycanShortPeptideMatcher.knownMatch("Locus:1.1.1.2754.11.3", "GPPVASVV", "core2_4",
//				"H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\20140321_O-fetuin_proteinaseK_500cps_CES5_3.mgf",
//				"H:\\OGLYCAN\\OGlycan_20140515_proteinaseK\\");
//		OGlycanShortPeptideMatcher.caocaocao("H:\\OGLYCAN\\OGlycan_20140515_proteinaseK\\fetuin_proteinaseK_all.select.xls",
//				"H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\mascot\\result.combine.only.xls");

//		OGlycanShortPeptideMatcher.compareWithMascot2("H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls",
//				"H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\mascot");

//		OGlycanShortPeptideMatcher.getTheoryPeptideWithGlycan("H:\\OGLYCAN\\OGlycan_20140410_IGA\\IGHA1.fasta",
//				"H:\\OGLYCAN\\OGlycan_20140410_IGA\\IGHA1 theory ions with glycan 1.txt");

//		OGlycanShortPeptideMatcher.getTheoryPeptideWithGlycan("H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\fetuin.fasta",
//				"H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\fetuin theory ions.txt");

//		OGlycanShortPeptideMatcher.itemOverlapTest("H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\fetuin theory ions.txt");
//		String database = "H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\select.sites.fetuin theory ions.txt";
        String database = "H:\\OGLYCAN2\\20141104_proteinaseK\\fetuin theory ions.txt";
//		String mgf = "H:\\OGLYCAN\\OGlycan_20140324\\fetuin.20140324.2.0\\fetuin.20140324.2.0.oglycan.mgf";
//		OGlycanShortPeptideMatcher matcher = new OGlycanShortPeptideMatcher(database);
//		matcher.match(mgf);
//		matcher.write("H:\\OGLYCAN\\OGlycan_20140324\\fetuin match.txt", "H:\\OGLYCAN\\OGlycan_20140324\\result.xls");

        String mgf1 = "H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\20140321_O-fetuin_proteinaseK_500cps_CES5_1.mgf";
        String mgf2 = "H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\20140321_O-fetuin_proteinaseK_500cps_CES5_2.mgf";
        String mgf3 = "H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\20140321_O-fetuin_proteinaseK_500cps_CES5_3.mgf";
        String iden = "H:\\OGLYCAN2\\20141024_15glyco\\20141031_fetuin\\20141031_fetuin.xls";
        String out = "H:\\OGLYCAN2\\20141104_proteinaseK\\select.multi.match.xls";
        String png = "H:\\OGLYCAN2\\20141104_proteinaseK\\multi png";
        OGlycanShortPeptideMatcher.test(database, new String[]{mgf1, mgf2, mgf3}, out, png);
//		OGlycanShortPeptideMatcher.testIdenSite(database, iden, new String[]{mgf1, mgf2, mgf3},  out, png);
//		OGlycanShortPeptideMatcher.testAll(database, new String[]{mgf1, mgf2, mgf3},  out, png);

        String mgf = "H:\\OGLYCAN\\OGlycan_20140324\\original data\\fetuin\\test.mgf";
//		OGlycanShortPeptideMatcher matcher = new OGlycanShortPeptideMatcher("H:\\OGLYCAN2\\20141104_proteinaseK\\fetuin theory ions.txt");
//		matcher.match(mgf1);

//		OGlycanShortPeptideMatcher.compareResult("H:\\OGLYCAN\\OGlycan_final_20140312\\20140324\\fetuin2.0\\fetuin2.0.xls",
//				"H:\\OGLYCAN\\OGlycan_20140324\\sp0003 match.txt",
//				"H:\\OGLYCAN\\OGlycan_20140324\\sp0004 match.txt");

//		OGlycanShortPeptideMatcher.compare("H:\\OGLYCAN\\OGlycan_20140324\\sp0003 match.txt", "H:\\OGLYCAN\\OGlycan_20140324\\sp0003 match2.txt");
//		OGlycanShortPeptideMatcher.compareWithMascot("H:\\OGLYCAN\\OGlycan_20140324\\fetuin simple match.xls",
//				"H:\\OGLYCAN\\OGlycan_20140324\\fetuin.new.20140408.combine\\fetuin.new.20140408.combine.oglycan.F002417.site.xls");
//		OGlycanShortPeptideMatcher.compareWithDeglyco("H:\\OGLYCAN\\OGlycan_20140405\\fetuin_20140405\\fetuin_20140405.xls",
//		OGlycanShortPeptideMatcher.compareWithDeglyco("H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat all\\select.match.xls");
//		OGlycanShortPeptideMatcher.compareWithDeglyco("H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls",
//				"H:\\OGLYCAN\\OGlycan_20140508_proteinaseK\\fetuin_proteinaseK_only.xls",
//				"H:\\OGLYCAN\\OGlycan_20140324\\fetuin.new.20140408.combine\\fetuin.new.20140408.combine.oglycan.F002417.site.only.xls");
//		OGlycanShortPeptideMatcher.compareWithDeglycoCopyPngAll("H:\\OGLYCAN\\OGlycan_20140423_core14b\\fetuin\\fetuin.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\all\\allmatch.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\all\\select.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\all\\png",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\all\\repeat");
//		OGlycanShortPeptideMatcher.select("H:\\OGLYCAN\\OGlycan_20140515_proteinaseK\\fetuin_proteinaseK_all.xls",
//				"H:\\OGLYCAN\\OGlycan_20140515_proteinaseK\\repeat2");
//		OGlycanShortPeptideMatcher.selectDatabase("H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\fetuin theory ions.txt",
//				"H:\\OGLYCAN\\OGlycan_20140321_proteinaseK_original\\select.sites.fetuin theory ions.txt");
//		OGlycanShortPeptideMatcher.select3("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\select.match.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\select.top.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\png",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\top");
//		OGlycanShortPeptideMatcher.selectReserve("H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\select.top.xls",
//				"H:\\OGLYCAN\\OGlycan_20140522_proteinaseK\\database repeat sites\\top");
    }

    private void parseDatabase(String database) throws IOException
    {

        ArrayList<DatabaseItem> list = new ArrayList<DatabaseItem>();
        BufferedReader reader = new BufferedReader(new FileReader(database));
        String line = null;
        while ((line = reader.readLine()) != null) {
            DatabaseItem item = new DatabaseItem(line);
//			if(item.sequence.contains("PP") && item.sequence.contains("A")){
//				System.out.println(item.sequence+"\t"+item.pepMass);
//			}
//			if(item.stcount>1) continue;
//			if(item.stcount==1) continue;
            list.add(item);
        }
        reader.close();
        System.out.println("OGlycanShortPeptideMatcher107\titem count\t" + list.size());
        this.items = list.toArray(new DatabaseItem[list.size()]);
        Arrays.sort(items);
        this.itemMasses = new double[items.length];
        for (int i = 0; i < items.length; i++) {
            this.itemMasses[i] = this.items[i].glycoPepMass;
        }
    }

    public void match(String mgf) throws DtaFileParsingException, IOException
    {

        this.infoList = new ArrayList<OGlycanPepInfo>();

        int scanCount = 0;
        MgfReader reader = new MgfReader(mgf);
        MS2Scan scan = null;
        while ((scan = reader.getNextMS2Scan()) != null) {
            String scanname = scan.getScanName().getBaseName();
//if(!scanname.contains("2877.6")) continue;

            int charge = scan.getCharge();
            double mz = scan.getPrecursorMZ();
            double pepmr = (mz - AminoAcidProperty.PROTON_W) * charge;

            OGlycanPepInfo info = this.match(scanname, pepmr, scan.getPeakList().getPeaksSortByIntensity());

            scanCount++;
            if (info != null) {
                if (info.getTotalScore() > 0) {
                    info.setPeaks(scan.getPeakList().getPeakArray());
                    infoList.add(info);
                }
            }
        }
        reader.close();
        System.out.println(scanCount + "\t" + infoList.size());
    }

    public ArrayList<OGlycanPepInfo> getInfoList()
    {
        return this.infoList;
    }

    private OGlycanPepInfo match(String scanname, double mass, IPeak[] peaks)
    {

        double percent = 0;
        double intenthres1 = 0;
        double intenthres2 = 0;
        int thres = 0;
        DecimalFormat df4 = DecimalFormats.DF0_4;

        if (peaks.length > 100) {

            if (peaks.length >= 400) {
                percent = 1.0;
            } else if (peaks.length < 400 && peaks.length >= 300) {
                percent = 0.75;
            } else if (peaks.length < 300 && peaks.length >= 200) {
                percent = 0.5;
            } else if (peaks.length < 200 && peaks.length >= 100) {
                percent = 0.25;
            }

            ArrayList<Double> tempintenlist = new ArrayList<Double>();
            double[] rsdlist = new double[peaks.length];

            for (int i = peaks.length - 1; i >= 0; i--) {
                tempintenlist.add(peaks[i].getIntensity());
                rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
            }

            for (int i = 0; i < rsdlist.length; i++) {
                if (rsdlist[i] > 0.5) {
                    intenthres1 = peaks[peaks.length - i].getIntensity();
                }
                if (rsdlist[i] > percent) {
                    intenthres2 = peaks[peaks.length - i].getIntensity() / 5.0;
                }
                if (intenthres1 > 0 && intenthres2 > 0) {
                    thres = i;
                    break;
                }
            }

            if (intenthres2 > intenthres1) {
                intenthres2 = intenthres1;
            }
        }

        IPeak[] peaks2 = new IPeak[peaks.length - thres];
        System.arraycopy(peaks, 0, peaks2, 0, peaks2.length);

        int[] markpeaks = new int[7];
        int[] neuAc = new int[2];
        ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();
        boolean oglycan = false;
        double tolerance1 = mass * 2E-5 * 3;
        double tolerance2 = 0.1;
        HashMap<Double, String> matchMap1 = new HashMap<Double, String>();

        for (int i = 0; i < peaks2.length; i++) {

            double mzi = Double.parseDouble(df4.format(peaks2[i].getMz()));
            double inteni = peaks2[i].getIntensity();

            if (inteni < intenthres1) {
                if (inteni > intenthres2)
                    highIntenList.add(peaks[i]);
            } else {
                if (Math.abs(mzi - 163.060101) < tolerance2) {
                    matchMap1.put(mzi, "Hex");
                    continue;
                } else if (Math.abs(mzi - 204.086649) < tolerance2) {
                    oglycan = true;
                    matchMap1.put(mzi, "HexNAc");
                    continue;
                } else if (Math.abs(mzi - 274.087412635) < tolerance2) {
                    neuAc[0] = 1;
                    matchMap1.put(mzi, "NeuAc-H2O");
                    continue;
                } else if (Math.abs(mzi - 292.102692635) < tolerance2) {
                    neuAc[1] = 1;
                    matchMap1.put(mzi, "NeuAc");
                    continue;
                } else if (Math.abs(mzi - 366.139472) < tolerance2) {
                    markpeaks[1] = 1;
                    matchMap1.put(mzi, "Hex+HexNAc");
                    continue;
                    // HexNAc+NeuAc
                } else if (Math.abs(mzi - 495.18269) < tolerance2) {
                    markpeaks[2] = 1;
                    matchMap1.put(mzi, "HexNAc+NeuAc");
                    continue;
                    // NeuAc*2
                } else if (Math.abs(mzi - 583.19873) < tolerance2) {
                    markpeaks[3] = 1;

                    continue;
                    // HexNAc*2
                } else if (Math.abs(mzi - 407.16665) < tolerance2) {
                    markpeaks[4] = 1;
                    matchMap1.put(mzi, "HexNAc*2");
                    continue;
                    // HexNAc*2+Hex
                } else if (Math.abs(mzi - 569.218847) < tolerance2) {
                    markpeaks[5] = 1;
                    matchMap1.put(mzi, "HexNAc*2+Hex");
                    continue;
                    // HexNAc*2+Hex*2
                } else if (Math.abs(mzi - 731.271672) < tolerance2) {
                    markpeaks[6] = 1;
                    matchMap1.put(mzi, "HexNAc*2+Hex*2");
                    continue;
                    // HexNAc+Hex*2, N-glyco
                } else if (Math.abs(mzi - 528.192299) < tolerance2) {
                    // if(inteni>intenthres2)
                    // return;
                    // HexNAc+Hex*3, N-glyco
                } else if (Math.abs(mzi - 690.245124) < tolerance2) {
                    // if(inteni>intenthres2)
                    // return;
                } else {
                    highIntenList.add(peaks[i]);
                    continue;
                }
            }
        }

        if (neuAc[0] + neuAc[1] == 2) {
            markpeaks[0] = 1;
        }
        if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
            return null;
        }

        if (highIntenList.size() <= 5)
            return null;

        IPeak[] noglyPeaks = highIntenList.toArray(new IPeak[highIntenList.size()]);
        RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);
        IPeak[] peaks3 = filter.filter(noglyPeaks);
        Arrays.sort(peaks3, new Comparator<IPeak>()
        {
            @Override
            public int compare(IPeak o1, IPeak o2)
            {
                // TODO Auto-generated method stub
                if (o1.getIntensity() < o2.getIntensity()) {
                    return 1;
                } else if (o1.getIntensity() > o2.getIntensity()) {
                    return -1;
                }
                return 0;
            }
        });

        int id = Arrays.binarySearch(this.itemMasses, mass - 0.1);
        if (id < 0)
            id = -id - 1;

        ArrayList<OGlycanPepInfo> infolist = new ArrayList<OGlycanPepInfo>();

        for (int i = id; i < this.items.length; i++) {

//			if(this.items[i].stcount>1) continue;

            if (Math.abs(mass - this.itemMasses[i]) < tolerance1) {

                String sequence = this.items[i].sequence;
                OGlycanUnit unit = OGlycanUnit.valueOf(this.items[i].glycan);
                HashMap<Double, String> matchmap2 = new HashMap<Double, String>();
                matchmap2.putAll(matchMap1);

                int stcount = this.items[i].stcount;
                OGlycanPepInfo tempinfo = validate(unit, sequence, peaks3,
                        this.items[i].bs, this.items[i].ys, this.items[i].gs, this.items[i].gNames, matchmap2);

                if (tempinfo == null)
                    continue;

                if (!this.validate(markpeaks, unit))
                    continue;

                tempinfo.setPepmr(this.items[i].pepMass);
                tempinfo.setScanname(scanname);
                infolist.add(tempinfo);

                if (stcount > 1) {
                    OGlycanUnit[][] units = unit.getSplitUnits();
                    if (units != null) {
                        for (int j = 0; j < units.length; j++) {
                            OGlycanPepInfo tempinfo2 = this.validate(units[j], sequence, peaks3,
                                    this.items[i].bs, this.items[i].ys, this.items[i].gs, this.items[i].gNames, stcount, matchmap2);

                            if (tempinfo2 != null) {
                                tempinfo2.setPepmr(this.items[i].pepMass);
                                tempinfo2.setScanname(scanname);
//								if(tempinfo2.getTotalScore()>tempinfo.getTotalScore())
                                infolist.add(tempinfo2);
                            }
                        }
                    }
                }

				/*System.out.println("matcher253\t"+tolerance1+"\t"+
						+ Math.abs(mass - this.itemMasses[i]) + "\t" + sequence
						+ "\t" + this.items[i].glycan + "\t"
						+ tempinfo.getTotalScore() + "\t"
						+ this.items[i].pepMass + "\t"
						+ this.items[i].glycoPepMass+"\t"+pass+"\t"+Arrays.toString(markpeaks));*/

				/*System.out.println(tempinfo.getModseq()+"\t"+tempinfo.getTotalScore()+"\t"+tempinfo.getGlycoScore());
				System.out.println("~~~~~~~~~");
				BufferedImage image = OGlycanShortPeptideMatcher.createImage(tempinfo, peaks);
				try {
					ImageIO.write(
							image,
							"PNG",
							new File(
									"H:\\OGLYCAN\\OGlycan_20140324\\original data\\fetuin\\"+i+".png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
            } else if (this.itemMasses[i] - mass > tolerance1) {
                break;
            }
        }

        OGlycanPepInfo[] infos = infolist.toArray(new OGlycanPepInfo[infolist.size()]);
        if (infos.length == 0) return null;

        Arrays.sort(infos, new Comparator<OGlycanPepInfo>()
        {

            @Override
            public int compare(OGlycanPepInfo info1, OGlycanPepInfo info2)
            {
                // TODO Auto-generated method stub
                if (info1 == null) {
                    if (info2 == null) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (info2 == null) {
                        return -1;
                    } else {

                        boolean pepion1 = info1.getMatchMap().containsValue("Pep");
                        boolean pepion2 = info2.getMatchMap().containsValue("Pep");

                        if (pepion1) {
                            if (!pepion2) {
                                return -1;
                            }
                        } else {
                            if (pepion2) {
                                return 1;
                            }
                        }

                        if (info1.getTotalScore() > info2.getTotalScore()) {
                            return -1;

                        } else if (info1.getTotalScore() < info2.getTotalScore()) {
                            return 1;

                        } else {
                            int n1 = 0;
                            OGlycanUnit[] units1 = info1.getUnits();
                            for (int i = 0; i < units1.length; i++) {
                                if (units1[i] == OGlycanUnit.core1_4) {
                                    n1++;
                                }
                            }
                            int n2 = 0;
                            OGlycanUnit[] units2 = info2.getUnits();
                            for (int i = 0; i < units2.length; i++) {
                                if (units2[i] == OGlycanUnit.core1_4) {
                                    n2++;
                                }
                            }
                            if (n1 > n2) {
                                return -1;
                            } else if (n1 < n2) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            }
        });

        if (infos.length == 1) {
            infos[0].setFormDeltaScore(1.0);
            return infos[0];

        } else {
            for (int i = 0; i < infos.length - 1; i++) {
                if (infos[i].getGlycoScore() > 0) {
                    infos[i].setFormDeltaScore((infos[i].getGlycoScore() - infos[i + 1].getGlycoScore()) / infos[i].getGlycoScore());
                } else {
                    infos[i].setFormDeltaScore(0.0);
                }
            }
            infos[infos.length - 1].setFormDeltaScore(0.0);
            return infos[0];
        }
    }

    private boolean validate(int[] markpeaks, OGlycanUnit unit)
    {
        int[] composition = unit.getCompCount();
        boolean core2 = unit.getID() >= 8;

        if (markpeaks[0] > 0) {
            if (composition[2] == 0)
                return false;
        }
        if (markpeaks[4] + markpeaks[5] + markpeaks[6] > 0) {
            if (!core2) return false;
        }
        return true;
    }

    private OGlycanPepInfo validate(OGlycanUnit[] units, String uniPepSeq, IPeak[] peaks, double[] bs, double[] ys,
            double[] gs,
            String[] gNames, int stcount, HashMap<Double, String> matchmap2)
    {

        double tolerance = 0.1;
        double H2O = 18.010565;
        DecimalFormat df4 = DecimalFormats.DF0_4;
        DecimalFormat df2 = DecimalFormats.DF0_2;

        int glycoCount = units.length;
        int[] bionSTCount = new int[uniPepSeq.length()];
        int[] yionSTCount = new int[uniPepSeq.length()];
        for (int i = 0; i < uniPepSeq.length(); i++) {
            if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
                for (int j = i; j < uniPepSeq.length(); j++)
                    bionSTCount[j]++;
            }
        }
        for (int i = 0; i < bionSTCount.length - 1; i++) {
            yionSTCount[i] = bionSTCount[bionSTCount.length - 1] - bionSTCount[bionSTCount.length - i - 2];
        }

        int[] initialList = new int[stcount];
        for (int i = 0; i < stcount; i++) {
            if (i < glycoCount) {
                initialList[i] = i;
            } else {
                initialList[i] = -1;
            }
        }
        int[][] positionList = Arrangmentor.arrangementArrays(initialList);
        double[] scoreList = new double[positionList.length];

        double[] binten = new double[bs.length];
        double[] yinten = new double[ys.length];
        double[] ginten = new double[gs.length];
        double[] ginten2 = new double[gs.length];

        int[][] glycoFragmentId = new int[units.length][];
        for (int i = 0; i < glycoFragmentId.length; i++) {
            glycoFragmentId[i] = new int[units[i].getFragid().length];
            System.arraycopy(units[i].getFragid(), 0, glycoFragmentId[i], 0, units[i].getFragid().length);
        }
        int[][] comFragIds = Arrangmentor.arrangeAll(glycoFragmentId);

        String[] fragnames = OGlycanUnit.getTotalFragmentNames();
        double[] fragmasses = OGlycanUnit.getTotalFragmentMasses();
        HashSet<String> usedset1 = new HashSet<String>();
        HashSet<String> usedset2 = new HashSet<String>();
        HashMap<String, String> annotionMap1 = new HashMap<String, String>();
        HashMap<String, String> annotionMap2 = new HashMap<String, String>();
        HashMap<String, Boolean> annotionMap3 = new HashMap<String, Boolean>();
        HashMap<Double, String> matchmap = new HashMap<Double, String>();
//		boolean coreb = false;
//		System.out.println(uniPepSeq+"\t"+annotionMap1+"\n"+annotionMap2);
        double thres = 0;
        for (int i = 0; i < peaks.length; i++) {
            if (i == 20) break;
            thres += peaks[i].getIntensity();
        }
        thres = thres / 20.0;
		/*if(peaks.length%5==0){
			thres = (peaks[peaks.length/5].getIntensity()+peaks[peaks.length/5-1].getIntensity())/2.0;
		}else{
			thres = peaks[peaks.length/5].getIntensity();
		}*/

        L:
        for (int i = 0; i < peaks.length; i++) {

            double mzi = Double.parseDouble(df4.format(peaks[i].getMz()));
            double inteni = peaks[i].getIntensity();
            if (inteni > thres) {
                inteni = (peaks.length - i) / (double) peaks.length * (2 - thres / peaks[i].getIntensity());
            } else {
                inteni = (peaks.length - i) / (double) peaks.length * (peaks[i].getIntensity() / thres);
            }
            double[] tempScoreList = new double[positionList.length];
            int mzMatchCount = 0;
            if (matchmap2.containsKey(mzi)) continue;

            for (int j = 0; j < gs.length; j++) {
                double gfragmz = gs[j];
                if (Math.abs(mzi - gfragmz) < tolerance) {
                    if (usedset1.contains(gNames[j]))
                        continue L;

                    usedset1.add(gNames[j]);
                    ginten[j] = inteni;
                    matchmap.put(mzi, gNames[j]);
                    continue L;
                }
                double gfragmz2 = gs[j] - H2O;
                if (Math.abs(mzi - gfragmz2) < tolerance) {
                    if (usedset1.contains(gNames[j] + "-H2O"))
                        continue L;

                    usedset1.add(gNames[j] + "-H2O");
                    ginten2[j] = inteni;
                    matchmap.put(mzi, gNames[j] + "-H2O");
                    continue L;
                }
            }

            for (int j = 0; j < bs.length; j++) {

                double bfragmz = bs[j];
                double yfragmz = ys[j];

                if (Math.abs(mzi - yfragmz) < tolerance) {
                    if (usedset1.contains("y" + (j + 1)))
                        continue L;

                    usedset1.add("y" + (j + 1));
                    yinten[j] = inteni;
                    matchmap.put(mzi, "y" + (j + 1));
                    continue L;
                }

                if (Math.abs(mzi - bfragmz) < tolerance) {
                    if (usedset1.contains("b" + (j + 1)))
                        continue L;

                    usedset1.add("b" + (j + 1));
                    binten[j] = inteni;
                    matchmap.put(mzi, "b" + (j + 1));
                    continue L;
                }

                for (int k = 0; k < comFragIds.length; k++) {
                    double glycoMasses = 0;
                    StringBuilder glycoSb = new StringBuilder();
                    for (int l = 0; l < comFragIds[k].length; l++) {
                        glycoMasses += fragmasses[comFragIds[k][l]];
                        glycoSb.append(fragnames[comFragIds[k][l]] + "+");
                    }
                    glycoSb.deleteCharAt(glycoSb.length() - 1);

                    if (comFragIds[k].length <= yionSTCount[j]) {
                        if (Math.abs(mzi - yfragmz - glycoMasses) < tolerance) {
                            if (usedset2.contains("y" + (j + 1) + "+(" + glycoSb + ")"))
                                continue L;

                            usedset2.add("y" + (j + 1) + "+(" + glycoSb + ")");
                            matchmap.put(mzi, "y" + (j + 1) + "+(" + glycoSb + ")");
                            this.match(positionList, tempScoreList, comFragIds[k], units, stcount - yionSTCount[j], inteni, 1);
                            mzMatchCount++;
//							System.out.println(mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }
                        for (int l = 1; l <= units.length && l <= comFragIds[k].length; l++) {
                            if (Math.abs(mzi - yfragmz - (glycoMasses - H2O * l)) < tolerance) {
                                if (usedset2.contains("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")"))
                                    continue L;

                                usedset2.add("y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
                                matchmap.put(mzi, "y" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
                                this.match(positionList, tempScoreList, comFragIds[k], units, stcount - yionSTCount[j], inteni, 1);
                                mzMatchCount++;
//								System.out.println(mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+Arrays.toString(scoreList));
//								continue L;
                            }
                        }
                    }
                    if (comFragIds[k].length <= bionSTCount[j]) {
                        if (Math.abs(mzi - bfragmz - glycoMasses) < tolerance) {
                            if (usedset2.contains("b" + (j + 1) + "+(" + glycoSb + ")"))
                                continue L;

                            usedset2.add("b" + (j + 1) + "+(" + glycoSb + ")");
                            matchmap.put(mzi, "b" + (j + 1) + "+(" + glycoSb + ")");
                            this.match(positionList, tempScoreList, comFragIds[k], units, bionSTCount[j], inteni, 0);
                            mzMatchCount++;
//							System.out.println(mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+Arrays.toString(scoreList));
//							continue L;
                        }
                        for (int l = 1; l <= units.length && l <= comFragIds[k].length; l++) {
                            if (Math.abs(mzi - bfragmz - (glycoMasses - H2O * l)) < tolerance) {
                                if (usedset2.contains("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")"))
                                    continue L;

                                usedset2.add("b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
                                matchmap.put(mzi, "b" + (j + 1) + "+(" + glycoSb + "-H2O*" + l + ")");
                                this.match(positionList, tempScoreList, comFragIds[k], units, bionSTCount[j], inteni, 0);
                                mzMatchCount++;
//								System.out.println(mzi+"\t"+inteni+"\t"+"y"+(j+1)+"+("+glycoSb+")"+"\t"+Arrays.toString(scoreList));
//								continue L;
                            }
                        }
                    }
                }
            }

            if (mzMatchCount > 0) {
                for (int j = 0; j < scoreList.length; j++) {
                    scoreList[j] += tempScoreList[j] / (double) mzMatchCount;
                }
            }
        }

        if (usedset1.size() < 4) {
            return null;
        }
//System.out.println(usedset1.size());
        double totalModScore = 0;
        double maxScore = 0;
        double baseScore = MathTool.getTotal(binten) + MathTool.getTotal(yinten) + MathTool.getTotal(ginten) + MathTool.getTotal(ginten2);
        matchmap.putAll(matchmap2);

        int maxId = -1;
        double[][] positionTypeModScore = new double[glycoCount][stcount];
        for (int i = 0; i < scoreList.length; i++) {

            totalModScore += scoreList[i];

            for (int j = 0; j < stcount; j++) {
                if (positionList[i][j] >= 0)
                    positionTypeModScore[positionList[i][j]][j] += scoreList[i];
            }
//			System.out.println();
            if (scoreList[i] > maxScore) {
                maxScore = scoreList[i];
                maxId = i;
            }
        }
//		System.out.println("724\tscorelist\t"+Arrays.toString(scoreList));

        Arrays.sort(scoreList);
        double peptideScore = 0;
        if (maxScore != 0) {
            if (scoreList.length == 1) {
                peptideScore = 1.0;
            } else {
                peptideScore = (maxScore - scoreList[scoreList.length - 2]) / maxScore;
            }
        }

        double[] positionModScore = new double[stcount];
        if (totalModScore == 0) {
            for (int i = 0; i < positionModScore.length; i++) {
                positionModScore[i] = glycoCount / (double) stcount;
            }
        } else {
            for (int i = 0; i < positionModScore.length; i++) {
                for (int j = 0; j < glycoCount; j++) {
                    positionModScore[i] += positionTypeModScore[j][i];
                }
//				positionModScore[i] = positionModScore[i]*(double)glycoCount/totalModScore;
                positionModScore[i] = positionModScore[i] / totalModScore;
            }
            initialList = positionList[maxId];
        }

        for (int j = 0; j < positionTypeModScore[0].length; j++) {
            double posiTotal = 0;
            for (int i = 0; i < positionTypeModScore.length; i++) {
                posiTotal += positionTypeModScore[i][j];
            }
            for (int i = 0; i < positionTypeModScore.length; i++) {
                positionTypeModScore[i][j] = posiTotal == 0 ? 0 : positionTypeModScore[i][j] / posiTotal;
            }
        }

        int stid = 0;
        int glycoid = 0;
        int[] locList = new int[glycoCount];
        double[] locScoreList = new double[glycoCount];
        boolean[] determined = new boolean[glycoCount];
        ArrayList<Double> restScore = new ArrayList<Double>();

        OGlycanUnit[] newUnits = new OGlycanUnit[glycoCount];
        double[][] newPositionTypeModScore = new double[glycoCount][stcount];
        StringBuilder finalsb = new StringBuilder();
        StringBuilder scoresb = new StringBuilder();
        for (int i = 0; i < uniPepSeq.length(); i++) {
            finalsb.append(uniPepSeq.charAt(i));
            scoresb.append(uniPepSeq.charAt(i));
            if (uniPepSeq.charAt(i) == 'S' || uniPepSeq.charAt(i) == 'T') {
                if (positionModScore[stid] != 0) {
                    scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
                    if (initialList[stid] >= 0) {
//						finalsb.append("(").append(units[initialList[stid]].getName())
//						.append(",").append(df2.format(positionModScore[stid])).append(")");
                        finalsb.append("[").append(units[initialList[stid]].getComposition()).append("]");
                        locList[glycoid] = (i + 1);
                        locScoreList[glycoid] = positionModScore[stid];
                        newUnits[glycoid] = units[initialList[stid]];
                        newPositionTypeModScore[glycoid] = positionTypeModScore[initialList[stid]];
                        glycoid++;
                    } else {
//						scoresb.append("[").append(df2.format(positionModScore[stid])).append("]");
                        restScore.add(positionModScore[stid]);
                    }
                }
                stid++;
            }
        }

        L:
        for (int i = 0; i < determined.length; i++) {
            for (int j = 0; j < restScore.size(); j++) {
                if (locScoreList[i] == restScore.get(j)) {
                    determined[i] = false;
                    continue L;
                }
            }
            determined[i] = true;
        }

        for (int i = 0; i < newUnits.length; i++) {
            if (newUnits[i] == null) {
                return null;
            }
        }
/*
		if(uniPepSeq.equals("AATVGSLAGQPLQER")){
			for(int i=0;i<positionModScore.length;i++){
				System.out.print(positionModScore[i]+"\t");
			}
			System.out.println("positionModScore");
			for(int i=0;i<newPositionTypeModScore.length;i++){
				for(int j=0;j<newPositionTypeModScore[i].length;j++){
					System.out.print(newPositionTypeModScore[i][j]+"\t");
				}
//				System.out.println();
			}
//			System.out.println("newPositionTypeModScore\n");
		}
*/
//		System.out.println(finalsb);
        OGlycanPepInfo pepInfo = new OGlycanPepInfo(uniPepSeq, finalsb.toString(), scoresb.toString(), maxScore, baseScore,
                locList, newUnits, locScoreList, determined, positionModScore, newPositionTypeModScore, matchmap, peaks);
//		if(peaks.length>=0 && peaks.length<30)
//		System.out.println(maxScore+"\t"+peaks.length);
        pepInfo.setAnnotionMap1(annotionMap1);
        pepInfo.setAnnotionMap2(annotionMap2);
        pepInfo.setAnnotionMap3(annotionMap3);
//		System.out.println(matchmap);
//		pepInfo.writeAnnotion();

        return pepInfo;
    }

    private void write(String in, String out) throws IOException
    {
        HashMap<String, String> sitemap = new HashMap<String, String>();
        HashMap<String, Integer> locmap = new HashMap<String, Integer>();
        HashMap<String, String> refmap = new HashMap<String, String>();
        for (int i = 0; i < this.items.length; i++) {
            locmap.put(items[i].sequence, items[i].begin);
            refmap.put(items[i].sequence, items[i].ref);
//			refmap.put(items[i].sequence, items[i].);
        }
        HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("\t");
            String sequence = cs[2];
            boolean b = false;
            int stcount = 0;
            int loc = -1;
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < sequence.length(); i++) {
                char aa = sequence.charAt(i);
                if (aa == '[') {
                    loc = i - 1;
                    b = true;
                } else if (aa == ']') {
                    b = false;
                } else {
                    if (b) {
                        sb2.append(aa);
                    } else {
                        sb1.append(aa);
                        if (aa == 'S' || aa == 'T') {
                            stcount++;
                        }
                    }
                }
            }
            String shortPeptide = sb1.toString();
            String glycan = sb2.toString();
            String ref = refmap.get(shortPeptide);
            int site = loc + locmap.get(sb1.toString());
//			System.out.println(sequence+"\t"+sb1+"\t"+sb2+"\t"+stcount+"\t"+ref+"\t"+site);
            if (countmap.containsKey(stcount)) {
                countmap.put(stcount, countmap.get(stcount) + 1);
            } else {
                countmap.put(stcount, 1);
            }
            if (stcount == 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(ref).append("\t");
                sb.append(site).append("\t");
                sb.append(glycan).append("\t");
                String key = sb.toString();
                if (sitemap.containsKey(key)) {

                } else {
                    sitemap.put(key, "");
                    System.out.println(key);
                }
            }
        }
        reader.close();
//		System.out.println(countmap);
    }

    public static class DatabaseItem implements Comparable<DatabaseItem>
    {

        private String sequence;
        private String glycan;
        private int stcount;
        private double pepMass;
        private double glycoPepMass;
        private int begin;
        private String ref;
        private double[] bs;
        private double[] ys;
        private double[] gs;
        private String[] gNames;

        DatabaseItem(String line)
        {
            String[] cs = line.split("\t");
            this.sequence = cs[0];
            this.glycan = cs[1];
            this.stcount = Integer.parseInt(cs[2]);
            this.pepMass = Double.parseDouble(cs[3]);
            this.glycoPepMass = Double.parseDouble(cs[4]);
            this.begin = Integer.parseInt(cs[5]);
            this.ref = cs[6];

            String[] lbs = cs[7].split("_");
            this.bs = new double[lbs.length];
            for (int i = 0; i < bs.length; i++) {
                bs[i] = Double.parseDouble(lbs[i]);
            }
            String[] lys = cs[8].split("_");
            this.ys = new double[lys.length];
            for (int i = 0; i < ys.length; i++) {
                ys[i] = Double.parseDouble(lys[i]);
            }
            String[] lgs = cs[9].split("_");
            this.gs = new double[lgs.length];
            for (int i = 0; i < gs.length; i++) {
                gs[i] = Double.parseDouble(lgs[i]);
            }
            this.gNames = cs[10].split("_");
        }


        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(DatabaseItem o)
        {
            // TODO Auto-generated method stub
            if (this.glycoPepMass < o.glycoPepMass)
                return -1;
            else if (this.glycoPepMass > o.glycoPepMass)
                return 1;
            return 0;
        }
    }

}
