package schedule.mock.data;

import com.google.gson.annotations.SerializedName;


public final class DONearByResult {

	@SerializedName("geometry")
	private DOGeometry mGeometry;
	@SerializedName("icon")
	private String mIcon;
	@SerializedName("id")
	private String mId;
	@SerializedName("name")
	private String mName;
	@SerializedName("reference")
	private String mReference;
	@SerializedName("vicinity")
	private String mVicinity;


	public DOGeometry getGeometry() {
		return mGeometry;
	}


	public void setGeometry(DOGeometry _geometry) {
		mGeometry = _geometry;
	}


	public String getIcon() {
		return mIcon;
	}


	public void setIcon(String _icon) {
		mIcon = _icon;
	}


	public String getId() {
		return mId;
	}


	public void setId(String _id) {
		mId = _id;
	}


	public String getName() {
		return mName;
	}


	public void setName(String _name) {
		mName = _name;
	}


	public String getReference() {
		return mReference;
	}


	public void setReference(String _reference) {
		mReference = _reference;
	}


	public String getVicinity() {
		return mVicinity;
	}


	public void setVicinity(String _vicinity) {
		mVicinity = _vicinity;
	}
}
