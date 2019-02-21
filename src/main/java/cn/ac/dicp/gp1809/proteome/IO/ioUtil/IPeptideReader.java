/*
 * *****************************************************************************
 * File: IPeptideReader.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;

import java.util.HashMap;

/**
 * Reader for peptide. The main method for this class is getPeptide(); When the reading is finished, close() method
 * should be execute is necessary.
 *
 * @author Xinning
 * @version 0.6.2, 05-20-2010, 16:56:14
 */
public interface IPeptideReader
{
    /**
     * Get the peptide format used for the peptide list string formating
     *
     * @since 0.6.1
     */
    IPeptideFormat<?> getPeptideFormat();

    /**
     * Set the peptide format used for the peptide string formating
     *
     * @param format
     * @since 0.6.1
     */
    void setPeptideFormat(IPeptideFormat<?> format);


    /**
     * Get the decoy database judger
     */
    IDecoyReferenceJudger getDecoyJudger();

    /**
     * Set the decoy database judger
     *
     * @param judger
     */
    void setDecoyJudger(IDecoyReferenceJudger judger);

    /**
     * Reading peptide instance for various of sources of different search algorithms.
     * <p>
     * For sequest, these include out, xml, xls, srf, ppl and ppls files.
     * <p>
     * For OMSSA and so on ...
     *
     * @return
     * @throws PeptideParsingException
     * @See ProReaderFactory.
     */
    IPeptide getPeptide() throws PeptideParsingException;

    /**
     * Get the peptide type for this reader.
     *
     * @return
     * @see PeptideType
     */
    PeptideType getPeptideType();

    /**
     * Get the database search parameter for this peptide reader.
     *
     * @return
     */
    ISearchParameter getSearchParameter();

    /**
     * Close the source of input for peptide reading
     */
    void close();

    /**
     * N of top matched peptides for reading in a out file.
     *
     * @return topn
     * @see #setTopN(int)
     * @since 0.6
     */
    int getTopN();

    /**
     * The topn matched peptides for a spectrum will be reported. If set as 1, only the top matched peptide will be
     * returned while calling for getPeptide(). The default value is 1 (top matched)
     * <p>
     * <b>Notice: this is a important parameter for the peptide reader, make
     * sure this value is correct before reading.</b>
     *
     * @param topn peptides for reading in a out file
     * @return currently always be true
     * @since 0.6
     */
    void setTopN(int topn);

    void setReplace(HashMap<Character, Character> replaceMap);

    /**
     * @return {@link ProteinNameAccesser}
     */
    ProteinNameAccesser getProNameAccesser();
}
