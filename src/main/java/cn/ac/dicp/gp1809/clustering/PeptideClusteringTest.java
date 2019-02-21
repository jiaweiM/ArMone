/* 
 ******************************************************************************
 * File:PeptideClustering.java * * * Created on 2012-7-27
 *
 * Copyright (c) 2011 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.penn.probability.wekakd.MyInstance;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import cn.ac.dicp.gp1809.group.MascotHtmlQueryGetter;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * @author ck
 *
 * @version 2012-7-27, 14:01:25
 */
public class PeptideClusteringTest {
	
	private IPeptideListReader reader;
	private String filename;
	
	public PeptideClusteringTest(String file) throws FileDamageException, IOException{
		this.reader = new PeptideListReader(file);
		this.filename = file;
	}
	
	public PeptideClusteringTest(File file) throws FileDamageException, IOException{
		this.reader = new PeptideListReader(file);
		this.filename = file.getAbsolutePath();
	}
	
	private void group() throws PeptideParsingException{

		int idx = 0;

		ArrayList <IPeptide> [] peplists = new ArrayList [18];
		for(int i=0;i<peplists.length;i++){
			peplists[i] = new ArrayList <IPeptide>();
		}
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int charge = pep.getCharge();
			int miss = pep.getMissCleaveNum();
			boolean mod = false;
			
			int length = PeptideUtil.getSequenceLength(seq);
			if(seq.length()-4>length)
				mod = true;
//			if(length!=6)
//				continue;
			
			if(pep.getRank()==1){

//	group by charge and miss cleave		
				
				if(mod){
					switch (charge){
					case 2:
						switch (miss){
						case 0:
							peplists[0].add(pep);
							break;
						case 1:
							peplists[1].add(pep);
							break;
						case 2:
							peplists[2].add(pep);
							break;
						}
						break;
					case 3:
						switch (miss){
						case 0:
							peplists[3].add(pep);
							break;
						case 1:
							peplists[4].add(pep);
							break;
						case 2:
							peplists[5].add(pep);
							break;
						}
						break;
					default:
						switch (miss){
						case 0:
							peplists[6].add(pep);
							break;
						case 1:
							peplists[7].add(pep);
							break;
						case 2:
							peplists[8].add(pep);
							break;
						}
						break;
					}
				}else{

					switch (charge){
					case 2:
						switch (miss){
						case 0:
							peplists[9].add(pep);
							break;
						case 1:
							peplists[10].add(pep);
							break;
						case 2:
							peplists[11].add(pep);
							break;
						}
						break;
					case 3:
						switch (miss){
						case 0:
							peplists[12].add(pep);
							break;
						case 1:
							peplists[13].add(pep);
							break;
						case 2:
							peplists[14].add(pep);
							break;
						}
						break;
					default:
						switch (miss){
						case 0:
							peplists[15].add(pep);
							break;
						case 1:
							peplists[16].add(pep);
							break;
						case 2:
							peplists[17].add(pep);
							break;
						}
						break;
					}
				
				}

//				peplists[idx].add(pep);
//				idx++;
//				if(idx==peplists.length)
//					idx=0;
			}
		}

		int totalTc = 0;
		int totalDc = 0;
		for(int i=0;i<peplists.length;i++){
			
			ArrayList <IPeptide> list = peplists[i];
			
			for(int k=0;k<50;k++){
				double totalScore = 0;
				int tc = 0;
				int dc = 0;
				int tc0 = 0;
				int dc0 = 0;
				for(int j=0;j<list.size();j++){
					IPeptide pepj = peplists[i].get(j);
					totalScore += pepj.getPrimaryScore();
					if(pepj.isTP()){
						tc0++;
						if(pepj.getPrimaryScore()>k)
							tc++;
					}else{
						dc0++;
						if(pepj.getPrimaryScore()>k)
							dc++;
					}
				}
				double ratio0 = (double)dc0/(double)tc0;
				double fdr = (double)dc/(double)tc;
				if(fdr<0.01){
					System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
							+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
					totalTc += tc;
					totalDc += dc;
					break;
				}
			}
		}
		
		System.out.println(totalTc+"\t"+totalDc);
	}
	
	private void group2() throws PeptideParsingException{

		ArrayList <IPeptide> [] peplists = new ArrayList [9];
		for(int i=0;i<peplists.length;i++){
			peplists[i] = new ArrayList <IPeptide>();
		}
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int charge = pep.getCharge();
			int miss = pep.getMissCleaveNum();

			if(pep.getRank()==1){
				
//	group by charge and miss cleave		
 			switch (charge){
				case 2:
					switch (miss){
					case 0:
						peplists[0].add(pep);
						break;
					case 1:
						peplists[1].add(pep);
						break;
					case 2:
						peplists[2].add(pep);
						break;
					}
					break;
				case 3:
					switch (miss){
					case 0:
						peplists[3].add(pep);
						break;
					case 1:
						peplists[4].add(pep);
						break;
					case 2:
						peplists[5].add(pep);
						break;
					}
					break;
				default:
					switch (miss){
					case 0:
						peplists[6].add(pep);
						break;
					case 1:
						peplists[7].add(pep);
						break;
					case 2:
						peplists[8].add(pep);
						break;
					}
					break;
				}
				
//				peplists[idx].add(pep);
//				idx++;
//				if(idx==peplists.length)
//					idx=0;
			}
		}

		int totalTc = 0;
		int totalDc = 0;
		int [] scoreThres = new int[peplists.length];
		
		for(int i=0;i<peplists.length;i++){
			
			ArrayList <IPeptide> list = peplists[i];
			
			for(int k=0;k<50;k++){
				double totalScore = 0;
				int tc = 0;
				int dc = 0;
				int tc0 = 0;
				int dc0 = 0;
				for(int j=0;j<list.size();j++){
					IPeptide pepj = peplists[i].get(j);
					totalScore += pepj.getPrimaryScore();
					if(pepj.isTP()){
						tc0++;
						if(pepj.getPrimaryScore()>k)
							tc++;
					}else{
						dc0++;
						if(pepj.getPrimaryScore()>k)
							dc++;
					}
				}
				double ratio0 = (double)dc0/(double)tc0;
				double fdr = (double)dc/(double)tc;
				if(fdr<0.01){
					System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
							+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
					totalTc += tc;
					totalDc += dc;
					scoreThres[i] = k;
					break;
				}
			}
		}
		
		System.out.println(totalTc+"\t"+totalDc);
		
/*		for(int k=0;k<50;k++){
			
			int totalTc0 = 0;
			int totalDc0 = 0;
			
			for(int i=0;i<peplists.length;i++){
					
				ArrayList <IPeptide> list = peplists[i];
				
				double totalScore = 0;

				for(int j=0;j<list.size();j++){
					
					IPeptide pepj = peplists[i].get(j);
					totalScore += pepj.getPrimaryScore();
					if(pepj.isTP()){
						if(pepj.getPrimaryScore()>k && pepj.getPrimaryScore()>scoreThres[i])
							totalTc0++;
					}else{
						if(pepj.getPrimaryScore()>k && pepj.getPrimaryScore()>scoreThres[i])
							totalDc0++;
					}
				}
			}
			
			double fdr = (double)totalDc0/(double)totalTc0;
			
			if(fdr<0.03){
				
				System.out.println(totalTc0+"\t"+totalDc0+"\t"+fdr+"\t"+k);
				break;
			}
		}
*/		
	}
	
	private void clustering0() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);

		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		
		Instances instances = creator.createInstances("data");

		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){

			int scannum = pep.getScanNumBeg();
			
			if(pep.getRank()==1){
				
				map.put(scannum+"_"+pep.getRank()+"_"+pep.getPrimaryScore(), pep);
//				IMS2PeakList peaklist = reader.getPeakLists()[0];
//				MyInstance mi = creator.createInstance(pep, peaklist);
				MyInstance mi = creator.createInstance(pep);
				instances.add(mi);
			}
		}
		
		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);
			
			for(int l=0;l<instances.numInstances();l++){
				
				MyInstance mil = (MyInstance) instances.get(l);
				int type = mil.getClusterType();
				
				if(thres[type]>=0 && mil.getScore()>thres[type]){
					IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
					System.out.println(peptide.getSequence()+"\t"+peptide.getPrimaryScore()+"\t"+peptide.getReferenceOutString());
//					System.out.println(peptide.isTP()+"\t"+!mil.isRev());
				}
			}
			
/*			totalTc = 0;
			totalDc = 0;
			
			for(int k=0;k<50;k++){

				int tc = 0;
				int dc = 0;
				int tc0 = 0;
				int dc0 = 0;
				
				for(int l=0;l<instances.numInstances();l++){
					
					MyInstance mil = (MyInstance) instances.get(l);
					int type = mil.getClusterType();
					
					if(thres[type]>=0 && mil.getScore()>thres[type]){
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
				}
				double ratio0 = (double)dc0/(double)tc0;
				double fdr = (double)dc/(double)tc;
				
				if(fdr<0.01){
					System.out.println(ratio0+"\t"+tc0+"\t"+dc0+"\t"
							+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t");
					
					totalTc += tc;
					totalDc += dc;
					break;
				}
			}
			System.out.println(totalTc+"\t"+totalDc);			
*/			
		}
/*		else{
			
			for(int j=0;j<num;j++){
				
				Instance ins = instances.instance(j);
				MyInstance mi = (MyInstance) ins;
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					
					if(mi.isRev()){
						dc0++;
						if(mi.getScore()>k)
							tc++;
					}else{
						tc0++;
						if(mi.getScore()>k)
							dc++;
					}
					
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)num+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+num);
						totalTc += tc;
						totalDc += dc;
						break;
					}
				}
			}
		}
*/
		System.out.println(totalTc+"\t"+totalDc);
	}

	private void clustering() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.01, 0.01);
		
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);

		HashMap <Integer, IPeptide> map = new HashMap <Integer, IPeptide>();
		
		Instances [] instancesList = new Instances[6];
		for(int i=0;i<instancesList.length;i++){
			instancesList[i] = creator.createInstances("Type "+i);
		}
		
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			
			String seq = pep.getSequence();
			int scannum = pep.getScanNumBeg();
			int length = PeptideUtil.getSequenceLength(seq);
			int modnum = seq.length()-length-4;
			int charge = pep.getCharge();
			int miss = pep.getMissCleaveNum();
			
			if(pep.getRank()==1){
				
				map.put(scannum, pep);
				IMS2PeakList peaklist = reader.getPeakLists()[0];
				MyInstance mi = creator.createInstance(pep, peaklist);
				
				switch (charge){
				case 2:
					switch (miss){
					case 0:
						instancesList[0].add(mi);
						break;
					default:
						instancesList[1].add(mi);
						break;
					}
					break;
				case 3:
					switch (miss){
					case 0:
						instancesList[2].add(mi);
						break;
					default:
						instancesList[3].add(mi);
						break;
					}
					break;
				default:
					switch (miss){
					case 0:
						instancesList[4].add(mi);
						break;
					default:
						instancesList[5].add(mi);
						break;
					}
					break;
				}
			}
		}
		
		int totalTc = 0;
		int totalDc = 0;
		
		for(int i=0;i<instancesList.length;i++){
			
			int num = instancesList[i].numInstances();
			if(num>200){
				
				SimpleKMeans cluster = new SimpleKMeans();
//				((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
				cluster.setNumClusters(num/200);
				cluster.buildClusterer(instancesList[i]);
				
				ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
				for(int j=0;j<milists.length;j++){
					milists[j] = new ArrayList <MyInstance>();
				}
				
				for(int j=0;j<instancesList[i].numInstances();j++){
					
					Instance ins = instancesList[i].instance(j);
					int type = cluster.clusterInstance(ins);
					MyInstance mi = (MyInstance) ins;
					milists[type].add(mi);
				}
				
				for(int j=0;j<milists.length;j++){
					
					ArrayList <MyInstance> list = milists[j];
					
					for(int k=0;k<50;k++){

						float totalScore = 0;
						int tc = 0;
						int dc = 0;
						int tc0 = 0;
						int dc0 = 0;
						for(int l=0;l<list.size();l++){
							
							MyInstance mil = milists[j].get(l);
							totalScore += mil.getScore();
							
							if(mil.isRev()){
								dc0++;
								if(mil.getScore()>k)
									dc++;
							}else{
								tc0++;
								if(mil.getScore()>k)
									tc++;
							}
						}
						double ratio0 = (double)dc0/(double)tc0;
						double fdr = (double)dc/(double)tc;
						
						if(fdr<0.01){
							System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
									+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
							totalTc += tc;
							totalDc += dc;
							break;
						}
					}
				}
			}else{
				
				for(int j=0;j<num;j++){
					
					Instance ins = instancesList[i].instance(j);
					MyInstance mi = (MyInstance) ins;
					
					for(int k=0;k<50;k++){

						float totalScore = 0;
						int tc = 0;
						int dc = 0;
						int tc0 = 0;
						int dc0 = 0;
						
						if(mi.isRev()){
							dc0++;
							if(mi.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mi.getScore()>k)
								tc++;
						}
						
						double ratio0 = (double)dc0/(double)tc0;
						double fdr = (double)dc/(double)tc;
						
						if(fdr<0.01){
							System.out.println(totalScore/(double)num+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
									+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+num);
							totalTc += tc;
							totalDc += dc;
							break;
						}
					}
				}
			}
		}
		System.out.println(totalTc+"\t"+totalDc);
	}
	
	private void clustering2() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				continue;

			MyInstance mi = creator.createInstance(pep);
			instances.add(mi);
		}
		
		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
						
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);
			
			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int type = mil.getClusterType();
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(thres[type]>-1){
						if(mil.getScore()>thres[type]){
							if(insMap.containsKey(scannum)){
								if(rank<insMap.get(scannum).getRank()){
									insMap.put(scannum, mil);
								}
							}else{
								insMap.put(scannum, mil);
							}
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			int ftc = 0;
			int fdc = 0;
			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
				}
			}
			
			System.out.println(ftc+"\t"+fdc);
		}
	}
	
	private void clustering3() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>1)
				continue;

			pros2.addPeptide(pep);
		}
		
		Protein [] proteins = pros2.getProteins();
		Arrays.sort(proteins);
		
		HashSet <String> usedset = new HashSet <String>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstance(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}

		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
						
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);
			
			Proteins2 fp2 = new Proteins2(reader.getProNameAccesser());
			
			for(int l=0;l<instances.numInstances();l++){
				
				MyInstance mil = (MyInstance) instances.get(l);
				int type = mil.getClusterType();
				
				if(thres[type]>=0 && mil.getScore()>thres[type]){
					IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
					fp2.addPeptide(peptide);
//					System.out.println(peptide.getSequence()+"\t"+peptide.getPrimaryScore()+"\t"+peptide.getReferenceOutString());
//					System.out.println(peptide.isTP()+"\t"+!mil.isRev());
				}
			}
			
			int trtc = 0;
			int trdc = 0;
			HashSet <String> ftcset = new HashSet <String>();
			HashSet <String> fdcset = new HashSet <String>();
			Protein [] fps = fp2.getProteins();
			for(int i=0;i<fps.length;i++){
//				if(fps[i].getSpectrumCount()<=2)
//					continue;
				 IReferenceDetail [] refs = fps[i].getReferences();
				 for(int j=0;j<refs.length;j++){
					 if(refs[j].getName().startsWith("SHF")){
						 trdc++;
						 IPeptide [] ps = fps[i].getAllPeptides();
						 for(int k=0;k<ps.length;k++){
							 fdcset.add(ps[k].getScanNum()+ps[k].getCharge());
						 }
						 break;
					 }
				 }
				 trtc++;
				 IPeptide [] ps = fps[i].getAllPeptides();
				 for(int k=0;k<ps.length;k++){
					 ftcset.add(ps[k].getScanNum()+ps[k].getCharge());
				 }			}
			System.out.println(trtc+"\t"+trdc+"\t"+ftcset+"\t"+fdcset);
/*
			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int type = mil.getClusterType();
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(thres[type]>-1){
						if(mil.getScore()>thres[type]){
							if(insMap.containsKey(scannum)){
								if(rank<insMap.get(scannum).getRank()){
									insMap.put(scannum, mil);
								}
							}else{
								insMap.put(scannum, mil);
							}
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			int ftc = 0;
			int fdc = 0;
			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
				}
			}			
			System.out.println(ftc+"\t"+fdc);
*/			
		}
	}

	private void clustering4() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				continue;

			pros2.addPeptide(pep);
		}
		
		Protein [] proteins = pros2.getProteins();
		Arrays.sort(proteins);
		
		HashSet <String> usedset = new HashSet <String>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstance(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}

		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
						
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);

			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int type = mil.getClusterType();
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(thres[type]>-1){
						if(mil.getScore()>thres[type]){
							if(insMap.containsKey(scannum)){
								if(rank<insMap.get(scannum).getRank()){
									insMap.put(scannum, mil);
								}
							}else{
								insMap.put(scannum, mil);
							}
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			
//			HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
//					"Literature\\peptide.txt", "3");
			
			Proteins2 fp2 = new Proteins2(reader.getProNameAccesser());

			int ftc = 0;
			int fdc = 0;
			
			int find = 0;
			int nofind = 0;
			
			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				
				IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
				fp2.addPeptide(peptide);
				
/*				String unique = PeptideUtil.getUniqueSequence(peptide.getSequence());
				if(set.contains(unique)){
					find++;
				}else{
					nofind++;
				}
*/				
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
				}
				if(peptide.getReferenceOutString().startsWith("SHF")){
					nofind++;
				}else if(peptide.getReferenceOutString().startsWith("IPI")){
					find++;
				}
			}			
			System.out.println(ftc+"\t"+fdc);
			System.out.println(find+"\t"+nofind);
			
			int trtc = 0;
			int trdc = 0;
			HashSet <String> ftcset = new HashSet <String>();
			HashSet <String> fdcset = new HashSet <String>();
			Protein [] fps = fp2.getProteins();
			for(int i=0;i<fps.length;i++){
//				if(fps[i].getSpectrumCount()<=2)
//					continue;
				boolean ipi = true;
				IReferenceDetail [] refs = fps[i].getReferences();
				for(int j=0;j<refs.length;j++){
//					if(!refs[j].getName().startsWith("IPI"))
//					System.out.println(refs[j].getName()+"\t"+refs[j].getCoverage());
//					if(refs[j].getName().startsWith("SHF")){
					if(refs[j].getName().startsWith("IPI")){
						trdc++;
						 
						IPeptide [] ps = fps[i].getAllPeptides();
						for(int k=0;k<ps.length;k++){
							fdcset.add(ps[k].getScanNum()+ps[k].getCharge());
						}
						 
						break;
						
					}
//					else if(refs[j].getName().startsWith("IPI")){
						
//					}
					else{
						ipi = false;
					}
					
				}
				
//				if(ipi)
//					System.out.println(fps[i].getSpectrumCount()+"\t"+refs[0].getName());

				trtc++;
				IPeptide [] ps = fps[i].getAllPeptides();
				for(int k=0;k<ps.length;k++){
					ftcset.add(ps[k].getScanNum()+ps[k].getCharge());
				}
				
			}
			System.out.println(trtc+"\t"+trdc+"\t"+ftcset.size()+"\t"+fdcset.size());
			
		}
	}

	private void clustering4QValue() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");
System.out.println(reader.getNumberofPeptides());
//		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				break;

			IMS2PeakList peaklist = reader.getPeakLists()[0];
//			pros2.addPeptide(pep);
//			MyInstance mi = creator.createInstance(pep, peaklist);
			MyInstance mi = creator.createInstance(pep);
			map.put(pep.getScanNumBeg()+"_"+pep.getRank()+"_"+pep.getPrimaryScore(), pep);
			instances.add(mi);
			System.out.println(pep.getScanNumBeg());
		}
Date nowTime = new Date();
System.out.println("1328\tfinish create instance\t"+nowTime);		
/*		Protein [] proteins = pros2.getProteins();
		Arrays.sort(proteins);
		
		HashSet <String> usedset = new HashSet <String>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstance(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}
*/
		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		String ss = filename.substring(0, filename.length()-8);
		PrintWriter pw = new PrintWriter(ss+".20130415.pep.txt");
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);

			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				MyInstance [] miss = list.toArray(new MyInstance[list.size()]);
				Arrays.sort(miss, new Comparator<MyInstance>(){

					@Override
					public int compare(MyInstance arg0, MyInstance arg1) {
						// TODO Auto-generated method stub
						if(arg0.getScore()<arg1.getScore())
							return 1;
						else if(arg0.getScore()>arg1.getScore())
							return -1;
						return 0;
					}
					
				});
				
				int target = 0;
				int decoy = 0;
				
				for(int k=0;k<miss.length;k++){
					if(miss[k].isRev()){
						decoy++;
						totalDc++;
					}else{
						target++;
						totalTc++;
					}
					float q = -1f;
					if(target==0){
						q = 1.0f;
					}else{
						q = (float)decoy/(float)target;
					}
					miss[k].setQValue(q);
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);

			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(mil.getQValue()<=0.01){
						if(insMap.containsKey(scannum)){
							if(rank<insMap.get(scannum).getRank()){
								insMap.put(scannum, mil);
							}
						}else{
							insMap.put(scannum, mil);
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			
//			HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
//					"Literature\\peptide.txt", "3");
			
/*			Proteins2 fp2 = new Proteins2(reader.getProNameAccesser());

			int ftc = 0;
			int fdc = 0;
			
			int find = 0;
			int nofind = 0;
			
//			HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
//					"Literature\\peptide.txt", "5");
					
			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				
				IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
				fp2.addPeptide(peptide);
				pw.write(peptide.getScanNum()+"\t"+peptide.getRank()+"\t"+peptide.getPrimaryScore()+"\t"+peptide.getSequence()
						+"\t"+mil.getQValue()+"\t"+peptide.getReferenceOutString()+"\n");
				String unique = PeptideUtil.getUniqueSequence(peptide.getSequence());
				if(set.contains(unique)){
					find++;
				}else{
					nofind++;
				}
				
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
				}
				if(peptide.getReferenceOutString().startsWith("SHF")){
					nofind++;
				}else if(peptide.getReferenceOutString().startsWith("IPI")){
					find++;
				}
				
			}			
			System.out.println(ftc+"\t"+fdc);
			System.out.println(find+"\t"+nofind);
			
			int trtc = 0;
			int trdc = 0;
			HashSet <String> ftcset = new HashSet <String>();
			HashSet <String> fdcset = new HashSet <String>();
			Protein [] fps = fp2.getProteins();
			for(int i=0;i<fps.length;i++){
//				if(fps[i].getSpectrumCount()<=2)
//					continue;
				boolean ipi = true;
				IReferenceDetail [] refs = fps[i].getReferences();
				for(int j=0;j<refs.length;j++){
//					if(!refs[j].getName().startsWith("IPI"))
//					System.out.println(refs[j].getName()+"\t"+refs[j].getCoverage());
//					if(refs[j].getName().startsWith("SHF")){
					if(refs[j].getName().startsWith("IPI")){
						trdc++;
						 
						IPeptide [] ps = fps[i].getAllPeptides();
						for(int k=0;k<ps.length;k++){
							fdcset.add(ps[k].getScanNum()+ps[k].getCharge());
						}
						 
						break;
						
					}
//					else if(refs[j].getName().startsWith("IPI")){
						
//					}
					else{
						ipi = false;
					}
					
				}
				
//				if(ipi)
//					System.out.println(fps[i].getSpectrumCount()+"\t"+refs[0].getName());

				trtc++;
				IPeptide [] ps = fps[i].getAllPeptides();
				for(int k=0;k<ps.length;k++){
					ftcset.add(ps[k].getScanNum()+ps[k].getCharge());
				}
				
			}
						
			System.out.println(trtc+"\t"+trdc+"\t"+ftcset.size()+"\t"+fdcset.size());
			
*/			
		}
		pw.close();
	}

	private void clustering4QValueDouble() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);

		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		Proteins2 pros = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				continue;

			pros.addPeptide(pep);
		}
		
		Protein [] proteins = pros.getProteins();
		Arrays.sort(proteins);
		
		ArrayList <IPeptide> list1 = this.validate(proteins, instances, creator, 300, 0.05f);
		Instances instances2 = creator.createInstances("data");
		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		for(int i=0;i<list1.size();i++){
			pros2.addPeptide(list1.get(i));
		}
		Protein [] proteins2 = pros2.getProteins();
		Arrays.sort(proteins2);
		
		ArrayList <IPeptide> list2 = this.validate(proteins2, instances2, creator, 200, 0.01f);
		HashMap <Integer, IPeptide> pepMap = new HashMap <Integer, IPeptide>();
		
		for(int j=0;j<list2.size();j++){
			
			IPeptide pepj = list2.get(j);
			int scannum = pepj.getScanNumBeg();
			int rank = pepj.getRank();
			if(pepMap.containsKey(scannum)){
				if(rank<pepMap.get(scannum).getRank()){
					pepMap.put(scannum, pepj);
				}
			}else{
				pepMap.put(scannum, pepj);
			}
		}
		System.out.println("size\t"+pepMap.size());
		
		int target = 0;
		int decoy = 0;
		int ipi = 0;
		int shf = 0;
		Iterator <Integer> it = pepMap.keySet().iterator();
		while(it.hasNext()){
			Integer scannum = it.next();
			IPeptide pepit = pepMap.get(scannum);
			if(pepit.isTP()){
				target++;
			}else{
				decoy++;
			}
			String ref = pepit.getProteinReferenceString();
			if(ref.startsWith("IPI")){
				ipi++;
			}else if(ref.startsWith("SHF")){
				shf++;
			}
		}
		System.out.println(target+"\t"+decoy);
		System.out.println(ipi+"\t"+shf);
	}
	
	private ArrayList <IPeptide> validate(Protein [] proteins, Instances instances, PSMInstanceCreator creator, 
			int aveNum, float maxQvalue) throws Exception{
		
		HashSet <String> usedset = new HashSet <String>();
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		ArrayList <IPeptide> peplist = new ArrayList <IPeptide>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstanceForLTQ(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}
		
		int num = instances.numInstances();
		int totalDc = 0;
		int totalTc = 0;
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/aveNum);
			cluster.buildClusterer(instances);

			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				MyInstance [] miss = list.toArray(new MyInstance[list.size()]);
				Arrays.sort(miss, new Comparator<MyInstance>(){

					@Override
					public int compare(MyInstance arg0, MyInstance arg1) {
						// TODO Auto-generated method stub
						if(arg0.getScore()<arg1.getScore())
							return 1;
						else if(arg0.getScore()>arg1.getScore())
							return -1;
						return 0;
					}
					
				});
				
				int target = 0;
				int decoy = 0;
				
				for(int k=0;k<miss.length;k++){
					if(miss[k].isRev()){
						decoy++;
						totalDc++;
					}else{
						target++;
						totalTc++;
					}
					float q = -1f;
					if(target==0){
						q = 1.0f;
					}else{
						q = (float)decoy/(float)target;
					}
					miss[k].setQValue(q);
					String key = miss[k].getIdx()+"_"+miss[k].getRank()+"_"+miss[k].getScore();
					if(q<maxQvalue){
						peplist.add(map.get(key));
					}
				}
			}
			
			System.out.println("list\t"+totalTc+"\t"+totalDc+"\t"+peplist.size());
		}
		return peplist;
	}

	private void clustering4_18mix() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				continue;

			pros2.addPeptide(pep);
		}
		
		Protein [] proteins = pros2.getProteins();
		Arrays.sort(proteins);
		
		HashSet <String> usedset = new HashSet <String>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstanceForLTQ(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}

		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
						
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);

			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int type = mil.getClusterType();
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(thres[type]>-1){
						if(mil.getScore()>thres[type]){
							if(insMap.containsKey(scannum)){
								if(rank<insMap.get(scannum).getRank()){
									insMap.put(scannum, mil);
								}
							}else{
								insMap.put(scannum, mil);
							}
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			
//			HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
//					"Literature\\peptide.txt", "3");
			
			Proteins2 fp2 = new Proteins2(reader.getProNameAccesser());

			int ftc = 0;
			int fdc = 0;
			
			int find = 0;
			int nofind = 0;
			
			MascotHtmlQueryGetter getter = new MascotHtmlQueryGetter("H:\\Validation\\18mix\\18MIX_03_2925.htm");
			getter.read();
			getter.getScan("H:\\Validation\\18mix\\18MIX_03_per_F002925.dat");
			HashSet <Integer> scanset = getter.getScanset();

			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				
				IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
				fp2.addPeptide(peptide);
				
				if(!scanset.contains(key)){
					System.out.println("-----\t"+peptide.getPrimaryScore());	
				}
				
//System.out.println("-----\t"+peptide.getPrimaryScore());				
/*				String unique = PeptideUtil.getUniqueSequence(peptide.getSequence());
				if(set.contains(unique)){
					find++;
				}else{
					nofind++;
				}
*/				
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
				}
				
			}			
			System.out.println(ftc+"\t"+fdc);
			System.out.println(find+"\t"+nofind);
			
/*			int trtc = 0;
			int trdc = 0;
			HashSet <String> ftcset = new HashSet <String>();
			HashSet <String> fdcset = new HashSet <String>();
			Protein [] fps = fp2.getProteins();
			for(int i=0;i<fps.length;i++){
//				if(fps[i].getSpectrumCount()<=2)
//					continue;
				boolean ipi = true;
				IReferenceDetail [] refs = fps[i].getReferences();
				for(int j=0;j<refs.length;j++){
					if(!refs[j].getName().startsWith("IPI") && !refs[j].getName().startsWith("REV"))
					System.out.println(refs[j].getName()+"\t"+refs[j].getCoverage());
//					if(refs[j].getName().startsWith("SHF")){
/*					if(refs[j].getName().startsWith("IPI")){
						trdc++;
						 
						IPeptide [] ps = fps[i].getAllPeptides();
						for(int k=0;k<ps.length;k++){
							fdcset.add(ps[k].getScanNum()+ps[k].getCharge());
						}
						 
						break;
						
					}
//					else if(refs[j].getName().startsWith("IPI")){
						
//					}
					else{
						ipi = false;
					}
*/					
//				}
				
/*				if(ipi)
					System.out.println(fps[i].getSpectrumCount()+"\t"+refs[0].getName());

				trtc++;
				IPeptide [] ps = fps[i].getAllPeptides();
				for(int k=0;k<ps.length;k++){
					ftcset.add(ps[k].getScanNum()+ps[k].getCharge());
				}
*/				
//			}
//			System.out.println(trtc+"\t"+trdc+"\t"+ftcset.size()+"\t"+fdcset.size());
			
		}
	}

	private void clustering4_downPhos() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				continue;

			pros2.addPeptide(pep);
		}
		
		Protein [] proteins = pros2.getProteins();
		Arrays.sort(proteins);
		
		HashSet <String> usedset = new HashSet <String>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstanceForOrbi(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}

		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
						
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);

			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int type = mil.getClusterType();
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(thres[type]>-1){
						if(mil.getScore()>thres[type]){
							if(insMap.containsKey(scannum)){
								if(rank<insMap.get(scannum).getRank()){
									insMap.put(scannum, mil);
								}
							}else{
								insMap.put(scannum, mil);
							}
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			
//			HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
//					"Literature\\peptide.txt", "3");
			
			Proteins2 fp2 = new Proteins2(reader.getProNameAccesser());

			int ftc = 0;
			int fdc = 0;
			
			int find = 0;
			int nofind = 0;
			
			MascotHtmlQueryGetter getter = new MascotHtmlQueryGetter("H:\\Validation\\phospho_download\\" +
					"Orbitrap_mgf\\CID_mgf\\mix1.htm");
			getter.read();
			getter.getScan("H:\\Validation\\phospho_download\\Orbitrap_mgf\\CID_mgf\\ppeptidemix1_CID_Orbi_F002977.dat");
			HashSet <Integer> scanset = getter.getScanset();

			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				
				IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
				fp2.addPeptide(peptide);
				
				if(!scanset.contains(key)){
					System.out.println("-----\t"+peptide.getPrimaryScore());	
				}
				
//System.out.println("-----\t"+peptide.getPrimaryScore());				
/*				String unique = PeptideUtil.getUniqueSequence(peptide.getSequence());
				if(set.contains(unique)){
					find++;
				}else{
					nofind++;
				}
*/				
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
				}
				
			}			
			System.out.println(ftc+"\t"+fdc);
			System.out.println(find+"\t"+nofind);
			
/*			int trtc = 0;
			int trdc = 0;
			HashSet <String> ftcset = new HashSet <String>();
			HashSet <String> fdcset = new HashSet <String>();
			Protein [] fps = fp2.getProteins();
			for(int i=0;i<fps.length;i++){
//				if(fps[i].getSpectrumCount()<=2)
//					continue;
				boolean ipi = true;
				IReferenceDetail [] refs = fps[i].getReferences();
				for(int j=0;j<refs.length;j++){
					if(!refs[j].getName().startsWith("IPI") && !refs[j].getName().startsWith("REV"))
					System.out.println(refs[j].getName()+"\t"+refs[j].getCoverage());
//					if(refs[j].getName().startsWith("SHF")){
/*					if(refs[j].getName().startsWith("IPI")){
						trdc++;
						 
						IPeptide [] ps = fps[i].getAllPeptides();
						for(int k=0;k<ps.length;k++){
							fdcset.add(ps[k].getScanNum()+ps[k].getCharge());
						}
						 
						break;
						
					}
//					else if(refs[j].getName().startsWith("IPI")){
						
//					}
					else{
						ipi = false;
					}
*/					
//				}
				
/*				if(ipi)
					System.out.println(fps[i].getSpectrumCount()+"\t"+refs[0].getName());

				trtc++;
				IPeptide [] ps = fps[i].getAllPeptides();
				for(int k=0;k<ps.length;k++){
					ftcset.add(ps[k].getScanNum()+ps[k].getCharge());
				}
*/				
//			}
//			System.out.println(trtc+"\t"+trdc+"\t"+ftcset.size()+"\t"+fdcset.size());
			
		}
	}

	private void clustering4_Phos() throws Exception{
		
		ISearchParameter parameter = reader.getSearchParameter();
		AminoacidFragment aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter
		        .getVariableInfo());
		int [] types = new int []{Ion.TYPE_B, Ion.TYPE_Y};
		ISpectrumThreshold spThres = new SpectrumThreshold(0.8, 0.01);
		HashMap <String, IPeptide> map = new HashMap <String, IPeptide>();
		PSMInstanceCreator creator = new PSMInstanceCreator(aaf, types, spThres);
		
		Instances instances = creator.createInstances("data");

		Proteins2 pros2 = new Proteins2(reader.getProNameAccesser());
		IPeptide pep = null;
		while((pep = reader.getPeptide())!=null){
			if(pep.getRank()>2)
				continue;

			pros2.addPeptide(pep);
		}
		
		Protein [] proteins = pros2.getProteins();
		Arrays.sort(proteins);
		
		HashSet <String> usedset = new HashSet <String>();
		for(int i=0;i<proteins.length;i++){
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				IPeptide p = peps[j];
				String key = p.getScanNum()+p.getCharge()+p.getRank();
				if(!usedset.contains(key)){
					usedset.add(key);
					int scount = proteins[i].getSpectrumCount();
					int pcount = proteins[i].getPeptideCount();
					
					MyInstance mi = creator.createInstanceForOrbi(p, scount, pcount);
					map.put(p.getScanNumBeg()+"_"+p.getRank()+"_"+p.getPrimaryScore(), p);
					instances.add(mi);
				}
			}
		}

		int totalTc = 0;
		int totalDc = 0;

		int num = instances.numInstances();
		
		if(num>300){
			
			SimpleKMeans cluster = new SimpleKMeans();
//			((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
			cluster.setNumClusters(num/300);
			cluster.buildClusterer(instances);
			
			double [] thres = new double[cluster.numberOfClusters()];
			Arrays.fill(thres, -1);
			
			ArrayList <MyInstance> [] milists = new ArrayList [cluster.numberOfClusters()];
			for(int j=0;j<milists.length;j++){
				milists[j] = new ArrayList <MyInstance>();
			}
			
			for(int j=0;j<instances.numInstances();j++){
				
				Instance ins = instances.instance(j);
				int type = cluster.clusterInstance(ins);
				MyInstance mi = (MyInstance) ins;
				mi.setClusterType(type);
				milists[type].add(mi);
			}

			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				
				for(int k=0;k<50;k++){

					float totalScore = 0;
					int tc = 0;
					int dc = 0;
					int tc0 = 0;
					int dc0 = 0;
					for(int l=0;l<list.size();l++){
						
						MyInstance mil = milists[j].get(l);
						totalScore += mil.getScore();
						
						if(mil.isRev()){
							dc0++;
							if(mil.getScore()>k)
								dc++;
						}else{
							tc0++;
							if(mil.getScore()>k)
								tc++;
						}
					}
					double ratio0 = (double)dc0/(double)tc0;
					double fdr = (double)dc/(double)tc;
					
					if(fdr<0.01){
						System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
								+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
						totalTc += tc;
						totalDc += dc;
						thres[j] = k;
						break;
						
					}
				}
			}
			
			System.out.println(totalTc+"\t"+totalDc);

			HashMap <Integer, MyInstance> insMap = new HashMap <Integer, MyInstance>();
			for(int j=0;j<milists.length;j++){
				
				ArrayList <MyInstance> list = milists[j];
				for(int l=0;l<list.size();l++){
					
					MyInstance mil = milists[j].get(l);
					int type = mil.getClusterType();
					int scannum = mil.getIdx();
					int rank = mil.getRank();
					if(thres[type]>-1){
						if(mil.getScore()>thres[type]){
							if(insMap.containsKey(scannum)){
								if(rank<insMap.get(scannum).getRank()){
									insMap.put(scannum, mil);
								}
							}else{
								insMap.put(scannum, mil);
							}
						}
					}
				}
			}
			System.out.println("size\t"+insMap.size());
			
//			HashSet <String> set = this.getPepSet("H:\\Validation\\phospho_download\\" +
//					"Literature\\peptide.txt", "3");
			
			Proteins2 fp2 = new Proteins2(reader.getProNameAccesser());

			int ftc = 0;
			int fdc = 0;
			
			int find = 0;
			int nofind = 0;
			
			int ipi = 0;
			int shf = 0;
			
//			MascotHtmlQueryGetter getter = new MascotHtmlQueryGetter("H:\\Validation\\phospho_download\\" +
//					"Orbitrap_mgf\\CID_mgf\\mix1.htm");
//			getter.read();
//			getter.getScan("H:\\Validation\\phospho_download\\Orbitrap_mgf\\CID_mgf\\ppeptidemix1_CID_Orbi_F002977.dat");
//			HashSet <Integer> scanset = getter.getScanset();

			Iterator <Integer> it = insMap.keySet().iterator();
			while(it.hasNext()){
				
				Integer key = it.next();
				MyInstance mil = insMap.get(key);
				
				IPeptide peptide = map.get(mil.getIdx()+"_"+mil.getRank()+"_"+mil.getScore());
				fp2.addPeptide(peptide);
				
//				if(!scanset.contains(key)){
//					System.out.println("-----\t"+peptide.getPrimaryScore());	
//				}
				
//System.out.println("-----\t"+peptide.getPrimaryScore());				
/*				String unique = PeptideUtil.getUniqueSequence(peptide.getSequence());
				if(set.contains(unique)){
					find++;
				}else{
					nofind++;
				}
*/				
				if(mil.isRev()){
					fdc++;
				}else{
					ftc++;
					String ref = peptide.getProteinReferenceString();
					if(ref.startsWith("SHF")){
						shf++;
					}else{
						ipi++;
					}
				}
				
			}			
			System.out.println(ftc+"\t"+fdc);
			System.out.println(ipi+"\t"+shf);
			System.out.println(find+"\t"+nofind);
			
/*			int trtc = 0;
			int trdc = 0;
			HashSet <String> ftcset = new HashSet <String>();
			HashSet <String> fdcset = new HashSet <String>();
			Protein [] fps = fp2.getProteins();
			for(int i=0;i<fps.length;i++){
//				if(fps[i].getSpectrumCount()<=2)
//					continue;
				boolean ipi = true;
				IReferenceDetail [] refs = fps[i].getReferences();
				for(int j=0;j<refs.length;j++){
					if(!refs[j].getName().startsWith("IPI") && !refs[j].getName().startsWith("REV"))
					System.out.println(refs[j].getName()+"\t"+refs[j].getCoverage());
//					if(refs[j].getName().startsWith("SHF")){
/*					if(refs[j].getName().startsWith("IPI")){
						trdc++;
						 
						IPeptide [] ps = fps[i].getAllPeptides();
						for(int k=0;k<ps.length;k++){
							fdcset.add(ps[k].getScanNum()+ps[k].getCharge());
						}
						 
						break;
						
					}
//					else if(refs[j].getName().startsWith("IPI")){
						
//					}
					else{
						ipi = false;
					}
*/					
//				}
				
/*				if(ipi)
					System.out.println(fps[i].getSpectrumCount()+"\t"+refs[0].getName());

				trtc++;
				IPeptide [] ps = fps[i].getAllPeptides();
				for(int k=0;k<ps.length;k++){
					ftcset.add(ps[k].getScanNum()+ps[k].getCharge());
				}
*/				
//			}
//			System.out.println(trtc+"\t"+trdc+"\t"+ftcset.size()+"\t"+fdcset.size());
			
		}
	}

	public HashSet <String> getPepSet(String in, String id) throws IOException{
		
		HashSet <String> set = new HashSet <String>();
		BufferedReader reader = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=reader.readLine())!=null && line.trim().length()>0){
			String [] ss = line.split("\t");
			if(ss[0].startsWith(id))
				set.add(ss[5]);
		}
		reader.close();
		return set;
	}
	
	public void close(){
		this.reader.close();
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
//		String file = "D:\\SILAC\\nuclus\\20110906\\400_F001782.dat.ppl";
//		String file = "H:\\Validation\\no_enzyme\\F001812.dat.ppl";
//		String file = "D:\\SILAC\\nuclus\\20110906\\1000_F001778.ppl";
//		String file = "D:\\SILAC\\nuclus\\20110906\\100_F001787.ppl";
//		String file = "H:\\Validation\\Byy_phos_5600_velos\\F002935_2_human_shuf.dat.ppl";
//		String file = "H:\\Validation\\2D_phos_new\\0_per_F003070.dat.ppl";
//		String file = "H:\\Validation\\18mix\\120917\\18mix_3_F002925.dat.ppl";
//		String file = "H:\\WFJ_mutiple_label\\turnover\\0_3_48\\0_3_48_700mM_F002807.dat.ppl";
		String file = "H:\\Phospho_database\\SCX_mouse-liver-control\\W-H-X-D-2\\normal\\score 0\\mouse-liver_W_0mM-2.F003539.ppl";
//		String file = "H:\\WFJ_mutiple_label\\2D_trypsin\\1000_F002464.dat.ppl";
//		String file = "H:\\Validation\\phospho_download\\Orbitrap_mgf\\CID_mgf\\mix4_F002979.dat.ppl";
//		String file = "H:\\Validation\\phospho_download\\Orbitrap_mgf\\CID_mgf\\120917\\mix4_F002979.dat.ppl";
		PeptideClusteringTest test = new PeptideClusteringTest(file);
//		test.group();
//		test.group2();
//		test.clustering0();
//		test.clustering();
//		test.clustering2();
//		test.clustering3();
//		test.clustering4();
		test.clustering4QValue();
//		test.clustering4QValueDouble();
//		test.clustering4_18mix();
//		test.clustering4_downPhos();
//		test.clustering4_Phos();
		test.close();
		
		
		long end = System.currentTimeMillis();
		System.out.println((end-begin)/1000);
	}

}
