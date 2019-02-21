/* 
 ******************************************************************************
 * File:MgfPeakListGetter.java * * * Created on 2009-11-19
 *
 * Copyright (c) 2009 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta;

import java.io.File;
import java.io.FileNotFoundException;

import cn.ac.dicp.gp1809.proteome.spectrum.IPeakListGetter;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.TempFilePeakListGettor;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.mgf.MgfReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ms2.MS2DtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;

/**
 * @author ck
 *
 * @version 2009-11-19, 18:58:49
 */
public class DtaPeakListGetter implements IPeakListGetter{
	
	private TempFilePeakListGettor getter;
	private IBatchDtaReader batchReader;
	
	public DtaPeakListGetter(String file, String typeName) throws DtaFileParsingException, FileNotFoundException{
		this(new File(file), typeName);
	}
	
	public DtaPeakListGetter(File file, String typeName) throws DtaFileParsingException, FileNotFoundException{
		
		DtaType type = DtaType.forTypeName(typeName);
		getter = new TempFilePeakListGettor();
		
		if(type==DtaType.MGF){
			createPeakList(new MgfReader(file));
		}
		
		if(type==DtaType.DTA){
			createPeakList(new SequestBatchDtaReader(file));
		}
		
		if(type==DtaType.MS2){
			createPeakList(new MS2DtaReader(file));
		}
	}
	
	public void createPeakList(IBatchDtaReader Reader) throws DtaFileParsingException, FileNotFoundException{
		this.batchReader = Reader;
		ScanDta dta;
		while ((dta = (ScanDta) batchReader.getNextDta(true)) != null) {
	//		System.out.println(dta);
			getter.addPeakList(2, dta.getScanNumberBeg(), dta.getPeakList());
		}
	}
	
	public MS2PeakList getPeakList(int scan_num){
		return (MS2PeakList) getter.getPeakList(scan_num);
	}
	
	public void dispose(){
		getter.dispose();
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws DtaFileParsingException 
	 */
	public static void main(String[] args) throws DtaFileParsingException, FileNotFoundException {
		// TODO Auto-generated method stub
		DtaPeakListGetter getter = new DtaPeakListGetter("E:\\Data\\wy" +
				"\\Phos_mouse","dta");
		System.out.println(getter.getPeakList(8023));
		getter.dispose();
	}


}
