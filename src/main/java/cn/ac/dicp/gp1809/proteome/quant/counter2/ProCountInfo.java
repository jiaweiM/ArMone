package cn.ac.dicp.gp1809.proteome.quant.counter2;

public class ProCountInfo {

	protected String ref;
	protected double scount;
	protected double sin;
	
	public ProCountInfo(String ref, double scount, double sin){
		this.ref = ref;
		this.scount = scount;
		this.sin = sin;
	}
	
	public void combine(ProCountInfo info){
		this.scount += info.scount;
		this.sin += info.sin;
	}

}
