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

import schedule.mock.events.FindMyLocationEvent;
import schedule.mock.events.ServiceLocationChangedEvent;
import schedule.mock.events.UIHighlightMenuItemEvent;
import schedule.mock.events.UIShowAfterFinishMockingEvent;
import schedule.mock.interfaces.ICanOpenByMenuItem;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.BusProvider;

public final class MyMapFragment extends SupportMapFragment implements ICanOpenByMenuItem {
	public static final String TAG = MyMapFragment.class.getName();
	public static final int MENU_POSITION = 2;
	private static final int MEDIUM_ZOOM = 16;
	private static final String EXTRAS_LAT = "extras.map.lat";
	private static final String EXTRAS_LNG = "extras.map.lng";
	private static final String EXTRAS_NAME = "extras.map.location.name";
	private static final String EXTRAS_GIVEN_LOCATION = "extras.map.given.location";

	public static MyMapFragment newInstance(Location _latLng, String _name) {
		MyMapFragment myMapFragment = new MyMapFragment();
		Bundle args = new Bundle();
		args.putDouble(EXTRAS_LAT, _latLng.getLatitude());
		args.putDouble(EXTRAS_LNG, _latLng.getLongitude());
		args.putString(EXTRAS_NAME, _name);
		args.putBoolean(EXTRAS_GIVEN_LOCATION, true);
		myMapFragment.setArguments(args);
		return myMapFragment;
	}

	public static MyMapFragment newInstance() {
		MyMapFragment myMapFragment = new MyMapFragment();
		Bundle args = new Bundle();
		args.putBoolean(EXTRAS_GIVEN_LOCATION, false);
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
		boolean given = args.getBoolean(EXTRAS_GIVEN_LOCATION);
		if (given) {
			double lat = args.getDouble(EXTRAS_LAT);
			double lng = args.getDouble(EXTRAS_LNG);
			setMapLocation(googleMap, lat, lng);
		} else {
			Location location = googleMap.getMyLocation();
			if (location != null) {
				setMapLocation(googleMap, location.getLatitude(), location.getLongitude());
			} else {
				BusProvider.getBus().post(new FindMyLocationEvent());
			}
		}
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
		BusProvider.getBus().post(new UIHighlightMenuItemEvent(getMenuItemPosition()));
	}

	/***
	 * Mocking is finished. See also in @link{MainActivity}.
	 * 
	 * @param _e
	 */
	@Subscribe
	public void onUIShowAfterFinishMocking(UIShowAfterFinishMockingEvent _e) {
		GoogleMap googleMap = getMap();
		if (googleMap != null) {
			googleMap.clear();
		}
	}

	/**
	 * Has gotten the newest location from trackings-services.
	 * **/
	@Subscribe
	public void onServiceLocationChanged(ServiceLocationChangedEvent _e) {
		GoogleMap googleMap = getMap();
		if (googleMap != null) {
			Location location = _e.getLocation();
			googleMap.clear();
			setMapLocation(googleMap, location.getLatitude(), location.getLongitude());
		}
	}

	@Override
	public int getMenuItemPosition() {
		return MENU_POSITION;
	}
}
