package reducemap;

import java.util.List;

import central.Point;

public class MappingRunnable implements Runnable {

	private List<Point> points;

	public MappingRunnable(List<Point> sublist) {
		points = sublist;
	}

	@Override
	public void run() {
		// TODO process points; rethink data structure
		// distance calculation, reassignment
		// return list -> Callable
	}

}
