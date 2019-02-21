/*
 * *****************************************************************************
 * File: XmlPeptideReader.java * * * Created on 10-08-2008
 * 
 * Copyright (c) 2008 Xinning Jiang (vext@163.com)
 * 
 * All right reserved. Use is subject to license terms.
 * 
 * ******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.readers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cn.ac.dicp.gp1809.exceptions.MyRuntimeException;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.XTandemMod;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.XTandemParameter;
import cn.ac.dicp.gp1809.proteome.IO.XTandem.peptides.XTandemPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.ImpactReaderTypeException;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.aasequence.ProteinSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.FastaDataBaseException;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;

/**
 * Getting peptide from X!Tandem outputted xml file.
 * 
 * @author Xinning
 * @version 0.2.7, 05-20-2010, 20:49:48
 */
public class XmlPeptideReader extends AbstractXTandemPeptideReader {
	/*
	 * The parameters for XTandem database search are put at the end of the
	 * output file, leave these bytes and begain to parse the parameters. The
	 * bytes should be larger than the common length of the bytes (<7000 bytes)
	 */
	private static final int PARAM_BYTES = 10000;
	
	private static final DateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

	protected final static QName uid = new QName("uid");
	
	protected final static QName id = new QName("id");

	protected final static QName z = new QName("z");

	protected final static QName expect = new QName("expect");

	protected final static QName label = new QName("label");

	protected final static QName delta = new QName("delta");

	protected final static QName mh = new QName("mh");

	protected final static QName hyperscore = new QName("hyperscore");
	
	protected final static QName nextscore = new QName("nextscore");
	
	protected final static QName y_score = new QName("y_score");
	
	protected final static QName b_score = new QName("b_score");

	protected final static QName seq = new QName("seq");

	protected final static QName pre = new QName("pre");

	protected final static QName post = new QName("post");

	protected final static QName start = new QName("start");

	protected final static QName at = new QName("at");

	protected final static QName modified = new QName("modified");

	protected final static QName type = new QName("type");
	

	private XMLEventReader reader;

	private XTandemParameter parameter;
	
//	private ProteinReferencePool pool;
	
	private HashMap<Integer, String> proteinmap = new HashMap<Integer, String>();
	
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;
	
	private IDecoyReferenceJudger djudger;

	public XmlPeptideReader(String filename, IFastaAccesser accesser) throws ImpactReaderTypeException,
	        ParameterParseException {
		this(new File(filename), accesser);
	}

	public XmlPeptideReader(File file, IFastaAccesser accesser) throws ImpactReaderTypeException,
	        ParameterParseException {
		super(file);

		this.parameter = this.parseParameter(file);
		this.parameter.setFastaAccesser(accesser);
		this.accesser = accesser;
		this.djudger = accesser.getDecoyJudger();
		this.proNameAccesser = new ProteinNameAccesser(accesser);

		XMLInputFactory fac = XMLInputFactory.newInstance();
		try {
			reader = fac.createXMLEventReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new MyRuntimeException(e);
		} catch (XMLStreamException e) {
			close();
			throw new ImpactReaderTypeException(
			        "ProphetXmlReader unsuit Exception");
		}
	}

	private XTandemParameter parseParameter(File file)
	        throws ParameterParseException {

		try {

			String tmppath = System.getProperty("java.io.tmpdir");
			if(tmppath == null)
				tmppath = "c:\\";
			
			File tmp = new File(new File(tmppath), format.format(new Date())+".tmp");
			tmp.deleteOnExit();

			RandomAccessFile raf = new RandomAccessFile(file, "r");
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
			        tmp)));
			
			pw.println("<?xml version=\"1.0\"?>");
			pw.println("<param>");

			long len = raf.length();

			long skipped = len - PARAM_BYTES;
			if (skipped < 0)
				skipped = 0;

			raf.seek(skipped);
			String line;
			while (!(line = raf.readLine()).equals("</bioml>")) {
				
				if (line
				        .equals("<group label=\"input parameters\" type=\"parameters\">")) {

					while (!(line = raf.readLine()).equals("</group>")) {
						pw.println(line);
					}

					continue;
				}

				if (line
				        .equals("<group label=\"performance parameters\" type=\"parameters\">")) {
					while (!(line = raf.readLine()).equals("</group>")) {
						pw.println(line);
					}

					continue;
				}

				// if(line.equals("<group label=\"unused input parameters\"
				// type=\"parameters\">"));
			}

			pw.println("</param>");
			
			raf.close();
			pw.close();

			return new XTandemParameter(tmp, null);

		} catch (Exception e) {
			throw new ParameterParseException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil
	 *      .AbstractPeptideReader#getPeptideImp()
	 */
	@Override
	protected XTandemPeptide getPeptideImp() throws PeptideParsingException {
		XMLEvent event;
		try {
L0:			while (reader.hasNext()) {// end
	
				event = reader.nextEvent();
				
				if(event.isEndDocument()){
					return null;
				}

				if (event.isStartElement()) {
					StartElement se = ((StartElement) event);
					String name = se.getName().getLocalPart();

					if (name.equals("group")) {
						
						/*
						 * Not the start of a peptide identification even though the tag is 
						 * also "group". May be parameter, or others?
						 */
						if(se.getAttributeByName(id) == null) 
								continue;
						
						
						String scanNum = null;
						// double mh = 0;
						double theomh = 0;
						double deltamh = 0;
						float hscore = 0;
						float nexts = 0;
						float yscore = 0;
						float bscore = 0;
						String sequence = null;
						String rawseq = null;

						
						/*
						 * ------------------
						 */
						// mh = Double.parseDouble(se.getAttributeByName(new
						// QName("mh")).getValue());

						short charge = Short.parseShort(se.getAttributeByName(z)
						        .getValue());
						double evalue = Double.parseDouble(se.getAttributeByName(
						        expect).getValue());

						// other scores
						
						
						

						// tag for peptide sequence, to make sure the sequence
						// was parsed once
						boolean pepparsed = false;
						
						//Because other entries also used group as the tag, mark this to determine 
						//whether the full information for a peptide has been read in.
						int innerGroup = 0;
						HashSet<ProteinReference> pros = new HashSet<ProteinReference>();
L1:						while (true) {
							event = reader.nextEvent();
							
							if(event.isStartElement()){
								se = ((StartElement) event);
								name = se.getName().getLocalPart();

								if (name.equals("protein")) {

									
									/*
									 * ------------ The peptide sequence
									 */
									if (!pepparsed) {

										PeptideSequence pepseq = null;
										LinkedList<XTandemMod> mods = new LinkedList<XTandemMod>();
										ArrayList<Integer> indexes = new ArrayList<Integer>(
										        4);
										int seqstart = 0;

										while ((!(event = reader.nextEvent()).isEndElement())
												|| (!((EndElement) event).getName().getLocalPart().equals("protein"))) {

											if (event.isStartElement()) {
												StartElement se1 = (StartElement) event;
												name = se1.getName().getLocalPart();

												if (name.equals("domain")) {
													deltamh = Double.parseDouble(se1.getAttributeByName(delta).getValue());
													hscore = Float.parseFloat(se1.getAttributeByName(hyperscore).getValue());
													nexts = Float.parseFloat(se1.getAttributeByName(nextscore).getValue());
													yscore = Float.parseFloat(se1.getAttributeByName(y_score).getValue());
													bscore = Float.parseFloat(se1.getAttributeByName(b_score).getValue());
													
													rawseq = se1.getAttributeByName(seq).getValue();
													String seqpre = se1.getAttributeByName(pre).getValue();
													char preaa = seqpre.charAt(seqpre.length() - 1);
													if(preaa == '[') preaa = '-';
													
													String seqafter = se1.getAttributeByName(post).getValue();
													char postaa = seqafter.charAt(0);
													if(postaa == ']') postaa = '-';

													pepseq = new PeptideSequence(rawseq,preaa,postaa);

													seqstart = Integer.parseInt(se1.getAttributeByName(start).getValue());
													theomh = Double.parseDouble(se1.getAttributeByName(mh).getValue());
													
													continue;
												}

												if (name.equals("aa")) {// modification
													double modi = Double.parseDouble(se1.getAttributeByName(modified).getValue());
													XTandemMod mod = this.parameter.getVariableModForMass(modi);
													int idx = Integer.parseInt(se1.getAttributeByName(at).getValue())- seqstart+ 1;
													
													if(mod!=null){
														mods.add(mod);
														indexes.add(idx);
													}
													
													else{
														//Test whether a undefined modification (I current don't know how this happened)
													
														if(!this.parameter.isFixModif(modi)){
															
															//Warning: modification -Mono: -17.0265; Avg: -17.0265 @C- is not a pre-defined modification, will be ignored.
															//Warning: modification -Mono: -18.0106; Avg: -18.0106 @E- is not a pre-defined modification, will be ignored.
															//Warning: modification -Mono: -17.0265; Avg: -17.0265 @Q- is not a pre-defined modification, will be ignored.

															/*
															 * XTandem will automatically add the 3 above modifications
															 * 
															 * I don't really know how XTandem defined this, for a enzymatically cleaved peptides, the actual mass should 
															 * add 18 to the mass of all amino acids. Just ignore these type of peptides for the consistent to other search 
															 * algorithms
															 */
															if(idx == 1){
																continue L0;
															}

															
															System.err.println("Warning: modification "+modi+"@"+se1.getAttributeByName(type).getValue()+" is not a pre-defined modification, will be ignored.");
														}
													
													}
													
													continue;
												}
											}
										}

										int siz = indexes.size();
										int[] ids = new int[siz];

										for (int i = 0; i < siz; i++) {
											ids[i] = indexes.get(i);
										}
										
										sequence = this.parseSequence(pepseq,
										        mods.toArray(new XTandemMod[mods
										                .size()]), ids);
										
										pepparsed = true;
									}
									else{
										
										/*
										 * For some conditions, peptide sequence with 
										 * the same mass but different sequence will be 
										 * output as the same group for XTandem. These 
										 * peptides commonly contains iso aminoacid (I & L)
										 * e.g.
										 * QELSITFIAHSR & QEISITFIAHSR 
										 */
										
										while ((!(event = reader.nextEvent()).isEndElement())
												|| (!((EndElement) event).getName().getLocalPart().equals("protein"))){
											if (event.isStartElement()) {
												StartElement se1 = (StartElement) event;
												name = se1.getName().getLocalPart();

												if (name.equals("domain")) {
													String sequ = se1.getAttributeByName(seq).getValue();
													
													if(!sequ.equals(rawseq)){
														
														//isomer sequence, proteins will not be added to the reference list
														
														System.out.println("Current: "+sequ+", with differnt sequence with raw: "+rawseq);
														
														continue L1;
													}
												}
											}
										}
									}
									
									
									int proidx = Integer.parseInt(se
									        .getAttributeByName(uid).getValue());
									String proname = se.getAttributeByName(label)
									        .getValue();
									
									
									String fullname = this.proteinmap.get(proidx);

									ProteinSequence proseq = this.accesser.getSequence(proidx);
									
									if(fullname == null) {

										if(proseq==null)
											throw new NullPointerException("Cannot find protein with uid: "+proidx+" & name "+proname);
										
										fullname = proseq.getReference();
										
										if(!fullname.contains(proname))
											throw new IllegalArgumentException("Ambiguous protein references, not the same protein database?");
										
										this.proteinmap.put(proidx, fullname);
									}

//									if(this.pool == null)
//										this.pool = new ProteinReferencePool(this.getDecoyJudger());
									
									boolean isDecoy = djudger.isDecoy(fullname);
									String partName;
									if(isDecoy){
										partName = fullname.substring(0, accesser.getSplitRevLength());
									}else{
										partName = fullname.substring(0, accesser.getSplitLength());
									}
									
									ProteinReference ref = new ProteinReference(proseq.index(), partName, isDecoy);
									
//									ProteinReference ref = this.pool.get(proidx, fullname);
									pros.add(ref);
									
									this.proNameAccesser.addRef(ref.getName(), proseq);
									
									continue;
								}
								
								if(name.equals("group")){
									String na = se.getAttributeByName(label).getValue();
									if(na.equals("fragment ion mass spectrum")){
										
										
										while((!(event = reader.nextEvent()).isEndElement()) 
												|| (!((EndElement) event).getName().getLocalPart().equals("group"))){
											if (event.isStartElement()) {
												se = (StartElement) event;
												name = se.getName().getLocalPart();
												
												//Only generate the scan number information
												if(name.equals("note")){
													Attribute att = se.getAttributeByName(label);
													if(att != null && att.getValue().equals("Description")){
														scanNum = ((Characters)reader.nextEvent()).getData();
														
														break;
													}
												}
											}
											
											
											//don't influent the inner group
										}
										
									}
									else{
										innerGroup ++;
									}
									
									continue;
								}
							}
							
							else if(event.isEndElement()){
								String ename = ((EndElement)event).getName().getLocalPart();
								if(ename.equals("group")){
									
									if(innerGroup == 0)
										break;
									else
										innerGroup --;
								}
							}
						}
						
						/*
						 * There may be no peptide identification for some spectra, 
						 * then the tage of pepparsed will be false. Continue the next peptide
						 */
						if(!pepparsed){
							continue;
						}
						
						XTandemPeptide pep = new XTandemPeptide(scanNum, sequence, charge,
						        theomh, deltamh, (short)1, evalue, hscore, nexts, yscore, bscore, pros, this.getPeptideFormat());
						pep.setEnzyme(this.parameter.getEnzyme());
						
						HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
						Iterator <ProteinReference> it = pros.iterator();
						while(it.hasNext()){
							ProteinReference pref = it.next();
							ProteinSequence proseq = accesser.getSequence(pref);
							int beg = proseq.indexOf(rawseq)+1;
							int end = beg + rawseq.length() - 1;
							int preloc = beg-8<0 ? 0 : beg-8;
							String pre = proseq.getAASequence(preloc, beg-1);
							
							String next = "";
							if(end<proseq.length()){
								int endloc = end+7>proseq.length() ? proseq.length() : end+7;
								next = proseq.getAASequence(end, endloc);
							}

							SeqLocAround sla = new SeqLocAround(beg, end, pre, next);
							locAroundMap.put(pref.toString(), sla);
						}
						
						pep.setPepLocAroundMap(locAroundMap);
						
						return pep;
					}
				}
			}
		} catch (Exception ne){
			throw new PeptideParsingException(ne);
		}

		//end of the document
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
	 */
	@Override
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return this.proNameAccesser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.sequest.peptides.readers
	 *      .AbstractSequestPeptideReader#getSearchParameter()
	 */
	@Override
	public XTandemParameter getSearchParameter() {
		return this.parameter;
	}

	@Override
	public void close() {
		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (XMLStreamException e) {
				System.out
				        .println("Error in closing the excel file after reading."
				                + " But it doesn't matter :)");
			}
		}
		System.out.println("Finished reading.");
	}
	
	public static void main(String[] args) throws ImpactReaderTypeException, ParameterParseException, PeptideParsingException, FastaDataBaseException, IOException {
		
//		String file = "H:\\other_search_enginer\\Xtandem\\output.2011_11_07_22_37_54.t.xml";
		String file = "H:\\XTandem\\output.2011_11_29_13_35_15.t.xml";
		String db = "E:\\DataBase\\ipi.HUMAN.v3.80\\Final_ipi.HUMAN.v3.80.fasta";
		IFastaAccesser accesser = new FastaAccesser(db, new DefaultDecoyRefJudger());
		XmlPeptideReader reader = new XmlPeptideReader(file, accesser);

		XTandemPeptide pep;
		while((pep = reader.getPeptide())!= null) {
			System.out.println(pep);
		}
		
		reader.close();
	}

	
}
