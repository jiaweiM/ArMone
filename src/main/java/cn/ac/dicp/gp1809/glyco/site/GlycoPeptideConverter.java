/* 
 ******************************************************************************
 * File: GlycoPeptideConverter.java * * * Created on 2010-12-22
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.site;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.aasequence.ModScoreSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ModifiedPeptideSequence;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;

/**
 * @author ck
 *
 * @version 2010-12-22, 19:07:47
 */
public class GlycoPeptideConverter {

	private IPeptideFormat glycoFormat;

	private ISearchParameter param;

	private AminoacidFragment aaf;

	private char old_Glyco_Symbol;
	private char new_Glyco_Symbol;
	
	public GlycoPeptideConverter(ISearchParameter parameter,
	        PeptideType orcType, IPeptideFormat orcFormat){
		this(parameter, GlycoModConstants.GLYCO_SYMBOL, orcType, orcFormat);
	}
	
	public GlycoPeptideConverter(ISearchParameter parameter,
	        char new_GlycoSymbol, PeptideType orcType, IPeptideFormat orcFormat){
		this.new_Glyco_Symbol = new_GlycoSymbol;
		this.param = parameter.deepClone();
		
		AminoacidModification aamodif = param.getVariableInfo();
		this.old_Glyco_Symbol = aamodif.getSymbolForMassWithTolerance(
				GlycoModConstants.GLYCO_ADD, 0.1);
		
		if(old_Glyco_Symbol==0){
			throw new NullPointerException(
	        "No modification of glycosylation was set while the database searching.");
		}
		
		this.aaf = new AminoacidFragment(param.getStaticInfo(), aamodif);
		this.glycoFormat = this.getPTMScoreFormat(orcType, orcFormat);
	}

	public IPeptideFormat getGlycoPepFormat(){
		return glycoFormat;
	}
	
	public ISearchParameter getScoreParameter() {
		return this.param;
	}
	
	public boolean convert(IPeptide pep, IMS2PeakList peaklist,
	        ISpectrumThreshold threshold) {
		
		return this.convert(pep, peaklist,
		        new int[] { Ion.TYPE_B, Ion.TYPE_Y }, threshold);
	}
	
	public boolean convert(IPeptide pep, IMS2PeakList peaklist, int[] types,
	        ISpectrumThreshold threshold) {
		
		ModifiedPeptideSequence seq = new ModifiedPeptideSequence(pep.getPeptideSequence());
		IModifSite [] sites = seq.getTargetMod(old_Glyco_Symbol);
		if(sites.length>0){
			ModScoreSequence modScoreSeq;
		}else{
			
		}
		return true;
	}
	
	private IPeptideFormat getPTMScoreFormat(PeptideType type,
	        IPeptideFormat oldformat) {
		return oldformat;
	}

}
