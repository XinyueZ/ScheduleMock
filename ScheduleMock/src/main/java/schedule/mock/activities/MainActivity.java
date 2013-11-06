package schedule.mock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.squareup.otto.Subscribe;

import java.util.Locale;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DOGeocodeFromLatLan;
import schedule.mock.data.DOGeocodeResult;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.StartLocationTrackingEvent;
import schedule.mock.events.StopLocationTrackingEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.fragments.HomeFragment;
import schedule.mock.services.StartLocationTrackingService;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.utils.LL;
import schedule.mock.utils.Utils;
import schedule.mock.views.AnimiImageView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;


public final class MainActivity extends BaseActivity implements
		uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener, View.OnClickListener {

	public static final int LAYOUT = R.layout.activity_main;
	private PullToRefreshAttacher mPullToRefreshAttacher;


	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(LAYOUT);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		setRefreshableView(this);
		if (_savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(App.MAIN_CONTAINER, HomeFragment.newInstance(getApplicationContext())).commit();
		}
		View customView = getSupportActionBar().getCustomView();
		View btnLocationTracking = customView.findViewById(R.id.btn_location_tracking);
		btnLocationTracking.setOnClickListener(this);
	}


	@Override
	protected void onDestroy() {
		mPullToRefreshAttacher = null;
		super.onDestroy();
	}


	@Subscribe
	public void onUIShowLoadingEvent(UIShowLoadingEvent _event) {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshing(true);
		}
	}


	@Subscribe
	public void onUIShowLoadingCompleteEvent(UIShowLoadingCompleteEvent _event) {
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.setRefreshComplete();
		}
	}


	public void setRefreshableView(
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener _refreshListener) {
		View view = findViewById(App.MAIN_CONTAINER);
		if (mPullToRefreshAttacher != null) {
			mPullToRefreshAttacher.addRefreshableView(view, _refreshListener);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, _menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem _item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (_item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(_item);
	}


	@Override
	public void onRefreshStarted(View _view) {
	}


	@Override
	public void onClick(View _v) {
		((AnimiImageView) _v).toggle();
	}


	@Subscribe
	public void onStartLocationTracking(StartLocationTrackingEvent _e) {
		startService(new Intent(getApplicationContext(), StartLocationTrackingService.class));
	}


	@Subscribe
	public void onStopLocationTracking(StopLocationTrackingEvent _e) {
		stopService(new Intent(getApplicationContext(), StartLocationTrackingService.class));
	}


	/**
	 * Has gotten the newest location from trackings-services.
	 * **/
	@Subscribe
	public void onServiceLocationChanged(ServiceLocationChangedEvent _e) {
		/*
		* Stop tracking current address.
		* */
		View customView = getSupportActionBar().getCustomView();
		AnimiImageView btnLocationTracking = (AnimiImageView) customView.findViewById(R.id.btn_location_tracking);
		btnLocationTracking.stopAnim();

		double lat = _e.getLocation().getLatitude();
		double lng = _e.getLocation().getLongitude();
		String url = String.format(App.API_GEOCODE_FROM_LAT_LAN, Utils.encodedKeywords(lat + "," + lng), Locale
				.getDefault().getLanguage());
		LL.d("Start geocode:" + url);
		/*
		* Translate from lanlat to string.
		* */
		new GsonRequestTask<DOGeocodeFromLatLan>(getApplicationContext(), Request.Method.GET, url.trim(),
				DOGeocodeFromLatLan.class).execute();
	}

	/**
	 * Get translated string of Geocode.
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGeocodeFromLatLan _geocode) {
		View customView = getSupportActionBar().getCustomView();
		TextView currentLoction = (TextView) customView.findViewById(R.id.tv_current_location);
		// FIXME There's a Check before the array being used.
		DOGeocodeResult[] results = _geocode.getGeocodeResults();
		DOGeocodeResult result = results[0];
		String fullAddress = result.getFullAddress();
		currentLoction.setText(fullAddress);
	}
}
