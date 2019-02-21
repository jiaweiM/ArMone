package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.util.ArrayList;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jan-2007
 * Time: 15:58:31
 */
/**
 * Modified by Xinning Jiang
 * @version 0.1, 11-13-2008, 16:50:46
 */

/**
 * This Class represents one entry in the ProteinSection of a raw datfile along
 * with the sources where peptides of this Protein were found.
 */
public class ProteinID {
	/**
	 * Protein mass.
	 */
	private double iMass;
	/**
	 * Protein accession.
	 */
	private String iAccession;
	/**
	 * Protein description.
	 */
	private String iDescription;

	/**
	 * The ProteinReference, this will be null before the setting of this
	 * instance using method {@link #setProteinReference(ProteinReference)}
	 */
	private ProteinReference proref;
	/**
	 * Vector with int[] describing the protein sources. </br> Example: <i></br>
	 * If PeptideHit "KENNYHELSER" was found in the second Hit of Query 567 and
	 * the first hit of Query 568; then this Vector's will contain two int[],
	 * wherein [0] is the QueryNumber and [1] is the HitNumber</br>
	 * Vector.get(0)=[567][2]</br> Vector.get(1)=[568][1]</i>
	 */
	private ArrayList<int[]> iSources = null;

	public ProteinID(String aAccession, double aMass, String aDescription) {
		iMass = aMass;
		iAccession = aAccession;
		iDescription = aDescription;
	}

	/**
	 * This method adds a source to the Vector with int[] describing the protein
	 * sources. </br> Example: <i></br> If PeptideHit "KENNYHELSER" was found
	 * in the second Hit of Query 567 and the first hit of Query 568; then this
	 * Vector's will contain two int[], wherein [0] is the QueryNumber and [1]
	 * is the HitNumber</br> Vector.get(0)=[567][2]</br>
	 * Vector.get(1)=[568][1]</i>
	 * 
	 * @param aQueryNumber
	 *            Querynumber wherein the ProteinHit was found.
	 * @param aPeptidehitNumber
	 *            PeptideHit number in the Query.
	 */
	public void addSource(int aQueryNumber, int aPeptidehitNumber) {
		if (iSources == null) {
			iSources = new ArrayList<int[]>();
		}
		iSources.add(new int[] { aQueryNumber, aPeptidehitNumber });
	}

	/**
	 * Getter for property 'accession'.
	 * 
	 * @return Value for property 'accession'.
	 */
	public String getAccession() {
		return iAccession;
	}

	/**
	 * Getter for property 'description'.
	 * 
	 * @return Value for property 'description'.
	 */
	public String getDescription() {
		return iDescription;
	}

	/**
	 * Set the description for this protein
	 * 
	 * @param description
	 * @return
	 */
	public void setDescription(String description) {
		this.iDescription = description;
	}

	/**
	 * Set the description for this protein
	 * 
	 * @param description
	 * @return
	 */
	public void setProteinReference(ProteinReference ref) {
		this.proref = ref;
	}

	/**
	 * the protein reference, make sure this instance has been set using method
	 * {@link #setProteinReference(ProteinReference)}
	 * 
	 * @return
	 */
	public ProteinReference getProteinReference() {
		return this.proref;
	}

	/**
	 * Getter for property 'mass'.
	 * 
	 * @return Value for property 'mass'.
	 */
	public double getMass() {
		return iMass;
	}

	/**
	 * Getter for property 'queryNumbers'.
	 * 
	 * @return Value for property 'queryNumbers'.
	 */
	public int[] getQueryNumbers() {
		int[] lQueryNumbers = new int[iSources.size()];
		Iterator<int[]> lIter = iSources.iterator();
		int lCount = 0;
		while (lIter.hasNext()) {
			lQueryNumbers[lCount] = lIter.next()[0];
			lCount++;
		}
		return lQueryNumbers;
	}

	/**
	 * Getter for property 'queryNumbersAndPeptideHits'.
	 * 
	 * @return Value for property 'queryNumbersAndPeptideHits'.
	 */
	public int[][] getQueryNumbersAndPeptideHits() {
		int[][] lQueryNumbersAndPeptideHits = new int[iSources.size()][2];
		Iterator<int[]> lEnumeration = iSources.iterator();
		int lCount = 0;
		while (lEnumeration.hasNext()) {
			// 1) Querynumber in first dimension, PeptideHit in second
			// dimension.
			lQueryNumbersAndPeptideHits[lCount] = lQueryNumbersAndPeptideHits[lCount] = lEnumeration
			        .next();
			lCount++;
		}
		return lQueryNumbersAndPeptideHits;
	}

	@Override
	public String toString() {
		return "ProteinID{" + "iMass=" + iMass + ", iAccession='" + iAccession
		        + '\'' + ", iDescription='" + iDescription + '\''
		        + ", iSources=" + iSources + ", ProteinReference="+proref+'}';
	}
}
