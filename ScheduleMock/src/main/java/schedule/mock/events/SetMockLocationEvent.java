package schedule.mock.events;

import schedule.mock.data.DOLatLng;

/***
 * This class is an event that the app set a mock location.
 */
public final class SetMockLocationEvent {
	private DOLatLng mLocation;

	public SetMockLocationEvent(DOLatLng _location) {
		mLocation = _location;
	}

	public DOLatLng getLocation() {
		return mLocation;
	}
}
