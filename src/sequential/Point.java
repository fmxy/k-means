package sequential;

public class Point {
	
	// 2D coordinates of data point
	double x;
	double y;
	
	public Point(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	public String toString(){
		return "(" + x + ", " + y + ")";
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}