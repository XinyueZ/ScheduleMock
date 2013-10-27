package schedule.mock.events;

public final class UIShowNetworkImageEvent {

	private String mURL;


	public UIShowNetworkImageEvent(String _URL) {
		mURL = _URL;
	}


	public String getURL() {
		return mURL;
	}
}
