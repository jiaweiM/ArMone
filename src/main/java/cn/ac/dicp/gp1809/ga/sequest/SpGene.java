package cn.ac.dicp.gp1809.ga.sequest;


public class SpGene extends SequestGene {

	public SpGene(SequestConfiguration param) {
		super(param);
	}

	@Override
	protected double getActualUpperBound() {
		return this.getValueLimit().getSpUpperlimit();
	}


	@Override
	protected double getActualLowerBound() {
		return this.getValueLimit().getSpLowerlimit();
	}

	@Override
	protected void setLength() {
		this.setLength(((SequestConfiguration)this.getConfiguration()).getSpGeneBit());
		
	}

}
