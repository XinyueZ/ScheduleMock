package schedule.mock.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import schedule.mock.R;

public class OKDialogFragment extends BaseDialogFragment {
	private static final String EXTRAS_TITLE = "extras.title";
	private static final String EXTRAS_CONTENT = "extras.content";

	public static void showInstance(FragmentActivity _context, String _title, String _content) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_TITLE, _title);
		args.putString(EXTRAS_CONTENT, _content);
		DialogFragment fragment = (DialogFragment) instantiate(_context, OKDialogFragment.class.getName(), args);
		show(_context, fragment, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle _savedInstanceState) {
		Bundle args = getArguments();
		String title = args.getString(EXTRAS_TITLE);
		String content = args.getString(EXTRAS_CONTENT);

		return new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(content)
				.setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						onOk();
					}
				}).create();
	}

	public void onOk() {
		dismiss();
	}

}
