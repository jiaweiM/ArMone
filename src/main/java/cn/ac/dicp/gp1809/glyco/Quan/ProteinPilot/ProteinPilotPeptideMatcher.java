/* 
 ******************************************************************************
 * File: ProteinPilotPeptideMatcher.java * * * Created on 2013-6-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.ProteinPilot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSpecStrucGetter;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 * 
 * @version 2013-6-6, 16:34:17
 */
public class ProteinPilotPeptideMatcher {

	private ProteinPilotPeptide[] peps;
	private NGlycoSSM[] ssms;
	private GlycoJudgeParameter parameter;
	
	private ProteinPilotGlycoPeptide[] gps;
	private NGlycoSSM[] notMatchedssms;

	public ProteinPilotPeptideMatcher(String peptidefile, String peakfile)
			throws IOException, XMLStreamException {

		ProteinPilotPeptideReader reader = new ProteinPilotPeptideReader(
				peptidefile);
		this.peps = reader.getNGlycoPeptides();

		NGlycoSpecStrucGetter getter = new NGlycoSpecStrucGetter(peakfile);
		this.ssms = getter.getGlycoSSMs();

		this.parameter = GlycoJudgeParameter.defaultParameter();
		this.match();
	}

	public ProteinPilotPeptideMatcher(String peptidefile, String peakfile,
			double scorethres, GlycoJudgeParameter parameter)
			throws IOException, XMLStreamException {

		ProteinPilotPeptideReader reader = new ProteinPilotPeptideReader(
				peptidefile, scorethres);
		this.peps = reader.getNGlycoPeptides();

		NGlycoSpecStrucGetter getter = new NGlycoSpecStrucGetter(peakfile,
				parameter);
		this.ssms = getter.getGlycoSSMs();

		this.parameter = parameter;
		this.match();
	}

	/**
	 * 
	 */
	private void match() {
		// TODO Auto-generated method stub

		ArrayList <ProteinPilotGlycoPeptide> gpslist = new ArrayList <ProteinPilotGlycoPeptide>();
		ArrayList <NGlycoSSM> nmlist = new ArrayList <NGlycoSSM>();
		
		double [] pepBackMws = new double[peps.length];
		for(int i=0;i<peps.length;i++){
			pepBackMws[i] = peps[i].getPeptideBackboneMw();
		}
		
		double ppm = parameter.getMzThresPPM();
		int ssmscannum = 0;
		int rank = 0;
		
		for(int i=0;i<ssms.length;i++){
			
			if(ssms[i].getScanNum()!=ssmscannum){
				ssmscannum = ssms[i].getScanNum();
				rank = 1;
			}
			
			double pepmass = ssms[i].getPepMassExperiment();
			int id = Arrays.binarySearch(pepBackMws, pepmass-1);
			if(id<0) id = -id-1;
			
			boolean match = false;
			for(int j=id;j<pepBackMws.length;j++){
				double deltaMz = pepmass-pepBackMws[j];
				double deltaMzPPM = Math.abs(pepmass-pepBackMws[j])*1E6/pepmass;
				if(deltaMzPPM<ppm){
					ProteinPilotGlycoPeptide gp = new ProteinPilotGlycoPeptide(peps[j], ssms[i]);
					gp.setRank(rank++);
					gp.setDeltaMz(deltaMz);
					gp.setDeltaMzPPM(deltaMzPPM);
					gpslist.add(gp);
					match = true;
				}
			}
			
			if(!match){
				nmlist.add(ssms[i]);
			}
		}
		
		this.gps = gpslist.toArray(new ProteinPilotGlycoPeptide[gpslist.size()]);
		this.notMatchedssms = nmlist.toArray(new NGlycoSSM[nmlist.size()]);
		System.out.println("ProteinPilotPeptideMatcher100\t"+peps.length+"\t"+ssms.length+"\t"+gpslist.size()+"\t"+notMatchedssms.length);
	}
	
	private void tempWriter(String output, int rank) throws IOException, RowsExceededException, WriteException{
		
		ExcelWriter writer = new ExcelWriter(output);
		ExcelFormat format = ExcelFormat.normalFormat;
		
		StringBuilder titlesb = new StringBuilder();
		titlesb.append("Glycopep scannum\t");
		titlesb.append("Rank\t");
		titlesb.append("Glycopep rt\t");
		titlesb.append("Precursor mz\t");
		titlesb.append("Precursor mw\t");
		titlesb.append("Precursor charge\t");
		titlesb.append("Theor glycan mw\t");
		titlesb.append("Calc peptide mw\t");
		titlesb.append("Glycopep score\t");
		titlesb.append("IUPAC Name\t");
		titlesb.append("Type\t");
		writer.addTitle(titlesb.toString(), 1, format);
		
		titlesb.append("Peptide scannum\t");
		titlesb.append("Sequence\t");
		titlesb.append("Peptide rt\t");
		titlesb.append("Theor peptide mw\t");
		titlesb.append("Delta mw\t");
		titlesb.append("Delta mw ppm\t");
		
		writer.addTitle(titlesb.toString(), 0, format);
		
		for(int i=0;i<gps.length;i++){
			
			if(gps[i].getRank()>rank)
				continue;
			
			StringBuilder sb = new StringBuilder();
			ProteinPilotPeptide peptide = gps[i].getPeptide();
			NGlycoSSM ssm = gps[i].getSsm();
			
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(gps[i].getRank()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMass()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");
			
			sb.append(peptide.getScannum()).append("\t");
			sb.append(peptide.getSequence()).append("\t");
			sb.append(peptide.getRt()).append("\t");
			sb.append(peptide.getPeptideBackboneMw()).append("\t");
			
			sb.append(gps[i].getDeltaMz()).append("\t");
			sb.append(gps[i].getDeltaMzPPM()).append("\t");
			
			writer.addContent(sb.toString(), 0, format);
		}
		
		for(int i=0;i<notMatchedssms.length;i++){
			
			NGlycoSSM ssm = notMatchedssms[i];
			if(ssm.getRank()>rank)
				continue;
			
			StringBuilder sb = new StringBuilder();
			sb.append(ssm.getScanNum()).append("\t");
			sb.append(ssm.getRank()).append("\t");
			sb.append(ssm.getRT()).append("\t");
			sb.append(ssm.getPreMz()).append("\t");
			sb.append(ssm.getPreMr()).append("\t");
			sb.append(ssm.getPreCharge()).append("\t");
			sb.append(ssm.getGlycoMass()).append("\t");
			sb.append(ssm.getPepMass()).append("\t");
			sb.append(ssm.getScore()).append("\t");
			sb.append(ssm.getGlycoTree().getIupacName()).append("\t");
			sb.append(ssm.getGlycoTree().getType()).append("\t");
			writer.addContent(sb.toString(), 1, format);
		}
		
		writer.close();
	}

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws IOException, XMLStreamException, RowsExceededException, WriteException {
		// TODO Auto-generated method stub

		long begin = System.currentTimeMillis();
		
		String peptidefile = "H:\\20130519_glyco\\iden\\AB\\2\\Rui_20130515_fetuin_HILIC_deglyco_HCD_noenzyme.proteinpilot_PeptideSummary.txt";
		String peakfile = "H:\\20130519_glyco\\HCD20130523\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms.mzXML";
		GlycoJudgeParameter parameter = new GlycoJudgeParameter(0.001f, 30f, 0.15f, 500, 0.3f, 60.0f, 3);
		
		ProteinPilotPeptideMatcher matcher = new ProteinPilotPeptideMatcher(peptidefile, peakfile, 95, parameter);
		matcher.tempWriter("H:\\20130519_glyco\\iden\\AB\\2\\Rui_20130515_fetuin_HILIC_HCD_30%_5ms_proteinpilot_noenzyme_rank1.xls", 1);
		
		long end = System.currentTimeMillis();
		System.out.println((end-begin)/60000.0+" min");
	}

}
