package schedule.mock.data;

import com.google.gson.annotations.SerializedName;

public final class DONearByGeometry {
	@SerializedName("location")
	private DONearByLocation mNearByLocation;

	public DONearByLocation getNearByLocation() {
		return mNearByLocation;
	}

	public void setNearByLocation(DONearByLocation _nearByLocation) {
		mNearByLocation = _nearByLocation;
	}
}
