package schedule.mock.events;


import android.location.Location;

public final class UIShowMapEvent {
	private Location mLocation;

	public UIShowMapEvent(Location _location) {
		mLocation = _location;
	}

	public Location getLocation() {
		return mLocation;
	}
}
