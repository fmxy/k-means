package util;

/**
 * Utility class for storing a message of the time measurement that can be
 * printed at any time.
 *
 */
public class ResultMessage {

	private RunStrategy runStrategy;
	private long time;

	public ResultMessage(RunStrategy runStrategy, long time) {
		this.runStrategy = runStrategy;
		this.time = time;
	}

	public void print() {
		System.out.println(runStrategy.toString() + ": " + time + " ms");
	}
}
