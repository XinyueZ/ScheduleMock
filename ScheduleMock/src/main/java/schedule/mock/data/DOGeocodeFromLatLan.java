package schedule.mock.data;


import com.google.gson.annotations.SerializedName;

public final class DOGeocodeFromLatLan{

	@SerializedName("results")
	private DOGeocodeResult[] mGeocodeResults;
	@SerializedName("status")
	private String mStatus;


	public DOGeocodeResult[] getGeocodeResults() {
		return mGeocodeResults;
	}


	public void setGeocodeResults(DOGeocodeResult[] _geocodeResults) {
		mGeocodeResults = _geocodeResults;
	}


	public String getStatus() {
		return mStatus;
	}


	public void setStatus(String _status) {
		mStatus = _status;
	}
}
