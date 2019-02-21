/*
 ******************************************************************************
 * File: RandomPCalor.java * * * Created on 02-18-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.math;

import org.apache.commons.math3.distribution.BinomialDistribution;

/**
 * A random event probability calculator; To calculate the probability for the
 * event to be a random one; As return, when the p value is 1, it indicated that
 * this event is an random event
 * 
 * Commonly, when the p value is lower than 0.01, the confidence for this event
 * is bigger than 99%
 * 
 * See as PNAS, 2004,9,14, 13417-13422
 * 
 * @author Xinning
 * @version 0.1, 02-18-2009, 14:23:56
 */
public class RandomPCalor {

	/**
	 * -10*Math.log10(randomprobility)
	 * 
	 * @param randomprobility
	 *            ��Ϊһ������¼��ļ���
	 * @return ƥ���¼��ĵ÷֣��÷�Խ�ߣ�˵��ƥ������ȷ�ļ���Խ�ߣ�����������¼�
	 */
	public static double getScore(double randomprobility) {
		return -10.0 * Math.log10(randomprobility);
	}

	/**
	 * -10*Math.log10(randomprobility)
	 * 
	 * @param trials
	 *            �ܹ��ж��ٸ�ƥ����¼���
	 * @param success
	 *            �ɹ�ƥ����¼���
	 * @param singlep
	 *            ����ƥ���¼���������ʣ�
	 * @return ƥ���¼��ĵ÷֣��÷�Խ�ߣ�˵��ƥ������ȷ�ļ���Խ�ߣ�����������¼�
	 */
	public static double getScore(int trials, int success, double singlep) {
		return getScore(getProbility(trials, success, singlep));
	}

	/**
	 * 
	 * @param trials
	 *            �ܹ��ж��ٸ�ƥ����¼���
	 * @param success
	 *            �ɹ�ƥ����¼���
	 * @param singlep
	 *            ����ƥ���¼���������ʣ�
	 * @return ��Ϊһ������¼��ļ��ʣ�
	 */
	public static double getProbility(int trials, int success, double singlep) {
		double p = 1.0;
		if (success == 0)
			return p;

		p = nk(trials, success) * Math.pow(singlep, success)
		        * Math.pow((1.0 - singlep), (trials - success));
		return p;
	}
	
	public static void main(String[] args) {
		
		System.out.println(getScore(6, 3, 0.6));
		System.out.println(getProbility(6, 3, 0.6));
		System.out.println(getProbility(6, 0, 0.06));
	}

	/**
	 * 
	 * (n)
	 * (k) = n!/(k!*(n-k)!);
	 * 
	 * @param k&n >0;
	 * @return n!/(k!*(n-k)!)
	 */
	public static double nk(int n, int k) {

		if (n <= 0 || n < k || k <= 0)
			throw new RuntimeException("N k format error: n-" + n + ", k-" + k);

		if (k == n)
			return 1;

		return calculatenk(n, k);

	}

	private static double calculatenk(int n, int k) {
		double value = 1.0;
		int j = n - k;

		if (2 * k < n) {
			for (int i = n; i > j; i--) {
				value *= i;
			}

			for (int i = 2; i <= k; i++) {
				value /= i;
			}
		} else {
			for (int i = n; i > k; i--) {
				value *= i;
			}

			for (int i = 2; i <= j; i++) {
				value /= i;
			}
		}

		return value;
	}
}
