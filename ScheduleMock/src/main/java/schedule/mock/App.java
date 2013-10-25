package schedule.mock;

import android.app.Application;

import schedule.mock.prefs.Prefs;
import schedule.mock.utils.net.TaskHelper;


public final class App extends Application {

	public static final int MAIN_CONTAINER = R.id.container;
	public static final int COUNT_VOICE_RESULT = 1;
	public static final int COUNT_GET_ADDRESS = 3;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}


	private void init() {
		Prefs.createInstance(getApplicationContext());
		TaskHelper.init(getApplicationContext());
	}

}
