package schedule.mock.activities;

import android.support.v7.app.ActionBarActivity;

import schedule.mock.App;

public abstract class BaseActivity extends ActionBarActivity  {
	@Override
	protected void onResume() {
		super.onResume();
		App.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.getBus().unregister(this);
	}
}
