package cn.ac.dicp.gp1809.ga;

import java.util.Random;

/**
 * 
 * @author Xingning Jiang(vext@dicp.ac.cn)
 * 
 */
public class RandomGenerator {
	private Random rand;
	
	public RandomGenerator(){
		this.rand = new Random();
	}
	
	public RandomGenerator(long seed){
		this.rand = new Random(seed);
	}
	
	public String generateBinString(int length){
		char randchar[] = new char[length];
		
		for(int i=0;i<length;i++){
			if(rand.nextBoolean())
				randchar[i] = '0';
			else
				randchar[i] = '1';
		}
		
		return new String(randchar);
	}
	
	public double nextDouble(){
		return rand.nextDouble();
	}
	
	public boolean nextBoolean(){
		return rand.nextBoolean();
	}
	
	public int nextInt(int upint){
		return rand.nextInt(upint);
	}
}
