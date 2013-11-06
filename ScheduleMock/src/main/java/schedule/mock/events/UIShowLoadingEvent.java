package schedule.mock.events;


public final class UIShowLoadingEvent {
	Class<?> mClass;

	public UIShowLoadingEvent(Class<?> _class) {
		mClass = _class;
	}

	public Class<?> getClassType() {
		return mClass;
	}
}
