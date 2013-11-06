package schedule.mock.data;


import com.google.gson.annotations.SerializedName;

public final class DOAddressComponent {
	@SerializedName("long_name")
	private String mLongName;
	@SerializedName("short_name")
	private String mShortName;
	@SerializedName("types")
	private String[] mTypes;

	public String getLongName() {
		return mLongName;
	}

	public void setLongName(String _longName) {
		mLongName = _longName;
	}

	public String getShortName() {
		return mShortName;
	}

	public void setShortName(String _shortName) {
		mShortName = _shortName;
	}

	public String[] getTypes() {
		return mTypes;
	}

	public void setTypes(String[] _types) {
		mTypes = _types;
	}
}
