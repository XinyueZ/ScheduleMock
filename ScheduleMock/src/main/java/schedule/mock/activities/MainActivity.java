package schedule.mock.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.squareup.otto.Subscribe;

import java.util.Locale;

import de.ankri.views.Switch;
import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DOGeocodeFromLatLng;
import schedule.mock.data.DOGeocodeResult;
import schedule.mock.data.DOLatLng;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.StartLocationMockTrackingEvent;
import schedule.mock.events.UIShowAfterFinishMockingEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.events.UIShowNetworkImageEvent;
import schedule.mock.events.UIShowOpenMockPermissionEvent;
import schedule.mock.fragments.HomeFragment;
import schedule.mock.fragments.ImageDialogFragment;
import schedule.mock.prefs.Prefs;
import schedule.mock.services.StartLocationTrackingService;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.DisplayUtil;
import schedule.mock.utils.Utils;
import schedule.mock.views.AnimiImageView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public final class MainActivity extends BaseActivity implements
		uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener, View.OnClickListener {

	public static final int LAYOUT = R.layout.activity_main;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private boolean mLocationInProcess;
	private ProgressDialog mProgressDialog;

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
		initActionBar();
	}

	private void initActionBar() {
		boolean currentMockStatus = Prefs.getInstance().getMockStatus();

		View customView = getSupportActionBar().getCustomView();
		Switch aSwitch = (Switch) customView.findViewById(R.id.switch_mock);
		aSwitch.setOnClickListener(this);
		/* Show switch to "off" mock status is possible. */
		if (currentMockStatus) {
			aSwitch.setChecked(true);
			aSwitch.setVisibility(View.VISIBLE);
		}
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
		switch (_item.getItemId()) {
		case R.id.menu_my_location:
			startLocationProcess(new Intent(getApplicationContext(), StartLocationTrackingService.class));
			return true;
		}
		return super.onOptionsItemSelected(_item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu _menu) {
		MenuItem tracking = _menu.findItem(R.id.menu_my_location);
		if( mLocationInProcess ) {
			tracking.setEnabled(false);
		} else {
			tracking.setEnabled(true);
		}
		return super.onPrepareOptionsMenu(_menu);
	}

	@Override
	public void onRefreshStarted(View _view) {
	}

	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
		case R.id.tv_current_location:
			/* Click on address on the actionbar. */
			if (_v.getTag() instanceof DOGeocodeResult) {
				openCurrentPositionInMap(_v);
			}
			break;
		case R.id.switch_mock:
			/* Click on switch to "off" mock status. */
			de.ankri.views.Switch aSwitch = (Switch) _v;
			if (!aSwitch.isChecked()) {
				/* Stop location system */
				stopLocationProcess();
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

	/**
	 * Has gotten the newest location from trackings-services.
	 * **/
	@Subscribe
	public void onServiceLocationChanged(ServiceLocationChangedEvent _e) {
		double lat = _e.getLocation().getLatitude();
		double lng = _e.getLocation().getLongitude();
		String url = String.format(App.API_GEOCODE_FROM_LAT_LNG, Utils.encodedKeywords(lat + "," + lng), Locale
				.getDefault().getLanguage());
		/*
		 * Translate from latlng to string.
		 */
		new GsonRequestTask<DOGeocodeFromLatLng>(getApplicationContext(), Request.Method.GET, url.trim(),
				DOGeocodeFromLatLng.class).execute();

		/*
		 * Stop tracking if it is not a mocked location, that means the normal
		 * tracking should be stopped when a location has been gotten.
		 */
		if (!_e.isMocked()) {
			stopLocationProcess();
		} else {
			onUIShowAfterStartMocking();

		}
	}

	/***
	 * Starting mocking, show switch and dismiss radar.
	 */
	private void onUIShowAfterStartMocking() {
		/*
		 * Switch to UI for mocking "ON"
		 * 
		 * Against to onUIShowAfterFinishMocking
		 */
		ActionBar actionBar = getSupportActionBar();
		View customView = actionBar.getCustomView();
		Switch aSwitch = (Switch) customView.findViewById(R.id.switch_mock);
		aSwitch.setVisibility(View.VISIBLE);
		aSwitch.setChecked(true);
		finish();
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

				/* Store current location to address text-view. */
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
	public void onStartLocationMockTracking(StartLocationMockTrackingEvent _e) {
		DOLatLng location = _e.getLocation();
		String name = _e.getName();
		if (location != null) {
			Intent intent = new Intent(getApplicationContext(), StartLocationTrackingService.class);
			intent.putExtra(StartLocationTrackingService.EXTRAS_MOCK_MODE, true);
			intent.putExtra(StartLocationTrackingService.EXTRAS_MOCK_LAT, location.getLatitude());
			intent.putExtra(StartLocationTrackingService.EXTRAS_MOCK_LNG, location.getLongitude());
			intent.putExtra(StartLocationTrackingService.EXTRAS_MOCK_NAME, name);
			startLocationProcess(intent);
		}
	}

	/***
	 * Start location tracking or mocking
	 * @param _intent
	 */
	private void startLocationProcess(Intent _intent) {
		startService(_intent);
		mLocationInProcess = true;
		mProgressDialog = ProgressDialog.show(this, null, getString(R.string.popup_my_location));
	}

	/***
	 * Stop location tracking or mocking
	 */
	private void stopLocationProcess() {
		stopService(new Intent(getApplicationContext(), StartLocationTrackingService.class));
		mLocationInProcess = false;
		if( mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Subscribe
	public void onUIShowAfterFinishMocking(UIShowAfterFinishMockingEvent _e) {
		View customView = getSupportActionBar().getCustomView();
		Switch aSwitch = (Switch) customView.findViewById(R.id.switch_mock);
		/*
		 * A bug on AnimiImageView, first set no listener then show, stop anim
		 * and connect callback again.
		 */
		aSwitch.setVisibility(View.GONE);
	}

	@Subscribe
	public void onShowOpenMockPermission(UIShowOpenMockPermissionEvent _e) {

	}
}
