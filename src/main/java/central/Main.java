package central;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import util.RunStrategy;

public class Main {

	// choice of data structure might be crucial
	static List<Point> points = new LinkedList<Point>();

	// number of data points
	static int n = 10000;
	static int k = 10;
	static int iterations = 100;

	public static void main(String[] args) throws FileNotFoundException {

		KMeans kmeans = new KMeans();

		// parsing
		// TODO: create csv file

		// parseCSVFile();

		createRandomPoints(n);

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

	private static void parseCSVFile() throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File("/path/to/csv"))) {

			scanner.useDelimiter(",");

			while (scanner.hasNext()) {
				System.out.println(scanner.next());
			}

			// scanner will be closed automatically in a try with resources
			// block
		}
	}

	/**
	 * Generates random data points and fills list with them
	 * 
	 * @param n
	 *            amount of points to be generated
	 */
	private static void createRandomPoints(int n) {
		Random r = new Random();

		for (int i = 0; i < n; i++) {
			points.add(new Point(r.nextDouble(), r.nextDouble()));
			// printPoints();
		}
	}

	private static void printPoints() {
		for (Point p : points) {
			System.out.println(p.toString());
		}
	}
}