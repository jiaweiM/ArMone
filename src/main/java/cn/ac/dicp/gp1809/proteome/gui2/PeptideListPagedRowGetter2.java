/*
 ******************************************************************************
 * File:PeptideListPagedRowGetter2.java * * * Created on 2011-9-2
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.*;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.PeptideUtil;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.gui.PeptideListInfo;
import cn.ac.dicp.gp1809.proteome.gui.PeptideRowObject;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatInfo;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatInfo.PeptideCountInfo;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;
import cn.ac.dicp.gp1809.util.beans.gui.AbstractPagedRowGettor;
import com.csvreader.CsvWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author ck
 * @version 2011-9-2, 10:15:14
 */
public class PeptideListPagedRowGetter2 extends
        AbstractPagedRowGettor<PeptideRowObject>
{

    /**
     * Contain the filters
     */
    private HashSet<IPeptideCriteria> filters = new HashSet<>();

    private PeptideListAccesser accesser;
    private String[] titles;
    private Class<?>[] classes;
    private boolean editable[];
    private int fileNum;

    /**
     * The backup indexes before the set of filters
     */
    private final int[] backIndexes;

    /**
     * whether the peptide selected. Use the index in the accesser to check
     * whether the peptide is selected
     */
    private boolean[] selects;

    /**
     * The sequences. Use the index in the accesser to get the sequence
     */
    private String[] sequeces;

    /**
     * Whether the peptide is target peptide. Use the index in the accesser to get.
     */
    private boolean[] targets;

    /**
     * The indexes of the used peptides in the peptide list.
     */
    private int[] indexes;

    private IPeptide[] peps;

    /**
     * The start index in current page
     */
    private int startIdxCurtPage;

    public PeptideListPagedRowGetter2()
    {
        this.backIndexes = null;
    }

    public PeptideListPagedRowGetter2(String pplfile)
            throws FileDamageException, IOException
    {
        this(new PeptideListAccesser(pplfile));
    }

    public PeptideListPagedRowGetter2(File pplfile)
            throws FileDamageException, IOException
    {
        this(new PeptideListAccesser(pplfile));
    }

    public PeptideListPagedRowGetter2(File[] fils)
            throws FileDamageException, IOException
    {
        this(new PeptideListMutilAccesser(fils));
    }

    public PeptideListPagedRowGetter2(PeptideListAccesser accesser)
    {
        this(accesser, null);
    }

    /**
     * Only the used peptides
     *
     * @param pplfile
     * @param usedPepIndxes The used indexes of the peptides
     * @throws FileDamageException
     * @throws IOException
     */
    public PeptideListPagedRowGetter2(String pplfile, int[] usedPepIndxes)
            throws FileDamageException, IOException
    {
        this(new PeptideListAccesser(pplfile), usedPepIndxes);
    }

    /**
     * Only the used peptides
     *
     * @param accesser
     * @param usedIndexes the used indexes of the peptides
     */
    public PeptideListPagedRowGetter2(PeptideListAccesser accesser,
                                      int[] usedPepIndxes)
    {
        this(accesser, usedPepIndxes, null);
    }

    /**
     * Only the used peptides
     *
     * @param accesser
     * @param usedIndexes the used indexes of the peptides
     * @param selected    the select information for each of the peptide in the peptide
     *                    list accesser. The length of the selects must equal to the
     *                    number of peptides in the accesser (NOT the used indexes!)
     */
    public PeptideListPagedRowGetter2(PeptideListAccesser accesser,
                                      int[] usedPepIndxes, boolean selected[])
    {

        this.accesser = accesser;
        this.fileNum = accesser.getFileNum();

        this.titles = this.accesser.getPeptideFormat().getTitle();
        //If this peptide selected
        this.titles[0] = "Selected";

        if (usedPepIndxes != null) {
            this.indexes = usedPepIndxes;
            this.peps = new IPeptide[usedPepIndxes.length];
            for (int i = 0; i < usedPepIndxes.length; i++) {
                peps[i] = accesser.getPeptide(usedPepIndxes[i]);
            }
        } else {
            int num = accesser.getNumberofPeptides();
            this.indexes = new int[num];
            this.peps = new IPeptide[num];

            for (int i = 0; i < num; i++) {
                this.indexes[i] = i;
                this.peps[i] = accesser.getPeptide(i);
            }
        }

        this.backIndexes = this.indexes;

        if (selected == null) {
            this.selects = new boolean[this.accesser.getNumberofPeptides()];
            Arrays.fill(this.selects, true);
        } else {
            if (selected.length != this.accesser.getNumberofPeptides()) {
                throw new IllegalArgumentException(
                        "The length of the selects doesn't equal to the number "
                                + "of peptides in the peptide list file");
            }

            this.selects = selected;
        }

        this.classes = new Class<?>[this.getColumnCount()];
        this.classes[0] = Boolean.class;
        Arrays.fill(this.classes, 1, this.getColumnCount(), String.class);

        this.editable = new boolean[this.getColumnCount()];
        this.editable[0] = true;
    }

    /**
     * Construct from the original peptide paged row getter. These two getter
     * use the common getter.
     *
     * @param accesser
     * @param usedIndexes the used indexes of the peptides
     * @param selected    the select information for each of the peptide in the peptide
     *                    list accesser. The length of the selects must equal to the
     *                    number of peptides in the accesser (NOT the used indexes!)
     */
    public PeptideListPagedRowGetter2(PeptideListPagedRowGetter2 getter,
                                      int[] usedPepIndxes, boolean selected[])
    {
        this(getter.accesser, usedPepIndxes, selected);
    }

    /**
     * Parse the peptide information
     *
     * @throws PeptideParsingException
     */
    @Deprecated
    public PeptideListInfo getPeptideInfo()
    {

        PeptideListInfo info = new PeptideListInfo();
        if (this.indexes != null && this.indexes.length > 0) {
            int total = 0;

            int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
            int target = 0, decoy = 0;
            for (int index : indexes) {

                if (!this.selects[index])
                    continue;

                total++;
                IPeptide pep = this.accesser.getPeptide(index);
                short charge = pep.getCharge();
                switch (charge) {
                    case 0:
                        System.err.println("Charge state of 0, skip counting.");
                        break;
                    case 1:
                        c1++;
                        break;
                    case 2:
                        c2++;
                        break;
                    case 3:
                        c3++;
                        break;
                    default:
                        c4++;
                        break;
                }

                if (pep.isTP())
                    target++;
                else
                    decoy++;
            }

            float fdr = decoy * 2f / total;
            if (fdr > 1)
                fdr = 1;

            info.setPep_total(total);
            info.setPep1(c1);
            info.setPep2(c2);
            info.setPep3(c3);
            info.setPep3plus(c4);
            info.setTarget(target);
            info.setDecoy(decoy);
            info.setFdr(fdr);
        }

        return info;
    }

    /**
     * Parse the peptide information
     *
     * @throws PeptideParsingException
     */
    public PeptideStatInfo getPeptideStatInfo()
    {
        PeptideCountInfo[] infos = new PeptideCountInfo[5];
        double totalSIn = 0;

        if (this.indexes != null && this.indexes.length > 0) {

            int c1 = 0, c2 = 0, c3 = 0, c4 = 0;
            int c1d = 0, c2d = 0, c3d = 0, c4d = 0;

            for (int index : indexes) {

                if (!this.selects[index])
                    continue;

//				IPeptide pep = this.accesser.getPeptide(index);
                IPeptide pep = peps[index];
                double fragInten = pep.getFragInten();

                totalSIn += fragInten;

                short charge = pep.getCharge();
                switch (charge) {
                    case 0:
                        System.err.println("Charge state of 0, skip counting.");
                        break;
                    case 1:
                        c1++;
                        if (!pep.isTP())
                            c1d++;
                        break;
                    case 2:
                        c2++;
                        if (!pep.isTP())
                            c2d++;
                        break;
                    case 3:
                        c3++;
                        if (!pep.isTP())
                            c3d++;
                        break;
                    default:
                        c4++;
                        if (!pep.isTP())
                            c4d++;
                        break;
                }
            }

            int total = c1 + c2 + c3 + c4;
            int totalD = c1d + c2d + c3d + c4d;

            infos[0] = new PeptideCountInfo("z=1", c1 - c1d, c1d);
            infos[1] = new PeptideCountInfo("z=2", c2 - c2d, c2d);
            infos[2] = new PeptideCountInfo("z=3", c3 - c3d, c3d);
            infos[3] = new PeptideCountInfo("z>=4", c4 - c4d, c4d);
            infos[4] = new PeptideCountInfo("total", total - totalD, totalD);
        }

        return new PeptideStatInfo(infos, totalSIn);
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
    protected int move2Page(int pageIdx) throws IndexOutOfBoundsException
    {
        int max = this.getMaxRecordsperPage();
        this.startIdxCurtPage = pageIdx * max;
        int end = (pageIdx + 1) * max;

        if (end > this.getRowCount())
            end = this.getRowCount();

        return end - this.startIdxCurtPage;
    }


    @Override
    public PeptideRowObject getRow(int idx)
    {
        int index = this.indexes[idx + this.startIdxCurtPage];
//		IPeptide pep = this.accesser.getPeptide(index);
        IPeptide pep = peps[index];
        IMS2PeakList[] peaklists = this.accesser.getPeakLists(index);
        return new PeptideRowObject(pep, peaklists, index, this.selects);
    }

    /**
     * Get the row and the spectra from the peptide list
     *
     * @param idx
     * @return
     */
    public PeptideRowObject getRowandSpectra(int idx)
    {
        int index = this.indexes[idx + this.startIdxCurtPage];
//		IPeptide pep = this.accesser.getPeptide(index);
        IPeptide pep = peps[index];
        IMS2PeakList[] peaklists = this.accesser.getPeakLists(index);

        return new PeptideRowObject(pep, peaklists, index, this.selects);
    }

    @Override
    public int getRowCount()
    {
        return this.indexes.length;
    }


    @Override
    public PeptideRowObject[] getRows(int[] idxs)
    {

        PeptideRowObject[] objs = new PeptideRowObject[idxs.length];

        for (int i = 0; i < idxs.length; i++) {
            objs[i] = this.getRow(idxs[i]);
        }

        return objs;
    }

    /**
     * The type of the peptides
     *
     * @return
     */
    public PeptideType getPeptideType()
    {
        return this.accesser.getPeptideType();
    }

    /**
     * The search parameter
     */
    public ISearchParameter getSearchParameter()
    {
        return this.accesser.getSearchParameter();
    }

    /**
     * Select all the displayed peptides
     */
    public void selectAllDisplayedPeptides()
    {
        for (int idx : this.indexes) {
            this.selects[idx] = true;
        }
    }

    /**
     * DisSelect all the displayed peptides
     */
    public void disSelectAllDisplayedPeptides()
    {
        for (int idx : this.indexes) {
            this.selects[idx] = false;
        }
    }

    /**
     * The sub set getter of all selected peptides or deselected peptides
     *
     * @param selected
     * @return
     */
    public PeptideListPagedRowGetter2 subSet(boolean selected)
    {
        IntArrayList list = new IntArrayList();

        for (int index : this.indexes) {
            boolean se = this.selects[index];

            if (se & selected) {
                list.add(index);
            }
        }

        return new PeptideListPagedRowGetter2(accesser, list.toArray(),
                this.selects);
    }

    @Override
    public Class<?>[] getColumnClasses()
    {
        return this.classes;
    }

    /**
     * If editable for each of the column. Default, can not be edit.
     *
     * @return
     */
    @Override
    public boolean[] isColumnEditable()
    {
        return this.editable;
    }

    /**
     * Set the filter
     *
     * @param filter
     */
    public void addFilter(IPeptideCriteria filter)
    {

        PeptideType filterType = filter.getPeptideType();

        if (filterType != PeptideType.GENERIC && filter.getPeptideType() != this.accesser.getPeptideType()) {

            throw new IllegalArgumentException(
                    "Unsuitable peptide filter. The peptide type is "
                            + this.accesser.getPeptideType()
                            + ", the criteria type is "
                            + filter.getPeptideType());
        }

        if (!this.filters.contains(filter)) {
            IntArrayList list = new IntArrayList();
            for (int index : this.indexes) {
                if (filter.filter(peps[index])) {
                    list.add(index);
                }
            }

            this.indexes = list.toArray();

            this.filters.add(filter);
        }
    }

    /**
     * Add a filter list, a peptide passed one of them will be selected.
     *
     * @param filter
     */
    public void addFilters(ArrayList<IPeptideCriteria> filters)
    {

        for (int i = 0; i < filters.size(); i++) {
            IPeptideCriteria filter = filters.get(i);
            PeptideType filterType = filters.get(i).getPeptideType();
            if (filterType != PeptideType.GENERIC && filter.getPeptideType() != this.accesser.getPeptideType()) {

                throw new IllegalArgumentException(
                        "Unsuitable peptide filter. The peptide type is "
                                + this.accesser.getPeptideType()
                                + ", the criteria type is "
                                + filter.getPeptideType());
            }
        }

        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < filters.size(); i++) {

            IPeptideCriteria filter = filters.get(i);
            if (!this.filters.contains(filter)) {
                for (int index : this.indexes) {
                    if (filter.filter(peps[index])) {
                        set.add(index);
                    }
                }
                this.filters.add(filter);
            }
        }

        Integer[] idlist = set.toArray(new Integer[set.size()]);
        Arrays.sort(idlist);

        this.indexes = new int[idlist.length];
        for (int i = 0; i < idlist.length; i++) {
            indexes[i] = idlist[i];
        }
    }

    /**
     * Refilter
     */
    public void reFilter()
    {
        if (this.filters.size() > 0) {
            IPeptideCriteria[] cirteria = this.filters
                    .toArray(new IPeptideCriteria[this.filters.size()]);

            IntArrayList list = new IntArrayList();
            for (int index : this.backIndexes) {
                boolean isTrue = true;
                IPeptide pep = peps[index];
                for (IPeptideCriteria filter : cirteria) {
                    if (!filter.filter(pep)) {
                        isTrue = false;
                        break;
                    }
                }

                if (isTrue)
                    list.add(index);

            }
            this.indexes = list.toArray();
        } else {
            this.indexes = this.backIndexes.clone();
        }
    }

    /**
     * Remove the filter instance. The filter should be the same instance as has
     * been added.
     *
     * @param filter
     * @return if removed
     */
    public boolean removeFilter(IPeptideCriteria filter)
    {

        boolean removed = this.filters.remove(filter);

        if (removed) {
            this.reFilter();
        }

        return removed;
    }

    public boolean removeFilters(ArrayList<IPeptideCriteria> filters)
    {
        boolean removed = false;
        for (int i = 0; i < filters.size(); i++) {
            removed = this.filters.remove(filters.get(i));
        }
        if (removed) {
            this.reFilter();
        }
        return removed;
    }

    public void reNewP4Mascot(float pvalue)
    {
        for (int index : this.backIndexes) {
            MascotPeptide pep = (MascotPeptide) peps[index];
            pep.reCal4PValue(pvalue);
        }
    }

    public HashSet<IPeptideCriteria> getFilters()
    {
        return this.filters;
    }

    public PPMDataset getPPMDataset()
    {

        ArrayList<Double> target = new ArrayList<Double>();
        ArrayList<Double> decoy = new ArrayList<Double>();

        for (int index : this.indexes) {
            IPeptide pep = peps[index];
            double ppm = pep.getDeltaMZppm();
            if (pep.isTP()) {
                target.add(ppm);
            } else {
                decoy.add(ppm);
            }
        }

        PPMDataset ppmdata = new PPMDataset(target, decoy);
        return ppmdata;
    }

    /**
     * Export the displayed peptides to new ppl file
     *
     * @param pplpath
     * @throws FileNotFoundException
     * @throws ProWriterException
     */
    public void exportDisplayToPpl(String pplpath) throws FileNotFoundException,
            ProWriterException
    {
        PeptideListWriter writer = new PeptideListWriter(pplpath, this.accesser
                .getPeptideFormat(), this.accesser.getSearchParameter(),
                this.accesser.getDecoyJudger(), this.accesser.getProNameAccesser());

        for (int idx : this.indexes) {
            if (this.selects[idx])
                writer.write(peps[idx], this.accesser
                        .getPeakLists(idx));
        }

        writer.close();
    }

    /**
     * Export the displayed peptides to csv file
     *
     * @param pplpath
     * @throws IOException
     */
    public void exportDisplayToCsv(String pplpath) throws IOException
    {
        CsvWriter writer = new CsvWriter(pplpath);

        String[] peptitle = this.accesser.getPeptideFormat().getTitle();
        String[] title = new String[peptitle.length + 3];
        System.arraycopy(peptitle, 0, title, 3, peptitle.length);
        title[0] = "Index";
        title[1] = "Sequence";
        title[2] = "Count";
        writer.writeRecord(title);
        HashMap<String, ArrayList<IPeptide>> pepMap = new HashMap<String, ArrayList<IPeptide>>();
        for (int idx : this.indexes) {
            if (this.selects[idx]) {

                IPeptide pep = peps[idx];
                String seq = PeptideUtil.getSequence(pep.getSequence());
                if (pepMap.containsKey(seq)) {
                    pepMap.get(seq).add(pep);
                } else {
                    ArrayList<IPeptide> peplist = new ArrayList<IPeptide>();
                    peplist.add(pep);
                    pepMap.put(seq, peplist);
                }
            }
        }
        int num = 1;
        Iterator<String> it = pepMap.keySet().iterator();
        while (it.hasNext()) {
            String seq = it.next();
            ArrayList<IPeptide> peplist = pepMap.get(seq);

            IPeptide p0 = peplist.get(0);
            String[] peparray0 = StringUtil.split(p0.toString(), '\t');
            String[] toWrite = new String[peparray0.length + 3];
            toWrite[0] = String.valueOf(num);
            toWrite[1] = seq;
            toWrite[2] = String.valueOf(peplist.size());
            System.arraycopy(peparray0, 0, toWrite, 3, peparray0.length);
            writer.writeRecord(toWrite);

            for (int i = 1; i < peplist.size(); i++) {
                IPeptide pi = peplist.get(i);
                String[] peparrayi = StringUtil.split(pi.toString(), '\t');
                toWrite = new String[peparrayi.length + 3];
                System.arraycopy(peparrayi, 0, toWrite, 3, peparrayi.length);
                writer.writeRecord(toWrite);
            }
            num++;
        }
        writer.close();
    }

    /**
     * The information of peptide list file.
     *
     * @return
     */
    public String getPplInfo()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[File name]: " + accesser.getFileName()).append("\n");
        sb.append("[Search algorithm]: ").append(accesser.getPeptideType().getAlgorithm_name()).append("\n");
        sb.append("[Search parameters]: \n").append(accesser.getSearchParameter().getStaticInfo().getModfiedAADescription(true))
                .append(accesser.getSearchParameter().getVariableInfo().getModficationDescription());
        return sb.toString();
    }

    public String getFileName()
    {
        return accesser.getFileName();
    }

    /**
     * The reader for the selected peptides in the peptide list viewer
     */
    public PeptideRowReader getSelectedPeptideReader()
    {
        PeptideRowReader pReader = new PeptideRowReader(accesser, this.indexes, this.selects);
        pReader.setPeptideStatInfo(this.getPeptideStatInfo());
        return pReader;
    }

    /**
     * See use the back indexes
     *
     * @return
     */
    public PeptideRowReader getSelectedAllPeptideReader()
    {
        PeptideRowReader pReader = new PeptideRowReader(accesser, this.backIndexes, this.selects);
        pReader.setPeptideStatInfo(this.getPeptideStatInfo());
        return pReader;
    }

    public int getFileNum()
    {
        return this.fileNum;
    }

    /**
     * Close and finished reading
     */
    public void closeList()
    {
        this.accesser.close();
    }

    /**
     * The reader for the selected peptide in the peptide list viewer
     *
     * @author Xinning
     * @version 0.1, 05-19-2009, 20:38:21
     */
    public static class PeptideRowReader implements IFilteredPeptideListReader
    {
        private PeptideListAccesser accesser;
        private int[] indexes;
        private boolean selects[];
        private int curtIdx = -1;
        private int num;
        private PeptideStatInfo pepInfo;

        private ProteinNameAccesser proNameAccesser;

        private PeptideRowReader(PeptideListAccesser accesser, int[] indexes,
                                 boolean[] selects)
        {
            this.accesser = accesser;
            this.selects = selects;
            this.indexes = indexes;
            this.num = this.indexes.length;
            this.proNameAccesser = accesser.getProNameAccesser();
        }

        public void setPeptideStatInfo(PeptideStatInfo pepInfo)
        {
            this.pepInfo = pepInfo;
        }

        public PeptideStatInfo getPeptideStatInfo()
        {
            return this.pepInfo;
        }

        public double getTotalSIn()
        {
            double SIns = this.pepInfo.getTotalSIn();
            return SIns;
        }

        @Override
        public IMS2PeakList[] getPeakLists()
        {
            return this.accesser.getPeakLists(this.indexes[this.curtIdx]);
        }

        @Override
        public void close()
        {

        }

        @Override
        public IPeptide getPeptide() throws PeptideParsingException
        {

            if (this.curtIdx + 1 >= num)
                return null;

            this.curtIdx++;

            int idx = this.indexes[this.curtIdx];

            if (this.selects[idx])
                return this.accesser.getPeptide(indexes[this.curtIdx]);
            else
                return this.getPeptide();
        }

        /**
         * Set the status of use for current reading peptide. You MUST use the
         * method getPeptide() first.
         */
        public void setUsed4CurtPeptide(boolean used)
        {
            selects[this.indexes[this.curtIdx]] = used;
        }

        /**
         * Get the row getter of the deselected rows in the reader, includes the
         * deselected ones when input.
         *
         * @return
         */
        public PeptideListPagedRowGetter2 getDeselectedSubset()
        {

            IntArrayList list = new IntArrayList();

            for (int idx : this.indexes) {
                if (!this.selects[idx]) {
                    list.add(idx);
                }
            }

            return new PeptideListPagedRowGetter2(accesser, list.toArray(),
                    this.selects);
        }

        @Override
        public IPeptideFormat<?> getPeptideFormat()
        {
            return this.accesser.getPeptideFormat();
        }

        @Override
        public PeptideType getPeptideType()
        {
            return this.accesser.getPeptideType();
        }

        @Override
        public ISearchParameter getSearchParameter()
        {
            return this.accesser.getSearchParameter();
        }

        @Override
        public int getTopN()
        {
            return 0;
        }

        @Override
        public void setPeptideFormat(IPeptideFormat<?> format)
        {
            throw new IllegalArgumentException("Peptide format cannot be set!");
        }

        @Override
        public void setTopN(int topn)
        {
            throw new IllegalArgumentException("TopN cannot be set!");
        }

        @Override
        public int getCurtPeptideIndex()
        {
            return this.curtIdx;
        }

        @Override
        public int getNumberofPeptides()
        {
            return num;
        }

        @Override
        public IPeptideCriteria<?> getCriteria()
        {
            return null;
        }

        public IPeptideListAccesser getAccesser()
        {
            // TODO Auto-generated method stub
            return this.accesser;
        }

        @Override
        public IDecoyReferenceJudger getDecoyJudger()
        {
            return this.accesser.getDecoyJudger();
        }

        @Override
        public void setDecoyJudger(IDecoyReferenceJudger judger)
        {
            throw new IllegalArgumentException("Cannot set");
        }

        @Override
        public void setReplace(HashMap<Character, Character> replaceMap)
        {

        }

        public void setProNameAccesser(ProteinNameAccesser proNameAccesser)
        {
            this.proNameAccesser = proNameAccesser;
        }

        @Override
        public ProteinNameAccesser getProNameAccesser()
        {
            // TODO Auto-generated method stub
            return proNameAccesser;
        }

    }

}
