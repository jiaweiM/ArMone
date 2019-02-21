/*
 ******************************************************************************
 * File: PplCreateTask.java * * * Created on 03-12-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IPplCreationTask;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.MascotEmbedPplCreatorTask;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.OMSSAOMXEmbedPplCreatorTask;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.PeptideListCreatorTask;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.SequestDtaoutPplCreatorTask;
import cn.ac.dicp.gp1809.proteome.IO.sequest.zipdata.ZippedDtaOutUltility;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.util.progress.ITaskDetails;

/**
 * The task for ppl creation
 *
 * @author Xinning
 * @version 0.2.1, 05-20-2010, 19:34:31
 */
class PplCreateTaskDetails implements ITaskDetails
{
    private PeptideType type;
    private DtaType dtatype;
    private String input;
    private String peaklistfile;
    private int topn;
    private String output;
    private String database;
    private String mascot_regex;
    private IDecoyReferenceJudger judger;

    private boolean usEmbededPeaklist = false;

    PplCreateTaskDetails(PeptideType type, String input, String peaklistfile,
            DtaType dtatype, int topn, String output, String database,
            String mascot_regex, IDecoyReferenceJudger judger)
    {
        this.type = type;
        this.input = input;
        this.peaklistfile = peaklistfile;
        this.topn = topn;
        this.output = output;
        this.database = database;
        this.mascot_regex = mascot_regex;
        this.judger = judger;

        this.validateNull();
        if (dtatype == null)
            this.dtatype = this.parseDtaType(peaklistfile);
        else
            this.dtatype = dtatype;
    }

    PplCreateTaskDetails(PeptideType type, String input,
            boolean useEmbededPeaklist, int topn, String output,
            String database, String mascot_regex, IDecoyReferenceJudger judger)
    {
        this.type = type;
        this.input = input;
        this.topn = topn;
        this.output = output;
        this.database = database;
        this.mascot_regex = mascot_regex;
        this.usEmbededPeaklist = useEmbededPeaklist;
        this.judger = judger;

        this.validateNull();
    }

    private void validateNull()
    {
        if (this.type == null)
            throw new NullPointerException("Type of search engine is null.");

        if (this.input == null || this.input.length() == 0)
            throw new NullPointerException("Input source is null.");

        if (!this.usEmbededPeaklist) {
            if (this.peaklistfile == null || this.peaklistfile.length() == 0)
                throw new NullPointerException("Peak list file is null.");
        }

        if (this.database == null || this.database.length() == 0) {
            this.database = null;
        }

        if (this.output == null || this.output.length() == 0)
            throw new NullPointerException("Ouput path is null.");

        if (this.type == PeptideType.MASCOT && this.mascot_regex == null)
            throw new NullPointerException(
                    "Mascot accession regular expression is null.");

        if (this.judger == null)
            throw new NullPointerException(
                    "Decoy system is null.");
    }

    /**
     * Parse the dta type by the extension of the peak list file. The mzxml file must be with extension of mzxml and
     * mzdata file must be with extension of xml
     *
     * @param peaklistname
     * @return
     */
    private DtaType parseDtaType(String peaklistname)
    {
        String lowname = peaklistname.toLowerCase();

        if (lowname.endsWith(".mzxml"))
            return DtaType.MZXML;

        if (lowname.endsWith(".xml"))
            return DtaType.MZDATA;

        throw new IllegalArgumentException("Unkown type of peak list file.");
    }

    @Override
    public IPplCreationTask getTask()
    {
        IPplCreationTask task;
        try {

            if (this.usEmbededPeaklist) {
                switch (type) {
                    case SEQUEST: {
                        if (!(new File(input).isDirectory() || input.toLowerCase().endsWith(ZippedDtaOutUltility.EXTENSION))) {
                            throw new IllegalArgumentException(
                                    "Only sequest directory of dta and out and the compressed directory and .xml file can use the embeded peak list.");
                        }

                        task = new SequestDtaoutPplCreatorTask(output, input, topn, database, false, this.judger);
                        break;
                    }
                    case MASCOT: {
                        task = new MascotEmbedPplCreatorTask(output, input, topn, database, false, mascot_regex, this.judger);
                        break;
                    }
                    case OMSSA: {
                        task = new OMSSAOMXEmbedPplCreatorTask(output, input, topn, database, false, this.judger);
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Currently unsupport the embeded peak list for :" + type);
                }
            } else {
                switch (type) {
                    case MASCOT:
                        task = new PeptideListCreatorTask(output, input, topn,
                                database, mascot_regex, dtatype,
                                peaklistfile, false, this.judger);
                        break;
                    case INSPECT:
                        //				thread = BatchPplCreator.create4InspectInNewThread(input,
                        //				        paramfile, sourcefile, output, topn, database, dtatype,
                        //				        peaklistfile);
                        task = null;
                        break;
                    case SEQUEST: {
                        task = new PeptideListCreatorTask(output, input, topn,
                                database, type, dtatype, peaklistfile, true, this.judger);
                        break;
                    }

                    default:
                        task = new PeptideListCreatorTask(output, input, topn,
                                database, type, dtatype, peaklistfile, false, this.judger);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return task;
    }

    @Override
    public String toString()
    {
        return this.input;
    }
}
