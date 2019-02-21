/*
 ******************************************************************************
 * File: ReverseFasta.java * * * Created on 03-20-2008
 *
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy;

import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaReader;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaWriter;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;

import java.io.*;

/**
 * Create a decoy database by reverse all the aminoacid sequences.
 *
 * @author Xinning
 * @version 0.2.1, 04-27-2010, 13:28:22
 */
public class ReverseFasta implements DecoyFasta
{
    /**
     * The platform dependent line separator. For Windows, this value is "\r\n" while for linux this value will be
     * "\n".
     */
    public final static String lineSeparator = System.lineSeparator();

    private String inputfilename, outputfilename;
    private DBDecoy reverser;

    public ReverseFasta(String inputfilename, String outputfilename,
            DBDecoy reverser)
    {
        this.inputfilename = inputfilename;
        this.outputfilename = outputfilename;
        this.reverser = reverser;
    }

    private static String reverse(StringBuilder stringbuilder)
    {
        StringBuilder reversed = stringbuilder.reverse();
        int len = reversed.length();
        int line = len / 80;
        int mode = len % 80;

        //with sufficent length
        StringBuilder sb = new StringBuilder(len + line * 2 + 2);

        int start = 0;
        for (int i = 0; i < line; i++) {
            int end = start + 80;
            sb.append(reversed.substring(start, end)).append(lineSeparator);
            start = end;
        }

        if (mode != 0) {//if it has just 80n aa,'\n' is not needed
            sb.append(reversed.substring(start)).append(lineSeparator);
        }

        return sb.toString();
    }

    private static String reverse(String seq)
    {
        StringBuilder reversed = (new StringBuilder(seq)).reverse();
        int len = reversed.length();
        int line = len / 80;
        int mode = len % 80;

        //with sufficent length
        StringBuilder sb = new StringBuilder(len + line * 2 + 2);

        int start = 0;
        for (int i = 0; i < line; i++) {
            int end = start + 80;
            sb.append(reversed.substring(start, end)).append(lineSeparator);
            start = end;
        }

        if (mode != 0) {//if it has just 80n aa,'\n' is not needed
            sb.append(reversed.substring(start)).append(lineSeparator);
        }

        return sb.toString();
    }

    public static void makeReverseSwissprot(String in, String out) throws IOException
    {
        FastaWriter writer = new FastaWriter(out);
        FastaReader fr = new FastaReader(in);
        ProteinSequence ps = null;
        while ((ps = fr.nextSequence()) != null) {
            String ref = ps.getReference();
            String seq = ps.getUniqueSequence();
            String revseq = (new StringBuilder(seq)).reverse().toString();
            String[] cs = ref.split("\\|");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cs.length; i++) {
                if (i != 1) {
                    sb.append(cs[i]);
                    sb.append("|");
                } else {
                    sb.append("REV_");
                    sb.append(cs[i]);
                    sb.append("|");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            writer.write(sb.toString(), revseq);
        }
        fr.close();
        writer.close();
    }

    public static void main(String[] args) throws IOException
    {

		/*String in = "F:\\DataBase\\yeast_20141010\\uniprot-yeast.fasta";
		String out = "F:\\DataBase\\yeast_20141010\\final.uniprot-yeast.fasta";
		DBDecoy rev = new DBDecoy();
		ReverseFasta fasta = new ReverseFasta(in, out, rev);

		fasta.makeFinal();*/

        ReverseFasta.makeReverseSwissprot("F:\\DataBase\\yeast_20141010\\uniprot-yeast.fasta",
                "F:\\DataBase\\yeast_20141010\\rev.uniprot-yeast.fasta");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cn.ac.dicp.gp1809.proteome.proteometools.dbdecoy.DecoyFasta#makeDecoy()
     */
    public void makeDecoy() throws IOException
    {
        StringBuilder sequence;
        sequence = new StringBuilder();
        String s;
        int tcount = 0;

        File reversefile = new File(outputfilename);
        if (reversefile.exists())
            reversefile.delete();

        BufferUtil bf = new BufferUtil(inputfilename);
        PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(
                reversefile)));
        String ref = "";

        reverser.jProgressBar.setMaximum(bf.length());
        String test_decoy = ">" + DECOY_SYM;
        while ((s = bf.readLine()) != null) {
            if (s.length() != 0) {
                if (s.charAt(0) == '>') {

                    /*
                     * Test decoy
                     */
                    if (s.startsWith(test_decoy)) {
                        bw.close();
                        throw new IllegalArgumentException(
                                "This database is already a composite database contains \""
                                        + DECOY_SYM + "\" entries");
                    }

                    if (sequence.length() != 0) {
                        ++tcount;
//						bw.println(">" + DECOY_SYM +"_"+ tcount+ "_Protein Decoy_Protein_"+tcount);
//						bw.println(">" + DECOY_SYM +"_"+ ref);
//						bw.println(">" + ref.substring(0, 3) + DECOY_SYM +"_"+ ref.substring(3, ref.length()));
                        bw.println(">" + DECOY_SYM + "_" + ref);
                        bw.print(reverse(sequence));
                        sequence.delete(0, sequence.length());
                    }

                    ref = s.substring(1, s.length());

                } else {
                    sequence.append(s);
                }
            }

            reverser.jProgressBar.setValue(bf.position());
        }

        //The last one
        if (sequence.length() != 0) {
            ++tcount;
//			bw.println(">" + DECOY_SYM +"_"+ tcount+ "_Protein");
            bw.println(">" + DECOY_SYM + "_" + ref);
            bw.print(reverse(sequence));
        }

        reverser.jProgressBar.setValue(bf.length());
        reverser.jLabel.setText("Processed " + tcount
                + " proteins, begain to combine ...");

//		reverser.jProgressBar.setString("Finish!");

        bw.close();
        bf.close();
    }

    public void makeFinal() throws IOException
    {

        StringBuilder sequence;
        sequence = new StringBuilder();
        String s;
        int tcount = 0;

        File reversefile = new File(outputfilename + ".temp");
        if (reversefile.exists())
            reversefile.delete();

        BufferUtil bf = new BufferUtil(inputfilename);
        PrintWriter bw = new PrintWriter(new BufferedWriter(new FileWriter(
                reversefile)));

        PrintWriter pw = new PrintWriter(outputfilename);

        String ref = "";

        reverser.jProgressBar.setMaximum(bf.length());
        String test_decoy = ">" + DECOY_SYM;
        while ((s = bf.readLine()) != null) {
            if (s.length() != 0) {

                pw.write(s + "\n");

                if (s.charAt(0) == '>') {

                    /*
                     * Test decoy
                     */
                    if (s.startsWith(test_decoy)) {
                        bw.close();
                        throw new IllegalArgumentException(
                                "This database is already a composite database contains \""
                                        + DECOY_SYM + "\" entries");
                    }

                    if (sequence.length() != 0) {
                        ++tcount;
//						bw.println(">" + DECOY_SYM +"_"+ tcount+ "_Protein Decoy_Protein_"+tcount);
//						bw.println(">" + DECOY_SYM +"_"+ ref);
                        bw.println(">" + ref.substring(0, ref.indexOf(" ")) + DECOY_SYM + ref.substring(ref.indexOf(" "), ref.length()));
                        bw.print(reverse(sequence));
                        sequence.delete(0, sequence.length());
                    }

                    ref = s.substring(1, s.length());

                } else {
                    sequence.append(s);
                }
            }

            reverser.jProgressBar.setValue(bf.position());
        }

        //The last one
        if (sequence.length() != 0) {
            ++tcount;
//			bw.println(">" + DECOY_SYM +"_"+ tcount+ "_Protein");
//			bw.println(">" + DECOY_SYM +"_"+ ref);
            bw.println(">" + ref.substring(0, ref.indexOf(" ")) + DECOY_SYM + ref.substring(ref.indexOf(" "), ref.length()));
            bw.print(reverse(sequence));
        }

        reverser.jProgressBar.setValue(bf.length());
        reverser.jLabel.setText("Processed " + tcount
                + " proteins, begain to combine ...");

        reverser.jProgressBar.setString("Finish!");

        bw.close();
        bf.close();


    }

}