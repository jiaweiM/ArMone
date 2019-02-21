/* 
 ******************************************************************************
 * File: GlycoLabelMaxQuantTask.java * * * Created on 2013-8-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.glyco.GlycoJudgeParameter;
import cn.ac.dicp.gp1809.glyco.peptide.GlycoPeptide;
import cn.ac.dicp.gp1809.glyco.peptide.IGlycoPeptide;
import cn.ac.dicp.gp1809.glyco.structure.NGlycoSSM;
import cn.ac.dicp.gp1809.proteome.IO.MaxQuant.peptides.readers.MaxQuantSitePepReader;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.IMS2Scan;
import cn.ac.dicp.gp1809.proteome.spectrum.rawdata.MzXMLReader;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2013-8-26, 16:53:40
 */
public class GlycoLabelMaxQuantTask implements ITask {
	
	private MaxQuantSitePepReader reader;
	private NGlyStrucLabelGetter getter;
	private LabelType type;
	private GlycoLabelFeaturesXMLWriter writer;
	private NGlycoPepCriteria nGlyCri = new NGlycoPepCriteria(true);
	
	private HashMap<Integer, Double> rtmap;
	private AminoacidModification aam;
	
	private IPeptide curtPeptide;
	private int total;
	private int curt;
	private boolean integration = false;
	
	public GlycoLabelMaxQuantTask(String maxquant, String peak, String glycopeak, String result, GlycoJudgeParameter jpara, LabelType labeltype,
			String fasta, String pattern) 
			throws IOException, XMLStreamException, FastaDataBaseException{
		
		this.reader = new MaxQuantSitePepReader(maxquant, fasta, pattern);
		this.rtmap = new HashMap<Integer, Double>();
		
		MzXMLReader mzreader = new MzXMLReader(peak);
		IMS2Scan ms2scan = null;
		while((ms2scan=mzreader.getNextMS2Scan())!=null){
			this.rtmap.put(ms2scan.getScanNum(), ms2scan.getRTMinute());
		}
		mzreader.close();
		
		this.aam = new AminoacidModification();
		this.aam.addModification(ModSite.newInstance_aa('N'), '*', 0.984016, "Deamidated");
		
		this.getter = new NGlyStrucLabelGetter(glycopeak, jpara, labeltype, aam);
		
		this.writer = new GlycoLabelFeaturesXMLWriter(result, labeltype, false);
		this.writer.addModification(aam);
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#hasNext()
	 */
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		try {

			boolean has = (curtPeptide = this.reader.getPeptide()) != null;
			
			if (has) {
				return true;
			} else {
				if (!this.integration) {
					this.integration = true;
					return true;
				} else
					return false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub
		if (this.integration) {
			
			this.writer.addProNameInfo(reader.getProNameAccesser());
			
			getter.match();
			
			IGlycoPeptide [] peps = getter.getGlycoPeptides();
			for(int i=0;i<peps.length;i++){
				this.writer.addIdenPep(peps[i]);
			}
			
			NGlycoSSM [] ssms = getter.getGlycoSpectra();
			for(int i=0;i<ssms.length;i++){
				this.writer.addGlycoSpectra(ssms[i]);
			}
			
			GlycoPeptideLabelPair [] feas = getter.getFeatures();
			for(int i=0;i<feas.length;i++){
				this.writer.addFeatures(feas[i]);
			}
			
			this.writer.addBestEstimate(getter.getBestEstimate());

			try {
				writer.write();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			
			if(this.curtPeptide == null)
				throw new NullPointerException("Null peptide. No more peptide?");

			if(nGlyCri.filter(curtPeptide)){
				IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
				int scannum = gp.getScanNumBeg();
				double rt = this.rtmap.get(scannum);
				gp.setRetentionTime(rt);
				this.getter.addPeptide(gp, aam);
				this.curt++;
			}
		}
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#inDetermineable()
	 */
	@Override
	public boolean inDetermineable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	private static void test() throws FileDamageException, IOException, FastaDataBaseException, 
		XMLStreamException, DocumentException,
		RowsExceededException, WriteException {

		String txt = "H:\\NGlyan_Quan_20130812\\serum\\iden\\MaxQuant\\Deamidation (N)Sites_normal-2.txt";
		String peak = "H:\\NGlyan_Quan_20130812\\serum\\iden\\20130805_serum_di-labeling_Normal_CID_quantification-2.mzXML";
		String glycopeak = "H:\\NGlyan_Quan_20130812\\serum\\Glycan\\20130723_serum_di-labeling_Normal_HCD_N-glycan_quantification-2.mzXML";
		String pxml = "H:\\NGlyan_Quan_20130812\\serum\\iden\\MaxQuant\\20130723_serum_di-labeling_Normal_HCD_N-glycan_quantification-2.pxml";
		String out = "H:\\NGlyan_Quan_20130812\\serum\\iden\\MaxQuant\\20130723_serum_di-labeling_Normal_HCD_N-glycan_quantification-2.xls";
		String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta";
		String pattern = ">IPI:([^| .]*)";
		
		LabelType type = LabelType.Dimethyl;
		LabelInfo[][] linfo = new LabelInfo[][] {
				{ LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt },
				{ LabelInfo.Dimethyl_C13D3_K, LabelInfo.Dimethyl_C13D3_Nt } };

		linfo[0][0].setSymbol('@');
		linfo[0][1].setSymbol('@');
		linfo[1][0].setSymbol('^');
		linfo[1][1].setSymbol('^');
		type.setInfo(linfo);
		type.setUsed(new short[] { 1, 2 });

		GlycoLabelMaxQuantTask task = new GlycoLabelMaxQuantTask(txt, peak, glycopeak, pxml, 
				GlycoJudgeParameter.defaultParameter(), type, fasta, pattern);
		
		while (task.hasNext()) {
			task.processNext();
		}
		task.dispose();

		GlycoLabelFeaturesXMLReader reader2 = new GlycoLabelFeaturesXMLReader(
				pxml);
		IGlycoPeptide[] peps = reader2.getAllGlycoPeptides();
		// GlycoPeptideLabelPair[] pairs = reader2.getAllSelectedPairs();
		GlycoQuanResult[] results = reader2.getAllResult();
		NGlycoSSM[] matchedssms = reader2.getMatchedGlycoSpectra();
		NGlycoSSM[] unmatchedssms = reader2.getUnmatchedGlycoSpectra();

		double[] bestEstimate = reader2.getBestEstimate();
		ProteinNameAccesser accesser = reader2.getProNameAccesser();
		LabelType labelType = reader2.getType();
		String[] ratioNames = new String[] { "2/1" };
		double[] theoryRatio = new double[] { 1.0 };

		GlycoQuanXlsWriter writer = new GlycoQuanXlsWriter(out, labelType,
				ratioNames, theoryRatio);
		writer.write(peps, matchedssms, unmatchedssms, results, bestEstimate,
				accesser, 10.0);
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws XMLStreamException 
	 * @throws FastaDataBaseException 
	 * @throws IOException 
	 * @throws WriteException 
	 * @throws FileDamageException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws RowsExceededException, FileDamageException, 
		WriteException, IOException, FastaDataBaseException, XMLStreamException, DocumentException {
		// TODO Auto-generated method stub

		GlycoLabelMaxQuantTask.test();
	}
	
}
