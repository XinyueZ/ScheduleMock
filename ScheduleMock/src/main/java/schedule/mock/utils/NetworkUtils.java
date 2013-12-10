package schedule.mock.utils;


import android.content.Context;
import android.location.LocationManager;

public final class NetworkUtils {
	public static boolean isGPSEnable(Context _context) {
		Context app = _context.getApplicationContext();
		LocationManager lm = (LocationManager) app
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public static boolean isNetworkLocationEnable(Context _context) {
		Context app = _context.getApplicationContext();
		LocationManager lm = (LocationManager) app
				.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
}
