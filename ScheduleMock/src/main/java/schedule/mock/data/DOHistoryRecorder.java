package schedule.mock.data;

public final class DOHistoryRecorder {
	private String mLatLng;
	private String mName;
	private long mCreateTime;

	public DOHistoryRecorder(String _latLng, String _name, long _createTime) {
		mLatLng = _latLng;
		mName = _name;
		mCreateTime = _createTime;
	}

	public String getLatLng() {
		return mLatLng;
	}

	public String getName() {
		return mName;
	}

	public long getCreateTime() {
		return mCreateTime;
	}
}
