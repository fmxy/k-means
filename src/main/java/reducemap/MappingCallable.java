package reducemap;

import java.util.concurrent.Callable;

import com.google.common.collect.Multiset;

public class MappingCallable implements Callable {

	private Multiset hashMultiset;

	public MappingCallable(Multiset hashMultiset) {
		this.hashMultiset = hashMultiset;
	}

	@Override
	public Multiset call() {
		// TODO: distance calculation, reassignment

		return hashMultiset;
	}

}
