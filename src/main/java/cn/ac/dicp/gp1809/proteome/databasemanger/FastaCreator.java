/* 
 ******************************************************************************
 * File:FastaCreator.java * * * Created on 2010-4-26
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.databasemanger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jxl.JXLException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Protein;
import cn.ac.dicp.gp1809.proteome.IO.proteome.Proteins;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.IProteinGroupSimplifier;
import cn.ac.dicp.gp1809.proteome.IO.proteome.protein.MostLocusSimplifier;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelFormat;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelReader;
import cn.ac.dicp.gp1809.util.ioUtil.excel.ExcelWriter;

/**
 * Tools used for create a fasta file.
 * 
 * @version 1.00
 * @author JiaweiMao
 * @version Jan 19, 2016, 9:44:33 AM
 */
public class FastaCreator {

	private FastaWriter writer;
	private FastaAccesser accesser;
	private static final IProteinGroupSimplifier SIMPLIFIER = new MostLocusSimplifier();
	private IDecoyReferenceJudger djudger;

	private String[] fastas;
	private HashMap<String, ProteinSequence> map;
	private HashMap<String, String> ipiMap;

	public FastaCreator(String output) throws IOException {
		this.writer = new FastaWriter(output);
	}

	public FastaCreator(String FastaFile, String output, String ipi_his) throws IOException, FastaDataBaseException {
		this(FastaFile, output, new DefaultDecoyRefJudger(), ipi_his);
	}

	public FastaCreator(String FastaFile, String output, IDecoyReferenceJudger judger)
			throws IOException, FastaDataBaseException {
		// this.accesser = new FastaAccesser(FastaFile, judger);
		this.writer = new FastaWriter(output);
		this.djudger = judger;
		this.map = new HashMap<String, ProteinSequence>();
		FastaReader reader = new FastaReader(FastaFile);
		ProteinSequence ps = null;
		while ((ps = reader.nextSequence()) != null) {
			String ref = ps.getReference().substring(4, 15);
			map.put(ref, ps);
		}

		this.ipiMap = new HashMap<String, String>();

		reader.close();
		System.out.println("ref\t" + map.size());
	}

	public FastaCreator(String FastaFile, String output, IDecoyReferenceJudger judger, String ipi_his)
			throws IOException, FastaDataBaseException {
		// this.accesser = new FastaAccesser(FastaFile, judger);
		this.writer = new FastaWriter(output);
		this.djudger = judger;
		this.map = new HashMap<String, ProteinSequence>();
		FastaReader reader = new FastaReader(FastaFile);
		ProteinSequence ps = null;
		while ((ps = reader.nextSequence()) != null) {
			// String ref = ps.getReference().substring(4, 15);
			String ref = ps.getGene();
			map.put(ref, ps);
		}

		this.ipiMap = this.ipiHisMap(ipi_his);

		reader.close();
		System.out.println("ref\t" + map.size());
	}

	public FastaCreator(String[] FastaFile, String output) throws IOException {
		this.fastas = FastaFile;
		this.writer = new FastaWriter(output);
	}

	public void getRefs(String refFile)
			throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IOException {
		String ref;
		BufferedReader reader = new BufferedReader(new FileReader(refFile));
		while ((ref = reader.readLine()) != null && ref.length() > 0) {
			// ref = ref.substring(0, ref.length()-2);
			// ProteinSequence seq = accesser.getSequence(ref);
			ProteinSequence seq = map.get(ref);
			if (seq != null)
				writer.write(seq);
			/*
			 * else{ String newref = this.ipiMap.get(ref); seq =
			 * map.get(newref); if(seq!=null) writer.write(seq); else{
			 * System.out.println(ref); } }
			 */
		}
		reader.close();
		writer.close();
	}

	public void getRefsXls(String refFile)
			throws ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, IOException, JXLException {
		String[] ref;
		ExcelReader reader = new ExcelReader(refFile, 2);
		while ((ref = reader.readLine()) != null && ref.length > 0) {
			// ref = ref.substring(0, ref.length()-2);
			// ProteinSequence seq = accesser.getSequence(ref[0]);
			ProteinSequence seq = map.get(ref[0]);
			if (seq != null)
				writer.write(seq);
			else {
				String newref = this.ipiMap.get(ref[0]);
				seq = map.get(newref);
				if (seq != null)
					writer.write(seq);
				else {
					System.out.println(ref[0]);
				}
			}
		}
		reader.close();
		writer.close();
	}

	public void getRefs(IPeptideReader reader)
			throws PeptideParsingException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {
		HashSet<ProteinReference> refSet = new HashSet<ProteinReference>();
		IPeptide pep;
		while ((pep = reader.getPeptide()) != null) {
			refSet.addAll(pep.getProteinReferences());
		}
		Iterator<ProteinReference> it = refSet.iterator();
		while (it.hasNext()) {
			ProteinReference pr = it.next();
			ProteinSequence seq;
			if (!pr.isDecoy()) {
				seq = accesser.getSequence(pr);
				writer.write(seq);
			}
		}
		reader.close();
		writer.close();
	}

	public void getRefs(IPeptideReader reader, boolean simplify) throws PeptideParsingException,
			ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException, FastaDataBaseException {

		HashSet<String> usedSet = new HashSet<String>();
		if (simplify) {
			Proteins proteins = new Proteins(accesser);
			IPeptide curtPeptide = null;
			while ((curtPeptide = reader.getPeptide()) != null) {
				proteins.addPeptide(curtPeptide);
			}
			Protein[] pros = proteins.getProteins();
			for (int i = 0; i < pros.length; i++) {
				String ref = SIMPLIFIER.simplify(pros[i].getReferences()).getName();
				if (djudger.isDecoy(ref))
					continue;

				if (usedSet.contains(ref)) {
					continue;
				} else {
					usedSet.add(ref);
				}
				ProteinReference pr = ProteinReference.parse(ref);
				ProteinSequence seq;
				seq = accesser.getSequence(pr);
				writer.write(seq);
			}
			reader.close();
			writer.close();
		} else {
			getRefs(reader);
		}
	}

	public void write(String[] refs)
			throws IOException, ProteinNotFoundInFastaException, MoreThanOneRefFoundInFastaException {

		for (int i = 0; i < refs.length; i++) {
			ProteinSequence seq = accesser.getSequence(refs[i]);
			writer.write(seq);
		}
		writer.close();
	}

	public void combine() throws FastaDataBaseException, IOException, ProteinNotFoundInFastaException,
			MoreThanOneRefFoundInFastaException {
		FastaReader[] accesser = new FastaReader[fastas.length];
		HashMap<String, ProteinSequence> proMap = new HashMap<String, ProteinSequence>();
		for (int i = 0; i < fastas.length; i++) {
			accesser[i] = new FastaReader(fastas[i]);
			ProteinSequence ps;
			while ((ps = accesser[i].nextSequence()) != null) {
				String ref = ps.getReference();
				if (!ref.startsWith("REV"))
					proMap.put(ref, ps);
			}
		}
		Iterator<ProteinSequence> it = proMap.values().iterator();
		System.out.println("proMap\t" + proMap.size());
		while (it.hasNext()) {
			writer.write(it.next());
		}
		writer.close();
	}

	public void combine(String dir) throws FastaDataBaseException, IOException, ProteinNotFoundInFastaException,
			MoreThanOneRefFoundInFastaException {

		File[] files = new File(dir).listFiles();
		FastaReader[] accesser = new FastaReader[files.length];
		HashMap<String, ProteinSequence> proMap = new HashMap<String, ProteinSequence>();

		for (int i = 0; i < files.length; i++) {
			accesser[i] = new FastaReader(files[i]);
			ProteinSequence ps;
			while ((ps = accesser[i].nextSequence()) != null) {
				String ref = ps.getReference();
				if (!ref.startsWith("REV"))
					proMap.put(ref, ps);
			}
		}
		Iterator<ProteinSequence> it = proMap.values().iterator();
		System.out.println("proMap\t" + proMap.size());
		while (it.hasNext()) {
			writer.write(it.next());
		}
		writer.close();
	}

	public static void switchAA(String of, String nf, char[] aa1, char[] aar) throws IOException {
		FastaReader reader = new FastaReader(of);
		FastaWriter writer = new FastaWriter(nf);
		ProteinSequence ps;
		while ((ps = reader.nextSequence()) != null) {
			String ref = ps.getReference();
			String seq = ps.getUniqueSequence();
			String nseq = seq;
			for (int i = 0; i < aa1.length; i++) {
				nseq = nseq.replace(aa1[i], aar[i]);
			}
			writer.write(ref, nseq);
		}
		reader.close();
		writer.close();
	}

	public HashMap<String, String> ipiHisMap(String ipi) throws IOException {
		HashMap<String, String> ipiMap = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(ipi));
		String s = reader.readLine();
		while ((s = reader.readLine()) != null) {
			String[] ss = s.split("\t");
			if (ss[3].startsWith("-"))
				continue;

			ipiMap.put(ss[0], ss[3]);
		}

		return this.update(ipiMap);
	}

	private HashMap<String, String> update(HashMap<String, String> ipimap) {

		HashMap<String, String> updataMap = new HashMap<String, String>();
		Iterator<String> it2 = ipimap.keySet().iterator();
		while (it2.hasNext()) {
			String key = it2.next();
			String value = ipimap.get(key);
			if (ipimap.containsKey(value)) {
				updataMap.put(key, ipimap.get(value));
			} else {
				updataMap.put(key, value);
			}
		}

		HashSet<String> ipiset = new HashSet<String>();
		Iterator<String> it1 = ipimap.keySet().iterator();
		while (it1.hasNext()) {
			String key = it1.next();
			String value = ipimap.get(key);
			if (ipimap.containsKey(value)) {
				ipiset.add(value);
			}
		}

		if (ipiset.size() == 0) {
			return updataMap;
		} else {
			return update(updataMap);
		}
	}

	public void select(String in) throws IOException {
		FastaReader reader = new FastaReader(in);
		ProteinSequence ps = null;
		while ((ps = reader.nextSequence()) != null) {
			String ref = ps.getReference();
			if (ref.contains("(L")) {
				this.writer.write(ps);
			}
		}
		reader.close();
		writer.close();
	}

	public static void count(String in) throws IOException {
		int count = 0;
		FastaReader reader = new FastaReader(in);
		ProteinSequence ps = null;
		while ((ps = reader.nextSequence()) != null) {
			count++;
			if (ps.getReference().contains(">")) {
				System.out.println(ps.getReference());
			}
		}
		System.out.println(count);
		reader.close();
	}

	private static void uniprotRev(String in) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(in));
		String out1 = in.replace("fasta", "rev.fasta");
		PrintWriter writer1 = new PrintWriter(out1);
		PrintWriter writer2 = new PrintWriter(in.replace("fasta", "final.fasta"));
		String line = null;
		StringBuilder sb = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(">")) {
				if (sb == null) {
					sb = new StringBuilder();
				} else {
					StringBuilder revsb = sb.reverse();
					int length = revsb.length();
					int linenumber = length / 60;
					for (int i = 0; i < linenumber; i++) {
						writer1.write(revsb.substring(i * 60, (i + 1) * 60) + "\n");
					}
					writer1.write(revsb.substring(linenumber * 60) + "\n");
					sb = new StringBuilder();
				}
				String revref = line.substring(0, line.indexOf("|") + 1) + "REV_"
						+ line.substring(line.indexOf("|") + 1);
				writer1.write(revref + "\n");
				writer2.write(line + "\n");
			} else {
				writer2.write(line + "\n");
				sb.append(line);
			}
		}
		StringBuilder revsb = sb.reverse();
		int length = revsb.length();
		int linenumber = length / 60;
		for (int i = 0; i < linenumber; i++) {
			writer1.write(revsb.substring(i * 60, (i + 1) * 60) + "\n");
		}
		writer1.write(revsb.substring(linenumber * 60) + "\n");
		reader.close();
		writer1.close();

		BufferedReader reader2 = new BufferedReader(new FileReader(out1));
		while ((line = reader2.readLine()) != null) {
			writer2.write(line + "\n");
		}
		reader2.close();
		writer2.close();
	}

	private static void uniprot2ipi(String uniprot, String ipi, String result) throws IOException, JXLException {

		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		FastaReader fr = new FastaReader(ipi);
		ProteinSequence ps = null;
		while ((ps = fr.nextSequence()) != null) {
			String ref = ps.getReference();
			String[] cs = ref.split("\\|");
			for (int i = 0; i < cs.length; i++) {
				if (cs[i].startsWith("REFSEQ")) {
					map.put(cs[i].substring(cs[i].indexOf(":") + 1), ref);
				}
			}
			String gene = cs[cs.length - 1];
			String name = "";
			int geneid = gene.indexOf("Gene_Symbol=");
			if (geneid > -1) {
				name = gene.substring(geneid + 1);
				name = name.substring(name.indexOf(" ") + 1);
				if (!name.startsWith("Uncharacterized"))
					map2.put(name, ref);
			}
		}
		fr.close();
		System.out.println(map.size());

		ExcelWriter writer = new ExcelWriter(result);
		ExcelFormat format = ExcelFormat.normalFormat;

		ExcelReader reader = new ExcelReader(uniprot);
		String[] line = reader.readLine();
		writer.addTitle(line, 0, format);

		while ((line = reader.readLine()) != null) {
			String[] cs = line[0].split("\\|");
			String key = cs[3].substring(0, cs[3].lastIndexOf("."));
			if (map.containsKey(key)) {
				StringBuilder sb = new StringBuilder();
				sb.append(map.get(key)).append("\t");
				for (int i = 1; i < line.length; i++) {
					sb.append(line[i]).append("\t");
				}
				writer.addContent(sb.toString(), 0, format);
			} else {
				String key2 = cs[4].substring(1, cs[4].length() - 15);
				if (map2.containsKey(key2)) {
					StringBuilder sb = new StringBuilder();
					sb.append(map2.get(key2)).append("\t");
					for (int i = 1; i < line.length; i++) {
						sb.append(line[i]).append("\t");
					}
					writer.addContent(sb.toString(), 0, format);
				}
			}
		}
		reader.close();
		writer.close();
	}

	private static void creatPepDatabase(String in, String fasta, String out) throws IOException, JXLException {
		FastaWriter writer = new FastaWriter(out);
		HashMap<String, ProteinSequence> psmap = new HashMap<String, ProteinSequence>();
		FastaReader fr = new FastaReader(fasta);
		ProteinSequence ps = null;
		while ((ps = fr.nextSequence()) != null) {
			String ref = ps.getReference();
			String key = ref.split("\\|")[1];
			// System.out.println(key);
			psmap.put(key, ps);
		}
		fr.close();
		ExcelReader reader = new ExcelReader(in);
		HashSet<String> set = new HashSet<String>();
		String[] line = null;
		while ((line = reader.readLine()) != null) {
			if (psmap.containsKey(line[1])) {
				ProteinSequence proseq = psmap.get(line[1]);
				int begin = proseq.indexOf(line[0]);
				int end = begin + line[0].length() - 1;
				String oref = proseq.getReference();
				String ref = oref.substring(0, 2) + "|" + line[1] + "|" + begin + "-" + end;
				// System.out.println(ref);
				// writer.write(ref, line[0]);
				if (set.contains(line[1]))
					continue;
				set.add(line[1]);
				writer.write(proseq);
			} else {
				System.out.println(line[0]);
			}
		}
		reader.close();
		writer.close();
	}

	private static void createRev4Swiss(String in, String out) throws IOException {
		FastaWriter writer = new FastaWriter(out);
		FastaReader fr = new FastaReader(in);
		ProteinSequence ps = null;
		HashSet<String> set = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();
		while ((ps = fr.nextSequence()) != null) {
			String ref = ps.getReference();
			String seq = ps.getUniqueSequence();
			String[] ss = ref.split("\\|");
			StringBuilder refsb = new StringBuilder();
			for (int i = 0; i < ss.length; i++) {
				if (i == 1) {
					refsb.append("REV_").append(ss[i]).append("|");
				} else {
					refsb.append(ss[i]).append("|");
				}
			}
			refsb.deleteCharAt(refsb.length() - 1);
			set.add(refsb.toString());
			set2.add(ref);
			StringBuilder seqsb = (new StringBuilder(seq)).reverse();
			writer.write(ps);
			writer.write(refsb.toString(), seqsb.toString());
		}
		fr.close();
		writer.close();
		System.out.println(set.size() + "\t" + set2.size());
	}

	/**
	 * @param args
	 * @throws FastaDataBaseException
	 * @throws IOException
	 * @throws MoreThanOneRefFoundInFastaException
	 * @throws ProteinNotFoundInFastaException
	 * @throws JXLException
	 */
	public static void main(String[] args) throws IOException, FastaDataBaseException, ProteinNotFoundInFastaException,
			MoreThanOneRefFoundInFastaException, JXLException {
		// TODO Auto-generated method stub

		String ref = "E:\\�½��ı��ĵ�.txt";
		// String fasta = "F:\\DataBase\\ipi.HUMAN.v3.80\\" +
		// "ipi.HUMAN.v3.80.fasta";
		String fasta = "F:\\DataBase\\IPI_mouse\\current\\" + "ipi.MOUSE.v3.80.fasta";
		String out = "E:\\mouse.fasta";

		// FastaCreator.creatPepDatabase("L:\\database\\database.xls",
		// "L:\\pan_UNIPROT_HUMAN.fasta",
		// "L:\\database\\pan_UNIPROT_HUMAN_pro.fasta");

		// FastaCreator.uniprot2ipi("I:\\20140605_hela_nucleus_turnover.xls",
		// "F:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta",
		// "I:\\20140605_hela_nucleus_turnover.ipi.xls");

		// FastaCreator creator = new
		// FastaCreator("K:\\R_pcdb_MOUSE.label.fasta");
		// creator.select("K:\\R_pcdb_MOUSE.fasta");

		// FastaCreator creator = new FastaCreator(fasta, out
		// , "D:\\ModDataBase\\ipi.HUMAN.history.3.80");
		// creator.getRefs(ref);

		// FastaCreator.uniprotRev("F:\\DataBase\\o_glycan\\O-glycoprotein_0.fasta");
		// FastaCreator.count("F:\\DataBase\\uniprot.human.20130312\\Final_uniprot_taxonomy_9606_homo_sapiens.fasta");
		// FastaCreator creator = new FastaCreator("D:\\combine2.fasta");
		// creator.combine("D:\\���ݿ�ϲ�\\new");
		// creator.getRefsXls(ref);

		String s1 = "E:\\uniprot-saccharomyces+cerevisiae+W303.fasta";
		String s2 = "E:\\uniprot-saccharomyces+cerevisiae+s288c.fasta";

		String[] ss = new String[] { s1, s2 };
		// FastaCreator creator = new
		// FastaCreator(ss,"E:\\uniprot-saccharomyces+cerevisiae+W303+s288c.fasta");
		// creator.combine();
		/*
		 * String f1 = "E:\\DataBase\\Final_HUMAN.fasta"; String f2 =
		 * "E:\\DataBase\\hlh_database.fasta"; // String f3 =
		 * "E:\\DataBase\\ipi.HUMAN.v3.52.decoy.fasta"; // String f4 =
		 * "E:\\DataBase\\QFasta\\uniprot-sponges.fasta"; String out =
		 * "E:\\DataBase\\Final_xue_HUMAN.fasta";
		 * 
		 * String [] fs = new String[]{f1,f2}; FastaCreator creator = new
		 * FastaCreator(fs,out); creator.combine();
		 */

		// String of =
		// "E:\\DataBase\\Final_ipi.HUMAN.v3.17\\Final_ipi.HUMAN.v3.17.fasta";
		// String nf =
		// "E:\\DataBase\\Final_ipi.HUMAN.v3.17\\Final_ipi.HUMAN.v3.17_ST_JU.fasta";
		// FastaCreator.switchAA(of, nf, new char[]{'S', 'T'}, new char[]{'J',
		// 'U'});

		FastaCreator.createRev4Swiss("D:\\ModDataBase\\v20141011.human\\human.jou.fasta",
				"D:\\ModDataBase\\v20141011.human\\final.human.jou.fasta");

	}

}
