/*
 ******************************************************************************
 * File: Shepard_ProbCalculator.java * * * Created on 11-30-2007
 *
 * Copyright (c) 2007 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import cn.ac.dicp.gp1809.proteome.penn.probability.PepNorm.Distant;


import cn.ac.dicp.gp1809.util.SmartTimer;


/**
 * The Shepard algorithm. The common idea is that the nearer the peptides, the more similar they are. The weight for
 * each peptide is the calculated by put 1 over the distance of peptide. Then the final value is computed by multiply
 * the weight by the value of this peptide.
 *
 * @author Xinning
 * @version 0.4, 12-07-2007, 10:24:14
 */
public class Shepard_ProbCalculator extends KN_Cluster_ProbCalculator
{

    /*
     * Put how many peptide into a bin and the distance weight will be calculated together.
     * This is to avoid that sometimes there may be peptide with too small distance and may introduce
     * some bias.
     */
    private static final int BIN_COUNT = 1000;
    /**
     * Overwrite KN_Cluster k near + 1 (including itself)
     */
    public static int K_NEAR = 4;
    /*
     * The last bin count; may be 0;
     */
    private static final int LAST_BIN_COUNT = K_NEAR % BIN_COUNT;

    /*
     * Is there individual in the last bin;
     */
    private static final boolean HAS_LAST_BIN = LAST_BIN_COUNT != 0;
    /**
     * Only peptide with distant weight bigger than this value will be used for probability calculation
     */
    private static final float MIN_WEIGHT = 0.01f;

    /*
     * The count of BINs, except the last bin (if the remained count is less than BIN_COUNT);
     */
    private static final int BINS = K_NEAR / BIN_COUNT;

    public static int target = -1;
    /*
     * The folder of extension when the elements containing within the initialed
     * distant is smaller than the knear value;
     */
    private static float EXTENSION = 1.5f;

    //---------------------------------------------------------//
    /*
     * This value is used to format the proper value to curt the distant array.
     * For efficiency, the number of elements in k_near distant array should not so
     * small (another circulation may be need) and not so big (waste time in array sort).
     */
    private int longRANGE = K_NEAR + K_NEAR / 5;
    private int count = 0;
    /*
     * Current used distant.
     * start with a small distant
     */
    private float dist = 0.05f;
    private int precount = 0;

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.probability.ProbCalculator#calculate
     * (cn.ac.dicp.gp1809.proteome.probability.PepNorm[])
     */
    @Override
    public void calculate(PepNorm[] peps)
    {
        int len = peps.length;
        System.out.println("Peptides: " + len + "\r\n" + "Calculating probability ...");
        SmartTimer t1 = new SmartTimer();

        EntropyDistanceCalculator dcalor = new EntropyDistanceCalculator(peps);

        /*
         * There are not enough peptides for the computing of probability.
         */
        if (len <= K_NEAR) {
            System.out.println("The total pepNorm number:" + len + " is less than the K_NEAR:" + K_NEAR
                    + ". Skip calculating.");
            return;
        }

        float[] distants = new float[len];
        //corresponding to distant, is the peptide true ?
        int[] tfs = new int[len];

        for (int i = 0; i < len; i++) {
            PepNorm curt = peps[i];

            curt.probablity = this.getProb(curt, peps, dcalor, distants, tfs);

            curt.count = precount;
        }

        System.out.println("Probability calculated for " + len + " peptides in " + t1);
    }

    /*
     * All the peptides are binned together by the distances.
     */
    @SuppressWarnings("unused")
    private float getProb1(PepNorm curtPep, PepNorm[] peps, EntropyDistanceCalculator dcalor)
    {

        PepNorm.Distant[] distants = this.getNearDistants(dcalor, curtPep, peps);

        float totalvalue = 0f;
        float totalweight = 0f;

        for (int i = 0; i < BINS; i++) {
            int start = i * BIN_COUNT;
            int end = start + BIN_COUNT;
            int value = 0;
            float tdist = 0f;


            for (int j = start; j < end; j++) {
                Distant tdis = distants[j];
                tdist += tdis.distant;

                if (peps[tdis.idx].isRev) {
                    value--;
                } else
                    value++;
            }

            float weight = BIN_COUNT / tdist;

            float tmp = value / tdist;
            totalvalue += tmp;
            if (value > 0)
                totalweight += weight;
        }

        if (HAS_LAST_BIN) {
            int start = BINS * BIN_COUNT;
            int end = start + LAST_BIN_COUNT;
            int value = 0;
            float tdist = 0f;


            for (int j = start; j < end; j++) {
                Distant tdis = distants[j];
                tdist += tdis.distant;

                if (peps[tdis.idx].isRev) {
                    value--;
                } else
                    value++;
            }

            float weight = LAST_BIN_COUNT / tdist;

            float tmp = value / tdist;
            totalvalue += tmp;
            if (value > 0)
                totalweight += weight;
        }


        float prob = 0f;
        if (totalweight - 0f > 0.000001)
            prob = totalvalue / totalweight;

        /*
         * The precise of float is not so high, so sometimes some value may be bigger than 1f
         */
        if (prob > 1f)
            prob = 1f;

        return prob <= 0f ? 0f : prob;
    }

    /**
     * The shepard method, all peptides are considered in probability calculation. The 1/distance between the current
     * peptide is used as weight, all then the probability is the final value divided by final weight;
     */
    @SuppressWarnings("unused")
    private float getProb(PepNorm curt, PepNorm[] peps,
            EntropyDistanceCalculator dcalor, float dis)
    {

        boolean print = curt.idx == target;
        PrintWriter pw = null;
        if (print) {
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter("d:\\temp.txt")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int len = peps.length;
        float prob = 0f;

        ArrayList<Distant> distlist = new ArrayList<Distant>(longRANGE);
        float totalvalue = 0f;
        float totalweight = 0f;
        for (int j = 0; j < len; j++) {
            PepNorm p = peps[j];
            float d2 = dcalor.calculateD2(curt, p);
            if (d2 < dist)
                distlist.add(new Distant(d2, j));
            else {
                float tmp = 1f / d2;
                totalweight += tmp;
                if (p.isRev)
                    totalvalue -= tmp;
                else
                    totalvalue += tmp;

                if (print) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(p.isRev ? 0 : 1).append("\t")
                            .append(p.xcn).append("\t").append(p.dcn).append("\t").append(d2);
                    pw.println(sb);
                }
            }
        }

        int c = distlist.size();

        if (c < K_NEAR) {
            dist *= EXTENSION;//extends

            if (print) {
                System.out.println("clost");
                pw.close();
            }

            prob = getProb(curt, peps, dcalor, dis);
        } else {
            Distant[] distants = distlist.toArray(new Distant[distlist.size()]);
            //from small to big
            Arrays.sort(distants);
            int tlen = distants.length;

            if (print) {
                System.out.println("length: " + distants.length);
                pw.println("length: " + distants.length);
            }

            /*
             * To avoid too small distance, K_NEAR peptides closest to the current peptide
             * will be grouped together and calculated for probability.
             */
            float tdist = 0f;
            float value = 0f;
            for (int j = 0; j < K_NEAR; j++) {
                Distant tdis = distants[j];
                tdist += tdis.distant;
                PepNorm p = peps[tdis.idx];

                if (p.isRev)
                    value--;
                else
                    value++;

                if (print) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(p.isRev ? -1 : 1).append("\t")
                            .append(p.xcn).append("\t").append(p.dcn).append("\t").append(tdis.distant);
                    pw.println(sb);
                }
            }

            float weight = K_NEAR / tdist;
            totalweight += weight * K_NEAR;
            totalvalue += value * weight;

            if (print) {
                StringBuilder sb = new StringBuilder();
                sb.append(tdist).append("\t").append(weight).append("\t")
                        .append(value).append("\t----------");
                pw.println(sb);
            }


            for (int j = K_NEAR; j < tlen; j++) {
                Distant tdis = distants[j];
                float tmp = 1f / tdis.distant;
                totalweight += tmp;
                PepNorm p = peps[tdis.idx];

                if (peps[tdis.idx].isRev)
                    totalvalue -= tmp;
                else
                    totalvalue += tmp;

                if (print) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(p.isRev ? 0 : 1).append("\t")
                            .append(p.xcn).append("\t").append(p.dcn).append("\t").append(tdis.distant);
                    pw.println(sb);
                }
            }

            if (print) {
                pw.println(totalweight + "\t" + totalvalue + "\t--------------");
                pw.close();
            }

            //read just the dist;
            if (c > longRANGE) {
                dist = (dis * count + distants[longRANGE].distant) / (++count);
            } else {
                //To avoid the so-max value.
                dist = (dis * count + dis * EXTENSION) / (++count);
            }
        }

        prob = totalvalue / totalweight;

        return prob;
    }

    /*
     * The nearest K_NEAR peptides are grouped together for calculation of the total weight to
     * avoid so-near condition. Then this value is considered as the biggest value (simmilar to
     * the base peak in MS spectrum), only peptides with distance weights bigger than 1% (or other)
     * are considered in probability calculation.
     */
    private float getProb(PepNorm curt, PepNorm[] peps,
            EntropyDistanceCalculator dcalor, float[] distants, int[] tfs)
    {
        int len = peps.length;
        HashSet<Integer> nearSet = new HashSet<Integer>(longRANGE);

        for (int j = 0; j < len; j++) {
            PepNorm p = peps[j];
            float d2 = dcalor.calculateD2(curt, p);

            distants[j] = d2;
            tfs[j] = p.isRev ? -1 : 1;

            if (d2 < dist)
                nearSet.add(new Integer(j));

        }

        return getProb3(peps, nearSet, distants, tfs, dist);
    }

    private float getProb3(PepNorm[] peps, HashSet<Integer> nearlist,
            float[] distants, int[] tfs, float disrange)
    {
        float prob;
        int c = nearlist.size();

        if (c < K_NEAR) {
            dist *= EXTENSION;//extends
            int len = distants.length;
            for (int i = 0; i < len; i++) {
                float d = distants[i];
                //add to near list;
                if (d <= dist)
                    nearlist.add(new Integer(i));

            }

            prob = getProb3(peps, nearlist, distants, tfs, disrange);
        } else {

            /*
             * Get the nearest distance array
             */
            Distant[] dists = new Distant[c];
            int m = 0;
            for (Iterator<Integer> iterator = nearlist.iterator(); m < c; m++) {
                int idx = iterator.next();
                dists[m] = new Distant(distants[idx], idx);
            }
            //from small to big
            Arrays.sort(dists);


            float nearweight = 0f;
            float nearvalue = 0f;


            /*
             * To avoid too small distance, K_NEAR peptides closest to the current peptide
             * will be grouped together and calculated for probability.
             */
            float tdist = 0f;
            float value = 0f;
            //The first one is itself
            for (int j = 0; j < K_NEAR; j++) {
                Distant tdis = dists[j];
                int idx = tdis.idx;
                tdist += tdis.distant;
                value += tfs[idx];
                tfs[idx] = 0;//set this peptide has been used.
            }

            //The biggest weight
            float bweight = (K_NEAR - 1) / tdist;
            nearweight += bweight * K_NEAR;
            nearvalue += value * bweight;

            /*
             * Only peptides with weight bigger than 1% of the biggest weight
             * were used.
             */
            float lbondsDist = 1f / (bweight * MIN_WEIGHT);

            //The evaluated probability;
//			float preProb  
            prob = this.getProb4(nearweight, nearvalue, lbondsDist, distants, tfs);


            /*
             * AS the SD of probability increased greatly while the probability of peptide decreased,
             * more peptide count should be used for calculation of probability.
             * Therefore, the probability is first precalculated and then recalcuated using
             * the new probability-weighted-low-distance bonds.
             * For peptide with preprobability low than 0.001, all the peptides should be used
             * for probability calcuation.
             */
//			float probweight = preProb < 0.00001f ? 0.00001f : preProb;
//			lbondsDist /= probweight;

            //the final probability
//			prob = this.getProb4(nearweight, nearvalue, lbondsDist, distants, tfs);

            //read just the dist;
            if (c > longRANGE)
                dist = (disrange * count + dists[longRANGE].distant) / (++count);
            else   //To avoid the so-max value.
                dist = (disrange * count + disrange * EXTENSION) / (++count);
        }

        return prob >= 0f ? prob : 0f;
    }

    private float getProb4(float nearweight, float nearvalue,
            float lowbonds, float[] distants, int[] tfs)
    {
        float totalweight = 0f;
        float totalvalue = 0f;

        int count = 0;
        int len = distants.length;
        for (int i = 0; i < len; i++) {
            float d = distants[i];
            if (d > lowbonds) continue;

            int tf = tfs[i];
            if (tf != 0) {
                float tmp = 1f / d;
                totalweight += tmp;
                totalvalue += tmp * tf;

                count++;
            }
        }

        precount = count;
        return totalvalue / totalweight;
    }
}
