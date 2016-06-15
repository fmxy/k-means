package reducemap;

import java.util.concurrent.Callable;

import com.google.common.collect.Multimap;

public class MappingCallable implements Callable<Multimap> {

	private Multimap multimap;

	public MappingCallable(Multimap multimap) {
		this.multimap = multimap;
	}

	@Override
	public Multimap call() {
		// TODO: distance calculation, reassignment

		// System.out.println("Processing multimap..");
		// System.out.println("Calculating distance");

		return multimap;
	}

}