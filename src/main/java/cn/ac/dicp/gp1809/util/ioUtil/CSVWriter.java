package cn.ac.dicp.gp1809.util.ioUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * Writer for csv files.
 * 
 * @author Xingning Jiang(vext@dicp.ac.cn)
 *
 */
public class CSVWriter {
	/*Whether the title has been writed for this csv file*/
	private boolean isTitleWrited;
	/* 
	 * For the csv files, the number of attributes defined in the title must equal to
	 * the number of attributes in each instence. This value is used to check whether
	 * this write is valide.
	 */
	private int col;
	
	private PrintWriter pw;
	
	/**
	 * Create a csv writer without writing of title.
	 * The method of writeTitle must be excuted afterward.
	 * 
	 * @param filename the name of cvs file.
	 * @throws IOException 
	 */
	public CSVWriter(String filename) throws IOException{
		String name = this.checkExtension(filename);
		pw = new PrintWriter(new BufferedWriter(new FileWriter(name)));
	}
	
	public CSVWriter(String filename, String[] title) throws IOException{
		this(filename);
		this.writeTitle(title);
	}
	
	private String checkExtension(String originname){
		String lowname = originname.toLowerCase();
		if(lowname.endsWith(".csv"))
			return originname;
		else
			return originname+".csv";
	}
	
	/**
	 * Write the tile contains all the attribute name into the csv file;
	 * <b> Null is not permited including the title object and the name of the attribute</b>
	 * @param title
	 * @throws NullPointerException if null objects inputed.
	 */
	public void writeTitle(String[] title){
		if(title==null)
			throw new NullPointerException("The title containing attributes must not be null!");
		
		int len = title.length;
		if(len==0)
			throw new NullPointerException("There must be more than one attribute in the title! Current: "+len);
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++){
			String atn = title[i];
			if(atn==null)
				throw new NullPointerException("The attribute name in the title must not be null!");
			sb.append(atn).append(",");
		}
		//elimate the last comma
		sb.setLength(sb.length()-1);
		col = len;
		pw.println(sb);
	}
	
	/**
	 * Write the instence into the file.
	 * The number of the atrribute must equals to the number of attribute names defined in the title.
	 * 
	 * Using null to indicate the miss attribute value.
	 * 
	 * @param instence
	 * @throws NullPointerException if null objects inputed.
	 */
	public void write(String[] instence){
		if(!this.isTitleWrited)
			throw new RuntimeException("Must write title first!");
		if(instence==null)
			throw new NullPointerException("The instence inputed must not be null!");
		int len = instence.length;
		if(len != col)
			throw new RuntimeException("The attribute number in the instence must " +
					"equal to the number of atribute defined in the title. Expected: "+col+", Current: "+len);
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++){
			String atn = instence[i];
			//using a o length string to instead the null string
			if(atn==null)
				atn = "?";
			sb.append(atn).append(",");
		}
		sb.setLength(sb.length()-1);
		
		pw.println(sb);
	}
	
	public void write(String instence){
		String towrite = instence.replaceAll("\t", ",");
		pw.print(towrite);
	}
	
	/**
	 * Close the csv writer and write all the data to the storage.
	 */
	public void finish(){
		this.pw.close();
	}
	
}
