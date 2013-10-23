package schedule.mock.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.TimerTask;

import schedule.mock.views.MyToast;


public final class LocationUtil {

	static final int LOCATION_SEARCH_FASTEST_INTERVAL = 3000;
	static final int LOCATION_SEARCH_INTERVAL = 5000;
	Activity context;
	LocationClient client;
	LocationListener listener;
	java.util.Timer timer = new java.util.Timer(true);
	boolean found = false;
	boolean showToast = true;

	public LocationUtil(Activity _context, GooglePlayServicesClient.ConnectionCallbacks _callbacks, GooglePlayServicesClient.OnConnectionFailedListener _failedListener,
	                    LocationListener _listener) {
		context = _context;
		client = new LocationClient(_context, _callbacks, _failedListener);
		listener = _listener;
		client.connect();
	}

	public static String getBestProvider(Context _context) {
		Context app = _context.getApplicationContext();
		LocationManager lm = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		ConnectivityManager conManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return LocationManager.GPS_PROVIDER;
		} else if( lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
			return LocationManager.NETWORK_PROVIDER;
		} else {
			return null;
		}
	}

	public Location getLast() {
		try {
			return client.getLastLocation();
		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return null;
	}

	public void startTracking(boolean _showToast) {
		try {
			LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setInterval(LOCATION_SEARCH_INTERVAL).setFastestInterval(LOCATION_SEARCH_FASTEST_INTERVAL);
			client.requestLocationUpdates(req, listener);
			if (_showToast) {
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if (!isFound()) {
							context.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (showToast) {
										MyToast.showInstance(context, MyToast.ToastType.LOCATION);
//										Util.showShortToast(context, "Dummy indicator while searching position.");
									}
								}
							});
						}
					}
				}, 5000, 5000);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	public void startTracking() {
		startTracking(true);
	}


	public void stopTracking() {
		try {
			stopTimer();
			if (client != null) {
				client.removeLocationUpdates(listener);
				client.disconnect();
				client = null;
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}

	public void onResume() {
		showToast = true;
	}

	public void onPause() {
		showToast = false;
		try {
			stopTracking();
		} catch (Exception _e) {
			_e.printStackTrace();
		}

	}



	void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	synchronized boolean isFound() {
		return found;
	}

	public synchronized void setFound(boolean _found) {
		found = _found;
	}
}
