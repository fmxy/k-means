package sequential;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import main.Cluster;
import main.Point;

public class KMeans {

	// TODO: implement strategy pattern for use of different data structures
	private List<Cluster> clusters = new LinkedList<Cluster>();

	public void run(List<Point> points, int k, int iterations) {

		// create clusters
		for (int i = 0; i < k; i++) {
			Cluster cluster = new Cluster();
			cluster.setCentroid(points.get(new Random().nextInt(points.size())));
			clusters.add(cluster);
		}

		for (int i = 1; i <= iterations; i++) {

			System.out.println("Iteration " + i + "/" + iterations);

			// for all datapoints calculate distance to centers
			for (Point p : points) {

				// ugly
				double savedDistance = 100;
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
				// assign point to cluster
				// TODO: catch nullpointer
				nearestCluster.addPoint(p);
			}
			for (Cluster cluster : clusters) {
				cluster.updateCentroid();
				if (i < iterations) {
					cluster.clearPoints();
				}
			}
		}

		// output
		for (Cluster cluster : clusters) {
			System.out.println("This cluster contains " + cluster.getPoints().size() + " elements.");
			System.out.println("Its elements are:");
			for (Point p : cluster.getPoints()) {
				System.out.println(p.toString());
			}
			System.out.println("");
		}

	}

	/**
	 * calculates the euclidean distance between two data points, used for
	 * selection of nearest centroid
	 * 
	 * @param p
	 *            Point 1
	 * @param c
	 *            Point 2
	 * @return positive rounded distance value
	 */
	public double calculateDistance(Point p, Point c) {
		// Math.sqrt returns POSITIVE rounded(!) square root
		return Math.sqrt(sq(p.getX() - c.getX()) + sq(p.getY() - c.getY()));
	}

	public double sq(double x) {
		return x * x;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

}