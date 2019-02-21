package cn.ac.dicp.gp1809.glyco.oglycan;

import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.RegionTopNIntensityFilter;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS2Scan;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.math.MathTool;
import omics.util.io.FileType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author ck
 */
public class OGlycanSpecSpliter5
{
    private final static double dm = 1.00286864;
    private static final String lineSeparator = IOConstant.lineSeparator;
    private static RegionTopNIntensityFilter filter = new RegionTopNIntensityFilter(8, 100);
    private static double tolerance = 0.05;
    private static DecimalFormat df4 = DecimalFormats.DF0_4;
    double totalrange = 0;
    double totalscancount = 0;
    double totalpeakcount = 0;
    private File in;
    private PrintWriter infoWriter;
    private PrintWriter scanWriter;
    // private double ppm = 20;
    private double[] mzs;
    private HashMap<Double, int[][]> unitmap;
    private OGlycanUnit[] units = new OGlycanUnit[]{OGlycanUnit.core1_1, OGlycanUnit.core1_2, OGlycanUnit.core1_3,
            OGlycanUnit.core1_4,
            // OGlycanUnit.core1_4b,
            OGlycanUnit.core1_5, OGlycanUnit.core2_1, OGlycanUnit.core2_2, OGlycanUnit.core2_3, OGlycanUnit.core2_4,
            OGlycanUnit.core2_5,
            // OGlycanUnit.core2_6, OGlycanUnit.core2_7,
            OGlycanUnit.core2_8, OGlycanUnit.core2_9
            //, OGlycanUnit.core2_10, OGlycanUnit.core2_11
    };
    private OGlycanUnit[] allUnits = OGlycanUnit.values();

    public OGlycanSpecSpliter5(String in, String out) throws IOException
    {
        this(new File(in), out);
    }

    public OGlycanSpecSpliter5(File in, String out) throws IOException
    {
        this.in = in;

        File file = new File(out);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filename = file.getName();
        String s1 = "";
        String s2 = "";

        if (filename.endsWith("mgf")) {

            s1 = filename.replace("mgf", "info");
            s2 = filename.replace("mgf", "oglycan.mgf");

        } else {

            s1 = filename + ".info";
            s2 = filename + ".oglycan.mgf";
        }

        this.infoWriter = new PrintWriter(out + "\\" + s1);
        this.scanWriter = new PrintWriter(out + "\\" + s2);

        OGlycanUnitStatistic statistic = new OGlycanUnitStatistic(units);
        this.mzs = statistic.getMzList();
        this.unitmap = statistic.getUnitIdMap();
    }

    private static void count(File in) throws DtaFileParsingException, FileNotFoundException
    {
        int fileCount = 0;
        int totalCount = 0;
        int glycoCount = 0;

        if (in.isDirectory()) {

            File[] files = in.listFiles(arg0 -> {
                return arg0.getName().endsWith("mgf");
            });

            fileCount = files.length;
            Arrays.sort(files);
            L:
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i]);
                MgfReader reader = new MgfReader(files[i]);
                MS2Scan ms2scan = null;
                while ((ms2scan = reader.getNextMS2Scan()) != null) {
                    totalCount++;

                    IMS2PeakList peaklist = ms2scan.getPeakList();
                    if (peaklist == null)
                        continue;
                    PrecursePeak pp = peaklist.getPrecursePeak();
                    double percent = 0;
                    double intenthres1 = 0;
                    double intenthres2 = 0;

                    IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
                    if (temppeaks.length > 100) {
                        if (temppeaks.length >= 400) {
                            percent = 1;
                        } else if (temppeaks.length < 400 && temppeaks.length >= 300) {
                            percent = 0.75;
                        } else if (temppeaks.length < 300 && temppeaks.length >= 200) {
                            percent = 0.5;
                        } else if (temppeaks.length < 200 && temppeaks.length >= 100) {
                            percent = 0.25;
                        }

                        ArrayList<Double> tempintenlist = new ArrayList<Double>();
                        double[] rsdlist = new double[temppeaks.length];

                        for (int j = temppeaks.length - 1; j >= 0; j--) {
                            tempintenlist.add(temppeaks[j].getIntensity());
                            rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
                        }

                        for (int j = 0; j < rsdlist.length; j++) {
                            if (rsdlist[j] > 0.5) {
                                intenthres1 = temppeaks[temppeaks.length - j].getIntensity();
                            }
                            if (rsdlist[j] > percent) {
                                intenthres2 = temppeaks[temppeaks.length - j].getIntensity() / 5.0;
                            }
                            if (intenthres1 > 0 && intenthres2 > 0) {
                                break;
                            }
                        }

                        if (intenthres2 > intenthres1) {
                            intenthres2 = intenthres1;
                        }
                    }

                    // System.out.println(temppeaks.length+"\t"+percent+"\t"+intenthres1+"\t"+intenthres2);

                    IPeak[] peaks = peaklist.getPeakArray();
                    Arrays.sort(peaks);

                    double maxinten = 0;
                    int[] markpeaks = new int[7];
                    int[] neuAc = new int[2];
                    ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();

                    boolean oglycan = false;

                    for (int j = 0; j < peaks.length; j++) {

                        double mzi = peaks[j].getMz();
                        double inteni = peaks[j].getIntensity();

                        if (inteni < intenthres1) {
                            if (inteni > intenthres2)
                                highIntenList.add(peaks[j]);
                        } else {
                            if (Math.abs(mzi - 163.060101) < tolerance) {
                                continue;
                            } else if (Math.abs(mzi - 204.086649) < tolerance) {
                                oglycan = true;
                                continue;
                            } else if (Math.abs(mzi - 274.087412635) < tolerance) {
                                neuAc[0] = 1;
                                continue;
                            } else if (Math.abs(mzi - 292.102692635) < tolerance) {
                                neuAc[1] = 1;
                                continue;
                            } else if (Math.abs(mzi - 366.139472) < tolerance) {
                                markpeaks[1] = 1;
                                continue;
                                // HexNAc+NeuAc
                            } else if (Math.abs(mzi - 495.18269) < tolerance) {
                                markpeaks[2] = 1;
                                continue;
                                // NeuAc*2
                            } else if (Math.abs(mzi - 583.19873) < tolerance) {
                                markpeaks[3] = 1;
                                continue;
                                // HexNAc*2
                            } else if (Math.abs(mzi - 407.16665) < tolerance) {
                                markpeaks[4] = 1;
                                continue;
                                // HexNAc*2+Hex
                            } else if (Math.abs(mzi - 569.218847) < tolerance) {
                                markpeaks[5] = 1;
                                continue;
                                // HexNAc*2+Hex*2
                            } else if (Math.abs(mzi - 731.271672) < tolerance) {
                                markpeaks[6] = 1;
                                continue;
                                // HexNAc+Hex*2, N-glyco
                            } else if (Math.abs(mzi - 528.192299) < tolerance) {
                                // if(inteni>intenthres2)
                                // return;
                                // HexNAc+Hex*3, N-glyco
                            } else if (Math.abs(mzi - 690.245124) < tolerance) {
                                // if(inteni>intenthres2)
                                // return;
                            } else {
                                highIntenList.add(peaks[j]);
                                if (peaks[j].getIntensity() > maxinten) {
                                    maxinten = peaks[j].getIntensity();
                                }
                                continue;
                            }
                        }
                    }

                    if (neuAc[0] + neuAc[1] == 2) {
                        markpeaks[0] = 1;
                    }
                    if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
                        continue;
                    }

                    if (highIntenList.size() <= 5)
                        continue;

                    glycoCount++;
                }
                reader.close();
            }
        } else {
            fileCount = 1;
            MgfReader reader = new MgfReader(in);
            MS2Scan ms2scan = null;
            while ((ms2scan = reader.getNextMS2Scan()) != null) {
                totalCount++;

                IMS2PeakList peaklist = ms2scan.getPeakList();
                if (peaklist == null)
                    break;
                PrecursePeak pp = peaklist.getPrecursePeak();
                double percent = 0;
                double intenthres1 = 0;
                double intenthres2 = 0;

                IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
                if (temppeaks.length > 100) {
                    if (temppeaks.length >= 400) {
                        percent = 1;
                    } else if (temppeaks.length < 400 && temppeaks.length >= 300) {
                        percent = 0.75;
                    } else if (temppeaks.length < 300 && temppeaks.length >= 200) {
                        percent = 0.5;
                    } else if (temppeaks.length < 200 && temppeaks.length >= 100) {
                        percent = 0.25;
                    }

                    ArrayList<Double> tempintenlist = new ArrayList<Double>();
                    double[] rsdlist = new double[temppeaks.length];

                    for (int j = temppeaks.length - 1; j >= 0; j--) {
                        tempintenlist.add(temppeaks[j].getIntensity());
                        rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
                    }

                    for (int j = 0; j < rsdlist.length; j++) {
                        if (rsdlist[j] > 0.5) {
                            intenthres1 = temppeaks[temppeaks.length - j].getIntensity();
                        }
                        if (rsdlist[j] > percent) {
                            intenthres2 = temppeaks[temppeaks.length - j].getIntensity() / 5.0;
                        }
                        if (intenthres1 > 0 && intenthres2 > 0) {
                            break;
                        }
                    }

                    if (intenthres2 > intenthres1) {
                        intenthres2 = intenthres1;
                    }
                }

                // System.out.println(temppeaks.length+"\t"+percent+"\t"+intenthres1+"\t"+intenthres2);

                IPeak[] peaks = peaklist.getPeakArray();
                Arrays.sort(peaks);

                double maxinten = 0;
                int[] markpeaks = new int[7];
                int[] neuAc = new int[2];
                ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();

                boolean oglycan = false;

                for (int j = 0; j < peaks.length; j++) {

                    double mzi = peaks[j].getMz();
                    double inteni = peaks[j].getIntensity();

                    if (inteni < intenthres1) {
                        if (inteni > intenthres2)
                            highIntenList.add(peaks[j]);
                    } else {
                        if (Math.abs(mzi - 163.060101) < tolerance) {
                            continue;
                        } else if (Math.abs(mzi - 204.086649) < tolerance) {
                            oglycan = true;
                            continue;
                        } else if (Math.abs(mzi - 274.087412635) < tolerance) {
                            neuAc[0] = 1;
                            continue;
                        } else if (Math.abs(mzi - 292.102692635) < tolerance) {
                            neuAc[1] = 1;
                            continue;
                        } else if (Math.abs(mzi - 366.139472) < tolerance) {
                            markpeaks[1] = 1;
                            continue;
                            // HexNAc+NeuAc
                        } else if (Math.abs(mzi - 495.18269) < tolerance) {
                            markpeaks[2] = 1;
                            continue;
                            // NeuAc*2
                        } else if (Math.abs(mzi - 583.19873) < tolerance) {
                            markpeaks[3] = 1;
                            continue;
                            // HexNAc*2
                        } else if (Math.abs(mzi - 407.16665) < tolerance) {
                            markpeaks[4] = 1;
                            continue;
                            // HexNAc*2+Hex
                        } else if (Math.abs(mzi - 569.218847) < tolerance) {
                            markpeaks[5] = 1;
                            continue;
                            // HexNAc*2+Hex*2
                        } else if (Math.abs(mzi - 731.271672) < tolerance) {
                            markpeaks[6] = 1;
                            continue;
                            // HexNAc+Hex*2, N-glyco
                        } else if (Math.abs(mzi - 528.192299) < tolerance) {
                            // if(inteni>intenthres2)
                            // return;
                            // HexNAc+Hex*3, N-glyco
                        } else if (Math.abs(mzi - 690.245124) < tolerance) {
                            // if(inteni>intenthres2)
                            // return;
                        } else {
                            highIntenList.add(peaks[j]);
                            if (peaks[j].getIntensity() > maxinten) {
                                maxinten = peaks[j].getIntensity();
                            }
                            continue;
                        }
                    }
                }

                if (neuAc[0] + neuAc[1] == 2) {
                    markpeaks[0] = 1;
                }
                if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
                    continue;
                }

                if (highIntenList.size() <= 5)
                    continue;

                glycoCount++;
            }
        }
        System.out.println(in.getName() + "\t" + fileCount + "\t" + totalCount + "\t" + glycoCount);
    }

    private static void test(String in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("BEGIN")) {
                System.out.println(line);
            } else if (line.startsWith("PEPMASS")) {
                System.out.println(line);
            }
        }
        reader.close();
    }

    private static void writeNewInfo(String filteredInfo, String nofilterInfo, String newInfo) throws IOException
    {
        HashMap<String, ArrayList<OGlycanScanInfo2>> filteredmap = new HashMap<String, ArrayList<OGlycanScanInfo2>>();
        BufferedReader filterreader = new BufferedReader(new FileReader(filteredInfo));
        String line = null;
        while ((line = filterreader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            if (filteredmap.containsKey(oriScanname)) {
                filteredmap.get(oriScanname).add(info);
            } else {
                ArrayList<OGlycanScanInfo2> list = new ArrayList<OGlycanScanInfo2>();
                list.add(info);
                filteredmap.put(oriScanname, list);
            }
        }
        filterreader.close();

        PrintWriter writer = new PrintWriter(newInfo);
        BufferedReader nofilterreader = new BufferedReader(new FileReader(nofilterInfo));
        while ((line = nofilterreader.readLine()) != null) {
            OGlycanScanInfo2 info = new OGlycanScanInfo2(line);
            String scanname = info.getScanname();
            String oriScanname = scanname.substring(0, scanname.lastIndexOf("."));
            double pepmw = info.getPepMw();
            if (filteredmap.containsKey(oriScanname)) {
                ArrayList<OGlycanScanInfo2> list = filteredmap.get(oriScanname);
                for (OGlycanScanInfo2 info2 : list) {
                    if (info2.getPepMw() == pepmw) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(scanname).append("\t");
                        sb.append(pepmw).append("\t");
                        int[] marks = info2.getMarkpeaks();
                        for (int i = 0; i < marks.length; i++) {
                            sb.append(marks[i]).append("_");
                        }
                        sb.append("\t");
                        int[] type = info2.getFindType();
                        for (int i = 0; i < type.length; i++) {
                            sb.append(type[i]).append("_");
                        }
                        sb.append("#");

                        OGlycanUnit[][] units = info2.getUnits();
                        for (int i = 0; i < units.length; i++) {
                            for (int j = 0; j < units[i].length; j++) {
                                sb.append(units[i][j].getName()).append("\t");
                            }
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append("#");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        writer.write(sb + "\n");
                    }
                }
            }
        }
        nofilterreader.close();
        writer.close();
    }

    private static void convertInfo(String oldinfo, String newinfo) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(oldinfo));
        PrintWriter pw = new PrintWriter(newinfo);

        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] cs = line.split("#");
            StringBuilder sb = new StringBuilder(cs[0]);
            HashSet<String> set = new HashSet<String>();
            for (int i = 1; i < cs.length; i++) {
                String rs = cs[i].replace("core1_4b", "core1_4");
                set.add(rs);
            }
            for (String rs : set) {
                sb.append("#").append(rs);
            }
            pw.write(sb + "\n");
        }
        reader.close();
        pw.close();
    }

    public static void main(String[] args) throws DtaFileParsingException, IOException
    {
        long begin = System.currentTimeMillis();

        for (File file : FileType.MGF.listFiles(new File("Z:\\MaoJiawei\\o-glycan\\D1"))) {
            String out = FilenameUtils.removeExtension(file.getAbsolutePath()) + "_deglyco";
            OGlycanSpecSpliter5 specSpliter5 = new OGlycanSpecSpliter5(file, out);
            specSpliter5.deglyco();
        }

//        OGlycanSpecSpliter5 spliter = new OGlycanSpecSpliter5("H:\\OGLYCAN\\OGlycan_0530_2D_original_data\\fetuin",
//                "H:\\OGLYCAN2\\20141211_14glyco\\fetuin");
//        spliter.deglyco();

        long end = System.currentTimeMillis();
        System.out.println((end - begin) / 60000.0);
    }

    public void deglyco() throws DtaFileParsingException, IOException
    {
        if (in.isDirectory()) {
            File[] files = in.listFiles(arg0 -> arg0.getName().endsWith("mgf"));
            Arrays.sort(files);

            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i]);
                MgfReader reader = new MgfReader(files[i]);
                MS2Scan ms2scan = null;
                while ((ms2scan = reader.getNextMS2Scan()) != null) {
                    this.judge(ms2scan, (i + 1));
                }
                reader.close();
            }
        } else {
            System.out.println(in);
            MgfReader reader = new MgfReader(in);
            MS2Scan ms2scan = null;
            while ((ms2scan = reader.getNextMS2Scan()) != null) {
                this.judge(ms2scan, 1);
            }
        }

        // System.out.println("O-glycan spectra count\t" + this.glycocount);

        this.infoWriter.close();
        this.scanWriter.close();
    }

    private void combine() throws DtaFileParsingException, IOException
    {
        if (in.isDirectory()) {

            File[] files = in.listFiles(arg0 -> arg0.getName().endsWith("mgf"));

            Arrays.sort(files);

            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i]);
                MgfReader reader = new MgfReader(files[i]);
                MS2Scan ms2scan = null;
                while ((ms2scan = reader.getNextMS2Scan()) != null) {
                    this.denoise(ms2scan, (i + 1));
                }
                reader.close();
            }
        } else {

            MgfReader reader = new MgfReader(in);
            MS2Scan ms2scan = null;
            while ((ms2scan = reader.getNextMS2Scan()) != null) {
                String name = ms2scan.getScanName().getScanName();
                this.denoise(ms2scan, 1);
            }
        }

        this.infoWriter.close();
        this.scanWriter.close();
    }

    private void judge(MS2Scan ms2scan, int fileid)
    {
        IMS2PeakList peaklist = ms2scan.getPeakList();
        if (peaklist == null)
            return;

//		HashSet<Double> filter8Set = new HashSet<Double>(); // 8 peaks per 100 bin
//		IPeak[] filter8peaks = filter.filter(peaklist).getPeakList();
//		for (IPeak peak : filter8peaks) {
//			filter8Set.add(peak.getMz());
//		}

        double intenthres1 = 0; // min intensity with rsd > 0.5 intensity, for marker detection
        double intenthres2 = 0; // intensity threshold of the percent rsd, should <= intensity1
        boolean filter = false;

        //<editor-fold desc="calculate threshold">
        IPeak[] temppeaks = peaklist.getPeaksSortByIntensity(); // peak with intensity in descending order
        if (temppeaks.length > 100) {
            filter = true;
            double percent; // rsd threshold
            if (temppeaks.length >= 400) {
                percent = 1;
            } else if (temppeaks.length >= 300) {
                percent = 0.75;
            } else if (temppeaks.length >= 200) {
                percent = 0.5;
            } else {
                percent = 0.25;
            }

            ArrayList<Double> tempintenlist = new ArrayList<>();
            double[] rsdlist = new double[temppeaks.length];

            for (int i = temppeaks.length - 1; i >= 0; i--) {
                tempintenlist.add(temppeaks[i].getIntensity());
                rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
            }

            for (int i = 0; i < rsdlist.length; i++) {
                if (rsdlist[i] > 0.5) {
                    intenthres1 = temppeaks[temppeaks.length - i].getIntensity();
                }
                if (rsdlist[i] > percent) {
                    intenthres2 = temppeaks[temppeaks.length - i].getIntensity() / 5.0;
                }
                if (intenthres1 > 0 && intenthres2 > 0) {
                    break;
                }
            }

            if (intenthres2 > intenthres1) {
                intenthres2 = intenthres1;
            }
        }
        //</editor-fold>

        IPeak[] peaks = peaklist.getPeakArray();
        Arrays.sort(peaks);

        double maxinten = 0;
        int[] markpeaks = new int[7]; // NeuAc,366,495,583,407,569,731
        int[] neuAc = new int[2]; // 274, 292
        ArrayList<IPeak> highIntenList = new ArrayList<>(); // all peaks > threshold2

        boolean oglycan = false;

        //<editor-fold desc="oxonium detection">
        for (IPeak peak : peaks) {

            double mzi = peak.getMz();
            double inteni = peak.getIntensity();

            if (inteni < intenthres1) {
                if (inteni > intenthres2)
                    highIntenList.add(peak);
            } else { // test all peaks > threshold1
                if (Math.abs(mzi - 204.086649) < tolerance) {
                    oglycan = true;
                } else if (Math.abs(mzi - 274.087412635) < tolerance) {
                    neuAc[0] = 1;
                } else if (Math.abs(mzi - 292.102692635) < tolerance) {
                    neuAc[1] = 1;
                } else if (Math.abs(mzi - 366.139472) < tolerance) {
                    markpeaks[1] = 1;
                    // HexNAc+NeuAc
                } else if (Math.abs(mzi - 495.18269) < tolerance) {
                    markpeaks[2] = 1;
                    // NeuAc*2
                } else if (Math.abs(mzi - 583.19873) < tolerance) {
                    markpeaks[3] = 1;
                    // HexNAc*2
                } else if (Math.abs(mzi - 407.16665) < tolerance) {
                    markpeaks[4] = 1;
                    // HexNAc*2+Hex
                } else if (Math.abs(mzi - 569.218847) < tolerance) {
                    markpeaks[5] = 1;
                    // HexNAc*2+Hex*2
                } else if (Math.abs(mzi - 731.271672) < tolerance) {
                    markpeaks[6] = 1;
                    // HexNAc+Hex*2, N-glyco
                } else if (Math.abs(mzi - 528.192299) < tolerance) {
                    // return;
                    // HexNAc+Hex*3, N-glyco
                } else if (Math.abs(mzi - 690.245124) < tolerance) {
                    // if(inteni>intenthres2)
                    // return;
                } else {
                    highIntenList.add(peak);
                    if (peak.getIntensity() > maxinten) {
                        maxinten = peak.getIntensity();
                    }
                }
            }
        }
        //</editor-fold>

        if (neuAc[0] + neuAc[1] == 2) {
            markpeaks[0] = 1;
        }
        if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
            return;
        }

        if (highIntenList.size() <= 5)
            return;
        IPeak[] noglyPeaks = highIntenList.toArray(new IPeak[0]); // 去除鎓离子后的谱峰

        totalrange += noglyPeaks[noglyPeaks.length - 1].getMz();
        totalscancount++;
        totalpeakcount += noglyPeaks.length;

        IPeak[] noglyPeaksIntenSort = new IPeak[noglyPeaks.length];
        System.arraycopy(noglyPeaks, 0, noglyPeaksIntenSort, 0, noglyPeaks.length);
        Arrays.sort(noglyPeaksIntenSort, (arg0, arg1) -> Double.compare(arg1.getIntensity(), arg0.getIntensity()));

        StringBuilder monoinfo = new StringBuilder();
        for (int markpeak : markpeaks) {
            monoinfo.append(markpeak).append("_");
        }

        PrecursePeak pp = peaklist.getPrecursePeak();
        double premz = pp.getMz();
        int precharge = pp.getCharge();
        HashMap<String, OGlycanTypeInfo2> infomap = new HashMap<>();
        if (precharge == 0) {
            this.judge(premz, 2, noglyPeaks, noglyPeaksIntenSort, markpeaks, infomap, monoinfo.toString());
            this.judge(premz, 3, noglyPeaks, noglyPeaksIntenSort, markpeaks, infomap, monoinfo.toString());
        } else {
            this.judge(premz, precharge, noglyPeaks, noglyPeaksIntenSort, markpeaks, infomap, monoinfo.toString());
        }

        int scannum = ms2scan.getScanNum();
        String name = ms2scan.getScanName().getScanName();
        if (name.endsWith(", "))
            name = name.substring(0, name.length() - 2);

        if (infomap.size() == 0)
            return;

        HashMap<Double, StringBuilder> mzmap2 = new HashMap<>();
        String[] keyArrays = infomap.keySet().toArray(new String[infomap.size()]);
        Arrays.sort(keyArrays);

        for (String uname : keyArrays) {
            OGlycanTypeInfo2 typeinfo = infomap.get(uname);
            int[] findtype = typeinfo.getFindType();
            if (findtype[0] == 0)
                continue;

            int charge = typeinfo.getCharge();
            int maxcharge = charge > 3 ? 3 : 2;
            double pepmz = Double
                    .parseDouble(df4.format(typeinfo.getMass() / (double) charge + AminoAcidProperty.PROTON_W));
            if (pepmz < 100)
                continue;

            if (mzmap2.containsKey(pepmz)) {
                mzmap2.get(pepmz).append("#").append(uname);
            } else {
                String newname;
                if (name.startsWith("Locus:1.1.1")) {
                    newname = name + "." + fileid + "." + (mzmap2.size() + 1);
                } else {
                    newname = "Locus:1.1.1." + scannum + "." + fileid + "." + (mzmap2.size() + 1);
                }

                StringBuilder sb2 = new StringBuilder();
                sb2.append(newname).append("\t");
                sb2.append(typeinfo.getMass()).append("\t");
                sb2.append(typeinfo.getMarks()).append("\t");
                sb2.append(typeinfo.getFindTypeString());
                sb2.append("#").append(uname);
                mzmap2.put(pepmz, sb2);

                System.out.println("751\t" + newname + "\t" + typeinfo.getExpGlycoMass());

                int peakcount = 0;
                StringBuilder sb = new StringBuilder();
                sb.append("BEGIN IONS" + lineSeparator);
                sb.append("PEPMASS=" + df4.format(pepmz) + lineSeparator);
                sb.append("CHARGE=" + charge + "+" + lineSeparator);
                sb.append("TITLE=" + newname + lineSeparator);

                L:
                for (IPeak noglyPeak : noglyPeaks) {

                    for (int chargeid = 1; chargeid <= maxcharge; chargeid++) {
                        double peakmass = (noglyPeak.getMz() - AminoAcidProperty.PROTON_W) * (double) chargeid;
                        double pepmass = typeinfo.getMass();

                        if (Math.abs(peakmass - pepmass) < tolerance || Math.abs(peakmass - pepmass - dm) < tolerance) {
                            continue L;
                        }

                        double[] fraglist = new double[]{203.079373, 365.132198};

                        for (double aFraglist : fraglist) {
                            if (Math.abs(peakmass - (pepmass + aFraglist)) <= tolerance
                                    || Math.abs(peakmass - (pepmass + aFraglist - 18.010565)) <= tolerance) {

                                continue L;
                            }

                            if (Math.abs(peakmass - (pepmass + aFraglist) - dm) <= tolerance
                                    || Math.abs(peakmass - (pepmass + aFraglist - 18.010565) - dm) <= tolerance) {
                                continue L;
                            }
                        }
                    }

                    peakcount++;
                    sb.append(noglyPeak.getMz() + "\t" + noglyPeak.getIntensity() + lineSeparator);
                }

                if (peakcount <= 5)
                    continue;

                sb.append("END IONS" + lineSeparator);
                this.scanWriter.write(sb.toString());
            }
        }

        for (Double pmz : mzmap2.keySet()) {
            this.infoWriter.write(mzmap2.get(pmz) + lineSeparator);
        }
    }

    private void judge(MS2Scan ms2scan, int fileid, double correction)
    {
        IMS2PeakList peaklist = ms2scan.getPeakList();
        if (peaklist == null)
            return;
        PrecursePeak pp = peaklist.getPrecursePeak();
        double premz = pp.getMz();
        int precharge = pp.getCharge();

        int scannum = ms2scan.getScanNum();
        String name = ms2scan.getScanName().getScanName();
        if (name.endsWith(", "))
            name = name.substring(0, name.length() - 2);

        double percent = 0;
        double intenthres1 = 0;
        double intenthres2 = 0;
        boolean filter = false;

        IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
        if (temppeaks.length > 100) {
            filter = true;
            if (temppeaks.length >= 400) {
                percent = 1;
            } else if (temppeaks.length < 400 && temppeaks.length >= 300) {
                percent = 0.75;
            } else if (temppeaks.length < 300 && temppeaks.length >= 200) {
                percent = 0.5;
            } else if (temppeaks.length < 200 && temppeaks.length >= 100) {
                percent = 0.25;
            }

            ArrayList<Double> tempintenlist = new ArrayList<Double>();
            double[] rsdlist = new double[temppeaks.length];

            for (int i = temppeaks.length - 1; i >= 0; i--) {
                tempintenlist.add(temppeaks[i].getIntensity());
                rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
            }

            for (int i = 0; i < rsdlist.length; i++) {
                if (rsdlist[i] > 0.5) {
                    intenthres1 = temppeaks[temppeaks.length - i].getIntensity();
                }
                if (rsdlist[i] > percent) {
                    intenthres2 = temppeaks[temppeaks.length - i].getIntensity() / 5.0;
                }
                if (intenthres1 > 0 && intenthres2 > 0) {
                    break;
                }
            }

            if (intenthres2 > intenthres1) {
                intenthres2 = intenthres1;
            }
        }

        // System.out.println(temppeaks.length+"\t"+percent+"\t"+intenthres1+"\t"+intenthres2);

        IPeak[] peaks = peaklist.getPeakArray();
        Arrays.sort(peaks);

        double maxinten = 0;
        int[] markpeaks = new int[7];
        int[] neuAc = new int[2];
        ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();

        boolean oglycan = false;

        for (int i = 0; i < peaks.length; i++) {

            double mzi = peaks[i].getMz();
            double inteni = peaks[i].getIntensity();

            if (inteni < intenthres1) {
                if (inteni > intenthres2)
                    highIntenList.add(peaks[i]);
            } else {
                if (Math.abs(mzi - 163.060101) < tolerance) {
                    continue;
                } else if (Math.abs(mzi - 204.086649) < tolerance) {
                    oglycan = true;
                    continue;
                } else if (Math.abs(mzi - 274.087412635) < tolerance) {
                    neuAc[0] = 1;
                    continue;
                } else if (Math.abs(mzi - 292.102692635) < tolerance) {
                    neuAc[1] = 1;
                    continue;
                } else if (Math.abs(mzi - 366.139472) < tolerance) {
                    markpeaks[1] = 1;
                    continue;
                    // HexNAc+NeuAc
                } else if (Math.abs(mzi - 495.18269) < tolerance) {
                    markpeaks[2] = 1;
                    continue;
                    // NeuAc*2
                } else if (Math.abs(mzi - 583.19873) < tolerance) {
                    markpeaks[3] = 1;
                    continue;
                    // HexNAc*2
                } else if (Math.abs(mzi - 407.16665) < tolerance) {
                    markpeaks[4] = 1;
                    continue;
                    // HexNAc*2+Hex
                } else if (Math.abs(mzi - 569.218847) < tolerance) {
                    markpeaks[5] = 1;
                    continue;
                    // HexNAc*2+Hex*2
                } else if (Math.abs(mzi - 731.271672) < tolerance) {
                    markpeaks[6] = 1;
                    continue;
                    // HexNAc+Hex*2, N-glyco
                } else if (Math.abs(mzi - 528.192299) < tolerance) {
                    // if(inteni>intenthres2)
                    // return;
                    // HexNAc+Hex*3, N-glyco
                } else if (Math.abs(mzi - 690.245124) < tolerance) {
                    // if(inteni>intenthres2)
                    // return;
                } else {
                    highIntenList.add(peaks[i]);
                    if (peaks[i].getIntensity() > maxinten) {
                        maxinten = peaks[i].getIntensity();
                    }
                    continue;
                }
            }
        }

        if (neuAc[0] + neuAc[1] == 2) {
            markpeaks[0] = 1;
        }
        if (!oglycan && MathTool.getTotal(markpeaks) < 3) {
            return;
        }

        if (highIntenList.size() <= 5)
            return;
        // System.out.println((double)highIntenList.size()/(double)temppeaks.length);
        IPeak[] noglyPeaks = highIntenList.toArray(new IPeak[highIntenList.size()]);

        totalrange += noglyPeaks[noglyPeaks.length - 1].getMz();
        totalscancount++;
        totalpeakcount += noglyPeaks.length;

        IPeak[] noglyPeaksIntenSort = new IPeak[noglyPeaks.length];
        System.arraycopy(noglyPeaks, 0, noglyPeaksIntenSort, 0, noglyPeaks.length);
        Arrays.sort(noglyPeaksIntenSort, new Comparator<IPeak>()
        {

            @Override
            public int compare(IPeak arg0, IPeak arg1)
            {
                // TODO Auto-generated method stub
                if (arg0.getIntensity() < arg1.getIntensity())
                    return 1;
                else if (arg0.getIntensity() > arg1.getIntensity())
                    return -1;
                return 0;
            }

        });

        StringBuilder monoinfo = new StringBuilder();
        for (int i = 0; i < markpeaks.length; i++) {
            monoinfo.append(markpeaks[i]).append("_");
        }

        HashMap<String, OGlycanTypeInfo2> infomap = new HashMap<String, OGlycanTypeInfo2>();
        if (precharge == 0) {
            this.judge(premz, 2, noglyPeaks, noglyPeaksIntenSort, markpeaks, infomap, monoinfo.toString());
            this.judge(premz, 3, noglyPeaks, noglyPeaksIntenSort, markpeaks, infomap, monoinfo.toString());
        } else {
            this.judge(premz, precharge, noglyPeaks, noglyPeaksIntenSort, markpeaks, infomap,
                    monoinfo.toString());
        }

        if (infomap.size() > 0) {

            // HashMap<Double, ArrayList<OGlycanTypeInfo2>> mzmap = new
            // HashMap<Double, ArrayList<OGlycanTypeInfo2>>();
            HashMap<Double, StringBuilder> mzmap2 = new HashMap<Double, StringBuilder>();
            String[] keyArrays = infomap.keySet().toArray(new String[infomap.size()]);
            Arrays.sort(keyArrays);

            for (String uname : keyArrays) {
                OGlycanTypeInfo2 typeinfo = infomap.get(uname);
                int[] findtype = typeinfo.getFindType();
                if (findtype[0] == 0)
                    continue;

                int charge = typeinfo.getCharge();
                int maxcharge = charge > 3 ? 3 : 2;
                double pepmz = Double
                        .parseDouble(df4.format(typeinfo.getMass() / (double) charge + AminoAcidProperty.PROTON_W));
                if (pepmz < 100)
                    continue;
                // System.out.println("495\t"+uname+"\t"+pepmz);

                if (mzmap2.containsKey(pepmz)) {
                    mzmap2.get(pepmz).append("#").append(uname);
                    // mzmap.get(pepmz).add(typeinfo);
                } else {

                    String newname;
                    if (name.startsWith("Locus:1.1.1")) {
                        newname = name + "." + fileid + "." + (mzmap2.size() + 1);
                    } else {
                        newname = "Locus:1.1.1." + scannum + "." + fileid + "." + (mzmap2.size() + 1);
                    }

                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(newname).append("\t");
                    sb2.append(typeinfo.getMass()).append("\t");
                    sb2.append(typeinfo.getMarks()).append("\t");
                    sb2.append(typeinfo.getFindTypeString());
                    sb2.append("#").append(uname);
                    mzmap2.put(pepmz, sb2);

                    // ArrayList<OGlycanTypeInfo2> list = new
                    // ArrayList<OGlycanTypeInfo2>();
                    // list.add(typeinfo);
                    // mzmap.put(pepmz, list);

                    int peakcount = 0;
                    StringBuilder sb = new StringBuilder();
                    sb.append("BEGIN IONS" + lineSeparator);
                    sb.append("PEPMASS=" + df4.format(pepmz) + lineSeparator);
                    sb.append("CHARGE=" + charge + "+" + lineSeparator);
                    // sb.append("PEPMASS=" + df4.format(typeinfo.getMass()+
                    // AminoAcidProperty.PROTON_W) + lineSeparator);
                    // sb.append("CHARGE=1" + "+" + lineSeparator);
                    sb.append("TITLE=" + newname + lineSeparator);

                    L:
                    for (int i = 0; i < noglyPeaks.length; i++) {

                        // boolean use = true;

                        for (int chargeid = 1; chargeid <= maxcharge; chargeid++) {

                            double peakmass = (noglyPeaks[i].getMz() - AminoAcidProperty.PROTON_W) * (double) chargeid;
                            double pepmass = typeinfo.getMass();

                            if (Math.abs(peakmass - pepmass) < tolerance
                                    || Math.abs(peakmass - pepmass - dm) < tolerance) {

                                // use = false;
                                continue L;
                            }

                            // double[] fraglist = typeinfo.getFragments();
                            double[] fraglist = new double[]{203.079373, 365.132198};

                            for (int j = 0; j < fraglist.length; j++) {

                                if (Math.abs(peakmass - (pepmass + fraglist[j])) <= tolerance
                                        || Math.abs(peakmass - (pepmass + fraglist[j] - 18.010565)) <= tolerance) {

                                    // use = false;
                                    continue L;
                                }

                                if (Math.abs(peakmass - (pepmass + fraglist[j]) - dm) <= tolerance
                                        || Math.abs(peakmass - (pepmass + fraglist[j] - 18.010565) - dm) <= tolerance) {

                                    // use = false;
                                    continue L;
                                }
                            }
                        }

                        // if (use) {
                        peakcount++;
                        sb.append(noglyPeaks[i].getMz() + "\t" + noglyPeaks[i].getIntensity() + lineSeparator);
                        // } else {
                        // sb2.append(noglyPeaks[i].getMz() + "\t"
                        // + noglyPeaks[i].getIntensity() + "\t");
                        // }
                    }

                    if (peakcount <= 5)
                        continue;

                    sb.append("END IONS" + lineSeparator);
                    this.scanWriter.write(sb.toString());
                }
            }

            // Iterator<Double> it1 = mzmap.keySet().iterator();
            // while(it1.hasNext()){
            // double pmz = it1.next();
            // ArrayList<OGlycanTypeInfo2> list = mzmap.get(pmz);

            // }

            Iterator<Double> it2 = mzmap2.keySet().iterator();
            while (it2.hasNext()) {
                double pmz = it2.next();
                // System.out.println("574\t"+pmz);
                this.infoWriter.write(mzmap2.get(pmz) + lineSeparator);
            }
        }
    }

    /**
     * @param premz               precursor mz
     * @param precharge           precursor charge
     * @param noglyPeaks          peaks with mz sort in ascending order, oxonium removed
     * @param noglyPeaksIntenSort same as <code>noglyPeaks</code>, except that peaks with intensity sort in descending
     *                            order
     * @param markpeaks           matched mark peaks
     * @param infomap             info map
     * @param monoinfo
     */
    private void judge(double premz, int precharge, IPeak[] noglyPeaks, IPeak[] noglyPeaksIntenSort,
            int[] markpeaks, HashMap<String, OGlycanTypeInfo2> infomap, String monoinfo)
    {
        double mw = (premz - AminoAcidProperty.PROTON_W) * precharge;
        int maxcharge = precharge > 3 ? 3 : 2;
        double firstIntensity = 0;

        for (IPeak aNoglyPeaksIntenSort : noglyPeaksIntenSort) {
            double mzi = aNoglyPeaksIntenSort.getMz();
            double inteni = aNoglyPeaksIntenSort.getIntensity();

            if (mzi < 450)
                continue;

            if (inteni * 10 < firstIntensity)
                break;

            for (int chargeid = 1; chargeid <= maxcharge; chargeid++) {

                double mass = (mzi - AminoAcidProperty.PROTON_W) * chargeid; // mass of current peak

                for (double mz : this.mzs) {
                    double[] toleis = new double[2];
                    toleis[0] = Math.abs(mw - mass - mz);
                    toleis[1] = Math.abs(mw - (mass - 203.079373) - mz);

                    for (int k = 0; k < toleis.length; k++) {
                        if (toleis[k] > tolerance)
                            continue;

                        int[][] unitIds = this.unitmap.get(mz);

                        double pepmass = mw - mz;
                        if (pepmass < 500)
                            continue;

                        for (int[] unitId : unitIds) {
                            OGlycanUnit[] units = new OGlycanUnit[unitId.length];
                            HashSet<Double> fragset = new HashSet<>();

                            StringBuilder sbname = new StringBuilder();
                            for (int id2 = 0; id2 < units.length; id2++) {
                                units[id2] = this.allUnits[unitId[id2]];
                                sbname.append(units[id2].getName()).append("\t");
                                double[] fragments = units[id2].getFragment();

                                for (double fragment : fragments) {
                                    fragset.add(fragment);
                                }
                            }

                            if (!this.validate(markpeaks, units))
                                continue;

                            if (firstIntensity == 0)
                                firstIntensity = inteni;

                            double[] fraglist = new double[fragset.size()];
                            int fragid = 0;
                            for (Double d : fragset) {
                                fraglist[fragid++] = d;
                            }

                            String uname = sbname.substring(0, sbname.length() - 1);

                            if (infomap.containsKey(uname)) {
                                int[] findtype = infomap.get(uname).getFindType();
                                findtype[k] = 1;
                                infomap.get(uname).setFindType(findtype);
                            } else {
                                int[] findtype = new int[5];
                                findtype[k] = 1;
                                OGlycanTypeInfo2 typeinfo = new OGlycanTypeInfo2(pepmass, findtype, fraglist, monoinfo);
                                typeinfo.setCharge(precharge);
                                infomap.put(uname, typeinfo);
                                if (k == 0) {
                                    typeinfo.setExpGlycoMass(mw - mass);
                                } else if (k == 1) {
                                    typeinfo.setExpGlycoMass(mw - (mass - 203.079373));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void denoise(MS2Scan ms2scan, int fileid)
    {
        IMS2PeakList peaklist = ms2scan.getPeakList();
        PrecursePeak pp = peaklist.getPrecursePeak();
        double premz = pp.getMz();
        int precharge = pp.getCharge();

        String name = ms2scan.getScanName().getScanName();
        if (name.endsWith(", "))
            name = name.substring(0, name.length() - 2);

        double percent = 0;
        double intenthres1 = 0;
        double intenthres2 = 0;

        IPeak[] temppeaks = peaklist.getPeaksSortByIntensity();
        if (temppeaks.length > 100) {
            if (temppeaks.length >= 400) {
                percent = 1;
            } else if (temppeaks.length < 400 && temppeaks.length >= 300) {
                percent = 0.75;
            } else if (temppeaks.length < 300 && temppeaks.length >= 200) {
                percent = 0.5;
            } else if (temppeaks.length < 200 && temppeaks.length >= 100) {
                percent = 0.25;
            }

            ArrayList<Double> tempintenlist = new ArrayList<Double>();
            double[] rsdlist = new double[temppeaks.length];

            for (int i = temppeaks.length - 1; i >= 0; i--) {
                tempintenlist.add(temppeaks[i].getIntensity());
                rsdlist[tempintenlist.size() - 1] = MathTool.getRSDInDouble(tempintenlist);
            }

            for (int i = 0; i < rsdlist.length; i++) {
                if (rsdlist[i] > 0.5) {
                    intenthres1 = temppeaks[temppeaks.length - i].getIntensity();
                }
                if (rsdlist[i] > percent) {
                    intenthres2 = temppeaks[temppeaks.length - i].getIntensity() / 5.0;
                }
                if (intenthres1 > 0 && intenthres2 > 0) {
                    break;
                }
            }

            if (intenthres2 > intenthres1) {
                intenthres2 = intenthres1;
            }
        }

        // System.out.println(temppeaks.length+"\t"+percent+"\t"+intenthres1+"\t"+intenthres2);

        IPeak[] peaks = peaklist.getPeakArray();
        Arrays.sort(peaks);

        /*
         * double maxinten = 0; int[] markpeaks = new int[7]; int[] neuAc = new
         * int[2]; ArrayList<IPeak> highIntenList = new ArrayList<IPeak>();
         *
         * boolean oglycan = false;
         *
         * for (int i = 0; i < peaks.length; i++) {
         *
         * double mzi = peaks[i].getMz(); double inteni =
         * peaks[i].getIntensity();
         *
         * if (inteni < intenthres1) { if(inteni > intenthres2)
         * highIntenList.add(peaks[i]); } else { if (Math.abs(mzi - 163.060101)
         * < tolerance) { continue; } else if (Math.abs(mzi - 204.086649) <
         * tolerance) { oglycan = true; continue; } else if (Math.abs(mzi -
         * 274.087412635) < tolerance) { neuAc[0] = 1; continue; } else if
         * (Math.abs(mzi - 292.102692635) < tolerance) { neuAc[1] = 1; continue;
         * } else if (Math.abs(mzi - 366.139472) < tolerance) { markpeaks[1] =
         * 1; continue; // HexNAc+NeuAc } else if (Math.abs(mzi - 495.18269) <
         * tolerance) { markpeaks[2] = 1; continue; // NeuAc*2 } else if
         * (Math.abs(mzi - 583.19873) < tolerance) { markpeaks[3] = 1; continue;
         * // HexNAc*2 } else if (Math.abs(mzi - 407.16665) < tolerance) {
         * markpeaks[4] = 1; continue; // HexNAc*2+Hex } else if (Math.abs(mzi -
         * 569.218847) < tolerance) { markpeaks[5] = 1; continue; //
         * HexNAc*2+Hex*2 } else if (Math.abs(mzi - 731.271672) < tolerance) {
         * markpeaks[6] = 1; continue; // HexNAc+Hex*2, N-glyco } else if
         * (Math.abs(mzi - 528.192299) < tolerance) { // if(inteni>intenthres2)
         * // return; // HexNAc+Hex*3, N-glyco } else if (Math.abs(mzi -
         * 690.245124) < tolerance) { // if(inteni>intenthres2) // return; }
         * else { highIntenList.add(peaks[i]); if (peaks[i].getIntensity() >
         * maxinten) { maxinten = peaks[i].getIntensity(); } continue; } } }
         *
         * if(neuAc[0]+neuAc[1]==2){ markpeaks[0] = 1; } if (!oglycan &&
         * MathTool.getTotal(markpeaks) < 3) { return; }
         *
         * if (highIntenList.size() <= 5) return;
         */
        String newname = name + "." + fileid;
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN IONS" + lineSeparator);
        sb.append("PEPMASS=" + df4.format(premz) + lineSeparator);
        if (precharge > 0)
            sb.append("CHARGE=" + precharge + "+" + lineSeparator);
        sb.append("TITLE=" + newname + lineSeparator);
        for (IPeak peak : peaks) {
            if (peak.getIntensity() > intenthres2)
                sb.append(peak.getMz() + "\t" + peak.getIntensity() + lineSeparator);
        }
        sb.append("END IONS" + lineSeparator);
        this.scanWriter.write(sb.toString());
    }

    private boolean validate(int[] marks, OGlycanUnit[] unit)
    {
        int[] possible = new int[5];
        boolean core2 = false;
        for (OGlycanUnit anUnit : unit) {
            int[] composition = anUnit.getCompCount();
            for (int j = 0; j < composition.length; j++) {
                possible[j] += composition[j];
            }
            if (anUnit.getID() >= 8) {
                core2 = true;
            }
        }

        if (marks[0] > 0) { // test neuAc
            if (possible[2] == 0)
                return false;
        }
        if (marks[4] + marks[5] + marks[6] > 0) {
            return core2;
        }
        return true;
    }

    private boolean validate2(int findType, int[] marks, OGlycanUnit[] unit)
    {
        int[] typeCount = new int[10];
        for (OGlycanUnit anUnit : unit) {
            if (anUnit == OGlycanUnit.core1_1) {
                typeCount[0]++;
            } else if (anUnit == OGlycanUnit.core1_2) {
                typeCount[1]++;
            } else if (anUnit == OGlycanUnit.core1_3) {
                typeCount[2]++;
            } else if (anUnit == OGlycanUnit.core1_4) {
                typeCount[3]++;
            } else if (anUnit == OGlycanUnit.core1_5) {
                typeCount[4]++;
            } else if (anUnit == OGlycanUnit.core2_1) {
                typeCount[5]++;
            } else if (anUnit == OGlycanUnit.core2_2) {
                typeCount[6]++;
            } else if (anUnit == OGlycanUnit.core2_3) {
                typeCount[7]++;
            } else if (anUnit == OGlycanUnit.core2_4) {
                typeCount[8]++;
            } else if (anUnit == OGlycanUnit.core2_5) {
                typeCount[9]++;
            }
        }

        boolean validate = false;
        if (findType == 2) {
            for (int i = 0; i < typeCount.length; i++) {
                if (i != 0 && i != 2 && typeCount[i] > 0) {
                    validate = true;
                    break;
                }
            }
        } else {
            validate = true;
        }

        if (!validate)
            return false;
        // System.out.println(Arrays.toString(typeCount));
        int[] possible = new int[3];
        for (int i = 0; i < unit.length; i++) {
            int[] composition = unit[i].getCompCount();
            for (int j = 0; j < composition.length; j++) {
                possible[j] += composition[j];
            }
        }

        for (int i = 0; i < marks.length; i++) {
            if (marks[i] == 0)
                continue;

            switch (i) {
                case 0:
                    if (possible[2] == 0)
                        return false;
                    break;
                case 1:
                    if (possible[1] == 0)
                        return false;
                    break;
                case 2:
                    if (typeCount[3] == 0 && typeCount[4] == 0)
                        return false;
                    break;
                case 3:
                    if (typeCount[4] == 0)
                        return false;
                    break;
                case 4:
                    if (typeCount[5] == 0 && typeCount[6] == 0 && typeCount[7] == 0 && typeCount[8] == 0
                            && typeCount[9] == 0)
                        return false;
                    break;
                case 5:
                    if (typeCount[5] == 0 && typeCount[6] == 0 && typeCount[7] == 0 && typeCount[8] == 0
                            && typeCount[9] == 0)
                        return false;
                    break;
                case 6:
                    if (typeCount[7] == 0 && typeCount[8] == 0 && typeCount[9] == 0)
                        return false;
                    break;
            }
        }

        return true;
    }
}
