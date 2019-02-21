/*
 ******************************************************************************
 * File: OGlycanScanInfo2.java * * * Created on 2013-12-12
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.oglycan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import jxl.JXLException;

import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.math.MathTool;

/**
 * @author ck
 * @version 2013-12-12, 20:45:02
 */
public class OGlycanScanInfo2
{
    private int type;
    private int count;
    private String scanname;
    /**
     * present of markers
     */
    private int[] markpeaks;
    /**
     * composition
     */
    private int[] findType;
    private double pepMw;
    /**
     * possible GlycanUnit combinations
     */
    private OGlycanUnit[][] units;
    private int[] typecount;

    public OGlycanScanInfo2(int type, int count, int[] markpeaks)
    {
        this.type = type;
        this.count = count;
        this.markpeaks = markpeaks;
    }

    /**
     * Construct with the line in the ".info" file
     *
     * @param line a line
     */
    public OGlycanScanInfo2(String line)
    {
        String[] cs = line.split("#");

        String[] cs0 = cs[0].split("\t");
        this.scanname = cs0[0];
        this.pepMw = Double.parseDouble(cs0[1]);
        String[] ps = cs0[2].split("_");
        this.markpeaks = new int[ps.length];
        for (int i = 0; i < markpeaks.length; i++) {
            markpeaks[i] = Integer.parseInt(ps[i]);
        }

        String[] ps2 = cs0[3].split("_");
        this.findType = new int[ps2.length];
        for (int i = 0; i < findType.length; i++) {
            findType[i] = Integer.parseInt(ps2[i]);
        }

        OGlycanUnit[][] units = new OGlycanUnit[cs.length - 1][];
        for (int i = 0; i < units.length; i++) {
            String[] cs1 = cs[i + 1].split("\t");
            units[i] = new OGlycanUnit[cs1.length];
            for (int j = 0; j < units[i].length; j++) {
                units[i][j] = OGlycanUnit.valueOf(cs1[j]);
            }
        }

        this.units = units;
    }

    public static void read(String in) throws IOException
    {
        int total = 0;
        int[] findType = new int[5];
        int[] counts = new int[4];
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info2 = new OGlycanScanInfo2(line);
            total++;
            int[] ft = info2.findType;
            for (int i = 0; i < findType.length; i++) {
                findType[i] += ft[i];
            }
            if (ft[0] == 1) {
                if (ft[1] == 1) {
                    counts[0]++;
                } else {
                    counts[1]++;
                }
            } else {
                if (MathTool.getTotal(ft) > 1) {
                    counts[2]++;
                } else {
                    counts[3]++;
                }
            }
        }
        reader.close();
        System.out.println("\t" + findType[0] + "\t" + findType[1] + "\t" + findType[2] + "\t" + findType[3] + "\t" + findType[4]);
        System.out.println(total + "\t" + (double) findType[0] / (double) total + "\t" + (double) findType[1] / (double) total + "\t" + (double) findType[2] / (double) total
                + "\t" + (double) findType[3] / (double) total + "\t" + (double) findType[4] / (double) total);
        System.out.println(counts[0] + "\t" + counts[1] + "\t" + counts[2] + "\t" + counts[3]);
    }

    public static void read(String in, String result) throws IOException, JXLException
    {
        ExcelReader er = new ExcelReader(result);
        HashSet<String> set = new HashSet<String>();
        String[] cs = er.readLine();
        while ((cs = er.readLine()) != null) {
            set.add(cs[0]);
        }
        er.close();

        int total = 0;
        int[] findType = new int[5];
        BufferedReader reader = new BufferedReader(new FileReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
            OGlycanScanInfo2 info2 = new OGlycanScanInfo2(line);
            if (set.contains(info2.getScanname())) {
                total++;
                int[] ft = info2.findType;
                for (int i = 0; i < findType.length; i++) {
                    findType[i] += ft[i];
                }
            }
        }
        reader.close();
        System.out.println("\t" + findType[0] + "\t" + findType[1] + "\t" + findType[2] + "\t" + findType[3] + "\t" + findType[4]);
        System.out.println(total + "\t" + (double) findType[0] / (double) total + "\t" + (double) findType[1] / (double) total + "\t" + (double) findType[2] / (double) total
                + "\t" + (double) findType[3] / (double) total + "\t" + (double) findType[4] / (double) total);
    }

    /**
     * @param args
     * @throws IOException
     * @throws JXLException
     */
    public static void main(String[] args) throws IOException, JXLException
    {
        // TODO Auto-generated method stub

        OGlycanScanInfo2.read("H:\\OGlycan_final_20131212\\trypsin_20131219\\trypsin_20131219.info");
//		OGlycanScanInfo2.read("H:\\OGlycan_final_20131212\\trypsin\\trypsin.info",
//				"H:\\OGlycan_final_20131212\\trypsin\\trypsin.oglycan.F001277.xls");
    }

    /**
     * @return the type
     */
    public int getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type)
    {
        this.type = type;
    }

    /**
     * @return the count
     */
    public int getCount()
    {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * @return the scanname
     */
    public String getScanname()
    {
        return scanname;
    }

    /**
     * @param scanname the scanname to set
     */
    public void setScanname(String scanname)
    {
        this.scanname = scanname;
    }

    /**
     * @return the markpeaks
     */
    public int[] getMarkpeaks()
    {
        return markpeaks;
    }

    /**
     * @param markpeaks the markpeaks to set
     */
    public void setMarkpeaks(int[] markpeaks)
    {
        this.markpeaks = markpeaks;
    }

    /**
     * @return the pepMw
     */
    public double getPepMw()
    {
        return pepMw;
    }

    /**
     * @param pepMw the pepMw to set
     */
    public void setPepMw(double pepMw)
    {
        this.pepMw = pepMw;
    }

    /**
     * @return the units
     */
    public OGlycanUnit[][] getUnits()
    {
        return units;
    }

    /**
     * @param units the units to set
     */
    public void setUnits(OGlycanUnit[][] units)
    {
        this.units = units;
    }

    /**
     * @return the typecount
     */
    public int[] getTypecount()
    {
        return typecount;
    }

    /**
     * @param typecount the typecount to set
     */
    public void setTypecount(int[] typecount)
    {
        this.typecount = typecount;
    }

    /**
     * @return the findType
     */
    public int[] getFindType()
    {
        return findType;
    }

    /**
     * @param findType the findType to set
     */
    public void setFindType(int[] findType)
    {
        this.findType = findType;
    }

}
