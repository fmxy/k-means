package central;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.opencsv.CSVReader;

import util.RunStrategy;

public class Main {

	// choice of data structure might be crucial
	static List<Point> points = new LinkedList<Point>();

	// number of data points
	static int n = 10000;
	static int k = 10;
	static int iterations = 100;
	static String filePath = "points.csv";

	public static void main(String[] args) throws NumberFormatException, IOException {

		final KMeans kmeans = new KMeans();

		// parsing

		parseCSVFile();

		createRandomPoints(n);

		// make list of points immutable
		points = Collections.unmodifiableList(points);

		// TODO: create a benchmark setting with run parameters for the
		// different strategies

		// very simple form of time measurement (doesn't measure initialization
		// phase, as well)
		// beware of JVM cashing, garbage collection

		// sequential
		benchmarkXRuns(5, RunStrategy.SEQUENTIAL);

		// parallel
		// benchmarkXRuns(5, RunStrategy.PARALLEL);

		KMeans.run(points, k, iterations, RunStrategy.REDUCEMAP);

	}

	private static void benchmarkXRuns(int runs, RunStrategy runStrategy) {
		System.out.println("Running the " + runStrategy.toString().toLowerCase() + "algorithm 5 times..");
		long start = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			KMeans.run(points, k, iterations, runStrategy);
			// hint garbage collector to do a collection
			System.gc();
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("The " + runStrategy.toString().toLowerCase() + " algorithm ran " + time / 5
				+ " milliseconds on average");
		System.out.println(" ");
	}

	/**
	 * reads csv file with data points and stores them in a data structure
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void parseCSVFile() throws NumberFormatException, IOException {
		// TODO: create list type interface
		// TODO: adapt for arbitrary number of variables

		// create CSVReader object
		CSVReader reader = new CSVReader(new FileReader("points.csv"), ',');

		List<Point> readPoints = new ArrayList<Point>();
		// read line by line
		String[] record = null;
		// skip header row
		reader.readNext();

		while ((record = reader.readNext()) != null) {
			Point readPoint = new Point(Double.parseDouble(record[0]), Double.parseDouble(record[1]));
			points.add(readPoint);
		}

		reader.close();

		System.out.println(points);
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