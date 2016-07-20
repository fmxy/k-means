package reducemap;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.collect.Multimap;

import central.KMeans;
import central.Point;

public class MappingCallable implements Callable<Multimap> {

	private Multimap<Integer, Point> multimap;
	private List<Point> centroids;

	public MappingCallable(Multimap multimap, List<Point> centroids) {
		this.multimap = multimap;
		this.centroids = centroids;
	}

	@Override
	public Multimap call() {

		int i = 1;
		Point[] copy = multimap.get(i).toArray(new Point[0]);

		for (Point p : copy) {
			int clusterNumber = 0;
			int index = 1;

			for (Point c : centroids) {

				// clear
				multimap.remove(i, p);

				// calc distance and reassign

				double savedDistance = Double.MAX_VALUE;

				// ugly

				double distance = KMeans.calculateDistance(p, c);
				// System.out.println("distance is: " + distance);
				if (distance <= savedDistance) {
					clusterNumber = index;
					savedDistance = distance;
				}
				index++;
			}
			// assign point to cluster
			multimap.put(clusterNumber, p);
			i++;

		}
		return multimap;
	}

}