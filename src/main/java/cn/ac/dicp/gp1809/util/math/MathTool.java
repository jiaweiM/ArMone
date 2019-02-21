/* 
 ******************************************************************************
 * File:MathTool.java * * * Created on 2010-3-24
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.util.math;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2010-3-24, 18:25:22
 */
public class MathTool {

	public final static DecimalFormat df4 = DecimalFormats.DF0_4;
	public final static DecimalFormat df6 = DecimalFormats.DF0_6;
	public final static DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;

	/**
	 * ��X��һ�������������E{[X-E(X)]^2}���ڣ����E{[X-E(X)]^2}ΪX�ķ��
	 * ��ΪD(X)��DX����D(X)=E{[X-E(X)]^2}������(X)=D(X)^0.5����X����ͬ�����٣�
	 * ��Ϊ��׼�������������ͳ��ѧ�еĸ��
	 * <p>
	 * <b>�������壬��׼�ʽ�����ڳ���n��������������׼�ʽ�����ڳ��ԣ�n-1)��
	 * �����������������㡣
	 * 
	 * @param data
	 * @return
	 */
	public static double getStdDev(double[] data) {
		double ave = 0;
		double STDEV = 0;
		for (int i = 0; i < data.length; i++) {
			ave += data[i];
		}
		ave = ave / data.length;
		for (int j = 0; j < data.length; j++) {
			STDEV += (data[j] - ave) * (data[j] - ave);
		}
		if (data.length == 1) {
			return 0;
		} else {
			return Double.parseDouble(df4.format(Math.sqrt(STDEV / (data.length - 1))));
		}
	}

	public static double getStdDevNoZero(double[] data) {

		double STDEV = 0;
		double ave = getAveNoZero(data);
		int count = 0;

		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0) {
				STDEV += (data[i] - ave) * (data[i] - ave);
				count++;
			}
		}
		if (count == 1 || count == 0) {
			return 0;
		} else {
			return Double.parseDouble(df4.format(Math.sqrt(STDEV / (count - 1))));
		}
	}

	public static double getStdDevInDouble(ArrayList<Double> data) {
		if (data.size() == 0)
			return 0;

		double ave = 0;
		double STDEV = 0;
		for (int i = 0; i < data.size(); i++) {
			ave += data.get(i);
		}
		ave = ave / data.size();
		for (int j = 0; j < data.size(); j++) {
			STDEV += (data.get(j) - ave) * (data.get(j) - ave);
		}
		if (data.size() == 1) {
			return 0;
		} else {
			return Double.parseDouble(df4.format(Math.sqrt(STDEV / (data.size() - 1))));
		}
	}

	public static float getStdDev(float[] data) {
		float ave = 0;
		float STDEV = 0;
		for (int i = 0; i < data.length; i++) {
			ave += data[i];
		}
		ave = ave / data.length;
		for (int j = 0; j < data.length; j++) {
			STDEV += (data[j] - ave) * (data[j] - ave);
		}
		if (data.length == 1) {
			return 0;
		} else {
			return Float.parseFloat(df4.format(Math.sqrt(STDEV / (data.length - 1))));
		}
	}

	public static float getStdDevInFloat(ArrayList<Float> data) {
		if (data.size() == 0)
			return 0;

		float ave = 0;
		float STDEV = 0;
		for (int i = 0; i < data.size(); i++) {
			ave += data.get(i);
		}
		ave = ave / data.size();
		for (int j = 0; j < data.size(); j++) {
			STDEV += (data.get(j) - ave) * (data.get(j) - ave);
		}
		if (data.size() == 1) {
			return 0;
		} else {
			return Float.parseFloat(df4.format(Math.sqrt(STDEV / (data.size() - 1))));
		}
	}

	public static double getRSD(double[] data) {
		double ave = 0;
		double STDEV = 0;
		for (int i = 0; i < data.length; i++) {
			ave += data[i];
		}
		ave = ave / data.length;
		for (int j = 0; j < data.length; j++) {
			STDEV += (data[j] - ave) * (data[j] - ave);
		}
		if (data.length == 1) {
			return 0;
		} else {
			if (ave == 0)
				return 0;
			return Double.parseDouble(df4.format(Math.sqrt(STDEV / (data.length - 1)) / ave));
		}
	}

	/**
	 * Returns RSD of list of values.
	 * 
	 * @param data raw data value
	 * @return rsd of the dataset
	 */
	public static double getRSDInDouble(ArrayList<Double> data) {
		if (data.size() == 0)
			return 0;

		double ave = 0;
		double STDEV = 0;
		// calculate the summary
		for (int i = 0; i < data.size(); i++) {
			ave += data.get(i);
		}
		ave = ave / data.size();
		for (int j = 0; j < data.size(); j++) {
			STDEV += (data.get(j) - ave) * (data.get(j) - ave);
		}
		if (data.size() == 1) {
			return 0;
		} else {
			if (ave == 0)
				return 0;
			return Double.parseDouble(df4.format(Math.sqrt(STDEV / (data.size() - 1)) / ave));
		}
	}

	public static float getRSD(float[] data) {
		double ave = 0;
		double STDEV = 0;
		for (int i = 0; i < data.length; i++) {
			ave += data[i];
		}
		ave = ave / data.length;
		for (int j = 0; j < data.length; j++) {
			STDEV += (data[j] - ave) * (data[j] - ave);
		}
		if (data.length == 1) {
			return 0;
		} else {
			if (ave == 0)
				return 0;
			return Float.parseFloat(df4.format(Math.sqrt(STDEV / (data.length - 1)) / ave));
		}
	}

	public static float getRSDInFloat(ArrayList<Float> data) {
		if (data.size() == 0)
			return 0;

		double ave = 0;
		double STDEV = 0;
		for (int i = 0; i < data.size(); i++) {
			ave += data.get(i);
		}
		ave = ave / data.size();
		for (int j = 0; j < data.size(); j++) {
			STDEV += (data.get(j) - ave) * (data.get(j) - ave);
		}
		if (data.size() == 1) {
			return 0;
		} else {
			if (ave == 0)
				return 0;
			return Float.parseFloat(df4.format(Math.sqrt(STDEV / (data.size() - 1)) / ave));
		}
	}

	public static double[] getVar(ArrayList<Double> data) {
		int length = data.size();
		ArrayList<Double> temp = new ArrayList<Double>();
		double[] dataList = new double[length];
		double total = 0;
		dataList[0] = 0;
		for (int i = 1; i < data.size(); i++) {

			double d1 = data.get(i);
			temp.add(d1);
			double root = 0;
			total += d1;

			double ave = total / (i + 1);
			for (Double d2 : temp) {
				root += (d2 - ave) * (d2 - ave);
			}
			root = Math.sqrt(root / (i));
			if (d1 > ave)
				dataList[i] = root;
			else
				dataList[i] = 0 - root;

			// System.out.println(i+"\t"+dataList[i]);
		}
		return dataList;
	}

	public static double[] getVar(Double[] data) {
		ArrayList<Double> temp = new ArrayList<Double>(data.length);
		for (int i = 0; i < data.length; i++) {
			temp.add(data[i]);
		}
		return getVar(temp);
	}

	public static int getMaxIndex(double[] array) {
		double max = array[0];
		int index = 0;
		for (int i = 1; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}

	public static int getMaxIndex(float[] array) {
		float max = array[0];
		int index = 0;
		for (int i = 1; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}

	public static int getMaxIndex(int[] array) {
		int max = array[0];
		int index = 0;
		for (int i = 1; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}

	/**
	 * Another method to calculate the standard deviation, the result is same as
	 * getVar().
	 * <p>
	 * ����������������㡣
	 * 
	 * @param data
	 * @return
	 */
	public static double[] getVar2(ArrayList<Double> data) {
		int length = data.size();
		ArrayList<Double> temp = new ArrayList<Double>();
		double[] dataList = new double[length];
		double total = 0;

		for (int i = 0; i < data.size(); i++) {
			double d1 = data.get(i);
			temp.add(d1);
			double root = 0;
			total += d1;

			double ave = total / (i + 1);
			for (Double d2 : temp) {
				root += d2 * d2;
			}

			root = Math.sqrt(root / (i + 1) - ave * ave);
			if (d1 > ave)
				dataList[i] = root;
			else
				dataList[i] = 0 - root;
			// System.out.println(i+"\t"+dataList[i]);
		}

		return dataList;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */

	public static double getVarRatio(ArrayList<Double> data) {
		double[] rmsList = getVar(data);
		double ratio = 0;
		int n = rmsList.length;
		double total = 0;
		for (double r : rmsList) {
			total += Math.abs(r);
		}
		if (total == 0)
			ratio = 0;
		else
			ratio = rmsList[n - 1] * n / total;
		// System.out.println(ratio);
		return ratio;
	}

	public static short[] deNoise(Double[] data, int length, double highThres, double lowThres, double lowThMin) {

		ArrayList<Double> temp = new ArrayList<Double>();
		ArrayList<Double> testData = new ArrayList<Double>();
		short[] dataList = new short[data.length];

		temp.add(data[0]);
		testData.add(data[0]);

		for (int i = 1; i < data.length; i++) {

			temp.add(data[i]);
			double ratio = getVarRatio(temp);

			if (ratio < 2.01 && ratio > lowThMin)
				testData.add(data[i]);
			else
				temp.remove(data[i]);

			if (testData.size() == length)
				break;
		}

		for (int i = 0; i < data.length; i++) {
			double di = data[i];
			testData.add(di);
			double ratio = getVarRatio(testData);
			// System.out.println(i+"~"+ratio+"~"+di+"~"+testData);
			if (ratio > highThres) {
				dataList[i] = 1;
				testData.remove(di);
				// System.out.print(di+"~"+ratio+"~");
				// System.out.println(testData);
			} else if (ratio < lowThres && ratio > lowThMin) {
				dataList[i] = 0;
				testData.remove(0);
				// System.out.print(di+"~"+ratio+"~");
				// System.out.println(testData);
			} else {
				dataList[i] = 0;
				testData.remove(di);
				// System.out.print(di+"~"+ratio+"~");
				// System.out.println(testData);
			}
		}

		// System.out.println(dataList);
		return dataList;
	}

	public static double[] getAveList(ArrayList<Double> dataList) {
		double[] ave = new double[dataList.size()];
		double total = 0;
		for (int i = 0; i < ave.length; i++) {
			total += dataList.get(i);
			ave[i] = Double.parseDouble(df4.format(total / (double) (i + 1)));
		}
		return ave;
	}

	public static double getAveInDouble(ArrayList<Double> dataList) {
		if (dataList.size() == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < dataList.size(); i++) {
			total += dataList.get(i);
		}
		double ave = Double.parseDouble(df4.format(total / (double) dataList.size()));
		return ave;
	}

	public static double getAve(double[] dataList) {
		if (dataList.length == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}

		// System.out.println(total+"\t"+dataList.length);
		double ave = Double.parseDouble(df4.format(total / (double) dataList.length));
		return ave;
	}

	public static double getAve(Double[] dataList) {
		if (dataList.length == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}

		// System.out.println(total+"\t"+dataList.length);
		double ave = Double.parseDouble(df4.format(total / (double) dataList.length));
		return ave;
	}

	public static double getAveNoZero(double[] dataList) {
		if (dataList.length == 0)
			return 0;

		double total = 0;
		int count = 0;
		for (int i = 0; i < dataList.length; i++) {
			if (dataList[i] != 0) {
				total += dataList[i];
				count++;
			}
		}

		// System.out.println(total+"\t"+dataList.length);
		double ave = count == 0 ? 0 : Double.parseDouble(df4.format(total / (double) count));
		return ave;
	}

	public static double getAve(int[] dataList) {
		if (dataList.length == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}

		// System.out.println(total+"\t"+dataList.length);
		double ave = Double.parseDouble(df4.format(total / (double) dataList.length));
		return ave;
	}

	public static double getWeightAve(double[] weight, double[] dataList) {
		if (dataList.length == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i] * weight[i];
		}
		double ave = Double.parseDouble(df4.format(total / getTotal(weight)));
		return ave;
	}

	public static double getWeightAveInDouble(ArrayList<Double> weight, ArrayList<Double> dataList) {
		if (dataList.size() == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < dataList.size(); i++) {
			total += dataList.get(i) * weight.get(i);
		}
		double ave = Double.parseDouble(df4.format(total / getTotalInDouble(weight)));
		return ave;
	}

	public static double[] getPlusAve(double[] d1, double[] d2) {

		if (d1.length != d2.length)
			return null;

		double[] total = new double[d1.length];
		for (int i = 0; i < d1.length; i++) {
			total[i] += d1[i] + d2[i];
		}

		return total;
	}

	public static double getTotalInDouble(ArrayList<Double> dataList) {
		double total = 0;
		for (int i = 0; i < dataList.size(); i++) {
			total += dataList.get(i);
		}
		return total;
	}

	public static double getTopnTotalInDouble(ArrayList<Double> dataList, int topn) {

		Double[] list = dataList.toArray(new Double[dataList.size()]);
		return getTopnTotal(list, topn);
	}

	public static float getTotalInFloat(ArrayList<Float> dataList) {
		float total = 0;
		for (int i = 0; i < dataList.size(); i++) {
			total += dataList.get(i);
		}
		return total;
	}

	public static double getTotal(double[] dataList) {
		double total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}
		return total;
	}

	public static double getTotal(Double[] dataList) {
		double total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}
		return total;
	}

	public static double getTopnTotal(double[] dataList, int topn) {

		if (topn > dataList.length)
			return getTotal(dataList);

		Arrays.sort(dataList);
		double total = 0;
		for (int i = dataList.length - 1; i >= dataList.length - topn; i--) {
			total += dataList[i];
		}
		return total;
	}

	public static double getTopnTotal(Double[] dataList, int topn) {

		if (topn > dataList.length)
			return getTotal(dataList);

		Arrays.sort(dataList);
		double total = 0;
		for (int i = dataList.length - 1; i >= dataList.length - topn; i--) {
			total += dataList[i];
		}
		return total;
	}

	public static double getTopnAve(ArrayList<Double> dataList, int topn) {
		Double[] list = dataList.toArray(new Double[dataList.size()]);
		return getTopnAve(list, topn);
	}

	public static double getTopnAve(double[] dataList, int topn) {

		if (topn > dataList.length)
			return getAve(dataList);

		if (topn == 0)
			return 0;

		Arrays.sort(dataList);
		double total = 0;
		for (int i = dataList.length - 1; i >= dataList.length - topn; i--) {
			total += dataList[i];
		}
		return Double.parseDouble(df4.format(total / (double) topn));
	}

	public static double getTopnAve(Double[] dataList, int topn) {

		if (topn > dataList.length)
			return getAve(dataList);

		if (topn == 0)
			return 0;

		Arrays.sort(dataList);
		double total = 0;
		for (int i = dataList.length - 1; i >= dataList.length - topn; i--) {
			total += dataList[i];
		}
		return Double.parseDouble(df4.format(total / (double) topn));
	}

	public static float getTotal(float[] dataList) {
		float total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}
		return total;
	}

	public static int getTotal(int[] dataList) {
		int total = 0;
		for (int i = 0; i < dataList.length; i++) {
			total += dataList[i];
		}
		return total;
	}

	public static double getGeoAve(ArrayList<Double> dataList) {
		if (dataList.size() == 0)
			return 0;

		double product = 1.0;
		double n = 1.0 / dataList.size();
		for (int i = 0; i < dataList.size(); i++) {
			product = product * dataList.get(i);
		}

		double geoAve = Math.pow(product, n);
		geoAve = Double.parseDouble(df4.format(geoAve));
		return geoAve;
	}

	public static double getGeoAve(double[] dataList) {
		if (dataList.length == 0)
			return 0;

		double product = 1.0;
		double n = 1.0d / (double) dataList.length;
		for (int i = 0; i < dataList.length; i++) {
			product = product * dataList[i];
		}
		double geoAve = Math.pow(product, n);
		geoAve = Double.parseDouble(df4.format(geoAve));
		return geoAve;
	}

	public static int getMedianInInteger(ArrayList<Integer> dataList) {
		if (dataList.size() == 0)
			return 0;

		int[] dataArrays = new int[dataList.size()];
		for (int i = 0; i < dataArrays.length; i++) {
			dataArrays[i] = dataList.get(i);
		}
		return getMedian(dataArrays);
	}

	public static double getMedianInDouble(ArrayList<Double> dataList) {
		if (dataList.size() == 0)
			return 0;

		double[] dataArrays = new double[dataList.size()];
		for (int i = 0; i < dataArrays.length; i++) {
			dataArrays[i] = dataList.get(i);
		}
		return getMedian(dataArrays);
	}

	public static float getMedianInFloat(ArrayList<Float> dataList) {
		if (dataList.size() == 0)
			return 0;

		float[] dataArrays = new float[dataList.size()];
		for (int i = 0; i < dataArrays.length; i++) {
			dataArrays[i] = dataList.get(i);
		}
		return getMedian(dataArrays);
	}

	public static int getMedian(int[] dataList) {
		if (dataList.length == 0)
			return 0;

		Arrays.sort(dataList);
		int len = dataList.length;
		if (len % 2 == 0) {
			int d1 = dataList[len / 2 - 1];
			int d2 = dataList[len / 2];
			return (int) ((d1 + d2) / 2.0);
		} else {
			return dataList[(len - 1) / 2];
		}
	}

	public static double getMedian(double[] dataList) {
		if (dataList.length == 0)
			return 0;

		Arrays.sort(dataList);
		int len = dataList.length;
		if (len % 2 == 0) {
			double d1 = dataList[len / 2 - 1];
			double d2 = dataList[len / 2];
			return Double.parseDouble(df4.format((d1 + d2) / 2.0));
		} else {
			return dataList[(len - 1) / 2];
		}
	}

	public static double getMedian(Double[] dataList) {
		if (dataList.length == 0)
			return 0;

		Arrays.sort(dataList);
		int len = dataList.length;
		if (len % 2 == 0) {
			double d1 = dataList[len / 2 - 1];
			double d2 = dataList[len / 2];
			return Double.parseDouble(df4.format((d1 + d2) / 2.0));
		} else {
			return dataList[(len - 1) / 2];
		}
	}

	public static float getMedian(float[] dataList) {
		if (dataList.length == 0)
			return 0;

		Arrays.sort(dataList);
		int len = dataList.length;
		if (len % 2 == 0) {
			double d1 = dataList[len / 2 - 1];
			double d2 = dataList[len / 2];
			return Float.parseFloat(df4.format((d1 + d2) / 2.0f));
		} else {
			return dataList[(len - 1) / 2];
		}
	}

	public static double[] minusBG(Double[] data, int length, double thres) {
		double[] backGround = new double[data.length];
		ArrayList<Double> temp = new ArrayList<Double>();
		ArrayList<Double> testData = new ArrayList<Double>();

		temp.add(data[0]);
		testData.add(data[0]);

		for (int i = 1; i < data.length; i++) {

			temp.add(data[i]);
			double ratio = getVarRatio(temp);

			if (ratio < 2.01)
				testData.add(data[i]);
			else
				temp.remove(data[i]);

			if (testData.size() == length)
				break;
		}

		for (int i = 0; i < data.length; i++) {
			double di = data[i];
			double ave = getAveInDouble(testData);
			backGround[i] = Double.parseDouble(df4.format(di - ave));
			// System.out.println(di+"~"+ave+"~"+backGround[i]);
			testData.add(di);
			double ratio = getVarRatio(testData);
			if (ratio > thres) {
				testData.remove(di);
			} else {
				testData.remove(0);
			}
		}

		return backGround;

	}

	/**
	 * For "static" average window.
	 * 
	 * @param data
	 * @param length
	 * @param thres
	 */
	public static Double[][] minusBG2(Double[] data) {

		Arrays.sort(data);
		ArrayList<Double> temp = new ArrayList<Double>(data.length);
		temp.add(data[0]);
		temp.add(data[1]);
		temp.add(data[2]);
		double t1 = getVarRatio(temp);
		int pos = 0;

		for (int i = 3; i < data.length; i++) {
			temp.add(data[i]);
			double t2 = getVarRatio(temp);
			if (t2 / t1 > 1.5) {
				pos = i;
				break;
			} else {
				t1 = t2;
			}
		}

		temp.remove(pos);
		double ave = getAveInDouble(temp);
		Double[][] sig = new Double[2][data.length];

		for (int j = 0; j < data.length; j++) {
			sig[0][j] = data[j];
			sig[1][j] = Double.parseDouble(df4.format(data[j] - ave));
			// System.out.println(sig[1][j]);
		}
		return sig;
	}

	public static double getX(double a) {
		double x;
		double d = Math.sqrt(a * a - a * 2);
		x = a - d;
		return x;
	}

	public static boolean hasSimilar(ArrayList<Double> douList) {

		for (int i = 0; i < douList.size(); i++) {
			double di = douList.get(i);
			for (int j = i + 1; j < douList.size(); j++) {
				double dj = douList.get(j);
				if (Double.parseDouble(df6.format(Math.abs(di - dj))) < 0.01)
					return true;
			}
		}

		return false;
	}

	public static double getFilterAveInDouble(ArrayList<Double> dataList) {
		ArrayList<Double> copylist = new ArrayList<Double>(dataList.size());
		copylist.addAll(dataList);
		ArrayList<Double> data;
		while (true) {
			if (copylist.size() < 4) {
				return getAveInDouble(copylist);
			}
			double threshold = getThres(copylist.size());
			data = getFilterData(copylist, threshold);
			if (data.size() == copylist.size()) {
				return (getAveInDouble(data) + getMedianInDouble(data)) / 2.0;
			} else {
				copylist = data;
			}
		}
	}

	public static double getFilterAve(ArrayList<Double> dataList, double threshold) {
		ArrayList<Double> copylist = new ArrayList<Double>(dataList.size());
		copylist.addAll(dataList);
		ArrayList<Double> data;
		while (true) {
			if (copylist.size() < 4) {
				return getAveInDouble(copylist);
			}
			data = getFilterData(copylist, threshold);
			if (data.size() == copylist.size()) {
				return (getAveInDouble(data) + getMedianInDouble(data)) / 2.0;
			} else {
				copylist = data;
			}
		}
	}

	public static ArrayList<Double> getFilterData(ArrayList<Double> dataList, double threshold) {
		ArrayList<Double> data = new ArrayList<Double>();
		double ave = getAveInDouble(dataList);
		for (int i = 0; i < dataList.size(); i++) {
			double d = dataList.get(i);
			if (d / ave < threshold && d / ave > (1.0 / threshold)) {
				data.add(d);
			}
		}
		if (data.size() == 0)
			return dataList;

		return data;
	}

	public static double getThres(int length) {
		return Math.log(length) / 6.0 + 1.5;
	}

	/**
	 * Using binomial distribution to calculate all the combination of the
	 * number in the list.
	 * 
	 * @param list
	 * @return
	 */
	public static HashSet<Double> doubleDiss(ArrayList<Double> list) {

		HashSet<Double> diss = new HashSet<Double>();
		int size = list.size();
		long num = (long) Math.pow(2.0, size) - 1;
		for (int i = 0; i <= num; i++) {
			int n = i;
			double d = 0;
			for (int j = 0; j < size; j++) {
				int k = n % 2;
				if (k == 1)
					d += list.get(j);
				n /= 2;
			}

			diss.add(d);
		}
		return diss;
	}

	public static double getMean2(double[][] data) {

		int len = data.length * data[0].length;
		if (len == 0)
			return 0;

		double total = 0;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				total += data[i][j];
			}
		}
		double mean = Double.parseDouble(df4.format(total / (double) len));
		return mean;
	}

	public static double getCorr2(double[][] a, double[][] b) {

		if (a.length != b.length || a[0].length != b[0].length)
			return 0;

		double meanA = getMean2(a);
		double meanB = getMean2(b);

		double d0 = 0;
		double da = 0;
		double db = 0;

		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				d0 += (a[i][j] - meanA) * (b[i][j] - meanB);
				da += (a[i][j] - meanA) * (a[i][j] - meanA);
				db += (b[i][j] - meanB) * (b[i][j] - meanB);
			}
		}

		if (da == 0 || db == 0)
			return 1;

		double r = Double.parseDouble(df4.format(d0 / Math.pow(da + db, 0.5)));
		return r;
	}

	public static void main(String[] args) throws IOException {
		/*
		 * BufferedReader reader = new BufferedReader(new
		 * FileReader("F:\\data\\Glyco\\peptide" + "\\�½� �ı��ĵ�.txt"));
		 * ArrayList <Double> list = new ArrayList <Double>(); String s;
		 * while((s=reader.readLine())!=null){ String [] ss = s.split("\\s+");
		 * double d1 = Double.parseDouble(ss[0]); double d2 =
		 * Double.parseDouble(ss[1]); list.add(d2/d1); }
		 * System.out.println(MathTool.getFilterAve(list, 1.5));
		 */
//		for (int i = 5; i < 1000; i++) {
//			System.out.println(i + "\t" + getThres(i));
//		}
		
		double[] intensities = new double[] { 7.0, 15.0, 36.0, 22.0, 12.0, 30.0, 7.0, 9.0, 14.0, 12.0, 20.0, 6.0, 2.0,
				4.0, 1.0, 24.0, 4.0, 14.0, 24.0, 10.0 };
		System.out.println(MathTool.getRSD(intensities));
	
	}

}
