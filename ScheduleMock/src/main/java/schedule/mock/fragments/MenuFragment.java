package schedule.mock.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import schedule.mock.R;
import schedule.mock.adapters.MenuAdapter;

public final class MenuFragment extends ListFragment {
	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		Activity activity = getActivity();
		if (activity != null) {
			setListAdapter(new MenuAdapter(activity.getApplicationContext()));
		}
		getListView().setBackgroundResource(R.drawable.bg_menu);
	}
}
