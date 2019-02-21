package cn.ac.dicp.gp1809.ga.sequest;

public class DeltaCnGene extends SequestGene{
	
	
	public DeltaCnGene(SequestConfiguration param) {
		super(param);
	}

	@Override
	protected double getActualUpperBound() {
		return this.getValueLimit().getDeltaCnUpperlimit();
	}


	@Override
	protected double getActualLowerBound() {
		return this.getValueLimit().getDeltaCnLowerlimit();
	}

	@Override
	protected void setLength() {
		this.setLength(((SequestConfiguration)this.getConfiguration()).getDeltaCnGeneBit());
		
	}
	
}
