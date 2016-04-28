package central;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private Point centroid;
	private List<Point> points = new ArrayList<Point>();

	/**
	 * calculates cluster mean and assigns it to centroid variable
	 */
	public void updateCentroid() {
		double xsum = 0;
		double ysum = 0;

		for (Point p : points) {
			xsum += p.getX();
			ysum += p.getY();
		}

		centroid = new Point(xsum / points.size(), ysum / points.size());
	}

	public void setCentroid(Point p) {
		centroid = p;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void addPoint(Point p) {
		points.add(p);
	}

	public List<Point> getPoints() {
		return points;
	}

	public void clearPoints() {
		points.clear();
	}
}
