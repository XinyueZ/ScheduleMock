package schedule.mock.activities;

import android.support.v7.app.ActionBarActivity;

import schedule.mock.tasks.net.GsonRequest;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;

public abstract class BaseActivity extends ActionBarActivity  {
	@Override
	protected void onResume() {
		super.onResume();
		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		BusProvider.getBus().unregister(this);

		TaskHelper.getRequestQueue().cancelAll(GsonRequest.TAG);
	}
}
