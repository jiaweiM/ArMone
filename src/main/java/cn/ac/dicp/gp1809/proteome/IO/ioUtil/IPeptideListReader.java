/*
 ******************************************************************************
 * File: IPeptideListReader.java * * * Created on 09-07-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;

/**
 * Reader for peptide list file
 *
 * <p>
 * Changes:
 * <li>0.3, 04-22-2009: add method {@link #getPeakLists()}
 * <li>0.3.1, 05-03-2009: pull up the method {@link #getPeptideFormat()},
 * {@link #setPeptideFormat(cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat)};
 *
 * @author Xinning
 * @version 0.4.1, 05-20-2010, 16:36:20
 */
public interface IPeptideListReader extends IPeptideReader
{
    /**
     * Get the peak list for current readin peptides, if currently no peptide has been read in or the last returned
     * peptide is null. null will be returned.
     */
    IMS2PeakList[] getPeakLists();

    /**
     * The number of peptides in the peptide list. If the number is indetermineable, the value will be -1.
     */
    int getNumberofPeptides();

    /**
     * The index of current returned peptide. This index is the absolute index of this peptide in the peptide list file.
     * From 0 - n-1. If the current returned peptide is null, or no peptide has been read, return -1.
     *
     * <p><b>
     * If some filters are set, for example the top N peptides, this value may not equal to the number of readin
     * peptides.</b></p>
     *
     * @see IPeptideListAccesser
     */
    int getCurtPeptideIndex();

}
