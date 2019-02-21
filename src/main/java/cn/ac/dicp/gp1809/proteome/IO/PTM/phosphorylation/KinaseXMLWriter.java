/*
 ******************************************************************************
 * File:KinaseXMLWriter.java * * * Created on 2009-12-24
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.PTM.phosphorylation;

import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author ck
 * @version 2009-12-24, 10:21:40
 */
public class KinaseXMLWriter
{

    private BufferUtil bufferUtil;
    private String catalogue;
    private HashSet<String> modSet = new HashSet<String>();
    private LinkedList<Kinase> kinaseSet = new LinkedList<Kinase>();

    private String output;
    private SAXReader reader;
    private Document document;
    private Element root;

    public KinaseXMLWriter(String input, String output) throws IOException, DocumentException
    {
        this.bufferUtil = new BufferUtil(input);
        this.output = output;
        this.preWrite();
//		System.out.println(bufferUtil.length());
    }

    public void getKinase()
    {

        String line = bufferUtil.readLine();
        line = bufferUtil.readLine();
        String pattern = "";
        String description = "";

        Kinase kinase = null;

        for (; line != null && line.trim() != ""; line = bufferUtil.readLine()) {

            System.out.println(line);
            if (line.indexOf(" ") > 4) {
                catalogue = line;
            } else {
                String kinaseInfo = line.substring(line.indexOf(" ") + 1);
                pattern = kinaseInfo.substring(0, kinaseInfo.indexOf(" "));
                description = kinaseInfo.substring(kinaseInfo.indexOf(" "), kinaseInfo.indexOf("(")).trim();

                if (modSet.contains(description)) {
                    kinase.addMotif(pattern);
                } else {
                    modSet.add(description);
                    kinase = new Kinase(catalogue, description);
                    kinase.addMotif(pattern);
                    kinaseSet.addLast(kinase);
                }

            }

        }
        System.out.println(modSet.size());
        System.out.println(kinaseSet.size());
    }

    public void preWrite() throws DocumentException, IOException
    {

        File file = new File(this.output);

        if (file.exists()) {
            reader = new SAXReader();
            document = reader.read(file);
            root = document.getRootElement();
        } else {

            document = DocumentHelper.createDocument();
            root = document.addElement("Motifs");
            XMLWriter output = new XMLWriter(new FileWriter(file));
            output.write(document);
            output.close();
        }
    }

    public void writeKinase(Kinase kinase) throws IOException
    {
        String catalogue = kinase.getCatalogue();
        String description = kinase.getDescription();
        HashSet<String> motifSet = kinase.getMotifSet();

        Element motif = root.addElement("motif");
        motif.addAttribute("description", description);
        motif.addAttribute("catalogue", catalogue);

        for (String m : motifSet) {
            Element pattern = motif.addElement("patterns");
            pattern.addAttribute("pattern", m);
        }

        XMLWriter w = new XMLWriter(new FileWriter(output));
        w.write(document);
        w.close();

    }

}
