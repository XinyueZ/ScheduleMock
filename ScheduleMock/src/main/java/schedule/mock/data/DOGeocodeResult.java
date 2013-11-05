package schedule.mock.data;

import com.google.gson.annotations.SerializedName;


public final class DOGeocodeResult {

	@SerializedName("geometry")
	private DOGeometry mGeometry;
	@SerializedName("formatted_address")
	private String mFullAddress;


	public String getFullAddress() {
		return mFullAddress;
	}


	public void setFullAddress(String _fullAddress) {
		mFullAddress = _fullAddress;
	}


	public DOGeometry getGeometry() {
		return mGeometry;
	}


	public void setGeometry(DOGeometry _geometry) {
		mGeometry = _geometry;
	}
}
