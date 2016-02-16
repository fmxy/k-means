package sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans {

	public void run(List<Point> points, int k) {

		List<Cluster> clusters = new ArrayList<Cluster>();

		// create clusters
		for (int i = 0; i < k; i++) {
			Cluster cluster = new Cluster();
			// cluster.add(centroids.get(i));
			clusters.add(cluster);
		}

		int iterations = 10;
		for (int i = 0; i < iterations; i++) {

			// for all datapoints calculate distance to centers
			for (Point p : points) {
					
				// ugly
				double savedDistance = 100;
				Cluster nearestCluster = null;

				for (Cluster cluster : clusters) {

					Point clusterMean = points.get(new Random().nextInt(points
							.size()));
					if (i > 0) {
						clusterMean = cluster.getCentroid();
					}

					double distance = calculateDistance(p, clusterMean);
					// System.out.println("distance is: " + distance);
					if (distance < savedDistance) {
						nearestCluster = cluster;
					}
					savedDistance = distance;
				}
				// assign point to cluster
				// TODO: catch nullpointer
				nearestCluster.addPoint(p);
			}
			for (Cluster cluster : clusters) {
				cluster.updateCentroid();
				if (i < iterations - 1) {
					cluster.clearPoints();
				}
			}
		}

		// output
		for (Cluster cluster : clusters) {
			System.out.println("This cluster contains "
					+ cluster.getPoints().size() + " elements.");
			System.out.println("Its elements are:");
			for (Point p : cluster.getPoints()) {
				System.out.println(p.toString());
			}
			System.out.println("");
		}

	}

	private double calculateDistance(Point p, Point c) {
		return Math.sqrt(sq(p.getX() - c.getX()) + sq(p.getY() - c.getY()));
	}

	private double sq(double x) {
		return x * x;
	}

}