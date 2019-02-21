/* 
 ******************************************************************************
 * File: MascotDatParser2.java * * * Created on 2011-4-11
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.parsers2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotFixMod;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotParameter;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.MascotVariableMod;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.dat.Enzymes;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.DefaultMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.IO.Mascot.peptides.IMascotPeptideFormat;
import cn.ac.dicp.gp1809.proteome.databasemanger.ProteinReference;
import cn.ac.dicp.gp1809.proteome.dbsearch.InvalidEnzymeCleavageSiteException;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModSite;
import cn.ac.dicp.gp1809.proteome.dbsearch.ModsReadingException;
import cn.ac.dicp.gp1809.util.DecimalFormats;

/**
 * 
 * @author JiaweiMao
 * @version Sep 17, 2015, 10:20:31 AM
 */
public class MascotDatParser2 {
	
	private BufferedReader reader;
	
	private MascotParameter parameter;
	private HashMap <Integer, MascotVariableMod> varModMap;
	private HashMap <MascotVariableMod, Character> modSymMap;
	private IMascotPeptideFormat format = new DefaultMascotPeptideFormat();
	private HashSet <ProteinReference> refSet;

	private final char[] symbol = new char[] { '*', '#', '@', '^', '~',
	        '$', '[', ']' };
	private final DecimalFormat df4 = DecimalFormats.DF0_4;

	public MascotDatParser2(String file) throws IOException, ModsReadingException, InvalidEnzymeCleavageSiteException{
		this(new File(file));
	}
	
	public MascotDatParser2(File file) throws IOException, ModsReadingException, InvalidEnzymeCleavageSiteException{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-16");   
		this.reader = new BufferedReader(isr);
		this.parseParameter();
	}
	
	private void parseParameter() throws IOException, ModsReadingException, InvalidEnzymeCleavageSiteException{
		
		this.varModMap = new HashMap <Integer, MascotVariableMod>();
		this.modSymMap = new HashMap <MascotVariableMod, Character>();
		boolean isMono = false;
		
		HashSet <MascotFixMod> fixMods = new HashSet<MascotFixMod>();
		HashSet <MascotVariableMod> variMods = new HashSet <MascotVariableMod>();
		
		String line;
		boolean begin = false;
		while((line=reader.readLine())!=null){
			System.out.println(line);
			if(line.startsWith("Content-Type")){
				if(line.contains("masses")){
					reader.readLine();
					begin = true;
					break;
				}else if(line.startsWith("MASS=")){
					if(line.contains("Monoisotopic")){
						isMono = true;
					}else{
						isMono = false;
					}
				}
			}
		}
		
		boolean findEnzyme = false;
		if(begin){
			int varcount = 1;
			int fixcount = 1;
			
			MascotVariableMod vmod = null;
			
			while((line=reader.readLine())!=null){
				if(line.startsWith("delta"+varcount)){
					String [] ss = line.split("[=, ]");
					
					HashSet <ModSite> modifiedAt = new HashSet<ModSite>();
					double add = Double.parseDouble(ss[1]);
					char [] site = ss[3].substring(1,ss[3].length()-1).toCharArray();
					if(site.length>2 && site[1]=='-'){
						if(site[0]=='N'){
							ModSite ms = ModSite.newInstance_PepNterm();
							modifiedAt.add(ms);
						}else if(site[0]=='C'){
							ModSite ms = ModSite.newInstance_PepCterm();
							modifiedAt.add(ms);
						}
					}else{
						for(int i=0;i<site.length;i++){
							ModSite ms = ModSite.newInstance_aa(site[i]);
							modifiedAt.add(ms);
						}
					}
					
					vmod = new MascotVariableMod(varcount, ss[2], add, add, modifiedAt);
				}else if(line.startsWith("NeutralLoss"+varcount+"=")){
					String [] ss = line.split("=");
					double neu = Double.parseDouble(ss[1]);
					if(neu>0){
						vmod.setNeutralloss(neu, neu);
					}
					varModMap.put(varcount, vmod);
					variMods.add(vmod);
					varcount++;
				}else if(line.startsWith("FixedMod"+fixcount+"=")){
					String [] ss = line.split("[=, ]");
					HashSet <ModSite> modifiedAt = new HashSet<ModSite>();
					double add = Double.parseDouble(ss[1]);
					char [] site = ss[3].substring(1,ss[3].length()-1).toCharArray();
					if(site.length>2 && site[1]=='-'){
						if(site[0]=='N'){
							ModSite ms = ModSite.newInstance_PepNterm();
							modifiedAt.add(ms);
						}else if(site[0]=='C'){
							ModSite ms = ModSite.newInstance_PepCterm();
							modifiedAt.add(ms);
						}
					}else{
						for(int i=0;i<site.length;i++){
							ModSite ms = ModSite.newInstance_aa(site[i]);
							modifiedAt.add(ms);
						}
					}
					MascotFixMod fmod = new MascotFixMod(fixcount, ss[2], add, add, modifiedAt);
					fixMods.add(fmod);
					fixcount++;
				}else if(line.startsWith("Content-Type")){
					if(line.contains("enzyme")){
						findEnzyme = true;
						break;
					}
				}
			}
		}else{
			throw new IOException("Can't find masses.");
		}
		
		Enzymes enzyme = null;
		if(findEnzyme){
			String title = "";
			String cleavage = "";
			String sense = "";
			while((line=reader.readLine())!=null){

				String [] ss = line.split(":");
				if(ss.length==2){
					if(ss[0].equals("Title")){
						title = ss[1];
					}else if(ss[0].equals("Cleavage")){
						cleavage = ss[1];
					}
				}else if(ss.length==1){
					if(ss[0].startsWith("Cterm")){
						sense = "CTERM";
						break;
					}else if(ss[0].startsWith("Nterm")){
						sense = "NTERM";
						break;
					}
				}
			}
			enzyme = new Enzymes(title, cleavage, "", sense);
//			System.out.println(enzyme);
			
		}else{
			enzyme = Enzymes.Trypsin;
		}
		
//		System.out.println(variMods);
//		System.out.println(fixMods);
		
		this.parameter = new MascotParameter(fixMods, variMods, enzyme, isMono);
	}
	
	private void parsePep() throws IOException{
		String line;
		boolean begin = false;
		while((line=reader.readLine())!=null){
			if(line.startsWith("Content-Type")){
				if(line.contains("peptides")){
					reader.readLine();
					begin = true;
					break;
				}
			}
		}
		
		if(begin){
			while((line=reader.readLine())!=null){
				
			}
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidEnzymeCleavageSiteException 
	 * @throws ModsReadingException 
	 */
	public static void main(String[] args) throws IOException, ModsReadingException, InvalidEnzymeCleavageSiteException {
		// TODO Auto-generated method stub

		long beg = System.currentTimeMillis();
		
		MascotDatParser2 p2 = new MascotDatParser2("F:\\data\\ModDatabase\\Phos_ZMY\\Mascot_hela-d\\jou\\F001294.dat");
		long end = System.currentTimeMillis();
		System.out.println((end-beg)/1000.0);
	}

}
