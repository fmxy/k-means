package reducemap;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.collect.Multimap;

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

		for (int i = 0; i < centroids.size(); i++) {
			int j = 1;

			for (Point p : multimap.get(j)) {

				// calc distance and reassign

				j++;
			}

		}
		return multimap;
	}

}