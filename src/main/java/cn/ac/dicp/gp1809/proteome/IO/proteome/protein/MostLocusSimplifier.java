/* 
 ******************************************************************************
 * File: MostLocusSimplifier.java * * * Created on 05-19-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.protein;

import java.util.Arrays;
import java.util.Comparator;

import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DecoyDBHelper;

/**
 * To reduce the protein references in a protein to one. As there may be same
 * peptides identifying some different proteins, to use a minimum set strategy,
 * only one proteins can be considered. Which one should be used? This is the
 * same strategy as Zeng Rong paper published in proteomics.
 * 
 * <p>The human plasma proteome: Analysis of Chinese serum using shotgun strategy, 
 * Proteomics 2005, 5, 3442¨C3453
 * 
 * <p>
 * <b>In some cases, proteins other than IPI proteins will be inserted into ipi
 * database for some usage, e.g. Trypsin, if these proteins have no grouped
 * reference, then they can be considered as ipi database too.</b>
 * 
 * <p>
 * <b>A decoy protein will also be considered as an ipi protein, and the decoy
 * protein with smallest molecular weight will be retained. </b>
 * 
 * @author Xinning
 * @version 0.2, 05-05-2010, 15:04:11
 */
public class MostLocusSimplifier<T extends ISimplifyable> implements IProteinGroupSimplifier<T> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.proteome.protein.IProteinGroupSimplifier
	 * #simplify(cn.ac.dicp.gp1809.proteome.IO.proteome.IReferenceDetail[])
	 */
	@Override
	public T simplify(T[] details) {

		int len = details.length;
		if (len != 1) {
			T[] clon = details.clone();

			if (isTarget(details)) {
				Arrays.sort(clon, new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						int v1 = 0;
						int v2 = 0;
						String name1 = o1.getName();
						String name2 = o2.getName();

						if (name1.indexOf("SWISS") != -1)
							v1 += 2;
						if (name1.indexOf("REFSEQ") != -1)
							v1 += 1;

						if (name2.indexOf("SWISS") != -1)
							v2 += 2;
						if (name2.indexOf("REFSEQ") != -1)
							v2 += 1;

						if (v1 == v2) {
							int len1 = o1.getNumAminoacids();
							int len2 = o1.getNumAminoacids();

							/* 
							 * ?????????????????
							 * Sequence length from short to long
							 * Only retain proteins with minimum random
							 * probability
							 */
							//return len1 > len2 ? 1 : -1;
							
							
							
							
							/*
							 * Remain the protein with more aminoacids 
							 * (short protein may be the one chain of the long protein)
							 */
							return len1 > len2 ? -1 : 1;
						}
						return v1 > v2 ? -1 : 1;
					}
				});
			} else {
				Arrays.sort(clon, new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						/*
						 * Because this protein is a decoy protein, target
						 * reference will be sorted to the end.
						 */
						if (DecoyDBHelper.isTarget(o1.getName()))
							return 1;
						if (DecoyDBHelper.isTarget(o2.getName()))
							return 1;

						int len1 = o1.getNumAminoacids();
						int len2 = o2.getNumAminoacids();
						if (len1 > len2)
							return 1;

						return len1 < len2 ? -1 : 0;
					}
				});
			}
			return clon[0];
		} else {
			return details[0];
		}
	}

	/**
	 * If the protein group is target protein group. If there is more than one
	 * protein from decoy database, the value is false;
	 * 
	 * @param details
	 * @return
	 */
	private boolean isTarget(T[] details) {
		for (T reft : details) {
			if (!DecoyDBHelper.isTarget(reft.getName())) {
				return false;
			}
		}
		return true;
	}
}
