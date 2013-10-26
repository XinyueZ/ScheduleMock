package schedule.mock.fragments;

import android.support.v4.app.Fragment;

import schedule.mock.tasks.net.GsonRequest;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;


public abstract class BaseFragment extends Fragment{
	@Override
	public void onResume() {
		super.onResume();
		BusProvider.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		BusProvider.getBus().unregister(this);

		TaskHelper.getRequestQueue().cancelAll(GsonRequest.TAG);
	}
}
