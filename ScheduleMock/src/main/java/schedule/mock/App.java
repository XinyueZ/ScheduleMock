package schedule.mock;

import android.app.Application;

import schedule.mock.prefs.Prefs;


public final class App extends Application {


	public static final int MAIN_CONTAINER = R.id.container;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		Prefs.createInstance(getApplicationContext());
	}

}
