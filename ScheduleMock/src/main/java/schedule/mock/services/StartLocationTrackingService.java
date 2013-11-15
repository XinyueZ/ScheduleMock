package schedule.mock.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.LL;

public final class StartLocationTrackingService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {
	public static final String EXTRAS_MOCK_MODE = "extras_mock_mode";
	public static final String EXTRAS_MOCK_LAT = "extras_mock_lat";
	public static final String EXTRAS_MOCK_LNG = "extras_mock_lng";
	/* Some values for mock location */
	// Conversion factor for boot time
	private static final long NANOSECONDS_PER_MILLISECOND = 1000000;
	// Conversion factor for time values
	private static final long MILLISECONDS_PER_SECOND = 1000;
	// Conversion factor for time values
	public static final long NANOSECONDS_PER_SECOND = NANOSECONDS_PER_MILLISECOND * MILLISECONDS_PER_SECOND;
	/* ---------------------------- */
	private static final int LOCATION_SEARCH_FASTEST_INTERVAL = 3000;
	private static final int LOCATION_SEARCH_INTERVAL = 5000;
	private LocationClient mLocationClient;
	private boolean mMockMode;
	private Location mMockLocation;

	@Override
	public int onStartCommand(Intent _intent, int _flags, int _startId) {
		mMockMode = _intent.getBooleanExtra(EXTRAS_MOCK_MODE, false);
		if (mMockMode) {
			mMockLocation = new Location(LocationClient.KEY_MOCK_LOCATION);
			mMockLocation.setLatitude(_intent.getDoubleExtra(EXTRAS_MOCK_LAT, 51.4521));
			mMockLocation.setLongitude(_intent.getDoubleExtra(EXTRAS_MOCK_LNG, 7.0038));
		}
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();

		return super.onStartCommand(_intent, _flags, _startId);
	}

	@Override
	public void onDestroy() {
		if (mMockMode) {
			mLocationClient.setMockMode(false);
		}
		mLocationClient.removeLocationUpdates(this);
		mLocationClient.disconnect();
		super.onDestroy();
	}

	public IBinder onBind(Intent _intent) {
		return null;
	}

	@Override
	public void onConnected(Bundle _bundle) {
		if (mMockMode) {

			try {
				mLocationClient.setMockMode(true);
				long elapsedRealTime = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? SystemClock
						.elapsedRealtimeNanos() : SystemClock.elapsedRealtime();
				long currentTime = System.currentTimeMillis();
				fireMockLocation(elapsedRealTime, currentTime);
				new MockLocationContinueTask(this, elapsedRealTime, currentTime).execute();
			} catch (SecurityException _e) {
				stopSelf();
				return;
			}
		}
		startTracking();
	}

	private void fireMockLocation(long _elapsedRealtimeNanos, long _currentTime) {
		mMockLocation.setAccuracy(3.0f);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mMockLocation.setElapsedRealtimeNanos(_elapsedRealtimeNanos);
		}
		mMockLocation.setTime(_currentTime);
		mLocationClient.setMockLocation(mMockLocation);
	}

	private void startTracking() {
		Location l = mLocationClient.getLastLocation();
		if (l != null) {
			BusProvider.getBus().post(new ServiceLocationChangedEvent(mMockMode, l));
			LL.d("startTracking -> find current location.");
		} else {
			try {
				LL.d("startTracking -> searching new.");
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
		BusProvider.getBus().post(new ServiceLocationChangedEvent(mMockMode, _location));
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
					service.fireMockLocation(mElapsedTimeNanos, mCurrentTime);

					/*
					 * Change the elapsed uptime and clock time by the amount of
					 * time requested.
					 */
					mElapsedTimeNanos += (long) mInjectionInterval
							* StartLocationTrackingService.NANOSECONDS_PER_SECOND;
					mCurrentTime += mInjectionInterval * StartLocationTrackingService.MILLISECONDS_PER_SECOND;
				}
			}
			return null;
		}
	}
}
