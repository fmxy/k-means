# k-means
This repository holds a command line tool for comparing JVM-based parallelizations of the k-means clustering algorithm.

### Command line argument usage:

gradle run -PRunArgs="['pathToDataPoints','k','iterations','runsPerStrategy','strategy']"

The arguments in the above order translate to the following parameters:

1.  file path to .csv file containing the two-dimensional data points
2.  number of clusters
3.  number of iterations
4.  number of runs per strategy
5.  strategy to be run, either 's' for sequential, 'str' for stream, 'pstr' for parallel
stream, 'p1' for naive parallel, 'p2' for optimized parallel version

If there are no arguments given, all strategies will be run once with 10000 data points and 100 iterations. 
