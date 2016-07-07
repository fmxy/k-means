package stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import central.Point;

/**
 * Created by evacchi on 27/02/15.
 */
public class StreamKMeans {

	int n = 10;
	int iters = 15;

	public void run(List<Point> xs) {
		Stream<Point> centroids = xs.stream().limit(n);
		for (int i = 0; i < iters; i++) {
			centroids = clusters(xs, centroids.collect(toList())).stream().map(this::average);
		}
		List<Point> ps = centroids.collect(toList());
		clusters(xs, ps);
	}

	public Collection<List<Point>> clusters(List<Point> xs, List<Point> centroids) {
		return xs.stream().collect(groupingBy((Point x) -> closest(x, centroids))).values();
	}

	public Point closest(final Point x, List<Point> choices) {
		return choices.stream().collect(minBy((y1, y2) -> dist(x, y1) <= dist(x, y2) ? -1 : 1)).get();
	}

	public double dist(Point x, Point y) {
		return x.minus(y).getModulus();
	}

	public Point average(List<Point> xs) {
		return xs.stream().reduce(Point::plus).get().div(xs.size());
	}

}