package schedule.mock.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Locale;

import schedule.mock.LL;
import schedule.mock.R;


public final class InputFragment extends BaseFragment {

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


	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(intent, REQUEST_CODE_VOICE_RECOGNITION);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_VOICE_RECOGNITION && resultCode == Activity.RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (String str : matches ) {
				LL.d(str);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
