package schedule.mock.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import schedule.mock.events.UIDismissAppEvent;

public final class GooglePlayServicesUtils {

	public static final int REQUEST_PLAY_SERVICE_PROBLEM = 9000;

	public static boolean isGooglePlayServicesNotAvailable(FragmentActivity _activity) {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(_activity.getApplicationContext());
		// If Google Play services is available
		if (ConnectionResult.SUCCESS != resultCode) {
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, _activity,
					REQUEST_PLAY_SERVICE_PROBLEM);
			switch (resultCode) {
			/*
			 * Some problems that can't be walked around should result in
			 * dismissing the App.
			 */
			case ConnectionResult.SERVICE_MISSING:
			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			case ConnectionResult.SERVICE_DISABLED:
			case ConnectionResult.INTERNAL_ERROR:
			case ConnectionResult.SERVICE_INVALID:
				if (errorDialog != null) {
					errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							BusProvider.getBus().post(new UIDismissAppEvent());
						}
					});
				}
				break;
			default:
				break;
			}
			if (errorDialog != null) {
				errorDialog.show();
			}
			return true;
		} else {
			return false;
		}
	}

	public static void onConnectionFailed(FragmentActivity _activity, ConnectionResult _connectionResult)
			throws IntentSender.SendIntentException {
		if (_connectionResult.hasResolution()) {
			try {
				_connectionResult.startResolutionForResult(_activity, REQUEST_PLAY_SERVICE_PROBLEM);
			} catch (IntentSender.SendIntentException e) {
				throw e;
			}
		} else {
			/*
			 * Some problems that can't be solved should result in dismissing
			 * the App.
			 */
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(_connectionResult.getErrorCode(), _activity,
					REQUEST_PLAY_SERVICE_PROBLEM);
			if (errorDialog != null) {
				errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						BusProvider.getBus().post(new UIDismissAppEvent());
					}
				});
				errorDialog.show();
			}
		}
	}
}
