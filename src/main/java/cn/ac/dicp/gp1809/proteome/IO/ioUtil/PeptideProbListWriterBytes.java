/* 
 ******************************************************************************
 * File: PeptideProbListWriter.java * * * Created on 01-03-2009
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
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * The peptide probability list writer
 * 
 * @author Xinning
 * @version 0.1.2, 05-20-2010, 16:16:30
 */
public class PeptideProbListWriterBytes extends AbstractPeptideListWriter {

	private static final DecimalFormat DF = DecimalFormats.DF0_3;

	/**
	 * This map contains the main information of this peptidelist. The key in
	 * this map is scancharge while the value is xcorr score.
	 */
	private Map<String, ProbLineInfor> keyMap;

	/**
	 * Set contains changed lines that should be removed
	 */
	private HashSet<Integer> removedLines;

	/**
	 * Line number of the line which changed the probability.
	 * 
	 * Currently this may be useless as the peptide identified with the maximum
	 * probability is retained.
	 * 
	 */
	private HashMap<Integer, MyMap> changedProbLines;

	private static final int SIZE = 2048;

	/**
	 * The extension of the ppls file
	 */
//	private static final String EXTENSION = ".ppls";

	private boolean isUniquePep;

	/*
	 * The min probability filter.
	 */
	private float minProb = 0f;

	public PeptideProbListWriterBytes(File output, IPeptideFormat<?> formatter,
	        ISearchParameter parameter, IDecoyReferenceJudger judger, boolean isUniquePep, 
	        ProteinNameAccesser proNameAccesser)
	        throws FileNotFoundException {
		this(output, formatter, parameter, judger,isUniquePep, 0f, proNameAccesser);
	}

	public PeptideProbListWriterBytes(String output, IPeptideFormat<?> formatter,
	        ISearchParameter parameter, IDecoyReferenceJudger judger, boolean isUniquePep, 
	        ProteinNameAccesser proNameAccesser)
	        throws FileNotFoundException {
		this(output, formatter, parameter, judger,isUniquePep, 0f, proNameAccesser);
	}

	public PeptideProbListWriterBytes(File output, IPeptideFormat<?> formatter,
	        ISearchParameter parameter, IDecoyReferenceJudger judger, boolean isUniquePep, 
	        float minProb, ProteinNameAccesser proNameAccesser)
	        throws FileNotFoundException {
		super(output, formatter, parameter, judger,new PeptideProbListHeader(
		        formatter.type(), isUniquePep, minProb), proNameAccesser);

		this.isUniquePep = isUniquePep;
		this.minProb = minProb;

		this.keyMap = new HashMap<String, ProbLineInfor>(SIZE);
		this.removedLines = new HashSet<Integer>(SIZE);
		this.changedProbLines = new HashMap<Integer, MyMap>(20);
	}

	public PeptideProbListWriterBytes(String output, IPeptideFormat<?> formatter,
	        ISearchParameter parameter, IDecoyReferenceJudger judger, boolean isUniquePep, 
	        float minProb, ProteinNameAccesser proNameAccesser)
	        throws FileNotFoundException {
		this(new File(output), formatter, parameter, judger,isUniquePep, minProb, proNameAccesser);
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
		return this.judgePrintMostProbRetain(peptide);
	}

	/**
	 * Judge whether or not a peptide should be printed into the ppl file.
	 * 
	 * <p>
	 * Only retain the peptide with most probability for each spectrum of
	 * different charge state. That is, for example, if more than one peptides
	 * are assigned to a spectrum, peptide with most probability (if any) will
	 * be retained.
	 * <p>
	 * In addition, the method
	 * {@link #refeshPeptide4FinalWritten(IPeptide, int)} will be invoked to
	 * downward the peptide identification probability
	 * 
	 * @param pep
	 */
	protected final boolean judgePrintMostProbRetain(IPeptide peptide) {
		int idx = this.getWrittenPepNum();
		float curtvalue = peptide.getProbabilty();

		/*
		 * Don't output the peptide with probability less than the cut off value
		 */
		float diff = curtvalue - this.minProb;

		if (diff < -0.00001f)
			return false;

		boolean print = true;
		
		
		/*
		 * !!!!!!!!!!!!!!Important:
		 * 
		 * Here, we assume that the input peptides are the "unique_scan_charge"
		 * peptides. Because the ppls should be created from the ppl file after
		 * the calculation of probability.
		 * 
		 * Just set the topn as 1 while the reading of peptides
		 */
		if(this.isUniquePep) {
			/*
			 * Retain the top matched (most probability) peptide for each charge
			 * state of each spectrum
			 */
			String key = peptide.getScanNum();
			ProbLineInfor temp = null;


			if ((temp = this.keyMap.get(key)) != null) {
				float value = temp.getProb();
//				String seq_no_term = peptide.getPeptideSequence().getSequence();
				Integer old_idx = temp.getPepIdx();

				// Keep the probability for each sequence in the map is the
				// maximum probability
				MyMap probmap = this.changedProbLines.get(old_idx);
//				Integer line_idx;
				
				if (value >= curtvalue) {
					print = false;
//					line_idx = old_idx;
					
					if (probmap == null) {
						probmap = new MyMap();
						this.changedProbLines.put(old_idx, probmap);
						probmap.put(temp.getSequence(), value);
					}
				} else {
					// Only one line retained in the peptide list file
					this.removedLines.add(old_idx);
//					line_idx = idx;
					
					if (probmap == null) {
						probmap = new MyMap();
						this.changedProbLines.put(idx, probmap);
						probmap.put(temp.getSequence(), value);
					} else {
						this.changedProbLines.remove(old_idx);
						this.changedProbLines.put(idx, probmap);
					}
				}

				/*
				 * Make sure the only the maximum probability for a peptide
				 * sequence is retained
				 */
				/*
				Float prob = probmap.get(seq_no_term);
				if (prob == null || prob < curtvalue)
					probmap.put(seq_no_term, curtvalue);
					*/

			}
			
			if (print)
				this.keyMap.put(key, new ProbLineInfor(idx, peptide));
		}
		



		return print;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter#close()
	 */
	@Override
	public void close() throws ProWriterException {
		super.close();

		if (this.isUniquePep) {
			this.removeRedundantCharge(this.getOutFile());
		}
	}

	/**
	 * In loss mass accuracy mass spectrometer, 2+ and 3+ will be assinged to a
	 * single spectrum if the charge state is ambiguous. Use this method to
	 * remove the redundant peptide identifications from duplicated charge state
	 * by retaining the peptide with highest probability.
	 * 
	 * 
	 */
	private void removeRedundantCharge(File pplfile) {
//		System.out.println("Do nothing remove.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter#
	 * isNeadRefresh4FinalWritten(int)
	 */
	@Override
	protected boolean isNeadRefresh4FinalWritten(int pep_idx) {
		boolean need = this.changedProbLines.get(pep_idx) != null;
		// The probability need to be refreshed.
		return need;
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
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.AbstractPeptideListWriter#
	 * refeshPeptide4FinalWritten
	 * (cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide, int)
	 */
	@Override
	protected IPeptide refeshPeptide4FinalWritten(IPeptide peptide, int line_idx) {
		MyMap map = this.changedProbLines.get(line_idx);
		if (map != null) {
			float orpb = peptide.getProbabilty();
			float newpb = this.reNewProbability(orpb, map);
			peptide.setProbability(newpb);
		}
		return peptide;
	}

	/**
	 * There may be more than one peptide identifications for different
	 * algorithms. Therefore, the probability need to be renewed before the
	 * determination of which peptide should be retained as the the final
	 * peptide identification.
	 * 
	 * @param prob
	 * @param probs_alg
	 * @return
	 */
	private float reNewProbability(float prob, MyMap map) {
		float newprob = prob * map.getFacotr();
		return newprob;
	}

	/**
	 * My map contains values to refresh the peptide probability. See Analytical
	 * Chemistry, Keller, PeptideProphet
	 * 
	 * @author Xinning
	 * @version 0.1, 01-06-2009, 09:31:08
	 */
	protected final static class MyMap extends HashMap<String, Float> {

		// A lazy field
		private float factor = -1f;

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		public MyMap() {
			super();
		}

		public MyMap(int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
		}

		public MyMap(int initialCapacity) {
			super(initialCapacity);
		}

		/**
		 * The factor to downward the probability
		 * 
		 * @return
		 */
		public float getFacotr() {
			if (factor < 0) {
				this.factor = getFacotrImp(this);
			}

			return factor;
		}

		/*
		 * The downward factor calculation
		 */
		private static float getFacotrImp(HashMap<String, Float> map) {
			Iterator<Float> vit = map.values().iterator();
			int size = map.size();
			float values[] = new float[size];
			int m = 0;
			while (vit.hasNext()) {
				values[m++] = vit.next().floatValue();
			}

			float denominator = 0;
			float numerator = 0;
			for (int i = 0; i < size; i++) {
				float ni = values[i];
				// There will be at least two values
				for (int j = i + 1; j < size; j++) {
					float nj = values[j];
					denominator += ni + nj;
					numerator += ni + nj - ni * nj;
				}
			}

			return denominator == 0 ? 0 : numerator / denominator;
		}
	}

	protected static class ProbLineInfor extends LineInfor {

		private float prob;

		protected ProbLineInfor(int line, IPeptide peptide) {
			super(line, peptide);
			this.prob = peptide.getProbabilty();
		}

		/**
		 * The probability
		 * 
		 * @return
		 */
		protected float getProb() {
			return prob;
		}
	}

	public static class PeptideProbListHeader extends DefaultPeptideListHeader {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		private boolean isUnique;

		private double min_prob;

		public PeptideProbListHeader(PeptideType type, boolean isUnique,
		        double min_prob) {
			super(type);

			this.isUnique = isUnique;
			this.min_prob = min_prob;
		}

		public boolean isUnique() {
			return isUnique;
		}

		public double getMin_prob() {
			return min_prob;
		}

		@Override
		public String getDescription() {
			return super.getDescription() + lineSeparator + "IsUnique: "
			        + this.isUnique + ", Min_pro: " + DF.format(this.min_prob);
		}

		@Override
		public String toString() {
			return super.toString() + "\tisUnique: " + this.isUnique
			        + "\tMin_Prob: " + DF.format(this.min_prob);
		}

		/**
		 * Parse the header from a string
		 * 
		 * @param header
		 * @return
		 */
		public static IPeptideListHeader parseHeader(String headers)
		        throws IllegalArgumentException {
			String[] cols = StringUtil.split(headers, '\t');
			if (cols.length < 4) {
				throw new IllegalArgumentException("Header parsing error: \""
				        + headers + "\".");
			}

			try {
				Date date = datetimeformat.parse(cols[0]);
				PeptideType type = PeptideType.typeOfFormat(cols[1]);
				boolean isUnique = Boolean.parseBoolean(cols[2].substring(
				        cols[2].indexOf(':') + 1).trim());
				double min_prob = Double.parseDouble(cols[3].substring(cols[3]
				        .indexOf(':') + 1));

				PeptideProbListHeader header = new PeptideProbListHeader(type,
				        isUnique, min_prob);
				header.current = date;
				return header;
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}

		}
	}

	/**
	 * Temporary used for evaluation of the probability calculation model
	 * 
	 * @param peptide
	 * @param count
	 *            an integer number for count of nearest neighbor
	 */
	protected void write(IPeptide peptide, int count) {
		if (this.judgePrint(peptide)) {
			super.write(peptide + "\t" + count);
		}
	}

	/**
	 * Temporary used for evaluation of the probability calculation model
	 * 
	 * @param peptide
	 * @param count
	 *            an integer number for count of nearest neighbor
	 */
	public void write(IPeptide peptide, String s) {
		if (this.judgePrint(peptide)) {
			super.write(peptide + "\t" + s);
		}
	}

}
