package schedule.mock.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import schedule.mock.tasks.net.GsonRequest;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;


public abstract class BaseFragment extends Fragment{
	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState)  {
		super. onViewCreated(  _view,   _savedInstanceState);
		BusProvider.getBus().register(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getBus().unregister(this);
		TaskHelper.getRequestQueue().cancelAll(GsonRequest.TAG);
	}
}
