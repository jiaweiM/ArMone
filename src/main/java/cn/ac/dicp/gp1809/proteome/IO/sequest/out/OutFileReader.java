/* 
 ******************************************************************************
 * File: OutFileReader.java * * * Created on 04-29-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or 
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.sequest.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

import cn.ac.dicp.gp1809.proteome.IO.sequest.out.OutFile.Hit;
import cn.ac.dicp.gp1809.proteome.databasemanger.IFastaAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.util.SequestScanName;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;

/**
 * Reader for a single .out file
 * 
 * <p>Changes:
 * <li>0.3.1, 07-20-2009: v0.28 Sequest may not contains the Sf entry (by batch search)
 * 
 * @author Xinning
 * @version 0.3.3, 09-14-2010, 19:03:00
 */
public class OutFileReader implements IOutFileReader{
	/**
	 *  PlatForm dependent turn for file writing.
	 */
	private static final String lineSeparator = IOConstant.lineSeparator;
	
	private IFastaAccesser accesser;
	private IDecoyReferenceJudger judger;
	private BufferedReader reader;
	private boolean isCloseAfterReading = true;
	private boolean has_pro_id;
	private boolean has_sf;
	
	private OutHeader header;
	
	/**
	 * create a out file from the specified file
	 * 
	 * @param outFile
	 * @throws FileNotFoundException 
	 */
	public OutFileReader(File outFile, IDecoyReferenceJudger judger) throws FileNotFoundException{
		this(new FileInputStream(outFile), judger, true);
	}
	
	public OutFileReader(File outFile, IDecoyReferenceJudger judger, IFastaAccesser accesser) throws FileNotFoundException{
		this(new FileInputStream(outFile), judger, true, accesser);
	}
	
	/**
	 * create a out file reader from the specified file name
	 * 
	 * @param outFilename
	 * @throws FileNotFoundException 
	 */
	public OutFileReader(String outFilename) throws FileNotFoundException{
		this(new File(outFilename));
	}
	
	/**
	 * create a out file from the specified file
	 * 
	 * @param outFile
	 * @throws FileNotFoundException 
	 */
	public OutFileReader(File outFile) throws FileNotFoundException{
		this(new FileInputStream(outFile), true);
	}
	
	/**
	 * Create a out file reader from the input stream
	 * 
	 * @param instream the input stream containing out file
	 * @param isCloseAfterReading if close the input stream after reading of the out file
	 */
	public OutFileReader(InputStream instream, boolean isCloseAfterReading){
		this(instream, new DefaultDecoyRefJudger(), isCloseAfterReading);
	}
	
	/**
	 * Create a out file reader from the input stream
	 * 
	 * @param instream the input stream containing out file
	 * @param isCloseAfterReading if close the input stream after reading of the out file
	 */
	public OutFileReader(InputStream instream, IDecoyReferenceJudger judger, boolean isCloseAfterReading){
		this.reader = new BufferedReader(new InputStreamReader(instream));
		this.judger = judger == null ? new DefaultDecoyRefJudger(): judger;
		this.isCloseAfterReading = isCloseAfterReading;
	}
	
	public OutFileReader(InputStream instream, IDecoyReferenceJudger judger, boolean isCloseAfterReading, IFastaAccesser accesser){
		this.reader = new BufferedReader(new InputStreamReader(instream));
		this.judger = judger == null ? new DefaultDecoyRefJudger(): judger;
		this.isCloseAfterReading = isCloseAfterReading;
		this.accesser = accesser;
	}
	
	/**
	 * create a out file from the specified file
	 * 
	 * @param outFile
	 * @throws FileNotFoundException 
	 */
	public OutFileReader(File outFile, IFastaAccesser accesser) throws FileNotFoundException{
		this(new FileInputStream(outFile), accesser, true);
	}
	
	/**
	 * Create a out file reader from the input stream.
	 * <b><p> Now this constructor is used.
	 * 
	 * @param instream the input stream containing out file
	 * @param isCloseAfterReading if close the input stream after reading of the out file
	 */
	public OutFileReader(InputStream instream, IFastaAccesser accesser, boolean isCloseAfterReading){
		this.reader = new BufferedReader(new InputStreamReader(instream));
		this.accesser = accesser;
		this.judger = accesser.getDecoyJudger();
		this.isCloseAfterReading = isCloseAfterReading;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.out.IOutFileReader#getOutFile()
	 */
	public OutFile getOutFile() throws OutFileReadingException {
		try{
			
			header = this.getHeader(reader);
			
			
			int version = header.getVersion();
			
			Hit[] hits = null;
			
			switch(version) {
			case 27: hits = this.parseHitsv27(); 

				break;
			case 28: hits = this.parseHitsv28();

				break;
			default: throw new RuntimeException("Unsupported version of SEQUEST: v."+version);
			}
			
		
			if(this.isCloseAfterReading)
				reader.close();
			
			
			OutFile outfile = null;
			if(hits==null || hits.length==0){ // No hit
				outfile = new OutFile(header, has_pro_id, new Hit[0], new ProteinReference[0]);
			}
			else{
				
				int prolen = header.getDisplay_top_n_pro();
				ProteinReference[] profull;
				if(prolen==0){
					profull = new ProteinReference[0];
					outfile = new OutFile(header, has_pro_id, hits, profull);
				}
				else{
					profull = new ProteinReference[0];
					outfile = new OutFile(header, has_pro_id, hits, profull);
					/* currently, useless
					if((line=reader.readLine())!=null&&line.length()>4){
						if(line.charAt(3)=='.'){
							//Skip
							reader.readLine();
							line = reader.readLine();
						}
						
						
						if(prolen>0){
							profull = new Reference[prolen];
							for(int i=0;i<prolen;i++){
								
							}
						}
					}
					*/
				}
			}
			
			
			return outfile;
    	}catch(IOException e){
    		throw new OutFileReadingException("Error occurs while paring the out file.",e);
    	}
    }
	
	
	/**
	 * Parse the hits from SEQUEST version .27
	 * 
	 * @param header
	 * @return
	 * @throws IOException
	 */
	private Hit[] parseHitsv27() throws IOException {
		LinkedList<Hit> list = new LinkedList<Hit>();
		
		String line = reader.readLine();
		while(line.length()>4){
			// a peptide hit
			short rank = Short.parseShort(line.substring(4,9).trim());
			short rsp = Short.parseShort(line.substring(10,13).trim());//rsp
			Pattern SEP = Pattern.compile("\\s+");
			String [] columns  = SEP.split(line.substring(24).trim());
			
			int pro_id = 0;
			if(this.has_pro_id){
				pro_id = Integer.parseInt(line.substring(13,24).trim());
			}
	
			int colNum = columns.length;
			double mh = -1;//mass
			float dcn = -1;//dcn
			float xcorr = -1;//xcorr
			float sp = -1;//sp
			String ions = "";
			String prot = "";
			String sequence = "";
			ProteinReference ref = null;
			int num_ref = 1;
			HashSet<ProteinReference> reflist = new HashSet<ProteinReference>();
			
			/*
			 * Support some version of sequest
			 */
//			start = StringUtil.getNextNoneBlankIndex(line, start);
//			String mhs = line.substring(start,start+=11);
			
			mh = Double.parseDouble(columns[0].trim());//mass
			dcn = Float.parseFloat(columns[1].trim());//dcn
			xcorr = Float.parseFloat(columns[2].trim());//xcorr
			sp = Float.parseFloat(columns[3].trim());//sp
			
			sequence = columns[colNum-1];
			if(columns[colNum-2].trim().startsWith("+")){
				num_ref = Integer.parseInt(columns[colNum-2].substring(1).trim());
				prot = columns[colNum-3];
				
				if(colNum==8){
					ions = columns[4];
				}
				if(colNum==9){
					ions = columns[4]+columns[5];
				}
				
			}
			else{
				num_ref = 1;
				prot = columns[colNum-2];
				
				if(colNum==7){
					ions = columns[4];
				}
				if(colNum==8){
					ions = columns[4]+columns[5];
				}				
			}

			boolean isDecoy = this.judger.isDecoy(prot);
			if(isDecoy){
				prot = prot.substring(0, accesser.getSplitRevLength());
			}else{
				prot = prot.substring(0, accesser.getSplitLength());
			}
			
			ref = new ProteinReference(pro_id, prot, isDecoy);
			reflist.add(ref);
			
			/*
			 * For some version of sequest, the output mh value contains only 4 digest after the 
			 * radix point but not 5 digests. in this condition, the index value should minus some 
			 * value.
			 */
/*			
			int i=1;
			while(mhs.charAt(mhs.length()-i)==' '){
				i++;
			}
			start = start - i + 1;
			
			double mh = Double.parseDouble(mhs.trim());//mass
			float dcn = Float.parseFloat(line.substring(start,start+=8));//dcn
			float xcorr = Float.parseFloat(line.substring(start,start+=8));//xcorr
			float sp = Float.parseFloat(line.substring(start,start+=8).trim());//sp
			String ions = line.substring(start,start+=9).trim();//ions
			
			int end = line.lastIndexOf(' ');
			String sequence = line.substring(end+1);//peptide
			
			////protein reference and the additional proteins
			String prot = line.substring(start,end).trim();
			int num_ref = 1;
			//If contains additional protein reference.
			int idx = prot.indexOf(" +");
			if(idx != -1){
				num_ref += Integer.parseInt(prot.substring(idx+2));
				prot = prot.substring(0, idx).trim();
			}
			ProteinReference ref = ProteinReference.newInstance(pro_id,prot);
			
			HashSet<ProteinReference> reflist = new HashSet<ProteinReference>();
			reflist.add(ref);	
*/				
			/*
			 * Ignore the blank row.
			 * This is mainly because the peptide with Rsp of 1 not displayed in the final list
			 * ,in which the peptides are arranged by their Xcorr.
			 * If the full name of top hit proteins are print, blank
			 */
			while((line = reader.readLine()).length()>4){
				
				if(line.charAt(3)=='.'){//end of the reference, this is a peptide line
					break;
				}
				
				pro_id = -1;
				if(this.has_pro_id){
					String trimline = line.trim();
					int ids = trimline.indexOf(' ');
					/*
					 * Exceptions like the following in srf exported dta out files
					 * 
					 *             116957   REVERSED_IPI:IPI00746007.1|ENSEMBL:ENS
					 *             117819   
					 *             118102   
					 *             118985   
					 *             119132    
					 */
					if(ids != -1)
						pro_id = Integer.parseInt(trimline.substring(0,ids).trim());
					else
						/*
						 * Skip
						 */
						continue ;
					
					prot = trimline.substring(ids).trim();
					boolean isDecoy2 = this.judger.isDecoy(prot);
					if(isDecoy2){
						prot = prot.substring(0, accesser.getSplitRevLength());
					}else{
						prot = prot.substring(0, accesser.getSplitLength());
					}
					
					ref = new ProteinReference(pro_id, prot, isDecoy2);
					reflist.add(ref);
				}
				else{
					prot = line.trim();
					boolean isDecoy2 = this.judger.isDecoy(prot);
					if(isDecoy2){
						prot = prot.substring(0, accesser.getSplitRevLength());
					}else{
						prot = prot.substring(0, accesser.getSplitLength());
					}
					
					ref = new ProteinReference(pro_id, prot, isDecoy2);
					reflist.add(ref);
				}
				
				reflist.add(ref);
			}
//			System.out.println(ions);
			list.add(new Hit(rank,rsp,mh,dcn,xcorr,sp,ions,sequence,num_ref,reflist));
		}
		
		
		return list.toArray(new Hit[list.size()]);
	}
	
	
	/**
	 * Parse the Hits for SEQUEST version .28
	 * 
	 * @param header
	 * @return
	 * @throws IOException
	 */
	private Hit[] parseHitsv28() throws IOException {
		LinkedList<Hit> list = new LinkedList<Hit>();
		
		String line = reader.readLine();
		while(line.length()>4){
			// a peptide hit
			short rank = Short.parseShort(line.substring(4,9).trim());
			short rsp = Short.parseShort(line.substring(10,13).trim());//rsp
			
			Pattern SEP = Pattern.compile("\\s+");
			String [] columns  = SEP.split(line.substring(14).trim());
			int colNum = columns.length;
			int pro_id = 0;
			double mh = -1;//mass
			float dcn = -1;//dcn
			float xcorr = -1;//xcorr
			float sp = -1;//sp
			float sf = -1;
			String ions = "";
			String prot = "";
			String sequence = "";
			ProteinReference ref = null;
			int num_ref = 1;
			
			HashSet<ProteinReference> reflist = new HashSet<ProteinReference>();
			
			if(this.has_pro_id){
				pro_id = Integer.parseInt(columns[0].trim());
			}
			
			if(this.has_sf){
				mh = Double.parseDouble(columns[1].trim());//mass
				dcn = Float.parseFloat(columns[2].trim());//dcn
				xcorr = Float.parseFloat(columns[3].trim());//xcorr
				sp = Float.parseFloat(columns[4].trim());//sp
				sf = Float.parseFloat(columns[5].trim());
				
				
				sequence = columns[colNum-1];
				if(columns[colNum-2].trim().startsWith("+")){
					num_ref = Integer.parseInt(columns[colNum-2].substring(1).trim());
					prot = columns[colNum-3];
					if(colNum==10){
						ions = columns[6];
					}
					if(colNum==11){
						ions = columns[6]+columns[7];
					}
				}
				else{
					num_ref = 1;
					prot = columns[colNum-2];
					if(colNum==9){
						ions = columns[6];
					}
					if(colNum==10){
						ions = columns[6]+columns[7];
					}
				}

				boolean isDecoy = this.judger.isDecoy(prot);
				if(isDecoy){
					if(prot.length()>accesser.getSplitRevLength())
						prot = prot.substring(0, accesser.getSplitRevLength());
				}else{
					if(prot.length()>accesser.getSplitLength())
						prot = prot.substring(0, accesser.getSplitLength());
				}
				
				ref = new ProteinReference(pro_id, prot, isDecoy);
				reflist.add(ref);	
				
			}else{
				
				mh = Double.parseDouble(columns[1].trim());//mass
				dcn = Float.parseFloat(columns[2].trim());//dcn
				xcorr = Float.parseFloat(columns[3].trim());//xcorr
				sp = Float.parseFloat(columns[4].trim());//sp
				sf = -1;
				
				sequence = columns[colNum-1];
				if(columns[colNum-2].trim().startsWith("+")){
					num_ref = Integer.parseInt(columns[colNum-2].substring(1).trim());
					prot = columns[colNum-3];
					if(colNum==9){
						ions = columns[5];
					}
					if(colNum==10){
						ions = columns[5]+columns[6];
					}
				}else{
					num_ref = 1;
					prot = columns[colNum-2];
					if(colNum==8){
						ions = columns[5];
					}
					if(colNum==9){
						ions = columns[5]+columns[6];
					}
				}
				
				boolean isDecoy = this.judger.isDecoy(prot);
				if(isDecoy){
					prot = prot.substring(0, accesser.getSplitRevLength());
				}else{
					prot = prot.substring(0, accesser.getSplitLength());
				}
				
				ref = new ProteinReference(pro_id, prot, isDecoy);
				reflist.add(ref);	
			}
			
		/*
			int start = 13;
			if(this.has_pro_id){
				pro_id = Integer.parseInt(line.substring(13,24).trim());
				start = 24;
			}
			
			
			start = StringUtil.getNextNoneBlankIndex(line, start);
			String mhs = line.substring(start,start+=11);
			
			
			int i=1;
			while(mhs.charAt(mhs.length()-i)==' '){
				i++;
			}
			start = start - i + 1;
			
			double mh = Double.parseDouble(mhs.trim());//mass
			float dcn = Float.parseFloat(line.substring(start,start+=8));//dcn
			float xcorr = Float.parseFloat(line.substring(start,start+=8));//xcorr
			float sp = Float.parseFloat(line.substring(start,start+=8).trim());//sp
			
			float sf = -1;
			if(this.has_sf)
				sf = Float.parseFloat(line.substring(start,start+=6));//sf
			
			String ions = line.substring(start,start+=9).trim();//ions
			
			int end = line.lastIndexOf(' ');
			String sequence = line.substring(end+1);//peptide
			
			////protein reference and the additional proteins
			String prot = line.substring(start,end).trim();
			int num_ref = 1;
			//If contains additional protein reference.
			int idx = prot.indexOf(" +");
			if(idx != -1){
				num_ref += Integer.parseInt(prot.substring(idx+2));
				prot = prot.substring(0, idx).trim();
			}
			ProteinReference ref = ProteinReference.newInstance(pro_id,prot);
			
//			HashSet<ProteinReference> reflist = new HashSet<ProteinReference>();
			reflist.add(ref);	
		*/		
			/*
			 * Ignore the blank row.
			 * This is mainly because the peptide with Rsp of 1 not displayed in the final list
			 * ,in which the peptides are arranged by their Xcorr.
			 * If the full name of top hit proteins are print, blank
			 */
			while((line = reader.readLine()).length()>4){
				
				if(line.charAt(3)=='.'){//end of the reference, this is a peptide line
					break;
				}

				pro_id = -1;
				if(this.has_pro_id){
					String trimline = line.trim();
					int ids = trimline.indexOf(' ');
					/*
					 * Exceptions like the following in srf exported dta out files
					 * 
					 *             116957   REVERSED_IPI:IPI00746007.1|ENSEMBL:ENS
					 *             117819   
					 *             118102   
					 *             118985   
					 *             119132    
					 */
					if(ids != -1)
						pro_id = Integer.parseInt(trimline.substring(0,ids).trim());
					else
						/*
						 * Skip
						 */
						continue ;
					
					prot = trimline.substring(ids).trim();
					
					boolean isDecoy = this.judger.isDecoy(prot);
					if(isDecoy){
						prot = prot.substring(0, accesser.getSplitRevLength());
					}else{
						prot = prot.substring(0, accesser.getSplitLength());
					}
					
					ref = new ProteinReference(pro_id, prot, isDecoy);
					reflist.add(ref);
				}
				else{
					prot = line.trim();
					
					boolean isDecoy = this.judger.isDecoy(prot);
					if(isDecoy){
						prot = prot.substring(0, accesser.getSplitRevLength());
					}else{
						prot = prot.substring(0, accesser.getSplitLength());
					}
					
					ref = new ProteinReference(pro_id, prot, isDecoy);
					reflist.add(ref);
				}
				
				reflist.add(ref);
			}
		
			list.add(new Hit(rank,rsp,mh,dcn,xcorr,sf, sp,ions,sequence,num_ref,reflist));
		}
		
		return list.toArray(new Hit[list.size()]);
	}
	
	
	/*
	 * Get the header information.
	 */
	private OutHeader getHeader(BufferedReader reader) throws IOException{
		reader.readLine();//escape the first blank line
		
		OutHeader header = new OutHeader();
		
		String outName = reader.readLine().substring(1);
		if(outName.charAt(0)=='.')//out by TPP
			outName = outName.substring(2);
		
		
		header.setScanName(new SequestScanName(outName));
		
		header.setSequestInfo(reader.readLine().trim());
		header.setLicence(reader.readLine().trim()+lineSeparator+reader.readLine());
		
		String line = reader.readLine();
		int idx = line.indexOf(',',line.indexOf(',',1)+1)+1;
		header.setDate(line.substring(1,idx));
		int idx2 = line.indexOf(' ',++idx);
		header.setUsed_time(
				Float.parseFloat(line.substring(idx, idx2)));
		idx = line.indexOf(' ',++idx2);//( on)
		header.setComputer(line.substring(idx+4));
		
		
		line = reader.readLine();
		idx = line.indexOf('=');
		idx+=2;
		idx2 = line.indexOf(' ',idx);
		header.setMh(Double.parseDouble(line.substring(idx,idx2)));
		idx2 += 3;
		idx = line.indexOf(' ',idx2);
		header.setPep_tolerance(Float.parseFloat(line.substring(idx2,idx)));
		idx = line.indexOf('=',idx);
		idx2 = line.indexOf(',',++idx);
		header.setFrag_tolerance(Float.parseFloat(line.substring(idx,idx2)));
		idx2 += 2;
		idx = line.indexOf('/',idx2);
		header.setPep_Mono(line.substring(idx2,idx).equals("MONO"));
		header.setFrag_Mono(line.substring(++idx).equals("MOMO"));
		
		
		line = reader.readLine();
		idx = line.indexOf('=')+2;
		idx2 = line.indexOf(',',idx);
		header.setTic(Float.parseFloat(line.substring(idx,idx2)));
		idx = line.indexOf('=',idx)+2;
		idx2 = line.indexOf(',',idx);
		header.setLowest_sp(Float.parseFloat(line.substring(idx,idx2)));
		idx = line.indexOf('=',idx)+2;
		header.setMatch_pep_count(Integer.parseInt(line.substring(idx)));
		
		// # amino acids = 203339, # proteins = 5875, G:\database\yeast_orf_trans_a_casein.fasta, G:\database\yeast_orf_trans_a_casein_phos_ms3.fasta.hdr
		line = reader.readLine();
		idx = line.indexOf('=')+2;
		idx2 = line.indexOf(',',idx);
		header.setAa_count(Integer.parseInt(line.substring(idx, idx2)));
		idx = line.indexOf('=',idx)+2;
		idx2 = line.indexOf(',',idx);
		header.setPro_in_db(Integer.parseInt(line.substring(idx,idx2)));
		idx2 += 2;
		idx = line.indexOf(',',idx2);
		if(idx!=-1){//two database because the last database is the indexed database for search
			header.setIndex_db(line.substring(idx+2), line.substring(idx2,idx));
		}
		else{
			header.setFasta_db(line.substring(idx2));
		}
		
		//ion series nABY ABCDVWXYZ: 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0
		line = reader.readLine();
		header.setIon_serises(line.substring(line.indexOf(':')+1));
		
		
		line = reader.readLine();
		idx = line.indexOf('/',13);
		header.setDisplay_top_n_pep(Integer.parseInt(line.substring(13,idx)));
		idx2 = line.indexOf(',',++idx);
		header.setDisplay_top_n_pro(Integer.parseInt(line.substring(idx,idx2)));
		idx = line.indexOf('=',idx2)+2;
		idx2 = line.indexOf(',',idx);
		header.setIon_percent(Float.parseFloat(line.substring(idx,idx2)));
		idx = line.indexOf('=',idx2)+2;
		header.setCode(line.substring(idx));
		
		
		line = reader.readLine();
		idx = line.indexOf("Enzyme:");
		String modif;
		if(idx==-1){//Old version of sequest, only found in rev .0 in TPP
			modif = line.substring(1).trim();
			header.setEnzyme("None selected");
		}
		else{
			modif = line.substring(1,idx).trim();
			header.setEnzyme(line.substring(idx+7).trim());
		}
		header.setModification(modif);
		
		reader.readLine();//blank line
		
		line = reader.readLine();
		header.setHit_description(line);
		if(line.contains("Id#")){
			this.has_pro_id = true;
		}
		
		/*
		 * For v.28 and latter??? (contains sf?)
		 */
		if(line.indexOf("Sf ")!=-1) {
			this.has_sf = true;
		}
		
		header.setHit_lines(reader.readLine());
		
		return header;
	}
	
	public static void main(String[] args) throws OutFileReadingException, IOException{
		String file = "E:\\Data\\dmm_test\\cancer_50mM.5742.5742.2.out";
		InputStream is = new FileInputStream(file);
		OutFileReader reader = new OutFileReader(is, false);
		OutFile out = reader.getOutFile();

		OutHeader header = out.getHeader();

		System.err.println("Version: "+header.getVersion());
		System.err.println("Aa_count: "+header.getAa_count());
		System.err.println("Basename: "+header.getScanName().getBaseName());
		System.err.println("Charge: "+header.getScanName().getCharge());
		System.err.println("Code: "+header.getCode());
		System.err.println("Computer: "+header.getComputer());
		System.err.println("Date: "+header.getDate());
		System.err.println("getDisplay_top_n_pep: "+header.getDisplay_top_n_pep());
		System.err.println("getDisplay_top_n_pro: "+header.getDisplay_top_n_pro());
		System.err.println("Enzyme: "+header.getEnzyme());
		System.err.println("getFasta_db: "+header.getFasta_db());
		System.err.println("getFrag_tolerance: "+header.getFrag_tolerance());
		System.err.println("getHit_description: "+header.getHit_description());
		System.err.println("getHit_lines: "+header.getHit_lines());
		System.err.println("getIndex_db: "+header.getIndex_db());
		System.err.println("getIon_percent: "+header.getIon_percent());
		System.err.println("getIon_serises: "+header.getIon_serises());
		System.err.println("getLicence: "+header.getLicence());
		System.err.println("getLowest_sp: "+header.getLowest_sp());
		System.err.println("getMatch_pep_count: "+header.getMatch_pep_count());
		System.err.println("getMh: "+header.getMh());
		System.err.println("getModification: "+header.getModification());
		System.err.println("getPep_tolerance: "+header.getPep_tolerance());
		System.err.println("getPro_in_db: "+header.getPro_in_db());
		System.err.println("getScanNumberBeg: "+header.getScanName().getScanNumBeg());
		System.err.println("getScanNumberEnd: "+header.getScanName().getScanNumEnd());
		System.err.println("getSequestInfo: "+header.getSequestInfo());
		System.err.println("getTic: "+header.getTic());
		System.err.println("getUsed_time: "+header.getUsed_time());

		Hit [] his = reader.parseHitsv27();
		System.out.println(his[0].getSequence());
		System.err.println();
	}
}
