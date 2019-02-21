/* 
 ******************************************************************************
 * File: SequestBatchDtaWriter.java * * * Created on 03-30-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.DtaWritingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.util.IKnownFormatScanName;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;

/**
 * The batch dta writer for sequest dta files
 * 
 * @author Xinning
 * @version 0.1, 03-30-2009, 21:08:47
 */
public class SequestBatchDtaWriter implements IBatchDtaWriter {

	private File output;

	private SequestDtawriter writer;

	public SequestBatchDtaWriter(String outputdir) {
		this(new File(outputdir));
	}
	
	public SequestBatchDtaWriter(File file){
		output = file;
		if(!file.exists()){
			file.mkdirs();
		}
		this.writer = new SequestDtawriter();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DtaType getDtaType() {
		return DtaType.DTA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(IScanDta dtafile) throws DtaWritingException {
		IScanName scanname = dtafile.getScanName();

		if (!(scanname instanceof IKnownFormatScanName)) {
			throw new IllegalArgumentException(
			        "To write the dta files, the base name, scan number and charge state must known.");
		}

		SequestScanName ssname = new SequestScanName(scanname.getBaseName(),
		        scanname.getScanNumBeg(), scanname.getScanNumEnd(), scanname
		                .getCharge(), "dta");

		this.writer.write(dtafile, new File(this.output, ssname.getScanName())
		        .getAbsolutePath());
	}

}
