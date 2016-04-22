package main;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import util.RunStrategy;

public class Main {

	// choice of data structure might be crucial
	static List<Point> points = new LinkedList<Point>();

	// number of data points
	static int n = 10000;
	static int k = 10;
	static int iterations = 100;

	public static void main(String[] args) {

		KMeans kmeans = new KMeans();

		Random r = new Random();

		for (int i = 0; i < n; i++) {
			points.add(new Point(r.nextDouble(), r.nextDouble()));
			// printPoints();
		}

		// make list of points immutable
		points = Collections.unmodifiableList(points);

		// very simple form of time measurement (doesn't measure initialization
		// phase, as well)
		// beware of JVM cashing

		// sequential
		long start_s = System.currentTimeMillis();
		kmeans.run(points, k, iterations, RunStrategy.SEQUENTIAL);
		long time_s = System.currentTimeMillis() - start_s;

		System.out.println("The algorithm ran " + time_s + " milliseconds");

	}

	private static void printPoints() {
		for (Point p : points) {
			System.out.println(p.toString());
		}
	}
}