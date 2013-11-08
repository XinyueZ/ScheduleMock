package schedule.mock.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.squareup.otto.Subscribe;

import java.util.Locale;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DOGeocodeFromLatLng;
import schedule.mock.data.DOGeocodeResult;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.SetMockLocationEvent;
import schedule.mock.events.StartLocationTrackingEvent;
import schedule.mock.events.StopLocationTrackingEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.events.UIShowNetworkImageEvent;
import schedule.mock.fragments.HomeFragment;
import schedule.mock.fragments.ImageDialogFragment;
import schedule.mock.services.StartLocationTrackingService;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.DisplayUtil;
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
		if (mPullToRefreshAttacher != null && !_event.getClassType().equals(DOGeocodeFromLatLng.class)) {
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
		switch (_v.getId()) {
		case R.id.tv_current_location:
			if (_v.getTag() instanceof DOGeocodeResult) {
				openCurrentPositionInMap(_v);
			}
			break;
		default:
			((AnimiImageView) _v).toggle();
			break;
		}
	}

	/**
	 * Clicking on the address of actionbar and open the static to show position
	 * of user.
	 * 
	 * @param _v
	 */
	private void openCurrentPositionInMap(View _v) {
		DOGeocodeResult latLng = (DOGeocodeResult) _v.getTag();
		DisplayMetrics displayMetrics = DisplayUtil.getDisplayMetrics(getApplicationContext());
		BusProvider.getBus().post(
				new UIShowNetworkImageEvent(String.format(App.API_STATIC_MAP, latLng.getGeometry().getLocation()
						.getLatitude(), latLng.getGeometry().getLocation().getLongitude(), displayMetrics.widthPixels
						+ "x" + displayMetrics.heightPixels, "17", "A", latLng.getGeometry().getLocation()
						.getLatitude(), latLng.getGeometry().getLocation().getLongitude())));
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
		double lat = _e.getLocation().getLatitude();
		double lng = _e.getLocation().getLongitude();
		String url = String.format(App.API_GEOCODE_FROM_LAT_LNG, Utils.encodedKeywords(lat + "," + lng), Locale
				.getDefault().getLanguage());
		LL.d("Start geocode:" + url);
		/*
		 * Translate from latlng to string.
		 */
		new GsonRequestTask<DOGeocodeFromLatLng>(getApplicationContext(), Request.Method.GET, url.trim(),
				DOGeocodeFromLatLng.class).execute();
	}

	/**
	 * Get translated string of Geocode from latlng.
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGeocodeFromLatLng _geocode) {
		if (_geocode != null) {
			View customView = getSupportActionBar().getCustomView();
			TextView currentLoction = (TextView) customView.findViewById(R.id.tv_current_location);
			DOGeocodeResult[] results = _geocode.getGeocodeResults();
			if (results != null && results.length > 0) {
				DOGeocodeResult result = results[0];
				String fullAddress = result.getFullAddress();
				currentLoction.setText(fullAddress);
				/*
				 * Stop tracking current address.
				 */
				AnimiImageView btnLocationTracking = (AnimiImageView) customView
						.findViewById(R.id.btn_location_tracking);
				btnLocationTracking.stopAnim();

				currentLoction.setTag(result);
				currentLoction.setOnClickListener(this);
			}
		}
	}

	@Subscribe
	public void onShowNetworkImage(UIShowNetworkImageEvent _e) {
		ImageDialogFragment.showInstance(this, _e.getURL());
	}

	@Subscribe
	public void onSetMockLocation(SetMockLocationEvent _e) {
		Location location = _e.getLocation();
		if (location != null) {

		}
	}
}
