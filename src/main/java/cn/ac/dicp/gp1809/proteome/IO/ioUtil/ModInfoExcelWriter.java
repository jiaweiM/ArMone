/* 
 ******************************************************************************
 * File: ModInfoExcelWriter.java * * * Created on 2011-9-20
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.ModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.Ascore.AscoreCalculator;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.IPhosphoSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.ISequestPhosphoPeptidePair;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 *
 * @version 2011-9-20, 13:55:44
 */
public class ModInfoExcelWriter {
	
	private ExcelWriter writer;
	private ExcelFormat ef1 = ExcelFormat.normalFormat;
	private IPeptideListReader reader;
	private IPeptideFormat <?> pepformat;
	private Protein [] proteins;
	private HashMap <String, IMS2PeakList> peakMap;
	private HashMap <Integer, Integer> countMap;
	private AminoacidFragment aaf;
	private boolean calAscore;
	private boolean nGlycan;
	private boolean apivase;
	private ISpectrumThreshold threshold = SpectrumThreshold.PERCENT_1_INTENSE_THRESHOLD;
	
	private int [] types;
	private AscoreCalculator ascoreCal;
	private String modName;
	private char symbol;
	private DecimalFormat df2 = DecimalFormats.DF0_2;
	
	public ModInfoExcelWriter(String file, IPeptideListReader reader, String modName, char symbol, String sites) throws IOException, 
		PeptideParsingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException, 
		RowsExceededException, WriteException{
		
		this.writer = new ExcelWriter(file, new String []{"Modified Peptide", "Site", "Unique Site"});
		this.reader = reader;
		this.pepformat = reader.getPeptideFormat();
		
		ISearchParameter para = reader.getSearchParameter();
		AminoacidModification aamodif = para.getVariableInfo();
		aaf = new AminoacidFragment(para.getStaticInfo(), aamodif);
//		types = new int[] { Ion.TYPE_B, Ion.TYPE_Y, Ion.TYPE_C, Ion.TYPE_Z };
		types = new int[] { Ion.TYPE_B, Ion.TYPE_Y,};

		if(sites.length()>0){
			
			if(modName.equals("N-Glyco")){
				
				this.nGlycan = true;
				
			}else{
				
				PeptideType pepType = reader.getPeptideType();
				if(pepType==PeptideType.APIVASE_SEQUEST){
					
					this.apivase = true;

				}else{
					
					this.ascoreCal = new AscoreCalculator("["+sites+"]");
//					this.calAscore = true;
				}
			}
		}

		this.modName = modName;
		this.symbol = symbol;
		
		this.countMap = new HashMap <Integer, Integer>();
		this.addTitle();
		this.iniProteins();		
	}
	
	private void addTitle() throws RowsExceededException, WriteException{
		
		String peptitle = pepformat.getSimpleFormatTitle();

		String title1 = "Site number\t";
		if(calAscore){
			title1 += "Ascore\tNew sequence\t";
		}else if(apivase){
			title1 += "Ascore\t";
		}
		title1 += peptitle;
		title1 += "Reference";

		this.writer.addTitle(title1, 0, ef1);
		
		String title2 = "Site\tSequence Around\t";
		if(calAscore || apivase){
			title2 += "Ascore\t";
		}
		title2 += "Reference\t";
		title2 += "Modified Sequence\tScan(s)";
		
		this.writer.addTitle(title2, 1, ef1);
		this.writer.addTitle(title2, 2, ef1);
	}
	
	private void iniProteins() throws PeptideParsingException, ProteinNotFoundInFastaException, 
		MoreThanOneRefFoundInFastaException, FastaDataBaseException{
		this.peakMap = new HashMap <String, IMS2PeakList>();
		ProteinNameAccesser accesser = reader.getProNameAccesser();
		Proteins2 pros2 = new Proteins2(accesser);
		IPeptide pep;
		while((pep=reader.getPeptide())!=null){
			pros2.addPeptide(pep);
			IMS2PeakList peaks = reader.getPeakLists()[0];
			peakMap.put(pep.getScanNum(), peaks);
		}
		this.proteins = pros2.getAllProteins();
		Arrays.sort(proteins);
	}
	
	public void write() throws PeptideParsingException, RowsExceededException, WriteException, IOException{

		HashSet <String> usedPepSet = new HashSet <String>();
		HashSet <String> usedSite = new HashSet <String>();
		HashMap <String, SiteInfo> usedMap = new HashMap <String, SiteInfo>();

		for(int i=0;i<proteins.length;i++){
			String refName = proteins[i].getRefwithSmallestMw().getName();
			IPeptide [] peps = proteins[i].getAllPeptides();
			for(int j=0;j<peps.length;j++){
				
				String scanname = peps[j].getScanNum();
				if(usedPepSet.contains(scanname)){
					continue;
				}
				
				if(calAscore){
					
					this.addPTMInfoWithAscore(peps[j], peakMap.get(scanname), refName, usedPepSet, usedMap);
				
				}else{
					if(nGlycan)
						this.addPTMInfoNGlycan(peps[j], refName, usedPepSet, usedSite);
					
					else if(apivase)
						this.addPTMInfoAPISEQ(peps[j], refName, usedPepSet, usedMap);
					
					else
						this.addPTMInfoNoAscore(peps[j], refName, usedPepSet, usedSite);
				}
			}
		}

		if(calAscore || apivase){
			Iterator <String> it = usedMap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();

				SiteInfo info = usedMap.get(key);
				this.writer.addContent(info.toString(), 2, ef1);
			}
		}
		
		this.addSummary(usedPepSet.size());
		this.close();
	}
	
	private void addPTMInfoWithAscore(IPeptide pep, IMS2PeakList peak, String proRefName, 
			HashSet <String> usedPepSet, HashMap <String, SiteInfo> usedMap) 
		throws RowsExceededException, WriteException{

		String seq = pep.getSequence();
		char preAA = seq.charAt(0);
		char nexAA = seq.charAt(seq.length()-1);
		String scannum = pep.getScanNum();

		String noTermSeq = seq.substring(2, seq.lastIndexOf("."));
		char [] cs = noTermSeq.toCharArray();
		StringBuilder uniqueSb = new StringBuilder();
		StringBuilder noTagModSb = new StringBuilder();
		ArrayList <IModifSite> modlist = new ArrayList <IModifSite>();
		ModSite mod = null;
		for(int i=0;i<cs.length;i++){
			
			if(cs[i]>='A' && cs[i]<='Z'){
				mod = ModSite.newInstance_aa(cs[i]);
				uniqueSb.append(cs[i]);
				noTagModSb.append(cs[i]);
				
			}else if(cs[i]==symbol){
				modlist.add(new ModifSite(mod, uniqueSb.length(), symbol));
			}else{
				noTagModSb.append(cs[i]);
			}
		}
		
		if(modlist.size()==0)
			return;
		
		IModifSite [] mods = modlist.toArray(new IModifSite[modlist.size()]);
		IModifiedPeptideSequence noTarModSeq = new ModifiedPeptideSequence(noTagModSb.toString(), preAA, nexAA);
		System.out.println(pep.getSequence()+"\t"+noTarModSeq);
		SeqvsAscore sva = ascoreCal.compute(noTarModSeq, pep
		        .getCharge(), peak, mods.length, aaf, types, threshold, symbol);

		double [] ascores = null;
		IModifiedPeptideSequence newseq = null;
		IModifSite [] newmods = null;
		
		if(sva!=null){
			
			ascores = sva.getAscores();
			newseq = sva.getSequence();
			newmods = newseq.getTargetMod(symbol);
			
		}else{
			
			newseq = ModifiedPeptideSequence.parseSequence(seq);
			newmods = newseq.getTargetMod(symbol);
			ascores = new double[newmods.length];
		}
//		if(scannum.contains("4738.7"))System.out.println(pep.getSequence()+"\t"+ascores.length+"\t"+newmods.length+"\t"+ascores[0]);
//		if(ascores.length!=newmods.length)
//			return;

		HashMap <String, SeqLocAround> seqLocMap = pep.getPepLocAroundMap();
		SeqLocAround sla = null;
		HashSet <ProteinReference> refset = pep.getProteinReferences();
		Iterator <ProteinReference> it = refset.iterator();
		while(it.hasNext()){
			ProteinReference pr = it.next();
			String refname = pr.getName();
			
			if(proRefName.contains(refname)){
				sla = seqLocMap.get(pr.toString());
				break;
			}
		}

		if(sla==null)
			return;
		
		int num = newmods.length;
		if(countMap.containsKey(num)){
			int count = countMap.get(num)+1;
			countMap.put(num, count);
		}else{
			countMap.put(num, 1);
		}
		usedPepSet.add(scannum);

		StringBuilder sb = new StringBuilder();
		sb.append(num).append("\t");
		for(int i=0;i<ascores.length;i++){
			sb.append(df2.format(ascores[i])).append(";");
		}
		sb.append("\t");
		sb.append(newseq.getFormattedSequence()).append("\t");
//		int en = newseq.getFormattedSequence().equals(seq) ? 0 : 1;
//		sb.append(en).append("\t");
		sb.append(pep.toSimpleInfoFormat());
		sb.append(proRefName).append("");
		
		writer.addContent(sb.toString(), 0, ef1);

		String [] sites = new String [newmods.length];
		String [] aaround = new String [newmods.length];
		
		int beg = sla.getBeg();
		String pre = sla.getPre();
		String next = sla.getNext();
		
		String fullSeq = pre + uniqueSb + next;
		
		for(int i=0;i<newmods.length;i++){
			int loc = newmods[i].modifLocation()+beg-1;
			sites[i] = newmods[i].modifiedAt().getModifAt()+loc;
			
			int aaroundBeg = newmods[i].modifLocation()+pre.length()-8>0 ? newmods[i].modifLocation()+pre.length()-8 : 0;
			int aaroundEnd = newmods[i].modifLocation()+pre.length()+7<fullSeq.length() ? 
					newmods[i].modifLocation()+pre.length()+7 : fullSeq.length();
					
			aaround[i] = fullSeq.substring(aaroundBeg, aaroundEnd);
			double ascore = i>=ascores.length? ascores[0] : ascores[i];
			
			SiteInfo info = new SiteInfo(sites[i], aaround[i], ascore, proRefName, 
					newseq.getFormattedSequence(), scannum);
			this.writer.addContent(info.toString(), 1, ef1);
			
			String key = sites[i]+proRefName;
			
			if(usedMap.containsKey(key)){
				SiteInfo info0 = usedMap.get(key);
				if(ascore>info0.ascore){
					usedMap.put(key, info);
				}
			}else{
				usedMap.put(key, info);
			}
		}
	}
	
	private void addPTMInfoNoAscore(IPeptide pep, String proRefName, 
			HashSet <String> usedPepSet, HashSet <String> usedSite) 
		throws RowsExceededException, WriteException{
		
		String seq = pep.getSequence();
		String noTermSeq = seq.substring(2, seq.lastIndexOf("."));
		char [] cs = noTermSeq.toCharArray();
		StringBuilder uniqueSb = new StringBuilder();
		ArrayList <IModifSite> modlist = new ArrayList <IModifSite>();
		ModSite mod = null;
		for(int i=0;i<cs.length;i++){
			
			if(cs[i]>='A' && cs[i]<='Z'){
				
				mod = ModSite.newInstance_aa(cs[i]);
				uniqueSb.append(cs[i]);
				
			}else if(cs[i]==symbol){
				modlist.add(new ModifSite(mod, uniqueSb.length(), symbol));
			}
		}

		if(modlist.size()==0)
			return;
		
		IModifSite [] mods = modlist.toArray(new IModifSite[modlist.size()]);
		
		HashMap <String, SeqLocAround> seqLocMap = pep.getPepLocAroundMap();
		SeqLocAround sla = null;
		HashSet <ProteinReference> refset = pep.getProteinReferences();
		Iterator <ProteinReference> it = refset.iterator();
		while(it.hasNext()){
			ProteinReference pr = it.next();
			String refname = pr.getName();
			if(proRefName.contains(refname)){
				sla = seqLocMap.get(pr.toString());
			}
		}
		
		if(sla==null)
			return;
		
		int num = mods.length;
		if(countMap.containsKey(num)){
			int count = countMap.get(num)+1;
			countMap.put(num, count);
		}else{
			countMap.put(num, 1);
		}
		usedPepSet.add(pep.getScanNum());
		
		StringBuilder sb = new StringBuilder();
		sb.append(num).append("\t");
		sb.append(pep.toSimpleInfoFormat());
		sb.append(proRefName);
		
		writer.addContent(sb.toString(), 0, ef1);

		String [] sites = new String [mods.length];
		String [] aaround = new String [mods.length];
		
		int beg = sla.getBeg();
		String pre = sla.getPre();
		String next = sla.getNext();
		
		String fullSeq = pre + uniqueSb + next;
		
		for(int i=0;i<mods.length;i++){
			int loc = mods[i].modifLocation()+beg-1;
			sites[i] = mods[i].modifiedAt().getModifAt()+loc;
			
			int aaroundBeg = mods[i].modifLocation()+pre.length()-8>0 ? mods[i].modifLocation()+pre.length()-8 : 0;
			int aaroundEnd = mods[i].modifLocation()+pre.length()+7<fullSeq.length() ? 
					mods[i].modifLocation()+pre.length()+7 : fullSeq.length();
					
			aaround[i] = fullSeq.substring(aaroundBeg, aaroundEnd);
			
			String content1 = sites[i]+"\t"+aaround[i]+"\t"+proRefName+"\t"+seq+"\t"+pep.getScanNum();
			this.writer.addContent(content1, 1, ef1);
			
			String key = sites[i]+proRefName;
			if(!usedSite.contains(key)){
				this.writer.addContent(content1, 2, ef1);
				usedSite.add(key);
			}
		}
	}
	
	private void addPTMInfoAPISEQ(IPeptide pep, String proRefName, 
			HashSet <String> usedPepSet, HashMap <String, SiteInfo> usedMap) 
		throws RowsExceededException, WriteException{
		
		ISequestPhosphoPeptidePair seqPair = (ISequestPhosphoPeptidePair) pep;
		
		IPhosphoSite [] mods = seqPair.getPhosphoSites();
		double [] as = seqPair.getAscores();
		if(as.length!=mods.length){
			double aaa = as[0];
			as = new double[mods.length];
			Arrays.fill(as, aaa);
		}
		String unique = PeptideUtil.getUniqueSequence(pep.getSequence());
		
		HashMap <String, SeqLocAround> seqLocMap = pep.getPepLocAroundMap();
		SeqLocAround sla = null;
		HashSet <ProteinReference> refset = pep.getProteinReferences();
		Iterator <ProteinReference> it = refset.iterator();
		while(it.hasNext()){
			ProteinReference pr = it.next();
			String refname = pr.getName();
			if(proRefName.contains(refname)){
				sla = seqLocMap.get(pr.toString());
			}
		}
		
		if(sla==null)
			return;
		
		int num = mods.length;
		if(countMap.containsKey(num)){
			int count = countMap.get(num)+1;
			countMap.put(num, count);
		}else{
			countMap.put(num, 1);
		}
		usedPepSet.add(pep.getScanNum());
		
		StringBuilder sb = new StringBuilder();
		sb.append(num).append("\t");
		for(int i=0;i<as.length;i++){
			sb.append(df2.format(as[i])).append(";");
		}
		sb.append("\t");
		sb.append(pep.toSimpleInfoFormat());
		sb.append(proRefName);
		
		writer.addContent(sb.toString(), 0, ef1);

		String [] sites = new String [mods.length];
		String [] aaround = new String [mods.length];
		
		int beg = sla.getBeg();
		String pre = sla.getPre();
		String next = sla.getNext();
		
		String fullSeq = pre + unique + next;
		
		for(int i=0;i<mods.length;i++){
			int loc = mods[i].modifLocation()+beg-1;
			sites[i] = mods[i].modifiedAt().getModifAt()+loc;
			
			int aaroundBeg = mods[i].modifLocation()+pre.length()-8>0 ? mods[i].modifLocation()+pre.length()-8 : 0;
			int aaroundEnd = mods[i].modifLocation()+pre.length()+7<fullSeq.length() ? 
					mods[i].modifLocation()+pre.length()+7 : fullSeq.length();
					
			aaround[i] = fullSeq.substring(aaroundBeg, aaroundEnd);
			
			SiteInfo info = new SiteInfo(sites[i], aaround[i], as[i], proRefName, pep.getSequence(), pep.getScanNum());
			this.writer.addContent(info.toString(), 1, ef1);
		
			String key = sites[i]+proRefName;
			
			if(usedMap.containsKey(key)){
				SiteInfo info0 = usedMap.get(key);
				if(as[i]>info0.ascore){
					usedMap.put(key, info);
				}
			}else{
				usedMap.put(key, info);
			}
		}
	}
	
	private void addPTMInfoNGlycan(IPeptide pep, String proRefName, 
			HashSet <String> usedPepSet, HashSet <String> usedSite) 
		throws RowsExceededException, WriteException{

		String seq = pep.getSequence();
		String noTermSeq = seq.substring(2, seq.lastIndexOf("."));
		HashSet<Integer> set = new HashSet<Integer>();
		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		Matcher m1 = N_GLYCO.matcher(seq);
		while(m1.find()){
			set.add(m1.start()-1);
		}
		
		char [] cs = noTermSeq.toCharArray();
		StringBuilder uniqueSb = new StringBuilder();
		ArrayList <IModifSite> modlist = new ArrayList <IModifSite>();
		ModSite mod = null;
		for(int i=0;i<cs.length;i++){
			
			if(cs[i]>='A' && cs[i]<='Z'){
				
				mod = ModSite.newInstance_aa(cs[i]);
				uniqueSb.append(cs[i]);
				
			}else if(cs[i]==symbol){
				if(set.contains(i))
					modlist.add(new ModifSite(mod, uniqueSb.length(), symbol));
			}
		}

		if(modlist.size()==0)
			return;
		
		IModifSite [] mods = modlist.toArray(new IModifSite[modlist.size()]);
		
		HashMap <String, SeqLocAround> seqLocMap = pep.getPepLocAroundMap();
		SeqLocAround sla = null;
		HashSet <ProteinReference> refset = pep.getProteinReferences();
		Iterator <ProteinReference> it = refset.iterator();
		while(it.hasNext()){
			ProteinReference pr = it.next();
			String refname = pr.getName();
			if(proRefName.contains(refname)){
				sla = seqLocMap.get(pr.toString());
			}
		}

		if(sla==null){
			return;
		}

		int num = mods.length;
		if(countMap.containsKey(num)){
			int count = countMap.get(num)+1;
			countMap.put(num, count);
		}else{
			countMap.put(num, 1);
		}
		usedPepSet.add(pep.getScanNum());
		
		StringBuilder sb = new StringBuilder();
		sb.append(num).append("\t");
		sb.append(pep.toSimpleInfoFormat());
		sb.append(proRefName);
		
		writer.addContent(sb.toString(), 0, ef1);

		String [] sites = new String [mods.length];
		String [] aaround = new String [mods.length];
		
		int beg = sla.getBeg();
		String pre = sla.getPre();
		String next = sla.getNext();
		
		String fullSeq = pre + uniqueSb + next;
		
		for(int i=0;i<mods.length;i++){
			int loc = mods[i].modifLocation()+beg-1;
			sites[i] = mods[i].modifiedAt().getModifAt()+loc;
			
			int aaroundBeg = mods[i].modifLocation()+pre.length()-8>0 ? mods[i].modifLocation()+pre.length()-8 : 0;
			int aaroundEnd = mods[i].modifLocation()+pre.length()+7<fullSeq.length() ? 
					mods[i].modifLocation()+pre.length()+7 : fullSeq.length();
					
			aaround[i] = fullSeq.substring(aaroundBeg, aaroundEnd);
			
			String content1 = sites[i]+"\t"+aaround[i]+"\t"+proRefName+"\t"+seq+"\t"+pep.getScanNum();
			this.writer.addContent(content1, 1, ef1);
			
			String key = sites[i]+proRefName;
			if(!usedSite.contains(key)){
				this.writer.addContent(content1, 2, ef1);
				usedSite.add(key);
			}
		}
	}

	private void addSummary(int pepCount) throws RowsExceededException, WriteException{
		
		this.writer.addBlankRow(0);
		this.writer.addBlankRow(0);
		this.writer.addContent("Summary", 0, ef1);
		this.writer.addContent("Variable mod\t"+this.modName, 0, ef1);
		this.writer.addBlankRow(0);

		DecimalFormat dfper = DecimalFormats.DF_PRECENT0_2;
		
		Integer [] numlist = this.countMap.keySet().toArray(new Integer[countMap.size()]);
		Arrays.sort(numlist);
		for(int i=0;i<numlist.length;i++){
			int count = countMap.get(numlist[i]);
			String percent = dfper.format((float)count/pepCount);
			String ss = "With "+numlist[i]+" mod\t"+count+"\t"+percent+"\n";
			this.writer.addContent(ss, 0, ef1);
		}
	}
	
	private void close() throws WriteException, IOException{
		this.reader.close();
		this.writer.close();
	}
	
	public static void batchWritePhos(String in) throws FileDamageException, IOException,
		RowsExceededException, PeptideParsingException, WriteException, 
		ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException{
		
		File [] files = (new File(in)).listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				if(arg0.getName().endsWith("ppl"))
					return true;
				return false;
			}
			
		});
		
		String modname = "Phospho";
		String site = "STY";
		char symbol = '@';
		
		for(int i=0;i<files.length;i++){
			
			IPeptideListReader reader = new PeptideListReader(files[i]);
			String path = files[i].getAbsolutePath();
			String out = path.replace("ppl", "site.xls");
			ModInfoExcelWriter writer = new ModInfoExcelWriter(out, reader, modname, symbol, site);
			writer.write();
		}
	}

	private class SiteInfo{
		
		private String site;
		private String sequence;
		private double ascore;
		private String ref;
		private String modseq;
		private String scanname;
		
		private SiteInfo(String site, String sequence, double ascore, String ref, String modseq, String scanname){
			this.site = site;
			this.sequence = sequence;
			this.ascore =ascore;
			this.ref = ref;
			this.modseq = modseq;
			this.scanname = scanname;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			
			sb.append(site).append("\t");
			sb.append(sequence).append("\t");
			sb.append(df2.format(ascore)).append("\t");
			sb.append(ref).append("\t");
			sb.append(modseq).append("\t");
			sb.append(scanname);
			
			return sb.toString();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws FastaDataBaseException 
	 * @throws WriteException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 * @throws PeptideParsingException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws FileDamageException, IOException, RowsExceededException, PeptideParsingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, WriteException, FastaDataBaseException {
		// TODO Auto-generated method stub

/*		String seq = "A.N#ASEEEEEN#SSS.A";
		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");
		Matcher m1 = N_GLYCO.matcher(seq);
		int gcount = m1.groupCount();
		System.out.println(gcount);
		while(m1.find()){
			gcount++;
			
		}
		System.out.println(gcount);
*/		
/*		String in = "\\\\searcher7\\E\\BYY\\BYY-RP-RP\\DAT files\\5600\\5600-F37.dat.ppl";
		String out = "\\\\searcher7\\E\\BYY\\BYY-RP-RP\\DAT files\\5600\\5600-F37.dat.site.xls";
		String modname = "Phospho";
		String site = "STY";
		char symbol = '#';
		IPeptideListReader reader = new PeptideListReader(in);
		ModInfoExcelWriter writer = new ModInfoExcelWriter(out, reader, modname, symbol, site);
		writer.write();
*/		
		ModInfoExcelWriter.batchWritePhos("H:\\新建文件夹 (2)");
	}

}
