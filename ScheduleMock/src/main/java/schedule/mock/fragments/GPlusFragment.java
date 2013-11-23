package schedule.mock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public final class GPlusFragment extends BaseFragment {
	public static final String TAG = GPlusFragment.class.getName();
	public static final int MENU_POSITION = 5;
	public static GPlusFragment newInstance(Context _context) {
		return (GPlusFragment) GPlusFragment.instantiate(_context, GPlusFragment.class.getName());
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