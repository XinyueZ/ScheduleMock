package schedule.mock.data;

public final class DOPlace {

	private String mName;
	private double mLatitude;
	private double mLongitude;


	public DOPlace(String _name, double _latitude, double _longitude) {
		mName = _name;
		mLatitude = _latitude;
		mLongitude = _longitude;
	}


	public String getName() {
		return mName;
	}


	public double getLatitude() {
		return mLatitude;
	}


	public double getLongitude() {
		return mLongitude;
	}


	@Override
	public String toString() {
		return new StringBuilder().append(mName).append(" ").append(mLatitude).append(',').append(mLongitude)
				.toString();
	}
}
