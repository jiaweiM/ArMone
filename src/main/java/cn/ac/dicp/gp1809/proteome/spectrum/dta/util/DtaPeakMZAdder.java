/*
 ******************************************************************************
 * File: DtaPeakMZAdder.java * * * Created on 12-29-2007
 *
 * Copyright (c) 2010 Xinning Jiang (vext@163.com)
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.spectrum.dta.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.sequest.SequestScanDta;


/**
 * After generating dta files from raw by bioworks, we can use this class
 * to change the peak mz values. This is used for the creation
 * of a totally fake data set so that all of the peptide identifications
 * from this falsified data set are incorrect.
 * 
 * @author Xinning
 * @version 0.1.1, 05-25-2010, 16:08:31
 */
public class DtaPeakMZAdder {
	
	private double add;
	
	public DtaPeakMZAdder(double addmz){
		this.add = addmz;
	}
	
	
	public void process(File indir, File outdir) throws IOException, DtaFileParsingException{
		SequestBatchDtaReader reader = new SequestBatchDtaReader(indir);
		
		if(!outdir.exists())
			outdir.mkdirs();
		
		SequestScanDta dta;
		while((dta = reader.getNextDta(true))!=null){
			IMS2PeakList peaklist = dta.getPeakList();
			IMS2PeakList newpeaklist = new MS2PeakList();
			newpeaklist.setPrecursePeak(peaklist.getPrecursePeak());
			
			IPeak[] peaks = peaklist.getPeakArray();
			for(int i=0;i<peaks.length;i++){
				newpeaklist.add(new Peak(peaks[i].getMz()+add,peaks[i].getIntensity()));
			}
			
			DtaWriter.writeToFile(new File(outdir,dta.getFile().getName()), newpeaklist);
		}
	}
	
	private static String usage(){
		String usage = "DtaPrecursorAdder -[b|s] inputdir outputdir addmz\r\n" +
				"\t Option: -b batch process (the input dir is a dir containing dirs of dta files)" +
				"\t         -s single process (the input dir is a dir containing dta files)";
		return usage;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws DtaFileParsingException 
	 */
	public static void main(String[] args) throws IOException, DtaFileParsingException {
		if(args.length!=4){
			System.out.println(usage());
		}
		else{
			String option = args[0];
			if(option.equals("-b")){
				double addmz = Double.parseDouble(args[3]);
				DtaPeakMZAdder adder = new DtaPeakMZAdder(addmz);
				File[] dirs = new File(args[1]).listFiles(new FileFilter(){
					public boolean accept(File pathname) {
						if(pathname.isDirectory())
							return true;
						return false;
					}
				});
				File out = new File(args[2]);
				for(int i=0;i<dirs.length;i++){
					adder.process(dirs[i], new File(out,dirs[i].getName()));
				}
			}
			else if(option.equals("-s")){
				double addmz = Double.parseDouble(args[3]);
				DtaPeakMZAdder adder = new DtaPeakMZAdder(addmz);
				adder.process(new File(args[1]), new File(args[2]));
			}
			else{
				System.out.println(usage());
			}
		}
	}

}
