package schedule.mock.data;

import com.google.gson.annotations.SerializedName;

public final class DOGeometry {
	@SerializedName("location")
	private DOLatLng mLocation;

	public DOLatLng getLocation() {
		return mLocation;
	}

	public void setLocation(DOLatLng _location) {
		mLocation = _location;
	}
}
