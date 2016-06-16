package central;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.opencsv.CSVReader;

import util.RunStrategy;

public class Main {

	// choice of data structure might be crucial
	static List<Point> points = new LinkedList<Point>();

	// number of data points
	static int n = 10000;
	static int k = 10;
	static int iterations = 100;
	// default
	static String filePath = "points_10000000.csv";

	public static void main(String[] args)
			throws NumberFormatException, IOException, InterruptedException, ExecutionException {

		if (args.length > 0) {
			if (args.length > 1) {
				System.err.println("Too many arguments");
			} else {
				filePath = args[0];
			}
		}

		final KMeans kmeans = new KMeans();

		// parsing a a data set or creating random points is possible

		points = parseCSVFile();

		// points = createRandomPoints(n);

		// make list of points immutable
		points = Collections.unmodifiableList(points);

		// very simple form of time measurement (doesn't measure initialization
		// phase, as well)
		// beware of JVM cashing, garbage collection

		// sequential
		benchmarkXRuns(1, RunStrategy.SEQUENTIAL);

		// parallel
		// benchmarkXRuns(1, RunStrategy.PARALLEL);

		// reducemap
		benchmarkXRuns(1, RunStrategy.REDUCEMAP);
	}

	private static void benchmarkXRuns(int runs, RunStrategy runStrategy)
			throws InterruptedException, ExecutionException {
		System.out.println("Running the " + runStrategy.toString().toLowerCase() + " algorithm " + runs + " times..");
		System.out.println(" ");
		long start = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			KMeans.run(points, k, iterations, runStrategy);
			// hint garbage collector to do a collection
			// TODO: do this correctly
			System.gc();
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("The " + runStrategy.toString().toLowerCase() + " algorithm ran " + time / 5
				+ " milliseconds on average");
		System.out.println(" ");
	}

	/**
	 * reads csv file with data points and returns a list of these points
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static List<Point> parseCSVFile() throws NumberFormatException, IOException {
		// TODO: create list type interface
		// TODO: adapt for arbitrary number of variables

		// create CSVReader object
		CSVReader reader = new CSVReader(new FileReader(filePath), ',');

		List<Point> readPoints = new ArrayList<Point>();
		// read line by line
		String[] record = null;
		// skip header row
		reader.readNext();

		while ((record = reader.readNext()) != null) {
			Point readPoint = new Point(Double.parseDouble(record[0]), Double.parseDouble(record[1]));
			// System.out.println("Adding Point " readPoint.toString());
			readPoints.add(readPoint);
			// or: addPoint(Set<Point> list, Point) as interface method
		}

		reader.close();
		return readPoints;
	}

	/**
	 * Generates random data points and fills list with them
	 * 
	 * @param n
	 *            amount of points to be generated
	 */
	public static List<Point> createRandomPoints(int n) {
		Random r = new Random();
		List<Point> points = new ArrayList<Point>();
		for (int i = 0; i < n; i++) {
			points.add(new Point(r.nextDouble(), r.nextDouble()));
		}
		// printPoints();
		return points;
	}

	private static void printPoints() {
		for (Point p : points) {
			System.out.println(p.toString());
		}
	}
}