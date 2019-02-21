/* 
 ******************************************************************************
 * File: AbstractPairXMLReader.java * * * Created on 2011-8-1
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.quant.rsc;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.ac.dicp.gp1809.proteome.quant.labelFree.LFreePeptidePair;
import cn.ac.dicp.gp1809.proteome.quant.modifQuan.ModInfo;
import cn.ac.dicp.gp1809.proteome.quant.profile.LabelType;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanPeptide;
import cn.ac.dicp.gp1809.proteome.quant.profile.QuanResult;
import cn.ac.dicp.gp1809.proteome.IO.proteome.IPeptide;
import cn.ac.dicp.gp1809.proteome.IO.proteome.SeqLocAround;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinNameAccesser;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.databasemanger.SimpleProInfo;
import cn.ac.dicp.gp1809.proteome.databasemanger.decoy.DefaultDecoyRefJudger;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * @author ck
 *
 * @version 2011-8-1, 15:29:24
 */
public abstract class AbstractPairXMLReader {

	protected Element root;
	protected Iterator <Element> feasIt;
	protected Iterator <Element> idenPepIt;
	protected File file;
	
	protected LabelType type;
	protected ModInfo [] mods;
	protected ModInfo [] noLabelMods;
	protected boolean gradient;

	protected double [] totalPairMedian;
	protected double [] theoryRatio;
	protected double [] realNormalFactor;

	protected String [] ratioNames;
	protected String [] allRatioNames;
	protected ProteinNameAccesser accesser;
	
	public AbstractPairXMLReader(String file) throws DocumentException {
		this(new File(file));
	}
	
	public AbstractPairXMLReader(File file) throws DocumentException {
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		this.root = document.getRootElement();		
		this.feasIt = root.elementIterator("Features_Pair");
		this.idenPepIt = this.root.elementIterator("Iden_Pep");
		this.file = file;
		
		getProfileData();
	}
	
	protected abstract void getProfileData();

	protected void setMods(){
		
		Iterator <Element> modIt = root.elementIterator("Modification");
		ArrayList <ModInfo> modlist = new ArrayList <ModInfo>();
		ArrayList <ModInfo> noLabelModlist = new ArrayList <ModInfo>();
		
		HashMap <Character, Double> labelModSet = type.getSymbolMap();
		if(labelModSet==null){
			labelModSet = new HashMap <Character, Double>();
		}
		
		while(modIt.hasNext()){
			Element eMod = modIt.next();
			String name = eMod.attributeValue("Name");
			double mass = Double.parseDouble(eMod.attributeValue("Mass"));
			char symbol = eMod.attributeValue("Symbol").charAt(0);
			String modStr = eMod.attributeValue("ModSite");
			if(modStr.equals("N-term")) modStr = "_n";
			ModSite site = ModSite.parseSite(modStr);		
			ModInfo info = new ModInfo(name, mass, symbol, site);
			modlist.add(info);
			
			if(!labelModSet.containsKey(symbol))
				noLabelModlist.add(info);
		}
		
		ModInfo [] modArray = modlist.toArray(new ModInfo[modlist.size()]);
		this.mods = modArray;

		ModInfo [] noLabelMod = noLabelModlist.toArray(new ModInfo[noLabelModlist.size()]);
		this.noLabelMods = noLabelMod;
	}
	
	public ModInfo[] getAllMods(){
		return this.mods;
	}
	
	public ModInfo[] getNoLabelMods(){
		return this.noLabelMods;
	}
	
	public double [] getRatiosMedian(){		
		return this.totalPairMedian;
	}
	
	/**
	 * 
	 */
	public abstract void readAllPairs();
	
	/**
	 * @return
	 */
	public abstract int getPairNum();
	
	/**
	 * 
	 * @param index
	 * @param reverse
	 * @param normal
	 * @return
	 */
	public abstract LFreePeptidePair [] getAllSelectedPairs(int [] index);
	
	public abstract LFreePeptidePair [] getAllSelectedPairs(int [] index, boolean normal, int [] outputRatio);
	
	public abstract LFreePeptidePair [] getAllSelectedPairs();
	
	public abstract LFreePeptidePair [] getAllSelectedPairs(boolean normal, int [] outputRatio);
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public abstract LFreePeptidePair getPair(int index);

	/**
	 * 
	 * @param index
	 * @param reverse
	 * @param nomod
	 * @param normal
	 * @return
	 * @throws Exception
	 */
	public abstract QuanResult [] getAllResult(int [] index, boolean nomod, 
			boolean normal, int [] outputRatio) throws Exception;
	
	/**
	 * 
	 * @param nomod
	 * @param normal
	 * @param outputRatio
	 * @return
	 * @throws Exception
	 */
	public abstract QuanResult [] getAllResult(boolean nomod, 
			boolean normal, int [] outputRatio) throws Exception;
	/**
	 * 
	 * @return
	 */
	public abstract String [] getTitle();
	
	/**
	 * 
	 */
	public abstract void close();
	
	/**
	 * 
	 */
	public void setTheoryRatio(double [] theoryRatio) {
		// TODO Auto-generated method stub
		DecimalFormat df4 = DecimalFormats.DF0_4;
		this.theoryRatio = theoryRatio;
		this.realNormalFactor = new double[theoryRatio.length*2];
		for(int i=0;i<theoryRatio.length;i++){
			
			realNormalFactor[i*2] = Double.parseDouble(df4.format(
					this.totalPairMedian[i]/theoryRatio[i]));
			
			realNormalFactor[i*2+1] = Double.parseDouble(df4.format(
					theoryRatio[i]/this.totalPairMedian[i]));
		}
	}

	public IPeptide getIdenPep(){

		if(idenPepIt.hasNext()){
			
			Element eIdenPep = idenPepIt.next();
			String baseName = eIdenPep.attributeValue("BaseName");
			int scanBeg = Integer.parseInt(eIdenPep.attributeValue("ScanBeg"));
			int scanEnd = Integer.parseInt(eIdenPep.attributeValue("ScanEnd"));
			String seq = eIdenPep.attributeValue("Sequence");
			String ref = eIdenPep.attributeValue("Reference");
			
			HashSet <ProteinReference> refset = new HashSet <ProteinReference>();
			HashMap <String, SeqLocAround> locAroundMap = new HashMap <String, SeqLocAround>();
			String [] reflist = ref.split("\\$");
			for(int i=0;i<reflist.length;i++){
				String [] sss = reflist[i].split("\\+");
				ProteinReference pr = ProteinReference.parseProReference(sss[0]);
				refset.add(pr);
				
				int beg = Integer.parseInt(sss[1]);
				int end = Integer.parseInt(sss[2]);
				
				if(sss.length==3){
					SeqLocAround sla = new SeqLocAround(beg, end, "", "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==4){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], "");
					locAroundMap.put(pr.toString(), sla);
				}else if(sss.length==5){
					SeqLocAround sla = new SeqLocAround(beg, end, sss[3], sss[4]);
					locAroundMap.put(sss[0], sla);
				}
			}
			
			IPeptide pep = new QuanPeptide(seq, (short)1, refset, baseName, scanBeg, scanEnd, locAroundMap);
			return pep;
		}
		
		return null;
	}
	
	protected void setProNameAccesser(){
		
		Iterator <Element> proIt = root.elementIterator("Protein_Info");

		Element ePro = proIt.next();
		int i1 = Integer.parseInt(ePro.attributeValue("Target_Length"));
		int i2 = Integer.parseInt(ePro.attributeValue("Decoy_Length"));
		boolean usePattern = Boolean.parseBoolean(ePro.attributeValue("Use_Pattern"));
		Pattern pattern = null;
		if(usePattern){
			pattern = Pattern.compile(ePro.attributeValue("Pattern"));
		}
		
		ProteinNameAccesser accesser = new ProteinNameAccesser(i1, i2, usePattern, pattern, new DefaultDecoyRefJudger());
		
		Iterator <Element> it = ePro.elementIterator();
		while(it.hasNext()){
			Element eInfo = it.next();
			String ref = eInfo.attributeValue("Reference");
			int length = Integer.parseInt(eInfo.attributeValue("Length"));
			double mw = Double.parseDouble(eInfo.attributeValue("MW"));
			double hydro = Double.parseDouble(eInfo.attributeValue("Hydro"));
			double PI = Double.parseDouble(eInfo.attributeValue("PI"));
			boolean isDecoy = Boolean.parseBoolean(eInfo.attributeValue("isDecoy"));
			String partRef = "";
			if(usePattern){
				Matcher matcher = pattern.matcher(ref);
				if (matcher.find()) {
					partRef = matcher.group();
				}
			}else{
				if(isDecoy){
					partRef = ref.substring(0, i2>ref.length() ? ref.length() : i2);
				}else{
					partRef = ref.substring(0, i1>ref.length() ? ref.length() : i1);
				}
			}
			
			SimpleProInfo pInfo = new SimpleProInfo(partRef, ref, length, mw, hydro, PI, isDecoy);
			accesser.addRef(partRef, pInfo);
		}
		this.accesser = accesser;	
	}

	public boolean isGradient(){
		return this.gradient;
	}

	/**
	 * 
	 * @return
	 */
	public LabelType getType(){
		return type;
	}

	/**
	 * 
	 * @return
	 */
	public ModInfo[] getMods() {
		// TODO Auto-generated method stub
		return getNoLabelMods();
	}
	
	/**
	 * 
	 * @return
	 */
	public ProteinNameAccesser getProNameAccesser() {
		// TODO Auto-generated method stub
		if(accesser==null)
			this.setProNameAccesser();
		
		return accesser;
	}
	
	/**
	 * 
	 * @return
	 */
	public File getFile() {
		// TODO Auto-generated method stub
		return file;
	}
	
	/**
	 * The names of the ratios which will be output
	 * @return
	 */
	public String[] getAllRatioNames() {
		// TODO Auto-generated method stub
		return this.allRatioNames;
	}
	
	/**
	 * 
	 * @return
	 */
	public Object[][] getRatioModelInfo() {
		// TODO Auto-generated method stub
		
		Object[][] objs = new Object[allRatioNames.length][3];
		for(int i=0;i<objs.length;i++){
			if(i%2==0)
				objs[i][0] = Boolean.TRUE;
			else
				objs[i][0] = Boolean.FALSE;
			
			objs[i][1] = allRatioNames[i];
			objs[i][2] = 1.000d;
		}
		
		return objs;
	}
}
