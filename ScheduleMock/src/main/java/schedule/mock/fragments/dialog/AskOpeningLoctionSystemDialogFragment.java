package schedule.mock.fragments.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import schedule.mock.R;
import schedule.mock.events.UIDismissAppEvent;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.NetworkUtils;

public final class AskOpeningLoctionSystemDialogFragment extends YesNoDialogFragment {

	public static boolean showInstance(FragmentActivity _context) {
		if (!NetworkUtils.isNetworkLocationEnable(_context) && !NetworkUtils.isGPSEnable(_context)) {
			Bundle args = new Bundle();
			args.putString(EXTRAS_TITLE, _context.getString(R.string.popup_location));
			args.putString(EXTRAS_CONTENT, _context.getString(R.string.popup_open_location_system));
			DialogFragment fragment = (DialogFragment) AskOpeningLoctionSystemDialogFragment.instantiate(_context,
					AskOpeningLoctionSystemDialogFragment.class.getName(), args);
			show(_context, fragment, null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onYes() {
		super.onYes();
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}

	@Override
	public void onNo() {
		super.onNo();
		BusProvider.getBus().post(new UIDismissAppEvent());
	}
}
