package schedule.mock.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;

public final class MyMapFragment extends SupportMapFragment {
	private static final int MEDIUM_ZOOM = 16;
	private static final String EXTRAS_LAT = "extras.map.lat";
	private static final String EXTRAS_LNG = "extras.map.lng";
	private static final String EXTRAS_NAME = "extras.map.location.name";

	public static MyMapFragment newInstance(Location _latLng, String _name) {
		MyMapFragment myMapFragment = new MyMapFragment();
		Bundle args = new Bundle();
		args.putDouble(EXTRAS_LAT, _latLng.getLatitude());
		args.putDouble(EXTRAS_LNG, _latLng.getLongitude());
		args.putString(EXTRAS_NAME, _name);
		myMapFragment.setArguments(args);
		return myMapFragment;
	}

	private static void setUpMap(SupportMapFragment _supportMapFragment) {
		GoogleMap googleMap = _supportMapFragment.getMap();
		googleMap.setTrafficEnabled(false);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setAllGesturesEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setZoomGesturesEnabled(true);
		googleMap.setMyLocationEnabled(true);
		double lat = _supportMapFragment.getArguments().getDouble(EXTRAS_LAT);
		double lng = _supportMapFragment.getArguments().getDouble(EXTRAS_LNG);
		LatLng mallLocation = new LatLng(lat, lng);
		// mMap.setOnMarkerClickListener(this);
		googleMap.addMarker(new MarkerOptions().position(mallLocation));
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mallLocation, MEDIUM_ZOOM));
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getBus().unregister(this);
		TaskHelper.getRequestQueue().cancelAll(GsonRequestTask.TAG);
	}

	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		BusProvider.getBus().register(this);
		if (_view != null) {
			setUpMap(this);
		}
	}
}
