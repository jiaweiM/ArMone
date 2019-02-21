/*
 ******************************************************************************
 * File: Matcher.java * * * Created on 05-30-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import java.util.logging.Logger;

import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * A utility class used for the matching of peaks from experiment and
 * theoretical ions.
 * 
 * @author Xinning
 * @version 0.3.3, 06-08-2010, 20:02:43
 */
public class SpectrumMatcher {

	private static Logger logger = Logger.getLogger(SpectrumMatcher.class.getName());
	
	/**
	 * The default threshold for match.
	 * 
	 * @see SpectrumThreshold.PERCENT_1_INTENSE_THRESHOLD
	 */
	public static final ISpectrumThreshold DEFAULT_THRESHOLD = SpectrumThreshold.PERCENT_1_INTENSE_THRESHOLD;

	/**
	 * Match the experimental peaks to the theoretical ions and return the
	 * PeakForMatch instances using default threshold. The number of returned
	 * PeakForMatch will equal to the number of peaks in the original peak list.
	 * In other words, the PeakForMatch is the experimental peak with match
	 * information.
	 * <p>
	 * Peak and ion within the tolerance and with intensity higher than the
	 * threshold will be considered as match.
	 * <p>
	 * For ions from peptide with charge state of 1 or 2, only single charged
	 * fragment ions will be matched to the spectrum. For 3+ peptides, 1+ and 2+
	 * fragment ions will be matched to the peaks.
	 * 
	 * @param peaklist
	 *            list of peaks from spectrum;
	 * @param ions
	 *            theoretical ions
	 * @param charge
	 *            the charge state of precursor ion.
	 * @param threshold
	 *            the intensity and tolerance threshold.
	 * @return PeakForMatch[] peaks containing match informations;
	 */
	public static PeakForMatch[] matchBY(IPeakList peaklist, Ions ions,
	        short charge) {
		return matchBY(peaklist, ions, charge, null);
	}

	/**
	 * Match the experimental peaks to the theoretical ions and return the
	 * PeakForMatch instances. The number of returned PeakForMatch will equal to
	 * the number of peaks in the original peak list. In other words, the
	 * PeakForMatch is the experimental peak with match information.
	 * <p>
	 * Peak and ion within the tolerance and with intensity higher than the
	 * threshold will be considered as match.
	 * <p>
	 * For ions from peptide with charge state of 1 or 2, only single charged
	 * fragment ions will be matched to the spectrum. For 3+ peptides, 1+ and 2+
	 * fragment ions will be matched to the peaks.
	 * 
	 * @param peaklist
	 *            list of peaks from spectrum;
	 * @param ions
	 *            theoretical ions
	 * @param charge
	 *            the charge state of precursor ion.
	 * @param threshold
	 *            the intensity and tolerance threshold.
	 * @return PeakForMatch[] peaks containing match informations;
	 */
	public static PeakForMatch[] matchBY(IPeakList  peaklist, Ions ions,
	        short charge, ISpectrumThreshold threshold) {
		return match(peaklist, ions, charge, threshold, new int[] { Ion.TYPE_B,
		        Ion.TYPE_Y });
	}

	/**
	 * Match the ions for these type of ions
	 * 
	 * @param peaklist
	 * @param ions
	 * @param charge
	 * @param threshold
	 * @param types
	 * @return
	 */
	public static PeakForMatch[] match(IPeakList  peaklist, Ions ions,
	        short charge, ISpectrumThreshold threshold, int[] types) {

		if (threshold == null)
			threshold = DEFAULT_THRESHOLD;

		int size = peaklist.size();
		PeakForMatch[] peaks = new PeakForMatch[size];
		double mx = peaklist.getBasePeak().getIntensity();
		for (int i = 0; i < size; i++) {
			peaks[i] = new PeakForMatch(peaklist.getPeak(i), mx);
		}
		
		if(ions!=null && ions.getTypes()!=null) {
			for (int type : types) {
				Ion[] fragment = ions.getIons(type);
				
				if(fragment == null || fragment.length==0) {
					logger.warning("No theorotical ions with type of: "+type);
				}
				else {
					match(peaks, fragment, charge, threshold, type);
				}
			}
		}
		else {
			logger.warning("No theorotical ions to match the peak list.");
		}
		
		return peaks;

	}

	/**
	 * Match the ions for these type of ions and the neutral loss
	 * 
	 * @param peaklist
	 * @param ions
	 * @param charge
	 * @param threshold
	 * @param types
	 * @return
	 */
	public static PeakForMatch[] match(IMS2PeakList peaklist, double precursor_mz, Ions ions,
	        NeutralLossInfo[] losses, short charge,
	        ISpectrumThreshold threshold, int[] types) {

		if (threshold == null)
			threshold = DEFAULT_THRESHOLD;
		
		PeakForMatch[] peaks = match(peaklist, ions, charge, threshold, types);

		if (losses != null && losses.length > 0) {
//			double mz = peaklist.getPrecursePeak().getMz();
			for (int i = 0; i < losses.length; i++) {
				NeutralLossInfo loss = losses[i];
				double tpmz = precursor_mz - loss.getLoss() / charge;
				NeutralLossIon ion = new NeutralLossIon(tpmz, charge, loss
				        .getCaption());

				for (PeakForMatch peak : peaks) {
					peak.match(Ion.TYPE_NEU, ion, charge, threshold);
				}
			}
		}

		return peaks;

	}

	/**
	 * Match the ions for all the ions in the instance of Ions and the neutral
	 * loss
	 * 
	 * @param peaklist
	 * @param ions
	 * @param charge
	 * @param threshold
	 * @param types
	 * @return
	 */
	public static PeakForMatch[] match(IMS2PeakList peaklist, double precursor_mz, Ions ions,
	        NeutralLossInfo[] losses, short charge, ISpectrumThreshold threshold) {
		return match(peaklist, precursor_mz, ions, losses, charge, threshold, ions.getTypes());
	}

	/**
	 * Match the ions for all the ions in the instance of Ions and the neutral
	 * loss
	 * 
	 * @param peaklist
	 * @param ions
	 * @param charge
	 * @param threshold
	 * @param types
	 * @return
	 */
	public static PeakForMatch[] match(IPeakList peaklist, Ions ions,
	        short charge, ISpectrumThreshold threshold) {
		return match(peaklist, ions, charge, threshold, ions.getTypes());
	}

	/**
	 * The peak
	 * 
	 * @param peaks
	 * @param fragment
	 * @param charge
	 * @param threshold
	 * @param type
	 */
	private static void match(PeakForMatch[] peaks, Ion[] fragment,
	        short charge, ISpectrumThreshold threshold, int type) {
		for (int i = 0, n = fragment.length; i < n; i++) {
			Ion ion = fragment[i];
			//charge states
			for (short j = 1; j <= charge; j++) {
				for (PeakForMatch temp : peaks) {
					temp.match(type, ion, j, threshold);
				}
			}
		}
	}

	/**
	 * 
	 * Match the experimental peaks to the theoretical ions and return the
	 * PeakForMatch instances. The number of returned PeakForMatch will equal to
	 * the number of peaks in the original peak list. In other words, the
	 * PeakForMatch is the experimental peak with match information.
	 * <p>
	 * The loss can be any mass, and mass will be added to the precursor mz and
	 * match to the peaks. Commonly, the loss is the mass difference with the
	 * precursor mz value. e.g. mz-H2O mz-H3PO4, mz-HPO3 and so on.
	 * <p>
	 * Peak and ion within the tolerance and with intensity higher than the
	 * threshold will be considered as match.
	 * <p>
	 * For ions from peptide with charge state of 1 or 2, only single charged
	 * fragment ions will be matched to the spectrum. For 3+ peptides, 1+ and 2+
	 * fragment ions will be matched to the peaks.
	 * 
	 * @param peaklist
	 *            list of peaks from spectrum;
	 * @param ions
	 *            theoretical ions
	 * @param charge
	 *            the charge state of precursor ion.
	 * @param losses
	 *            The neutral loss informations
	 * @return PeakForMatch[] peaks containing match informations;
	 */
	public static PeakForMatch[] matchBYNeu(IMS2PeakList peaklist, double precursor_mz, Ions ions,
	        short charge, NeutralLossInfo[] losses) {
		return matchBYNeu(peaklist, precursor_mz, ions, charge, losses, null);
	}

	/**
	 * 
	 * Match the experimental peaks to the theoretical ions and return the
	 * PeakForMatch instances. The number of returned PeakForMatch will equal to
	 * the number of peaks in the original peak list. In other words, the
	 * PeakForMatch is the experimental peak with match information.
	 * <p>
	 * The loss can be any mass, and mass will be added to the precursor mz and
	 * match to the peaks. Commonly, the loss is the mass difference with the
	 * precursor mz value. e.g. mz-H2O mz-H3PO4, mz-HPO3 and so on.
	 * <p>
	 * Peak and ion within the tolerance and with intensity higher than the
	 * threshold will be considered as match.
	 * <p>
	 * For ions from peptide with charge state of 1 or 2, only single charged
	 * fragment ions will be matched to the spectrum. For 3+ peptides, 1+ and 2+
	 * fragment ions will be matched to the peaks.
	 * 
	 * @param peaklist
	 *            list of peaks from spectrum;
	 * @param ions
	 *            theoretical ions
	 * @param charge
	 *            the charge state of precursor ion.
	 * @param loss
	 *            [] the mass of neutral losses. e.g. the mass of the neutral
	 *            loss of phosphate is 80Da if null, @see matchBY(); The meaning
	 *            which are indicated by these masses are weaken one by one.
	 *            e.g. if one peak has matched to the neutral loss from this
	 *            neutral loss list, it won't match to another even though it
	 *            may be. So the strongest meaning loss must be put at first.
	 * @param losstring
	 *            [] the strings of the losses indicated. These string is
	 *            complemetry for losses. e.g. a loss of 80 Da indicated a
	 *            phosphoric acid, thus the loss string is [MH-H3PO4]. 18 Da is
	 *            [MH-H2O] and so on;
	 * @param threshold
	 *            the intensity and tolerance threshold.
	 * @return PeakForMatch[] peaks containing match informations;
	 */
	public static PeakForMatch[] matchBYNeu(IMS2PeakList peaklist, double precursor_mz, Ions ions,
	        short charge, NeutralLossInfo[] losses, ISpectrumThreshold threshold) {

		if (threshold == null)
			threshold = DEFAULT_THRESHOLD;

		PeakForMatch[] peaks = matchBY(peaklist, ions, charge, threshold);
		if (losses != null && losses.length > 0) {
//			double mz = peaklist.getPrecursePeak().getMz();
			for (int i = 0; i < losses.length; i++) {
				NeutralLossInfo loss = losses[i];
				double tpmz = precursor_mz - loss.getLoss() / charge;
				NeutralLossIon ion = new NeutralLossIon(tpmz, charge, loss
				        .getCaption());

				for (PeakForMatch peak : peaks) {
					peak.match(Ion.TYPE_NEU, ion, charge, threshold);
				}
			}
		}
		return peaks;
	}

	/**
	 * 
	 * Match the experimental peaks to the theoretical ions and return the
	 * PeakForMatch instances using the default threshold. The number of
	 * returned PeakForMatch will equal to the number of peaks in the original
	 * peak list. In other words, the PeakForMatch is the experimental peak with
	 * match information.
	 * <p>
	 * The loss can be any mass, and mass will be added to the precursor mz and
	 * match to the peaks. Commonly, the loss is the mass difference with the
	 * precursor mz value. e.g. mz-H2O mz-H3PO4, mz-HPO3 and so on.
	 * <p>
	 * Peak and ion within the tolerance and with intensity higher than the
	 * threshold will be considered as match.
	 * <p>
	 * For ions from peptide with charge state of 1 or 2, only single charged
	 * fragment ions will be matched to the spectrum. For 3+ peptides, 1+ and 2+
	 * fragment ions will be matched to the peaks.
	 * 
	 * @param peaklist
	 *            list of peaks from spectrum;
	 * @param ions
	 *            theoretical ions
	 * @param charge
	 *            the charge state of precursor ion.
	 * @param loss
	 *            [] the mass of neutral losses. e.g. the mass of the neutral
	 *            loss of phosphate is 80Da if null, @see matchBY(); The meaning
	 *            which are indicated by these masses are weaken one by one.
	 *            e.g. if one peak has matched to the neutral loss from this
	 *            neutral loss list, it won't match to another even though it
	 *            may be. So the strongest meaning loss must be put at first.
	 * @param losstring
	 *            [] the strings of the losses indicated. These string is
	 *            complemetry for losses. e.g. a loss of 80 Da indicated a
	 *            phosphoric acid, thus the loss string is [MH-H3PO4]. 18 Da is
	 *            [MH-H2O] and so on;
	 * @param threshold
	 *            the intensity and tolerance threshold.
	 * @return PeakForMatch[] peaks containing match informations;
	 */
	@Deprecated
	public static PeakForMatch[] matchBYNeu(IMS2PeakList peaklist, Ions ions,
	        short charge, double[] loss, String[] lossstring) {
		return matchBYNeu(peaklist, ions, charge, loss, lossstring, null);
	}

	/**
	 * 
	 * Match the experimental peaks to the theoretical ions and return the
	 * PeakForMatch instances. The number of returned PeakForMatch will equal to
	 * the number of peaks in the original peak list. In other words, the
	 * PeakForMatch is the experimental peak with match information.
	 * <p>
	 * The loss can be any mass, and mass will be added to the precursor mz and
	 * match to the peaks. Commonly, the loss is the mass difference with the
	 * precursor mz value. e.g. mz-H2O mz-H3PO4, mz-HPO3 and so on.
	 * <p>
	 * Peak and ion within the tolerance and with intensity higher than the
	 * threshold will be considered as match.
	 * <p>
	 * For ions from peptide with charge state of 1 or 2, only single charged
	 * fragment ions will be matched to the spectrum. For 3+ peptides, 1+ and 2+
	 * fragment ions will be matched to the peaks.
	 * 
	 * @param peaklist
	 *            list of peaks from spectrum;
	 * @param ions
	 *            theoretical ions
	 * @param charge
	 *            the charge state of precursor ion.
	 * @param loss
	 *            [] the mass of neutral losses. e.g. the mass of the neutral
	 *            loss of phosphate is 80Da if null, @see matchBY(); The meaning
	 *            which are indicated by these masses are weaken one by one.
	 *            e.g. if one peak has matched to the neutral loss from this
	 *            neutral loss list, it won't match to another even though it
	 *            may be. So the strongest meaning loss must be put at first.
	 * @param losstring
	 *            [] the strings of the losses indicated. These string is
	 *            complemetry for losses. e.g. a loss of 80 Da indicated a
	 *            phosphoric acid, thus the loss string is [MH-H3PO4]. 18 Da is
	 *            [MH-H2O] and so on;
	 * @param threshold
	 *            the intensity and tolerance threshold.
	 * @return PeakForMatch[] peaks containing match informations;
	 */
	@Deprecated
	public static PeakForMatch[] matchBYNeu(IMS2PeakList peaklist, Ions ions,
	        short charge, double[] loss, String[] lossstring,
	        ISpectrumThreshold threshold) {

		if (threshold == null)
			threshold = DEFAULT_THRESHOLD;

		PeakForMatch[] peaks = matchBY(peaklist, ions, charge);
		if (loss != null) {
			double mz = peaklist.getPrecursePeak().getMz();
			for (int i = 0; i < loss.length; i++) {
				double tpmz = mz - loss[i] / charge;
				NeutralLossIon ion = new NeutralLossIon(tpmz, charge,
				        lossstring[i]);
				for (PeakForMatch peak : peaks) {
					peak.match(Ion.TYPE_NEU, ion, charge, threshold);
				}
			}
		}
		return peaks;
	}
}
