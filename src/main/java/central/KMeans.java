package central;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import reducemap.MappingCallable;
import stream.ParallelStreamKMeans;
import stream.StreamKMeans;
import threadbased.DistanceCalculationCallable;
import util.RunStrategy;

public class KMeans {

	private static List<Cluster> clusters = new LinkedList<Cluster>();

	// central method that runs the algorithm in a specific pattern according to
	// the chosen strategy
	public static void run(List<Point> points, int k, int iterations, RunStrategy strategy)
			throws InterruptedException, ExecutionException {

		switch (strategy) {

		case PARALLEL:
			runInParallel(points, k, iterations);
			break;

		case SEQUENTIAL:
			runSequentially(points, k, iterations);
			break;

		case STREAM:
			runWithStreams(points, k, iterations);
			break;

		case PARALLELSTREAM:
			runWithParallelStreams(points, k, iterations);
			break;

		case REDUCEMAP:
			runReduceMap(points, k, iterations);
			break;

		}

	}

	private static void runReduceMap(List<Point> points, int k, int iterations)
			throws InterruptedException, ExecutionException {

		System.out.println("Iteration 1");

		// try cached threadpool, as well
		int n = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<MappingCallable> callables = new ArrayList<MappingCallable>();

		// threads need fast read-only access, updated after every iteration
		// that's a view!
		List<Point> centroids = points.subList(0, k);

		// Eight Simple Rules: Split point list into sublists (watch out for
		// views and real sublists)

		// use guava to split list
		List<List<Point>> sublists = Lists.partition(points, points.size() / n);
		int assignmentNumber = 1;

		// process sublists in multiple threads, join
		System.out.println("Split points into " + n + " sublists");
		for (List<Point> sublist : sublists) {
			// calculate distances to have initial cluster assignments

			Multimap<Integer, Point> multimap = ArrayListMultimap.create();

			for (Point p : sublist) {
				double savedDistance = Double.MAX_VALUE;

				// ugly
				int clusterNumber = 0;
				int index = 1;

				for (Point centroid : centroids) {
					double distance = calculateDistance(p, centroid);
					// System.out.println("distance is: " + distance);
					if (distance <= savedDistance) {
						clusterNumber = index;
						savedDistance = distance;
					}
					index++;
				}
				// assign point to cluster
				// System.out.println("Assigning point " + p.toString() + "
				// to cluster " + clusterNumber);
				multimap.put(clusterNumber, p);
				// System.out.println("Assignment " + assignmentNumber);
				assignmentNumber++;
			}
			callables.add(new MappingCallable(multimap, centroids));
		}

		// get future result (process callables); makes sure all futures are
		// done
		List<Future<Multimap>> futures = executor.invokeAll(callables);
		List<Multimap<Integer, Point>> multimaps = new ArrayList<Multimap<Integer, Point>>();

		for (Future<Multimap> future : futures) {
			multimaps.add(future.get());
		}

		// recalculate centroids here as all elements across sublists must
		// be considered (no local means)

		// ugly
		Multimap<Integer, Point> allLocalMeans = ArrayListMultimap.create();
		for (Multimap<Integer, Point> m : multimaps) {
			// get cluster elements by integer key and sum them up (->local
			// means)
			for (int keyNumber = 1; keyNumber <= k; keyNumber++) {
				// get mean
				double xsum = 0;
				double ysum = 0;

				Collection<Point> values = m.get(keyNumber);
				for (Point p : values) {
					xsum += p.getX();
					ysum += p.getY();
				}

				Point localMean = new Point(xsum / values.size(), ysum / values.size());
				allLocalMeans.put(keyNumber, localMean);
			}
		}

		// calculate global means
		centroids = new ArrayList<Point>();
		for (int c = 0; c < n; c++) {

			double xsum = 0;
			double ysum = 0;

			Collection<Point> values = allLocalMeans.get(c + 1);
			for (Point p : values) {
				xsum += p.getX();
				ysum += p.getY();
			}

			Point globalMean = new Point(xsum / values.size(), ysum / values.size());
			// System.out.println(globalMean.toString());
			centroids.add(globalMean);

		}

		for (int i = 2; i <= iterations; i++) {

			System.out.println("Iteration " + i + "/" + iterations);

			// get future result (process callables); makes sure all futures are
			// done
			callables.clear();
			for (Multimap multimap2 : multimaps) {
				callables.add(new MappingCallable(multimap2, centroids));
			}

			// reset list
			multimaps = new ArrayList<Multimap<Integer, Point>>();
			List<Future<Multimap>> futures2 = executor.invokeAll(callables);

			for (Future<Multimap> future2 : futures2) {
				multimaps.add(future2.get());
			}

			// recalculate centroids here as all elements across sublists must
			// be considered (no local means)

			// ugly
			Multimap<Integer, Point> allLocalMeans2 = ArrayListMultimap.create();
			for (Multimap<Integer, Point> m : multimaps) {
				// get cluster elements by integer key and sum them up (->local
				// means)
				for (int keyNumber = 1; keyNumber <= k; keyNumber++) {
					// get mean
					double xsum = 0;
					double ysum = 0;

					Collection<Point> values = m.get(keyNumber);
					for (Point p : values) {
						xsum += p.getX();
						ysum += p.getY();
					}

					Point localMean2 = new Point(xsum / values.size(), ysum / values.size());
					allLocalMeans2.put(keyNumber, localMean2);
				}
			}

			// calculate global means
			centroids.clear();
			for (int c = 0; c < n; c++) {

				double xsum = 0;
				double ysum = 0;

				Collection<Point> values = allLocalMeans.get(c + 1);
				for (Point p : values) {
					xsum += p.getX();
					ysum += p.getY();
				}

				Point globalMean = new Point(xsum / values.size(), ysum / values.size());
				centroids.add(globalMean);

			}
		}

		executor.shutdownNow();
	}

	private static void runSequentially(List<Point> points, int k, int iterations) {
		// create clusters
		createAndInitializeClusters(points, k);

		for (int i = 1; i <= iterations; i++) {

			System.out.println("Iteration " + i + "/" + iterations);

			// for all datapoints calculate distance to centers
			for (Point p : points) {

				// ugly
				double savedDistance = Double.MAX_VALUE;
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
		// use printClusters(); to print cluster elements and current centroid
	}

	@SuppressWarnings("unused")
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

	private static void runWithStreams(List<Point> points, int k, int iterations) {
		final StreamKMeans streamKMeans = new StreamKMeans();
		streamKMeans.run(points, k, iterations);
	}

	private static void runWithParallelStreams(List<Point> points, int k, int iterations) {
		final ParallelStreamKMeans parallelStreamKMeans = new ParallelStreamKMeans();
		parallelStreamKMeans.run(points, k, iterations);
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