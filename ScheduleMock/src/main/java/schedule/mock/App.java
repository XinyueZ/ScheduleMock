package schedule.mock;

import android.app.Application;

import schedule.mock.prefs.Prefs;
import schedule.mock.tasks.net.TaskHelper;


public final class App extends Application {

	public static final int MAIN_CONTAINER = R.id.container;
	public static final int COUNT_VOICE_RESULT = 1;
	public static final int COUNT_GET_ADDRESS = 1;
	private static final String API_KEY = "AIzaSyCw17hv5-w5PjkzBOXxTX_oS0325sCA2lQ";
	public static final String API_NEAR_BY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s,%s&radius=%s&language=%s&sensor=false&key=" + API_KEY;
	// public static final String API_PLACE_DETAILS =
	// "https://maps.googleapis.com/maps/api/place/details/json?reference=%s&language=%s&sensor=true&key="
	// + API_KEY;
	public static final String API_STATIC_MAP = "http://maps.google.com/maps/api/staticmap?center=%s,%s&size=%s&format=png&sensor=true&zoom=%s&maptype=roadmap&markers=color:blue|label:%s|%s,%s";
	public static final String API_GEOCODE_TO_LATLNG = "http://maps.google.com/maps/api/geocode/json?address=%s&sensor=false&language=%s";
	public static final String API_GEOCODE_TO_ADDRESS = "http://maps.google.com/maps/api/geocode/json?latlng=%s&sensor=false&language=%s";


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
