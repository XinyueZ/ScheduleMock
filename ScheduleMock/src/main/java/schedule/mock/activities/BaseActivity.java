package schedule.mock.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.OnResumeUtil;

public abstract class BaseActivity extends ActionBarActivity {

	@Override
	protected void onPause() {
		super.onPause();

		TaskHelper.getRequestQueue().cancelAll(GsonRequestTask.TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		OnResumeUtil.onResume(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showOverflowAlways();
		BusProvider.getBus().register(this);
	}

	@Override
	protected void onDestroy() {
		BusProvider.getBus().unregister(this);
		super.onDestroy();
	}

	private void showOverflowAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	protected boolean findFragment(String _tag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		return fragmentManager.findFragmentByTag(_tag) != null;
	}

}
