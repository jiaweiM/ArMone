/* 
 ******************************************************************************
 * File: PeptideListWriter1.java * * * Created on 01-03-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.util.ioUtil.SimpleFilenameChecker;

/**
 * The ppl (peptide list file) writer
 * 
 * @author Xinning
 * @version 0.2.2, 05-20-2010, 16:01:18
 */
public class PeptideListWriter extends AbstractPeptideListWriter {
	
	/**
	 * The extension of the peptide list file
	 */
	private static final String EXTENSION = "ppl";

	/**
	 * This map contains the main information of this peptidelist. The key in
	 * this map is scancharge while the value is xcorr score.
	 */
	protected Map<String, LineInfor> keyMap;

	/**
	 * Set contains changed lines that should be removed
	 */
	protected HashSet<Integer> removedLines;

	/**
	 * <line number of the line which changed proteins, StringBuilder which
	 * contains protein reference to be added>
	 * 
	 * <p>
	 * for xml or excel output, peptide with more than one protein reference
	 * often be duplicated to more than one times. when merged to a single non
	 * duplicated line, this protein reference must be added to the end of the
	 * original protein. A group of proteins can be formed;
	 */
	protected Map<Integer, Set<ProteinReference>> changedProteinLines;

	private HashMap<Integer, HashMap<String, SeqLocAround>> changedProMap;

	private static final int SIZE = 2048;

	// only the top matched peptide for each spectrum with different charge
	// state is reported.
	private boolean uniq_charge = false;

	/**
	 * 
	 * @param output
	 * @param formatter
	 * @param parameter
	 * @throws FileNotFoundException
	 */
	public PeptideListWriter(File output, IPeptideFormat<?> formatter, ISearchParameter parameter,
			IDecoyReferenceJudger judger, ProteinNameAccesser proNameAccesser) throws FileNotFoundException {
		this(output, formatter, parameter, judger, false, proNameAccesser);
	}

	public PeptideListWriter(String output, IPeptideFormat<?> formatter, ISearchParameter parameter,
			IDecoyReferenceJudger judger, ProteinNameAccesser proNameAccesser) throws FileNotFoundException {
		this(output, formatter, parameter, judger, false, proNameAccesser);
	}

	/**
	 * 
	 * @param output
	 * @param formatter
	 * @param parameter
	 * @param uniq_charge only retain the top matched peptide for each scan of
	 *            different charge state. The top match is determined by the
	 *            getPrimaryScore for each of the peptide
	 * @throws FileNotFoundException
	 */
	public PeptideListWriter(File output, IPeptideFormat<?> formatter, ISearchParameter parameter,
			IDecoyReferenceJudger judger, boolean uniq_charge, ProteinNameAccesser proNameAccesser)
			throws FileNotFoundException {
		super(new File(SimpleFilenameChecker.check(output.getAbsolutePath(), EXTENSION)), formatter, parameter, judger,
				new DefaultPeptideListHeader(formatter.type()), proNameAccesser);

		this.uniq_charge = uniq_charge;

		this.keyMap = new HashMap<String, LineInfor>(SIZE);
		this.removedLines = new HashSet<Integer>(SIZE);
		this.changedProteinLines = new HashMap<Integer, Set<ProteinReference>>(20);
		this.changedProMap = new HashMap<Integer, HashMap<String, SeqLocAround>>(20);
	}

	/**
	 * 
	 * @param output
	 * @param formatter
	 * @param parameter
	 * @param uniq_charge only retain the top matched peptide for each scan of
	 *            different charge state. The top match is determined by the
	 *            getPrimaryScore for each of the peptide
	 * @throws FileNotFoundException
	 */
	public PeptideListWriter(String output, IPeptideFormat<?> formatter, ISearchParameter parameter,
			IDecoyReferenceJudger judger, boolean uniq_charge, ProteinNameAccesser proNameAccesser)
			throws FileNotFoundException {

		this(new File(output), formatter, parameter, judger, uniq_charge, proNameAccesser);
	}

	/**
	 * The key which represents the peptide id. That is, if two peptide with the
	 * same id, then only one of these two peptide will be retained. The details
	 * of which one will be retained can be defined in the write(Peptide
	 * peptide) method.
	 * 
	 * <p>
	 * currently, if <code>uniq_chage</code> key: scannumber-charge else key:
	 * scannumber-charge-sequence (for bioworks output or omssa csv output, to
	 * remove the duplicate sequence from different proteins)
	 * 
	 * @param peptide
	 * @return
	 */
	protected String getKey(IPeptide peptide) {
		if (this.uniq_charge)
			return peptide.getScanNum() + "-" + peptide.getCharge();
		else
			return new StringBuilder(peptide.getScanNum()).append("-").append(peptide.getCharge()).append('-')
					.append(PeptideUtil.getSequence(peptide.getSequence())).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter#judgePrint
	 * (cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	protected boolean judgePrint(IPeptide peptide) {
		String key = this.getKey(peptide);

		LineInfor temp = null;
		float curtvalue = peptide.getPrimaryScore();

		boolean print = true;

		if ((temp = this.keyMap.get(key)) != null) {
			// Only top matched peptide is retained.
			if (this.uniq_charge) {

				float value = temp.getPrimaryScore();

				/*
				 * Retain the peptide with biggest primary score, the previously
				 * printed one should be removed
				 */
				if (curtvalue > value) {
					// print = true;

					this.removedLines.add(temp.getPepIdx());
				}

				/*
				 * In this condition, this two peptide was the same peptide with
				 * different protein. In other word, this peptide can due to
				 * more than one protein identifications.
				 * 
				 * <p> Different sequence indicated that this two sequence can
				 * not be distinguished, Currently, only the first printed one
				 * is retained
				 */
				else if (value == curtvalue) {

					if (PeptideUtil.getSequence(peptide.getSequence()).equals(temp.getSequence())) {
						/*
						 * This may be occurred from the reading of excel and
						 * xml or omssa csv file files. Peptide in this type of
						 * file commonly grouped into protein first. So if a
						 * peptide has more than one protein identifications,
						 * when the next time this peptide is identified,
						 * another protein reference should be added at the end
						 * of protein reference.
						 */
						Set<ProteinReference> set;
						HashMap<String, SeqLocAround> map;
						Integer line = temp.getPepIdx();
						if ((set = this.changedProteinLines.get(line)) == null) {
							set = new HashSet<ProteinReference>();
							map = new HashMap<String, SeqLocAround>();
							this.changedProteinLines.put(line, set);
							this.changedProMap.put(line, map);
						} else {
							map = this.changedProMap.get(line);
						}

						map.putAll(peptide.getPepLocAroundMap());
						set.addAll(peptide.getProteinReferences());

						print = false;
					}

					/*
					 * An ambiguous peptide identification which comes from
					 * unclassified amino acid mass e.g. I & L.
					 */
					else {

						// !!!!!!!!!!!!!!

						/*
						 * temporary, only retain the first one
						 */
						print = false;
					}
				}

				else {// currentvalue < value
					print = false;
				}
			} else {// process the duplicated sequences from different proteins
				/*
				 * This may be occurred from the reading of excel and xml or
				 * omssa csv file files. Peptide in this type of file commonly
				 * grouped into protein first. So if a peptide has more than one
				 * protein identifications, when the next time this peptide is
				 * identified, another protein reference should be added at the
				 * end of protein reference.
				 * 
				 * 2. use dta2mgf.mgf for OMSSA database search will result in
				 * duplicated peptide identifications.
				 */
				Set<ProteinReference> set;
				HashMap<String, SeqLocAround> map;

				Integer line = temp.getPepIdx();
				if ((set = this.changedProteinLines.get(line)) == null) {
					set = new HashSet<ProteinReference>();
					map = new HashMap<String, SeqLocAround>();
					this.changedProteinLines.put(line, set);
					this.changedProMap.put(line, map);
				} else {
					map = this.changedProMap.get(line);
				}

				set.addAll(peptide.getProteinReferences());
				map.putAll(peptide.getPepLocAroundMap());

				print = false;
			}

		}

		if (print)
			this.keyMap.put(key, new LineInfor(this.getWrittenPepNum(), peptide));

		return print;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter#
	 * isNeadRefresh4FinalWritten(int)
	 */
	@Override
	protected boolean isNeadRefresh4FinalWritten(int pep_idx) {
		// The protein reference need to be refreshed
		return this.changedProteinLines.get(pep_idx) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter#
	 * isRemovePep4FinalWritten(int)
	 */
	@Override
	protected boolean isRemovePep4FinalWritten(int pep_idx) {
		return this.removedLines.contains(pep_idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.
	 * AbstractPeptideListWriter#refeshPeptide4FinalWritten
	 * (cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide, int)
	 */
	@Override
	protected IPeptide refeshPeptide4FinalWritten(IPeptide peptide, int line_idx) {
		Set<ProteinReference> origin = peptide.getProteinReferences();
		Set<ProteinReference> added = this.changedProteinLines.get(line_idx);
		origin.addAll(added);

		HashMap<String, SeqLocAround> originmap = peptide.getPepLocAroundMap();
		HashMap<String, SeqLocAround> addmap = this.changedProMap.get(line_idx);
		originmap.putAll(addmap);

		return peptide;
	}

	/**
	 * The details write to the end of the
	 * 
	 * @author Xinning
	 * @version 0.3, 05-04-2009, 15:20:27
	 */
	protected static class ListDetails extends AbstractPeptideListWriter.ListDetails {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param param
		 * @param peptideIndexes
		 */
		private ListDetails(ISearchParameter param, IPeptideFormat<?> formatter, IPeptideListIndex[] indexes) {
			super(param, formatter, indexes);
		}
	}
}
