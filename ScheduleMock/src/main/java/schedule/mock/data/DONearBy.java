package schedule.mock.data;


import com.google.gson.annotations.SerializedName;

//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.5535442,10.0899821&radius=100&sensor=false&key=AIzaSyCw17hv5-w5PjkzBOXxTX_oS0325sCA2lQ
public final class DONearBy {
	@SerializedName("results")
	private DONearByResult[] mNearByResults;

	public DONearByResult[] getNearByResults() {
		return mNearByResults;
	}

	public void setNearByResults(DONearByResult[] _nearByResults) {
		mNearByResults = _nearByResults;
	}
}
