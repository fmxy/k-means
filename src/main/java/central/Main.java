package central;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.opencsv.CSVParser;

import util.RunStrategy;

public class Main {

	// choice of data structure might be crucial
	static List<Point> points = new LinkedList<Point>();

	// number of data points
	static int n = 10000;
	static int k = 10;
	static int iterations = 100;
	static String filePath = "points.csv";

	public static void main(String[] args) throws FileNotFoundException {

		KMeans kmeans = new KMeans();

		// parsing

		parseCSVFile();

		createRandomPoints(n);

		// make list of points immutable
		points = Collections.unmodifiableList(points);

		// very simple form of time measurement (doesn't measure initialization
		// phase, as well)
		// beware of JVM cashing, garbage collection

		// sequential
		long start_s = System.currentTimeMillis();
		kmeans.run(points, k, iterations, RunStrategy.SEQUENTIAL);
		long time_s = System.currentTimeMillis() - start_s;

		System.out.println("The algorithm ran " + time_s + " milliseconds");

	}

	// TODO: change to use CSVParser from opencsv or apache commons library
	// TODO: store points in data structure
	private static void parseCSVFile() throws FileNotFoundException {

		// create opencsv CSVParser with comma separator
		CSVParser parser = new CSVParser(',');

		Scanner scanner = new Scanner(new File(filePath));

		scanner.useDelimiter(",");

		while (scanner.hasNext()) {
			System.out.println(scanner.next());

			// TODO: interface

			/*
			 * store data in list parse for line breaks in order to recognize
			 * amount of variables would probably be easier with library
			 * CSVParser depends on
			 */
		}

		scanner.close();
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