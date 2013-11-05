package schedule.mock.data;

import com.google.gson.annotations.SerializedName;

public final class DOGeometry {
	@SerializedName("location")
	private DOLatLng mNearByLocation;

	public DOLatLng getNearByLocation() {
		return mNearByLocation;
	}

	public void setNearByLocation(DOLatLng _nearByLocation) {
		mNearByLocation = _nearByLocation;
	}
}
