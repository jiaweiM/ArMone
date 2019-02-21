/*
 ******************************************************************************
 * File: PeptideType.java * * * Created on 08-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

/**
 * The type of peptide (the algorithm used for peptide identification)
 *
 * <p>
 * Changes:
 * <li>0.2, 02-27-2009: Add APIVSE peptide type
 * <li>0.2.1, 03-24-2009: add Inspect peptide type
 * <li>0.3, 04-22-2009: add method {@link #isPeptidePair()}
 * <li>0.3.1, 08-08-2009: add GENERIC type.
 *
 * @author Xinning
 * @version 0.3.1, 08-08-2009, 12:21:13
 */
public enum PeptideType
{
    /**
     * generic peptide type
     */
    GENERIC("Generic", -1, false),

    /**
     * Peptide identified by SEQUEST, index: 0
     */
    SEQUEST("SEQUEST", 0, false),

    /**
     * Peptide identified by Mascot, index: 1
     */
    MASCOT("MASCOT", 1, false),

    /**
     * Peptide identified by X!Tandem, index: 2
     */
    XTANDEM("X!TANDEM", 2, false),

    /**
     * Peptide identified by OMSSA, index: 3
     */
    OMSSA("OMSSA", 3, false),

    /**
     * Peptide identified by Inspect, index: 4
     */
    INSPECT("Inspect", 4, false),

    /**
     * Peptide identified by Inspect, index: 5
     */
    CRUX("Crux", 5, false),

    /**
     * Peptide identified by Inspect, index: 6
     */
    MaxQuant("MaxQuant", 6, false),

    /**
     * peptide of combined scores for multi peptide identification algorithms, index: 19
     */
    COMBPEP("CombPep", 19, false),

    /**
     * APIVASE Peptide Pair for Sequest, index: 20
     */
    APIVASE("APIVASE", 20, true),

    /**
     * APIVASE Peptide Pair for Sequest, index: 21
     */
    APIVASE_SEQUEST("APIVASE_SEQUEST", 21, true),

    /**
     * APIVASE Peptide Pair for Mascot, index: 22
     */
    APIVASE_MASCOT("APIVASE_Mascot", 22, true),

    /**
     * APIVASE Peptide Pair for XTandem, index: 23
     */
    APIVASE_XTANDEM("APIVASE_XTandem", 23, true),

    /**
     * APIVASE Peptide Pair for OMSSA, index: 24
     */
    APIVASE_OMSSA("APIVASE_OMSSA", 24, true),

    /**
     * APIVASE Peptide Pair for OMSSA, index: 25
     */
    APIVASE_INSPECT("APIVASE_Inspect", 25, true),

    /**
     * APIVASE Peptide Pair for OMSSA, index: 25
     */
    APIVASE_CRUX("APIVASE_Crux", 26, true);

    private int type;
    private String algorithm_name;
    private boolean isPeptidePair;

    /**
     * @param type
     * @param algorithm_name
     */
    private PeptideType(String algorithm_name, int type, boolean isPeptidePair)
    {
        this.type = type;
        this.algorithm_name = algorithm_name;
        this.isPeptidePair = isPeptidePair;
    }

    /**
     * Get the peptide type by the index
     *
     * <li>SEQUEST("SEQUEST", 0),
     *
     * <li>MASCOT("MASCOT", 1),
     *
     * <li>XTANDEM("X!TANDEM", 2),
     *
     * <li>OMSSA("OMSSA", 3),
     *
     * <li>INSPECT("Inspect", 4),
     *
     * <li>CRUX("Crux", 5),
     *
     * <li>CombPep("CombPep", 19);
     *
     * <li>APIVASE("APIVASE", 20);
     *
     * <li>APIVASE_SEQUEST("APIVASE", 21);
     *
     * <li>APIVASE_MASCOT("APIVASE", 22);
     *
     * <li>APIVASE_XTANDEM("APIVASE", 23);
     *
     * <li>APIVASE_OMSSA("APIVASE", 24);
     *
     * <li>APIVASE_INSPECT("APIVASE", 25);
     *
     * <li>APIVASE_CRUX("APIVASE", 26);
     *
     * @param index
     * @return
     */
    public static PeptideType getTypebyIndex(int index)
    {
        PeptideType[] types = PeptideType.values();
        for (PeptideType type : types) {
            //The name and index must both equals
            if (type.getType() == index) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unkown type for index: " + index);
    }

    /**
     * Get the peptide type by the name of the algorithm
     *
     * <li>SEQUEST("SEQUEST", 0),
     *
     * <li>MASCOT("MASCOT", 1),
     *
     * <li>XTANDEM("X!TANDEM", 2),
     *
     * <li>OMSSA("OMSSA", 3),
     *
     * <li>APIVASE("CombPep", 19);
     *
     * <li>APIVASE("APIVASE", 20);
     *
     * @param name : e.g. X!TANDEM by NOT XTANDEM
     * @return
     */
    public static PeptideType getTypebyName(String name)
    {
        PeptideType[] types = PeptideType.values();
        for (PeptideType type : types) {
            //The name and index must both equals
            if (type.getAlgorithm_name().equals(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unkown type for name: " + name);
    }

    /**
     * Parse the formatted string.
     *
     * @param formatStr The string should be "Algorithm_name, type"
     * @return
     * @throws IllegalArgumentException
     */
    public static PeptideType typeOfFormat(String formatStr)
            throws IllegalArgumentException
    {
        if (formatStr == null || formatStr.length() == 0)
            throw new IllegalArgumentException(
                    "The format string is null or with length of 0");

        String[] cells = formatStr.split(",");
        if (cells.length != 2)
            throw new IllegalArgumentException(
                    "The legal format string is \"algorithm_name, type\", current: "
                            + formatStr);

        String name = cells[0].trim();
        int idx = Integer.parseInt(cells[1].trim());

        PeptideType[] types = PeptideType.values();
        for (PeptideType type : types) {
            // The name and index must both equals
            if (type.getAlgorithm_name().equals(name) && type.getType() == idx) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unkown type for format string: "
                + formatStr);
    }

    /**
     * @return the type of the algorithm used for database search
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return the name of algorithm used for peptide search
     */
    public String getAlgorithm_name()
    {
        return algorithm_name;
    }

    /**
     * If the peptide of this type is a peptide pair (Currently, only APIVASE peptide is peptide pair)
     *
     * @return
     * @since 0.3
     */
    public boolean isPeptidePair()
    {
        return this.isPeptidePair;
    }

    /**
     * Algorithm_name+", "+type
     */
    @Override
    public String toString()
    {
        return this.algorithm_name + ", " + type;
    }
}
