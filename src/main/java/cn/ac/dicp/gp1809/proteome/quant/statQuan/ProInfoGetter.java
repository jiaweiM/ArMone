/* 
 ******************************************************************************
 * File: ProInfoGetter.java * * * Created on 2011-4-6
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.statQuan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.JXLException;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.SequenceGenerationException;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.MoreThanOneRefFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNotFoundInFastaException;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;

/**
 * @author ck
 *
 * @version 2011-4-6, 18:47:58
 */
public class ProInfoGetter {
	
	private IFastaAccesser accesser;
	private PhosPepUnit [] units;

	public ProInfoGetter(String fasta, IDecoyReferenceJudger judger) 
		throws FastaDataBaseException, IOException{
		
		this.accesser = new FastaAccesser(fasta, judger);
	}
	
	public void getInfo(String file, int sheetNum, String out) throws IOException, JXLException, 
		ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, SequenceGenerationException{
		
		ArrayList <PhosPepUnit> list = new ArrayList <PhosPepUnit>();
//		ExcelWriter writer = new ExcelWriter(out, sheetNum);
		ExcelFormat format = new ExcelFormat(false, 0);
		for(int i=0;i<sheetNum;i++){
			ExcelReader reader = new ExcelReader(file, i);
//			writer.addTitle("Mod\tAverage\tReference\tSwiss\tGene\tSite\tSeq", i, format);
			
			String [] cs = reader.readLine();
			while((cs=reader.readLine())!=null){
				String seq = cs[0];
//				System.out.println(i+"\t"+seq);
				double ratio = Double.parseDouble(cs[1]);
				String ref = cs[2];
				int end = ref.indexOf("(");
				ref = ref.substring(0, end);
				ProteinSequence ps = accesser.getSequence(ref);
				
				PhosPepUnit unit = new PhosPepUnit(seq, ratio, ps);
//				writer.addContent(unit.toString(), i, format);
				
				list.add(unit);
			}
		}
		
		this.units = list.toArray(new PhosPepUnit[list.size()]);
//		writer.close();
	}

	public PhosPepUnit [] getUnits(){
		return this.units;
	}

	public class PhosPepUnit{
		
		private String seq;
		private double ratio;
		private ProteinSequence ps;
		
		private String [] mods;
		private String [] aas;
		
		private String swiss = "";
		private String gene= "";
		
		public PhosPepUnit(String seq, double ratio, ProteinSequence ps) throws SequenceGenerationException{
			this.seq = seq;
			this.ratio = ratio;
			this.ps = ps;
			this.initial();
		}
	
		public void initial() throws SequenceGenerationException{
			
			String pepseq = seq.split("\\s+")[0];
			int aan = 0;
			ArrayList <Integer> site = new ArrayList <Integer>();
			StringBuilder sb = new StringBuilder();
			char [] cs = pepseq.toCharArray();
			for(int i=0;i<cs.length;i++){
				if(cs[i]>='A' && cs[i]<='Z'){
					sb.append(cs[i]);
					aan++;
				}else if(cs[i]=='p'){
					site.add(aan);
				}
			}
			
			int beg = ps.indexOf(sb.toString());
			if(beg!=-1){
				String proseq = ps.getUniqueSequence();
				String [] mods = new String [site.size()];
				String [] aas = new String [site.size()];
				for(int i=0;i<site.size();i++){
					Integer it = site.get(i);
					int loc = beg+it;
					mods[i] = proseq.charAt(loc)+""+(loc+1);
					aas[i] = ps.getSeqAround(loc);
				}
				this.mods = mods;
				this.aas = aas;
				
				String reference = ps.getReference();
				Pattern pswiss = Pattern.compile("SWISS-PROT:([^|\\s]*)");
				Matcher mswiss = pswiss.matcher(reference);
				if(mswiss.find()){
					this.swiss = mswiss.group(1);
				}
				
				Pattern pgene = Pattern.compile("Gene_Symbol=([^|\\s]*)");
				Matcher mgene = pgene.matcher(reference);
				if(mgene.find()){
					this.gene = mgene.group(1);
				}
			}else{
				System.out.println(sb);
				System.out.println(ps.getReference());
			}
		}
		
		public String getRef(){
			return ps.getReference();
		}
		
		public String [] getMods(){
			return mods;
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(seq).append("\t");
			sb.append(ratio).append("\t");
			sb.append(ps.getReference()).append("\t");
			sb.append(swiss).append("\t");
			sb.append(gene).append("\t");
			for(int i=0;i<mods.length;i++){
				sb.append(mods[i]).append("\t").append(aas[i]).append("\t");
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FastaDataBaseException 
	 * @throws JXLException 
	 * @throws SequenceGenerationException 
	 * @throws MoreThanOneRefFoundInFastaException 
	 * @throws ProteinNotFoundInFastaException 
	 */
	public static void main(String[] args) throws FastaDataBaseException, IOException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, SequenceGenerationException, JXLException {
		// TODO Auto-generated method stub

		String fasta = "E:\\DataBase\\ipi.HUMAN.v3.52\\Final_ipi_human352_0.fasta";
		String in = "F:\\data\\regulatedRSD50%.xls";
		String out = "F:\\data\\regulatedRSD50%_pro_info.xls";
		
		IDecoyReferenceJudger judger = new DefaultDecoyRefJudger();
		ProInfoGetter getter = new ProInfoGetter(fasta, judger);
		getter.getInfo(in, 1, out);
/*		
		FastaReader reader = new FastaReader(fasta);
		FastaWriter writer = new FastaWriter("E:\\DataBase\\ipi.HUMAN.v3.52\\Final_ipi_human352_no_con_0.fasta");
		ProteinSequence ps;
		while((ps=reader.nextSequence())!=null){
			String ref = ps.getReference();
			if(ref.startsWith("IPI:CON"))
				continue;
			writer.write(ps);
		}
		writer.close();
*/		
	}

}
