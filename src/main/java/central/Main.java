package central;

import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.opencsv.CSVReader;
import com.sun.xml.internal.bind.marshaller.Messages;

import util.ResultMessage;
import util.RunStrategy;

public class Main {

	// choice of data structure might be crucial
	static List<Point> points = new LinkedList<Point>();
	static List<ResultMessage> msgs = new ArrayList<ResultMessage>();

	// default values:

	// number of data points
	static int n = 10000;
	// number of clusters
	static int k = 10;
	// number of iterations
	static int iterations = 100;
	// number of benchmark runs per strategy
	static int runs = 1;
	// path to dataset
	static String filePath = "points_10000.csv";

	public static void main(String[] args)
			throws NumberFormatException, IOException, InterruptedException, ExecutionException {

		printIntro();

		readRunParameters(args);

		// parameter validity check
		if (n < 2 || k < 1 || iterations < 1 || k > n) {
			System.err.println("Invalid k-means parameters; follow these restrictions:");
			System.err.println("1 < n (number of data points) < k (number of clusters to be created)");
			System.err.println("iterations > 0");
			throw new InvalidParameterException();
		}

		final KMeans kmeans = new KMeans();

		// parsing a a data set or creating random points is possible
		// TODO: make it possible to choose
		points = parseCSVFile();

		// points = createRandomPoints(n);

		// make list of points immutable
		points = Collections.unmodifiableList(points);

		for (RunStrategy runStrategy : RunStrategy.values()) {
			benchmarkXRuns(runs, runStrategy);
		}

		// print times
		for (ResultMessage msg : msgs) {
			msg.print();
		}
	}

	private static void readRunParameters(String[] args) {
		if (args.length > 0) {
			filePath = args[0];
			if (args.length > 1) {
				k = Integer.parseInt(args[1]);
			}
			if (args.length > 2) {
				iterations = Integer.parseInt(args[2]);
			}
			if (args.length > 3) {
				runs = Integer.parseInt(args[3]);
			} else {
				System.err.println("Too many arguments");
			}
		}
	}

	/**
	 * prints intro message and gives information on run parameters
	 */
	private static void printIntro() {

		System.out.println("Welcome to this java-based k-means benchmark tool. ");
		System.out.println("If you don't provide any run arguments, the default benchmark with " + n + " data points, "
				+ k + " clusters, " + iterations + " iteration(s) and " + runs + " run(s) per strategy will be run.");
		System.out.println("");

		// Usage info
		// Information about benchmark that is being run

	}

	/**
	 * runs the specified algorithm strategy a provided number of times and
	 * prints the average running time
	 * 
	 * @param runs
	 *            number of times the algorithm should run
	 * @param runStrategy
	 *            strategy specifying the algorithm
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private static void benchmarkXRuns(int runs, RunStrategy runStrategy)
			throws InterruptedException, ExecutionException {
		System.out.println("Running the " + runStrategy.toString().toLowerCase() + " algorithm " + runs + " time(s)..");
		System.out.println(" ");
		long start = System.currentTimeMillis();
		for (int i = 0; i < runs; i++) {
			KMeans.run(points, k, iterations, runStrategy);
			// hint garbage collector to do a collection
			System.gc();
		}
		// simple form of time measurement
		long time = System.currentTimeMillis() - start;
		System.out.println(" ");
		System.out.println("The " + runStrategy.toString().toLowerCase() + " algorithm ran " + time / runs
				+ " milliseconds on average");
		msgs.add(new ResultMessage(runStrategy, time / runs));
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

	/**
	 * prints all data points to console
	 */
	private static void printPoints() {
		for (Point p : points) {
			System.out.println(p.toString());
		}
	}

	private static void printCurrentTime() {
		System.out.println("Current system time: " + System.currentTimeMillis());
	}
}
