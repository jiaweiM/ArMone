package cn.ac.dicp.gp1809.ga;

public interface FitnessEvaluator {
	
	public boolean isFitter(double fitness_value1, double fitness_value2);

	public boolean isFitter(Chromosome chrom1, Chromosome chrom2);
}
