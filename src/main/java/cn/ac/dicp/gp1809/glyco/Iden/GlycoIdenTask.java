/*
 ******************************************************************************
 * File: GlycoIdenTask.java * * * Created on 2012-5-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Iden;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSpecStrucGetter;
import cn.ac.dicp.gp1809.util.progress.ITask;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * @author ck
 * @version 2012-5-16, 08:45:22
 */
public class GlycoIdenTask implements ITask
{
    private NGlycoSpecStrucGetter getter;
    private GlycoIdenXMLWriter writer;
    private NGlycoSSM[] ssms;
    private int id = 0;

    public GlycoIdenTask(String peakfile, String result, GlycoJudgeParameter jpara)
            throws XMLStreamException, IOException
    {

        this.getter = new NGlycoSpecStrucGetter(peakfile, jpara);
        this.writer = new GlycoIdenXMLWriter(result);
        this.ssms = getter.getGlycoSSMs();
    }

    @Override
    public float completedPercent()
    {
        return 0;
    }

    @Override
    public void dispose()
    {
        try {
            this.writer.write();
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getter = null;
        System.gc();
    }

    @Override
    public boolean hasNext()
    {
        return id < ssms.length;
    }

    @Override
    public boolean inDetermineable()
    {
        return false;
    }

    @Override
    public void processNext()
    {
        this.writer.addGlycan(ssms[id]);
        this.id++;
    }

    public static void batchProcess(String in) throws XMLStreamException, IOException
    {
        GlycoJudgeParameter jpara = new GlycoJudgeParameter(0.001f, 20f, 0.15f, 500, 0.3f, 60.0f, 1);
        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("mzXML")) {
                System.out.println(files[i].getName());
                String out = files[i].getAbsolutePath().replace("mzXML", "iden.pxml");
                GlycoIdenTask task = new GlycoIdenTask(files[i].getAbsolutePath(), out, jpara);
                while (task.hasNext()) {
                    task.processNext();
                }

                task.dispose();
            }
        }
    }

    public static void batchProcess(String in, String out) throws Exception
    {
        GlycoJudgeParameter jpara = GlycoJudgeParameter.defaultParameter();
        File[] files = (new File(in)).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith("mzXML")) {
                System.out.println(files[i].getName());
                String output = out + "\\" + files[i].getName().replace("mzXML", "iden-3.pxml");
                GlycoIdenTask task = new GlycoIdenTask(files[i].getAbsolutePath(), output, jpara);
                while (task.hasNext()) {
                    task.processNext();
                }

                task.dispose();
            }
        }
        GlycoIdenXlsWriter.batchWrite(out);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        String in = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.mzXML";
        String out = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.iden.0927.pxml";
        GlycoJudgeParameter jpara = new GlycoJudgeParameter(0.001f, 20f, 0.15f, 500, 0.3f, 60.0f, 1);

        GlycoIdenTask.batchProcess("I:\\20150205_ML_sialic_acid_Ox_Hz_HA_release",
                "I:\\20150205_ML_sialic_acid_Ox_Hz_HA_release");
    }
}
