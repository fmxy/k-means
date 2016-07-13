import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.BeforeClass;
import org.junit.Test;

import central.Cluster;
import central.KMeans;
import central.Main;
import central.Point;
import util.RunStrategy;

public class KMeansTests {

	static KMeans kmeans = new KMeans();
	static List<Point> points = new ArrayList<Point>();

	static int n = 100;
	static int iterations = 100;
	static int k = 10;

	@BeforeClass
	public static void setupBeforeClass() throws InterruptedException, ExecutionException {
		Random r = new Random();

		points = Main.createRandomPoints(n);
		kmeans.run(points, k, iterations, RunStrategy.SEQUENTIAL);
		// kmeans.run(points, k, iterations, RunStrategy.PARALLEL);
	}

	@Test
	public void amountOfClustersIsK() {
		assertTrue(kmeans.getClusters().size() == k);
	}

	@Test
	public void allDataPointsAreInACluster() {
		int count = 0;
		for (Cluster cluster : kmeans.getClusters()) {
			count += cluster.getPoints().size();
		}
		System.out.println("There are " + points.size() + " data points and " + count + " cluster elements");
		assertTrue(points.size() == count);
	}

	/*
	 * check if distance from cluster points to cluster centroid is minimal
	 * compared to other cluster centroids
	 */
	@Test
	public void clusterPointsBelongToCentroid() {
		Boolean fail = false;
		List<Cluster> clusters = kmeans.getClusters();

		for (Cluster cluster : clusters) {

			for (Point p : cluster.getPoints()) {
				double distanceToOwnCentroid = kmeans.calculateDistance(p, cluster.getCentroid());

				for (Cluster c : clusters) {
					double distanceToOtherCentroid = kmeans.calculateDistance(p, c.getCentroid());
					fail = distanceToOtherCentroid < distanceToOwnCentroid;
				}
			}
		}
		assertFalse(fail);
	}

	/*
	 * check if calculateDistance returns the same result when changing
	 * parameter order
	 */
	@Test
	public void calculateDistanceIsCommutative() {
		// creating explicit points as x and y could be the same by random
		// selection
		Point p1 = new Point(0.1337, 0.42);
		Point p2 = new Point(0.18, 0.1303);

		// assertEquals is deprecated
		assertTrue(kmeans.calculateDistance(p1, p2) == kmeans.calculateDistance(p2, p1));
	}

	@Test
	public void calculateDistanceValueIsPositiveOrZero() {
		// creating random points
		Random r = new Random();

		Point p1 = new Point(r.nextDouble(), r.nextDouble());
		Point p2 = new Point(r.nextDouble(), r.nextDouble());

		assertTrue(kmeans.calculateDistance(p1, p2) >= 0);
	}

	// TODO: fill test logic
	// @Test
	public void parallelKMeansProducesSameSolutionAsSequentialKMeans() {
		Boolean fail = false;

		// check if points are in the same cluster

		assertTrue(!fail);

	}

	@Test
	public void createRandomPointsCreatesPoints() {
		points = Main.createRandomPoints(100);
		assertTrue(!points.isEmpty());
	}

	@Test
	public void createRandomPointsCreatesCorrectAmountOfPoints() {
		points = Main.createRandomPoints(n);
		assertTrue(points.size() == n);
	}

	@Test
	public void parseCSVFileCreatesPoints() throws NumberFormatException, IOException {
		points = Main.parseCSVFile();
		assertTrue(!points.isEmpty());
	}

	/**
	 * tests whether the benchmarking value of measured time is positive
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void timeProgresses() throws InterruptedException {
		long start = System.currentTimeMillis();
		Thread.sleep(30);
		long time = System.currentTimeMillis() - start;
		System.out.println(time);
		assertTrue(time > 0);
	}

}
