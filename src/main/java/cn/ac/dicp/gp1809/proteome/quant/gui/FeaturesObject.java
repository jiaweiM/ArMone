/*
 ******************************************************************************
 * File:LabelPairObject.java * * * Created on 2010-6-29
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.gui;

import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject;

/**
 * @author JiaweiMao
 * @version Sep 17, 2015, 10:18:47 AM
 */
public class FeaturesObject implements ITableRowObject
{
    private PeptidePair pair;
    private String refs;
    private String[] columns;
    private boolean[] selected;
    private int index;

    public FeaturesObject(PeptidePair pair, int index, boolean[] selected)
    {
        this.pair = pair;
        String[] ss = pair.getPairObjectString().split("\t");
        this.columns = new String[ss.length + 1];
        System.arraycopy(ss, 0, columns, 1, ss.length);
        this.index = index;
        this.selected = selected;
    }

    public PeptidePair getPeitdePair()
    {
        return pair;
    }

    public String getRefs()
    {
        return refs;
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject#getValueAt(int)
     */
    @Override
    public Object getValueAt(int colIdx)
    {
        // TODO Auto-generated method stub
        if (colIdx == 0) {
            return this.isSelected();
        }
        return columns[colIdx];
    }

    /* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.util.beans.gui.ITableRowObject#setValueAt(java.lang.Object, int)
     */
    @Override
    public void setValueAt(Object obj, int colIdx)
    {
        // TODO Auto-generated method stub
        if (colIdx == 0) {
            this.setSelected((Boolean) obj);
        }
    }

    public boolean isSelected()
    {
        return this.selected[this.index];
    }

    public void setSelected(boolean selected)
    {
        this.selected[this.index] = selected;
    }
}
