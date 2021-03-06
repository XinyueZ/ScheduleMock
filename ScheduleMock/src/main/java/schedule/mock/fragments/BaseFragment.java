package schedule.mock.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import schedule.mock.events.UIHighlightMenuItemEvent;
import schedule.mock.interfaces.ICanOpenByMenuItem;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;

public abstract class BaseFragment extends Fragment implements ICanOpenByMenuItem {

	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);

		BusProvider.getBus().register(this);
		BusProvider.getBus().post(new UIHighlightMenuItemEvent(getMenuItemPosition()));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getBus().unregister(this);
		TaskHelper.getRequestQueue().cancelAll(GsonRequestTask.TAG);
	}

}
