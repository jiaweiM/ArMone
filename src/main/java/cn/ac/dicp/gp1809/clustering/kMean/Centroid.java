package cn.ac.dicp.gp1809.clustering.kMean;


public class Centroid {
	
	private Cluster cluster;
	private DataPoint dp;
	
	public Centroid(DataPoint dp){
		this.dp = dp;
	}
	
	public void calcCentroid(){

		this.dp = this.cluster.getMeanDataPoint();
		int num = this.cluster.getNumOfDP();
		for(int i=0;i<num;i++){
			this.cluster.getDataPoint(i).calcDistance();
		}
		this.cluster.calcSumOfDis();
	}
	
	public Cluster getCluster(){
		return cluster;
	}
	
	public void setCluster(Cluster cluster){
		this.cluster = cluster;
	}
	
	public DataPoint getDataPoint(){
		return dp;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
