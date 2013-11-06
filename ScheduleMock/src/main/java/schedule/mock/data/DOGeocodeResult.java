package schedule.mock.data;

import com.google.gson.annotations.SerializedName;


public final class DOGeocodeResult {

	@SerializedName("address_components")
	private DOAddressComponent[] mDOAddressComponents;
	@SerializedName("geometry")
	private DOGeometry mGeometry;
	@SerializedName("formatted_address")
	private String mFullAddress;


	public DOAddressComponent[] getDOAddressComponents() {
		return mDOAddressComponents;
	}


	public void setDOAddressComponents(DOAddressComponent[] _DOAddressComponents) {
		mDOAddressComponents = _DOAddressComponents;
	}


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
