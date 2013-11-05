package schedule.mock.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;


public abstract class BaseDialogFragment extends DialogFragment {

	protected static void show(FragmentActivity _activty, DialogFragment _dlgFrg, String _tagName) {
		try {
			if (_dlgFrg != null) {
				DialogFragment dialogFragment = _dlgFrg;
				FragmentTransaction ft = _activty.getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = _activty.getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(_tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, _tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		BusProvider.getBus().register(this);
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getBus().unregister(this);
		TaskHelper.getRequestQueue().cancelAll(GsonRequestTask.TAG);
	}
}
