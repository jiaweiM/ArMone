package cn.ac.dicp.gp1809.ga;

public class DefaultFitnessEvaluator implements FitnessEvaluator{
	
	public boolean isFitter(final double a_fitness_value1,final double a_fitness_value2) {
		return a_fitness_value1 > a_fitness_value2;
	}

	public boolean isFitter(Chromosome a_chrom1, Chromosome a_chrom2) {
		return isFitter(a_chrom1.getFitnessValue(), a_chrom2.getFitnessValue());
	}
}
