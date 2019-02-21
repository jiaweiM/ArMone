/*
 * *****************************************************************************
 * File: ArraySorter.java * * * Created on 10-22-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.util.arrayutil;

import java.util.Arrays;

/**
 * The sorter utilities.
 *
 * @author Xinning
 * @version 0.2, 10-22-2008, 21:42:43
 */
public class ArraySorter
{

    private ArraySorter()
    {

    }

    /**
     * In this array,the adjacent two numbers are pairs, the first is start number and the last is end number. Commonly,
     * this pair of number indicated a continuous object eg1-4,3-6,7-8,8-10,15-20. The region should be (1,4], that is 1
     * is not included in the region, and the elements in the region are 2, 3, 4. therefore, 1-4 && 5-7 should not be
     * merged together (5 is not included in), but 1-4 & 4-7 can be merged together to 1-7;.
     *
     * @return the merged array. (1-6, 7-10, 15-20) <b>Important: the length of returned array may not equal to the
     * input array</b>
     */

    public static int[] numberPairOneDSort(int[] array)
    {
        int len = array.length;

        if (len % 2 != 0) {
            throw new RuntimeException(
                    "The array for sort must be with even number of element!");
        }
        int half = len / 2;

        int[] array1 = array.clone();

        /*
         * Sort by the even array;
         */
        for (int i = 2; i < len; i += 2) {
            int temp1 = array1[i];
            int temp2 = array1[i + 1];

            for (int j = i - 2; j >= 0; j -= 2) {
                int temp = array1[j];
                if (temp > temp1) {
                    // move array[j&&j+1] to next;
                    array1[j + 2] = temp;
                    array1[j + 3] = array1[j + 1];

                    // move array[j+2&&j+3] to before;
                    array1[j] = temp1;
                    array1[j + 1] = temp2;
                } else
                    break;
            }
        }

        int count = len;
        /*
         * merge up
         */

        for (int i = 2; i < len; i += 2) {
            int front = i - 1;
            int last = i + 1;

            int last1 = array1[last - 1];
            int last2 = array1[last];
            int front1 = array1[front - 1];
            int front2 = array1[front];
            // 存在交叉 如 1-2&&2-3 或 1-4 && 2-3;
            if (front2 >= last1) {
                // 如 1－4 && 2-3 包含关系
                // 将后面的数对变为前面的数组对
                if (front2 >= last2) {
                    array1[last - 1] = front1;
                    array1[last] = front2;
                }
                // 存在交叉 如: 1-2 2-3 或1-2 1-4;
                // 将后面数组对的第一个数变为前面的第一个数
                else {
                    array1[last - 1] = front1;
                }

                // 将前面的数组对变为长度为0，方便以后统计长度
                array1[front] = array1[front - 1];
                // The count of entries which contain information
                count -= 2;
            }
        }

        /**
         * put the merged region into a new array
         */
        int[] array2 = new int[count];
        int idx = 0;
        for (int i = 0; i < half; i++) {
            int t = i << 1;
            if (array1[t + 1] - array1[t] > 0) {
                array2[idx++] = array1[t];
                array2[idx++] = array1[t + 1];
            }
        }

        return array2;
    }

    /**
     * Sort the objects by their corresponding values. That is sorting the objects by the array of values from small to
     * big. After sorting, both the objects and the values will be refreshed; and the new object array and the number
     * array will also corresponding to each other.
     *
     * @param objs   the objects for sorting
     * @param values the values using which the objects are sorted.
     */
    public static void sortByValue(Object[] objects, Number[] values)
    {

        if (objects == null || values == null)
            throw new NullPointerException("The objects for sorting is null.");

        int len = objects.length;
        if (len != values.length) {
            throw new IllegalArgumentException(
                    "The number of objects and their corresponding values "
                            + "should equal to each other.");
        }

        ComparableImp[] comps = new ComparableImp[len];
        for (int i = 0; i < len; i++) {
            comps[i] = new ComparableImp(objects[i], values[i]);
        }

        Arrays.sort(comps);

        /*
         * Renew the values and the objects.
         */
        for (int i = 0; i < len; i++) {
            ComparableImp comp = comps[i];

            objects[i] = comp.getObj();
            values[i] = comp.getValue();
        }
    }

    public static void main(String[] args)
    {
        int[] t = new int[]{1, 4};
        int[] q = numberPairOneDSort(t);
        System.out.println("Return length: " + q.length);
        for (int i = 0; i < q.length; i++)
            System.out.println(q[i]);
    }

    private static class ComparableImp implements Comparable<ComparableImp>
    {

        private Object obj;
        private Number value;
        private double dvalue;

        /**
         * @param obj
         * @param value
         */
        public ComparableImp(Object obj, Number value)
        {
            super();
            this.obj = obj;
            this.value = value;
            this.dvalue = value.doubleValue();
        }

        @Override
        public int compareTo(ComparableImp o)
        {

            if (dvalue > o.dvalue) {
                return 1;
            }

            return dvalue < o.dvalue ? -1 : 0;
        }

        /**
         * @return the obj
         */
        public final Object getObj()
        {
            return obj;
        }

        /**
         * @return the value
         */
        public final Number getValue()
        {
            return value;
        }

    }
}
