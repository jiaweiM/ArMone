/*
 ******************************************************************************
 * File: DefaultDecoyRefJudger.java * * * Created on 05-20-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger.decoy;

/**
 * The decoy reference judger using the default decoy system.
 *
 * @author Xinning
 * @version 0.1, 05-20-2010, 11:41:25
 * @see {@link DecoyDBHelper}
 */
public class DefaultDecoyRefJudger implements IDecoyReferenceJudger
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String decoy_sym = DecoyDBHelper.DEFAULT_DECOY_SYM;
    private int len = decoy_sym.length();

    public DefaultDecoyRefJudger()
    {
    }

    @Override
    public boolean isDecoy(String ref)
    {
        if (ref == null || ref.length() == 0)
            throw new NullPointerException("The input reference is null.");

        return ref.startsWith(decoy_sym);
    }

    @Override
    public int endIndexof(String ref)
    {
        return ref.indexOf(decoy_sym) + len;
    }
}
