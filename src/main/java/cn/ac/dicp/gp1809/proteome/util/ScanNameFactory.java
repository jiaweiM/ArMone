/* 
 ******************************************************************************
 * File: ScanNameFactory.java * * * Created on 11-14-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.util;

/**
 * Factory to parse the different formatted scan name into IScanName instance.
 * 
 * @author Xinning
 * @version 0.1.2, 05-02-2010, 19:32:02
 */
public class ScanNameFactory {

	/**
	 * Parse the scan name and generate the informations: scan number begin,
	 * scan number end, base name and so on. If a unknown formatted scan number
	 * is input, an instance of UnknownFormatScanName will be returned; and you
	 * can not get useful informations.
	 * 
	 * @param scanname
	 * @return
	 */
	public static IScanName parseName(String scanname) {
		
		if(scanname==null)
			throw new NullPointerException("The scanname is null.");
		
		scanname = scanname.trim();
		
		if(TriTofMgfScanName.isFormat(scanname)){
//			System.out.println("5600peakview");
			return new TriTofMgfScanName(scanname);
		}
		
		if(QTofMgfScanName.isFormat(scanname)){
//			System.out.println("qtof");
			return new QTofMgfScanName(scanname);
		}
		
		if(FormattedScanName.isFormat(scanname)){
//			System.out.println("format");
			return new FormattedScanName(scanname);
		}

		if(XTandemMzxmlScanName.isFormat(scanname)){
//			System.out.println("tandem");
			return new XTandemMzxmlScanName(scanname);
		}
		
		if(OMSSAMzxmlScanName.isFormat(scanname)){
//			System.out.println("omssa");
			return new OMSSAMzxmlScanName(scanname);
		}
		
		if(MaxQuantScanName.isFormat(scanname)){
//			System.out.println("max");
			return new MaxQuantScanName(scanname);
		}
		
		if(PDScanName.isFormat(scanname)){
//			System.out.println("pd");
			return new PDScanName(scanname);
		}
		
		if(ProteoScanName.isFormat(scanname)){
//			System.out.println("proteo");
			return new ProteoScanName(scanname);
		}
		
		if(ProteoScanName2.isFormat(scanname)){
//			System.out.println("proteo");
			return new ProteoScanName2(scanname);
		}
		
		if(SequestScanName.isFormat(scanname)){
//			System.out.println("sequest");
			return new SequestScanName(scanname);
		}
		
		if(SimpleScanName.isFormat(scanname)){
//			System.out.println("simple");
			return new SimpleScanName(scanname);
		}

//		System.out.println("unkonwn");
		return new UnknownFormatScanName(scanname);
		
	}

}
