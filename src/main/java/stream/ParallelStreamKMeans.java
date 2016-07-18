package stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import central.Point;

/**
 * This implementation is a sequential java 8 stream-based k-means algorithm.
 * Adapted from the code by github.com/evacchi added to the public k-means
 * benchmark repository github.com/andreaferretti/kmeans on 27/02/15.
 */
public class ParallelStreamKMeans {

	public void run(List<Point> xs, int k, int iterations) {
		Stream<Point> centroids = xs.parallelStream().limit(k);
		for (int i = 1; i <= iterations; i++) {
			System.out.println("Iteration " + i + "/" + iterations);
			centroids = clusters(xs, centroids.collect(toList())).parallelStream().map(this::average);
		}
		List<Point> ps = centroids.collect(toList());
		clusters(xs, ps);
	}

	public Collection<List<Point>> clusters(List<Point> xs, List<Point> centroids) {
		return xs.parallelStream().collect(groupingBy((Point x) -> closest(x, centroids))).values();
	}

	public Point closest(final Point x, List<Point> choices) {
		return choices.parallelStream().collect(minBy((y1, y2) -> dist(x, y1) <= dist(x, y2) ? -1 : 1)).get();
	}

	public double dist(Point x, Point y) {
		return x.minus(y).getModulus();
	}

	public Point average(List<Point> xs) {
		return xs.parallelStream().reduce(Point::plus).get().div(xs.size());
	}

}