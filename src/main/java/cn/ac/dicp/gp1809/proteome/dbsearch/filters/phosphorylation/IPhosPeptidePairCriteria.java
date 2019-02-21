/* 
 ******************************************************************************
 * File: IPhosPeptidePairCriteria.java * * * Created on 04-29-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.dbsearch.filters.phosphorylation;

import cn.ac.dicp.gp1809.proteome.IO.proteome.phospeptides.IPhosPeptidePair;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;

/**
 * The phospeptide pair criteria
 * 
 * @author Xinning
 * @version 0.1, 04-29-2009, 20:06:40
 */
public interface IPhosPeptidePairCriteria<Pep extends IPhosPeptidePair> extends
        IPeptideCriteria<Pep> {

}
