package schedule.mock.events;

public final class UICloseSidebarEvent {
	private Object mOpenEvent;

	public UICloseSidebarEvent(Object _openEvent    ) {
		mOpenEvent = _openEvent;
	}


	public Object getOpenEvent() {
		return mOpenEvent;
	}
}
