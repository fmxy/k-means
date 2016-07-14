package util;

/**
 * prints the
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
