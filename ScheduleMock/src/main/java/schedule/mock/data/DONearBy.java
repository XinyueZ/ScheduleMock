package schedule.mock.data;


import com.google.gson.annotations.SerializedName;

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
