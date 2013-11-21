package schedule.mock.events;

public final class UIHighlightMenuItemEvent {
	private int mPosition;

	public UIHighlightMenuItemEvent(int _position) {

		mPosition = _position;
	}

	public int getPosition() {
		return mPosition;
	}
}
