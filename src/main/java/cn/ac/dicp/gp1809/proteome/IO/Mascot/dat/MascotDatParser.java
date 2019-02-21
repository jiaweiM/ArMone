/* 
 ******************************************************************************
 * File: MascotDatParser.java * * * Created on 11-05-2008
 *
 * Copyright (c) 2008 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.ioUtil.nio.BufferUtil;
import cn.ac.dicp.gp1809.util.ioUtil.nio.RestrictedBufferUtil;

/**
 * Parse the mascot dat file and generate useful informations for the entries 
 * in dat file.
 * 
 * @author Xinning
 * @version 0.1, 11-05-2008, 21:32:56
 */
public class MascotDatParser{
	
	/** The parameter name in index, used for get the target byte position*/
	public static final String KEY_PARAMETER = "parameters";
	
	/** The header name in index, used for get the target byte position*/
	public static final String KEY_HEADER = "header";
	
	/** The masses name in index, used for get the target byte position*/
	public static final String KEY_MASSES = "masses";
	
	/** The summary name in index, used for get the target byte position*/
	public static final String KEY_SUMMARY = "summary";
	
	/** The mixture name in index, used for get the target byte position*/
	public static final String KEY_MIXTURE = "mixture";
	
	/** The peptides name in index, used for get the target byte position*/
	public static final String KEY_PEPTIDES = "peptides";
	
	/** The proteins name in index, used for get the target byte position*/
	public static final String KEY_PROTEINS = "proteins";
	
	/** The enzyme in index, used for get the target byte position*/
	public static final String KEY_ENZYME = "enzyme";
	
	/** The index entry, used for get the target byte position*/
	public static final String KEY_INDEX = "index";
	
	/** The start of the query index, used for get the target byte position*/
	public static final String START_QUERY = "query";
	
	/**
	 * In mascot dat file, the mark of the end of a region (block) often start
	 * with this value followed with the boundary String. Use this to test
	 * whether a block ended.
	 */
	final static String END_REGION_START = "--";
	

	private File file;
	// For dat from a url, the tmp file should be deleted after parsing.
//	private boolean delOnExit = false;

	private BufferUtil buffer;
	// The string indicates the boundary
//	private String boundary;

//	private LinkedHashMap<String, Idx> solidIndexMap;
	private LinkedHashMap<String, Integer> indexMap;

	// The query list contains dta and peptide informations
	private QueryIdx[] queries;
	

	/**
	 * parameter string is kept in memory
	 */
	private String[] parameter;

	/**
	 * header string is kept in memory
	 */
	private String[] header;

	/**
	 * masses string is kept in memory
	 */
	private String[] masses;
	
	/**
	 * enzyme string is kept in memory
	 */
	private String[] enzymes;
	
	
	/**
	 * Create the DatReader from a local .dat file from Mascot
	 * 
	 * @param localFile
	 * @throws MascotDatParsingException 
	 */
	public MascotDatParser(String localFile) throws MascotDatParsingException {
		this(new File(localFile));
	}
	
	/**
	 * Create the DatReader from a local .dat file from Mascot
	 * 
	 * @param localFile
	 * @throws MascotDatParsingException 
	 */
	public MascotDatParser(File localFile) throws MascotDatParsingException {
		
		this.file = localFile;
		
		try{
			
			this.buffer = new BufferUtil(localFile);
			this.buildBytesIndex();
			
		}catch(Exception e){
			throw new MascotDatParsingException(e);
		}
	}
	

	/**
	 * Create the DatReader from url of the dta file
	 * 
	 * @param instream
	 */
	public MascotDatParser(String server, String date, String filename) {

		try {
			// example:
			// ***http://cavell.ugent.be/mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir=20060419&ResJob=F011580.dat***
			String URL = server
			        + "mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir="
			        + date + "&ResJob=" + filename;

			URL lDatfileLocation = new URL(URL);
			URLConnection lURLConnection = lDatfileLocation.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        lURLConnection.getInputStream()));

			String tmppath = System.getProperty("java.io.tmpdir");
			if (tmppath == null)
				tmppath = "c:\\";

			File tmp = new File(new File(tmppath), filename);
			tmp.deleteOnExit();

			PrintWriter pw = new PrintWriter(tmp);
			String line;
			while ((line = br.readLine()) != null)
				pw.println(line);

			br.close();
			pw.close();

		} catch (MalformedURLException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Get the query by the index. <b>From 1-n</b>
	 * 
	 * @param qidx
	 *            the index of the query. <b>from 1-n</b>
	 * @return
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of bounds (idx < 1 || idx > num_query)
	 */
	public QueryIdx getQueryIdx(int qidx) throws IndexOutOfBoundsException {
		
		return this.queries[qidx];
	}
	
	/**
	 * The number of queries in this dat file
	 * 
	 * @return
	 */
	public int getQueryNum(){
		return this.queries.length;
	}


	/**
	 * The localization of the file in local hard disk. Dat 
	 * files at web will also be downloaded and stored.
	 * 
     * @return the file of dat
     */
	public final File getLocalFile() {
    	return file;
    }

	/**
     * @return the parameter strings original get from dat file, 
     * 			each string in the array represents a line
     * 			containing the title and boundary line
     */
	public final String[] getParameter() {
    	return parameter;
    }

	/**
     * @return the header strings original get from dat file, 
     * 			each string in the array represents a line
     * 			containing the title and boundary line
     */
	public final String[] getHeader() {
    	return header;
    }

	/**
     * @return the masses strings original get from dat file, 
     * 			each string in the array represents a line
     * 			containing the title and boundary line
     */
	public final String[] getMasses() {
    	return masses;
    }
    
    /**
     * @return the enzyme strings original get from dat file, 
     * 			each string in the array represents a line
     * 			containing the title and boundary line
     */
	public final String[] getEnzyme() {
    	return this.enzymes;
    }
    
    /**
     * The proteins section contains assession -> protein map
     * 
     * @return the proteins strings original get from dat file, 
     * 			each string in the array represents a line 
     * 			containing the title and boundary line
     */
	public final String[] getProteins(){
    	this.buffer.position(this.indexMap.get(KEY_PROTEINS));
    	LinkedList<String> list = new LinkedList<String>();
    	
    	String line;
    	while(!(line=this.buffer.readLine()).startsWith(END_REGION_START)){
    		list.add(line);
    	}
    	
    	list.add(line);
    	
    	return list.toArray(new String[list.size()]);
    }
    
    
    /**
     * The buffered bytes for fasta reading.
     * 
     * @return
     */
	public final BufferUtil getRestrictedReader(int byte_from, int byte_to){
    	return new RestrictedBufferUtil(this.buffer.getBuffer(), byte_from, byte_to);
    }
    
    
    /**
     * The buffered bytes for fasta reading. You may need to 
     * rewind the buffer first.
     * 
     * @return
     */
	public final BufferUtil getReaderForSection(String section_name){
    	return this.buffer;
    }
    
	/**
	 * Build the byte indexes for quick reading
	 */
	private void buildBytesIndex() throws IOException {
		
		System.out.println("Building byte index for Mascot dat ...");

		//---------parse the boundary string
		// skip the first line
		String line = this.buffer.readLine();
		// Find the boundary.
		while (line != null && line.indexOf("boundary") < 0) {
			line = buffer.readLine();
			System.out.println(line);
		}

		// If the line is 'null' here, we read the entire datfile without encountering a boundary.
		if (line == null) {
			throw new IllegalArgumentException(
			        "Did not find 'boundary' definition in the datfile!");
		}

		while (!this.buffer.readLine().startsWith(END_REGION_START));

		//---building the index
		
		this.indexMap = new LinkedHashMap<String, Integer>();
		ArrayList<QueryIdx> querylist = new ArrayList<QueryIdx>(1000);
		
		do {
			int position = this.buffer.position();
			// The content line
			line = this.buffer.readLine();
			String name = getProp(line, "name");

			// The end of useful blocks
			if (name.equals(KEY_INDEX)) {
				break;
			}

			if (name.startsWith(START_QUERY)) {
				
				//Enter this region the second time, the hit and query don't equal
				if(this.indexMap.get(START_QUERY)!=null){
					throw new IllegalArgumentException(
					        "The number of hitted queries doesn't equal to the number of query spectra, "
			                + querylist.size() + " : "
			                + (Integer.parseInt(name.substring(5))));
				}
				
				this.indexMap.put(START_QUERY, position);
				
				while(true){
					// query number
					int id = Integer.parseInt(name.substring(5));
					QueryIdx qy = this.getQueryIdx(id, querylist);
					
					qy.setByteIdx_dta(position);

					// Get the name of the query
					this.buffer.readLine();
					line = this.buffer.readLine();
					String mtitle = getProp(line, "title");
					//No title
					if(mtitle==null || mtitle.length()==0)
						qy.setQname("No title (Query " + id + ")");
					else
						qy.setQname(parseTitle(mtitle));

					while (!this.buffer.readLine().startsWith(END_REGION_START))
						;
					
					qy.setByteLen_dta(this.buffer.position()-position);
					
					position = this.buffer.position();
					name = getProp(this.buffer.readLine(), "name");
					
					if (!name.startsWith(START_QUERY)) {
						this.buffer.position(position);
						break;
					}
				}
			}
			
			
			
			else {
				this.indexMap.put(name, position);

				if (KEY_PEPTIDES.equals(name)) {
					// Skip the blank line
					this.buffer.readLine();

					String preIdx = "^_^, don't equal";
					int hitNum = -1;
					QueryIdx preqy = null;
					String preline = null;
					
					while (true) {
						line = this.buffer.readLine();
						
						boolean end = line.startsWith(END_REGION_START);
						
						//End of the peptide hits of current query
						if (end || !line.startsWith(preIdx)) {
							// Number of hits
							if (preqy != null) {
								
								if(hitNum==-1)
									preqy.setNum_pep_hits(-1);
								else{
									StringBuilder sb = new StringBuilder(3);
									//e.g. q537_p
									int idx = preline.indexOf('_')+2;
									//The char after the hit index may be '=' or '_' if the line is a subst line
									//e.g. q537_p5_subst=10,X,W
									char c;
									while(Character.isDigit((c=preline.charAt(idx++)))){
										sb.append(c);
									}
									
									preqy.setNum_pep_hits(Integer.parseInt(sb.toString()));
								}
								
								preqy.setByteLen_pep(this.buffer.getBSLinePosition()-preqy.getByteIdx_pep());
							}
							
							//End of the section
							if(end)
								break;
							
							hitNum = line.endsWith("=-1") ? -1 : 1;
							preIdx = line.substring(0, line.indexOf('_'));
							int idx = Integer.parseInt(preIdx.substring(1));
							
							preqy = this.getQueryIdx(idx, querylist);
							preqy.setByteIdx_pep(this.buffer.getBSLinePosition());
						}
						
						preline = line;
					}
				} 
				/*
				 * Parsing the summary section
				 */
				else if(KEY_SUMMARY.equals(name)){
					// Skip the blank line
					this.buffer.readLine();
					
					HashMap<String, String> map = new HashMap<String, String>();
					
					while (!(line = this.buffer.readLine())
					        .startsWith("num_hits=")) {
						this.parseIntoMap(line, map);
					}
					
					for(int i=1; ; i++){
						String key1 = "qexp"+i;
						String values = map.get(key1);
						//All the entries
						if(values == null)
							break;
						
						String[] vs = StringUtil.split(values,',');
						if(vs.length != 2){
							throw new IllegalArgumentException("The qexp entry is excepted to" +
									" contain 2 values, mz & charge, current "+vs.length);
						}

						QueryIdx queryIdx = this.getQueryIdx(i, querylist);
						queryIdx.setMz(Double.parseDouble(vs[0].trim()));
						if(vs[1].trim().equals("Mr")){
							queryIdx.setCharge((short) 1);
						}else{
							queryIdx.setCharge(Short.parseShort(vs[1].trim().substring(0,1)));
						}

						queryIdx.setQmatch(Double.parseDouble(map.get("qmatch"+i)));
						queryIdx.setQplughole(Double.parseDouble(map.get("qplughole"+i)));
						
						String intens = map.get("qintensity"+i);
						if(intens != null)
							queryIdx.setInten(Double.parseDouble(intens));
					}

				}
				else if (KEY_PARAMETER.equals(name)) {
					LinkedList<String> list = new LinkedList<String>();
					
					list.add(line);
					do {
						line = this.buffer.readLine();
						list.add(line);
					}while(!line.startsWith(END_REGION_START));

					this.parameter = list.toArray(new String[list.size()]);
				} else if (KEY_HEADER.equals(name)) {
					LinkedList<String> list = new LinkedList<String>();
					
					list.add(line);
					do {
						line = this.buffer.readLine();
						list.add(line);
					}while(!line.startsWith(END_REGION_START));

					this.header = list.toArray(new String[list.size()]);
				} else if (KEY_MASSES.equals(name)) {
					LinkedList<String> list = new LinkedList<String>();
					
					list.add(line);
					do {
						line = this.buffer.readLine();
						list.add(line);
					}while(!line.startsWith(END_REGION_START));

					this.masses = list.toArray(new String[list.size()]);
				}
				else if (KEY_ENZYME.equals(name)) {
					LinkedList<String> list = new LinkedList<String>();
					
					list.add(line);
					do {
						line = this.buffer.readLine();
						list.add(line);
					}while(!line.startsWith(END_REGION_START));

					this.enzymes = list.toArray(new String[list.size()]);
				}
			}

		} while (true);
		
		
		this.queries = querylist.toArray(new QueryIdx[querylist.size()]);
		
		System.out.println("Finished building byte index for Mascot dat.");
	}
	
	
	/**
	 * Parse a line into map with key--value pairs
	 * 
	 * @param section
	 * @return
	 */
	private void parseIntoMap(String line, HashMap<String, String> map) {
		if (line == null)
			throw new NullPointerException("The input line is null.");

		DatEntryParser.Entry entry = DatEntryParser.parseEntry(line);
		map.put(entry.getKey(), entry.getValue());
	}
	
	
	/**
	 * Get the query index from the query list. If there is no instance of query index with the qidx,
	 * new instance of this qidx will be created and set at the corresponding position of the quey list.
	 * 
	 * @param qidx
	 * @param list
	 * @return
	 */
	private QueryIdx getQueryIdx(int qidx, ArrayList<QueryIdx> list){
		QueryIdx queryIdx;
		if(qidx >= list.size()){
			int toAdd = qidx - list.size();
			//Add null to the poistion between current size and the new size;
			for(int i=0; i< toAdd; i++)
				list.add(null);
			
			list.add(queryIdx = new QueryIdx(qidx));
		}
		else{
			queryIdx = list.get(qidx);
			if(queryIdx == null){
				list.set(qidx, queryIdx = new QueryIdx(qidx));
			}
		}
		
		return queryIdx;
	}
	

	/**
	 * parse the solid indexes created by mascot.
	 * 
	 * @throws IOException
	 * @throws NullPointerException
	 *             if the number of queries is 0.
	 */
	
	/*
	private void parseSolidIndexes() throws IOException, NullPointerException {
		int skip = 100;
		this.buffer.position(this.buffer.length() - skip);

		String lastline = null;
		String line;
		while ((line = this.buffer.readLine()) != null) {
			if (line.endsWith(this.boundary + "--"))
				break;
			lastline = line;
		}
		// Number of queries
		int queryNum = Integer.parseInt(lastline.substring(5, lastline
		        .indexOf('=')));
		// The approximate number of bytes for a query index.
		int multi = 38;

		this.querylist = new ArrayList<Query>(queryNum);

		int approxIdxSize = queryNum * multi;

		this.buffer.position(this.buffer.length() - approxIdxSize);
		while ((line = this.buffer.readLine()) != null
		        && !line.endsWith("name=\"index\"")) {
		}

		if (line == null) {
			System.err.println("The index position should be extended.");
		}

		// Parsing the indexes.
		this.solidIndexMap = new LinkedHashMap<String, Idx>(queryNum);

		while (!(line = this.buffer.readLine()).endsWith("--")) {
			String key = line.substring(0, line.indexOf('='));

			int start = line.indexOf('=');
			String value = line.substring(start + 1).trim();
			// Trim away opening and closing '"'.
			int len = value.length() - 1;
			if (len >= 0) {
				if (value.charAt(0) == '"') {
					value = value.substring(1);
				}

				if (value.charAt(len) == '"') {
					value = value.substring(0, len);
				}
			}

			int lineid = Integer.parseInt(value.trim());

			// The dta of the query
			if (key.startsWith("query")) {
				// The first query
				if (this.querylist.size() == 0) {
					this.solidIndexMap.put(START_QUERY, new Idx(lineid));
				}

				int id = Integer.parseInt(line.substring(5));

				Query qy = new Query(id);
//				qy.setLineIdx_dta(lineid);

				this.querylist.add(qy);
			} else {
				// put the entries into map except the query
				this.solidIndexMap.put(key, new Idx(lineid));
			}
		}

		if (this.querylist.size() == 0)
			throw new NullPointerException("There is no query in the dat file.");

		// this.solidIndexMap.put(KEY_END, new Idx(lineid));
	}
	*/
	
	/**
	 * Peptide query informations
	 * 
	 * @author Xinning
	 * @version 0.1, 11-10-2008, 10:45:04
	 */
	public static class QueryIdx implements java.io.Serializable{

		/**
         * 
         */
        private static final long serialVersionUID = 1L;
        
		private String qname;
		private int qidx;
		
		private int byteIdx_dta = -1;
		private int byteIdx_pep = -1;
		
		private int byteLen_dta = -1;
		private int byteLen_pep = -1;

		private int num_pep_hits;
		
		private double mz;
		private double inten;
		
		private short charge;
		private double qmatch;
		private double qplughole;


		/**
		 * @param qidx
		 *            the query index (e.g. 822 in "query<u>822</u>")
		 */
		public QueryIdx(int qidx) {
			this.qidx = qidx;
		}

		/**
		 * The name of the query scan
		 * 
		 * @return the qname
		 */
		public final String getQname() {
			return qname;
		}

		/**
		 * The name of the query scan
		 * 
		 * @param qname
		 *            the qname to set
		 */
		public final void setQname(String qname) {
			this.qname = qname;
		}

		/**
		 * The byte idx of the dta (peak list)
		 * 
		 * @return the byteIdx_dta
		 */
		public final int getByteIdx_dta() {
			return byteIdx_dta;
		}

		/**
		 * The byte idx of the dta (peak list)
		 * 
		 * @param byteIdx_dta
		 *            the byteIdx_dta to set
		 */
		public final void setByteIdx_dta(int byteIdx_dta) {
			this.byteIdx_dta = byteIdx_dta;
		}

		/**
		 * The byte idx of the peptide hit
		 * 
		 * @return the byteIdx_pep
		 */
		public final int getByteIdx_pep() {
			return byteIdx_pep;
		}

		/**
		 * The byte idx of the peptide hit
		 * 
		 * @param byteIdx_pep
		 *            the byteIdx_pep to set
		 */
		public final void setByteIdx_pep(int byteIdx_pep) {
			this.byteIdx_pep = byteIdx_pep;
		}
		
		

		/**
		 * The length of the bytes represent the dta informations.
		 * 
         * @return the byteLen_dta
         */
       public final int getByteLen_dta() {
        	return byteLen_dta;
        }

		/**
		 * The length of the bytes represent the dta informations.
		 * 
         * @param byteLen_dta the byteLen_dta to set
         */
       public final void setByteLen_dta(int byteLen_dta) {
        	this.byteLen_dta = byteLen_dta;
        }

		/**
		 * The length of the bytes represent the peptide hit informations.
		 * 
         * @return the byteLen_pep
         */
       public final int getByteLen_pep() {
        	return byteLen_pep;
        }

		/**
		 * The length of the bytes represent the peptide hit informations.
		 * 
         * @param byteLen_pep the byteLen_pep to set
         */
       public final void setByteLen_pep(int byteLen_pep) {
        	this.byteLen_pep = byteLen_pep;
        }

		/**
		 * The actual number of peptide hits for this query. -1 if 
		 * there is no peptide hit for this query.
		 * 
		 * @return the num_pep_hits
		 */
		public final int getNum_pep_hits() {
			return num_pep_hits;
		}

		/**
		 * The actual number of peptide hits for this query�� -1 if 
		 * there is no peptide hit for this query.
		 * 
		 * @param num_pep_hits
		 *            the num_pep_hits to set
		 */
		public final void setNum_pep_hits(int num_pep_hits){
			this.num_pep_hits = num_pep_hits;
		}

		/**
		 * The query index
		 * 
		 * @return the qidx
		 */
		public final int getQidx() {
			return qidx;
		}
		
		/**
         * @return the mz of precursor ion
         */
        public final double getMz() {
        	return mz;
        }

		/**
         * @param the mz of precursor ion
         */
        public final void setMz(double mz) {
        	this.mz = mz;
        }

		/**
         * @return the inten
         */
        public final double getInten() {
        	return inten;
        }

		/**
         * @param inten the inten to set
         */
        public final void setInten(double inten) {
        	this.inten = inten;
        }

		/**
         * @return the charge
         */
        public final short getCharge() {
        	return charge;
        }

		/**
         * @param charge the charge to set
         */
        public final void setCharge(short charge) {
        	this.charge = charge;
        }

		/**
         * @return the qmatch
         */
        public final double getQmatch() {
        	return qmatch;
        }

		/**
         * @param qmatch the qmatch to set
         */
        public final void setQmatch(double qmatch) {
        	this.qmatch = qmatch;
        }

		/**
         * @return the qplughole
         */
        public final double getQplughole() {
        	return qplughole;
        }

		/**
         * @param qplughole the qplughole to set
         */
        public final void setQplughole(double qplughole) {
        	this.qplughole = qplughole;
        }

		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("Query-").append(this.qidx).append(", name: ")
			  .append(this.qname).append(", idx_dta: ").append(this.byteIdx_dta)
			  .append(", len_dta: ").append(this.byteLen_dta).append(", idx_pep: ")
			  .append(this.byteIdx_pep).append(", len_pep: ").append(this.byteLen_pep)
			  .append(", num_hits: ").append(this.num_pep_hits).append(", charge: ")
			  .append(this.charge).append(", mz: ").append(this.mz).append(", intensitry: ")
			  .append(this.inten).append(", qmatch: ").append(this.qmatch).append(", qplughole: ")
			  .append(this.qplughole);
			
			return sb.toString();
		}
	}

	/**
	 * This method finds a property, associated by a name in the following
	 * context: <br /> NAME=VALUE
	 * 
	 * @param line String with the line on which the 'KEY=VALUE' pair is to be
	 * 			found. 
	 * @param propName String with the name of the KEY. 
	 * @return String with the VALUE
	 */
	static String getProp(String line, String propName) {
		int start = line.indexOf(propName);
		if (start >= 0) {
			// "propName="
			int offset = propName.length() + 1;
			String found = line.substring(start + offset).trim();
			// Trim away opening and closing '"'.
			int len = found.length() - 1;
			if (len >= 0) {
				if (found.charAt(0) == '"') {
					found = found.substring(1);
					len --;
				}
				if (found.charAt(len) == '"') {
					found = found.substring(0, len);
				}
			}

			return found.trim();
		} else
			return "";

	}

	/**
	 * This method gets the unparsed Title value out of the Query Hashmap,
	 * parses the String and returns a readable String.
	 * 
	 * @return String Readable filename
	 */
	public static String parseTitle(String title) {
		return MascotMimeParser.decodeString(title);
	}

	/**
	 * Close the reader and clear all the instances in memory
	 */
	void close() {
		this.buffer.close();
		
		
		if(this.enzymes != null) this.enzymes = null;
		if(this.header != null) this.header = null;
		if(this.masses != null) this.masses = null;
		if(this.indexMap != null) this.indexMap = null;
		if(this.parameter != null) this.parameter = null;
	}

	public static void main(String[] args) throws IOException, MascotDatParsingException {
		
		File file = new File("L:\\Data_DICP\\turnover\\20130920_MCF_PTturnover_velos\\DATfiles_20130920_MCF_PT\\temp" +
				"\\2013920-MCFPT0_3_6h_50mM_F001496.dat");
		
		MascotDatParser parser = new MascotDatParser(file);
		
		int num = parser.getQueryNum();
		for(int i=1; i<num; i++){
			System.out.println(i+"\t"+parser.getQueryIdx(i).getQidx()+"\t"+parser.getQueryIdx(i).getQname());
		}
	
/*		
		System.out.println(parser.getParameter().length);
		for(int i=0;i<parser.getParameter().length;i++){
			System.out.println(parser.getParameter()[i]);
		}
		System.out.println(parser.getHeader().length);
		for(int i=0;i<parser.getHeader().length;i++){
			System.out.println(parser.getHeader()[i]);
		}
		
		if(true)
			return ;
		

		String login = "http://mserver1/mascot/cgi/login.pl?action=login&username=mascot&password=mascot&savecookie=1&display=logout_prompt";
		String name = "http://mserver1/mascot/cgi/login.pl?action=login&username=mascot&password=mascot&referer=../x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir=20081103&ResJob=F001236.dat";

		// example:
		// ***http://cavell.ugent.be/mascot/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir=20060419&ResJob=F011580.dat***
		name = "http://mserver1/mascot/x-cgi/ms-status.exe?MASCOT_SESSION=Jiang_38855859865867&MASCOT_USERNAME=Jiang&MASCOT_USERID=1006Autorefresh=false&Show=RESULTFILE&DateDir=20081103&ResJob=F001236.dat";
		URL lDatfileLocation = new URL(login);
		URLConnection lURLConnection = lDatfileLocation.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(
		        lURLConnection.getInputStream()));

		File tmp = new File("d:\\tmp.dat");

		PrintWriter pw = new PrintWriter(tmp);
		String line;
		while ((line = br.readLine()) != null)
			pw.println(line);

		br.close();
		pw.close();

		// new URL("").openConnection();
		 * 
*/
		
	}
}
