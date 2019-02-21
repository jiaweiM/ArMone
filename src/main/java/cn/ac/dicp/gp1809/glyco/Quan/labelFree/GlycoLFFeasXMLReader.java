/* 
 ******************************************************************************
 * File: GlycoLFFeasXMLReader.java * * * Created on 2011-11-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeature;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.IO.LFFeasXMLReader;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import cn.ac.dicp.gp1809.glyco.GlycoSite;
import cn.ac.dicp.gp1809.glyco.glycoCT.GlycoTree;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;

/**
 * @author ck
 *
 * @version 2011-11-8, 19:31:19
 */
public class GlycoLFFeasXMLReader extends LFFeasXMLReader
{

	private GlycoPepFreeFeatures [] allPairs;
	private Iterator <Element> feasIt;
	
	private HashMap <Integer, String> peakOneLineMap;
	private IGlycoPeptide [] allPep;
	
	public GlycoLFFeasXMLReader(String file) throws DocumentException{
		this(new File(file));
	}
	
	public GlycoLFFeasXMLReader(File file) throws DocumentException{
		super(file);
	}
	
	protected void getProfileData() {
		
		System.out.println("Reading "+file.getName()+" ......");
		
		this.feasIt = root.elementIterator("GlycoSpectra");
		this.totalCurrent = Double.parseDouble(root.attributeValue("TotalCurrent"));
		
		this.peakOneLineMap = new HashMap <Integer, String>();
		Iterator <Element> spIt = root.elementIterator("Spectrum");
		while(spIt.hasNext()){
			
			Element sp = spIt.next();
			Integer scannum = Integer.parseInt(sp.attributeValue("Scannum"));
			String peakOneLine = sp.attributeValue("PeakOneLine");
			
			this.peakOneLineMap.put(scannum, peakOneLine);
		}
		
		try {
			
			this.setProNameAccesser();
			this.setMods();
			this.getAllPairs();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	protected void getAllPairs() {
	// TODO Auto-generated method
		
		ArrayList <IGlycoPeptide> peplist = new ArrayList <IGlycoPeptide>();
		ArrayList <GlycoPepFreeFeatures> pairlist = new ArrayList <GlycoPepFreeFeatures>();
		
//		GlycoPepFreeFeatures pair;
//		while((pair = this.getFeasPair())!=null){
//			pairlist.add(pair);
//			peplist.add((IGlycoPeptide) pair.getPeptide());
//		}
		
		IGlycoPeptide gp;
		while((gp=this.getGlycoPeptide())!=null){
			peplist.add(gp);
		}
//		this.allPairs = pairlist.toArray(new GlycoPepFreeFeatures[pairlist.size()]);
		this.allPep = peplist.toArray(new IGlycoPeptide [peplist.size()]);
	}
	
	protected GlycoPepFreeFeatures getFeasPair(){
		
		if(feasIt.hasNext()){
			
			Element eFeas = feasIt.next();
			
			String baseName = eFeas.attributeValue("BaseName");
			int scanBeg = Integer.parseInt(eFeas.attributeValue("scanBeg"));
			int scanEnd = Integer.parseInt(eFeas.attributeValue("scanEnd"));
			String seq = eFeas.attributeValue("Sequence");
			short pepCharge = Short.parseShort(eFeas.attributeValue("Charge"));
			double peprt = Double.parseDouble(eFeas.attributeValue("rt"));
			double pepMr = Double.parseDouble(eFeas.attributeValue("pepMr"));
			String ref = eFeas.attributeValue("Reference");

			double pepMass = Double.parseDouble(eFeas.attributeValue("pepMr"));
			
			String [] sp = eFeas.attributeValue("Glyco_Percents").split("_");
			double [] glycoPercents = new double [sp.length];
			for(int i=0;i<sp.length;i++){
				glycoPercents[i] = Double.parseDouble(sp[i]);
			}

			HashSet <ProteinReference> refset = new HashSet <ProteinReference>();
			HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
			HashMap <String, SimpleProInfo> proInfoMap = new HashMap <String, SimpleProInfo>();
			
			String proref = "";
			String [] reflist = ref.split("\\$");
			
			for(int i=0;i<reflist.length;i++){
				String [] sss = reflist[i].split("\\+");
				ProteinReference pr = ProteinReference.parse(sss[0]);
				refset.add(pr);
				
				int beg = Integer.parseInt(sss[1]);
				int end = Integer.parseInt(sss[2]);
				
				if(sss.length==3){
					SeqLocAround sla = new SeqLocAround(beg, end, "", "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==4){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==5){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
					locAroundMap.put(sss[0], sla);
				}
				
				String refname = pr.getName();
				SimpleProInfo info = this.accesser.getProInfo(refname);
				proInfoMap.put(pr.toString(), info);
				proref += info.getPartRef();
				proref += ";";
			}
			
			GlycoPeptide peptide = new GlycoPeptide(seq, pepCharge, refset, baseName, 
					scanBeg, scanEnd, locAroundMap, pepMass);
			
			peptide.setRetentionTime(peprt);
			peptide.setGlycoPercents(glycoPercents);
			peptide.setProInfoMap(proInfoMap);
			peptide.setDelegateReference(proref.substring(0, proref.length()-1));
			
			ArrayList <GlycoSite> sitelist = new ArrayList <GlycoSite>();
			
			FreeFeatures fs = new FreeFeatures();
			Iterator <Element> itF = eFeas.nodeIterator();
			while(itF.hasNext()){
		
				Element ef = itF.next();

				if(ef.getName().equals("Feature")){
					
					int scannum = Integer.parseInt(ef.attributeValue("scannum"));
					double rt = Double.parseDouble(ef.attributeValue("retention_time"));
//					double [] intens = new double[3];
//					intens[0] = Float.parseFloat(ef.attributeValue("intensity_1"));
//					intens[1] = Float.parseFloat(ef.attributeValue("intensity_2"));
//					intens[2] = Float.parseFloat(ef.attributeValue("intensity_3"));
					double intensity = Double.parseDouble(ef.attributeValue("retention_time"));
					FreeFeature f = new FreeFeature(scannum, pepMr, rt, intensity);
					fs.addFeature(f);
					
				}else if(ef.getName().equals("Site_Info")){
					
					ModSite site = ModSite.newInstance_aa(ef.attributeValue("Site").charAt(0));
					int loc = Integer.parseInt(ef.attributeValue("Loc"));
					char sym = ef.attributeValue("Symbol").charAt(0);
					double mass = Double.parseDouble(ef.attributeValue("Mass"));
					
					peptide.addGlycoSite(new GlycoSite(site, loc, sym));
					
				}else if(ef.getName().equals("GlycoSpectra")){
					
					Iterator <Element> glycanIt = ef.elementIterator("Glycan");
					while(glycanIt.hasNext()){
						
						Element eGlycan = glycanIt.next();
						Integer scannum = Integer.parseInt(eGlycan.attributeValue("ScanNum"));
						int rt = Integer.parseInt(eGlycan.attributeValue("RT"));
						
						String peakOneLine = this.peakOneLineMap.get(scannum);
						MS2PeakList ms2PeakList = MS2PeakList.parsePeaksOneLine(peakOneLine);
						IPeak [] peaks = ms2PeakList.getPeakArray();
						double preMz = ms2PeakList.getPrecursePeak().getMz();
						int preCharge = ms2PeakList.getPrecursePeak().getCharge();

						int rank = Integer.parseInt(eGlycan.attributeValue("Rank"));
						double score = Double.parseDouble(eGlycan.attributeValue("Score"));
						double mass = Double.parseDouble(eGlycan.attributeValue("GlycoMass"));
						double pepmass = Double.parseDouble(eGlycan.attributeValue("PeptideMassExperiment"));
						int bestMatchPep = Integer.parseInt(eGlycan.attributeValue("BestMatchPep"));
						String matched = eGlycan.attributeValue("MatchedPeaks");
						String glycoCT = eGlycan.attributeValue("GlycoCT").replaceAll(" ", "\n");
						String name = eGlycan.attributeValue("Name");
						
						HashSet <Integer> matchedPeaks = new HashSet <Integer>();
						String [] ss = matched.split("_");
						for(int i=0;i<ss.length;i++){
							matchedPeaks.add(Integer.parseInt(ss[i]));
						}
						
						GlycoTree tree = new GlycoTree(glycoCT);
						tree.setIupacName(name);
						tree.setMonoMass(mass);

						NGlycoSSM ssm = new NGlycoSSM(scannum, preCharge, preMz, pepmass, peaks, rank, matchedPeaks, tree, score);
						ssm.setRT(rt);
						ssm.setBestPepScannum(bestMatchPep);
						peptide.addHcdPsmInfo(ssm);
					}
				}
			}

			fs.setInfo();

			GlycoPepFreeFeatures pair = new GlycoPepFreeFeatures(peptide, 
					fs, LabelType.LabelFree, sitelist.toArray(new GlycoSite [sitelist.size()]));

			return pair;
		}
		return null;
	}
		
	protected GlycoPeptide getGlycoPeptide(){
		
		if(feasIt.hasNext()){
			
			Element eFeas = feasIt.next();
			
			String baseName = eFeas.attributeValue("BaseName");
			int scanBeg = Integer.parseInt(eFeas.attributeValue("scanBeg"));
			int scanEnd = Integer.parseInt(eFeas.attributeValue("scanEnd"));
			String seq = eFeas.attributeValue("Sequence");
			short pepCharge = Short.parseShort(eFeas.attributeValue("Charge"));
			double peprt = Double.parseDouble(eFeas.attributeValue("rt"));
			double pepMr = Double.parseDouble(eFeas.attributeValue("pepMr"));
			String ref = eFeas.attributeValue("Reference");

			double pepMass = Double.parseDouble(eFeas.attributeValue("pepMr"));
			
			String [] sp = eFeas.attributeValue("Glyco_Percents").split("_");
			double [] glycoPercents = new double [sp.length];
			for(int i=0;i<sp.length;i++){
				glycoPercents[i] = Double.parseDouble(sp[i]);
			}

			HashSet <ProteinReference> refset = new HashSet <ProteinReference>();
			HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
			HashMap <String, SimpleProInfo> proInfoMap = new HashMap <String, SimpleProInfo>();
			
			String proref = "";
			String [] reflist = ref.split("\\$");
			
			for(int i=0;i<reflist.length;i++){
				String [] sss = reflist[i].split("\\+");
				ProteinReference pr = ProteinReference.parse(sss[0]);
				refset.add(pr);
				
				int beg = Integer.parseInt(sss[1]);
				int end = Integer.parseInt(sss[2]);
				
				if(sss.length==3){
					SeqLocAround sla = new SeqLocAround(beg, end, "", "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==4){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==5){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
					locAroundMap.put(sss[0], sla);
				}
				
				String refname = pr.getName();
				SimpleProInfo info = this.accesser.getProInfo(refname);
				proInfoMap.put(pr.toString(), info);
				proref += info.getPartRef();
				proref += ";";
			}
			
			GlycoPeptide peptide = new GlycoPeptide(seq, pepCharge, refset, baseName, 
					scanBeg, scanEnd, locAroundMap, pepMass);
			
			peptide.setRetentionTime(peprt);
			peptide.setGlycoPercents(glycoPercents);
			peptide.setProInfoMap(proInfoMap);
			peptide.setDelegateReference(proref.substring(0, proref.length()-1));
			
			ArrayList <GlycoSite> sitelist = new ArrayList <GlycoSite>();
			
			FreeFeatures fs = new FreeFeatures();
			Iterator <Element> itF = eFeas.nodeIterator();
			while(itF.hasNext()){
		
				Element ef = itF.next();

				if(ef.getName().equals("Feature")){
					
					int scannum = Integer.parseInt(ef.attributeValue("scannum"));
					double rt = Double.parseDouble(ef.attributeValue("retention_time"));
//					double [] intens = new double[3];
//					intens[0] = Float.parseFloat(ef.attributeValue("intensity_1"));
//					intens[1] = Float.parseFloat(ef.attributeValue("intensity_2"));
//					intens[2] = Float.parseFloat(ef.attributeValue("intensity_3"));
					double intensity = Double.parseDouble(ef.attributeValue("retention_time"));
					FreeFeature f = new FreeFeature(scannum, pepMr, rt, intensity);
					fs.addFeature(f);
					
				}else if(ef.getName().equals("Site_Info")){
					
					ModSite site = ModSite.newInstance_aa(ef.attributeValue("Site").charAt(0));
					int loc = Integer.parseInt(ef.attributeValue("Loc"));
					char sym = ef.attributeValue("Symbol").charAt(0);
					double mass = Double.parseDouble(ef.attributeValue("Mass"));
					
					peptide.addGlycoSite(new GlycoSite(site, loc, sym));
					
				}else if(ef.getName().equals("GlycoSpectra")){
					
					Iterator <Element> glycanIt = ef.elementIterator("Glycan");
					while(glycanIt.hasNext()){
						
						Element eGlycan = glycanIt.next();
						Integer scannum = Integer.parseInt(eGlycan.attributeValue("ScanNum"));
						double rt = Double.parseDouble(eGlycan.attributeValue("RT"));
						
						String peakOneLine = this.peakOneLineMap.get(scannum);
						MS2PeakList ms2PeakList = MS2PeakList.parsePeaksOneLine(peakOneLine);
						IPeak [] peaks = ms2PeakList.getPeakArray();
						double preMz = ms2PeakList.getPrecursePeak().getMz();
						int preCharge = ms2PeakList.getPrecursePeak().getCharge();

						int rank = Integer.parseInt(eGlycan.attributeValue("Rank"));
						double score = Double.parseDouble(eGlycan.attributeValue("Score"));
						double mass = Double.parseDouble(eGlycan.attributeValue("GlycoMass"));
						double pepmass = Double.parseDouble(eGlycan.attributeValue("PeptideMassExperiment"));
						int bestMatchPep = Integer.parseInt(eGlycan.attributeValue("BestMatchPep"));
						String matched = eGlycan.attributeValue("MatchedPeaks");
						String glycoCT = eGlycan.attributeValue("GlycoCT").replaceAll(" ", "\n");
						String name = eGlycan.attributeValue("Name");
						
						HashSet <Integer> matchedPeaks = new HashSet <Integer>();
						String [] ss = matched.split("_");
						for(int i=0;i<ss.length;i++){
							matchedPeaks.add(Integer.parseInt(ss[i]));
						}
						
						GlycoTree tree = new GlycoTree(glycoCT);
						tree.setIupacName(name);
						tree.setMonoMass(mass);

						NGlycoSSM ssm = new NGlycoSSM(scannum, preCharge, preMz, pepmass, peaks, rank, matchedPeaks, tree, score);
						ssm.setRT(rt);
						ssm.setBestPepScannum(bestMatchPep);
						peptide.addHcdPsmInfo(ssm);
						if(peptide.getDeleStructure()==null) peptide.setDeleStructure(ssm);
					}
				}
			}
			return peptide;
		}
		return null;
	}
	
	public HashMap <String, ArrayList <GlycoPepFreeFeatures>> getPairMap(){
		
		GlycoPepFreeFeatures [] pairs = this.allPairs;
		
		HashMap <String, ArrayList <GlycoPepFreeFeatures>> pairMap = 
			new HashMap <String, ArrayList <GlycoPepFreeFeatures>>();
		
		for(int i=0;i<pairs.length;i++){
			String seq = pairs[i].getSequence();
			if(pairMap.containsKey(seq)){
				pairMap.get(seq).add((GlycoPepFreeFeatures) pairs[i]);
			}else{
				ArrayList <GlycoPepFreeFeatures> list = new ArrayList <GlycoPepFreeFeatures>();
				list.add((GlycoPepFreeFeatures) pairs[i]);
				pairMap.put(seq, list);
			}
		}
		
		return pairMap;
	}

//	public GlycoPepFreeFeatures [] getAllSelectedPairs(){
		
//		return allPairs;
//	}

	public int getPairNum(){
		return allPep.length;
	}
	
	public IGlycoPeptide [] getAllSelectedPeps(){
		return allPep;
	}
	
	public IGlycoPeptide [] getAllSelectedPeps(int [] idx){
		
		ArrayList <IGlycoPeptide> list = new ArrayList <IGlycoPeptide>();
		for(int i=0;i<idx.length;i++){
			list.add(this.allPep[idx[i]]);
		}
		
		return list.toArray(new IGlycoPeptide[list.size()]);
	}
	
	public IGlycoPeptide getGlycoPep(int id){
		return this.allPep[id];
	}	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String file1 = "H:\\glyco\\label-free\\20111123_HILIC_1105_HCD.pxml";
		String file2 = "H:\\glyco\\label-free\\final\\20111123_HILIC_1105_HCD_111124034738_match_1.pxml";
		GlycoLFFeasXMLReader reader1 = new GlycoLFFeasXMLReader(file1);
		HashMap <String, HashSet<String>> map = new HashMap <String, HashSet<String>>();
		HashSet <Integer> set1 = new HashSet <Integer>();
		HashSet <String> s1 = new HashSet <String>();
		HashSet <String> s2 = new HashSet <String>();
		ArrayList <Double> avelist = new ArrayList <Double>();
		HashMap <String, ArrayList <Double>> mwMap = new HashMap <String, ArrayList <Double>>();
		System.out.println(reader1.allPep.length);
/*		for(int i=0;i<reader1.allPairs.length;i++){
			GlycoPepFreeFeatures pair = reader1.allPairs[i];
			IGlycoPeptide pep = (IGlycoPeptide) pair.getPeptide();
			HashMap<Integer, ArrayList<NGlycoSSM>> ssmmap = pep.getHcdPsmInfoMap();
			set1.addAll(ssmmap.keySet());
			NGlycoSSM ssm = pep.getDeleStructure();
			String seq = pair.getSequence();
			String glycan = pair.getGlycanInfo().split("\t")[0];
//			if(set1.add(ssm.getScanNum())){
				s1.add(seq);
				s2.add(glycan);
				String key = seq.substring(2, seq.length()-2);
//				if(key.equals("AGPN*GTLFVADAYK")){
					double glycoMass = ssm.getGlycoMass();
					double pepMass = pep.getPepMrNoGlyco();
					double glycoPepMass = ssm.getPepMass();
					double deltaMass = glycoPepMass - pepMass;
					double ppm = deltaMass/glycoPepMass*1E6;
//					System.out.println(ppm);
					avelist.add(Math.abs(ppm));
					if(map.containsKey(seq)){
						map.get(seq).add(glycan);
						mwMap.get(seq).add(glycoMass);
					}else{
						HashSet <String> set = new HashSet <String>();
						set.add(glycan);
						map.put(seq, set);
						ArrayList <Double> list = new ArrayList <Double>();
						list.add(glycoMass);
						mwMap.put(seq, list);
					}
//				}
//			}
		}
		GlycoLFFeasXMLReader reader2 = new GlycoLFFeasXMLReader(file2);
		HashSet <Integer> set2 = new HashSet <Integer>();
		for(int i=0;i<reader2.allPairs.length;i++){
			GlycoPepFreeFeatures pair = reader2.allPairs[i];
			IGlycoPeptide pep = (IGlycoPeptide) pair.getPeptide();
			HashMap<Integer, ArrayList<NGlycoSSM>> ssmmap = pep.getHcdPsmInfoMap();
			set1.addAll(ssmmap.keySet());
			NGlycoSSM ssm = pep.getDeleStructure();
			String seq = pair.getSequence();
			String glycan = pair.getGlycanInfo().split("\t")[0];
//			if(set2.add(ssm.getScanNum())){
				s1.add(seq);
				s2.add(glycan);
				String key = seq.substring(2, seq.length()-2);
//				if(key.equals("AGPN*GTLFVADAYK")){
					double glycoMass = ssm.getGlycoMass();
					double pepMass = pep.getPepMrNoGlyco();
					double glycoPepMass = ssm.getPepMass();
					double deltaMass = glycoPepMass - pepMass;
					double ppm = deltaMass/glycoPepMass*1E6;
//					System.out.println(ppm);
					avelist.add(Math.abs(ppm));
					if(map.containsKey(seq)){
						map.get(seq).add(glycan);
						mwMap.get(seq).add(glycoMass);
					}else{
						HashSet <String> set = new HashSet <String>();
						set.add(glycan);
						map.put(seq, set);
						ArrayList <Double> list = new ArrayList <Double>();
						list.add(glycoMass);
						mwMap.put(seq, list);
					}
//				}
//			}
		}
		
		Iterator <String> it = map.keySet().iterator();
		int total = 0;
		while(it.hasNext()){
			String key = it.next();
			total+=map.get(key).size();
		}
		System.out.println(map.size()+"\t"+total);
		System.out.println(s1.size()+"\t"+s2.size());
		System.out.println(MathTool.getAve(avelist));
		
		GlycoIdenXMLReader ir1 = new GlycoIdenXMLReader("H:\\glyco\\label-free\\final\\" +
			"20111123_HILIC_1105_HCD.pxml");
		NGlycoSSM [] irs1 = ir1.getAllMatches();
		for(int i=0;i<irs1.length;i++){
			if(!set1.contains(irs1[i].getScanNum()))
				System.out.println(irs1[i].getScanNum());
		}
		
		GlycoIdenXMLReader ir2 = new GlycoIdenXMLReader("H:\\glyco\\label-free\\final\\" +
			"20111123_HILIC_1105_HCD_111124034738.pxml");
		NGlycoSSM [] irs2 = ir2.getAllMatches();
		for(int i=0;i<irs2.length;i++){
			if(!set1.contains(irs2[i].getScanNum()))
				System.out.println(irs2[i].getScanNum());
		}


		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("H:\\glyco\\label-free\\final\\�½� �ı��ĵ�.txt"));
			String line = br.readLine();
			int eq = 0;
			while((line=br.readLine())!=null){
				String [] ss = line.split("\t");
				if(mwMap.containsKey(ss[4])){
					double mass = Double.parseDouble(ss[0]);
					ArrayList <Double> list = mwMap.get(ss[4]);
					for(int i=0;i<list.size();i++){
						if(Math.abs(mass-list.get(i))<1){
							eq++;
						}
					}
				}else{
					System.out.println(ss[4]);
				}
			}
			System.out.println(eq);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	}

	
}
