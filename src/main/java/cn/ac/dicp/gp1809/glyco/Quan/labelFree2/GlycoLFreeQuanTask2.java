/*
 ******************************************************************************
 * File: GlycoLFreeQuanTask2.java * * * Created on 2013-6-13
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree2;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.util.progress.ITask;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.dom4j.DocumentException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * @author ck
 * @version 2013-6-13, 13:48:20
 */
public class GlycoLFreeQuanTask2 implements ITask
{
    private IPeptideListReader reader;
    private NGlycoFreeGetter2 getter;
    private AminoacidModification aam;

    private GlycoLFFeasXMLWriter2 writer;
    private NGlycoPepCriteria nGlyCri = new NGlycoPepCriteria(true);

    private IPeptide curtPeptide;
    private int total;
    private int curt;
    private boolean integration = false;

    public GlycoLFreeQuanTask2(IPeptideListReader reader, String peakfile,
                               String result, GlycoJudgeParameter jpara, int glycanType)
            throws IOException, XMLStreamException
    {
        this.reader = reader;
        this.total = reader.getNumberofPeptides();
        this.aam = reader.getSearchParameter().getVariableInfo();

        this.getter = new NGlycoFreeGetter2(peakfile, jpara);
        this.writer = new GlycoLFFeasXMLWriter2(result);
        this.writer.addTotalCurrent(getter.getMS1TotalCurrent());
        this.writer.addModification(aam);
        this.writer.addProNameInfo(reader.getProNameAccesser());
    }

    @Override
    public boolean hasNext()
    {
        try {
            boolean has = (curtPeptide = this.reader.getPeptide()) != null;

            if (has) {
                return true;
            } else {
                if (!this.integration) {
                    this.integration = true;
                    return true;
                } else
                    return false;
            }
        } catch (PeptideParsingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processNext()
    {
        if (this.integration) {

            try {

                getter.match();

                NGlycoSSM[] ssms = getter.getGlycoSpectra();
                IGlycoPeptide[] peps = getter.getGlycoPeptides();

                for (NGlycoSSM ssm : ssms) {
                    writer.addGlycoSpectra(ssm);
                }

                for (IGlycoPeptide pep : peps) {
                    writer.addIdenPep(pep);
                }

                writer.addBestEstimate(getter.getBestEstimate());
                writer.write();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            if (this.curtPeptide == null)
                throw new NullPointerException("Null peptide. No more peptide?");

            if (!curtPeptide.isTP())
                return;

            if (nGlyCri.filter(curtPeptide)) {
//			if (curtPeptide.getSequence().contains("N*")) {
                IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
                this.getter.addPeptide(gp, aam);
            }

            this.curt = this.reader.getCurtPeptideIndex();
        }
    }

    @Override
    public float completedPercent()
    {
        float per = (float) curt / (float) total;
        return per > 1 ? 1 : per;
    }

    @Override
    public boolean inDetermineable()
    {
        return false;
    }

    @Override
    public void dispose()
    {
        reader.close();
    }

    public GlycoLFFeasXMLReader2 createReader()
    {
        return this.writer.createReader();
    }

    private static void test() throws FileDamageException, IOException,
            XMLStreamException, WriteException, DocumentException
    {
        String pep = "H:\\NGLYCO\\NGlyco_original_data_1D\\O18\\Rui_20130620_HEK_HILIC_deglyco_PNGaseF_18O_HCD_130622201552_F004957.csv.ppl";
        String peak = "H:\\NGLYCO\\NGlyco_original_data_1D\\O18\\Rui_20130624_HEK_HILIC_deglyco_PNGaseF_18O_HCD.mzXML";
        String out = "H:\\NGLYCO\\NGlyco_original_data_1D\\O18\\Rui_20130624_HEK_HILIC_deglyco_PNGaseF_18O_HCD.test.pxml";
        String xls = "H:\\NGLYCO\\NGlyco_original_data_1D\\O18\\Rui_20130624_HEK_HILIC_deglyco_PNGaseF_18O_HCD.test.xls";

//		String pep = "H:\\NGLYCO\\NGlyco_original_data_20130613\\Rui_20130620_HEK_HILIC_deglyco_PNGaseF_18O_HCD_130622201552_F004957.csv.ppl";
//		String pep = "H:\\NGLYCO\\NGlyco_original_data_20130613\\2D\\iden\\Rui_20130604_HEK_HILIC_F2_deglyco_F004827.csv.ppl";
//		String peak = "H:\\NGLYCO\\NGlyco_original_data_20130613\\2D\\Rui_20130604_HEK_HILIC_F2.mzXML";
//		String out = "D:\\P\\graduation\\20140718 Rui_20130604_HEK_HILIC_F2.pxml";
//		String xls = "D:\\P\\graduation\\20140718 Rui_20130604_HEK_HILIC_F2.xls";

        IPeptideListReader reader = new PeptideListReader(pep);

        GlycoJudgeParameter para = new GlycoJudgeParameter(0.001f, 30f, 0.15f,
                500, 0.3f, 30.0f, 1);

        GlycoLFreeQuanTask2 task = new GlycoLFreeQuanTask2(reader, peak, out,
                para, 0);

        while (task.hasNext()) {
            task.processNext();
        }
        task.dispose();

        GlycoLFFeasXMLReader2 resultreader = new GlycoLFFeasXMLReader2(out);
        GlycoMatchXlsWriter2 writer = new GlycoMatchXlsWriter2(xls);
        IGlycoPeptide[] peps = resultreader.getAllGlycoPeptides();
        NGlycoSSM[] matchedssms = resultreader.getMatchedGlycoSpectra();
        NGlycoSSM[] unmatchedssms = resultreader.getUnmatchedGlycoSpectra();
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        writer.write(peps, matchedssms, unmatchedssms, resultreader.getBestEstimate(), accesser, para.getMzThresPPM(), para.getRtTole());
    }

    private static void testO18(String dir) throws FileDamageException, IOException,
            XMLStreamException, WriteException,
            DocumentException
    {
        String pep = "H:\\NGLYCO\\NGlyco_original_data_20130613\\Rui_20130624_HEK_HILIC_deglyco_PNGaseF_18O_HCD_F004969.csv.ppl";

        File[] files = (new File("H:\\NGLYCO\\NGlyco_original_data_20130613\\2D\\glyco_spectra\\1")).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().endsWith("mzXML"))
                continue;

            String peak = files[i].getAbsolutePath();
            String out = dir + "\\" + files[i].getName().replace("mzXML", "pxml");
            String xls = dir + "\\" + files[i].getName().replace("mzXML", "xls");

            IPeptideListReader reader = new PeptideListReader(pep);

            GlycoJudgeParameter para = new GlycoJudgeParameter(0.001f, 30f, 0.15f,
                    500, 0.3f, 30.0f, 1);

            GlycoLFreeQuanTask2 task = new GlycoLFreeQuanTask2(reader, peak, out,
                    para, 0);

            while (task.hasNext()) {
                task.processNext();
            }
            task.dispose();

            GlycoLFFeasXMLReader2 resultreader = new GlycoLFFeasXMLReader2(out);
            GlycoMatchXlsWriter2 writer = new GlycoMatchXlsWriter2(xls);
            IGlycoPeptide[] peps = resultreader.getAllGlycoPeptides();
            NGlycoSSM[] matchedssms = resultreader.getMatchedGlycoSpectra();
            NGlycoSSM[] unmatchedssms = resultreader.getUnmatchedGlycoSpectra();
            ProteinNameAccesser accesser = reader.getProNameAccesser();
            writer.write(peps, matchedssms, unmatchedssms, resultreader.getBestEstimate(), accesser,
                    para.getMzThresPPM() / 3.0, para.getRtTole());
        }
    }

    private static void testwsy() throws IOException, FileDamageException, XMLStreamException, DocumentException, WriteException
    {
        String file = "Z:\\WangShuyue\\my_everything\\170722_uf_deg_3.csv.ppl";
        String peak = "Z:\\WangShuyue\\my_everything\\170722_uf_g_3.mzXML";
        String out = peak.replace("mzXML", "pxml");
        String xls = peak.replace("mzXML", "xls");

        IPeptideListReader reader = new PeptideListReader(file);

        GlycoJudgeParameter para = new GlycoJudgeParameter(0.001f, 30f, 0.15f,
                500, 0.3f, 30.0f, 1);

        GlycoLFreeQuanTask2 task = new GlycoLFreeQuanTask2(reader, peak, out, para, 0);
        while (task.hasNext()) {
            task.processNext();
        }
        task.dispose();

        GlycoLFFeasXMLReader2 resultreader = new GlycoLFFeasXMLReader2(out);
        GlycoMatchXlsWriter2 writer = new GlycoMatchXlsWriter2(xls);
        IGlycoPeptide[] peps = resultreader.getAllGlycoPeptides();
        NGlycoSSM[] matchedssms = resultreader.getMatchedGlycoSpectra();
        NGlycoSSM[] unmatchedssms = resultreader.getUnmatchedGlycoSpectra();
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        writer.write(peps, matchedssms, unmatchedssms, resultreader.getBestEstimate(), accesser,
                para.getMzThresPPM() / 3.0, para.getRtTole());
    }

    public static void main(String[] args) throws FileDamageException,
            IOException, XMLStreamException, RowsExceededException, WriteException, DocumentException
    {
        long begin = System.currentTimeMillis();
//        testwsy();
//        GlycoLFreeQuanTask2.test();

//		GlycoLFreeQuanTask2.testO18("H:\\NGLYCO\\NGlyco_final_20140401\\decoy_match");

        String file = "Z:\\WangShuyue\\data\\csv\\170722_uf_deg_3.csv.ppl";//args[0];
        String peak = "Z:\\WangShuyue\\data\\mzxml\\170722_uf_g_6.mzXML";//args[1];
        String out = peak.replace("mzXML", "pxml");
        String xls = peak.replace("mzXML", "xls");

        IPeptideListReader reader = new PeptideListReader(file);

        GlycoJudgeParameter para = new GlycoJudgeParameter(0.001f, 30f, 0.15f,
                500, 0.3f, 30.0f, 1);

        GlycoLFreeQuanTask2 task = new GlycoLFreeQuanTask2(reader, peak, out, para, 0);
        while (task.hasNext()) {
            task.processNext();
        }
        task.dispose();

        GlycoLFFeasXMLReader2 resultreader = new GlycoLFFeasXMLReader2(out);
        GlycoMatchXlsWriter2 writer = new GlycoMatchXlsWriter2(xls);
        IGlycoPeptide[] peps = resultreader.getAllGlycoPeptides();
        NGlycoSSM[] matchedssms = resultreader.getMatchedGlycoSpectra();
        NGlycoSSM[] unmatchedssms = resultreader.getUnmatchedGlycoSpectra();
        ProteinNameAccesser accesser = reader.getProNameAccesser();
        writer.write(peps, matchedssms, unmatchedssms, resultreader.getBestEstimate(), accesser,
                para.getMzThresPPM() / 3.0, para.getRtTole());

        long end = System.currentTimeMillis();

        System.out.println((end - begin) / 6.0E4 + " min");
    }
}
