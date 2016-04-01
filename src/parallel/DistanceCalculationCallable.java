package parallel;

import java.util.List;
import java.util.concurrent.Callable;

import sequential.Cluster;
import sequential.Point;

/**
 * calculates distance from a point to each cluster and returns nearest cluster
 *
 */
public class DistanceCalculationCallable implements Callable<Cluster> {

	private Point p;
	private List<Cluster> clusters;

	public DistanceCalculationCallable(Point p, List<Cluster> clusters) {
		this.p = p;
		this.clusters = clusters;
	}


	@Override
	public Cluster call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



	public double calculateDistance(Point p, Point c) {
		// Math.sqrt returns POSITIVE rounded(!) square root
		return Math.sqrt(sq(p.getX() - c.getX()) + sq(p.getY() - c.getY()));
	}

	public double sq(double x) {
		return x * x;
	}

}
