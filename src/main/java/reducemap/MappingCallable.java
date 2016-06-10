package reducemap;

import java.util.concurrent.Callable;

import com.google.common.collect.Multimap;

public class MappingCallable implements Callable {

	private Multimap multimap;

	public MappingCallable(Multimap multimap) {
		this.multimap = multimap;
	}

	@Override
	public Multimap call() {
		// TODO: distance calculation, reassignment

		return multimap;
	}

}