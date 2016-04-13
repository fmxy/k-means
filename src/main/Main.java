package main;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import parallel.ParallelKMeans;
import sequential.KMeans;

public class Main {
	static List<Point> points = new LinkedList<Point>();

	// number of data points
	static int n = 100;
	static int k = 10;
	static int iterations = 100;

	public static void main(String[] args) {

		KMeans sequentialKMeans = new KMeans();
		ParallelKMeans parallelKMeans = new ParallelKMeans();

		Random r = new Random();

		for (int i = 0; i < n; i++) {
			points.add(new Point(r.nextDouble(), r.nextDouble()));
			// printPoints();
		}

		// very simple form of time measurement (doesn't measure initialization
		// phase, as well)
		// beware of JVM cashing
		// TODO: adapt JVM parameters

		// sequential
		// long start_s = System.currentTimeMillis();
		// sequentialKMeans.run(points, k, iterations);
		// long time_s = System.currentTimeMillis() - start_s;

		// parallel (thread-based)
		long start_p = System.currentTimeMillis();
		parallelKMeans.run(points, k, iterations);
		long time_p = System.currentTimeMillis() - start_p;

		// System.out.println("The sequential algorithm ran " + time_s + " milli
		// seconds");
		System.out.println("The parallel algorithm ran " + time_p + " milli seconds");
	}

	private static void printPoints() {
		for (Point p : points) {
			System.out.println(p.toString());
		}
	}
}