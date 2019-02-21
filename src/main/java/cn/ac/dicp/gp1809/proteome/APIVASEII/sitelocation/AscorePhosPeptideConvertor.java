/* 
 ******************************************************************************
 * File: AscorePeptideConvertor.java * * * Created on 06-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.APIVASEII.sitelocation;

import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.Inspect.peptides.AscoreInspectPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.AscoreMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.AscoreOMSSAPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation.PhosphoUtil;
import cn.ac.dicp.gp1809.proteome.IO.PTM.sitelocation.SeqvsAscore;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.AscoreXTandemPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.AscoreCruxPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.crux.peptides.ICruxPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListAccesser;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.sequest.peptides.AscoreSequestPeptideFormat;
import cn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.PhosphoPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.phosphorylation.PhosConstants;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;

/**
 * Convert a normal peptide to phosphopeptide with ascore calculated.
 * 
 * @author Xinning
 * @version 0.2.3, 09-28-2009, 21:28:32
 */
public class AscorePhosPeptideConvertor {

	private IPeptideFormat ascoreFormat;
	private ISearchParameter ms2param;
	private AminoacidFragment aaf;

	private char old_Phos_Symbol;
	private char new_Phos_Symbol;

	private char old_neu_Symbol;
	private char new_neu_Symbol;

	public AscorePhosPeptideConvertor(ISearchParameter parameter, PeptideType orcType, IPeptideFormat orcFormat) {
		this(parameter, PhosConstants.PHOS_SYMBOL, PhosConstants.NEU_SYMBOL, orcType, orcFormat);
	}

	/**
	 * For MS2 peptides
	 * 
	 * @param parameter
	 * @param new_PhosSymbol
	 * @param orcType
	 * @param orcFormat
	 */
	public AscorePhosPeptideConvertor(ISearchParameter parameter, char new_PhosSymbol, PeptideType orcType,
			IPeptideFormat orcFormat) {
		this(parameter, new_PhosSymbol, PhosConstants.NEU_SYMBOL, orcType, orcFormat);
	}

	/**
	 * For MS3 peptides
	 * 
	 * @param parameter
	 * @param new_PhosSymbol
	 * @param new_neuSymbol
	 * @param orcType
	 * @param orcFormat
	 */
	public AscorePhosPeptideConvertor(ISearchParameter parameter, char new_PhosSymbol, char new_neuSymbol,
			PeptideType orcType, IPeptideFormat orcFormat) {
		this.new_Phos_Symbol = new_PhosSymbol;
		this.new_neu_Symbol = new_neuSymbol;
		ms2param = parameter.deepClone();

		AminoacidModification aamodif = ms2param.getVariableInfo();
		old_Phos_Symbol = aamodif.getSymbolForMassWithTolerance(PhosConstants.PHOS_ADD, 0.2);

		if (old_Phos_Symbol == 0) {
			throw new NullPointerException("No modification of phosphorylation was set while the database searching.");
		}

		if (old_Phos_Symbol != new_Phos_Symbol)
			aamodif.changeModifSymbol(old_Phos_Symbol, new_Phos_Symbol);

		this.old_neu_Symbol = aamodif.getSymbolForMassWithTolerance(PhosConstants.NEU_ADD, 0.2);

		/*
		 * For MS2 peptides, this is useless
		 */
		if (this.old_neu_Symbol != 0) {
			if (this.old_neu_Symbol != new_neu_Symbol)
				aamodif.changeModifSymbol(old_neu_Symbol, new_neu_Symbol);
		}

		aaf = new AminoacidFragment(ms2param.getStaticInfo(), aamodif);

		this.ascoreFormat = this.getAscoreFormat(orcType, orcFormat);
	}

	/**
	 * The new peptide format of ascore
	 * 
	 * @return
	 */
	public IPeptideFormat getAscorePeptideFormat() {
		return this.ascoreFormat;
	}

	/**
	 * The new search parameter of Ascore
	 * 
	 * @return
	 */
	public ISearchParameter getAscoreParameter() {
		return this.ms2param;
	}

	/**
	 * The peptide will be converted to phosphopeptide with new assigned
	 * phosphorylation symbol and the AScore
	 * 
	 * @param pep
	 * @param peaklist
	 * @param threshold
	 * @return convertable: if false, this peptide contains more than acceptable
	 *         phosphorylation sites. Notice: unmodified peptide will also
	 *         return true.
	 */
	public boolean convert(IPeptide pep, IMS2PeakList peaklist, ISpectrumThreshold threshold) {
		return this.convert(pep, peaklist, new int[] { Ion.TYPE_B, Ion.TYPE_Y }, threshold);
	}

	/**
	 * The peptide will be converted to phosphopeptide with new assigned
	 * phosphorylation symbol and the AScore
	 * 
	 * @param pep
	 * @param peaklist
	 * @param threshold
	 * @return convertable: if false, this peptide contains more than acceptable
	 *         phosphorylation sites. Notice: unmodified peptide will also
	 *         return true.
	 */
	public boolean convert(IPeptide pep, IMS2PeakList peaklist, int[] types, ISpectrumThreshold threshold) {

		PhosphoPeptideSequence phosseq = new PhosphoPeptideSequence(pep.getPeptideSequence(), old_Phos_Symbol,
				this.old_neu_Symbol);

		int num = phosseq.getPhosphorylationNumber();
		int neunum = phosseq.getNeutralLostPhosphorylationNumber();

		if (num > 0) {
			IModifiedPeptideSequence sequence_no_phos = PhosphoUtil.getModifiedSequenceNoPhosModif(pep.getSequence(),
					old_Phos_Symbol, this.old_neu_Symbol);

			SeqvsAscore sva;
			if (neunum == 0)
				sva = AScorePhosCalculator.compute(sequence_no_phos, pep.getCharge(), peaklist, num, aaf, types,
						threshold, this.new_Phos_Symbol);
			else
				sva = AScorePhosCalculator.compute(sequence_no_phos, pep.getCharge(), peaklist, num, neunum, aaf, types,
						threshold, this.new_Phos_Symbol, this.new_neu_Symbol);

			if (sva == null)
				return false;

			pep.updateSequence(sva.getSequence());
			pep.setAscores(sva.getAscores());
		} else {
			pep.updateSequence(phosseq);
		}

		pep.setPeptideFormat(ascoreFormat);

		return true;
	}

	/**
	 * 
	 * 
	 * @param type
	 * @param format , only useful for crux
	 * @return
	 */
	private IPeptideFormat getAscoreFormat(PeptideType type, IPeptideFormat oldformat) {

		switch (type) {
		case SEQUEST:
			return AscoreSequestPeptideFormat.newInstance(oldformat.getIndexMap());
		case MASCOT:
			return AscoreMascotPeptideFormat.newInstance();
		case OMSSA:
			return AscoreOMSSAPeptideFormat.newInstance();
		case XTANDEM:
			return AscoreXTandemPeptideFormat.newInstance();
		case CRUX: {
			return AscoreCruxPeptideFormat.newInstance((ICruxPeptideFormat) oldformat);
		}
		case INSPECT:
			return AscoreInspectPeptideFormat.newInstance();
		default:
			throw new IllegalArgumentException("Unknown type");
		}

	}

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileDamageException
	 */
	public static void main(String[] args) throws FileDamageException, IOException {

		// FileOutputStream out = new
		// FileOutputStream("D:\\ascore_try\\ascore_stream.txt", true);
		// System.setOut(new PrintStream(out));

		String pplfile = "D:\\ascore_try\\sp.ppl";

		PeptideListAccesser accesser = new PeptideListAccesser(pplfile);

		ISearchParameter param = accesser.getSearchParameter();

		AscorePhosPeptideConvertor convert = new AscorePhosPeptideConvertor(param, accesser.getPeptideType(),
				accesser.getPeptideFormat());

		int idx = 1;

		IPeptide peptide = accesser.getPeptide(idx);
		ISpectrumThreshold threshold = new SpectrumThreshold(1, 0.5);

		convert.convert(peptide, accesser.getPeakLists(idx)[0], threshold);

		System.out.println(peptide);

		accesser.close();
	}
}
