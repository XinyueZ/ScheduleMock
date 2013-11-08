package schedule.mock.events;

import android.location.Location;

/***
 * This class is an event that the app set a mock location.
 */
public final class SetMockLocationEvent {
	private Location mLocation;

	public SetMockLocationEvent(Location _location) {
		mLocation = _location;
	}

	public Location getLocation() {
		return mLocation;
	}
}
