/* 
 ******************************************************************************
 * File: AbstractPeptideFormat.java * * * Created on 09-16-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters;

import java.util.HashSet;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.IllegalFormaterException;

/**
 * Abstract peptide format
 * 
 * @author Xinning
 * @version 0.1.1, 09-22-2008, 21:24:02
 */
public abstract class AbstractPeptideFormat<Pep extends IPeptide> implements IPeptideFormat<Pep> {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	/**
	 * Number of columns. It should be noted that this value may not equal to
	 * the number of elements in the peptideIndexMap. And the other columns
	 * which have not been assigned with a name will be filled with the
	 * separator.
	 */
	protected int num_columns;

	/**
	 * Validate whether the current column has been used by other attribute, or
	 * the current column is valid [1 - n].
	 * 
	 * @param columns
	 *            the column indexes contains previously used columns
	 * @param integer
	 *            current column index
	 */
	protected void validateColumns(HashSet<Integer> columns, Integer index)
	        throws IllegalFormaterException {

		if (index < 1) {
			throw new IllegalFormaterException(
			        "The column index value should be within [1 - n].");
		}

		if (columns.contains(index))
			throw new IllegalFormaterException(
			        "One column has been assigned to different attributes.");

		int value = index.intValue();
		if (this.num_columns <= value)
			this.num_columns = value + 1;

		columns.add(index);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideFormat#getTitle()
	 */
	@Override
	public String getTitleString() {
		String[] strs = this.getTitle();

		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			if (s != null)
				sb.append(s);

			sb.append(SEPARATOR);
		}

		return sb.toString();
	}

}
