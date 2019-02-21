/* 
 ******************************************************************************
 * File: Glycosyl.java * * * Created on 2011-3-16
 *
 * Copyright (c) 2010 Kai Cheng cksakuraever@msn.com
 *
 * All right reserved. Use is subject to license terms.
 * 
 *******************************************************************************
 */
package cn.ac.dicp.gp1809.glyco;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ck
 *
 * @version 2011-3-16, 10:02:48
 */
public enum Glycosyl {
	
	Xylose("Xyl", "Xylose", new int[]{5, 8, 4, 0}, 132.04226, 132.11462, "[abox]-[dlx]xyl-PEN-[0-9x]:[0-9x]", 17),

	Fuc("Fuc", "Fucose", new int[]{6, 10, 4, 0}, 146.057909, 146.1412, "[abox]-[dlx]gal-HEX-[0-9x]:[0-9x]\\|[0-9x]:d$", 18),
		
	Hex("Hex", "Hexose", new int[]{6, 10, 5, 0}, 162.052825, 162.1406, "[abox]-[dlx](gal|man|glu)-HEX-[0-9x]:[0-9x]$", 6),
	
	Gal("Gal", "Galcose", new int[]{6, 10, 5, 0}, 162.052825, 162.1406, "[abox]-[dlx]gal-HEX-[0-9x]:[0-9x]$", 7),
	
	Glc("Glc", "Glucose", new int[]{6, 10, 5, 0}, 162.052825, 162.1406, "[abox]-[dlx]glc-HEX-[0-9x]:[0-9x]$", 9),
	
	Man("Man", "Mannose", new int[]{6, 10, 5, 0}, 162.052825, 162.1406, "[abox]-[dlx]man-HEX-[0-9x]:[0-9x]$", 8),
	
	HexNAc("HexNAc", "N-Acetylhexosamine", new int[]{8, 13, 5, 1}, 203.079373, 203.1925, 
			"[abox]-[dlx](gal|man|glu)-HEX-[0-9x]:[0-9x]\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)n-acetyl$", 1),
			
	ManNAc("ManNAc", "N-Acetylhexosamine", new int[]{8, 13, 5, 1}, 203.079373, 203.1925, 
			"[abox]-[dlx]man-HEX-[0-9x]:[0-9x]\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)n-acetyl$", 1),
	
	GalNAc("GalNAc", "N-Acetylhexosamine", new int[]{8, 13, 5, 1}, 203.079373, 203.1925, 
			"[abox]-[dlx]gal-HEX-[0-9x]:[0-9x]\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)n-acetyl$", 2),		
			
	GlcNAc("HexNAc", "N-Acetylhexosamine", new int[]{8, 13, 5, 1}, 203.079373, 203.1925, 
			"[abox]-[dlx]glc-HEX-[0-9x]:[0-9x]\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)n-acetyl$", 3),

	NeuAc_H2O("NeuAc_H2O", "N-Acetyl Neuraminic Acid Loss Water", new int[]{11, 15, 7, 1}, 
			273.080136635, 273.2393, "", 0),
	
	NeuAc("NeuAc", "N-Acetyl neuraminic Acid", new int[]{11, 17, 8, 1}, 291.095416635, 291.25458, 
			"[abox]-[dlx]gro-[dlx]gal-NON-[0-9x]:[0-9x]\\|[0-9x]:a\\|[0-9x]:keto\\|[0-9x]:d\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)n-acetyl$", 12),
	
	NeuGc("NeuGc", "N-glycoyl neuraminic acid", new int[]{11, 16, 9, 1}, 306.08250611849996, 306.24604, 
			"[abox]-[dlx]gro-[dlx]gal-NON-[0-9x]:[0-9x]\\|[0-9x]:a\\|[0-9x]:keto\\|[0-9x]:d\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)n-glycolyl$", 10),
			
	Galactosamine("GalpN", "Galactosamine", new int[]{6, 11, 4, 1}, 161.0688078475, 161.15583999999998,
			"[abox]-[dlx]gal-HEX-[0-9x]:[0-9x]\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)amino$", 5),
			
	Glucosamine("GlcpN", "Glucosamine", new int[]{6, 11, 4, 1}, 161.0688078475, 161.15583999999998,
			"[abox]-[dlx]glc-HEX-[0-9x]:[0-9x]\\|\\|\\(-?[0-9x]d:-?[0-9x]\\)amino$", 4),
			
	Galacturonic_acid("GalpA", "Galacturonic acid", new int[]{6, 9, 5, 1}, 175.048072406, 175.13936,
			"[abox]-[dlx]gal-HEX-[0-9x]:[0-9x]\\|[0-9x]:a$", 15),
			
	Glucuronic_acid("GlcpA", "Glucuronic acid", new int[]{6, 9, 5, 1}, 175.048072406, 175.13936,
			"[abox]-[dlx]Glc-HEX-[0-9x]:[0-9x]\\|[0-9x]:a$", 14),
			
	Iduronic_acid("IdopA", "Iduronic acid", new int[]{6, 9, 5, 1}, 175.048072406, 175.13936,
			"[abox]-[dlx]ido-HEX-[0-9x]:[0-9x]\\|[0-9x]:a$", 13),
			
	Mannuronic_acid("ManpA", "Mannuronic acid", new int[]{6, 9, 5, 1}, 175.048072406, 175.13936,
			"[abox]-[dlx]Man-HEX-[0-9x]:[0-9x]\\|[0-9x]:a$", 16),				
			
	KDN("Kdnp", "KDN", new int[]{9, 14, 8, 0}, 250.068867425, 250.20265999999998,
			"[abox]-[dlx]gro-[dlx]gal-NON-[0-9x]:[0-9x]\\|[0-9x]:a\\|[0-9x]:keto\\|[0-9x]:d$", 11),

	Unknown("Unknown", "Unknown", new int[]{6, 10, 5, 0}, 0.0, 0.0, "", 20),
	;
	
	private String title;
	private String fullname;
	/**
	 * C,H,O
	 */
	private int [] CHO;
	private double mono_mass;
	private double avge_mass;
	private String pattern;
	private int graphicsId;
	
	Glycosyl(String title, String fullname, int [] CHO,double mono_mass, double avge_mass, String pattern, int graphicsId){
		this.title = title;
		this.fullname = fullname;
		this.CHO = CHO;
		this.mono_mass = mono_mass;
		this.avge_mass = avge_mass;
		this.pattern = pattern;
		this.graphicsId = graphicsId;
	}
	
	public int getGraphicsId(){
		return graphicsId;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getFullname(){
		return fullname;
	}
	
	public void setFullname(String fullname){
		this.fullname = fullname;
	}
	
	public double getMonoMass(){
		return mono_mass;
	}
	
	public void setMonoMass(double mono_mass){
		this.mono_mass = mono_mass;
	}
	
	public double getAvgeMass(){
		return avge_mass;
	}
	
	public void setAvgeMass(double avge_mass){
		this.avge_mass = avge_mass;
	}
	
	public String getPattern(){
		return pattern;
	}
	
	public int [] getCHON(){
		return this.CHO;
	}
	
	public static Glycosyl judgeType(String fullname){
		
		Glycosyl [] gs = Glycosyl.values();
		Glycosyl g = null;
		int id = -1;
		
		for(int i=0;i<gs.length;i++){
			
			Pattern pattern = Pattern.compile(gs[i].getPattern());
			Matcher m = pattern.matcher(fullname);
			if(m.matches()){
				if(gs[i].graphicsId>id){
					g = gs[i];
					id = g.graphicsId;
				}
			}
		}
		
		if(g!=null)
			return g;
		
		return Glycosyl.Unknown;
	}
	
	
}
