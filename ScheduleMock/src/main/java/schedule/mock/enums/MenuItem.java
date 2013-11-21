package schedule.mock.enums;

import schedule.mock.R;
import schedule.mock.events.UIShowGPlusEvent;
import schedule.mock.events.UIShowGoogleMapEvent;
import schedule.mock.events.UIShowHistoryEvent;
import schedule.mock.events.UIShowInputEvent;
import schedule.mock.events.UIShowScheduleEvent;

public enum MenuItem {
	INPUT(R.string.menu_input, new UIShowInputEvent()), MAP(R.string.menu_map, new UIShowGoogleMapEvent()), HISTORY(
			R.string.menu_history, new UIShowHistoryEvent()), SCHEDULE(R.string.menu_schedule,
			new UIShowScheduleEvent()), G_PLUS(R.string.menu_gplus, new UIShowGPlusEvent());
	private int mNameResId;
	private Object mOpenEvent;

	public boolean isEnable() {
		return mEnable;
	}

	private boolean mEnable = true;

	MenuItem(int _nameResId, Object _openEvent) {
		mNameResId = _nameResId;
		mOpenEvent = _openEvent;
	}

	public void setEnable(boolean _enable) {
		mEnable = _enable;
	}

	public int getNameResId() {
		return mNameResId;
	}

	public Object getOpenEvent() {
		return mOpenEvent;
	}
}
