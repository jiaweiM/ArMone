package cn.ac.dicp.gp1809.clustering.kMean;



public abstract class DataPoint {
	
	protected Cluster cluster;
	protected double distance;
	protected int id;
	
	public DataPoint(int id){
		this.id = id;
	}

	public void setCluster(Cluster cluster) {   
        this.cluster = cluster;   
        calcDistance();
    }   

	public int getId(){
		return id;
	}
	
	/**
	 * 
	 */
	public abstract void calcDistance();
	
	/**
	 * 
	 */
	public abstract double calcDistance(Centroid centroid);
	
	/**
	 * 
	 * @return
	 */
	public double getDistance(){
		return this.distance;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public abstract DataPoint plus(DataPoint data);
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public abstract DataPoint divide(int n);
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	public abstract DataPoint multiply(int n);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
