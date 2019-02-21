package cn.ac.dicp.gp1809.ga.sequest;

public class XcorrGene extends SequestGene{
	
	public XcorrGene(SequestConfiguration param) {
		super(param);
	}

	@Override
	protected double getActualUpperBound() {
		return this.getValueLimit().getXcorrUpperlimit();
	}


	@Override
	protected double getActualLowerBound() {
		return this.getValueLimit().getXcorrLowerlimit();
	}

	@Override
	protected void setLength() {
		this.setLength(((SequestConfiguration)this.getConfiguration()).getXcorrGeneBit());
	}

}
