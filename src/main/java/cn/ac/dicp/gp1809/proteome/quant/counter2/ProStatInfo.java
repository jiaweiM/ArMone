package cn.ac.dicp.gp1809.proteome.quant.counter2;

import java.text.DecimalFormat;

import cn.ac.dicp.gp1809.util.DecimalFormats;

public class ProStatInfo {

	private String ref;
	private double [] scs;
	private double [] sins;
	private double scratio;
	private double sinratio;
	
	private DecimalFormat df4 = DecimalFormats.DF0_4;
	
	public ProStatInfo(ProCountInfo info1, ProCountInfo info2){
		
		this.ref = info1.ref;
		
		double sc1 = info1.scount;
		double sc2 = info2.scount;
		
		double sin1 = info1.sin;
		double sin2 = info2.sin;
		
		this.scs = new double [] {sc1, sc2};
		this.sins = new double [] {sin1, sin2};
		
		this.scratio = sc1==0 ? 0 : sc2/sc1;
		this.sinratio = sin1==0 ? 0 : sin2/sin1;
		
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(ref).append("\t");
		
		sb.append(df4.format(scratio)).append("\t");
		sb.append(df4.format(sinratio)).append("\t");
		
		for(int i=0;i<scs.length;i++){
			sb.append(scs[i]).append("\t");
		}
		for(int i=0;i<sins.length;i++){
			sb.append(sins[i]).append("\t");
		}

		return sb.toString();
	}
	
}
