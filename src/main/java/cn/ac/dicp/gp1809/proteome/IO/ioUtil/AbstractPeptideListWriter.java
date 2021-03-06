/* 
 ******************************************************************************
 * File: AbstractPeptideListWriter.java * * * Created on 01-02-2009
 *
 * Copyright (c) 2009 Xinning Jiang vext@163.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.ioUtil;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.ac.dicp.gp1809.proteome.IO.exceptions.ProWriterException;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.formatters.IPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.IReader4Ppl;
import cn.ac.dicp.gp1809.proteome.IO.ioUtil.ppl.Reader4PplFactory;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.IDecoyReferenceJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ISearchParameter;
import cn.ac.dicp.gp1809.proteome.spectrum.AminoacidFragment;
import cn.ac.dicp.gp1809.proteome.spectrum.IMS2PeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.IPeakList;
import cn.ac.dicp.gp1809.proteome.spectrum.Ion;
import cn.ac.dicp.gp1809.proteome.spectrum.Ions;
import cn.ac.dicp.gp1809.proteome.spectrum.PeakForMatch;
import cn.ac.dicp.gp1809.proteome.spectrum.SpectrumMatcher;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.ISpectrumThreshold;
import cn.ac.dicp.gp1809.proteome.spectrum.filters.SpectrumThreshold;
import cn.ac.dicp.gp1809.util.DecimalFormats;
import cn.ac.dicp.gp1809.util.StringUtil;
import cn.ac.dicp.gp1809.util.arrayutil.IntArrayList;
import cn.ac.dicp.gp1809.util.ioUtil.FileUtil;
import cn.ac.dicp.gp1809.util.ioUtil.IOConstant;
import cn.ac.dicp.gp1809.util.ioUtil.ParameterAppender;

/**
 * The abstract peptide list writer
 * 
 * @author Xinning
 * @version 0.2.2, 05-20-2010, 15:50:34
 */
public abstract class AbstractPeptideListWriter implements IPeptideListWriter {

	private IPeptideFormat formatter;

	private File output;

	private File tempoutfile;
	//Temp peak list file
	private File temppeakout;

	private ISearchParameter parameter;
	
	private IDecoyReferenceJudger judger;
	
	private DataType dataType;
		
	private ISpectrumThreshold threshold = new SpectrumThreshold(0.8, 0.5);

	/**
	 * The peptides are first write to temp files, and regenerated to the
	 * outputfile when close();
	 */
	protected PrintWriter tempwriter;
	/**
	 * Writer for the temporary peak list file
	 */
	protected PrintWriter tempPeakWriter;
	/**
	 * The current written peptide number.
	 */
	private int curtWrittenPepNum;

	/**
	 * The peak list indexes for the printed peptides in temporary file
	 */
	private ArrayList<long[]> peakListIndexes;

	/**
	 * The map of peak list, key = scanKey, value = byte index of printed peak
	 * list.
	 */
	private HashMap<String, long[]> peaklistMap;

	/**
	 * This is used for calculate the intensity of fragment ions of the peptide.
	 */
	private AminoacidFragment aaf;
	private int [] type_b_y = new int []{Ion.TYPE_B, Ion.TYPE_Y};
	
	private ProteinNameAccesser proNameAccesser;
	
	private DecimalFormat df = DecimalFormats.DF_E3;

	/**
	 * Create a peptide list writer for the specific output path
	 * 
	 * @param output
	 * @param parameter
	 * @throws FileNotFoundException
	 */
	protected AbstractPeptideListWriter(String output,
	        IPeptideFormat<?> formatter, ISearchParameter parameter, IDecoyReferenceJudger judger,
	        IPeptideListHeader header, ProteinNameAccesser proNameAccesser) throws FileNotFoundException {
		this(new File(output), formatter, parameter, judger, header, proNameAccesser);
	}

	/**
	 * Create a peptide list writer for the specific output file
	 * 
	 * @param output
	 * @param parameter
	 * @throws FileNotFoundException
	 */
	protected AbstractPeptideListWriter(File output,
	        IPeptideFormat<?> formatter, ISearchParameter parameter, IDecoyReferenceJudger judger,
	        IPeptideListHeader header, ProteinNameAccesser proNameAccesser) throws FileNotFoundException {
		
		this.output = output;
		this.tempoutfile = new File(output.getAbsolutePath() + ".tmp");
		this.temppeakout = new File(output.getAbsolutePath() + ".peaks.tmp");
		
		this.parameter = parameter;
		this.judger = judger;
		this.formatter = formatter;

		this.peakListIndexes = new ArrayList<long[]>(2000);
		this.peaklistMap = new HashMap<String, long[]>();

		this.tempwriter = new PrintWriter(this.tempoutfile);
		this.tempPeakWriter = new PrintWriter(this.temppeakout);
		
		this.proNameAccesser = proNameAccesser;
		
		this.aaf = new AminoacidFragment(parameter.getStaticInfo(), parameter.getVariableInfo());
		this.writeHeader(header, this.tempwriter);
	}

	public void setDataType(DataType dataType){
		this.dataType = dataType;
	}

	/**
	 * Write the header into peptide list file.
	 * 
	 * @return
	 */
	private void writeHeader(IPeptideListHeader header, PrintWriter writer) {
		writer.println(header);
		writer.println(this.formatter.getTitleString());
	}

	/**
	 * The file of the output ppl file.
	 * 
	 * @return
	 */
	protected final File getOutFile() {
		return this.output;
	}

	/**
	 * In current scheme, peptides are first written to a temporary file to
	 * remove the redundancy; and then they are re-read from the temp file as
	 * IPeptide instances, after that, the re-read peptide instances are
	 * modified for special aim (supply the full protein name list, assign new
	 * probability and etc.). Finally they can be written to a final ppl file
	 * with indexes in then end.
	 * 
	 * <p>
	 * This is the main method to determine whether this peptide will be wrote
	 * to the temporary file
	 * 
	 * @param peptide
	 */
	protected abstract boolean judgePrint(IPeptide peptide);

	/**
	 * The peptide formatter
	 * 
	 * @return
	 */
	protected final IPeptideFormat getFormatter() {
		return this.formatter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter#getSearchParameter()
	 */
	@Override
	public ISearchParameter getSearchParameter() {
		return this.parameter;
	}
	
	public ProteinNameAccesser getProteinAccesser(){
		return this.proNameAccesser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter#
	 * write(cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide)
	 */
	@Override
	public boolean write(IPeptide peptide, IMS2PeakList [] peaklist) {
		
		boolean print = this.judgePrint(peptide);
		
		if (print) {

			if(peaklist.length==1){
				double fragInten = 0.0;
				Ions ions = aaf.fragment(peptide.getPeptideSequence(), type_b_y, true);
				short charge = peptide.getCharge();
			
				for(int i=0;i<peaklist.length;i++){

					PeakForMatch [] peaks = SpectrumMatcher.matchBY(peaklist[i], ions, charge);
					for(int j=0;j<peaks.length;j++){
						PeakForMatch peak = peaks[j];
						if(peak.isMatch2BIons() || peak.isMatch2YIons()){
							double inten = peak.getAbIntensity();
							fragInten += inten;
						}					
					}
				}
				peptide.setFragInten(Double.parseDouble(df.format(fragInten)));
			}
			this.write(this.formatter.format(peptide));
			
			this.peakListIndexes
	        	.add(this.write(peptide.getScanNum(), peaklist));
			
		}
		
		return print;
	}

	/**
	 * Write a string line to the ppl file. Directly writing.
	 * 
	 * @param pep_str
	 */
	protected void write(String pep_str) {
		this.tempwriter.println(pep_str);
		this.writtenPepIncrease();
		this.tempwriter.flush();
	}

	/**
	 * Write a string line to the ppl file. Directly writing.
	 * 
	 * @param pep_str
	 * @return the index array of the peak lists, the same peak list with same
	 *         scan key will not be printed twice, the returned value will be
	 *         the index of the singly printed peaklist
	 */
	protected long[] write(String scanKey, IPeakList[] peaklist) {
		long[] indexes;
		if ((indexes = this.peaklistMap.get(scanKey)) == null) {
			if(peaklist == null || peaklist.length == 0)
				return null;
			
			int size = peaklist.length;
			indexes = new long[size];
			indexes[0] = this.temppeakout.length();
			
			this.tempPeakWriter.println(scanKey);
			
			for (int i = 0; i < size; i++) {
				if(i!=0)
					indexes[i] = (int) this.temppeakout.length();
				
				this.tempPeakWriter.println(peaklist[i].toPeaksOneLine());
				this.tempPeakWriter.flush();
			}

			this.peaklistMap.put(scanKey, indexes);
		}

		return indexes;
	}

	/**
	 * The number of peptides have written to the ppl file
	 * 
	 * @return current line number;
	 */
	protected final int getWrittenPepNum() {
		return this.curtWrittenPepNum;
	}

	/**
	 * lineNumber increased by one while every excution;
	 * 
	 * @return line number after this excution
	 */
	private final void writtenPepIncrease() {
		this.curtWrittenPepNum++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter#getPeptideType()
	 */
	@Override
	public final PeptideType getPeptideType() {
		return this.formatter.type();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideWriter#close()
	 */
	@Override
	public void close() throws ProWriterException {
		this.tempwriter.close();
		this.tempPeakWriter.close();

		if (this.output.exists())
			this.output.delete();
		
		this.write2FinalFile();
	}

	/**
	 * This method will be called by {@link #close()} to rewrite the peptide to
	 * the final ppl(s) file from temp ppl(s) file.
	 * 
	 * @since 0.2
	 * @return the byte index array of the peptide list in the final file.
	 * @throws ProWriterException
	 */
	private void write2FinalFile() throws ProWriterException {
		DefaultLongPeptideListIndex[] indexes = null;
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new FileReader(this.tempoutfile));
		} catch (FileNotFoundException e) {
			throw new ProWriterException(
			        "Cann't write to final ppl file, the temporary peptide list file "
			                + "is unreachable.", e);
		}

		IReader4Ppl reader = null;
		try {
			reader = Reader4PplFactory.createReader(this.temppeakout);
		} catch (IOException e) {
			throw new ProWriterException(
			        "Cann't write to final ppl file, the temporary peak list file "
			                + "is unreachable.", e);
		}

		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new FileOutputStream(this.output), true);
		} catch (IOException e) {
			throw new ProWriterException(
			        "Error in creating print writer for output, make "
			                + "sure that the file \"" + this.output.getName()
			                + "\" is valid and writeable!", e);
		}

		try {
			// Print the title and the tag
			pw.println(bfr.readLine());
			pw.println(bfr.readLine());

			IntArrayList intlist = new IntArrayList(this.getWrittenPepNum());
			//The final printed lines
			IntArrayList finallines = new IntArrayList(this.getWrittenPepNum());

			int pep_num = this.getWrittenPepNum();
			for (int i = 0; i < pep_num; i++) {
				String string = bfr.readLine();
				if (!this.isRemovePep4FinalWritten(i)) {
					intlist.add((int) this.output.length());
					finallines.add(i);

					// Add the additional protein references
					if (this.isNeadRefresh4FinalWritten(i)) {
						
						IPeptide peptide = this.formatter.parse(string);
						peptide = this.refeshPeptide4FinalWritten(peptide, i);

						pw.println(this.formatter.format(peptide));
					} else
						pw.println(string);
				}
			}

			pw.println();

			StringBuilder des = new StringBuilder();
			des.append('[').append(this.getPeptideType().getAlgorithm_name());
			des.append(" Parameters]").append(IOConstant.lineSeparator).append(
			        this.parameter.getStaticInfo().getModfiedAADescription(
			                this.parameter.isMonoPeptideMass())).append(
			        this.parameter.getVariableInfo()
			                .getModficationDescription()).append(
			        IOConstant.lineSeparator).append(IOConstant.lineSeparator)
			        .append("[Peak lists]");

			pw.println(des);
			bfr.close();

			/*
			 * Print the peak list, and construct the final index
			 */
			int num_peps = finallines.size();
			indexes = new DefaultLongPeptideListIndex[num_peps];
			HashMap<String, long[]> map = new HashMap<String, long[]>();

			for (int i = 0; i < num_peps; i++) {
				int lineidx = finallines.get(i);
				long[] temppeakindexes = this.peakListIndexes.get(lineidx);
				int siz = temppeakindexes == null ? 0 : temppeakindexes.length;
				long startPos;
				
				if(siz == 0) 
					startPos = -1 ;
				else {
					reader.position(temppeakindexes[0]);
					String scanKey = reader.readLine();
					
					long[] finalpeakIdxes;
					if ((finalpeakIdxes = map.get(scanKey)) == null) {
						finalpeakIdxes = new long[siz];
						finalpeakIdxes[0] = this.output.length();
						pw.println(scanKey);

						for (int j = 0; j < siz; j++) {
							pw.println(reader.readLine());
						}

						map.put(scanKey, finalpeakIdxes);
					}
					
					startPos = finalpeakIdxes[0];
				}

				indexes[i] = new DefaultLongPeptideListIndex(intlist.get(i),
						startPos, siz);
			}

			pw.close();
			reader.close();
			
			this.tempoutfile.delete();
			FileUtil.deleteDelay(this.temppeakout);
		} catch (IOException e) {
			throw new ProWriterException(
			        "Error occurs when writing to final ppl file from tempary file", e);
		}

		try {
			ParameterAppender.appendToEnd(this.output, "[Bin parameters]",
			        new LongListDetails(this.parameter, this.formatter, indexes, this.judger, this.proNameAccesser));
		} catch (IOException e) {
			throw new ProWriterException(
			        "Error while appending parameter to the end of file.", e);
		}
	}

	/**
	 * If the current peptide with index of pep_idx need to be refreshed for
	 * final written. If true, the peptide will be parsed and refreshed use the
	 * method {@link #refeshPeptide4FinalWritten(IPeptide, int)}
	 * 
	 * @param pep_idx
	 *            the index of current peptide
	 * @return
	 */
	protected abstract boolean isNeadRefresh4FinalWritten(int pep_idx);

	/**
	 * Refresh the current peptide with index of line_idx from the temp ppl
	 * file, then the returned peptide (can be the same instance with modified
	 * value) will be written into the final list.
	 * 
	 * <p>
	 * <b>The refresh of peptide that will be removed from final peptide list
	 * file will affect nothing</b>
	 * 
	 * 
	 * @param peptide
	 *            the peptide for written to the final list file. If the
	 *            returned value is null, the current peptide will not be
	 *            written to the final file.
	 * @param line_idx
	 *            the index of line in temp ppl file of current peptide.
	 * @return
	 */
	protected abstract IPeptide refeshPeptide4FinalWritten(IPeptide peptide,
	        int line_idx);

	/**
	 * If the current peptide with index of pep_idx should be removed while
	 * written to the final ppl file.
	 * 
	 * @param pep_idx
	 *            the index of current peptide
	 * @return
	 */
	protected abstract boolean isRemovePep4FinalWritten(int pep_idx);

	/**
	 * The information of a line previously printed.
	 * 
	 * @author Xinning
	 * @version 0.3.1, 12-10-2008, 15:05:44
	 */
	protected static class LineInfor {
		private int pepidx;
		private float pscore;
		private String sequence;
		private short charge;

		protected LineInfor(int pepidx, IPeptide peptide) {
			this.pepidx = pepidx;
			this.pscore = peptide.getPrimaryScore();
			this.sequence = peptide.getPeptideSequence().getSequence();
			this.charge = peptide.getCharge();
		}

		protected LineInfor(int pepidx, IPeptide peptide,
		        String seq_without_term) {
			this.pepidx = pepidx;
			this.pscore = peptide.getPrimaryScore();
			this.sequence = seq_without_term;
			this.charge = peptide.getCharge();
		}

		protected int getPepIdx() {
			return this.pepidx;
		}

		protected short getCharge() {
			return this.charge;
		}

		protected float getPrimaryScore() {
			return this.pscore;
		}

		/**
		 * Sequence without terminals
		 * 
		 * @return
		 */
		protected String getSequence() {
			return this.sequence;
		}

		@Override
		public int hashCode() {
			return pepidx;
		}

		@Override
		public boolean equals(Object o) {
			return this.hashCode() == o.hashCode();
		}
	}

	/**
	 * The default peptide list header
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 01-02-2009, 17:16:59
	 */
	public static class DefaultPeptideListHeader implements IPeptideListHeader {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		// The time format for time tag
		protected static final DateFormat datetimeformat = new SimpleDateFormat(
		        "yyyy-MM-dd HH:mm:ss");

		protected static final String lineSeparator = IOConstant.lineSeparator;

		private PeptideType type;
		protected Date current;

		private DefaultPeptideListHeader() {
		}

		public DefaultPeptideListHeader(PeptideType type) {
			this.type = type;
			this.current = new Date();
		}

		/**
		 * Parse the header from a string
		 * 
		 * @param header
		 * @return
		 */
		public static IPeptideListHeader parseHeader(String headers)
		        throws IllegalArgumentException {
			String[] cols = StringUtil.split(headers, '\t');
			if (cols.length < 2) {
				throw new IllegalArgumentException("Header parsing error: \""
				        + headers + "\".");
			}

			DefaultPeptideListHeader header = new DefaultPeptideListHeader();

			try {
				header.current = datetimeformat.parse(cols[0]);
				header.type = PeptideType.typeOfFormat(cols[1]);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}

			return header;
		}

		@Override
		public PeptideType getPeptideType() {
			return this.type;
		}

		@Override
		public Date getWriteDate() {
			return this.current;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter
		 * .IPeptideListHeader#getDescription()
		 */
		@Override
		public String getDescription() {
			return "Search engine: " + this.type.getAlgorithm_name()
			        + lineSeparator + "Created in " + this.current;
		}

		@Override
		public String toString() {
			return datetimeformat.format(current) + "\t" + type;
		}
	}

	/**
	 * 
	 * To extend the old one (for compatible)
	 * 
	 * The details write to the end of the.
	 * 
	 * @author Xinning
	 * @version 0.1, 09-09-2010, 16:30:11
	 */
	protected static class LongListDetails implements Serializable {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		private IDecoyReferenceJudger judger;

		private ISearchParameter param;
		private IPeptideFormat format;
		private ILongPeptideListIndex[] indexes;
		private ProteinNameAccesser proNameAccesser;

		/**
		 * @param param
		 * @param peptideIndexes
		 */
		protected LongListDetails(ISearchParameter param, IPeptideFormat format,
				ILongPeptideListIndex[] indexes, IDecoyReferenceJudger judger, 
				ProteinNameAccesser proNameAccesser) {
			this.param = param;
			this.format = format;
			this.indexes = indexes;
			this.judger = judger;
			this.proNameAccesser = proNameAccesser;
		}

		/**
		 * The search parameter
		 * 
		 * @return
		 */
		public ISearchParameter getSearchParameter() {
			return this.param;
		}

		/**
         * @return the format the peptide formatter
         */
        public IPeptideFormat<?> getFormat() {
        	return format;
        }

		/**
		 * The indexes of each peptides. This index is the byte position of the
		 * start of each line indicating the peptide and the byte position of
		 * the peak list. This is for the easy reading of target peptide from
		 * PeptideListAccesser.
		 * 
		 * @return
		 */
		public ILongPeptideListIndex[] getPeptideIndexes() {
			return this.indexes;
		}

		/**
		 * The decoy system judger
		 * 
		 * @return
		 */
		public IDecoyReferenceJudger getDecoyJudger() {
			return this.judger;
		}
		
		public ProteinNameAccesser getProNameAccesser(){
			return this.proNameAccesser;
		}
	}
	
	/**
	 * 
	 * To extend the old one (for compatible)
	 * 
	 * The details write to the end of the.
	 * 
	 * @author Xinning
	 * @version 0.1, 05-20-2010, 15:46:08
	 */
	protected static class ListDetails2 extends ListDetails {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		private IDecoyReferenceJudger judger;

		/**
		 * @param param
		 * @param peptideIndexes
		 */
		protected ListDetails2(ISearchParameter param, IPeptideFormat<?> format,
		        IPeptideListIndex[] indexes, IDecoyReferenceJudger judger) {
			super(param, format, indexes);
			this.judger = judger;
		}

		/**
		 * The decoy system judger
		 * 
		 * @return
		 */
		public IDecoyReferenceJudger getDecoyJudger() {
			return this.judger;
		}
	}
	
	
	protected static interface IListDetails{
		
	}
	
	
	/**
	 * The details write to the end of the
	 * 
	 * @author Xinning
	 * @version 0.2, 04-22-2009, 20:15:49
	 */
	protected static class ListDetails implements Serializable, IListDetails {

		/**
         * 
         */
		private static final long serialVersionUID = 1L;

		private ISearchParameter param;
		private IPeptideFormat<?> format;
		private IPeptideListIndex[] indexes;

		/**
		 * @param param
		 * @param peptideIndexes
		 */
		protected ListDetails(ISearchParameter param, IPeptideFormat<?> format,
		        IPeptideListIndex[] indexes) {
			this.param = param;
			this.format = format;
			this.indexes = indexes;
		}

		/**
		 * The search parameter
		 * 
		 * @return
		 */
		public ISearchParameter getSearchParameter() {
			return this.param;
		}

		/**
         * @return the format the peptide formatter
         */
        public IPeptideFormat<?> getFormat() {
        	return format;
        }

		/**
		 * The indexes of each peptides. This index is the byte position of the
		 * start of each line indicating the peptide and the byte position of
		 * the peak list. This is for the easy reading of target peptide from
		 * PeptideListAccesser.
		 * 
		 * @return
		 */
		public IPeptideListIndex[] getPeptideIndexes() {
			return this.indexes;
		}
	}

	/**
	 * The byte index for each of the peptides in the peptide list
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 04-22-2009, 19:02:46
	 */
	protected static class DefaultPeptideListIndex implements IPeptideListIndex {

		/**
         * 
         */
        private static final long serialVersionUID = 1L;
        
		private int position;
		private int peakPosition;
		private int numPeakList;

		/**
		 * @param position
		 * @param peakPositions
		 */
		protected DefaultPeptideListIndex(int position, int peakPosition,
		        int numPeakList) {
			this.position = position;
			this.peakPosition = peakPosition;
			this.numPeakList = numPeakList;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListIndex
		 * #getPeptideStartPosition()
		 */
		@Override
		public int getPeptideStartPosition() {
			return this.position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListIndex
		 * #getSpectraStartPositions()
		 */
		@Override
		public int getSpectraStartPositions() {
			return this.peakPosition;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListIndex
		 * #getNumerofSpectra()
		 */
		@Override
		public int getNumerofSpectra() {
			return numPeakList;
		}

		/* (non-Javadoc)
         * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.IPeptideListIndex#toLongPeptideListIndex()
         */
        @Override
        public ILongPeptideListIndex toLongPeptideListIndex() {
	        return new DefaultLongPeptideListIndex(this.position, this.peakPosition, this.numPeakList);
        }
	}
	
	
	/**
	 * The byte index for each of the peptides in the peptide list
	 * 
	 * 
	 * @author Xinning
	 * @version 0.1, 09-08-2010, 12:44:25
	 */
	protected static class DefaultLongPeptideListIndex implements ILongPeptideListIndex {

		/**
         * 
         */
        private static final long serialVersionUID = 1L;
        
		private long position;
		private long peakPosition;
		private int numPeakList;

		/**
		 * @param position
		 * @param peakPositions
		 */
		protected DefaultLongPeptideListIndex(long position, long peakPosition,
		        int numPeakList) {
			this.position = position;
			this.peakPosition = peakPosition;
			this.numPeakList = numPeakList;
		}

		/*
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.ILongPeptideListIndex#getPeptideStartPosition()
		 */
		@Override
		public long getPeptideStartPosition() {
			return this.position;
		}

		/*
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.ILongPeptideListIndex#getSpectraStartPositions()
		 */
		@Override
		public long getSpectraStartPositions() {
			return this.peakPosition;
		}

		/*
		 * (non-Javadoc)
		 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideListWriter.ILongPeptideListIndex#getNumerofSpectra()
		 */
		@Override
		public int getNumerofSpectra() {
			return numPeakList;
		}

	}
	
	/**
	 * The output file path
	 */
	@Override
	public String toString() {
		return this.output.getAbsolutePath();
	}
}
