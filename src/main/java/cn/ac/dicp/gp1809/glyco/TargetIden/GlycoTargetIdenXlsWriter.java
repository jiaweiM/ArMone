/* 
 ******************************************************************************
 * File: GlycoTargetIdenXlsWriter.java * * * Created on 2013-5-28
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.TargetIden;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import cn.ac.dicp.gp1809.glyco.Iden.GlycoIdenXMLReader;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * @author ck
 * 
 * @version 2013-5-28, 9:58:52
 */
public class GlycoTargetIdenXlsWriter {

	private ExcelWriter writer;
	private ExcelFormat ef;
	private DecimalFormat df4 = DecimalFormats.DF0_4;

	public GlycoTargetIdenXlsWriter(String file) throws IOException,
			RowsExceededException, WriteException {
		this.writer = new ExcelWriter(file);
		this.ef = ExcelFormat.normalFormat;

		this.addTitle();
	}

	/**
	 * @param out
	 * @throws IOException
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public GlycoTargetIdenXlsWriter(File file) throws IOException,
			RowsExceededException, WriteException {
		// TODO Auto-generated constructor stub
		this.writer = new ExcelWriter(file);
		this.ef = ExcelFormat.normalFormat;

		this.addTitle();
	}

	/**
	 * @throws WriteException
	 * @throws RowsExceededException
	 * 
	 */
	private void addTitle() throws RowsExceededException, WriteException {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("Scannum\t");
		sb.append("Charge\t");
		sb.append("Mz\t");
		sb.append("Retention Time\t");
		sb.append("Rank\t");
		sb.append("Score\t");
		sb.append("Glyco Mass\t");
		sb.append("Peptide Exp Mass\t");
		sb.append("Peptide Calc Mass\t");
		sb.append("Delta Mass\t");
		sb.append("Peptide sequence\t");
		sb.append("IUPAC Name\t");
		sb.append("Type\t");

		this.writer.addTitle(sb.toString(), 0, ef);
	}

	public void write(NGlycoSSM[] ssms) throws RowsExceededException,
			WriteException {

		for (int i = 0; i < ssms.length; i++) {

			this.write(ssms[i]);
		}

	}

	public void write(NGlycoSSM ssm) throws RowsExceededException,
			WriteException {

		StringBuilder sb = new StringBuilder();
		sb.append(ssm.getScanNum()).append("\t");
		sb.append(ssm.getPreCharge()).append("\t");
		sb.append(ssm.getPreMz()).append("\t");
		sb.append(ssm.getRT()).append("\t");
		sb.append(ssm.getRank()).append("\t");
		sb.append(ssm.getScore()).append("\t");
		sb.append(ssm.getGlycoMass()).append("\t");
		sb.append(df4.format(ssm.getPepMass())).append("\t");
		sb.append(ssm.getPepMassExperiment()).append("\t");
		sb.append(df4.format(ssm.getPepMassExperiment()-ssm.getPepMass())).append("\t");
		sb.append(ssm.getSequence()).append("\t");
		sb.append(ssm.getName()).append("\t");
		sb.append(ssm.getGlycoTree().getType());

		this.writer.addContent(sb.toString(), 0, ef);
	}

	public void close() throws WriteException, IOException {
		this.writer.close();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		GlycoTargetIdenXlsWriter writer = new GlycoTargetIdenXlsWriter(
				"D:\\sun_glyco\\20130529\\130528_TRAF_FA_glyco_HCD_30%_10ms_10MSMS.proteinmatch2.xls");
		GlycoIdenXMLReader reader = new GlycoIdenXMLReader(
				"D:\\sun_glyco\\20130529\\130528_TRAF_FA_glyco_HCD_30%_10ms_10MSMS.proteinmatch.pxml");

		NGlycoSSM[] ssms = reader.getAllMatches();
		for (int i = 0; i < ssms.length; i++) {
//			if (ssms[i].getRank() == 1)
				writer.write(ssms[i]);
		}

		writer.close();
	}

}
