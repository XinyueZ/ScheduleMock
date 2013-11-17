package schedule.mock.fragments;

import java.util.ArrayList;
import java.util.Locale;

import schedule.mock.App;
import schedule.mock.BuildConfig;
import schedule.mock.R;
import schedule.mock.data.DOGecodeFromVoiceAddress;
import schedule.mock.data.DOGeocodeFromAddress;
import schedule.mock.data.DOGeocodeFromLatLng;
import schedule.mock.data.DOGeocodeResult;
import schedule.mock.data.DOGeometry;
import schedule.mock.data.DOLatLng;
import schedule.mock.data.DONearBy;
import schedule.mock.data.DONearByResult;
import schedule.mock.events.UIPlaceListIsReadyEvent;
import schedule.mock.events.UIShowPlaceListEvent;
import schedule.mock.fragments.dialog.PlaceListDialogFragment;
import schedule.mock.prefs.Prefs;
import schedule.mock.tasks.net.GsonRequestTask;
import schedule.mock.utils.BusProvider;
import schedule.mock.utils.GeocodeUtil;
import schedule.mock.utils.LL;
import schedule.mock.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.squareup.otto.Subscribe;


public final class InputFragment extends BaseFragment implements View.OnClickListener, TextWatcher {

	public static final int LAYOUT = R.layout.fragment_input;
	private static final int REQUEST_CODE_VOICE_RECOGNITION = 1234;
	private DONearByResult[] mNearByResults;


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
			View search = _view.findViewById(R.id.btn_search);
			search.setOnClickListener(this);
			ImageView voiceInput = (ImageView) _view.findViewById(R.id.btn_voice_input);
			voiceInput.setOnClickListener(this);
			/*
			 * Any tap on street, city, country inputs the hidden text for
			 * latlng will be cleared.
			 */
			EditText street = (EditText) _view.findViewById(R.id.et_mocked_street_name);
			street.addTextChangedListener(this);
			EditText city = (EditText) _view.findViewById(R.id.et_mocked_city_name);
			city.addTextChangedListener(this);
			EditText country = (EditText) _view.findViewById(R.id.et_mocked_county_name);
			country.addTextChangedListener(this);
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
				/*
				 * Voice input for search location.
				 */
				if (_resultCode == Activity.RESULT_OK) {
					ArrayList<String> matches = _data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					if (matches != null) {
						/*
						 * Try to validate the voice-input address.
						 */
						clearHiddenLatLng();
						searchVoicePlace(matches.get(0));
					}
				}
				break;
		}
		super.onActivityResult(_requestCode, _resultCode, _data);
	}


	/**
	 * Get near-by places through voice input. To find latlng then fill boxes of
	 * location and finish.
	 * 
	 * @param _str
	 */
	private void searchVoicePlace(String _str) {
		String url = String.format(App.API_GEOCODE_FROM_ADDRESS, Utils.encodedKeywords(_str), Locale.getDefault()
				.getLanguage());
		/*
		 * Try to get latlng first.
		 */
		new GsonRequestTask<DOGecodeFromVoiceAddress>(getActivity().getApplicationContext(), Request.Method.GET,
				url.trim(), DOGecodeFromVoiceAddress.class).execute();
	}


	/**
	 * Get near-by from boxes. To find latlng first and finish.
	 */
	private void searchInputPlaces() {
		View view = getView();
		if (view != null) {
			String address = ((TextView) view.findViewById(R.id.et_mocked_street_name)).getText().toString();
			String city = ((TextView) view.findViewById(R.id.et_mocked_city_name)).getText().toString();
			String fullLocation = new StringBuilder().append(address).append(city).toString();
			String url = String.format(App.API_GEOCODE_FROM_ADDRESS, Utils.encodedKeywords(fullLocation), Locale
					.getDefault().getLanguage());
			/*
			 * Try to get latlng first.
			 */
			if (!TextUtils.isEmpty(address)) {
				new GsonRequestTask<DOGeocodeFromAddress>(getActivity().getApplicationContext(), Request.Method.GET,
						url.trim(), DOGeocodeFromAddress.class).execute();
			} else {
				Utils.showLongToast(getActivity().getApplicationContext(), R.string.warning_input_address);
			}
		}
	}


	@Override
	public void onClick(View _view) {
		switch (_view.getId()) {
			case R.id.btn_search:
				searchInputPlaces();
				break;
			case R.id.btn_voice_input:
				startVoiceRecognitionActivityAndPayloadPlaces();
				break;
		}
	}


	/**
	 * Finished converting from address to latlng(Geocode job). Directly to find
	 * near-by by input.
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGeocodeFromAddress _geocode) {
		if (_geocode != null) {
			Context ctx = getActivity().getApplicationContext();
			DOGeocodeResult[] results = _geocode.getGeocodeResults();
			if (results != null && results.length > 0) {
				DOGeocodeResult result = results[0];
				DOGeometry geometry = result.getGeometry();
				if (geometry != null && geometry.getLocation() != null) {
					DOLatLng latlng = geometry.getLocation();
					double lat = latlng.getLatitude();
					double lng = latlng.getLongitude();
					showDebugLatLng(latlng);
					findNearByPlaces(ctx, lat, lng);
				}
			}
		}
	}


	/**
	 * Find the list of near-by which could be mocked late.
	 */
	private void findNearByPlaces(Context _ctx, double _lat, double _lng) {
		String url = String.format(App.API_NEAR_BY, _lat, _lng, Prefs.getInstance().getRadius(), Locale.getDefault()
				.getLanguage());
		LL.d("Start place near-by:" + url);
		new GsonRequestTask<DONearBy>(_ctx, Request.Method.GET, url, DONearBy.class).execute();
	}


	/**
	 * Finished loading near-by places.
	 * 
	 * **/
	@Subscribe
	public void onDONearBySuccess(DONearBy _nearBy) {
		if (_nearBy != null) {
			mNearByResults = _nearBy.getNearByResults();
			if (mNearByResults != null) {
				PlaceListDialogFragment.showInstance(getActivity());
			}
		}
	}


	/**
	 * UI is ready showing near-by places, and we push data onto it.
	 * 
	 * **/
	@Subscribe
	public void onPlaceListIsReady(UIPlaceListIsReadyEvent _e) {
		BusProvider.getBus().post(new UIShowPlaceListEvent(mNearByResults));
	}


	/**
	 * Get translated string of Geocode from latlng, it is the handler for
	 * location tracking of clicking radar on actionbar. Don't do search of
	 * near-by.
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGeocodeFromLatLng _geocode) {
		if (_geocode != null) {
			DOGeocodeResult[] results = _geocode.getGeocodeResults();
			if (results != null && results.length > 0) {
				fillLocationList(results[0]);
			}
		}
	}


	/**
	 * Get translated string of Geocode from latlng, it is the handler for
	 * checking voice input, see @link{searchVoicePlace}. To find near-by after
	 * voice input.
	 * **/
	@Subscribe
	public void onDOGeocodeSuccess(DOGecodeFromVoiceAddress _geocode) {
		if (_geocode != null) {
			DOGeocodeResult[] results = _geocode.getGeocodeResults();
			if (results != null && results.length > 0) {
				DOLatLng latLng = fillLocationList(results[0]);
				if (latLng != null) {
					findNearByPlaces(getActivity().getApplicationContext(), latLng.getLatitude(), latLng.getLongitude());
				}
			}
		}
	}


	/**
	 * Fill in all location information onto street, city, country....
	 * Radar-search and voice input need this to translate and validate.
	 * 
	 * @return a lat and lng
	 * */
	private DOLatLng fillLocationList(DOGeocodeResult _result) {
		DOGeocodeResult result = _result;
		DOGeometry geometry = result.getGeometry();
		GeocodeUtil.GeocodedAddress geocodedAddress = GeocodeUtil.fromGeocodeResult(result);
		View view = getView();
		if (view != null) {
			if (geocodedAddress != null) {
				EditText street = (EditText) view.findViewById(R.id.et_mocked_street_name);
				EditText city = (EditText) view.findViewById(R.id.et_mocked_city_name);
				EditText country = (EditText) view.findViewById(R.id.et_mocked_county_name);
				street.setText(geocodedAddress.getStreet());
				city.setText(geocodedAddress.getCity());
				country.setText(geocodedAddress.getCountry());
			}
			if (geometry != null && geometry.getLocation() != null) {
				DOLatLng latLng = geometry.getLocation();
				showDebugLatLng(latLng);
				return latLng;
			}
		}
		return null;
	}


	/**
	 * Show debug info(latlng) on textview.
	 * 
	 * @param _latLng
	 */
	private void showDebugLatLng(DOLatLng _latLng) {
		View view = getView();
		if (view != null) {
			if (BuildConfig.DEBUG) {
				TextView latlng = (TextView) view.findViewById(R.id.tv_mocked_lanlng);
				latlng.setText(_latLng.getLatitude() + "," + _latLng.getLongitude());
				latlng.setVisibility(View.VISIBLE);
			}
		}
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}


	@Override
	public void afterTextChanged(Editable s) {
		clearHiddenLatLng();
	}


	/**
	 * Help method that clear latlng in debug-box.
	 */
	private void clearHiddenLatLng() {
		View view = getView();
		if (view != null) {
			TextView lanlng = (TextView) view.findViewById(R.id.tv_mocked_lanlng);
			lanlng.setText("");
		}
	}
}
