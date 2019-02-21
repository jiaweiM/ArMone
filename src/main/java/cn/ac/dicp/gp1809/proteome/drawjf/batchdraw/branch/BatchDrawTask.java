/* 
 ******************************************************************************
 * File: BatchDrawTask.java * * * Created on 05-26-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins2;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IProteinCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.BatchDrawHtmlWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * The batch draw writing task
 * 
 * @author Xinning
 * @version 0.1, 05-26-2009, 20:34:48
 */
public class BatchDrawTask implements ITask {

	private ISpectrumThreshold threshold = SpectrumThreshold.PERCENT_1_INTENSE_THRESHOLD;
	private NeutralLossInfo[] neutrallosses;
	private IBatchDrawWriter writer;
	private IPeptideListReader reader;
	private int[] types;
	private boolean useUniPep;

	private HashMap <String, Float> scoreMap;
	private HashMap <String, IMS2PeakList[]> peakMap;
	private HashMap <IPeptide, IMS2PeakList[]> pepPeakMap;
	private HashMap <String, IPeptide> pepMap;
	
	private Proteins2 proteins;
	private boolean integration = false;
	private boolean useProFilter = false;
	private ArrayList<IProteinCriteria> proFilter;

	private IPeptide curtPeptide;
	private IMS2PeakList [] curtPeakList;
	private int total;
	private float totalf;
	private int curt;
	private static NGlycoPepCriteria nnn = new NGlycoPepCriteria(true);

	public BatchDrawTask(IBatchDrawWriter writer, IPeptideListReader reader,
			int[] types, boolean useUniPep) {
		this(writer, reader, types, useUniPep, null, null);
	}

	public BatchDrawTask(IBatchDrawWriter writer, IPeptideListReader reader,
			int[] types, boolean useUniPep, NeutralLossInfo[] neutrallosses) {
		this(writer, reader, types, useUniPep, null, neutrallosses);
	}
	
	public BatchDrawTask(IBatchDrawWriter writer, IPeptideListReader reader,
			int[] types, boolean useUniPep, ISpectrumThreshold threshold, NeutralLossInfo[] neutrallosses) {
		
		this.writer = writer;
		this.reader = reader;
		this.proteins = new Proteins2(reader.getProNameAccesser());
		this.types = types;
		this.useUniPep = useUniPep;

		this.total = reader.getNumberofPeptides();
		this.totalf = total;

		if (threshold != null) {
			this.threshold = threshold;
		}

		this.peakMap = new HashMap <String, IMS2PeakList[]>();
		this.pepMap = new HashMap <String, IPeptide> ();
		this.pepPeakMap = new HashMap <IPeptide, IMS2PeakList[]>();
		this.scoreMap = new HashMap<String, Float>(total);

		this.neutrallosses = neutrallosses;
	}

	public void setUseProFilter(boolean useProFilter){
		this.useProFilter = useProFilter;
	}
	
	public void setProFilter(ArrayList<IProteinCriteria> proFilter){
		this.proFilter = proFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		return this.curt / this.totalf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		this.reader.close();
		this.writer.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		try {
//			return (curtPeptide = this.reader.getPeptide()) != null;
			
			boolean has = (curtPeptide = this.reader.getPeptide()) != null;

			if (has) {
				
				this.curtPeakList = this.reader.getPeakLists();
				this.curt = this.reader.getCurtPeptideIndex();
				
//				String seqseq = curtPeptide.getSequence();
//				if(seqseq.contains("*")){
//					seqseq = seqseq.replaceAll("[*]", "#");
//				}				
//				curtPeptide.updateSequence(seqseq);
				
				String seq = curtPeptide.getSequence();
				if(scoreMap.containsKey(seq)){
					float s0 = scoreMap.get(seq);
					float s1 = curtPeptide.getPrimaryScore();
					if(s1>s0){
						this.scoreMap.put(seq, s1);
						this.pepMap.put(seq, curtPeptide);
						this.peakMap.put(seq, curtPeakList);
					}
				}else{
					this.scoreMap.put(seq, curtPeptide.getPrimaryScore());
					this.pepMap.put(seq, curtPeptide);
					this.peakMap.put(seq, curtPeakList);
				}
				
				this.pepPeakMap.put(curtPeptide, curtPeakList);
				this.proteins.addPeptide(curtPeptide);
				
				return true;
				
			} else {
				if (!this.integration) {
					this.integration = true;
					return true;
				} else
					return false;
			}
			
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		
		try {
			
			if(useProFilter){
				
				if (this.integration) {
					
					if(useUniPep){
						
						Protein[] pros;
						try {
							pros = proteins.getProteins();
							HashSet <String> pepSet = new HashSet <String>();
L:							for(int i=0;i<pros.length;i++){
								for(int j=0;j<proFilter.size();j++){
									if(!proFilter.get(j).filter(pros[i]))
										continue L;
								}
								
								IPeptide [] peps = pros[i].getAllPeptides();
								for(int k=0;k<peps.length;k++){
									IPeptide pep = peps[k];
									String seq = pep.getSequence();
									if(pepSet.contains(seq))
										continue;

									pepSet.add(seq);
									pep = this.pepMap.get(seq);
									IMS2PeakList [] peaks = this.peakMap.get(seq);
									if (this.neutrallosses != null)
										this.writer.write(pep, peaks,
												types, threshold, this.neutrallosses);
									else
											this.writer.write(pep, peaks,
													types, threshold);
									
								}
							}		
							
						} catch (ProteinNotFoundInFastaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MoreThanOneRefFoundInFastaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FastaDataBaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
					}else{
						try {
							Protein [] pros = proteins.getProteins();
							HashSet <IPeptide> pepSet = new HashSet <IPeptide>();
L:							for(int i=0;i<pros.length;i++){
								for(int j=0;j<proFilter.size();j++){
									if(!proFilter.get(j).filter(pros[i]))
										continue L;
								}
								
								IPeptide [] peps = pros[i].getAllPeptides();
								for(int k=0;k<peps.length;k++){
									IPeptide pep = peps[k];
									if(pepSet.contains(pep))
										continue;
									
									pepSet.add(pep);
									IMS2PeakList [] peaks = this.pepPeakMap.get(peps[k]);
									if (this.neutrallosses != null)
										this.writer.write(pep, peaks,
												types, threshold, this.neutrallosses);
									else
										this.writer.write(pep, peaks,
												types, threshold);
									
								}
							}

						} catch (ProteinNotFoundInFastaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (MoreThanOneRefFoundInFastaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FastaDataBaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					
					
				}else{
					if (this.curtPeptide == null)
						throw new NullPointerException("Null peptide. No more peptide?");

				}

			}else{
				if(useUniPep){
					if (this.integration) {
						Iterator <String> it = this.pepMap.keySet().iterator();
						while(it.hasNext()){
							String seq = it.next();
							IPeptide pep = this.pepMap.get(seq);
							IMS2PeakList [] peaks = this.peakMap.get(seq);
							if (this.neutrallosses != null)
								this.writer.write(pep, peaks,
										types, threshold, this.neutrallosses);
							else
								this.writer.write(pep, peaks,
										types, threshold);
							}
					}else{
						if (this.curtPeptide == null)
							throw new NullPointerException("Null peptide. No more peptide?");

					}
				}else{
					if(!this.integration){
						if (this.neutrallosses != null){
							
//							if(nnn.filter(curtPeptide))
							this.writer.write(this.curtPeptide, this.curtPeakList,
									types, threshold, this.neutrallosses);
						}else{
							
//							if(nnn.filter(curtPeptide))
							this.writer.write(this.curtPeptide, this.curtPeakList,
									types, threshold);
						}

						this.curt = this.reader.getCurtPeptideIndex();
					}
				}			
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String [] args) throws FileDamageException, IOException{
	
		String file = "\\\\searcher5\\D\\zhujun\\glycoproteomics\\20111114-5mix-HILIC tip\\Tip-3.csv.ppl";
		IPeptideListReader reader = new PeptideListReader(file);
		ISearchParameter para = reader.getSearchParameter();
		
		AminoacidModification aam = para.getVariableInfo();
/*		
		Modif [] mfs = aam.getModifications();
		
		for(int i=0;i<mfs.length;i++){
			System.out.println(mfs[i].getSymbol()+"\t"+mfs[i].getMass());
		}
*/		
		
		String path = "\\\\searcher5\\D\\zhujun\\glycoproteomics\\20111114-5mix-HILIC tip\\Tip-3.html";
		
		IBatchDrawWriter writer = new BatchDrawHtmlWriter(path, reader
		        .getSearchParameter(), reader.getPeptideType());
		
		BatchDrawTask task = new BatchDrawTask(writer, reader, new int []{Ion.TYPE_B, Ion.TYPE_Y}, 
				false);
		
		while(task.hasNext()){
			task.processNext();
		}
		task.dispose();
	}

}
