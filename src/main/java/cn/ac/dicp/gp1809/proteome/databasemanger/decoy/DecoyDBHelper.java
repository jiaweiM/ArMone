/*
 ******************************************************************************
 * File: DecoyDBHelper.java * * * Created on 05-05-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger.decoy;

/**
 * The helper of target decoy database strategy
 *
 * @author Xinning
 * @version 0.1, 05-05-2010, 13:50:11
 */
public class DecoyDBHelper
{
    /**
     * The symbol of a decoy protein. This symbol is the start of the protein reference. Commonly this symbol is "REV";
     */
    public final static String DEFAULT_DECOY_SYM = "REV";

    /**
     * The currently used decoy symbol. If this value is changed, {@link #isTarget(String)}, {@link #isDecoy(String)}
     * will be influenced. So take care of this
     */
    public static String decoy_sym = DEFAULT_DECOY_SYM;

    /**
     * Justify whether a protein reference is a target protein. <b>Case sensitive</b>
     *
     * @param ref
     * @return true if the protein is a decoy protein.
     */
    public static boolean isTarget(String ref)
    {
        return isTarget(ref, decoy_sym, true);
    }

    /**
     * Justify whether a protein reference is a target protein. The symbol should be the start of the protein reference,
     * e.g. REV_IPI:IPIxxxxxx. <b>Case sensitive</b> Otherwise, please use {@link #isTarget(String, String, boolean)}
     *
     * @param ref
     * @param decoy_prefix the prefix of the decoy
     * @return true if the protein is a decoy protein.
     * @see {@link #isDecoy(String, String)}
     */
    public static boolean isTarget(String ref, String decoy_sym)
    {
        return isTarget(ref, decoy_sym, true);
    }

    /**
     * Justify whether a protein reference is a target protein. The symbol may be the start of the protein reference or
     * in the middle of the protein reference. <b>Case sensitive</b>
     *
     * @param ref
     * @param decoy_prefix the prefix of the decoy
     * @param isStart      if the decoy symbol is the start of the protein reference
     * @return true if the protein is a decoy protein.
     * @see #isDecoy(String, String, boolean)
     */
    public static boolean isTarget(String ref, String decoy_sym,
            boolean isStart)
    {
        return !isDecoy(ref, decoy_sym, isStart);
    }

    /**
     * Justify whether a protein reference is a decoy protein. <b>Case sensitive</b>
     *
     * @param ref
     * @return true if the protein is a decoy protein.
     */
    public static final boolean isDecoy(String ref)
    {
        return isDecoy(ref, decoy_sym);
    }

    /**
     * Justify whether a protein reference is a decoy protein. The symbol should be the start of the protein reference,
     * e.g. REV_IPI:IPIxxxxxx. <b>Case sensitive</b> Otherwise, please use {@link #isDecoy(String, String, boolean)}
     *
     * @param ref
     * @param decoy_prefix the prefix of the decoy
     * @return true if the protein is a decoy protein.
     */
    public static final boolean isDecoy(String ref, String decoy_sym)
    {
        return isDecoy(ref, decoy_sym, true);
    }

    /**
     * Justify whether a protein reference is a decoy protein. The symbol may be the start of the protein reference or
     * in the middle of the protein reference. <b>Case sensitive</b>
     *
     * @param ref
     * @param decoy_prefix the prefix of the decoy
     * @param isStart      if the decoy symbol is the start of the protein reference
     * @return true if the protein is a decoy protein.
     */
    public static final boolean isDecoy(String ref, String decoy_sym, boolean isStart)
    {
        if (ref == null || ref.length() == 0)
            throw new NullPointerException(
                    "The protein name for justify must not be null.");

        if (isStart)
            return ref.startsWith(decoy_sym);
        else
            return ref.contains(decoy_sym);
    }
}
