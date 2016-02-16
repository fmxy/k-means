package sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	static List <Point> points = new ArrayList<Point>();

	public static void main(String[] args) {

		KMeans kmeans = new KMeans();
		Random r = new Random();
		
		
		for(int i=0; i<100 ;i++){
		points.add(new Point(r.nextDouble(), r.nextDouble()));
		// printPoints();
		}
		kmeans.run(points, 2);
	}

	
	private static void printPoints(){
		for(Point p : points){
			System.out.println(p.toString());
		}
	}
}