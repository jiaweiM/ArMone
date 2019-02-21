/*
 ******************************************************************************
 * File: PeakListGetterFactory.java * * * Created on 04-13-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ReaderGenerateException;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IRawSpectraReader;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.RawSpectraReaderFactory;

/**
 * Factory for the construction of PeakListGetter
 *
 * @author Xinning
 * @version 0.1, 04-13-2009, 23:07:23
 */
public class PeakListGetterFactory
{

    /**
     * Construct the peak list getter for the following dta format
     *
     * @param type
     * @param path
     * @return
     * @throws ReaderGenerateException
     */
    public static IRawSpectraReader create(DtaType type, String path)
            throws ReaderGenerateException
    {

        switch (type) {
            case MZDATA:
            case MZXML:
                return constructForRawDtaFormat(type, path);
            case DTA:
            case MGF:
            case MS2:
//			return constructForBatchDtaReader(type, path);
            default:
                throw new IllegalArgumentException("Unknow dta format: " + type);
        }
    }

    /**
     * For raw dta format
     *
     * @param type
     * @param path
     * @return
     * @throws ReaderGenerateException
     */
    private static IRawSpectraReader constructForRawDtaFormat(DtaType type,
            String path) throws ReaderGenerateException
    {
        try {
            IRawSpectraReader reader = RawSpectraReaderFactory.createReader(
                    type, path);

            return reader
                    ;
        } catch (Exception e) {
            throw new ReaderGenerateException(e);
        }
    }

    /**
     * For raw dta format
     *
     * @param type
     * @param path
     * @return
     * @throws ReaderGenerateException
     */
    private static IPeakListGetter constructForBatchDtaReader(DtaType type,
            String path) throws ReaderGenerateException
    {
        try {
//			aa
            return null;
        } catch (Exception e) {
            throw new ReaderGenerateException(e);
        }
    }

}
