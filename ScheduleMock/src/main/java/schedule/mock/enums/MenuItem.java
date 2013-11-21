package schedule.mock.enums;

import schedule.mock.R;
import schedule.mock.events.UIShowGPlusEvent;
import schedule.mock.events.UIShowGoogleMapEvent;
import schedule.mock.events.UIShowHistoryEvent;
import schedule.mock.events.UIShowHomeEvent;
import schedule.mock.events.UIShowInputEvent;
import schedule.mock.events.UIShowScheduleEvent;

public enum MenuItem {
	HOME(R.string.menu_home, R.drawable.ic_menu_home, new UIShowHomeEvent()), INPUT(R.string.menu_input,
			R.drawable.ic_menu_input, new UIShowInputEvent()), MAP(R.string.menu_map, R.drawable.ic_menu_map,
			new UIShowGoogleMapEvent()), HISTORY(R.string.menu_history, R.drawable.ic_menu_history,
			new UIShowHistoryEvent()), SCHEDULE(R.string.menu_schedule, R.drawable.ic_menu_schedule,
			new UIShowScheduleEvent()), G_PLUS(R.string.menu_gplus, R.drawable.ic_menu_gplus, new UIShowGPlusEvent());
	private int mNameResId;
	private int mIconResId;
	private Object mOpenEvent;
	private boolean mEnable = true;

	MenuItem(int _nameResId, int _iconResId, Object _openEvent) {
		mNameResId = _nameResId;
		mIconResId = _iconResId;
		mOpenEvent = _openEvent;
	}

	public int getIconResId() {
		return mIconResId;
	}

	public boolean isEnable() {
		return mEnable;
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
