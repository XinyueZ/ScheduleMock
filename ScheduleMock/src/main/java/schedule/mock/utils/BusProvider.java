package schedule.mock.utils;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


public final class BusProvider {

	private static volatile Bus sBus = new Bus(ThreadEnforcer.ANY);


	public static Bus getBus() {
		return sBus;
	}
}
