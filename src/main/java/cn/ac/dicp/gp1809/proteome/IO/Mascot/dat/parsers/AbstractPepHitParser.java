/* 
 ******************************************************************************
 * File: AbstractPepHitParser.java * * * Created on 11-12-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers;

import java.util.ArrayList;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.PeptideHit;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.ProteinHit;
import cn.ac.dicp.gp1809.util.StringUtil;

/**
 * Abstract peptide hit parser contains useful utilities
 * 
 * @author Xinning
 * @version 0.1, 11-12-2008, 09:58:22
 */
public abstract class AbstractPepHitParser implements IPepHitParser {

	/**
	 * Parse the peptide hit information into peptide hit
	 * 
	 * @param pepstr
	 * @return
	 */
	protected PeptideHit parsePeptide(String pepstr) {

		// Protein accession may contains character ';'
		int idx = pepstr.indexOf(';');

		if (idx == -1) {
			throw new IllegalArgumentException("Cann't find the splitter '" + idx + "' to split the peptide and protein string.");
		}

		String[] peps = StringUtil.split(pepstr.substring(0, idx), ',');

		if (peps.length != 11) {
			throw new IllegalArgumentException("Wrong String with input data for peptide informations  " + "(found \""
					+ peps.length + "\" tokens instead of expected 11).\")");
		}

		PeptideHit pephit = new PeptideHit();

		// OK, now we know we have 11 tokens, read them all.
		pephit.setMissedCleavages(Integer.parseInt(peps[0]));
		pephit.setPeptideMr(Double.parseDouble(peps[1]));
		pephit.setDeltaMass(Double.parseDouble(peps[2]));
		pephit.setNumberOfIonsMatched(Integer.parseInt(peps[3]));
		pephit.setSequence(peps[4]);
		pephit.setPeaksUsedFromIons1(Integer.parseInt(peps[5]));
		pephit.setVariableModificationsArray(peps[6]);
		pephit.setIonsScore(Float.parseFloat(peps[7]));
		pephit.setIonSeries(peps[8]);
		pephit.setPeaksUsedFromIons2(Integer.parseInt(peps[9]));
		pephit.setPeaksUsedFromIons3(Integer.parseInt(peps[10]));

		pephit.setProteinHits(this.parseProteins(pepstr.substring(idx + 1)));

		return pephit;
	}

	/**
	 * Parse the protein hit
	 * 
	 * @param pros
	 * @return
	 */
	private ArrayList<ProteinHit> parseProteins(String prostr) {

		String[] pros = StringUtil.split(prostr, ',');
		ArrayList<ProteinHit> list = new ArrayList<ProteinHit>(pros.length);

		for (String pro : pros) {
			ProteinHit prohit = new ProteinHit();

			// 1. Extract the accession in between quotes.
			int lQuote1 = pro.indexOf('"');
			int lQuote2 = pro.indexOf("\":", lQuote1 + 1);

			if (lQuote1 < 0 || lQuote2 < 0) {
				throw new IllegalArgumentException(
						"ProteinHit accession not found. The protein " + "instance could not be created.");
			}

			prohit.setAccession(pro.substring(lQuote1 + 1, lQuote2));

			String[] strs = StringUtil.split(pro.substring(lQuote2 + 2), ':');

			if (strs.length != 4) {
				throw new IllegalArgumentException("Wrong String with input data for protein hit informations  "
						+ "(found \"" + strs.length + "\" tokens instead of expected 5).\")");
			}

			// 3.OK! now there are 4 tokens, read them out.
			// 3.a) FrameNumber.
			prohit.setFrameNumber(Integer.parseInt(strs[0]));
			// 3.b) Start
			prohit.setStart(Integer.parseInt(strs[1]));
			// 3.c) Stop
			prohit.setStop(Integer.parseInt(strs[2]));
			// 3.d) Multiplicity
			prohit.setMultiplicity(Integer.parseInt(strs[3]));

			list.add(prohit);
		}

		return list;
	}
}
