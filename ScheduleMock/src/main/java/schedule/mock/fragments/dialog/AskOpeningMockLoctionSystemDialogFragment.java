package schedule.mock.fragments.dialog;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import schedule.mock.R;
import schedule.mock.events.UIDismissAppEvent;
import schedule.mock.utils.BusProvider;

public final class AskOpeningMockLoctionSystemDialogFragment extends YesNoDialogFragment {

	public static boolean showInstance(FragmentActivity _context) {
		int allow = 0;
		try {
			allow = Settings.Secure.getInt(_context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		if (allow == 0) {
			Bundle args = new Bundle();
			args.putString(EXTRAS_TITLE, _context.getString(R.string.popup_mock_location));
			args.putString(EXTRAS_CONTENT, _context.getString(R.string.popup_open_mock_location));
			DialogFragment fragment = (DialogFragment) AskOpeningMockLoctionSystemDialogFragment.instantiate(_context,
					AskOpeningMockLoctionSystemDialogFragment.class.getName(), args);
			show(_context, fragment, null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onYes() {
		super.onYes();
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings"));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}

	@Override
	public void onNo() {
		super.onNo();
		BusProvider.getBus().post(new UIDismissAppEvent());
	}
}
