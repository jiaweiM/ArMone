/*
 ******************************************************************************
 * File: SimCalculator.java * * * Created on 05-07-2008
 *
 * Copyright (c) 2009 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.calculators.sim;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.SequestFileIllegalException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListWriter;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.Aminoacids;
import cn.ac.dicp.gp1809.proteome.proteometools.calculators.mw.MwCalculator;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;

/**
 * Calculator of similarity scores. The sim value is defined as described in
 * paper: 1. Z. Zhang,
 * "Prediction of low-energy collision-induced dissociation spectra of peptides"
 * . Anal. Chem. (2004), 76(14), 3908-3922. 2. Z. Zhang, "Prediction of
 * Low-Energy Collision-Induced Dissociation Spectra of Peptides with Three or
 * More Charges", Anal. Chem. (2005), 77(19), 6364-6373.
 * 
 * And the sim score is calculated by the KineticModel.dll provided by Z.Q.
 * Zhang in Amgen
 * 
 * 
 * @author Xinning
 * @version 0.1.2, 08-08-2009, 15:23:47
 */
public class SimCalculator {

	/**
	 * The type of mass spectrometer.
	 */
	public static final short LCQ = 1;
	/**
	 * The type of mass spectrometer.
	 */
	public static final short LTQ = 2;
	/**
	 * The type of mass spectrometer.
	 */
	public static final short ORBITRAP = 3;
	/**
	 * The type of mass spectrometer.
	 */
	public static final short LTQFT = 4;
	/**
	 * The type of mass spectrometer.
	 */
	public static final short QTOF = 5;

	/**
	 * The type of mass spectrometer and the performance parameters. This is the
	 * preset parameter, you can setup your own parameter using the constructor
	 * of Instrument.
	 */
	public static final Instrument INST_LCQ = new Instrument(LCQ, 35f, 0.03f,
	        50f, 2000f, 2f, 800f, 0.4f);
	/**
	 * The type of mass spectrometer and the performance parameters. This is the
	 * preset parameter, you can setup your own parameter using the constructor
	 * of Instrument.
	 */
	public static final Instrument INST_LTQ = new Instrument(LTQ, 35f, 0.03f,
	        50f, 2000f, 2f, 800f, 0.4f);
	/**
	 * The type of mass spectrometer and the performance parameters. This is the
	 * preset parameter, you can setup your own parameter using the constructor
	 * of Instrument.
	 */
	public static final Instrument INST_ORBITRAP = new Instrument(ORBITRAP,
	        35f, 0.03f, 50f, 2000f, 2f, 40000f, 0.4f);
	/**
	 * The type of mass spectrometer and the performance parameters. This is the
	 * preset parameter, you can setup your own parameter using the constructor
	 * of Instrument.
	 */
	public static final Instrument INST_LTQFT = new Instrument(LTQ, 35f, 0.03f,
	        50f, 2000f, 2f, 80000f, 0.4f);
	/**
	 * The type of mass spectrometer and the performance parameters. This is the
	 * preset parameter, you can setup your own parameter using the constructor
	 * of Instrument.
	 */
	//	public static final Instrument INST_QTOF;

	private static final DecimalFormat DF = new DecimalFormat("+#0.###;-#0.###");

	//The unmodified aminoacids
	private static Aminoacids original_aacids = Aminoacids.getInstance();

	private short instrument;
	private float startMass;
	private float endMass;
	private float collisionEnergy;
	private float reactionTime;
	private float isolationWidth;
	private float resolution;
	private float frag_tolerance;

	/**
	 * Construct a SimCalculator for the specific Instrument condition
	 * 
	 * @param instrument
	 *            parameter
	 */
	public SimCalculator(Instrument inst) {
		this.instrument = inst.getInstrument();
		this.startMass = inst.getStartMass();
		this.endMass = inst.getEndMass();
		this.collisionEnergy = inst.getCollisionEnergy();
		this.reactionTime = inst.getReactionTime();
		this.isolationWidth = inst.getIsolationWidth();
		this.resolution = inst.getResolution();
		this.frag_tolerance = inst.getFrag_tolerance();
	}

	/**
	 * Instrument information used for the prediction of theoretical methods.
	 * 
	 * @param instrumentfloat
	 * @param StartMass
	 * @param EndMass
	 * @param CollisionEnergy
	 * @param ReactionTime
	 * @param IsolationWidth
	 * @param Resolution
	 */
	public SimCalculator(short instrument, float startMass, float endMass,
	        float collisionEnergy, float reactionTime, float isolationWidth,
	        float resolution, float frag_tolerance) {
		this.instrument = instrument;
		this.startMass = startMass;
		this.endMass = endMass;
		this.collisionEnergy = collisionEnergy;
		this.reactionTime = reactionTime;
		this.isolationWidth = isolationWidth;
		this.resolution = resolution;
		this.frag_tolerance = frag_tolerance;
	}

	/**
	 * Get the Sim sequence for the peptide sequence (KineticModel sequence).
	 * Translate the normal peptide sequence into the KineticModel accepted
	 * sequence.
	 * 
	 * @see KineticModelUtil
	 * @param seq
	 * @return
	 */
	public static String getSimSequence(String seq_sequest, Aminoacids aacids,
	        AminoacidModification aamodif) {

		String seqnoterm = PeptideUtil.getSequence(seq_sequest);
		LinkedList<Modif> list = new LinkedList<Modif>();

		int l = seqnoterm.length();
		int aacount = 0;

		double termmodif = aacids.getNterminalStaticModif();
		if (Math.abs(termmodif) > 0.000001) {
			list.add(new Modif(seqnoterm.charAt(0), 1, termmodif));
		}

		StringBuilder sb = new StringBuilder(l);
		for (int i = 0; i < l; i++) {
			double addms;
			boolean modified = false;
			char c = seqnoterm.charAt(i);
			if (Aminoacids.isAminoacid(c)) {
				sb.append(c);
				aacount++;

				addms = aacids.get(c).getMonoMass()
				        - original_aacids.get(c).getMonoMass();

				if (addms > 0.000001) {//Has modifications
					modified = true;
				}
			} else {
				addms = aamodif.getAddedMassForModif(c);
				c = seqnoterm.charAt(i - 1);
				modified = true;
			}
			if (modified)
				list.add(new Modif(c, aacount, addms));
		}
		//C term static modification
		termmodif = aacids.getCterminalStaticModif();
		if (Math.abs(termmodif) > 0.000001) {
			list.add(new Modif(sb.charAt(aacount - 1), aacount, termmodif));
		}

		if (list.size() > 0) {
			for (Iterator<Modif> iterator = list.iterator(); iterator.hasNext();) {
				Modif modif = iterator.next();
				if (modif.hasInstead())
					sb.setCharAt(modif.index - 1, modif.getInstead());
				else {
					sb.append('(').append(modif.aa).append(modif.index).append(
					        DF.format(modif.mass)).append(')');
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Get the Sim value for this peptide identification from a spectrum.
	 * 
	 * @param seq_sequest
	 *            A.AAAA#AA.A
	 * @param mono_mh
	 * @param charge
	 * @param CollisionEnergy
	 * @param ReactionTime
	 * @param IsolationWidth
	 * @param Resolution
	 * @param dta
	 * @return
	 */
	public float getSim(String seq_sequest, MwCalculator mwcalor, int charge,
	        ScanDta dta) {
		return this.getSim(seq_sequest, mwcalor, charge, dta.getPeakList());
	}

	/**
	 * Get the Sim value for this peptide identification from a spectrum.
	 * 
	 * @param seq_sequest
	 *            A.AAAA#AA.A
	 * @param mono_mh
	 * @param charge
	 * @param CollisionEnergy
	 * @param ReactionTime
	 * @param IsolationWidth
	 * @param Resolution
	 * @param dta
	 * @return
	 */
	public float getSim(String seq_sequest, MwCalculator mwcalor, int charge,
	        IMS2PeakList peaklist) {

		String seq = PeptideUtil.getSequence(seq_sequest);

		String kineticSeq = getSimSequence(seq, mwcalor.getAminoacids(),
		        mwcalor.getAAModification());

		int size = peaklist.size();
		float[] mzs = new float[size];
		float[] intensities = new float[size];

		for (int i = 0; i < size; i++) {
			IPeak peak = peaklist.getPeak(i);
			mzs[i] = (float) peak.getMz();
			intensities[i] = (float) peak.getIntensity();
		}

		//		System.out.println((float)mwcalor.getMonoIsotopeMh(seq));

		float sim = KineticModelUtil.getSim(kineticSeq, 0.0f, charge,
		        instrument, startMass, endMass, collisionEnergy, reactionTime,
		        isolationWidth, resolution, mzs, intensities, size,
		        frag_tolerance);
		return sim;
	}

	private static class Modif {
		char aa;
		int index;
		double mass;
		char instead;

		public Modif(char aa, int index, double mass) {
			this.aa = aa;
			this.index = index;
			this.mass = mass;
		}

		/**
		 * Using the following character to instead the commonly used
		 * modifications. J - carboxymethylated cys (58.00548d) U -
		 * carboxyamidomethylated cys (57.02146d) O - oxidized methionine
		 * (15.99940d) s,t,y - phosphoserine, phosphothreonine and
		 * phosphotyrosine (phosphopeptides not fully trained yet) (79.96633d)
		 * 
		 * @see KineticModelUtil
		 * @return if this modif a commonly modification which can be changed.
		 */
		boolean hasInstead() {
			switch (aa) {
			case 'C': {
				double difference = Math.abs(mass - 57.02146d);
				if (difference < 0.1d) {//To precisely determine it in low mass accuracy
					instead = 'U';
					return true;
				}

				difference = Math.abs(mass - 58.00548d);
				if (difference < 0.1d) {
					instead = 'J';
					return true;
				}

				return false;
			}
			case 'M': {
				double difference = Math.abs(mass - 15.99940d);
				if (difference < 0.1d) {//To precisely determine it in low mass accuracy
					instead = 'O';
					return true;
				}

				return false;
			}
			case 'S': {
				double difference = Math.abs(mass - 79.96633d);
				if (difference < 0.1d) {//To precisely determine it in low mass accuracy
					instead = 's';
					return true;
				}

				return false;
			}
			case 'T': {
				double difference = Math.abs(mass - 79.96633d);
				if (difference < 0.1d) {//To precisely determine it in low mass accuracy
					instead = 'O';
					return true;
				}

				return false;
			}
			case 'Y': {
				double difference = Math.abs(mass - 79.96633d);
				if (difference < 0.1d) {//To precisely determine it in low mass accuracy
					instead = 'O';
					return true;
				}

				return false;
			}
			default:
				return false;
			}
		}

		/**
		 * Call hasInstead() first to determine whether the aminoacid
		 * modification has instead simple letter, then use this method to get
		 * it.
		 * 
		 * @return
		 */
		char getInstead() {
			return instead;
		}
	}

	/**
	 * The instrument parameters used for the collection of experimental spectra
	 * and the prediction of theoretical spectra
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 05-14-2008, 15:06:51
	 */
	public static class Instrument {
		private short instrument;
		private float startMass;
		private float endMass;
		private float collisionEnergy;
		private float reactionTime;
		private float isolationWidth;
		private float resolution;
		private float frag_tolerance;

		public Instrument(short instrument, float collisionEnergy,
		        float reactionTime, float startMass, float endMass,
		        float isolationWidth, float resolution, float frag_tolerance) {
			this.instrument = instrument;
			this.startMass = startMass;
			this.endMass = endMass;
			this.collisionEnergy = collisionEnergy;
			this.reactionTime = reactionTime;
			this.isolationWidth = isolationWidth;
			this.resolution = resolution;
			this.frag_tolerance = frag_tolerance;
		}

		/**
		 * @return the instrument
		 */
		public short getInstrument() {
			return instrument;
		}

		/**
		 * @return the startMass
		 */
		public float getStartMass() {
			return startMass;
		}

		/**
		 * @return the endMass
		 */
		public float getEndMass() {
			return endMass;
		}

		/**
		 * @return the collisionEnergy
		 */
		public float getCollisionEnergy() {
			return collisionEnergy;
		}

		/**
		 * @return the reactionTime
		 */
		public float getReactionTime() {
			return reactionTime;
		}

		/**
		 * @return the isolationWidth
		 */
		public float getIsolationWidth() {
			return isolationWidth;
		}

		/**
		 * @return the resolution
		 */
		public float getResolution() {
			return resolution;
		}

		/**
		 * @return the frag_tolerance
		 */
		public float getFrag_tolerance() {
			return frag_tolerance;
		}
	}

	public static void main(String[] args) throws SequestFileIllegalException,
	        ProWriterException, PeptideParsingException, FileDamageException,
	        IOException {
		/*
		 * String sequence = "HPGDFGADAQGAM*TK"; short charge = 3;
		 * 
		 * Aminoacids aacids = new Aminoacids();
		 * aacids.setCysCarboxyamidomethylation();
		 * 
		 * AminoacidModification aamodif = new AminoacidModification();
		 * aamodif.setModification('M','*', 16d);
		 * 
		 * MwCalculator mwcalor = new MwCalculator(aacids, aamodif);
		 * System.out.println(getSimSequence(sequence, aacids, aamodif));
		 * 
		 * SimCalculator simcalor = new SimCalculator(INST_LTQ); DtaReader
		 * reader = new DtaReader(new
		 * File("G:\\data\\final_7protein_yeast\\dtas\\" +
		 * "7_protein_10_071127_071127133237\\7_protein_10_071127_071127133237.2282.2282.3.dta"
		 * ));
		 * 
		 * System.out.println(simcalor.getSim(sequence, mwcalor, charge,
		 * reader.getDtaFile(true)));
		 * 
		 * return ;
		 */

		SimCalculator calor = new SimCalculator(SimCalculator.INST_LTQ);

		IPeptideListReader reader = new PeptideListReader(args[0]);

		MwCalculator mwcalor = new MwCalculator(reader.getSearchParameter()
		        .getStaticInfo(), reader.getSearchParameter().getVariableInfo());

		PeptideListWriter writer = new PeptideListWriter(args[0].substring(0,
		        args[0].length() - 3)
		        + "sim.ppl", reader.getPeptideFormat(), reader
		        .getSearchParameter(), reader.getDecoyJudger(), reader.getProNameAccesser());

		IPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			IMS2PeakList [] lists = reader.getPeakLists();
			float sim = calor.getSim(pep.getSequence(), mwcalor, pep
			        .getCharge(), lists[0]);
			pep.setSim(sim);
			System.out.println(sim);
			writer.write(pep, reader.getPeakLists());
		}
		writer.close();
		reader.close();
	}
}
