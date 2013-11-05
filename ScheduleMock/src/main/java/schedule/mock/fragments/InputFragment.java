package schedule.mock.fragments;

import java.util.ArrayList;
import java.util.Locale;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DOGeocode;
import schedule.mock.data.DOGeocodeResult;
import schedule.mock.data.DOGeometry;
import schedule.mock.data.DOLatLng;
import schedule.mock.data.DONearBy;
import schedule.mock.data.DONearByResult;
import schedule.mock.events.UIPlaceListIsReadyEvent;
import schedule.mock.events.UIShowPlaceListEvent;
import schedule.mock.prefs.Prefs;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.LL;
import schedule.mock.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.squareup.otto.Subscribe;


public final class InputFragment extends BaseFragment implements View.OnClickListener {

	public static final int LAYOUT = R.layout.fragment_input;
	private static final int REQUEST_CODE_VOICE_RECOGNITION = 1234;
	DONearByResult[] mNearByResults;


	public static InputFragment newInstance(Context _context) {
		return (InputFragment) InputFragment.instantiate(_context, InputFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		startVoiceRecognitionActivityAndPayloadPlaces();
		return rootView;
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			_view.findViewById(R.id.btn_search).setOnClickListener(this);
			_view.findViewById(R.id.btn_voice_input).setOnClickListener(this);
		}
	}


	private void startVoiceRecognitionActivityAndPayloadPlaces() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, App.COUNT_VOICE_RESULT);
		startActivityForResult(intent, REQUEST_CODE_VOICE_RECOGNITION);
	}


	@Override
	public void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		switch (_requestCode) {
			case REQUEST_CODE_VOICE_RECOGNITION:
				if (_resultCode == Activity.RESULT_OK) {
					ArrayList<String> matches = _data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					if (matches != null) {
						payloadPlaces(matches.get(0));
					}
				} else {
					View view = getView();
					if (view != null) {
						view.findViewById(R.id.btn_voice_input).setVisibility(View.VISIBLE);
					}
				}
				break;
		}
		super.onActivityResult(_requestCode, _resultCode, _data);
	}


	private void payloadPlaces(final String str) {
		String url = String.format(App.API_GEOCODE, Utils.encodedKeywords(str), Locale.getDefault()
				.getLanguage());
		LL.d("Start geocode:" + url);
		new GsonRequestTask<DOGeocode>(getActivity().getApplicationContext(), Request.Method.GET, url.trim(), DOGeocode.class)
				.execute();
	}


	private void searchAndPayloadPlaces() {
		View view = getView();
		if (view != null) {
			String address = ((TextView) view.findViewById(R.id.et_mocked_address_name)).getText().toString();
			String city = ((TextView) view.findViewById(R.id.et_mocked_city_name)).getText().toString();
			String fullLocation = new StringBuilder().append(address).append(city).toString();
			payloadPlaces(fullLocation);
		}
	}


	@Override
	public void onClick(View _view) {
		switch (_view.getId()) {
			case R.id.btn_search:
				searchAndPayloadPlaces();
				break;
			case R.id.btn_voice_input:
				startVoiceRecognitionActivityAndPayloadPlaces();
				break;
		}
	}


	/**
	 * Finished converting from address to lat-lan(Geocode job).
	 * 
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGeocode _geocode) {
		Context ctx = getActivity().getApplicationContext();
		DOGeocodeResult[] results = _geocode.getGeocodeResults();
		DOGeocodeResult result = results[0];
		DOGeometry geometry = result.getGeometry();
		DOLatLng location = geometry.getNearByLocation();
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		// DOPlace place = new DOPlace(result.getFullAddress(), lat, lng);
		// LL.d("Geocode result:" + place.toString());
		// if (place != null) {
		String url = String.format(App.API_NEAR_BY, lat, lng, Prefs.getInstance().getRadius(), Locale.getDefault()
				.getLanguage());
		LL.d("Start place near-by:" + url);
		new GsonRequestTask<DONearBy>(ctx, Request.Method.GET, url, DONearBy.class).execute();
		// }
	}


	/**
	 * Finished loading near-by places.
	 * 
	 * **/
	@Subscribe
	public void onDONearBySuccess(DONearBy _nearBy) {
		mNearByResults = _nearBy.getNearByResults();
		if (mNearByResults != null) {
			PlaceListDialogFragment.showInstance(getActivity());
		}
	}

	/**
	 * UI is ready showing near-by places, and we push data onto it.ç
	 *
	 * **/
	@Subscribe
	public void onPlaceListIsReady(UIPlaceListIsReadyEvent _e) {
		BusProvider.getBus().post(new UIShowPlaceListEvent(mNearByResults));
	}
}
