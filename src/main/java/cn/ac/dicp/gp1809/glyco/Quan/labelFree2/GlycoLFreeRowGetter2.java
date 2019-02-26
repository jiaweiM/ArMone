/*
 ******************************************************************************
 * File: GlycoLFreeRowGetter2.java * * * Created on 2013-7-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.labelFree2;

import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author ck
 * @version 2013-7-2, 9:19:43
 */
public class GlycoLFreeRowGetter2 extends AbstractPagedRowGettor<GlycoPepObject2>
{
    private GlycoLFFeasXMLReader2 reader;

    protected String[] titles;
    protected int[] indexes;
    protected boolean[] selects;
    protected Class<?>[] classes;
    protected boolean editable[];

    protected int startIdxCurtPage;
    protected int[] backIndexes;

    public GlycoLFreeRowGetter2(GlycoLFFeasXMLReader2 reader)
    {
        this(reader, null);
    }

    public GlycoLFreeRowGetter2(GlycoLFFeasXMLReader2 reader, int[] usedIndxes)
    {
        this.reader = reader;
        this.setTitle();
        int num = reader.getPairNum();

        if (usedIndxes != null)
            this.indexes = usedIndxes;
        else {
            this.indexes = new int[num];
            for (int i = 0; i < num; i++) {
                this.indexes[i] = i;
            }
        }
        this.backIndexes = this.indexes;
        this.selects = new boolean[num];
        Arrays.fill(this.selects, true);

        this.classes = new Class<?>[this.getColumnCount()];
        this.classes[0] = Boolean.class;
        Arrays.fill(this.classes, 1, this.getColumnCount(), String.class);

        this.editable = new boolean[this.getColumnCount()];
        this.editable[0] = true;
    }

    public void setTitle()
    {
        ArrayList<String> list = new ArrayList<>();

        list.add("Selected");
        list.add("Pep Scannum");
        list.add("Sequence");
        list.add("Reference");
        list.add("Glycan scannum");
        list.add("Name");
        list.add("Pep mass");
        list.add("Glycan mass");
        list.add("Score");
        list.add("Rank");
        list.add("RT");

        this.titles = list.toArray(new String[list.size()]);
    }

    @Override
    protected int move2Page(int pageIdx)
    {
        int max = this.getMaxRecordsperPage();
        this.startIdxCurtPage = pageIdx * max;
        int end = (pageIdx + 1) * max;

        if (end > this.getRowCount())
            end = this.getRowCount();

        return end - this.startIdxCurtPage;
    }

    /**
     * Select all the displayed pairs
     */
    public void selectAllDisplayedPairs()
    {
        for (int idx : this.indexes) {
            this.selects[idx] = true;
        }
    }

    /**
     * DisSelect all the displayed pairs
     */
    public void disSelectAllDisplayedPairs()
    {
        for (int idx : this.indexes) {
            this.selects[idx] = false;
        }
    }

    @Override
    public Class<?>[] getColumnClasses()
    {
        return this.classes;
    }

    public boolean[] isColumnEditable()
    {
        return this.editable;
    }

    @Override
    public int getColumnCount()
    {
        return this.titles.length;
    }

    @Override
    public String[] getColumnNames()
    {
        return this.titles;
    }

    @Override
    public GlycoPepObject2 getRow(int idx)
    {
        int index = this.indexes[idx + this.startIdxCurtPage];

        return reader.getMatchObject(index, selects);
    }

    @Override
    public int getRowCount()
    {
        return this.indexes.length;
    }

    @Override
    public GlycoPepObject2[] getRows(int[] idxs)
    {
        GlycoPepObject2[] objs = new GlycoPepObject2[idxs.length];
        for (int i = 0; i < objs.length; i++) {
            objs[i] = this.getRow(idxs[i]);
        }

        return objs;
    }

    /**
     * @return
     */
    public String getFileName()
    {
        return reader.getFileName();
    }

    public File getParentFile()
    {
        return reader.getParentFile();
    }

    public ProteinNameAccesser getAccesser()
    {
        return reader.getProNameAccesser();
    }

    public IGlycoPeptide[] getAllGlycoPeptides()
    {
        return reader.getAllGlycoPeptides();
    }

    public NGlycoSSM[] getMatchedGlycoSpectra()
    {
        return reader.getMatchedGlycoSpectra();
    }

    public NGlycoSSM[] getUnmatchedGlycoSpectra()
    {
        return reader.getUnmatchedGlycoSpectra();
    }

    public double[] getBestEstimate()
    {
        return reader.getBestEstimate();
    }
}
