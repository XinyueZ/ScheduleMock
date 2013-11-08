package schedule.mock.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

/**
 * An service that mocks location of device.
 */
public final class MockLocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
	private LocationClient mLocationClient;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnected(Bundle _bundle) {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location _location) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult _connectionResult) {

	}
}
