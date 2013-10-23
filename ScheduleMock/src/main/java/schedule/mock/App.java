package schedule.mock;

import android.app.Application;

import com.squareup.otto.Bus;

import schedule.mock.prefs.Prefs;


public final class App extends Application {

	private static final Bus BUS = new Bus();
	public static final int MAIN_CONTAINER = R.id.container;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		Prefs.createInstance(getApplicationContext());
	}

	public static Bus getBus() {
		return BUS;
	}
}
