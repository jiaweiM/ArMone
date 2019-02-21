/* 
 ******************************************************************************
 * File:LabelQuanTask.java * * * Created on 2010-5-17
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.label.IO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.dicp.gp1809.proteome.quant.label.LabelInfo;
import cn.ac.dicp.gp1809.proteome.quant.label.PeptidePairGetter;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.PeptidePair;
import org.dom4j.DocumentException;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.FileDamageException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.PeptideListReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.dbsearch.AminoacidModification;
import cn.ac.dicp.gp1809.util.progress.ITask;

/**
 * @author ck
 *
 * @version 2010-5-17, 06:14:28
 */
public class LabelQuanTask implements ITask {

	private IPeptideListReader reader;
	protected LabelFeaturesXMLWriter writer;
	private PeptidePairGetter getter;
	private LabelType type;

	private IPeptide curtPeptide;
	private int total;
	private int curt;
	private boolean integration = false;
	
	public LabelQuanTask(){
		
	}

	public LabelQuanTask(IPeptideListReader reader, String pixfile, 
			String result, LabelType type, int mzxmlType) throws IOException, FastaDataBaseException {
		
		this(reader, new PeptidePairGetter(pixfile, type, mzxmlType), result, type);
	}
	
	public LabelQuanTask(IPeptideListReader reader, PeptidePairGetter getter, String result, LabelType type) throws IOException{
		
		this.reader = reader;
		this.total = reader.getNumberofPeptides();
		
		AminoacidModification aamodif = reader.getSearchParameter().getVariableInfo();

		this.type = type;
		this.getter = getter;
		this.getter.setLabelType(type);
		this.getter.setModif(aamodif);
		
		this.writer = new LabelFeaturesXMLWriter(result, type, false);
		writer.addModification(aamodif);
		writer.addProNameInfo(reader.getProNameAccesser());
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#completedPercent()
	 */
	@Override
	public float completedPercent() {
		// TODO Auto-generated method stub

		float per = (float)curt/(float)total;
		return per;
	}

	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.util.progress.ITask#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		getter.close();
		reader.close();
		System.gc();
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
			
			HashMap <String, PeptidePair> pairMap = getter.getPeptidPairs();

			Iterator <String> it = pairMap.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				PeptidePair pair = pairMap.get(key);
				this.writer.addPeptidePair(pair);
			}
			
			HashMap <String, IPeptide> idenPepMap = getter.getIdenPepMap();
			HashMap <String, HashSet<Integer>> labelTypeMap = getter.getLabelTypeMap();
			Iterator <String> idenPepIt = idenPepMap.keySet().iterator();
			while(idenPepIt.hasNext()){
				String key = idenPepIt.next();
				this.writer.addIdenPep(idenPepMap.get(key), labelTypeMap.get(key));
			}

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

			/**
			 *
			MascotPeptide mp = (MascotPeptide) curtPeptide;
			if(mp.getHomoThres()==0){
				if(mp.getIonscore()>mp.getIdenThres()){
					this.getter.addPeptide(curtPeptide);
				}
			}else{
				if(mp.getIonscore()>mp.getHomoThres()){
					this.getter.addPeptide(curtPeptide);
				}
			}
			*/
			if(curtPeptide.getPrimaryScore()>30)
			this.getter.addPeptide(curtPeptide);
			this.curt = this.reader.getCurtPeptideIndex();
		}
	}

	public LabelFeaturesXMLReader createReader() throws DocumentException{
		return writer.createReader();
	}
	
	public static void batchProcess(String ppl, String peak, LabelType type) throws IOException{
		
		HashMap <String, String> pplmap = new HashMap <String, String>();
		HashMap <String, String> peakmap = new HashMap <String, String>();
		
		File [] files1 = (new File(ppl)).listFiles();
		for(int i=0;i<files1.length;i++){
			String name = files1[i].getName();
			if(name.endsWith("ppl")){
				int id = name.lastIndexOf("_");
				pplmap.put(name.substring(0, name.length()-8), files1[i].getAbsolutePath());
				System.out.println(name.substring(0, name.length()-8));
			}
		}
		
		File [] files2 = (new File(peak)).listFiles();
		for(int i=0;i<files2.length;i++){
			String name = files2[i].getName();
			if(name.endsWith("mzXML")){
				int id = name.lastIndexOf("_");
//				peakmap.put(name.substring(id+1, name.length()-21), files2[i].getAbsolutePath());
//				System.out.println(name.substring(id+1, name.length()-21)+"\tmzxml");
				peakmap.put(name.substring(id+1, name.length()-6), files2[i].getAbsolutePath());
				System.out.println(name.substring(id+1, name.length()-6)+"\tmzxml");
			}
		}
		
		Iterator <String> it = peakmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			String pplfile = pplmap.get(key);
			String peakfile = peakmap.get(key);
			String result = peak+"\\20130405\\2\\"+key+".pxml";
			
			PeptideListReader pReader = null;
			try {
				pReader = new PeptideListReader(pplfile);
			} catch (FileDamageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LabelQuanTask task = null;
			try {
				task = new LabelQuanTask(pReader, peakfile, result, type, 0);
			} catch (FastaDataBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(task.hasNext()){
				task.processNext();
			}
			task.dispose();
		}
	}
	
	public static void batchProcess2(String ppl, String peak) throws IOException{
		
		long begin = System.currentTimeMillis();
		
		HashMap <String, String> pplmap = new HashMap <String, String>();
		HashMap <String, String> peakmap = new HashMap <String, String>();
		
		File [] files1 = (new File(ppl)).listFiles();
		for(int i=0;i<files1.length;i++){
			String name = files1[i].getName();
			if(name.endsWith("ppl")){
				int id = name.lastIndexOf("_");
//				pplmap.put(name.substring(0, id), files1[i].getAbsolutePath());
				pplmap.put(name.substring(0, name.length()-16), files1[i].getAbsolutePath());
//				System.out.println("peptide\t"+name.substring(0, name.length()-7));
			}
		}
		
		File [] files2 = (new File(peak)).listFiles();
		for(int i=0;i<files2.length;i++){
			String name = files2[i].getName();
			if(name.endsWith("mzXML")){
				int id = name.lastIndexOf("_");
//				peakmap.put(name.substring(id+1, name.length()-21), files2[i].getAbsolutePath());
//				System.out.println(name.substring(id+1, name.length()-21)+"\tmzxml");
				peakmap.put(name.substring(0, name.length()-6), files2[i].getAbsolutePath());
				System.out.println(name.substring(0, name.length()-6)+"\tmzxml");
			}
		}
		
		Iterator <String> it = peakmap.keySet().iterator();
		while(it.hasNext()){
			
			String key = it.next();
			if(pplmap.containsKey(key)){
				String pplfile = pplmap.get(key);
				String peakfile = peakmap.get(key);
				String result = peakfile.replace("mzXML", "pxml");
				
				testDimethyl12(pplfile, peakfile, result);
				
				long end = System.currentTimeMillis();
				System.out.println("Time:\t"+(end-begin)/6E5);
			}
		}
	}

	private static void testDimethyl13(String ppl, String mzxml, String out) throws IOException{
		
		LabelType type = LabelType.Dimethyl;
		LabelInfo [][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_C13D3_K
			, LabelInfo.Dimethyl_C13D3_Nt}};
		
		linfo[0][0].setSymbol('@');
		linfo[0][1].setSymbol('@');
		linfo[1][0].setSymbol('^');
		linfo[1][1].setSymbol('^');
		type.setInfo(linfo);
		type.setUsed(new short[]{1,2});
		
		PeptideListReader pReader = null;		
		try {
			pReader = new PeptideListReader(ppl);
		} catch (FileDamageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LabelQuanTask task = null;
		try {
			task = new LabelQuanTask(pReader, mzxml, out, type, 0);
		} catch (FastaDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(task.hasNext()){
			
			task.processNext();
		}
		
		task.dispose();
	}
	
	private static void testDimethyl12(String ppl, String mzxml, String out) throws IOException{
		
		LabelType type = LabelType.Dimethyl;
//		LabelInfo [][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_CHD2_K
//			, LabelInfo.Dimethyl_CHD2_Nt}};
		
		LabelInfo[][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_CHD2_K
			, LabelInfo.Dimethyl_CHD2_Nt}};
		
		linfo[0][0].setSymbol('#');
		linfo[0][1].setSymbol('#');
		linfo[1][0].setSymbol('@');
		linfo[1][1].setSymbol('@');
		type.setInfo(linfo);
		type.setUsed(new short[]{1,2});
		
		PeptideListReader pReader = null;		
		try {
			pReader = new PeptideListReader(ppl);
		} catch (FileDamageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LabelQuanTask task = null;
		try {
			task = new LabelQuanTask(pReader, mzxml, out, type, 0);
		} catch (FastaDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(task.hasNext()){
			
			task.processNext();
		}
		
		task.dispose();
	}

	private static void testDimethyl123(String ppl, String mzxml, String out)
			throws IOException {

		LabelType type = LabelType.Dimethyl;
		// LabelInfo [][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K,
		// LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_CHD2_K
		// , LabelInfo.Dimethyl_CHD2_Nt}};

		LabelInfo[][] linfo = new LabelInfo[][] {
				{ LabelInfo.Dimethyl_CH3_K, LabelInfo.Dimethyl_CH3_Nt },
				{ LabelInfo.Dimethyl_CHD2_K, LabelInfo.Dimethyl_CHD2_Nt } ,
				{LabelInfo.Dimethyl_C13D3_K, LabelInfo.Dimethyl_C13D3_Nt}};

		linfo[0][0].setSymbol('#');
		linfo[0][1].setSymbol('#');
		linfo[1][0].setSymbol('@');
		linfo[1][1].setSymbol('@');
		linfo[2][0].setSymbol('^');
		linfo[2][1].setSymbol('^');
		
		type.setInfo(linfo);
		type.setUsed(new short[] { 1, 2, 3 });

		PeptideListReader pReader = null;
		try {
			pReader = new PeptideListReader(ppl);
		} catch (FileDamageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LabelQuanTask task = null;
		try {
			task = new LabelQuanTask(pReader, mzxml, out, type, 0);
		} catch (FastaDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (task.hasNext()) {

			task.processNext();
		}

		task.dispose();
	}
	
	private static void danteng(String ppl, String quan) throws FileDamageException, IOException, DocumentException{
		
		LabelFeaturesXMLReader lr = new LabelFeaturesXMLReader(quan);
		PeptidePair[] pairs = lr.getAllSelectedPairs();
		HashSet<String> seqset = new HashSet<String>();
		for(int i=0;i<pairs.length;i++){
			String seq = pairs[i].getSequence();
			seqset.add(seq);
		}
		lr.close();
		
		int count = 0;
		int all = 0;
		int phos = 0;
		int phosall = 0;
		PeptideListReader pr = new PeptideListReader(ppl);
		IPeptide pep;
		while((pep=pr.getPeptide())!=null){
			String seq = pep.getSequence();
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<seq.length();i++){
				char aa = seq.charAt(i);
				if(aa!='#' && aa!='@'){
					sb.append(aa);
				}
				if(aa=='^'){
					phosall++;
				}
			}
			all++;
			if(seqset.contains(sb.toString())) count++;
			if(seq.contains("^")) phos++;
		}
		pr.close();
		
		System.out.println(count+"\t"+all+"\t"+phos+"\t"+phosall);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long startTime=System.currentTimeMillis();

//		LabelQuanTask.testDimethyl13("I:\\human_liver_glycan_quantification\\DAT\\"
//				+ "20140213_humanliver_no-glycan_HCC_normal_1.F001894.dat.ppl", 
//				"I:\\human_liver_glycan_quantification\\20140213_humanliver_no-glycan_HCC_normal_1.mzXML", 
//				"I:\\human_liver_glycan_quantification\\20140213_humanliver_no-glycan_HCC_normal_1.pxml");
		
//		LabelQuanTask.testDimethyl13("H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\"
//				+ "4p_2_1-2.csv.ppl", 
//				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\20130805_4p_di-labeling_CID_quantification_2_1-2.mzXML", 
//				"H:\\NGLYCO_QUAN\\NGlycan_Quan_20130812\\4Glyco_protein\\Iden\\20130805_4p_di-labeling_CID_quantification_2_1-2.pxml");

		LabelQuanTask.batchProcess2("H:\\Phospho_database\\SCX_mouse-liver-control\\W-H-X-D-2\\normal\\final", 
				"H:\\Phospho_database\\SCX_mouse-liver-control\\W-H-X-D-2");
		
//		LabelQuanTask.danteng("I:\\SCX-online labeling\\mouse_liver_50ug_500mM_3_F003415.ppl", 
//				"I:\\SCX-online labeling\\mouse_liver_50ug_500mM_3.pxml");
		/*LabelInfo [][] linfo = new LabelInfo[][]{{LabelInfo.Dimethyl_CH3_K, 
			LabelInfo.Dimethyl_CH3_Nt}, {LabelInfo.Dimethyl_CHD2_K
				, LabelInfo.Dimethyl_CHD2_Nt}, {LabelInfo.Dimethyl_C13D3_K, LabelInfo.Dimethyl_C13D3_Nt}};
		
		linfo[0][0].setSymbol('#');
		linfo[0][1].setSymbol('#');
		linfo[1][0].setSymbol('@');
		linfo[1][1].setSymbol('@');
		linfo[2][0].setSymbol('^');
		linfo[2][1].setSymbol('^');
		
		type.setInfo(linfo);
		type.setUsed(new short[]{1,2,3});
		*/

		
		
/*
//		LabelInfo [][] linfo = new LabelInfo[][]{{}, {LabelInfo.SILAC_Arg10, LabelInfo.SILAC_Lys8}};
		
//		linfo[1][0].setSymbol('@');
//		linfo[1][1].setSymbol('#');
		
		FeaturesGetter getter = new FeaturesGetter(pix, type, 0);

//		System.out.println("getter ok\t"+System.currentTimeMillis());
		LabelQuanTask task = null;
		try {
			task = new LabelQuanTask(pReader, getter, result, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println((double)(System.currentTimeMillis()-startTime)/100000.0);
		while(task.hasNext()){
			
			task.processNext();
		}
		
		task.dispose();
*/
		/*try {
			LabelQuanTask.batchProcess(ppl, pix, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		long endTime=System.currentTimeMillis(); 
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)/60000.0+"min");   
	}

}
