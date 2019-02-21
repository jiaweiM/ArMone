/*
 ******************************************************************************
 * File: PeptideListCreatorTask.java * * * Created on 05-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.*;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.RawSpectraReaderFactory;
import cn.ac.dicp.gp1809.util.DecimalFormats;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Create a ppl file
 *
 * @author Xinning
 * @version 0.1.1, 05-20-2010, 18:04:12
 */
public class PeptideListCreatorTask implements IPplCreationTask
{
    private static final DecimalFormat df4 = DecimalFormats.DF0_4;
    private IRawSpectraReader rawReader;
    private IPeptideReader reader;
    private IPeptideWriter pwriter;
    private boolean isPair;
    private IPeptide peptide;
    private boolean hasNext = true;
    private boolean closed = false;
    private boolean end = false;
    // key is scan number begin
    private HashMap<Integer, HashSet<IPeptide>> pepMap;
    private HashMap<String, HashSet<ProteinReference>> refMap;
    private HashMap<String, HashMap<String, SeqLocAround>> seqLocMap;

    public PeptideListCreatorTask(String output, String input, int topn,
            String database, PeptideType type, DtaType dtaType, String dtaPath,
            boolean uniq_charge, IDecoyReferenceJudger judger) throws IOException, ReaderGenerateException,
            ImpactReaderTypeException, FastaDataBaseException, DtaFileParsingException
    {
        IRawSpectraReader rawReader = null;
        try {
            rawReader = RawSpectraReaderFactory.createReader(dtaType, dtaPath);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        this.pepMap = new HashMap<>();
        this.refMap = new HashMap<>();
        this.seqLocMap = new HashMap<>();
        this.rawReader = rawReader;

        reader = PeptideReaderFactory.createRawReader(input, database, type, judger);
        reader.setTopN(topn);

        ISearchParameter parameter = reader.getSearchParameter();
        pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
                parameter, judger, uniq_charge, reader.getProNameAccesser());

        isPair = reader.getPeptideType().isPeptidePair();
    }

    /**
     * For mascot
     *
     * @param output          output path
     * @param input           input path
     * @param topn            top N
     * @param database        database
     * @param accession_regex protein recognize regex
     * @param dtaType
     * @param dtaPath
     * @throws ImpactReaderTypeException
     * @throws ReaderGenerateException
     * @throws IOException
     * @throws DtaFileParsingException
     */
    public PeptideListCreatorTask(String output, String input, int topn,
            String database, String accession_regex,
            DtaType dtaType, String dtaPath, boolean uniq_charge,
            IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException,
            IOException, DtaFileParsingException
    {
        IRawSpectraReader rawReader;
        try {
            rawReader = RawSpectraReaderFactory.createReader(dtaType, dtaPath);
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        this.pepMap = new HashMap<>();
        this.refMap = new HashMap<>();
        this.seqLocMap = new HashMap<>();
        this.rawReader = rawReader;

        reader = PeptideReaderFactory.createRawReader4Mascot(input, database, accession_regex, judger);
        reader.setTopN(topn);

        ISearchParameter parameter = reader.getSearchParameter();
        pwriter = new PeptideListWriter(output, reader.getPeptideFormat(),
                parameter, judger, uniq_charge, reader.getProNameAccesser());

        isPair = reader.getPeptideType().isPeptidePair();
    }

    public static void batchWrite(String in, String database, String accession_regex, IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException, DtaFileParsingException, IOException
    {
        File[] files = (new File(in)).listFiles(arg0 -> arg0.getName().endsWith("csv"));

        for (File file : files) {
            String path = file.getAbsolutePath();
            String name = file.getName();
            String mgf = path.replace("csv", "mgf");

            String out = path.replace("csv", "ppl");
            PeptideListCreatorTask task = new PeptideListCreatorTask(out, path, 1, database, accession_regex,
                    DtaType.MGF, mgf, false, judger);
            while (task.hasNext()) {
                task.processNext();
            }
            task.dispose();
            System.gc();
        }
    }

    public static void batchWrite(String ppls, String mgfs, String database, String accession_regex,
            IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException, DtaFileParsingException,
            IOException
    {
        File[] files = (new File(ppls)).listFiles(arg0 -> arg0.getName().endsWith("csv"));

        for (File file : files) {
            String path = file.getAbsolutePath();
            String name = file.getName();
            String mgf = path.replace("csv", "mgf");
            String[] ss = name.split("_");
            String subname = "";
            for (String s : ss) {
                if (s.startsWith("F")) {
                    subname = s;
                    break;
                }
            }
            String out = path.replace("csv", "ppl");
            PeptideListCreatorTask task = new PeptideListCreatorTask(out, path, 1, database, accession_regex,
                    DtaType.MGF, mgf, false, judger);
            while (task.hasNext()) {
                task.processNext();
            }
            task.dispose();
            System.gc();
        }
    }

    public static void batchWriteXML(String in, String database, String accession_regex, IDecoyReferenceJudger judger)
            throws ReaderGenerateException, ImpactReaderTypeException, DtaFileParsingException,
            IOException
    {
        File[] files = (new File(in)).listFiles();
        HashMap<String, String> csvmap = new HashMap<String, String>();
        HashMap<String, String> peakmap = new HashMap<String, String>();

        for (File file : files) {
            String path = file.getAbsolutePath();
            String name = file.getName();

            if (name.endsWith("csv")) {
//				name = name.substring(0, name.lastIndexOf("_"));
                name = name.substring(0, name.length() - 12);
                csvmap.put(name, path);
            } else if (name.endsWith("mzXML")) {
//				name = name.substring(name.lastIndexOf("_")+1, name.lastIndexOf("-"));
//				name = name.substring(0, name.lastIndexOf("."));
                name = name.substring(0, name.length() - 6);
                peakmap.put(name, path);
            }
        }

        for (String key : csvmap.keySet()) {
            if (peakmap.containsKey(key)) {
                String path = csvmap.get(key);
                String mzxml = peakmap.get(key);
                String out = path.replace("csv", "ppl");
                System.out.println(key);
                PeptideListCreatorTask task = new PeptideListCreatorTask(out, path, 1, database, accession_regex,
                        DtaType.MZXML, mzxml, false, judger);
                while (task.hasNext()) {
                    task.processNext();
                }
                task.dispose();
            }
        }
    }

    public static void main(
            String args[]) throws ImpactReaderTypeException, DtaFileParsingException, ReaderGenerateException, IOException
    {
        long beg = System.currentTimeMillis();

        String input = "Z:\\MaShujuan\\QE20180712\\D29_RESUM_20180711_1.csv";
        String mgf = "Z:\\MaShujuan\\QE20180712\\D29_RESUM_20180711_1.mgf";

        String database = "Z:\\MaShujuan\\QEcanshu\\human_reviewed_20180517_msj.fasta";
        String accession_regex = ">..\\|\\([^|]*\\)";

        PeptideListCreatorTask task = new PeptideListCreatorTask(args[1], args[0], 1,
                args[3], accession_regex, DtaType.MGF, args[2], false, new DefaultDecoyRefJudger());

        while (task.hasNext()) {
            task.processNext();
        }

        task.dispose();
        long end = System.currentTimeMillis();
        System.out.println((end - beg) / 1000 + " s");

    }

    @Override
    public float completedPercent()
    {
        return 0;
    }

    @Override
    public boolean hasNext()
    {
        try {
            boolean has = (peptide = this.reader.getPeptide()) != null;

            if (has) {
                return true;
            } else {
                if (!this.end) {
                    this.end = true;
                    return true;
                } else
                    return false;
            }
        } catch (PeptideParsingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processNext()
    {
        if (end) {
            IMS2Scan scan;
            while ((scan = this.rawReader.getNextMS2Scan()) != null) {

                int scannum = scan.getScanNum();

                if (pepMap.containsKey(scannum)) {

                    HashSet<IPeptide> pepset = pepMap.get(scannum);
                    for (IPeptide peptide : pepset) {
                        String key = PeptideUtil.getUniqueSequence(peptide.getSequence());

                        peptide.setProteinReference(this.refMap.get(key));
                        peptide.setPepLocAroundMap(this.seqLocMap.get(key));

                        IMS2PeakList peaklist = scan.getPeakList();

                        if (peptide.getRetentionTime() <= 0) {
                            peptide.setRetentionTime(Double.parseDouble(df4.format(peaklist.getPrecursePeak().getRT())));
                        }

                        if (peptide.getInten() <= 0) {
                            peptide.setInten(Double.parseDouble(df4.format(peaklist.getPrecursePeak().getIntensity())));
                        }

                        pwriter.write(peptide, new IMS2PeakList[]{peaklist});
                    }
                }
            }

        } else {

            if (peptide != null) {

                String key = PeptideUtil.getUniqueSequence(peptide.getSequence());

                if (refMap.containsKey(key)) {

                    HashSet<ProteinReference> refset = refMap.get(key);
                    HashMap<String, SeqLocAround> locMap = seqLocMap.get(key);

                    refset.addAll(peptide.getProteinReferences());
                    locMap.putAll(peptide.getPepLocAroundMap());

                } else {

                    HashSet<ProteinReference> refset = new HashSet<ProteinReference>();
                    HashMap<String, SeqLocAround> locMap = new HashMap<String, SeqLocAround>();

                    refset.addAll(peptide.getProteinReferences());
                    locMap.putAll(peptide.getPepLocAroundMap());

                    refMap.put(key, refset);
                    seqLocMap.put(key, locMap);
                }

                if (pepMap.containsKey(peptide.getScanNumBeg())) {

                    pepMap.get(peptide.getScanNumBeg()).add(peptide);

                } else {

                    HashSet<IPeptide> pepset = new HashSet<IPeptide>();
                    pepset.add(peptide);
                    pepMap.put(peptide.getScanNumBeg(), pepset);
                }
            }
        }
    }

    private MS2PeakList getPeakList(int scan)
    {
        MS2PeakList peaklist = (MS2PeakList) rawReader.getMS2PeakList(scan);

        if (peaklist == null) {
            System.err.println("Cannot find the peak list for scan " + scan
                    + " in the raw file.");
        }

        return peaklist;
    }


    @Override
    public boolean inDetermineable()
    {
        return true;
    }


    @Override
    public void dispose()
    {
        if (!this.closed) {
            if (this.rawReader != null)
                this.rawReader.close();

            if (this.reader != null)
                this.reader.close();

            if (this.pwriter != null)
                try {
                    this.pwriter.close();
                } catch (ProWriterException e) {
                    throw new RuntimeException(e);
                }

            this.closed = true;
            System.gc();
        }
    }

    @Override
    public String toString()
    {
        return this.pwriter.toString();
    }

}
