package schedule.mock.events;

import android.location.Location;

public final class UIShowGoogleMapEvent {
	private Location mLocation;

	public UIShowGoogleMapEvent() {
	}

	public UIShowGoogleMapEvent(Location _location) {
		mLocation = _location;
	}

	public Location getLocation() {
		return mLocation;
	}

	public void setLocation(Location _location) {
		mLocation = _location;
	}
}
