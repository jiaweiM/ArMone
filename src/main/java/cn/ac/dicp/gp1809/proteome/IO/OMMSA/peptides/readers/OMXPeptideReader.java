/* 
 ******************************************************************************
 * File: OMXPeptideReader.java * * * Created on 09-05-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.readers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAEnzymes;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.OMSSAParameter;
import cn.ac.dicp.gp1809.proteome.IO.OMMSA.peptides.OMSSAPeptide;
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
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.Enzyme;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ParameterParseException;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeak;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.MS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Peak;
import cn.ac.dicp.gp1809.proteome.spectrum.PrecursePeak;
import cn.ac.dicp.gp1809.proteome.spectrum.TempMapPeakListGettor;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.IScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.DtaFileParsingException;
import cn.ac.dicp.gp1809.proteome.spectrum.dta.ScanDta;
import cn.ac.dicp.gp1809.proteome.spectrum.format.DtaType;
import cn.ac.dicp.gp1809.proteome.util.IScanName;
import cn.ac.dicp.gp1809.proteome.util.ScanNameFactory;
import cn.ac.dicp.gp1809.util.arrayutil.DoubleArrayList;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;

/**
 * OMSSA peptide reader for OMX format OMSSA output
 * 
 * @author Xinning
 * @version 0.3, 05-02-2010, 10:13:24
 */
public class OMXPeptideReader extends AbstractOMSSAPeptideReader implements
        IBatchDtaReader {
	
	/*
	 * The default location of the mods file. Make sure to copy the 
	 * "mods.xml" file to this path
	 */
	private static final String default_mods_loc = "bin/omssa/mods.xml";
	/*
	 * The default location of the user defined mods file. Make sure to copy the 
	 * "usermods.xml" file to this path
	 */
	private static final String default_usermods_loc = "bin/omssa/usermods.xml";
	
	/*
	 * OMSSA often multiply the mass with specific number. I don't actually know 
	 * why.
	 */
	private double mh_multiplier = 1000d;
	
	private File modsfile;
	private File usermodsfile;

	private OMSSAParameter parameter;
	
	private XMLEventReader dtaReader;
	
	/**
	 * The peaklist gettor
	 */
//	private TempFilePeakListGettor gettor = new TempFilePeakListGettor();
	
	private TempMapPeakListGettor gettor = new TempMapPeakListGettor();
	
	private HashMap<Integer, String> nameMap = new HashMap<Integer, String>();
	
	private HashMap<Integer, Double> precursorMap = new HashMap<Integer, Double>();
	
	private HashMap<Integer, String> proteinmap = new HashMap<Integer, String>();
	/**
	 * The indeces of the spectra data
	 */
	private int[] indeces;
	
	private int curtdtaidx = -1;
	
	private String currentName;
	
	/*
	 * List of peptide queue for different charge states
	 */
	private LinkedList<OMSSAPeptide>[] chargeList = new LinkedList[4];
	
	//The current scan number
	private String curtScanNum;
	//The current charge state
	private int curtCharge = 1;
	//The current peptide index
	private int curtPepIdx = 0;
	//The current charge list
	private LinkedList<OMSSAPeptide> curtChargeList;
	
	/**
	 * The index of currently reading peptides of spectra
	 */
	private int curtScanIndex=-1;
	//to avoid duplicated reading of peak list
	private int preScanIndex = -2;
	private IPeakList prepeaklist;
	
	private IFastaAccesser accesser;
	
	private ProteinNameAccesser proNameAccesser;
	
	private ProteinReferencePool pool;

	/**
	 * Create the reader for omx file. Make sure mods.xml file and usermods.xml 
	 * file were copied to the directory of "omssa\\" in the same directory as 
	 * jar file
	 * 
	 * @throws ParameterParseException
	 * @throws ImpactReaderTypeException
	 * @throws IOException 
	 * 
	 */
	public OMXPeptideReader(String filename, IFastaAccesser accesser) throws ImpactReaderTypeException,
	        ParameterParseException, IOException {
		this(new File(filename), accesser);
	}
	
	/**
	 * Create the reader for omx file. Make sure mods.xml file and usermods.xml 
	 * file were copied to the directory of "omssa\\" in the same directory as 
	 * jar file
	 * 
	 * @throws ParameterParseException
	 * @throws ImpactReaderTypeException
	 * @throws IOException 
	 * 
	 */
	public OMXPeptideReader(File file, IFastaAccesser accesser) throws ImpactReaderTypeException,
	        ParameterParseException, IOException {
		super(file);
		
		this.modsfile = new File(default_mods_loc);
		this.usermodsfile = new File(default_usermods_loc);

//		if(!this.modsfile.exists()){
		if(modsfile==null){
			throw new ParameterParseException("Please copy \"mods.xml\" file " +
					"to omssa\\ directory in the jar location directory.");
		}

		this.accesser = accesser;
		this.proNameAccesser = new ProteinNameAccesser(accesser);

		this.parse();
	}
	
	/**
	 * @throws ParameterParseException
	 * @throws ImpactReaderTypeException
	 * @throws IOException 
	 * 
	 */
	public OMXPeptideReader(String filename, String modsfile,
	        String usermodsfile, IFastaAccesser accesser) throws ImpactReaderTypeException,
	        ParameterParseException, IOException {
		this(new File(filename), new File(modsfile), new File(usermodsfile), accesser);
	}

	public OMXPeptideReader(File file, File modsfile, File usermodsfile, IFastaAccesser accesser)
	        throws ImpactReaderTypeException, ParameterParseException, IOException {
		
		super(file);

		this.modsfile = modsfile;
		this.usermodsfile = usermodsfile;

		this.accesser = accesser;
		this.proNameAccesser = new ProteinNameAccesser(accesser);
		
		this.parse();
	}
	
	/**
	 * Parse the file and begin to read peptides
	 * 
	 * @throws IOException
	 * @throws ParameterParseException
	 */
	private void parse() throws IOException, ParameterParseException {

		
		try {
			System.out.println("Prereading the omx file ...");
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
	        dtaReader = factory.createXMLEventReader(new FileInputStream(this.getFile()));
	        
	        System.out.println("Parsing peak list ...");
	        this.parsePeakList();
	        
	        System.out.println("Parsing parameters ...");
	        this.parseParameter();
	        
	        System.out.println("Begin reading ...");

        } catch (XMLStreamException e) {
	        throw new IOException("Error while parsing omx file.", e);
        }
	}
	
	/**
	 * Parse the peak list
	 * 
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private void parsePeakList() throws FileNotFoundException, XMLStreamException {
		
		DoubleArrayList mzlist = new DoubleArrayList();
		DoubleArrayList intenlist = new DoubleArrayList();
		
		boolean parsed = false;
		/*
		 * Bug? the scale is only for abundance
		 */
		double mzscale = 1000;
		double scale = 10000;
		
		String scanname = null;
		double precursorMz = 0;
		short charge = 0;
		int spec_idx = -1;
		
		IntArrayList idxlist = new IntArrayList();
		
		XMLEvent event;
		while (!(event = dtaReader.nextEvent()).isEndDocument()) {
			if (event.isStartElement()) {
				StartElement se = ((StartElement) event);
				String name = se.getName().getLocalPart();

				if (name.equals("MSSpectrum")) {
					
					while(true) {
						event = dtaReader.nextEvent();
						if(event.isEndElement()) {
							//end of this spectrum
							if(((EndElement)event).getName().getLocalPart().equals("MSSpectrum")) {
								break;
							}
						}
						
						if(event.isStartElement()) {
							name = ((StartElement)event).getName().getLocalPart();
							
							//start of the mz values array
							if(name.equals("MSSpectrum_mz")) {
								while(true) {
									event = dtaReader.nextEvent();
									if(event.isStartElement()) {
										if(((StartElement)event).getName().getLocalPart().equals("MSSpectrum_mz_E")) {
											event = dtaReader.nextEvent();
											double value = Long.parseLong(((Characters)event).getData().trim());
											mzlist.add(value);
											
											continue;
										}
									}
									
									if(event.isEndElement()) {
										if(((EndElement)event).getName().getLocalPart().equals("MSSpectrum_mz"))
											break;
									}
								}
								
								continue;
							}
							
							if(name.equals("MSSpectrum_abundance")) {
								while(true) {
									event = dtaReader.nextEvent();
									if(event.isStartElement()) {
										if(((StartElement)event).getName().getLocalPart().equals("MSSpectrum_abundance_E")) {
											event = dtaReader.nextEvent();
											double value = Long.parseLong(((Characters)event).getData().trim());
											intenlist.add(value);
											
											continue;
										}
									}
									
									if(event.isEndElement()) {
										if(((EndElement)event).getName().getLocalPart().equals("MSSpectrum_abundance"))
											break;
									}
								}
								
								continue;
							}
							
							if(name.equals("MSSpectrum_iscale")) {
								
								if(parsed)
									continue;
								
								event = dtaReader.nextEvent();
								scale = Long.parseLong(((Characters)event).getData().trim());
								parsed = true;
								continue;
							}
							
							if(name.equals("MSSpectrum_ids_E")) {
								event = dtaReader.nextEvent();
								scanname = ((Characters)event).getData().trim();
								continue;
							}
							
							if(name.equals("MSSpectrum_precursormz")) {
								event = dtaReader.nextEvent();
								precursorMz = Long.parseLong(((Characters)event).getData().trim());
								continue;
							}
							
							if(name.equals("MSSpectrum_charge_E")) {
								
								if(charge != 0) {
									System.err.println("With more than one charge states for scan: "+spec_idx);
								}
								
								event = dtaReader.nextEvent();
								charge = Short.parseShort(((Characters)event).getData().trim());
								continue;
							}
							
							if(name.equals("MSSpectrum_number")) {
								event = dtaReader.nextEvent();
								spec_idx = Integer.parseInt(((Characters)event).getData().trim());
								continue;
							}
						}
					}
					
					int len = mzlist.size();
					if(len != intenlist.size()) {
						throw new IllegalArgumentException(
								"The mz array and intensity array is not with identical number of ids for scan : "+scanname);
					}
					
					IMS2PeakList peaklist = new MS2PeakList();
					double[] mzs = mzlist.toArray();
					double[] intens = intenlist.toArray();

					for(int i=0; i<len; i++) {
						IPeak peak = new Peak(mzs[i]/mzscale, intens[i]/scale);
						peaklist.add(peak);
					}
					
					PrecursePeak ppeak = new PrecursePeak();
					ppeak.setCharge(charge);
					double pMz = precursorMz/mzscale;
					ppeak.setMz(pMz);
					peaklist.setPrecursePeak(ppeak);
					
					gettor.addPeakList(spec_idx, peaklist);
					idxlist.add(spec_idx);
					nameMap.put(spec_idx, scanname);
					precursorMap.put(spec_idx, pMz);
					
					
					scanname = null;
					precursorMz = 0;
					charge = 0;
					spec_idx = -1;
					
					mzlist = new DoubleArrayList();
					intenlist = new DoubleArrayList();
					
				}
				
				
				continue;
			}
			
			if(event.isEndElement()) {
				//end of reading peak list
				if(((EndElement)event).getName().getLocalPart().equals("MSRequest_spectra")) {
//					gettor.finishedAdding();
					break;
				}
			}
		}
		
		this.indeces = idxlist.toArray();
		this.curtdtaidx = this.indeces.length >0 ? this.indeces[0] : 0;
	}

	/**
	 * Parse the parameter file after the peak list
	 * 
	 * @throws ParameterParseException
	 */
	private void parseParameter() throws ParameterParseException {
		try {
			
			OMSSAEnzymes oenzymes= new OMSSAEnzymes();
			
			Enzyme enzyme = null;
			LinkedList<Integer> variableMods = new LinkedList<Integer>();
			LinkedList<Integer> fixMods = new LinkedList<Integer>();
			boolean mono_precursor = true;
			boolean mono_fragment = true;
			
			
			XMLEventReader reader = this.dtaReader;

			XMLEvent event;
			while (!(event = reader.nextEvent()).isEndDocument()) {
				if (event.isStartElement()) {
					StartElement se = ((StartElement) event);
					String name = se.getName().getLocalPart();
					
					if (name.equals("MSSearchSettings_db")) {
//						database = ((Characters)reader.nextEvent()).getData();
						continue;
					}

					if (name.equals("MSEnzymes")) {
						enzyme = oenzymes.getEnzyme(Integer.parseInt(((Characters)reader.nextEvent()).getData()));
						continue;
					}

					if (name.equals("MSSearchSettings_fixed")) {
						
						while((!(event = reader.nextEvent()).isEndElement()) 
							|| (!((EndElement)event).getName().getLocalPart().equals("MSSearchSettings_fixed"))){
							
							if (event.isStartElement()) {
								se = ((StartElement) event);
								name = se.getName().getLocalPart();
								
								if(name.equals("MSMod")){
									fixMods.add(Integer.valueOf(((Characters)reader.nextEvent()).getData()));
								}
								
							}
						}

						continue;
					}

					if (name.equals("MSSearchSettings_variable")) {
						
						while((!(event = reader.nextEvent()).isEndElement()) 
							|| (!((EndElement)event).getName().getLocalPart().equals("MSSearchSettings_variable"))){
							
							if (event.isStartElement()) {
								se = ((StartElement) event);
								name = se.getName().getLocalPart();
								
								if(name.equals("MSMod")){
									variableMods.add(Integer.valueOf(((Characters)reader.nextEvent()).getData()));
								}
							}
						}

						continue;
					}
					
					if (name.equals("MSSearchSettings_precursorsearchtype")) {
						
						while((!(event = reader.nextEvent()).isEndElement()) 
								|| (!((EndElement)event).getName().getLocalPart().equals("MSSearchSettings_precursorsearchtype"))){
								
							if (event.isStartElement()) {
								se = ((StartElement) event);
								name = se.getName().getLocalPart();
								
								if(name.equals("MSSearchType")){
									int parentType = Integer.parseInt(((Characters)reader.nextEvent()).getData());
								
									switch(parentType){
									case 0: mono_precursor = true; break;
									case 1: mono_precursor = false; break;
									default: throw new IllegalArgumentException("Unknown mass type: "+parentType);
									}
								}
							}
						}
						continue;
					}
					
					
					if (name.equals("MSSearchSettings_productsearchtype")) {
						
						while((!(event = reader.nextEvent()).isEndElement()) 
								|| (!((EndElement)event).getName().getLocalPart().equals("MSSearchSettings_productsearchtype"))){
								
							if (event.isStartElement()) {
								se = ((StartElement) event);
								name = se.getName().getLocalPart();
								
								if(name.equals("MSSearchType")){
									int parentType = Integer.parseInt(((Characters)reader.nextEvent()).getData());
								
									switch(parentType){
									case 0: mono_fragment = true; break;
									case 1: mono_fragment = false; break;
									default: throw new IllegalArgumentException("Unknown mass type: "+parentType);
									}
								}
							}
						}
						continue;
					}
				}
				
				if(event.isEndElement()) {
					if(((EndElement)event).getName().getLocalPart().equals("MSRequest_settings")){
						break;
					}
				}
			}
			
			int[] variableModifs = new int[variableMods.size()];
			for(int i=0; i< variableMods.size(); i++)
				variableModifs[i] = variableMods.get(i).intValue();
			
			int[] fixModifs = new int[fixMods.size()];
			for(int i=0; i< fixMods.size(); i++)
				fixModifs[i] = fixMods.get(i).intValue();
			
			this.parameter =  new OMSSAParameter(modsfile.getAbsolutePath(), usermodsfile.getAbsolutePath(), 
					enzyme, fixModifs,
			        	variableModifs, mono_precursor, mono_fragment);
			
		}  catch (ModsReadingException e) {
			throw new ParameterParseException(
			        "Errors occur when parsing the Mmodification information", e);
        }catch (Exception e) {
			throw new ParameterParseException(
			        "Errors occur when parsing the parameters", e);
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.OMMSA.peptides.AbstractOMSSAPeptideReader
	 *      #getParameter()
	 */
	@Override
	public OMSSAParameter getSearchParameter() {
		return this.parameter;
	}
	
	/*
	 * Only the top matched peptide will be returned.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.OMMSA.peptides.IOMSSAPeptideReader
	 *      #getOMSSAPeptide()
	 */
	@Override
	protected OMSSAPeptide getPeptideImp() throws PeptideParsingException {
		try {
			
L1:			while(this.curtChargeList==null || this.curtPepIdx>=this.getTopN()){
	
				this.curtPepIdx = 0;
				
				while(++this.curtCharge < this.chargeList.length){
					if((this.curtChargeList = this.chargeList[this.curtCharge])!=null){
						continue L1;
					}
				}
				
				//End of the current spectrum
				this.curtCharge = 0;
				this.curtChargeList = null;
				this.curtScanNum = this.paseNextScan();
				
				//End of the data set
				if(this.curtScanNum==null){
					System.out.println("Finished.");
					return null;
				}
			}
			
			if(this.curtChargeList.size()>0){
				OMSSAPeptide pep = this.curtChargeList.pop();
				
				pep.setScanNum(this.curtScanNum);
				
				this.curtPepIdx ++;
				
				return pep;
			}
			else{
				this.curtChargeList = null;
				
				return this.getPeptideImp();
			}
			
		}catch (Exception ne){
			throw new PeptideParsingException(ne);
		}
	}
	
	/*
	 * Get the identified peptides for the next spectrum <b>with peptide identifications</b>.
	 * 
	 * @return the scan number of the next scan
	 * @throws PeptideParsingException 
	 */
	private String paseNextScan() throws PeptideParsingException{
		try {

			/*
			 * Clear the list 
			 */
			for(int i=0; i< this.chargeList.length; i++){
				if(this.chargeList[i] != null)
					this.chargeList[i] = null;
			}
			
			
			String scanNum = null;
			XMLEvent event;
			while (!(event = this.dtaReader.nextEvent()).isEndDocument()) {
				if (event.isStartElement()) {//StartElement
					String name = ((StartElement) event).getName().getLocalPart();
					
					if(name.equals("MSHitSet")){//A spectrum
						//If there are peptide hits
						
						while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
								|| (!((EndElement)event).getName().getLocalPart().equals("MSHitSet"))){
							if(event.isStartElement()){//StartElement
								name = ((StartElement) event).getName().getLocalPart();
								
								/*
								 * The index
								 */
								if(name.equals("MSHitSet_number")) {
									XMLEvent e = this.dtaReader.nextEvent();
									if(!e.isCharacters())
										throw new IllegalArgumentException("Error while parsing the peptides");
									
									this.curtScanIndex = Integer.parseInt(((Characters)e).getData().trim());
									continue;
								}
								
								if(name.equals("MSHitSet_hits")){
									
									//Readin all the peptides for this spectrum
								while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
											|| (!((EndElement)event).getName().getLocalPart().equals("MSHitSet_hits"))){
									
										if(event.isStartElement() && 
												((StartElement) event).getName().getLocalPart().equals("MSHits")){
											
											// double mh = 0;
											double theomh = 0;
											double mh = 0;
											double pvalue = 0;
											double evalue = 0;
											String rawseq = null;
											char preseq = '-';
											char postseq = '-';
											short charge = 0;
											
											HashSet<ProteinReference> pros = new HashSet<ProteinReference>();
											
											//Modificiations
											ArrayList<Integer> mods = null;
											ArrayList<Integer> modifAts = null;
											
											while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
													|| (!((EndElement)event).getName().getLocalPart().equals("MSHits"))){
												if(event.isStartElement()){//StartElement
													name = ((StartElement) event).getName().getLocalPart();
													
													if(name.equals("MSHits_evalue")){
														evalue = Double.parseDouble(((Characters)dtaReader.nextEvent()).getData());
														
														continue;
													}
													
													if(name.equals("MSHits_pvalue")){
														pvalue = Double.parseDouble(((Characters)dtaReader.nextEvent()).getData());
														continue;
													}
													
													if(name.equals("MSHits_charge")){
														charge = Short.parseShort(((Characters)dtaReader.nextEvent()).getData());
														
														continue;
													}
													
													if(name.equals("MSHits_mass")){
														mh = Double.parseDouble(((Characters)dtaReader.nextEvent()).getData())/mh_multiplier;
														continue;
													}
													
													
													if(name.equals("MSHits_theomass")){
														theomh = Double.parseDouble(((Characters)dtaReader.nextEvent()).getData())/mh_multiplier;
														continue;
													}
													
													if(name.equals("MSHits_pepstring")){
														rawseq = ((Characters)dtaReader.nextEvent()).getData();
														continue;
													}
													
													if(name.equals("MSHits_pepstart")){
														
														event = dtaReader.nextEvent();
														if(event.isEndElement()){
															preseq = '-';
														}else
															preseq = ((Characters)event).getData().charAt(0);
														
														continue;
													}
													
													if(name.equals("MSHits_pepstop")){
														
														//<MSHits_pepstop></MSHits_pepstop>
														event = dtaReader.nextEvent();
														if(event.isEndElement()){
															postseq = '-';
														}else
															postseq = ((Characters)event).getData().charAt(0);
														
														continue;
													}
													
													if(name.equals("MSHits_pephits")){
														
														while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
																|| (!((EndElement)event).getName().getLocalPart().equals("MSHits_pephits"))){
															
															if(event.isStartElement()){//StartElement
																name = ((StartElement) event).getName().getLocalPart();
																
																if(name.equals("MSPepHit")){
																	//From 0 - n
																	int proidx = 0;
																	String proname = null;
																	
																	while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
																			|| (!((EndElement)event).getName().getLocalPart().equals("MSPepHit"))){
																		if(event.isStartElement()){//StartElement
																			name = ((StartElement) event).getName().getLocalPart();
																			
																			if(name.equals("MSPepHit_oid")){
																				proidx = Integer.parseInt(((Characters)dtaReader.nextEvent()).getData())+1;
																				continue;
																			}
																			
																			if(name.equals("MSPepHit_defline")){
																				proname = ((Characters)dtaReader.nextEvent()).getData();
																				continue;
																			}
																		}
																	}
																	
																	String fullname = this.proteinmap.get(proidx);
																	
																	ProteinSequence proseq = accesser.getSequence(proidx);
																	
																	if(fullname == null) {

																		if(proseq==null)
																			throw new NullPointerException("Cannot find protein with uid: "+proidx+" & name "+proname);
																		
																		fullname = proseq.getReference();
																		
																		if(!fullname.contains(proname))
																			throw new IllegalArgumentException("Ambiguous protein references, not the same protein database?");
																		
																		this.proteinmap.put(proidx, fullname);
																	}
																	
																	if(this.pool == null)
																		this.pool = new ProteinReferencePool(this.getDecoyJudger());
																	
																	ProteinReference ref = pool.get(proidx, fullname);
																	this.accesser.renewReference(ref);
																	this.proNameAccesser.addRef(ref.getName(), proseq);
																	
																	pros.add(ref);
																}
															}

														}

														
														continue;
													}//~~proteins
													
													
													if(name.equals("MSHits_mods")){
														
														mods = new ArrayList<Integer> ();
														modifAts = new ArrayList<Integer> ();
														
														while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
																|| (!((EndElement)event).getName().getLocalPart().equals("MSHits_mods"))){
															
															if(event.isStartElement()){//StartElement
																name = ((StartElement) event).getName().getLocalPart();
																
																if(name.equals("MSModHit")){
																	
																	//The case that more than one modification on a aminoacid is ignorated.
																	
																	while((!(event = this.dtaReader.nextEvent()).isEndElement()) 
																			|| (!((EndElement)event).getName().getLocalPart().equals("MSModHit"))){
																		
																	
																		if(event.isStartElement()){//StartElement
																			name = ((StartElement) event).getName().getLocalPart();
																			
																			if(name.equals("MSModHit_site")){
																				modifAts.add(Integer.parseInt(((Characters)dtaReader.nextEvent()).getData())+1);
																				continue;
																			}
																			
																			if(name.equals("MSMod")){
																				Integer v = Integer.valueOf(((Characters)dtaReader.nextEvent()).getData());
																				mods.add(v);
																				continue;
																			}
																		}
																	}
																}
															}
															
														}
														
														continue;
													}
												
												}
											}//~~A peptide hit
											
											
											
											/*
											 * Only the new best matched peptide (with minimum Evalue) can come here!!
											 * 
											 * =========================================================
											 * The peptide is a "new best matched" peptide or is a duplicated entry for a 
											 * same sequence with different references.
											 */
											PeptideSequence pseq = new PeptideSequence(rawseq, preseq, postseq);
											String seq;
											if(mods != null){
												int siz = mods.size();
												int[] modsi = new int[siz];
												int[] modsati = new int[siz];
												
												for(int i=0; i<siz; i++){
													
													modsi[i] = mods.get(i).intValue();
													modsati[i] = modifAts.get(i).intValue();
												}
												
												seq = this.parameter.parseSequence(pseq, modsi, modsati);
											}
											else{
												seq = pseq.toString();
											}
											
											//The scannum is null, currently
											OMSSAPeptide pep = new OMSSAPeptide(scanNum, seq, charge, theomh, mh-theomh, (short)0,
													evalue, pvalue, pros, this.getPeptideFormat());
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
											
											//Enlarge the charge list
											if(charge >= this.chargeList.length){
												LinkedList<OMSSAPeptide>[] tmplist = new LinkedList[charge+1];
												System.arraycopy(this.chargeList, 0, tmplist, 0, this.chargeList.length);
												
												this.chargeList = tmplist;
											}
											
											
											if(chargeList[charge] == null)
												chargeList[charge] = new LinkedList<OMSSAPeptide>();
											
											//Set the rank 
											pep.setRank((short)(chargeList[charge].size() + 1));
											chargeList[charge].add(pep);
											
										}
									}
								} //~ peptide hits information
								
								
								if(name.equals("MSHitSet_ids_E")){
									scanNum = ((Characters)dtaReader.nextEvent()).getData();
									continue;
								}
							}
						}
						
						return scanNum;
					}
				}
			}
			
			this.curtScanIndex = -1;
			return null;
		}catch (Exception ne){
			throw new PeptideParsingException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.peptideIO.ioUtil.IPeptideReader#close()
	 */
	@Override
	public void close() {
		try {
	        this.dtaReader.close();
        } catch (XMLStreamException e) {
	        System.err.println("Error while closing the omx file, but it maybe not matter :)");
        }
        
		this.gettor.dispose();
		System.out.println("Finish, file closed.");
	}
	
	
	/**
	 * The scan data for current readin peptide, if no peptide has been read, return null.
	 * 
	 * @param isIncludePeakList
	 * @return
	 * @throws DtaFileParsingException
	 */
    public IScanDta getCurtDta(boolean isIncludePeakList)
            throws DtaFileParsingException {
    	return this.getScanDta(isIncludePeakList, this.curtScanIndex);
    }
    
    /**
     * The scan dta
     * @param isIncludePeakList
     * @param idx
     * @return
     */
    protected IScanDta getScanDta(boolean isIncludePeakList, int idx) {
    	if(idx<0)
    		return null;
    	
    	String name = this.nameMap.get(idx);
    	
    	if(name == null)
    		return null;
    	IScanName scanname = ScanNameFactory.parseName(name);
    	
		if(isIncludePeakList) {
			IMS2PeakList peaklist = (IMS2PeakList) this.gettor.getPeakList(idx);
			return new ScanDta(scanname, peaklist);
		}
		else {
			double premz = this.precursorMap.get(idx);
			return new ScanDta(scanname, premz);
		}
    }

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getDtaType()
	 */
	@Override
    public DtaType getDtaType() {
	    return DtaType.SEARCHOUT;
    }

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNameofCurtDta()
	 */
	@Override
    public String getNameofCurtDta() {
	    return this.currentName;
    }

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNumberofDtas()
	 */
	@Override
    public int getNumberofDtas() {
	    return this.indeces.length;
    }

	/*
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.spectrum.dta.IBatchDtaReader#getNextDta(boolean)
	 */
	@Override
    public IScanDta getNextDta(boolean isIncludePeakList)
            throws DtaFileParsingException {
		
		if(++this.curtdtaidx >= this.indeces.length)
			return null;
		
		int idx = this.indeces[this.curtdtaidx];
		
		return this.getScanDta(isIncludePeakList, idx);
    }
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
	 */
	@Override
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return proNameAccesser;
	}
	
	public static void main(String[] args) throws ImpactReaderTypeException, ParameterParseException, IOException, PeptideParsingException, DtaFileParsingException, FastaDataBaseException {
		
		String omxfile = "H:\\other_search_enginer\\OMSSA\\test.omx";
		String database = "E:\\DataBase\\ipi.HUMAN.v3.80\\ipi.HUMAN.v3.80.fasta";
		IFastaAccesser accesser = new FastaAccesser(database, new DefaultDecoyRefJudger());
		OMXPeptideReader reader = new OMXPeptideReader(omxfile, accesser);
		
		OMSSAPeptide pep;
		while((pep = reader.getPeptide()) !=null) {
			System.out.println(pep);
			reader.getCurtDta(true);
		}
		
		reader.close();

		
//		File f = new File(OMXPeptideReader.default_mods_loc);
//		System.out.println(f.exists());
		
	}

	
}
