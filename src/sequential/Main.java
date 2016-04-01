package sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
	static List <Point> points = new ArrayList<Point>();

	public static void main(String[] args) {

		KMeans sequentialKMeans = new KMeans();
		ParallelKMeans parallelKMeans = new ParallelKMeans();
		
		Random r = new Random();
		
		
		for(int i=0; i<100 ;i++){
		points.add(new Point(r.nextDouble(), r.nextDouble()));
		// printPoints();
		}
		
		// very simple form of time measurement (doesn't measure initialization phase, as well)
		
		// sequential
		long start_s = System.currentTimeMillis();
		sequentialKMeans.run(points, 8);
		long time_s = System.currentTimeMillis() - start_s;
		
		
		// parallel (thread-based)
		long start_p = System.currentTimeMillis();
		parallelKMeans.run(points, 8);
		long time_p = System.currentTimeMillis() - start_p;
		
		System.out.println("The sequential algorithm ran " + time_s + " milli seconds");
		System.out.println("The parallel algorithm ran " + time_p + " milli seconds");
	}

	
	private static void printPoints(){
		for(Point p : points){
			System.out.println(p.toString());
		}
	}
}