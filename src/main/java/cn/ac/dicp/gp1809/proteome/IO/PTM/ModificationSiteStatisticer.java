/*
 ******************************************************************************
 * File: ModificationSiteStatisticer.java * * * Created on 05-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.opencsv.CSVWriter;

import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.Kinase;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.ModifSequGetter;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.BioException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.IProteinGroupSimplifier;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.MostLocusSimplifier;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;

/**
 * Statistic the modification site informations.
 * 
 * 
 * @author Xinning
 * @version 0.1.1, 06-11-2009, 21:17:19
 */
public class ModificationSiteStatisticer {

	private static final IProteinGroupSimplifier <IReferenceDetail> SIMPLIFIER = 
		new MostLocusSimplifier<IReferenceDetail>();

	private static final DecimalFormat df2 = DecimalFormats.DF0_2;
	private static final DecimalFormat df5 = DecimalFormats.DF0_5;
	private static final DecimalFormat dfPer = DecimalFormats.DF_PRECENT0_3;
	
	private File tempFile;

	private CSVWriter out;

	private boolean closed;

	private HashSet<String> seqSet;

	/*
	 * The number of peptides with different number of modification sites
	 */
	private int[] numsvssiteCount;
	private int kinasePepNum;
	private int totalPepNum;
	private int totalSitesNum;
	private int kinaseSitesNum;

	/**
	 * Site and the number of modifications on this site
	 */
	private HashMap<String, Integer> siteMap;

	private char[] symbols;

	private FastaAccesser accesser;
	
	private ProteinReferencePool pool;
	
	private IDecoyReferenceJudger judger;

	/**
	 * 
	 * @param output
	 * @param symbol
	 *            the modification of peptide which currently may be 'p' or 'g'
	 *            for phosporylation and glycolation at front of modified
	 *            aminoacid
	 * @throws FileTypeErrorException
	 * @throws IOException
	 * @throws FastaDataBaseException
	 */
	public ModificationSiteStatisticer(String database, IDecoyReferenceJudger judger, char[] symbols)
	        throws IOException, FastaDataBaseException {
		this(new FastaAccesser(database, judger), symbols);
	}

	/**
	 * 
	 * @param output
	 * @param symbol
	 *            the modification of peptide which currently may be 'p' or 'g'
	 *            for phosporylation and glycolation at front of modified
	 *            aminoacid
	 * @throws FileTypeErrorException
	 * @throws IOException
	 */
	public ModificationSiteStatisticer(FastaAccesser accesser, char[] symbols)
	        throws IOException {
		this.symbols = symbols;
		this.accesser = accesser;
		this.judger = accesser.getDecoyJudger();
		
		this.seqSet = new HashSet<String>();
		this.numsvssiteCount = new int[10];
		this.siteMap = new HashMap<String, Integer>();
		
		this.tempFile = FileUtil.getTempFile();
		this.out = new CSVWriter(new FileWriter(tempFile));

	}

	public void writePhosTitle(){
		this.out.writeNext(this.getPhosOutputTitle());
	}
	
	public void writeKinaTitle(){
		this.out.writeNext(this.getKinaOutputTitle());
	}
	
	/**
	 * The title of the output. Specified for different modifications
	 * 
	 * @return
	 */
	protected String[] getPhosOutputTitle() {
		return new String[] {"Sequence","MH+","Proteins","pI","NumofTerminals","NumofPhosphosites","SeqAround","Sites"};
	}
	
	protected String[] getKinaOutputTitle(){
		return new String[] {"KinaseInfomation","Sequence","MH+","Proteins","pI","NumofTerminals","NumofPhosphosites","SeqAround","Sites","MotifDescription"};
	}

	/**
	 * The Peptide String for statistic output, corresponding to the title
	 * Now this method is not used
	 * 
	 * @return
	 */
	protected ArrayList<String> getOutputPeptideString(IPeptide pep, Protein pro) {
		
		HashSet<ProteinReference> set = new HashSet<ProteinReference>();
		if(this.pool == null)
			this.pool = new ProteinReferencePool(this.judger);
		
		ProteinReference ref = this.pool.get(pro.getReferences()[0].getName());
		set.add(ref);
		
		pep.setProteinReference(set);

		ArrayList<String> list = new ArrayList<String>();
		list.add(pep.getSequence());
		list.add(df5.format(pep.getMH()));
		list.add(pep.getProteinReferenceString());
		list.add(df2.format(pep.getPI()));
		list.add(String.valueOf(pep.getNumberofTerm()));
		
		return list;
	}
	
	protected LinkedList <String> getOutputPeptideString(IPeptide pep, IReferenceDetail detail){
		
		String ref = detail.getName();
		LinkedList<String> list = new LinkedList <String> ();
		list.add(pep.getSequence());
		list.add(df5.format(pep.getMH()));
		list.add(ref);
		list.add(df2.format(pep.getPI()));
		list.add(String.valueOf(pep.getNumberofTerm()));
		
		return list;
	}

	/**
	 * Add a protein into the statisticer
	 * 
	 * @param pro
	 * @throws BioException
	 * @throws SequenceGenerationException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public void addPhos(Protein pro) throws BioException,
	        SequenceGenerationException, ProteinNotFoundInFastaException,
	        MoreThanOneRefFoundInFastaException {

		IReferenceDetail detail = SIMPLIFIER.simplify(pro.getReferences());
		
		ProteinSequence pseq = this.accesser.getSequence(detail.getName());

		IPeptide[] peptides = pro.getAllPeptides();
		for (IPeptide peptide : peptides) {

			IModifiedPeptideSequence mseq = peptide.getPeptideSequence();
			String s = mseq.getSequence();
			if (this.seqSet.contains(s)) {//This is a cross peptide which has been calculated in other protein
				//do nothing
			} else {

				int idx = pseq.indexOf(mseq.getUniqueSequence());

				if (idx == -1) {
					throw new NullPointerException("Peptide: \""
					        + mseq.getUniqueSequence()
					        + "\" is not a digest of protein"
					        + " with name of \"" + pseq.getReference() + "\".");
				}

				/*
				 * List containing raw phosphorylation site information. (Site
				 * numbered by the protein sequence). From 1-n.
				 */
				LinkedList<IModifSite> sitelist = null;

				IModifSite[] sites = mseq.getModifications();
				if (sites != null && sites.length != 0) {

					/*
					 * List containing raw phosphorylation site information.
					 * (Site numbered by the protein sequence). From 1-n.
					 */
					sitelist = new LinkedList<IModifSite>();
					for (IModifSite site : sites) {
						char sym = site.symbol();
						for (char symbol : this.symbols) {
							if (sym == symbol) {
								int pidx = idx + site.modifLocation();
								IModifSite clone = site.deepClone();
								clone.setModifLocation(pidx);
								sitelist.add(clone);
							}
						}
					}
				}

				if (sitelist != null && sitelist.size() > 0) {//This is  a modified peptide

					LinkedList <String> pepinfo = this.getOutputPeptideString(peptide, detail);
					
					pepinfo.add(String.valueOf(sitelist.size()));
					
					Iterator<IModifSite> iterator = sitelist.iterator();
					while (iterator.hasNext()) {
						IModifSite site = iterator.next();
						String rawseq = pseq
						        .getSeqAround(site.modifLocation() - 1);
						String annot = new StringBuilder().append(
						        site.modifiedAt().getModifAt()).append(site.modifLocation())
						        .toString();

						pepinfo.add(rawseq);
						pepinfo.add(annot);

						String aa = site.modifiedAt().getModifAt();
						Integer count = this.siteMap.get(aa);
						if (count != null) {
							this.siteMap.put(aa, ++count);
						} else {
							this.siteMap.put(aa, 1);
						}
					}
					
					this.out.writeNext(pepinfo.toArray(new String[pepinfo.size()]));
					
					this.numsvssiteCount[sitelist.size()]++;
				}

				this.seqSet.add(s);
			}
		}
	}
	
	public void addKinase(Protein protein, Kinase kinase) throws ProteinNotFoundInFastaException, 
		MoreThanOneRefFoundInFastaException, IOException, SequenceGenerationException{
	
		IReferenceDetail detail = SIMPLIFIER.simplify(protein.getReferences());
		ProteinSequence pseq = this.accesser.getSequence(detail.getName());
	
		HashSet <String> patSet = kinase.getMotifSet();
		HashMap <Integer, String> sitesMap = new HashMap <Integer, String> ();
	
		for(String pattern:patSet){
			ModifSequGetter getter = new ModifSequGetter(pattern);
			Integer [] sitesList = getter.getModifList(pseq, pattern);
		
			for(Integer integer: sitesList){
				sitesMap.put(integer.intValue(),pattern);
			}
		}

		IPeptide [] peps = protein.getAllPeptides();
	
		for (IPeptide peptide : peps){
			IModifiedPeptideSequence mseq = peptide.getPeptideSequence();
			String s = mseq.getSequence();
			if (this.seqSet.contains(s)) {//This is a cross peptide which has been calculated in other protein
				//do nothing
			} else {
			
				int idx = pseq.indexOf(mseq.getUniqueSequence());
				
				if (idx == -1) {
					throw new NullPointerException("Peptide: \""
				        + mseq.getUniqueSequence()
				        + "\" is not a digest of protein"
				        + " with name of \"" + pseq.getReference() + "\".");
				}
				
				LinkedList<IModifSite> sitelist = null;

				IModifSite[] sites = mseq.getModifications();
				if (sites != null && sites.length != 0) {

					/*
					 * List containing raw phosphorylation site information.
					 * (Site numbered by the protein sequence). From 1-n.
					 */
					sitelist = new LinkedList<IModifSite>();
					for (IModifSite site : sites) {
						char sym = site.symbol();
						for (char symbol : this.symbols) {
							if (sym == symbol) {
								int pidx = idx + site.modifLocation();
								IModifSite clone = site.deepClone();
								clone.setModifLocation(pidx);
								sitelist.add(clone);
							}
						}
					}
				}
				
				if (sitelist != null && sitelist.size() > 0) {//This is  a modified peptide

					LinkedList <String> pepinfo = this.getOutputPeptideString(peptide, detail);
					
					pepinfo.add(String.valueOf(sitelist.size()));
					
					Iterator<IModifSite> iterator = sitelist.iterator();
					String str = "";
					int it = 0;
					while (iterator.hasNext()) {
						it++;
						totalSitesNum++;
						IModifSite site = iterator.next();
						String rawseq = pseq
						        .getSeqAround(site.modifLocation() - 1);
						String annot = new StringBuilder().append(
						        site.modifiedAt().getModifAt()).append(site.modifLocation())
						        .toString();

						pepinfo.add(rawseq);
						pepinfo.add(annot);
						
						if(sitesMap.containsKey(site.modifLocation())){
							pepinfo.addLast(kinase.getDescription()+","+sitesMap.get(site.modifLocation()));
							kinaseSitesNum++;
							str += Integer.toString(it);
							str += ";";
						}else{
							pepinfo.addLast("null");
						}

						String aa = site.modifiedAt().getModifAt();
						Integer count = this.siteMap.get(aa);
						if (count != null) {
							this.siteMap.put(aa, ++count);
						} else {
							this.siteMap.put(aa, 1);
						}
					}
					
					if(str.length()>0){
						pepinfo.addFirst(str);
						kinasePepNum++;
					}else{
						pepinfo.addFirst("");
					}

					this.out.writeNext(pepinfo.toArray(new String[pepinfo.size()]));
					this.numsvssiteCount[sitelist.size()]++;
				}
				totalPepNum++;
				this.seqSet.add(s);
			}
		}
	}

	public void addKinase2(Protein protein, Kinase kinase) throws ProteinNotFoundInFastaException, 
		MoreThanOneRefFoundInFastaException, IOException, SequenceGenerationException{

		IReferenceDetail detail = SIMPLIFIER.simplify(protein.getReferences());
		ProteinSequence pseq = this.accesser.getSequence(detail.getName());
		String proSeq = pseq.getUniqueSequence();

		HashSet <String> patSet = kinase.getMotifSet();
//		HashSet <String> patSet = new HashSet <String>();
//		patSet.add("[K/R]X[pS/pT]");
//		patSet.add("[K/R]XX[pS/pT]");
		HashMap <Integer, String> sitesMap = new HashMap <Integer, String> ();

		for(String pattern:patSet){
			ModifSequGetter getter = new ModifSequGetter(pattern);
			Integer [] sitesList = getter.getModifList(pseq, pattern);
	
			for(Integer integer: sitesList){
				sitesMap.put(integer.intValue(),pattern);
			}
		}

		IPeptide [] peps = protein.getAllPeptides();

		for (IPeptide peptide : peps){
			IModifiedPeptideSequence mseq = peptide.getPeptideSequence();
			String s = mseq.getSequence();
			if (this.seqSet.contains(s)) {//This is a cross peptide which has been calculated in other protein
				//do nothing
			} else {
			
				int idx = pseq.indexOf(mseq.getUniqueSequence());
				int end = idx + mseq.getUniqueSequence().length();
				
				if (idx == -1) {
					throw new NullPointerException("Peptide: \""
				        + mseq.getUniqueSequence()
				        + "\" is not a digest of protein"
				        + " with name of \"" + pseq.getReference() + "\".");
				}
				
				LinkedList<IModifSite> sitelist = new LinkedList<IModifSite>();
				Iterator <Integer> iti = sitesMap.keySet().iterator();
				while(iti.hasNext()){
					int loc = iti.next();
					if(loc>idx && loc<=end){
						IModifSite modsite = new ModifSite(ModSite.newInstance_aa(proSeq.charAt(loc-1)), loc, '\u0000');
						sitelist.add(modsite);
					}
				}

				if (sitelist != null && sitelist.size() > 0) {//This is  a modified peptide

					LinkedList <String> pepinfo = this.getOutputPeptideString(peptide, detail);
					
					pepinfo.add(String.valueOf(sitelist.size()));
					
					Iterator<IModifSite> iterator = sitelist.iterator();
					String str = "";
					int it = 0;
					while (iterator.hasNext()) {
						it++;
						totalSitesNum++;
						IModifSite site = iterator.next();
						String rawseq = pseq
						        .getSeqAround(site.modifLocation() - 1);
						String annot = new StringBuilder().append(
						        site.modifiedAt().getModifAt()).append(site.modifLocation())
						        .toString();

						pepinfo.add(rawseq);
						pepinfo.add(annot);
						
						if(sitesMap.containsKey(site.modifLocation())){
							pepinfo.addLast(kinase.getDescription()+","+sitesMap.get(site.modifLocation()));
							kinaseSitesNum++;
							str += Integer.toString(it);
							str += ";";
						}else{
							pepinfo.addLast("null");
						}

						String aa = site.modifiedAt().getModifAt();
						Integer count = this.siteMap.get(aa);
						if (count != null) {
							this.siteMap.put(aa, ++count);
						} else {
							this.siteMap.put(aa, 1);
						}
					}
					
					if(str.length()>0){
						pepinfo.addFirst(str);
						kinasePepNum++;
					}else{
						pepinfo.addFirst("");
					}

					this.out.writeNext(pepinfo.toArray(new String[pepinfo.size()]));
					this.numsvssiteCount[sitelist.size()]++;
				}
				totalPepNum++;
				this.seqSet.add(s);
			}
		}
	}
	
	public String getKinasePepProp(){
		double d1 = kinasePepNum;
		double d2 = totalPepNum;
		String result = dfPer.format(d1/d2);
		return result;
	}
	
	public String getKinaseSitesProp(){
		double d1 = kinaseSitesNum;
		double d2 = totalSitesNum;
		String result = dfPer.format(d1/d2);
		return result;
	}
	
	public void outputKinaStat(){
		this.out.writeNext(new String []{"",""});
		this.out.writeNext(new String []{"","Total Peptide","Kinase Phosphorylation Peptide","Proportion"});
		this.out.writeNext(new String []{"Peptide Number",Integer.toString(totalPepNum),Integer.toString(kinasePepNum),this.getKinasePepProp()});
		this.out.writeNext(new String []{"Phosphorylation Sites Number",Integer.toString(totalSitesNum),Integer.toString(kinaseSitesNum),this.getKinaseSitesProp()});
	}
	
	/**
	 * When finished the statistic, this method <b>must</b> be called to clear
	 * the cache for writing.
	 * @throws IOException 
	 */
	public void finish() throws IOException {
		this.out.close();
		this.closed = true;
	}

	/**
	 * The site map with key of modified aminoacid and the value of number of
	 * modification count.
	 * 
	 * @return
	 */
	public HashMap<String, Integer> getSiteMap() {
		if (!this.closed) {
			throw new RuntimeException("The statiscer must be closed frist.");
		}

		return this.siteMap;
	}

	/**
	 * Number of distinct peptide vs the modification site count. The number of
	 * peptides with specific modification count can be accessed by the index of
	 * the array. For example, the int[1] is the number of singly modified
	 * peptides
	 * 
	 * @return
	 */
	public int[] getNumDistinctPepsVsSiteCount() {
		return this.numsvssiteCount;
	}

	/**
	 * Print the detail information to the output
	 * 
	 * @param output
	 * @throws IOException
	 */
	public void printDetails(String output) throws IOException {
		if (!this.closed) {
			throw new RuntimeException("The statiscer must be closed first.");
		}

		FileUtil.copy(this.tempFile, new File(output));
	}

	/**
	 * The inner fasta accesser
	 * 
	 * @return
	 */
	public FastaAccesser getFastaAccesser() {
		return this.accesser;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
