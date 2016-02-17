package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import sequential.Cluster;
import sequential.KMeans;
import sequential.Point;

public class KMeansTests {

	static KMeans kmeans = new KMeans();

	@BeforeClass
	public static void setupBeforeClass() {
		final List<Point> points = new ArrayList<Point>();
		Random r = new Random();

		for (int i = 0; i < 100; i++) {
			points.add(new Point(r.nextDouble(), r.nextDouble()));
			// printPoints();
		}
		kmeans.run(points, 8);
	}

	// check if distance from cluster points to cluster centroid is minimal
	// compared to other cluster centroids
	@Test
	public void clusterPointsBelongToCentroid() {
		Boolean fail=false;
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

}
