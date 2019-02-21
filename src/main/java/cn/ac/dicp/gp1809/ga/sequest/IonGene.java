package cn.ac.dicp.gp1809.ga.sequest;


public class IonGene extends SequestGene {

	public IonGene(SequestConfiguration param) {
		super(param);
	}

	@Override
	protected double getActualUpperBound() {
		return this.getValueLimit().getIonPercentUpperlimit();
	}


	@Override
	protected double getActualLowerBound() {
		return this.getValueLimit().getIonPercentLowlimit();
	}

	@Override
	protected void setLength() {
		this.setLength(((SequestConfiguration)this.getConfiguration()).getIonGeneBit());
	}

}
