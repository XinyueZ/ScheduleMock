package schedule.mock.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import schedule.mock.events.CutMockingEvent;
import schedule.mock.events.FindMyLocationEvent;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.StartLocationMockTrackingEvent;
import schedule.mock.events.UICloseSidebarEvent;
import schedule.mock.events.UIShowAfterFinishMockingEvent;
import schedule.mock.events.UIShowCanNotMockLocationEvent;
import schedule.mock.events.UIShowGPlusEvent;
import schedule.mock.events.UIShowGoogleMapEvent;
import schedule.mock.events.UIShowHistoryEvent;
import schedule.mock.events.UIShowHomeEvent;
import schedule.mock.events.UIShowInputEvent;
import schedule.mock.events.UIShowLoadingCompleteEvent;
import schedule.mock.events.UIShowLoadingEvent;
import schedule.mock.events.UIShowMapEvent;
import schedule.mock.events.UIShowNetworkImageEvent;
import schedule.mock.events.UIShowOpenMockPermissionEvent;
import schedule.mock.events.UIShowScheduleEvent;
import schedule.mock.events.VoiceInputEvent;
import schedule.mock.fragments.GPlusFragment;
import schedule.mock.fragments.HistoryListFragment;
import schedule.mock.fragments.HomeFragment;
import schedule.mock.fragments.InputFragment;
import schedule.mock.fragments.MyMapFragment;
import schedule.mock.fragments.ScheduleFragment;
import schedule.mock.fragments.dialog.AskCuttingMockDialogFragment;
import schedule.mock.fragments.dialog.ImageDialogFragment;
import schedule.mock.prefs.Prefs;
import schedule.mock.services.StartLocationTrackingService;
import schedule.mock.tasks.db.InsertHistoryTask;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.DisplayUtil;
import schedule.mock.utils.Utils;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public final class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener,
		uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener, View.OnClickListener {

	public static final int LAYOUT = R.layout.activity_main;
	private static final int MAIN_CONTAINER = R.id.container;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private boolean mLocationInProcess;
	private ProgressDialog mProgressDialog;
	private boolean mCuttingMock;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(LAYOUT);
		initPull2LoadView();
		if (_savedInstanceState == null) {
			initHome();
		}
		changeSwitchStatus(Prefs.getInstance().getMockStatus());
		initSidebar();


	}

	@Override
	protected void onResume() {
		super.onResume();
		schedule.mock.enums.MenuItem selectedMenuItem = App.getSelectedMenuItem();
		if( selectedMenuItem != null) {
			BusProvider.getBus().post(selectedMenuItem.getOpenEvent());
		}
	}

	private void initPull2LoadView() {
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		setRefreshableView(this);
	}

	private void initSidebar() {
		DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.sidebar);
		sidebar.setDrawerListener(this);
		mDrawerToggle = new ActionBarDrawerToggle(this, sidebar, R.drawable.ic_drawer, -1, -1);
	}

	private void initHome() {
		getSupportFragmentManager().beginTransaction()
				.replace(MAIN_CONTAINER, HomeFragment.newInstance(getApplicationContext()), HomeFragment.TAG).commit();
	}

	@Subscribe
	public void onShowHome(UIShowHomeEvent _e) {
		getSupportFragmentManager().beginTransaction()
				.replace(MAIN_CONTAINER, HomeFragment.newInstance(getApplicationContext()), HomeFragment.TAG)
				 .commit();
	}

	@Override
	public void onDrawerOpened(View _drawerView) {
		mDrawerToggle.onDrawerOpened(_drawerView);
	}

	@Override
	public void onDrawerClosed(View _drawerView) {
		mDrawerToggle.onDrawerClosed(_drawerView);
		DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.sidebar);
		Object event = sidebar.getTag();
		if (event != null) {
			/*
			 * Open view that menuItem means. See MenuFragment.java for more
			 * "menu" stories.
			 */
			BusProvider.getBus().post(event);
			sidebar.setTag(null);
		}
	}

	@Override
	public void onDrawerSlide(View _drawerView, float slideOffset) {
		mDrawerToggle.onDrawerSlide(_drawerView, slideOffset);
	}

	@Override
	public void onDrawerStateChanged(int _newState) {
		mDrawerToggle.onDrawerStateChanged(_newState);
	}

	@Override
	protected void onPostCreate(Bundle _savedInstanceState) {
		super.onPostCreate(_savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration _newConfig) {
		super.onConfigurationChanged(_newConfig);
		mDrawerToggle.onConfigurationChanged(_newConfig);
	}

	/***
	 * The only place that show the status of "mocked location".
	 * 
	 * @param _status
	 *            "ON": location's been mocked.
	 */
	private void changeSwitchStatus(boolean _status) {
		View customView = getSupportActionBar().getCustomView();
		Switch aSwitch = (Switch) customView.findViewById(R.id.switch_mock);
		aSwitch.setOnClickListener(this);
		aSwitch.setChecked(_status);
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
		View view = findViewById(MAIN_CONTAINER);
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
		if (mDrawerToggle.onOptionsItemSelected(_item)) {
			return true;
		} else {
			switch (_item.getItemId()) {
			case R.id.menu_my_location:
				findMyLocation(null);
				return true;
			case R.id.menu_voice_input:
				if (findFragment(InputFragment.TAG)) {
					/* Do not open 2nd input-view. */
					BusProvider.getBus().post(new VoiceInputEvent());
				} else {
					/* Open input-view and start voice-input. */
					onOpenInput(new UIShowInputEvent(true));
				}
				return true;
			}
		}
		return super.onOptionsItemSelected(_item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu _menu) {
		MenuItem tracking = _menu.findItem(R.id.menu_my_location);

		/*
		 * At mock status, user can not change to real location. But there's
		 * still a fallback dialog to prevent switching. See.
		 * AskCuttingMockDialogFragment in findMyLocation().
		 */
		tracking.setEnabled(!(mLocationInProcess || Prefs.getInstance().getMockStatus()));

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
				mCuttingMock = true;
			}
			break;
		default:
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
		/*
		 * Stop tracking if it is not a mocked location, that means the normal
		 * tracking should be stopped when a location has been gotten, and
		 * translate latlng to a readable name.
		 */
		if (!_e.isMocked()) {
			double lat = _e.getLocation().getLatitude();
			double lng = _e.getLocation().getLongitude();
			String url = String.format(App.API_GEOCODE_FROM_LAT_LNG, Utils.encodedKeywords(lat + "," + lng), Locale
					.getDefault().getLanguage());
			/*
			 * Translate from latlng to string.
			 */
			new GsonRequestTask<DOGeocodeFromLatLng>(getApplicationContext(), Request.Method.GET, url.trim(),
					DOGeocodeFromLatLng.class).execute();
			stopLocationProcess();// no more loction now.
		} else {
			onUIShowAfterStartMocking(_e.getLocation());
		}
	}

	/***
	 * Starting mocking, show switch and dismiss radar.
	 * 
	 * @param _location
	 */
	private void onUIShowAfterStartMocking(Location _location) {
		/*
		 * Switch to UI for mocking "ON"
		 * 
		 * Against to onUIShowAfterFinishMocking
		 */
		ActionBar actionBar = getSupportActionBar();
		View customView = actionBar.getCustomView();
		Switch aSwitch = (Switch) customView.findViewById(R.id.switch_mock);
		aSwitch.setChecked(true);
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		// finish();
		onUIShowGoogleMap(new UIShowGoogleMapEvent(_location));
	}

	@Subscribe
	public void onUIShowGoogleMap(UIShowGoogleMapEvent _e) {
		onShowMap(new UIShowMapEvent(_e.getLocation()));
	}

	@Subscribe
	public void onShowMap(UIShowMapEvent _e) {
		Prefs prefs = Prefs.getInstance();
		Location location = _e.getLocation();
		/* In mocking status. */
		boolean isMocking = prefs.getMockStatus();
		if (isMocking) {
			location = new Location("mock");
			location.setLatitude(Double.valueOf(prefs.getMockLat()));
			location.setLongitude(Double.valueOf(prefs.getMockLng()));
		}
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_from_down_to_top_fast, R.anim.no, R.anim.no,
						R.anim.slide_out_from_top_to_down_fast)
				.replace(MAIN_CONTAINER,
						location != null ? MyMapFragment.newInstance(location, null) : MyMapFragment.newInstance(),
						MyMapFragment.TAG).commit();
	}

	/**
	 * Get translated string of Geocode from latlng.
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGeocodeFromLatLng _geocode) {
		if (_geocode != null) {
			View customView = getSupportActionBar().getCustomView();
			TextView currentLocation = (TextView) customView.findViewById(R.id.tv_current_location);
			DOGeocodeResult[] results = _geocode.getGeocodeResults();
			if (results != null && results.length > 0) {
				DOGeocodeResult result = results[0];
				String fullAddress = result.getFullAddress();
				currentLocation.setText(fullAddress);

				/* Store current location to address text-view. */
				currentLocation.setTag(result);
				currentLocation.setOnClickListener(this);
			}
		}
	}

	@Subscribe
	public void onShowNetworkImage(UIShowNetworkImageEvent _e) {
		ImageDialogFragment.showInstance(this, _e.getURL());
	}

	/***
	 * Only place to start mocking. Against to findMyLocation to find current
	 * location.
	 * 
	 * @param _e
	 */
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

			/* Insert history to DB. */
			new InsertHistoryTask(location.toString(), name).exec();
		}
	}

	/***
	 * Only place to find my current location. Against to
	 * onStartLocationMockTracking to mock location.
	 */
	@Subscribe
	public void findMyLocation(FindMyLocationEvent _e) {
		if (Prefs.getInstance().getMockStatus()) {
			/* In mock-status should stop mocking first. */
			AskCuttingMockDialogFragment.showInstance(this);
		} else {
			startLocationProcess(new Intent(getApplicationContext(), StartLocationTrackingService.class));
		}
	}

	/***
	 * Cut mocking, it could be fired when user reopen the App by clicking
	 * notification center and then try to find "my loction" @{findMyLocation} .
	 * 
	 * @param _e
	 */
	@Subscribe
	public void onCutMocking(CutMockingEvent _e) {
		stopLocationProcess();
		mCuttingMock = true;
	}

	/***
	 * Start location tracking or mocking
	 * 
	 * @param _intent
	 */
	private void startLocationProcess(Intent _intent) {
		startService(_intent);
		mLocationInProcess = true;
		mProgressDialog = ProgressDialog.show(this, null, getString(R.string.popup_my_location), true);
	}

	/***
	 * Stop location tracking or mocking
	 */
	private void stopLocationProcess() {
		stopService(new Intent(getApplicationContext(), StartLocationTrackingService.class));
		mLocationInProcess = false;
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Subscribe
	public void onOpenInput(UIShowInputEvent _e) {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_from_down_to_top_fast, R.anim.no, R.anim.no,
						R.anim.slide_out_from_top_to_down_fast)
				.replace(MAIN_CONTAINER, InputFragment.newInstance(this, _e.isVoiceInput()), InputFragment.TAG)
				 .commit();
	}

	/***
	 * Mocking is finished. See also in @link{MyMapFragment}.
	 * 
	 * @param _e
	 */
	@Subscribe
	public void onUIShowAfterFinishMocking(UIShowAfterFinishMockingEvent _e) {
		changeSwitchStatus(false);

		/* Stop mocking location for some reasons manually. */
		if (mCuttingMock) {
			mCuttingMock = false;
			findMyLocation(null);
		}
	}

	@Subscribe
	public void onCloseSidebar(UICloseSidebarEvent _e) {
		DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.sidebar);
		/*
		 * To close sidebar if a menuItem is clicked. The event that opens next
		 * view will be stored in TAG of view.
		 * 
		 * See onDrawerClosed for more INFO that handles to open a view.
		 */
		if (sidebar.isDrawerOpen(GravityCompat.START)) {
			sidebar.closeDrawers();
		} else {
			sidebar.openDrawer(GravityCompat.START);
		}
		sidebar.setTag(_e.getOpenEvent());
	}

	@Subscribe
	public void onUIShowCanNotMockLocation(UIShowCanNotMockLocationEvent _e) {
		changeSwitchStatus(false);
	}

	@Subscribe
	public void onUIShowHistory(UIShowHistoryEvent _e) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(MAIN_CONTAINER, HistoryListFragment.newInstance(getApplicationContext()),
						HistoryListFragment.TAG).commit();
	}

	@Subscribe
	public void onUIShowSchedule(UIShowScheduleEvent _e) {
		getSupportFragmentManager().beginTransaction()
				.replace(MAIN_CONTAINER, ScheduleFragment.newInstance(getApplicationContext()), ScheduleFragment.TAG)
				 .commit();
	}

	@Subscribe
	public void onUIShowGPlus(UIShowGPlusEvent _e) {
		getSupportFragmentManager().beginTransaction()
				.replace(MAIN_CONTAINER, GPlusFragment.newInstance(getApplicationContext()), GPlusFragment.TAG)
			 .commit();
	}

	@Subscribe
	public void onShowOpenMockPermission(UIShowOpenMockPermissionEvent _e) {

	}

}
