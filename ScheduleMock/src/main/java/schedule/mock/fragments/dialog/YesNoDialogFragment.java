package schedule.mock.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import schedule.mock.R;

public abstract class YesNoDialogFragment extends BaseDialogFragment {
	protected static final String EXTRAS_TITLE = "extras.title";
	protected static final String EXTRAS_CONTENT = "extras.content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle _savedInstanceState) {
		Bundle args = getArguments();
		String title = args.getString(EXTRAS_TITLE);
		String content =  args.getString(EXTRAS_CONTENT);

		return new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(content)
				.setPositiveButton(R.string.general_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						onYes();
					}
				}).setNegativeButton(R.string.general_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						onNo();
					}
				}).create();
	}

	public void onYes() {
		dismiss();
	}

	public void onNo() {
		dismiss();
	}
}
