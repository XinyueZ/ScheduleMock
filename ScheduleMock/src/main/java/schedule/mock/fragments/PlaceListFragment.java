package schedule.mock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import schedule.mock.R;
import schedule.mock.adapters.PlaceListAdapter;
import schedule.mock.events.UIPlaceListIsReadyEvent;
import schedule.mock.events.UIShowPlaceListEvent;
import schedule.mock.utils.BusProvider;


public final class PlaceListFragment extends BaseFragment {

	public static final int LAYOUT = R.layout.fragment_place_list;


	public static PlaceListFragment newInstance(Context _context) {
		return (PlaceListFragment) PlaceListFragment.instantiate(_context, PlaceListFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		BusProvider.getBus().post(new UIPlaceListIsReadyEvent());
	}


	@Subscribe
	public void onShowPlaceList(UIShowPlaceListEvent _e) {
		View view = getView();
		if (view != null && _e.getNearByResults() != null) {
			PlaceListAdapter adapter = new PlaceListAdapter(getActivity().getApplicationContext(),
					_e.getNearByResults());
			ListView listView = (ListView) view.findViewById(R.id.lv_places);
			listView.setAdapter(adapter);
		}
	}
}
