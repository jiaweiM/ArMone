/*
 ******************************************************************************
 * File: GlycoLFreeQuanTask.java * * * Created on 2011-11-8
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultVariModPepFilter;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.quant.labelFree.FreeFeatures;
import cn.ac.dicp.gp1809.util.progress.ITask;
import flanagan.analysis.Regression;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ck
 * @version 2011-11-8, 19:19:41
 */
public class GlycoLFreeQuanTask implements ITask
{

    private IPeptideListReader reader;
    private AbstractFeaLFreeGetter getter;

    // 0: N-glycans; 1: O-glycans
    private int glycoType;
    private AminoacidModification aam;
    private DefaultVariModPepFilter[] oGlyCirs;
    private NGlycoPepCriteria nGlyCri = new NGlycoPepCriteria(true);
    private float rtTolerance;

    private GlycoLFFeasXMLWriter writer;

    private IPeptide curtPeptide;
    private int total;
    private int curt;
    private boolean integration = false;

    public GlycoLFreeQuanTask(IPeptideListReader reader, String peakfile, String result,
            GlycoJudgeParameter jpara, int glycanType) throws IOException, XMLStreamException
    {

        this.reader = reader;
        this.total = reader.getNumberofPeptides();
        this.glycoType = glycanType;
        this.aam = reader.getSearchParameter().getVariableInfo();
        this.rtTolerance = jpara.getRtTole();

        switch (glycanType) {

            case 0: {

                this.getter = new NGlyStrucFreeGetter(peakfile, jpara);
                break;
            }

            case 1: {
				
/*				HashSet <DefaultVariModPepFilter> oGlyCirs = new HashSet <DefaultVariModPepFilter>();
				HashSet <Character> symset = new HashSet <Character>();
				Modif [] modifs = aam.getModifications();
				for(int i=0;i<modifs.length;i++){
					double mass = modifs[i].getMass();
					if(Math.abs(mass-365.132196)<0.01){
						symset.add(modifs[i].getSymbol());
					}
					if(Math.abs(mass-203.079373)<0.01){
						symset.add(modifs[i].getSymbol());
					}
					DefaultVariModPepFilter oGlyCri = 
						new DefaultVariModPepFilter(modifs[i].getName(), modifs[i].getSymbol(), "ST");
					oGlyCirs.add(oGlyCri);
				}
				
				this.oGlyCirs = oGlyCirs.toArray(new DefaultVariModPepFilter[oGlyCirs.size()]);
				this.getter = new OGlyFreeGetter(peakfile, jpara, symset);
				break;
*/
            }
        }

        this.writer = new GlycoLFFeasXMLWriter(result);
        this.writer.addTotalCurrent(getter.getMS1TotalCurrent());
        this.writer.addModification(aam);
        this.writer.addProNameInfo(reader.getProNameAccesser());
    }

    /**
     * @param args
     * @throws XMLStreamException
     * @throws IOException
     * @throws FileDamageException
     * @throws PeptideParsingException
     */
    public static void main(
            String[] args) throws IOException, XMLStreamException, FileDamageException, PeptideParsingException
    {
        // TODO Auto-generated method stub

        String peak = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_10ms.mzXML";
        String ppl1 = "H:\\glyco\\label-free\\20111122_HILIC_1105_deglyco_111122212916_F001841.csv.ppl";
        String ppl2 = "H:\\glyco\\label-free\\20111122_HILIC_1105_deglyco_111123021533_F001914.csv.ppl";
        String ppl3 = "H:\\glyco\\label-free\\20111117_HILIC_1024_deglyco_2023.csv.ppl";
        String ppl4 = "H:\\glyco\\label-free\\20111117_HILIC_1024_deglyco_111120150937_2022.csv.ppl";
        String out = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_10ms.match.pxml";

//		PeptideListPagedRowGetter2 getter = new PeptideListPagedRowGetter2(new File[]{new File(ppl1), new File(ppl2),
//			new File(ppl3), new File(ppl4)});

        IPeptideListReader reader = new PeptideListReader("H:\\20130519_glyco\\iden\\" +
                "F004707.csv.ppl");
        GlycoJudgeParameter para = new GlycoJudgeParameter(0.001f, 20f, 0.15f, 500, 0.3f, 60.0f, 1);
        GlycoLFreeQuanTask task = new GlycoLFreeQuanTask(reader, peak, out,
                para, 0);

        while (task.hasNext()) {
            task.processNext();
        }
        task.dispose();
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
     */
    @Override
    public float completedPercent()
    {
        // TODO Auto-generated method stub
        float per = (float) curt / (float) total;
        return per > 1 ? 1 : per;
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
     */
    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub
        reader.close();
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
     */
    @Override
    public boolean hasNext()
    {
        // TODO Auto-generated method stub
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
    public boolean inDetermineable()
    {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
     */
    @Override
    public void processNext()
    {
        if (this.integration) {

            HashMap<String, FreeFeatures> feaMap = getter.getGlycoFeatures();
            HashMap<String, IGlycoPeptide> glycoPepMap = getter.getGlycoPepMap();
            double median = ((NGlyStrucFreeGetter) getter).getMedianDiff();

            double[] glycoRt = new double[glycoPepMap.size()];
            double[] pepRt = new double[glycoPepMap.size()];

            int count = 0;
            Iterator<String> it0 = glycoPepMap.keySet().iterator();
            while (it0.hasNext()) {
                String key = it0.next();
                IGlycoPeptide pep = glycoPepMap.get(key);
                glycoRt[count] = pep.getDeleStructure().getRT();
                pepRt[count] = pep.getRetentionTime();
                count++;
            }

            if (count > 10) {

                Regression reg = new Regression(glycoRt, pepRt);
                reg.linear();
                double[] fit = reg.getBestEstimates();

                Iterator<String> it = feaMap.keySet().iterator();
                while (it.hasNext()) {

                    String key = it.next();
                    FreeFeatures feas = feaMap.get(key);
                    IGlycoPeptide pep = glycoPepMap.get(key);

                    double pr = pep.getRetentionTime();
                    double gr = pep.getDeleStructure().getRT();

                    double y = fit[0] + fit[1] * gr;

                    if (Math.abs(pr - y) < this.rtTolerance)
                        this.writer.addFeature(feas, pep);

                }

            } else {

                Iterator<String> it = feaMap.keySet().iterator();
                while (it.hasNext()) {

                    String key = it.next();
                    FreeFeatures feas = feaMap.get(key);
                    IGlycoPeptide pep = glycoPepMap.get(key);

                    if (Math.abs(pep.getRtDiff() - median) < this.rtTolerance)
                        this.writer.addFeature(feas, pep);

                }
            }

            try {

                writer.write();
                writer.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {

            if (this.curtPeptide == null)
                throw new NullPointerException("Null peptide. No more peptide?");

            if (!curtPeptide.isTP())
                return;

            if (glycoType == 0) {
                if (nGlyCri.filter(curtPeptide)) {
                    IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
                    this.getter.addPeptide(gp, aam);
                }
            } else if (glycoType == 1) {
                for (int i = 0; i < oGlyCirs.length; i++) {
                    if (oGlyCirs[i].filter(curtPeptide)) {
                        IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
                        this.getter.addPeptide(gp, aam);
                    }
                }
            }

            this.curt = this.reader.getCurtPeptideIndex();
        }
    }

    public GlycoLFFeasXMLReader createReader()
    {
        return this.writer.createReader();
    }

}
