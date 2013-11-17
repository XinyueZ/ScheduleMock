package schedule.mock.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.UIShowAfterFinishMockingEvent;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;

public final class MyMapFragment extends SupportMapFragment {
	public static final String TAG = MyMapFragment.class.getName();
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
		Bundle args = _supportMapFragment.getArguments();
		double lat =args .getDouble(EXTRAS_LAT);
		double lng =args.getDouble(EXTRAS_LNG);
		setMapLocation(googleMap, lat, lng);
	}

	private static void setMapLocation(GoogleMap _googleMap, double _lat, double _lng) {
		LatLng latLng = new LatLng(_lat, _lng);
		// mMap.setOnMarkerClickListener(this);
		_googleMap.addMarker(new MarkerOptions().position(latLng));
		_googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MEDIUM_ZOOM));
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

	/***
	 * Mocking is finished. See also in @link{MainActivity}.
	 * @param _e
	 */
	@Subscribe
	public void onUIShowAfterFinishMocking(UIShowAfterFinishMockingEvent _e) {
		getMap().clear();
	}

	/**
	 * Has gotten the newest location from trackings-services.
	 * **/
	@Subscribe
	public void onServiceLocationChanged(ServiceLocationChangedEvent _e) {
		getMap().clear();
		setMapLocation(getMap(), _e.getLocation().getLatitude(), _e.getLocation().getLongitude());
	}
}
