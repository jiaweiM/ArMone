/*
 * *****************************************************************************
 * File: file_name * * * Created on 10-10-2007 Copyright (c) 2007 Xinning Jiang
 * vext@163.com This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.penn.probability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cn.ac.dicp.gp1809.proteome.penn.probability.wekakd.MyInstance;
import cn.ac.dicp.gp1809.proteome.penn.probability.PepNorm.Distant;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.KDTree;

import cn.ac.dicp.gp1809.util.SmartTimer;

/**
 * By setting a value for how many points (peptides) should be pooled together,
 * the probability can be calculated from the pooled peptides in this cluster.
 * 
 * 
 * @author Xinning
 * @version 0.5, 12-02-2007, 16:41:53
 */
public class KN_Cluster_ProbCalculator implements IPeptideProbCalculator{
	
	/**
	 * How many nearest points are used to calculate the probability.
	 */
	public static int K_NEAR = 500;
	
	
	/**
	 * Calculate the peptide probability without Standard deviation
	 * @param peps PepNorm[]
	 */
	public void calculate(PepNorm[] peps){
		this.calculate2(peps);
	}
	
	
	
	/**
	 * Calculate the peptide probability without Standard deviation
	 * @param peps PepNorm[]
	 */
	public void calculate1(PepNorm[] peps){
		int len = peps.length;
		System.out.println("Peptides: "+len+"\r\n"+"Calculating probability ...");
		SmartTimer t1 = new SmartTimer();
		
		EntropyDistanceCalculator dcalor = new EntropyDistanceCalculator(peps);
		
		/*
		 * There are not enough peptides for the computing of probability.
		 */
		if(len<=K_NEAR){
			System.out.println("The total pepNorm number:"+len+" is less than the K_NEAR:"+K_NEAR
					+". Skip calculating.");
			return ;
		}
		
		for(int i=0;i<len;i++){
			PepNorm curt = peps[i];
			Distant[] distants = getNearDistants(dcalor,curt,peps);
			
			//calculate the probability
			int rev=0;
			for(int m=0;m<K_NEAR;m++){
				Distant tdis = distants[m];
				
				if(curt.idx == 2)
					System.out.println(peps[tdis.idx]+" "+tdis.distant);
				
				if(peps[tdis.idx].isRev){
					rev++;
				}
			}

			int fal = K_NEAR-2*rev;
			curt.probablity = (fal<=0 ? 0f : fal)/K_NEAR;
		}
		
//		System.out.println("ReCalculate the probability from probability cluster ...");
//		this.reCalculate(peps);
		
		System.out.println("Finished in "+t1);
	}
	
	/**
	 * Calculate the peptide based on a KDtree
	 * <b>Using KD tree</b>
	 * @param peps PepNorm[]
	 */
	public void calculate2(PepNorm[] peps){
		int len = peps.length;
		System.out.println("Peptides: "+len+"\r\n"+"Calculating probability ...");
		SmartTimer t1 = new SmartTimer();
		
		EntropyDistanceCalculator dcalor = new EntropyDistanceCalculator(peps);
		FastVector attInfo = new FastVector(dcalor.getAllScaleCount());
		attInfo.addElement(new Attribute("Xcorr"));
		attInfo.addElement(new Attribute("Dcn"));
		attInfo.addElement(new Attribute("Sp"));
		attInfo.addElement(new Attribute("Rsp"));
		attInfo.addElement(new Attribute("Dms"));
		
		KDTree kdtree = new KDTree();
		kdtree.setNormalizeNodeWidth(false);
		
		Instances instances = new Instances("Datasets", attInfo, peps.length);
		for(int i=0;i<peps.length;i++){
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
		
		int knear = K_NEAR-1;
		
		try {
			for(int i=0;i<instances.numInstances();i++){
				MyInstance ist = (MyInstance)instances.instance(i);
	
				Instances nears = kdtree.kNearestNeighbours(ist, K_NEAR);
				PepNorm p = peps[ist.getIdx()];
				//calculate the probability
				int rev=0;
				if(p.isRev) rev ++;
				for(int m=0;m<knear;m++){
					if(((MyInstance)nears.instance(m)).isRev()){
						rev++;
					}
				}
	
				int fal = K_NEAR-2*rev;
				peps[ist.getIdx()].probablity = (fal<=0 ? 0f : fal)/K_NEAR;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished in "+t1);
	}
	
	/*
	 * Temp file to write the distant array.
	 */
	private final static String DISTFILE = "disttmp.tmp";
	/*
	 * The size of the byte buffer
	 * The bytes for a distant is 8, thus, it should write once for a peptide's
	 * distant array 
	 */
	private final static int BUFFER_SIZE = (K_NEAR-1)*8;
	/**
	 * @param peps
	 */
	@SuppressWarnings("unused")
	private void calculateVsSD(PepNorm[] peps){
		System.out.println("Calculating probability ...");
		SmartTimer t1 = new SmartTimer();
		EntropyDistanceCalculator dcalor = new EntropyDistanceCalculator(peps);
		int len = peps.length;
		
		/*
		 * There are not enough peptides for the computing of probability.
		 */
		if(len<=K_NEAR){
			System.out.println("The total pepNorm number:"+len+" is less than the K_NEAR:"+K_NEAR
					+". Skip calculating.");
			return ;
		}
		
		File tmpFile = new File( System.currentTimeMillis()+DISTFILE);
		
		try{
		FileChannel channel = null;
		channel = new FileOutputStream(tmpFile).getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		for(int i=0;i<len;i++){
			PepNorm curt = peps[i];
			Distant[] distants = getNearDistants(dcalor,curt,peps);

			//In the distant matrix, the same point was also calculated for value.
			//so the nearest point for a point is itself and it should be in the first position 
			//of the distant array.
			
			//calculate the probability
			int rev=0;
			for(int m=0;m<K_NEAR;m++){
				Distant dis = distants[m];
				
				buffer.putInt(dis.idx);
				buffer.putFloat(dis.distant);
				
				if(peps[distants[m].idx].isRev)
					rev++;
			}
			
			//Write to temp file
			buffer.flip();
			channel.write(buffer);
			buffer.clear();
			
			int fal = K_NEAR-2*rev;
			curt.probablity = (fal<=0 ? 0f : fal)/K_NEAR;
		}
		channel.close();
		
		calSD(peps, tmpFile);
		
		tmpFile.delete();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println("Finished in "+t1);
	}
	
	/**
	 * Used to test whether two float values are equal.
	 */
	public final static  float EQUF = 0.000001f;
	
	/**
	 * Recalculate the probability by cluster the band of peptides with
	 * same probability which calculated from K_NEAR compution.
	 * @param peps
	 */
	@SuppressWarnings("unused")
	private void reCalculate(PepNorm[] peps){
		PepNorm[] copy = peps.clone();
		
		//Sort by the probability, from small to big
		Arrays.sort(copy,new Comparator<PepNorm>(){

			public int compare(PepNorm o1, PepNorm o2) {
				if(o1.probablity==o2.probablity)
					return 0;
				return o1.probablity>o2.probablity?1:-1;
			}
			
		});
		
		ArrayList<ProbCluster> list = new ArrayList<ProbCluster>(K_NEAR);
		float preProb = copy[0].probablity;
		int count = 1;
		int falsecount = copy[0].isRev?1:0;
		int from = 0;
		
		for(int i=1;i<copy.length;i++){
			PepNorm tp = copy[i];
			float p = tp.probablity;
			float gap = Math.abs(p-preProb);
			if(gap<EQUF){//equals
				count ++;
				if(tp.isRev)
					falsecount++;
			}
			else{
				list.add(new ProbCluster(preProb,count,falsecount,from,i));
				
				preProb = p;
				count = 1;
				falsecount = tp.isRev?1:0;
				from = i;
			}
		}
		list.add(new ProbCluster(preProb,count,falsecount,from,copy.length));
		
		int size = list.size();
		for(int i=1;i<size;i++){//0 is the cluster with the probability of 0;
			ProbCluster pc = list.get(i);
			int tcount = pc.getCount();
			int fcount = pc.getFalsecount();
			float newp;
			if(tcount<K_NEAR){
				int pre = i;
				int aft = i;
				while(true){
					boolean deadcir = true;
					pre--;
					aft++;
					if(pre>0){
						ProbCluster tpc = list.get(pre);
						tcount += tpc.getCount();
						fcount += tpc.getFalsecount();
						deadcir  = false;
					}
					if(aft<size){
						ProbCluster tpc = list.get(aft);
						tcount += tpc.getCount();
						fcount += tpc.getFalsecount();
						deadcir = false;
					}
					
					if(tcount>=K_NEAR||deadcir)
						break;
				}
			}
			
			newp = 1f-fcount*2f/tcount;
			for(int j=pc.getFrom();j<pc.getTo();j++){
				copy[j].probablity = newp<0f ? 0f: newp;
			}
		}
	}
	
	/**
	 * The cluster of pepnorm information with the specific probability
	 * 
	 * @author Xinning
	 * @version 0.1, 11-12-2007, 10:06:04
	 */
	private static class ProbCluster{
		private float prob;
		private int count;
		private int falsecount;
		private int from;
		private int to;
		private ProbCluster(float prob, int count, int falsecount, int from, int to) {
			super();
			this.prob = prob;
			this.count = count;
			this.falsecount = falsecount;
			this.from = from;
			this.to = to;
		}
		
		
		/**
		 * @return the from
		 */
		public int getFrom() {
			return from;
		}

		
		/**
		 * @return the to
		 */
		public int getTo() {
			return to;
		}

		/**
		 * @return the prob
		 */
		public float getProb() {
			return prob;
		}
		
		/**
		 * @return the count
		 */
		public int getCount() {
			return count;
		}
		
		/**
		 * @return the falsecount
		 */
		public int getFalsecount() {
			return falsecount;
		}
		
	}
	
	
	/**
	 * Calculate the bias and remove the distant array.
	 * @param peps
	 * @throws IOException 
	 */
	private static void calSD(PepNorm[] peps, File tmpFile) throws IOException{
		
		FileChannel channel = null;
		channel = new FileInputStream(tmpFile).getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		for(int i=0;i<peps.length;i++){
			PepNorm pep = peps[i];
			float curtp = pep.probablity;
			long position = (long)i*BUFFER_SIZE;
			float bias = 0f;
			
			buffer.clear();
			channel.read(buffer, position);
			buffer.flip();
			
			while(buffer.hasRemaining()){
				int idx = buffer.getInt();
				buffer.getFloat();//currently no use
				
				float p = peps[idx].probablity;
				float d = curtp-p;
				bias += d*d;
			}
			
//			pep.SD = (float) Math.pow(bias/K_NEAR,0.5d);
		}
		
		channel.close();
	}

	//---------------------------------------------------------//
	
	/*
	 * The folder of extension when the elements containing within the initialed 
	 * distant is smaller than the knear value;
	 */
	private static float EXTENSION = 1.5f;

	/*
	 * This value is used to format the proper value to curt the distant array.
	 * For efficiency, the number of elements in k_near distant array should not so 
	 * small (another circulation may be need) and not so big (waste time in array sort).
	 */
	private int longKNear = K_NEAR + K_NEAR/5;
	
	private int count = 0;
	/*
	 * Current used distant.
	 * start with a small distant
	 */
	private float dist = 0.05f;
	
	/**
	 * Return the nearest individual, 
	 * 
	 * @param dcalor
	 * @param curt
	 * @param peps
	 * @param curtidx
	 * @param knear
	 * @return
	 */
	protected Distant[] getNearDistants(EntropyDistanceCalculator dcalor, PepNorm curt, PepNorm[] peps){
		int len = peps.length;
		/*
		 * The number of total peptide is smaller than that of the knear.
		 * Then return all the distant;
		 */
		if(K_NEAR >= len){
			PepNorm.Distant[] dists = new Distant[len];
			for(int j=0;j<len;j++){
				PepNorm p = peps[j];
				float d2 = dcalor.calculateD2(curt,p);
				dists[j] = new Distant(d2,j);
			}
			return dists;
		}

		float temp = dist;
		ArrayList<Distant> distlist = new ArrayList<Distant>(longKNear);
		for(int j=0;j<len;j++){
			PepNorm p = peps[j];
			float d2 = dcalor.calculateD2(curt,p);
			if(d2<dist)
				distlist.add(new Distant(d2,j));
		}
		
		int c = distlist.size();
		Distant[] distants;
		if(c<K_NEAR){
			dist *= EXTENSION;//extends 
			distants = getNearDistants(dcalor,curt,peps,dist);
		}
		else
			distants = distlist.toArray(new Distant[distlist.size()]);
		
		//from small to big
		Arrays.sort(distants);
		
		//readjust the dist;
		if(c>longKNear){
			dist = (temp*count+distants[longKNear].distant)/(++count);
		}
		else{
			//To avoid the so-max value.
			dist = (temp*count+temp*EXTENSION)/(++count);
		}

		return distants;
	}
	
	private Distant[] getNearDistants(EntropyDistanceCalculator dcalor, PepNorm curt, 
					PepNorm[] peps, double dis){
		int len = peps.length;
		
		ArrayList<Distant> distlist = new ArrayList<Distant>(longKNear);
		for(int j=0;j<len;j++){
			PepNorm p = peps[j];
			float d2 = dcalor.calculateD2(curt,p);
			if(d2<dist)
				distlist.add(new Distant(d2,j));
		}
		
		int c = distlist.size();
		if(c<K_NEAR){
			//extend the dist to 1.5 fold large
			dist *= EXTENSION;
			return getNearDistants(dcalor,curt,peps,dist);
		}
		
		return distlist.toArray(new Distant[distlist.size()]);
	}
}
