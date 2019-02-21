/*
 ******************************************************************************
 * File: Combinator.java * * * Created on  02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math;

import java.math.BigDecimal;

/**
 * @author Xinning
 * @version 0.1.1, 09-04-2009, 21:51:53
 */
public class Combinator
{
    private static int count = 0;

    /**
     * Compute the value of    Ck n
     *
     * @param k select k from n
     * @param n the total number
     * @return the combination of k from n;
     */
    public static int compute(int k, int n)
    {
        int value = 1;

        if (n < 0 || k < 0)
            return 0;

        if (k == 0 || k == n)
            return 1;

        int j = k;
        if (k * 2 < n) {
            j = n - k;
        }

        for (int i = n; i > j; i--)
            value *= i;

        int temp = 1;
        for (int i = n - j; i > 0; i--)
            temp *= i;

        return value / temp;
    }

    public static BigDecimal computeBig(int k, int n)
    {

        BigDecimal bigvalue = new BigDecimal(1);

        if (n < 0 || k < 0)
            return new BigDecimal(0);

        if (k == 0 || k == n)
            return new BigDecimal(1);

        int j = k;
        if (k * 2 < n) {
            j = n - k;
        }

        for (int i = n; i > j; i--) {
            bigvalue = bigvalue.multiply(new BigDecimal(i));
        }

        BigDecimal tempvalue = new BigDecimal(1);

        for (int i = n - j; i > 0; i--) {
            tempvalue = tempvalue.multiply(new BigDecimal(i));
        }

        BigDecimal result = bigvalue.divide(tempvalue);
        return result;
    }

    public static int compute(long k, long n)
    {
        int value = 1;

        if (n < 0 || k < 0)
            return 0;

        if (k == 0 || k == n)
            return 1;

        long j = k;
        if (k * 2 < n) {
            j = n - k;
        }

        for (long i = n; i > j; i--)
            value *= i;

        int temp = 1;
        for (long i = n - j; i > 0; i--)
            temp *= i;

        return value / temp;
    }

    /**
     * Get all the possible combinations (select k from n) from the objs
     *
     * @param objs
     * @param k
     * @return
     */
    public static Object[][] getCombination(Object[] objs, int k)
    {
        int len = objs.length;
        int c;
        if (k == 0 || len < k || (c = compute(k, len)) == 0)
            return null;

        Object[][] rps = new Object[c][];

        fillArray(rps, objs, new Object[k], k, len, 0, 0);
        count = 0;

        return rps;
    }

    private static void fillArray(Object[][] rps, Object[] raw, Object[] curt, int k, int n,
            int curtk, int curti)
    {
        int len = n - k + curtk;

        for (int i = curti; i <= len; i++) {//System.out.println("77\t"+curtk+"\t"+curti+"\t"+len);
            Object tobj = raw[i];//System.out.println(tobj);
            if (curtk < k - 1) {
                curt[curtk] = tobj;
                fillArray(rps, raw, curt, k, n, curtk + 1, i + 1);
            } else {//System.out.println("83\t"+curtk+"\t"+curti);
                Object[] newobjs = new Object[k];
                System.arraycopy(curt, 0, newobjs, 0, k - 1);
                newobjs[k - 1] = tobj;
                rps[count++] = newobjs;
            }
        }
    }

    public static int[][] getCombination(int[] objs, int k)
    {
        int len = objs.length;
        int c;
        if (k == 0 || len < k || (c = compute(k, len)) == 0)
            return null;

        int[][] rps = new int[c][];

        fillArray(rps, objs, new int[k], k, len, 0, 0);
        count = 0;

        return rps;
    }

    private static void fillArray(int[][] rps, int[] raw, int[] curt, int k, int n,
            int curtk, int curti)
    {

        int len = n - k + curtk;

        for (int i = curti; i <= len; i++) {//System.out.println("77\t"+curtk+"\t"+curti+"\t"+len);
            int tobj = raw[i];//System.out.println(tobj);
            if (curtk < k - 1) {
                curt[curtk] = tobj;
                fillArray(rps, raw, curt, k, n, curtk + 1, i + 1);
            } else {//System.out.println("83\t"+curtk+"\t"+curti);
                int[] newobjs = new int[k];
                System.arraycopy(curt, 0, newobjs, 0, k - 1);
                newobjs[k - 1] = tobj;
                rps[count++] = newobjs;
            }
        }
    }

    public static void perm(char[] buf, int start, int end)
    {
        if (start == end) { // 当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可
            for (int i = 0; i <= end; i++) {
//            	System.out.print(buf[i]);
            }
            System.out.println("97\t" + (new String(buf)));
        } else { // 多个字母全排列
            for (int i = start; i <= end; i++) {
                char temp = buf[start]; // 交换数组第一个元素与后续的元素
                buf[start] = buf[i];
                buf[i] = temp;

                System.out.println("104\t" + i + "\t" + start + "\t" + new String(buf));
                perm(buf, start + 1, end); // 后续元素递归全排列

                System.out.println("107\t" + i + "\t" + start + "\t" + new String(buf));
                temp = buf[start]; // 将交换后的数组还原
                buf[start] = buf[i];
                buf[i] = temp;
                System.out.println("111\t" + i + "\t" + start + "\t" + new String(buf));
            }
        }
    }

    public static void perm(int[][] arran, int[] arrays, int start, int end)
    {
        if (start == end) {
            arran[count] = arrays;
            count++;
        } else {
            for (int i = start; i <= end; i++) {
                int in = arrays[start];
                arrays[start] = arrays[i];
                arrays[i] = in;
                perm(arran, arrays, start + 1, end);
                in = arrays[start];
                arrays[start] = arrays[i];
                arrays[i] = in;
            }
        }
    }

    public static int[][] getArrangement(int[] objs)
    {
        int len = objs.length;
        int c = 1;
        for (int i = 1; i <= len; i++) {
            c *= i;
        }

        int[][] rps = new int[c][];

        perm(rps, objs, 0, objs.length - 1);
        count = 0;

        return rps;
    }

    public static void main(String args[])
    {

        Integer[] ints = new Integer[]{1, 2, 3, 4};

//		for(int num=1;num<=4;num++){
//			Object [][] combines = getCombination(ints, 2);
//			for(int i=0;i<combines.length;i++){
//				Object[] is = combines[i];
//				for(int j=0;j<is.length;j++){
//					System.out.print(is[j]+"\t");
//				}
//				System.out.println();
//			}

//		}
//		Combinator.perm(new char[]{'a', 'b', 'c', 'd'}, 0, 3);

        System.out.println(Combinator.computeBig(6, 100));
    }
}
