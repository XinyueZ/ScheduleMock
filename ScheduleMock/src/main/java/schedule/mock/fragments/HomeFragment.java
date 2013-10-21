package schedule.mock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import schedule.mock.R;


public final class HomeFragment extends BaseFragment {

	public static final int LAYOUT = R.layout.fragment_main;

	public static HomeFragment newInstance(Context _context) {
		return (HomeFragment) HomeFragment.instantiate(_context, HomeFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}
}
