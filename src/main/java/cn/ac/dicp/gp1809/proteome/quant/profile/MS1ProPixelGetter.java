/*
 ******************************************************************************
 * File: MS1ProPixelGetter.java * * * Created on 2012-7-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.profile;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelQParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.MS1PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.MSXMLSequentialParser;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.mzxml.ScanHeader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.Description;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1ScanList;
import flanagan.analysis.Regression;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author ck
 * @version 2012-7-4, 21:37:03
 */
public class MS1ProPixelGetter extends MS1PixelGetter
{

    /**
     * nbt.1511-S1, p9
     */
    private final static double dm = 1.00286864;
    private double ppm = 0f;
    private int missNum = 0;
    private int leastINum = 0;

    private double ms1TotalCurrent;
    private MS1ScanList scanlist;
//	private TempFilePeakListGettor getter;

    private static final double coeThres = 0.4;
    private static final int halfLength = 15;
    private static final int missCount = 1;
    private static final double width = 0.005;

    public MS1ProPixelGetter(String file) throws IOException, XMLStreamException
    {
        this(new File(file), LabelQParameter.default_parameter());
    }

    public MS1ProPixelGetter(String file, LabelQParameter parameter) throws IOException, XMLStreamException
    {
        this(new File(file), parameter);
    }

    public MS1ProPixelGetter(File file) throws IOException, XMLStreamException
    {
        this(file, LabelQParameter.default_parameter());
    }

    public MS1ProPixelGetter(File file, LabelQParameter parameter) throws IOException, XMLStreamException
    {
        super(file, parameter);
    }

    protected void createReader(File file) throws IOException, XMLStreamException
    {
        MSXMLSequentialParser msParser = new MSXMLSequentialParser();
        msParser.open(file.getAbsolutePath());

        this.scanlist = new MS1ScanList(DtaType.MZXML);

        while (msParser.hasNextScan()) {
            Scan scan = null;

            try {
                scan = msParser.getNextScan();
            } catch (XMLStreamException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ScanHeader header = scan.header;

            int scanNum = header.getNum();
            int msLevel = header.getMsLevel();

            if (msLevel != 1) {
                continue;
            }

            double rt = header.getDoubleRetentionTime() / 60.0d;
            float totIonCurrent = header.getTotIonCurrent();
            Description des = new Description(scanNum, msLevel, rt, totIonCurrent);
            double[][] mzIntenList = scan.getMassIntensityList();
            IPeakList peaklist = new MS1PeakList(mzIntenList[0].length);

            double min = Double.MAX_VALUE;
            for (int i = 0; i < mzIntenList[0].length; i++) {
                double inten = mzIntenList[1][i];
                if (inten < min) {
                    min = inten;
                }
            }

            for (int i = 0; i < mzIntenList[0].length; i++) {
                double mz = mzIntenList[0][i];
                double inten = mzIntenList[1][i];

                if (inten > min) {
                    IPeak ip = new Peak(mz, inten - min);
                    peaklist.add(ip);
                }
            }

            MS1Scan ms1scan = new MS1Scan(des, peaklist);
            this.scanlist.add(ms1scan);
        }
        msParser.close();
    }

    public Pixel getPixel(int scannum, double mz)
    {
        return getPixel(this.scanlist.getScan(scannum), mz);
    }

    public Pixel getPixel(IMS1Scan scan, double mz)
    {

        int scanNum = scan.getScanNum();
        IPeak[] peaks = scan.getPeakList().getPeakArray();
        IPeak findpeak = new Peak(mz, 0d);

        int index = Arrays.binarySearch(peaks, findpeak);
        if (index < 0) {
            index = -index - 1;
        } else if (index >= peaks.length) {
            return new Pixel(scanNum, mz, 0);
        }

        if (index <= 4 || index + 4 > peaks.length)
            return new Pixel(scanNum, mz, 0);

        LinkedList<Double> mzlist = new LinkedList<Double>();
        LinkedList<Double> intenlist = new LinkedList<Double>();

        for (int i = 0; i < halfLength; i++) {
            if (index - i > 0) {
                double mzi = peaks[index - i - 1].getMz();
                double inteni = peaks[index - i - 1].getIntensity();
                if (peaks[index - i].getMz() - mzi < width) {
                    mzlist.addFirst(mzi);
                    intenlist.addFirst(inteni);
                } else {
                    break;
                }
            }
        }

        for (int i = 0; i < halfLength; i++) {
            if (index + i < peaks.length) {
                double mzi = peaks[index + i].getMz();
                double inteni = peaks[index + i].getIntensity();
                if (mzi - peaks[index].getMz() < width) {
                    mzlist.addLast(mzi);
                    intenlist.addLast(inteni);
                } else {
                    break;
                }
            }
        }

        if (mzlist.size() < 8) {
            return new Pixel(scanNum, mz, 0);
        }

        double[] mzs = new double[mzlist.size()];
        double[] intens = new double[intenlist.size()];
        for (int i = 0; i < mzs.length; i++) {
            mzs[i] = mzlist.get(i);
            intens[i] = intenlist.get(i);
        }

        Regression reg = new Regression(mzs, intens);
        reg.gaussian();

        double calMz = reg.getBestEstimates()[0];
        double calInten = reg.getBestEstimates()[2];
        double coe = reg.getCoefficientOfDetermination();

        Pixel pix;

        if (coe > coeThres) {
            pix = new Pixel(scanNum, calMz, calInten);
        } else {
            pix = new Pixel(scanNum, mz, 0);
        }

        return pix;
    }

    public PixelList getPixelList(Pixel pix)
    {

        int scanNum = pix.getScanNum();
        double mz = pix.getMz();
        pix = getPixel(scanNum, mz);

        PixelList pixList;
        boolean miss1;
        boolean miss2;
        int missCount1;
        int missCount2;

        if (pix.getInten() > 0) {

            pixList = new PixelList(pix);
            miss1 = false;
            miss2 = false;
            missCount1 = 0;
            missCount2 = 0;

        } else {

            pixList = new PixelList();
            miss1 = true;
            miss2 = true;
            missCount1 = 1;
            missCount2 = 1;
        }

        IMS1Scan next;
        int nextscan = scanNum;
        while ((next = this.scanlist.getNextScan(nextscan)) != null) {

            nextscan = next.getScanNum();
            Pixel nexPix = getPixel(next, mz);

            if (nexPix != null) {

                pixList.addPixel(nexPix);

                if (miss1) {
                    missCount1 = 0;
                    miss1 = false;
                }
            } else {
                if (miss1)
                    missCount1++;
                else
                    miss1 = true;
            }
            if (missCount1 >= 2)
                break;
        }

        IMS1Scan prev;
        int prevScan = scanNum;
        while ((prev = this.scanlist.getPreviousScan(prevScan)) != null) {

            prevScan = prev.getScanNum();
            Pixel prePix = getPixel(prev, mz);

            if (prePix != null) {

                pixList.addPixel(prePix);
                if (miss2) {
                    missCount2 = 0;
                    miss2 = false;
                }
            } else {
                if (miss2)
                    missCount2++;
                else
                    miss2 = true;
            }
            if (missCount2 >= 2)
                break;
        }

        return pixList;
    }

    public void setPPM(double ppm)
    {
        this.ppm = ppm;
    }

    public void setLeastIdenNum(int num)
    {
        this.leastINum = num;
    }

    public int getLeastIdenNum()
    {
        return this.leastINum;
    }

    public double getMS1TotalCurrent()
    {
        return ms1TotalCurrent;
    }

    public void close()
    {
        this.scanlist = null;
        System.gc();
    }

    /**
     * @param args
     * @throws IOException
     * @throws NumberFormatException
     * @throws XMLStreamException
     */
    public static void main(String[] args) throws NumberFormatException, IOException, XMLStreamException
    {
        // TODO Auto-generated method stub
		
/*		BufferedReader reader = new BufferedReader(new FileReader("F:\\Data\\Profile data\\gaussian3.txt"));
		String line = null;
		ArrayList <double[]> list = new ArrayList <double[]>();
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			double mz = Double.parseDouble(ss[0]);
			double inten = Double.parseDouble(ss[1]);
			list.add(new double[]{mz, inten});
//			if(inten>1000){
//				System.out.println(mz+"\t"+inten);
//			}
		}
		reader.close();
		
		double [] x = new double[list.size()];
		double [] y = new double[list.size()];
		for(int i=0;i<list.size();i++){
			x[i] = list.get(i)[0];
			y[i] = list.get(i)[1];
		}
//		System.out.println(MathTool.getMedian(y));
//		ProbabilityPlot pp = new ProbabilityPlot(y);
//		pp.gaussianProbabilityPlot();
//		System.out.println(pp.gaussianCorrelationCoefficient()+"\t"+pp.gaussianMu()+"\t"+pp.gaussianSigma());
		
		Regression reg = new Regression(x, y);
		reg.gaussian();
		System.out.println(reg.getCoefficientOfDetermination());
		double [] dd = reg.getBestEstimates();
		System.out.println(dd.length);
		System.out.println(dd[0]+"\t"+dd[1]+"\t"+dd[2]);
//		reg.print("H:\\wiff\\result2.txt");
*/
        MS1ProPixelGetter getter = new MS1ProPixelGetter("H:\\wiff_control\\120401_FSGP_L_H_in_situ_1mg\\" +
                "120401_FSGP_L_H_in_situ_1mg_2-20120401.mzXML");
        Pixel pix = new Pixel(4563, 755.39624);
        pix.setCharge(2);

        PrintWriter pw = new PrintWriter("H:\\wiff_control\\120401_FSGP_L_H_in_situ_1mg\\" +
                "4563.txt");
        IMS1Scan scan = getter.scanlist.getScan(4563);
        IPeak[] peaks = scan.getPeakList().getPeakArray();
        for (int i = 0; i < peaks.length; i++) {
            pw.write(peaks[i].getMz() + "\t" + peaks[i].getIntensity() + "\n");
        }
        pw.close();
    }

}
