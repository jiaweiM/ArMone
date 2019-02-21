/* 
 ******************************************************************************
 * File: IPeptideInfor.java * * * Created on 03-27-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.drawjf.pepinfo;

import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.NeutralLossInfo;

/**
 * The information of peptides for drawing onto the picture or tables near the
 * picture.
 * 
 * @author Xinning
 * @version 0.2, 04-15-2009, 14:10:32
 */
public interface IPeptideInfo {
	
	/**
	 * The peptide sequence
	 * 
	 * @return
	 */
	public String getSequence();
	
	/**
	 * The peptide
	 * 
	 * @return
	 */
	public IPeptide getPeptide();

	/**
	 * The charge state of the peptide identification
	 * 
	 * @return
	 */
	public short getCharge();

	/**
	 * The mz of the parent ions
	 * 
	 * @return
	 */
	public double getMZ();

	/**
	 * The theoretical ions for this peptide
	 * 
	 * @return
	 */
	public Ions getIons();

	/**
	 * Because the peptide often loss water or NH3 for the parent and produce
	 * relative high peaks. Or in neutral loss MS3 scan, the phosphate will
	 * always be lost for the phosphrylated peptides. Give the mass of thess
	 * ions if you want to label them in the spectra.
	 * <p>
	 * The value is the lost mass value (not mz),e.g. for H2O, this value is 18.
	 * The mz will be automatically calculated for the specific charge state.
	 * 
	 * @return
	 */
	public NeutralLossInfo[] getLosses();
}
