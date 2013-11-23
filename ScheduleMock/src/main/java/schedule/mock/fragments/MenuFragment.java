package schedule.mock.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.adapters.MenuAdapter;
import schedule.mock.enums.MenuItem;
import schedule.mock.events.UICloseSidebarEvent;
import schedule.mock.events.UIHighlightMenuItemEvent;
import schedule.mock.events.UIShowHomeEvent;
import schedule.mock.utils.BusProvider;

public final class MenuFragment extends ListFragment {

	private MenuItem[] mMenuItems = { MenuItem.HOME, MenuItem.INPUT, MenuItem.MAP, MenuItem.HISTORY, MenuItem.SCHEDULE,
			MenuItem.G_PLUS };
	private MenuAdapter mMenuAdapter;

	@Override
	public void onDestroyView() {
		BusProvider.getBus().unregister(this);
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		BusProvider.getBus().register(this);
		Activity activity = getActivity();
		if (activity != null) {
			setListAdapter(mMenuAdapter = new MenuAdapter(activity.getApplicationContext(), mMenuItems));


			DisplayMetrics displaymetrics = new DisplayMetrics();
			Display[] displays = DisplayManagerCompat.getInstance(getActivity().getApplicationContext()).getDisplays();
			Display display = displays[ 0 ];
			display.getMetrics(displaymetrics);
			int width = displaymetrics.widthPixels;
			getListView().getLayoutParams().width = (int)(width * (2 / 3.0));
		}
		getListView().setBackgroundResource(R.drawable.bg_menu);
		/* Init menu */
		BusProvider.getBus().post(new UICloseSidebarEvent(new UIShowHomeEvent()));

	}

	@Override
	public void onListItemClick(ListView _l, View _v, int _position, long _id) {
		super.onListItemClick(_l, _v, _position, _id);
		/*
		 * To close sidebar, see the Event-Handler for more INFO in
		 * MainActivity.java.
		 */
		MenuItem menuItem = mMenuItems[_position];
		Object openEvent = null;
		if (menuItem.isEnable()) {
			openEvent = menuItem.getOpenEvent();
		}
		BusProvider.getBus().post(new UICloseSidebarEvent(openEvent));
	}

	@Subscribe
	public void onHighlightMenuItem(UIHighlightMenuItemEvent _e) {
		/* What being selected. */
		MenuItem menuItem = mMenuItems[_e.getPosition()];
		if (menuItem.isEnable()) {
			menuItem.setEnable(false);
			App.setSelectedMenuItem(menuItem);
			for (MenuItem item : mMenuItems) {
				if (item != menuItem) {
					/* What not being selected. */
					item.setEnable(true);
				}
			}
			mMenuAdapter.notifyDataSetChanged();
		}
	}
}
