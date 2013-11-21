package schedule.mock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import schedule.mock.R;
import schedule.mock.events.UIShowGoogleMapEvent;
import schedule.mock.events.UIShowInputEvent;
import schedule.mock.utils.BusProvider;

public final class HomeFragment extends BaseFragment implements View.OnClickListener {
	public static final String TAG = HomeFragment.class.getName();
	public static final int LAYOUT = R.layout.fragment_main;
	public static final int MENU_POSITION = 0;

	public static HomeFragment newInstance(Context _context) {
		return (HomeFragment) HomeFragment.instantiate(_context, HomeFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		_view.findViewById(R.id.btn_mock_one_time).setOnClickListener(this);
		_view.findViewById(R.id.btn_schedule).setOnClickListener(this);
		_view.findViewById(R.id.btn_map).setOnClickListener(this);
	}

	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
		case R.id.btn_mock_one_time:
			BusProvider.getBus().post(new UIShowInputEvent());
			break;
		case R.id.btn_schedule:
			break;
		case R.id.btn_map:
			BusProvider.getBus().post(new UIShowGoogleMapEvent());
			break;
		}
	}

	@Override
	public int getMenuItemPosition() {
		return MENU_POSITION;
	}
}
