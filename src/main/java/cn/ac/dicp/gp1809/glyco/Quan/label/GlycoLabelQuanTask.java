/* 
 ******************************************************************************
 * File: GlycoQuanTask.java * * * Created on 2011-3-30
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco.Quan.label;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

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
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification.Modif;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.DefaultVariModPepFilter;
import cn.ac.dicp.gp1809.proteome.dbsearch.filters.peptide.NGlycoPepCriteria;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2011-3-30, 19:08:09
 */
public class GlycoLabelQuanTask implements ITask {

	private IPeptideListReader reader;
	private NGlyStrucLabelGetter getter;
	private LabelType type;

	private boolean idenLabel;
	private AminoacidModification aam;
	private NGlycoPepCriteria nGlyCri = new NGlycoPepCriteria(true);
	private DefaultVariModPepFilter [] oGlyCirs;
	
	// 0: N-glycans; 1: O-glycans
	private int glycoType;
	
	private GlycoLabelFeaturesXMLWriter writer;
	
	private IPeptide curtPeptide;
	private int total;
	private int curt;
	private boolean integration = false;
	
	/**
	 * 
	 * @param reader
	 * @param database
	 * @param pixfile
	 * @param result
	 * @param jpara
	 * @param type
	 * @param glycanType 0 = N-linked glycans; 1 = O-linked glycans
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public GlycoLabelQuanTask(IPeptideListReader reader, String peakfile, String result, 
			GlycoJudgeParameter jpara, LabelType labeltype, int glycanType) 
		throws FastaDataBaseException, IOException, XMLStreamException{

		this.reader = reader;
		this.total = reader.getNumberofPeptides();
		this.glycoType = glycanType;
		this.type = labeltype;
		
		this.idenLabel = jpara.getDeGlycoLabel();
		this.aam = reader.getSearchParameter().getVariableInfo();
		
		switch (glycanType){
		
			case 0: {
				this.getter = new NGlyStrucLabelGetter(peakfile, jpara, labeltype, aam);
				break;
			}

			case 1: {
				
				HashSet <DefaultVariModPepFilter> oGlyCirs = new HashSet <DefaultVariModPepFilter>();
				HashSet <Character> symset = new HashSet <Character>();
				Modif [] modifs = aam.getModifications();
				for(int i=0;i<modifs.length;i++){
					double mass = modifs[i].getMass();
					if(Math.abs(mass-365.132196)<0.01){
						symset.add(modifs[i].getSymbol());
					}
					if(Math.abs(mass-203.079373)<0.01){
						symset.add(modifs[i].getSymbol());
					}
					DefaultVariModPepFilter oGlyCri = 
						new DefaultVariModPepFilter(modifs[i].getName(), modifs[i].getSymbol(), "ST");
					oGlyCirs.add(oGlyCri);
				}
				
				this.oGlyCirs = oGlyCirs.toArray(new DefaultVariModPepFilter[oGlyCirs.size()]);
//				this.getter = new OGlyLabelGetter(peakfile, jpara, labeltype, symset);
				break;
			}
		}

		this.writer = new GlycoLabelFeaturesXMLWriter(result, labeltype, false);
		this.writer.addModification(aam);
		this.writer.addProNameInfo(reader.getProNameAccesser());
	}
	
	/**
	 * 
	 * @param reader
	 * @param accesser
	 * @param getter
	 * @param result
	 * @param type
	 * @param glycanType 0 = N-linked glycans; 1 = O-linked glycans
	 * @throws IOException
	 */
	public GlycoLabelQuanTask(IPeptideListReader reader, NGlyStrucLabelGetter getter, 
			String result, LabelType labeltype, int glycanType) throws IOException{
		
		this.reader = reader;
		this.total = reader.getNumberofPeptides();
		this.getter = getter;
		this.type = labeltype;
		this.idenLabel = getter.getParameter().getDeGlycoLabel();
		this.aam = reader.getSearchParameter().getVariableInfo();
		
		this.writer = new GlycoLabelFeaturesXMLWriter(result, labeltype, false);
		this.writer.addModification(aam);
		this.writer.addProNameInfo(reader.getProNameAccesser());
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub
		float per = (float)curt/(float)total;
		return per>1 ? 1 : per;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		reader.close();
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
		} catch (PeptideParsingException e) {
			throw new RuntimeException(e);
		}
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
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#processNext()
	 */
	@Override
	public void processNext() {
		// TODO Auto-generated method stub
		if (this.integration) {

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
			
			if(!curtPeptide.isTP())
				return;
			
			if(this.idenLabel){
				
				if(glycoType==0){
					if(nGlyCri.filter(curtPeptide)){
						IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
						this.getter.addPeptide2(gp, aam);
					}
				}else if(glycoType==1){
					for(int i=0;i<oGlyCirs.length;i++){
						if(oGlyCirs[i].filter(curtPeptide)){
							IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
							this.getter.addPeptide2(gp, aam);
						}
					}
				}
				
			}else{
				
				if(glycoType==0){
					if(nGlyCri.filter(curtPeptide)){
						IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
						this.getter.addPeptide(gp, aam);
					}
				}else if(glycoType==1){
					for(int i=0;i<oGlyCirs.length;i++){
						if(oGlyCirs[i].filter(curtPeptide)){
							IGlycoPeptide gp = new GlycoPeptide(curtPeptide);
							this.getter.addPeptide(gp, aam);
						}
					}
				}
			}
			this.curt = this.reader.getCurtPeptideIndex();
		}
	}
	
	public GlycoLabelFeaturesXMLReader createReader() throws DocumentException{
		return this.writer.createReader();
	}
	
	private static void test() throws FileDamageException, IOException, FastaDataBaseException, 
		XMLStreamException, DocumentException, RowsExceededException, WriteException{
		
		String ppl = "I:\\human_liver_glycan_quantification\\2014.02.16_2D\\20140216_humanliver_2D_no-glycan_HCC_normal_C18-PGC_20%ACN-2.dat.RT.ppl";
		String peak = "I:\\human_liver_glycan_quantification\\2014.02.16_2D\\20140216_humanliver_2D_with-glycan_HCC_normal_C18-PGC_20%ACN-2.mzXML";
		String pxml = "I:\\human_liver_glycan_quantification\\2014.02.16_2D\\20140216_humanliver_2D_with-glycan_HCC_normal_C18-PGC_20%ACN-2.pxml";
		String out = "I:\\human_liver_glycan_quantification\\2014.02.16_2D\\20140216_humanliver_2D_with-glycan_HCC_normal_C18-PGC_20%ACN-2.xls";
		
		/*String ppl = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Iden\\4p_2_1-2.csv.ppl";
		String peak = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Glycan\\20130805_4p_di-labeling_HCD_N-glycan_quantification_2_1-2.mzXML";
		String pxml = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Glycan\\20130805_4p_di-labeling_HCD_N-glycan_quantification_2_1-2.pxml";
		String out = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Glycan\\20130805_4p_di-labeling_HCD_N-glycan_quantification_2_1-2.xls";*/
		
		LabelType type = LabelType.Dimethyl;
		LabelInfo[][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_C13D3_K
			, LabelInfo.Dimethyl_C13D3_Nt}};
		
		linfo[0][0].setSymbol('@');
		linfo[0][1].setSymbol('@');
		linfo[1][0].setSymbol('^');
		linfo[1][1].setSymbol('^');
		type.setInfo(linfo);
		type.setUsed(new short[]{1,2});
		
		IPeptideListReader reader = new PeptideListReader(ppl);

		GlycoLabelQuanTask task = new GlycoLabelQuanTask(reader, peak, pxml, 
				GlycoJudgeParameter.defaultParameter(), type, 0);
		while(task.hasNext()){
			task.processNext();
		}
		task.dispose();
		
		GlycoLabelFeaturesXMLReader reader2 = new GlycoLabelFeaturesXMLReader(pxml);
		IGlycoPeptide[] peps = reader2.getAllGlycoPeptides();
//		GlycoPeptideLabelPair[] pairs = reader2.getAllSelectedPairs();
		GlycoQuanResult[] results = reader2.getAllResult();
		NGlycoSSM[] matchedssms = reader2.getMatchedGlycoSpectra();
		NGlycoSSM[] unmatchedssms = reader2.getUnmatchedGlycoSpectra();
		
		double[] bestEstimate = reader2.getBestEstimate();
		ProteinNameAccesser accesser = reader2.getProNameAccesser();
		LabelType labelType = reader2.getType();
		String [] ratioNames = new String[]{"2/1"};
		double [] theoryRatio = new double[]{1.0};
		
		GlycoQuanXlsWriter writer = new GlycoQuanXlsWriter(out, labelType, ratioNames, theoryRatio);
		writer.write(peps, matchedssms, unmatchedssms, results, bestEstimate, accesser, 10.0);
	}

	private static void batchTest(String ppl, String mzxml) throws FastaDataBaseException, IOException, XMLStreamException, 
		FileDamageException, DocumentException, RowsExceededException, WriteException{
		
		HashMap<String, String> pplmap = new HashMap<String, String>();
		File[] pplfiles = (new File(ppl)).listFiles();
		for(int i=0;i<pplfiles.length;i++){
			String name = pplfiles[i].getName();
			if(name.endsWith("ppl")){
				String key = name.replace("CID", "HCD");
				key = key.substring(0, key.length()-7);
				pplmap.put(key, pplfiles[i].getAbsolutePath());
				System.out.println(key);
			}
		}
		File[] mzxmlfiles = (new File(mzxml)).listFiles();
		for(int i=0;i<mzxmlfiles.length;i++){
			
			String name = mzxmlfiles[i].getName();
			name = name.substring(0, name.length()-6);
			System.out.println(name);
			
			if(pplmap.containsKey(name)){
				
				String pplin = pplmap.get(name);
				String peak = mzxmlfiles[i].getAbsolutePath();
				String pxml = peak.replace("mzXML", "pxml");
				String out = peak.replace("mzXML", "xls");

				LabelType type = LabelType.Dimethyl;
				LabelInfo [][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_C13D3_K
					, LabelInfo.Dimethyl_C13D3_Nt}};
				
				linfo[0][0].setSymbol('@');
				linfo[0][1].setSymbol('@');
				linfo[1][0].setSymbol('^');
				linfo[1][1].setSymbol('^');
				type.setInfo(linfo);
				type.setUsed(new short[]{1,2});
				
				IPeptideListReader reader = new PeptideListReader(pplin);

				GlycoLabelQuanTask task = new GlycoLabelQuanTask(reader, peak, pxml, 
						GlycoJudgeParameter.defaultParameter(), type, 0);
				while(task.hasNext()){
					task.processNext();
				}
				task.dispose();
				
				GlycoLabelFeaturesXMLReader reader2 = new GlycoLabelFeaturesXMLReader(pxml);
				IGlycoPeptide[] peps = reader2.getAllGlycoPeptides();
//				GlycoPeptideLabelPair[] pairs = reader2.getAllSelectedPairs();
				GlycoQuanResult[] results = reader2.getAllResult();
				NGlycoSSM[] matchedssms = reader2.getMatchedGlycoSpectra();
				NGlycoSSM[] unmatchedssms = reader2.getUnmatchedGlycoSpectra();
				
				double[] bestEstimate = reader2.getBestEstimate();
				ProteinNameAccesser accesser = reader2.getProNameAccesser();
				LabelType labelType = reader2.getType();
				String [] ratioNames = new String[]{"2/1"};
				double [] theoryRatio = new double[]{1.0};
				
				GlycoQuanXlsWriter writer = new GlycoQuanXlsWriter(out, labelType, ratioNames, theoryRatio);
				writer.write(peps, matchedssms, unmatchedssms, results, bestEstimate, accesser, 10.0);
			}
		}
	}
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 * @throws FileDamageException 
	 * @throws FastaDataBaseException 
	 * @throws DocumentException 
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public static void main(String[] args) throws IOException, XMLStreamException, FileDamageException, 
		FastaDataBaseException, RowsExceededException, WriteException, DocumentException {
		// TODO Auto-generated method stub
		long op = System.currentTimeMillis();
		
		/*String peak = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Glycan\\20130805_4p_di-labeling_HCD_N-glycan_quantification_1_1-1.mzXML";
		String ppl = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Iden\\4p_1_1.csv.ppl";
		String out = "H:\\NGlyan_Quan_20130812\\4Glyco_protein\\Glycan\\20130805_4p_di-labeling_HCD_N-glycan_quantification_1_1-1.pxml";
		*/

		/*LabelType type = LabelType.SILAC;
		LabelInfo [][] linfo = new LabelInfo[][]{{}, {LabelInfo.SILAC_Arg10
				, LabelInfo.SILAC_Lys8}};
		linfo[1][0].setSymbol('@');
		linfo[1][1].setSymbol('#');
		type.setInfo(linfo);
		type.setUsed(new short[]{1,2});*/
		
//		GlycoLabelQuanTask.test();
		
		GlycoLabelQuanTask.batchTest("J:\\serum glycan quantification\\4th\\20131109-velos\\deglyco", 
				"J:\\serum glycan quantification\\4th\\20131109-velos\\glyco");
		
		long ed = System.currentTimeMillis();
		System.out.println((ed-op)/1000.0+"s");
	}

}
