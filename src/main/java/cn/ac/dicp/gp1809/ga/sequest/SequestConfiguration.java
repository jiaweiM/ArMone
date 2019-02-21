/*
 ******************************************************************************
 * File: SequestConfiguration.java * * * Created on 03-01-2010
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.ga.sequest;

import cn.ac.dicp.gp1809.ga.Chromosome;
import cn.ac.dicp.gp1809.ga.Configuration;
import cn.ac.dicp.gp1809.ga.FitnessFunction;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The configuration for SFOER
 *
 * @author Xinning
 * @version 0.1.1, 09-14-2010, 20:03:36
 */
public class SequestConfiguration extends Configuration
{

    private boolean isxcorr = true;
    private boolean isdcn = true;
    private boolean issp, isrsp, ision, isdeltams;

    private short xcorrbit, dcnbit, spbit, rspbit, ionbit, deltamsbit;

    private double maxFPR;
    private short optimizeType = 0;
    private int maxgenenum = 5;

    private float[][] peptides;

    private SequestValueLimit valueLimit;

    /**
     * whenever setFPR() or setPeptides is excuted, this value is set to true; And function of fitness need to refresh;
     */
    private boolean isChanged = true;
    private FitnessFunction function;

    /**
     * Sometimes not all the genes are used for the optimizing, then default values should be used for the final
     * filtering, use this values.
     */
    private double[] nullChromosomeValues = new double[]{0, 0, 0, 500, 50000, 0};
    ;

    @Override
    public FitnessFunction getFitnessFunction()
    {
        if (this.isChanged) {
            this.function = new SequestFitnessFunction(peptides, this.getMaxFPR(), optimizeType);
        }

        return this.function;
    }

    public void setPeptides(float[][] peptides)
    {
        this.peptides = peptides;
        this.isChanged = true;
    }

    /**
     * 采用Xcorr deltaCn Sp Rsp deltamsppm ion的顺序规则； 即，染色体中的基因采用上述规律排列,当有一个或多个上述基因未使用时，使用null代替，而规则变
     * 即，染色体中的的总基因数不变,允许使用空基因
     */
    @Override
    public Chromosome getSampleChromosome()
    {
        int maxgene = this.maxGeneNum();
        Chromosome sample = new SequestChromosome(this, maxgene);

        if (this.isXcorrFilter())
            sample.setGene(new XcorrGene(this), 0);

        if (this.isDeltaCnFilter())
            sample.setGene(new DeltaCnGene(this), 1);

        if (this.isSpFilter())
            sample.setGene(new SpGene(this), 2);

        if (this.isRspFilter())
            sample.setGene(new RspGene(this), 3);

        if (this.isDeltaMSFilter())
            sample.setGene(new DeltaMSGene(this), 4);

        if (this.isIonFilter())
            sample.setGene(new IonGene(this), 5);


        /*
         * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         * Check the null chromosome values
         * check the sequest fittness function
         * check the optimizied filter (in optimizer)
         * check add@sequestppldataforinput
         */

        return sample;
    }

    /**
     * These values indicate the null filter when one of the genes are not used for final filtering. for xcorr, deltacn,
     * sp, ions, this value is 0, while for Rsp, deltams, this value is 500 and 50000
     *
     * @return
     */
    public double[] getNullChromosomeValues()
    {
        return this.nullChromosomeValues;
    }

    public SequestValueLimit getSequestValueLimit()
    {
        if (this.valueLimit == null)
            this.valueLimit = new SequestValueLimit();

        return this.valueLimit;
    }

    public void setValueLimit(SequestValueLimit valuelimit)
    {
        this.valueLimit = valuelimit;
    }

    public double setMaxFPR(double fpr)
    {
        this.maxFPR = fpr;
        this.isChanged = true;
        return fpr;
    }

    public double getMaxFPR()
    {
        return this.maxFPR;
    }

    public short getOptimizeType()
    {
        return this.optimizeType;
    }

    public void setOptimizeType(short optimizeType)
    {
        if (this.optimizeType != optimizeType) {
            this.isChanged = true;
            this.optimizeType = optimizeType;
        }
    }

    /**
     * ��ǰ���Խ��ܵ���������������������ͬʱ�Ż��Ĳ�������Ŀ��
     */
    public int maxGeneNum()
    {
        return this.maxgenenum;
    }

    /**
     * �����λ��
     */
    public short getXcorrGeneBit()
    {
        return this.xcorrbit;
    }

    public void setXcorrGeneBit(short genebit)
    {
        this.xcorrbit = genebit;
    }

    public short getDeltaCnGeneBit()
    {
        return this.dcnbit;
    }

    public void setDeltaCnGeneBit(short genebit)
    {
        this.dcnbit = genebit;
    }

    public short getSpGeneBit()
    {
        return this.spbit;
    }

    public void setSpGeneBit(short genebit)
    {
        this.spbit = genebit;
    }

    public short getRspGeneBit()
    {
        return this.rspbit;
    }

    public void setRspGeneBit(short genebit)
    {
        this.rspbit = genebit;
    }

    public short getIonGeneBit()
    {
        return this.ionbit;
    }

    public void setIonGeneBit(short genebit)
    {
        this.ionbit = genebit;
    }

    public short getDeltaMSGeneBit()
    {
        return this.deltamsbit;
    }

    public void setDeltaMSGeneBit(short genebit)
    {
        this.deltamsbit = genebit;
    }


    /**
     * �Ƿ�ѡ�����в���
     */
    public boolean isXcorrFilter()
    {
        return this.isxcorr;
    }

    public void setXcorrFilter(boolean isxcorr)
    {
        this.isxcorr = isxcorr;
    }

    public boolean isDeltaCnFilter()
    {
        return this.isdcn;
    }

    public void setDeltaCnFilter(boolean isdcn)
    {
        this.isdcn = isdcn;
    }

    public boolean isSpFilter()
    {
        return this.issp;
    }

    public void setSpFilter(boolean issp)
    {
        this.issp = issp;
    }

    public boolean isRspFilter()
    {
        return this.isrsp;
    }

    public void setRspFilter(boolean isrsp)
    {
        this.isrsp = isrsp;
    }

    public boolean isIonFilter()
    {
        return this.ision;
    }

    public void setIonFilter(boolean ision)
    {
        this.ision = ision;
    }

    public boolean isDeltaMSFilter()
    {
        return this.isdeltams;
    }

    public void setDeltaMSFilter(boolean isdeltams)
    {
        this.isdeltams = isdeltams;
    }

    @Override
    public Configuration readFromFile(InputStream instream)
    {
        return null;
    }

    @Override
    public void write(OutputStream outstream)
    {

    }
}
