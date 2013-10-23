package schedule.mock.fragments;

import android.support.v4.app.Fragment;

import schedule.mock.App;


public abstract class BaseFragment extends Fragment{
	@Override
	public void onResume() {
		super.onResume();
		App.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		App.getBus().unregister(this);
	}
}
