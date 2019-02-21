package cn.ac.dicp.gp1809.ga.sequest;


public class RspGene extends SequestGene {

	public RspGene(SequestConfiguration param) {
		super(param);
	}

	@Override
	protected double getActualUpperBound() {
		return this.getValueLimit().getRspUpperlimit();
	}


	@Override
	protected double getActualLowerBound() {
		return this.getValueLimit().getRspLowerlimit();
	}

	@Override
	protected void setLength() {
		this.setLength(((SequestConfiguration)this.getConfiguration()).getRspGeneBit());
	}

}
