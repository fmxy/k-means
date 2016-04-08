package forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import sequential.Cluster;
import sequential.KMeans;
import sequential.Point;

public class ForkJoinKMeans extends KMeans {

	private List<Cluster> clusters = new ArrayList<Cluster>();
	private static final ForkJoinPool fjp = new ForkJoinPool();

	public void run(List<Point> points, int k) {
		// create RecursiveTasks to do distance calculation
	}

}
