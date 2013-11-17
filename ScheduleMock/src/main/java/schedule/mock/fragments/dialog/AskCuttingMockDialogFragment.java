package schedule.mock.fragments.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import schedule.mock.R;
import schedule.mock.events.CutMockingEvent;
import schedule.mock.utils.BusProvider;

public final class AskCuttingMockDialogFragment extends YesNoDialogFragment {

	public static void showInstance(FragmentActivity _context) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_TITLE, _context.getString(R.string.popup_stop_mocking));
		args.putString(EXTRAS_CONTENT, _context.getString(R.string.popup_cut_mocking));
		DialogFragment fragment = (DialogFragment) AskCuttingMockDialogFragment.instantiate(_context,
				AskCuttingMockDialogFragment.class.getName(), args);
		show(_context, fragment, null);
	}

	@Override
	public void onYes() {
		super.onYes();
		BusProvider.getBus().post(new CutMockingEvent());
	}
}
