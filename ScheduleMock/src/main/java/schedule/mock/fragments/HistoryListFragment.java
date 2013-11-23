package schedule.mock.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import schedule.mock.R;
import schedule.mock.adapters.HistoryListAdapter;
import schedule.mock.data.DOHistoryRecorder;
import schedule.mock.tasks.db.LoadHistoryTask;

public final class HistoryListFragment extends BaseFragment {
	public static final String TAG = HistoryListFragment.class.getName();
	public static final int MENU_POSITION = 3;
	private static final int LAYOUT = R.layout.fragment_history_list;

	public static HistoryListFragment newInstance(Context _context) {
		return (HistoryListFragment) HistoryListFragment.instantiate(_context, HistoryListFragment.class.getName());
	}

	@Override
	public int getMenuItemPosition() {
		return MENU_POSITION;
	}

	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle _savedInstanceState) {
		super.onActivityCreated(_savedInstanceState);
		showList();
	}

	private void showList() {
		Activity activity = getActivity();
		if (activity != null) {
			LoadHistoryTask task = new LoadHistoryTask(activity.getApplicationContext()) {
				@Override
				protected void onShowList(Context _context, List<DOHistoryRecorder> _historyRecorders) {
					View view = getView();
					if (view != null) {
						ListView listView = (ListView) view.findViewById(R.id.lv_history_list);
						listView.setAdapter(new HistoryListAdapter(_context, _historyRecorders));
					}
				}
			};

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
			} else {
				task.execute();
			}
		}
	}
}
