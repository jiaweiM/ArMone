/* 
 ******************************************************************************
 * File: MutilClusQuan.java * * * Created on 2012-8-21
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.multiple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.penn.probability.wekakd.MyInstance;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

import cn.ac.dicp.gp1809.clustering.PSMInstanceCreator;
import cn.ac.dicp.gp1809.proteome.quant.label.PeptidePairGetter;
import cn.ac.dicp.gp1809.proteome.quant.label.LabelFeatures;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * @author ck
 *
 * @version 2012-8-21, 16:04:59
 */
public class MutilClusQuan {
	
	private IPeptideListReader reader;
	private MutilLabelPairXMLWriter writer;
	private PeptidePairGetter getter;
	
	public MutilClusQuan(IPeptideListReader reader, String pixfile, 
			String result, int type, int mzxmlType) throws IOException{
		
		this.reader = reader;
		
		AminoacidModification aamodif = reader.getSearchParameter().getVariableInfo();
		if(type==5){
			this.getter = new FiveFeaturesGetter(pixfile, mzxmlType);
			this.writer = new MutilLabelPairXMLWriter(new File(result), LabelType.FiveLabel, false);
			
		}else if(type==6){
			this.getter = new SixFeaturesGetter(pixfile, mzxmlType);
			this.writer = new MutilLabelPairXMLWriter(new File(result), LabelType.SixLabel, false);
		}
		
		getter.setModif(aamodif);
		writer.addModification(aamodif);
		writer.addProNameInfo(reader.getProNameAccesser());
	}

	public void process() throws Exception{
		
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

		SimpleKMeans cluster = new SimpleKMeans();
//		((EuclideanDistance)cluster.getDistanceFunction()).setDontNormalize(true);
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
				
				if(fdr<0.1){
//					System.out.println(totalScore/(double)list.size()+"\t"+ratio0+"\t"+tc0+"\t"+dc0+"\t"
//							+k+"\t"+tc+"\t"+dc+"\t"+fdr+"\t"+list.size());
					totalTc += tc;
					totalDc += dc;
					thres[j] = k;
					break;
					
				}
			}
		}
		
		System.out.println(totalTc+"\t"+totalDc);

		HashMap <String, MyInstance> insMap = new HashMap <String, MyInstance>();
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
								String key = scannum+"_"+rank+"_"+mil.getScore();
								insMap.put(key, mil);
							}
						}else{
							String key = scannum+"_"+rank+"_"+mil.getScore();
							insMap.put(key, mil);
						}
					}
				}
			}
		}
		System.out.println("size\t"+insMap.size());
	
		Iterator <String> it = insMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			if(map.containsKey(key)){
				
				IPeptide peptide = map.get(key);
				this.getter.addPeptide(peptide);
				
			}else{
				System.out.println(key);
			}
		}

		HashMap <String, PeptidePair> pairMap = getter.getPeptidPairs();

		Iterator <String> it2 = pairMap.keySet().iterator();
		while(it2.hasNext()){
			String key = it2.next();
			PeptidePair pair = pairMap.get(key);
			this.writer.addPeptidePair(pair);
		}
		
		HashMap <String, IPeptide> idenPepMap = getter.getIdenPepMap();
		Iterator <String> idenPepIt = idenPepMap.keySet().iterator();
		while(idenPepIt.hasNext()){
			IPeptide peptide = idenPepMap.get(idenPepIt.next());
			this.writer.addIdenPep(peptide);
		}

		try {
			writer.write();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void dispose() {
		// TODO Auto-generated method stub
		getter.close();
		reader.close();
		System.gc();
	}
	
	public static void batchProcess(String in) throws Exception{
		
		HashMap <String, String> pplmap = new HashMap <String, String>();
		HashMap <String, String> peakmap = new HashMap <String, String>();
		
		File [] files = (new File(in)).listFiles();
		for(int i=0;i<files.length;i++){
			String name = files[i].getName();
			if(name.endsWith("mzXML")){
				int id = name.indexOf("_");
				peakmap.put(name.substring(id+1, name.length()-8), files[i].getAbsolutePath());
				System.out.println(name.substring(id+1, name.length()-8)+"\tmzxml");
			}else if(name.endsWith("ppl")){
				int id = name.lastIndexOf("_");
				pplmap.put(name.substring(0, id), files[i].getAbsolutePath());
				System.out.println(name.substring(0, id));
			}
		}
		
		Iterator <String> it = pplmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			String pplfile = pplmap.get(key);
			String peakfile = peakmap.get(key);
			String result = peakfile+".pxml";
			
			PeptideListReader pReader = null;
			try {
				pReader = new PeptideListReader(pplfile);
			} catch (FileDamageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MutilClusQuan task = new MutilClusQuan(pReader, peakfile, result, 6, 0);
			task.process();
			task.dispose();
		}
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
/*		String ppl = "H:\\WFJ_mutiple_label\\2D_trypsin\\100_F002470.dat.ppl";
		
		String pix = "H:\\WFJ_mutiple_label\\2D_trypsin" +
			"\\20120531Mix1_100mM.mzXML";
		
		String result = "H:\\WFJ_mutiple_label\\2D_trypsin" +
			"\\20120531Mix1_100mM_new.pxml";
		
		PeptideListReader reader = new PeptideListReader(ppl);		
		
		MutilClusQuan task = new MutilClusQuan(reader, pix, result, 6, 0);
		task.process();
		task.dispose();
*/
		BufferedReader reader = new BufferedReader(new FileReader("H:\\Validation\\Byy_phos_5600_velos\\" +
				"final_20120730_Human_liver_tryptic_50ug_2_F002998_percolator_percolator.dat.tab.txt"));
		String line = reader.readLine();
		int target = 0;
		int decoy = 0;
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			double qvalue = Double.parseDouble(ss[2]);
			if(qvalue<0.01){
				if(ss[5].startsWith("REV")){
					decoy++;
				}else{
					target++;
				}
			}
		}
		System.out.println(target+"\t"+decoy);
		reader.close();
	}

}
