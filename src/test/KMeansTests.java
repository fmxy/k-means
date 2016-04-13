package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import main.Cluster;
import main.Point;
import sequential.KMeans;
import util.RunStrategy;

public class KMeansTests {

	static KMeans kmeans = new KMeans();
	static List<Point> points = new ArrayList<Point>();

	static int n = 100;
	static int iterations = 100;
	static int k = 10;

	@BeforeClass
	public static void setupBeforeClass() {
		Random r = new Random();

		for (int i = 0; i < n; i++) {
			points.add(new Point(r.nextDouble(), r.nextDouble()));
			// printPoints();
		}
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
		Point p1 = new Point(0.1337, 0.42);
		Point p2 = new Point(0.18, 0.1303);

		// assertEquals is deprecated
		assertTrue(kmeans.calculateDistance(p1, p2) == kmeans.calculateDistance(p2, p1));
	}

	// TODO: fill test logic
	// @Test
	public void parallelKMeansProducesSameSolutionAsSequentialKMeans() {
		Boolean fail = false;

		// check if points are in the same cluster

		assertTrue(!fail);

	}

}
