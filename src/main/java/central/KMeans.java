package central;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import reducemap.MappingCallable;
import threadbased.DistanceCalculationCallable;
import util.RunStrategy;

public class KMeans {

	private static List<Cluster> clusters = new LinkedList<Cluster>();

	// central method that runs the algorithm in a specific pattern according to
	// the chosen strategy
	public static void run(List<Point> points, int k, int iterations, RunStrategy strategy)
			throws InterruptedException {

		switch (strategy) {

		case FORKJOIN:
			// runWithForkJoin(points, k, iterations);
			break;

		case PARALLEL:
			runInParallel(points, k, iterations);
			break;

		case SEQUENTIAL:
			runSequentially(points, k, iterations);
			break;

		case STREAM:
			// add andrea ferretti github version?
			break;

		case REDUCEMAP:
			runReduceMap(points, k, iterations);
			break;

		}

	}

	private static void runReduceMap(List<Point> points, int k, int iterations) throws InterruptedException {

		// try cached threadpool, as well
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<MappingCallable> callables = new ArrayList<MappingCallable>();

		for (int i = 1; i <= iterations; i++) {

			System.out.println("Iteration " + i + "/" + iterations);

			// threads need fast read-only access, updated after every iteration
			List<Point> centroids = points.subList(0, k);

			// Eight Simple Rules:
			// Split point list into sublists (watch out for views and real
			// sublists)

			int n = Runtime.getRuntime().availableProcessors();
			// use guava to split list
			List<List<Point>> sublists = Lists.partition(points, points.size() / n);

			// TODO: add loop that adds key(sublist) and value(cluster
			// number)

			// list of futures to catch result
			List<Future<Multiset>> results;

			// global mean?

			// process sublists in multiple threads, join
			System.out.println("Processing " + n + " multimaps");
			for (List<Point> sublist : sublists) {
				// calculate distances to have initial cluster assignments

				// create hashmultiset
				Multimap<Integer, Point> multimap = ArrayListMultimap.create();

				for (Point p : sublist) {
					double savedDistance = 100;

					// ugly
					int clusterNumber = 0;

					for (Point centroid : centroids) {
						int index = 1;
						double distance = calculateDistance(p, centroid);
						// System.out.println("distance is: " + distance);
						if (distance <= savedDistance) {
							clusterNumber = index;
							savedDistance = distance;
						}
						index++;
					}
					// assign point to cluster
					multimap.put(clusterNumber, p);

					// test
					System.out.println(multimap.size());
				}
				callables.add(new MappingCallable(multimap));
			}
		}

		// get future result (process callables)
		List<Future<Multimap>> results = executor.invokeAll(callables);

		// recalculate centroids here as all elements across sublists must
		// be considered (no local means)
		// or: collect local means and join them (to global mean)

		// revisit this
		executor.shutdownNow();
	}

	private void runWithForkJoin(List<Point> points, int k, int iterations) {

		final ForkJoinPool fjp = new ForkJoinPool();
		// TODO make a forkjointask for all

	}

	private static void runSequentially(List<Point> points, int k, int iterations) {
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
				try {
					nearestCluster.addPoint(p);
				} catch (NullPointerException E) {
					System.err.println(E.getMessage());
				}

			}

			for (Cluster cluster : clusters) {
				cluster.updateCentroid();
				if (i < iterations) {
					cluster.clearPoints();
				}
			}
		}
		System.out.println(" ");
		// printClusters();
	}

	private static void printClusters() {
		for (Cluster cluster : clusters) {
			System.out.println("This cluster contains " + cluster.getPoints().size() + " elements.");
			System.out.println("Its elements are:");
			for (Point p : cluster.getPoints()) {
				System.out.println(p.toString());
			}
			System.out.println("");
		}
	}

	private static void runInParallel(List<Point> points, int k, int iterations) {

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
		System.out.println(" ");

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
	public static double calculateDistance(Point p, Point c) {
		// Math.sqrt returns POSITIVE rounded(!) square root
		return Math.sqrt(sq(p.getX() - c.getX()) + sq(p.getY() - c.getY()));
	}

	public static double sq(double x) {
		return x * x;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	/**
	 * Create clusters with first k data points as initial centroids (as points
	 * have been created randomly, this is considered to be OK)
	 * 
	 * @param points
	 *            data points to process
	 * @param k
	 *            amount of clusters
	 */
	private static void createAndInitializeClusters(List<Point> points, int k) {
		for (int i = 0; i < k; i++) {
			Cluster cluster = new Cluster();

			// using the first k list elements; use 'new
			// Random().nextInt(points.size())' instead of i for random points
			cluster.setCentroid(points.get(i));
			clusters.add(cluster);
		}
	}
}