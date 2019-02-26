/*
 ******************************************************************************
 * File:PeptideListViewerPanel2.java * * * Created on 2011-8-25
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.gui2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;

import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultDeltaMZCriteria;
import cn.ac.dicp.gp1809.util.beans.gui.SelectablePagedTable;

public class PeptideListViewerPanel2 extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private PeptideListPagedRowGetter2 getter;
    private IPeptideCriteria usedFilter;
    private ArrayList<IPeptideCriteria> modFilters;
    private JButton jButtonClose;
    private SelectablePagedTable selectablePagedTable0;
    private CriteriaPanel criteriaPanel0;
    private InfoPanel infoPanel0;
    private JLabel jLabelTitle;
    private File file;

    private static final String PREFERRED_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";

    public PeptideListViewerPanel2()
    {
        initComponents();
    }

    public PeptideListViewerPanel2(PeptideListPagedRowGetter2 getter)
    {
        this.getter = getter;
        initComponents();
    }

    private void initComponents()
    {
        setLayout(new GroupLayout());
        add(addJLabelTitle(), new Constraints(new Bilateral(0, 0, 1000, 1000), new Leading(0, 20, 10, 10)));
        add(getSelectablePagedTable0(), new Constraints(new Bilateral(0, 0, 1000, 1000), new Leading(20, 400, 10, 10)));
        add(getInfoPanel0(), new Constraints(new Leading(663, 635, 6, 6), new Leading(418, 350, 6, 6)));
        add(getCriteriaPanel0(), new Constraints(new Leading(0, 645, 6, 6), new Leading(418, 350, 6, 6)));
        setSize(1200, 800);
    }

    private JLabel addJLabelTitle()
    {
        if (jLabelTitle == null) {
            jLabelTitle = new JLabel("    " + getter.getFileName());
            Font myFont = new Font("Serif", Font.BOLD, 12);
            jLabelTitle.setFont(myFont);
        }
        return jLabelTitle;
    }

    private InfoPanel getInfoPanel0()
    {

        if (infoPanel0 == null) {
            if (getter == null)
                infoPanel0 = new InfoPanel();
            else
                infoPanel0 = new InfoPanel(getter);
//			infoPanel0.setBorder(BorderFactory.createTitledBorder(null, "Peptide information", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog",
//					Font.BOLD, 15), new Color(51, 51, 51)));
            infoPanel0.add(getJButtonClose(), new Constraints(new Leading(466, 10, 10), new Leading(218, 10, 10)));
        }

        infoPanel0.addSelectListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {

                if (modFilters != null) {
                    removeFilters(modFilters);
                }
                modFilters = infoPanel0.getModFilters();
                addFilters(modFilters);
                refreshFilterInfo();
            }
        });

        infoPanel0.addDisposeListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {

                if (modFilters != null) {
                    removeFilters(modFilters);
                }
                refreshFilterInfo();
                modFilters = null;
            }

        });

        return infoPanel0;
    }

    private CriteriaPanel getCriteriaPanel0()
    {

        if (criteriaPanel0 == null) {

            if (this.getter == null) {
                criteriaPanel0 = new CriteriaPanel();
            } else {
                criteriaPanel0 = new CriteriaPanel(getter);
            }

            criteriaPanel0.addFilterActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // TODO Auto-generated method stub

                    if (usedFilter != null)
                        removeFilter(usedFilter);

                    usedFilter = criteriaPanel0.getCriteria();
                    addFilter(usedFilter);
                    setUsedFilter(usedFilter);
                    refreshFilterInfo();
                }

            });

            criteriaPanel0.removeFilterActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // TODO Auto-generated method stub
                    reFilter();
                    refreshFilterInfo();
                }

            });

            criteriaPanel0.addMzFilterActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // TODO Auto-generated method stub

                    DefaultDeltaMZCriteria mzfilter = criteriaPanel0.getMzFilter();

                    if (criteriaPanel0.getJCheckBoxDeltaMz().isSelected()) {
                        addFilter(mzfilter);
                    } else {
                        removeFilter(mzfilter);
                    }
                    refreshFilterInfo();
                }

            });

            criteriaPanel0.addSetPvalueActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    // TODO Auto-generated method stub

                    float pvalue = criteriaPanel0.getMascotPvalue();
                    getter.reNewP4Mascot(pvalue);
                    selectablePagedTable0.reLoad(getter);
                    refreshFilterInfo();
                }

            });

//			criteriaPanel0.setBorder(BorderFactory.createTitledBorder(null, "Filter", TitledBorder.LEADING, TitledBorder.CENTER, new Font("SansSerif",
//					Font.BOLD, 15), new Color(59, 59, 59)));
        }
        return criteriaPanel0;
    }

    public void setUsedFilter(IPeptideCriteria filter)
    {
        this.usedFilter = filter;
    }

    private SelectablePagedTable getSelectablePagedTable0()
    {
        if (selectablePagedTable0 == null) {
            selectablePagedTable0 = new SelectablePagedTable(getter);
            selectablePagedTable0.setMinimumSize(new Dimension(300, 200));
            selectablePagedTable0.setPreferredSize(new Dimension(300, 200));
        }
        return selectablePagedTable0;
    }

    public JButton getJButtonClose()
    {
        if (jButtonClose == null) {
            jButtonClose = new JButton();
            jButtonClose.setText("Close");
        }
        return jButtonClose;
    }

    public void addFilter(IPeptideCriteria filter)
    {
        if (this.getter != null) {
            this.getter.addFilter(filter);
        }
    }

    public void addFilters(ArrayList<IPeptideCriteria> filters)
    {
        if (this.getter != null) {
            this.getter.addFilters(filters);
        }
    }

    public void removeFilter(IPeptideCriteria filter)
    {
        if (this.getter != null) {
            this.getter.removeFilter(filter);
        }
    }

    public void removeFilters(ArrayList<IPeptideCriteria> filters)
    {
        if (this.getter != null) {
            this.getter.removeFilters(filters);
        }
    }

    public void reFilter()
    {
        if (this.getter != null) {
            if (usedFilter != null) {
                removeFilter(usedFilter);
            }
        }
    }

    public void refreshFilterInfo()
    {
        if (this.getter != null) {
            this.selectablePagedTable0.refresh();
            this.getInfoPanel0().setStatPanel(getter.getPeptideStatInfo());
            this.getInfoPanel0().setPPMPanel(getter.getPPMDataset());
            this.updateUI();
        }
    }

    protected PeptideListPagedRowGetter2 getPepGetter()
    {
        return this.getter;
    }

    public String getName()
    {
        return "Peptide List";
    }

    protected File getFile()
    {
        return file;
    }

    protected void setFile(File file)
    {
        this.file = file;
    }

    protected int getFileNum()
    {
        return this.getter.getFileNum();
    }

    public void dispose()
    {
        this.getter = null;
        System.gc();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        // TODO Auto-generated method stub
        Object obj = e.getSource();
/*		
		if(obj == this.getCriteriaPanel0().getJButtonSetPvalue()){
			float pvalue = criteriaPanel0.getMascotPvalue();
			getter.reNewP4Mascot(pvalue);
//			selectablePagedTable0.refresh();
			selectablePagedTable0.reLoad(getter);
			selectablePagedTable0.refresh();
			selectablePagedTable0.updateUI();
			updateUI();
		}
*/
    }

}
