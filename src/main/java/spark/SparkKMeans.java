package spark;

import java.util.Vector;

import central.KMeans;

public class SparkKMeans {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("K-means Example");
		JavaSparkContext sc = new JavaSparkContext(conf);

		// Load and parse data
		String path = "data/mllib/kmeans_data.txt";
		JavaRDD<String> data = sc.textFile(path);
		JavaRDD<Vector> parsedData = data.map(new Function<String, Vector>() {
			public Vector call(String s) {
				String[] sarray = s.split(" ");
				double[] values = new double[sarray.length];
				for (int i = 0; i < sarray.length; i++)
					values[i] = Double.parseDouble(sarray[i]);
				return Vectors.dense(values);
			}
		});
		parsedData.cache();

		// Cluster the data into two classes using KMeans
		int numClusters = 2;
		int numIterations = 20;
		KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);

		// Evaluate clustering by computing Within Set Sum of Squared Errors
		double WSSSE = clusters.computeCost(parsedData.rdd());
		System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

		// Save and load model
		clusters.save(sc.sc(), "myModelPath");
		KMeansModel sameModel = KMeansModel.load(sc.sc(), "myModelPath");
	}
}