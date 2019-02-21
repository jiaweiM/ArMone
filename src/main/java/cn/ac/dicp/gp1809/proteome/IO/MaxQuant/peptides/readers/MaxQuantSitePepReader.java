/* 
 ******************************************************************************
 * File: MaxQuantSitePepReader.java * * * Created on 2012-1-4
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotDBPattern;
import cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.DefaultMaxQuantPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.IMaxQuantPeptide;
import cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.IMaxQuantPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.MaxQuantPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aaproperties.AminoAcidProperty;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.AccessionFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaWriter;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;

/**
 * @author ck
 *
 * @version 2012-1-4, 14:19:35
 */
public class MaxQuantSitePepReader {
	
	private int scannameID = -1;
	
	private int scannumID = -1;
	
	private int ProteinsID = -1;
	
	private int PEPID = -1;
	
	private int ScoreID = -1;
	
	private int modSeqID = -1;
	
	private int positionID = -1;
	
	private int chargeID = -1;
	
	private int mzID = -1;
	
	private int ppmID = -1;
	
	private int revID = -1;
	
	private int conID = -1;
	
	private IMaxQuantPeptideFormat <IMaxQuantPeptide> format = new DefaultMaxQuantPeptideFormat();

	private MascotDBPattern pattern;
	
	private BufferedReader reader;
	
	private AccessionFastaAccesser accesser;
	
	private IDecoyReferenceJudger judger;
	
	private ProteinNameAccesser proNameAccesser;
	
	public MaxQuantSitePepReader(String file) throws IOException{
		
		this.reader = new BufferedReader(new FileReader(file));
		String title = reader.readLine();
		parseTitle(title);
	}
	
	public MaxQuantSitePepReader(String file, String database, String accession_regex
			) throws IOException, FastaDataBaseException{
		
		this.reader = new BufferedReader(new FileReader(file));
		this.pattern = new MascotDBPattern(accession_regex);
		this.judger = new DefaultDecoyRefJudger();
		this.accesser = new AccessionFastaAccesser(database, pattern.getPattern(), judger);
		this.proNameAccesser = new ProteinNameAccesser(1, 1, true, pattern.getPattern(), judger);
		
		String title = reader.readLine();
		parseTitle(title);
	}
	
	public MaxQuantSitePepReader(String file, String database, String accession_regex, 
			IDecoyReferenceJudger judger) throws IOException, FastaDataBaseException{
		
		this.reader = new BufferedReader(new FileReader(file));
		this.pattern = new MascotDBPattern(accession_regex);
		this.accesser = new AccessionFastaAccesser(database, pattern.getPattern(), judger);
		this.proNameAccesser = new ProteinNameAccesser(1, 1, true, pattern.getPattern(), judger);
		this.judger = judger;
		String title = reader.readLine();
		parseTitle(title);
	}
	
	public MaxQuantSitePepReader(File file, File database, String accession_regex, 
			IDecoyReferenceJudger judger) throws IOException, FastaDataBaseException{
		
		this.reader = new BufferedReader(new FileReader(file));
		this.pattern = new MascotDBPattern(accession_regex);
		this.accesser = new AccessionFastaAccesser(database, pattern.getPattern(), judger);
		this.proNameAccesser = new ProteinNameAccesser(1, 1, true, pattern.getPattern(), judger);
		this.judger = judger;
		String title = reader.readLine();
		parseTitle(title);
	}
	
	private void parseTitle(String title){
		
		String [] cs = title.split("\t");
		
		for(int i=0;i<cs.length;i++){
			
			if(cs[i].equals("Best Localization Raw File")){
				
				scannameID = i;
				
			}else if(cs[i].equals("Best Localization Scan Number")){
				
				scannumID = i;
				
			}else if(cs[i].equals("Proteins")){
				
				ProteinsID = i;
				
			}else if(cs[i].equals("PEP")){
				
				PEPID = i;
				
			}else if(cs[i].equals("Score")){
				
				ScoreID = i;
				
			}else if(cs[i].equals("Modified Sequence")){
				
				modSeqID = i;
				
			}else if(cs[i].equals("Position in peptide")){
				
				positionID = i;
				
			}else if(cs[i].equals("Charge")){
				
				chargeID = i;
				
			}else if(cs[i].equals("m/z")){
				
				mzID = i;
				
			}else if(cs[i].equals("Mass Error [ppm]")){
				
				ppmID = i;
				
			}else if(cs[i].equals("Reverse")){
				
				revID = i;
				
			}else if(cs[i].equals("Contaminant")){
				
				conID = i;
				
			}
		}
	}
	
	public IPeptide getPeptide() throws IOException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException{
		
		String line = null;
		if((line=reader.readLine())!=null){

			String [] cs = line.split("\t");
			
			if(this.conID<cs.length){
				String Contaminant = cs[this.conID];
				
				if(Contaminant.contains("+"))
					return getPeptide();
			}

			String scanname = cs[this.scannameID];
			
			int scanbeg = Integer.parseInt(cs[this.scannumID]);
			
			int scanend = scanbeg;
			
			String modseq = cs[this.modSeqID];
			
			int position = Integer.parseInt(cs[this.positionID]);
			
			String sequence = parseSequence(modseq, position);
			
			short charge = Short.parseShort(cs[this.chargeID]);
			
			double mz = Double.parseDouble(cs[this.mzID]);
			
			double mh = (mz - AminoAcidProperty.PROTON_W) * (double) charge + AminoAcidProperty.PROTON_W;

			double deltamh = Double.parseDouble(cs[this.ppmID]) * mz / 1.0E6;
			
			short rank = 1;
			
			double score = Double.parseDouble(cs[this.ScoreID]);
			
			double PEP = Double.parseDouble(cs[this.PEPID]);
			
			String [] refstr = cs[this.ProteinsID].split(";");
			
			HashSet<ProteinReference> refset = parseProteinReference(refstr);
			
			if(refset.size()==0) return getPeptide();
			
			IPeptide pep = new MaxQuantPeptide(scanname, scanbeg, scanend, sequence, charge,
					mh, deltamh, rank, score, PEP, refset, format);
			
			String uniseq = PeptideUtil.getUniqueSequence(pep.getSequence());
			
			HashMap <String, SeqLocAround> locMap = getLocAroundMap(uniseq, refset);
			
			pep.setPepLocAroundMap(locMap);
			
			return pep;
			
		}
		
		return null;
	}
	
	private String parseSequence(String modseq, int position){
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("-.");
		
		char [] cs = modseq.toCharArray();
		boolean add = false;
		int loc = 0;
		
		for(int i=0;i<cs.length;i++){
			if(cs[i]>='A' && cs[i]<='Z'){
				sb.append(cs[i]);
				loc++;
			}

			if(loc==position && !add){
				sb.append("*");
				add = true;
			}
		}
		
		sb.append(".-");
		return sb.toString();
	}
	
	private HashSet<ProteinReference> parseProteinReference(String [] refStr)
		throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException{

		HashSet <ProteinReference> refs = new HashSet<ProteinReference>();
		for(int i=0;i<refStr.length;i++){
			if(refStr[i].startsWith("CON") || refStr[i].startsWith("REV")) continue;
//			String refname = refStr[i].substring(1);
			String refname = refStr[i];
			ProteinSequence pseq = accesser.getSequence(refname);
			boolean isDecoy = judger.isDecoy(refname);

			ProteinReference pref = new ProteinReference(pseq.index(), refname, isDecoy);
			refs.add(pref);
			this.proNameAccesser.addRef(pref.getName(), pseq);
		}

		return refs;
	}
	
	private HashMap <String, SeqLocAround> getLocAroundMap(String pepseq, HashSet <ProteinReference> refs) 
		throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException{
		
		HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
		Iterator <ProteinReference> it = refs.iterator();
		while(it.hasNext()){
			
			ProteinReference pref = it.next();
			ProteinSequence pseq = accesser.getSequence(pref);
			int beg = pseq.indexOf(pepseq)+1;
			int end = beg + pepseq.length() - 1;
			int preloc = beg-8<0 ? 0 : beg-8;
			String pre = pseq.getAASequence(preloc, beg-1);
			
			String next = "";
			if(end<pseq.length()){
				int endloc = end+7>pseq.length() ? pseq.length() : end+7;
				next = pseq.getAASequence(end, endloc);
			}

			SeqLocAround sla = new SeqLocAround(beg, end, pre, next);
			locAroundMap.put(pref.toString(), sla);
		}

		return locAroundMap;
	}
	
	public void getProteinSeuqence(String fasta, String output) throws IOException{
		
		HashMap <String, ProteinSequence> psmap = new HashMap <String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while((ps=fr.nextSequence())!=null){
			String key = ps.getReference().substring(4, 15);
			psmap.put(key, ps);
		}
		fr.close();
		
		HashSet <String> set = new HashSet <String>();
		String line = null;
		while((line=reader.readLine())!=null){
			String [] ss = line.split("\t");
			if(ss[revID].equals("+") || ss[conID].equals("+")) continue;
			
			String [] refs = ss[ProteinsID].split(";");
			for(int i=0;i<refs.length;i++){
				set.add(refs[i]);
			}
		}
		
		FastaWriter writer = new FastaWriter(output);
		
		Iterator <String> it = set.iterator();
		while(it.hasNext()){
			String ref = it.next();
			if(psmap.containsKey(ref)){
				writer.write(psmap.get(ref));
			}else{
				System.out.println(ref);
			}
		}
		writer.close();
	}
	
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return proNameAccesser;
	}

	/**
	 * @param args
	 * @throws FastaDataBaseException 
	 * @throws IOException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 */
	public static void main(String[] args) throws IOException, FastaDataBaseException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {
		// TODO Auto-generated method stub

		String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta";
		String file = "H:\\NGlyan_Quan_20130812\\serum\\iden\\MaxQuant\\Deamidation (N)Sites_HCC-1.txt";
//		String reg = ">\\([^| ]*\\)";
		String reg = ">IPI:([^| .]*)";
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		
		int count = 0;
		MaxQuantSitePepReader reader = new MaxQuantSitePepReader(new File(file), new File(fasta), reg, judger);
		Pattern N_GLYCO = Pattern.compile("N[^A-Z^\\.][A-OQ-Z][^A-Z]{0,2}[ST]");

		HashSet<String> set = new HashSet<String>();
		IPeptide pep = null;
		while((pep=reader.getPeptide())!=null){
			System.out.println(pep.getMH()+"\t"+pep.getMr()+"\t"+pep.getSequence()+"\t"+pep.getPrimaryScore());
			String sequence = pep.getSequence();
			Matcher matcher = N_GLYCO.matcher(sequence);
			while (matcher.find()) {
				set.add(sequence);
			}
		}
		System.out.println(set.size());
		System.out.println(reader.getProNameAccesser().getInfosofProteins()[0].getRef());
//		reader.getProteinSeuqence(fasta, "H:\\Huang_maxquant_phos\\site3.fasta");
	}

}
