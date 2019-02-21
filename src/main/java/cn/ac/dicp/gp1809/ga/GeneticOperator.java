package cn.ac.dicp.gp1809.ga;

import java.util.Random;

/**
 * 
 * @author Xingning Jiang(vext@dicp.ac.cn)
 * 
 * Operator for genes: contains cross-over and mutation
 *
 */
public class GeneticOperator {
	
	private Configuration config;
	private Random rand;
	
	public GeneticOperator(Configuration config){
		this.config = config;
		this.rand = new Random();
	}
	
	public Configuration getConfiguration(){
		return this.config;
	}
	
	public void operate(Population population){
		this.crossover(population);
		this.mutate(population);
	}
	
	protected void mutate(Population population){
		int size = population.size()-1;
		for(int i=0;i<size;i++){
			population.getChromosome(i).mutate();
		}
	}
	
	protected void crossover(Population population){
		//The last one (elite) does not incorporate;
		int size= population.size()-1;
		int numCrossovers = (int) (size*getConfiguration().getCrossRate());
		
		Random temprand = this.rand;
		
		int index1, index2;
		for (int i = 0; i < numCrossovers; i++) {
			index1 = temprand.nextInt(size);
			index2 = temprand.nextInt(size);
			Chromosome chrom1 = population.getChromosome(index1);
			Chromosome chrom2 = population.getChromosome(index2);
			
			int length = chrom1.size();
			int locus = temprand.nextInt(length);
			int len = length-locus;
			
			chrom1.cross(chrom2,locus,len);
		}
	}
}
