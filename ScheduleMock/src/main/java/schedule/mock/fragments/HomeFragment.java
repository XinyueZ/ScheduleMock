package schedule.mock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import schedule.mock.App;
import schedule.mock.R;


public final class HomeFragment extends BaseFragment implements View.OnClickListener {

	public static final int LAYOUT = R.layout.fragment_main;


	public static HomeFragment newInstance(Context _context) {
		return (HomeFragment) HomeFragment.instantiate(_context, HomeFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		_view.findViewById(R.id.btn_mock_one_time).setOnClickListener(this);
		_view.findViewById(R.id.btn_schedule).setOnClickListener(this);
		_view.findViewById(R.id.btn_map).setOnClickListener(this);
	}


	@Override
	public void onClick(View _v) {
		switch (_v.getId()) {
			case R.id.btn_mock_one_time:
				getFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.slide_in_from_down_to_top_fast, R.anim.no,
								R.anim.no, R.anim.slide_out_from_top_to_down_fast)
						.add(App.MAIN_CONTAINER, InputFragment.newInstance(getActivity())).addToBackStack(null).commit();
				break;
			case R.id.btn_schedule:
				break;
			case R.id.btn_map:
				break;
		}
	}
}
