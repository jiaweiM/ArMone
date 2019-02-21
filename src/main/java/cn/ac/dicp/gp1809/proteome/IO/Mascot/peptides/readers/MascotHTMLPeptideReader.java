package cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.readers;

import cn.ac.dicp.gp1809.proteome.IO.Mascot.*;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.Enzymes;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.DefaultMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.MascotPeptide;
import cn.ac.dicp.gp1809.proteome.IO.exceptions.PeptideParsingException;
import cn.ac.dicp.gp1809.proteome.aasequence.PeptideSequence;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReferencePool;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import org.htmlparser.Node;
import org.htmlparser.lexer.InputStreamSource;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.ParserException;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class MascotHTMLPeptideReader {
	
	private InputStreamSource ss;
	private Page page;
	private Lexer lexer;
	private Node node;

	private int pepPosition;
	
	private ProteinReferencePool pool;
	private LinkedList <String> pepList;
	private HashSet <ProteinReference> ref;
	
	private MascotParameter msParas;
	
	private static HashMap <String, MascotMod> usedMascotMods = new HashMap <String, MascotMod> ();
	
	private DefaultMascotPeptideFormat format = new DefaultMascotPeptideFormat();
	
	public MascotHTMLPeptideReader(String file, String unimod) 
		throws UnsupportedEncodingException, FileNotFoundException, ParserException, 
			ModsReadingException, InvalidEnzymeCleavageSiteException, XMLStreamException {
		this (new File(file), unimod);
		// TODO Auto-generated constructor stub
	}
	
	public MascotHTMLPeptideReader(File file, String unimod) 
		throws UnsupportedEncodingException, FileNotFoundException, ParserException,
			ModsReadingException, InvalidEnzymeCleavageSiteException, XMLStreamException {
//		super(file);
		// TODO Auto-generated constructor stub
		
		ss = new InputStreamSource(new FileInputStream(file));
		page = new Page(ss);
//		String encoding = page.getEncoding();
//		System.out.println(encoding);
		page.setEncoding("utf-8");
		lexer = new Lexer(page);
		getReadyToRead();
		getSearchParameter(unimod);
	}
	
	public MascotHTMLPeptideReader(String file) 
			throws UnsupportedEncodingException, FileNotFoundException, ParserException,
				ModsReadingException, InvalidEnzymeCleavageSiteException, XMLStreamException {
//			super(file);
			// TODO Auto-generated constructor stub
				
			ss = new InputStreamSource(new FileInputStream(file));
			page = new Page(ss);
//			String encoding = page.getEncoding();
//			System.out.println(encoding);
			page.setEncoding("utf-8");
			lexer = new Lexer(page);
			getReadyToRead();
		}
	
	public MascotHTMLPeptideReader(File file) 
		throws UnsupportedEncodingException, FileNotFoundException, ParserException,
			ModsReadingException, InvalidEnzymeCleavageSiteException, XMLStreamException {
//		super(file);
		// TODO Auto-generated constructor stub
			
		ss = new InputStreamSource(new FileInputStream(file));
		page = new Page(ss);
//		String encoding = page.getEncoding();
//		System.out.println(encoding);
		page.setEncoding("utf-8");
		lexer = new Lexer(page);
		getReadyToRead();
	}
	
	protected MascotPeptide getPeptideImp() throws PeptideParsingException {
		// TODO Auto-generated method stub
		
		String pepString = "";
		if(pepList==null||pepList.size()==0){
			try {
				readTable();
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pepString = pepList.removeFirst();
			if(pepString.equals("END")){
				return null;
			}
		}
		else if(pepList.size()>0){
			pepString = pepList.removeFirst();
		}
		
		
		try {

			return parsePeptide(pepString);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public MascotParameter getSearchParameter() {
		// TODO Auto-generated method stub
		return this.msParas;
	}

	public void close() {
		// TODO Auto-generated method stub
		try {
			this.ss.close();
			this.page.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void getReadyToRead() throws ParserException{
		
		node = lexer.nextNode();
		boolean begin1 = false;
		
		while(node!=null){
//			System.out.println(lexer.getCurrentLineNumber());
			
			node = lexer.nextNode();
			if(node instanceof TagNode){
				
				TagNode tag = (TagNode) node;
				
				if(tag.getTagName().equals("H1")){
					begin1 = true;
				}
				
				if(tag.getTagName().equals("H3")){
				
					begin1 = false;
					break;
				}
				
				if(tag.getText().equals("TABLE")){

				}
				
				if(tag.getText().equals("/TABLE")){

				}
				
			}
			
			if(node instanceof TextNode){
				
				TextNode text = (TextNode) node;
				
				if(begin1){
					String str = text.getText();
//					if(!str.equals("\n"))
//						System.out.println("~~~~~~~~~~~~"+str);
				}
			}	
		}
		
		while(node!=null){
			
//			System.out.println(lexer.getCurrentLineNumber());
			node = lexer.nextNode();
			if(node.getText().equals("/FORM")){
				pepPosition = lexer.getPosition();
				break;
			}	
		}
		
/*		while(node!=null){
//			System.out.println(lexer.getCurrentLineNumber());
			node = lexer.nextNode();
			if(node.getText().equals("H3")){
				break;
			}
		}
*/		
	}
	
	protected void getSearchParameter(String unimod) throws ParserException, ModsReadingException, 
		FileNotFoundException, XMLStreamException, InvalidEnzymeCleavageSiteException{
		
		boolean getText = false;
		String [] paras = new String [11];
		int num = 0;
		
		MascotMods mmods = new MascotMods(unimod);
		HashMap <String, MascotMod> modMap = mmods.getModMap();
		
		HashSet <MascotFixMod> fixMascotMods = new HashSet <MascotFixMod> ();
		HashSet <MascotVariableMod> variMascotMods = new HashSet <MascotVariableMod> ();
		boolean isMonoMass = false;
		
		while(node!=null){
			node = lexer.nextNode();
//			System.out.println("mascothtml212\t"+lexer.getCurrentLineNumber());
			if(node instanceof TagNode){
				TagNode tag = (TagNode) node;
				
				if(tag.getText().equals("B")){
					getText = true;
				}
				if(tag.getText().equals("/B")){
					num++;
					getText = false;
				}
				if(tag.getTagName().equals("TABLE")){
					break;
				}	
			}
			
			if(node instanceof TextNode && getText){
				TextNode text = (TextNode) node;
				paras [num] = text.getText().trim();
//				System.out.println(paras[num]+num);
			}
		}
		
		for(int i=1;i<5;i++){
			
			int j = paras[i].indexOf(":");
			String pName = paras[i].substring(0, j).trim();
			String pValue = paras[i].substring(j+1).trim();
			
			if(pName.equals("Enzyme")){
				
			}
			
			if(pName.equals("Fixed modifications")){
				String [] fMods = pValue.split(",");
				for(String str: fMods){
					String fModName = str.substring(0, str.indexOf("(")).trim();
					if(modMap.containsKey(fModName)){
						MascotMod msfMod = modMap.get(fModName);
						MascotFixMod mFixMod = new MascotFixMod(msfMod.getIndex(), msfMod.getName(),
								msfMod.getAddedMonoMass(),msfMod.getAddedAvgMass(), msfMod.getModifiedAt());
						fixMascotMods.add(mFixMod);
						usedMascotMods.put(fModName, modMap.get(fModName));					
					}
					else{
						MascotFixMod newFMod = new MascotFixMod(0,"noName",0.0,0.0,null);
						fixMascotMods.add(newFMod);
						usedMascotMods.put(newFMod.getName(), newFMod);
					}
				}
			}
			
			if(pName.equals("Variable modifications")){
				String [] vMods = pValue.split(",");
	//			System.out.println(fMods.length);
				for(String str: vMods){
					String vModName = str.substring(0, str.indexOf("(")).trim();
					if(modMap.containsKey(vModName)){
						MascotMod msvMod = modMap.get(vModName);
						MascotVariableMod mVariMod = new MascotVariableMod(msvMod.getIndex(), msvMod.getName(),
								msvMod.getAddedMonoMass(),msvMod.getAddedAvgMass(), msvMod.getModifiedAt());
						variMascotMods.add(mVariMod);
						usedMascotMods.put(vModName, modMap.get(vModName));
						
					}
					else{
						MascotVariableMod newVMod = new MascotVariableMod(0,"noName",0.0,0.0,null);
						variMascotMods.add(newVMod);
						usedMascotMods.put(newVMod.getName(), newVMod);
					}
				}
			}
			
			if(pName.equals("Mass values")){
				if(pValue.equalsIgnoreCase("Monoisotopic")){
					isMonoMass = true;
				}
			}
			
		}
//		System.out.println(usedMascotMods.size());
		msParas = new MascotParameter(fixMascotMods, variMascotMods, Enzymes.Trypsin, isMonoMass);
		lexer.setPosition(pepPosition);
	}
	
	protected void readTable() throws ParserException{
		
		pepList = new LinkedList<String>();
		ref = new HashSet <ProteinReference> ();

		boolean begin = false;
		boolean modif = false;
		StringBuilder tr = new StringBuilder();
		StringBuilder td = new StringBuilder();
		
		LinkedList <String> oneTable = new LinkedList <String> ();
		LinkedList <LinkedList<String>> tableList = new LinkedList <LinkedList<String>> ();
		LinkedList <LinkedList<String>> refList = new LinkedList <LinkedList<String>> ();
		
		int tableNum = 0;
		
		while(node!=null){
			node = lexer.nextNode();
//			System.out.println(lexer.getCurrentLineNumber());
			
			if(node instanceof TagNode){
				TagNode tag = (TagNode) node;
				
				if(tag.getTagName().equals("TABLE")&&!tag.getText().equals("/TABLE")){
					oneTable = new LinkedList<String>();
					begin = true;
				}
				
				if(tag.getText().equals("/TABLE")){
					if(oneTable!=null&&oneTable.size()>0){
//						System.out.println("~~~~~~~oneTable~~~~~~~~~"+oneTable.size());
						LinkedList <String> temp = new LinkedList<String>();
						temp.addAll(oneTable);
						tableList.addLast(temp);
					}
					tableNum++;
					begin = false;
				}
/*				
				if(tag.getText().equals("TR")){
						
				}
				
				if(tag.getTagName().equals("TD")&&!tag.getText().equals("/TD")){
					
				}
*/				
				if(tag.getText().equals("/TD")){
					String tdData = td.toString();

					if(!Pattern.matches("\\s+", tdData)){
						tr.append(tdData);
//						System.out.println("~~~~~~~~~td~~~~~~~~~"+tdData);
						tr.append("\t");
					}
					
					td.delete(0, td.capacity());
				}
				
				if(tag.getText().equals("/TR")){			
					String trData = tr.toString();
					if(trData!=null&&trData.length()>0){
						oneTable.addLast(trData);
					}
//					System.out.println("~~~~~trtrtrtr~~~"+trData);
					
					tr.delete(0, tr.capacity());
				}
				
				if(tag.getText().equals("U")){
					modif = true;
				}
				
				if(tag.getText().equals("/U")){
					modif = false;
				}
				
				if(tag.getText().equals("HR")){
					break;
				}			
			}
			
			if(node instanceof TextNode){
			
				TextNode text = (TextNode) node;
				String str = text.getText();
				
				if(str.equals("Peptide matches not assigned to protein hits:")){
					pepList.add("END");
					System.out.println("The peptide list is end.");
					return;
				}
				
				String str0 = str.replaceAll("\n", "");
				String str1 = str0.replaceAll("&nbsp;", "");
				
				if(!Pattern.matches("\\s+", str1)&&str1.length()>0){
					
//					System.out.println("~~~~~~~~~~newstr~~~~~~~~~"+str1);
					if(modif){
						str1 += "%";
					}
					td.append(str1);
				}	
			}
		}

	
		LinkedList <String> onePro = tableList.removeFirst();
		LinkedList notUse = tableList.removeFirst();
		refList.add(onePro);
		while(tableList.size()>2){
			refList.add(tableList.removeLast());
		}
		
//		if(this.pool == null)
//			this.pool = new ProteinReferencePool(this.getDecoyJudger());
		
//		System.out.println("~~~~~~~~num"+tableList.size());
		
		for(LinkedList <String> nameList:refList){
			String name = nameList.getFirst();
//			System.out.println(name);
			int beg = name.indexOf(".");
			name = name.substring(beg+1).trim();
			name = name.substring(0,11).trim();
			ProteinReference rf = this.pool.get(name);

			ref.add(rf);
		}
		
		pepList = tableList.removeFirst();
		pepList.removeFirst();
		
//			System.out.println("~~~~~~~pep~~~~~~~~"+pep);
		
	}
	
	private MascotPeptide parsePeptide(String pepString) throws ParserException{
		
		String [] info = pepString.split("\t");
//		System.out.println("~~~info~~"+info.length);
		
		String scanNum = info[1].trim();
		double mh = Double.parseDouble(info[2].trim());
		double mrEXP = Double.parseDouble(info[3].trim());
		double mrCal = Double.parseDouble(info[4].trim());
		short charge = (short) (mrEXP/mh+1);
		double deltaMs = mrEXP-mrCal;
		int miss = Integer.parseInt(info[6].trim());
		
		short score = 0;
		String scoreStr = info[7].trim();
		if(scoreStr.indexOf('(')>-1){
			int b = scoreStr.indexOf('(');
			int e = scoreStr.indexOf(')');
			score = Short.parseShort(scoreStr.substring((b+1), e).trim());
		}
		else{
			score = Short.parseShort(info[7].trim());
		}
		
		double eValue = Double.parseDouble(info[8].trim());
		short rank = Short.parseShort(info[9].trim());
		
		String peptide = info[10].trim();
		String pepSequence;
		
		if(peptide.indexOf("+")>-1){
			String sequence = peptide.substring(0, peptide.indexOf("+")).trim();
			String description = peptide.substring(peptide.indexOf("+")+1).trim();
			pepSequence = getSequence(sequence, description);
			
		}else
			pepSequence = peptide;
		
		MascotPeptide msPeptide = new MascotPeptide(scanNum, pepSequence, charge, mh, 
				deltaMs, rank, score, eValue, ref, format);
		
		return msPeptide;
	}
	
	public String getSequence(String rawSeq, String description){
		PeptideSequence raw = PeptideSequence.parseSequence(rawSeq);
		String [] subStr = rawSeq.split("%");
		String [] des = description.split(";");
		int [] modifAt = new int [subStr.length-1];
		MascotMod [] modifs = new MascotMod [subStr.length-1];
		int local = 0;
		
		for(int i=0;i<modifAt.length;i++){
			String sub = subStr[i];
			local += sub.length();
			modifAt[i] = local-2;
			String aa = sub.substring(sub.length()-1);
			for(String modDes:des){
				modDes = modDes.trim();
				if(modDes.charAt(1)==' '){
					modDes = modDes.substring(2);
				}
				
				int b = modDes.indexOf('(');
				int e = modDes.indexOf(')');
				String name = modDes.substring(0, b).trim();
				String sites = modDes.substring(b+1, e);
		
				if(aa.equals(".")){
					if(sites.contains("term")){
						if(usedMascotMods.containsKey(name)){
							modifs [i] = usedMascotMods.get(name);						
						}
						else{
							modifs [i] = usedMascotMods.get("noName");
						}
					}
				}
				else{
					if(sites.indexOf(aa)>-1){
						if(usedMascotMods.containsKey(name)){
							modifs [i] = usedMascotMods.get(name);
						}
						else{
							modifs [i] = usedMascotMods.get("noName");
						}
					}
				}
				
			}
		}
		String mascotPep = msParas.parseSequence(raw, modifs, modifAt);
		return mascotPep;
	}
	
	/* (non-Javadoc)
	 * @see cn.ac.dicp.gp1809.proteome.IO.ioUtil.IPeptideReader#getProNameAccesser()
	 */
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws ParserException 
	 * @throws XMLStreamException 
	 * @throws InvalidEnzymeCleavageSiteException 
	 * @throws ModsReadingException 
	 * @throws PeptideParsingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, 
		ParserException, ModsReadingException, InvalidEnzymeCleavageSiteException, XMLStreamException, PeptideParsingException {
		// TODO Auto-generated method stub
//		MascotHTMLPeptideReader reader = new MascotHTMLPeptideReader("E:\\IO\\" +
//		"091108glyco3Peptide Summary Report (Submitted from wy by Mascot Daemon on WORKSTATION).htm", "E:\\CK\\workspace\\ArCommon\\Mascot\\unimod.xml", null);
	
//		MascotHTMLPeptideReader reader = new MascotHTMLPeptideReader("E:\\Data\\" +
//				"SCX-ONLINE-DIMETHYL\\mouse-liver_W_5mM_10h01m24s\\Peptide Summary Report (Submitted from W-on-line-dimethyl by Mascot Daemon on SEARCHER6).htm", 
//				"E:\\CK\\workspace\\ArCommon\\bin\\Mascot\\unimod.xml");
		
//		MascotHTMLPeptideReader reader = new MascotHTMLPeptideReader("E:\\IO\\" +
//				"091108glyco1Peptide Summary Report (Submitted from wy by Mascot Daemon on WORKSTATION).htm", "E:\\CK\\workspace\\ArCommon\\Mascot\\unimod.xml", null);
		
		
//		MascotPeptide newPep = reader.getPeptideImp();
//		System.out.println(newPep);
		
/*		int i = 0;
		while(true){
			i++;
			String s = newPep.getSequence();
			String t = newPep.getScanNum();
			System.out.println(i+"..."+s+"@@"+t);
			newPep = reader.getPeptideImp();
			if(newPep==null)
				break;
		}
*/		
		
		MascotHTMLPeptideReader reader = new MascotHTMLPeptideReader("H:\\Validation\\" +
				"phospho_download\\Orbitrap_mgf\\CID_mgf\\mix5.htm");
		
	}

}
