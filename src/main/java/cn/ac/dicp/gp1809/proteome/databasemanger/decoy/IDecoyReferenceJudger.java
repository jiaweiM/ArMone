/*
 ******************************************************************************
 * File: IDecoyReferenceJudger.java * * * Created on 05-20-2010
 *
 * Copyright (c) 2010 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 *
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger.decoy;

import java.io.Serializable;

/**
 * To justify whether a protein reference is a decoy protein
 *
 * @author Xinning
 * @version 0.1, 05-20-2010, 11:39:55
 */
public interface IDecoyReferenceJudger extends Serializable
{

    /**
     * If this protein a decoy protein
     *
     * @param ref protein accession
     * @return true if it is a decoy protein
     */
    boolean isDecoy(String ref);

    /**
     * The end index of the decoy symbol. For the default decoy system, e.g. REV_IPI:IPIxxxx, this value is 3.
     */
    int endIndexof(String ref);

}
