package schedule.mock.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import schedule.mock.R;
import schedule.mock.adapters.PlaceListAdapter;
import schedule.mock.events.UIClosePlaceListDialogFragmentEvent;
import schedule.mock.events.UIPlaceListIsReadyEvent;
import schedule.mock.events.UIShowPlaceListEvent;
import schedule.mock.utils.BusProvider;


public final class PlaceListDialogFragment extends BaseDialogFragment implements View.OnClickListener {
	private static final String TAG = "PlaceListDialogFragment";
	public static final int LAYOUT = R.layout.fragment_place_list;


	public static void showInstance(FragmentActivity _context) {
		DialogFragment fragment = (DialogFragment) PlaceListDialogFragment.instantiate(_context,
				PlaceListDialogFragment.class.getName());
		show(  _context, fragment, TAG);
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
			View close = view.findViewById(R.id.btn_cyan);
			((TextView)close).setText(R.string.general_close);
			close.setOnClickListener(this);
			PlaceListAdapter adapter = new PlaceListAdapter(getActivity().getApplicationContext(),
					_e.getNearByResults());
			ListView listView = (ListView) view.findViewById(R.id.lv_places);
			listView.setAdapter(adapter);
			getDialog().setTitle(R.string.title_mock_from_list);
		}
	}

	@Override
	public void onClick(View v) {
		BusProvider.getBus().post(new UIClosePlaceListDialogFragmentEvent());
	}

	@Subscribe
	public void onClosePlaceListFragmentDialog(UIClosePlaceListDialogFragmentEvent _e) {
		dismiss();
	}
}
