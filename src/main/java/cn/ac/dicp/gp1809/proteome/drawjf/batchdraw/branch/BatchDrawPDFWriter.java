/*
 ******************************************************************************
 * File: BatchDrawHtmlWriter.java * * * Created on 04-16-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.branch;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideType;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.AbstractBatchDrawWriter;
import cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.util.ioUtil.pdf.BatchPdfStamper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Write the peptide to a html file with hyper links
 *
 * @author Xinning
 * @version 0.1, 04-16-2009, 20:52:51
 */
public class BatchDrawPDFWriter extends AbstractBatchDrawWriter
        implements IBatchDrawWriter
{

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_PAGEIDX = "pageIdx";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_SCAN = "Scan";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_Mr = "Mr";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_DELTAMH = "DeltaMr";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_CHARGE = "Charge";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_SEQUENCE = "Sequence";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_PROTEINS = "Protein";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_RANK = "Rank";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_Score = "Score";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_NTT = "NumOfTerminals";

    /**
     * The field name of the specific term in the template
     */
    protected static final String FIELD_MissCleaves = "Miss Cleaves";

    private String templete = "/src/resources/PeptideTemplate.pdf";

    private int index = 1;

    private PeptideType type;
    private File output;
    private BatchPdfStamper pdf;
    /**
     * @param output
     * @param htmHeader header contains style and other informations
     * @throws IOException
     * @throws DocumentException
     */
    public BatchDrawPDFWriter(String output, ISearchParameter parameter,
            PeptideType type) throws IOException, DocumentException
    {

        super(parameter, type);

        this.type = type;
        //Check the extension
        String lowname = output.toLowerCase();
        if (!lowname.endsWith("pdf")) {
            output += ".pdf";
        }

        this.output = new File(output);
        this.printHeader(parameter);

        pdf = new BatchPdfStamper((new File(System.getProperty("user.dir") + templete).
                getAbsolutePath()), output, true);
    }

    /**
     * Get writer instance for the specific parameter and peptide type
     *
     * @param output
     * @param parameter
     * @param type
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static BatchDrawPDFWriter getInstance(String output,
            ISearchParameter parameter, PeptideType type) throws IOException,
            DocumentException
    {
        return new BatchDrawPDFWriter(output, parameter, type);
    }

    /**
     * @param args
     * @throws IOException
     * @throws FileDamageException
     * @throws DocumentException
     */
    public static void main(String[] args) throws FileDamageException,
            IOException, DocumentException
    {

        PeptideListReader reader = new PeptideListReader(
                "C:\\Documents and Settings\\ck\\My Documents\\20120621LysCMix1_100mM_F001309.dat.ppl");
        reader.setTopN(1);
        BatchDrawPDFWriter writer = new BatchDrawPDFWriter("C:\\Documents and Settings\\ck\\My Documents\\" +
                "ppp.pdf", reader.getSearchParameter(),
                PeptideType.MASCOT);
//			BatchDrawPDFWriter.getInstance(
//		        "E:\\Data\\srf\\try_drawer.pdf", reader.getSearchParameter(),
//		        PeptideType.SEQUEST);
        IPeptide pep;
        int count = 0;
        while ((pep = reader.getPeptide()) != null) {
            if (count++ > 10)
                break;
            System.out.println("Drawing " + pep.getScanNum());
            writer.write(pep, reader.getPeakLists(), new int[]{Ion.TYPE_B, Ion.TYPE_Y});
        }

        writer.close();
        reader.close();
    }

    /**
     * Print the header
     *
     * @param parameter
     * @throws DocumentException
     * @throws IOException
     */
    private void printHeader(ISearchParameter parameter)
            throws DocumentException, IOException
    {

        String header = "Fix modification(s): "
                + parameter.getStaticInfo().getModfiedAADescription(true)
                + "\nVariable modification(s): "
                + parameter.getVariableInfo().getModficationDescription();

        Document document = new Document();
        FileOutputStream os = new FileOutputStream(this.output);
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph(header));

        document.close();
        os.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#write(cn
     * .ac.dicp.gp1809.proteome.IO.proteome.IPeptide,
     * cn.ac.dicp.gp1809.proteome.spectrum.IPeakList<?>[],
     * cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold,
     * cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo[])
     */
    @Override
    public void write(IPeptide peptide, IMS2PeakList[] peaklists, int[] types,
            ISpectrumThreshold threshold, NeutralLossInfo[] losses)
            throws IOException
    {

        this.validate(peptide);

        BufferedImage image = this.createImage(peptide, peaklists, types, threshold, losses);

        try {
            this.pdf.stamp(this.getFieldMap(peptide), image, index - 1);
        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }

    /**
     * Create the field to value map for the template
     *
     * @param peptide
     * @return
     */
    protected HashMap<String, String> getFieldMap(IPeptide peptide)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if (index % 2 == 1) {
            map.put(FIELD_PAGEIDX, String.valueOf(index / 2 + 1));
        } else {
            map.put(FIELD_PAGEIDX, String.valueOf(index / 2));
        }
        map.put(FIELD_SCAN, String.valueOf(peptide.getScanNumBeg()));
        map.put(FIELD_CHARGE, String.valueOf(peptide.getCharge()));
        map.put(FIELD_DELTAMH, String.valueOf(peptide.getDeltaMH()));
        map.put(FIELD_Mr, String.valueOf(peptide.getMr()));
        map.put(FIELD_Score, String.valueOf(peptide.getPrimaryScore()));
        map.put(FIELD_NTT, String.valueOf(peptide.getNumberofTerm()));
        map.put(FIELD_MissCleaves, String.valueOf(peptide.getMissCleaveNum()));
        map.put(FIELD_PROTEINS, peptide.getReferenceOutString());
        map.put(FIELD_RANK, String.valueOf(peptide.getRank()));
        map.put(FIELD_SEQUENCE, putPBefore(peptide.getSequence()));

        index++;
        return map;
    }

    /*
     * (non-Javadoc)
     *
     * @see cn.ac.dicp.gp1809.proteome.drawjf.batchdraw.IBatchDrawWriter#close()
     */
    @Override
    public void close()
    {
        try {
            this.pdf.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
