/* 
 ******************************************************************************
 * File: ModifedPeptideSequence.java * * * Created on 02-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.aasequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite;
import cn.ac.dicp.gp1809.proteome.IO.PTM.PTMUtil;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;

/**
 * The peptide sequence with modifications
 * 
 * @author Xinning
 * @version 0.1.2, 09-04-2009, 22:48:41
 */
public class ModifiedPeptideSequence extends PeptideSequence implements
        IModifiedPeptideSequence {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * Parse a peptide sequence from the sequest(or other database search engin
	 * ?) outputted sequences. These sequence should with the following format:
	 * "X.XXXXXX#XX.X", "X.XXXXpXXXX." or "X.XXXXgXXXX".
	 * 
	 * @param seq_sequest
	 */
	public static ModifiedPeptideSequence parseSequence(String seq_sequest) {

		if (seq_sequest == null || seq_sequest.length() == 0)
			return null;

		int st;
		int en = seq_sequest.length() - 2;
		char prev;
		char next;
		if (seq_sequest.charAt(1) != '.') {
			st = 0;
			prev = '-';
		} else {
			st = 2;
			prev = seq_sequest.charAt(0);
		}

		if (seq_sequest.charAt(en) == '.') {
			next = seq_sequest.charAt(en + 1);
		} else {
			next = '-';

			// D.GSA@SSS.
			if (seq_sequest.charAt(en + 1) == '.')
				en++;

			// D.GSA@SSS
			else
				en += 2;
		}

		String seq = seq_sequest.substring(st, en);

		return new ModifiedPeptideSequence(seq, prev, next);
	}

	private String sequence;

	private IModifSite[] modifSites;

	/**
	 * Construct another modified peptide sequence. The information will be deep cloned
	 * 
	 * @param msequence
	 */
	public ModifiedPeptideSequence(IModifiedPeptideSequence msequence) {
		super(msequence.getUniqueSequence(), msequence.getPreviousAA(),
		        msequence.getNextAA());

		this.sequence = msequence.getSequence();
		this.modifSites = msequence.getModifications();
		
		if (this.modifSites != null) {
			this.modifSites = this.modifSites.clone();

			for (int i = 0; i < this.modifSites.length; i++) {
				this.modifSites[i] = this.modifSites[i].deepClone();
			}
		}
	}

	/**
	 * 
	 * @param seq
	 *            seq without terminal aminoacid (with modifications)
	 * @param pep_prev_aa
	 * @param pep_next_aa
	 */
	public ModifiedPeptideSequence(String seq, char pep_prev_aa,
	        char pep_next_aa) {
		super(PeptideUtil.getUniqueSequence(seq), pep_prev_aa, pep_next_aa);

		this.sequence = seq;
		this.modifSites = PTMUtil.getModifSites(seq);
	}

	@Override
	public IModifSite[] getModifications() {
		return this.modifSites;
	}

	@Override
	public IModifSite[] getTargetMod(char symbol) {
		// TODO Auto-generated method stub
		ArrayList <IModifSite> modList = new ArrayList <IModifSite>();
		for(int i=0;i<modifSites.length;i++){
			IModifSite site = modifSites[i];
			char s = site.symbol();
			if(s==symbol){
				modList.add(site);
			}
		}
		
		if(modList.size()==0)
			return null;
		else
			return modList.toArray(new IModifSite[modList.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence#
	 * getModificationAt(int)
	 */
	@Override
	public IModifSite getModificationAt(int loc) {

		if (this.modifSites == null || this.modifSites.length == 0)
			return null;

		for (IModifSite site : this.modifSites) {
			if (site.modifLocation() == loc) {
				return site;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence#getSequence()
	 */
	@Override
	public String getSequence() {
		return this.sequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.aasequence.IPeptideSequence#getFormattedSequence
	 * ()
	 */
	@Override
	public String getFormattedSequence() {
		StringBuilder sb = new StringBuilder(this.length() + 4);
		sb.append(this.getPreviousAA()).append('.').append(this.sequence)
		        .append('.').append(this.getNextAA());
		return sb.toString();
	}

	/**
	 * Set the modification sites (can be null).
	 * 
	 * @param sites
	 */
	protected void setModification(IModifSite[] sites) {
		this.modifSites = sites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence#
	 * renewModifiedSequence()
	 */
	@Override
	public void renewModifiedSequence() {

		//The new modification is null.
		if (this.modifSites == null || this.modifSites.length == 0)
			this.sequence = this.getUniqueSequence();  // no modification

		IModifSite[] msites = this.modifSites;

//		if (!justifyOrder(msites)) {
			//Don't reOrder the modifSites
//			msites = msites.clone();
			Arrays.sort(msites, new Comparator<IModifSite>() {

				@Override
				public int compare(IModifSite o1, IModifSite o2) {

					int l1 = o1.modifLocation();
					int l2 = o2.modifLocation();

					if (l1 == l2)
						throw new IllegalArgumentException(
						        "There cannot be two modification "
						                + "symbols on a single aminoacid, loc: "
						                + l1);
					return l1 > l2 ? 1 : -1;
				}

			});
//		}

		String useq = this.getUniqueSequence();
		int len = useq.length();
		StringBuilder sb = new StringBuilder(useq.length() + msites.length);

		int preloc = 0;
		for (int i = 0; i < msites.length; i++) {
			IModifSite site = msites[i];
			int loc = site.modifLocation();
			ModSite ms = site.modifiedAt();
			String dchar = ms.getModifAt();
			String echar;
			boolean nterm = false;

			if(loc==0){
				echar = "N-term";
				nterm = true;
			}else{
				if(nterm){
					echar = String.valueOf(useq.charAt(loc));
				}else{
					echar = String.valueOf(useq.charAt(loc-1));
				}
			}
			
			
//			if()
			
			if (ms == ModSite.newInstance_Not_Defined()) {
				site.setModifiedAt(ms);
			} else
			/*
			 * Not the same aminoacid. Here there is also an unsafe way by
			 * setting of the aa as (char)0 (do not defined).
			 */
			if (!(echar.endsWith(dchar))) {
				throw new IllegalArgumentException(
				        "The new modifications used to renew the sequence is illegal. "
				                + "Aminoaicd at " + loc + " on peptide is "
				                + echar + "; but the predefined aminoacid is "
				                + dchar);
			}

			for (int j = preloc; j < loc; j++) {
				sb.append(useq.charAt(j));
			}
			sb.append(site.symbol());
			preloc = loc;
		}

		//The tail
		for (; preloc < len; preloc++) {
			sb.append(useq.charAt(preloc));
		}

		this.sequence = sb.toString();
	}

	/**
	 * Justify whether the modifications are with the original order.
	 * Localization from small to big.
	 * 
	 * @param msite
	 */
	private boolean justifyOrder(IModifSite[] msite) {
		int pre = 0;
		for (IModifSite site : msite) {
			int curt = site.modifLocation();
			if (curt < pre)
				return false;

			pre = curt;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence#
	 * renewModifiedSequence(cn.ac.dicp.gp1809.proteome.IO.PTM.IModifSite[])
	 */
	@Override
	public void renewModifiedSequence(IModifSite[] newmodifs) {
		
		IModifSite[] copySites = null;
		if (newmodifs != null) {
			int len = newmodifs.length;
			copySites = new IModifSite[len];
			for (int i = 0; i < len; i++) {
				copySites[i] = newmodifs[i].deepClone();
			}
		}
		
		this.modifSites = copySites;
		this.renewModifiedSequence();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecn.ac.dicp.gp1809.proteome.aasequence.IModifiedPeptideSequence#
	 * getModificationNumber()
	 */
	@Override
	public int getModificationNumber() {
		return this.modifSites == null ? 0 : this.modifSites.length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModifiedPeptideSequence clone() {
		return (ModifiedPeptideSequence) super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.lang.IDeepCloneable#deepClone()
	 */
	@Override
	public ModifiedPeptideSequence deepClone() {
		ModifiedPeptideSequence mseq = this.clone();
		if (this.modifSites != null && this.modifSites.length > 0) {
			int len = this.modifSites.length;
			mseq.modifSites = new IModifSite[len];
			for (int i = 0; i < len; i++) {
				mseq.modifSites[i] = this.modifSites[i].deepClone();
			}
		}

		return mseq;
	}
	
	public static void main(String [] args){
		ModifiedPeptideSequence mps = 
//			new ModifiedPeptideSequence("@ASDJF*AOSG#IJHO", 'K','R');
		ModifiedPeptideSequence.parseSequence("K.@ASDJF*AOSG#IJHO.R");
		mps.renewModifiedSequence();
		System.out.println(mps.getModifications()[0]);
	}

	
}
