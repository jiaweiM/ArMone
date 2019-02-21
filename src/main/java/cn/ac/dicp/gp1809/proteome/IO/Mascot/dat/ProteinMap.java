/*
 ******************************************************************************
 * File: ProteinMap.java * * * Created on 05-21-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.util.HashMap;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.AccessionFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jan-2007
 * Time: 14:02:21
 */

/**
 * Modified by Xinning Jiang
 * 
 * @version 0.2, 05-21-2010, 14:42:09
 */

/**
 * This Class is a Map of all the proteins information in this MascotDatfile.
 * Mind that this Class is using the proteinSection from the raw datfile as a
 * base. Knowing that not all the ProteinHit accessions encountered in the
 * PeptideHits are located in the proteins section of the datfile!
 */
public class ProteinMap {
	private int iNumberOfProteins;
	private HashMap<String, ProteinID> iProteinMap = new HashMap<String, ProteinID>();

	/**
	 * Constructor of the ProteinMap. An instance that contains all the protein
	 * related information from a datfile.
	 * 
	 * @param aProteinSection -
	 *            HashMap Summary from the Mascot results file.
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 */
	public ProteinMap(HashMap<String, String> aProteinSection){
		if (aProteinSection != null) {
			iNumberOfProteins = aProteinSection.size();
			buildProteinMap(aProteinSection);
		}
	}

	/**
	 * Private method delegated by the Constructor. Build of this instance.
	 * 
	 * @param aProteinSection
	 *            HashMap with the proteinsection of a raw datfile.
	 */
	private void buildProteinMap(HashMap<String, String> aProteinSection) {
		iProteinMap = new HashMap<String, ProteinID>(iNumberOfProteins);
		Iterator<String> iter = aProteinSection.keySet().iterator();

		ProteinID lProteinID = null;
		while (iter.hasNext()) {
			// 1. Get the next protein accession.
			String lAccession = iter.next();
			// 2. Get the corresponding protein values.
			String lProteinValue = aProteinSection.get(lAccession);
			// ex. 1030.68,"(*CE*) ACYP1_HUMAN Acylphosphatase, organ-common
			// type isozyme (EC 3.6.1.7) (Acylphosphatephosphohydrolase)
			// (Acylphosphatase, erythrocyte isozyme)."

			double lMass = Double.parseDouble(lProteinValue.substring(0,
			        lProteinValue.indexOf(',', 0)));
			String lDescription = lProteinValue.substring((lProteinValue
			        .indexOf("\"") + 1), lProteinValue.lastIndexOf("\""));

			// Remove the enclosing quotation mark
			lAccession = DatEntryParser.trim_quotation_mark(lAccession);

			lProteinID = new ProteinID(lAccession, lMass, lDescription);
			iProteinMap.put(lAccession, lProteinID);
		}
	}

	/**
	 * Public method accessed during the QueryToPeptideMap Generation. If a
	 * PeptideHit contains a ProteinHit with an accession found in this
	 * ProteinMap, this method will be called and the corresponding ProteinID
	 * instance will be updated with a QueryNumber & PeptideHitNumber wherein
	 * that ProteinID was found.
	 * 
	 * @param aAccession
	 *            String identifier of the Protein.
	 * @param aQueryNumber
	 *            QueryNumber wherein a PeptideHit was linked to this ProteinID.
	 * @param aPeptideHitNumber
	 *            PeptideHitNumber of the QueryNumber.
	 */
	public void addProteinSource(String aAccession, int aQueryNumber,
	        int aPeptideHitNumber) {
		if (iProteinMap.get(aAccession) != null) {
			(iProteinMap.get(aAccession)).addSource(aQueryNumber,
			        aPeptideHitNumber);
		}
	}

	/**
	 * This method returns a ProteinID instance corresponding to aAccession.
	 * Mind that the ProteinMap object is using the proteinSection from the raw
	 * datfile as a base. Know that not all the ProteinHit accessions
	 * encountered in the PeptideHits are located in the proteins section of the
	 * datfile! Therefor a ProteinID with aAccesion, mass:-1.0 and "No
	 * Description" is returned if the Accession is not found in the ProteinMap.
	 * 
	 * @param aAccession
	 *            String identifier of the ProteinHit.
	 * @return ProteinID instance corresponding to param aAccession.
	 */
	public ProteinID getProteinID(String aAccession) {
		ProteinID lProteinID = null;
		if (iProteinMap.get(aAccession) == null) {
			throw new NullPointerException(
			        "Cann't find protein with accession: \"" + aAccession
			                + "\" in ProteinMap");
		} else {
			lProteinID = iProteinMap.get(aAccession);
		}
		return lProteinID;
	}

	/**
	 * This method returns a description String corresponding to aAccession.
	 * Mind that the ProteinMap object is using the proteinSection from the raw
	 * datfile as a base. Know that not all the ProteinHit accessions
	 * encountered in the PeptideHits are located in the proteins section of the
	 * datfile! Therefor a "No Description." String is returned if the Accession
	 * is not found in the ProteinMap.
	 * 
	 * @param aAccession
	 *            String identifier of the ProteinHit.
	 * @return ProteinID instance corresponding to param aAccession.
	 */
	public String getProteinDescription(String aAccession) {
		String result = null;
		if (iProteinMap.get(aAccession) != null) {
			result = iProteinMap.get(aAccession).getDescription();
		} else {
			result = "No Description.";
		}
		return result;
	}

	/**
	 * This method returns the number Protein entries there were in the Protein
	 * section of the raw datfile.
	 * 
	 * @return int Number of Proteins in the Protein section.
	 */
	public int getNumberOfProteins() {
		return iNumberOfProteins;
	}

	/**
	 * Returns an iterator of the keys (Assession) in the proteinmap.
	 * 
	 * @return Iterator instance on the keyset of the ProteinMap HashMap. Each
	 *         key can be used in the getProteinID method, this Iterator can as
	 *         such be used to iterate over all ProteinID's in the
	 *         MascotDatfile.
	 */
	public Iterator<String> getAssessionIterator() {
		return iProteinMap.keySet().iterator();
	}

	/**
	 * Returns an iterator of the values in the proteinmap.
	 * 
	 * @return Iterator instance on the valueset of the ProteinMap HashMap. This
	 *         Iterator can as such be used to iterate over all ProteinID's in
	 *         the MascotDatfile.
	 */
	public Iterator<ProteinID> getProteinIDIterator() {
		return iProteinMap.values().iterator();
	}

	/**
	 * Fill the protein reference informations into each of the ProteinID using
	 * the list of protein full names from a fasta protein database.
	 * <p>
	 * This method is used to translate the assession->description pairs to the
	 * start part of the full protein names. After calling of this method, all
	 * the ProteinID instances in the protein map will contain the
	 * ProteinReference entry and the ProteinRefence instance can be get by
	 * ProteinID.getProteinReference();
	 * <p>
	 * The translation key is the accession of each protein, therefore, please
	 * make sure the assession is unique.
	 * 
	 * @param proteinsInFasta
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 */
	public void fillProteinReferenceInfo(AccessionFastaAccesser accesser) 
		throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException{
		
		ProteinReferencePool pool = new ProteinReferencePool(accesser.getDecoyJudger());
		
		for (Iterator<ProteinID> iterator = this.getProteinIDIterator(); iterator
		        .hasNext();) {
			ProteinID protein = iterator.next();
			String ass = protein.getAccession();

			ProteinSequence pseq = accesser.getSequence(ass);
			ProteinReference ref = pool.get(pseq.index(),
			        pseq.getReference());

			protein.setProteinReference(ref);

			accesser.renewReference(ref);
		}
	}
}
