package schedule.mock.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.LL;


public final class StartLocationTrackingService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
	public static final String EXTRAS_MOCK_MODE = "extras_mock_mode";
	public static final String EXTRAS_MOCK_LAT = "extras_mock_lat";
	public static final String EXTRAS_MOCK_LNG = "extras_mock_lng";
	static final int LOCATION_SEARCH_FASTEST_INTERVAL = 3000;
	static final int LOCATION_SEARCH_INTERVAL = 5000;
	private LocationClient mLocationClient;
	private boolean mMockMode;
	private Location mMockLocation;

	@Override
	public int onStartCommand(Intent _intent, int _flags, int _startId) {
		mMockMode = _intent.getBooleanExtra(EXTRAS_MOCK_MODE, false);
		if(mMockMode) {
			mMockLocation =  new Location("mock_provider");
			mMockLocation.setLatitude(_intent.getDoubleExtra(EXTRAS_MOCK_LAT, 51.4521));
			mMockLocation.setLongitude(_intent.getDoubleExtra(EXTRAS_MOCK_LNG, 7.0038));
		}
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();

		return super.onStartCommand(_intent, _flags, _startId);
	}

	@Override
	public void onDestroy() {
		mLocationClient.removeLocationUpdates(this);
		mLocationClient.disconnect();
		super.onDestroy();
	}


	public IBinder onBind(Intent _intent) {
		return null;
	}


	@Override
	public void onConnected(Bundle _bundle) {
		if(mMockMode) {
			mLocationClient.setMockMode(true);
			mMockLocation.setAccuracy(50);
			mMockLocation.setTime(10000);
			mLocationClient.setMockLocation(mMockLocation);
		}
		startTracking();
	}


	private void startTracking() {
		Location l = mLocationClient.getLastLocation();
		if (l != null) {
			BusProvider.getBus().post(new ServiceLocationChangedEvent(l));
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
		BusProvider.getBus().post(new ServiceLocationChangedEvent(_location));
		LL.d("onLocationChanged");
	}


	@Override
	public void onDisconnected() {
	}


	@Override
	public void onConnectionFailed(ConnectionResult _connectionResult) {
	}
}
