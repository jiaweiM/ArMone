/* 
 ******************************************************************************
 * File: FilteredPeptideListReader.java * * * Created on 05-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.File;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.IPeptideCriteria;
import cn.ac.dicp.gp1809.proteome.gui.PeptideStatInfo;

/**
 * The filtered peptide list reader
 * 
 * @author Xinning
 * @version 0.1, 05-27-2009, 09:20:38
 */
public class FilteredPeptideListReader extends PeptideListReader implements
        IFilteredPeptideListReader {

	private ProteinNameAccesser proNameAccesser;
	
	private IPeptideCriteria criteria;
	
	/**
	 * @param listfile
	 * @throws FileDamageException
	 * @throws IOException
	 */
	public FilteredPeptideListReader(String listfile, IPeptideCriteria criteria)
	        throws FileDamageException, IOException {
		super(listfile);
		
		
		if(criteria.getPeptideType() != this.getPeptideType()) {
			throw new IllegalArgumentException("The criteria type is different from the type of peptides");
		}
		this.criteria = criteria;
	}

	/**
	 * @param file
	 * @throws FileDamageException
	 * @throws IOException
	 */
	public FilteredPeptideListReader(File file, IPeptideCriteria criteria) throws FileDamageException,
	        IOException {
		super(file);
		if(criteria.getPeptideType() != this.getPeptideType()) {
			throw new IllegalArgumentException("The criteria type is different from the type of peptides");
		}
		this.criteria = criteria;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader#getCriteria()
	 */
	@Override
	public IPeptideCriteria<?> getCriteria() {
		return this.criteria;
	}

	/* (non-Javadoc)
     * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader#getPeptide()
     */
    @Override
    public IPeptide getPeptide() {
	    IPeptide pep = super.getPeptide();
	    
	    if(pep == null) {
	    	return null;
	    }
	    
	    if(!this.criteria.filter(pep)) {
	    	return super.getPeptide();
	    }
	    
	    return pep;
    }

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader#getPeptideStatInfo()
	 */
	@Override
	public PeptideStatInfo getPeptideStatInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IFilteredPeptideListReader#setProNameAccesser(cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser)
	 */
	@Override
	public void setProNameAccesser(ProteinNameAccesser proNameAccesser) {
		// TODO Auto-generated method stub
		this.proNameAccesser = proNameAccesser;
	}
}
