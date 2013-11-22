package schedule.mock.events;

public final class UIShowInputEvent {
	private boolean mVoiceInput;

	public UIShowInputEvent() {
	}

	public UIShowInputEvent(boolean _voiceInput) {
		mVoiceInput = _voiceInput;
	}

	public boolean isVoiceInput() {
		return mVoiceInput;
	}
}
