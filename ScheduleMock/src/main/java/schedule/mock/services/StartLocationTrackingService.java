package schedule.mock.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import schedule.mock.R;
import schedule.mock.activities.MainActivity;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.UIShowAfterFinishMockingEvent;
import schedule.mock.events.UIShowCanNotMockLocationEvent;
import schedule.mock.prefs.Prefs;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.LL;
import schedule.mock.utils.Utils;

public final class StartLocationTrackingService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {
	public static final String EXTRAS_MOCK_MODE = "extras_mock_mode";
	public static final String EXTRAS_MOCK_LAT = "extras_mock_lat";
	public static final String EXTRAS_MOCK_LNG = "extras_mock_lng";
	public static final String EXTRAS_MOCK_NAME = "extras_mock_name";
	public static final int ID_TRAY_NOTIFICATION = 0x999990;
	// Conversion factor for time values
	private static final int[] LOCK = { 1 };
	/* Some values for mock location */
	// Conversion factor for boot time
	private static final long NANOSECONDS_PER_MILLISECOND = 1000000;
	// Conversion factor for time values
	private static final long MILLISECONDS_PER_SECOND = 1000;
	private static final long NANOSECONDS_PER_SECOND = NANOSECONDS_PER_MILLISECOND * MILLISECONDS_PER_SECOND;
	/* ---------------------------- */
	private static final int LOCATION_SEARCH_FASTEST_INTERVAL = 3000;
	private static final int LOCATION_SEARCH_INTERVAL = 5000;
	private LocationClient mLocationClient;
	private boolean mInMockMode;
	private Location mMockLocation;
	private MockLocationContinueTask mMockLocationContinueTask;
	private int msgResId = -1;
	private String mMockAddressName;

	@Override
	public int onStartCommand(Intent _intent, int _flags, int _startId) {
		mInMockMode = _intent.getBooleanExtra(EXTRAS_MOCK_MODE, false);
		if (mInMockMode) {
			mMockLocation = new Location(LocationClient.KEY_MOCK_LOCATION);
			mMockLocation.setLatitude(_intent.getDoubleExtra(EXTRAS_MOCK_LAT, 53.544344));
			mMockLocation.setLongitude(_intent.getDoubleExtra(EXTRAS_MOCK_LNG, 9.948814));
			mMockAddressName = _intent.getStringExtra(EXTRAS_MOCK_NAME);
		}
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();

		return super.onStartCommand(_intent, _flags, _startId);
	}

	@Override
	public void onDestroy() {
		synchronized (LOCK) {
			msgResId = R.string.toast_stop_location_tracking;
			if (mInMockMode) {
				if (mMockLocationContinueTask != null) {
					mMockLocationContinueTask.cancel(true);
					mMockLocationContinueTask = null;
				}
				Prefs.getInstance().setMockStatus(false);
				mLocationClient.setMockMode(false);
				mInMockMode = false;
				removeNotification();
				BusProvider.getBus().post(new UIShowAfterFinishMockingEvent());
				msgResId = R.string.toast_stop_location_mocking;
			}
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		}

		if (msgResId != -1) {
			Utils.showShortToast(getApplicationContext(), msgResId);
		}
		super.onDestroy();
	}

	public IBinder onBind(Intent _intent) {
		return null;
	}

	@Override
	public void onConnected(Bundle _bundle) {
		msgResId = R.string.toast_start_location_tracking;
		if (mInMockMode) {
			try {
				mLocationClient.setMockMode(true);
				Prefs.getInstance().setMockStatus(true);
				msgResId = R.string.toast_start_location_mocking;
				long elapsedRealTime = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? SystemClock
						.elapsedRealtimeNanos() : SystemClock.elapsedRealtime();
				long currentTime = System.currentTimeMillis();
				setMockLocation(elapsedRealTime, currentTime);
				mMockLocationContinueTask = new MockLocationContinueTask(this, elapsedRealTime, currentTime);
				mMockLocationContinueTask.execute();
				postNotification();
			} catch (SecurityException _e) {
				forceCloseServiceCantMockLocation();
				return;
			}
		}
		if (msgResId != -1) {
			Utils.showShortToast(getApplicationContext(), msgResId);
		}
		startTracking();
	}

	private   void forceCloseServiceCantMockLocation() {
		BusProvider.getBus().post(new UIShowCanNotMockLocationEvent());
		stopSelf();
	}

	private void setMockLocation(long _elapsedRealtimeNanos, long _currentTime) throws SecurityException{
		synchronized (LOCK) {
			if (mInMockMode) {
				mMockLocation.setAccuracy(3.0f);
				if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					mMockLocation.setElapsedRealtimeNanos(_elapsedRealtimeNanos);
				}
				mMockLocation.setTime(_currentTime);
				mLocationClient.setMockLocation(mMockLocation);
			}
		}
	}

	private void startTracking() {
		Location l = mLocationClient.getLastLocation();
		if (l != null) {
			BusProvider.getBus().post(new ServiceLocationChangedEvent(mInMockMode, l));
		} else {
			try {
				LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
						.setInterval(LOCATION_SEARCH_INTERVAL).setFastestInterval(LOCATION_SEARCH_FASTEST_INTERVAL);
				mLocationClient.requestLocationUpdates(req, this);
			} catch (Exception _e) {
				_e.printStackTrace();
			}
		}
	}

	@Override
	public void onLocationChanged(Location _location) {
		BusProvider.getBus().post(new ServiceLocationChangedEvent(mInMockMode, _location));
		LL.d("onLocationChanged");
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult _connectionResult) {
		// Shut down. Testing can't continue until the problem is fixed.
		stopSelf();
	}

	/***
	 * Dismiss the app and show a notification to continue mocking.
	 */
	private void postNotification() {

		/*
		 * Instantiate a new notification builder, using the API version that's
		 * backwards compatible to platform version 4.
		 */
		NotificationCompat.Builder builder;

		// Get the notification title
		String contentTitle = this.getString(R.string.title_notification);

		// prepare an intent with activity
		Intent intentNotify = new Intent(this, MainActivity.class);
		intentNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent called later
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNotify, 0);

		// Add values to the builder
		builder = new NotificationCompat.Builder(this).setAutoCancel(false).setSmallIcon(R.drawable.ic_radar_long)
				.setContentTitle(contentTitle).setContentText(mMockAddressName).setContentIntent(pendingIntent);

		startForeground(ID_TRAY_NOTIFICATION, builder.build());
	}

	/**
	 * Remove all notifications from the notification bar.
	 */
	private void removeNotification() {

		// An instance of NotificationManager is needed to remove notifications
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Remove all notifications
		notificationManager.cancelAll();
	}

	private static class MockLocationContinueTask extends AsyncTask<Void, Void, Void> {

		/* Some values for mock location */
		private int mPauseInterval = 2;// temp const
		private int mInjectionInterval = 1;// temp const
		private long mElapsedTimeNanos;
		private long mCurrentTime;
		private WeakReference<StartLocationTrackingService> mRefService;

		private MockLocationContinueTask(StartLocationTrackingService _service, long _elapsedTimeNanos,
				long _currentTime) {
			mRefService = new WeakReference<StartLocationTrackingService>(_service);
			mElapsedTimeNanos = _elapsedTimeNanos;
			mCurrentTime = _currentTime;
		}

		@Override
		protected Void doInBackground(Void... _params) {
			StartLocationTrackingService service = mRefService.get();
			if (service != null) {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						service.setMockLocation(mElapsedTimeNanos, mCurrentTime);
					}catch (SecurityException _e) {
						service.forceCloseServiceCantMockLocation();
					}

					/*
					 * Change the elapsed uptime and clock time by the amount of
					 * time requested.
					 */
					mElapsedTimeNanos += (long) mInjectionInterval
							* StartLocationTrackingService.NANOSECONDS_PER_SECOND;
					mCurrentTime += mInjectionInterval * StartLocationTrackingService.MILLISECONDS_PER_SECOND;
				}
			} else {
				//TODO There's no service more, what the App should do is not clear.
			}
			return null;
		}
	}
}
