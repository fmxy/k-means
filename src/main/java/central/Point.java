package central;

/**
 * two-dimensional data point with positive
 * 
 * @param x
 *            first value
 * @param y
 *            second value
 */
public class Point {

	// 2D coordinates of data point
	double x;
	double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point plus(Point p2) {
		return new Point(x + p2.getX(), y + p2.getY());
	}

	public Point minus(Point p2) {
		return new Point(x - p2.getX(), y - p2.getY());
	}

	public Point div(double d) {
		return new Point(x / d, y / d);
	}

	public Double getModulus() {
		return Math.sqrt(sq(x) + sq(y));
	}

	private double sq(double x) {
		return x * x;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

}