package schedule.mock.events;

public  final class TaskSuccessEvent<T> {
	private T mData;

	public TaskSuccessEvent(T _data) {
		mData = _data;
	}

	public T getData() {
		return mData;
	}
}
