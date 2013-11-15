package schedule.mock.events;

import android.location.Location;

public final class ServiceLocationChangedEvent {
	private boolean mMocked;
	private Location mLocation;

	public ServiceLocationChangedEvent(boolean _mocked, Location _location) {

		mMocked = _mocked;
		mLocation = _location;
	}

	public boolean isMocked() {
		return mMocked;
	}

	public Location getLocation() {
		return mLocation;
	}
}
