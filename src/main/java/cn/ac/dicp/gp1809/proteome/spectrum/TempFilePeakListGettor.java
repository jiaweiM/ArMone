/*
 ******************************************************************************
 * File: TempFilePeakListGettor.java * * * Created on 02-25-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.Description;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MS1Scan;
import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

/**
 * To enable the fast access of large number of spectra files, the peak lists are first printed to a temporary file, and
 * then they can be get by this class.
 *
 * @author Xinning
 * @version 0.1.1, 08-11-2009, 13:13:36
 */
public class TempFilePeakListGettor
{
    private HashMap<Integer, Integer> indexmap;

    private DataOutputStream ostream;

    private File file;
    private ByteBuffer mappedBuffer;
    //For clean up
    private FileInputStream bufferStream;
    private FileChannel bufferChannel;

    public TempFilePeakListGettor() throws FileNotFoundException
    {
        this(new File(new File(System.getProperty("java.io.tmpdir")),
                new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())));
    }

    public TempFilePeakListGettor(String tempfile) throws FileNotFoundException
    {
        this(new File(tempfile));
    }

    public TempFilePeakListGettor(File tempfile) throws FileNotFoundException
    {
        this.file = tempfile;
        this.file.deleteOnExit();

        this.indexmap = new HashMap<Integer, Integer>();
        ostream = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(tempfile)));
    }

    /**
     * Add the peaklist for the current scan number
     *
     * @param scan_num
     * @param peaklist
     */
    public void addPeakList(int level, int scan_num, IPeakList peaklist)
    {
        try {
/*			
			if (this.indexmap.get(scan_num) != null)
				System.err
				        .println("Duplicated peak lists with the same scan number "
				                + scan_num
				                + ". The first one will be overrided.");
*/
            int start = this.writeToStream(level, peaklist);
            this.indexmap.put(scan_num, start);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * The method must be invoked when the adding of peaklist has been finished.
     */
    public void finishedAdding()
    {
        try {
            this.ostream.close();
            this.ostream = null;

            System.gc();

            this.bufferStream = new FileInputStream(this.file);
            this.bufferChannel = this.bufferStream.getChannel();
            //map for reading

// The reason of map failed			
//			this.mappedBuffer = this.bufferChannel.map(
//			        FileChannel.MapMode.READ_ONLY, 0, file.length());


            int readSize = 128 * 1024 * 1024; // mirror size
            long s = 0;// mirror start
            long e = 0;// mirror end
            int i = 0;
            long allSize = bufferChannel.size();
            System.out.println("temp 111\t" + allSize);
            this.mappedBuffer = ByteBuffer.allocateDirect((int) allSize);
            while (true) {
                s = i++ * readSize;
                e = s + readSize > allSize ? allSize % readSize : readSize;

                ByteBuffer bb = this.bufferChannel.map(
                        FileChannel.MapMode.READ_ONLY, s, e);

                this.mappedBuffer.put(bb);
                if ((s + readSize) >= allSize)
                    break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int writeToStream(int level, IPeakList peaklist) throws IOException
    {
        if (ostream == null)
            throw new NullPointerException(
                    "The temporary file has closed for writing a new peak list.");

        int start = ostream.size();
        //The number of peaks
        ostream.writeInt(level);
        ostream.writeInt(peaklist.size());

        //write the precursor ions
        if (level != 1) {
            PrecursePeak ppeak = ((IMS2PeakList) peaklist).getPrecursePeak();
            ostream.writeDouble(ppeak.getMz());
            ostream.writeDouble(ppeak.getIntensity());
            ostream.writeShort(ppeak.getCharge());
        }

        //write the peaks
        IPeak[] peaks = peaklist.getPeakArray();
        double min = Double.MAX_VALUE;
        for (IPeak peak : peaks) {
            if (peak.getIntensity() < min) {
                min = peak.getIntensity();
            }
        }

        for (IPeak peak : peaks) {
            if (peak.getIntensity() > min) {
                ostream.writeDouble(peak.getMz());
                ostream.writeDouble(peak.getIntensity() - min);
            }
        }

        return start;
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.ac.dicp.gp1809.proteome.rawdata.IPeakListGettor#getPeakList (int)
     */
    public IPeakList getPeakList(int scan_num)
    {

        if (this.mappedBuffer == null) {
            this.finishedAdding();
        }

        Integer start = this.indexmap.get(scan_num);

        if (start == null) {
            return null;
        }

        this.mappedBuffer.position(start);

        int level = this.mappedBuffer.getInt();
        int peaknum = this.mappedBuffer.getInt();
        IPeakList peaklist = null;
        if (level == 1) {

            peaklist = new MS1PeakList(peaknum);

        } else {

            peaklist = new MS2PeakList(peaknum);
            double mz = mappedBuffer.getDouble();
            double intense = mappedBuffer.getDouble();
            short charge = mappedBuffer.getShort();
            PrecursePeak ppeak = new PrecursePeak(mz, intense);
            ppeak.setCharge(charge);
            ((MS2PeakList) peaklist).setPrecursePeak(ppeak);
        }

        //The precursor ion
        /*
         */
        for (int i = 0; i < peaknum; i++) {
            IPeak peak = new Peak(mappedBuffer.getDouble(), mappedBuffer
                    .getDouble());
            peaklist.add(peak);
        }

        return peaklist;
    }

    public IPeakList getNextPeakList(int scan_num)
    {

        if (this.mappedBuffer == null) {
            this.finishedAdding();
        }

        Integer start = null;
        while (true) {
            if (this.indexmap.containsKey(scan_num)) {
                start = this.indexmap.get(scan_num);
                break;
            } else {
                scan_num++;
            }
        }

        if (start == null) {
            return null;
        }

        this.mappedBuffer.position(start);

        int level = this.mappedBuffer.getInt();
        int peaknum = this.mappedBuffer.getInt();
        MS1PeakList peaklist = new MS1PeakList(peaknum);

        for (int i = 0; i < peaknum; i++) {
            IPeak peak = new Peak(mappedBuffer.getDouble(), mappedBuffer
                    .getDouble());
            peaklist.add(peak);
        }

        return peaklist;
    }

    public MS1Scan getNextMS1Scan(int scan_num)
    {

        if (this.mappedBuffer == null) {
            this.finishedAdding();
        }

        Integer start = null;
        while (true) {
            if (this.indexmap.containsKey(scan_num)) {
                start = this.indexmap.get(scan_num);
                break;
            } else {
                scan_num++;
            }
        }

        if (start == null) {
            return null;
        }

        this.mappedBuffer.position(start);

        int level = this.mappedBuffer.getInt();
        int peaknum = this.mappedBuffer.getInt();
        MS1PeakList peaklist = new MS1PeakList(peaknum);
        Description des = new Description(scan_num, 1, 0, 0);

        for (int i = 0; i < peaknum; i++) {
            IPeak peak = new Peak(mappedBuffer.getDouble(), mappedBuffer
                    .getDouble());
            peaklist.add(peak);
        }

        MS1Scan scan = new MS1Scan(des, peaklist);
        return scan;
    }

    public IPeakList getPrevPeakList(int scan_num)
    {

        if (this.mappedBuffer == null) {
            this.finishedAdding();
        }

        Integer start = null;
        while (true) {
            if (this.indexmap.containsKey(scan_num)) {
                start = this.indexmap.get(scan_num);
                break;
            } else {
                scan_num--;
            }
        }

        if (start == null) {
            return null;
        }

        this.mappedBuffer.position(start);

        int level = this.mappedBuffer.getInt();
        int peaknum = this.mappedBuffer.getInt();
        MS1PeakList peaklist = new MS1PeakList(peaknum);

        for (int i = 0; i < peaknum; i++) {
            IPeak peak = new Peak(mappedBuffer.getDouble(), mappedBuffer
                    .getDouble());
            peaklist.add(peak);
        }

        return peaklist;
    }

    public MS1Scan getPrevMS1Scan(int scan_num)
    {

        if (this.mappedBuffer == null) {
            this.finishedAdding();
        }

        Integer start = null;
        while (true) {
            if (this.indexmap.containsKey(scan_num)) {
                start = this.indexmap.get(scan_num);
                break;
            } else {
                scan_num--;
            }
        }

        if (start == null) {
            return null;
        }

        this.mappedBuffer.position(start);

        int level = this.mappedBuffer.getInt();
        int peaknum = this.mappedBuffer.getInt();
        MS1PeakList peaklist = new MS1PeakList(peaknum);
        Description des = new Description(scan_num, 1, 0, 0);

        for (int i = 0; i < peaknum; i++) {
            IPeak peak = new Peak(mappedBuffer.getDouble(), mappedBuffer
                    .getDouble());
            peaklist.add(peak);
        }

        MS1Scan scan = new MS1Scan(des, peaklist);
        return scan;
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.ac.dicp.gp1809.proteome.rawdata.IPeakListGettor#dispose()
     */
    public void dispose()
    {
        try {

            this.indexmap = null;
//			this.mappedBuffer.force();
            this.mappedBuffer = null;

            this.bufferChannel.close();
            this.bufferStream.close();

            System.gc();

            FileUtil.deleteDelay(this.file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
