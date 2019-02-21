/* 
 ******************************************************************************
 * File: ProteinGroupSimplifierFactory.java * * * Created on 03-17-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.proteome.protein;

/**
 * Factory to create the protein reference simplifier
 * 
 * @author Xinning
 * @version 0.1, 03-17-2010, 16:01:54
 */
public class ProteinGroupSimplifierFactory {

	/**
	 * 
	 */
	private static final MostLocusSimplifier MOST_LOCUS_SIMP = new MostLocusSimplifier();

	/**
	 * Get the simplifier for specific type
	 * 
	 * @param idx
	 * @return
	 */
	public static IProteinGroupSimplifier getSimplifier(SimplifierType type) {
		switch (type) {
		case MOST_LOCUS:
			return MOST_LOCUS_SIMP;
		default:
			throw new RuntimeException(
			        "Currently only most locus simplifier can be used");
		}
	}

	/**
	 * All the supported simplifier
	 * 
	 * @return
	 */
	public static SimplifierType[] getAllSupportedTypes() {
		return new SimplifierType[] { SimplifierType.MOST_LOCUS };
	}

	/**
	 * The types of simplifier
	 * 
	 * @author Xinning
	 * @version 0.1, 03-17-2010, 16:43:24
	 */
	public static enum SimplifierType {

		MOST_LOCUS(1, "Most Locus(Only for IPI database!)");

		private int index;
		private String description;

		private SimplifierType(int index, String description) {
			this.index = index;
			this.description = description;
		}

		/**
		 * The index
		 * 
		 * @return
		 */
		public final int getIndex() {
			return this.index;
		}

		/**
		 * The description of the peptides
		 * 
		 * @return
		 */
		public final String getDescription() {
			return this.description;
		}

		@Override
		public String toString() {
			return this.description;
		}
	}

}
