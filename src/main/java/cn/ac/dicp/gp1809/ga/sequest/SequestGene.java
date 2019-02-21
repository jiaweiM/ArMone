package cn.ac.dicp.gp1809.ga.sequest;

import cn.ac.dicp.gp1809.ga.Gene;

/**
 * @author Xingning Jiang(vext@dicp.ac.cn)
 */
public abstract class SequestGene extends Gene
{

    //need not to be clone, all use the same instence;
    private final SequestValueLimit vlimit;

    /**
     * Max value of this gene. When decoding gene to actual value, this max bound must be computed; this value equals
     * 2^genelength; The observed value of the random sequest gene is ranged from 0-maxbitvalue.
     */
    private final double maxBitValue;

    private SequestConfiguration sconfig;


    public SequestGene(final SequestConfiguration config)
    {
        super(config);
        this.sconfig = config;
        this.vlimit = config.getSequestValueLimit();

        this.maxBitValue = Math.pow(2d, this.length());
    }

    @Override
    public String encode()
    {
        String geneString = this.getRandomGenerator().generateBinString(this.length());
        return geneString;
    }

    @Override
    public double value()
    {
        double obvalue = Integer.parseInt(this.genestring, 2);
        double acvalue = this.getActualLowerBound() +
                (getActualUpperBound() - getActualLowerBound()) * obvalue / this.getMaxBitValue();

        return acvalue;
    }

    /**
     * Max value of this gene. When decoding gene to actual value, this max bound must be computed; this value equals
     * 2^genelength; The observed value of the random sequest gene is ranged from 0-maxbitvalue.
     */
    public final double getMaxBitValue()
    {
        return this.maxBitValue;
    }

    public SequestValueLimit getValueLimit()
    {
        return this.vlimit;
    }

    protected SequestConfiguration getSequestConfiguration()
    {
        return this.sconfig;
    }

    /**
     * In sequest filtering criteria optimization, not all double values can be a criterion. To minish search region,
     * both actual upper bound and lower bound are needed;
     *
     * @return actual upper bound for search;
     */
    protected abstract double getActualUpperBound();

    /**
     * In sequest filtering criteria optimization, not all double values can be a criterion. To minish search region,
     * both actual upper bound and lower bound are needed;
     *
     * @return actual lower bound for search;
     */
    protected abstract double getActualLowerBound();

}
