package schedule.mock.data;


import com.google.gson.annotations.SerializedName;

public final class DOLatLng {
	@SerializedName("lat")
	private double mLatitude;
	@SerializedName("lng")
	private double mLongitude;

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double _latitude) {
		mLatitude = _latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double _longitude) {
		mLongitude = _longitude;
	}
}
