package schedule.mock.events;

import schedule.mock.data.DOLatLng;

/***
 * This class is an event that the app set a mock location.
 */
public final class StartLocationMockTrackingEvent {
	private String mName;
	private DOLatLng mLocation;

	public StartLocationMockTrackingEvent(String _name, DOLatLng _location) {
		mName = _name;
		mLocation = _location;
	}

	public String getName() {
		return mName;
	}

	public DOLatLng getLocation() {
		return mLocation;
	}
}
