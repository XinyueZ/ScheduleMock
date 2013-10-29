package schedule.mock.events;

import android.location.Location;

public final class ServiceLocationChangedEvent {
	private Location mLocation;

	public ServiceLocationChangedEvent(Location _location) {
		mLocation = _location;
	}

	public Location getLocation() {
		return mLocation;
	}
}
