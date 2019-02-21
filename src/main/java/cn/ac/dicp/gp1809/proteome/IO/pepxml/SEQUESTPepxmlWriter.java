/*
 ******************************************************************************
 * File: SEQUESTPepxmlWriter.java * * * Created on 03-31-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.pepxml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.SequestParameter;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.SequestPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite.ModType;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlWriter;
import cn.ac.dicp.gp1809.util.ioUtil.xml.XmlWritingException;

/**
 * A specific Prowriter which write Peptide into a formated pepxml file.
 * 
 * @author Xinning
 * @version 0.2.2, 05-20-2010, 20:43:58
 */
public class SEQUESTPepxmlWriter extends AbstractPepxmlWriter {

	private static final Aminoacids originalaas = Aminoacids.getInstance();

	private String filename;

	//The underlying writer;
	private BufferedWriter underWriter;

	private XmlWriter xmlwriter;

	/*
	 * The index of peptides have been written.
	 */
	private int index = 0;

	private IFastaAccesser accesser;
	
	private IDecoyReferenceJudger judger;

	private int min_ref_idx;

	private int min_ref_idx_rev;

	private Aminoacids aacids;

	private AminoacidModification aamodif;

	private char[] staticModifAA;

	private SequestParameter param;
	
	private Enzyme enzyme;
	
	/**
	 * Whether the header has been written to the file
	 */
	private boolean headerWritten = false;

	public SEQUESTPepxmlWriter(String pepxml, SequestParameter param, IDecoyReferenceJudger judger)
	        throws IOException, FastaDataBaseException, XmlWritingException {
		this(new File(pepxml), param, judger);
	}

	public SEQUESTPepxmlWriter(File pepxml, SequestParameter param, IDecoyReferenceJudger judger)
	        throws IOException, FastaDataBaseException, XmlWritingException {

		underWriter = new BufferedWriter(new FileWriter(pepxml));

		this.xmlwriter = new XmlWriter(underWriter);
		this.filename = pepxml.getPath();
		this.judger = judger;

		this.param = param;
		this.enzyme = param.getEnzyme();
		this.accesser = new FastaAccesser(param.getDatabase(), judger);
		this.min_ref_idx_rev = this.accesser.getSplitRevLength();
		this.min_ref_idx = this.accesser.getSplitLength();

		this.aacids = param.getStaticInfo();
		this.aamodif = param.getVariableInfo();
		this.staticModifAA = this.aacids.getModifiedAAs();

		this.writeHeader();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPepxmlWriter#writeHeader()
	 */
	public void writeHeader() throws XmlWritingException {
		String date = DATF.format(Calendar.getInstance().getTime());
		this.xmlwriter.writeEntity("msms_pipeline_analysis");
		this.xmlwriter.writeAttribute("date", date);
		this.xmlwriter.writeAttribute("xmlns",
		        "http://regis-web.systemsbiology.net/pepXML");
		this.xmlwriter.writeAttribute("xmlns:xsi",
		        "http://www.w3.org/2001/XMLSchema-instance");
		this.xmlwriter
		        .writeAttribute(
		                "xsi:schemaLocation",
		                "http://regis-web.systemsbiology.net/pepXML "
		                        + "file:///C:/Documents and Settings/vext/pepXML/pepXML_v18.xsd");
		this.xmlwriter.writeAttribute("summary_xml", this.filename);

		this.xmlwriter.writeEntity("analysis_summary");
		this.xmlwriter.writeAttribute("analysis", "probabilityRatio");
		this.xmlwriter.writeAttribute("time", date);
		this.xmlwriter.endEntity();

		this.xmlwriter.writeEntity("msms_run_summary");
		this.xmlwriter.writeAttribute("base_name", this.filename);
		this.xmlwriter.writeAttribute("raw_data_type", "raw");
		this.xmlwriter.writeAttribute("raw_data", ".raw");

		this.xmlwriter.writeEntity("sample_enzyme");
		this.xmlwriter
		        .writeAttribute("name", param.getEnzyme().getEnzymeName());

		this.xmlwriter.writeEntity("specificity");
		this.xmlwriter.writeAttribute("cut", enzyme
		        .getCleaveSites());
		this.xmlwriter.writeAttribute("no_cut", enzyme
		        .getNotCleaveSites());
		this.xmlwriter.writeAttribute("sence", String
		        .valueOf(enzyme.getSence()));
		this.xmlwriter.endEntity();
		this.xmlwriter.endEntity();

		this.xmlwriter.writeEntity("search_summary");
		this.xmlwriter.writeAttribute("base_name", this.filename);
		this.xmlwriter.writeAttribute("search_engine", "SEQUEST");
		this.xmlwriter.writeAttribute("precursor_mass_type", param
		        .isMonoPeptideMass() ? "monoisotopic" : "average");
		this.xmlwriter.writeAttribute("fragment_mass_type", param
		        .getFragmentMassAverage() ? "average" : "monoisotopic");
		this.xmlwriter.writeAttribute("out_data_type", "out");
		this.xmlwriter.writeAttribute("out_data", ".ppls");
		this.xmlwriter.writeAttribute("search_id", "1");

		this.xmlwriter.writeEntity("search_database");
		this.xmlwriter.writeAttribute("local_path", param.getDatabase());
		this.xmlwriter.writeAttribute("type", "AA");
		this.xmlwriter.endEntity();

		this.xmlwriter.writeEntity("enzymatic_search_constraint");
		this.xmlwriter.writeAttribute("enzyme", enzyme
		        .getEnzymeName());
		this.xmlwriter.writeAttribute("max_num_internal_cleavages", String
		        .valueOf(param.getMaxMissCleaveSites()));
		this.xmlwriter.writeAttribute("min_number_termini", String
		        .valueOf(param.getMinTermNum()));
		this.xmlwriter.endEntity();

		for (char aa : this.staticModifAA) {
			double oms = 0d;
			double zms = 0d;
			if (aa == 'n') {
				zms = this.aacids.getNterminalStaticModif();
			} else if (aa == 'c') {
				zms = this.aacids.getCterminalStaticModif();
			} else {
				if (param.isMonoPeptideMass()) {
					oms = originalaas.get(aa).getAverageMass();
					zms = this.aacids.get(aa).getAverageMass();
				} else {
					oms = originalaas.get(aa).getMonoMass();
					zms = this.aacids.get(aa).getMonoMass();
				}
			}

			this.xmlwriter.writeEntity("aminoacid_modification");
			this.xmlwriter.writeAttribute("aminoacid", String.valueOf(aa));
			this.xmlwriter.writeAttribute("massdiff", DF4.format(zms - oms));
			this.xmlwriter.writeAttribute("mass", DF4.format(zms));
			this.xmlwriter.writeAttribute("variable", "N");
			this.xmlwriter.endEntity();
		}

		ModSite[] aasites = this.aamodif.getModifiedSites();

		for (ModSite aasite : aasites) {

			ModType type = aasite.getModType();

			char aa = 0;

			switch (type) {
			case modaa:
				aa = aasite.getSymbol().charAt(0);
				break;
			default:
				throw new RuntimeException(
				        "Currently, the modification can only be amino acid");
			}

			double oms = param.isMonoPeptideMass() ? this.aacids.get(aa)
			        .getAverageMass() : this.aacids.get(aa).getMonoMass();

			cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif[] modifsyms = this.aamodif
			        .getModifSymbols(aasite)
			        .toArray(
			                new cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif[0]);

			for (int i = 0, n = modifsyms.length; i < n; i++) {
				char sym = modifsyms[i].getSymbol();
				double zms = modifsyms[i].getMass();

				this.xmlwriter.writeEntity("aminoacid_modification");
				this.xmlwriter.writeAttribute("aminoacid", String.valueOf(aa));
				this.xmlwriter.writeAttribute("massdiff", DF4.format(zms));
				this.xmlwriter.writeAttribute("mass", DF4.format(zms + oms));
				this.xmlwriter.writeAttribute("variable", "Y");
				this.xmlwriter.writeAttribute("symbol", String.valueOf(sym));
				this.xmlwriter.endEntity();
			}

		}

		this.xmlwriter.writeEntity("parameter");
		this.xmlwriter.writeAttribute("name", "peptide_mass_tol");
		this.xmlwriter.writeAttribute("value", DF4.format(param
		        .getPrecursorMassTolerance()));
		this.xmlwriter.endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "fragment_ion_tol").writeAttribute("value",
		        DF4.format(param.getFragmentMassTolerance()));
		this.xmlwriter.endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "ion_series").writeAttribute("value", param.getIonseries());
		this.xmlwriter.endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "max_num_differential_AA_per_mod").writeAttribute("value",
		        String.valueOf(param.getMaxModifSitePerPeptide())).endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "nucleotide_reading_frame").writeAttribute("value",
		        String.valueOf(param.getNucleotide_reading_frame()))
		        .endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "num_output_lines").writeAttribute("value",
		        String.valueOf(param.getOutputPeptides())).endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "remove_precursor_peak").writeAttribute("value",
		        param.isRemovePrecursorPeak() ? "1" : "0").endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "ion_cutoff_percentage").writeAttribute("value",
		        DF1.format(param.getIonPercentCutoff())).endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "match_peak_count").writeAttribute("value",
		        String.valueOf(param.getMatchPeakCount())).endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "match_peak_allowed_error").writeAttribute("value",
		        String.valueOf(param.getMatchPeakAllowedError())).endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "match_peak_tolerance").writeAttribute("value",
		        DF1.format(param.getMatchTolerance())).endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "protein_mass_filter").writeAttribute(
		        "value",
		        param.isProteinMassFilterUsed() ? DF4.format(param
		                .getProteinMassFilter()[0])
		                + " " + DF4.format(param.getProteinMassFilter()[1])
		                : "0 0").endEntity();

		this.xmlwriter.writeEntity("parameter").writeAttribute("name",
		        "sequence_header_filter").writeAttribute("value",
		        param.getSequenceHeaderFilter()).endEntity();

		this.xmlwriter.endEntity();///Search summary

		this.xmlwriter.writeEntity("analysis_timestamp").writeAttribute(
		        "analysis", "probabilityRatio").writeAttribute("time",
		        DATF.format(Calendar.getInstance().getTime())).writeAttribute(
		        "id", "1").endEntity();

	}

	/**
	 * Writing a Peptide to the pepxml file.
	 * <p>
	 * ONLY for SequestPeptide. Otherwise, IllegalArgumentException will be
	 * threw.
	 * 
	 * @param IPeptide
	 *            only for SequestPeptide
	 * @param peaklist
	 *            can be null.
	 */
	private boolean writeImpl(SequestPeptide pep) {
		try {
			if (pep != null) {
					xmlwriter.writeEntity("spectrum_query");
					xmlwriter.writeAttribute("spectrum", pep
					        .getDtaOutPreference());
					xmlwriter.writeAttribute("start_scan", String.valueOf(pep
					        .getScanNumBeg()));
					xmlwriter.writeAttribute("end_scan", String.valueOf(pep
					        .getScanNumEnd()));
					xmlwriter.writeAttribute("precursor_neutral_mass", DF4
					        .format(pep.getMH() + pep.getDeltaMH() - 1d));
					xmlwriter.writeAttribute("assumed_charge", String
					        .valueOf(pep.getCharge()));
					xmlwriter.writeAttribute("index", String.valueOf(++index));

					xmlwriter.writeEntity("search_result");

					IModifiedPeptideSequence pseq = pep.getPeptideSequence();
					String ions = pep.getIons();
					int idx = ions.indexOf('/');
					Set<ProteinReference> refs = pep.getProteinReferences();
					String[] proteins = new String[refs.size()];
					int len = proteins.length;
					try {
						int i = 0;
						for (Iterator<ProteinReference> it = refs.iterator(); it
						        .hasNext(); i++) {
							proteins[i] = accesser.getSequence(it.next())
							        .getReference();
						}
					} catch (ProteinNotFoundInFastaException pe) {
						pe.printStackTrace();
					} catch (MoreThanOneRefFoundInFastaException me) {
						me.printStackTrace();
					}

					xmlwriter.writeEntity("search_hit");
					xmlwriter.writeAttribute("hit_rank", "1");
					xmlwriter.writeAttribute("peptide", pseq.getUniqueSequence());
					xmlwriter.writeAttribute("peptide_prev_aa", String
					        .valueOf(pseq.getPreviousAA()));
					xmlwriter.writeAttribute("peptide_next_aa", String
					        .valueOf(pseq.getNextAA()));

					String pref = proteins[0];
					int idex = this.getProteinSplitIdx(pref);
					xmlwriter
					        .writeAttribute("protein", pref.substring(0, idex));

					xmlwriter.writeAttribute("num_tot_proteins", String
					        .valueOf(len));
					xmlwriter.writeAttribute("num_matched_ions", ions
					        .substring(0, idx));
					xmlwriter.writeAttribute("tot_num_ions", ions
					        .substring(idx + 1));
					xmlwriter.writeAttribute("calc_neutral_pep_mass", DF4
					        .format(pep.getMH()));
					xmlwriter.writeAttribute("massdiff", DF4.format(pep
					        .getDeltaMH()));
					xmlwriter.writeAttribute("num_tol_term", String.valueOf(pep
					        .getNumberofTerm()));
					xmlwriter.writeAttribute("num_missed_cleavages", String
					        .valueOf(pep.getMissCleaveNum()));
					xmlwriter.writeAttribute("is_rejected", "0");

					String decr = pref.substring(idex).trim();
					if (decr.length() > 0)
						xmlwriter.writeAttribute("protein_descr", decr);

					if (len > 1) {
						for (int i = 1; i < len; i++) {
							String pro = proteins[i];
							idex = this.getProteinSplitIdx(pro);
							xmlwriter.writeEntity("alternative_protein");
							xmlwriter.writeAttribute("protein", pro.substring(
							        0, idex));

							decr = pro.substring(idex).trim();
							if (decr.length() > 0)
								xmlwriter.writeAttribute("protein_descr", decr);

							xmlwriter.writeAttribute("num_tol_term", String
							        .valueOf(pep.getNumberofTerm()));
							xmlwriter.endEntity();
						}
					}

					String seqnoterm = pseq.getSequence();
					LinkedList<Modif> list = new LinkedList<Modif>();

					int l = seqnoterm.length();
					int aacount = 0;
					StringBuilder sb = new StringBuilder(l + 4);
					L2: for (int i = 0; i < l; i++) {
						char c = seqnoterm.charAt(i);
						if (Aminoacids.isAminoacid(c)) {
							sb.append(c);
							aacount++;

							if (this.staticModifAA != null) {
								for (char aa : this.staticModifAA) {
									if (c == aa) {
										list.add(new Modif(aacount, this.aacids
										        .get(c).getMonoMass()));
										continue L2;
									}
								}
							}
						} else {
							char bf = seqnoterm.charAt(i - 1);
							double ms = aacids.get(bf).getMonoMass()
							        + aamodif.getAddedMassForModif(c);
							sb.append('[').append(DF0.format(ms)).append(']');
							list.add(new Modif(aacount, ms));
						}
					}

					xmlwriter.writeEntity("modification_info");
					xmlwriter.writeAttribute("modified_peptide", sb.toString());

					for (Iterator<Modif> iterator = list.iterator(); iterator
					        .hasNext();) {
						Modif modif = iterator.next();
						xmlwriter.writeEntity("mod_aminoacid_mass");
						xmlwriter.writeAttribute("position", String
						        .valueOf(modif.index));
						xmlwriter
						        .writeAttribute("mass", DF4.format(modif.mass));
						xmlwriter.endEntity();
					}
					xmlwriter.endEntity();

					xmlwriter.writeEntity("search_score");
					xmlwriter.writeAttribute("name", "xcorr");
					xmlwriter.writeAttribute("value", DF3
					        .format(pep.getXcorr()));
					xmlwriter.endEntity();
					xmlwriter.writeEntity("search_score");
					xmlwriter.writeAttribute("name", "deltacn");
					xmlwriter.writeAttribute("value", DF3.format(pep
					        .getDeltaCn()));
					xmlwriter.endEntity();
					xmlwriter.writeEntity("search_score");
					xmlwriter.writeAttribute("name", "deltacnstar");
					xmlwriter.writeAttribute("value", "0");
					xmlwriter.endEntity();
					xmlwriter.writeEntity("search_score");
					xmlwriter.writeAttribute("name", "spscore");
					xmlwriter.writeAttribute("value", DF1.format(pep.getSp()));
					xmlwriter.endEntity();
					xmlwriter.writeEntity("search_score");
					xmlwriter.writeAttribute("name", "sprank");
					xmlwriter.writeAttribute("value", String.valueOf(pep
					        .getRsp()));
					xmlwriter.endEntity();

					xmlwriter.writeEntity("analysis_result");
					xmlwriter.writeAttribute("analysis", "ArMone");

					//The probability
					
					xmlwriter.writeEntity("peptideprophet_result");
					String prob = DF4.format(pep.getProbabilty());
					xmlwriter.writeAttribute("probability", prob);
					xmlwriter.writeAttribute("all_ntt_prob", "(0.0000,0.0000,"
					        + prob + ")");
					 
					
					xmlwriter.writeEntity("search_score_summary");
					xmlwriter.writeEntity("parameter").writeAttribute("name",
					        "Neighbor_count").writeAttribute("value", "0")
					        .endEntity();
					xmlwriter.endEntity();
					xmlwriter.endEntity();
					xmlwriter.endEntity();

					xmlwriter.endEntity();//search hit
					xmlwriter.endEntity();//search result
					xmlwriter.endEntity();//spectrum query
			}
			
			return true;
			
		} catch (XmlWritingException xe) {
			throw new RuntimeException(xe);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPepxmlWriter#close()
	 */
	@Override
	public void close() throws ProWriterException {
		try {

			this.xmlwriter.endEntity();//msms_run_summary
			this.xmlwriter.endEntity();//msms_pipeline_analysis

			this.xmlwriter.close();
			this.underWriter.close();
		} catch (Exception e) {
			throw new ProWriterException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter#getSearchParameter()
	 */
	@Override
    public ISearchParameter getSearchParameter() {
	    return this.param;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean write(IPeptide peptide) {
		
		if(peptide.getPeptideType() == PeptideType.SEQUEST) {
			SequestPeptide pep  = (SequestPeptide) peptide;
			
			//write the header if necessary
			if(!this.headerWritten) {
				/*
				 * Use the enzyme from the first peptide, so all the peptide should be use the sam enzymatic
				 */
				this.enzyme = pep.getEnzyme();
				
				try {
	                this.writeHeader();
                } catch (XmlWritingException e) {
	                throw new RuntimeException("Error while writing the xml header!",e);
                }
				this.headerWritten = true;
			}
			
			return this.writeImpl(pep);
		}
		else {
			throw new IllegalArgumentException("Must be a SEQUEST peptide!");
		}
    }

	/*
	 * For the protein reference partial name and the description generation.
	 */
	private int getProteinSplitIdx(String ref) {
		int st = this.judger.isDecoy(ref) ? this.min_ref_idx_rev
		        : this.min_ref_idx;

		int idx2 = ref.indexOf(' ', st);
		if (idx2 == -1)
			idx2 = ref.length();
		return idx2;
	}

	private class Modif {
		int index;
		double mass;

		public Modif(int index, double mass) {
			this.index = index;
			this.mass = mass;
		}
	}

}
