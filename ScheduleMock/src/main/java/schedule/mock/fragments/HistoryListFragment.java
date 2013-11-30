package schedule.mock.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.util.List;

import schedule.mock.R;
import schedule.mock.adapters.HistoryListAdapter;
import schedule.mock.data.DOHistoryRecorder;
import schedule.mock.tasks.db.DeleteHistoryTask;
import schedule.mock.tasks.db.LoadHistoryTask;

public final class HistoryListFragment extends BaseFragment implements OnDismissCallback {
	public static final String TAG = HistoryListFragment.class.getName();
	public static final int MENU_POSITION = 3;
	private static final int LAYOUT = R.layout.fragment_history_list;
	private HistoryListAdapter mAdapter;

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
			new LoadHistoryTask(activity.getApplicationContext()) {
				@Override
				protected void onShowList(Context _context, List<DOHistoryRecorder> _historyRecorders) {
					View view = getView();
					if (view != null) {
						View emptyWarning = view.findViewById(R.id.ll_empty_list);
						if (_historyRecorders != null && _historyRecorders.size() > 0) {
							ListView listView = (ListView) view.findViewById(R.id.lv_history_list);
							mAdapter = new HistoryListAdapter(_context, _historyRecorders);
							supportCardAnim(listView);
							emptyWarning.setVisibility(View.GONE);
						} else {
							emptyWarning.setVisibility(View.VISIBLE);
						}
					}
				}
			}.exec();

		}
	}

	private void supportCardAnim(ListView _listView) {
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mAdapter, this));
		swingBottomInAnimationAdapter.setAbsListView(_listView);
		_listView.setAdapter(swingBottomInAnimationAdapter);
	}

	@Override
	public void onDismiss(AbsListView _listView, int[] _reverseSortedPositions) {
		DOHistoryRecorder row;
		List<DOHistoryRecorder> recorders = mAdapter.getRecorders();
		for (final int position : _reverseSortedPositions) {
			row = recorders.get(position);
			new DeleteHistoryTask(row.getLatLng()) {
				@Override
				protected void onHistoryDeleted() {
					mAdapter.remove(position);
					mAdapter.notifyDataSetChanged();
					if (mAdapter.getCount() == 0) {
						View view = getView();
						if (view != null) {
							View emptyWarning = view.findViewById(R.id.ll_empty_list);
							emptyWarning.setVisibility(View.VISIBLE);
						}
					}
				}
			}.exec();
		}
	}
}
