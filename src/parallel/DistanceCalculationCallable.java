package parallel;

import java.util.List;
import java.util.concurrent.Callable;

import sequential.Cluster;
import sequential.Point;

/**
 * calculates distance from a point to each cluster and returns nearest cluster
 *
 */
public class DistanceCalculationCallable implements Callable<List<Cluster>> {

	private Point p;
	private List<Cluster> clusters;

	public DistanceCalculationCallable(Point p, List<Cluster> clusters) {
		this.p = p;
		this.clusters = clusters;
	}

	@Override
	public List<Cluster> call(){
		double savedDistance = 1000000;
		Cluster nearestCluster = null;

		for (Cluster cluster : clusters) {

			Point clusterMean = cluster.getCentroid();

			double distance = calculateDistance(p, clusterMean);
			// System.out.println("distance is: " + distance);
			if (distance <= savedDistance) {
				nearestCluster = cluster;
				savedDistance = distance;
			}
		}
		
		// assigning point to cluster
		nearestCluster.addPoint(p);
		return clusters;
	}

	public double calculateDistance(Point p, Point c) {
		// Math.sqrt returns POSITIVE rounded(!) square root
		return Math.sqrt(sq(p.getX() - c.getX()) + sq(p.getY() - c.getY()));
	}

	public double sq(double x) {
		return x * x;
	}

}
