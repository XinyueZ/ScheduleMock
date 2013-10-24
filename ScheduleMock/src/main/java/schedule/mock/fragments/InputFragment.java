package schedule.mock.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import schedule.mock.App;
import schedule.mock.R;
import schedule.mock.data.DOPlace;
import schedule.mock.tasks.GetPlacesTask;
import schedule.mock.utils.LL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public final class InputFragment extends BaseFragment implements View.OnClickListener {

	public static final int LAYOUT = R.layout.fragment_input;
	private static final int REQUEST_CODE_VOICE_RECOGNITION = 1234;


	public static InputFragment newInstance(Context _context) {
		return (InputFragment) InputFragment.instantiate(_context, InputFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		startVoiceRecognitionActivity();
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


	private void startVoiceRecognitionActivity() {
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
					for (String str : matches) {
						payloadPlaces(str);
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
		new GetPlacesTask(getActivity().getApplicationContext(), str) {

			@Override
			protected void onPostExecute(List<DOPlace> _result) {
				for (DOPlace place : _result) {
					LL.d(place.toString());
				}
			}
		}.execute();
	}


	private void searchAndPayloadPlaces() {
		View view = getView();
		if (view != null) {
			payloadPlaces(((TextView) view.findViewById(R.id.et_mocked_address_name)).getText().toString());
		}
	}


	@Override
	public void onClick(View _view) {
		switch (_view.getId()) {
			case R.id.btn_search:
				searchAndPayloadPlaces();
				break;
			case R.id.btn_voice_input:
				startVoiceRecognitionActivity();
				break;
		}
	}
}
