/*
 ******************************************************************************
 * File: Base64Peak.java * * * Created on 11-21-2007
 *
 * Copyright (c) 2007 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.rawdata;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.MS1PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import org.apache.commons.codec.binary.Base64;


/**
 * Utilities for decode the peaklist which is encoded by base64
 *
 * @author Xinning
 * @version 0.1.1, 04-22-2010, 23:31:30
 */
public class Base64Peak
{
    /**
     * Decode peaklist from a base64 string in mzdata file
     *
     * @return the peak list in this scan
     */
    public static IMS2PeakList decodeMS2(String base64mz, String base64inten, int expectedlen,
            int precison, boolean isBigEndian, PrecursePeak ppeak)
    {

        /*
         * At least one peak in the list, set as [0,0]
         */
        if (expectedlen == 0) {
            IMS2PeakList peaks = new MS2PeakList(1);
            peaks.setPrecursePeak(ppeak);
            peaks.add(new Peak(0, 0));
            return peaks;
        }

        boolean isDouble = precison == 64 ? true : false;

        byte[] mzs = Base64.decodeBase64(base64mz);
        if (mzs == null)
            throw new RuntimeException("Eorror in decoding base64 MZ string: " + base64mz + " \nReturn null!");
        byte[] intens = Base64.decodeBase64(base64inten);
        if (intens == null)
            throw new RuntimeException("Eorror in decoding base64 intensity string: " + base64inten + " \nReturn null!");

        double[] mzarr = decode(mzs, expectedlen, isDouble, isBigEndian);
        double[] intenarr = decode(intens, expectedlen, isDouble, isBigEndian);
        IMS2PeakList peaks = new MS2PeakList(expectedlen);
        for (int i = 0; i < expectedlen; i++) {
            peaks.add(new Peak(mzarr[i], intenarr[i]));
        }
        peaks.setPrecursePeak(ppeak);
        return peaks;
    }

    /**
     * Decode peaklist from a base64 string in mzdata file
     *
     * @return the peak list in this scan
     */
    public static IPeakList decodeMS1(String base64mz, String base64inten, int expectedlen,
            int precison, boolean isBigEndian)
    {

        /*
         * At least one peak in the list, set as [0,0]
         */
        if (expectedlen == 0) {
            IPeakList peaks = new MS1PeakList(1);
            peaks.add(new Peak(0, 0));
            return peaks;
        }

        boolean isDouble = precison == 64 ? true : false;

        byte[] mzs = Base64.decodeBase64(base64mz);
        if (mzs == null)
            throw new RuntimeException("Eorror in decoding base64 MZ string: " + base64mz + " \nReturn null!");
        byte[] intens = Base64.decodeBase64(base64inten);
        if (intens == null)
            throw new RuntimeException("Eorror in decoding base64 intensity string: " + base64inten + " \nReturn null!");

        double[] mzarr = decode(mzs, expectedlen, isDouble, isBigEndian);
        double[] intenarr = decode(intens, expectedlen, isDouble, isBigEndian);

        IPeakList peaks = new MS1PeakList(expectedlen);
        for (int i = 0; i < expectedlen; i++) {
            peaks.add(new Peak(mzarr[i], intenarr[i]));
        }

        return peaks;
    }

    /**
     * Decode peaklist from a base64 string in mzxml file
     *
     * @return the peak list in this scan
     */
    public static IMS2PeakList decode(String base64, int expectedlen,
            int precison, boolean isBigEndian, double precursorMZ, double precursorinten)
    {

        if (expectedlen == 0) {
            IMS2PeakList peaks = new MS2PeakList(1);
            peaks.setPrecursePeak(new PrecursePeak(precursorMZ, precursorinten));
            peaks.add(new Peak(0, 0));
            return peaks;
        }

        boolean isDouble = precison == 64 ? true : false;

        byte[] mzs = Base64.decodeBase64(base64);
        if (mzs == null)
            throw new RuntimeException("Eorror in decoding base64 peak string: " + base64 + " \nReturn null!");

        double[] mzarr = decode(mzs, expectedlen, isDouble, isBigEndian);
        IMS2PeakList peaks = new MS2PeakList(expectedlen);
        for (int i = 0; i < expectedlen; i += 2) {
            peaks.add(new Peak(mzarr[i], mzarr[i + 1]));
        }
        peaks.setPrecursePeak(new PrecursePeak(precursorMZ, precursorinten));
        return peaks;
    }

    private static double[] decode(byte[] databytes, int expectedlen,
            boolean isDoublePrecise, boolean isBigEndian)
    {
        double[] darr = new double[expectedlen];
        int len = databytes.length;
        ByteBuffer orderedbuf = ByteBuffer.wrap(databytes);
        //default is big
        if (!isBigEndian)
            orderedbuf.order(ByteOrder.LITTLE_ENDIAN);

        if (isDoublePrecise) {
            if (len % 8 != 0) {
                throw new RuntimeException("Byte data array has length " + len + " which isn't a multiple of 8");
            }

            if (len / 8 != expectedlen) {
                throw new RuntimeException("Expected data length of " + expectedlen + " but got actual length of " + len + "/8");
            }

            for (int i = 0; i < expectedlen; i++) {
                darr[i] = orderedbuf.getDouble();
            }
        } else {
            if (len % 4 != 0) {
                throw new RuntimeException("Byte data array has length " + len + " which isn't a multiple of 4");
            }

            if (len / 4 != expectedlen) {
                throw new RuntimeException("Expected data length of " + expectedlen + " but got actual length of " + len + "/4");
            }

            for (int i = 0; i < expectedlen; i++) {
                darr[i] = orderedbuf.getFloat();
            }
        }
        return darr;
    }
}
