package schedule.mock;

import android.app.Application;

import schedule.mock.prefs.Prefs;
import schedule.mock.tasks.net.TaskHelper;


public final class App extends Application {

	public static final int MAIN_CONTAINER = R.id.container;
	public static final int COUNT_VOICE_RESULT = 1;
	public static final int COUNT_GET_ADDRESS = 1;
	public static final String API_NEAR_BY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%s&sensor=false&key=AIzaSyCw17hv5-w5PjkzBOXxTX_oS0325sCA2lQ";


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
