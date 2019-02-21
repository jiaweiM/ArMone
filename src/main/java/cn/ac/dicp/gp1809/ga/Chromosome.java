package cn.ac.dicp.gp1809.ga;

/**
 * 
 * @author Xingning Jiang(vext@dicp.ac.cn)
 *
 */

public class Chromosome implements Cloneable{

	  /**
	   * The configuration object to use
	   */
	  private Configuration m_configuration;

	  /**
	   * The array of Genes contained in this Chromosome.
	   */
	  private Gene[] m_genes;
	  
	  /**
	   * Gene number in the chromosome;
	   */
	  private int length;
	  
	  /**
	   * Stores the fitness value of this Chromosome as determined by the
	   * active fitness function. A value of -1 indicates that this field
	   * has not yet been set with this Chromosome's fitness values (valid
	   * fitness values are always positive).
	   */
	  private double m_fitnessValue = FitnessFunction.NO_FITNESS_VALUE;

	  /**
	   * Constructor for specifying the number of genes.
	   * @param a_configuration the configuration to use
	   * @param a_desiredSize number of genes the chromosome contains of
	   */
	  public Chromosome(final Configuration a_configuration,final int a_desiredSize){
		  this.m_configuration = a_configuration;
		  if (a_desiredSize <= 0) {
			  throw new IllegalArgumentException("Chromosome size must be greater than zero");
		  }
		  m_genes = new Gene[a_desiredSize];
		  this.length = a_desiredSize;
	  }

	  /**
	   * Constructs a Chromosome of the given size separate from any specific
	   * Configuration. This constructor will use the given sample Gene to
	   * construct a new Chromosome instance containing genes all of the same
	   * type as the sample Gene. This can be useful for constructing sample
	   * chromosomes that use the same Gene type for all of their genes and that
	   * are to be used to setup a Configuration object.
	   *
	   * @param a_configuration the configuration to use
	   * @param a_sampleGene a concrete sampleGene instance that will be used
	   * as a template for all of the genes in this Chromosome
	   * @param a_desiredSize the desired size (number of genes) of this Chromosome
	   */
	  public Chromosome(final Configuration a_configuration,final Gene a_sampleGene, final int a_desiredSize){
	    this(a_configuration, a_desiredSize);
	    initFromGene(a_sampleGene);
	  }

	  protected void initFromGene(Gene a_sampleGene) {
	    if (a_sampleGene == null) {
	      throw new IllegalArgumentException("Sample Gene cannot be null.");
	    }

	    for (int i = 0; i < m_genes.length; i++) {
	      m_genes[i] = a_sampleGene.newGene();
	    }
	  }
	  
	  /**
	   * Use this chromosome as seed and generate another random instence of chromosome;
	   * @return another random generated chromosome;
	   */
	  public Chromosome newChromosome(){
	    	Chromosome copy = null;
			try {
				copy = (Chromosome) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
			copy.m_fitnessValue=FitnessFunction.NO_FITNESS_VALUE;
			
			int len = this.size();
			Gene[] tempgenes = new Gene[len];
			Gene[] origin = copy.getGenes();
			for(int i=0;i<len;i++){
				Gene temp = origin[i];
				//null gene is accepted
				if(temp!=null)
					temp = temp.newGene();
				
				tempgenes[i] = temp;
			}
	        
			copy.setGenes(tempgenes);
			
		    return copy;
	  }

	  @Override
	public Chromosome clone() {
	    
	    	Chromosome copy = null;
			try {
				copy = (Chromosome) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
			int len = this.size();
			Gene[] tempgenes = new Gene[len];
			Gene[] origin = copy.getGenes();
			for(int i=0;i<len;i++){
				Gene temp = origin[i];
				//null gene is accepted
				if(temp!=null)
					temp = temp.clone();
				
				tempgenes[i] = temp;
			}
	        
			copy.setGenes(tempgenes);
	        
		    return copy;
	  }
	  
	  public double[] values(){
			double[] values = new double[this.length];
			for(int i=0;i<this.length;i++){
				Gene temp = this.getGene(i);
				if(temp==null)
					values[i] = -1d;
				else
					values[i] = temp.value();
			}
			return values;
	  }
	  
	  /**
	   * Returns the Gene at the given index (locus) within the Chromosome. The
	   * first gene is at index zero and the last gene is at the index equal to
	   * the size of this Chromosome - 1.
	   *
	   * @param a_desiredLocus index of the gene value to be returned
	   * @return Gene at the given index
	   */
	  public Gene getGene(int a_desiredLocus) {
	    return m_genes[a_desiredLocus];
	  }

	  /**
	   * Retrieves the set of genes that make up this Chromosome. This method
	   * exists primarily for the benefit of GeneticOperators that require the
	   * ability to manipulate Chromosomes at a low level.
	   *
	   * @return an array of the Genes contained within this Chromosome
	   */
	  public Gene[] getGenes() {
	    return m_genes;
	  }

	  /**
	   * Returns the size of this Chromosome (the number of genes it contains).
	   * A Chromosome's size is constant and will not change, until setGenes(...)
	   * is used.
	   *
	   * @return number of genes contained within this Chromosome instance
	   */
	  public final int size() {
		  return this.length;
	  }

	  /**
	   * Retrieves the fitness value of this Chromosome, as determined by the
	   * active fitness function. If a bulk fitness function is in use and
	   * has not yet assigned a fitness value to this Chromosome, then -1 is
	   * returned.<p>
	   * Attention: should not be called from toString() as the fitness value would
	   * be computed if it was initial!
	   *
	   * @return a positive double value representing the fitness of this
	   * Chromosome, or -1 if a bulk fitness function is in use and has not yet
	   * assigned a fitness value to this Chromosome
	   */
	  public double getFitnessValue() {
	    if (m_fitnessValue >= 0.000d) {
	      return m_fitnessValue;
	    }
	    else {
	      return calcFitnessValue();
	    }
	  }

	  /**
	   * @return fitness value of this chromosome determined via the registered fitness function
	   */
	  protected double calcFitnessValue() {
	    if (getConfiguration() != null) {
	      FitnessFunction normalFitnessFunction = getConfiguration().getFitnessFunction();
	      if (normalFitnessFunction != null) {
	        m_fitnessValue = normalFitnessFunction.getFitnessValue(this);
	      }
	    }
	    return m_fitnessValue;
	  }


	  /**
	   * Sets the fitness value of this Chromosome directly without any
	   * constraint checks, conversions or checks. Only use if you know what
	   * you do.
	   *
	   * @param a_newFitnessValue a positive integer representing the fitness
	   * of this Chromosome
	   */
	  public void setFitnessValue(double a_newFitnessValue) {
	    m_fitnessValue = a_newFitnessValue;
	  }

	  /**
	   * Convenience method that returns a new Chromosome instance with its
	   * genes values (alleles) randomized. Note that, if possible, this method
	   * will acquire a Chromosome instance from the active ChromosomePool
	   * (if any) and then randomize its gene values before returning it. If a
	   * Chromosome cannot be acquired from the pool, then a new instance will
	   * be constructed and its gene values randomized before returning it.
	   *
	   * @param a_configuration the configuration to use
	   * @return randomly initialized Chromosome
	   * @throws InvalidConfigurationException if the given Configuration
	   * instance is invalid
	   * @throws IllegalArgumentException if the given Configuration instance
	   * is null
	   */
	  public static Chromosome randomInitialChromosome(Configuration a_configuration){
	    if (a_configuration == null) {
	      throw new IllegalArgumentException("Configuration instance must not be null");
	    }
	    
	    return a_configuration.getSampleChromosome().newChromosome();
	  }

	  public void setGene(Gene a_gene, int a_desiredLocus){
		  this.m_genes[a_desiredLocus] = a_gene;
	  }
	  
	  /**
	   * Sets the genes for the chromosome.
	   * @param a_genes the genes to set for the chromosome
	   */
	  public void setGenes(Gene[] a_genes){
	    m_genes = a_genes;
	    this.length = m_genes.length;
	  }

	  /**
	   * @return the configuration used
	   */
	  public Configuration getConfiguration() {
	    return m_configuration;
	  }
	  
	  /**
	   * Mutate
	   * @return if mutation occured;
	   */
	  public boolean mutate(){
		  boolean mutated = false;
		  for(int i=0;i<this.length;i++){
			  Gene gene = this.m_genes[i];
			  if(gene!=null)
				  if(gene.mutate())
					  mutated=true;
		  }
		  if(mutated)
			  this.m_fitnessValue = FitnessFunction.NO_FITNESS_VALUE;
		  
		  return mutated;
	  }
	  
	  /**
	   * cross with chrome specified from the locus with len genes crossed;
	   * 
	   * @param chrom2,
	   * @param locus from which point cross over occured
	   * @param len how many genes to cross;
	   */
	  public void cross(Chromosome chrom2,int locus,int len){
		  Gene[] temp = new Gene[len];
		  System.arraycopy(this.m_genes,locus,temp,0,len);
		  
		  System.arraycopy(chrom2.m_genes,locus,this.m_genes,locus,len);
		  System.arraycopy(temp,0,chrom2.m_genes,locus,len);
		  
		  this.m_fitnessValue = FitnessFunction.NO_FITNESS_VALUE;
		  chrom2.m_fitnessValue = FitnessFunction.NO_FITNESS_VALUE;
	  }

}
