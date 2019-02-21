/* 
 ******************************************************************************
 * File: DtaWriterFactory.java * * * Created on 03-05-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import java.io.File;

import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaWriter;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 15:40:29
 */
public class DtaWriterFactory {
	
	
	/**
	 * Create Dta batch reader.
	 * 
	 * @param type
	 * @param input
	 * @return
	 */
	public IBatchDtaWriter createDtaReader(DtaType type, String output){
		return createDtaReader(type, new File(output));
	}
	
	
	/**
	 * Create Dta batch reader.
	 * 
	 * @param type
	 * @param input
	 * @return
	 */
	public IBatchDtaWriter createDtaReader(DtaType type, File output){
		String name = output.getAbsolutePath();
		switch(type){
		case DTA :
			return new SequestBatchDtaWriter(name);
		case MGF:
			throw new NullPointerException("Not desinged for mgf");
		case MS2:
			throw new NullPointerException("Not desinged for ms2");
		}
		
		throw new IllegalArgumentException("Unknown dta format.");
	}
}
