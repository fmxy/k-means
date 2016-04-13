package sequential;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import main.Cluster;
import main.Point;
import parallel.DistanceCalculationCallable;
import util.RunStrategy;

public class KMeans {

	private List<Cluster> clusters = new LinkedList<Cluster>();

	// central method that runs the algorithm in a specific pattern according to
	// the chosen strategy
	public void run(List<Point> points, int k, int iterations, RunStrategy strategy) {

		switch (strategy) {

		case FORKJOIN:
			break;

		case PARALLEL:
			runInParallel(points, k, iterations);
			break;

		case SEQUENTIAL:
			runSequentially(points, k, iterations);
			break;

		case STREAM:
			break;

		}

	}

	private void runSequentially(List<Point> points, int k, int iterations) {
		// create clusters
		createAndInitializeClusters(points, k);

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
		printClusters();
	}

	private void printClusters() {
		for (Cluster cluster : clusters) {
			System.out.println("This cluster contains " + cluster.getPoints().size() + " elements.");
			System.out.println("Its elements are:");
			for (Point p : cluster.getPoints()) {
				System.out.println(p.toString());
			}
			System.out.println("");
		}
	}

	private void runInParallel(List<Point> points, int k, int iterations) {

		createAndInitializeClusters(points, k);

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for (int i = 1; i <= iterations; i++) {

			System.out.println("Iteration " + i + "/" + iterations);

			// for all datapoints calculate distance to centers
			for (Point p : points) {

				// create Callable that calculates Distances to cluster
				// centroids
				DistanceCalculationCallable callable = new DistanceCalculationCallable(p, clusters);

				Future<List<Cluster>> future = executor.submit(callable);
				try {
					clusters = future.get();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				} catch (ExecutionException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}

			}

			for (Cluster cluster : clusters) {
				cluster.updateCentroid();
				if (i < iterations) {
					cluster.clearPoints();
				}
			}
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
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

	/**
	 * Create clusters with random data point as initial centroid
	 * 
	 * @param points
	 *            data points to process
	 * @param k
	 *            amount of clusters
	 */
	private void createAndInitializeClusters(List<Point> points, int k) {
		for (int i = 0; i < k; i++) {
			Cluster cluster = new Cluster();
			cluster.setCentroid(points.get(new Random().nextInt(points.size())));
			clusters.add(cluster);
		}
	}
}