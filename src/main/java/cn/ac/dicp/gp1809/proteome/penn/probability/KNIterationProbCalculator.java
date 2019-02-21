/* 
 ******************************************************************************
 * File: KNIterationProbCalculator.java * * * Created on 12-25-2007
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

import cn.ac.dicp.gp1809.proteome.penn.probability.wekakd.MyInstance;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.KDTree;
import cn.ac.dicp.gp1809.util.SmartTimer;


/**
 * First compute the basic probability using B_NEAR instances, then
 * iterating the probability until a specific precise.
 * 
 * 
 * Known problems:
 * 1. Solve the exceptions when there are more than B_NEAR count of instance with the same
 * score vector as the query instance, that is, the distance between them is 0.
 * 
 * 
 * 
 * @author Xinning
 * @version 0.4, 06-09-2008, 16:55:42
 */
public class KNIterationProbCalculator implements IPeptideProbCalculator {
	
	/**
	 * The normally k nearest neighbors to be catch.
	 */
	private final static int K_NEAR = 900;
	
	/**
	 * which distance is used for weighting calculation.
	 * must smaller than K_NEAR (at least 1);
	 */
	private final static int B_NEAR = 199;
	
	/**
	 * The precise for probability calculation
	 */
	private final static double precise = 0.001d;
	
	
	private final static int multiplier = 2;
	
	/* 
	 * 
	 * �������ʹ�ñ߽�ֵ��������
	 * 
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.probability.ProbCalculator#
	 * 							calculate(cn.ac.dicp.gp1809.proteome.probability.PepNorm[])
	 * 
	 */
	@Override
	public void calculate(PepNorm[] peps) {
		
		int len = peps.length;
		
		if(len<=B_NEAR+1){
			System.out.println("The total instance number:"+len+" is not bigger the K_NEAR:"+(B_NEAR+1)
					+". Skip calculation.");
			return ;
		}
		
		int knear = Math.min(len-1, K_NEAR);
		
		System.out.println("Peptides: "+len+"\r\n"+"Calculating probability ...");
		SmartTimer t1 = new SmartTimer();
		
		AbstractDistanceCalculator dcalor = new EntropyDistanceCalculator(peps);
		
		FastVector attInfo = new FastVector();
		if(dcalor.isXcorrUsed())
			attInfo.addElement(new Attribute("Xcorr"));
		if(dcalor.isDcnUsed())
			attInfo.addElement(new Attribute("Dcn"));
		if(dcalor.isSpUsed())
			attInfo.addElement(new Attribute("Sp"));
		if(dcalor.isRspUsed())
			attInfo.addElement(new Attribute("Rsp"));
		if(dcalor.isDeltaMsUsed())
			attInfo.addElement(new Attribute("Dms"));
		if(dcalor.isIonsUsed())
			attInfo.addElement(new Attribute("Ions"));
		if(dcalor.isMPFUsed())
			attInfo.addElement(new Attribute("MPF"));
		if(dcalor.isSimUsed())
			attInfo.addElement(new Attribute("Sim"));
		
		KDTree kdtree = new KDTree();
		kdtree.setNormalizeNodeWidth(false);
		
		int size = attInfo.size();
		
		System.out.println(attInfo.size());
		
		Instances instances = new Instances("Datasets", attInfo, len);
		for(int i=0;i<len;i++){
			PepNorm pep = peps[i];
			double[] axis = new double[size];
			
			int c = 0;
			if(dcalor.isXcorrUsed())
				axis[c++] = pep.getXcn()*dcalor.getXcweight();
			if(dcalor.isDcnUsed())
				axis[c++] = pep.getDcn()*dcalor.getDcnweight();
			if(dcalor.isSpUsed())
				axis[c++] = pep.getSpn()*dcalor.getSpweight();
			if(dcalor.isRspUsed())
				axis[c++] = pep.getRspn()*dcalor.getRspweight();
			if(dcalor.isDeltaMsUsed())
				axis[c++] = pep.getDMS()*dcalor.getDmsweight();
			if(dcalor.isIonsUsed())
				axis[c++] = pep.getIons()*dcalor.getIonsweight();
			if(dcalor.isMPFUsed())
				axis[c++] = pep.getMPF()*dcalor.getMPFweight();
			if(dcalor.isSimUsed())
				axis[c++] = pep.getSim()*dcalor.getSimweight();

			Instance ist = new MyInstance(axis,i, pep.isRev);
			instances.add(ist);
		}
		
		
		EuclideanDistance udist = new EuclideanDistance(instances);
		udist.setDontNormalize(true);
		
		
		try {
			kdtree.setDistanceFunction(udist);
			kdtree.setInstances(instances);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("KDTree builded in "+t1);
		
		try {
			for(int i=0;i<len;i++){
				MyInstance ist = (MyInstance)instances.instance(i);
	
				Instances nears = kdtree.kNearestNeighbours(ist, knear);
				double[] dists = kdtree.getDistances();
				
				/*
				 * Used for distance weight calculation
				 */
				double bounds = dists[B_NEAR-1];
				
				
				
//				if(bounds <= 1e-15){//precision of double
					
//				}
				
				double tweight = 1d;
				double tvalue = 0d;
				
				PepNorm p = peps[ist.getIdx()];
				//calculate the probability
				
				double weight = 1d;
				tvalue += p.isRev ? -weight : weight;
				for(int m=0;m<B_NEAR;m++){
					MyInstance mi = (MyInstance)nears.instance(m);
					double d = dists[m];
					
					weight = bounds/(bounds+d);
					tweight += weight;
					tvalue += mi.isRev() ? -weight : weight;
				}
				
				double prob1 = tvalue/tweight;
				p.probablity = (float)this.iterateProbability(prob1,tweight, tvalue, 
								ist, nears, dists, knear, B_NEAR, bounds, kdtree);
				p.count = count;
//				System.out.println(count);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished in "+t1);
		
	}
	
	/**
	 * Ϊ�˼�����ֵ����Ȩ�صķ����Ƿ���Ч��������peptideprophet��ͬ�ķ����ȼ���discriminating score��Ȼ�����
	 * ��ͬ�ķ�������kdtree ������⡣
	 * @param peps
	 */
	public void calcuatedVerDiscrimatingScore(PepNorm[] peps){
		int len = peps.length;
		
		if(len<=B_NEAR+1){
			System.out.println("The total instance number:"+len+" is not bigger the K_NEAR:"+(B_NEAR+1)
					+". Skip calculation.");
			return ;
		}
		
		int knear = Math.min(len-1, K_NEAR);
		
		System.out.println("Peptides: "+len);
		System.out.println("Calculating probability verus discriminating score ...");
		
		SmartTimer t1 = new SmartTimer();
		
		AbstractDistanceCalculator dcalor = new GADistanceCalculator(peps);
		
		FastVector attInfo = new FastVector();
		attInfo.addElement(new Attribute("Discriminating score"));
		
		KDTree kdtree = new KDTree();
		kdtree.setNormalizeNodeWidth(false);
		
		Instances instances = new Instances("Datasets", attInfo, len);
		for(int i=0;i<len;i++){
			PepNorm pep = peps[i];
			double away = 0d;
			
			if(dcalor.isXcorrUsed())
				away += pep.getXcn()*pep.getXcn()*dcalor.getXcw2();
			if(dcalor.isDcnUsed())
				away += pep.getDcn()*pep.getDcn()*dcalor.getDcnw2();
			if(dcalor.isSpUsed())
				away += pep.getSpn()*pep.getSpn()*dcalor.getSpw2();
			if(dcalor.isRspUsed())
				away += pep.getRspn()*pep.getRspn()*dcalor.getRspw2();
			if(dcalor.isDeltaMsUsed())
				away += pep.getDMS()*pep.getDMS()*dcalor.getDmsw2();
			if(dcalor.isIonsUsed())
				away += pep.getIons()*pep.getIons()*dcalor.getIonsw2();
			
			Instance  ist = new MyInstance(new double[]{away} ,i, pep.isRev);
			instances.add(ist);
		}
		
		
		EuclideanDistance udist = new EuclideanDistance(instances);
		udist.setDontNormalize(true);
		
		
		try {
			kdtree.setDistanceFunction(udist);
			kdtree.setInstances(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("KDTree builded in "+t1);
		
		try {
			for(int i=0;i<len;i++){
				MyInstance ist = (MyInstance)instances.instance(i);
	
				Instances nears = kdtree.kNearestNeighbours(ist, knear);
				double[] dists = kdtree.getDistances();
				
				/*
				 * Used for distance weight calculation
				 */
				double bounds = dists[B_NEAR-1];
				
				double tweight = 1d;
				double tvalue = 0d;
				
				PepNorm p = peps[ist.getIdx()];
				//calculate the probability
				
				double weight = 1d;
				tvalue += p.isRev ? -weight : weight;
				for(int m=0;m<B_NEAR;m++){
					MyInstance mi = (MyInstance)nears.instance(m);
					double d = dists[m];
					
					weight = bounds/(bounds+d);
					tweight += weight;
					tvalue += mi.isRev() ? -weight : weight;
				}
				
				double prob1 = tvalue/tweight;
				p.probablity = (float)this.iterateProbability(prob1,tweight, tvalue, 
								ist, nears, dists, knear, B_NEAR, bounds, kdtree);
				p.count = count;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished in "+t1);
	}
	
	private int count;
	
	/**
	 * Iterate the probability until it is with specific precise.
	 * 
	 * @param prob1 previous probability
	 * @param tweight total weight
	 * @param tvalue total value
	 * @param curt current instance
	 * @param instances nearest instances
	 * @param dists distances of the nearest instances.
	 * @param knear the number of nearest instances
	 * @param curtnearidx current index of the current used nearest neighbor
	 * @param bounds the bounds for distance weighting calculation
	 * @param kdtree the kd tree
	 * @return probability with specific precise.
	 * @throws Exception from k nearest neighbor generation.
	 */
	private double iterateProbability(double prob1, double tweight, double tvalue, MyInstance curt,
				Instances instances, double[] dists, int knear, int curtnearidx,
				double bounds, KDTree kdtree) throws Exception{

		int max = kdtree.getInstances().numInstances()-1;
		double preprob = prob1;
		double prob = 0d;
		for(int i=curtnearidx;i<=max;i++){
			
			//another more widely ranged neighbors.
			if(i>=knear){
				knear *= multiplier;
				if(knear>=max){
					if(i>=max){
						System.out.println("All instances are used for probability calculation: idx"
										+ curt.getIdx()+"; Prob"+preprob);
						count = max+1;
						return preprob < 0d ? 0d : preprob;
					}
					
					knear = max;
				}
				
				instances = kdtree.kNearestNeighbours(curt,knear);
				dists = kdtree.getDistances();
			}
			
			double weight = bounds/(bounds+dists[i]);
			tweight += weight;
			tvalue +=((MyInstance)instances.instance(i)).isRev() ? -weight : weight;
			
			prob = tvalue/tweight;
			
			//for peptide with probability less than 1, at least 400 peptides should be used
//			if(prob >= 1d || i>399){
				if(Math.abs(preprob-prob)<precise){
					prob = (prob+preprob)/2d;
					count = i+2;
					break;
				}
//			}
			
			preprob = prob;
		}
		
		return prob < 0d ? 0d : prob;
	}
	
	
	
	private final static int MIN_COUNT = 200;
	
	/**
	 * ʹ�þ���ĵ�����ΪȨ�أ����м���
	 * 
	 * @param peps
	 */
	public void calculateVerDist(PepNorm[] peps) {
		
		int len = peps.length;
		
		if(len<=MIN_COUNT+1){
			System.out.println("The total instance number:"+len+" is not bigger the K_NEAR:"+(MIN_COUNT+1)
					+". Skip calculation.");
			return ;
		}
		
		int knear = Math.min(len-1, K_NEAR);
		
		System.out.println("Peptides: "+len+"\r\n"+"Calculating probability ...");
		SmartTimer t1 = new SmartTimer();
		
		AbstractDistanceCalculator dcalor = new GADistanceCalculator(peps);
		
		FastVector attInfo = new FastVector(dcalor.getAllScaleCount());
		attInfo.addElement(new Attribute("Xcorr"));
		attInfo.addElement(new Attribute("Dcn"));
		attInfo.addElement(new Attribute("Sp"));
		attInfo.addElement(new Attribute("Rsp"));
		attInfo.addElement(new Attribute("Dms"));
		
		KDTree kdtree = new KDTree();
		kdtree.setNormalizeNodeWidth(false);
		
		Instances instances = new Instances("Datasets", attInfo, len);
		for(int i=0;i<len;i++){
			PepNorm pep = peps[i];
			Instance  ist = new MyInstance(new double[]{pep.xcn*dcalor.getXcweight(),
					pep.dcn*dcalor.getDcnweight(),pep.spn*dcalor.getSpweight(),pep.rspn*dcalor.getRspweight()
					,pep.dMS*dcalor.getDmsweight()} ,i, pep.isRev);
			instances.add(ist);
		}
		
		
		EuclideanDistance udist = new EuclideanDistance(instances);
		udist.setDontNormalize(true);
		
		
		try {
			kdtree.setDistanceFunction(udist);
			kdtree.setInstances(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("KDTree builded in "+t1);
		
		try {
			for(int i=0;i<len;i++){
				MyInstance ist = (MyInstance)instances.instance(i);
	
				Instances nears = kdtree.kNearestNeighbours(ist, knear);
				double[] dists = kdtree.getDistances();
				
				double tweight = 0d;
				double tvalue = 0d;
				
				PepNorm p = peps[ist.getIdx()];
				//calculate the probability
				
				double td = 0d;
				for(int m=0;m<B_NEAR;m++){
					double d = dists[m];
					td += d;
				}
				
				double weight = B_NEAR/td;
				tweight += B_NEAR*weight;

				for(int m=0;m<B_NEAR;m++){
					MyInstance mi = (MyInstance)nears.instance(m);
					tvalue += mi.isRev() ? -weight : weight;
				}
				
				for(int j=B_NEAR;j<MIN_COUNT;j++){
					weight = 1/dists[j];
					tweight += weight;
					tvalue +=((MyInstance)instances.instance(i)).isRev() ? -weight : weight;
				}
				
				double prob1 = tvalue/tweight;
				p.probablity = (float)this.iterateProbability(prob1,tweight, tvalue, 
								ist, nears, dists, knear, MIN_COUNT, kdtree);
				p.count = count;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished in "+t1);
		
	}
	
	/**
	 * Iterate the probability until it is with specific precise.
	 * 
	 * @param prob1 previous probability
	 * @param tweight total weight
	 * @param tvalue total value
	 * @param curt current instance
	 * @param instances nearest instances
	 * @param dists distances of the nearest instances.
	 * @param knear the number of nearest instances
	 * @param curtnearidx current index of the current used nearest neighbor
	 * @param bounds the bounds for distance weighting calculation
	 * @param kdtree the kd tree
	 * @return probability with specific precise.
	 * @throws Exception from k nearest neighbor generation.
	 */
	private double iterateProbability(double prob1, double tweight, double tvalue, MyInstance curt,
				Instances instances, double[] dists, int knear, int curtnearidx,
				KDTree kdtree) throws Exception{

		int max = kdtree.getInstances().numInstances()-1;
		double preprob = prob1;
		double prob = 0d;
		for(int i=curtnearidx;i<=max;i++){
			
			//another more widely ranged neighbors.
			if(i>=knear){
				knear *= multiplier;
				if(knear>=max){
					if(i>=max){
						System.out.println("All instances are used for probability calculation: idx"
										+ curt.getIdx()+"; Prob"+preprob);
						count = max+1;
						return preprob < 0d ? 0d : preprob;
					}
					
					knear = max;
				}
				
				instances = kdtree.kNearestNeighbours(curt,knear);
				dists = kdtree.getDistances();
			}
			
			double weight = 1/dists[i];
			tweight += weight;
			tvalue +=((MyInstance)instances.instance(i)).isRev() ? -weight : weight;
			
			prob = tvalue/tweight;
			
			if(Math.abs(preprob-prob)<precise){
				prob = (prob+preprob)/2d;
				count = i+2;
				break;
			}
			
			preprob = prob;
		}
		
		return prob < 0d ? 0d : prob;
	}
}
