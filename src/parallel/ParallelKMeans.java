package parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import main.Cluster;
import main.Point;
import sequential.KMeans;

// maybe change to interface
public class ParallelKMeans extends KMeans {

	private List<Cluster> clusters = new ArrayList<Cluster>();

	public void run(List<Point> points, int k, int iterations) {

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
		// output
		for (Cluster cluster : clusters) {
			System.out.println("This cluster contains " + cluster.getPoints().size() + " elements.");
			System.out.println("Its elements are:");
			for (Point p : cluster.getPoints()) {
				System.out.println(p.toString());
			}
			System.out.println("");
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		}
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
