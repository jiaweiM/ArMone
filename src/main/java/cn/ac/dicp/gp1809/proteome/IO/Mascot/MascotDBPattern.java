/*
 ******************************************************************************
 * File: MascotDBPattern.java * * * Created on 03-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.util.regex.Pattern;

/**
 * Mascot often first parse the fasta database using specific regular expression patterns which can be defined in
 * database maintenance option, so that the protein names and aminoacid sequence can be accessed using the accession of
 * protein reference. This is a mascot db pattern.
 *
 * @author Xinning
 * @version 0.1.1, 04-01-2010, 22:37:46
 */
public class MascotDBPattern
{
    private String mascotPattern;
    private Pattern pattern;

    /**
     * Construct a pattern for mascot
     *
     * @param mascotPattern
     */
    public MascotDBPattern(String mascotPattern)
    {
        this.mascotPattern = mascotPattern;
        this.pattern = complie(mascotPattern);
    }

    /**
     * Compile the regx string into regular expression pattern.
     *
     * @param regx
     * @return
     */
    private static Pattern complie(String regex)
    {
        if (regex == null || regex.length() == 0) {
            throw new NullPointerException(
                    "The regular expression string for access number "
                            + "generation must not be null.");
        }

        // Remove the parentheses
        if (regex.charAt(0) == '\"' && regex.charAt(regex.length() - 1) == '\"') {
            regex = regex.substring(1, regex.length() - 1);
        }

        /*
         * Remove the first > for fasta database match because the full name of
         * protein contains no >
         */
        if (regex.charAt(0) == '>')
            regex = regex.substring(1);

        regex = regex.replaceAll("\\\\\\(", "(").replaceAll("\\\\\\)", ")");
//		regex = regex.replaceAll("\\\\\\|", "|");

        return Pattern.compile(regex);
    }

    /**
     * The pattern string defined in mascot
     *
     * @return the mascotPattern
     */
    public String getMascotPattern()
    {
        return mascotPattern;
    }

    @Override
    public String toString()
    {
        return this.mascotPattern;
    }

    /**
     * The java regular expression pattern
     *
     * @return the pattern
     */
    public Pattern getPattern()
    {
        return pattern;
    }
}
