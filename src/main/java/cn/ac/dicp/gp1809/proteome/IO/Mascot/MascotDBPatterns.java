/*
 ******************************************************************************
 * File: MascotDBPatterns.java * * * Created on 03-08-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * The mascot patterns for database parsing
 *
 * @author Xinning
 * @version 0.1, 03-08-2009, 19:40:59
 */
public class MascotDBPatterns
{
    /**
     * The default mascot pattern ini file
     */
    private static final String INI_FILE = "mascot_pattern.ini";

    /**
     * The patterns defined in Mascot
     */
    private static final String[] MPATTERNSTR = new String[]{
            ">owl[^ ]*|\\\\([^ ]*\\\\)",
            ">owl[^ ]*|[^ ]*[ ]\\\\(.*\\\\)",
            ">[A-Z][0-9];\\\\([^ ]*\\\\)",
            ">\\\\([^ ]*\\\\)",
            ">\\([^ ]*\\)",
            ">[^ ]* \\(.*\\)",
            ">\\(gi\\|[0-9]*\\)",
            ">[^ ]* \\(.*\\)",
            "\\*\\(.*\\)>",
            "\\*.*\\(>[A-Z][0-9];.*\\)",
            "\\(LOCUS .*\\)ORIGIN ",
            "\\(LOCUS .*\\)",
            ">\\([^ ]*\\)",
            ">[^ ]* \\(.*\\)",
            "<pre>\\(.*\\)</pre>",
            "^ID   \\([^ ]*\\)",
            "\\*.*\\(ID   [A-Z0-9]*_[A-Z0-9]* .*\\)",
            ">\\([^ ]*\\)",
            ">[^ ]* \\(.*\\)",
            ">[A-Z][0-9];\\([^ ]*\\)[ ]*",
            ">\\(.*\\)",
            ">IPI:\\([^| .]*\\)",
            "\\*.*\\(ID   IPI[0-9]* .*\\)",
            "\\(.*\\)",
            "\\*.*\\(ID   [-A-Z0-9_].*\\)",
            ">[^(]*.\\([^)]*\\)",
            "^AC   \\([^ ;]*\\)",
            "\\*.*\\(AC   [A-Z0-9]*;.*\\)",
            "^ID   \\([^ .]*\\)",
            "\\*.*\\(ID   IPI[0-9.]* .*\\)",
            ">UniRef100_\\([^ ]*\\)",
            ">..\\|\\([^|]*\\)",
            ">[^|]*|\\([^ ]*\\)",
            ">IPI:([^| .]*)",
            ">\\([^| ]*\\)",
            ">.* Tax_Id=9606 \\(.*\\)",
            ">..\\|[^|]*\\|\\([^ ]*\\)",
    };
    private LinkedList<MascotDBPattern> patterns;
    private String inifile = INI_FILE;

    public MascotDBPatterns()
    {
        patterns = new LinkedList<>();

        MascotDBPattern[] mps = null;

        try {
            mps = load(inifile);
        } catch (FileNotFoundException e) {
            System.err.println("INI file don't exist, load default patterns.");
            mps = loadDefault();
            try {
                writeDefault(inifile);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            mps = loadDefault();
            System.err.println("Parsing ini file error, load default.");
        }

        patterns.addAll(Arrays.asList(mps));
    }

    /**
     * Load the patterns from ini file in the hard drive
     *
     * @param inifile
     * @return
     * @throws IOException
     */
    private static MascotDBPattern[] load(String inifile) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(inifile));

        ArrayList<String> patterns = new ArrayList<String>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0)
                    patterns.add(line);
            }
        } finally {
            if (reader != null)
                reader.close();
        }

        MascotDBPattern[] mpatterns = new MascotDBPattern[patterns.size()];

        for (int i = 0; i < patterns.size(); i++) {
            mpatterns[i] = new MascotDBPattern(patterns.get(i));
        }

        return mpatterns;
    }

    /**
     * The solid default patterns
     *
     * @return
     */
    private static MascotDBPattern[] loadDefault()
    {
        MascotDBPattern[] mpatterns = new MascotDBPattern[MPATTERNSTR.length];
        for (int i = 0; i < MPATTERNSTR.length; i++) {
            mpatterns[i] = new MascotDBPattern(MPATTERNSTR[i]);
        }

        return mpatterns;
    }

    /**
     * Write the default patterns into the ini file
     *
     * @param inifile
     * @throws FileNotFoundException
     */
    private static void writeDefault(String inifile) throws FileNotFoundException
    {
        PrintWriter pw = new PrintWriter(inifile);

        for (String str : MPATTERNSTR) {
            pw.println(str);
        }

        pw.close();
    }

    /**
     * Add a pattern
     *
     * @param pattern
     */
    public void addPattern(MascotDBPattern pattern)
    {
        this.patterns.add(pattern);
    }

    /**
     * Get the patterns
     *
     * @return
     */
    public MascotDBPattern[] getPatterns()
    {
        return this.patterns.toArray(new MascotDBPattern[this.patterns.size()]);
    }
}
