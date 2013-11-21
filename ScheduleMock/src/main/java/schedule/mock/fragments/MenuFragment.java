package schedule.mock.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import schedule.mock.R;
import schedule.mock.adapters.MenuAdapter;
import schedule.mock.enums.MenuItem;
import schedule.mock.events.UICloseSidebarEvent;
import schedule.mock.utils.BusProvider;

public final class MenuFragment extends ListFragment {

	private MenuItem[] mMenuItems = { MenuItem.INPUT, MenuItem.MAP, MenuItem.HISTORY, MenuItem.SCHEDULE,
			MenuItem.G_PLUS };
	private MenuAdapter mMenuAdapter;

	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		Activity activity = getActivity();
		if (activity != null) {
			setListAdapter(mMenuAdapter = new MenuAdapter(activity.getApplicationContext(), mMenuItems));
		}
		getListView().setBackgroundResource(R.drawable.bg_menu);
	}

	@Override
	public void onListItemClick(ListView _l, View _v, int _position, long _id) {
		super.onListItemClick(_l, _v, _position, _id);
		/* What being selected. */
		MenuItem menuItem = mMenuItems[_position];
		Object openEvent  = null;
		if (menuItem.isEnable()) {
			openEvent = menuItem.getOpenEvent();
			menuItem.setEnable(false);
			for (MenuItem item : mMenuItems) {
				if (item != menuItem) {
					/* What not being selected. */
					item.setEnable(true);
				}
			}
			mMenuAdapter.notifyDataSetChanged();
		}

		/*
		 * To close sidebar, see the Event-Handler for more INFO in
		 * MainActivity.java.
		 */
		BusProvider.getBus().post(new UICloseSidebarEvent(openEvent));
	}
}
