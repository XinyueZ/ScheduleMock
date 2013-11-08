package schedule.mock.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;

import schedule.mock.R;
import schedule.mock.tasks.net.TaskHelper;
import schedule.mock.utils.DisplayUtil;


public final class ImageDialogFragment extends BaseDialogFragment {

	public static final int LAYOUT = R.layout.fragment_image;
	private static final String TAG = "ImageDialogFragment";
	private static final String KEY_URL = "url";


	/**
	 * Show a dialog fragment instance with image url.
	 * @param _context
	 * @param _url
	 */
	public static void showInstance(FragmentActivity _context, String _url) {
		Bundle args = new Bundle();
		args.putString(KEY_URL, _url);
		DialogFragment fragment = (DialogFragment) ImageDialogFragment.instantiate(_context,
				ImageDialogFragment.class.getName(), args);
		show(_context, fragment, TAG);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.No_Title_Dialog_Style);
	}


	@Override
	public View onCreateView(LayoutInflater _inflater, ViewGroup _container, Bundle _savedInstanceState) {
		View rootView = _inflater.inflate(LAYOUT, _container, false);
		return rootView;
	}


	@Override
	public void onViewCreated(View _view, Bundle _savedInstanceState) {
		super.onViewCreated(_view, _savedInstanceState);
		if (_view != null) {
			NetworkImageView networkImageView = (NetworkImageView) _view.findViewById(R.id.iv_place_static_map);
			DisplayMetrics displayMetrics = DisplayUtil.getDisplayMetrics(getActivity().getApplicationContext());
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) networkImageView.getLayoutParams();
			params.width = displayMetrics.widthPixels;
			params.height = displayMetrics.heightPixels;
			networkImageView.setLayoutParams(params);
			String url = getArguments().getString(KEY_URL);
			if (!TextUtils.isEmpty(url)) {
				networkImageView.setImageUrl(url, TaskHelper.getImageLoader());
			}
		}
	}
}
