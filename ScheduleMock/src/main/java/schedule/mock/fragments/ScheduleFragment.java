package schedule.mock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public final class ScheduleFragment extends BaseFragment {
	public static final String TAG = ScheduleFragment.class.getName();
	public static final int MENU_POSITION = 4;
	public static ScheduleFragment newInstance(Context _context) {
		return (ScheduleFragment) ScheduleFragment.instantiate(_context, ScheduleFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}

	@Override
	public int getMenuItemPosition() {
		return MENU_POSITION;
	}
}